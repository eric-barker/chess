package ui;


import exception.ResponseException;
import model.Auth;

import java.util.Scanner;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final InGameClient inGameClient;
    private UserState state;
    private Auth auth = null;
    private String username = null;
    private String authToken = null;


    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl, this);
        this.postLoginClient = new PostLoginClient(serverUrl, this);
        this.inGameClient = new InGameClient(serverUrl, this);
        this.state = UserState.LOGGEDOUT; // Initial state is logged out
    }

    public void run() {
        System.out.println("Welcome to Chess Client!");
        System.out.println("Type 'help' for a list of commands.");

        Scanner scanner = new Scanner(System.in);
        var input = "";

        try {
            while (!input.equalsIgnoreCase("quit")) {
                printPrompt();

                input = scanner.nextLine().trim();
                String result = "";
                try {
                    switch (state) {
                        case LOGGEDOUT:
                            result = preLoginClient.eval(input);
                            break;
                        case LOGGEDIN:
                            result = postLoginClient.eval(input);
                            break;
                        case INGAME:
                            result = inGameClient.eval(input);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected state: " + state);
                    }

                    if (!result.isEmpty()) {
                        System.out.println(result);
                    }

                } catch (Exception e) {
                    throw new ResponseException(500, e.getMessage());
                }

            }
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
    }

    public void changeState(UserState newState) {
        this.state = newState;
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + "[" + state.name() + "]" +
                EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_YELLOW);
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
