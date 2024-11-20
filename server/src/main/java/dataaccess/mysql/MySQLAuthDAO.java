package dataaccess.mysql;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import logging.LoggerManager;
import model.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLAuthDAO implements AuthDAO {
    private static final Logger logger = LoggerManager.getLogger(MySQLAuthDAO.class.getName());

    static {
        logger.setLevel(Level.INFO); // Set local logging level to INFO
    }

    public MySQLAuthDAO() throws DataAccessException {
        logger.info("Initializing MySQLAuthDAO and configuring database...");
        configureDatabase();
    }

    @Override
    public void addAuth(String username, String authToken) throws DataAccessException {
        logger.log(Level.INFO, "Adding auth token for username: {0}", username);
        String insertStatement = "INSERT INTO auth (username, auth_token) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertStatement)) {

            ps.setString(1, username);
            ps.setString(2, authToken);
            ps.executeUpdate();
            logger.log(Level.INFO, "Auth token added successfully for username: {0}", username);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to add auth token for username: {0}", username);
            throw new DataAccessException("Failed to add auth token: " + e.getMessage());
        }
    }

    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        logger.log(Level.INFO, "Fetching auth token: {0}", authToken);
        String selectStatement = "SELECT username, auth_token FROM auth WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            ps.setString(1, authToken);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Auth auth = new Auth(rs.getString("username"), rs.getString("auth_token"));
                    logger.log(Level.INFO, "Auth token fetched successfully: {0}", auth);
                    return auth;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to fetch auth token: {0}", authToken);
            throw new DataAccessException("Failed to retrieve auth token: " + e.getMessage());
        }
        logger.log(Level.WARNING, "Auth token not found: {0}", authToken);
        return null;
    }

    @Override
    public Collection<Auth> listTokens() throws DataAccessException {
        logger.info("Fetching all auth tokens...");
        String selectStatement = "SELECT username, auth_token FROM auth";
        Collection<Auth> tokens = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Auth auth = new Auth(rs.getString("username"), rs.getString("auth_token"));
                tokens.add(auth);
                logger.log(Level.FINE, "Fetched auth token: {0}", auth);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to list auth tokens");
            throw new DataAccessException("Failed to list auth tokens: " + e.getMessage());
        }
        logger.info("All auth tokens fetched successfully.");
        return tokens;
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) throws DataAccessException {
        logger.log(Level.INFO, "Updating auth token from {0} to {1}", new Object[]{oldAuthToken, newAuthToken});
        String updateStatement = "UPDATE auth SET auth_token = ? WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateStatement)) {

            ps.setString(1, newAuthToken);
            ps.setString(2, oldAuthToken);
            ps.executeUpdate();
            logger.log(Level.INFO, "Auth token updated successfully from {0} to {1}", new Object[]{oldAuthToken, newAuthToken});
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update auth token from {0} to {1}", new Object[]{oldAuthToken, newAuthToken});
            throw new DataAccessException("Failed to update auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        logger.log(Level.INFO, "Deleting auth token: {0}", authToken);
        String deleteStatement = "DELETE FROM auth WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteStatement)) {

            ps.setString(1, authToken);
            ps.executeUpdate();
            logger.log(Level.INFO, "Auth token deleted successfully: {0}", authToken);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete auth token: {0}", authToken);
            throw new DataAccessException("Failed to delete auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllAuthTokens() throws DataAccessException {
        logger.info("Deleting all auth tokens...");
        String truncateStatement = "TRUNCATE TABLE auth";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(truncateStatement)) {

            ps.executeUpdate();
            logger.info("All auth tokens deleted successfully.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete all auth tokens");
            throw new DataAccessException("Failed to delete all the auth tokens: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        logger.info("Configuring database for auth...");
        try {
            DatabaseManager.createDatabase();
            logger.info("Database created or already exists.");
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "Failed to create database");
            throw new DataAccessException("Failed to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                        CREATE TABLE IF NOT EXISTS auth (
                            username VARCHAR(50) NOT NULL,
                            auth_token VARCHAR(255) PRIMARY KEY
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """;

            try (PreparedStatement preparedStatement = conn.prepareStatement(createTable)) {
                preparedStatement.executeUpdate();
                logger.info("Auth table created or already exists.");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to configure database: {0}", ex.getMessage());
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }
}
