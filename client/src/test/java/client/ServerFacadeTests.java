package client;

import model.Auth;
import model.Game;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.User;
import exception.ResponseException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static int port;
    private ServerFacade facade;


    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0); // Start the server on a dynamic port
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop(); // Stop the server after tests
    }

    @BeforeEach
    public void setUp() {
        facade = new ServerFacade("http://localhost:" + port); // Initialize ServerFacade for tests
    }

    @AfterEach
    public void clear() throws ResponseException {
        try {
            facade.clear();
        } catch (ResponseException e) {
            throw new ResponseException(421, e.getMessage());
        }
    }

    @Test
    public void testClearPositive() {
        assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    public void testClearNegative() {
        ServerFacade invalidFacade = new ServerFacade("http://localhost:9999"); // Invalid server URL
        assertThrows(ResponseException.class, invalidFacade::clear);
    }

    @Test
    public void testRegisterPositive() {
        User user = new User("testuser" + System.currentTimeMillis(), "password123", "testemail@email.com");
        try {
            Auth registeredUser = facade.register(user);

            // Check if the registered user object is not null
            assertNotNull(registeredUser, "Registered user should not be null");

            // Validate the username in the response matches
            assertEquals(user.username(), registeredUser.username(), "Returned username does not match");
        } catch (Exception e) {
            fail("An unexpected exception was thrown: " + e.getMessage());
        }
    }


    @Test
    public void testRegisterNegative() {
        User invalidUser = new User("", "", null); // Invalid user
        assertThrows(ResponseException.class, () -> facade.register(invalidUser));
    }

    @Test
    public void testLoginPositive() {
        User user = new User("testuser", "password123", "testemail@email.com");
        // Ensure the user is registered first
        assertDoesNotThrow(() -> facade.register(user));

        // Test login with correct credentials
        assertDoesNotThrow(() -> {
            Auth loggedInUser = facade.login("testuser", "password123");
            assertNotNull(loggedInUser);
            assertEquals("testuser", loggedInUser.username());
        });
    }

    @Test
    public void testLoginNegative() {
        // Test login with incorrect credentials
        assertThrows(ResponseException.class, () -> facade.login("wronguser", "wrongpassword"));
    }

    @Test
    public void testLogoutPositive() {
        try {
            // Register a user and get an auth token
            User user = new User("testuser_logout", "password123", "testlogout@email.com");
            Auth auth = facade.register(user);

            // Ensure the auth token is not null
            assertNotNull(auth.authToken(), "Auth token should not be null after registration");

            // Logout using the auth token
            assertDoesNotThrow(() -> facade.logout(auth.authToken()));
        } catch (Exception e) {
            fail("An unexpected exception was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLogoutNegative() {
        // Attempt to logout with an invalid auth token
        String invalidAuthToken = "invalid-token";
        assertThrows(ResponseException.class, () -> facade.logout(invalidAuthToken));
    }

    @Test
    @DisplayName("Create Game - Positive")
    public void testListGamesDebug() {
        try {
            User user = new User("debug_user", "password123", "debug@email.com");
            Auth auth = facade.register(user);
            assertNotNull(auth.authToken(), "Auth token should not be null");

            facade.createGame("game1", auth.authToken());
            facade.createGame("game2", auth.authToken());

            Game[] games = facade.listGames(auth.authToken());
            assertNotNull(games, "Games list should not be null");
            assertTrue(games.length >= 2, "Games list should contain at least 2 games");

            Arrays.stream(games).forEach(game ->
                    System.out.println("GameID: " + game.gameID() + ", GameName: " + game.gameName())
            );
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Create Game - Invalid Auth Token")
    public void testCreateGameInvalidAuthToken() {
        // Attempt to create a game with an invalid auth token
        String invalidAuthToken = "invalid_token";
        String gameName = "test_game_invalid_auth";

        assertThrows(ResponseException.class, () -> facade.createGame(gameName, invalidAuthToken),
                "Expected ResponseException when creating a game with an invalid auth token");
    }

    @Test
    @DisplayName("Create Game - Invalid Game Name")
    public void testCreateGameInvalidGameName() {
        try {
            // Register a user to obtain a valid auth token
            User user = new User("game_creator_invalid_name", "password123", "creator_invalid@email.com");
            Auth auth = facade.register(user);
            assertNotNull(auth.authToken(), "Auth token should not be null after registration");

            // Attempt to create a game with an invalid game name
            assertThrows(IllegalArgumentException.class, () -> facade.createGame(null, auth.authToken()),
                    "Expected IllegalArgumentException when creating a game with a null game name");

            assertThrows(IllegalArgumentException.class, () -> facade.createGame("", auth.authToken()),
                    "Expected IllegalArgumentException when creating a game with an empty game name");
        } catch (Exception e) {
            fail("Unexpected exception during invalid game name test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Game - No Auth Token")
    public void testCreateGameNoAuthToken() {
        String gameName = "test_game_no_auth";

        // Attempt to create a game with no auth token
        assertThrows(IllegalArgumentException.class, () -> facade.createGame(gameName, null),
                "Expected IllegalArgumentException when creating a game with no auth token");

        assertThrows(IllegalArgumentException.class, () -> facade.createGame(gameName, ""),
                "Expected IllegalArgumentException when creating a game with an empty auth token");
    }

    @Test
    @DisplayName("List Games - Positive Case")
    public void testListGamesPositive() {
        try {
            // Register a user to obtain a valid auth token
            User user = new User("list_games_user", "password123", "listgames@email.com");
            Auth auth = facade.register(user);
            assertNotNull(auth.authToken(), "Auth token should not be null");

            // Create a few games
            facade.createGame("game1", auth.authToken());
            facade.createGame("game2", auth.authToken());

            // List the games
            Game[] games = facade.listGames(auth.authToken());
            assertNotNull(games, "Games list should not be null");
            assertTrue(games.length >= 2, "Games list should contain at least 2 games");

            // Verify that game names are returned
            boolean game1Found = Arrays.stream(games).anyMatch(game -> "game1".equals(game.gameName()));
            boolean game2Found = Arrays.stream(games).anyMatch(game -> "game2".equals(game.gameName()));

            assertTrue(game1Found, "Game1 should be in the list of games");
            assertTrue(game2Found, "Game2 should be in the list of games");
        } catch (Exception e) {
            fail("Unexpected exception during listGames: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("List Games - Negative Case")
    public void testListGamesNegative() {
        // Use an invalid auth token
        String invalidAuthToken = "invalid_auth_token";

        // Attempt to list games with an invalid auth token
        assertThrows(ResponseException.class, () -> facade.listGames(invalidAuthToken),
                "Expected ResponseException when listing games with an invalid auth token");
    }


}
