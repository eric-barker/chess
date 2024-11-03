package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.GameDAO;
import model.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public Game createGame(Game game) throws DataAccessException {
        String insertStatement = "INSERT INTO games (white_username, black_username, game_name, game_data) VALUES (?, ?, ?, ?)";
        int gameID = executeUpdate(insertStatement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());

        if (gameID > 0) {
            return new Game(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        } else {
            throw new DataAccessException("Game creation failed, no ID obtained.");
        }
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
                            (ChessGame) rs.getObject("game_data")  // Assuming ChessGame is serializable
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
        executeUpdate(updateStatement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), game.gameID());
    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        String selectSql = "SELECT gameID, white_username, black_username, game_name, game_data FROM games";
        Collection<Game> gameList = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                gameList.add(new Game(
                        resultSet.getInt("gameID"),
                        resultSet.getString("white_username"),
                        resultSet.getString("black_username"),
                        resultSet.getString("game_name"),
                        (ChessGame) resultSet.getObject("game_data")  // Assuming serialization
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

    /**
     * Utility method to execute SQL update statements with dynamic parameters.
     * Returns the generated key if available; otherwise, returns 0.
     */
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                switch (param) {
                    case String s -> ps.setString(i + 1, s);
                    case Integer integer -> ps.setInt(i + 1, integer);
                    case ChessGame chessGame -> ps.setObject(i + 1, param); // Assuming ChessGame serializable
                    case null -> ps.setNull(i + 1, Types.NULL);
                    default -> {
                    }
                }
            }

            ps.executeUpdate();

            // Retrieve generated keys
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("Unable to execute update: " + statement + ", " + e.getMessage());
        }
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
                        game_data BLOB
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
