
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
            default:
                return "Unknown command. Type 'help' for a list of commands.";
        }
    }

    private String getHelpText() {
        return "Available commands:\n" +
                "help         - Show this help text.\n" +
                "renderboard  - Display the chessboard.\n";
    }

    private String renderBoard() {
        // Stub for rendering the board
        return "Rendering the chessboard (stub).";
    }
}