package model;

public record Auth(String authToken, String username) {
    @Override
    public String username() {
        return username;
    }

    @Override
    public String authToken() {
        return authToken;
    }
}

