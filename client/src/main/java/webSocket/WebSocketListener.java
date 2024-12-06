package webSocket;


import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameLoad;
import websocket.messages.Notification;

public interface WebSocketListener {
    void onGameLoad(GameLoad gameLoadMessage);

    void onNotification(Notification notificationMessage);

    void onError(ErrorMessage errorMessage);
}