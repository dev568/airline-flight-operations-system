# Sequence Diagrams

## Create Flight

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway
    participant FlightController
    participant FlightService
    participant FlightRepository
    participant Database

    Client->>APIGateway: POST /api/v1/flights
    APIGateway->>FlightController: POST /api/v1/flights
    FlightController->>FlightService: createFlight(request)
    FlightService->>FlightService: validateRequest(request)
    FlightService->>FlightService: checkAirportCodes(request)
    FlightService->>FlightService: validateDates(request)
    FlightService->>FlightRepository: save(flight)
    FlightRepository->>Database: INSERT INTO flights
    Database-->>FlightRepository: flight
    FlightRepository-->>FlightService: flight
    FlightService->>FlightService: createAuditRecord(flight, "CREATE")
    FlightService-->>FlightController: flightResponse
    FlightController-->>APIGateway: 201 Created
    APIGateway-->>Client: 201 Created
```

## Update Flight Status

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway
    participant FlightController
    participant FlightService
    participant FlightRepository
    participant Database

    Client->>APIGateway: PATCH /api/v1/flights/{id}/status
    APIGateway->>FlightController: PATCH /api/v1/flights/{id}/status
    FlightController->>FlightService: updateFlightStatus(id, status)
    FlightService->>FlightRepository: findById(id)
    FlightRepository->>Database: SELECT * FROM flights
    Database-->>FlightRepository: flight
    FlightRepository-->>FlightService: flight
    FlightService->>FlightService: validateStatusTransition(currentStatus, newStatus)
    FlightService->>FlightService: updateStatus(flight, newStatus)
    FlightService->>FlightRepository: save(flight)
    FlightRepository->>Database: UPDATE flights SET status
    Database-->>FlightRepository: flight
    FlightRepository-->>FlightService: flight
    FlightService->>FlightService: createAuditRecord(flight, "STATUS_UPDATE")
    FlightService-->>FlightController: flightResponse
    FlightController-->>APIGateway: 200 OK
    APIGateway-->>Client: 200 OK
```

## Assign Crew to Flight

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway
    participant CrewController
    participant CrewService
    participant CrewRepository
    participant FlightServiceClient
    participant FlightService
    participant Database

    Client->>APIGateway: POST /api/v1/crew-assignments
    APIGateway->>CrewController: POST /api/v1/crew-assignments
    CrewController->>CrewService: assignCrewToFlight(request)
    CrewService->>CrewRepository: findById(crewMemberId)
    CrewRepository->>Database: SELECT * FROM crew_members
    Database-->>CrewRepository: crewMember
    CrewRepository-->>CrewService: crewMember
    CrewService->>CrewService: validateCrewActive(crewMember)
    CrewService->>FlightServiceClient: getFlight(flightId)
    FlightServiceClient->>FlightService: GET /api/v1/flights/{id}
    FlightService-->>FlightServiceClient: flight
    FlightServiceClient-->>CrewService: flight
    CrewService->>CrewService: validateFlightNotCancelled(flight)
    CrewService->>CrewService: checkCrewAvailability(crewMember, flight)
    CrewService->>CrewRepository: findOverlappingAssignments(crewMember, flight)
    CrewRepository->>Database: SELECT * FROM crew_assignments
    Database-->>CrewRepository: assignments
    CrewRepository-->>CrewService: assignments
    CrewService->>CrewService: validateNoOverlap(assignments)
    CrewService->>CrewRepository: save(assignment)
    CrewRepository->>Database: INSERT INTO crew_assignments
    Database-->>CrewRepository: assignment
    CrewRepository-->>CrewService: assignment
    CrewService->>CrewService: createAuditRecord(assignment, "ASSIGN")
    CrewService-->>CrewController: assignmentResponse
    CrewController-->>APIGateway: 201 Created
    APIGateway-->>Client: 201 Created
```

## Process Delay Event

```mermaid
sequenceDiagram
    participant Client
    participant OperationsController
    participant CamelRoute
    participant EventProcessor
    participant FlightServiceClient
    participant FlightService
    participant EventRepository
    participant Database

    Client->>OperationsController: POST /api/v1/operations/events
    OperationsController->>CamelRoute: send(event)
    CamelRoute->>CamelRoute: validateEvent(event)
    CamelRoute->>CamelRoute: routeByEventType(event)
    alt Event Type = DELAY
        CamelRoute->>EventProcessor: processDelayEvent(event)
        EventProcessor->>EventProcessor: validateDelayData(event)
        EventProcessor->>FlightServiceClient: updateFlightDelay(flightId, delayMinutes)
        FlightServiceClient->>FlightService: POST /api/v1/flights/{id}/delay
        FlightService-->>FlightServiceClient: success
        FlightServiceClient-->>EventProcessor: success
        EventProcessor->>EventRepository: save(event)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>EventProcessor: event
        EventProcessor->>EventProcessor: createAuditRecord(event, "DELAY_PROCESSED")
        EventProcessor-->>CamelRoute: success
    else Event Type = INVALID
        CamelRoute->>CamelRoute: routeToDeadLetter(event)
        CamelRoute->>EventRepository: save(event, FAILED)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>CamelRoute: event
    end
    CamelRoute-->>OperationsController: eventResponse
    OperationsController-->>Client: 201 Created
