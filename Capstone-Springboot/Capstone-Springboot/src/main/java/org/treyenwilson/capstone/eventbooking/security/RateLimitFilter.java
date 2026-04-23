package org.treyenwilson.capstone.eventbooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitFilter extends OncePerRequestFilter implements Ordered {

    @Value("${rate.limit.auth.requests:10}")
    private int authRequestsPerMinute;

    // Increased from 100 to 600 to accommodate admin calendar UI usage
    // Admin calendar browsing triggers multiple rapid GET requests for events, venues, tickets, etc.
    @Value("${rate.limit.api.requests:600}")
    private int apiRequestsPerMinute;

    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private Instant windowStart = Instant.now();

        public boolean incrementAndCheck(int limit) {
            synchronized (this) {
                Instant now = Instant.now();
                if (Duration.between(windowStart, now).toMinutes() >= 1) {
                    // Reset window
                    count.set(0);
                    windowStart = now;
                }
                return count.incrementAndGet() <= limit;
            }
        }
    }

    private final Map<String, RequestCounter> authRequestCounters = new ConcurrentHashMap<>();
    private final Map<String, RequestCounter> apiRequestCounters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        // Check if this is an auth endpoint
        if (isAuthEndpoint(path)) {
            RequestCounter counter = authRequestCounters.computeIfAbsent(clientIp, k -> new RequestCounter());
            if (!counter.incrementAndCheck(authRequestsPerMinute)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many authentication requests. Please try again later.");
                return;
            }
        } else if (path.startsWith("/api/") || path.startsWith("/auth/")) {
            // Apply general API rate limiting
            RequestCounter counter = apiRequestCounters.computeIfAbsent(clientIp, k -> new RequestCounter());
            if (!counter.incrementAndCheck(apiRequestsPerMinute)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(String path) {
        return path.equals("/auth/login") || 
               path.equals("/api/auth/login") || 
               path.equals("/api/auth/register");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}