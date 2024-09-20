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
    private ChessPiece[][] squares = new ChessPiece[9][9];

    public ChessBoard() {
//        // Empty board
//        emptyBoard();
//        setTeamPieces(ChessGame.TeamColor.WHITE);
//        setTeamPieces(ChessGame.TeamColor.BLACK);
    }

    private void emptyBoard(){
        for(int col = 1; col <= 8; col++){
            for(int row = 1; row <= 8; row++){
                squares[row][col] = null;
            }
        }
    }

    private void setTeamPieces(ChessGame.TeamColor color) {
        int backRow;
        int frontRow;

        if(color == ChessGame.TeamColor.WHITE){
            backRow = 1;
            frontRow = 2;
        }
        else{
            backRow = 8;
            frontRow = 7;
        }

        // Place pieces
        // Pawns
        for(int col = 1; col <= 8; col++){
            squares[frontRow][col] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }

        // Rooks
        squares[backRow][1] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        squares[backRow][8] = new ChessPiece(color, ChessPiece.PieceType.ROOK);

        // Knights
        squares[backRow][2] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[backRow][7] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);

        // Bishops
        squares[backRow][3] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[backRow][6] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);

        // Queen
        squares[backRow][4] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);

        // King
        squares[backRow][5] = new ChessPiece(color, ChessPiece.PieceType.KING);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return squares[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        emptyBoard();
        setTeamPieces(ChessGame.TeamColor.WHITE);
        setTeamPieces(ChessGame.TeamColor.BLACK);
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
