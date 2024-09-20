package chess.movecalculators;

import chess.ChessBoard;
import chess.ChessGame;
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

        int direction;
        int startRow;
        int endRow;

        if(getTeamColor() == ChessGame.TeamColor.WHITE){
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

        // Move forward
        int row = position.getRow() + direction;
        int col = position.getColumn();

        // Is it in bounds of the Board?
        if(row >= 1 && row <= 8){
            ChessPosition pos = new ChessPosition(row,col);
            // Is it an empty space?
            if(board.getPiece(pos) == null){
                // Add the move to the moves
                moves.add(new ChessMove(position,pos,null));
            }
        }

        // Any Pieces to Capture?
        row = position.getRow() + direction;
        for(columnOffset: new int[]{-1,1}){
            col = position.getColumn() + columnOffset;
        }
            // Add the move to moves


        // Double move at beginning?


        // Promote the piece


        return moves;
    }
}
