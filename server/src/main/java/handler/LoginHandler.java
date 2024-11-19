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
            // Deserialize login credentials
            User loginUser = gson.fromJson(req.body(), User.class);

            // Validate required fields
            if (loginUser.username() == null || loginUser.password() == null) {
                res.status(400);  // Bad Request
                return gson.toJson("Error: Missing credentials");
            }

            // Perform login and get Auth object
            Auth auth = userService.login(loginUser.username(), loginUser.password());

            // Serialize and send response
            String json = gson.toJson(auth);
            res.status(203);
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));
            return json;

        } catch (ResponseException e) {
            res.status(500);
            return gson.toJson(e.getMessage());
        } catch (Exception e) {
            res.status(509);
            return gson.toJson("Error: Internal Server Error: " + e.getMessage());
        }
    }

}
