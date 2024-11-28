package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.User;
import model.Game;
import model.Auth;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private MySQLUserDAO userDAO;
    private MySQLGameDAO gameDAO;
    private MySQLAuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Initialize DAOs and ClearService before each test
        userDAO = new MySQLUserDAO();
        gameDAO = new MySQLGameDAO();
        authDAO = new MySQLAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        clearService.clear();
    }

    @Test
    public void testClearAllData() throws Exception {
        // Insert some users, games, and auth tokens
        User user1 = new User("user1", "password1", "user1@mail.com");
        User user2 = new User("user2", "password2", "user2@mail.com");

        ChessGame chessGame = new ChessGame();
        Game game1 = new Game(0, "user1", null, "Game 1", chessGame);
        Game game2 = new Game(0, null, "user2", "Game 2", chessGame);

        Auth auth1 = new Auth("authToken1", "user1");
        Auth auth2 = new Auth("authToken2", "user2");

        // Insert data into the DAOs
        userDAO.addUser(user1);
        userDAO.addUser(user2);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        authDAO.addAuth(auth1.authToken(), auth1.username());
        authDAO.addAuth(auth2.authToken(), auth2.username());

        // Verify that data has been inserted
        assertEquals(2, userDAO.listUsers().size(), "Users should be added.");
        assertEquals(2, gameDAO.listGames().size(), "Games should be added.");
        Auth myAuth1 = authDAO.getAuth("authToken1");
        Auth myAuth2 = authDAO.getAuth("authToken2");
        assertNotNull(myAuth1, "Auth token 1 should exist.");
        assertNotNull(myAuth2, "Auth token 2 should exist.");

        // Call the clear service
        clearService.clear();

        // Verify that all data has been cleared
        assertEquals(0, userDAO.listUsers().size(), "Users should be cleared.");
        assertEquals(0, gameDAO.listGames().size(), "Games should be cleared.");
        assertNull(authDAO.getAuth("authToken1"), "Auth token 1 should be cleared.");
        assertNull(authDAO.getAuth("authToken2"), "Auth token 2 should be cleared.");
    }

    @Test
    public void testClearEmptyData() throws Exception {
        // Ensure DAOs are empty to begin with
        assertEquals(0, userDAO.listUsers().size(), "Users should be empty initially.");
        assertEquals(0, gameDAO.listGames().size(), "Games should be empty initially.");
        assertEquals(0, authDAO.listTokens().size(), "Auth tokens should be empty initially.");

        // Call the clear service
        clearService.clear();

        // Verify that DAOs are still empty after calling clear
        assertEquals(0, userDAO.listUsers().size(), "Users should still be empty.");
        assertEquals(0, gameDAO.listGames().size(), "Games should still be empty.");
        assertEquals(0, authDAO.listTokens().size(), "Auth tokens should still be empty.");
    }
}


