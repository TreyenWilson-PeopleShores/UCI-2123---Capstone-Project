# Event Booking Platform - Spring Boot Backend

## Overview
Spring Boot backend for an event booking platform with JWT authentication, role-based authorization, and RESTful APIs.

## Security Architecture

### SQL Injection Protection
The backend is protected against SQL injection through multiple layers:

1. **Spring Data JPA Parameter Binding**
   - All database queries use Spring Data JPA repositories
   - JPQL queries use named parameters (`:param`) that are bound as JDBC prepared statement parameters
   - No string concatenation in any SQL or JPQL queries
   - No `nativeQuery = true` annotations exist in the codebase

2. **Input Validation**
   - All request DTOs have validation constraints (`@NotBlank`, `@Size`, `@Positive`, etc.)
   - Controllers use `@Valid` to enforce validation before processing
   - Invalid input is rejected with HTTP 400 before reaching the database

3. **Safe Pagination and Sorting**
   - Pagination uses Spring Data's `Pageable` interface
- Filter parameters are passed as named parameters to JPQL queries
- `sortBy` parameters are validated against a whitelist of allowed field names

### Security Features
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN and USER roles with appropriate permissions
- **Input Validation**: All request data validated with Jakarta Bean Validation
- **SQL Injection Protection**: Parameterized queries through JPA/Hibernate
- **CORS Configuration**: Restricted to frontend origins
- **Password Security**: BCrypt password hashing

## API Documentation

**Complete API Documentation**: See `../Capstone Documentation/API-Documentation-V4.md` for 100% verified documentation of all 29 endpoints.

### Authentication
- `POST /api/auth/login` - Login with username/password, returns JWT token
- `POST /api/auth/register` - Register new user, returns JWT token

### Events
- `GET /api/events` - List all events with pagination
- `GET /api/events/id/{id}` - Get event by ID
- `GET /api/events/status/{status}` - Get events by status
- `GET /api/events/date` - Filter events by date range
- `POST /api/events` - Create new event (ADMIN only)
- `PUT /api/events/id/{id}/{status}` - Update event status (ADMIN only)

### Venues
- `GET /api/venues` - List all venues with pagination
- `GET /api/venues/id/{id}` - Get venue by ID
- `GET /api/venues/location/{location}` - Get venues by location
- `POST /api/venues` - Create new venue
- `PUT /api/venues/id/{id}/{location}` - Update venue location

### Tickets
- `GET /api/tickets` - List all tickets with pagination
- `GET /api/tickets/id/{id}` - Get ticket by ID
- `GET /api/tickets/event/{event_id}` - Get tickets by event ID
- `POST /api/tickets` - Create new ticket (ADMIN only)
- `PUT /api/tickets/id/{id}/sold/{sold}` - Update sold count (ADMIN only)
- `PUT /api/tickets/id/{id}/increment-sold` - Increment sold count (USER/ADMIN)
- `DELETE /api/tickets/delete/{id}` - Delete ticket (ADMIN only)

### Tickets Sold
- `GET /api/tickets-sold` - List all ticket sales (ADMIN only)
- `GET /api/tickets-sold/id/{id}` - Get ticket sale by ID (ADMIN only)
- `GET /api/tickets-sold/user/{userId}` - Get ticket sales by user ID
- `POST /api/tickets-sold` - Create ticket sale (USER/ADMIN)

### Users
- `GET /api/users` - List all users (ADMIN only)
- `GET /api/users/id/{id}` - Get user by ID
- `POST /api/users` - Create new user

## Security Documentation

Detailed security information is available in the `/docs` directory:

- [SQL Injection Defense](docs/SQL-Injection-Defense.md) - How the backend prevents SQL injection
- [Security Rules Summary](docs/Security-Rules-Summary.md) - Endpoint authorization rules
- [Auth Security Endpoints](docs/Auth-Security-Endpoints.md) - Authentication details
- [Security SQL Injection Audit](docs/security-sql-injection.md) - Complete security audit

## Running the Application

### Prerequisites
- Java 21 or higher (Maven target) - developed with Java 25 runtime
- Maven 3.6+
- MySQL 8.0+

### Configuration
1. Create a MySQL database named `capstone_improved`
2. Update `application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/capstone_improved
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Build and Run
```bash
# Navigate to the project directory
cd Capstone-Springboot/Capstone-Springboot

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Testing
Run the test suite:
```bash
mvn test
```

## Database Schema
The database schema is defined in the entity classes and can be initialized with:
- `SETUP-CapstoneDatabase.sql` - Initial database setup
- Entity Relationship Diagrams in `/Capstone ERD/` directory

## License
This project is part of the UCI 2123 Capstone Project.