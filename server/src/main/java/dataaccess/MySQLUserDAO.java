package dataaccess;

import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.User;

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
    public User addUser(User user) throws ResponseException {
        String statement = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.username());  // Accessor without "get" prefix
            ps.setString(2, user.password());
            ps.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new User(user.username(), user.password(), user.email());
                } else {
                    throw new ResponseException("Failed to retrieve generated ID for new user.");
                }
            }
        } catch (SQLException e) {
            throw new ResponseException("Unable to add user: " + e.getMessage());
        }
    }

    @Override
    public Collection<User> listUsers() throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getUser(String username) throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteUser(String username) throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllUsers() {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void update(User user) {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
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
}