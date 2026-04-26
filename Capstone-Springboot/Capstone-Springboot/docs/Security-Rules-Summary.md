# Security Rules Summary (Updated)

## Public endpoints (no authentication required)
- `POST /auth/login` (legacy frontend contract)
- `GET /auth/me` (returns 401 placeholder)
- `POST /api/auth/login` (JWT login)
- `POST /api/auth/register` (JWT registration)
- `GET /api/events/**` (read-only)
- `GET /api/venues/**` (read-only)
- `GET /api/tickets/**` (read-only ticket inventory and pricing)

## Protected endpoints (require authentication)
All other `/api/**` endpoints require a valid JWT token.

## Role-based access

### ADMIN-only endpoints
- `PUT /api/events/id/{id}/{status}` (change event status)
- `DELETE /api/tickets/delete/{id}` (delete ticket)
- `GET /api/users` (list all users)
- `GET /api/tickets-sold/id/{id}` (view any ticket sale by ID)
- `GET /api/tickets-sold` (list all ticket sales)
- `POST /api/tickets` (create ticket)
- `PUT /api/tickets/id/{id}/sold/{sold}` (update sold count)

### USER-only endpoints
- `POST /api/tickets-sold` (purchase a ticket)

### Mixed authorization (USER can access own data, ADMIN can access any)
- `GET /api/tickets-sold/user/{userId}`
  - USER can access only if `userId` matches their own ID.
  - ADMIN can access any user's ticket sales.

## Authorization details
- `SecurityUtil.isCurrentUser(Long userId)` checks if the authenticated user's ID matches the given ID.
- `@PreAuthorize("hasRole('ADMIN') or @securityUtil.isCurrentUser(#userId)")` used for user-specific endpoints.
- Roles are stored as "USER" or "ADMIN" in the database; Spring Security adds "ROLE_" prefix.

## CORS
- Allowed origins: `http://localhost:5173`, `http://localhost:3000`
- Allowed headers: `Authorization`, `Content-Type`, `Accept`
- Exposed headers: `Authorization`
- Credentials allowed.

## Expected responses
- Unauthenticated call to `/api/tickets-sold/user/{id}` → `401 Unauthorized`
- Authenticated USER accessing another user's tickets → `403 Forbidden`
- Authenticated USER accessing own tickets → `200 OK`
- ADMIN accessing any user's tickets → `200 OK`