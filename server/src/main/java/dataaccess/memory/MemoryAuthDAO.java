package dataaccess.memory;

import dataaccess.interfaces.AuthDAO;
import model.Auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, Auth> authTokens = new HashMap<>();

    @Override
    public void addAuth(String authToken, String username) {
        Auth auth = new Auth(authToken, username);
        authTokens.put(authToken, auth);
    }

    @Override
    public Auth getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public Collection<Auth> listTokens() {
        return authTokens.values();
    }

    @Override
    public void updateAuth(String oldAuthToken, String newAuthToken) {
        if (authTokens.get(oldAuthToken) != null) {
            Auth newAuth = new Auth(newAuthToken, authTokens.get(oldAuthToken).username());
            authTokens.put(newAuthToken, newAuth);

            authTokens.remove(oldAuthToken, authTokens.get(oldAuthToken));
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public void deleteAllAuthTokens() {
        authTokens.clear();
    }
}
