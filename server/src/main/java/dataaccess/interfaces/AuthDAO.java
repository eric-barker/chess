package dataaccess.interfaces;

import model.Auth;

public interface AuthDAO {

    public void createAuth(String authToken, String username);

    public Auth readAuth(String authToken);

    public void updateAuth(String oldAuthToken, String newAuthToken);

    public void deleteAuth(String authToken);

    public void deleteAllAuthTokens();
}
