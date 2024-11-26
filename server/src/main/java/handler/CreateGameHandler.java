package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import logging.LoggerManager;
import model.Game;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.logging.Logger;

public class CreateGameHandler {

    private static final Logger LOGGER = LoggerManager.getLogger(CreateGameHandler.class.getName());
    private final GameService gameService;
    private final UserService userService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    public Object handle(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");


            // Validate authorization token
            if (authToken == null || authToken.isEmpty() || !userService.isLoggedIn(authToken)) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorMessage("Error: unauthorized"));
            }

            // Deserialize the request body to get the game name
            CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
            LOGGER.info("Game Name: " + createGameRequest);

            // Validate gameName
            if (createGameRequest.gameName() == null || createGameRequest.gameName().isEmpty()) {
                res.status(400);  // Bad Request
                return gson.toJson(new ErrorMessage("Error: bad request - missing game name"));
            }

            Game createdGame = gameService.createGame(createGameRequest.gameName(), authToken);

            // Return the new game ID in the response
            res.status(200);
            return gson.toJson(new CreateGameResult(createdGame.gameID()));

        } catch (ResponseException e) {
            LOGGER.warning("ResponseException: " + e.getMessage());
            res.status(e.statusCode());  // Use the status code from the exception
            return gson.toJson(new ErrorMessage(e.getMessage()));
        } catch (DataAccessException e) {
            LOGGER.severe("DataAccessException: " + e.getMessage());
            res.status(510);
            return gson.toJson(new ErrorMessage("Error: Data Access Error: " + e.getMessage()));
        } catch (Exception e) {
            LOGGER.severe("Unhandled exception: " + e.getMessage());
            res.status(503);  // Internal Server Error
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
