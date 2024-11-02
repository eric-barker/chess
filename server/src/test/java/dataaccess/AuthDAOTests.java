package dataaccess;

import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.mysql.MySQLAuthDAO;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {

        authDAO = new MySQLAuthDAO();  // initializing DAO before each test
    }

    @Test
    public void testAddAndGetAuth() throws DataAccessException {
        // add an auth token and then retrieve it
        authDAO.addAuth("authToken1", "user1");
        Auth auth = authDAO.getAuth("authToken1");
        assertNotNull(auth, "Expected non-null Auth object after adding.");
        assertEquals("user1", auth.username(), "The username should be user1.");
    }

    @Test
    public void testUpdateAuth() throws DataAccessException {
        // Add the original token, then update it to a new one
        authDAO.addAuth("authToken1", "user1");

        System.out.println("Auth tokens before update:");
        for (Auth auth : authDAO.listTokens()) {
            System.out.println("Token: " + auth.authToken() + ", Username: " + auth.username());
        }

        authDAO.updateAuth("authToken1", "newAuthToken");  // updating token

        System.out.println("Auth tokens after update:");
        for (Auth auth : authDAO.listTokens()) {
            System.out.println("Token: " + auth.authToken() + ", Username: " + auth.username());
        }

        // Retrieve the updated token
        Auth updatedAuth = authDAO.getAuth("newAuthToken");
        assertNotNull(updatedAuth, "Updated auth token should be found.");
        assertEquals("user1", updatedAuth.username(), "Username should remain the same after update.");

        // Check that the old token no longer exists
        assertNull(authDAO.getAuth("authToken1"), "Old token should be removed.");
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        // Add and then delete the token
        authDAO.addAuth("authToken1", "user1");
        authDAO.deleteAuth("authToken1");

        // Check that it was deleted
        assertNull(authDAO.getAuth("authToken1"), "Expected auth token to be deleted.");
    }


    @Test
    public void testListTokens() throws DataAccessException {
        // Add two tokens, then list them
        authDAO.addAuth("authToken1", "user1");
        authDAO.addAuth("authToken2", "user2");
        Collection<Auth> tokens = authDAO.listTokens();

        // Make sure the size is correct
        assertEquals(2, tokens.size(), "Should have exactly 2 auth tokens.");
    }

    @Test
    public void testDeleteAllAuthTokens() throws DataAccessException {
        // Add some tokens
        authDAO.addAuth("authToken1", "user1");
        authDAO.addAuth("authToken2", "user2");

        // Now clear them all
        authDAO.deleteAllAuthTokens();

        // Make sure no tokens remain
        assertTrue(authDAO.listTokens().isEmpty(), "All auth tokens should be deleted.");
    }
}