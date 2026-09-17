# Airline Flight Operations Management System

A complete full-stack airline flight operations management system with a professional React frontend and Spring Boot microservices backend.

## Business Purpose

This system provides a comprehensive interface for managing:
- Flight schedules and status tracking
- Crew member information and management
- Flight operation lifecycle with status transition validation
- Real-time dashboard with operational metrics
- Professional airline operations UI

## Architecture

The system follows a microservices-oriented architecture with:

**Backend Services:**
- **api-gateway** (port 8080) - Single entry point, routes requests to backend services
- **flight-service** (port 8081) - Flight and airport management
- **crew-service** (port 8082) - Crew member management
- **operations-service** (port 8083) - Flight operation lifecycle with status transition validation

**Frontend:**
- **React + Vite** (port 5173) - Professional dashboard UI
- **API Integration** - Communicates through API Gateway at port 8080

**Database:**
- **PostgreSQL 16** - Production database (port 5432)
- **H2** - In-memory database for testing

Each service owns its data and communicates via REST APIs through the API Gateway.

## Technology Stack

**Backend:**
- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Cloud 2023.0.3**
- **Spring Data JPA** with Hibernate
- **PostgreSQL 16**
- **Flyway 9.22.3** for database migrations
- **Apache Camel 4.4.0**
- **H2** for testing
- **SpringDoc OpenAPI** for API documentation
- **Docker** for containerization
- **Kubernetes** manifests for deployment

**Frontend:**
- **React 18**
- **Vite** - Build tool and dev server
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Lucide React** - Icon library
- **Tailwind-style CSS** - Professional styling

## Prerequisites

- **Java 21** (required for backend)
- **Node.js 18+** (required for frontend)
- **Maven 3.9+** (or use Maven Wrapper)
- **Docker** (required for PostgreSQL)
- **npm** (for frontend dependencies)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd airline-flight-operations-system
```

### 2. Start PostgreSQL

```bash
# Set database password
export DB_PASSWORD=your_secure_password

# Start PostgreSQL with Docker Compose
docker-compose up -d

# Verify PostgreSQL is running
docker ps
```

### 3. Start Backend Services

In separate terminals (or using background processes):

```bash
# Terminal 1: Start API Gateway
.\mvnw.cmd spring-boot:run -pl api-gateway

# Terminal 2: Start Flight Service
.\mvnw.cmd spring-boot:run -pl flight-service

# Terminal 3: Start Crew Service
.\mvnw.cmd spring-boot:run -pl crew-service

