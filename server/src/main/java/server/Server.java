package server;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.User;

import spark.*;

public class Server {

    private final UserDAO userDAO;
    private final Gson gson = new Gson();

    public Server(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Endpoint to add a user
        Spark.post("/user", this::addUser);

        // Endpoint to list all users
        Spark.get("/user/list", this::listUsers);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object addUser(Request req, Response res) throws ResponseException {
        User newUser = gson.fromJson(req.body(), User.class);
        userDAO.addUser(newUser);
        res.status(201); // Created
        return "User added successfully";
    }

    private Object listUsers(Request req, Response res) throws ResponseException {
        res.type("application/json");
        return gson.toJson(userDAO.listUsers());
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
