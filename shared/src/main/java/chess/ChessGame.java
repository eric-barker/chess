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
    private ChessBoard gameBoard;
    private TeamColor whoseTurn;
    private String invalidMoveException;
    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public ChessGame() {
        this.gameBoard = new ChessBoard();
        //this.gameBoard.resetBoard(); // Reset the board when you make a game.
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
        return gameBoard;
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
        ChessPiece testPiece = gameBoard.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Is the startSquare empty?
        if(testPiece == null){
            invalidMoveException = "No piece in starting square";
//            validMoves = null;
            return validMoves;
        }

        Collection<ChessMove> possibleMoves = testPiece.pieceMoves(gameBoard, startPosition);

        // Check if it is this piece's turn
        if(testPiece.getTeamColor() != whoseTurn){
            invalidMoveException = "Not the piece in starting square's turn";
            return validMoves;
        }

        // Check each move and if they put my King in check
        for(ChessMove move: possibleMoves) {
            ChessPiece capPiece = gameBoard.getPiece(move.getEndPosition());

            // Perform the move temporarily
            doMove(move, null, testPiece);

            // Check if the move places the king in check
            if (!isInCheck(whoseTurn)) {
                validMoves.add(move);
            }

            // Undo the move to restore the gameBoard state
            doMove(move, testPiece, capPiece);
        }

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
        ChessPiece myPiece = gameBoard.getPiece(startPosition);


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

        // Execute my move
        doMove(move, null, myPiece);

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
        TeamColor enemyColor;
        if(teamColor == TeamColor.WHITE){
            kingPosition = whiteKingPosition;
            enemyColor = TeamColor.BLACK;
        }
        else{
            kingPosition = blackKingPosition;
            enemyColor = TeamColor.WHITE;
        }

        // Get all the moves of the enemy team
        Collection<ChessMove> enemyMoves = getTeamMoves(enemyColor);

        // Loop through all the enemy moves to see if they overlap the kings position.
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
        // Get all possible moves from my team
        Collection<ChessMove> myTeamMoves = getTeamMoves(teamColor);

        // Can my King escape Check?
        // Loop through the moves my pieces can make to see if my king can escape check
        for(ChessMove move: myTeamMoves) {

            // Log the gameBoard so I can revert after testing
            ChessPiece capPiece = gameBoard.getPiece(move.getEndPosition());
            ChessPiece testPiece = gameBoard.getPiece(move.getStartPosition());

            // Test move
            doMove(move, null, testPiece);

            // Does this take King out of check?
            boolean inCheck = isInCheck(teamColor);

            // return the gameBoard to its original state
            doMove(move, testPiece, capPiece);

            // Is the King still in check?
            if(!inCheck) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If the team is in check, it's not a stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        // Retrieve all possible moves for this team
        Collection<ChessMove> myTeamMoves = getTeamMoves(teamColor);

        // Loop moves to see if I can get out of stalemate
        for (ChessMove move : myTeamMoves) {
            // record current state of the gameBoard
            ChessPiece capturedPiece = gameBoard.getPiece(move.getEndPosition());
            ChessPiece movingPiece = gameBoard.getPiece(move.getStartPosition());

            // test move
            doMove(move, null, movingPiece);

            // is the king still in check?
            boolean inCheck = isInCheck(teamColor);

            // put the move back
            doMove(move, movingPiece, capturedPiece);

            // If there is a move that doesn't leave the king in check, it's not a stalemate
            if (!inCheck) {
                return false;
            }
        }

        // If no valid moves are available and the team is not in check, it's a stalemate
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
        findKings();
    }

    private Collection<ChessMove> getTeamMoves(TeamColor color){
        ChessPosition myPos;
        ChessPiece myPiece;
        Collection<ChessMove> teamMoves = new ArrayList<>();

        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                myPos = new ChessPosition(i, j);
                myPiece = gameBoard.getPiece(myPos);
                //Is there a piece && is it my team color?
                if(myPiece != null && myPiece.getTeamColor() == color){
                    // add the piece moves to teamMoves
                    teamMoves.addAll(myPiece.pieceMoves(gameBoard, myPos));
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
                ChessPiece myPiece = gameBoard.getPiece(myPos);
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

    private void doMove(ChessMove move, ChessPiece startSquare, ChessPiece endSquare){
        gameBoard.addPiece(move.getEndPosition(), endSquare);
        gameBoard.addPiece(move.getStartPosition(), startSquare);
    }

}
