package dataaccess.interfaces;

import dataaccess.DataAccessException;
import model.Auth;

import java.util.Collection;

public interface AuthDAO {

    public void addAuth(String authToken, String username) throws DataAccessException;

    public Auth getAuth(String authToken) throws DataAccessException;

    public Collection<Auth> listTokens() throws DataAccessException;

    public void updateAuth(String oldAuthToken, String newAuthToken) throws DataAccessException;

    public void deleteAuth(String authToken) throws DataAccessException;

    public void deleteAllAuthTokens() throws DataAccessException;
}
