package webSocket;

import com.google.gson.Gson;
import logging.LoggerManager;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

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

        this.session = container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig endpointConfig) {
                LOGGER.info("WebSocket connection established!");
            }
        }, uri);

        session.addMessageHandler((MessageHandler.Whole<String>) this::handleMessage);

        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(gson.toJson(connectCommand));
    }

    private void handleMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> listener.onGameLoad(gson.fromJson(message, LoadGameMessage.class));
            case NOTIFICATION -> listener.onNotification(
                    gson.fromJson(message, NotificationMessage.class));
            case ERROR -> listener.onError(gson.fromJson(message, ErrorMessage.class));
        }
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
            LOGGER.info("sent LEAVE command to server.");
        } else {
            LOGGER.warning("WebSocket session is not open. unable to send LEAVE command.");
        }
    }
}
