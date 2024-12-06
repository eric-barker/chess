package ui;


import exception.ResponseException;
import model.Auth;
import model.Game;
import server.ServerFacade;
import webSocket.WebSocketListener;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameLoad;
import websocket.messages.Notification;

import java.util.Scanner;

public class Repl implements WebSocketListener {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final InGameClient inGameClient;
    private UserState state;
    private Auth auth = null;
    private String username = null;
    private String authToken = null;
    private final String serverUrl;
    private boolean isObserver = false;
    private Game game;


    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl, this);
        this.postLoginClient = new PostLoginClient(serverUrl, this);
        this.inGameClient = new InGameClient(serverUrl, this);
        this.state = UserState.LOGGEDOUT; // Initial state is logged out
        this.serverUrl = serverUrl;
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
                        case INGAME, OBSERVER:
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
            gracefulClose();

        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
    }

    @Override
    public void onGameLoad(GameLoad gameLoadMessage) {
        String message = "The state of the game has changed! <Placeholder>";
        printNotification(message);
    }

    @Override
    public void onNotification(Notification notificationMessage) {
        String message = notificationMessage.getMessage();
        printNotification(message);
    }

    @Override
    public void onError(ErrorMessage errorMessage) {
        String message = errorMessage.getMessage();
        printNotification(message);
    }

    public void changeState(UserState newState) {
        this.state = newState;
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + "[" + state.name() + "]" +
                EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_YELLOW);
    }

    private void printNotification(String message) {
        System.out.print("/n" + EscapeSequences.RESET_TEXT_COLOR + "--- " + EscapeSequences.SET_TEXT_COLOR_YELLOW + message);
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

    public void setIsObserver(boolean isObserver) {
        this.isObserver = isObserver;
    }

    public boolean getIsObserver() {
        return this.isObserver;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return this.game;
    }

    private void gracefulClose() {
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        try {
            serverFacade.logout(authToken);
            authToken = null;
            username = null;
        } catch (ResponseException e) {
            System.err.println("Graceful Close Error: " + e.getMessage());
        }
    }

}
