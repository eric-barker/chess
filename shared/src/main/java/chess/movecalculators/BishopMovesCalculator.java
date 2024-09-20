package chess.movecalculators;



import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Concrete class for calculating moves of a bishop.
 */
public class BishopMovesCalculator extends PieceMovesCalculator {

    public BishopMovesCalculator(ChessBoard board, ChessPosition position) {
        super(board, position);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishop move Logic

        // Define diagonal movement directions
        int[][] directions = {
                {1,-1}, // Upper-left
                {1,1}, // Upper-right
                {-1,-1}, // Bottom-left
                {-1,1} // Bottom-right
        };

        // Iterate over each diagonal direction
        for(int[] direction: directions){
            // Start from the current position
            int row = position.getRow();
            int col = position.getColumn();

            // Move in direction within the board boundaries
            while (true){
                // Update position in the current diagonal direction
                row += direction[0];
                col += direction[1];

                // Is the new position out of bound?
                if(row < 1 || row > 8 || col < 1 || col > 8){
                    break;
                }
                // Create new ChessPosition and ChessPiece objects
                ChessPosition newPos = new ChessPosition(row,col);
                ChessPosition offByOnePos = new ChessPosition(row -1, col -1);
                ChessPiece piece = board.getPiece(newPos);

                // Is the square empty?
                if(piece == null){
                    // Add the position to the moves Collection
                    moves.add(new ChessMove(position, newPos, null));
                }
                // There is a piece
                else{
                    // Is it an enemy piece?
                    if(piece.getTeamColor() != board.getPiece(position).getTeamColor()){
                        // Add ChessMove
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    // Stop moving further in this direction
                    break;
                }
            }
        }

        return moves;
    }
}