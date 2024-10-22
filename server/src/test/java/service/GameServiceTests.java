package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import exception.ResponseException;
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
        gameService = new GameService(new MemoryGameDAO(), new MemoryAuthDAO());
    }

    @BeforeEach
    public void clearBeforeTest() throws DataAccessException {
        // Clear all games before each test
        gameService.clearGames();
    }

    @Test
    @DisplayName("Create Game Test")
    public void testCreateGame() throws DataAccessException, ResponseException {
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
    public void testJoinGameAsBlack() throws DataAccessException, ResponseException {
        String gameName = "Test Join Game";
        String whitePlayer = "WhitePlayer";
        String blackPlayer = "BlackPlayer";

        // Simulate the login process to get a valid auth token
        String whitePlayerAuthToken = UUID.randomUUID().toString();  // Generate a fake auth token
        gameService.getAuthDAO().createAuth(whitePlayerAuthToken, whitePlayer);  // Store the auth token in the DAO

        // Create the game with the white player using the auth token
        Game createdGame = gameService.createGame(gameName, whitePlayerAuthToken);

        // Join the game as Black
        gameService.joinGame(createdGame.gameID(), ChessGame.TeamColor.BLACK, blackPlayer);

        // Retrieve the updated game
        Game updatedGame = gameService.getGame(createdGame.gameID());

        // Assertions
        assertNotNull(updatedGame, "Game should exist after creation");
        assertEquals(blackPlayer, updatedGame.blackUsername(), "Black player should be assigned correctly");
        assertEquals(whitePlayer, updatedGame.whiteUsername(), "White player should remain unchanged");
    }

    @Test
    @DisplayName("Join Game as White (Fail) Test")
    public void testJoinGameAsWhiteFail() throws DataAccessException, ResponseException {
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

    @Test
    @DisplayName("List Games Test")
    public void testListGames() throws DataAccessException {
        String game1Name = "Game 1";
        String game2Name = "Game 2";
        String whitePlayer1 = "WhitePlayer1";
        String whitePlayer2 = "WhitePlayer2";

        gameService.createGame(game1Name, whitePlayer1);
        gameService.createGame(game2Name, whitePlayer2);

        Collection<Game> games = gameService.listGames();

        assertNotNull(games, "Games list should not be null");
        assertEquals(2, games.size(), "There should be 2 games created");

        assertTrue(games.stream().anyMatch(game -> game.gameName().equals(game1Name)), "Game 1 should exist in the list");
        assertTrue(games.stream().anyMatch(game -> game.gameName().equals(game2Name)), "Game 2 should exist in the list");
    }


    @Test
    @DisplayName("Clear Games Test")
    public void testClearGames() throws DataAccessException {
        String game1Name = "Game 1";
        String whitePlayer1 = "WhitePlayer1";

        gameService.createGame(game1Name, whitePlayer1);

        Collection<Game> gamesBeforeClear = gameService.listGames();
        assertEquals(1, gamesBeforeClear.size(), "There should be 1 game before clearing");

        gameService.clearGames();

        Collection<Game> gamesAfterClear = gameService.listGames();
        assertEquals(0, gamesAfterClear.size(), "There should be no games after clearing");
    }
}
