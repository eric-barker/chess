package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.interfaces.UserDAO;
import model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public User addUser(User user) throws DataAccessException {
        users.put(user.username(), user);
        return user;
    }

    @Override
    public Collection<User> listUsers() throws DataAccessException {
        return users.values();
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
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
