package org.treyenwilson.capstone.eventbooking.mapper;

import org.junit.jupiter.api.Test;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void testToResponse() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setRole("USER");

        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertNull(response.getPassword()); // Password should be null for security
        assertEquals("USER", response.getRole());
    }

    @Test
    void testToEntity() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("plainpassword");
        request.setRole("ADMIN");

        // Act
        User user = userMapper.toEntity(request);

        // Assert
        assertNotNull(user);
        assertNull(user.getId()); // ID should not be set from request
        assertEquals("newuser", user.getUsername());
        assertEquals("plainpassword", user.getPassword());
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testToEntityWithNullRequest() {
        // Arrange
        UserRequest request = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userMapper.toEntity(request));
    }

    @Test
    void testToResponseWithNullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userMapper.toResponse(user));
    }
}