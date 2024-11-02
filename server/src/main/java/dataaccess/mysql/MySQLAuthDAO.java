package dataaccess.mysql;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            throw new DataAccessException("Failed to add auth token: " + e.getMessage());
        }
    }

    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        String selectStatement = "SELECT auth_token, username FROM auth WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Auth(rs.getString("auth_token"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<Auth> listTokens() throws DataAccessException {
        String selectStatement = "SELECT auth_token, username FROM auth";
        Collection<Auth> tokens = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tokens.add(new Auth(rs.getString("auth_token"), rs.getString("username")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list auth tokens: " + e.getMessage());
        }
        return tokens;
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) throws DataAccessException {
        String updateStatement = "UPDATE auth SET auth_token = ? WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateStatement)) {

            ps.setString(1, newAuthToken);
            ps.setString(2, oldAuthToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String deleteStatement = "DELETE FROM auth WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteStatement)) {

            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllAuthTokens() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE auth";


        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(truncateStatement)) {

            ps.executeUpdate();
        } catch (SQLException e) {

            throw new DataAccessException("Failed to delete all the auth tokens: " + e.getMessage());
        }
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
