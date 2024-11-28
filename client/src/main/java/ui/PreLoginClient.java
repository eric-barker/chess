package ui;

import logging.LoggerManager;
import model.User;
import server.ServerFacade;

import java.util.Scanner;
import java.util.logging.Logger;

public class PreLoginClient {
    private static final Logger LOGGER = LoggerManager.getLogger(PreLoginClient.class.getName());
    private final String serverUrl;
    private final Repl repl;
    private final ServerFacade serverFacade;

    public PreLoginClient(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.serverFacade = new ServerFacade(serverUrl); // Initialize ServerFacade with serverUrl
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

        System.out.println("Attempting to login...");
        try {
            var auth = serverFacade.login(username, password); // Use ServerFacade for login
            repl.changeState(UserState.LOGGEDIN);
            repl.setUsername(auth.username());
            repl.setAuthToken(auth.authToken());
            return "Login successful. Welcome, " + auth.username() + "!";
        } catch (Exception e) {
            LOGGER.warning("Login failed: " + e.getMessage());
            return "Login failed!";
        }
    }

    private String register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.println("Attempting to register...");
        User newUser = new User(username, password, email);

        try {
            var auth = serverFacade.register(newUser); // Use ServerFacade for registration
            repl.changeState(UserState.LOGGEDIN);
            repl.setUsername(auth.username());
            repl.setAuthToken(auth.authToken());
            return "Registration successful. Welcome, " + auth.username() + "!";
        } catch (Exception e) {
            LOGGER.warning("Registration failed: " + e.getMessage());
            return "Registration failed!";
        }
    }
}
