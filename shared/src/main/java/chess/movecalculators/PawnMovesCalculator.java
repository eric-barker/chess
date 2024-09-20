package chess.movecalculators;

import chess.*;

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
        int[] columnOffset = {-1,1};
        for(columnNum: columnOffset){
            col = position.getColumn() + columnNum;

            // Is it in the board boundaries?
            if(row >= 1 && row <=8 && col >=1 && col <=8){
                ChessPosition pos = new ChessPosition(row, col);

                // Is there an enemy piece?
                if(board.getPiece(pos) != null){
                    if(board.getPiece(pos).getTeamColor() != board.getPiece(position).getTeamColor()){
                        moves.add(new ChessMove(position, pos, null));
                    }
                }
            }
        }


        // Double move at beginning?
        if(position.getRow() == startRow){
            row = position.getRow() + 2;
            ChessPosition pos = new ChessPosition(row,col);
            ChessPosition posOneLess = new ChessPosition(row-1, col);

            // is the space empty and the space before it empty?
            if(board.getPiece(pos) == null && board.getPiece(posOneLess) == null){
                // Add move to moves
                moves.add(new ChessMove(position, pos, null));
            }
        }


        // Promote the piece
//        if(position.getRow() == endRow){
//
//        }


        return moves;
    }
}
