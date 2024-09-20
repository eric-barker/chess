package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator extends PieceMovesCalculator{
    public PawnMovesCalculator(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Which Direction are the pawns moving? Based on color?

        // Where do they start?

        // Move forward

        // Is it in bounds of the Board?
            // Is it an empty space?
                // Add the move to the moves

        // Any Pieces to Capture?
            // Add the move to moves


        // Double move at beginning?


        // Promote the piece


        return moves;
    }
}
