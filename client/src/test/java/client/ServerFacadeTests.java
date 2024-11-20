package client;

import model.Auth;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import model.User;
import exception.ResponseException;

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
}
