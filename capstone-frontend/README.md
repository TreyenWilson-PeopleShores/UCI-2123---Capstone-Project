# Event Booking Platform - React Frontend

## Overview
React frontend for the Event Booking Platform capstone project. Built with Vite for fast development and React Router for navigation.

## Technology Stack
- **React**: 19.2.4
- **Vite**: 8.0.1
- **React Router DOM**: 7.14.1
- **Fetch API**: For API communication (custom httpService wrapper)

## Features
- Calendar-based event discovery
- User authentication with JWT
- Role-based UI (USER vs ADMIN)
- Real-time ticket availability
- Responsive design

## Development
```bash
npm install
npm run dev
```

The application will run on `http://localhost:5173`

## API Integration
Connects to Spring Boot backend at `http://localhost:8080`
See API-Documentation-V4.md for complete endpoint documentation.
