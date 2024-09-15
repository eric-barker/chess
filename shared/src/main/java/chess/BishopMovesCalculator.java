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

        // Define diagonal movement directions
        int[][] directions = {
                {1,1}, // Upper-right
                {1,-1}, // Upper-left
                {-1,1}, // Bottom-right
                {-1,-1} // Bottom-left
        };

        // Iterate over each diagonal direction
        for(int[] direction: directions){
            // Start from the current position

            // Move in direction within the board boundaries
                // Update position in the current diagonal direction
                // Is the new position out of bound?
                // Is the square empty?
                    // Add the position to the moves Collection
                // There is a piece
                // Is it an enemy piece?
                    // Add ChessMove
                // Stop moving further in this direction
        }


        return moves;
    }
}
