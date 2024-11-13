import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
    }


    private static void displayPreLoginHelp() {
        System.out.println("\nPre-Login Help:");
        System.out.println("---------------");
        System.out.println("Help      : Displays this help information.");
        System.out.println("Quit      : Exits the program.");
        System.out.println("Login     : Prompts for username and password to log in to your account.");
        System.out.println("Register  : Prompts for new account information to register and log in.\n");
    }
}