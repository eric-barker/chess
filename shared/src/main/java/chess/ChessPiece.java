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

        // Add Logic to figure out which moves the Bishop can make on the board.

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
