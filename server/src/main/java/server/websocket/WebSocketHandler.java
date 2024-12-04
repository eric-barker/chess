package server.websocket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import handler.LoginHandler;
import logging.LoggerManager;
import model.Auth;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
//import dataaccess.DataAccess;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Timer;
import java.util.logging.Logger;


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


            // Check AuthToken... use DAO?
            String authToken = command.getAuthToken();
            Auth myAuth = authDAO.getAuth(authToken);
            String username = myAuth.username();

            // Where am I saving my session?  Is this for the WEbSocket Handler?
            connections.add(command.getGameID(), username, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, command);
                case LEAVE -> leave(session, username, command);
                case RESIGN -> resign(session, username, command);
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
            Game game = gameDAO.getGame(gameID);
            ChessGame chessGame = game.game();
            LOGGER.info("chessGame: " + chessGame);
        } catch (DataAccessException e) {
            LOGGER.info("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }

        System.out.println("Connect stub function");
    }

    private void makeMove(Session session, String username, UserGameCommand command) {
        System.out.println("MakeMove stub function");
    }

    private void leave(Session session, String username, UserGameCommand command) {
        System.out.println("Leave stub function");
    }

    private void resign(Session session, String username, UserGameCommand command) {
        System.out.println("Resign stub function");
    }


}
