package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;

@websocket
public class WebSocketHandler {

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try{
            UserGameCommand command = Serializer.fromJson(message, UserGameCommand.class);

            // Check AuthToken
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            } catch (UnauthorizedException e){
                sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
            } catch (Exception ex){
                ex.printStackTrace();
                sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
            }
    }
}
