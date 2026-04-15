package org.treyenwilson.capstone.eventbooking.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.UserMapper;
import org.treyenwilson.capstone.eventbooking.repository.UserRepository;

@Service
public class UserService{
    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    public UserResponse getByUserId(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    // Pagination Code
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public UserResponse createUser(@Valid UserRequest request) {
        User user = userMapper.toEntity(request);
        User saved = repository.save(user);
        return userMapper.toResponse(saved);
    }

}
