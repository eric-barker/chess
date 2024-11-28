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
            // Deserialize authToken from request body
            String authToken = req.headers("authorization");

            if (authToken == null || authToken.isEmpty()) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorMessage("Error: Missing authorization token"));
            }

            userService.logout(authToken);

            String json = gson.toJson(new SuccessMessage("User successfully logged out"));
            res.status(200);
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));

            return json;
        } catch (ResponseException e) {
            res.status(401);
            return gson.toJson(new ErrorMessage("Error: Unauthorized"));
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
