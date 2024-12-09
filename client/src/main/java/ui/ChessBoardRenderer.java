package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import logging.LoggerManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ChessBoardRenderer {

    private static final Logger LOGGER = LoggerManager.getLogger(ChessBoardRenderer.class.getName());
    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_WHITE; // White background   "\u001B[47m"
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_BLACK;  // Black background "\u001B[40m"
    private static final String TEXT_COLOR = EscapeSequences.SET_TEXT_COLOR_MIDTONE_GREY;
    private static final String RESET_COLOR = "\u001B[0m";  //  RESET color"\u001B[0m

    // Mapping ChessPiece to symbols
    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();

    static {
        // Map pieces to terminal symbols (can be updated as needed)
        PIECE_SYMBOLS.put("WHITE_PAWN", "P");
        PIECE_SYMBOLS.put("WHITE_ROOK", "R");
        PIECE_SYMBOLS.put("WHITE_KNIGHT", "N");
        PIECE_SYMBOLS.put("WHITE_BISHOP", "B");
        PIECE_SYMBOLS.put("WHITE_QUEEN", "Q");
        PIECE_SYMBOLS.put("WHITE_KING", "K");

        PIECE_SYMBOLS.put("BLACK_PAWN", "p");
        PIECE_SYMBOLS.put("BLACK_ROOK", "r");
        PIECE_SYMBOLS.put("BLACK_KNIGHT", "n");
        PIECE_SYMBOLS.put("BLACK_BISHOP", "b");
        PIECE_SYMBOLS.put("BLACK_QUEEN", "q");
        PIECE_SYMBOLS.put("BLACK_KING", "k");
    }

    public static void renderChessBoard(ChessBoard board, boolean whitePerspective) {
        LOGGER.info("White perspective: " + whitePerspective);
        ChessPiece[][] squares = board.getSquares(); // Assuming this retrieves the board state as a 2D array of ChessPieces.

        // Print column letters (top)
        printColumnLabels(whitePerspective);

        // Traverse the board row by row
        for (int row = 0; row < 8; row++) {
            int actualRow = whitePerspective ? 8 - row : row + 1;
            System.out.print(actualRow + " "); // Row number on the left side

            for (int col = 0; col < 8; col++) {
                int actualCol = whitePerspective ? col : 7 - col;

                String squareColor = ((row + actualCol) % 2 == 0) ? LIGHT_SQUARE : DARK_SQUARE;
                ChessPosition position = new ChessPosition(actualRow, actualCol + 1);
                ChessPiece piece = board.getPiece(position);

                String pieceSymbol = " ";
                if (piece != null) {
                    String key = piece.getPieceColor() + "_" + piece.getPieceType();
                    pieceSymbol = PIECE_SYMBOLS.getOrDefault(key, "?");
                }

                System.out.print(squareColor + " " + pieceSymbol + " " + RESET_COLOR);
            }
            System.out.println(" " + actualRow); // Row number on the right side
        }

        // Print column letters (bottom)
        printColumnLabels(whitePerspective);
    }

    public static void renderLegalMoves(ChessBoard board,
                                        ChessPosition evalPosition,
                                        Collection<ChessMove> legalMoves,
                                        boolean whitePerspective) {

        // Extract the valid end positions from the legal moves
        Collection<ChessPosition> legalPositions = legalMoves.stream()
                .map(ChessMove::getEndPosition)
                .toList();

        // Print column letters (top)
        printColumnLabels(whitePerspective);

        // Traverse the board row by row
        for (int row = 0; row < 8; row++) {
            int actualRow = whitePerspective ? 8 - row : row + 1;
            System.out.print(actualRow + " "); // Row number on the left side

            for (int col = 0; col < 8; col++) {
                int actualCol = whitePerspective ? col : 7 - col;

                ChessPosition currentPosition = new ChessPosition(actualRow, actualCol + 1);
                ChessPiece piece = board.getPiece(currentPosition);

                String squareColor;
                if (currentPosition.equals(evalPosition)) {
                    squareColor = EscapeSequences.SET_BG_COLOR_YELLOW;
                } else if (legalPositions.contains(currentPosition)) {
                    squareColor = ((row + actualCol) % 2 == 0) ?
                            EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                } else {
                    squareColor = ((row + actualCol) % 2 == 0) ?
                            EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                }


                String pieceSymbol = " ";
                if (piece != null) {
                    String key = piece.getPieceColor() + "_" + piece.getPieceType();
                    pieceSymbol = PIECE_SYMBOLS.getOrDefault(key, "?");
                }

                System.out.print(squareColor + " " + pieceSymbol + " " + RESET_COLOR);
            }
            System.out.println(" " + actualRow); // Row number on the right side
        }

        // Print column letters (bottom)
        printColumnLabels(whitePerspective);
    }

    private static void printColumnLabels(boolean whitePerspective) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "  "); // Indentation for row numbers
        for (int col = 0; col < 8; col++) {
            char label = (char) ('a' + (whitePerspective ? col : 7 - col));
            System.out.print(" " + label + " ");
        }
        System.out.println();
    }
}
