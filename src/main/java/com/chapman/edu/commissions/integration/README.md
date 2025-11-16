# Commission Calculator Integration Application

## Overview

This is an **end-to-end Commission Calculator Application** built using a **Layered Model-View-Controller (MVC) Architecture** with plain Java (no Spring framework). The application demonstrates modern software engineering principles and patterns through a sales commission management domain.

## Architecture

The application follows a **3-Tier Layered Architecture**:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  Controllers (Servlets) + Security Filter + Swagger UI      │
│  - DealController: Handles HTTP requests/responses          │
│  - AuthenticationFilter: HTTP Basic Auth                    │
│  - SwaggerServlet: API documentation                        │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                       │
│                    Services                                  │
│  - DealService: Business rules and validation               │
│  - UserService: User management and authentication          │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                   DATA ACCESS LAYER                          │
│                    Repositories                              │
│  - H2DealRepository: JDBC-based Deal persistence            │
│  - H2UserRepository: JDBC-based User persistence            │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                     DATABASE LAYER                           │
│                   H2 Database                                │
│  - DatabaseManager: Connection management and schema init   │
│  - File-based persistence: ./data/commissions               │
└─────────────────────────────────────────────────────────────┘
```

## Key Features

### 1. **Layered MVC Architecture**
- **Controller Layer**: Servlets handle HTTP requests and responses
- **Service Layer**: Business logic, validation, and orchestration
- **Repository Layer**: Data access with JDBC
- **Database Layer**: H2 embedded database with file persistence

### 2. **RESTful API**
- Full CRUD operations for Deals
- Query parameter filtering (by status, sales rep)
- Action-based endpoints (e.g., close deal as WON)
- Proper HTTP status codes (200, 201, 204, 400, 401, 404, 409, 500)

### 3. **H2 Database Integration**
- Embedded database with file persistence
- Web-based H2 Console for database management
- JDBC-based repositories (no ORM framework)
- Database schema auto-initialization
- SQL DDL and DML operations

### 4. **Security**
- HTTP Basic Authentication
- Servlet Filter for request interception
- User authentication via UserService
- Protected API endpoints
- Public endpoints for Swagger and H2 Console

### 5. **API Documentation**
- Swagger UI for interactive API testing
- OpenAPI 3.0 specification
- Self-documenting REST API
- Try-it-out functionality in browser

### 6. **Testing**
- Integration tests across all layers
- JUnit 5 with comprehensive test coverage
- Database reset between tests for isolation
- Business logic validation testing

## Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Web Server** | Embedded Tomcat 10.1.15 | Servlet container |
| **Database** | H2 2.2.224 | Embedded SQL database |
| **Persistence** | JDBC | Database access |
| **JSON** | Gson 2.10.1 | JSON serialization |
| **API Docs** | Swagger UI 5.10.3 | Interactive API documentation |
| **Logging** | SLF4J + Logback | Application logging |
| **Testing** | JUnit 5 | Unit and integration testing |
| **Build** | Maven | Dependency management and build |

## Project Structure

```
src/main/java/com/chapman/edu/commissions/integration/
├── controller/
│   ├── DealController.java           # MVC Controller for Deal endpoints
│   └── UserController.java           # MVC Controller for User endpoints
├── dto/                               # ⭐ Data Transfer Objects (DTO Pattern)
│   ├── DealDTO.java                  # Deal response DTO
│   ├── DealProductDTO.java           # Nested product DTO
│   ├── CreateDealRequest.java        # Deal creation request DTO
│   ├── UpdateDealRequest.java        # Deal update request DTO
│   ├── UserDTO.java                  # User response DTO (no password!)
│   ├── DealMapper.java               # DTO ↔ Entity mapper for Deals
│   └── UserMapper.java               # DTO ↔ Entity mapper for Users
├── service/
│   ├── DealService.java              # Business logic for Deals
│   └── UserService.java              # Business logic for Users
├── repository/
│   ├── H2DealRepository.java         # JDBC repository for Deals
│   └── H2UserRepository.java         # JDBC repository for Users
├── database/
│   └── DatabaseManager.java          # Database connection and schema management
├── security/
│   └── AuthenticationFilter.java    # HTTP Basic Auth filter
├── servlet/
│   ├── BaseServlet.java              # Base servlet with common functionality
│   ├── JsonHelper.java               # JSON serialization utilities
│   └── SwaggerServlet.java           # Swagger UI and OpenAPI spec
└── IntegrationApplication.java       # Main application entry point
```

## Running the Application

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Build and Run

```bash
# Clean and compile
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.IntegrationApplication"

