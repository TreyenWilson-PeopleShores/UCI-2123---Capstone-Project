# Event Booking Platform - API Documentation V3

## Overview
This document provides comprehensive API documentation for the Event Booking Platform backend. The API follows RESTful principles and uses JWT authentication for secure access.

**Base URL**: `http://localhost:8080`

## Authentication

### JWT Authentication Flow
1. Obtain token via `/api/auth/login` or `/api/auth/register`
2. Include token in `Authorization: Bearer <token>` header
3. Token expires after 1 hour (3600000 ms)

### Access Levels
- **PUBLIC**: No authentication required
- **USER**: Requires valid JWT token with USER role
- **ADMIN**: Requires valid JWT token with ADMIN role

---

## Authentication Endpoints

### 1. Login (PUBLIC)
**POST** `/api/auth/login`

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "username": "treyen",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 2,
    "username": "treyen",
    "role": "USER"
  }
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid username or password
- `400 Bad Request`: Missing or invalid request body

### 2. Register (PUBLIC)
**POST** `/api/auth/register`

Registers a new user and returns a JWT token.

**Request Body:**
```json
{
  "username": "newuser",
  "password": "securepassword",
  "role": "USER"
}
```

**Note:** `role` is optional and defaults to `USER`. Only `USER` or `ADMIN` are valid roles.

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 9,
    "username": "newuser",
    "role": "USER"
  }
}
```

### 3. Legacy Login (PUBLIC)
**POST** `/auth/login`

Legacy endpoint for backward compatibility. Returns same response as `/api/auth/login`.

**Request Body:**
```json
{
  "username": "treyen",
  "password": "password123"
}
```

### 4. Get Current User (PUBLIC)
**GET** `/auth/me`

Placeholder endpoint that currently returns 401 Unauthorized.

**Response:** `401 Unauthorized`

---

## Event Management Endpoints

### 5. Get All Events (PUBLIC)
**GET** `/api/events`

Retrieves paginated list of all events with sorting options.

**Query Parameters:**
- `page` (optional, default: 0): Page number (0-indexed)
- `size` (optional, default: 31): Number of items per page
- `sortBy` (optional, default: "id"): Field to sort by. Allowed values: `id`, `event_name`, `date`, `status`, `total_spots`, `venue_id`
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/events?page=0&size=10&sortBy=date&ascending=false
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "event_name": "Tech Summit 2026",
      "date": "2026-04-26",
      "status": "SCHEDULED",
      "total_spots": 15000,
      "venue_id": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalPages": 80,
  "totalElements": 800,
  "last": false,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "first": true,
  "numberOfElements": 10,
  "empty": false
}
```

### 6. Get Event by ID (PUBLIC)
**GET** `/api/events/id/{id}`

Retrieves a specific event by its ID.

**Path Parameters:**
- `id` (required): Event ID

**Example Request:**
```
GET /api/events/id/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "event_name": "Tech Summit 2026",
  "date": "2026-04-26",
  "status": "SCHEDULED",
  "total_spots": 15000,
  "venue_id": 1
}
```

**Error Responses:**
- `404 Not Found`: Event with specified ID not found

### 7. Get Events by Status (PUBLIC)
**GET** `/api/events/status/{status}`

Retrieves paginated list of events filtered by status.

**Path Parameters:**
- `status` (required): Event status. Allowed values: `scheduled`, `cancelled`, `completed`

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/events/status/scheduled?page=0&size=5&sortBy=date&ascending=true
```

### 8. Filter Events by Date Range (PUBLIC)
**GET** `/api/events/date`

Retrieves events within a specified date range.

**Query Parameters:**
- `start` (required): Start date (YYYY-MM-DD format)
- `end` (required): End date (YYYY-MM-DD format)
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/events/date?start=2026-04-01&end=2026-04-30&page=0&size=10
```

### 9. Create Event (ADMIN)
**POST** `/api/events`

Creates a new event. Requires ADMIN role.

**Request Body:**
```json
{
  "event_name": "New Concert 2026",
  "date": "2026-05-15",
  "status": "scheduled",
  "total_spots": 5000,
  "venue_id": 3
}
```

