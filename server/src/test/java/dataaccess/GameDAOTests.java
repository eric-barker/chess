package dataaccess;

import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import model.Game;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new MemoryGameDAO();  // run before each test to reset the DAO
    }

    @Test
    public void testCreateAndGetGame() throws DataAccessException {
        // Create a new ChessGame and store it in the DAO
        ChessGame chessGame = new ChessGame();  // Assuming this exists
        Game game = new Game(1, "user1", "user2", "Test Game", chessGame);

        gameDAO.createGame(game);  // create the game

        // Retrieve the game by its ID
        Game retrievedGame = gameDAO.getGame(1);
        assertNotNull(retrievedGame, "The game should be retrievable after creation.");
        assertEquals("Test Game", retrievedGame.gameName(), "Game name should match the created game.");
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        // First, create a game
        ChessGame chessGame = new ChessGame();
        Game game = new Game(1, "user1", "user2", "Test Game", chessGame);
        gameDAO.createGame(game);

        // Now, update the game name
        Game updatedGame = new Game(1, "user1", "user2", "Updated Game", chessGame);
        gameDAO.updateGame(updatedGame);

        // Check if the update was successful
        Game retrievedGame = gameDAO.getGame(1);
        assertEquals("Updated Game", retrievedGame.gameName(), "Game name should reflect the updated value.");
    }

    @Test
    public void testDeleteAllGames() throws DataAccessException {
        // Add two games
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new Game(1, "user1", "user2", "Game 1", chessGame));
        gameDAO.createGame(new Game(2, "user3", "user4", "Game 2", chessGame));

        // Now delete all the games
        gameDAO.deleteAllGames();

        // Check if the list is empty
        Collection<Game> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "All games should be deleted.");
    }

    @Test
    public void testListGames() throws DataAccessException {
        // Add two games
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new Game(1, "user1", "user2", "Game 1", chessGame));
        gameDAO.createGame(new Game(2, "user3", "user4", "Game 2", chessGame));

        // Check that both games exist
        Collection<Game> games = gameDAO.listGames();
        assertEquals(2, games.size(), "There should be exactly 2 games listed.");
    }
}
