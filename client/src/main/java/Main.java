import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        System.out.println("\nWelcome to the 240 Chess Client\n");
        var serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }


    private static void displayPreLoginHelp() {
        System.out.println("\nPre-Login Help:");
        System.out.println("------------------------------");
        System.out.println("--help      : Displays this help information.");
        System.out.println("--quit      : Exits the program.");
        System.out.println("--login     : Prompts for username and password to log in to your account.");
        System.out.println("--register  : Prompts for new account information to register and log in.\n");
    }
}