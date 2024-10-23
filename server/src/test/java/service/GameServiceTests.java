package service;

import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.Auth;
import model.Game;
import org.junit.jupiter.api.*;
import exception.ResponseException;
import dataaccess.DataAccessException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private static GameService gameService;
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;

    private static String validAuthToken;

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(gameDAO, authDAO);

        // Add a valid user and their auth token
        String username = "testUser";
        validAuthToken = "validAuthToken";
        authDAO.addAuth(validAuthToken, username);
    }

    @BeforeEach
    public void clearData() throws DataAccessException {
        gameService.clearGames();
    }

    @Test
    @DisplayName("Create Game - Success")
    public void createGameSuccess() throws DataAccessException, ResponseException {
        Game createdGame = gameService.createGame("New Game", validAuthToken);

        assertNotNull(createdGame, "Game should have been created");
        assertEquals("New Game", createdGame.gameName(), "Game name does not match");
        assertNull(createdGame.whiteUsername(), "White player should not be assigned");
        assertNull(createdGame.blackUsername(), "Black player should not be assigned");
    }

    @Test
    @DisplayName("Create Game - Unauthorized")
    public void createGameUnauthorized() {
        try {
            gameService.createGame("Unauthorized Game", "invalidAuthToken");
            fail("Expected ResponseException for invalid auth token");
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode(), "Unauthorized status code should be 401");
            assertEquals("Error: unauthorized", e.getMessage(), "Error message should be 'Error: unauthorized'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    @DisplayName("Create Game - Bad Request (Empty Game Name)")
    public void createGameBadRequest() {
        try {
            gameService.createGame("", validAuthToken);
            fail("Expected ResponseException for empty game name");
        } catch (ResponseException e) {
            assertEquals(400, e.statusCode(), "Bad request status code should be 400");
            assertEquals("Error: bad request", e.getMessage(), "Error message should be 'Error: bad request'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    @DisplayName("Get Game - Success")
    public void getGameSuccess() throws DataAccessException, ResponseException {
        Game createdGame = gameService.createGame("Retrievable Game", validAuthToken);

        Game retrievedGame = gameService.getGame(createdGame.gameID());

        assertNotNull(retrievedGame, "Game should be retrieved successfully");
        assertEquals(createdGame.gameID(), retrievedGame.gameID(), "Game ID should match");
    }

    @Test
    @DisplayName("Get Game - Game Not Found")
    public void getGameNotFound() {
        try {
            gameService.getGame(9999);  // Assuming 9999 is an invalid game ID
            fail("Expected ResponseException for game not found");
        } catch (ResponseException e) {
            assertEquals(504, e.statusCode(), "Game not found status code should be 504");
            assertEquals("Error: Game not found", e.getMessage(), "Error message should be 'Error: Game not found'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    @DisplayName("Join Game - Success")
    public void joinGameSuccess() throws DataAccessException, ResponseException {
        Game createdGame = gameService.createGame("Joinable Game", validAuthToken);

        gameService.joinGame(createdGame.gameID(), "WHITE", "testUser", validAuthToken);

        Game updatedGame = gameService.getGame(createdGame.gameID());
        assertEquals("testUser", updatedGame.whiteUsername(), "White player should be assigned correctly");
        assertNull(updatedGame.blackUsername(), "Black player should not be assigned yet");
    }

    @Test
    @DisplayName("Join Game - Already Taken")
    public void joinGameAlreadyTaken() {
        try {
            Game createdGame = gameService.createGame("Joinable Game", validAuthToken);
            gameService.joinGame(createdGame.gameID(), "WHITE", "testUser", validAuthToken);

            gameService.joinGame(createdGame.gameID(), "WHITE", "anotherUser", validAuthToken);
            fail("Expected ResponseException for trying to join already taken spot");
        } catch (ResponseException e) {
            assertEquals(403, e.statusCode(), "Already taken status code should be 403");
            assertEquals("Error: already taken", e.getMessage(), "Error message should be 'Error: already taken'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    @DisplayName("Join Game - Bad Request (Invalid Color)")
    public void joinGameBadRequest() {
        try {
            Game createdGame = gameService.createGame("Joinable Game", validAuthToken);
            gameService.joinGame(createdGame.gameID(), "INVALID", "testUser", validAuthToken);
            fail("Expected ResponseException for invalid color");
        } catch (ResponseException e) {
            assertEquals(400, e.statusCode(), "Bad request status code should be 400");
            assertEquals("Error: bad request", e.getMessage(), "Error message should be 'Error: bad request'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }

    @Test
    @DisplayName("List Games - Success")
    public void listGamesSuccess() throws DataAccessException, ResponseException {
        gameService.createGame("Game 1", validAuthToken);
        gameService.createGame("Game 2", validAuthToken);

        Collection<Game> games = gameService.listGames(validAuthToken);

        assertEquals(2, games.size(), "Should list all created games");
    }

    @Test
    @DisplayName("List Games - Unauthorized")
    public void listGamesUnauthorized() {
        try {
            gameService.listGames("invalidAuthToken");
            fail("Expected ResponseException for invalid auth token");
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode(), "Unauthorized status code should be 401");
            assertEquals("Error: unauthorized", e.getMessage(), "Error message should be 'Error: unauthorized'");
        } catch (DataAccessException e) {
            fail("Unexpected DataAccessException thrown");
        }
    }
}
