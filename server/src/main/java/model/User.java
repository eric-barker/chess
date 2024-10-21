package model;

public record User(String username, String password, String email) {

    public User setUsername(String username) {
        return new User(username, this.password, this.email);
    }
}
