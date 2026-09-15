# Database Design

## Database Strategy

### Database per Service Pattern

Each microservice has its own database:

- **flight_db** - Flight service data
- **crew_db** - Crew service data
- **operations_db** - Operations service data

### Local Development Setup

Single PostgreSQL container with three logical databases:
- Created via `init-db.sql` on container startup
- Each service connects to its respective database

### Migration Strategy

- **Tool**: Flyway
- **Approach**: SQL-based migrations
- **Location**: `src/main/resources/db/migration/`
- **Naming**: `V{version}__{description}.sql`

### Schema Generation

- Development: `spring.jpa.hibernate.ddl-auto=validate`
- Test: H2 with auto-creation
- Production: Flyway migrations only, no auto-generation

## Identifier Strategy

### UUID for All Entities

**Rationale:**
- Microservices: No ID collisions across services
- Security: Non-guessable IDs
- Distribution: No central ID generation needed
- Performance: Sufficient for airline scale

**Implementation:**
```java
@Id
@GeneratedValue
@org.hibernate.annotations.UuidGenerator
private UUID id;
```

**Note:** Uses Hibernate 6 `@UuidGenerator` annotation which is compatible with Jakarta Persistence (jakarta.persistence.*).

**Storage:**
- PostgreSQL: Native UUID type
- H2: UUID type

## Flight Service Schema

### airports

```sql
CREATE TABLE airports (
    id UUID PRIMARY KEY,
    airport_code VARCHAR(5) NOT NULL UNIQUE,
    airport_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_airports_code ON airports(airport_code);
CREATE INDEX idx_airports_active ON airports(active);
```

### flights

```sql
CREATE TABLE flights (
    id UUID PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    airline_code VARCHAR(5) NOT NULL,
    departure_airport_code VARCHAR(5) NOT NULL,
    arrival_airport_code VARCHAR(5) NOT NULL,
    scheduled_departure_time TIMESTAMP NOT NULL,
    scheduled_arrival_time TIMESTAMP NOT NULL,
    actual_departure_time TIMESTAMP,
    actual_arrival_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    aircraft_code VARCHAR(10),
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_departure_airport FOREIGN KEY (departure_airport_code) 
        REFERENCES airports(airport_code),
    CONSTRAINT fk_arrival_airport FOREIGN KEY (arrival_airport_code) 
        REFERENCES airports(airport_code),
    CONSTRAINT chk_different_airports 
        CHECK (departure_airport_code != arrival_airport_code),
    CONSTRAINT chk_arrival_after_departure 
        CHECK (scheduled_arrival_time > scheduled_departure_time)
);

CREATE INDEX idx_flights_number ON flights(flight_number);
CREATE INDEX idx_flights_status ON flights(status);
CREATE INDEX idx_flights_departure ON flights(departure_airport_code);
CREATE INDEX idx_flights_arrival ON flights(arrival_airport_code);
CREATE INDEX idx_flights_scheduled_dep ON flights(scheduled_departure_time);
```

## Crew Service Schema

### crew_members

```sql
CREATE TABLE crew_members (
    id UUID PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    base_airport_code VARCHAR(5),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_crew_employee_code ON crew_members(employee_code);
CREATE INDEX idx_crew_role ON crew_members(role);
CREATE INDEX idx_crew_active ON crew_members(active);
CREATE INDEX idx_crew_base_airport ON crew_members(base_airport_code);
```

### crew_assignments

```sql
CREATE TABLE crew_assignments (
    id UUID PRIMARY KEY,
    flight_id UUID NOT NULL,
    crew_member_id UUID NOT NULL,
    assignment_role VARCHAR(20) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_assignment_flight FOREIGN KEY (flight_id) 
        REFERENCES flights(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_crew FOREIGN KEY (crew_member_id) 
        REFERENCES crew_members(id),
    CONSTRAINT uk_flight_crew UNIQUE (flight_id, crew_member_id)
);

CREATE INDEX idx_assignments_flight ON crew_assignments(flight_id);
CREATE INDEX idx_assignments_crew ON crew_assignments(crew_member_id);
```

## Operations Service Schema

### operational_events

```sql
CREATE TABLE operational_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    flight_id UUID NOT NULL,
    event_time TIMESTAMP NOT NULL,
    event_source VARCHAR(100) NOT NULL,
    event_payload JSONB,
    processing_status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_flight_event_time UNIQUE (flight_id, event_time, event_type)
);

CREATE INDEX idx_events_flight ON operational_events(flight_id);
CREATE INDEX idx_events_type ON operational_events(event_type);
CREATE INDEX idx_events_status ON operational_events(processing_status);
CREATE INDEX idx_events_time ON operational_events(event_time);
```

### audit_records

```sql
CREATE TABLE audit_records (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    old_value JSONB,
    new_value JSONB,
    performed_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_entity ON audit_records(entity_type, entity_id);
CREATE INDEX idx_audit_action ON audit_records(action);
CREATE INDEX idx_audit_created ON audit_records(created_at);
```

## Data Integrity

### Constraints
- Primary keys on all tables
- Unique constraints on business keys
- Foreign key constraints for relationships
- Check constraints for business rules
- Not-null constraints on required fields

### Indexes
- Indexes on foreign keys
- Indexes on search fields
- Indexes on filter fields
- Composite indexes where appropriate

### Optimistic Locking
- `version` column on flights table
- Prevents lost updates

## Transaction Boundaries

- Service layer methods are transactional
- Repository methods use default transaction propagation
- Rollback on runtime exceptions
- Commit on successful completion

## Backup and Recovery

- PostgreSQL native backup tools (pg_dump, pg_restore)
- Point-in-time recovery capability
- Regular backup schedules (production)