**Validation Rules:**
- `event_name`: Required, max 200 characters
- `date`: Required, valid date
- `status`: Required, must be "scheduled", "cancelled", or "completed"
- `total_spots`: Required, positive number
- `venue_id`: Required, positive number

**Response (201 Created):**
```json
{
  "id": 801,
  "event_name": "New Concert 2026",
  "date": "2026-05-15",
  "status": "SCHEDULED",
  "total_spots": 5000,
  "venue_id": 3
}
```

### 10. Update Event Status (ADMIN)
**PUT** `/api/events/id/{id}/{status}`

Updates the status of an existing event. Requires ADMIN role.

**Path Parameters:**
- `id` (required): Event ID
- `status` (required): New status. Allowed values: `scheduled`, `cancelled`, `completed`

**Example Request:**
```
PUT /api/events/id/1/cancelled
```

**Response (200 OK):**
```json
{
  "id": 1,
  "event_name": "Tech Summit 2026",
  "date": "2026-04-26",
  "status": "CANCELLED",
  "total_spots": 15000,
  "venue_id": 1
}
```

**Error Responses:**
- `400 Bad Request`: Invalid status value
- `404 Not Found`: Event not found

---

## Venue Management Endpoints

### 11. Get All Venues (PUBLIC)
**GET** `/api/venues`

Retrieves paginated list of all venues with filtering and sorting options.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field. Allowed values: `id`, `venue_name`, `location`, `total_capacity`
- `ascending` (optional, default: true): Sort direction
- `city` (optional): Filter by city name
- `state` (optional): Filter by state name

**Example Requests:**
```
GET /api/venues?page=0&size=10&sortBy=total_capacity&ascending=false
GET /api/venues?city=Los%20Angeles&page=0&size=5
GET /api/venues?state=OH&page=0&size=5
```

### 12. Get Venue by ID (PUBLIC)
**GET** `/api/venues/id/{id}`

Retrieves a specific venue by its ID.

**Path Parameters:**
- `id` (required): Venue ID

**Example Request:**
```
GET /api/venues/id/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "venue_name": "Columbus Convention Center",
  "location": "Columbus, OH",
  "total_capacity": 20000
}
```

### 13. Get Venues by Location (PUBLIC)
**GET** `/api/venues/location/{location}`

Retrieves venues filtered by location.

**Path Parameters:**
- `location` (required): Location string (e.g., "Columbus, OH")

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/venues/location/Columbus%2C%20OH?page=0&size=5
```

### 14. Create Venue (USER/ADMIN)
**POST** `/api/venues`

Creates a new venue.

**Request Body:**
```json
{
  "venue_name": "New Stadium",
  "location": "Miami, FL",
  "total_capacity": 65000
}
```

**Response (201 Created):**
```json
{
  "id": 21,
  "venue_name": "New Stadium",
  "location": "Miami, FL",
  "total_capacity": 65000
}
```

### 15. Update Venue Location (USER/ADMIN)
**PUT** `/api/venues/id/{id}/{location}`

Updates the location of an existing venue.

**Path Parameters:**
- `id` (required): Venue ID
- `location` (required): New location

**Example Request:**
```
PUT /api/venues/id/1/Los%20Angeles%2C%20CA
```

**Note:** This endpoint is not recommended for general use due to URL encoding issues with spaces. Use query parameter filtering instead.

---

## Ticket Management Endpoints

### 16. Get All Tickets (PUBLIC)
**GET** `/api/tickets`

Retrieves paginated list of all tickets with price filtering and sorting.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field. Allowed values: `id`, `event_id`, `price`, `total_quantity`, `sold`
- `ascending` (optional, default: true): Sort direction
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter

**Example Requests:**
```
GET /api/tickets?page=0&size=10&sortBy=price&ascending=true
GET /api/tickets?minPrice=50.0&page=0&size=5
GET /api/tickets?maxPrice=100.0&page=0&size=5
```

### 17. Get Ticket by ID (PUBLIC)
**GET** `/api/tickets/id/{id}`

Retrieves a specific ticket by its ID.

**Path Parameters:**
- `id` (required): Ticket ID

**Example Request:**
```
GET /api/tickets/id/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "event_id": 1,
  "price": 85.50,
  "total_quantity": 15000,
  "sold": 7500
}
```

### 18. Get Tickets by Event ID (PUBLIC)
**GET** `/api/tickets/event/{event_id}`

Retrieves tickets for a specific event.

**Path Parameters:**
- `event_id` (required): Event ID

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/tickets/event/1?page=0&size=5&sortBy=price&ascending=true
```

