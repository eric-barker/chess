package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import model.Auth;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public void createAuth(String AuthID, String Username) {
        
    }

    @Override
    public Auth readAuth(Auth authData) {
        return null;
    }

    @Override
    public Auth updateAuth(Auth oldAuthData, Auth newAuthData) {
        return null;
    }

    @Override
    public void deleteAuth(String AuthToken) {

    }

    @Override
    public void deleteAllAuthTokens() {

    }
}
