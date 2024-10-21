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
}