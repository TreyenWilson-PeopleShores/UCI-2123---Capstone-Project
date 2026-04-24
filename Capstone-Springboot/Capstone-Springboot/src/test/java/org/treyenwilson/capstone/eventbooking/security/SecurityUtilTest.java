package org.treyenwilson.capstone.eventbooking.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.treyenwilson.capstone.eventbooking.entity.User;
import org.treyenwilson.capstone.eventbooking.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityUtilTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityUtil securityUtil;

    @Test
    void testGetCurrentUserId_AuthenticatedUser() {
        // Arrange
        String username = "testuser";
        Long userId = 1L;
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        User user = new User();
        user.setId(userId);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertEquals(userId, result);
    }

    @Test
    void testGetCurrentUserId_NoAuthentication() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCurrentUserId_NotAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCurrentUserId_PrincipalNotUserDetails() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn("notUserDetails");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertNull(result);
    }

    @Test
    void testGetCurrentUserId_UserNotFound() {
        // Arrange
        String username = "nonexistent";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Long result = securityUtil.getCurrentUserId();

        // Assert
        assertNull(result);
    }

    @Test
    void testIsCurrentUser_True() {
        // Arrange
        String username = "testuser";
        Long userId = 1L;
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        User user = new User();
        user.setId(userId);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        boolean result = securityUtil.isCurrentUser(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsCurrentUser_FalseDifferentId() {
        // Arrange
        String username = "testuser";
        Long currentUserId = 1L;
        Long otherUserId = 2L;
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        User user = new User();
        user.setId(currentUserId);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        boolean result = securityUtil.isCurrentUser(otherUserId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsCurrentUser_FalseNoCurrentUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isCurrentUser(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAdmin_True() {
        // Arrange
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Collection<GrantedAuthority> authorities = List.of(adminAuthority);
        
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAdmin_FalseNoAdminRole() {
        // Arrange
        GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        Collection<GrantedAuthority> authorities = List.of(userAuthority);
        
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAdmin_FalseNoAuthentication() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAdmin_FalseNotAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsAdmin_MultipleAuthorities() {
        // Arrange
        GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        Collection<GrantedAuthority> authorities = List.of(userAuthority, adminAuthority);
        
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsAdmin_EmptyAuthorities() {
        // Arrange
        Collection<GrantedAuthority> authorities = List.of();
        
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = securityUtil.isAdmin();

        // Assert
        assertFalse(result);
    }
}