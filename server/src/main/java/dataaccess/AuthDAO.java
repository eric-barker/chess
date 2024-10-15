package dataaccess;

import Models.Auth;

public interface AuthDAO {
    // Create
    public void createAuthData(String AuthID, String Username);

    // Read
    public Auth readAuthData(Auth authData);

    // Update
    public Auth updateAuthData(Auth oldAuthData, Auth newAuthData);

    // Delete


}
