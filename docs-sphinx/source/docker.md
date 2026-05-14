# Docker and Docker Compose

## Quick Start

```bash
# Start application + MySQL
docker compose up

# Rebuild after code or Dockerfile changes
docker compose up --build

# Run in background
docker compose up -d

# Stop all services
docker compose down
```

## Dockerfile

The project's `Dockerfile` packages the Spring Boot JAR into a lightweight JRE image:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## docker-compose.yml

The compose file wires the app container to a MySQL service:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/jailqdb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      db:
        condition: service_healthy

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: jailqdb
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
```

## Database only

To start only MySQL without the app (for development with `mvn spring-boot:run`):

```bash
docker compose up db -d
```

## Clean data

To also remove MySQL volumes:

```bash
docker compose down -v
```
