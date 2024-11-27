package model;

import com.google.gson.annotations.SerializedName;

public record Auth(@SerializedName("username") String username, @SerializedName("authToken") String authToken) {
}

