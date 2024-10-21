package service;

import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import model.User;
import model.Auth;
import org.junit.jupiter.api.*;
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

        existingUser = new User("ExistingUser", "existingPassword", "existingUser@mail.com");
        newUser = new User("NewUser", "newPassword", "newUser@mail.com");
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
        assertEquals(403, thrown.StatusCode(), "Status code should be 403 for already existing user.");
    }

    @Test
    @DisplayName("Successful User Login")
    public void testLoginSuccess() throws DataAccessException, ResponseException {
        // Test successful login
        Auth auth = userService.login(existingUser);
        assertNotNull(auth, "Auth token should not be null after successful login.");
        assertEquals(existingUser.username(), auth.username(), "Logged in username should match.");
    }

    @Test
    @DisplayName("Login with Incorrect Password Should Fail")
    public void testLoginWithIncorrectPassword() throws DataAccessException {
        // Test failed login due to wrong password
        ResponseException thrown = assertThrows(ResponseException.class, () -> {
            userService.login(new User(existingUser.username(), "wrongPassword", existingUser.email()));
        });
        assertEquals(401, thrown.StatusCode(), "Status code should be 401 for incorrect login.");
    }
}