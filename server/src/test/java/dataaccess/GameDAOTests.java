package dataaccess;

import dataaccess.interfaces.GameDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.mysql.DatabaseManager;
import dataaccess.mysql.MySQLGameDAO;
import model.Game;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private GameDAO gameDAO;
    private static final boolean USE_MYSQL = true; // Toggle between MySQL and Memory implementations

    @BeforeEach
    public void setUp() throws DataAccessException {
        if (USE_MYSQL) {
            gameDAO = new MySQLGameDAO();
            System.out.println("Using MySQLGameDAO for tests.");
        } else {
            gameDAO = new MemoryGameDAO();
            System.out.println("Using MemoryGameDAO for tests.");
        }
        gameDAO.deleteAllGames(); // Clear all games to start fresh for each test
    }

    @Test
    public void testDatabaseConnection() {
        if (USE_MYSQL) {
            try (Connection conn = DatabaseManager.getConnection()) {
                assertNotNull(conn, "Connection should be established.");
                System.out.println("Database connection successful.");
            } catch (Exception e) {
                fail("Database connection failed: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping database connection test for MemoryGameDAO.");
        }
    }

    @Test
    public void testCreateAndGetGame() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        Game game = new Game(1, "user1", "user2", "Test Game", chessGame);

        Game createdGame = gameDAO.createGame(game);
        Game retrievedGame = gameDAO.getGame(createdGame.gameID());

        assertNotNull(retrievedGame, "The game should be retrievable after creation.");
        assertEquals("Test Game", retrievedGame.gameName(), "Game name should match the created game.");
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        Game game = new Game(1, "user1", "user2", "Test Game", chessGame);
        Game createdGame = gameDAO.createGame(game);

        Game updatedGame = new Game(createdGame.gameID(), "user1", "user2", "Updated Game", chessGame);
        gameDAO.updateGame(updatedGame);

        Game retrievedGame = gameDAO.getGame(createdGame.gameID());
        assertEquals("Updated Game", retrievedGame.gameName(), "Game name should reflect the updated value.");
    }

    @Test
    public void testDeleteAllGames() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new Game(1, "user1", "user2", "Game 1", chessGame));
        gameDAO.createGame(new Game(2, "user3", "user4", "Game 2", chessGame));

        gameDAO.deleteAllGames();

        Collection<Game> games = gameDAO.listGames();
        assertTrue(games.isEmpty(), "All games should be deleted.");
    }

    @Test
    public void testListGames() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new Game(1, "user1", "user2", "Game 1", chessGame));
        gameDAO.createGame(new Game(2, "user3", "user4", "Game 2", chessGame));

        Collection<Game> games = gameDAO.listGames();
        assertEquals(2, games.size(), "There should be exactly 2 games listed.");
    }
}
