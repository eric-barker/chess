package server;

import chess.ChessGame;
import exception.ResponseException;
import logging.LoggerManager;
import model.Auth;
import model.Game;
import com.google.gson.Gson;
import model.User;

import java.io.*;
import java.net.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFacade {

    private final String serverUrl;
    private static final Logger logger = LoggerManager.getLogger(ServerFacade.class.getName());

    static {
        // Set the logger level to ALL to capture all log messages
        logger.setLevel(Level.ALL);
    }

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    public Auth register(User user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, Auth.class);
    }

    public Auth login(String username, String password) throws ResponseException {
        var path = "/session";
        var requestBody = new User(username, password, null);
        return this.makeRequest("POST", path, requestBody, Auth.class);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session"; // The endpoint for LogoutHandler
        this.makeRequest("DELETE", path, authToken, null);
    }

    public Game[] listGames() throws ResponseException {
        var path = "/game";
        record listGameResponse(Game[] game) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class);
        return response.game();
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            String fullPath = serverUrl + path;
            logger.info("Constructing URL with serverUrl: " + serverUrl + " and path: " + path);
            logger.info("Full URL: " + fullPath);

            URL url = (new URI(fullPath)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(request != null);

            logger.info("[HTTP] Making " + method + " request to: " + url);
            if (request != null) {

                var body = writeBody(request, http);
//                logger.info("[HTTP] Request body: " + body);
            }

            http.connect();
            logger.info("[HTTP] Response Code: " + http.getResponseCode());
            logger.info("[HTTP] Response Body: " + http.getContentLength());
            throwIfNotSuccessful(http);
            T response = readBody(http, responseClass);

            logger.info("[HTTP] Response body: " + new Gson().toJson(response));
            if (response != null) {
                logger.info("[HTTP] Response body: " + new Gson().toJson(response));
            }
            return response;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "[HTTP] Request failed: " + e.getMessage(), e);
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static String writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
            return reqData;
        } else {
            return "Req body is null";
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null) {
                    String errorMessage = new BufferedReader(new InputStreamReader(errorStream))
                            .lines()
                            .reduce("", String::concat);
                    logger.warning("[HTTP] Error response: " + errorMessage);
                }
            }
            throw new ResponseException(status, "[HTTP] HTTP error: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
