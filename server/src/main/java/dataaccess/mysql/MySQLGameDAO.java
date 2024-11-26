package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import logging.LoggerManager;
import model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLGameDAO implements GameDAO {

    private static final Logger LOGGER = LoggerManager.getLogger(MySQLGameDAO.class.getName());
    private final Gson gson = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        LOGGER.info("Initializing MySQLGameDAO...");
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        LOGGER.info("Creating game: " + game);
        String statement = "INSERT INTO games (white_username, black_username, game_name, game_data) VALUES (?, ?, ?, ?)";
        String gameDataJson = gson.toJson(game.game());
        LOGGER.info("Serialized game data: " + gameDataJson);
        int gameID = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson);
        LOGGER.info("Game created with ID: " + gameID);

        return new Game(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        LOGGER.info("Fetching game with ID: " + gameID);
        String statement = "SELECT gameID, white_username, black_username, game_name, game_data FROM games WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Game game = readGame(rs);
                    LOGGER.info("Game retrieved: " + game);
                    return game;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to retrieve game: " + e.getMessage(), e);
            throw new DataAccessException("Failed to retrieve game: " + e.getMessage());
        }
        LOGGER.warning("No game found with ID: " + gameID);
        return null;
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {
        LOGGER.info("Updating game: " + game);
        String statement = "UPDATE games SET white_username = ?, black_username = ?, game_name = ?, game_data = ? WHERE gameID = ?";
        String gameDataJson = gson.toJson(game.game());
        LOGGER.info("Serialized game data for update: " + gameDataJson);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gameDataJson);
            ps.setInt(5, game.gameID());

            int affectedRows = ps.executeUpdate();
            LOGGER.info("Rows affected by update: " + affectedRows);

            if (affectedRows == 0) {
                LOGGER.warning("Update failed: no rows affected.");
                throw new DataAccessException("Update failed: no rows affected.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update game: " + e.getMessage(), e);
            throw new DataAccessException("Failed to update game: " + e.getMessage());
        }
    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        LOGGER.info("Listing all games...");
        String query = "SELECT gameID, white_username, black_username, game_name, game_data FROM games";
        List<Game> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Game game = readGame(rs);
                LOGGER.info("Game found: " + game);
                games.add(game);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to list games: " + e.getMessage(), e);
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }
        LOGGER.info("Total games found: " + games.size());
        return games;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        LOGGER.info("Deleting all games...");
        String statement = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {

            int affectedRows = ps.executeUpdate();
            LOGGER.info("All games deleted. Rows affected: " + affectedRows);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete all games: " + e.getMessage(), e);
            throw new DataAccessException("Failed to delete all games: " + e.getMessage());
        }
    }

    private Game readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("white_username");
        String blackUsername = rs.getString("black_username");
        String gameName = rs.getString("game_name");
        ChessGame gameData = gson.fromJson(rs.getString("game_data"), ChessGame.class);

        Game game = new Game(gameID, whiteUsername, blackUsername, gameName, gameData);
        LOGGER.fine("Deserialized game: " + game);
        return game;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        LOGGER.info("Executing update: " + statement);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else if (params[i] instanceof String param) {
                    ps.setString(i + 1, param);
                } else if (params[i] instanceof Integer param) {
                    ps.setInt(i + 1, param);
                }
            }

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedKey = rs.getInt(1);
                    LOGGER.info("Generated key: " + generatedKey);
                    return generatedKey;
                }
            }
            return 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to execute update: " + e.getMessage(), e);
            throw new DataAccessException("Unable to execute update: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        LOGGER.info("Configuring database...");
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
            LOGGER.info("Database configuration completed.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to configure games table: " + e.getMessage(), e);
            throw new DataAccessException("Failed to configure games table: " + e.getMessage());
        }
    }
}
