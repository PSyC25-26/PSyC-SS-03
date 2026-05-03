# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app

# Copiar todos los archivos (incluyendo pom.xml)
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
