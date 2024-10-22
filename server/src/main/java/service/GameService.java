package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import exception.ResponseException;
import model.Game;

import java.lang.module.ResolutionException;
import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private int gameID = 1;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Game createGame(String gameName, String whiteUsername) throws DataAccessException {
//        if (gameName == null || gameName.isEmpty()) {
//            throw new ResponseException(400, "bad request");
//        }
        ChessGame chessGame = new ChessGame();
        Game newGame = new Game(gameID++, whiteUsername, null, gameName, chessGame);
        gameDAO.createGame(newGame);
        return newGame;
    }


    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException, ResponseException {
        Game game = gameDAO.getGame(gameID);

        // Join available color
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: White player spot already taken");
            }
            Game updatedGame = new Game(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: Black player spot already taken");
            }
            Game updatedGame = new Game(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        } else {
            throw new DataAccessException("Error: Invalid team color");
        }
    }

    public Collection<Game> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public Game getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void clearGames() throws DataAccessException {
        gameDAO.deleteAllGames();
        gameID = 1;
    }
}
