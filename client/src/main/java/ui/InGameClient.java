
package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.Game;

public class InGameClient {
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
                case "exitgame":
                    return exitGame();
                default:
                    return "Unknown command. Type 'help' for a list of commands.";
            }
        } else {
            return switch (command) {
                case "help" -> getHelpText();
                case "renderboard" -> renderBoard();
                case "makemove" -> makeMove();
                case "exitgame" -> exitGame();
                default -> "Unknown command. Type 'help' for a list of commands.";
            };
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "makemove     - Make a move.\n" +
                "exitgame     - Exit the game.\n";
    }

    private String getObserverHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "exitgame     - Exit the game.\n";
    }

    private String renderBoard() {

        ChessBoard board = game.game().getBoard();
//        board.resetBoard();

        if (board == null) {
            return "No active game board found.";
        }

        // Render the board from both perspectives
        System.out.println("White's Perspective:");
        ChessBoardRenderer.renderChessBoard(board, true);

        System.out.println("\nBlack's Perspective:");
        ChessBoardRenderer.renderChessBoard(board, false);

        return "Chessboard rendered successfully.";
    }


    private String makeMove() {
        // Stupb for making moves
        return "Making a move";
    }

    private String exitGame() {
        repl.changeState(UserState.LOGGEDIN);
        return "Exiting Game";
    }


}