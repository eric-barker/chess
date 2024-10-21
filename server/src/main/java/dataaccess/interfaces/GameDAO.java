package dataaccess.interfaces;

import model.Game;
import dataaccess.DataAccessException;

import java.util.Collection;

public interface GameDAO {

    void createGame(Game game) throws DataAccessException;

    Game getGame(int gameID) throws DataAccessException;

    void updateGame(Game game) throws DataAccessException;

    Collection<Game> listGames() throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}