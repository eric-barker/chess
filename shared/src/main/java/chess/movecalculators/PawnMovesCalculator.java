package chess.movecalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PawnMovesCalculator extends PieceMovesCalculator{
    public PawnMovesCalculator(ChessBoard board, ChessPosition position){
        super(board, position);
    }


    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPawn = board.getPiece(position);

        int direction;
        int startRow;
        int endRow;

        if(myPawn.getTeamColor() == ChessGame.TeamColor.WHITE){
            // Which Direction are the pawns moving? Based on color?
            direction = 1;
            // Where do they start?
            startRow = 2;
            // Where do they end?
            endRow = 8;
        }
        else{
            // Which Direction are the pawns moving? Based on color?
            direction = -1;
            // Where do they start?
            startRow = 7;
            // Where do they end?
            endRow = 1;
        }





        // Any Pieces to Capture?
        int[] columnOffset = {-1,0,1};
        int row = position.getRow() + direction;

        for(int column : columnOffset){
            int col = position.getColumn() + column;
            ChessPosition pos = new ChessPosition(row, col);

            // Is it out of board boundaries?
            if(row < 1 || row > 8 || col < 1 || col > 8) {
                continue; // Move on to next for loop iteration
            }

            // Moving straight?
            if(column == 0){

                // Is the next board space empty?
                if(board.getPiece(pos) == null){
                    // Add move to moves
                   super.addMoves(moves, position, pos, endRow);

                    // Am I at the start row?
                    if(position.getRow() == startRow){
                        // look at space 2 in front of myPawn
                        row += direction;

                        // Is it out of board boundaries?
                        if(row < 1 || row > 8 || col < 1 || col > 8) {
                            continue; // Move on to next for loop iteration
                        }

                        pos = new ChessPosition(row, col);

                        // Is the space empty?
                        if(board.getPiece(pos) == null){
                            // add move to moves.
                            super.addMoves(moves, position, pos, endRow);
                        }
                    }
                }
            }


            // Check Diagonals for enemies
            if(column == -1 || column == 1) {
                // Is there an enemy piece?
                if (board.getPiece(pos) != null) {
                    if (board.getPiece(pos).getTeamColor() != board.getPiece(position).getTeamColor()) {
                       super.addMoves(moves, position, pos, endRow);
                    }
                }
            }
        }


        return moves;
    }
}
