# Airline Flight Operations - Frontend

A professional React + Vite frontend for the Airline Flight Operations Management System.

## Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Lucide React** - Icon library

## Prerequisites

- Node.js 18+ 
- npm or yarn
- Backend services running on http://localhost:8080

## Installation

```bash
# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Update API base URL if needed
# VITE_API_BASE_URL=http://localhost:8080
```

## Development

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── Button.jsx
│   ├── Card.jsx
│   ├── Header.jsx
│   ├── Modal.jsx
│   ├── Sidebar.jsx
│   └── Table.jsx
├── pages/              # Page components
│   ├── Dashboard.jsx
│   ├── Flights.jsx
│   ├── CrewMembers.jsx
│   └── FlightOperations.jsx
├── services/           # API service layer
│   ├── api.js
│   ├── flightService.js
│   ├── crewService.js
│   └── operationService.js
├── App.jsx             # Main app component
├── main.jsx            # Entry point
└── index.css           # Global styles
```

## Features

- **Dashboard** - Overview with statistics and quick actions
- **Flights Management** - CRUD operations for flights
- **Crew Management** - CRUD operations for crew members
- **Flight Operations** - CRUD operations for flight operations
- **Search & Filter** - Advanced search capabilities
- **Responsive Design** - Mobile-friendly interface
- **Error Handling** - User-friendly error messages
- **Loading States** - Loading indicators for async operations

## API Integration

The frontend connects to the backend API Gateway at `http://localhost:8080`:

- `/api/v1/flights` - Flight management
- `/api/v1/crew-members` - Crew member management
- `/api/v1/flight-operations` - Flight operation management

## Environment Variables

- `VITE_API_BASE_URL` - Backend API base URL (default: http://localhost:8080)

## Styling

- Custom CSS with component-scoped styles
- Professional airline operations dashboard theme
- Responsive design with mobile support
- Consistent color system and typography
