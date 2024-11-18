
package ui;

public class PostLoginClient {
    private final String serverUrl;
    private final Repl repl;

    public PostLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
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
            case "playgame":
                return playGame();
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
                "playgame     - Join a game to play.\n" +
                "observegame  - Observe a game in progress.\n";
    }

    private String logout() {
        repl.changeState(UserState.LOGGEDOUT);
        return "You have been logged out.";
    }

    private String createGame() {
        // Stub for creating a game
        return "Game created successfully (stub).";
    }

    private String listGames() {
        // Stub for listing games
        return "Listing games (stub).";
    }

    private String playGame() {
        // Stub for joining a game
        repl.changeState(UserState.INGAME);
        return "Joining game (stub).";
    }

    private String observeGame() {
        // Stub for observing a game
        return "Observing game (stub).";
    }
}
