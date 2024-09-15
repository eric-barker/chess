package chess;

import java.util.Collection;


/**
 * Abstract base class for calculating moves of different chess pieces.
 */
public abstract class PieceMovesCalculator {
    /**
     * Calculates all valid moves for a piece at the given position on the board.
     *
     * @param board The chessboard on which to calculate moves.
     * @param position The current position of the piece.
     * @return A collection of valid moves.
     */
    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    // Default Constructor
    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
    }
}
