package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        return null;
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {

    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void deleteAllGames() throws DataAccessException {

    }

    private void configureDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Failed to create database: " + e.getMessage());
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                    CREATE TABLE IF NOT EXISTS games (
                        gameID INT AUTO_INCREMENT PRIMARY KEY,
                        white_username VARCHAR(50) NOT NULL,
                        black_username VARCHAR(50) NOT NULL,
                        game_name VARCHAR(100) NOT NULL,
                        game_data BLOB  -- Modify type if JSON storage is needed
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """;

            try (PreparedStatement ps = conn.prepareStatement(createTable)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure games table: " + e.getMessage());
        }
    }
}
