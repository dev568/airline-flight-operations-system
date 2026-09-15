# Architecture

## High-Level Architecture

The system follows a microservices-oriented architecture with four services:

```
                    ┌─────────────┐
                    │   Client    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │
                    │   :8080     │
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
    ┌──────▼──────┐ ┌─────▼──────┐ ┌──────▼──────┐
    │Flight Service│ │Crew Service│ │Operations   │
    │   :8081      │ │   :8082     │ │Service      │
    └──────┬──────┘ └─────┬──────┘ │   :8083      │
           │               │        └──────┬──────┘
           │               │               │
    ┌──────▼──────┐ ┌─────▼──────┐ ┌──────▼──────┐
    │ flight_db   │ │  crew_db   │ │operations_db│
    └─────────────┘ └────────────┘ └─────────────┘
```

## Service Responsibilities

### API Gateway
- Single entry point for clients
- Route requests to backend services
- Request correlation ID support
- Gateway-level configuration
- No business logic

### Flight Service
- Airport management (CRUD)
- Flight creation, retrieval, update, deletion
- Flight scheduling
- Flight status tracking
- Flight search and filtering
- Flight data ownership

### Crew Service
- Crew member management (CRUD)
- Crew roles
- Crew availability checking
- Crew assignment
- Crew conflict checking
- Crew data ownership

### Operations Service
- Flight operational events
- Delay event processing
- Cancellation event processing
- Status transition workflows
- Audit event creation
- Operational event processing
- Integration workflows using Apache Camel

## Database Ownership

Each service owns its database:

| Service | Database | Entities |
|---------|----------|----------|
| flight-service | flight_db | Airport, Flight |
| crew-service | crew_db | CrewMember, CrewAssignment |
| operations-service | operations_db | OperationalEvent, AuditRecord |

Services communicate via REST APIs. No direct database access between services.

## Communication Patterns

### Synchronous Communication
- REST APIs between services
- HTTP/JSON
- Correlation IDs for tracing

### Asynchronous Communication (Future)
- Apache Camel for internal event processing
- Event-driven patterns for future scalability

## Integration Patterns (Apache Camel)

The operations service uses Enterprise Integration Patterns:

- **Message Channel** - Event routing
- **Content-Based Router** - Route by event type
- **Message Translator** - Event transformation
- **Filter** - Event validation
- **Dead Letter Channel** - Failed event handling
- **Idempotent Consumer** - Duplicate prevention

## Data Consistency

- Each service owns its data
- Eventual consistency for cross-service operations
- Transactions within service boundaries
- Audit trails for important changes

## Security Architecture

### Current Implementation (Security-Aware)
- Input validation
- Safe error responses
- No secrets in Git
- Environment-based configuration
- Audit logging
- Correlation IDs
- Health checks

### Future Enhancements
- Authentication (OAuth2/JWT)
- Authorization (RBAC)
- API rate limiting
- Request signing

## Deployment Architecture

### Local Development
- Docker Compose
- Single PostgreSQL container with logical databases
- All services running locally

### Production
- Kubernetes deployment
- Separate PostgreSQL instances per service
- Horizontal scaling
- Load balancing

## Technology Rationale

### Java 21
- Modern language features (pattern matching, records, virtual threads)
- Long-term support
- Performance improvements
- Modern garbage collectors
- Aligned with current enterprise Java standards

### Spring Boot 3.3.5
- Latest stable version
- Jakarta EE 10+ support (jakarta.* namespace)
- Native image support
- Enhanced observability
- Improved security
- Requires Java 17+ (using Java 21)

### PostgreSQL
- ACID compliance
- Advanced features (JSON, arrays)
- Strong reliability
- Good performance

### Apache Camel
- Enterprise Integration Patterns
- Flexible routing
- Good for event processing
- Spring integration

### Microservices
- Independent deployment
- Technology diversity
- Fault isolation
- Scalability

## Implementation Milestones

1. Repository foundation
2. Flight service foundation
3. Flight business rules
4. Crew service
5. Operations service
6. Apache Camel integration
7. API Gateway
8. Observability and reliability
9. Docker
10. Kubernetes/OpenShift
11. CI/CD and security readiness
12. Final professional review
