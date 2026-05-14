# Test and Quality Reports

All reports below are generated automatically by the CI/CD pipeline and published
alongside this Sphinx portal on GitHub Pages.

## JaCoCo — Code Coverage

JaCoCo instruments the bytecode and measures which lines, branches, and methods
are exercised by the test suite.

**Direct link ->** [JaCoCo Coverage Report](../site/jacoco/index.html)

## Surefire — Unit Test Report

The Surefire plugin records every test case result (pass / fail / skipped)
and execution time.

**Direct link ->** [Surefire Unit Test Report](../site/surefire-report.html)

## Performance — Test Report

Generated when running the `performance` Maven profile. Shows throughput
and latency for endpoints annotated with `@JUnitPerfTest`.

**Direct link ->** [Performance Report](../performance/index.html)

## Doxygen — Technical Reference

Doxygen parses the Java source and produces a cross-referenced HTML reference
with collaboration diagrams and navigable source code.

**Direct link ->** [Doxygen HTML Reference](../doxygen/index.html)

---

## Generating All Reports Locally

```bash
# Unit tests + JaCoCo
cd JailQ
mvn test jacoco:report

# Full Maven site (includes Surefire report)
mvn site

# Performance tests
mvn -Pperformance integration-test

# Generate Doxygen
mkdir -p target/doxygen
doxygen src/main/resources/Doxyfile

# Build Sphinx
cd ../docs-sphinx
sphinx-build -b html source _build/html
```# Test and Quality Reports

All reports below are generated automatically by the CI/CD pipeline and published
alongside this Sphinx portal on GitHub Pages.

## JaCoCo — Code Coverage

JaCoCo instruments the bytecode and measures which lines, branches, and methods
are exercised by the test suite.

**Direct link ->** [JaCoCo Coverage Report](../site/jacoco/index.html)

## Surefire — Unit Test Report

The Surefire plugin records every test case result (pass / fail / skipped)
and execution time.

**Direct link ->** [Surefire Unit Test Report](../site/surefire-report.html)

## Performance — Test Report

Generated when running the `performance` Maven profile. Shows throughput
and latency for endpoints annotated with `@JUnitPerfTest`.

**Direct link ->** [Performance Report](../performance/index.html)

## Doxygen — Technical Reference

Doxygen parses the Java source and produces a cross-referenced HTML reference
with collaboration diagrams and navigable source code.

**Direct link ->** [Doxygen HTML Reference](../doxygen/index.html)

---

## Generating All Reports Locally

```bash
# Unit tests + JaCoCo
cd JailQ
mvn test jacoco:report

# Full Maven site (includes Surefire report)
mvn site

# Performance tests
mvn -Pperformance integration-test

# Generate Doxygen
mkdir -p target/doxygen
doxygen src/main/resources/Doxyfile

# Build Sphinx
cd ../docs-sphinx
sphinx-build -b html source _build/html
```