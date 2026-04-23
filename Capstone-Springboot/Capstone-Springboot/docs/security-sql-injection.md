# SQL Injection Security Audit

**Date:** 2026-04-23  
**Scope:** All repository queries, service-layer database access, controller input handling, and request DTOs.

---

## Why This Application Is Largely Safe by Default

This backend uses **Spring Data JPA with Hibernate** as its ORM layer. JPA/Hibernate parameterizes all values before sending them to the database driver, which means:

- Named parameters (`:param`) in `@Query` annotations are **never** string-concatenated into the SQL string — they are bound as JDBC `PreparedStatement` parameters.
- Spring Data derived query methods (e.g., `findByStatus`, `findByUsername`) are compiled by Spring into parameterized queries at startup.
- `JpaRepository` CRUD methods (`findById`, `save`, `deleteById`, etc.) use parameterized statements internally.

No raw `EntityManager.createNativeQuery(...)` calls with string concatenation were found anywhere in the codebase.

---

## What Was Audited

### Repositories

| Repository | Query Type | Risk |
|---|---|---|
| `EventRepository` | Derived methods (`findByStatus`, `findByDateBetween`) | ✅ Safe — Spring-generated parameterized queries |
| `TicketRepository` | `@Query` JPQL with `:event_id`, `:minPrice`, `:maxPrice` | ✅ Safe — named parameters |
| `TicketSoldRepository` | `@Query` JPQL with `:month`, `:year`, `:userId` | ✅ Safe — named parameters |
| `UserRepository` | Derived method (`findByUsername`) | ✅ Safe — Spring-generated parameterized query |
| `VenueRepository` | Derived method + `@Query` JPQL with `:city`, `:state` | ✅ Safe — named parameters |

**No `nativeQuery = true` was found in any repository.**  
**No string concatenation was found in any `@Query` annotation.**

### Services

All service classes (`EventService`, `VenueService`, `UserService`, `TicketService`, `TicketSoldService`, `AuthService`) delegate exclusively to repository methods. No raw SQL construction was found.

### Controllers — `sortBy` Parameter (Risk Found & Fixed)

**Risk:** All five paginated controllers accepted a free-text `sortBy` query parameter and passed it (after a partial alias mapping) directly to `Sort.by(sortBy)`. Spring Data JPA passes the sort field name into the `ORDER BY` clause via Hibernate. An unvalidated value could allow an attacker to probe column names or cause unexpected query behavior (ORDER BY injection).

**Example of the vulnerable pattern (before fix):**
```java
// BEFORE — user-supplied sortBy passed through after partial mapping only
String mappedSortBy = sortBy;
if ("eventName".equalsIgnoreCase(sortBy)) {
    mappedSortBy = "event_name";
}
Sort sort = Sort.by(mappedSortBy).ascending(); // ← arbitrary value reaches ORDER BY
```

**Fix applied:** Each controller now has a `resolveSortField()` helper that maps aliases and then validates the result against a `static final Set<String>` whitelist. Any value not in the whitelist silently falls back to `"id"`.

```java
// AFTER — strict whitelist enforced
private static final Set<String> ALLOWED_SORT_FIELDS =
        Set.of("id", "event_name", "date", "status", "total_spots", "venue_id");

private String resolveSortField(String sortBy) {
    String mapped = sortBy;
    if ("eventName".equalsIgnoreCase(sortBy))  mapped = "event_name";
    else if ("totalSpots".equalsIgnoreCase(sortBy)) mapped = "total_spots";
    else if ("venueId".equalsIgnoreCase(sortBy))    mapped = "venue_id";
    return ALLOWED_SORT_FIELDS.contains(mapped) ? mapped : "id";
}
```

### Controllers — `status` Path Variable (Risk Found & Fixed)

**Risk:** `EventController.changeStatus()` accepted any string as the `{status}` path variable and stored it directly in the database without validation.

**Fix applied:** The endpoint now validates the value against `ALLOWED_STATUSES = Set.of("scheduled", "cancelled", "completed")` and returns HTTP 400 for any other value.

---

## What Was Fixed

