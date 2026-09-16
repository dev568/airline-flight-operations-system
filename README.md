# Airline Flight Operations Management System

A backend system for managing airline flight operations, demonstrating modern Java enterprise development practices.

## Business Purpose

This system provides APIs for managing:
- Flight schedules and status
- Crew member information
- Flight operation lifecycle with status transition validation
- Operational events and workflows

## Architecture

The system follows a microservices-oriented architecture with four services:

- **api-gateway** (port 8080) - Single entry point for clients, routes requests to backend services
- **flight-service** (port 8081) - Flight and airport management
- **crew-service** (port 8082) - Crew member management
- **operations-service** (port 8083) - Flight operation lifecycle with status transition validation

Each service owns its data and communicates via REST APIs.

## Technology Stack

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

## Prerequisites

- **Java 21** (required)
- **Maven 3.9+** (or use Maven Wrapper)
- **Docker** (optional, for container builds)
- **PostgreSQL** (optional, for integration testing)

## Quick Start

### Build and Test

```bash
# Build the entire project
mvn clean package

# Run all tests
mvn clean test

# Run tests for a specific module
mvn test -pl flight-service
```

### Run Services Locally

```bash
# Run a specific service
mvn spring-boot:run -pl flight-service

# Run API Gateway
mvn spring-boot:run -pl api-gateway
```

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

The current Docker Compose configuration defines PostgreSQL only:

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

## Environment Variables

All services support the following environment variables:

- `DB_URL` - Database connection URL (default: jdbc:postgresql://localhost:5432/<service_db>)
- `DB_USERNAME` - Database username (default: airline_user)
- `DB_PASSWORD` - Database password (default: empty, must be set for PostgreSQL)

The API Gateway also supports:
- `FLIGHT_SERVICE_URL` - Flight service URL (default: http://localhost:8081)
- `CREW_SERVICE_URL` - Crew service URL (default: http://localhost:8082)
- `OPERATIONS_SERVICE_URL` - Operations service URL (default: http://localhost:8083)

## Project Structure

```
airline-flight-operations-system/
├── api-gateway/          # API Gateway service
├── flight-service/       # Flight and airport management
├── crew-service/         # Crew management
├── operations-service/   # Flight operation lifecycle
├── docs/                 # Documentation
├── k8s/                  # Kubernetes manifests
├── docker-compose.yml    # PostgreSQL configuration
└── init-db.sql          # Database initialization
```

## API Endpoints

### API Gateway
- `GET /api/v1/flights/**` → Flight Service
- `GET /api/v1/crew-members/**` → Crew Service
- `GET /api/v1/flight-operations/**` → Operations Service

### Flight Service
- `POST /api/v1/flights` - Create flight
- `GET /api/v1/flights` - List all flights
- `GET /api/v1/flights/{id}` - Get flight by ID
- `PUT /api/v1/flights/{id}` - Update flight
- `DELETE /api/v1/flights/{id}` - Delete flight
- `GET /api/v1/flights/search` - Search flights

### Crew Service
- `POST /api/v1/crew-members` - Create crew member
- `GET /api/v1/crew-members` - List all crew members
- `GET /api/v1/crew-members/{id}` - Get crew member by ID
- `PUT /api/v1/crew-members/{id}` - Update crew member
- `DELETE /api/v1/crew-members/{id}` - Delete crew member
- `GET /api/v1/crew-members/search` - Search crew members

### Operations Service
- `POST /api/v1/flight-operations` - Create flight operation
- `GET /api/v1/flight-operations` - List all operations
- `GET /api/v1/flight-operations/{id}` - Get operation by ID
- `PUT /api/v1/flight-operations/{id}` - Update operation (with status transition validation)
- `DELETE /api/v1/flight-operations/{id}` - Delete operation
- `GET /api/v1/flight-operations/search` - Search operations

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

## API Documentation

Each service exposes OpenAPI/Swagger documentation:

- API Gateway: http://localhost:8080/swagger-ui.html
- Flight Service: http://localhost:8081/swagger-ui.html
- Crew Service: http://localhost:8082/swagger-ui.html
- Operations Service: http://localhost:8083/swagger-ui.html

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

**Not Validated:**
- ❌ Kubernetes cluster deployment (manifests created but not tested)
- ❌ Full integration testing with PostgreSQL
- ❌ End-to-end API testing through gateway

**Current Limitations:**
- Docker Compose defines PostgreSQL only (application containers not included)
- Kubernetes manifests have not been tested against a real cluster
- No authentication or authorization
- No crew assignment scheduling
- No passenger booking functionality
- Not production ready

## License

This is a demonstration project for portfolio and interview purposes.
