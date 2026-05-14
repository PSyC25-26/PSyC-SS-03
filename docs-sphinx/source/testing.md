# Testing Strategy

The project applies a three-tier testing strategy, each with its own Maven profile and tooling.

## Unit Tests

**Framework:** JUnit 5 + Mockito
**Location:** `src/test/java/com/example/JailQ/`
**Command:**

```bash
mvn test
```

Unit tests mock all external dependencies and run using the H2 in-memory database.
They are fast and run on every commit.

Key test classes:
- `CarcelServiceTest` — validates prison business logic and occupancy statistics
- `PresoServiceTest` — validates date validations, minimum age and sentence
- `CuentaServiceTest` — validates authentication and account management

## Integration Tests

**Framework:** Spring Boot Test + real MySQL
**Maven profile:** `integration`
**Command:**

```bash
mvn -Pintegration integration-test
```

Integration tests start the full Spring context against a live database.
In CI they rely on the MySQL Docker service defined in the GitHub Actions workflow.

## Performance Tests

**Framework:** JUnitPerf
**Maven profile:** `performance`
**Command:**

```bash
mvn -Pperformance integration-test
```

Performance tests annotate methods with `@JUnitPerfTest` to define throughput
and latency thresholds. The HTML report is saved to `target/junitperf/report.html`.

## GUI Tests

**Framework:** AssertJ Swing
**Command:**

```bash
mvn test -Dtest="*GUITest"
```

Automated testing of Swing windows behaviour.

## Code Coverage

**Tool:** JaCoCo

```bash
mvn clean test jacoco:report
# Report at: target/site/jacoco/index.html
```

## Static Analysis

| Tool | Purpose | Report |
|------|---------|--------|
| **JaCoCo** | Code coverage | `target/site/jacoco/index.html` |
| **Surefire** | Test results | `target/site/surefire-report.html` |
