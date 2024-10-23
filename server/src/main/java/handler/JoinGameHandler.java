package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.User;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            User user = userService.getUser(authToken);
            if (user == null) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }

            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            if (joinRequest.playerColor == null || joinRequest.gameID == null) {
                res.status(400);  // Bad Request
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }

            gameService.joinGame(joinRequest.gameID, joinRequest.playerColor, user.username(), authToken);
            res.status(200);  // Success
            return "";

        } catch (ResponseException e) {
            res.status(e.StatusCode());
            return gson.toJson(new ErrorResponse(e.getMessage()));

        } catch (DataAccessException e) {
            res.status(501);  // Internal Server Error
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private static class JoinGameRequest {
        String playerColor;
        Integer gameID;
    }

    private static class ErrorResponse {

        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
