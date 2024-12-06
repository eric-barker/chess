package webSocket;

import com.google.gson.Gson;
import logging.LoggerManager;
import ui.PostLoginClient;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

public class WebSocketHandler {
    private static final Logger LOGGER = LoggerManager.getLogger(WebSocketHandler.class.getName());
    private Session session;
    private final String serverUrl;
    private final WebSocketListener listener; // Delegate message processing
    private final Gson gson = new Gson();

    public WebSocketHandler(String serverUrl, WebSocketListener listener) {
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

        switch (serverMessage.serverMessageType) {
            case LOAD_GAME -> listener.onGameLoad(serverMessage);
            case NOTIFICATION -> listener.onNotification(
                    gson.fromJson(message, NotificationMessage.class));
            case ERROR -> listener.onError(serverMessage.errorMessage);
        }
    }

    public void disconnect() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
