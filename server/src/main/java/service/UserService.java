package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.AuthDAO;
import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.Auth;
import model.User;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public Auth register(User user) throws ResponseException, DataAccessException {
        return null;
    }

    public Auth login(User user) throws ResponseException, DataAccessException {
        return null;
    }

    public void logout(String authToken) throws ResponseException {
    }
}
