package model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public record User(String username, String password, String email) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }


    public String username() {
        return username;
    }


    public String password() {
        return password;
    }


    public String getEmail() {
        return email;
    }
}