| Location | Issue | Fix |
|---|---|---|
| `EventController` | Unvalidated `sortBy` passed to `Sort.by()` | Whitelist via `resolveSortField()` |
| `VenueController` | Unvalidated `sortBy` passed to `Sort.by()` | Whitelist via `resolveSortField()` |
| `TicketController` | Unvalidated `sortBy` passed to `Sort.by()` | Whitelist via `resolveSortField()` |
| `TicketSoldController` | Unvalidated `sortBy` passed to `Sort.by()` | Whitelist via `resolveSortField()` |
| `UserController` | Unvalidated `sortBy` passed to `Sort.by()` | Whitelist via `resolveSortField()` |
| `EventController.changeStatus` | Free-text `status` path variable stored to DB | Validated against `ALLOWED_STATUSES` set |
| `LoginRequest` | No `@NotBlank` constraints; `@Valid` was used but had no effect | Added `@NotBlank` + `@Size` constraints |
| `AuthController.login` | Missing `@Valid` on `@RequestBody` | Added `@Valid` |
| `EventRequest.status` | No format validation on status field | Added `@Pattern` constraint |
| `EventRequest.total_spots` | No range validation | Added `@Positive` |
| `VenueRequest.total_capacity` | No range validation | Added `@Positive` |
| `TicketRequest.price` | No range validation | Added `@DecimalMin` |
| `TicketRequest.total_quantity` | No range validation | Added `@Positive` |
| `TicketRequest.sold` | No range validation | Added `@PositiveOrZero` |
| `UserRequest` / `RegisterRequest` | No length limits on username/password | Added `@Size` constraints |
| `VenueRequest` / `EventRequest` | No length limits on name/location strings | Added `@Size` constraints |

---

## What Was Confirmed Safe (No Changes Needed)

- All `@Query` JPQL annotations use named parameters — no concatenation.
- No `nativeQuery = true` exists anywhere.
- No `EntityManager` usage exists in any service or repository.
- `UserRepository.findByUsername` is a derived query — fully parameterized.
- Password handling uses `BCryptPasswordEncoder` — no plaintext storage.
- `TicketSoldController` user-scoping uses `@securityUtil.isCurrentUser(#userId)` — authorization is enforced at the method level.

---

## Guidance for Future Features

### DO — Safe Patterns

```java
// ✅ Derived query — Spring generates a parameterized query
Optional<User> findByUsername(String username);

// ✅ Named parameter in JPQL
@Query("SELECT e FROM Event e WHERE e.status = :status")
Page<Event> findByStatus(@Param("status") String status, Pageable pageable);

// ✅ Whitelisted sort field
private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "date");
String safe = ALLOWED_SORT_FIELDS.contains(input) ? input : "id";
Sort.by(safe);
```

### DON'T — Dangerous Patterns

```java
// ❌ String concatenation in JPQL — SQL injection risk
@Query("SELECT e FROM Event e WHERE e.status = '" + status + "'")

// ❌ Native query with concatenation
entityManager.createNativeQuery("SELECT * FROM events WHERE name = '" + name + "'");

// ❌ Unvalidated sort field passed directly to Sort.by()
Sort.by(request.getParam("sortBy")); // attacker controls ORDER BY column

// ❌ @Valid on controller but no constraints on DTO fields — validation is silently skipped
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) { ... }
// LoginRequest has no @NotBlank — @Valid does nothing useful
```

### Checklist for New Endpoints

- [ ] All `@Query` annotations use `:namedParam` — never string concatenation.
- [ ] Any `sortBy` parameter is validated against a `Set<String>` whitelist before being passed to `Sort.by()`.
- [ ] Any path variable or query param that maps to an enum-like domain value (status, role, type) is validated against an allowed-values set.
- [ ] All request DTOs have `@NotBlank`/`@NotNull` on required fields.
- [ ] String fields have `@Size(max = N)` to prevent oversized payloads.
- [ ] Numeric fields have `@Positive` or `@Min`/`@Max` as appropriate.
- [ ] Controllers use `@Valid` on `@RequestBody` parameters.
- [ ] No `nativeQuery = true` is introduced without a security review.
