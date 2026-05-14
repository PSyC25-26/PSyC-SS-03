# Getting Started

## Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Java (JDK) | 17 |
| Maven | 3.9 |
| MySQL | 8.0 |
| Docker and Docker Compose | 24+ (optional) |

## Database Setup

```bash
# With Docker (recommended)
cd JailQ
docker compose up -d

# Without Docker: start local MySQL and create the database
mysql -u root -p < src/main/resources/dbsetup.sql
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jailqdb
spring.datasource.username=<your_user>
spring.datasource.password=<your_password>
```

## Running the Application

```bash
# Full build and start
mvn clean install
mvn spring-boot:run

# Fast start without tests
mvn -DskipTests spring-boot:run
```

Server starts at `http://localhost:8080`.

## Running with Docker

```bash
docker compose up
docker compose up --build
docker compose down
```

## Running the GUI

```bash
mvn exec:java -Dexec.mainClass="com.example.JailQ.GUI.JailQMainGUI"
```

Or run `JailQMainGUI.java` directly from IntelliJ or VS Code.

## Quick Reference: Maven Commands

| Goal | Command |
|------|---------|
| Unit tests | `mvn test` |
| Integration tests | `mvn -Pintegration integration-test` |
| Performance tests | `mvn -Pperformance integration-test` |
| JaCoCo report | `mvn clean test jacoco:report` |
| Generate Doxygen | `doxygen src/main/resources/Doxyfile` |
| Build Sphinx | `cd docs-sphinx && make html` |
