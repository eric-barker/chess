
package ui;

import chess.ChessBoard;

public class InGameClient {
    private final String serverUrl;
    private final Repl repl;

    public InGameClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        String[] tokens = input.split("\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return getHelpText();
            case "renderboard":
                return renderBoard();
            case "makemove":
                return makeMove();
            case "exitgame":
                return exitGame();
            default:
                return "Unknown command. Type 'help' for a list of commands.";
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "makemove     - Make a move.\n" +
                "exitgame     - Exit the game.\n";
    }

    private String renderBoard() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

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