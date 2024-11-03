package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        String statement = "INSERT INTO games (white_username, black_username, game_name, game_data) VALUES (?, ?, ?, ?)";
        String gameDataJson = gson.toJson(game.game()); // Serialize ChessGame to JSON

        int gameID = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson);
        return new Game(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        String statement = "SELECT gameID, white_username, black_username, game_name, game_data FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readGame(rs); // Use helper to map ResultSet to Game
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
        return null; // Return null if no game is found
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

    private Game readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("white_username");
        String blackUsername = rs.getString("black_username");
        String gameName = rs.getString("game_name");
        ChessGame gameData = gson.fromJson(rs.getString("game_data"), ChessGame.class); // Deserialize JSON to ChessGame
        return new Game(gameID, whiteUsername, blackUsername, gameName, gameData);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String param) {
                    ps.setString(i + 1, param);
                } else if (params[i] instanceof Integer param) {
                    ps.setInt(i + 1, param);
                } else if (params[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                }
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0; // Return 0 if no generated key is found

        } catch (SQLException e) {
            throw new DataAccessException("Unable to execute update: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        String createTable = """
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT AUTO_INCREMENT PRIMARY KEY,
                    white_username VARCHAR(50),
                    black_username VARCHAR(50),
                    game_name VARCHAR(100),
                    game_data TEXT
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(createTable)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure games table: " + e.getMessage());
        }
    }

    // Additional CRUD methods to follow...
}
