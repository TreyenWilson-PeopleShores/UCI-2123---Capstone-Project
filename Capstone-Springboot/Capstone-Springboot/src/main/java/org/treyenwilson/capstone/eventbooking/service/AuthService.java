package org.treyenwilson.capstone.eventbooking.service;

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

    public AuthService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse authenticate(String username, String password) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Compare passwords (plaintext for now - will be encrypted in later phase)
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Return user response (password will be excluded by mapper)
        return userMapper.toResponse(user);
    }
}