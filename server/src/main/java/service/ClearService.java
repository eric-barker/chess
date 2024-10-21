package service;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;

public class ClearService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    // Constructor
    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.deleteAllUsers();
        gameDAO.deleteAllGames();
        AuthDAO.deleteAllAuthTokens();
    }

}
