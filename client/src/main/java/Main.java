import chess.*;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("\nMust Provide arguments.");
            displayPreLoginHelp();
        }
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