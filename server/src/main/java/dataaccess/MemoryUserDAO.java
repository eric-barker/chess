package dataaccess;

import exception.ResponseException;
import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public User addUser(User user) throws ResponseException {
        users.put(user.username(), user);
        return user;
    }

    @Override
    public Collection<User> listUsers() throws ResponseException {
        return users.values();
    }

    @Override
    public User getUser(String username) throws ResponseException {
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) throws ResponseException {
        users.remove(username);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }

    @Override
    public void update(User user) {
        users.put(user.username(), user);
    }
}
