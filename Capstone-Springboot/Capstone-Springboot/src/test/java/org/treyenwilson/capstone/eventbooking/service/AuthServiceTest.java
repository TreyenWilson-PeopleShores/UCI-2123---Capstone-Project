package org.treyenwilson.capstone.eventbooking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.mapper.UserMapper;
import org.treyenwilson.capstone.eventbooking.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserResponse testUserResponse;

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
    }

    @Test
    void authenticate_Success() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = authService.authenticate(username, password);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(userMapper).toResponse(testUser);
    }

    @Test
    void authenticate_WithWhitespace_TrimsInput() {
        // Arrange
        String username = "  testuser  ";
        String password = "  password123  ";
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = authService.authenticate(username, password);

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        // Arrange
        String username = "nonexistent";
        String password = "password123";
        
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticate_WrongPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        
        User userWithPassword = new User();
        userWithPassword.setId(1L);
        userWithPassword.setUsername("testuser");
        userWithPassword.setPassword("encodedPassword");
        userWithPassword.setRole("USER");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userWithPassword));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

    @Test
    void authenticate_EmptyUsername_ThrowsException() {
        // Arrange
        String username = "";
        String password = "password123";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticate_EmptyPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticate_NullUsername_ThrowsException() {
        // Arrange
        String username = null;
        String password = "password123";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticate_NullPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = null;

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.authenticate(username, password));
        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void authenticate_LegacyPassword_UpdatesToEncoded() {
        // Arrange
        String username = "testuser";
        String password = "plainPassword";
        User legacyUser = new User();
        legacyUser.setId(1L);
        legacyUser.setUsername("testuser");
        legacyUser.setPassword("plainPassword"); // Plain text password
        legacyUser.setRole("USER");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(legacyUser));
        when(passwordEncoder.matches("plainPassword", "plainPassword")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toResponse(legacyUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = authService.authenticate(username, password);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(legacyUser);
        verify(passwordEncoder).encode("plainPassword");
        assertEquals("encodedPassword", legacyUser.getPassword());
    }

    @Test
    void register_Success() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String role = "USER";
        
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = authService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("USER", result.getRole());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithWhitespace_TrimsInput() {
        // Arrange
        String username = "  newuser  ";
        String password = "  password123  ";
        String role = "  USER  ";
        
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = authService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("USER", result.getRole());
    }

    @Test
    void register_WithRolePrefix_NormalizesRole() {
        // Arrange
        String username = "adminuser";
        String password = "password123";
        String role = "ROLE_ADMIN";
        
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = authService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
    }

    @Test
    void register_EmptyRole_DefaultsToUser() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String role = "";
        
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = authService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals("USER", result.getRole());
    }

    @Test
    void register_NullRole_DefaultsToUser() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String role = null;
        
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = authService.register(username, password, role);

        // Assert
        assertNotNull(result);
        assertEquals("USER", result.getRole());
    }

    @Test
    void register_EmptyUsername_ThrowsException() {
        // Arrange
        String username = "";
        String password = "password123";
        String role = "USER";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(username, password, role));
        assertEquals("Username and password are required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmptyPassword_ThrowsException() {
        // Arrange
        String username = "newuser";
        String password = "";
        String role = "USER";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(username, password, role));
        assertEquals("Username and password are required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_NullUsername_ThrowsException() {
        // Arrange
        String username = null;
        String password = "password123";
        String role = "USER";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(username, password, role));
        assertEquals("Username and password are required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_NullPassword_ThrowsException() {
        // Arrange
        String username = "newuser";
        String password = null;
        String role = "USER";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(username, password, role));
        assertEquals("Username and password are required", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}