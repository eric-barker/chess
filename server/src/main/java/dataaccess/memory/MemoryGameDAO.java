package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {

    private final Map<Integer, Game> games = new HashMap<>();
    private int nextGameID = 1;


    @Override
    public Game createGame(Game game) throws DataAccessException {
        // Update the game object and return it
        game = new Game(nextGameID++, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(game.gameID(), game);
        return game;  // Return the updated game with the correct ID
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }
}
