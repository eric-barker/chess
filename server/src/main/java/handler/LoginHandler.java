package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import model.Auth;
import model.User;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            // Deserialize
            User userToLogin = gson.fromJson(req.body(), User.class);

            // is the User valid?
            if (userToLogin.password() == null || userToLogin.username() == null) {
                res.status(400);
                return gson.toJson(new ErrorMessage("Error: missing username or password"));
            }

            var auth = userService.login(userToLogin);

            res.status(200);
            return gson.toJson(new AuthResult(userToLogin.username(), auth.authToken()));

        } catch (ResponseException e) {
            res.status(401);  // Unauthorized
            return gson.toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorMessage("Error: Internal Server Error: " + e.getMessage()));
        }
    }

    record ErrorMessage(String message) {
    }

    record AuthResult(String username, String authToken) {
    }

}
