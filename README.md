# UCI-2123---Capstone-Project

This Capstone project is a mock Event Booking Platform demonstrating full-stack development with Spring Boot backend and React frontend.

## Project Structure

- **`Capstone-Springboot/`** - Spring Boot backend with REST APIs, JWT authentication, and MySQL database
- **`capstone-frontend/`** - React frontend with Vite, authentication, and event management UI
- **`Capstone Documentation/`** - Project documentation, diagrams, and planning
- **`Capstone ERD/`** - Database schema, SQL scripts, and entity relationship diagrams

## Security Features

The backend implements comprehensive security measures:

### SQL Injection Protection
- **Parameterized Queries**: Spring Data JPA uses prepared statements for all database access
- **Input Validation**: Request DTOs have validation constraints (`@NotBlank`, `@Size`, `@Positive`)
- **Safe Sorting**: `sortBy` parameters validated against whitelists to prevent ORDER BY injection
- **No Unsafe Patterns**: No native SQL queries, no string concatenation in queries

### Authentication & Authorization
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access**: ADMIN and USER roles with appropriate permissions
- **Input Validation**: All request data validated before processing
- **Password Security**: BCrypt password hashing

## Documentation

### Backend Security Documentation
- [SQL Injection Defense](Capstone-Springboot/Capstone-Springboot/docs/SQL-Injection-Defense.md) - How the backend prevents SQL injection
- [Security Rules Summary](Capstone-Springboot/Capstone-Springboot/docs/Security-Rules-Summary.md) - Endpoint authorization rules
- [Security SQL Injection Audit](Capstone-Springboot/Capstone-Springboot/docs/security-sql-injection.md) - Complete security audit

### Database Documentation
- Entity Relationship Diagrams in `Capstone ERD/` directory
- SQL setup scripts for database initialization

## Quick Start

### Backend
```bash
cd Capstone-Springboot/Capstone-Springboot
mvn spring-boot:run
```

### Frontend
```bash
cd capstone-frontend
npm install
npm run dev
```

## License
This project is part of the UCI 2123 Capstone Project.
