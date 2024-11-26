
package ui;

import exception.ResponseException;
import model.Game;
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
        String gameName = "";
        boolean gameNameExists = true;

        try {
            String authToken = repl.getAuthToken();

            while (gameNameExists) {
                System.out.print("Enter Game Name: ");
                gameName = scanner.nextLine();

                // Validate the game name against existing games
                var listOfGames = serverFacade.listGames(authToken);
                gameNameExists = false;
                for (var game : listOfGames) {
                    if (game.gameName().equalsIgnoreCase(gameName)) {
                        gameNameExists = true;
                        System.out.println("Game name already exists. Please choose a different name.");
                        break;
                    }
                }

                if (gameName.isBlank()) {
                    System.out.println("Game name cannot be empty. Please try again.");
                    gameNameExists = true;
                }
            }

            System.out.println("Attempting to create game...");
            serverFacade.createGame(gameName, authToken);
            return "Game created successfully.";

        } catch (ResponseException e) {
            return "Game creation failed: " + e.getMessage();
        }
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
            int counter = 1;
            for (var game : listOfGames) {
                System.out.println(counter + " - " + game.gameName() + ", White User: "
                        + game.whiteUsername() + ", Black User: " + game.blackUsername());
                counter++;
            }

            return "Games listed successfully.";
        } catch (ResponseException e) {
            return "List games failed: " + e.getMessage();
        }
    }


    private String joinGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the game you want to join: ");

        boolean gameExists = false;
        String gameName = "";
        int gameID = 0;

        while (!gameExists) {
            gameName = scanner.nextLine();

            // Validate that the game exists in the database
            try {
                var listOfGames = serverFacade.listGames(repl.getAuthToken());
                for (var game : listOfGames) {
                    if (game.gameName().equalsIgnoreCase(gameName)) {
                        gameExists = true;
                        gameID = game.gameID();
                        break;
                    }
                }

                if (!gameExists) {
                    System.out.println("Game not found. Please try again or type 'exit' to cancel.");
                    if (gameName.equalsIgnoreCase("exit")) {
                        return "Join game cancelled.";
                    }
                }
            } catch (ResponseException e) {
                return "Error retrieving game list: " + e.getMessage();
            }
        }

        // List available colors
        System.out.println("Available colors: white, black");

        // Ask the user to choose one of the available colors
        String playerColor = "";
        while (!playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
            System.out.print("Choose your color (white/black): ");
            playerColor = scanner.nextLine();
            if (!playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
                System.out.println("Invalid color. Please choose 'white' or 'black'.");
            }
        }

        // Attempt to join the game
        try {
            String authToken = repl.getAuthToken();
            serverFacade.joinGame(gameID, playerColor, authToken);
            repl.changeState(UserState.INGAME);
            return "Successfully joined the game '" + gameName + "' as " + playerColor + ".";
        } catch (ResponseException e) {
            return "Failed to join the game: " + e.getMessage();
        }
    }


    private String observeGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the game you want to observe: ");

        Game myGame = null;
        boolean gameExists = false;
        String gameName = "";
        int gameID = 0;

        while (!gameExists) {
            gameName = scanner.nextLine();

            // Validate that the game exists in the database
            try {
                var listOfGames = serverFacade.listGames(repl.getAuthToken());
                for (var game : listOfGames) {
                    if (game.gameName().equalsIgnoreCase(gameName)) {
                        gameExists = true;
                        gameID = game.gameID();
                        myGame = game;
                        break;
                    }
                }

                if (!gameExists) {
                    System.out.println("Game not found. Please try again or type 'exit' to cancel.");
                    if (gameName.equalsIgnoreCase("exit")) {
                        return "Join game cancelled.";
                    }
                }
            } catch (ResponseException e) {
                return "Error retrieving game list: " + e.getMessage();
            }
        }

        // Attempt observe the game
        try {
            String authToken = repl.getAuthToken();

            repl.changeState(UserState.OBSERVER);
            repl.setIsObserver(true);


            return "Successfully observing game '" + myGame.gameName() + "', Game ID: " + myGame.gameID();

        } catch (Exception e) {
            return "Failed to join the game: " + e.getMessage();
        }
    }
}
