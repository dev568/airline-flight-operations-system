# Testing Strategy

## Testing Philosophy

- Test early, test often
- Unit tests for business logic
- Integration tests for API endpoints
- Testcontainers for database testing
- Minimum 80% code coverage
- Tests should be fast and reliable

## Test Layers

### Unit Tests

**Purpose:** Test individual components in isolation

**Scope:**
- Service layer business logic
- Validation rules
- Business rule validation
- Edge cases
- Exception scenarios

**Tools:**
- JUnit 5
- Mockito
- AssertJ

**Example:**
```java
@Test
void whenDepartureAndArrivalSame_thenThrowException() {
    // Arrange
    CreateFlightRequest request = CreateFlightRequest.builder()
        .flightNumber("AA123")
        .departureAirportCode("JFK")
        .arrivalAirportCode("JFK")
        .build();
    
    // Act & Assert
    assertThatThrownBy(() -> flightService.createFlight(request))
        .isInstanceOf(ValidationException.class)
        .hasMessage("Departure and arrival airports must be different");
}
```

### Repository Tests

**Purpose:** Test JPA repository behavior

**Scope:**
- Query behavior
- Custom queries
- Constraint validation (where practical)
- Relationship loading

**Tools:**
- JUnit 5
- Spring Boot Test
- H2 in-memory database

### Controller Tests

**Purpose:** Test REST API endpoints

**Scope:**
- Request validation
- HTTP status codes
- JSON response structure
- Error responses
- Path variables and query params

**Tools:**
- JUnit 5
- MockMvc
- Spring Boot Test
- Mockito

**Example:**
```java
@Test
void whenCreateFlightWithValidRequest_thenReturnCreated() throws Exception {
    // Arrange
    CreateFlightRequest request = CreateFlightRequest.builder()
        .flightNumber("AA123")
        .departureAirportCode("JFK")
        .arrivalAirportCode("LAX")
        .build();
    
    // Act & Assert
    mockMvc.perform(post("/api/v1/flights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.flightNumber").value("AA123"));
}
```

### Integration Tests

**Purpose:** Test full stack from controller to database

**Scope:**
- Controller-service-repository flow
- Database transactions
- PostgreSQL-compatible behavior
- End-to-end workflows

**Tools:**
- JUnit 5
- Spring Boot Test
- Testcontainers (PostgreSQL)
- MockMvc

**Example:**
```java
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class FlightIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void whenCreateFlight_thenPersistInDatabase() {
        // Test full workflow
    }
}
```

### Apache Camel Route Tests

**Purpose:** Test Camel routes and integration patterns

**Scope:**
- Route execution
- Message transformation
- Routing logic
- Error handling

**Tools:**
- JUnit 5
- Camel Test Support
- Mock endpoints

## Test Coverage Goals

- **Service layer:** > 90%
- **Controller layer:** > 80%
- **Repository layer:** > 70%
- **Overall:** > 80%

## Test Data Management

### Test Fixtures

- Use `@Sql` or `@BeforeEach` for test data setup
- Clean up after each test
- Use realistic but anonymized test data

### Test Profiles

- `application-test.yml` for test configuration
- H2 for fast unit tests
- Testcontainers for integration tests

## Important Test Cases

### Flight Service
- Create flight with valid data
- Create flight with same departure/arrival airport
- Create flight with arrival before departure
- Create flight with duplicate flight number
- Update flight status with valid transition
- Update flight status with invalid transition
- Cancel flight and prevent further status changes
- Search flights with filters
- Pagination

### Crew Service
- Create crew member with unique employee code
- Create crew member with duplicate employee code
- Assign crew to flight
- Assign inactive crew to flight (should fail)
- Assign crew to overlapping flights (should fail)
- Assign crew to cancelled flight (should fail)
- Duplicate assignment to same flight (should fail)
- Check crew availability

### Operations Service
- Create delay event
- Create cancellation event
- Create status event
- Process event successfully
- Handle invalid event
- Handle duplicate event
- Create audit record
- Dead-letter channel for failed events

## Continuous Testing

- Run tests on every commit
- Run tests in CI/CD pipeline
- Fail build if tests fail
- Generate coverage reports

## Performance Testing

- Load testing for critical endpoints
- Database query performance analysis
- Index effectiveness verification

## Test Execution

```bash
# Run all tests
./mvnw test

# Run tests for specific module
./mvnw test -pl flight-service

# Run with coverage
./mvnw test jacoco:report

# Run integration tests only
./mvnw test -Dtest="*IntegrationTest"
```

## Known Limitations

- Testcontainers requires Docker daemon
- H2 may not perfectly match PostgreSQL behavior
- Some edge cases may be difficult to test
- External service mocking may be required
