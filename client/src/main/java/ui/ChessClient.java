package ui;

import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

public class ChessClient {
    private final String serverUrl;
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;
    private final WebSocketFacade ws;
    private UserState state = UserState.SIGNEDOUT;


    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public void eval(String input) throws Exception {
        switch (input.toLowerCase()) {
            case "help":
                displayHelp();
                break;
            case "login":
                System.out.println("Login functionality coming soon!");
                break;
            case "list":
                System.out.println("List of games coming soon!");
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help   - Display this help message.");
        System.out.println("  quit   - Exit the application.");
        System.out.println("  login  - Log in to your account.");
        System.out.println("  list   - List available games.");
    }
}