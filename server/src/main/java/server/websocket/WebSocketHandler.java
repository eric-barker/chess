package server.websocket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import logging.LoggerManager;
import model.Auth;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.*;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.logging.Logger;

import static websocket.messages.ServerMessage.ServerMessageType.*;
import static server.websocket.ConnectionManager.BroadcastType.*;


@WebSocket
public class WebSocketHandler {
    private static final Logger LOGGER = LoggerManager.getLogger(WebSocketHandler.class.getName());
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private final ConnectionManager connections = new ConnectionManager() {
    };

    public WebSocketHandler(AuthDAO authDAO, UserDAO userDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {

            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            MakeMoveCommand makeMoveCommand = null;
            ConnectCommand connectCommand = null;
            LeaveCommand leaveCommand = null;
            ResignCommand resignCommand = null;
            switch (command.getCommandType()) {
                case CONNECT:
                    connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    break;
                case MAKE_MOVE:
                    makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    break;
                case LEAVE:
                    leaveCommand = new Gson().fromJson(message, LeaveCommand.class);
                    break;
                case RESIGN:
                    resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    break;
            }


            // Check AuthToken... use DAO?
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();
            Auth myAuth = authDAO.getAuth(authToken);
            if (myAuth == null) {
                LOGGER.warning("Auth is null");
                String error = "Error: Auth is null.";
                ErrorMessage errorMessage = new ErrorMessage(ERROR, error);
                String myErrorMessage = new Gson().toJson(errorMessage);
                session.getRemote().sendString(myErrorMessage);
                return; // Exit method to prevent further processing
            }

            String username = myAuth.username();

            // Where am I saving my session?  Is this for the WEbSocket Handler?
            connections.add(command.getGameID(), username, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, connectCommand);
                case MAKE_MOVE -> makeMove(session, username, makeMoveCommand);
                case LEAVE -> leave(session, username, leaveCommand);
                case RESIGN -> resign(session, username, resignCommand);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.severe("Error: " + ex.getMessage());
            // Send message?
        }
    }

    private void connect(Session session, String username, UserGameCommand command) {
        LOGGER.info("command: " + command);
        Integer gameID = command.getGameID();
        LOGGER.info("gameID: " + gameID);

        try {
            // Fetch the game from the database
            Game gameData = gameDAO.getGame(gameID);

            // Handle invalid gameID
            if (gameData == null) {
                LOGGER.warning("Invalid gameID: " + gameID);
                String error = "Error: invalid gameID: " + gameID;
                ErrorMessage errorMessage = new ErrorMessage(ERROR, error);
                connections.broadcast(gameID, username, errorMessage, JUST_ME);
                return; // Exit method to prevent further processing
            }

            // Process valid game data
            ChessGame game = gameData.game();
            LOGGER.info("chess game: " + game);

            // Send LOAD_GAME message to the connecting user
            LoadGameMessage loadGameMessage = new LoadGameMessage(LOAD_GAME, game);
            connections.broadcast(gameID, username, loadGameMessage, JUST_ME);

            // Notify other users in the game
            String notification = username + " has joined the lobby";
            NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION, notification);
            connections.broadcast(gameID, username, notificationMessage, EVERYONE_BUT_ME);
        } catch (DataAccessException e) {
            LOGGER.severe("DataAccessException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try {
            //      Server verifies the validity of the move.

            Game game = gameDAO.getGame(command.getGameID());
            ChessGame chessGame = game.game();
            chessGame.makeMove(command.getMove());

            // Game is updated to represent the move. Game is updated in the database.
            Game updatedGame = new Game(game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chessGame);
            gameDAO.updateGame(updatedGame);

            // Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
            LoadGameMessage loadGameMessage = new LoadGameMessage(LOAD_GAME, chessGame);
            connections.broadcast(command.getGameID(), username, loadGameMessage, EVERYONE);

            // Server sends a Notification message to all other clients in that game informing them what move was made.
            NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION,
                    "Player " + username + " has made a move");
            NotificationMessage notificationMessageStatus = null;
            connections.broadcast(command.getGameID(), username, notificationMessage, EVERYONE_BUT_ME);

            // If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
            if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                notificationMessageStatus = new NotificationMessage(NOTIFICATION, chessGame.getTeamTurn().toString() + " is in check mate, " + username + " wins.");
                connections.broadcast(command.getGameID(), username, notificationMessageStatus, EVERYONE);
            } else if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                notificationMessageStatus = new NotificationMessage(NOTIFICATION, chessGame.getTeamTurn().toString() + " is in check");
                connections.broadcast(command.getGameID(), username, notificationMessageStatus, EVERYONE);
            } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                notificationMessageStatus = new NotificationMessage(NOTIFICATION, "Stalemate, game over.");
                connections.broadcast(command.getGameID(), username, notificationMessageStatus, EVERYONE);
            }


        } catch (Exception e) {
            LOGGER.warning("Error making move: " + e.getMessage());
            ErrorMessage errorMessage = new ErrorMessage(ERROR, "Error making move: " + e.getMessage());
            connections.broadcast(command.getGameID(), username, errorMessage, JUST_ME);
        }

    }

    private void leave(Session session, String username, UserGameCommand command) {
        try {
            LOGGER.info(username + " attempting to leave game...");

            // Remove player from their role.
            Game game = gameDAO.getGame(command.getGameID());
            Game newGame;

            LOGGER.info(game.whiteUsername() + " is the whiteUsername");
            LOGGER.info(game.blackUsername() + " is the blackUsername");
            if (game.whiteUsername().equals(username)) {
                LOGGER.info("Removing whiteUsername...");
                newGame = new Game(command.getGameID(), null, game.blackUsername(), game.gameName(), game.game());
            } else {
                LOGGER.info("Removing blackUsername...");
                newGame = new Game(command.getGameID(), game.whiteUsername(), null, game.gameName(), game.game());
            }

            LOGGER.info("Updating " + game.gameName() + ": \n" + game.toString() + "\n to: \n" + newGame.toString());
            gameDAO.updateGame(newGame);

            // remove the root client
            LOGGER.info("Closing session...");
            session.close();
            LOGGER.info("Removing connection...");
            connections.remove(username);

            // inform the other clients on the game.
            NotificationMessage message = new NotificationMessage(NOTIFICATION, username + " has left the game");
            connections.broadcast(command.getGameID(), username, message, EVERYONE_BUT_ME);
        } catch (Exception e) {
            LOGGER.warning("Error leaving game: " + e.getMessage());
        }
    }

    private void resign(Session session, String username, UserGameCommand command) {
        System.out.println("Resign stub function");
    }


}
