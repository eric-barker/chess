package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.ROOK;


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

    public void addMoves(Collection<ChessMove> moves, ChessPosition position, ChessPosition pos, int endRow){
        // Can the pawn promote?
        if(pos.getRow() == endRow){
            moves.add(new ChessMove(position, pos, QUEEN));
            moves.add(new ChessMove(position, pos, BISHOP));
            moves.add(new ChessMove(position, pos, KNIGHT));
            moves.add(new ChessMove(position, pos, ROOK));
        }
        // Add a move like normal
        else{
            moves.add(new ChessMove(position, pos, null));
        }
    }
}