### 19. Create Ticket (ADMIN)
**POST** `/api/tickets`

Creates a new ticket. Requires ADMIN role.

**Request Body:**
```json
{
  "event_id": 1,
  "price": 75.00,
  "total_quantity": 1000,
  "sold": 0
}
```

**Response (201 Created):**
```json
{
  "id": 801,
  "event_id": 1,
  "price": 75.00,
  "total_quantity": 1000,
  "sold": 0
}
```

### 20. Update Ticket Sold Count (ADMIN)
**PUT** `/api/tickets/id/{id}/sold/{sold}`

Updates the sold count for a ticket. Requires ADMIN role.

**Path Parameters:**
- `id` (required): Ticket ID
- `sold` (required): New sold count

**Example Request:**
```
PUT /api/tickets/id/1/sold/500
```

### 21. Increment Ticket Sold Count (USER/ADMIN)
**PUT** `/api/tickets/id/{id}/increment-sold`

Increments the sold count by 1. Used for ticket purchases.

**Path Parameters:**
- `id` (required): Ticket ID

**Example Request:**
```
PUT /api/tickets/id/1/increment-sold
```

### 22. Delete Ticket (ADMIN)
**DELETE** `/api/tickets/delete/{id}`

Deletes a ticket. Requires ADMIN role.

**Path Parameters:**
- `id` (required): Ticket ID

**Example Request:**
```
DELETE /api/tickets/delete/1
```

**Response:** `204 No Content`

---

## User Management Endpoints

### 23. Get All Users (ADMIN)
**GET** `/api/users`

Retrieves paginated list of all users. Requires ADMIN role.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field. Allowed values: `id`, `username`, `role`
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/users?page=0&size=10&sortBy=username&ascending=true
```

**Headers Required:**
```
Authorization: Bearer <admin-token>
```

### 24. Get User by ID (USER/ADMIN)
**GET** `/api/users/id/{id}`

Retrieves a specific user by ID.

**Path Parameters:**
- `id` (required): User ID

**Example Request:**
```
GET /api/users/id/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

### 25. Create User (PUBLIC)
**POST** `/api/users`

Creates a new user. Similar to `/api/auth/register` but returns user data without token.

**Request Body:**
```json
{
  "username": "newuser",
  "password": "securepassword",
  "role": "USER"
}
```

**Response (201 Created):**
```json
{
  "id": 9,
  "username": "newuser",
  "role": "USER"
}
```

---

## Ticket Sales Endpoints

### 26. Get All Ticket Sales (ADMIN)
**GET** `/api/tickets-sold`

Retrieves paginated list of all ticket sales with date filtering. Requires ADMIN role.

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field. Allowed values: `id`, `userId`, `ticketId`, `dateSold`
- `ascending` (optional, default: true): Sort direction
- `month` (optional): Filter by month (1-12)
- `year` (optional): Filter by year

**Example Requests:**
```
GET /api/tickets-sold?page=0&size=10&sortBy=dateSold&ascending=false
GET /api/tickets-sold?month=4&page=0&size=5
GET /api/tickets-sold?year=2026&page=0&size=5
GET /api/tickets-sold?month=4&year=2026&page=0&size=5
```

### 27. Get Ticket Sale by ID (ADMIN)
**GET** `/api/tickets-sold/id/{id}`

Retrieves a specific ticket sale by ID. Requires ADMIN role.

**Path Parameters:**
- `id` (required): Ticket sale ID

**Example Request:**
```
GET /api/tickets-sold/id/1
```

### 28. Get Ticket Sales by User ID (USER/ADMIN)
**GET** `/api/tickets-sold/user/{userId}`

Retrieves ticket sales for a specific user. Users can only access their own sales; admins can access any user's sales.

