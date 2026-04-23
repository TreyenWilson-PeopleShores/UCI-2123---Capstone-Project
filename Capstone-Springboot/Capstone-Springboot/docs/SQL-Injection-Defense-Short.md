# SQL Injection Defense - Quick Explanation

## How We Prevent SQL Injection

### 1. JPA/Hibernate Uses Prepared Statements
- All database queries go through Spring Data JPA
- User input is bound as JDBC parameters, never concatenated into SQL strings
- This is automatic with JPA repository methods and JPQL queries

### 2. Input Validation Before Database Access
- Request DTOs have validation annotations (`@NotBlank`, `@Size`, `@Positive`)
- Controllers use `@Valid` to enforce validation
- Invalid input is rejected with HTTP 400 before reaching the database

### 3. Safe Sorting with Whitelists
- `sortBy` parameters are validated against allowed field names
- Unknown values fall back to default sorting
- Prevents ORDER BY injection attacks

### 4. No Unsafe Code Patterns
- No `nativeQuery = true` annotations
- No `EntityManager.createNativeQuery()` calls
- No string concatenation in JPQL or SQL

## Key Security Points

1. **Parameterized Queries**: JPA automatically uses prepared statements
2. **Validation First**: Input validated before any database operation  
3. **Defense in Depth**: Multiple layers prevent SQL injection
4. **Safe by Default**: Spring Data JPA patterns are secure by design

## For Presentation/Instructor Q&A

**Q: How does this backend prevent SQL injection?**
A: Three main ways:
1. Spring Data JPA uses parameterized queries (prepared statements) automatically
2. All user input is validated with constraints before database access
3. Sorting parameters are whitelisted to prevent ORDER BY injection

**Q: What unsafe patterns are avoided?**
A: No native SQL queries, no string concatenation in queries, no raw JDBC calls.

**Q: How is user input validated?**
A: Request DTOs have annotations like `@NotBlank`, `@Size`, `@Positive`. Controllers use `@Valid` to enforce these rules before processing.

**Q: What about pagination and sorting?**
A: Spring Data's `Pageable` interface handles pagination safely. Sort fields are validated against a whitelist of allowed column names.