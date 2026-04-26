package org.treyenwilson.capstone.eventbooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.treyenwilson.capstone.eventbooking.dto.UserRequest;
import org.treyenwilson.capstone.eventbooking.dto.UserResponse;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.service.UserService;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private UserService userService;

    @Test
    public void testGetUserById_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(userId);
        mockResponse.setUsername("testuser");
        mockResponse.setPassword("password123");
        mockResponse.setRole("USER");

        when(userService.getByUserId(userId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/id/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    public void testGetUserById_NotFound() throws Exception {
        // Arrange
        Long userId = 999L;
        when(userService.getByUserId(userId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/users/id/{id}", userId))
                .andExpect(status().isOk()); // Controller returns 200 even if null
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setPassword("newpassword123");
        request.setRole("USER");

        UserResponse mockResponse = new UserResponse();
        mockResponse.setId(1L);
        mockResponse.setUsername("newuser");
        mockResponse.setPassword("newpassword123");
        mockResponse.setRole("USER");

        when(userService.createUser(any(UserRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    public void testCreateUser_ValidationFailure() throws Exception {
        // Arrange - Create invalid request (missing required fields)
        UserRequest request = new UserRequest();
        request.setUsername(""); // Empty username
        request.setPassword("123"); // Too short password
        request.setRole(""); // Empty role

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "id")
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.content[0].role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_InvalidSortBy_FallsBackToId() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10, Sort.by("id").ascending()), 1);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert - Test with invalid sortBy field
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "invalid_field") // Should fall back to "id"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_ValidSortByLowerCase_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10, Sort.by("username").ascending()), 1);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert - Test with lowercase field name
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "username") // Should be accepted
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_ValidSortByUpperCase_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("USER");

        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10, Sort.by("role").ascending()), 1);
        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        // Act & Assert - Test with uppercase field name (should be lowercased)
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "ROLE") // Should be lowercased to "role"
                        .param("ascending", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }
}