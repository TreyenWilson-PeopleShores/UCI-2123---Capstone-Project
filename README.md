# Event Booking Platform - Capstone Project

## Project Overview
This is a full-stack Event Booking Platform capstone project built with:
- **Spring Boot Backend**: RESTful APIs with JWT authentication and role-based authorization
- **React Frontend**: Modern UI with Vite, authentication, and event management
- **MySQL Database**: Relational database with comprehensive event, venue, and ticket management
- **JWT Authentication**: Secure token-based authentication with ADMIN and USER roles

## Project Structure
```
UCI-2123---Capstone-Project/
├── Capstone-Springboot/           # Spring Boot backend
│   └── Capstone-Springboot/
│       ├── src/                   # Java source code
│       ├── pom.xml               # Maven dependencies
│       ├── application.properties # Configuration
│       └── docs/                 # Security documentation
├── capstone-frontend/            # React frontend
│   ├── src/                      # React components
│   ├── package.json             # Node.js dependencies
│   └── vite.config.js           # Vite configuration
├── Capstone Documentation/       # Project documentation
├── Capstone ERD/                # Database schema and SQL scripts
└── README.md                    # This file
```

## Prerequisites
- **Java 25** (Used during development; earlier LTS versions may work but were not tested)


- **Node.js 18+** and npm
- **MySQL 8.0+** database server
- **Maven 3.8+** for backend build

## Database Setup

### 1. Create and Initialize Database
Run the SQL script to create the database schema and populate with sample data:

```bash
# Navigate to the database scripts directory
cd "Capstone ERD"

# Connect to MySQL and run the setup script
mysql -u root -p < SETUP-CapstoneDatabase.sql
```

### 2. Database Schema
The database includes the following tables:
- **events**: Event information with status (SCHEDULED, CANCELLED, COMPLETED)
- **venues**: Venue details with capacity information
- **tickets**: Ticket pricing and availability
- **users**: User accounts with ADMIN/USER roles
- **tickets_sold**: Ticket purchase history

### 3. Default Credentials
For demonstration purposes, the SQL seed data includes example users:
- **Admin**: `admin` / `adminpass` (ADMIN role)
- **Regular Users**: `treyen`, `alex`, `jordan`, etc. / `password123` (USER role)

## Backend Setup (Spring Boot)

### 1. Configure Database Connection
Edit `Capstone-Springboot/Capstone-Springboot/src/main/resources/application.properties` if needed:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/capstone_improved
spring.datasource.username=root
spring.datasource.password=password
```

### 2. Build and Run the Backend
```bash
# Navigate to backend directory
cd "Capstone-Springboot/Capstone-Springboot"

# Clean and build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on **port 8080** (default).

### 3. Backend Ports and Configuration
- **Main Application**: `http://localhost:8080`
- **API Base Path**: `/api`
- **Authentication Endpoints**: `/api/auth/*`
- **Database**: MySQL on `localhost:3306`

## Frontend Setup (React)

### 1. Install Dependencies
```bash
# Navigate to frontend directory
cd "capstone-frontend"

# Install npm dependencies
npm install
```

### 2. Configure Proxy
The frontend is configured to proxy API requests to the backend (see `vite.config.js`):
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
    },
    '/auth': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
    }
  }
}
```

### 3. Run the Frontend
```bash
# Start development server
npm run dev
```

The frontend will start on **port 5173** (Vite default).

### 4. Frontend Ports
- **Development Server**: `http://localhost:5173`
- **API Proxy**: Routes to `http://localhost:8080`

## Authentication System

### JWT Authentication Flow
1. **Login**: `POST /api/auth/login` with username/password
2. **Token Generation**: Returns JWT access token
3. **Authorization**: Include token in `Authorization: Bearer <token>` header
4. **Role-Based Access**: ADMIN vs USER permissions enforced

### Available Roles
- **ADMIN**: Full access to all endpoints including user management and event status changes
- **USER**: Can view events, venues, tickets, and purchase tickets

### Authentication Endpoints
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/register` - Register new user (defaults to USER role)

## API Documentation

### Public Endpoints (No Authentication Required)
- `GET /api/events` - List all events with pagination
- `GET /api/events/id/{id}` - Get event by ID
- `GET /api/events/status/{status}` - Get events by status
- `GET /api/events/date` - Filter events by date range
- `GET /api/venues` - List all venues
- `GET /api/tickets` - List ticket availability

### Protected Endpoints (Require Authentication)
- `POST /api/events` - Create new event (ADMIN only)
- `PUT /api/events/id/{id}/{status}` - Update event status (ADMIN only)
- `GET /api/users` - List users (ADMIN only)



## Testing with Postman

### 1. Obtain JWT Token
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "treyen",
  "password": "password123"
}
```

Response will include:
```json
{
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0cmV5ZW4iLCJpYXQiOjE3NzcxNDU0NDYsImV4cCI6MTc3NzE0OTA0Nn0.9n-WDlRO7HZxrMG4svizW3lVQj9WHtZKXP5Nbv4y-T4GG-LOc9_XXH60b5N61XOYxiAZhdtMghWz8uKoVSmJ9A",
    "user": {
        "id": 2,
        "password": null,
        "role": "USER",
        "username": "treyen"
    },
    "tokenType": "Bearer"
}
```

### 2. Use Token in Requests
Add to Headers:
```
Authorization: Bearer <your-access-token>
```

### 3. Test Admin Endpoints
Use admin credentials:
```json
{
  "username": "admin",
  "password": "adminpass"
}
```

## Security Features

