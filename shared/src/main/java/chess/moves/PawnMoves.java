package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves extends BaseMoves{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int startRow;
        int endRow;
        int direction;

        // Is the team color white?
        if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            startRow = 2;
            endRow = 8;
            direction = 1;
        }
        else{ // Team color is Black
            startRow = 7;
            endRow = 1;
            direction = -1;
        }

        int row = position.getRow();
        int col = position.getColumn();

        // Handle forward movement
        ChessPosition forward = new ChessPosition(row + direction, col);
        ChessPosition doubleForward = new ChessPosition(row + 2*direction, col);

        // Is the space empty?
        if(board.getPiece(forward) == null){
            addMove(position, forward, moves, endRow);

            // Handle Double start move

            // Is the space empty?
            if(position.getRow() == startRow && board.getPiece(doubleForward) == null){
                addMove(position, doubleForward, moves, endRow);
            }
        }

        // Handle Capture moves
        int[] directions = {-1,1};

        ChessPosition left = new ChessPosition(row + direction, col - 1);
        ChessPosition right = new ChessPosition(row + direction, col + 1);

        for(int direct: directions){
            ChessPosition myPosition;
            if(direct == -1){
                myPosition = left;
            }
            else{
                myPosition = right;
            }

            // Check to see if myPosition is out of bounds
            if(myPosition.getRow() < 1 || myPosition.getRow() > 8 || myPosition.getColumn() < 1 || myPosition.getColumn() > 8){
                break;
            }

            // Is the space occupied?
            if(board.getPiece(myPosition) != null){
                // Is it an enemy piece?
                if(board.getPiece(myPosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    addMove(position, myPosition, moves, endRow);
                }
            }
        }



        return moves;
    }

    private void addMove(ChessPosition position, ChessPosition newPosition, Collection<ChessMove> moves, int endRow){
        if(newPosition.getRow() == endRow){
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
        }
        else{
            moves.add(new ChessMove(position, newPosition, null));
        }
    }
}
