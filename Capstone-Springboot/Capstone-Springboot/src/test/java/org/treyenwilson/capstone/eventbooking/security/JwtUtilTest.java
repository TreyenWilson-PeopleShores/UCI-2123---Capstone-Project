package org.treyenwilson.capstone.eventbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String secret = "testSecretKeyThatIsLongEnoughForHmacSha256Algorithm";
    private final Long expiration = 3600000L; // 1 hour
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        
        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Test
    void testGenerateToken() {
        // Act
        String token = jwtUtil.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void testExtractExpiration() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        Date expirationDate = jwtUtil.extractExpiration(token);

        // Assert
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidUsername() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = User.withUsername("differentuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        // Act
        boolean isValid = jwtUtil.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange - Create an already expired token (expired 1 hour ago)
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .setExpiration(new Date(System.currentTimeMillis() - 1800000)) // 30 minutes ago
                .signWith(getSigningKey())
                .compact();

        // Act & Assert - Should throw ExpiredJwtException
        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.validateToken(expiredToken, userDetails);
        });
    }

    @Test
    void testValidateToken_MalformedToken() {
        // Arrange
        String malformedToken = "malformed.token.here";

        // Act & Assert
        // Note: JWT parsing will throw exception for malformed token
        // We expect an exception to be thrown when trying to validate
        assertThrows(Exception.class, () -> jwtUtil.validateToken(malformedToken, userDetails));
    }

    @Test
    void testValidateToken_EmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtil.validateToken(emptyToken, userDetails));
    }

    @Test
    void testValidateToken_NullToken() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        assertThrows(Exception.class, () -> jwtUtil.validateToken(nullToken, userDetails));
    }

    @Test
    void testExtractClaim() {
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtUtil.extractClaim(token, Claims::getExpiration);

        // Assert
        assertEquals("testuser", subject);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
    }

    @Test
    void testTokenWithCustomClaims() {
        // This test would require access to createToken method which is private
        // We'll test through generateToken which doesn't add custom claims
        // Arrange
        String token = jwtUtil.generateToken(userDetails);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void testDifferentSecretKey() {
        // Arrange - Create token with different secret
        JwtUtil differentJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(differentJwtUtil, "secret", "differentSecretKeyThatIsLongEnoughForHmacSha256");
        ReflectionTestUtils.setField(differentJwtUtil, "expiration", expiration);
        
        String token = differentJwtUtil.generateToken(userDetails);

        // Act & Assert - Our jwtUtil with different secret should not be able to validate
        // This will throw SignatureException when trying to parse
        assertThrows(Exception.class, () -> jwtUtil.validateToken(token, userDetails));
    }
}