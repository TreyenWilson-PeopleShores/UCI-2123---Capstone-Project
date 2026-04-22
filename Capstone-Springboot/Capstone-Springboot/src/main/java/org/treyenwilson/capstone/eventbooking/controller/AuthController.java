package org.treyenwilson.capstone.eventbooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.treyenwilson.capstone.eventbooking.dto.LoginRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.dto.AuthWithUserResponse;
import org.treyenwilson.capstone.eventbooking.service.AuthService;
import org.treyenwilson.capstone.eventbooking.security.CustomUserDetailsService;
import org.treyenwilson.capstone.eventbooking.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                         CustomUserDetailsService userDetailsService,
                         JwtUtil jwtUtil) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            // Also generate JWT token for backward compatibility
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthWithUserResponse(token, user));
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