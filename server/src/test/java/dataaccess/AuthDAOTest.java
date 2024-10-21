package dataaccess;

import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() {

        authDAO = new MemoryAuthDAO();  // initializing DAO before each test
    }

    @Test
    public void testAddAndGetAuth() {
        // add an auth token and then retrieve it
        authDAO.addAuth("authToken1", "user1");
        Auth auth = authDAO.getAuth("authToken1");
        assertNotNull(auth, "Expected non-null Auth object after adding.");
        assertEquals("user1", auth.username(), "The username should be user1.");
    }

    @Test
    public void testUpdateAuth() {
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
}