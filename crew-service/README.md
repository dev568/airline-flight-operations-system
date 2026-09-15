# Crew Service

The Crew Service is responsible for managing crew member records for the Airline Flight Operations Management System. It provides RESTful APIs for creating, retrieving, updating, and deleting crew member information.

## Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Cloud 2023.0.3
- Spring Data JPA
- PostgreSQL (runtime)
- H2 (testing)
- Flyway 9.22.3 (database migrations)
- Jakarta Bean Validation
- SpringDoc OpenAPI (Swagger)

## Server Configuration

- **Port:** 8082
- **Context Path:** /
- **Database:** PostgreSQL (crew_db)
- **Swagger UI:** http://localhost:8082/swagger-ui.html

## API Endpoints

### Base URL
```
http://localhost:8082/api/v1/crew-members
```

### Create Crew Member
```http
POST /api/v1/crew-members
Content-Type: application/json

{
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "PILOT",
  "status": "ACTIVE",
  "baseAirport": "JFK",
  "hireDate": "2020-01-01"
}
```

**Response:** 201 Created
```json
{
  "id": "uuid",
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "PILOT",
  "status": "ACTIVE",
  "baseAirport": "JFK",
  "hireDate": "2020-01-01",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### Get Crew Member by ID
```http
GET /api/v1/crew-members/{id}
```

**Response:** 200 OK
```json
{
  "id": "uuid",
  "employeeId": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "role": "PILOT",
  "status": "ACTIVE",
  "baseAirport": "JFK",
  "hireDate": "2020-01-01",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

### Get All Crew Members
```http
GET /api/v1/crew-members
```

**Response:** 200 OK
```json
[
  {
    "id": "uuid",
    "employeeId": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "PILOT",
    "status": "ACTIVE",
    "baseAirport": "JFK",
    "hireDate": "2020-01-01",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
]
```

### Search Crew Members
```http
GET /api/v1/crew-members/search?employeeId=EMP&role=PILOT&status=ACTIVE&baseAirport=JFK
```

**Query Parameters (all optional):**
- `employeeId` - Partial match (case-insensitive)
- `email` - Partial match (case-insensitive)
- `role` - Exact match (PILOT, COPILOT, CABIN_CREW, PURSER, FLIGHT_ENGINEER)
- `status` - Exact match (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED)
- `baseAirport` - Exact match (3-letter IATA code)

**Response:** 200 OK
```json
[
  {
    "id": "uuid",
    "employeeId": "EMP001",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "PILOT",
    "status": "ACTIVE",
    "baseAirport": "JFK",
    "hireDate": "2020-01-01",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
]
```

### Update Crew Member
```http
PUT /api/v1/crew-members/{id}
Content-Type: application/json

{
  "employeeId": "EMP002",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "role": "COPILOT",
  "status": "ACTIVE",
  "baseAirport": "LAX",
  "hireDate": "2020-01-01"
}
```

**Response:** 200 OK
```json
{
  "id": "uuid",
  "employeeId": "EMP002",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "role": "COPILOT",
  "status": "ACTIVE",
  "baseAirport": "LAX",
  "hireDate": "2020-01-01",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T01:00:00Z"
}
```

### Delete Crew Member
```http
DELETE /api/v1/crew-members/{id}
```

**Response:** 204 No Content

## Validation Rules

### CreateCrewMemberRequest
- `employeeId`: Required, 1-50 characters
- `firstName`: Required, max 100 characters
- `lastName`: Required, max 100 characters
- `email`: Required, valid email format, max 255 characters
- `role`: Required (PILOT, COPILOT, CABIN_CREW, PURSER, FLIGHT_ENGINEER)
- `status`: Required (ACTIVE, INACTIVE, ON_LEAVE, SUSPENDED)
- `baseAirport`: Required, 3-letter IATA code (uppercase)
- `hireDate`: Required, must not be in the future

### UpdateCrewMemberRequest
All fields are optional. Same validation rules apply when provided.

### Business Rules
- `employeeId` must be unique
- `email` must be unique

## Error Response Format

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/crew-members",
  "correlationId": "uuid",
  "validationErrors": {
    "employeeId": "Employee ID is required",
    "email": "Email must be valid"
  }
}
```

## HTTP Status Codes

- `201 Created` - Crew member created successfully
- `200 OK` - Successful GET or PUT
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation error or invalid input
- `404 Not Found` - Crew member not found
- `409 Conflict` - Duplicate employee ID or email
- `500 Internal Server Error` - Unexpected error

## Correlation ID

The service supports correlation IDs for request tracing:

- Include `X-Correlation-ID` header in requests
- If not provided, a UUID will be generated
- The correlation ID is returned in the response header
- Error responses include the correlation ID in the body

## Database Schema

### crew_members Table

| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | PRIMARY KEY |
| employee_id | VARCHAR(50) | NOT NULL, UNIQUE |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| role | VARCHAR(20) | NOT NULL |
| status | VARCHAR(20) | NOT NULL |
| base_airport | VARCHAR(3) | NOT NULL |
| hire_date | DATE | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

### Indexes
- idx_crew_members_employee_id
- idx_crew_members_email
- idx_crew_members_role
- idx_crew_members_status
- idx_crew_members_base_airport
- idx_crew_members_hire_date

### Check Constraints
- chk_crew_role: role IN ('PILOT', 'COPILOT', 'CABIN_CREW', 'PURSER', 'FLIGHT_ENGINEER')
- chk_crew_status: status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE', 'SUSPENDED')
- chk_base_airport: base_airport ~ '^[A-Z]{3}$'

## Local Setup

### Prerequisites
- Java 21
- Maven 3.9+
- PostgreSQL 12+

### Database Setup

1. Create the database:
```sql
CREATE DATABASE crew_db;
```

2. Create the user (if not exists):
```sql
CREATE USER airline_user WITH PASSWORD 'airline_password';
GRANT ALL PRIVILEGES ON DATABASE crew_db TO airline_user;
```

### Environment Variables (Optional)

Set the following environment variables to override defaults:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/crew_db
export DB_USERNAME=airline_user
export DB_PASSWORD=airline_password
```

