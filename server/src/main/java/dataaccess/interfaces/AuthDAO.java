package dataaccess.interfaces;

import model.Auth;

public interface AuthDAO {

    public void createAuth(String AuthID, String Username);

    public Auth readAuth(Auth authData);

    public Auth updateAuth(Auth oldAuthData, Auth newAuthData);

    public void deleteAuth(String AuthToken);

    public void deleteAllAuthTokens();
}
