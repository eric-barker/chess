
package ui;

import chess.*;
import logging.LoggerManager;
import model.Game;

import java.util.Collection;
import java.util.Scanner;
import java.util.logging.Logger;

public class InGameClient {
    private static final Logger LOGGER = LoggerManager.getLogger(InGameClient.class.getName());
    private final String serverUrl;
    private final Repl repl;


    public InGameClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
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
                case "legalmoves":
                    return renderLegalMoves();
                case "leave":
                    return leave();
                default:
                    return "Unknown command. Type 'help' for a list of commands.";
            }
        } else {
            return switch (command) {
                case "help" -> getHelpText();
                case "renderboard" -> renderBoard();
                case "legalmoves" -> renderLegalMoves();
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
                "legalmoves   - highlight legal moves" +
                "resign       - Resign from game.\n" +
                "leave        - Exit the game.\n";
    }

    private String getObserverHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "legalmoves   - highlight legal moves" +
                "leave        - Exit the game.\n";
    }

    private String renderBoard() {
        Game game = repl.getGame();
        ChessGame myGame = game.game();
        LOGGER.info("ChessGame: " + myGame);

        ChessBoard board = myGame.getBoard();
        LOGGER.info("ChessBoard: " + board);


        if (board == null) {
            return "No active game board found.";
        }

        LOGGER.info("Repl Username: " + repl.getUsername());
        LOGGER.info("Repl getGame().whiteUsername() " + repl.getGame().whiteUsername());
        LOGGER.info("Repl getGame().blackUsername() " + repl.getGame().blackUsername());
        if (repl.getUsername().equals(repl.getGame().blackUsername())) {
            LOGGER.info("Rendering board from black's perspective");
            ChessBoardRenderer.renderChessBoard(board, false);
        } else {
            LOGGER.info("Rendering board from white's perspective");
            ChessBoardRenderer.renderChessBoard(board, true);
        }

        return "Chessboard rendered successfully.";
    }

    private String renderLegalMoves() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your square in the format '[col][row]' (e.g., 'e2'): ");
        String input = scanner.nextLine().trim();

        if (input.length() != 2) {
            return "Invalid move format. Please use the format '[col][row]' (e.g., 'e2').";
        }

        try {
            ChessPosition evalPosition = parseChessNotation(input);

            Game game = repl.getGame();
            ChessGame myGame = game.game();
            Collection<ChessMove> legalMoves = myGame.validMoves(evalPosition);
            ChessBoard board = myGame.getBoard();

            if (board == null) {
                return "No active game board found.";
            }

            if (repl.getUsername().equals(repl.getGame().blackUsername())) {
                LOGGER.info("Rendering legal moves from black's perspective");
                ChessBoardRenderer.renderLegalMoves(board, evalPosition, legalMoves, false);
            } else {
                LOGGER.info("Rendering legal moves from white's perspective");
                ChessBoardRenderer.renderLegalMoves(board, evalPosition, legalMoves, true);
            }

            return "Legal moves for position " + evalPosition.toString() + "rendered successfully.";
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid chess notation: " + e.getMessage());
            return "Invalid chess notation. Please use the format '[col][row]' (e.g., 'e2').";
        }
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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            return "Resignation canceled.";
        }

        try {
            repl.getWebSocketHandler().resignGame(repl.getAuthToken(), repl.getGame().gameID());
            LOGGER.info("Successfully resigned from the game.");
            return "You have resigned from the game.";
        } catch (Exception e) {
            LOGGER.warning("Error resigning from game: " + e.getMessage());
            return "Error resigning from game: " + e.getMessage();
        }
    }

    private String makeMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your move in the format 'start end' (e.g., 'e2 e4'): ");
        String input = scanner.nextLine().trim();
        String[] tokens = input.split("\\s+");

        if (tokens.length != 2) {
            return "Invalid move format. Please use the format 'start end' (e.g., 'e2 e4').";
        }

        try {
            String start = tokens[0].toLowerCase();
            String end = tokens[1].toLowerCase();

            ChessPosition startPosition = parseChessNotation(start);
            ChessPosition endPosition = parseChessNotation(end);

            // Check for pawn promotion
            System.out.print("Enter promotion piece type if applicable (e.g., QUEEN), or press Enter to skip: ");
            String promotionInput = scanner.nextLine().trim().toUpperCase();

            ChessPiece.PieceType promotionPiece = null;
            if (!promotionInput.isEmpty()) {
                try {
                    promotionPiece = ChessPiece.PieceType.valueOf(promotionInput);
                } catch (IllegalArgumentException e) {
                    return "Invalid promotion piece type. Please enter a valid piece type (e.g., QUEEN).";
                }
            }

            ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
            repl.getWebSocketHandler().makeMove(repl.getAuthToken(), repl.getGame().gameID(), move);

            LOGGER.info("Move sent: " + move);
            return "Move sent to the server.";
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid move input: " + e.getMessage());
            return "Invalid chess notation. Please use standard format (e.g., 'e2 e4').";
        } catch (Exception e) {
            LOGGER.warning("Error making move: " + e.getMessage());
            return "Error making move: " + e.getMessage();
        }
    }

    private ChessPosition parseChessNotation(String notation) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Invalid chess notation: " + notation);
        }

        char columnChar = notation.charAt(0);
        char rowChar = notation.charAt(1);

        if (columnChar < 'a' || columnChar > 'h' || rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Chess notation out of bounds: " + notation);
        }

        int row = rowChar - '0';         // Convert '1'-'8' directly to 1-8
        int col = columnChar - 'a' + 1; // Convert 'a'-'h' to 1-8

        return new ChessPosition(row, col);
    }

}