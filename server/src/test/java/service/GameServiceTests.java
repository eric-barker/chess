package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryGameDAO;
import model.Game;
import chess.ChessGame;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {

    private static GameService gameService;

    @BeforeAll
    public static void init() {
        // Initialize the GameService with an in-memory DAO for testing
        gameService = new GameService(new MemoryGameDAO());
    }

    @BeforeEach
    public void clearBeforeTest() throws DataAccessException {
        // Clear all games before each test
        gameService.clearGames();
    }

    @Test
    @DisplayName("Create Game Test")
    public void testCreateGame() throws DataAccessException {
        String gameName = "Test Game";
        String whitePlayer = "WhitePlayer";

        Game createdGame = gameService.createGame(gameName, whitePlayer);

        assertNotNull(createdGame, "Game should be created");
        assertEquals(gameName, createdGame.gameName(), "Game name should match");
        assertEquals(whitePlayer, createdGame.whiteUsername(), "White player should be assigned");
        assertNull(createdGame.blackUsername(), "Black player should not be assigned yet");
        assertNotNull(createdGame.game(), "ChessGame object should be initialized");
    }

    @Test
    @DisplayName("Join Game as Black Test")
    public void testJoinGameAsBlack() throws DataAccessException {
        String gameName = "Test Join Game";
        String whitePlayer = "WhitePlayer";
        String blackPlayer = "BlackPlayer";

        Game createdGame = gameService.createGame(gameName, whitePlayer);
        gameService.joinGame(createdGame.gameID(), ChessGame.TeamColor.BLACK, blackPlayer);

        Game updatedGame = gameService.getGame(createdGame.gameID());

        assertNotNull(updatedGame, "Game should exist after creation");
        assertEquals(blackPlayer, updatedGame.blackUsername(), "Black player should be assigned correctly");
        assertEquals(whitePlayer, updatedGame.whiteUsername(), "White player should remain unchanged");
    }

    @Test
    @DisplayName("Join Game as White (Fail) Test")
    public void testJoinGameAsWhiteFail() throws DataAccessException {
        String gameName = "Test Join Fail";
        String whitePlayer = "WhitePlayer";
        String anotherWhitePlayer = "AnotherWhitePlayer";

        Game createdGame = gameService.createGame(gameName, whitePlayer);

        Exception exception = null;
        try {
            gameService.joinGame(createdGame.gameID(), ChessGame.TeamColor.WHITE, anotherWhitePlayer);
        } catch (DataAccessException e) {
            exception = e;
        }
        assertNotNull(exception, "Expected DataAccessException to be thrown.");

        assertTrue(exception.getMessage().contains("White player spot already taken"));
    }
}
