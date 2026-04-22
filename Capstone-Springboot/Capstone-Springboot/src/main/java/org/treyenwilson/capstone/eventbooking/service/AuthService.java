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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            if (password.equals(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid username or password");
            }
        }

        return userMapper.toResponse(user);
    }

    public User register(String username, String password, String role) {
        String normalizedRole = normalizeRole(role);
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
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