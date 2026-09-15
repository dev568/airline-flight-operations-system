# Deployment

## Local

### Required Software

- **Java 21** (JDK 21 required - not Java 11 or Java 17)
- **Maven 3.9+** (or use included Maven Wrapper)
- **Docker 20+** (for local development and Testcontainers)
- **Git** (for version control)

### Java 21 Installation

Download and install JDK 21 from one of:
- Oracle JDK 21: https://www.oracle.com/java/technologies/downloads/#java21
- Eclipse Temurin 21: https://adoptium.net/temurin/releases/?version=21
- Microsoft Build of OpenJDK 21: https://learn.microsoft.com/en-us/java/openjdk/download

After installation:
1. Set `JAVA_HOME` environment variable to JDK 21 installation directory
2. Add `%JAVA_HOME%\bin` to PATH
3. Verify: `java -version` should show Java 21

### Setup

1. Clone repository
2. Start PostgreSQL:
```bash
docker-compose up -d postgres
```

3. Build project:
```bash
./mvnw clean install
```

4. Run services:
```bash
# Terminal 1 - Flight Service
./mvnw spring-boot:run -pl flight-service

# Terminal 2 - Crew Service
./mvnw spring-boot:run -pl crew-service

# Terminal 3 - Operations Service
./mvnw spring-boot:run -pl operations-service

# Terminal 4 - API Gateway
./mvnw spring-boot:run -pl api-gateway
```

### Environment Configuration

Create `application-local.yml` for local overrides:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/flight_db
    username: airline_user
    password: airline_password
```

## Docker Deployment

### Build Images

```bash
# Build all images
docker-compose build

# Build specific service
docker build -t airline/flight-service:latest flight-service/
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Dockerfile Structure

Multi-stage build for small images:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (minikube, kind, or cloud)
- kubectl configured
- Container registry access

### Build and Push Images

```bash
# Build images
docker-compose build

# Tag for registry
docker tag airline/flight-service:latest registry.example.com/airline/flight-service:1.0.0

# Push to registry
docker push registry.example.com/airline/flight-service:1.0.0
```

### Deploy to Kubernetes

```bash
# Apply all manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get deployments
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/flight-service
```

### Kubernetes Resources

#### Deployment
- Replicas: 3
- Resource requests: 512Mi CPU, 1Gi memory
- Resource limits: 1Gi CPU, 2Gi memory
- Liveness probe: `/actuator/health`
- Readiness probe: `/actuator/readiness`

#### Service
- Type: ClusterIP
- Port: 8080
- Target port: 8081 (service-specific)

#### ConfigMap
- Application configuration
- Database connection strings (without passwords)
- Service URLs

#### Secret
- Database passwords
- API keys (if needed)
- Never commit secrets to Git

## OpenShift Deployment

### OpenShift-Specific Resources

OpenShift can use standard Kubernetes resources with additional features:

#### Routes
```yaml
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: flight-service
spec:
  to:
    kind: Service
    name: flight-service
  port:
    targetPort: 8081
  tls:
    termination: edge
```

#### DeploymentConfig (Legacy)
- Use standard Deployment instead
- OpenShift 4.x supports standard Kubernetes resources

### OpenShift Build Configs

```yaml
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: flight-service
spec:
  source:
    git:
      uri: https://github.com/your-org/airline-flight-operations-system
    contextDir: flight-service
  strategy:
    sourceStrategy:
      from:
        kind: ImageStreamTag
        name: java:21
  output:
    to:
      kind: ImageStreamTag
      name: flight-service:latest
```

### OpenShift Specific Features

- Built-in image registry
- Source-to-image (S2I) builds
- Automatic rollouts
- Integrated logging (EFK stack)
- Integrated monitoring (Prometheus/Grafana)

## Environment Variables

### Required Variables

| Variable | Description | Example |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | Database JDBC URL | jdbc:postgresql://postgres:5432/flight_db |
| SPRING_DATASOURCE_USERNAME | Database username | airline_user |
| SPRING_DATASOURCE_PASSWORD | Database password | ${DB_PASSWORD} |
| SPRING_PROFILES_ACTIVE | Active profile | prod |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Server port | 8081 |
| LOGGING_LEVEL_ROOT | Log level | INFO |
| FLYWAY_ENABLED | Enable migrations | true |

## Health Checks

### Liveness Probe
- Endpoint: `/actuator/health`
- Interval: 30s
- Timeout: 3s
- Failure threshold: 3

### Readiness Probe
- Endpoint: `/actuator/readiness`
- Interval: 10s
- Timeout: 3s
- Failure threshold: 3

## Scaling

### Horizontal Scaling

```bash
# Scale deployment
kubectl scale deployment flight-service --replicas=5

# Auto-scaling (HPA)
kubectl autoscale deployment flight-service --min=2 --max=10 --cpu-percent=70
```

### Vertical Scaling

Adjust resource requests and limits in deployment manifest.

## Monitoring

### Actuator Endpoints

- `/actuator/health` - Health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information
- `/actuator/prometheus` - Prometheus metrics (if configured)

### Logging

- Structured JSON logging
- Log aggregation (ELK/EFK stack)
- Centralized log management

## Backup and Recovery

### Database Backup

```bash
# Backup
pg_dump -h postgres -U airline_user flight_db > flight_db_backup.sql

# Restore
psql -h postgres -U airline_user flight_db < flight_db_backup.sql
```

### Volume Snapshots

- Use Kubernetes volume snapshots
- Regular scheduled backups
- Test recovery procedures

## Troubleshooting

### Common Issues

1. **Service won't start**
   - Check logs: `kubectl logs deployment/flight-service`
   - Verify environment variables
   - Check database connectivity

2. **Database connection failed**
   - Verify PostgreSQL is running
   - Check credentials
   - Verify network policies

3. **Health check failing**
   - Check dependencies
   - Verify resource limits
   - Review application logs

See [troubleshooting.md](troubleshooting.md) for more details.
