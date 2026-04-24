package org.treyenwilson.capstone.eventbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.exception.ResourceNotFoundException;
import org.treyenwilson.capstone.eventbooking.mapper.UserMapper;
import org.treyenwilson.capstone.eventbooking.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserResponse testUserResponse;
    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");

        testUserResponse = new UserResponse();
        testUserResponse.setId(1L);
        testUserResponse.setUsername("testuser");
        testUserResponse.setRole("USER");

        testUserRequest = new UserRequest();
        testUserRequest.setUsername("newuser");
        testUserRequest.setPassword("password123");
        testUserRequest.setRole("USER");
    }

    @Test
    void getByUserId_Success() {
        // Arrange
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.getByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());
        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void getByUserId_UserNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        Long userId = 999L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> userService.getByUserId(userId));
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void findAll_ReturnsPageOfUsers() {
        // Arrange
        Pageable pageable = Pageable.ofSize(10);
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<User> result = userService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(users, result.getContent());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void createUser_Success() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole("USER");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(testUserRequest)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.createUser(testUserRequest);

        // Assert
        assertNotNull(result);
        assertEquals("encodedPassword", newUser.getPassword());
        assertEquals("USER", newUser.getRole());
        verify(userMapper).toEntity(testUserRequest);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(newUser);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void createUser_WithRolePrefix_NormalizesRole() {
        // Arrange
        UserRequest requestWithRolePrefix = new UserRequest();
        requestWithRolePrefix.setUsername("adminuser");
        requestWithRolePrefix.setPassword("password123");
        requestWithRolePrefix.setRole("ROLE_ADMIN");
        
        User newUser = new User();
        newUser.setUsername("adminuser");
        newUser.setPassword("password123");
        newUser.setRole("ROLE_ADMIN");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("adminuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("ADMIN");
        
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("adminuser");
        expectedResponse.setRole("ADMIN");
        
        when(userMapper.toEntity(requestWithRolePrefix)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.createUser(requestWithRolePrefix);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals("ADMIN", newUser.getRole()); // Should be normalized
    }

    @Test
    void createUser_EmptyRole_DefaultsToUser() {
        // Arrange
        UserRequest requestWithEmptyRole = new UserRequest();
        requestWithEmptyRole.setUsername("newuser");
        requestWithEmptyRole.setPassword("password123");
        requestWithEmptyRole.setRole("");
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole("");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(requestWithEmptyRole)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.createUser(requestWithEmptyRole);

        // Assert
        assertNotNull(result);
        assertEquals("USER", newUser.getRole()); // Should be normalized to USER
    }

    @Test
    void createUser_NullRole_DefaultsToUser() {
        // Arrange
        UserRequest requestWithNullRole = new UserRequest();
        requestWithNullRole.setUsername("newuser");
        requestWithNullRole.setPassword("password123");
        requestWithNullRole.setRole(null);
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole(null);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(requestWithNullRole)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.createUser(requestWithNullRole);

        // Assert
        assertNotNull(result);
        assertEquals("USER", newUser.getRole()); // Should be normalized to USER
    }

    @Test
    void createUser_WithWhitespaceRole_NormalizesRole() {
        // Arrange
        UserRequest requestWithWhitespaceRole = new UserRequest();
        requestWithWhitespaceRole.setUsername("newuser");
        requestWithWhitespaceRole.setPassword("password123");
        requestWithWhitespaceRole.setRole("  admin  ");
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole("  admin  ");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("ADMIN");
        
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("newuser");
        expectedResponse.setRole("ADMIN");
        
        when(userMapper.toEntity(requestWithWhitespaceRole)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.createUser(requestWithWhitespaceRole);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertEquals("ADMIN", newUser.getRole()); // Should be normalized
    }

    @Test
    void createUser_EncodesPassword() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plainPassword");
        newUser.setRole("USER");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(testUserRequest)).thenReturn(newUser);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        userService.createUser(testUserRequest);

        // Assert
        verify(passwordEncoder).encode("plainPassword");
        assertEquals("encodedPassword", newUser.getPassword());
    }

    @Test
    void normalizeRole_WithRolePrefix_RemovesPrefix() {
        // Arrange
        String roleWithPrefix = "ROLE_ADMIN";
        
        // Act - This is a private method, but we can test it through createUser
        UserRequest request = new UserRequest();
        request.setUsername("adminuser");
        request.setPassword("password123");
        request.setRole(roleWithPrefix);
        
        User newUser = new User();
        newUser.setUsername("adminuser");
        newUser.setPassword("password123");
        newUser.setRole(roleWithPrefix);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("adminuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("ADMIN");
        
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("adminuser");
        expectedResponse.setRole("ADMIN");
        
        when(userMapper.toEntity(request)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void normalizeRole_EmptyString_ReturnsUser() {
        // Arrange
        String emptyRole = "";
        
        // Act - This is a private method, but we can test it through createUser
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRole(emptyRole);
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole(emptyRole);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(request)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertEquals("USER", result.getRole());
    }

    @Test
    void normalizeRole_Null_ReturnsUser() {
        // Arrange
        // Act - This is a private method, but we can test it through createUser
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRole(null);
        
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setRole(null);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("USER");
        
        when(userMapper.toEntity(request)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertEquals("USER", result.getRole());
    }

    @Test
    void normalizeRole_Lowercase_ConvertsToUppercase() {
        // Arrange
        String lowercaseRole = "admin";
        
        // Act - This is a private method, but we can test it through createUser
        UserRequest request = new UserRequest();
        request.setUsername("adminuser");
        request.setPassword("password123");
        request.setRole(lowercaseRole);
        
        User newUser = new User();
        newUser.setUsername("adminuser");
        newUser.setPassword("password123");
        newUser.setRole(lowercaseRole);
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("adminuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("ADMIN");
        
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);
        expectedResponse.setUsername("adminuser");
        expectedResponse.setRole("ADMIN");
        
        when(userMapper.toEntity(request)).thenReturn(newUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.createUser(request);

        // Assert
        assertEquals("ADMIN", result.getRole());
    }
}