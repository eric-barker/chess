package server;

import com.google.gson.Gson;
import dataaccess.interfaces.UserDAO;
import dataaccess.memory.MemoryUserDAO;
import exception.ResponseException;
import model.User;

import spark.*;

public class Server {

    private final UserDAO userDAO;
    private final Gson gson = new Gson();

    public Server() {
        this.userDAO = new MemoryUserDAO();
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Endpoints
        Spark.post("/user", this::registerUser);
        Spark.get("/user/list", this::listUsers);
        Spark.get("/user/delete", this::deleteUser);


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }


    private Object registerUser(Request req, Response res) throws ResponseException {
        User newUser = gson.fromJson(req.body(), User.class);

        // Check if the user already exists
        if (userDAO.getUser(newUser.username()) != null) {
            res.status(403);  // Forbidden
            return "User already exists";
        }

        // Add the new user
        userDAO.addUser(newUser);
        res.status(201);  // Created
        return "User Successfully Added";
        // return gson.toJson(new TestAuthResult(newUser.username(), "authToken123"));
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
