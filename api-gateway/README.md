# API Gateway

The API Gateway serves as the single entry point for all client requests to the Airline Flight Operations Management System. It routes requests to the appropriate backend services and provides correlation ID support for distributed tracing.

## Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Cloud 2023.0.3
- Spring Cloud Gateway

## Server Configuration

- **Default Port**: 8080
- **Health Endpoint**: http://localhost:8080/actuator/health

## Environment Variables

The gateway uses environment variables to configure service URLs with sensible local defaults:

| Variable | Description | Default |
|----------|-------------|---------|
| `FLIGHT_SERVICE_URL` | Flight Service URL | `http://localhost:8081` |
| `CREW_SERVICE_URL` | Crew Service URL | `http://localhost:8082` |
| `OPERATIONS_SERVICE_URL` | Operations Service URL | `http://localhost:8083` |

## Route Mappings

| Path Pattern | Target Service |
|--------------|----------------|
| `/api/v1/flights/**` | Flight Service (8081) |
| `/api/v1/crew-members/**` | Crew Service (8082) |
| `/api/v1/flight-operations/**` | Operations Service (8083) |

## Running Locally

### Prerequisites

- Java 21 installed
- Maven installed
- Backend services running (flight-service, crew-service, operations-service)

### Start the Gateway

```bash
# From the project root
./mvnw.cmd spring-boot:run -pl api-gateway
```

### Start with Custom Service URLs

```bash
# Windows PowerShell
$env:FLIGHT_SERVICE_URL="http://flight-service.example.com"
$env:CREW_SERVICE_URL="http://crew-service.example.com"
$env:OPERATIONS_SERVICE_URL="http://operations-service.example.com"
./mvnw.cmd spring-boot:run -pl api-gateway

# Linux/Mac
export FLIGHT_SERVICE_URL="http://flight-service.example.com"
export CREW_SERVICE_URL="http://crew-service.example.com"
export OPERATIONS_SERVICE_URL="http://operations-service.example.com"
./mvnw spring-boot:run -pl api-gateway
```

## Example curl Commands

### Get All Flights (via Gateway)
```bash
curl http://localhost:8080/api/v1/flights
```

### Get Flight by ID (via Gateway)
```bash
curl http://localhost:8080/api/v1/flights/{id}
```

### Get All Crew Members (via Gateway)
```bash
curl http://localhost:8080/api/v1/crew-members
```

### Get All Flight Operations (via Gateway)
```bash
curl http://localhost:8080/api/v1/flight-operations
```

### With Correlation ID
```bash
curl -H "X-Correlation-ID: 550e8400-e29b-41d4-a716-446655440000" \
  http://localhost:8080/api/v1/flights
```

## Correlation ID Behavior

The gateway automatically manages correlation IDs for distributed tracing:

- **Incoming Request**: If the `X-Correlation-ID` header is present, it is preserved
- **Missing Header**: If the header is absent or blank, a new UUID is generated
- **Propagation**: The correlation ID is added to all downstream service requests
- **Response**: The correlation ID is included in the gateway response headers

This enables end-to-end request tracing across all services without requiring client-side correlation ID management.

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Current Status

- ✅ Basic routing configuration
- ✅ Environment variable support for service URLs
- ✅ Correlation ID propagation
- ✅ Health endpoint
- ⏳ Authentication (not yet implemented)
- ⏳ Service-to-service business logic (not yet implemented)
- ⏳ Resilience4j circuit breakers (not yet implemented)

## Architecture

The gateway follows a simple routing pattern:

```
Client → API Gateway (8080) → Backend Services
                              ├─ Flight Service (8081)
                              ├─ Crew Service (8082)
                              └─ Operations Service (8083)
```

## Notes

- The gateway does not contain business logic
- All business operations are handled by the respective backend services
- Authentication and authorization will be added in a future milestone
- The gateway is designed to be stateless and horizontally scalable
