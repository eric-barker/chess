package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor whoseTurn;
    private String invalidMoveException;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard(); // Reset the board when you make a game.
        this.whoseTurn = TeamColor.WHITE;
        this.whiteKingPosition = new ChessPosition(1,5);
        this.blackKingPosition = new ChessPosition(8, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whoseTurn = team;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Is the startSquare empty?
        if(piece == null){
           invalidMoveException = "No piece in starting square";
           return validMoves;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        // Check if it is this piece's turn
        if(piece.getTeamColor() != whoseTurn){
            invalidMoveException = "Not the piece in starting square's turn";
        }

        // TODO: Am I moving into check?

        // TODO: Does my move cause my king to be in check?

        // TODO: Does my move put the enemy king in check?

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece myPiece = board.getPiece(startPosition);


        // Collection of valid moves
        Collection<ChessMove> validMoves = this.validMoves(startPosition);

        // Check if move is valid
        boolean isMoveValid = false;
        for(ChessMove aMove: validMoves){
            if(aMove == move){
                isMoveValid = true;
                break; // Condition is met, terminate for loop.
            }
        }

        // If move is invalid, throw a relevant exception
        if(!isMoveValid){
            throw new InvalidMoveException("Invalid Move: " + invalidMoveException);
        }

        // move the piece to the endPosition
        board.addPiece(endPosition, myPiece);
        // Erase piece from the startPosition
        board.addPiece(startPosition, null);

        // Update KingPosition
        if(myPiece.getPieceType() == ChessPiece.PieceType.KING){
            if(myPiece.getTeamColor() == TeamColor.WHITE){
                whiteKingPosition = endPosition;
            }
            else{
                blackKingPosition = endPosition;
            }
        }


        // Switch team moves
        if(whoseTurn == TeamColor.WHITE){
            whoseTurn = TeamColor.BLACK;
        }
        else{
            whoseTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Get the king's position
        ChessPosition kingPosition;
        if(teamColor == TeamColor.WHITE){
            kingPosition = whiteKingPosition;
        }
        else{
            kingPosition = blackKingPosition;
        }

        // Get all the moves of the enemy team
        TeamColor enemyColor;
        if(teamColor == TeamColor.WHITE){
            enemyColor = TeamColor.BLACK;
        }
        else{
            enemyColor = TeamColor.WHITE;
        }
        Collection<ChessMove> enemyMoves = getTeamMoves(enemyColor);

        // Loop through all of the enemy moves to see if they overlap the kings position.
        for(ChessMove move: enemyMoves){
            if(move.getEndPosition().equals(kingPosition)){
                return true; // The king is in Check
            }
        }

        return false; // The king is not in Check
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    private Collection<ChessMove> getTeamMoves(TeamColor color){
        ChessPosition myPos;
        ChessPiece myPiece;
        Collection<ChessMove> teamMoves = new ArrayList<>();

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                myPos = new ChessPosition(i, j);
                myPiece = board.getPiece(myPos);
                //Is there a piece && is it my team color?
                if(myPiece != null && myPiece.getTeamColor() == color){
                    // add the piece moves to teamMoves
                    teamMoves.addAll(myPiece.pieceMoves(board, myPos));
                }
            }
        }

        return teamMoves;
    }

    private void findKings(){
        ChessPosition myPos;
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++) {
                myPos = new ChessPosition(i, j);
                ChessPiece myPiece = board.getPiece(myPos);
                // If there is a piece and it is a king
                if(myPiece != null && myPiece.getPieceType() == ChessPiece.PieceType.KING){
                    if(myPiece.getTeamColor() == TeamColor.WHITE){
                        whiteKingPosition = myPos;
                    }
                    else{
                        blackKingPosition = myPos;
                    }
                }

            }
        }
    }

}
