package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.sql.*;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        String insertStatement = "INSERT INTO games (white_username, black_username, game_name, game_data) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setObject(4, game.game());  // Assuming game data can be stored as a BLOB or JSON

            ps.executeUpdate();

            // Retrieve generated game ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int gameID = rs.getInt(1);
                    return new Game(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
        throw new DataAccessException("Game creation failed, no ID obtained.");
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        String selectStatement = "SELECT gameID, white_username, black_username, game_name, game_data FROM games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectStatement)) {

            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Game(
                            rs.getInt("gameID"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            (ChessGame) rs.getObject("game_data")  // Casting based on assumption
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {
        String updateStatement = "UPDATE games SET white_username = ?, black_username = ?, game_name = ?, game_data = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateStatement)) {

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setObject(4, game.game());  // Assuming the data can be stored as BLOB or JSON
            ps.setInt(5, game.gameID());

            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("No game found to update with ID: " + game.gameID());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage());
        }
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
