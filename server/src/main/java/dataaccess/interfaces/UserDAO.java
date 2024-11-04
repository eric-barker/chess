package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.User;
import exception.ResponseException;

import java.util.Collection;


public interface UserDAO {

    User addUser(User user) throws DataAccessException;

    Collection<User> listUsers() throws DataAccessException;

    User getUser(String username) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;

    void update(User user) throws DataAccessException;

    void storeUserPassword(String username, String password, String email) throws DataAccessException;

    boolean verifyUserPassword(String username, String providedClearTextPassword) throws DataAccessException;
}
