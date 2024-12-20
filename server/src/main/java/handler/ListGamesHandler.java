package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import logging.LoggerManager;
import model.Game;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;
import java.util.logging.Logger;

public class ListGamesHandler {

    private static final Logger LOGGER = LoggerManager.getLogger(ListGamesHandler.class.getName());
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) {
        try {

            // Retrieve the authToken
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);  // Unauthorized
                return gson.toJson(new ErrorResponse("Error: unauthorized"));
            }
            // Get the list of games
            Collection<Game> games = gameService.listGames(authToken);

            // Create a response
            ListGamesResponse response = new ListGamesResponse(games);
            res.status(200);  // Success
            var json = gson.toJson(response);

            LOGGER.info("json response: " + json);
            res.type("application/json");
            res.header("Content-Length", String.valueOf(json.length()));
            return json;
        } catch (ResponseException e) {
            res.status(e.statusCode());
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (DataAccessException e) {
            res.status(502);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    // Helper class to format each game in the list
    private static class GameEntry {
        private final int gameID;
        private final String whiteUsername;
        private final String blackUsername;
        private final String gameName;
        private final ChessGame game;

        public GameEntry(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
            this.gameID = gameID;
            this.whiteUsername = whiteUsername;
            this.blackUsername = blackUsername;
            this.gameName = gameName;
            this.game = game;
        }
    }

    // Helper class to format the response
    private static class ListGamesResponse {
        private final GameEntry[] games;

        public ListGamesResponse(Collection<Game> games) {
            this.games = games.stream()
                    .map(game -> new GameEntry(
                            game.gameID(),
                            game.whiteUsername(),
                            game.blackUsername(),
                            game.gameName(),
                            game.game()
                    ))
                    .toArray(GameEntry[]::new);
        }
    }


    // Helper class for error response
    private static class ErrorResponse {

        private final String message;


        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
