package ui;


import chess.ChessGame;
import exception.ResponseException;
import logging.LoggerManager;
import model.Auth;
import model.Game;
import server.ServerFacade;
import webSocket.WebSocketFacade;
import webSocket.WebSocketListener;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;
import java.util.logging.Logger;

public class Repl implements WebSocketListener {

    private static final Logger LOGGER = LoggerManager.getLogger(Repl.class.getName());
    private WebSocketFacade webSocketFacade;
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
        this.webSocketFacade = new WebSocketFacade(serverUrl, this);
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
    public void onGameLoad(LoadGameMessage loadGameMessageMessage) {
        ChessGame chessGame = game.game();
//        game. = gameLoadMessage.getGame(); // Update the game state in Repl
        printNotification("Game state updated!");
//        inGameClient.renderBoard(); // Call InGameClient to redraw the chessboard
    }

    @Override
    public void onNotification(NotificationMessage notificationMessage) {
        String message = notificationMessage.getMessage();
        if (message.contains("left the game")) {
            LOGGER.info("Notification received: " + message);
        }
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

    public WebSocketFacade getWebSocketHandler() {
        return this.webSocketFacade;
    }

    private void gracefulClose() {
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        try {
            serverFacade.logout(authToken);
            webSocketFacade.disconnect();
            authToken = null;
            username = null;
        } catch (ResponseException e) {
            LOGGER.warning("Error gracefully closing: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Error gracefully closing: " + e.getMessage());
        }
    }

}
