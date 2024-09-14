package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Maybe use a switch statement to cycle through each of the piece types?
        switch (this.type){
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KING:
                return kingMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            default:
                return new ArrayList<>();
        }
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();


        int[][] bishopMoveDirections = {
                {1,1}, // Upper Right
                {1,-1}, // Upper Left
                {-1,1}, // Lower Right
                {-1,-1}}; // Lower Left

        // check each of the diagonal directions
        for(int[] direction: bishopMoveDirections){
            // Set row and column to first square in the direction
            int rowNum = myPosition.getRow() + direction[0];
            int columnNum = myPosition.getColumn() + direction[1];

            // Keep iterating and adding moves until out of bounds
            while(rowNum >= 1 && rowNum <= 8 && columnNum >= 1 && columnNum <= 8){
                ChessPosition newPosition = new ChessPosition(rowNum, columnNum);
                ChessPiece pieceInSquare = board.getPiece(newPosition);

                // Check if there is a piece in the square
                if(pieceInSquare != null){

                    // Is the piece on my team?
                    if(pieceInSquare.getTeamColor() == this.pieceColor){
                        // We have reached the end of options in this direction.
                        break;
                    }
                    // Add move to available moves
                    else{
                        availableMoves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    }
                }

                // If no piece add square to available moves
                availableMoves.add(new ChessMove(myPosition, newPosition, null));

                // Increment the row and column in the given direction
                rowNum += direction[0];
                columnNum += direction[1];

            }
        }





        return availableMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();



        return availableMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();



        return availableMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();



        return availableMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();



        return availableMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> availableMoves = new ArrayList<>();



        return availableMoves;
    }


}
