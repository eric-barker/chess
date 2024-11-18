package client;

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
        User user = new User("testuser", "password123", "testemail@email.com");
        assertDoesNotThrow(() -> {
            User registeredUser = facade.register(user);
            assertNotNull(registeredUser);
            assertEquals("testuser", registeredUser.getUsername());
        });
    }

    @Test
    public void testRegisterNegative() {
        User invalidUser = new User("", "", null); // Invalid user
        assertThrows(ResponseException.class, () -> facade.register(invalidUser));
    }

    @Test
    public void testLoginPositive() {
        User user = new User("testuser", "password123", null);
        // Ensure the user is registered first
        assertDoesNotThrow(() -> facade.register(user));

        // Test login with correct credentials
        assertDoesNotThrow(() -> {
            User loggedInUser = facade.login("testuser", "password123");
            assertNotNull(loggedInUser);
            assertEquals("testuser", loggedInUser.getUsername());
        });
    }

    @Test
    public void testLoginNegative() {
        // Test login with incorrect credentials
        assertThrows(ResponseException.class, () -> facade.login("wronguser", "wrongpassword"));
    }
}
