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
    private static final String LIGHT_SQUARE = EscapeSequences.SET_BG_COLOR_LIGHT_BEIGE; // Light beige for light squares
    private static final String DARK_SQUARE = EscapeSequences.SET_BG_COLOR_WOOD;        // Wood tone for dark squares
    private static final String TEXT_COLOR_BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK_MINE;   // Black text for pieces
    private static final String TEXT_COLOR_WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE_MINE;   // White text for pieces
    private static final String RESET_COLOR = "\u001B[0m"; // Reset all colors

    // Mapping ChessPiece to symbols
    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();

    static {
        // Map pieces to terminal symbols (can be updated as needed)
        PIECE_SYMBOLS.put("WHITE_PAWN", EscapeSequences.WHITE_PAWN);
        PIECE_SYMBOLS.put("WHITE_ROOK", EscapeSequences.WHITE_ROOK);
        PIECE_SYMBOLS.put("WHITE_KNIGHT", EscapeSequences.WHITE_KNIGHT);
        PIECE_SYMBOLS.put("WHITE_BISHOP", EscapeSequences.WHITE_BISHOP);
        PIECE_SYMBOLS.put("WHITE_QUEEN", EscapeSequences.WHITE_QUEEN);
        PIECE_SYMBOLS.put("WHITE_KING", EscapeSequences.WHITE_KING);

        PIECE_SYMBOLS.put("BLACK_PAWN", EscapeSequences.BLACK_PAWN);
        PIECE_SYMBOLS.put("BLACK_ROOK", EscapeSequences.BLACK_ROOK);
        PIECE_SYMBOLS.put("BLACK_KNIGHT", EscapeSequences.BLACK_KNIGHT);
        PIECE_SYMBOLS.put("BLACK_BISHOP", EscapeSequences.BLACK_BISHOP);
        PIECE_SYMBOLS.put("BLACK_QUEEN", EscapeSequences.BLACK_QUEEN);
        PIECE_SYMBOLS.put("BLACK_KING", EscapeSequences.BLACK_KING);
    }

    public static void renderChessBoard(ChessBoard board, boolean whitePerspective) {
        LOGGER.info("White perspective: " + whitePerspective);
        ChessPiece[][] squares = board.getSquares(); // Assuming this retrieves the board state as a 2D array of ChessPieces.

        // Print column letters (top)
        System.out.println("\n");
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

                String pieceSymbol = EscapeSequences.EMPTY;
                if (piece != null) {
                    String key = piece.getPieceColor() + "_" + piece.getPieceType();
                    pieceSymbol = PIECE_SYMBOLS.getOrDefault(key, "?");
                }

                String textColor = piece != null && piece.getPieceColor().equals("WHITE") ? TEXT_COLOR_WHITE : TEXT_COLOR_BLACK;

                System.out.print(squareColor + textColor + pieceSymbol + RESET_COLOR);
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
                    squareColor = ((row + actualCol) % 2 == 0) ? LIGHT_SQUARE : DARK_SQUARE;
                }

                String pieceSymbol = EscapeSequences.EMPTY;
                if (piece != null) {
                    String key = piece.getPieceColor() + "_" + piece.getPieceType();
                    pieceSymbol = PIECE_SYMBOLS.getOrDefault(key, "?");
                }

                String textColor = piece != null && piece.getPieceColor().equals("WHITE") ? TEXT_COLOR_WHITE : TEXT_COLOR_BLACK;

                System.out.print(squareColor + textColor + pieceSymbol + RESET_COLOR);
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
            System.out.print(" " + label + "  ");
        }
        System.out.println();
    }
}
