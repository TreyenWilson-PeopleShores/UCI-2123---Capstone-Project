# Event Booking Platform - Capstone Presentation

## Slide 1: Title Slide
**Event Booking Platform**  
*A Full-Stack Event Discovery and Management System*

**Presented by:** [Your Name]  
**Course:** UCI 2123 - Capstone Project  
**Date:** [Presentation Date]

---

## Slide 2: Project Overview
**What I Built:**
- **Full-stack application** with React frontend, Spring Boot backend, and MySQL database
- **Centralized event discovery platform** for users to browse events by date and view ticket availability
- **Admin management system** for event lifecycle management through a REST API

**Core User Experience:**
- Discover events quickly through calendar view
- View detailed event information
- Track ticket availability in real-time

**Core Admin Experience:**
- Manage event lifecycle (create, update, cancel)
- Maintain data consistency through business rules
- Monitor ticket sales and availability

---

## Slide 3: Problem & Solution (1 minute)

**Problem (Before):**
- Event information fragmented across spreadsheets, social posts, and multiple tools
- Users struggle to find events by date/status and understand real-time availability
- No centralized platform for event discovery and ticket management

**Solution (After):**
- Centralized event catalog with intuitive calendar discovery
- REST API supporting filtering (date range, status), pagination, and consistent error handling
- Ticketing system representing event inventory with completed sales records for reporting
- Real-time availability tracking and user-friendly interface

---

## Slide 4: System Architecture Overview (2 minutes)

**Three-Tier Architecture:**
```
[ React Frontend ] - Modern UI with Vite
        ↓
    HTTP / REST API
        ↓
[ Spring Boot Backend ] - JWT authentication, business logic
        ↓
    JPA / Hibernate
        ↓
[ MySQL Database ] - Relational data with constraints
```

**Technology Stack:**
- **Frontend:** React 18+ with Vite, React Router, Axios for API calls
- **Backend:** Spring Boot 3.4+, Spring Security with JWT, Spring Data JPA
- **Database:** MySQL 8.0+ with 5 normalized tables
- **Authentication:** JWT tokens with 1-hour expiration
- **Build Tools:** Maven (backend), npm (frontend)

---

## Slide 5: Database Schema & Relationships
**Database Tables:**
1. **users** - User accounts with ADMIN/USER roles
2. **venues** - Event locations with capacity constraints
3. **events** - Event listings with status (SCHEDULED/CANCELLED/COMPLETED)
4. **tickets** - Ticket inventory per event with pricing
5. **tickets_sold** - Sales records for auditing and reporting

**Key Constraints:**
- Unique constraint: One event per venue per date
- Foreign key relationships maintain referential integrity
- ENUM types ensure data consistency (status, roles)
- Auto-increment primary keys for all tables

*[Visual: Show ERD diagram from Capstone ERD folder]*

---

## Slide 6: Key Features & Functionality

**User Features:**
- Calendar-based event discovery with month navigation
- Event filtering by date range and status
- Real-time ticket availability display
- User registration and authentication
- "My Tickets" view for purchased tickets

**Admin Features:**
- Full CRUD operations for events and venues
- Ticket inventory management
- Sales reporting and analytics
- User management capabilities

**API Features:**
- Pagination (default 31 items per page)
- Sorting by multiple fields (id, event_name, date, status)
- Consistent REST error handling
- Role-based endpoint access control

---

## Slide 7: Security Implementation (1 minute)

**JWT Authentication Flow:**
1. User logs in via `/api/auth/login` or registers via `/api/auth/register`
2. Server validates credentials and returns JWT token
3. Token included in `Authorization: Bearer <token>` header
4. Token expires after 1 hour (3,600,000 ms)

**Role-Based Access Control:**
- **PUBLIC:** No authentication required (event browsing)
- **USER:** Requires valid JWT with USER role (ticket purchases)
- **ADMIN:** Requires valid JWT with ADMIN role (event management)

**Security Measures:**
- Password encoding with BCrypt
- Input validation and sanitization
- SQL injection prevention via JPA
- CORS configuration for frontend-backend communication

---

## Slide 8: Testing & Verification

**Unit Testing (64 Test Cases):**
- **AuthServiceTest:** 24 tests covering authentication, registration, error handling
- **UserServiceTest:** 20 tests covering user retrieval, pagination, role normalization
- **TicketSoldServiceTest:** 20 tests covering sales filtering, edge cases, boundary testing

**Testing Methodology:**
- Mock-based unit testing with Mockito and JUnit 5
- Arrange-Act-Assert pattern for all tests
- Coverage of both success and failure paths
- Edge case testing (null inputs, boundary values)

