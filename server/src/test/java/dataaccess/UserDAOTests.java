package dataaccess;

import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.MySQLUserDAO;
import model.User;
import exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();  // Setup fresh instance for each test
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
        // Add a user
        User user = new User("user1", "password1", "user1@mail.com");
        userDAO.addUser(user);

        // Update the user's password
        User updatedUser = new User("user1", "newPassword", "user1@mail.com");
        userDAO.update(updatedUser);

        // Retrieve the updated user
        User retrievedUser = userDAO.getUser("user1");
        assertEquals("newPassword", retrievedUser.password(), "Password should reflect the update.");
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
