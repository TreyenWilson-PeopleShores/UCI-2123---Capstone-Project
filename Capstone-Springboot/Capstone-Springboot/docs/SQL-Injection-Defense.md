# SQL Injection Defense

## How This Backend Prevents SQL Injection

### 1. Spring Data JPA and Hibernate Use Parameterized Queries
All database queries go through Spring Data JPA repositories. JPA and Hibernate automatically convert method calls and JPQL queries into **prepared statements** with parameter binding. This means user input is never concatenated into SQL strings.

**Example:**
```java
// Safe: Spring generates a parameterized query
Optional<User> findByUsername(String username);

// Safe: Named parameters are bound as JDBC parameters
@Query("SELECT e FROM Event e WHERE e.status = :status")
Page<Event> findByStatus(@Param("status") String status, Pageable pageable);
```

### 2. Input Validation with DTOs and @Valid
All incoming request data is validated before reaching the database:

- **Request DTOs** have constraints like `@NotBlank`, `@Size`, `@Positive`
- **Controllers** use `@Valid` to enforce validation
- **Invalid input** is rejected with HTTP 400 before any database operation

**Example:**
```java
@PostMapping
public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
    // Only reaches here if validation passes
    return userService.createUser(request);
}
```

### 3. Safe Pagination, Filtering, and Sorting
- **Pagination**: Uses Spring Data's `Pageable` interface with safe defaults
- **Filtering**: All filter parameters are passed as named parameters to JPQL queries
- **Sorting**: `sortBy` parameters are validated against a whitelist of allowed field names

**Sorting protection:**
```java
// Whitelist prevents ORDER BY injection
private static final Set<String> ALLOWED_SORT_FIELDS = 
    Set.of("id", "event_name", "date", "status");
    
private String resolveSortField(String sortBy) {
    return ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
}
```

### 4. No Native Queries or String Concatenation
The codebase contains:
- **No `nativeQuery = true`** annotations
- **No `EntityManager.createNativeQuery()`** calls  
- **No string concatenation** in JPQL or SQL

### 5. What This Prevents
- **SQL injection**: User input cannot modify query structure
- **Data type attacks**: Validation ensures numbers are numbers, strings have length limits
- **ORDER BY injection**: Sort fields are whitelisted
- **Oversized payloads**: String fields have maximum length constraints

### 6. For Future Development
When adding new features:
- Always use named parameters (`:param`) in `@Query` annotations
- Never use string concatenation in JPQL or SQL
- Validate all user input with DTO constraints
- Whitelist any `sortBy` parameters
- Avoid `nativeQuery = true` unless absolutely necessary

The combination of JPA's parameter binding, Spring Data's repository pattern, and input validation provides defense-in-depth against SQL injection attacks.