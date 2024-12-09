package websocket;


import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface WebSocketListener {
    void onGameLoad(LoadGameMessage loadGameMessageMessage);

    void onNotification(NotificationMessage notificationMessage);

    void onError(ErrorMessage errorMessage);
}