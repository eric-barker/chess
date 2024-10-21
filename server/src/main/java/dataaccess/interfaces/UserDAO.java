package dataaccess.interfaces;

import model.User;
import exception.ResponseException;

import java.util.Collection;


public interface UserDAO {

    User addUser(User user) throws ResponseException;

    Collection<User> listUsers() throws ResponseException;

    User getUser(String username) throws ResponseException;

    void deleteUser(String username) throws ResponseException;

    void deleteAllUsers();

    void update(User user);

}
