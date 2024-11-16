package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            // Deserialize
            String authToken = req.headers("authorization");

            if (authToken == null || authToken.isEmpty()) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorMessage("Error: Missing authorization token"));
            }

            this.userService.logout(authToken);

            res.status(200);
            return gson.toJson(new SuccessMessage("User successfully Logged out"));
        } catch (ResponseException e) {
            res.status(401);
            return gson.toJson(new ErrorMessage("Error: unauthorized"));
        } catch (Exception e) {
            res.status(509);
            return gson.toJson(new ErrorMessage("Error: Internal Server Error: " + e.getMessage()));
        }
    }

    record ErrorMessage(String message) {
    }

    record SuccessMessage(String message) {
    }
}