```

## Process Cancellation Event

```mermaid
sequenceDiagram
    participant Client
    participant OperationsController
    participant CamelRoute
    participant EventProcessor
    participant FlightServiceClient
    participant FlightService
    participant CrewServiceClient
    participant CrewService
    participant EventRepository
    participant Database

    Client->>OperationsController: POST /api/v1/operations/events
    OperationsController->>CamelRoute: send(event)
    CamelRoute->>CamelRoute: validateEvent(event)
    CamelRoute->>CamelRoute: routeByEventType(event)
    alt Event Type = CANCELLATION
        CamelRoute->>EventProcessor: processCancellationEvent(event)
        EventProcessor->>EventProcessor: validateCancellationData(event)
        EventProcessor->>FlightServiceClient: cancelFlight(flightId)
        FlightServiceClient->>FlightService: POST /api/v1/flights/{id}/cancel
        FlightService-->>FlightServiceClient: success
        FlightServiceClient-->>EventProcessor: success
        EventProcessor->>CrewServiceClient: removeCrewAssignments(flightId)
        CrewServiceClient->>CrewService: DELETE /api/v1/flights/{id}/crew
        CrewService-->>CrewServiceClient: success
        CrewServiceClient-->>EventProcessor: success
        EventProcessor->>EventRepository: save(event)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>EventProcessor: event
        EventProcessor->>EventProcessor: createAuditRecord(event, "CANCELLATION_PROCESSED")
        EventProcessor-->>CamelRoute: success
    else Event Type = INVALID
        CamelRoute->>CamelRoute: routeToDeadLetter(event)
        CamelRoute->>EventRepository: save(event, FAILED)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>CamelRoute: event
    end
    CamelRoute-->>OperationsController: eventResponse
    OperationsController-->>Client: 201 Created
```

## Handle Failed Operational Event

```mermaid
sequenceDiagram
    participant Client
    participant OperationsController
    participant CamelRoute
    participant EventProcessor
    participant DeadLetterChannel
    participant EventRepository
    participant Database
    participant Alerting

    Client->>OperationsController: POST /api/v1/operations/events
    OperationsController->>CamelRoute: send(event)
    CamelRoute->>CamelRoute: validateEvent(event)
    alt Validation Fails
        CamelRoute->>CamelRoute: routeToDeadLetter(event)
        CamelRoute->>DeadLetterChannel: send(event)
        DeadLetterChannel->>EventRepository: save(event, FAILED)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>DeadLetterChannel: event
        DeadLetterChannel->>Alerting: sendAlert(event)
        DeadLetterChannel-->>CamelRoute: failed
    else Processing Fails
        CamelRoute->>EventProcessor: processEvent(event)
        EventProcessor->>EventProcessor: process(event)
        EventProcessor-->>EventProcessor: exception
        EventProcessor->>CamelRoute: exception
        CamelRoute->>CamelRoute: handleException(exception)
        CamelRoute->>DeadLetterChannel: send(event)
        DeadLetterChannel->>EventRepository: save(event, FAILED)
        EventRepository->>Database: INSERT INTO operational_events
        Database-->>EventRepository: event
        EventRepository-->>DeadLetterChannel: event
        DeadLetterChannel->>Alerting: sendAlert(event)
        DeadLetterChannel-->>CamelRoute: failed
    end
    CamelRoute-->>OperationsController: errorResponse
    OperationsController-->>Client: 422 Unprocessable Entity
```

## Service Communication via API Gateway

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway
    participant FlightService
    participant CrewService
    participant OperationsService

    Client->>APIGateway: GET /api/v1/flights
    APIGateway->>FlightService: GET /api/v1/flights
    FlightService-->>APIGateway: flights
    APIGateway-->>Client: flights

    Client->>APIGateway: GET /api/v1/crew-members
    APIGateway->>CrewService: GET /api/v1/crew-members
    CrewService-->>APIGateway: crewMembers
    APIGateway-->>Client: crewMembers

    Client->>APIGateway: POST /api/v1/operations/events
    APIGateway->>OperationsService: POST /api/v1/operations/events
    OperationsService->>FlightService: GET /api/v1/flights/{id}
    FlightService-->>OperationsService: flight
    OperationsService->>CrewService: GET /api/v1/crew-members/{id}
    CrewService-->>OperationsService: crewMember
    OperationsService-->>APIGateway: event
    APIGateway-->>Client: event
```

## Health Check Flow

```mermaid
sequenceDiagram
    participant K8s/LoadBalancer
    participant Service
    participant Actuator
    participant Database
    participant Dependencies

    K8s/LoadBalancer->>Service: GET /actuator/health
    Service->>Actuator: healthCheck()
    Actuator->>Database: checkConnection()
    Database-->>Actuator: OK
    Actuator->>Dependencies: checkDependencies()
    Dependencies-->>Actuator: OK
    Actuator-->>Service: UP
    Service-->>K8s/LoadBalancer: 200 OK

    K8s/LoadBalancer->>Service: GET /actuator/readiness
    Service->>Actuator: readinessCheck()
    Actuator->>Actuator: checkStartupComplete()
    Actuator->>Actuator: checkCanAcceptTraffic()
    Actuator-->>Service: READY
    Service-->>K8s/LoadBalancer: 200 OK
```
