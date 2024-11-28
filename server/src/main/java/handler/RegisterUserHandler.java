package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import model.Auth;
import model.User;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterUserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterUserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            // Deserialize incoming JSON into an object
            User newUser = gson.fromJson(req.body(), User.class);

            // Ensure the required fields are present
            if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
                res.status(400);  // Bad Request
                return gson.toJson(new ErrorMessage("Error: bad request"));
            }

            var auth = userService.register(newUser);

            // Respond with a success message and the auth token
            String json = gson.toJson(new Auth(newUser.username(), auth.authToken()));

            res.status(200);  // Success
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));

            return gson.toJson(new TestAuthResult(newUser.username(), auth.authToken()));

        } catch (ResponseException e) {
            // Handle known errors from ResponseException
            if (e.getMessage().contains("already taken")) {
                res.status(403);  // Forbidden
                return gson.toJson(new ErrorMessage("Error: already taken"));
            } else {
                res.status(e.statusCode());
                return gson.toJson(new ErrorMessage(e.getMessage()));
            }
        } catch (Exception e) {
            // Handle anything else
            res.status(508);
            return gson.toJson(new ErrorMessage("Internal Server Error: " + e.getMessage()));
        }
    }

    // Helper class for error messages
    record ErrorMessage(String message) {
    }

    // Helper class for successful auth result
    record TestAuthResult(String username, String authToken) {
    }
}
