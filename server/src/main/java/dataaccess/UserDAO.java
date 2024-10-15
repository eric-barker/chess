package dataaccess;

import Models.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    // Create a new user
    void create(User user);

    // Read or retrieve a user by username
    Optional<User> read(String username);

    // Update an existing user
    void update(User user);

    // Delete a user by username
    void delete(String username);

    // Retrieve all users
    List<User> readAll();
}
