package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import logging.LoggerManager;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLUserDAO implements UserDAO {
    private static final Logger LOGGER = LoggerManager.getLogger(MySQLUserDAO.class.getName());

    static {
        LOGGER.setLevel(Level.INFO);
    }

    public MySQLUserDAO() throws DataAccessException {
        LOGGER.info("Initializing MySQLUserDAO and configuring database...");
        configureDatabase();
    }

    @Override
    public User addUser(User user) throws DataAccessException {
        LOGGER.log(Level.INFO, "Adding user: {0}", user.username());
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String insertStatement = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertStatement, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.email());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    LOGGER.log(Level.INFO, "User added successfully: {0}", user.username());
                    return new User(user.username(), hashedPassword, user.email());
                } else {
                    LOGGER.warning("Failed to retrieve generated ID for new user.");
                    throw new DataAccessException("Failed to retrieve generated ID for new user.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to add user: {0}", e.getMessage());
            throw new DataAccessException("Unable to add user: " + e.getMessage());
        }
    }

    public Collection<User> listUsers() throws DataAccessException {
        LOGGER.info("Listing all users...");
        String selectStatement = "SELECT username, password_hash, email FROM users";
        Collection<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String email = rs.getString("email");
                users.add(new User(username, passwordHash, email));
                LOGGER.log(Level.FINE, "User retrieved: {0}", username);
            }
            LOGGER.info("All users listed successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to list users: {0}", e.getMessage());
            throw new DataAccessException("Unable to list users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        LOGGER.log(Level.INFO, "Retrieving user: {0}", username);
        String selectStatement = "SELECT username, password_hash, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("password_hash");
                    String email = rs.getString("email");
                    LOGGER.log(Level.INFO, "User found: {0}", username);
                    return new User(username, passwordHash, email);
                }
            }
            LOGGER.log(Level.WARNING, "User not found: {0}", username);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to retrieve user: {0}", e.getMessage());
            throw new DataAccessException("Unable to retrieve user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        LOGGER.log(Level.INFO, "Deleting user: {0}", username);
        String deleteStatement = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteStatement)) {

            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.log(Level.INFO, "User deleted: {0}", username);
            } else {
                LOGGER.log(Level.WARNING, "No user found to delete: {0}", username);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to delete user: {0}", e.getMessage());
            throw new DataAccessException("Unable to delete user: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        LOGGER.info("Deleting all users...");
        String truncateStatement = "TRUNCATE TABLE users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(truncateStatement)) {

            ps.executeUpdate();
            LOGGER.info("All users deleted successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to delete all users: {0}", e.getMessage());
            throw new DataAccessException("Unable to delete all users: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) throws DataAccessException {
        LOGGER.log(Level.INFO, "Updating user: {0}", user.username());
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String updateStatement = "UPDATE users SET password_hash = ?, email = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateStatement)) {

            ps.setString(1, hashedPassword);
            ps.setString(2, user.email());
            ps.setString(3, user.username());
            ps.executeUpdate();
            LOGGER.log(Level.INFO, "User updated successfully: {0}", user.username());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to update user: {0}", e.getMessage());
            throw new DataAccessException("Unable to update user: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        LOGGER.info("Configuring database for users...");
        try {
            DatabaseManager.createDatabase();
            LOGGER.info("Database created or already exists.");
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to create database: {0}", e.getMessage());
            throw new DataAccessException("Failed to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INT NOT NULL AUTO_INCREMENT,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password_hash VARCHAR(255) NOT NULL,
                            email VARCHAR(255),
                            PRIMARY KEY (id)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """;

            try (PreparedStatement preparedStatement = conn.prepareStatement(createTable)) {
                preparedStatement.executeUpdate();
                LOGGER.info("Users table created or already exists.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Unable to configure database: {0}", ex.getMessage());
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    @Override
    public boolean verifyUserPassword(String username, String providedClearTextPassword) throws DataAccessException {
        LOGGER.log(Level.INFO, "Verifying password for user: {0}", username);
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            LOGGER.log(Level.FINE, "Executing query to fetch password hash for user: {0}", username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                LOGGER.log(Level.FINE, "Retrieved password hash for user {0}: {1}", new Object[]{username, storedHash});
                LOGGER.log(Level.FINE, "Provided password: {0}", providedClearTextPassword);

                boolean matches = BCrypt.checkpw(providedClearTextPassword, storedHash);

                LOGGER.log(Level.INFO, "Password verification result for user {0}: {1}", new Object[]{username, matches});
                return matches;
            }
            LOGGER.log(Level.WARNING, "User not found for password verification: {0}", username);
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to verify user password: {0}", e.getMessage());
            throw new DataAccessException("Failed to verify user password: " + e.getMessage());
        }
    }

}
