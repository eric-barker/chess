package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import model.Game;
import model.Auth;
import exception.ResponseException;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    // Constructor
    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    // Create a new game (requires authToken for authorization)
    public Game createGame(String gameName, String authToken) throws DataAccessException, ResponseException {
        validateAuthToken(authToken);  // Check if authToken is valid

        if (gameName == null || gameName.isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }

        Game newGame = new Game(1, null, null, gameName, null);  // Let DAO handle the gameID assignment
        gameDAO.createGame(newGame);

        // Retrieve the game back to ensure the gameID is updated
        Game createdGame = gameDAO.getGame(newGame.gameID());

        return createdGame;  // Return the game with the correct gameID
    }

    // Retrieve a game by its ID
    public Game getGame(int gameID) throws ResponseException, DataAccessException {
        Game game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new ResponseException(504, "Error: Game not found");
        }
        return game;
    }

    // List all games (requires authToken for authorization)
    public Collection<Game> listGames(String authToken) throws DataAccessException, ResponseException {
        validateAuthToken(authToken);  // Check if authToken is valid
        return gameDAO.listGames();
    }

    public void joinGame(int gameID, String playerColor, String username, String authToken) throws DataAccessException, ResponseException {
        validateAuthToken(authToken);  // Check if authToken is valid

        if (playerColor == null || (!playerColor.equalsIgnoreCase("WHITE") && !playerColor.equalsIgnoreCase("BLACK"))) {
            throw new ResponseException(400, "Error: bad request");
        }

        Game game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new ResponseException(507, "Error: Game not found");
        }

        // Assign player to the correct color
        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            game = new Game(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.blackUsername() != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            game = new Game(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }

        // Update the game after assigning the player
        gameDAO.updateGame(game);
    }

    // Clear all game data (used for testing)
    public void clearGames() throws DataAccessException {
        gameDAO.deleteAllGames();
    }

    // Helper method to validate authToken
    private void validateAuthToken(String authToken) throws ResponseException {
        Auth auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}
