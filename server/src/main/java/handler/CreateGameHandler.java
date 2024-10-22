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

            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new ErrorMessage("Error: unauthorized"));
            }

            // Deserialize
            Game newGame = gson.fromJson(req.body(), Game.class);

            if (newGame.gameName() == null || newGame.gameName().isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorMessage("Error: bad request"));
            }

            gameService.createGame(newGame.gameName(), newGame.whiteUsername());

            res.status(200);
            return gson.toJson(new SuccessMessage("Game successfully created"));
        } catch (ResponseException e) {
            res.status(401);
            return gson.toJson(new ErrorMessage("Error: bad request"));
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    record SuccessMessage(String message) {
    }

    record ErrorMessage(String message) {
    }
}
