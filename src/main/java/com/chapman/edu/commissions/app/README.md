# Deal Management Application

## Overview

The **Deal Management Application** is a comprehensive educational example demonstrating enterprise-grade RESTful web application architecture using Java, Jakarta Servlets, and embedded Tomcat. Built on top of the integration layer classes, this application showcases best practices in layered architecture, separation of concerns, and SOLID design principles.

## Table of Contents

1. [What Was Created](#what-was-created)
2. [Architecture Overview](#architecture-overview)
3. [Design Patterns](#design-patterns)
4. [Technology Stack](#technology-stack)
5. [Running the Application](#running-the-application)
6. [API Endpoints](#api-endpoints)
7. [Architectural Diagrams](#architectural-diagrams)
8. [Educational Value](#educational-value)
9. [Code Structure](#code-structure)

---

## What Was Created

This implementation consists of:

### 1. Application Class
- **`DealManagementApp.java`** - Main application entry point with extensive documentation
  - Application bootstrap and dependency injection
  - Embedded Tomcat server configuration
  - Sample data initialization
  - Graceful shutdown handling
  - Comprehensive inline comments explaining architectural concepts

### 2. C4 Architecture Diagrams

The following PlantUML diagrams document the system architecture at different levels:

- **`context-app.puml`** - System Context Diagram (C4 Level 1)
  - Shows the big picture: users, system, and external dependencies
  - Illustrates how the application fits into its environment

- **`container-app.puml`** - Container Diagram (C4 Level 2)
  - Shows high-level technical building blocks
  - Illustrates: Web Server, REST API, Business Logic, Data Access, Database

- **`app-component.puml`** - Component Diagram (C4 Level 3)
  - Shows key components within each container
  - Illustrates: Controllers, Services, Repositories, and their relationships

- **`app-sequence.puml`** - Sequence Diagram (Runtime Behavior)
  - Shows runtime interaction when creating a deal
  - Illustrates complete request/response flow through all layers

- **`app-class.puml`** - Class Diagram (Implementation View)
  - Shows static structure with all classes and relationships
  - Illustrates inheritance, composition, and dependencies

### 3. Documentation
- **`README.md`** (this file) - Comprehensive documentation of the application

---

## Architecture Overview

The application follows a **Layered Architecture** pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│              Presentation Layer                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │ DealController (HttpServlet)                      │  │
│  │ - Handles HTTP requests/responses                 │  │
│  │ - JSON serialization/deserialization              │  │
│  │ - HTTP status code mapping                        │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│           Business Logic Layer                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │ DealService                                       │  │
│  │ - Business rule validation                        │  │
│  │ - Business logic enforcement                      │  │
│  │ - Complex operations                              │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│            Data Access Layer                             │
│  ┌──────────────────────────────────────────────────┐  │
│  │ H2DealRepository (implements Repository<Deal>)    │  │
│  │ - CRUD operations                                 │  │
│  │ - SQL query execution                             │  │
│  │ - Object-Relational Mapping                       │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Database Layer                              │
│  ┌──────────────────────────────────────────────────┐  │
│  │ H2 Embedded Database                              │  │
│  │ - File-based persistence                          │  │
│  │ - JDBC connectivity                               │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### 1. Presentation Layer (Controller)
- **Location**: `DealController.java`
- **Responsibilities**:
  - Parse HTTP requests
  - Validate request format (not business rules)
  - Delegate to service layer
  - Format HTTP responses
  - Map exceptions to HTTP status codes
- **Technology**: Jakarta Servlets
- **Principle**: Thin controller - no business logic

#### 2. Business Logic Layer (Service)
- **Location**: `DealService.java`, `UserService.java`
- **Responsibilities**:
  - Enforce business rules
  - Validate domain constraints
  - Coordinate complex operations
  - Provide business-level queries
- **Examples**:
  - "Deal value must be positive"
  - "Can only delete OPEN deals"
  - "Cannot modify CANCELLED deals"
- **Principle**: Single source of truth for business rules

#### 3. Data Access Layer (Repository)
- **Location**: `H2DealRepository.java`, `H2UserRepository.java`
- **Responsibilities**:
  - CRUD operations
  - SQL query execution
  - Object-Relational Mapping
  - ID generation
- **Pattern**: Repository Pattern
- **Principle**: Abstracts database implementation details

#### 4. Database Layer
- **Location**: `DatabaseManager.java`
- **Responsibilities**:
  - Connection pooling
  - Schema initialization
  - Connection lifecycle management
- **Technology**: H2 embedded database

---

## Design Patterns

### 1. Layered Architecture
- **Purpose**: Organize code by technical responsibility
- **Benefits**:
  - Clear separation of concerns
  - Independent layer testing
  - Easy to understand and maintain
  - Flexible (can swap implementations)

### 2. Repository Pattern
- **Purpose**: Abstract data access behind collection-like interface
- **Implementation**: `Repository<T>` interface
- **Benefits**:
  - Hide SQL/JDBC complexity
  - Swappable implementations (H2 → MySQL, PostgreSQL, etc.)
  - Testable (mock repositories)
- **Methods**:
  - `findAll()`, `findById()`, `save()`, `deleteById()`, `generateId()`

### 3. Dependency Injection (Manual)
- **Purpose**: Provide dependencies from outside
- **Implementation**: Constructor injection
- **Flow**:
  ```
  DatabaseManager → Repository → Service → Controller
  ```
- **Benefits**:
  - Loose coupling
  - Testability (inject mocks)
  - Flexibility (swap implementations)

### 4. MVC Pattern (Modified for REST)
- **Model**: `Deal`, `DealProduct`, `User` (domain objects)
- **View**: JSON responses (instead of HTML)
- **Controller**: `DealController` (request routing and response formatting)

### 5. Singleton Pattern
- **Purpose**: Ensure single instance
- **Implementation**: `DatabaseManager.getInstance()`
- **Benefits**:
  - Single connection pool
  - Consistent database state
  - Thread-safe access

### 6. Facade Pattern
- **Purpose**: Simplify complex subsystem
- **Implementation**: `DealManagementApp` (hides bootstrap complexity)
- **Benefits**:
  - Simple interface to start/stop application
  - Coordinates multiple components

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| **Web Server** | Apache Tomcat 10.1 (embedded) |
| **HTTP Framework** | Jakarta Servlets |
| **Database** | H2 (embedded, file-based) |
| **Data Access** | JDBC with PreparedStatements |
| **JSON Processing** | Gson |
| **Logging** | SLF4J with Logback |
| **Build Tool** | Maven |
| **Java Version** | Java 21 |

---

## Running the Application

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Start the Application

```bash
# From project root directory
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementApp"
```

### Expected Output

```
================================================================================
Starting Deal Management Application
================================================================================
[1/7] Initializing database...
[2/7] Creating repositories (Data Access Layer)...
[3/7] Creating services (Business Logic Layer)...
[4/7] Creating controllers (Presentation Layer)...
[5/7] Configuring embedded Tomcat server...
[6/7] Loading sample data...
[7/7] Starting Tomcat server...
================================================================================
Deal Management Application started successfully!
================================================================================

Server Information:
  Server:           Apache Tomcat 10.1
  Port:             8080
  Base URL:         http://localhost:8080

API Endpoints:
  List all deals:   GET    http://localhost:8080/api/v1/integration/deals
  Get deal by ID:   GET    http://localhost:8080/api/v1/integration/deals/{id}
  Create deal:      POST   http://localhost:8080/api/v1/integration/deals
  Update deal:      PUT    http://localhost:8080/api/v1/integration/deals/{id}
  Delete deal:      DELETE http://localhost:8080/api/v1/integration/deals/{id}
  Close deal:       POST   http://localhost:8080/api/v1/integration/deals/{id}/close

Development Tools:
  H2 Console:       http://localhost:8080/h2-console

Database Connection:
  JDBC URL:         jdbc:h2:./data/commissions
  Username:         sa
  Password:         (empty)
```

### Verify It's Running

```bash
# Get all deals
curl http://localhost:8080/api/v1/integration/deals

# Get deals by status
curl http://localhost:8080/api/v1/integration/deals?status=OPEN
```

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1/integration/deals
```

### Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| **GET** | `/deals` | List all deals | 200 OK |
| **GET** | `/deals?status=OPEN` | Filter by status | 200 OK |
| **GET** | `/deals?salesRepId=USER-123` | Filter by sales rep | 200 OK |
| **GET** | `/deals/{id}` | Get specific deal | 200 OK, 404 Not Found |
| **POST** | `/deals` | Create new deal | 201 Created, 400 Bad Request |
| **PUT** | `/deals/{id}` | Update deal | 200 OK, 400 Bad Request, 404 Not Found |
| **DELETE** | `/deals/{id}` | Delete deal | 204 No Content, 404 Not Found, 409 Conflict |
| **POST** | `/deals/{id}/close` | Close deal as WON | 200 OK, 404 Not Found, 409 Conflict |

### Example Requests

#### Create a Deal
```bash
curl -X POST http://localhost:8080/api/v1/integration/deals \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "New Enterprise Deal",
    "salesRepId": "USER-123",
    "status": "OPEN",
    "products": [
      {
        "productId": "PROD-001",
        "name": "Software License",
        "quantity": 1,
        "unitPrice": 50000.00
      }
    ]
  }'
```

#### Update a Deal
```bash
curl -X PUT http://localhost:8080/api/v1/integration/deals/DEAL-abc123 \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Updated Deal Title",
    "salesRepId": "USER-123",
    "status": "OPEN",
    "products": [...]
  }'
```

#### Close a Deal
```bash
curl -X POST http://localhost:8080/api/v1/integration/deals/DEAL-abc123/close
```

#### Delete a Deal
```bash
curl -X DELETE http://localhost:8080/api/v1/integration/deals/DEAL-abc123
```

### HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| **200 OK** | Success | GET, PUT, POST (close deal) |
| **201 Created** | Resource created | POST (create deal) |
| **204 No Content** | Success, no response body | DELETE |
| **400 Bad Request** | Validation error | Invalid input, missing required fields |
| **404 Not Found** | Resource not found | GET, PUT, DELETE on non-existent deal |
| **409 Conflict** | Business rule violation | Delete closed deal, close non-open deal |
| **500 Internal Server Error** | Unexpected error | Database error, system error |

---

## Architectural Diagrams

### 1. System Context Diagram (`context-app.puml`)
**C4 Level 1** - Shows the big picture

- **Actors**: Sales Representatives, Sales Managers, System Administrators
- **System**: Deal Management Application
- **External Systems**: H2 Database
- **Purpose**: Understand who uses the system and what it depends on

### 2. Container Diagram (`container-app.puml`)
**C4 Level 2** - Shows high-level technical building blocks

- **Containers**:
  - Embedded Tomcat Web Server
  - REST API (Jakarta Servlets)
  - Business Logic Layer (Services)
  - Data Access Layer (Repositories)
  - H2 Database Console
  - H2 Database
- **Purpose**: Understand the application's runtime structure

### 3. Component Diagram (`app-component.puml`)
**C4 Level 3** - Shows components within containers

- **Components**:
  - Presentation: DealController
  - Business Logic: DealService, UserService
  - Data Access: H2DealRepository, H2UserRepository, DatabaseManager
  - Domain Model: Deal, DealProduct, User
  - Utilities: JsonHelper
- **Purpose**: Understand the application's internal structure

### 4. Sequence Diagram (`app-sequence.puml`)
**Runtime Behavior** - Shows request/response flow

- **Scenario**: Creating a new deal via POST request
- **Flow**: User → Tomcat → Controller → Service → Repository → Database → Response
- **Purpose**: Understand how components interact at runtime

### 5. Class Diagram (`app-class.puml`)
**Implementation View** - Shows static structure

- **Classes**: All application classes with methods and relationships
- **Relationships**: Inheritance, composition, dependencies
- **Purpose**: Understand the code structure and design patterns

### Viewing Diagrams

The `.puml` files can be viewed using:
1. **PlantUML plugin** in IntelliJ IDEA / VS Code
2. **Online PlantUML editor**: http://www.plantuml.com/plantuml/uml/
3. **C4Builder** (mentioned in pom.xml) - run `docs/run-c4builder.bat`

---

## Educational Value

This application demonstrates:

### 1. Software Architecture Principles

#### SOLID Principles
- **Single Responsibility Principle (SRP)**
  - `DealController`: Only HTTP handling
  - `DealService`: Only business logic
  - `H2DealRepository`: Only data access

- **Open/Closed Principle (OCP)**
  - Can add new `Repository` implementations without changing `DealService`
  - Can add new endpoints without changing existing ones

- **Liskov Substitution Principle (LSP)**
  - Any `Repository<Deal>` implementation can replace `H2DealRepository`

- **Interface Segregation Principle (ISP)**
  - `Repository<T>` interface has only essential methods

- **Dependency Inversion Principle (DIP)**
  - `DealService` depends on `Repository<T>` interface, not `H2DealRepository`
  - High-level modules don't depend on low-level modules

#### Other Principles
- **Separation of Concerns (SoC)**: Each layer has distinct responsibility
- **Don't Repeat Yourself (DRY)**: Business validation in one place
- **Keep It Simple, Stupid (KISS)**: Simple, straightforward design

### 2. Design Patterns
- Layered Architecture
- Repository Pattern
- MVC Pattern
- Singleton Pattern
- Dependency Injection
- Facade Pattern

### 3. RESTful API Design
- Resource-based URLs
- HTTP method semantics (GET, POST, PUT, DELETE)
- Proper status code usage
- JSON request/response format
- Query parameters for filtering

### 4. Enterprise Java Techniques
- Jakarta Servlets
- JDBC with PreparedStatements
- Try-with-resources for resource management
- Exception handling and error reporting
- Logging best practices

### 5. Database Access Patterns
- Object-Relational Mapping (ORM)
- PreparedStatement for SQL injection prevention
- JSON serialization for complex objects
- Connection management
- Schema initialization

---

## Code Structure

```
src/main/java/com/chapman/edu/commissions/
│
├── app/                                    # APPLICATION PACKAGE
│   ├── DealManagementApp.java             # Main application entry point
│   ├── README.md                           # This documentation
│   ├── context-app.puml                    # C4 Level 1: System Context
│   ├── container-app.puml                  # C4 Level 2: Container Diagram
│   ├── app-component.puml                  # C4 Level 3: Component Diagram
│   ├── app-sequence.puml                   # Sequence Diagram
│   └── app-class.puml                      # Class Diagram
│
├── integration/                            # INTEGRATION LAYER (Used by app)
│   ├── controller/
│   │   └── DealController.java            # HTTP request handler
│   ├── service/
│   │   ├── DealService.java               # Business logic
│   │   └── UserService.java               # User management
│   ├── repository/
│   │   ├── H2DealRepository.java          # Deal persistence
│   │   └── H2UserRepository.java          # User persistence
│   ├── database/
│   │   └── DatabaseManager.java           # Connection management
│   └── IntegrationApplication.java        # Alternative entry point
│
├── model/                                  # DOMAIN MODEL
│   ├── Deal.java                          # Deal entity
│   ├── DealProduct.java                   # Product line item
│   ├── DealStatus.java                    # Deal status enum
│   ├── User.java                          # User entity
│   └── UserRole.java                      # User role enum
│
└── api/rest/                               # REST API INFRASTRUCTURE
    ├── Repository.java                    # Repository interface
    ├── BaseServlet.java                   # Base servlet class
    └── JsonHelper.java                    # JSON utilities
```

---

## Key Features

### 1. Comprehensive Documentation
- **Inline Comments**: Every class and method documented with purpose and concepts
- **Architectural Explanations**: Comments explain WHY, not just WHAT
- **Pattern Identification**: Design patterns clearly marked

### 2. Layered Architecture
- **Clear Separation**: Each layer has single responsibility
- **Dependency Management**: Proper dependency flow (Controller → Service → Repository)
- **Testability**: Layers can be tested independently

### 3. RESTful API
- **Resource-Based**: URLs represent resources (deals)
- **HTTP Semantics**: Proper use of GET, POST, PUT, DELETE
- **Status Codes**: Correct HTTP status codes for each scenario
- **Query Parameters**: Filtering via query strings

### 4. Business Rule Enforcement
- **Service Layer Validation**: All business rules in one place
- **Examples**:
  - "Deal title is required"
  - "Deal value must be positive"
  - "Can only delete OPEN deals"
  - "Cannot modify CANCELLED deals"

### 5. Database Access
- **Repository Pattern**: Clean abstraction over JDBC
- **SQL Injection Prevention**: PreparedStatements for all queries
- **ORM Concepts**: ResultSet mapping to domain objects
- **JSON Storage**: Complex objects stored as JSON in database

### 6. Sample Data
- **Pre-loaded Data**: 2 users, 3 deals created on startup
- **Testing Ready**: Immediately testable without manual setup

### 7. Development Tools
- **H2 Console**: Web-based SQL interface at `/h2-console`
- **Comprehensive Logging**: SLF4J logging at all layers
- **Error Messages**: Detailed error messages for debugging

---

## Learning Objectives

By studying this implementation, you will learn:

1. **How to structure a multi-layer web application**
   - Presentation, Business Logic, Data Access layers
   - Dependency flow and separation of concerns

2. **How to implement RESTful APIs**
   - URL design, HTTP methods, status codes
   - Request/response handling with JSON

3. **How to apply design patterns**
   - Repository, MVC, Singleton, Dependency Injection
   - When and why to use each pattern

4. **How to enforce business rules**
   - Where to put validation logic
   - How to separate business rules from infrastructure

5. **How to work with databases in Java**
   - JDBC, PreparedStatements, connection management
   - ORM concepts and object mapping

6. **How to document architecture**
   - C4 model diagrams (Context, Container, Component)
   - Sequence diagrams for runtime behavior
   - Class diagrams for static structure

7. **How to apply SOLID principles**
   - Real-world examples of each principle
   - Benefits of SOLID in practice

---

## Comparison with IntegrationApplication

The `DealManagementApp` is similar to `IntegrationApplication.java` but with:

1. **Enhanced Documentation**: Extensive inline comments explaining every concept
2. **Educational Focus**: Comments designed for learning, not just reference
3. **Pattern Identification**: Design patterns explicitly called out
4. **Architectural Diagrams**: Complete C4 model documentation
5. **README Documentation**: This comprehensive guide

Both applications use the same underlying classes:
- `DealController` (from integration package)
- `DealService` (from integration package)
- `H2DealRepository` (from integration package)
- `DatabaseManager` (from integration package)

---

## Next Steps

To extend this application, consider:

1. **Add Authentication**
   - Implement `AuthenticationFilter` (already in integration package)
   - Add session management
   - Implement role-based access control

2. **Add More Endpoints**
   - User management endpoints
   - Commission calculation endpoints
   - Reporting endpoints

3. **Add Validation**
   - More comprehensive input validation
   - Field-level validation annotations

4. **Add Testing**
   - Unit tests for each layer
   - Integration tests for API endpoints
   - Test with JUnit 5 and Mockito

5. **Add Frontend**
   - React, Angular, or Vue.js frontend
   - Connect to REST API

6. **Migrate Database**
   - Switch from H2 to PostgreSQL or MySQL
   - Demonstrate Repository pattern flexibility

---

## References

- **C4 Model**: https://c4model.com/
- **Jakarta Servlets**: https://jakarta.ee/specifications/servlet/
- **Repository Pattern**: https://martinfowler.com/eaaCatalog/repository.html
- **Layered Architecture**: https://martinfowler.com/bliki/PresentationDomainDataLayering.html
- **SOLID Principles**: https://en.wikipedia.org/wiki/SOLID
- **RESTful API Design**: https://restfulapi.net/

---

## Authors

Commission Calculator Team - Educational Project for Chapman University

---

## License

This is an educational project for demonstration purposes.