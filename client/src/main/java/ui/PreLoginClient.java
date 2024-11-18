
package ui;

import java.util.Scanner;

public class PreLoginClient {
    private final String serverUrl;
    private final Repl repl;

    public PreLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public String eval(String input) {
        String[] tokens = input.split("\\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return getHelpText();
            case "login":
                return login();
            case "register":
                return register();
            case "quit":
                return "quit";
            default:
                return "Unknown command. Type 'help' for a list of commands.";
        }
    }

    private String getHelpText() {
        return """
                Available commands:
                help     - Show this help text.
                login    - Login to your account.
                register - Register a new account.
                quit     - Exit the application.
                """;
    }

    private String login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Placeholder for server login interaction
        System.out.println("Attempting to login...");
        boolean success = stubServerLogin(username, password);

        if (success) {
            repl.changeState(UserState.LOGGEDIN);
            return "Login successful. Welcome, " + username + "!";
        } else {
            return "Login failed. Please try again.";
        }
    }

    private String register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        // Stub for server registration interaction
        System.out.println("Attempting to register...");
        boolean success = stubServerRegister(username, password);

        if (success) {
            repl.changeState(UserState.LOGGEDIN);
            return "Registration successful. Welcome, " + username + "!";
        } else {
            return "Registration failed. Username may already exist.";
        }
    }

    // Stub methods for server interaction
    private boolean stubServerLogin(String username, String password) {
        // Replace this with actual server interaction later.
        return "user".equals(username) && "pass".equals(password);
    }

    private boolean stubServerRegister(String username, String password) {
        // Replace this with actual server interaction later.
        return !username.isEmpty() && !password.isEmpty();
    }
}

