package webSocket;

import chess.ChessMove;
import com.google.gson.Gson;
import logging.LoggerManager;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

@ClientEndpoint
public class WebSocketFacade {
    private static final Logger LOGGER = LoggerManager.getLogger(WebSocketFacade.class.getName());
    private Session session;
    private final String serverUrl;
    private final WebSocketListener listener; // Delegate message processing
    private final Gson gson = new Gson();

    public WebSocketFacade(String serverUrl, WebSocketListener listener) {
        this.serverUrl = serverUrl.replace("http", "ws") + "/ws";
        this.listener = listener;
    }

    public void connect(String authToken, int gameID) throws Exception {
        URI uri = new URI(serverUrl);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        container.connectToServer(this, uri); // Directly connect this client endpoint

        // Wait for session to initialize
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("Failed to open WebSocket session.");
        }

        // Send the CONNECT message
        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(connectCommand));
        LOGGER.info("Sent CONNECT command: " + gson.toJson(connectCommand));
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        LOGGER.info("WebSocket connection established!");
        session.addMessageHandler(String.class, this::handleMessage);
    }

    @OnMessage
    public void handleMessage(String message) {
        LOGGER.info("Received WebSocket message: " + message);

        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                LOGGER.info("Routing to onGameLoad");
                listener.onGameLoad(gson.fromJson(message, LoadGameMessage.class));
            }
            case NOTIFICATION -> {
                LOGGER.info("Routing to onNotification");
                listener.onNotification(gson.fromJson(message, NotificationMessage.class));
            }
            case ERROR -> {
                LOGGER.warning("Routing to onError");
                listener.onError(gson.fromJson(message, ErrorMessage.class));
            }
            default -> LOGGER.warning("Unhandled message type: " + serverMessage.getServerMessageType());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOGGER.info("WebSocket connection closed: " + reason);
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.severe("WebSocket error: " + throwable.getMessage());
    }

    public void disconnect() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
            LOGGER.info("WebSocket Connection disconnected");
        }
    }

    public void leaveGame(String authToken, int gameID) throws Exception {
        if (session != null && session.isOpen()) {
            LeaveCommand leaveCommand = new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            session.getBasicRemote().sendText(gson.toJson(leaveCommand));
            LOGGER.info("Sent LEAVE command to server.");
        } else {
            LOGGER.warning("WebSocket session is not open. Unable to send LEAVE command.");
        }
    }

    public void resignGame(String authToken, int gameID) throws Exception {
        if (session != null && session.isOpen()) {
            ResignCommand resignCommand = new ResignCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            session.getBasicRemote().sendText(gson.toJson(resignCommand));
            LOGGER.info("Sent RESIGN command to server.");
        } else {
            LOGGER.warning("WebSocket session is not open. Unable to send RESIGN command.");
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        if (session != null && session.isOpen()) {
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            session.getBasicRemote().sendText(gson.toJson(makeMoveCommand));
            LOGGER.info("Sent MAKE_MOVE command to server: " + move);
        } else {
            LOGGER.warning("WebSocket session is not open. Unable to send MAKE_MOVE command.");
        }
    }
}
