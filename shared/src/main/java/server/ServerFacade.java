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
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFacade {

    private final String serverUrl;
    private static final Logger LOGGER = LoggerManager.getLogger(ServerFacade.class.getName());


    static {
        // Set the logger level to ALL to capture all log messages
        LOGGER.setLevel(Level.ALL);
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

    public int createGame(String gameName, String authToken) throws ResponseException {
        if (gameName == null || gameName.isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty.");
        }
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }

        var path = "/game";
        record CreateGameRequest(String gameName) {
        }
        var requestBody = new CreateGameRequest(gameName);

        try {
            HttpURLConnection http = (HttpURLConnection) new URL(serverUrl + path).openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Authorization", authToken); // Set the auth token in the header
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);

            // Write the request body
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(new Gson().toJson(requestBody).getBytes());
            }

            // Read and handle the response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream respBody = http.getInputStream();
                     InputStreamReader reader = new InputStreamReader(respBody)) {
                    Game createdGame = new Gson().fromJson(reader, Game.class); // Parse response as a Game object
                    return createdGame.gameID(); // Extract the gameID
                }
            } else {
                throw new ResponseException(http.getResponseCode(), "Failed to create game");
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Error communicating with the server: " + e.getMessage());
        }
    }


    public Game[] listGames(String authToken) throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }

        var path = "/game";
        try {
            // Construct the URL and open a connection
            HttpURLConnection http = (HttpURLConnection) new URL(serverUrl + path).openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Authorization", authToken); // Set the auth token in the header

            // Connect and log the raw response
            http.connect();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                // Parse the response body into ListGamesResponse
                ListGamesResponse response = readBody(http, ListGamesResponse.class);
                if (response == null || response.getGames() == null) {
                    throw new ResponseException(500, "Failed to parse games from server response.");
                }

                // Map GameEntry[] to Game[] for client-side use
                return Arrays.stream(response.getGames())
                        .map(entry -> new Game(entry.getGameID(), entry.getWhiteUsername(), entry.getBlackUsername(),
                                entry.getGameName(), entry.getGame()))
                        .toArray(Game[]::new);
            } else {
                throw new ResponseException(http.getResponseCode(), "Failed to list games");
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Error communicating with the server: " + e.getMessage());
        }
    }


    private static class ListGamesResponse {
        private GameEntry[] games;

        public GameEntry[] getGames() {
            return games;
        }
    }

    private static class GameEntry {
        private int gameID;
        private String gameName;
        private String whiteUsername;
        private String blackUsername;
        private ChessGame game;

        // Add getters for deserialization
        public int getGameID() {
            return gameID;
        }

        public String getGameName() {
            return gameName;
        }

        public String getWhiteUsername() {
            return whiteUsername;
        }

        public String getBlackUsername() {
            return blackUsername;
        }

        public ChessGame getGame() {
            return game;
        }
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ResponseException {
        if (playerColor == null || playerColor.isEmpty()) {
            throw new IllegalArgumentException("Player color cannot be null or empty.");
        }
        if (authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }

        var path = "/game";
        record JoinGameRequest(int gameID, String playerColor) {
        }
        var requestBody = new JoinGameRequest(gameID, playerColor);

        try {
            // Construct the full URL and open a connection
            HttpURLConnection http = (HttpURLConnection) new URL(serverUrl + path).openConnection();
            http.setRequestMethod("PUT");
            http.setRequestProperty("Authorization", authToken); // Add Authorization header
            http.setRequestProperty("Content-Type", "application/json");
            http.setDoOutput(true);

            // Write the request body
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(new Gson().toJson(requestBody).getBytes());
            }

            // Check the response code and handle errors
            http.connect();
            if (http.getResponseCode() >= 300) {
                throw new ResponseException(http.getResponseCode(), "Failed to join game");
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Error communicating with the server: " + e.getMessage());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            String fullPath = serverUrl + path;
            LOGGER.info("Constructing URL with serverUrl: " + serverUrl + " and path: " + path);
            LOGGER.info("Full URL: " + fullPath);

            URL url = (new URI(fullPath)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(request != null);

            LOGGER.info("[HTTP] Making " + method + " request to: " + url);
            if (request != null) {

                var body = writeBody(request, http);
//                LOGGER.info("[HTTP] Request body: " + body);
            }

            http.connect();
            LOGGER.info("[HTTP] Response Code: " + http.getResponseCode());
            LOGGER.info("[HTTP] Response Body: " + http.getContentLength());
            throwIfNotSuccessful(http);
            T response = readBody(http, responseClass);

            LOGGER.info("[HTTP] Response body: " + new Gson().toJson(response));
            if (response != null) {
                LOGGER.info("[HTTP] Response body: " + new Gson().toJson(response));
            }
            return response;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[HTTP] Request failed: " + e.getMessage(), e);
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
                    LOGGER.warning("[HTTP] Error response: " + errorMessage);
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