# Terminal 4: Start Operations Service
.\mvnw.cmd spring-boot:run -pl operations-service
```

### 4. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at http://localhost:5173 (or the port shown in the terminal).

### 5. Access the Application

- **Frontend Dashboard:** http://localhost:5173
- **API Gateway:** http://localhost:8080
- **API Documentation:** http://localhost:8080/swagger-ui.html

## Project Structure

```
airline-flight-operations-system/
├── api-gateway/          # API Gateway service
├── flight-service/       # Flight and airport management
├── crew-service/         # Crew management
├── operations-service/   # Flight operation lifecycle
├── frontend/             # React + Vite frontend
│   ├── src/
│   │   ├── components/  # Reusable UI components
│   │   ├── pages/       # Page components
│   │   ├── services/    # API service layer
│   │   └── App.jsx      # Main app component
│   ├── vite.config.js   # Vite configuration
│   └── package.json
├── docs/                 # Documentation
├── k8s/                  # Kubernetes manifests
├── docker-compose.yml    # PostgreSQL configuration
└── init-db.sql          # Database initialization
```

## API Endpoints

### API Gateway (http://localhost:8080)
- `GET /api/v1/flights/**` → Flight Service
- `GET /api/v1/crew-members/**` → Crew Service
- `GET /api/v1/flight-operations/**` → Operations Service
- `GET /actuator/health` - Health check

### Flight Service (http://localhost:8081)
- `POST /api/v1/flights` - Create flight
- `GET /api/v1/flights` - List all flights
- `GET /api/v1/flights/{id}` - Get flight by ID
- `PUT /api/v1/flights/{id}` - Update flight
- `DELETE /api/v1/flights/{id}` - Delete flight
- `GET /api/v1/flights/search` - Search flights
- `GET /actuator/health` - Health check

### Crew Service (http://localhost:8082)
- `POST /api/v1/crew-members` - Create crew member
- `GET /api/v1/crew-members` - List all crew members
- `GET /api/v1/crew-members/{id}` - Get crew member by ID
- `PUT /api/v1/crew-members/{id}` - Update crew member
- `DELETE /api/v1/crew-members/{id}` - Delete crew member
- `GET /api/v1/crew-members/search` - Search crew members
- `GET /actuator/health` - Health check

### Operations Service (http://localhost:8083)
- `POST /api/v1/flight-operations` - Create flight operation
- `GET /api/v1/flight-operations` - List all operations
- `GET /api/v1/flight-operations/{id}` - Get operation by ID
- `PUT /api/v1/flight-operations/{id}` - Update operation (with status transition validation)
- `DELETE /api/v1/flight-operations/{id}` - Delete operation
- `GET /api/v1/flight-operations/search` - Search operations
- `GET /actuator/health` - Health check

## Frontend Features

### Dashboard
- Real-time statistics for flights, crew members, and operations
- Quick actions for creating records
- Recent activity feed
- System health monitoring

### Flights Management
- Create, read, update, delete flights
- Search by flight number, status, departure/arrival airports
- Status badges with color coding
- Real-time table updates

### Crew Members Management
- Create, read, update, delete crew members
- Search by employee ID, email, role, status, base airport
- Role badges (PILOT, COPILOT, CABIN_CREW, PURSER, FLIGHT_ENGINEER)
- Status badges (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED)

### Flight Operations Management
- Create, read, update, delete flight operations
- Search by operation reference, status, airport code
- Flight selection dropdown for operation creation
- Status transition validation (enforced by backend)
- Operation reference badges

## Flight Operation Status Transitions

The operations service enforces valid status transitions:

- **PLANNED** → CHECK_IN_OPEN, DELAYED, CANCELLED
- **CHECK_IN_OPEN** → BOARDING, DELAYED, CANCELLED
- **BOARDING** → DEPARTED, DELAYED, CANCELLED
- **DEPARTED** → ARRIVED, DELAYED
- **ARRIVED** → COMPLETED
- **DELAYED** → Any operational state, CANCELLED
- **CANCELLED** → Terminal state
- **COMPLETED** → Terminal state

Invalid transitions return HTTP 400 with a descriptive error message.

## Database Schema

### Flights Table (flight_db)
- `id` (UUID, primary key)
- `flight_number` (VARCHAR, format: XX123 or XX1234)
- `departure_airport` (VARCHAR, 3-letter IATA code)
- `arrival_airport` (VARCHAR, 3-letter IATA code)
- `scheduled_departure` (TIMESTAMP)
- `scheduled_arrival` (TIMESTAMP)
- `status` (ENUM: SCHEDULED, BOARDING, DEPARTED, ARRIVED, DELAYED, CANCELLED)
- `aircraft_type` (VARCHAR, optional)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Crew Members Table (crew_db)
- `id` (UUID, primary key)
- `employee_id` (VARCHAR, 1-50 characters)
- `first_name` (VARCHAR, max 100 characters)
- `last_name` (VARCHAR, max 100 characters)
- `email` (VARCHAR, max 255 characters)
- `role` (ENUM: PILOT, COPILOT, CABIN_CREW, PURSER, FLIGHT_ENGINEER)
- `status` (ENUM: ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED)
- `base_airport` (VARCHAR, 3-letter IATA code)
- `hire_date` (DATE)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

### Flight Operations Table (operations_db)
- `id` (UUID, primary key)
- `flight_id` (UUID, foreign key to flights)
- `operation_reference` (VARCHAR, 1-50 characters)
- `status` (ENUM: PLANNED, CHECK_IN_OPEN, BUILDING, DEPARTED, ARRIVED, DELAYED, CANCELLED, COMPLETED)
- `scheduled_at` (TIMESTAMP)
- `actual_at` (TIMESTAMP, optional)
- `airport_code` (VARCHAR, 3-letter IATA code)
- `remarks` (VARCHAR, max 500 characters, optional)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

## Health Endpoints

Spring Boot Actuator health endpoints are available on all services:

- API Gateway: `http://localhost:8080/actuator/health`
- Flight Service: `http://localhost:8081/actuator/health`
- Crew Service: `http://localhost:8082/actuator/health`
- Operations Service: `http://localhost:8083/actuator/health`

## Correlation ID Handling

The API Gateway generates a correlation ID for each request and propagates it to backend services via the `X-Correlation-ID` header. This ID is included in error responses for tracing.

## Validation and Error Handling

- Bean validation on all request DTOs
- Cross-field validation (e.g., arrival time must be after departure time)
- Centralized global exception handling
- Correlation ID inclusion in error responses
- Safe error messages (no stack traces exposed)
- Status transition validation in operations service

## API Documentation

Each service exposes OpenAPI/Swagger documentation:

- API Gateway: http://localhost:8080/swagger-ui.html
- Flight Service: http://localhost:8081/swagger-ui.html
- Crew Service: http://localhost:8082/swagger-ui.html
- Operations Service: http://localhost:8083/swagger-ui.html

## Frontend Development

```bash
cd frontend
npm install
npm run dev
npm run build
npm run preview
```

The frontend uses a Vite proxy to route API requests to the backend API Gateway.

## Docker

### Build Images

```bash
# Build all images from repository root
docker build -t airline-api-gateway -f api-gateway/Dockerfile .
docker build -t airline-flight-service -f flight-service/Dockerfile .
docker build -t airline-crew-service -f crew-service/Dockerfile .
docker build -t airline-operations-service -f operations-service/Dockerfile .
```

### Docker Compose

```bash
# Start PostgreSQL with required DB_PASSWORD environment variable
export DB_PASSWORD=your_password
docker-compose up -d

# Stop PostgreSQL
docker-compose down
```

The docker-compose.yml creates three logical databases:
- `flight_db`
- `crew_db`
- `operations_db`

Application containers are not currently defined in docker-compose.yml. Services can be run individually using Docker images or Maven.

## Kubernetes Deployment

Kubernetes manifests are available in the `k8s/` directory:

- namespace.yaml - Kubernetes namespace
- configmap.yaml - Configuration for database URLs and service routing
- secret.yaml - Secret for database password (placeholder)
- api-gateway-deployment.yaml + service.yaml
- flight-service-deployment.yaml + service.yaml
- crew-service-deployment.yaml + service.yaml
- operations-service-deployment.yaml + service.yaml

**Note:** These manifests have not been tested against a real Kubernetes cluster. See `k8s/README.md` for deployment instructions.

## Documentation

- [Architecture](docs/architecture.md)
- [Deployment](docs/deployment.md)

## Test Status

- **Total tests:** 116
- **API Gateway:** 6 tests
- **Flight Service:** 28 tests
- **Crew Service:** 39 tests
- **Operations Service:** 43 tests
- **All tests passing:** ✅

## Current Validation Status

**Completed:**
- ✅ Maven multi-module build
- ✅ All unit tests passing (116 tests)
- ✅ Executable JARs generated for all services
- ✅ Docker images built successfully
- ✅ Docker Compose PostgreSQL configuration validated
- ✅ Flyway migrations configured
- ✅ Environment variable support for database configuration
- ✅ Flight operation status transition validation
- ✅ Kubernetes manifests created
- ✅ Health endpoints configured
- ✅ Correlation ID handling
- ✅ React + Vite frontend implemented
- ✅ Frontend API Gateway integration
- ✅ Full CRUD workflows tested via API
- ✅ Database persistence verified

**Not Validated:**
- ❌ Kubernetes cluster deployment (manifests created but not tested)
- ❌ End-to-end frontend browser testing through API Gateway

**Current Limitations:**
- Docker Compose defines PostgreSQL only (application containers not included)
- Kubernetes manifests have not been tested against a real cluster
- No authentication or authorization
- No crew assignment scheduling
- No passenger booking functionality
- Not production ready

## License

This is a demonstration project for portfolio and interview purposes.
