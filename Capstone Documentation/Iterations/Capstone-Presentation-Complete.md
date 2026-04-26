# Event Booking Platform - Complete Presentation Slides

## Slide 1: Title Slide
**Event Booking Platform**  
UCI 2123 Capstone Project

**Pitch:** A full-stack app that centralizes event discovery and ticket tracking, letting users browse events by date and view ticket availability while admins manage listings through a REST API.

- The core user experience is: discover events quickly, view details, and proceed toward booking.
- The core admin experience is: manage event lifecycle and data consistency through rules (immutability where appropriate).

---

## Slide 2: Problem & Solution (1 minute)

**Problem (Before):**
- Event information is often fragmented (spreadsheets, posts, multiple tools)
- Users struggle to find events by date/status and understand availability
- No centralized platform for event discovery and management

**Solution (After):**
- Centralized event catalog with calendar discovery
- REST API supports filtering (date range, status), pagination, and consistent error handling
- Ticketing represented as event inventory and completed sales records for reporting/auditing

---

## Slide 3: Architecture Overview (2 minutes)

**Three-Tier Architecture:**
```
[ React Frontend ] - Component-based UI with Vite
        |
    HTTP / REST API
        |
[ Spring Boot API ] - Controller/service/repository layers
        |
    JPA / Hibernate
        |
[ MySQL Database ] - Tables + constraints
```

**Frontend:** Calls the API for event data and renders calendar/list experience
- Example: `http://localhost:8080/api/events`
- React components: Cal.jsx (calendar), EventModal.jsx, UpcomingEvents.jsx
- Pages: EventsPage.jsx, Login.jsx, MyTickets.jsx, AdminManager.jsx

**Backend:** Enforces business rules, validation, pagination, and consistent REST patterns
- Spring Boot (as configured in pom.xml) with Spring Security JWT
- 29 verified API endpoints with role-based access (documented in API-Documentation-V4.md)
- Comprehensive error handling and input validation

**Database:** Persists events, venues, users, tickets, and ticket sales for reporting
- 5 normalized tables with referential integrity
- Sample data: 20 venues, 8 users, multiple events
- Constraints: Unique event per venue per date, foreign key relationships

*[Visual: Show ERD diagram - explain relationships between tables]*

---

## Slide 4: Live Demo Preparation (Transition)

**Current Status:**
- App is running locally with full functionality
- All API endpoints verified and working (100% accuracy)
- Database populated with realistic sample data

**Setup Steps (Documented in README):**
1. **Database:** Run `SETUP-CapstoneDatabase.sql` to initialize MySQL
2. **Backend:** Start Spring Boot on port 8080 (`mvn spring-boot:run`)
3. **Frontend:** Run Vite dev server on port 5173 (`npm run dev`)
4. **Access:** Open `http://localhost:5173` in browser

**Demo Credentials:**
- User: `treyen` / `password123` (USER role)
- Admin: `admin` / `adminpass` (ADMIN role)

---

## Slide 5: Live Demo (3 minutes)

**Demo Flow (Recommended Order):**
1. **Home / Calendar Route** - Show month navigation and event discovery
2. **Event Detail View** - Display modal with full event name/date/status and "Buy Ticket" button
3. **Events List Route** - Demonstrate pagination, sorting, and filtering working from API
4. **My Tickets Route** - Show user-facing ticket history/ownership concept
5. **Admin Manager** - Show admin management interface and events with sold-out tickets

**Key Interactions to Highlight:**
- Real-time API calls visible in browser DevTools
- JWT token handling in request headers
- Role-based UI changes (USER vs ADMIN views)
- Database constraint enforcement (unique venue/date)

*[Optional: Include recorded video backup showing Event Booking Application]*

---

## Slide 6: Technical Deep Dive (2 minutes)

**Backend Technical Stack:**
- **Spring Boot 3.4+** with Maven build system
- **Spring Security** with JWT authentication
- **Spring Data JPA** for database operations
- **MySQL 8.0+** relational database
- **Java 25** (development environment)