# Or build and run the JAR
mvn package
java -jar target/commission-calculator.jar
```

### Access Points

Once started, the application is available at:

| Endpoint | URL | Purpose | Authentication |
|----------|-----|---------|----------------|
| **API** | http://localhost:8080/api/v1/integration/deals | RESTful API | Required |
| **Swagger UI** | http://localhost:8080/swagger-ui/ | Interactive API docs | None |
| **H2 Console** | http://localhost:8080/h2-console | Database management | None |

### H2 Database Console

Access the H2 Console at: http://localhost:8080/h2-console

**Connection Settings:**
- **JDBC URL**: `jdbc:h2:./data/commissions`
- **User Name**: `sa`
- **Password**: (leave empty)

### Authentication

The API uses HTTP Basic Authentication.

**Test Credentials:**
- **Email**: `john.doe@example.com`
- **Password**: `password`

**Using cURL:**
```bash
# Get all deals
curl -u john.doe@example.com:password http://localhost:8080/api/v1/integration/deals

# Create a deal
curl -u john.doe@example.com:password \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"title":"New Deal","customerName":"ABC Corp","salesRepId":"USER-xxx","products":[{"name":"Software","price":10000,"quantity":1}]}' \
  http://localhost:8080/api/v1/integration/deals
```

## API Endpoints

### Deals

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/integration/deals` | Get all deals | Yes |
| GET | `/api/v1/integration/deals?status=OPEN` | Get deals by status | Yes |
| GET | `/api/v1/integration/deals?salesRepId={id}` | Get deals by sales rep | Yes |
| GET | `/api/v1/integration/deals/{id}` | Get specific deal | Yes |
| POST | `/api/v1/integration/deals` | Create new deal | Yes |
| PUT | `/api/v1/integration/deals/{id}` | Update deal | Yes |
| DELETE | `/api/v1/integration/deals/{id}` | Delete deal (OPEN only) | Yes |
| POST | `/api/v1/integration/deals/{id}/close` | Close deal as WON | Yes |

### Sample Request/Response

**Create Deal Request** (CreateDealRequest DTO):
```json
{
  "title": "Enterprise Software License",
  "salesRepId": "USER-xxx",
  "products": [
    {
      "productId": "PROD-001",
      "productName": "Software License",
      "price": 50000.00,
      "quantity": 1,
      "discount": 0
    },
    {
      "productId": "PROD-002",
      "productName": "Training Package",
      "price": 10000.00,
      "quantity": 1,
      "discount": 0
    }
  ]
}
```

**Note:** CreateDealRequest does NOT include:
- `id` (server-generated)
- `status` (server-controlled, defaults to OPEN)
- `createdDate` / `lastModifiedDate` (server-controlled)
- `closeDate` (set when deal is closed)
- `totalValue` (computed from products)

**Response (201 Created)** (DealDTO):
```json
{
  "id": "DEAL-123e4567-e89b-12d3-a456-426614174000",
  "title": "Enterprise Software License",
  "status": "OPEN",
  "salesRepId": "USER-xxx",
  "closeDate": null,
  "products": [
    {
      "productId": "PROD-001",
      "productName": "Software License",
      "price": 50000.00,
      "quantity": 1,
      "discount": 0
    },
    {
      "productId": "PROD-002",
      "productName": "Training Package",
      "price": 10000.00,
      "quantity": 1,
      "discount": 0
    }
  ],
  "totalValue": 60000.00,
  "createdDate": "2025-11-08",
  "lastModifiedDate": "2025-11-08"
}
```

**Note:** DealDTO includes computed and server-controlled fields that weren't in the request.

## Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=IntegrationApplicationTest