**Path Parameters:**
- `userId` (required): User ID

**Query Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Items per page
- `sortBy` (optional, default: "id"): Sort field
- `ascending` (optional, default: true): Sort direction

**Example Request:**
```
GET /api/tickets-sold/user/3?page=0&size=10&sortBy=dateSold&ascending=true
```

### 29. Create Ticket Sale (USER/ADMIN)
**POST** `/api/tickets-sold`

Creates a new ticket sale record. Used when a user purchases a ticket.

**Request Body:**
```json
{
  "user_id": 2,
  "ticket_id": 1,
  "date_sold": "2026-04-25"
}
```

**Response (201 Created):**
```json
{
  "id": 6001,
  "user_id": 2,
  "ticket_id": 1,
  "date_sold": "2026-04-25"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request parameters or body
- `401 Unauthorized`: Authentication required or invalid credentials
- `403 Forbidden`: Insufficient permissions (wrong role)
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Error Response Format
```json
{
  "timestamp": "2026-04-25T14:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status. Allowed values: scheduled, cancelled, completed",
  "path": "/api/events/id/1/invalid"
}
```

---

## Security Notes

### JWT Token Usage
1. Obtain token from `/api/auth/login` or `/api/auth/register`
2. Include in request headers: `Authorization: Bearer <token>`
3. Token expires after 1 hour

### Role-Based Access Control
- **ADMIN**: Full access to all endpoints
- **USER**: Can access most endpoints except admin-only operations
- **PUBLIC**: No authentication required (read-only endpoints)

### SQL Injection Protection
All endpoints use:
- Parameterized queries via Spring Data JPA
- Whitelist validation for sort parameters
- Input validation with Jakarta Bean Validation
- No native SQL queries in the codebase

### Rate Limiting
- Authentication endpoints: 10 requests per minute
- API endpoints: 600 requests per minute

### CORS Configuration
Allowed origins:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (alternative dev server)

---

## Database Schema Reference

### Events Table
```sql
CREATE TABLE events (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    status ENUM('SCHEDULED','CANCELLED','COMPLETED') NOT NULL,
    total_spots INT NOT NULL,
    venue_id INT NOT NULL,
    UNIQUE KEY events_venue_date_unique (venue_id, date)
);
```

### Venues Table
```sql
CREATE TABLE venues (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    venue_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    total_capacity BIGINT NOT NULL
);
```

### Tickets Table
```sql
CREATE TABLE tickets (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    total_quantity INT NOT NULL,
    sold INT NOT NULL,
    UNIQUE KEY tickets_event_id_unique (event_id)
);
```

### Users Table
```sql
CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER')
);
```

### Tickets Sold Table
```sql
CREATE TABLE tickets_sold (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ticket_id INT NOT NULL,
    date_sold DATE NOT NULL
);
```

---

## Sample Data

### Default Users
| Username | Password | Role |
|----------|----------|------|
| admin | adminpass | ADMIN |
| treyen | password123 | USER |
| alex | password123 | USER |
| jordan | password123 | USER |

### Sample Events (April 2026)
- April 26th: Multiple SCHEDULED events for presentation day
- April 1-25th: COMPLETED or CANCELLED events
- ~20% of SCHEDULED events are sold out
- ~40% of April 2026 events are sold out

---

## Testing with Postman

### 1. Obtain Token
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "treyen",
  "password": "password123"
}
```

### 2. Use Token in Requests
```http
GET http://localhost:8080/api/events
Authorization: Bearer <your-token>
```

### 3. Test Admin Endpoints
Use admin credentials:
```json
{
  "username": "admin",
  "password": "adminpass"
}
```

---

## Version History
- **V1**: Initial API design
- **V2**: Added JWT authentication and role-based access
- **V3**: Current version with comprehensive endpoint documentation, pagination, filtering, and enhanced security

---

## Support
For API issues or questions, refer to:
- Backend README: `Capstone-Springboot/Capstone-Springboot/README.md`
- Security Documentation: `Capstone-Springboot/Capstone-Springboot/docs/`
- Database Schema: `Capstone ERD/SETUP-CapstoneDatabase.sql`