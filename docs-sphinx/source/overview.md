# Project Overview

## Purpose

**JailQ** es una aplicacion Spring Boot desarrollada para la asignatura
**SPQ (Software Process and Quality)** de la Universidad de Deusto.
Demuestra buenas practicas en:

- Diseno de API REST con Spring Boot
- Arquitectura por capas (Controller, Service, DAO, Entidades)
- Testing automatizado (unitario, integracion, performance, GUI)
- Pipelines CI/CD con GitHub Actions
- Documentacion tecnica (Doxygen, Sphinx)

## Key Features

| Feature | Technology |
|---------|------------|
| REST API backend | Spring Boot 3, Spring MVC |
| Database ORM | Spring Data JPA + Hibernate |
| Database | MySQL (prod), H2 (tests) |
| GUI | Java Swing |
| Unit tests | JUnit 5 + Mockito |
| Integration tests | Spring Boot Test + MySQL |
| Performance tests | JUnitPerf |
| GUI tests | AssertJ Swing |
| Code coverage | JaCoCo |
| Containerization | Docker + Docker Compose |
| Documentation | Doxygen, Sphinx |

## Live Documentation Links

| Document | URL |
|----------|-----|
| This Sphinx portal | `https://psyc25-26.github.io/PSyC-SS-03/sphinx/` |
| Doxygen | `https://psyc25-26.github.io/PSyC-SS-03/doxygen/` |
| GitHub Pages root | `https://psyc25-26.github.io/PSyC-SS-03/` |

## Repository Structure

```
PSyC-SS-03/
├── .github/workflows/
│   ├── doxygen-docs.yml
│   └── sphinx-docs.yml
├── docs/
│   └── index.html
├── docs-sphinx/
│   ├── source/
│   │   ├── conf.py
│   │   ├── index.rst
│   │   ├── overview.md
│   │   ├── architecture.md
│   │   ├── getting_started.md
│   │   ├── testing.md
│   │   ├── reports.md
│   │   ├── api_rest.md
│   │   ├── javadoc_link.md
│   │   ├── cicd.md
│   │   ├── docker.md
│   │   └── sphinx_101.md
│   ├── Makefile
│   └── requirements.txt
└── JailQ/
    ├── src/main/resources/
    │   ├── Doxyfile
    │   └── assets/
    ├── docker-compose.yml
    └── pom.xml
```
