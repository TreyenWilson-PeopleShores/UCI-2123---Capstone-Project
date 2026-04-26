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
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitFilter rateLimitFilter;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        rateLimitFilter = new RateLimitFilter();
        // Set rate limits via reflection
        ReflectionTestUtils.setField(rateLimitFilter, "authRequestsPerMinute", 2);
        ReflectionTestUtils.setField(rateLimitFilter, "apiRequestsPerMinute", 5);
        
        responseWriter = new StringWriter();
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));    }

    @Test
    void testAuthEndpointWithinLimit() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        
        // First request
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        
        // Second request (still within limit)
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(2)).doFilter(request, response);
    }

    @Test
    void testAuthEndpointExceedsLimit() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        
        // Make 3 requests (limit is 2)
        for (int i = 0; i < 3; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }
        
        // Third request should be rate limited
        verify(filterChain, times(2)).doFilter(request, response); // Only first 2 pass
        verify(response).setStatus(429); // TOO_MANY_REQUESTS
    }

    @Test
    void testApiEndpointWithinLimit() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/events");
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");
        
        // Make 5 requests (limit is 5)
        for (int i = 0; i < 5; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }
        
        verify(filterChain, times(5)).doFilter(request, response);
    }

    @Test
    void testApiEndpointExceedsLimit() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/events");
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");
        
        // Make 6 requests (limit is 5)
        for (int i = 0; i < 6; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }
        
        // Sixth request should be rate limited
        verify(filterChain, times(5)).doFilter(request, response); // Only first 5 pass
        verify(response).setStatus(429); // TOO_MANY_REQUESTS
    }

    @Test
    void testNonApiEndpointNoRateLimit() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/public/page");
        when(request.getRemoteAddr()).thenReturn("192.168.1.3");
        
        // Make many requests to non-API endpoint
        for (int i = 0; i < 10; i++) {
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }
        
        // All should pass (no rate limiting for non-API endpoints)
        verify(filterChain, times(10)).doFilter(request, response);
    }

    @Test
    void testDifferentIpsSeparateCounters() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/auth/login");
        
        // IP 1 makes 2 requests (within limit)
        when(request.getRemoteAddr()).thenReturn("192.168.1.10");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        
        // IP 2 makes 2 requests (within limit, separate counter)
        when(request.getRemoteAddr()).thenReturn("192.168.1.20");
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        
        // All 4 requests should pass
        verify(filterChain, times(4)).doFilter(request, response);
    }

    @Test
    void testGetClientIpWithXForwardedFor() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2");
        // getRemoteAddr() is not called when X-Forwarded-For is present, so no stub needed
        
        rateLimitFilter.doFilterInternal(request, response, filterChain);
        
        // Should use first IP from X-Forwarded-For header
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testIsAuthEndpoint() {
        RateLimitFilter filter = new RateLimitFilter();
        
        // Test various auth endpoints
        assert (Boolean) ReflectionTestUtils.invokeMethod(filter, "isAuthEndpoint", "/auth/login") == true;
        assert (Boolean) ReflectionTestUtils.invokeMethod(filter, "isAuthEndpoint", "/api/auth/login") == true;
        assert (Boolean) ReflectionTestUtils.invokeMethod(filter, "isAuthEndpoint", "/api/auth/register") == true;
        
        // Test non-auth endpoints
        assert (Boolean) ReflectionTestUtils.invokeMethod(filter, "isAuthEndpoint", "/api/events") == false;
        assert (Boolean) ReflectionTestUtils.invokeMethod(filter, "isAuthEndpoint", "/auth/logout") == false;
    }

    @Test
    void testGetOrder() {
        RateLimitFilter filter = new RateLimitFilter();
        assert filter.getOrder() == 0;
    }
}