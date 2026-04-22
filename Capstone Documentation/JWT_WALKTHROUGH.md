# Spring Security JWT + React Login — Step-by-Step Walkthrough

A step-by-step guide to building a JWT-authenticated Spring Boot API with a React frontend. Covers login, registration, role-based access control, and user-level resource ownership checks.

> **Who this is for:** Junior developers who have basic Java and React knowledge but haven't worked with Spring Security or JWTs before. Every step tells you exactly what file to create or edit, and where it goes.

---

## Table of Contents

1. [Project Setup](#1-project-setup)
2. [Configure the Database Connection](#2-configure-the-database-connection)
3. [Create the Role Enum](#3-create-the-role-enum)
4. [Create the User Entity](#4-create-the-user-entity)
5. [Create the User Repository](#5-create-the-user-repository)
6. [Create the UserDetailsService](#6-create-the-userdetailsservice)
7. [Create the JWT Utility Class](#7-create-the-jwt-utility-class)
8. [Create the Auth DTOs](#8-create-the-auth-dtos)
9. [Create the Auth Controller (Login & Register)](#9-create-the-auth-controller-login--register)
10. [Create the JWT Request Filter](#10-create-the-jwt-request-filter)
11. [Create the Security Configuration](#11-create-the-security-configuration)
12. [Role-Based Endpoint Filtering](#12-role-based-endpoint-filtering)
13. [User ID Matching (Resource Ownership)](#13-user-id-matching-resource-ownership)
14. [Add Refresh Token Support](#14-add-refresh-token-support)
15. [Set Up the React Frontend](#15-set-up-the-react-frontend)
16. [Create the Axios API Client](#16-create-the-axios-api-client)
17. [Create the Auth Context](#17-create-the-auth-context)
18. [Create the Login Page](#18-create-the-login-page)
19. [Create a Protected Dashboard Page](#19-create-a-protected-dashboard-page)
20. [Create Protected Route Components](#20-create-protected-route-components)
21. [Wire Up the React Router](#21-wire-up-the-react-router)
22. [Testing with cURL](#22-testing-with-curl)
23. [Common Pitfalls](#23-common-pitfalls)
24. [Security Annotation Cheat Sheet](#24-security-annotation-cheat-sheet)

---

## 1. Project Setup

### Step 1a: Generate the Spring Boot Backend

1. Go to [start.spring.io](https://start.spring.io)
2. Fill in the form exactly like this:
   - **Project:** Maven
   - **Language:** Java
   - **Spring Boot:** 3.4.x (latest 3.x)
   - **Group:** `com.example`
   - **Artifact:** `jwt-demo`
   - **Packaging:** Jar
   - **Java:** 17
3. Under **Dependencies**, click "Add Dependencies" and add these five:
   - `Spring Web`
   - `Spring Security`
   - `Spring Data JPA`
   - `MySQL Driver`
   - `Lombok`
4. Click **Generate**. This downloads a `.zip` file.
5. Unzip it into a folder called `backend`.

Your folder structure should now look like:

```
backend/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/example/jwtdemo/
        │       └── JwtDemoApplication.java
        └── resources/
            └── application.properties
```

### Step 1b: Add the JWT Library to `pom.xml`

Open the file `backend/pom.xml`.

Find the `<dependencies>` section. Add these three dependencies **inside** the `<dependencies>` tag, right before the closing `</dependencies>`:

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

Save the file. If your IDE asks to reload/sync Maven, click **Yes**.

### Step 1c: Create the React Frontend

Open a terminal, `cd` into your project root (the folder that contains `backend/`), and run:

```bash
npx create-react-app frontend
cd frontend
npm install axios react-router-dom jwt-decode
```

This creates a `frontend/` folder next to `backend/`.

---

## 2. Configure the Database Connection

You should already have MySQL installed and running on your machine. If not, install it first.

**Open the file** `backend/src/main/resources/application.properties`. It may already exist with some content, or it may be empty. **Replace its entire contents** with:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_demo?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

jwt.secret=dGhpcyBpcyBhIHZlcnkgc2VjcmV0IGtleSBmb3Igand0IGRlbW8gYXBw

server.port=8080
```

> **Important:** Change `spring.datasource.username` and `spring.datasource.password` to match **your** MySQL credentials if they are different from `root`/`root`.

> **What is `jwt.secret`?** This is a Base64-encoded key used to sign your JWTs. The one above is just for development. In production, generate a real one with `openssl rand -base64 32` and keep it in an environment variable.

> **Note:** If you see a file called `application.yml` in that same folder, delete it. We are using `application.properties` instead.

---

## 3. Create the Role Enum

We need a Role enum that defines what roles a user can have. We'll start with two: `ROLE_USER` and `ROLE_ADMIN`.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/entity/Role.java
```

> If the `entity` folder doesn't exist yet inside `com/example/jwtdemo/`, right-click on the `jwtdemo` folder and create a new folder called `entity`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.entity;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
```

> **Why the `ROLE_` prefix?** Spring Security expects role names to start with `ROLE_`. When you write `hasRole("ADMIN")` in a security annotation, Spring automatically checks for the authority `ROLE_ADMIN`. If you forget this prefix, your role checks will silently fail and you'll spend hours debugging.

---

## 4. Create the User Entity

This is the Java class that maps to your `users` table in MySQL. It also implements Spring Security's `UserDetails` interface, which is how Spring Security knows about your users.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/entity/User.java
```

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // --- UserDetails implementation ---
    // Spring Security calls these methods to check your user's status.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converts our Role enum into a Spring Security authority
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

> **What do all those annotations mean?**
> - `@Entity` — tells JPA this class maps to a database table
> - `@Table(name = "users")` — the table in MySQL will be called `users`
> - `@Data` — Lombok generates getters, setters, `toString`, `equals`, `hashCode` for you automatically
> - `@NoArgsConstructor` / `@AllArgsConstructor` — Lombok generates a no-argument constructor and an all-arguments constructor
> - `@Builder` — lets you create users with `User.builder().username("alice").build()`
> - `@Id` + `@GeneratedValue` — the `id` column is an auto-incrementing primary key
> - `@Enumerated(EnumType.STRING)` — stores the role as the string `"ROLE_USER"` in the database, not a number

---

## 5. Create the User Repository

A "repository" is how Spring Data JPA lets you query the database without writing SQL.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/repository/UserRepository.java
```

> If the `repository` folder doesn't exist, create it inside `com/example/jwtdemo/`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.repository;

import com.example.jwtdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

> **What does this do?** `JpaRepository<User, Long>` gives you methods like `findAll()`, `findById()`, `save()`, `deleteById()` for free — no SQL needed. The `findByUsername` method is a custom query — Spring Data JPA automatically generates the SQL `SELECT * FROM users WHERE username = ?` just from the method name.

---

## 6. Create the UserDetailsService

Spring Security needs a way to load a user from your database. You provide this by creating a class that implements `UserDetailsService`.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/service/CustomUserDetailsService.java
```

> If the `service` folder doesn't exist, create it inside `com/example/jwtdemo/`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.service;

import com.example.jwtdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

> **How does this work?** When Spring Security needs to authenticate someone, it calls `loadUserByUsername()`. This method looks up the user in the database by username. If found, it returns the `User` object (which implements `UserDetails`). If not found, it throws an exception and authentication fails.

---

## 7. Create the JWT Utility Class

This class handles everything JWT-related: creating tokens, reading data from tokens, and validating tokens.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/security/JwtUtil.java
```

> If the `security` folder doesn't exist, create it inside `com/example/jwtdemo/`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.example.jwtdemo.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // This reads the jwt.secret value from application.properties
    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;       // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    // --- Token Generation ---

    public String generateAccessToken(User user) {
        return buildToken(user, ACCESS_TOKEN_EXPIRATION, "access");
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, REFRESH_TOKEN_EXPIRATION, "refresh");
    }

    private String buildToken(User user, long expiration, String type) {
        return Jwts.builder()
                .subject(user.getUsername())          // who the token is for
                .claim("userId", user.getId())        // custom claim: user's database id
                .claim("role", user.getRole().name()) // custom claim: user's role
                .claim("type", type)                  // "access" or "refresh"
                .issuedAt(new Date())                 // when the token was created
                .expiration(new Date(System.currentTimeMillis() + expiration)) // when it expires
                .signWith(getSigningKey())            // sign it with our secret key
                .compact();                           // build the final token string
    }

    // --- Reading Data from a Token ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    // --- Validation ---

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // --- Internal Helpers ---

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

> **What is a JWT?** A JSON Web Token is a long encoded string like `eyJhbGciOi...`. It has three parts separated by dots: a header, a payload (with your claims like userId and role), and a signature. The server signs the token with a secret key. When the token comes back on a later request, the server verifies the signature hasn't been tampered with — without needing to store anything in a database or session.

---

## 8. Create the Auth DTOs

DTOs (Data Transfer Objects) define the shape of the JSON that gets sent to and from your API. We need four: one for login, one for register, one for the response, and one for refresh.

**Create four new files** inside:

```
backend/src/main/java/com/example/jwtdemo/dto/
```

> If the `dto` folder doesn't exist, create it inside `com/example/jwtdemo/`.

---

**File 1 — Create** `dto/AuthRequest.java`:

```java
package com.example.jwtdemo.dto;

public record AuthRequest(String username, String password) {}
```

---

**File 2 — Create** `dto/AuthResponse.java`:

```java
package com.example.jwtdemo.dto;

public record AuthResponse(String accessToken, String refreshToken) {}
```

---

**File 3 — Create** `dto/RegisterRequest.java`:

```java
package com.example.jwtdemo.dto;

public record RegisterRequest(String username, String password) {}
```

---

**File 4 — Create** `dto/RefreshRequest.java`:

```java
package com.example.jwtdemo.dto;

public record RefreshRequest(String refreshToken) {}
```

> **What is a `record`?** Java records (Java 16+) are a short way to make a class that just holds data. `record AuthRequest(String username, String password)` automatically creates a class with a constructor, getters (`username()` and `password()`), `equals`, `hashCode`, and `toString`. No boilerplate needed.

---

## 9. Create the Auth Controller (Login & Register)

This controller handles the **public** endpoints: login, register, and token refresh. No JWT is needed to call these.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/controller/AuthController.java
```

> If the `controller` folder doesn't exist, create it inside `com/example/jwtdemo/`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.controller;

import com.example.jwtdemo.dto.*;
import com.example.jwtdemo.entity.User;
import com.example.jwtdemo.entity.Role;
import com.example.jwtdemo.repository.UserRepository;
import com.example.jwtdemo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Step 1: Spring Security checks the username + password against the database.
        // If they're wrong, this line throws an exception and returns 401 automatically.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Step 2: If we get here, the credentials are valid. Look up the user.
        User user = userRepository.findByUsername(request.username()).orElseThrow();

        // Step 3: Generate tokens and send them back.
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Check if username is already taken
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Create the new user with an encrypted password
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER) // new users always start as ROLE_USER
                .build();

        userRepository.save(user);

        // Generate tokens so the user is auto-logged-in after registration
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            String token = request.refreshToken();

            // Make sure this is actually a refresh token, not an access token
            if (!"refresh".equals(jwtUtil.extractType(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findByUsername(username).orElseThrow();

            if (!jwtUtil.isTokenValid(token, user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Issue a brand new pair of tokens
            String newAccess = jwtUtil.generateAccessToken(user);
            String newRefresh = jwtUtil.generateRefreshToken(user);

            return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
```

> **What's happening when a user logs in?**
> 1. The frontend sends `{ "username": "alice", "password": "secret123" }` as JSON.
> 2. `authenticationManager.authenticate(...)` uses your `CustomUserDetailsService` to load the user from the DB, then compares the passwords using BCrypt.
> 3. If the password matches, we generate two JWTs (access + refresh) and send them back as JSON.
> 4. The frontend saves these tokens and sends the access token on every future request.

---

## 10. Create the JWT Request Filter

This is the most important piece of the puzzle. This filter runs **before every single HTTP request** that hits your API. It checks if the request has a valid JWT in the `Authorization` header, and if so, tells Spring Security who the user is.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/security/JwtAuthenticationFilter.java
```

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.security;

import com.example.jwtdemo.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request.
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no header, or it doesn't start with "Bearer ", skip this filter.
        // Public endpoints (like /api/auth/login) will work fine without a token.
        // Protected endpoints will get a 401 from Spring Security automatically.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token (everything after "Bearer ").
        final String jwt = authHeader.substring(7);

        try {
            // Step 4: Read the username from the token.
            final String username = jwtUtil.extractUsername(jwt);

            // Step 5: If we got a username and there's no authentication set yet...
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load the full user from the database.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Step 7: Validate the token (correct user + not expired).
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // Step 8: Tell Spring Security "this user is authenticated for this request".
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,                  // the principal (our User object)
                                    null,                         // credentials (not needed, already validated)
                                    userDetails.getAuthorities()  // the user's roles
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            // Token is invalid or expired — don't set any authentication.
            // The request continues as anonymous. Spring Security will
            // return 401/403 for protected endpoints automatically.
        }

        // Step 9: Continue to the next filter or the controller.
        filterChain.doFilter(request, response);
    }
}
```

> **The key idea:** Every request passes through this filter. If you send a valid Bearer token, Spring Security knows who you are for the rest of that request. If you don't send a token, the request is anonymous. Protected endpoints will reject anonymous requests automatically.

---

## 11. Create the Security Configuration

This class ties everything together. It tells Spring Security:
- Which endpoints are public (no login needed)
- Which endpoints require a certain role
- To use our JWT filter instead of the default session-based login
- How to handle CORS (so React on `localhost:3000` can talk to the API on `localhost:8080`)

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/config/SecurityConfig.java
```

> If the `config` folder doesn't exist, create it inside `com/example/jwtdemo/`.

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.config;

import com.example.jwtdemo.security.JwtAuthenticationFilter;
import com.example.jwtdemo.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // <-- THIS LINE enables @PreAuthorize annotations on controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF because we're using stateless JWT tokens, not cookies
            .csrf(csrf -> csrf.disable())

            // Enable CORS so React on port 3000 can call the API on port 8080
            .cors(Customizer.withDefaults())

            // Don't create HTTP sessions — every request must carry its own JWT
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define which URLs require what level of access
            .authorizeHttpRequests(auth -> auth
                // Anyone can call /api/auth/** (login, register, refresh) without a token
                .requestMatchers("/api/auth/**").permitAll()

                // Only ADMIN users can call /api/admin/**
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // All other /api/** endpoints require the user to be logged in (any role)
                .requestMatchers("/api/**").authenticated()

                // Anything else (like static files) is open
                .anyRequest().permitAll()
            )

            // Use our custom auth provider (which uses our UserDetailsService + BCrypt)
            .authenticationProvider(authenticationProvider())

            // Add our JWT filter BEFORE Spring Security's default authentication filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // React dev server
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

> **Order matters in `authorizeHttpRequests`!** Spring checks the rules top to bottom and uses the **first match**. If you put `.requestMatchers("/api/**").authenticated()` above `.requestMatchers("/api/auth/**").permitAll()`, then the login endpoint would require authentication — which makes no sense. Always put the most specific rules first.

### Test it!

At this point, your backend should be able to start. Open a terminal in the `backend/` folder and run:

```bash
mvn spring-boot:run
```

If it starts without errors, you're on track. Stop it with `Ctrl+C` after confirming.

---

## 12. Role-Based Endpoint Filtering

There are three ways to restrict endpoints by role. You can use one, two, or all three in the same project.

### Approach A: URL-pattern based (already done in SecurityConfig)

You already set this up in Step 11:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

This means **every** endpoint under `/api/admin/` requires the `ROLE_ADMIN` authority. You don't need to add annotations to individual controllers for this.

You could add more patterns:

```java
.requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
```

### Approach B: `@PreAuthorize` on individual controller methods

This gives you per-method control. Let's create a controller that uses this.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/controller/UserController.java
```

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.controller;

import com.example.jwtdemo.entity.User;
import com.example.jwtdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Only ADMIN can list all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Any authenticated user can get a user by ID
    // (We'll tighten this with ownership checks in the next section)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    // Only ADMIN can delete a user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

> **Important:** `@PreAuthorize` only works because we added `@EnableMethodSecurity` to the `SecurityConfig` class in Step 11. Without that annotation, `@PreAuthorize` is silently ignored — your endpoints would be open to everyone.

### Approach C: Complex SpEL expressions

You can write more complex conditions using Spring Expression Language (SpEL):

```java
// Admin OR the manager of the department
@PreAuthorize("hasRole('ADMIN') or @departmentService.isManager(#deptId, authentication.name)")
@GetMapping("/departments/{deptId}/reports")
public ResponseEntity<List<Report>> getDeptReports(@PathVariable Long deptId) { ... }
```

This calls a method on a Spring bean called `departmentService` and passes in the department ID and the logged-in user's username. We'll see a concrete example of this pattern in the next section.

---

## 13. User ID Matching (Resource Ownership)

This is the pattern where a user can only access **their own** resources. For example: a user can view their own profile but not someone else's. An admin can view anyone's.

There are three ways to do this. Pick the one that fits your situation.

### Option 1: Inline `@PreAuthorize` with principal comparison

**Open the file** `backend/src/main/java/com/example/jwtdemo/controller/UserController.java`.

Find this method:

```java
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
```

**Replace** the `@PreAuthorize` annotation so the method looks like this:

```java
    // Owner or admin can view a profile
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }
```

> **How does `#id == authentication.principal.id` work?**
> - `#id` refers to the `@PathVariable Long id` method parameter (the `/users/5` part of the URL)
> - `authentication.principal` is the `User` object that our JWT filter put into the security context
> - `.id` accesses the user's database ID (from our `User` entity)
> - So this checks: "Is the user requesting their own profile, OR are they an admin?"

### Option 2: Custom security expression via a service bean

For more complex ownership checks (like "does this user own this order?"), create a reusable authorization service.

**Create a new file** at this exact path:

```
backend/src/main/java/com/example/jwtdemo/security/AuthorizationService.java
```

Paste this entire contents into the file and save it:

```java
package com.example.jwtdemo.security;

import com.example.jwtdemo.entity.User;
import com.example.jwtdemo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("authz")   // <-- "authz" is the name you'll use in @PreAuthorize expressions
@RequiredArgsConstructor
public class AuthorizationService {

    private final OrderRepository orderRepository;

    /**
     * Returns true if the currently authenticated user owns the given resource.
     */
    public boolean isOwner(Long resourceUserId, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        return principal.getId().equals(resourceUserId);
    }

    /**
     * Returns true if the authenticated user owns the order.
     * Looks up the order in the database and compares its userId to the logged-in user's id.
     */
    public boolean ownsOrder(Long orderId, Authentication auth) {
        User principal = (User) auth.getPrincipal();
        return orderRepository.findById(orderId)
                .map(order -> order.getUserId().equals(principal.getId()))
                .orElse(false);
    }
}
```

> **Note:** This class references `OrderRepository`. If you want to use the `ownsOrder` method, you'll need an `Order` entity and `OrderRepository` — those aren't in this walkthrough but follow the same pattern as the `User` entity and `UserRepository`. The important thing is understanding the pattern: create a `@Service`, name it with a string (`"authz"`), and reference it in `@PreAuthorize` expressions.

Now you can use it in any controller like this:

```java
@PutMapping("/orders/{orderId}")
@PreAuthorize("hasRole('ADMIN') or @authz.ownsOrder(#orderId, authentication)")
public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody OrderDto dto) {
    // If we get here, Spring has already verified the user has access
    return ResponseEntity.ok(orderService.update(orderId, dto));
}
```

> **What does `@authz.ownsOrder(...)` mean?**
> - `@authz` tells Spring to look up the bean named `"authz"` (our `AuthorizationService`)
> - Then it calls the `ownsOrder` method on that bean
> - `#orderId` passes the `@PathVariable` value into the method
> - `authentication` passes the current user's authentication object

### Option 3: Programmatic check inside the method body

Sometimes the `@PreAuthorize` expression gets too complex and hard to read. In those cases, just do the check manually inside the method.

**Open the file** `backend/src/main/java/com/example/jwtdemo/controller/UserController.java`.

Add these imports at the top (if they aren't already there):

```java
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
```

Then add this new method to the class:

```java
    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(
            @PathVariable Long id,
            @RequestBody User request,
            @AuthenticationPrincipal User currentUser  // <-- Spring injects the logged-in user
    ) {
        // Manual ownership check
        if (!currentUser.getId().equals(id) && !currentUser.getRole().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // If we get here, the user has access
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(request.getUsername());
        return ResponseEntity.ok(userRepository.save(user));
    }
```

> **What is `@AuthenticationPrincipal`?** This annotation tells Spring to inject the currently logged-in `User` object directly into your method parameter. It's the same object the JWT filter put into the security context.

> **When to use which approach?**
>
> | Approach | Best for |
> |---|---|
> | **Option 1** — `@PreAuthorize` inline | Simple ownership checks: "is this the user's own resource?" |
> | **Option 2** — `@authz` bean method | Reusable checks shared across multiple controllers |
> | **Option 3** — manual `if` statement | Complex logic that doesn't fit in a one-line expression |

---

## 14. Add Refresh Token Support

We already built the `/api/auth/refresh` endpoint inside the `AuthController` in Step 9 — go look at it if you haven't reviewed it yet.

**How refresh tokens work — the full flow:**

1. The user logs in. The backend returns **two** tokens: an access token (expires in 15 minutes) and a refresh token (expires in 24 hours).
2. The frontend saves both tokens in `localStorage`.
3. On every API call, the frontend sends the **access token** in the `Authorization` header.
4. After 15 minutes, the access token expires. The next API call gets a `401` error.
5. The axios interceptor (which we'll set up in Step 16) catches the `401`, sends the **refresh token** to `/api/auth/refresh`, gets a new pair of tokens, saves them, and retries the original request — all automatically.
6. If the refresh token is also expired (after 24 hours), the user must log in again.

---

## 15. Set Up the React Frontend

If you already ran `npx create-react-app frontend` in Step 1c, skip this step.

Otherwise, open a terminal in your **project root** (the folder that contains `backend/`):

```bash
npx create-react-app frontend
cd frontend
npm install axios react-router-dom jwt-decode
```

Your project structure should now look like:

```
your-project/
├── backend/
│   ├── pom.xml
│   └── src/
└── frontend/
    ├── package.json
    ├── public/
    └── src/
        ├── App.js
        ├── index.js
        └── ...
```

---

## 16. Create the Axios API Client

Axios is the HTTP library we'll use to call the backend. We'll configure it with "interceptors" that:
- Automatically attach the JWT token to every outgoing request
- Automatically try to refresh the token if we get a `401` error back

**Create a new file** at this exact path:

```
frontend/src/api/axios.js
```

> Create the `api` folder inside `frontend/src/` if it doesn't exist.

Paste this entire contents into the file and save it:

```js
import axios from "axios";

// Create an axios instance pointed at our backend
const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

// REQUEST INTERCEPTOR
// Runs before every request. Attaches the access token if we have one.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// RESPONSE INTERCEPTOR
// Runs after every response. If we get a 401, try to refresh the token.
api.interceptors.response.use(
  // If the response is successful (2xx), just pass it through
  (response) => response,

  // If the response is an error...
  async (error) => {
    const originalRequest = error.config;

    // If it's a 401 AND we haven't already tried to refresh for this request
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // prevents infinite loop

      try {
        // Try to get new tokens using our refresh token
        const refreshToken = localStorage.getItem("refreshToken");
        const { data } = await axios.post(
          "http://localhost:8080/api/auth/refresh",
          { refreshToken }
        );

        // Save the new tokens
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);

        // Retry the original failed request with the new access token
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed too (refresh token is also expired) — force logout
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

> **Why not just use `axios` directly everywhere?** By creating a custom instance with `axios.create()`, all our API calls automatically get the token attached and the refresh logic. We just `import api from "../api/axios"` in our components instead of `import axios from "axios"`.

---

## 17. Create the Auth Context

React Context lets us share the logged-in user's data across all components without passing props everywhere.

**Create a new file** at this exact path:

```
frontend/src/context/AuthContext.js
```

> Create the `context` folder inside `frontend/src/` if it doesn't exist.

Paste this entire contents into the file and save it:

```jsx
import { createContext, useContext, useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);

  // When the app first loads, check if there's already a saved token
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        // Check if the token hasn't expired yet
        if (decoded.exp * 1000 > Date.now()) {
          setUser({
            username: decoded.sub,      // "sub" is the subject claim (username)
            userId: decoded.userId,     // our custom claim
            role: decoded.role,         // our custom claim
          });
        } else {
          // Token is expired — clean up
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
        }
      } catch {
        // Token is malformed — clean up
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
      }
    }
  }, []);

  // Call this after a successful login or register
  const login = (accessToken, refreshToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    const decoded = jwtDecode(accessToken);
    setUser({
      username: decoded.sub,
      userId: decoded.userId,
      role: decoded.role,
    });
  };

  // Call this when the user clicks logout
  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook — use this in any component to access auth data
// Example: const { user, login, logout } = useAuth();
export const useAuth = () => useContext(AuthContext);
```

> **What does `jwtDecode` do?** It reads the payload of a JWT token without needing the secret key. The payload contains the claims we put in (username, userId, role, expiration time). This is just **reading** — the backend still validates the signature on every request. The frontend only decodes the token to display user info.

---

## 18. Create the Login Page

**Create a new file** at this exact path:

```
frontend/src/pages/LoginPage.js
```

> Create the `pages` folder inside `frontend/src/` if it doesn't exist.

Paste this entire contents into the file and save it:

```jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api/axios";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault(); // prevent the browser from refreshing the page
    setError("");

    try {
      // Send login request to the backend
      const { data } = await api.post("/auth/login", { username, password });

      // Save tokens and update the auth context
      login(data.accessToken, data.refreshToken);

      // Redirect to the dashboard
      navigate("/dashboard");
    } catch (err) {
      setError("Invalid username or password");
    }
  };

  return (
    <div>
      <h2>Login</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "10px", maxWidth: "300px" }}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button type="submit">Login</button>
      </form>
    </div>
  );
}
```

---

## 19. Create a Protected Dashboard Page

This page calls a protected backend endpoint. The axios interceptor automatically attaches the JWT — you don't have to do anything special.

**Create a new file** at this exact path:

```
frontend/src/pages/Dashboard.js
```

Paste this entire contents into the file and save it:

```jsx
import { useEffect, useState } from "react";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    // Call GET /api/users/{id}
    // The token is attached automatically by the axios request interceptor
    api.get(`/users/${user.userId}`)
      .then((res) => setProfile(res.data))
      .catch(console.error);
  }, [user]);

  if (!profile) return <p>Loading...</p>;

  return (
    <div>
      <h1>Welcome, {profile.username}</h1>
      <p>Role: {profile.role}</p>
      <p>User ID: {profile.id}</p>
    </div>
  );
}
```

---

## 20. Create Protected Route Components

These components prevent unauthenticated or unauthorized users from seeing certain pages in the browser.

**Create a new file** at this exact path:

```
frontend/src/components/ProtectedRoute.js
```

> Create the `components` folder inside `frontend/src/` if it doesn't exist.

Paste this entire contents into the file and save it:

```jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// Requires the user to be logged in. Redirects to /login if not.
export function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

// Requires ROLE_ADMIN. Redirects to /unauthorized if they don't have the right role.
export function AdminRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== "ROLE_ADMIN") return <Navigate to="/unauthorized" replace />;
  return children;
}
```

> **Important:** These are *client-side* guards only. They prevent the user from seeing a page in the browser, but they don't protect the actual data. The backend still validates the JWT on every API call. Even if someone bypasses the React route guard (with browser dev tools, for example), the API will reject the request without a valid token.

---

## 21. Wire Up the React Router

Now we connect all the pages together with routing.

**Open the file** `frontend/src/App.js`. **Replace its entire contents** with:

```jsx
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { ProtectedRoute, AdminRoute } from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public route — anyone can see the login page */}
          <Route path="/login" element={<LoginPage />} />

          {/* Protected route — must be logged in */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />

          {/* Unauthorized page — shown when a non-admin tries to access admin routes */}
          <Route path="/unauthorized" element={<h1>403 — Forbidden</h1>} />

          {/* Default — anything else goes to login */}
          <Route path="*" element={<LoginPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
```

> **To add an admin-only route later**, import your `AdminPanel` component and add:
> ```jsx
> <Route path="/admin" element={
>   <AdminRoute>
>     <AdminPanel />
>   </AdminRoute>
> } />
> ```

### Start everything and test it!

**Terminal 1 — start the backend:**

```bash
cd backend
mvn spring-boot:run
```

**Terminal 2 — start the frontend:**

```bash
cd frontend
npm start
```

React should open `http://localhost:3000` in your browser. Try logging in with a user you've registered.

---

## 22. Testing with cURL

You can test the backend without the React frontend using `curl` in a terminal. This is useful for debugging.

### Register a new user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'
```

You'll get back something like:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Copy the `accessToken` value** — you'll need it for the next commands.

### Login with an existing user

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"secret123"}'
```

### Access a protected endpoint

Replace `<ACCESS_TOKEN>` with the actual token string you copied:

```bash
curl http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Try an admin-only endpoint (will return 403 if you're not an admin)

```bash
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

### Refresh your token

Replace `<REFRESH_TOKEN>` with the refresh token from the login response:

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

---

## 23. Common Pitfalls

| Problem | What's going wrong | How to fix it |
|---|---|---|
| CORS errors in the browser console | The backend doesn't allow requests from `localhost:3000` | Make sure you have the `corsConfigurationSource` bean in `SecurityConfig` (Step 11) |
| 403 Forbidden on every request | CSRF protection is enabled (it's on by default in Spring Security) | Make sure you have `.csrf(csrf -> csrf.disable())` in `SecurityConfig` |
| `@PreAuthorize` is completely ignored | You forgot to enable method security | Add `@EnableMethodSecurity` to the top of `SecurityConfig` |
| Role checks always fail | Your roles don't have the `ROLE_` prefix | `hasRole("ADMIN")` expects `ROLE_ADMIN`. Check your `Role` enum values |
| `authentication.principal.id` fails in SpEL | Your user class doesn't expose an `id` field | Make sure your entity implements `UserDetails` directly (like in Step 4) |
| Token expired but no refresh happens | The React interceptor isn't set up | Check the axios response interceptor in Step 16 |
| `401` on login or register | The auth endpoints aren't marked public | Make sure `.requestMatchers("/api/auth/**").permitAll()` is in `SecurityConfig` **before** `.requestMatchers("/api/**").authenticated()` |
| `password` field shows up in API responses | The User entity exposes all fields | Add `@JsonIgnore` on the password field, or return a DTO instead of the entity |

---

## 24. Security Annotation Cheat Sheet

Quick reference for all the `@PreAuthorize` expressions you can use on controller methods:

```java
// Only admins
@PreAuthorize("hasRole('ADMIN')")

// Admins or moderators
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")

// Any logged-in user
@PreAuthorize("isAuthenticated()")

// Owner or admin (compares path variable to logged-in user's id)
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")

// Custom bean check (calls a method on the "authz" Spring bean)
@PreAuthorize("@authz.ownsOrder(#orderId, authentication)")

// Combine multiple conditions
@PreAuthorize("hasRole('ADMIN') and #request.amount < 10000")

// Post-filter: runs the method, then removes items from the returned list
// that the user doesn't own (unless they're admin)
@PostFilter("filterObject.userId == authentication.principal.id or hasRole('ADMIN')")
public List<Order> getAllOrders() { return orderRepository.findAll(); }

// Post-authorize: runs the method, then checks the returned object.
// Returns 403 if the user doesn't own the result (unless they're admin)
@PostAuthorize("returnObject.userId == authentication.principal.id or hasRole('ADMIN')")
public Order getOrder(Long id) { return orderRepository.findById(id).orElseThrow(); }
```