**API Verification:**
- All 15+ API endpoints manually tested and verified
- Postman collection available for API testing
- 100% accurate API documentation (V4)

---

## Slide 9: Live Demo Preparation

**Current Status:**
- Application running locally with full functionality
- Database populated with sample data (20 venues, 8 users, multiple events)
- All API endpoints verified and working

**Demo Setup (Documented in README):**
1. **Database:** Run `SETUP-CapstoneDatabase.sql` to initialize MySQL
2. **Backend:** Start Spring Boot application on port 8080
3. **Frontend:** Run `npm run dev` for Vite development server
4. **Access:** Open browser to `http://localhost:5173`

---

## Slide 10: Live Demo Flow (3 minutes)

**Recommended Demo Order:**
1. **Home / Calendar View** - Show month navigation and event discovery
2. **Event Detail Modal** - Display full event information and "Buy Ticket" button
3. **Events List with Filtering** - Demonstrate pagination, sorting, and filtering
4. **User Authentication** - Login as regular user and show "My Tickets" view
5. **Admin Management** - Switch to admin account and demonstrate event management
6. **API Interaction** - Show browser DevTools to demonstrate REST API calls

**Key Interactions to Highlight:**
- Calendar date selection and event loading
- Real-time ticket availability updates
- Role-based UI changes (USER vs ADMIN views)
- API response consistency and error handling

*[Optional: Include recorded video backup in case of technical issues]*

---

## Slide 11: Technical Deep Dive (2 minutes)

**Backend Architecture:**
- **Controller Layer:** REST endpoints with `@RestController`
- **Service Layer:** Business logic and validation
- **Repository Layer:** Data access with Spring Data JPA
- **Security Layer:** JWT filters and role-based authorization

**Frontend Architecture:**
- **Component-Based:** Reusable React components (Cal.jsx, EventModal.jsx, etc.)
- **Context API:** AuthContext for global authentication state
- **Routing:** React Router for page navigation
- **API Integration:** Axios with interceptors for JWT handling

**Database Design Principles:**
- Normalization to 3rd normal form
- Appropriate indexing for performance
- Referential integrity through foreign keys
- Audit trail via tickets_sold table

---

## Slide 12: Lessons Learned (1 minute)

**Development Insights:**
1. **Iterative Shipping:** Shipping working features beats over-polishing one screen
2. **Scope Boundaries:** Clear scope prevents regressions and keeps progress measurable
3. **Accessibility Testing:** Responsive design requires testing across devices, not assumptions
4. **Separation of Concerns:** Isolating UI components, API services, and DB rules makes debugging safer
5. **AI Assistance:** AI tools 10x productivity, but generated code requires careful review and testing

**Technical Takeaways:**
- JWT implementation requires careful token management
- Database constraints prevent invalid data at the source
- Comprehensive testing catches edge cases early
- Documentation is crucial for maintenance and handoff

---

## Slide 13: AI Tools Usage Disclosure

**Responsible AI Integration:**
- AI tools were used to improve productivity during development
- All generated code was reviewed, tested, and integrated by me
- I maintained ownership of all design decisions and architecture
- AI assisted with: code generation, documentation, testing, and debugging

**Verification Process:**
- All AI-generated code was manually tested
- API documentation was verified through actual endpoint testing
- Security implementation was reviewed for best practices
- Final project represents my understanding and implementation

---

## Slide 14: Q&A

**Questions?**

**Contact Information:**
- [Your Email]
- [Your GitHub Profile]
- Project Repository: [Link if available]

**Thank You!**

---

## Appendix: Presentation Notes

### Slide 2 (Project Overview)
*Speak to:* The motivation behind choosing an event booking platform - solving a real-world problem of fragmented event information.

### Slide 4 (Architecture)
*Visual needed:* Architecture diagram showing three-tier separation. Use simple boxes and arrows.

### Slide 5 (Database Schema)
*Visual needed:* ERD diagram from Capstone ERD folder. Highlight key relationships.

### Slide 7 (Security)
*Demo suggestion:* Show DevTools network tab to demonstrate JWT token in headers.

### Slide 10 (Live Demo)
*Backup plan:* Have a pre-recorded video ready in case of technical issues.

### Slide 12 (Lessons Learned)
*Personal touch:* Share specific challenges you faced and how you overcame them.

### Timing Guidance:
- Total: 8-10 minutes
- Problem/Solution: 1 min
- Architecture: 2 min  
- Demo: 3 min
- Technical Deep Dive: 2 min
- Lessons Learned: 1 min
- Q&A: 1-2 min