package org.treyenwilson.capstone.eventbooking.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;
import org.treyenwilson.capstone.eventbooking.dto.AuthResponse;
import org.treyenwilson.capstone.eventbooking.dto.AuthWithUserResponse;
import org.treyenwilson.capstone.eventbooking.dto.LoginRequest;
import org.treyenwilson.capstone.eventbooking.dto.RegisterRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.service.AuthService;
import org.treyenwilson.capstone.eventbooking.security.CustomUserDetailsService;
import org.treyenwilson.capstone.eventbooking.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public ApiAuthController(AuthService authService,
                             CustomUserDetailsService userDetailsService,
                             JwtUtil jwtUtil) {
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            UserResponse user = authService.authenticate(request.getUsername(), request.getPassword());
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthWithUserResponse(token, user));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String role = request.getRole();
            var user = authService.register(request.getUsername(), request.getPassword(), role);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            // Convert User entity to UserResponse
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setRole(user.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthWithUserResponse(token, userResponse));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
