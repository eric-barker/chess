package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
    private boolean gameIsOver = false;

    public ChessGame() {
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard(); // Reset the board when you make a game.
        this.whoseTurn = TeamColor.WHITE; // White starts
        this.whiteKingPosition = null;
        this.blackKingPosition = null;
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

    public Boolean getGameIsOver() {
        return gameIsOver;
    }

    public void setGameIsOver() throws Exception {
        if (this.gameIsOver == true) {
            throw new Exception("You cannot resign, the game is already over");
        }
        this.gameIsOver = !gameIsOver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && whoseTurn == chessGame.whoseTurn && Objects.equals(invalidMoveException,
                chessGame.invalidMoveException) && Objects.equals(whiteKingPosition, chessGame.whiteKingPosition) && Objects.equals(blackKingPosition,
                chessGame.blackKingPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, whoseTurn, invalidMoveException, whiteKingPosition, blackKingPosition);
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

        if (gameIsOver) {
            throw new InvalidMoveException("The game is over: No new moves can be made.");
        }

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

//        boolean validMovesExist;
//        if (validMoves == null) {
//            validMovesExist = false;
//        }

        // Check if the move is valid
//        boolean isMoveValid = false;
//        for (ChessMove aMove : validMoves) {
//            if (aMove.getStartPosition().equals(move.getStartPosition()) &&
//                    aMove.getEndPosition().equals(move.getEndPosition())) {
//                isMoveValid = true;
//                break;  // Found a valid move
//            }
//        }

        // If the move is invalid, throw an exception
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid Move: The move is not allowed. ChessMove: " + move.toString());
        }

        // Perform the move
        doMove(move, null, myPiece);

        // Handle promotion if a promotion type is specified (aligning with Option 1)
        if (move.getPromotionPiece() != null) {
            myPiece = new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
            gameBoard.addPiece(endPosition, myPiece);  // Update the board with promoted piece
        }


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
        this.findKings();

        if (teamColor == TeamColor.WHITE) {
            kingPosition = whiteKingPosition;
            enemyColor = TeamColor.BLACK;
        } else {
            kingPosition = blackKingPosition;
            enemyColor = TeamColor.WHITE;
        }

        // If no king is on the board, return false (i.e., not in check)
        if (kingPosition == null) {
            return false;
        }

        // Get all the moves of the enemy team
        Collection<ChessMove> enemyMoves = getTeamMoves(enemyColor);

        // Loop through all the enemy moves to see if they overlap the king's position.
        for (ChessMove move : enemyMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true; // The king is in check
            }
        }

        return false; // The king is not in check
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition currPosition = new ChessPosition(r, c);
                ChessPiece currPiece = gameBoard.getPiece(currPosition);

                // If there is a piece of the current team
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    // Get valid moves for this piece
                    Collection<ChessMove> validMoves = validMoves(currPosition);

                    // Are there any valid moves?
                    if (validMoves != null && !validMoves.isEmpty()) {
                        return true;  // Found valid moves, so not in checkmate/stalemate
                    }
                }
            }
        }
        return false;  // No valid moves found
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Is the team in check?  Has to be or its Stalemate not Checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // If the team has no valid moves, it's checkmate
        return !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If the team is in check, it's checkmate not stalemate.
        if (isInCheck(teamColor)) {
            return false;
        }

        // If the team has no valid moves, it's stalemate
        return !hasValidMoves(teamColor);
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

    private Collection<ChessMove> getTeamMoves(TeamColor color) {
        ChessPosition myPos;
        ChessPiece myPiece;
        Collection<ChessMove> teamMoves = new ArrayList<>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                myPos = new ChessPosition(i, j);
                myPiece = gameBoard.getPiece(myPos);
                //Is there a piece && is it my team color?
                if (myPiece != null && myPiece.getTeamColor() == color) {
                    // add the piece moves to teamMoves
                    teamMoves.addAll(myPiece.pieceMoves(gameBoard, myPos));
                }
            }
        }

        return teamMoves;
    }

    private void findKings() {
        ChessPosition myPos;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                myPos = new ChessPosition(i, j);
                ChessPiece myPiece = gameBoard.getPiece(myPos);
                // If there is a piece and it is a king
                if (myPiece != null && myPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (myPiece.getTeamColor() == TeamColor.WHITE) {
                        whiteKingPosition = myPos;
                    } else {
                        blackKingPosition = myPos;
                    }
                }

            }
        }
    }

    private void doMove(ChessMove move, ChessPiece startSquare, ChessPiece endSquare) {
        gameBoard.addPiece(move.getEndPosition(), endSquare);
        gameBoard.addPiece(move.getStartPosition(), startSquare);
    }

}
