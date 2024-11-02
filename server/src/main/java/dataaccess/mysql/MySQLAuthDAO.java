package dataaccess.mysql;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void addAuth(String authToken, String username) throws DataAccessException {
        String insertStatement = "INSERT INTO auth (auth_token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertStatement)) {

            ps.setString(1, authToken);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to add auth token: " + e.getMessage());
        }
    }

    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        // Placeholder for retrieving an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Collection<Auth> listTokens() throws DataAccessException {
        // Placeholder for listing all auth tokens
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) throws DataAccessException {
        // Placeholder for updating an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // Placeholder for deleting an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllAuthTokens() throws DataAccessException {
        // Placeholder for deleting all auth tokens
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void configureDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                        CREATE TABLE IF NOT EXISTS auth (
                            auth_token VARCHAR(255) PRIMARY KEY,
                            username VARCHAR(50) NOT NULL
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
