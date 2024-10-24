package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth register(User user) throws ResponseException, DataAccessException {

        if (userDAO.getUser(user.username()) != null) {
            throw new ResponseException(403, "Error: User already exists");
        }

        userDAO.addUser(user);
        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(authToken, user.username());

        return new Auth(authToken, user.username());
    }

    public User getUser(String authToken) throws ResponseException, DataAccessException {
        Auth auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        String username = auth.username();
        return userDAO.getUser(username);
    }

    public Auth login(User user) throws ResponseException, DataAccessException {
        User knownUser = userDAO.getUser(user.username());
        if (knownUser == null) {
            throw new ResponseException(401, "Error: User doesn't exist");
        }
        if (!user.password().equals(knownUser.password())) {
            throw new ResponseException(401, "Error: Password is incorrect");
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.addAuth(authToken, user.username());
        return new Auth(authToken, user.username());
    }

    public boolean isLoggedIn(String authToken) throws ResponseException {
        for (var token : authDAO.listTokens()) {
            if (!Objects.equals(authToken, token.authToken())) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void logout(String authToken) throws ResponseException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ResponseException(401, "Error: Unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
