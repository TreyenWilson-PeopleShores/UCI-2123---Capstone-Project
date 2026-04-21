package org.treyenwilson.capstone.eventbooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.treyenwilson.capstone.eventbooking.dto.LoginRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // For now, return 401 since we don't have session/token-based auth yet
        // This endpoint can be implemented later with JWT
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication required");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // For now, just return success since we don't have session management
        // This endpoint can be implemented later with JWT
        return ResponseEntity.ok("Logged out successfully");
    }
}