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

    void deleteAllUsers();

    void update(User user);

}
