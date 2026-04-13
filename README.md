# Task Manager API

### Hexlet tests and linter status:
[![Actions Status](https://github.com/Katherini17/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Katherini17/java-project-99/actions)

### Code Quality:
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Katherini17_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Katherini17_java-project-99)

### CI/CD Status:
[![Java CI with Gradle](https://github.com/Katherini17/java-project-99/actions/workflows/main.yml/badge.svg)](https://github.com/Katherini17/java-project-99/actions/workflows/main.yml)

A REST API for task management. The application allows users to create, update, and track tasks, assign them to other users, and categorize resources using customizable statuses and labels.

### [🚀 Live Demo on Render](https://java-project-99-5ygj.onrender.com)

---

## Functional Overview
* **Authentication:** JWT-based (asymmetric RSA encryption).
* **Tasks:** CRUD operations with filtering by title, status, assignee, and labels.
* **Users:** Registration and profile management.
* **Entities:** Management of task statuses and category labels.
* **Data:** Partial resource updates (JSON Merge Patch) and automatic database seeding.

## Tech Stack
* **Core:** Java 21, Spring Boot 3.4 (Spring Security, Spring Data JPA).
* **Database:** PostgreSQL, H2 (for development and testing).
* **Mapping & Logic:** MapStruct, JPA Specifications, Hibernate.
* **Integrations:** Sentry (monitoring), Jackson (JsonNullable).
* **Documentation:** OpenAPI / Swagger UI.
* **Testing:** JUnit 5, MockMvc, Instancio.

---

## 📸 Preview

#### Tasks Dashboard
A view of the task list for the "Blueberry Pie" project, showing statuses, assignees, and labels.
![Tasks Dashboard](./assets/tasks.PNG)

#### Advanced Filtering
Dynamic task search using JPA Specifications to filter results by multiple criteria.
![Filtering](./assets/filtering.PNG)

#### Workflow Management
Predefined task statuses to track every stage of the development or production cycle.
![Task statuses](./assets/statuses.PNG)

#### Task Management
A comprehensive form for creating and editing tasks with full entity integration.
![Task Management](./assets/task-edit.PNG)

#### API Documentation
Interactive Swagger UI with built-in JWT authorization support for testing endpoints.
![Swagger UI](./assets/swagger.PNG)

--- 

## 📖 API Documentation

The project follows the OpenAPI standard. Detailed documentation of all endpoints, request/response formats, and security schemes is available via:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI Specification (JSON):** `http://localhost:8080/v3/api-docs`

Use these resources to explore the full list of available operations, including filtering, sorting, and authentication.

### Default Admin Credentials
* **Login:** `hexlet@example.com`
* **Password:** `qwerty`

---

## Installation & Commands

```bash
# Build and prepare the distribution
make install

# Launch the application from the distribution
make run-dist

# Run tests and checkstyle
make test
make lint
```