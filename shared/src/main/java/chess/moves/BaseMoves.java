package chess.moves;

import chess.*;
import java.util.Collection;

public abstract class BaseMoves {
    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    protected void calculateBaseMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int[][] directions, boolean hasContinuousMovement){
        for(int[] direction: directions){
            // Initialize row and col
            int row = position.getRow();
            int col = position.getColumn();

            while(true){
                // Update row and col by direction
                row += direction[0];
                col += direction[1];

                // Is the updated position out of bounds?
                if(row < 1 || row > 8 || col < 1 || col > 8){
                    break; // It's out of bounds, try a different direction
                }

                // Make a new Chess Position
                ChessPosition newPosition = new ChessPosition(row, col);

                // Is the space empty?
                if(board.getPiece(newPosition) == null){
                    moves.add(new ChessMove(position, newPosition, null));
                }
                else{ // There is a piece there
                    // Is it an enemy piece?
                    if(board.getPiece(newPosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                        moves.add(new ChessMove(position, newPosition, null));
                    }

                    break; // We have either run into our own piece or added a capture move, stop moving in this direction.
                }

                // Does this piece have continuous movement?
                if(!hasContinuousMovement){
                    break; // Don't keep moving in that direction.
                }
            }
        }
    }
}
