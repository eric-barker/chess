package dataaccess.interfaces;

import model.Auth;

import java.util.Collection;

public interface AuthDAO {

    public void addAuth(String authToken, String username);

    public Auth getAuth(String authToken);

    public Collection<Auth> listTokens();

    public void updateAuth(String oldAuthToken, String newAuthToken);

    public void deleteAuth(String authToken);

    public void deleteAllAuthTokens();
}
