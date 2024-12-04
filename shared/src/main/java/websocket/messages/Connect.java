package websocket.messages;

import chess.ChessGame;

public class Connect extends ServerMessage {
    private ChessGame game;

    public Connect(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
