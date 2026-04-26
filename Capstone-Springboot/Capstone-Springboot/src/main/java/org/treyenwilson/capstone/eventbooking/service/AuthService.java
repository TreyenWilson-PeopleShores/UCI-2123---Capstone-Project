package org.treyenwilson.capstone.eventbooking.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.UserMapper;
import org.treyenwilson.capstone.eventbooking.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse authenticate(String username, String password) {
        // Trim and sanitize inputs
        String trimmedUsername = username != null ? username.trim() : "";
        String trimmedPassword = password != null ? password.trim() : "";
        
        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }
        
        User user = userRepository.findByUsername(trimmedUsername)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(trimmedPassword, user.getPassword())) {
            if (trimmedPassword.equals(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(trimmedPassword));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid username or password");
            }
        }

        return userMapper.toResponse(user);
    }

    public User register(String username, String password, String role) {
        // Trim and sanitize inputs
        String trimmedUsername = username != null ? username.trim() : "";
        String trimmedPassword = password != null ? password.trim() : "";
        String trimmedRole = role != null ? role.trim() : "";
        
        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            throw new RuntimeException("Username and password are required");
        }
        
        String normalizedRole = normalizeRole(trimmedRole);
        User user = new User();
        user.setUsername(trimmedUsername);
        user.setPassword(passwordEncoder.encode(trimmedPassword));
        user.setRole(normalizedRole);
        return userRepository.save(user);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }

        String normalized = role.trim();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }

        return normalized.toUpperCase();
    }
}