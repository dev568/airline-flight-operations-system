# Airline Flight Operations Management System

A professional, production-oriented backend system for managing airline flight operations, demonstrating modern Java enterprise development practices.

## Purpose

This project demonstrates practical experience in:
- Java 21
- Spring Boot 3.x
- Spring Data JPA
- Hibernate
- REST API development
- Microservices architecture
- PostgreSQL
- Apache Camel
- Enterprise Integration Patterns
- Docker
- Kubernetes
- OpenShift concepts
- Testcontainers
- Integration testing

## Architecture

The system follows a clean microservices-oriented architecture with four services:

- **api-gateway** - Single entry point for clients, routes requests to backend services
- **flight-service** - Airport and flight management
- **crew-service** - Crew member management and assignments
- **operations-service** - Operational events and integration workflows

Each service owns its data and communicates via REST APIs.

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Cloud 2023.0.3**
- **Spring Data JPA**
- **PostgreSQL**
- **Apache Camel 4.4.0**
- **Enterprise Integration Patterns**
- **Docker**
- **Kubernetes**
- **OpenShift concepts**
- **Testcontainers**
- **Integration testing**

## Prerequisites

- **Java 21** (required)
- **Maven 3.9+** (or use Maven Wrapper)
- **Docker 20+** (for local development and Testcontainers)
- **PostgreSQL 14+** (or use Docker Compose)

## Quick Start

### Using Maven Wrapper

```bash
# Build the entire project
./mvnw clean install

# Run a specific service
./mvnw spring-boot:run -pl flight-service
```

### Using Docker Compose

```bash
# Start PostgreSQL and all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Project Structure

```
airline-flight-operations-system/
├── api-gateway/          # API Gateway service
├── flight-service/       # Flight and airport management
├── crew-service/         # Crew management
├── operations-service/   # Operational events and Camel integration
├── docs/                 # Documentation
└── k8s/                  # Kubernetes manifests
```

## Documentation

- [Requirements](docs/requirements.md)
- [Architecture](docs/architecture.md)
- [Database Design](docs/database-design.md)
- [API Design](docs/api-design.md)
- [Testing Strategy](docs/testing-strategy.md)
- [Deployment](docs/deployment.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Security](docs/security.md)
- [Sequence Diagrams](docs/sequence-diagrams.md)

## Running Tests

```bash
# Run all tests
./mvnw test

# Run tests for a specific module
./mvnw test -pl flight-service
```

## API Documentation

Each service exposes OpenAPI/Swagger documentation:

- API Gateway: http://localhost:8080/swagger-ui.html
- Flight Service: http://localhost:8081/swagger-ui.html
- Crew Service: http://localhost:8082/swagger-ui.html
- Operations Service: http://localhost:8083/swagger-ui.html

## Health Checks

Spring Boot Actuator endpoints are available:

- `/actuator/health` - Health status
- `/actuator/readiness` - Readiness probe
- `/actuator/liveness` - Liveness probe

## Development

### Database Setup

The project uses Docker Compose for local development with a single PostgreSQL container containing three logical databases:
- `flight_db`
- `crew_db`
- `operations_db`

### Configuration

Environment-specific configuration is managed through Spring profiles:
- `application.yml` - Default configuration
- `application-dev.yml` - Development overrides
- `application-test.yml` - Test configuration

### Code Style

- Clean code principles
- Small, focused methods
- Meaningful class and variable names
- DTOs for API layer (no entity exposure)
- Centralized exception handling
- Bean validation for requests

## Security Considerations

This project implements security-aware practices:
- Input validation
- Safe error responses (no stack traces)
- No secrets in Git
- Environment-based configuration
- Audit logging
- Correlation IDs
- Health and readiness checks

Authentication and authorization are deferred to a later milestone.

## License

This is a demonstration project for portfolio and interview purposes.

## Status

**Current Milestone:** 6 - Configuration and Documentation

**Completed Services:**
- ✅ Flight Service (port 8081)
- ✅ Crew Service (port 8082)
- ✅ Operations Service (port 8083)
- ✅ API Gateway (port 8080)

**Test Status:**
- Total tests: 110
- All tests passing: ✅
- Verified locally: ✅

**Current Limitations:**
- Docker/PostgreSQL not available in local environment
- Docker Compose has not been executed because Docker is unavailable
- Current Docker Compose defines PostgreSQL only
- Application containers are not currently defined in docker-compose.yml
- Kubernetes deployment manifests pending
- Integration tests pending
- Not production ready

See [docs/architecture.md](docs/architecture.md) for implementation roadmap.
