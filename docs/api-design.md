# API Design

## API Principles

- RESTful design
- DTOs for request/response (no entity exposure)
- Meaningful HTTP status codes
- Consistent error responses
- Versioned APIs (`/api/v1/`)
- OpenAPI/Swagger documentation

## Standard Error Response

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/flights",
  "correlationId": "abc123-def456",
  "validationErrors": [
    {
      "field": "flightNumber",
      "message": "Flight number is required"
    }
  ]
}
```

## Flight Service APIs

### Airport Management

#### Create Airport
- **POST** `/api/v1/airports`
- **Request:**
```json
{
  "airportCode": "JFK",
  "airportName": "John F. Kennedy International",
  "city": "New York",
  "country": "United States",
  "timezone": "America/New_York"
}
```
- **Response:** 201 Created
- **Validation:** airportCode unique, required fields

#### List Airports
- **GET** `/api/v1/airports`
- **Query params:** page, size, sort, active
- **Response:** 200 OK with paginated list

#### Get Airport
- **GET** `/api/v1/airports/{id}`
- **Response:** 200 OK or 404 Not Found

#### Update Airport
- **PUT** `/api/v1/airports/{id}`
- **Response:** 200 OK or 404 Not Found

### Flight Management

#### Create Flight
- **POST** `/api/v1/flights`
- **Request:**
```json
{
  "flightNumber": "AA123",
  "airlineCode": "AA",
  "departureAirportCode": "JFK",
  "arrivalAirportCode": "LAX",
  "scheduledDepartureTime": "2024-01-15T10:00:00Z",
  "scheduledArrivalTime": "2024-01-15T13:00:00Z",
  "aircraftCode": "B737"
}
```
- **Response:** 201 Created
- **Validation:** flightNumber, different airports, arrival after departure

#### List Flights
- **GET** `/api/v1/flights`
- **Query params:** page, size, sort, status, departureAirport, arrivalAirport, dateFrom, dateTo
- **Response:** 200 OK with paginated list

#### Get Flight
- **GET** `/api/v1/flights/{id}`
- **Response:** 200 OK or 404 Not Found

#### Update Flight
- **PUT** `/api/v1/flights/{id}`
- **Response:** 200 OK or 404 Not Found

#### Update Flight Status
- **PATCH** `/api/v1/flights/{id}/status`
- **Request:**
```json
{
  "status": "DEPARTED"
}
```
- **Response:** 200 OK
- **Validation:** Valid status transition

#### Delete Flight
- **DELETE** `/api/v1/flights/{id}`
- **Response:** 204 No Content or 404 Not Found

## Crew Service APIs

### Crew Member Management

#### Create Crew Member
- **POST** `/api/v1/crew-members`
- **Request:**
```json
{
  "employeeCode": "EMP001",
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@airline.com",
  "role": "PILOT",
  "baseAirportCode": "JFK"
}
```
- **Response:** 201 Created
- **Validation:** employeeCode unique, valid role

#### List Crew Members
- **GET** `/api/v1/crew-members`
- **Query params:** page, size, sort, role, active, baseAirport
- **Response:** 200 OK with paginated list

#### Get Crew Member
- **GET** `/api/v1/crew-members/{id}`
- **Response:** 200 OK or 404 Not Found

#### Update Crew Member
- **PUT** `/api/v1/crew-members/{id}`
- **Response:** 200 OK or 404 Not Found

#### Check Availability
- **GET** `/api/v1/crew-members/{id}/availability`
- **Query params:** dateFrom, dateTo
- **Response:** 200 OK with availability status

### Crew Assignment

#### Create Assignment
- **POST** `/api/v1/crew-assignments`
- **Request:**
```json
{
  "flightId": "uuid",
  "crewMemberId": "uuid",
  "assignmentRole": "PILOT"
}
```
- **Response:** 201 Created
- **Validation:** Crew active, no overlap, valid role

#### Get Flight Crew
- **GET** `/api/v1/flights/{flightId}/crew`
- **Response:** 200 OK with crew list

#### Delete Assignment
- **DELETE** `/api/v1/crew-assignments/{id}`
- **Response:** 204 No Content

## Operations Service APIs

### Operational Events

#### Create Event
- **POST** `/api/v1/operations/events`
- **Request:**
```json
{
  "eventType": "DELAY",
  "flightId": "uuid",
  "eventTime": "2024-01-15T10:00:00Z",
  "eventSource": "SYSTEM",
  "eventPayload": {
    "delayMinutes": 30,
    "reason": "Weather"
  }
}
```
- **Response:** 201 Created

#### Get Event
- **GET** `/api/v1/operations/events/{id}`
- **Response:** 200 OK or 404 Not Found

#### List Events
- **GET** `/api/v1/operations/events`
- **Query params:** page, size, sort, eventType, flightId, status
- **Response:** 200 OK with paginated list

### Flight Operations

#### Delay Flight
- **POST** `/api/v1/flights/{flightId}/delay`
- **Request:**
```json
{
  "delayMinutes": 30,
  "reason": "Weather"
}
```
- **Response:** 200 OK

#### Cancel Flight
- **POST** `/api/v1/flights/{flightId}/cancel`
- **Request:**
```json
{
  "reason": "Mechanical issue"
}
```
- **Response:** 200 OK

#### Status Events
- **POST** `/api/v1/flights/{flightId}/status-events`
- **Request:**
```json
{
  "status": "DEPARTED",
  "eventTime": "2024-01-15T10:05:00Z"
}
```
- **Response:** 201 Created

### Audit Records

#### List Audit Records
- **GET** `/api/v1/audit-records`
- **Query params:** page, size, sort, entityType, entityId, action, dateFrom, dateTo
- **Response:** 200 OK with paginated list

## HTTP Status Codes

- **200 OK** - Successful GET, PUT, PATCH
- **201 Created** - Successful POST
- **204 No Content** - Successful DELETE
- **400 Bad Request** - Validation error, invalid input
- **404 Not Found** - Resource not found
- **409 Conflict** - Duplicate resource, constraint violation
- **422 Unprocessable Entity** - Business rule violation
- **500 Internal Server Error** - Server error

## Pagination

- Default page size: 20
- Max page size: 100
- Response format:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

## API Gateway Routes

- `/api/v1/airports/**` → Flight Service
- `/api/v1/flights/**` → Flight Service
- `/api/v1/crew-members/**` → Crew Service
- `/api/v1/crew-assignments/**` → Crew Service
- `/api/v1/operations/**` → Operations Service
- `/api/v1/audit-records/**` → Operations Service