# Run with coverage report
mvn clean test jacoco:report
```

### Test Coverage

The integration tests cover:
- User creation and validation
- User authentication (valid/invalid credentials)
- Deal CRUD operations
- Business rule validation (e.g., can't delete closed deals)
- Deal filtering by status and sales rep
- Deal lifecycle (create → update → close → delete)
- Pipeline value calculations

## Design Patterns and Principles

### Design Patterns Used

1. **MVC (Model-View-Controller)**
   - Controllers: DealController (Servlets)
   - Models: Deal, User, DealProduct domain objects
   - Views: JSON representations

2. **DTO Pattern (Data Transfer Object)** ⭐ NEW
   - Decouples API from internal domain model
   - Separate DTOs for requests (`CreateDealRequest`, `UpdateDealRequest`) and responses (`DealDTO`, `UserDTO`)
   - Mappers handle conversion between DTOs and entities (`DealMapper`, `UserMapper`)
   - Security: UserDTO excludes sensitive fields like passwords
   - Benefits:
     - API contract independent of database structure
     - Can change domain entities without breaking API consumers
     - Clear separation between what clients send vs receive
     - Server-controlled fields (id, createdDate) not in request DTOs
   - Location: `com.chapman.edu.commissions.integration.dto` package

3. **Repository Pattern**
   - Abstracts data access logic
   - Repository interface with H2 implementations
   - Separation of business logic from persistence

4. **Service Layer Pattern**
   - Encapsulates business logic
   - Coordinates between controllers and repositories
   - Transaction boundaries

5. **Mapper Pattern** ⭐ NEW
   - Centralized conversion logic between DTOs and entities
   - Static methods for stateless transformation
   - Null-safe conversions
   - Bidirectional mapping (DTO→Entity and Entity→DTO)
   - Example: `DealMapper.toDTO(deal)` converts Deal entity to DealDTO

6. **Singleton Pattern**
   - DatabaseManager (single database connection)

7. **Filter/Interceptor Pattern**
   - AuthenticationFilter for cross-cutting security concerns

8. **Dependency Injection**
   - Manual constructor-based DI
   - Loose coupling between layers

### SOLID Principles

- **Single Responsibility**: Each class has one reason to change
  - DealController: HTTP handling only
  - DealService: Business logic only
  - H2DealRepository: Data access only

- **Open/Closed**: Extensible without modification
  - Repository interface allows different implementations
  - Service layer can be extended with new business rules

- **Liskov Substitution**: Can swap Repository implementations
  - H2DealRepository can be replaced with MySQL, Postgres, etc.

- **Interface Segregation**: Focused interfaces
  - Repository interface provides only essential CRUD methods

- **Dependency Inversion**: Depend on abstractions
  - DealService depends on Repository interface, not concrete implementation

### Other Principles

- **Separation of Concerns**: Each layer has distinct responsibilities
- **DRY (Don't Repeat Yourself)**: Common logic in base classes and services
- **KISS (Keep It Simple)**: Straightforward implementations without over-engineering

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Deals Table
```sql
CREATE TABLE deals (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    value DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    sales_rep_id VARCHAR(255) NOT NULL,
    expected_close_date DATE,
    actual_close_date DATE,
    products TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sales_rep_id) REFERENCES users(id)
);
```

## Key Concepts Demonstrated

### 1. Layered Architecture with DTO Pattern
- Clear separation between presentation, business, and data access layers
- **DTO Layer** sits between HTTP and domain model:
  - Controllers receive DTOs from clients
  - Mappers convert DTOs to entities before calling services
  - Services work with domain entities (not DTOs)
  - Mappers convert entities back to DTOs for responses
- Each layer communicates only with adjacent layers
- Dependencies flow downward (Controller → DTO → Service → Repository)
- Data transformation happens at layer boundaries

### 2. JDBC and Database Management
- Connection management with DatabaseManager
- PreparedStatement for SQL injection prevention
- ResultSet mapping to domain objects
- DDL for schema creation
- Foreign key relationships and constraints

### 3. HTTP and REST
- RESTful resource design
- Proper HTTP verbs (GET, POST, PUT, DELETE)
- Status codes reflect operation results
- Query parameters for filtering
- JSON request/response bodies

### 4. Security
- Authentication filter intercepts requests
- HTTP Basic Auth implementation
- Base64 credential decoding
- Public vs. protected endpoints
- User credentials stored in database

### 5. Validation and Error Handling
- Business rule validation in service layer
- Meaningful error messages
- HTTP status codes for different error types
- Exception translation across layers

## Educational Value

This application serves as a comprehensive example of:

1. **Enterprise Application Architecture**
   - Real-world layered architecture
   - Separation of concerns
   - Dependency management

2. **JDBC and Database Programming**
   - Connection pooling concepts
   - SQL DDL and DML
   - Object-relational mapping
   - Transaction management

3. **Web Development**
   - Servlet API usage
   - HTTP protocol understanding
   - RESTful API design
   - Filter chains

4. **Security Practices**
   - Authentication mechanisms
   - Request interception
   - Credential validation

5. **Testing Strategies**
   - Integration testing
   - Test isolation
   - Test data management

## Future Enhancements

Potential improvements for learning purposes:

1. **Connection Pooling**: Use HikariCP instead of single connection
2. **Password Hashing**: Use bcrypt for secure password storage
3. **JWT Authentication**: Replace Basic Auth with token-based auth
4. **Transaction Management**: Implement explicit transaction boundaries
5. **Caching**: Add caching layer for frequently accessed data
6. **Validation Framework**: Use Bean Validation (JSR 380)
7. **Exception Handling**: Global exception handler with @ControllerAdvice pattern
8. **Audit Logging**: Track all CRUD operations with timestamps and users
9. **Rate Limiting**: Prevent API abuse with rate limiting filter
10. **CORS Support**: Enable cross-origin requests for frontend integration

## License

Educational use for Chapman University Commission Calculator project.

## Contact

Chapman University - Software Engineering Course