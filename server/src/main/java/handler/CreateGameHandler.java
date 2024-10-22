package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import model.Game;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");

            // Validate authorization token
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorMessage("Error: unauthorized"));
            }

            // Deserialize the request body to get the game name
            CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

            // Validate gameName
            if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                res.status(400);  // Bad Request
                return gson.toJson(new ErrorMessage("Error: bad request - missing game name"));
            }

            Game createdGame = gameService.createGame(createGameRequest.gameName(), authToken);

            // Return the new game ID in the response
            res.status(200);
            return gson.toJson(new CreateGameResult(createdGame.gameID()));

        }
//        catch (ResponseException e) {
//            res.status(e.StatusCode());  // Use the status code from the exception
//            return gson.toJson(new ErrorMessage(e.getMessage()));
//        }
        catch (Exception e) {
            res.status(500);  // Internal Server Error
            return gson.toJson(new ErrorMessage("Error: Internal Server Error: " + e.getMessage()));
        }
    }

    record CreateGameRequest(String gameName) {
    }

    record CreateGameResult(int gameID) {
    }

    record ErrorMessage(String message) {
    }
}
