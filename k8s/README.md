# Kubernetes Deployment

This directory contains Kubernetes manifests for deploying the Airline Flight Operations Management System.

## Prerequisites

- Kubernetes cluster (minikube, kind, or cloud provider)
- kubectl configured to access your cluster
- Docker images built and available in your registry or cluster

## Deployment Steps

1. **Create namespace:**
   ```bash
   kubectl apply -f namespace.yaml
   ```

2. **Create ConfigMap:**
   ```bash
   kubectl apply -f configmap.yaml
   ```

3. **Create Secret with actual password:**
   ```bash
   kubectl apply -f secret.yaml
   ```
   Note: Update the DB_PASSWORD in secret.yaml with a secure value before deployment.

4. **Deploy services:**
   ```bash
   kubectl apply -f flight-service-deployment.yaml
   kubectl apply -f flight-service-service.yaml
   kubectl apply -f crew-service-deployment.yaml
   kubectl apply -f crew-service-service.yaml
   kubectl apply -f operations-service-deployment.yaml
   kubectl apply -f operations-service-service.yaml
   kubectl apply -f api-gateway-deployment.yaml
   kubectl apply -f api-gateway-service.yaml
   ```

## PostgreSQL Configuration

The ConfigMap defines service-specific database URLs:
- `FLIGHT_DB_URL`: jdbc:postgresql://airline-postgres:5432/flight_db
- `CREW_DB_URL`: jdbc:postgresql://airline-postgres:5432/crew_db
- `OPERATIONS_DB_URL`: jdbc:postgresql://airline-postgres:5432/operations_db

These manifests assume an external PostgreSQL instance named `airline-postgres` in the same namespace. The database URLs in the ConfigMap should be updated to match your PostgreSQL deployment.

For development, you can use the Docker Compose PostgreSQL configuration or deploy a PostgreSQL instance in Kubernetes.

## Verification

Check pod status:
```bash
kubectl get pods -n airline-operations
```

Check service status:
```bash
kubectl get svc -n airline-operations
```

Access the API Gateway:
```bash
kubectl port-forward svc/api-gateway 8080:8080 -n airline-operations
```

## Important Notes

- These manifests have not been tested against a real Kubernetes cluster
- Resource limits are minimal and should be adjusted based on actual usage
- Health check delays and thresholds may need tuning for your environment
- Consider using a proper database service (AWS RDS, Cloud SQL, etc.) for production
- Update image names and tags to match your registry
- Consider adding persistence, logging, and monitoring for production deployments
