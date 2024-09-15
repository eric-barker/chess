package chess;



import java.util.ArrayList;
import java.util.Collection;

/**
 * Concrete class for calculating moves of a bishop.
 */
public class BishopMovesCalculator extends PieceMovesCalculator{

    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishop move Logic


        return moves;
    }
}
