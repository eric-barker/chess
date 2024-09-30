package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] squares = new ChessPiece[9][9];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearBoard();
        setTeamPieces(ChessGame.TeamColor.WHITE);
        setTeamPieces(ChessGame.TeamColor.BLACK);
    }

    private void clearBoard() {
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                squares[i][j] = null;
            }
        }
    }

    private void setTeamPieces(ChessGame.TeamColor color){
        int frontRow;
        int backRow;

        if(color == ChessGame.TeamColor.WHITE){
            frontRow = 1;
            backRow = 0;
        }
        else{
            frontRow = 6;
            backRow = 7;
        }

        // Populate the pawns
        for(int i = 0; i < 8; i++){
            squares[frontRow][i]= new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }

        // Populate the backRow
        squares[backRow][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        squares[backRow][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);

        squares[backRow][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        squares[backRow][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);

        squares[backRow][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[backRow][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);

        squares[backRow][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[backRow][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
