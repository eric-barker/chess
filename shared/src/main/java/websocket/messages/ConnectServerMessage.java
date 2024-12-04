package websocket.messages;

import chess.ChessGame;

public class ConnectServerMessage extends ServerMessage {
    private ChessGame game;

    public ConnectServerMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
