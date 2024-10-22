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
    @Order(1)
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

}
