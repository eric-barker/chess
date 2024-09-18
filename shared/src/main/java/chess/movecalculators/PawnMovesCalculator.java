package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator extends PieceMovesCalculator{
    public PawnMovesCalculator(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        return List.of();
    }
}
