package webSocket;


import websocket.commands.UserGameCommand;

public interface UserGameCommandHandler {
    void notify(UserGameCommand notification);


}