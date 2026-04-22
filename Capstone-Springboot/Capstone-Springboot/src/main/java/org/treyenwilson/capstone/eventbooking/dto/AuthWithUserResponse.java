package org.treyenwilson.capstone.eventbooking.dto;

public class AuthWithUserResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserResponse user;

    public AuthWithUserResponse() {
    }

    public AuthWithUserResponse(String accessToken, UserResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}