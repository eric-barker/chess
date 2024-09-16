package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator{
    // Constructor
    public KnightMovesCalculator(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishop move Logic

        // Define diagonal movement directions
        int[][] directions = {
                {3,-1}, // Upper-left
                {3,1}, // Upper-right
                {1,3}, // Right-up
                {-1,3}, // Right-down
                {-3,1}, // Bottom-right
                {-3,-1}, // Bottom-left
                {-3,1}, // Left-up
                {-3,-1} // Left-down
        };

        // Iterate over each diagonal direction
        for(int[] direction: directions){
            // Start from the current position
            int row = position.getRow();
            int col = position.getColumn();

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
            }
        }

        return moves;
    }
}
