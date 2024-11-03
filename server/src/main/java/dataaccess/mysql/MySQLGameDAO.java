package dataaccess.mysql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {
    private final Gson gson = new Gson();

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        String insertStatement = "INSERT INTO games (white_username, black_username, game_name, game_data) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {

            // Use setNull for nullable values if they are actually null
            if (game.whiteUsername() == null) {
                ps.setNull(1, Types.VARCHAR);
            } else {
                ps.setString(1, game.whiteUsername());
            }

            if (game.blackUsername() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, game.blackUsername());
            }

            ps.setString(3, game.gameName());

            // Serialize the game data to JSON format
            String gameDataJson = gson.toJson(game.game());
            ps.setString(4, gameDataJson);

            ps.executeUpdate();

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
                    ChessGame gameData = gson.fromJson(rs.getString("game_data"), ChessGame.class); // Deserialize JSON to ChessGame
                    return new Game(
                            rs.getInt("gameID"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            gameData
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
        String gameDataJson = gson.toJson(game.game()); // Serialize ChessGame to JSON
        executeUpdate(updateStatement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson, game.gameID());
    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        String selectSql = "SELECT gameID, white_username, black_username, game_name, game_data FROM games";
        Collection<Game> gameList = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ChessGame gameData = gson.fromJson(resultSet.getString("game_data"), ChessGame.class); // Deserialize JSON to ChessGame
                gameList.add(new Game(
                        resultSet.getInt("gameID"),
                        resultSet.getString("white_username"),
                        resultSet.getString("black_username"),
                        resultSet.getString("game_name"),
                        gameData
                ));
            }
        } catch (SQLException sqlException) {
            throw new DataAccessException("Error retrieving games: " + sqlException.getMessage());
        }
        return gameList;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String truncateStatement = "TRUNCATE TABLE games";
        executeUpdate(truncateStatement);
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            String createTable = """
                    CREATE TABLE IF NOT EXISTS games (
                        gameID INT AUTO_INCREMENT PRIMARY KEY,
                        white_username VARCHAR(50),
                        black_username VARCHAR(50),
                        game_name VARCHAR(100),
                        game_data TEXT  -- Store JSON string instead of BLOB
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """;

            try (PreparedStatement ps = conn.prepareStatement(createTable)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure games table: " + e.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

                // Bind each parameter to the PreparedStatement
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) {
                        String json = gson.toJson(p); // Convert the ChessGame to JSON
                        ps.setString(i + 1, json);
                    } else if (param == null) ps.setNull(i + 1, Types.NULL);
                }

                // Execute the update
                ps.executeUpdate();

                // Retrieve and return generated keys (like auto-incremented IDs)
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
                return 0;  // Return 0 if no generated key is obtained

            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update database: " + e.getMessage());
        }
    }
}
