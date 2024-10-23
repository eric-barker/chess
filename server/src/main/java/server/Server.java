package server;

import com.google.gson.Gson;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.GameDAO;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import exception.ResponseException;
import handler.*;
import model.User;

import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

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

    public Server() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();

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

    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Endpoints
        Spark.post("/user", (req, res) -> registerHandler.handle(req, res));
        Spark.delete("/db", (req, res) -> clearHandler.handle(req, res));
        Spark.post("/session", loginHandler::handle);
        Spark.delete("/session", logoutHandler::handle);
        Spark.post("/game", createGameHandler::handle);
        Spark.get("/game", listGamesHandler::handle);
        Spark.put("/game", joinGameHandler::handle);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }
    

    private Object listUsers(Request req, Response res) throws ResponseException {
        res.type("application/json");
        return gson.toJson(userDAO.listUsers());
    }

    private Object deleteUser(Request req, Response res) throws ResponseException {
        User newUser = gson.fromJson(req.body(), User.class);
        if (userDAO.getUser(newUser.username()) != null) {
            res.status(201);
            return "User successfully deleted";
        }

        res.status(403);
        return "User does not exist";
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
