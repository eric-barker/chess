package websocket.messages;

import chess.ChessGame;

public class GameLoad extends ServerMessage {
    private ChessGame game;

    public GameLoad(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return this.game;
    }
}
