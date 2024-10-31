package dataaccess;

import dataaccess.interfaces.AuthDAO;
import dataaccess.DataAccessException;
import model.Auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void addAuth(String authToken, String username) throws DataAccessException {
        // Placeholder for adding a new auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        // Placeholder for retrieving an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Collection<Auth> listTokens() throws DataAccessException {
        // Placeholder for listing all auth tokens
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) throws DataAccessException {
        // Placeholder for updating an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // Placeholder for deleting an auth token
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllAuthTokens() throws DataAccessException {
        // Placeholder for deleting all auth tokens
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void configureDatabase() throws DataAccessException {
        // Placeholder for configuring database if table doesn't exist
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
