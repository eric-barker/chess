package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;

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

    public Auth login(User user) throws ResponseException, DataAccessException {

        return null;
    }

    public void logout(String authToken) throws ResponseException {
    }
}
