
package ui;

import chess.ChessBoard;
import chess.ChessGame;
import logging.LoggerManager;
import model.Game;

import java.util.logging.Logger;

public class InGameClient {
    private static final Logger LOGGER = LoggerManager.getLogger(InGameClient.class.getName());
    private final String serverUrl;
    private final Repl repl;
    private Game game;

    public InGameClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.game = repl.getGame();
    }

    public String eval(String input) {
        String[] tokens = input.split("\s+");
        String command = tokens[0].toLowerCase();
        if (repl.getIsObserver()) {
            switch (command) {
                case "help":
                    return getObserverHelpText();
                case "renderboard":
                    return renderBoard();
                case "leave":
                    return leave();
                default:
                    return "Unknown command. Type 'help' for a list of commands.";
            }
        } else {
            return switch (command) {
                case "help" -> getHelpText();
                case "renderboard" -> renderBoard();
                case "makemove" -> makeMove();
                case "resign" -> resignGame();
                case "leave" -> leave();
                default -> "Unknown command. Type 'help' for a list of commands.";
            };
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "makemove     - Make a move.\n" +
                "resign       - Resign from game.\n" +
                "leave     - Exit the game.\n";
    }

    private String getObserverHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "leave     - Exit the game.\n";
    }

    private String renderBoard() {
        game = repl.getGame();
        ChessGame myGame = game.game();
        LOGGER.info("ChessGame: " + myGame);

        ChessBoard board = myGame.getBoard();
        LOGGER.info("ChessBoard: " + board);


        if (board == null) {
            return "No active game board found.";
        }

        // Render the board from both perspectives
        System.out.println(EscapeSequences.RESET_TEXT_COLOR + "White's Perspective:");
        ChessBoardRenderer.renderChessBoard(board, true);

        System.out.println("\nBlack's Perspective:");
        ChessBoardRenderer.renderChessBoard(board, false);

        return "Chessboard rendered successfully.";
    }


    private String makeMove() {
        // Stub for making moves
        return "Making a move";
    }

    private String leave() {
        try {
            repl.getWebSocketHandler().leaveGame(repl.getAuthToken(), repl.getGame().gameID());
            repl.getWebSocketHandler().disconnect();
            repl.changeState(UserState.LOGGEDIN);
            repl.setIsObserver(false);
            LOGGER.info("Successfully left the game.");
            return "Leaving Game";
        } catch (Exception e) {
            LOGGER.warning("Error leaving game: " + e.getMessage());
            return "Error leaving game: " + e.getMessage();
        }
    }

    private String resignGame() {
        try {
            repl.getWebSocketHandler().resignGame(repl.getAuthToken(), repl.getGame().gameID());
            LOGGER.info("Successfully resigned from the game.");
            return "You have resigned from the game.";
        } catch (Exception e) {
            LOGGER.warning("Error resigning from game: " + e.getMessage());
            return "Error resigning from game: " + e.getMessage();
        }
    }


}