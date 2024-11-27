package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import logging.LoggerManager;
import model.Auth;
import model.User;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginHandler {
    private static final Logger LOGGER = LoggerManager.getLogger(LoginHandler.class.getName());
    private final UserService userService;
    private final Gson gson = new Gson();

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            LOGGER.log(Level.INFO, "Processing login request");

            // Log the request details
            LOGGER.info("Request Method: " + req.requestMethod());
            LOGGER.info("Request URL: " + req.url());
            LOGGER.info("Request Headers: " + req.headers());

            // Deserialize login credentials
            User loginUser = gson.fromJson(req.body(), User.class);
            LOGGER.log(Level.INFO, "Deserialized request body: {0}", loginUser);

            // Validate required fields
            if (loginUser.username() == null || loginUser.password() == null) {
                LOGGER.log(Level.WARNING, "Missing credentials in login request");
                res.status(400);  // Bad Request
                return gson.toJson("Error: Missing credentials");
            }

            // Perform login and get Auth object
            LOGGER.log(Level.INFO, "Attempting login for username: {0}", loginUser.username());
            Auth auth = userService.login(loginUser.username(), loginUser.password());
            LOGGER.log(Level.INFO, "Login successful for username: {0}", auth.username());

            // Serialize and send response
            String json = gson.toJson(auth);
            LOGGER.log(Level.FINE, "Serialized Auth response: {0}", json);

            res.status(200);
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));
            return json;

        } catch (ResponseException e) {
            LOGGER.log(Level.WARNING, "Login failed: {0}", e.getMessage());
            res.status(500);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during login: {0}", e.getMessage());
            res.status(509);
            return gson.toJson("Error: Internal Server Error: " + e.getMessage());
        }
    }
}
