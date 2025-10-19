# Commission Calculator REST API

A RESTful API implementation for the Commission Calculator system using embedded Tomcat and plain Java servlets. This implementation demonstrates modern software engineering principles, design patterns, and REST API best practices.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Design Patterns](#design-patterns)
4. [Design Principles](#design-principles)
5. [API Endpoints](#api-endpoints)
6. [Running the Server](#running-the-server)
7. [Testing the API](#testing-the-api)
8. [Project Structure](#project-structure)
9. [Key Concepts](#key-concepts)

## Overview

This REST API provides comprehensive CRUD (Create, Read, Update, Delete) operations for managing:

- **Deals**: Sales deals and their associated products
- **Users**: System users with roles and permissions
- **Commission Plans**: Rules and tiers for commission calculations
- **Disputes**: Commission-related disputes and their resolution workflow

### Technology Stack

- **Java 21**: Modern Java features and syntax
- **Embedded Tomcat 10.1.15**: Self-contained web server (no external deployment needed)
- **Gson 2.10.1**: JSON serialization/deserialization
- **Jakarta Servlet API**: Standard servlet specification

## Architecture

The API follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│           HTTP Clients (Browser, curl)      │
└────────────────┬────────────────────────────┘
                 │ HTTP Requests
                 ▼
┌─────────────────────────────────────────────┐
│         ApiServer (Embedded Tomcat)         │
│  • Initializes Tomcat                       │
│  • Registers servlets                       │
│  • Configures URL mappings                  │
└────────────────┬────────────────────────────┘
                 │ Delegates to
                 ▼
┌─────────────────────────────────────────────┐
│           Servlet Layer                     │
│  • DealServlet                              │
│  • UserServlet                              │
│  • CommissionPlanServlet                    │
│  • DisputeServlet                           │
│  (Extends BaseServlet)                      │
└────────────────┬────────────────────────────┘
                 │ Uses
                 ▼
┌─────────────────────────────────────────────┐
│         Repository Layer                    │
│  • Repository<T> interface                  │
│  • InMemoryRepository<T> implementation     │
└────────────────┬────────────────────────────┘
                 │ Manages
                 ▼
┌─────────────────────────────────────────────┐
│         Domain Models                       │
│  • Deal, User, CommissionPlan, Dispute      │
└─────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility | Location |
|-----------|---------------|----------|
| **ApiServer** | Embedded Tomcat initialization, servlet registration, dependency injection | `ApiServer.java:1` |
| **BaseServlet** | Common HTTP handling logic (template methods) | `BaseServlet.java:1` |
| **DealServlet** | Deal-specific HTTP request handling | `DealServlet.java:1` |
| **UserServlet** | User-specific HTTP request handling | `UserServlet.java:1` |
| **CommissionPlanServlet** | Commission plan HTTP request handling | `CommissionPlanServlet.java:1` |
| **DisputeServlet** | Dispute HTTP request handling | `DisputeServlet.java:1` |
| **Repository<T>** | Generic data access interface | `Repository.java:1` |
| **InMemoryRepository<T>** | Thread-safe in-memory storage implementation | `InMemoryRepository.java:1` |
| **JsonHelper** | JSON serialization/deserialization utility | `JsonHelper.java:1` |

## Design Patterns

This implementation demonstrates the following design patterns:

### 1. Repository Pattern

**Purpose**: Abstracts data access logic from business logic.

**Implementation**:
- `Repository<T>` interface defines standard CRUD operations (`Repository.java:13`)
- `InMemoryRepository<T>` provides thread-safe in-memory implementation (`InMemoryRepository.java:20`)
- Servlets depend on the `Repository` interface, not concrete implementation (Dependency Inversion)

**Benefits**:
- Easy to swap storage implementations (in-memory → database)
- Improved testability (repositories can be mocked)
- Separation of concerns

### 2. Template Method Pattern

**Purpose**: Define skeleton of HTTP handling in base class, with specific implementations in subclasses.

**Implementation**:
- `BaseServlet` provides common methods for all servlets (`BaseServlet.java:18`)
  - `readRequestBody()` - Parse JSON from request
  - `sendJsonResponse()` - Send JSON response
  - `sendErrorResponse()` - Send error with proper status code
  - `extractResourceId()` - Extract ID from URL path

**Benefits**:
- DRY (Don't Repeat Yourself) - common logic written once
- Consistent error handling across all endpoints
- Easier to add new resource servlets

### 3. Dependency Injection

**Purpose**: Pass dependencies to classes rather than having them create dependencies.

**Implementation**:
- `ApiServer` creates repositories and injects them into servlets (`ApiServer.java:60`)
- Each servlet receives its repository via constructor (`DealServlet.java:37`)

**Benefits**:
- Loose coupling between components
- Easier testing (can inject mock repositories)
- Single source of truth for dependency creation

### 4. Singleton Pattern

**Purpose**: Ensure only one instance of a resource exists.

**Implementation**:
- `JsonHelper` uses a single, configured `Gson` instance (`JsonHelper.java:29`)

**Benefits**:
- Consistent JSON formatting across application
- Efficient (no repeated configuration)

### 5. Embedded Server Pattern

**Purpose**: Package web server with application for easy deployment.

**Implementation**:
- `ApiServer` embeds Tomcat programmatically (`ApiServer.java:50`)
- No external server configuration needed

**Benefits**:
- Self-contained application
- Simplified deployment (single JAR)
- Easier development and testing

## Design Principles

### SOLID Principles

#### Single Responsibility Principle (SRP)
Each class has one reason to change:
- `JsonHelper` only handles JSON conversion
- `DealServlet` only handles Deal HTTP requests
- `InMemoryRepository` only handles data storage

#### Open/Closed Principle (OCP)
- `BaseServlet` is open for extension (can create new servlets) but closed for modification
- New servlets extend `BaseServlet` without changing its code

#### Liskov Substitution Principle (LSP)
- Any `Repository<T>` implementation can be substituted for another
- All servlet implementations can be used interchangeably where `HttpServlet` is expected

#### Interface Segregation Principle (ISP)
- `Repository<T>` interface contains only essential CRUD methods
- No client is forced to depend on methods it doesn't use

#### Dependency Inversion Principle (DIP)
- High-level servlets depend on `Repository` abstraction, not concrete `InMemoryRepository`
- Both depend on the interface, enabling flexible implementation swapping

### Additional Principles

#### DRY (Don't Repeat Yourself)
- Common HTTP logic centralized in `BaseServlet`
- Generic `Repository<T>` works with any entity type

#### Separation of Concerns
- HTTP handling → Servlet layer
- Data access → Repository layer
- JSON conversion → JsonHelper utility
- Business logic → Domain models

## API Endpoints

All endpoints are prefixed with `/api/v1/` and return JSON responses.

### Deals

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/deals` | List all deals (optional: `?status=WON&salesRepId=USER-001`) | 200, 400 |
| GET | `/deals/{id}` | Get specific deal | 200, 404 |
| POST | `/deals` | Create new deal | 201, 400 |
| PUT | `/deals/{id}` | Update existing deal | 200, 400, 404 |
| DELETE | `/deals/{id}` | Delete deal | 204, 404 |

### Users

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/users` | List all users (optional: `?role=SALES_REP`) | 200, 400 |
| GET | `/users/{id}` | Get specific user | 200, 404 |
| POST | `/users` | Create new user | 201, 400 |
| PUT | `/users/{id}` | Update existing user | 200, 400, 404 |
| DELETE | `/users/{id}` | Delete user | 204, 404 |

### Commission Plans

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/commission-plans` | List all plans (optional: `?status=ACTIVE`) | 200, 400 |
| GET | `/commission-plans/{id}` | Get specific plan | 200, 404 |
| POST | `/commission-plans` | Create new plan | 201, 400 |
| PUT | `/commission-plans/{id}` | Update existing plan | 200, 400, 404 |
| DELETE | `/commission-plans/{id}` | Delete plan | 204, 404 |

### Disputes

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| GET | `/disputes` | List all disputes (optional: `?status=UNDER_REVIEW`) | 200, 400 |
| GET | `/disputes/{id}` | Get specific dispute | 200, 404 |
| POST | `/disputes` | Create new dispute | 201, 400 |
| PUT | `/disputes/{id}` | Update existing dispute | 200, 400, 404 |
| DELETE | `/disputes/{id}` | Delete dispute | 204, 404 |

### HTTP Status Codes

- **200 OK**: Successful GET or PUT request
- **201 Created**: Successful POST request (resource created)
- **204 No Content**: Successful DELETE request (no response body)
- **400 Bad Request**: Invalid input data or query parameters
- **404 Not Found**: Resource with specified ID doesn't exist

## Running the Server

### From Command Line

```bash
# Compile the project
mvn clean compile

# Run the API server
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.ApiServer"

# Or run with custom port (sample data loaded by default)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.ApiServer" -Dexec.args="8081"

# Run without sample data (empty repositories)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.ApiServer" -Dexec.args="--no-sample-data"

# Run with custom port and no sample data
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.ApiServer" -Dexec.args="8081 --no-sample-data"
```

### From JAR

```bash
# Build the JAR
mvn clean package

# Run the server (sample data loaded by default)
java -cp target/commission-calculator.jar com.chapman.edu.commissions.api.rest.ApiServer

# Or with custom port (sample data still loaded)
java -cp target/commission-calculator.jar com.chapman.edu.commissions.api.rest.ApiServer 8081

# Run without sample data (empty repositories)
java -cp target/commission-calculator.jar com.chapman.edu.commissions.api.rest.ApiServer --no-sample-data

# Run with custom port and no sample data
java -cp target/commission-calculator.jar com.chapman.edu.commissions.api.rest.ApiServer 8081 --no-sample-data
```

### Command Line Arguments

| Argument | Description | Example |
|----------|-------------|---------|
| `[port]` | Custom port number (default: 8080) | `8081` |
| `--no-sample-data` | Start with empty repositories (sample data loaded by default) | `--no-sample-data` |

**Sample Data Loaded by Default:**
- 6 Users (sales reps, managers, admin)
- 8 Deals (various statuses: OPEN, WON, LOST, CANCELLED)
- 4 Commission Plans (ACTIVE, DRAFT, ARCHIVED)
- 4 Disputes (INITIATED, UNDER_REVIEW, ESCALATED, RESOLVED)

### Expected Output

**With Sample Data (Default):**
```
Starting Commission Calculator API Server...
Port: 8080
✓ Servlets registered successfully

=== Loading Sample Data ===
Loading sample data...
Sample data loaded successfully!
  Users: 6
  Deals: 8
  Commission Plans: 4
  Disputes: 4
===========================

✓ Server started successfully!

API Endpoints:
  - http://localhost:8080/api/v1/deals
  - http://localhost:8080/api/v1/users
  - http://localhost:8080/api/v1/commission-plans
  - http://localhost:8080/api/v1/disputes

Press Ctrl+C to stop the server.
```

## Testing the API

### Using curl

#### Create a Deal
```bash
curl -X POST http://localhost:8080/api/v1/deals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Enterprise Software License",
    "value": 100000.00,
    "salesRepId": "USER-001",
    "status": "OPEN"
  }'
```

#### Get All Deals
```bash
curl http://localhost:8080/api/v1/deals
```

#### Get Specific Deal
```bash
curl http://localhost:8080/api/v1/deals/DEAL-001
```

#### Update a Deal
```bash
curl -X PUT http://localhost:8080/api/v1/deals/DEAL-001 \
  -H "Content-Type: application/json" \
  -d '{
    "id": "DEAL-001",
    "title": "Enterprise Software License - Updated",
    "value": 150000.00,
    "salesRepId": "USER-001",
    "status": "WON"
  }'
```

#### Delete a Deal
```bash
curl -X DELETE http://localhost:8080/api/v1/deals/DEAL-001
```

#### Filter Deals by Status
```bash
curl "http://localhost:8080/api/v1/deals?status=WON"
```

### Using Browser

Navigate to:
- http://localhost:8080/api/v1/deals
- http://localhost:8080/api/v1/users

For POST/PUT/DELETE operations, use browser extensions like:
- Postman
- RESTClient
- Advanced REST Client

## Project Structure

```
src/main/java/com/chapman/edu/commissions/api/
├── ApiServer.java              # Embedded Tomcat server and main entry point
├── BaseServlet.java            # Abstract base class with common HTTP logic
├── DealServlet.java            # Deal resource endpoint handler
├── UserServlet.java            # User resource endpoint handler
├── CommissionPlanServlet.java  # Commission plan endpoint handler
├── DisputeServlet.java         # Dispute endpoint handler
├── Repository.java             # Generic repository interface
├── InMemoryRepository.java     # In-memory repository implementation
├── JsonHelper.java             # JSON serialization utility
├── openapi.yaml                # OpenAPI 3.0 specification
├── api-architecture.puml       # PlantUML architecture diagram
├── README.md                   # This file
├── QUESTIONS.md                # Educational questions about concepts
└── ANSWERS.md                  # Answers to educational questions
```

## Key Concepts

### RESTful API Design

REST (Representational State Transfer) is an architectural style for designing networked applications. Key principles:

1. **Resource-Based**: Everything is a resource (Deal, User, etc.)
2. **Standard HTTP Methods**:
   - GET for reading
   - POST for creating
   - PUT for updating
   - DELETE for deleting
3. **Stateless**: Each request contains all information needed
4. **JSON Format**: Standard data interchange format

### Embedded Tomcat

Traditional web applications require deployment to external servers (Tomcat, Jetty, etc.). Embedded servers:

- Package the server with the application
- No separate installation/configuration needed
- Simplified deployment (single JAR)
- Easier development and testing

Our implementation uses `org.apache.tomcat.embed:tomcat-embed-core` to run Tomcat programmatically.

### Thread Safety

`InMemoryRepository` uses thread-safe collections:
- `ConcurrentHashMap`: Thread-safe map for storing entities
- `AtomicLong`: Thread-safe counter for ID generation

This ensures the API can handle concurrent requests safely.

### Generic Programming

`Repository<T>` and `InMemoryRepository<T>` use Java generics:
- Single implementation works with any entity type
- Type safety at compile time
- No code duplication

### Dependency Inversion

High-level modules (servlets) don't depend on low-level modules (repositories). Both depend on abstractions (Repository interface).

**Benefits**:
- Can swap `InMemoryRepository` for `DatabaseRepository` without changing servlets
- Easier testing with mock repositories
- Follows SOLID principles

## OpenAPI Specification

The `openapi.yaml` file provides a machine-readable API specification following OpenAPI 3.0 standard. It documents:

- All endpoints and operations
- Request/response schemas
- HTTP status codes
- Query parameters
- Data models

Tools like Swagger UI can render this as interactive documentation.

## Architecture Diagram

The `api-architecture.puml` file contains a PlantUML class diagram showing:

- All classes and their relationships
- Design patterns applied
- Component annotations explaining concepts
- Visual representation of the architecture

Generate the diagram using PlantUML tools or online renderers like plantuml.com.

## Learning Objectives

This implementation demonstrates:

1. **REST API Design**: Standard endpoints, HTTP methods, status codes
2. **Servlet Programming**: Request handling, response generation
3. **Embedded Servers**: Self-contained application deployment
4. **Design Patterns**: Repository, Template Method, Dependency Injection, Singleton
5. **SOLID Principles**: SRP, OCP, LSP, ISP, DIP applied throughout
6. **Thread Safety**: Concurrent data structure usage
7. **Generic Programming**: Type-safe, reusable components
8. **JSON Handling**: Serialization with custom type adapters
9. **Separation of Concerns**: Clear layer boundaries
10. **Clean Code**: Well-commented, self-documenting code

## Next Steps

To extend this implementation:

1. **Add Database Support**: Replace `InMemoryRepository` with JPA/Hibernate repository
2. **Add Authentication**: Implement JWT-based authentication
3. **Add Validation**: Use Bean Validation API for input validation
4. **Add Pagination**: Implement page/limit query parameters
5. **Add Sorting**: Allow sorting results by different fields
6. **Add Filtering**: More advanced query capabilities
7. **Add CORS Support**: Enable cross-origin requests
8. **Add API Versioning**: Support multiple API versions
9. **Add Rate Limiting**: Prevent API abuse
10. **Add Logging**: Comprehensive request/response logging

## References

- [Jakarta Servlet Specification](https://jakarta.ee/specifications/servlet/)
- [Apache Tomcat Documentation](https://tomcat.apache.org/tomcat-10.1-doc/)
- [REST API Design Best Practices](https://restfulapi.net/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Design Patterns in Java](https://refactoring.guru/design-patterns)
- [SOLID Principles](https://www.digitalocean.com/community/conceptual_articles/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)