package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import model.User;
import model.Auth;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import service.UserService;
import exception.ResponseException;
import dataaccess.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {

    private static User existingUser;
    private static User newUser;
    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static UserService userService;

    private String existingAuthToken;

    @BeforeAll
    public static void init() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO, authDAO);

        // Hash the existing user's password before setting it up
        String hashedPassword = BCrypt.hashpw("existingPassword", BCrypt.gensalt());
        existingUser = new User("ExistingUser", hashedPassword, "existingUser@mail.com");
        newUser = new User("NewUser", BCrypt.hashpw("newPassword", BCrypt.gensalt()), "newUser@mail.com");
    }

    @BeforeEach
    public void setup() throws DataAccessException, ResponseException {
        // Clear the DAOs before each test
        authDAO.deleteAllAuthTokens();
        userDAO.deleteAllUsers();

        // Register existing user and get its token
        Auth existingAuth = userService.register(existingUser);
        existingAuthToken = existingAuth.authToken();
    }

    @Test
    @DisplayName("Register a New User")
    public void testRegisterUserSuccess() throws DataAccessException, ResponseException {
        // Test successful user registration
        Auth newAuth = userService.register(newUser);
        assertNotNull(newAuth, "Auth should not be null after successful registration.");
        assertEquals(newUser.username(), newAuth.username(), "The registered username should match.");
    }

    @Test
    @DisplayName("Register Existing User Should Fail")
    public void testRegisterExistingUserFails() throws DataAccessException {
        // Test registration of an existing user, should fail
        ResponseException thrown = assertThrows(ResponseException.class, () -> {
            userService.register(existingUser);
        });
        assertEquals(403, thrown.statusCode(), "Status code should be 403 for already existing user.");
    }

    @Test
    @DisplayName("Successful User Login")
    public void testLoginSuccess() throws DataAccessException, ResponseException {
        // Attempt to log in with the correct password
        User loginAttemptUser = new User("ExistingUser", "existingPassword", "existingUser@mail.com");
        Auth auth = userService.login(loginAttemptUser);

        assertNotNull(auth, "Auth token should not be null after successful login.");
        assertEquals(existingUser.username(), auth.username(), "Logged-in username should match.");
    }

    @Test
    @DisplayName("Login with Incorrect Password Should Fail")
    public void testLoginWithIncorrectPassword() throws DataAccessException {
        // Test failed login due to wrong password
        ResponseException thrown = assertThrows(ResponseException.class, () -> {
            userService.login(new User(existingUser.username(), "wrongPassword", existingUser.email()));
        });
        assertEquals(401, thrown.statusCode(), "Status code should be 401 for incorrect login.");
    }

    @Test
    @DisplayName("Logout Success")
    public void testLogoutSuccess() throws DataAccessException, ResponseException {
        // Test successful logout
        userService.logout(existingAuthToken);
        assertNull(authDAO.getAuth(existingAuthToken), "Auth token should be removed after logout.");
    }

    @Test
    @DisplayName("Logout with Invalid AuthToken Should Fail")
    public void testLogoutInvalidToken() {
        // Test logout with invalid token
        ResponseException thrown = assertThrows(ResponseException.class, () -> {
            userService.logout("invalidAuthToken");
        });
        assertEquals(401, thrown.statusCode(), "Status code should be 401 for invalid auth token.");
    }

    // Extra test cases for new methods :(
    @Test
    @DisplayName("Get User by Auth Token Success")
    public void testGetUserSuccess() throws DataAccessException, ResponseException {
        // Test successful retrieval of user by valid auth token
        User retrievedUser = userService.getUser(existingAuthToken);
        assertNotNull(retrievedUser, "Retrieved user should not be null.");
        assertEquals(existingUser.username(), retrievedUser.username(), "The retrieved username should match.");
    }

    @Test
    @DisplayName("Get User by Invalid Auth Token Should Fail")
    public void testGetUserInvalidToken() {
        // Test get user with invalid auth token
        ResponseException thrown = assertThrows(ResponseException.class, () -> {
            userService.getUser("invalidAuthToken");
        });
        assertEquals(401, thrown.statusCode(), "Status code should be 401 for unauthorized access.");
    }

    @Test
    @DisplayName("Check User is Logged In")
    public void testIsLoggedInSuccess() throws DataAccessException, ResponseException {
        // Test that the user is logged in with a valid auth token
        assertTrue(userService.isLoggedIn(existingAuthToken), "The user should be logged in with a valid auth token.");
    }

    @Test
    @DisplayName("Check User is Not Logged In with Invalid Auth Token")
    public void testIsLoggedInInvalidToken() throws DataAccessException, ResponseException {
        // Test that the user is not logged in with an invalid auth token
        assertFalse(userService.isLoggedIn("invalidAuthToken"), "The user should not be logged in with an invalid auth token.");
    }
}