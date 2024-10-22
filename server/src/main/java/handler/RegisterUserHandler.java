package handler;

import com.google.gson.Gson;
import exception.ResponseException;
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

            var auth = userService.register(newUser);

            // Respond with a success message and the auth token
            res.status(200);  // Success
            return gson.toJson(new TestAuthResult(newUser.username(), auth.authToken()));
        } catch (ResponseException e) {
            // Handle known exceptions like user already exists
            res.status(e.StatusCode());
            return gson.toJson(new ErrorMessage(e.getMessage()));
        } catch (Exception e) {
            // Handle anything else
            res.status(500);
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
