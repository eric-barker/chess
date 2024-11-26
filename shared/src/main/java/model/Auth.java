package model;

import com.google.gson.annotations.SerializedName;

public record Auth(@SerializedName("user_name") String username, @SerializedName("auth_token") String authToken) {
}

