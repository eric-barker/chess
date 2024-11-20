
package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Scanner;

public class PostLoginClient {
    private final String serverUrl;
    private final Repl repl;
    private final ServerFacade serverFacade;

    public PostLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.serverFacade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        String[] tokens = input.split("\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return getHelpText();
            case "logout":
                return logout();
            case "creategame":
                return createGame();
            case "listgames":
                return listGames();
            case "joingame":
                return joinGame();
            case "observegame":
                return observeGame();
            default:
                return "Unknown command. Type 'help' for a list of commands.";
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "logout       - Logout and return to the main menu.\n" +
                "creategame   - Create a new game.\n" +
                "listgames    - List all available games.\n" +
                "joingame     - Join a game to play.\n" +
                "observegame  - Observe a game in progress.\n";
    }

    private String logout() {
        repl.changeState(UserState.LOGGEDOUT);

        try {
            String authToken = repl.getAuthToken();
            serverFacade.logout(authToken);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        return "You have been logged out.";
    }

    private String createGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Game Name: ");
        String gameName = scanner.nextLine();

        System.out.println("Attempting to create game...");
        try {
            String authToken = repl.getAuthToken();
            var auth = serverFacade.createGame(gameName, authToken);
        } catch (ResponseException e) {
            return "Game creation failed: " + e.getMessage();
        }
        // Stub for creating a game
        return "Game created successfully.";
    }

    private String listGames() {
        System.out.println("Attempting to list games...");
        try {
            // Retrieve the list of games
            var listOfGames = serverFacade.listGames(repl.getAuthToken());
            if (listOfGames == null || listOfGames.length == 0) {
                return "No games found.";
            }

            // Print the game names to the console
            System.out.println("Games available:");
            for (var game : listOfGames) {
                System.out.println("- " + game.gameName());
            }

            return "Games listed successfully.";
        } catch (ResponseException e) {
            return "List games failed: " + e.getMessage();
        }
    }


    private String joinGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the game you want to join: ");

        boolean isUsername = false;
        String gameName = "";
        while (!isUsername || gameName == "exit") {
            gameName = scanner.nextLine();

            // validate that the game exists in the database

        }
        // List available colors.

        // Ask for user to choose one of the available colors


        // Stub for joining a game
        repl.changeState(UserState.INGAME);
        return "Joining game (stub).";
    }

    private String observeGame() {
        // Stub for observing a game
        return "Observing game (stub).";
    }
}
