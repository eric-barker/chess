package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import model.Auth;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, Auth> authTokens = new HashMap<>();

    @Override
    public void createAuth(String authToken, String username) {
        Auth auth = new Auth(authToken, username);
        authTokens.put(authToken, auth);
    }

    @Override
    public Auth readAuth(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) {
        if (authTokens.get(oldAuthToken) != null) {
            Auth newAuth = new Auth(newAuthToken, authTokens.get(oldAuthToken).username());
            authTokens.put(newAuthToken, newAuth);
        }
    }

    @Override
    public void deleteAuth(String AuthToken) {
        authTokens.remove(AuthToken);
    }

    @Override
    public void deleteAllAuthTokens() {
        authTokens.clear();
    }
}
