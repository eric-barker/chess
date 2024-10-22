package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final GameDAO gameDAO;
    private int gameID = 1;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Game createGame(String gameName, String whiteUsername) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        Game newGame = new Game(gameID++, whiteUsername, null, gameName, chessGame);
        gameDAO.createGame(newGame);
        return newGame;
    }


    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        Game game = gameDAO.getGame(gameID);
        
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
