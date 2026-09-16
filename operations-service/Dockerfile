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
RUN mvn clean package -DskipTests -pl operations-service

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=build /app/operations-service/target/operations-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
