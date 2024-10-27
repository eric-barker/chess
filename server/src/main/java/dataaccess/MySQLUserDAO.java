package dataaccess;

import dataaccess.interfaces.UserDAO;
import exception.ResponseException;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws ResponseException {
        configureDatabase();
    }

    @Override
    public User addUser(User user) throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Collection<User> listUsers() throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User getUser(String username) throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteUser(String username) throws ResponseException {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllUsers() {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void update(User user) {
        // Placeholder method
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public void configureDatabase() throws ResponseException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}