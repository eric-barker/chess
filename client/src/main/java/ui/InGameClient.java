
package ui;

public class InGameClient {
    private final String serverUrl;
    private final Repl repl;

    public InGameClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        String[] tokens = input.split("\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return getHelpText();
            case "renderboard":
                return renderBoard();
            case "makemove":
                return makeMove();
            case "exitgame":
                return exitGame();
            default:
                return "Unknown command. Type 'help' for a list of commands.";
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n" +
                "makeMove     - Make a move.\n" +
                "exitGame     - Exit the game.\n";
    }

    private String renderBoard() {
        // Stub for rendering the board
        return "Rendering the chessboard (stub).";
    }

    private String makeMove() {
        // Stupb for making moves
        return "Making a move";
    }

    private String exitGame() {
        repl.changeState(UserState.LOGGEDIN);
        return "Exiting Game";
    }
}