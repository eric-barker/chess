package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import server.ServerFacade;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.util.Arrays;

public class ChessClient {
    private final String serverUrl;
    private final ServerFacade server;
    private final NotificationHandler notificationHandler;
    private final WebSocketFacade ws;
    private UserState state = UserState.SIGNEDOUT;


    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (input.toLowerCase()) {
                case "help":
                    displayHelp();
                    break;
                case "login":
                    System.out.println("Login functionality coming soon!");
                    break;
                case "list":
                    System.out.println("List of games coming soon!");
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for a list of commands.");
            }
            ;
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

//    public String signIn(String... params) throws ResponseException {
//        if(params.length >= 1){
//            state = UserState.SIGNEDIN;
//            visitorName = String.join("-", params);
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
//            return String.format("You signed in as %s.", visitorName);
//        }
//        throw new ResponseException(400, "Expected: <yourname>");
//    }

    public String list() throws ResponseException {
        assertSignedIn();
        var games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    private String displayHelp() {
        if (state == UserState.SIGNEDOUT) {
            System.out.println("Available commands:");
            System.out.println("-help      - Display this help message.");
            System.out.println("-quit      - Exit the application.");
            System.out.println("-login     - Log in to your account.");
            System.out.println("-register  - Register as a new user.");
        }
        return """
                - create    <name>                  - a game
                - list                              - games
                - join      <ID>    [WHITE|BLACK]   - a game
                - observe   <ID>                    - a game
                - help
                - logout
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == UserState.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}