# Troubleshooting

## Common Issues

### Build Issues

#### Maven Build Fails

**Symptom:** `./mvnw clean install` fails

**Solutions:**
1. Check Java version: `java -version` (should be 21)
2. Clear Maven cache: `./mvnw clean`
3. Remove `.m2/repository` and retry
4. Check network connectivity for dependency downloads
5. Verify Maven version: `mvn -version` (should be 3.9+)

#### Dependency Conflicts

**Symptom:** ClassNotFound or version mismatch errors

**Solutions:**
1. Check dependency tree: `./mvnw dependency:tree`
2. Verify parent POM version management
3. Exclude conflicting dependencies
4. Use `mvn dependency:analyze` to find unused dependencies

### Runtime Issues

#### Service Won't Start

**Symptom:** Service fails to start or exits immediately

**Solutions:**
1. Check application logs for error messages
2. Verify database connectivity
3. Check port availability: `netstat -an | grep 8081`
4. Verify environment variables are set
5. Check JVM memory settings

#### Database Connection Failed

**Symptom:** `Connection refused` or `Unable to connect to database`

**Solutions:**
1. Verify PostgreSQL is running: `docker ps`
2. Check database credentials
3. Verify database exists: `\l` in psql
4. Check network connectivity
5. Verify JDBC URL format

#### Port Already in Use

**Symptom:** `Port 8081 is already in use`

**Solutions:**
1. Find process using port: `netstat -ano | findstr :8081` (Windows) or `lsof -i :8081` (Linux/Mac)
2. Kill the process or change port in `application.yml`
3. Use different ports for different services

### Docker Issues

#### Docker Build Fails

**Symptom:** Docker build fails during `RUN mvn package`

**Solutions:**
1. Check Docker daemon is running
2. Verify Docker has sufficient memory
3. Check network connectivity for Maven downloads
4. Use Docker build with `--no-cache` to avoid cache issues

#### Container Won't Start

**Symptom:** Container exits immediately

**Solutions:**
1. Check container logs: `docker logs <container-id>`
2. Verify environment variables
3. Check health check configuration
4. Verify volume mounts

#### Docker Compose Issues

**Symptom:** Services can't communicate

**Solutions:**
1. Verify services are on same network
2. Check service names in docker-compose.yml
3. Verify port mappings
4. Check firewall settings

### Kubernetes Issues

#### Pod Won't Start

**Symptom:** Pod status is `CrashLoopBackOff` or `Error`

**Solutions:**
1. Check pod logs: `kubectl logs <pod-name>`
2. Describe pod: `kubectl describe pod <pod-name>`
3. Check resource limits
4. Verify image exists in registry
5. Check ConfigMap and Secret mounts

#### Service Not Accessible

**Symptom:** Can't reach service from outside cluster

**Solutions:**
1. Check Service type (NodePort, LoadBalancer)
2. Verify Service selector matches Pod labels
3. Check network policies
4. Verify port configuration
5. Check firewall rules

#### Health Check Failing

**Symptom:** Readiness/Liveness probe failing

**Solutions:**
1. Check probe endpoint is accessible: `kubectl exec <pod> -- curl localhost:8081/actuator/health`
2. Verify probe configuration (path, port, interval)
3. Check application startup time
4. Adjust initial delay and timeout

### Test Issues

#### Tests Fail Locally

**Symptom:** Unit or integration tests fail

**Solutions:**
1. Run specific test: `./mvnw test -Dtest=FlightServiceTest`
2. Check test logs for specific failure
3. Verify test data setup
4. Check H2/PostgreSQL configuration for tests
5. Run with debug: `./mvnw test -X`

#### Testcontainers Failures

**Symptom:** Testcontainers can't start containers

**Solutions:**
1. Verify Docker daemon is running
2. Check Docker has sufficient resources
3. Verify Testcontainers can reach Docker
4. Check Ryuk container is running
5. Disable Ryuk if needed: `testcontainers.reuse.enable=true`

### Apache Camel Issues

#### Route Not Executing

**Symptom:** Camel route doesn't process messages

**Solutions:**
1. Check route is started: `camelContext.getRouteStatus()`
2. Verify endpoint URI is correct
3. Check for exceptions in route
4. Enable Camel logging: `logging.level.org.apache.camel=DEBUG`
5. Use Camel debugger or tracing

#### Dead Letter Channel

**Symptom:** Messages going to dead letter channel

**Solutions:**
1. Check dead letter endpoint logs
2. Verify message format
3. Check validation rules
4. Review exception handling
5. Check for idempotency issues

### Performance Issues

#### Slow API Responses

**Symptom:** API endpoints are slow

**Solutions:**
1. Check database query performance
2. Verify indexes are being used
3. Check for N+1 query problems
4. Enable SQL logging: `spring.jpa.show-sql=true`
5. Use database EXPLAIN ANALYZE

#### High Memory Usage

**Symptom:** Service consuming too much memory

**Solutions:**
1. Check for memory leaks
2. Verify heap size settings
3. Review entity loading (lazy vs eager)
4. Check connection pool size
5. Use JVM profiling tools

### Security Issues

#### Secrets in Logs

**Symptom:** Passwords or sensitive data in logs

**Solutions:**
1. Review logging configuration
2. Mask sensitive fields
3. Use environment variables for secrets
4. Check for hardcoded credentials
5. Review audit logs

#### CORS Errors

**Symptom:** Browser CORS errors

**Solutions:**
1. Configure CORS in Spring Security
2. Verify allowed origins
3. Check preflight requests
4. Review API Gateway configuration

## Debugging Tips

### Enable Debug Logging

```yaml
logging:
  level:
    com.airline: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Check Database Queries

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### Enable Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers
```

### Use Spring Boot DevTools

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

## Getting Help

### Log Locations

- Application logs: Console output or configured log file
- Docker logs: `docker logs <container>`
- Kubernetes logs: `kubectl logs <pod>`

### Useful Commands

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Docker status
docker ps
docker logs <container>

# Check Kubernetes status
kubectl get nodes
kubectl get pods
kubectl logs <pod>

# Check PostgreSQL
docker exec -it airline-postgres psql -U airline_user -d flight_db
```

### Resources

- Spring Boot Documentation: https://docs.spring.io/spring-boot/
- Spring Cloud Documentation: https://spring.io/projects/spring-cloud
- Apache Camel Documentation: https://camel.apache.org/
- PostgreSQL Documentation: https://www.postgresql.org/docs/
- Kubernetes Documentation: https://kubernetes.io/docs/
