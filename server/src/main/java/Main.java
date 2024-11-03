import chess.*;
import dataaccess.DataAccessException;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        var port = 8080;
        var server = new Server();  // Create an instance of Server
        int actualPort = server.run(port);  // Call the run method on the instance
        System.out.println("Server is running on port: " + actualPort);
    }
}