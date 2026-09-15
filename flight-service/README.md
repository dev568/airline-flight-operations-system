# Flight Service

## Overview

The Flight Service is responsible for managing flight records in the Airline Flight Operations Management System. It provides RESTful APIs for creating, reading, updating, deleting, and searching flight information.

## Technology Stack

- Java 21
- Spring Boot 3.3.5
- Spring Data JPA
- PostgreSQL
- Flyway for database migrations
- Jakarta Bean Validation
- SpringDoc OpenAPI (Swagger)

## API Endpoints

Base URL: `http://localhost:8081/api/v1/flights`

### Create Flight

**Endpoint:** `POST /api/v1/flights`

**Request Body:**
```json
{
  "flightNumber": "AA123",
  "departureAirport": "JFK",
  "arrivalAirport": "LAX",
  "scheduledDeparture": "2024-12-15T10:00:00",
  "scheduledArrival": "2024-12-15T13:00:00",
  "status": "SCHEDULED",
  "aircraftType": "Boeing 737"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "flightNumber": "AA123",
  "departureAirport": "JFK",
  "arrivalAirport": "LAX",
  "scheduledDeparture": "2024-12-15T10:00:00",
  "scheduledArrival": "2024-12-15T13:00:00",
  "status": "SCHEDULED",
  "aircraftType": "Boeing 737",
  "createdAt": "2024-12-01T08:00:00",
  "updatedAt": "2024-12-01T08:00:00"
}
```

### Get Flight by ID

**Endpoint:** `GET /api/v1/flights/{id}`

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "flightNumber": "AA123",
  "departureAirport": "JFK",
  "arrivalAirport": "LAX",
  "scheduledDeparture": "2024-12-15T10:00:00",
  "scheduledArrival": "2024-12-15T13:00:00",
  "status": "SCHEDULED",
  "aircraftType": "Boeing 737",
  "createdAt": "2024-12-01T08:00:00",
  "updatedAt": "2024-12-01T08:00:00"
}
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2024-12-01T08:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Flight not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/v1/flights/550e8400-e29b-41d4-a716-446655440000"
}
```

### Get All Flights

**Endpoint:** `GET /api/v1/flights`

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "AA123",
    "departureAirport": "JFK",
    "arrivalAirport": "LAX",
    "scheduledDeparture": "2024-12-15T10:00:00",
    "scheduledArrival": "2024-12-15T13:00:00",
    "status": "SCHEDULED",
    "aircraftType": "Boeing 737",
    "createdAt": "2024-12-01T08:00:00",
    "updatedAt": "2024-12-01T08:00:00"
  }
]
```

### Search Flights

**Endpoint:** `GET /api/v1/flights/search`

**Query Parameters:**
- `flightNumber` (optional): Search by flight number (partial match)
- `status` (optional): Filter by flight status
- `departureAirport` (optional): Filter by departure airport
- `arrivalAirport` (optional): Filter by arrival airport

**Example:** `GET /api/v1/flights/search?flightNumber=AA123`

**Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "flightNumber": "AA123",
    "departureAirport": "JFK",
    "arrivalAirport": "LAX",
    "scheduledDeparture": "2024-12-15T10:00:00",
    "scheduledArrival": "2024-12-15T13:00:00",
    "status": "SCHEDULED",
    "aircraftType": "Boeing 737",
    "createdAt": "2024-12-01T08:00:00",
    "updatedAt": "2024-12-01T08:00:00"
  }
]
```

### Update Flight

**Endpoint:** `PUT /api/v1/flights/{id}`

**Request Body:**
```json
{
  "flightNumber": "AA456",
  "departureAirport": "SFO",
  "arrivalAirport": "ORD",
  "scheduledDeparture": "2024-12-15T11:00:00",
  "scheduledArrival": "2024-12-15T14:00:00",
  "status": "DEPARTED",
  "aircraftType": "Airbus A320"
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "flightNumber": "AA456",
  "departureAirport": "SFO",
  "arrivalAirport": "ORD",
  "scheduledDeparture": "2024-12-15T11:00:00",
  "scheduledArrival": "2024-12-15T14:00:00",
  "status": "DEPARTED",
  "aircraftType": "Airbus A320",
  "createdAt": "2024-12-01T08:00:00",
  "updatedAt": "2024-12-01T09:00:00"
}
```

### Delete Flight

**Endpoint:** `DELETE /api/v1/flights/{id}`

**Response (204 No Content)**

## Validation Rules

### Flight Number
- Required for creation
- Format: XX123 or XX1234 (e.g., AA123, UA4567)
- Case-sensitive uppercase

### Airport Codes
- Required for creation
- Format: 3-letter IATA code (e.g., JFK, LAX, SFO)
- Case-sensitive uppercase

### Scheduled Times
- Required for creation
- Must be in the future
- Scheduled arrival must be after scheduled departure

### Flight Status
- Required for creation
- Valid values: SCHEDULED, BOARDING, DEPARTED, ARRIVED, DELAYED, CANCELLED

### Aircraft Type
- Optional
- Maximum length: 50 characters

## Error Response Format

All error responses follow this consistent format:

```json
{
  "timestamp": "2024-12-01T08:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/flights"
}
```

### HTTP Status Codes

- **201 Created**: Flight successfully created
- **200 OK**: Successful read or update operation
- **204 No Content**: Successful deletion
- **400 Bad Request**: Validation error or invalid input
- **404 Not Found**: Flight not found
- **500 Internal Server Error**: Unexpected server error

## How to Run Flight Service Locally

### Prerequisites

1. Java 21 installed and JAVA_HOME configured
2. PostgreSQL database running (or use Docker Compose)
3. Maven 3.9+ (or use Maven Wrapper)

### Database Setup

Using Docker Compose:
```bash
cd e:\airline-flight-operations-system
docker-compose up -d postgres
```

This will start PostgreSQL with the `flight_db` database.

### Configuration

The service uses environment variables for database configuration. Update `src/main/resources/application.yml` or set environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/flight_db
    username: airline_user
    password: airline_password
```

### Build and Run

Using Maven Wrapper:
```bash
cd e:\airline-flight-operations-system
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run -pl flight-service
```

Or build and run the JAR:
```bash
.\mvnw.cmd clean package -pl flight-service
java -jar flight-service/target/flight-service-1.0.0-SNAPSHOT.jar
```

### Verify Service

Health check:
```bash
curl http://localhost:8081/actuator/health
```

Swagger UI:
```
http://localhost:8081/swagger-ui.html
```

## Running Tests

```bash
.\mvnw.cmd test -pl flight-service
```

Tests use H2 in-memory database and do not require a running PostgreSQL instance.

## Database Schema

The `flights` table is created by Flyway migration `V1__create_flights_table.sql`:

```sql
CREATE TABLE flights (
    id UUID PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    departure_airport VARCHAR(3) NOT NULL,
    arrival_airport VARCHAR(3) NOT NULL,
    scheduled_departure TIMESTAMP NOT NULL,
    scheduled_arrival TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    aircraft_type VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
```

Indexes are created on:
- flight_number
- status
- scheduled_departure
- departure_airport
- arrival_airport

## Package Structure

```
com.airline.flightservice
├── controller          # REST controllers
├── dto                 # Data Transfer Objects
├── entity              # JPA entities
├── exception           # Custom exceptions and error handlers
├── model               # Enums and domain models
├── repository          # JPA repositories
└── service             # Business logic
```

## Current Status

**Milestone 2: Flight Service Foundation** - ✅ Complete

Implemented features:
- Flight entity with UUID identifiers
- Flight repository with query methods
- CRUD operations via REST API
- Input validation using Jakarta Bean Validation
- Global exception handling
- Database schema with Flyway migration
- Unit and integration tests
- OpenAPI/Swagger documentation
