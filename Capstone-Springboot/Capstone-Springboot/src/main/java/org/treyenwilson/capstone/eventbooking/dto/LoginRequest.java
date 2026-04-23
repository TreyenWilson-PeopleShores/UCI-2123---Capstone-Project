package org.treyenwilson.capstone.eventbooking.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    @jakarta.validation.constraints.Size(max = 50, message = "Username must not exceed 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(max = 100, message = "Password must not exceed 100 characters")
    private String password;

    public LoginRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

