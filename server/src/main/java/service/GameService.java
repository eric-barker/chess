package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Game createGame(String gameName, String whiteUsername) throws DataAccessException {
        return null;
    }


    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
    }

    public Collection<Game> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public Game getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void clearGames() throws DataAccessException {
        gameDAO.deleteAllGames();
    }

    private int generateGameID() {
        return 0;
    }
}