### Build and Run

```bash
# Build
./mvnw.cmd clean install

# Run
./mvnw.cmd spring-boot:run -pl crew-service
```

Or use the Maven wrapper:
```bash
cd crew-service
../mvnw.cmd spring-boot:run
```

### Run Tests

```bash
# Test crew-service only
./mvnw.cmd clean test -pl crew-service

# Test entire project
./mvnw.cmd clean test
```

## Package Structure

```
com.airline.crew/
├── controller/
│   └── CrewMemberController.java
├── service/
│   └── CrewMemberService.java
├── repository/
│   └── CrewMemberRepository.java
├── entity/
│   └── CrewMember.java
├── dto/
│   ├── CreateCrewMemberRequest.java
│   ├── UpdateCrewMemberRequest.java
│   └── CrewMemberResponse.java
├── model/
│   ├── CrewRole.java
│   └── CrewStatus.java
├── exception/
│   ├── CrewMemberNotFoundException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
└── filter/
    └── CorrelationIdFilter.java
```

## Current Status

**Milestone 3: Crew Service Foundation** - ✅ Complete

- ✅ Domain model (CrewMember entity, CrewRole, CrewStatus enums)
- ✅ Repository layer with query methods
- ✅ DTOs (CreateCrewMemberRequest, UpdateCrewMemberRequest, CrewMemberResponse)
- ✅ Service layer with CRUD operations
- ✅ REST controller with full CRUD and search endpoints
- ✅ Global exception handling with correlation ID support
- ✅ Flyway migration for crew_members table
- ✅ Unit and integration tests
- ✅ Documentation

## Next Steps

Future milestones may include:
- Inter-service communication with flight-service
- Authentication and authorization
- Advanced search and filtering
- Crew scheduling integration
- Performance optimization
