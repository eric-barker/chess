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
    private static final Logger logger = LoggerManager.getLogger(LoginHandler.class.getName());
    private final UserService userService;
    private final Gson gson = new Gson();

    static {
        logger.setLevel(Level.INFO);
    }

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            logger.log(Level.INFO, "Processing login request");

            // Log the request details
            logger.info("Request Method: " + req.requestMethod());
            logger.info("Request URL: " + req.url());
            logger.info("Request Headers: " + req.headers());

            // Deserialize login credentials
            User loginUser = gson.fromJson(req.body(), User.class);
            logger.log(Level.INFO, "Deserialized request body: {0}", loginUser);

            // Validate required fields
            if (loginUser.username() == null || loginUser.password() == null) {
                logger.log(Level.WARNING, "Missing credentials in login request");
                res.status(400);  // Bad Request
                return gson.toJson("Error: Missing credentials");
            }

            // Perform login and get Auth object
            logger.log(Level.INFO, "Attempting login for username: {0}", loginUser.username());
            Auth auth = userService.login(loginUser.username(), loginUser.password());
            logger.log(Level.INFO, "Login successful for username: {0}", auth.username());

            // Serialize and send response
            String json = gson.toJson(auth);
            logger.log(Level.FINE, "Serialized Auth response: {0}", json);

            res.status(203);
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));
            return json;

        } catch (ResponseException e) {
            logger.log(Level.WARNING, "Login failed: {0}", e.getMessage());
            res.status(500);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during login: {0}", e.getMessage());
            res.status(509);
            return gson.toJson("Error: Internal Server Error: " + e.getMessage());
        }
    }
}
