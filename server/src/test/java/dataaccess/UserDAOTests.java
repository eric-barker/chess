package dataaccess;

import dataaccess.interfaces.UserDAO;
import dataaccess.mysql.MySQLUserDAO;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.mysql.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();  // Setup fresh instance for each test

        // Clear existing data to ensure clean state for each test
        userDAO.deleteAllUsers();
    }

    @Test
    public void testDatabaseConnection() {
        // Attempt to establish a connection
        try (Connection conn = DatabaseManager.getConnection()) {
            assertNotNull(conn, "Connection should be established.");
            System.out.println("Database connection successful.");
        } catch (DataAccessException | SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddAndGetUser() throws DataAccessException {
        // Add a user
        User user = new User("user1", "password1", "user1@mail.com");
        userDAO.addUser(user);

        // Retrieve the user by username
        User retrievedUser = userDAO.getUser("user1");
        assertNotNull(retrievedUser, "User should exist after being added.");
        assertEquals("user1", retrievedUser.username(), "Username should match.");
    }

    @Test
    public void testUpdateUser() throws DataAccessException {
        // Step 1: Add a user with an initial hashed password
        User user = new User("user1", BCrypt.hashpw("password1", BCrypt.gensalt()), "user1@mail.com");
        userDAO.addUser(user);

        // Step 2: Update the user's password to a new value and hash it
        String newPassword = "newPassword";
        User updatedUser = new User("user1", newPassword, "user1@mail.com");
        userDAO.update(updatedUser);

        // Step 3: Retrieve the updated user
        User retrievedUser = userDAO.getUser("user1");

        // Step 4: Use BCrypt.checkpw to verify that the new password matches the stored hash
        assertTrue(BCrypt.checkpw(newPassword, retrievedUser.password()), "Password should reflect the update.");
    }

    @Test
    public void testDeleteUser() throws DataAccessException {
        // Add and delete a user
        User user = new User("user1", "password1", "user1@mail.com");
        userDAO.addUser(user);
        userDAO.deleteUser("user1");

        // Check if the user was deleted
        User retrievedUser = userDAO.getUser("user1");
        assertNull(retrievedUser, "User should be deleted.");
    }

    @Test
    public void testListUsers() throws DataAccessException {
        // Add multiple users
        userDAO.addUser(new User("user1", "password1", "user1@mail.com"));
        userDAO.addUser(new User("user2", "password2", "user2@mail.com"));

        // Check the list of users
        Collection<User> users = userDAO.listUsers();
        assertEquals(2, users.size(), "There should be 2 users in the list.");
    }

    @Test
    public void testDeleteAllUsers() throws DataAccessException {
        // Add users
        userDAO.addUser(new User("user1", "password1", "user1@mail.com"));
        userDAO.addUser(new User("user2", "password2", "user2@mail.com"));

        // Clear all users
        userDAO.deleteAllUsers();

        // Check that no users remain
        Collection<User> users = userDAO.listUsers();
        assertTrue(users.isEmpty(), "All users should be deleted.");
    }
}
