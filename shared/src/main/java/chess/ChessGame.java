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
        this.gameBoard.resetBoard(); // Reset the board when you make a game.
        this.whoseTurn = TeamColor.WHITE; // White starts
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
        // Check if there's a piece at startPosition, This is stated in the documentation.
        ChessPiece testPiece = gameBoard.getPiece(startPosition);
        if (testPiece == null) {
            return null;  // No piece at this position
        }

        // Get possible moves from the piece
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> possibleMoves = testPiece.pieceMoves(gameBoard, startPosition);



        // Filter out invalid moves that leave the king in check
        for (ChessMove move : possibleMoves) {
            ChessPiece capPiece = gameBoard.getPiece(move.getEndPosition());

            // Temporarily store the current king position
            ChessPosition originalKingPosition = null;
            boolean isKingMoving = testPiece.getPieceType() == ChessPiece.PieceType.KING;

            // Update the king's position temporarily if it's the king moving
            if (isKingMoving) {
                if (testPiece.getTeamColor() == TeamColor.WHITE) {
                    originalKingPosition = whiteKingPosition;
                    whiteKingPosition = move.getEndPosition();
                } else {
                    originalKingPosition = blackKingPosition;
                    blackKingPosition = move.getEndPosition();
                }
            }

            // Perform the move temporarily
            doMove(move, null, testPiece);


            // Is the King out of Check after the temporary move
            if (!isInCheck(testPiece.getTeamColor())) {
                validMoves.add(move);
            }

            // Undo the move to restore the board state
            doMove(move, testPiece, capPiece);

            // Restore the king's original position if the king moved
            if (isKingMoving) {
                if (testPiece.getTeamColor() == TeamColor.WHITE) {
                    whiteKingPosition = originalKingPosition;
                } else {
                    blackKingPosition = originalKingPosition;
                }
            }
        }

        return validMoves;  // Return empty collection if no valid moves are found
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

        // Is the start square empty?
        if (myPiece == null) {
            throw new InvalidMoveException("Invalid Move: No piece in starting square.");
        }

        // Is it the correct team's turn?
        if (myPiece.getTeamColor() != whoseTurn) {
            throw new InvalidMoveException("Invalid Move: It's not your turn.");
        }

        // Get valid moves for this piece
        Collection<ChessMove> validMoves = this.validMoves(startPosition);

        // Check if the move is valid
        boolean isMoveValid = false;
        for (ChessMove aMove : validMoves) {
            if (aMove.getStartPosition().equals(move.getStartPosition()) &&
                    aMove.getEndPosition().equals(move.getEndPosition())) {
                isMoveValid = true;
                break;  // Found a valid move
            }
        }

        // If the move is invalid, throw an exception
        if (!isMoveValid) {
            throw new InvalidMoveException("Invalid Move: The move is not allowed.");
        }

        // Perform the move
        doMove(move, null, myPiece);

        // Update the king's position if the piece is a king
        if (myPiece.getPieceType() == ChessPiece.PieceType.KING) {
            if (myPiece.getTeamColor() == TeamColor.WHITE) {
                whiteKingPosition = endPosition;
            } else {
                blackKingPosition = endPosition;
            }
        }

        // Switch turns
        whoseTurn = (whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
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
