package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinGameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) {
        try {
            
            // Retrieve the authToken from header
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            // Deserialize to get the playerColor and gameID
            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);

            // Validate that both fields are provided
            if (joinRequest.playerColor == null || joinRequest.gameID == null) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Error: bad request"));
            }

            // Join the game
            gameService.joinGame(joinRequest.gameID, joinRequest.playerColor, req.headers("username"), authToken);
            res.status(200);
            return "";
        } catch (ResponseException e) {
            res.status(e.StatusCode());
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
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
