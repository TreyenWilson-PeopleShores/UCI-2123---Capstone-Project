# Auth and Security Endpoints

## Existing endpoint preserved
- `POST /auth/login`
  - Continues to return the existing `UserResponse` object.
  - Authentication now uses BCrypt matching and JWT internally.
  - This endpoint remains public and unchanged for the frontend contract.
- `GET /auth/me`
  - Still returns `401 Unauthorized` as a placeholder.

## New JWT auth endpoints
- `POST /api/auth/login`
  - Request body: `{ "username": "...", "password": "..." }`
  - Response body: `{ "accessToken": "...", "tokenType": "Bearer" }`

- `POST /api/auth/register`
  - Request body: `{ "username": "...", "password": "...", "role": "USER" }`
  - `role` is optional and defaults to `USER`.
  - Response body: `{ "accessToken": "...", "tokenType": "Bearer" }`

## Security behavior
- JWT tokens are expected in the `Authorization` header as `Bearer <token>`.
- `/auth/**` and `/api/auth/**` are public.
- `GET /api/events/**` and `GET /api/venues/**` remain public.
- All other `/api/**` endpoints require authentication.

## Admin role enforcement
- `PUT /api/events/id/{id}/{status}` requires `ADMIN` role to change event status.
- `DELETE /api/tickets/delete/{id}` requires `ADMIN` role.
- `GET /api/users` requires `ADMIN` role and protects user listing.

## CORS
- Allowed origins:
  - `http://localhost:5173`
  - `http://localhost:3000`
- Allowed request headers include `Authorization`, `Content-Type`, and `Accept`.
- `Authorization` is exposed in responses.
