package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;
import logging.LoggerManager;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger LOGGER = LoggerManager.getLogger(UserService.class.getName());
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        LOGGER.setLevel(Level.WARNING);
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth register(User user) throws ResponseException, DataAccessException {
        LOGGER.log(Level.INFO, "Attempting to register user: {0}", user.username());

        if (userDAO.getUser(user.username()) != null) {
            LOGGER.log(Level.WARNING, "User already exists: {0}", user.username());
            throw new ResponseException(403, "Error: User already exists");
        }

        userDAO.addUser(user);
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(user.username(), authToken);

        Auth auth = new Auth(user.username(), authToken);
        LOGGER.log(Level.INFO, "Registration successful. Generated Auth: {0}", auth);
        return auth;
    }

    public User getUser(String authToken) throws ResponseException, DataAccessException {
        LOGGER.log(Level.INFO, "Fetching user for authToken: {0}", authToken);

        Auth auth = authDAO.getAuth(authToken);
        if (auth == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access attempt with authToken: {0}", authToken);
            throw new ResponseException(401, "Error: unauthorized");
        }

        String username = auth.username();
        User user = userDAO.getUser(username);
        LOGGER.log(Level.INFO, "User fetched successfully: {0}", username);
        return user;
    }

    public Auth login(String username, String password) throws ResponseException, DataAccessException {
        LOGGER.log(Level.INFO, "Attempting login for username: {0}", username);

        User user = userDAO.getUser(username);
        if (user == null || !userDAO.verifyUserPassword(username, password)) {
            LOGGER.log(Level.WARNING, "Invalid login attempt for username: {0}", username);
            throw new ResponseException(401, "Invalid credentials");
        }

        String authToken = UUID.randomUUID().toString(); // Generate new auth token
        authDAO.addAuth(username, authToken);           // Save to database

        Auth auth = new Auth(username, authToken);
        LOGGER.log(Level.INFO, "Login successful. Generated Auth: {0}", auth);
        return auth;
    }

    public boolean isLoggedIn(String authToken) throws ResponseException, DataAccessException {
        LOGGER.log(Level.INFO, "Checking login status for authToken: {0}", authToken);

        for (var token : authDAO.listTokens()) {
            if (Objects.equals(authToken, token.authToken())) {
                LOGGER.log(Level.INFO, "Auth token is valid: {0}", authToken);
                return true;
            }
        }

        LOGGER.log(Level.WARNING, "Auth token is not valid: {0}", authToken);
        return false;
    }

    public void logout(String authToken) throws ResponseException, DataAccessException {
        LOGGER.log(Level.INFO, "Attempting logout for authToken: {0}", authToken);

        if (authDAO.getAuth(authToken) == null) {
            LOGGER.log(Level.WARNING, "Unauthorized logout attempt with authToken: {0}", authToken);
            throw new ResponseException(401, "Error: Unauthorized");
        }

        authDAO.deleteAuth(authToken);
        LOGGER.log(Level.INFO, "Logout successful for authToken: {0}", authToken);
    }
}
