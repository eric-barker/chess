package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {
    // Constructor
    public RookMovesCalculator(ChessBoard board, ChessPosition position){
        super(board, position);
    }

    /**
     * Calculates all valid moves for a ROOK at the given position on the board.
     *
     * @param board The chessboard on which to calculate moves.
     * @param position The current position of the piece.
     * @return A collection of valid moves.
     */
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        // Bishop move Logic

        // Define diagonal movement directions
        int[][] directions = {
                {0,1}, // Up
                {1,0}, // Right
                {0,-1}, // Bottom
                {-1,0} // Left
        };

        // Iterate over each direction
        for (int[] direction : directions) {
            // Start from the current position
            int row = position.getRow();
            int col = position.getColumn();

            while(true) {
                // Update position in the current diagonal direction
                row += direction[0];
                col += direction[1];

                // Is the new position out of bound?
                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }
                // Create new ChessPosition and ChessPiece objects
                ChessPosition newPos = new ChessPosition(row, col);
                ChessPosition offByOnePos = new ChessPosition(row - 1, col - 1);
                ChessPiece piece = board.getPiece(newPos);

                // Is the square empty?
                if (piece == null) {
                    // Add the position to the moves Collection
                    moves.add(new ChessMove(position, newPos, null));
                }
                // There is a piece
                else {
                    // Is it an enemy piece?
                    if (piece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        // Add ChessMove
                        moves.add(new ChessMove(position, newPos, null));
                    }
                    // Stop moving in that direction
                    break;
                }
            }
        }

        return moves;
    }
}