**API Design Principles:**
- RESTful endpoints with consistent naming (`/api/events`, `/api/auth/login`)
- Pagination support (default 31 items per page)
- Sorting by multiple fields (id, event_name, date, status, total_spots, venue_id)
- Comprehensive error handling with appropriate HTTP status codes

**Database Schema Details:**
- **events:** `id, event_name, date, status, total_spots, venue_id`
- **venues:** `id, venue_name, location, total_capacity`
- **tickets:** `id, event_id, price, total_quantity, sold`
- **users:** `id, username, password, role`
- **tickets_sold:** `id, user_id, ticket_id, date_sold`

**Business Rules Enforced:**
- One event per venue per date (unique constraint)
- Ticket sales cannot exceed available quantity
- Event status transitions (SCHEDULED → COMPLETED/CANCELLED)
- Role-based access control at API level

---

## Slide 7: Security & Testing (1 minute)

**JWT Authentication Implementation:**
- Token-based authentication with 1-hour expiration
- Role-based authorization (ADMIN/USER/PUBLIC)
- Secure password storage with encoding
- CORS configuration for frontend-backend communication

**Testing & Verification:**
- **42 Unit Tests** covering AuthService, UserService, TicketSoldService
- **Mock-based testing** with Mockito and JUnit 5
- **API Verification:** All endpoints manually tested and documented
- **Code Coverage:** JaCoCo integration for test coverage reporting

**Security Measures:**
- Input validation and sanitization at service layer
- SQL injection prevention via JPA parameterized queries
- Password encoding with BCryptPasswordEncoder
- HTTPS-ready configuration (for production deployment)

**AI Tools Usage Disclosure:**
AI tools were used responsibly to improve productivity; I reviewed all generated code and maintained ownership of design decisions.

---

## Slide 8: Lessons Learned (1 minute)

**Development Insights:**
- **Shipping iteratively** beats over-polishing one screen
- **Clear scope boundaries** prevent regressions and keep progress measurable
- **Accessibility and responsiveness** require testing across constraints, not assumptions
- **Separating concerns** (UI components vs API services vs DB rules) makes debugging and enhancement safer

**Technical Takeaways:**
- JWT implementation requires careful token management and expiration handling
- Database constraints are more reliable than application-level validation
- Comprehensive testing catches edge cases before they reach production
- Documentation is not optional - it's essential for maintenance

**AI Integration Learnings:**
- AI really does 10x productivity for boilerplate code and documentation
- Generated code must be thoroughly reviewed and tested - errors do occur
- AI excels at suggesting patterns but human judgment is needed for architecture
- The combination of AI assistance + human oversight produces the best results

---

## Slide 9: Q&A

**Questions?**

**Project Resources:**
- Complete API Documentation (V4 - 100% verified)
- Database schema and setup scripts
- Source code: Spring Boot backend + React frontend
- Testing reports and verification documents

**Thank You!**

---

## Presentation Notes & Timing

### Total Time: 8-10 minutes

**Suggested Timing:**
- Slide 1-2 (Introduction & Problem): 1 minute
- Slide 3 (Architecture): 2 minutes
- Slide 4 (Demo Prep): 30 seconds (transition)
- Slide 5 (Live Demo): 3 minutes
- Slide 6 (Technical Deep Dive): 2 minutes
- Slide 7 (Security & Testing): 1 minute
- Slide 8 (Lessons Learned): 1 minute
- Slide 9 (Q&A): 1-2 minutes

**Visuals Needed:**
1. Architecture diagram (simple boxes and arrows)
2. ERD diagram from Capstone ERD folder
3. Screenshot of application interface
4. API call example from DevTools

**Demo Backup Plan:**
- Have a pre-recorded video ready
- Screenshots of key flows as fallback
- Test all technology before presentation

**Key Messages to Emphasize:**
1. This solves a real problem (fragmented event information)
2. The architecture is production-ready and scalable
3. Security was implemented from the ground up
4. Everything has been tested and verified
5. Lessons learned are applicable to real-world development