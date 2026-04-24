package org.treyenwilson.capstone.eventbooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidTokenSetsAuthentication() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        
        // Verify authentication was set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        assert authentication.getPrincipal().equals(userDetails);
    }

    @Test
    void testNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        
        // No authentication should be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication == null;
    }

    @Test
    void testInvalidAuthorizationHeaderFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        
        // No authentication should be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication == null;
    }

    @Test
    void testInvalidTokenExtraction() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService, never()).loadUserByUsername(any());
        
        // No authentication should be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication == null;
    }

    @Test
    void testAuthenticationAlreadySet() throws ServletException, IOException {
        // Set up existing authentication
        UserDetails existingUser = new User("existinguser", "password", Collections.emptyList());
        Authentication existingAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            existingUser, null, existingUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);
        
        String token = "valid.jwt.token";
        String username = "testuser";
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(jwtUtil, never()).validateToken(any(), any());
        
        // Existing authentication should remain unchanged
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication.equals(existingAuth);
    }

    @Test
    void testInvalidTokenValidation() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(false);
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateToken(token, userDetails);
        
        // No authentication should be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication == null;
    }

    @Test
    void testEmptyBearerToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        // When token is empty string, extractUsername will be called with ""
        when(jwtUtil.extractUsername("")).thenThrow(new RuntimeException("Invalid token"));
        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUsername("");
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(jwtUtil, never()).validateToken(any(), any());
        
        // No authentication should be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication == null;
    }

    @Test
    void testGetOrder() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        assert filter.getOrder() == 1;
    }
}