package dataaccess;

import dataaccess.interfaces.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.mysql.DatabaseManager;
import dataaccess.mysql.MySQLAuthDAO;
import model.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private AuthDAO authDAO;
    private static final boolean USE_MYSQL = true; // Set to `true` for MySQL, `false` for in-memory tests

    @BeforeEach
    public void setUp() throws DataAccessException {
        if (USE_MYSQL) {
            authDAO = new MySQLAuthDAO();
            System.out.println("Using MySQLAuthDAO for tests.");
        } else {
            authDAO = new MemoryAuthDAO();
            System.out.println("Using MemoryAuthDAO for tests.");
        }
        authDAO.deleteAllAuthTokens(); // Clear all tokens before each test
    }

    @Test
    public void testDatabaseConnection() {
        // Check if a connection can be established to the database
        if (USE_MYSQL) {
            try (Connection conn = DatabaseManager.getConnection()) {
                assertNotNull(conn, "Database connection should be established.");
                System.out.println("Database connection successful.");
            } catch (Exception e) {
                fail("Database connection failed: " + e.getMessage());
            }
        } else {
            System.out.println("Skipping database connection test for MemoryAuthDAO.");
        }
    }

    @Test
    public void testAddAndGetAuth() throws DataAccessException {
        authDAO.addAuth("authToken1", "user1");
        Auth auth = authDAO.getAuth("authToken1");
        assertNotNull(auth, "Expected non-null Auth object after adding.");
        assertEquals("user1", auth.username(), "The username should be user1.");
    }

    @Test
    public void testUpdateAuth() throws DataAccessException {
        authDAO.addAuth("authToken1", "user1");
        authDAO.updateAuth("authToken1", "newAuthToken");

        Auth updatedAuth = authDAO.getAuth("newAuthToken");
        assertNotNull(updatedAuth, "Updated auth token should be found.");
        assertEquals("user1", updatedAuth.username(), "Username should remain the same after update.");
        assertNull(authDAO.getAuth("authToken1"), "Old token should be removed.");
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        authDAO.addAuth("authToken1", "user1");
        authDAO.deleteAuth("authToken1");
        assertNull(authDAO.getAuth("authToken1"), "Expected auth token to be deleted.");
    }

    @Test
    public void testListTokens() throws DataAccessException {
        authDAO.addAuth("authToken1", "user1");
        authDAO.addAuth("authToken2", "user2");
        Collection<Auth> tokens = authDAO.listTokens();
        assertEquals(2, tokens.size(), "Should have exactly 2 auth tokens.");
    }

    @Test
    public void testDeleteAllAuthTokens() throws DataAccessException {
        authDAO.addAuth("authToken1", "user1");
        authDAO.addAuth("authToken2", "user2");
        authDAO.deleteAllAuthTokens();
        assertTrue(authDAO.listTokens().isEmpty(), "All auth tokens should be deleted.");
    }
}
