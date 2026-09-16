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
export DB_PASSWORD=your_password
docker-compose up -d
```

3. Build project:
```bash
mvn clean package
```

4. Run services:
```bash
# Terminal 1 - Flight Service
mvn spring-boot:run -pl flight-service

# Terminal 2 - Crew Service
mvn spring-boot:run -pl crew-service

# Terminal 3 - Operations Service
mvn spring-boot:run -pl operations-service

# Terminal 4 - API Gateway
mvn spring-boot:run -pl api-gateway
```

### Environment Configuration

All services support environment variables for database configuration. No local application.yml overrides are required.

## Docker Deployment

**Current Status:**
- ✅ Docker available (version 29.8.0)
- ✅ Docker images built successfully for all services
- ✅ Docker Compose PostgreSQL configuration validated
- ❌ Application containers not currently defined in docker-compose.yml
- ✅ Dockerfiles validated with root-context builds

### Build Images

```bash
# Build all images from repository root
docker build -t airline-api-gateway -f api-gateway/Dockerfile .
docker build -t airline-flight-service -f flight-service/Dockerfile .
docker build -t airline-crew-service -f crew-service/Dockerfile .
docker build -t airline-operations-service -f operations-service/Dockerfile .
```

### Run with Docker Compose

```bash
# Set required environment variable
export DB_PASSWORD=your_password

# Start PostgreSQL only
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Dockerfile Structure

Multi-stage build for small images with repository root context:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml ./api-gateway/
COPY flight-service/pom.xml ./flight-service/
COPY crew-service/pom.xml ./crew-service/
COPY operations-service/pom.xml ./operations-service/
COPY api-gateway/src ./api-gateway/src
COPY flight-service/src ./flight-service/src
COPY crew-service/src ./crew-service/src
COPY operations-service/src ./operations-service/src
RUN mvn clean package -DskipTests -pl api-gateway

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/api-gateway/target/api-gateway-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Note:** Dockerfiles use repository root as build context, copy all module POMs and source directories, build specific modules with Maven `-pl` flag, use exact JAR filenames (no wildcards), and install curl for healthchecks.

## Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (minikube, kind, or cloud)
- kubectl configured
- Container registry access
- Docker available for image building

### Current Status

**Note:** Kubernetes deployment manifests are available in the `k8s/` directory but have not been tested against a real Kubernetes cluster.

**Available Manifests:**
- namespace.yaml - Kubernetes namespace
- configmap.yaml - Configuration for database URLs and service routing
- secret.yaml - Secret for database password (placeholder)
- api-gateway-deployment.yaml + service.yaml
- flight-service-deployment.yaml + service.yaml
- crew-service-deployment.yaml + service.yaml
- operations-service-deployment.yaml + service.yaml

### Build and Push Images

```bash
# Build images
docker build -t airline-api-gateway -f api-gateway/Dockerfile .
docker build -t airline-flight-service -f flight-service/Dockerfile .
docker build -t airline-crew-service -f crew-service/Dockerfile .
docker build -t airline-operations-service -f operations-service/Dockerfile .

# Tag for registry
docker tag airline-api-gateway:latest registry.example.com/airline/api-gateway:1.0.0
docker tag airline-flight-service:latest registry.example.com/airline/flight-service:1.0.0
docker tag airline-crew-service:latest registry.example.com/airline/crew-service:1.0.0
docker tag airline-operations-service:latest registry.example.com/airline/operations-service:1.0.0

# Push to registry
docker push registry.example.com/airline/api-gateway:1.0.0
docker push registry.example.com/airline/flight-service:1.0.0
docker push registry.example.com/airline/crew-service:1.0.0
docker push registry.example.com/airline/operations-service:1.0.0
```

### Deploy to Kubernetes

```bash
# Apply namespace
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap
kubectl apply -f k8s/configmap.yaml

# Update Secret with actual password
kubectl apply -f k8s/secret.yaml

# Deploy services
kubectl apply -f k8s/flight-service-deployment.yaml
kubectl apply -f k8s/flight-service-service.yaml
kubectl apply -f k8s/crew-service-deployment.yaml
kubectl apply -f k8s/crew-service-service.yaml
kubectl apply -f k8s/operations-service-deployment.yaml
kubectl apply -f k8s/operations-service-service.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml
kubectl apply -f k8s/api-gateway-service.yaml

# Check deployment status
kubectl get deployments -n airline-operations
kubectl get pods -n airline-operations
kubectl get services -n airline-operations

# View logs
kubectl logs -f deployment/flight-service -n airline-operations
```

### Kubernetes Resources

#### Deployment
- Replicas: 1 (adjust based on requirements)
- Resource requests: 256Mi-512Mi CPU, 250m-500m memory
- Resource limits: 512Mi-1Gi CPU, 500m-1000m memory
- Liveness probe: `/actuator/health`
- Readiness probe: `/actuator/health`

#### Service
- Type: ClusterIP (internal), LoadBalancer (gateway)
- Ports: 8080-8083

#### ConfigMap
- Application configuration
- Database connection strings (without passwords)
- Service URLs

#### Secret
- Database passwords (placeholder in manifests)
- Never commit real secrets to Git

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
| DB_URL | Database JDBC URL | jdbc:postgresql://localhost:5432/flight_db |
| DB_USERNAME | Database username | airline_user |
| DB_PASSWORD | Database password | (must be set for PostgreSQL) |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| FLIGHT_SERVICE_URL | Flight service URL (gateway) | http://localhost:8081 |
| CREW_SERVICE_URL | Crew service URL (gateway) | http://localhost:8082 |
| OPERATIONS_SERVICE_URL | Operations service URL (gateway) | http://localhost:8083 |

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