### SQL Injection Protection
- **Parameterized Queries**: Spring Data JPA uses prepared statements
- **Input Validation**: Jakarta Bean Validation on all DTOs
- **Safe Sorting**: Whitelist validation for sort parameters
- **No Native Queries**: All database access through JPA repositories

### Authentication Security
- **JWT Tokens**: Stateless authentication with expiration
- **BCrypt Password Hashing**: Secure password storage
- **Role-Based Authorization**: Fine-grained access control
- **CORS Configuration**: Restricted to frontend origins
- **Rate Limiting**: Protection against brute force attacks

## Development Workflow

### 1. Start Database
Ensure MySQL is running with the `capstone_improved` database.

### 2. Start Backend
```bash
cd "Capstone-Springboot/Capstone-Springboot"
mvn spring-boot:run
```

### 3. Start Frontend
```bash
cd "capstone-frontend"
npm run dev
```

### 4. Access Application
- **Frontend**: `http://localhost:5173`
- **Backend API**: `http://localhost:8080/api`
- **Database**: `localhost:3306/capstone_improved`

## Project Documentation

### Technical Documentation
- **Database ERD**: `Capstone ERD/` directory contains entity relationship diagrams
- **Security Documentation**: `Capstone-Springboot/Capstone-Springboot/docs/`
  - `SQL-Injection-Defense.md` - SQL injection prevention strategies
  - `Security-Rules-Summary.md` - Endpoint authorization rules
  - `Auth-Security-Endpoints.md` - Authentication endpoint details
- **Component Diagrams**: `Capstone Documentation/Component Diagram.md`

### API Documentation
- **API Documentation V4 (100% Verified)**: `API-Documentation-V4.md` - All 29 endpoints tested and confirmed working
- **Postman Collection**: `Capstone-Springboot/Capstone-Springboot/postman-collection.json`
- **Backend README**: `Capstone-Springboot/Capstone-Springboot/README.md`
- **Previous Version (Archive)**: `API-Documentation-V3-ARCHIVE.md` - For reference only

## Architecture Decisions

### Backend Technology Choices
- **Spring Boot 4.0.5**: Rapid development with embedded Tomcat
- **Spring Security**: Comprehensive security framework
- **Spring Data JPA**: Object-relational mapping with repository pattern
- **MySQL**: Relational database for structured event data
- **JWT**: Stateless authentication for scalability

### Frontend Technology Choices
- **React 19**: Component-based UI library
- **Vite**: Fast development server and build tool
- **React Router**: Client-side routing
- **Proxy Configuration**: Simplified API calls during development

### Security Decisions
- **JWT over Sessions**: Stateless authentication for REST APIs
- **BCrypt Hashing**: Industry-standard password security
- **Role-Based Access Control**: Clear separation of ADMIN/USER permissions
- **CORS Restrictions**: Limited to development origins
- **Input Validation**: Defense in depth at multiple layers

## Troubleshooting

### Common Issues

#### Backend Won't Start
1. Check MySQL is running: `mysql -u root -p`
2. Verify database exists: `SHOW DATABASES;`
3. Check application.properties configuration
4. Clean and rebuild: `mvn clean install`

#### Frontend Can't Connect to Backend
1. Ensure backend is running on port 8080
2. Check proxy configuration in `vite.config.js`
3. Verify CORS settings in backend SecurityConfig
4. Check browser console for errors

#### Authentication Failures
1. Verify username/password in database
2. Check JWT token in Authorization header
3. Ensure token hasn't expired (default 1 hour)
4. Verify role permissions for endpoint

### Logs and Debugging
- **Backend Logs**: Check console output from `mvn spring-boot:run`
- **Frontend Logs**: Browser developer console
- **Database Logs**: MySQL error log

## AI Usage Disclosure

This project was developed with extensive assistance from AI tools, specifically the VS Code Copilot AI Agent. The AI was used for:

### Code Generation & Assistance
- **Backend Development**: Spring Boot controllers, services, repositories, and security configuration
- **Frontend Development**: React components, authentication context, and API integration
- **Database Design**: SQL schema creation and sample data generation
- **Security Implementation**: JWT authentication, role-based authorization, and SQL injection protection


## Demo Notes

- The application is intended to be run locally for demonstration.
- Authentication is demonstrated via:
  - React frontend login flow
  - Postman using JWT Bearer tokens (ADMIN and USER roles)
- Seed data includes:
  - Events with available tickets
  - Events that are sold out to demonstrate business rule handling



### Documentation & Planning
- **Technical Documentation**: API documentation, security guidelines, and setup instructions
- **Project Planning**: Architecture decisions, component diagrams, and workflow design
- **Code Review**: Security audit and best practices validation

### Human Oversight
While AI tools provided significant assistance, all code was:
- **Reviewed** by the developer for correctness and security
- **Tested** to ensure functionality meets requirements
- **Validated** against project specifications and best practices
- **Guided** by developer decisions on architecture and implementation

The AI served as a collaborative tool to accelerate development while maintaining code quality and security standards.

## License & Acknowledgments

This project was developed as a capstone project for educational purposes. The code is available for learning and reference.

### Built With
- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [MySQL](https://www.mysql.com/)
- [Vite](https://vitejs.dev/)


### Acknowledgments
- Per Scholas for the technical training and capstone framework
- Accenture for industry-aligned guidance and professional development standards
- Spring and React open-source communities for excellent documentation and tooling
- VS Code Copilot for AI-assisted development and documentation support

```

## License
This project is part of the UCI 2123 Capstone Project.
