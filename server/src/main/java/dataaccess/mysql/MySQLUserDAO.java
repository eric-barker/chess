package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public User addUser(User user) throws DataAccessException {
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
                    return new User(user.username(), hashedPassword, user.email());
                } else {
                    throw new DataAccessException("Failed to retrieve generated ID for new user.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to add user: " + e.getMessage());
        }
    }

    public Collection<User> listUsers() throws DataAccessException {
        String selectStatement = "SELECT username, password_hash, email FROM users";
        Collection<User> users = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement);
             ResultSet rs = ps.executeQuery()) {

            // Loop through the result set and add each user to the collection
            while (rs.next()) {
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String email = rs.getString("email");

                users.add(new User(username, passwordHash, email));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        String selectStatement = "SELECT username, password_hash, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            // Set the username parameter
            ps.setString(1, username);

            // Execute the query and process the result
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("password_hash");
                    String email = rs.getString("email");

                    // Return the User object if found
                    return new User(username, passwordHash, email);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to retrieve user: " + e.getMessage());
        }

        // Return null if the user is not found
        return null;
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        String deleteStatement = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteStatement)) {

            // Set the username parameter
            ps.setString(1, username);

            // Execute the delete operation
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete user: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(truncateStatement)) {

            // Execute the truncate operation
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete all users: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        String updateStatement = "UPDATE users SET password_hash = ?, email = ? WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateStatement)) {

            ps.setString(1, hashedPassword);
            ps.setString(2, user.email());
            ps.setString(3, user.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update user: " + e.getMessage());
        }
    }


    private void configureDatabase() throws DataAccessException {
        // Ensures the database is created if it doesn't exist
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to create database: " + e.getMessage());
        }

        // Establishes a connection and creates the users table if not present
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
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    public void storeUserPassword(String username, String clearTextPassword, String email) throws DataAccessException {
        // Hash the password using bcrypt
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);  // Store the hashed password
            ps.setString(3, email);           // Use the provided email

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to store user password: " + e.getMessage());
        }
    }


    @Override
    public boolean verifyUserPassword(String username, String providedClearTextPassword) throws DataAccessException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return BCrypt.checkpw(providedClearTextPassword, storedHash);
            }
            return false;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to verify user password: " + e.getMessage());
        }
    }
    
}