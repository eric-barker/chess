package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import handler.*;
import logging.LoggerManager;
import model.User;

import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = LoggerManager.getLogger(Server.class.getName());

    static {
        LOGGER.setLevel(Level.ALL);
    }

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final Gson gson = new Gson();

    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    private final RegisterUserHandler registerHandler;
    private final ClearHandler clearHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final CreateGameHandler createGameHandler;
    private final ListGamesHandler listGamesHandler;
    private final JoinGameHandler joinGameHandler;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        try {
            this.userDAO = new MySQLUserDAO();
            this.authDAO = new MySQLAuthDAO();
            this.gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ;

        this.userService = new UserService(userDAO, authDAO);
        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);

        this.registerHandler = new RegisterUserHandler(userService);
        this.clearHandler = new ClearHandler(clearService);
        this.loginHandler = new LoginHandler(userService);
        this.logoutHandler = new LogoutHandler(userService);
        this.createGameHandler = new CreateGameHandler(gameService, userService);
        this.listGamesHandler = new ListGamesHandler(gameService);
        this.joinGameHandler = new JoinGameHandler(gameService, userService);
        this.webSocketHandler = new WebSocketHandler();


    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.before(this::logRequest);

        // Register your endpoints and handle exceptions here.

        // Endpoints
        Spark.post("/user", (req, res) -> registerHandler.handle(req, res));
        Spark.delete("/db", (req, res) -> clearHandler.handle(req, res));
        Spark.post("/session", loginHandler::handle);
        Spark.delete("/session", logoutHandler::handle);
        Spark.post("/game", createGameHandler::handle);
        Spark.get("/game", listGamesHandler::handle);
        Spark.put("/game", joinGameHandler::handle);

        // Add the websocket endpoint here.
        Spark.webSocket("/ws", webSocketHandler);


        // After filter to log the response
        Spark.after(this::logResponse);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void logRequest(spark.Request req, spark.Response res) {
    }

    private void logResponse(spark.Request req, spark.Response res) {
        LOGGER.info("Response Status: " + res.status());
        LOGGER.info("Response Type: " + res.type());
        LOGGER.info("Response Body: " + res.body());
    }


}
