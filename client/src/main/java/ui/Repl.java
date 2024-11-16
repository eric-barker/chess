package ui;

import ui.ChessClient;
import ui.websocket.NotificationHandler;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to Chess Client!");
        System.out.println("Type 'help' for a list of commands.");

        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Exiting the application...");
                client.cleanup(); // Disconnect WebSocket and other cleanup tasks
                break;
            }

            try {
                client.eval(input); // Delegate command processing to ChessClient
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    @Override
    public void handleNotification(String message) {
        // Print notifications from the server
        System.out.println("\n[Notification]: " + message);
        System.out.print("> "); // Reprint prompt after a notification
    }
}
