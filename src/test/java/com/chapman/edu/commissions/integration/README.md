# Integration Package Testing Implementation

## Overview

This directory contains a comprehensive testing suite demonstrating **Unit, Mock, Integration, and API testing** for a layered MVC (Model-View-Controller) architecture. The tests cover all layers of the integration package and serve as educational examples of professional testing practices in Java enterprise applications.

---

## Architecture Overview

The integration package implements a **4-layer MVC architecture**:

```
┌──────────────────────────────────────┐
│   Presentation Layer (Controller)    │ ← HTTP endpoints, Filters
├──────────────────────────────────────┤
│   Business Logic Layer (Service)     │ ← Business rules, Validation
├──────────────────────────────────────┤
│   Data Access Layer (Repository)     │ ← JDBC, SQL operations
├──────────────────────────────────────┤
│   Database Layer (H2)                │ ← Persistence
└──────────────────────────────────────┘
```

### Components Tested:

- **Controller Layer**: `DealController`, `UserController`, `AuthenticationFilter`
- **Service Layer**: `DealService`, `UserService`
- **Repository Layer**: `H2DealRepository`, `H2UserRepository`
- **Database Layer**: `DatabaseManager`

---

## Testing Strategy

We follow the **Testing Pyramid** approach:

```
        ┌──────────┐
        │   API    │ ← 10% (Slowest, End-to-End)
        ├──────────┤
        │Integration│ ← 20% (Multiple layers)
        ├──────────┤
        │   Mock   │ ← 30% (Pure isolation)
        ├──────────┤
        │   Unit   │ ← 40% (Fastest, Focused)
        └──────────┘
```

### Rationale:

- **Many fast tests at the base**: Quick feedback during development
- **Fewer slow tests at the top**: Comprehensive end-to-end validation
- **Balanced coverage**: Each layer tested at appropriate level

---

## Directory Structure

```
src/test/java/com/chapman/edu/commissions/integration/
│
├── model/
│   └── unit/
│       ├── DealModelUnitTest.java
│       ├── DealProductModelUnitTest.java
│       └── UserModelUnitTest.java
│
├── repository/
│   ├── unit/
│   │   └── H2DealRepositoryUnitTest.java
│   ├── mock/
│   │   └── H2DealRepositoryMockTest.java
│   └── integration/
│       └── H2DealRepositoryIntegrationTest.java
│
├── service/
│   ├── unit/
│   │   └── DealServiceUnitTest.java
│   ├── mock/
│   │   └── DealServiceMockTest.java
│   └── integration/
│       └── DealServiceIntegrationTest.java
│
├── controller/
│   ├── unit/
│   │   └── DealControllerUnitTest.java
│   ├── mock/
│   │   └── DealControllerMockTest.java
│   ├── integration/
│   │   └── DealControllerIntegrationTest.java
│   ├── api/
│   │   └── DealControllerAPITest.java
│   ├── mockserver/
│   │   └── DealControllerMockServerTest.java
│   └── externalserver/
│       └── DealControllerExternalServerTest.java
│
├── security/
│   ├── unit/
│   │   └── AuthenticationFilterUnitTest.java
│   └── integration/
│       └── AuthenticationFilterIntegrationTest.java
│
└── README.md (this file)
```

### Design Principle:

Each layer has its own test directory structure:
- `unit/`: Tests for the layer with minimal mocking
- `mock/`: Tests with all dependencies mocked
- `integration/`: Tests verifying layer integration (layer + layer below)
- `api/`: End-to-end HTTP API tests (controller layer only)
- `mockserver/`: HTTP endpoint mocking using MockServer framework
- `externalserver/`: Tests against external third-party mock API services

---

## Test Types Explained

### 1. Unit Tests

**Purpose**: Test a single component in isolation from external dependencies.

**Characteristics**:
- Fast execution (milliseconds)
- No external I/O (minimal mocking)
- Focus on business logic
- High code coverage

**Example**: `H2DealRepositoryUnitTest`
```java
@Test
void testSaveNewDeal() {
    // Arrange
    Deal deal = createTestDeal(null, "New Deal");

    // Act
    Deal saved = repository.save(deal);

    // Assert
    assertNotNull(saved.getId());
    assertEquals("New Deal", saved.getTitle());
}
```

**When to Use**:
- Testing business logic
- Validating algorithms
- Testing data transformations
- Quick regression testing

---

### 2. Mock Tests

**Purpose**: Test component in complete isolation by mocking ALL dependencies.

**Characteristics**:
- Fastest execution
- No external dependencies
- Tests logic in pure isolation
- Easy to simulate error conditions

**Example**: `H2DealRepositoryMockTest`
```java
@Test
void testFindByIdWithMocks() throws SQLException {
    // Arrange: Mock entire JDBC chain
    when(mockDbManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
    when(mockStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockResultSet.next()).thenReturn(true);
    when(mockResultSet.getString("id")).thenReturn("DEAL-123");

    // Act
    Optional<Deal> result = repository.findById("DEAL-123");

    // Assert
    assertTrue(result.isPresent());
    verify(mockStatement).setString(1, "DEAL-123");
}
```

**When to Use**:
- Testing error handling
- Simulating edge cases
- Verifying method interactions
- Testing without external resources

---

### 3. Integration Tests

**Purpose**: Test multiple components working together with real dependencies.

**Characteristics**:
- Slower than unit tests
- Uses real database
- Verifies component integration
- Tests actual SQL execution

**Example**: `H2DealRepositoryIntegrationTest`
```java
@Test
void testFullCRUDLifecycle() {
    // CREATE
    Deal deal = dealRepository.save(createTestDeal(null, "Integration Test"));
    String dealId = deal.getId();

    // READ
    Optional<Deal> retrieved = dealRepository.findById(dealId);
    assertTrue(retrieved.isPresent());

    // UPDATE
    retrieved.get().setTitle("Updated");
    dealRepository.save(retrieved.get());

    // VERIFY UPDATE
    Optional<Deal> updated = dealRepository.findById(dealId);
    assertEquals("Updated", updated.get().getTitle());

    // DELETE
    boolean deleted = dealRepository.deleteById(dealId);
    assertTrue(deleted);

    // VERIFY DELETE
    assertFalse(dealRepository.findById(dealId).isPresent());
}
```

**When to Use**:
- Verifying SQL queries
- Testing database constraints
- Validating foreign keys
- Testing transactions

---

### 4. API Tests

**Purpose**: Test entire application stack via HTTP interface.

**Characteristics**:
- Slowest tests
- Black-box testing
- Real HTTP requests/responses
- Tests from client perspective

**Example**: `DealControllerAPITest`
```java
@Test
void testCreateDeal() {
    Map<String, Object> dealRequest = Map.of(
        "title", "API Test Deal",
        "status", "OPEN",
        "salesRepId", testUserId,
        "products", List.of(
            Map.of("name", "Cloud Platform", "price", 25000.00, "quantity", 1)
        )
    );

    given()
        .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .contentType(ContentType.JSON)
        .body(dealRequest)
    .when()
        .post("/deals")
    .then()
        .statusCode(201)
        .body("title", equalTo("API Test Deal"))
        .body("id", notNullValue());
}
```

**When to Use**:
- End-to-end validation
- API contract testing
- Acceptance testing
- Testing authentication

---

## Layer-by-Layer Testing

### Repository Layer (`repository/`)

**What We Test**:
- CRUD operations (Create, Read, Update, Delete)
- SQL query execution
- Database constraints (foreign keys, unique)
- JSON serialization/deserialization
- NULL handling
- ID generation

**Test Files**:
1. **Unit Test** (`H2DealRepositoryUnitTest.java`)
   - Tests with real H2 database
   - Validates repository contract
   - Tests data persistence

2. **Mock Test** (`H2DealRepositoryMockTest.java`)
   - Mocks DatabaseManager, Connection, PreparedStatement, ResultSet
   - Tests error handling (SQLException)
   - Verifies SQL parameter binding
   - Tests without database I/O

3. **Integration Test** (`H2DealRepositoryIntegrationTest.java`)
   - Tests with real database schema
   - Validates foreign key constraints
   - Tests complex queries
   - Direct SQL verification

**Key Concepts**:
- Repository Pattern
- JDBC operations
- PreparedStatement (SQL injection prevention)
- Object-Relational Mapping (ORM)
- Database transactions

---

### Model Layer (`model/`)

**What We Test**:
- Domain model business logic
- Data validation
- Object equality (equals/hashCode)
- Data transformations
- Calculations

**Test Files**:
1. **Unit Test** (`DealModelUnitTest.java` - 20 tests)
   - Tests Deal domain model
   - Validates calculateTotalValue() business logic
   - Tests equals/hashCode contract
   - Tests status lifecycle (OPEN → WON/LOST/CANCELLED)
   - Tests product management (addProduct, setProducts)
   - Tests null handling

2. **Unit Test** (`DealProductModelUnitTest.java` - 7 tests)
   - Tests DealProduct model
   - Validates calculateTotalPrice() (quantity × price)
   - Tests constructor and getters/setters
   - Tests product equality

3. **Unit Test** (`UserModelUnitTest.java` - 9 tests)
   - Tests User domain model
   - Validates role management (SALES_REP, SYSTEM_ADMIN, MANAGER)
   - Tests active status logic
   - Tests user equality

**Key Concepts**:
- Domain Model testing
- Business logic in models
- Value objects
- Equals/HashCode contract
- Immutability patterns

---

### Service Layer (`service/`)

**What We Test**:
- Business validation rules
- Business logic enforcement
- Service coordination
- Filtering and aggregation
- Error handling
- **Integration: Service + Repository + Database**

**Test Files**:
1. **Unit Test** (`DealServiceUnitTest.java` - 15 tests)
   - Mocks Repository dependency
   - Tests business validation (title required, products required, positive value)
   - Tests business rules (cannot modify cancelled deals, cannot delete WON deals)
   - Tests filtering logic (by status, by sales rep)
   - Tests aggregation (calculate pipeline value)

2. **Mock Test** (`DealServiceMockTest.java` - 12 tests)
   - Pure isolation testing with ArgumentCaptor
   - Verifies exact service-repository interactions
   - Tests business logic without database
   - Validates service orchestration patterns

3. **Integration Test** (`DealServiceIntegrationTest.java` - 9 tests)
   - **Tests: Service + Repository + Database**
   - Real business logic WITH real database persistence
   - Validates complete service layer workflows
   - Tests transaction boundaries

**Key Concepts**:
- Business Logic Layer testing
- Dependency Injection with mocks
- Validation testing
- Business rule enforcement
- Service orchestration
- **Integration: Testing layer + layer below**

**Example Business Rules Tested**:
- Deal title is required
- Deal must have at least one product
- Deal value must be positive
- Cannot modify cancelled deals
- Can only delete OPEN deals
- Can only close OPEN deals

---

### Controller Layer (`controller/`)

**What We Test**:
- HTTP request/response handling
- REST endpoint routing
- Authentication/Authorization
- JSON serialization
- HTTP status codes
- **Integration: Controller + Service + Repository + Database**
- **MockServer: HTTP endpoint mocking**
- **External API: Third-party mock API integration**

**Test Files**:
1. **Unit Test** (`DealControllerUnitTest.java` - 10 tests)
   - Mocks DealService dependency
   - Tests request parsing (JSON → Deal object)
   - Tests response formatting (Deal → JSON)
   - Tests HTTP status code logic
   - Validates controller routing

2. **Mock Test** (`DealControllerMockTest.java` - 11 tests)
   - Pure isolation with ArgumentCaptor
   - Verifies exact controller-service interactions
   - Tests without HTTP layer
   - Validates parameter extraction

3. **Integration Test** (`DealControllerIntegrationTest.java` - 9 tests)
   - **Tests: Controller + Service + Repository + Database**
   - Full stack WITHOUT HTTP server
   - Validates complete request → response flow
   - Tests business logic + persistence

4. **API Test** (`DealControllerAPITest.java` - 14 tests)
   - Tests entire HTTP request/response cycle
   - Embedded Tomcat server
   - Tests authentication (401 Unauthorized)
   - Tests CRUD endpoints:
     - POST /deals (201 Created)
     - GET /deals (200 OK)
     - GET /deals/{id} (200 OK, 404 Not Found)
     - PUT /deals/{id} (200 OK, 400 Bad Request)
     - DELETE /deals/{id} (204 No Content, 409 Conflict)
     - POST /deals/{id}/close (200 OK)
   - Tests query parameters (filtering)
   - Tests error responses

5. **MockServer Test** (`DealControllerMockServerTest.java` - 10 tests)
   - HTTP endpoint mocking using MockServer framework
   - Simulates external API endpoints locally
   - Tests client-side HTTP logic
   - Validates request/response handling without real server

6. **External Server Test** (`DealControllerExternalServerTest.java` - 10 tests)
   - Tests against REAL external mock API (Apidog)
   - Endpoint: https://mock.apidog.com/m1/1110236-1100800-946851/deals
   - Validates third-party API integration
   - Tests network communication and latency
   - Demonstrates real-world API integration patterns
   - **⚠️ DISABLED BY DEFAULT** - Requires internet connectivity
   - **Tagged with @Tag("external")** for selective execution
   - **10-second timeout per test** to prevent hanging

**Key Concepts**:
- RESTful API testing
- HTTP Basic Authentication
- RestAssured framework
- JSON request/response
- HTTP status codes
- End-to-end testing
- **Integration: Testing full stack**
- **MockServer: Local HTTP mocking**
- **External API: Third-party integration testing**

---

### Security Layer (`security/`)

**What We Test**:
- Authentication logic
- Authorization enforcement
- Filter chain behavior
- Public vs protected endpoints
- Credential validation
- **Integration: Filter + UserService + Repository + Database**

**Test Files**:
1. **Unit Test** (`AuthenticationFilterUnitTest.java` - 11 tests)
   - Mocks HttpServletRequest, HttpServletResponse, FilterChain, UserService
   - Tests public endpoint bypass (/, /index.html, /webjars/*)
   - Tests protected endpoint authentication (/api/*)
   - Tests valid credentials (sets user in request attribute)
   - Tests invalid credentials (returns 401)
   - Tests missing Authorization header
   - Tests malformed Authorization header

2. **Integration Test** (`AuthenticationFilterIntegrationTest.java` - 7 tests)
   - **Tests: Filter + UserService + Repository + Database**
   - Real authentication flow with database lookup
   - Validates filter chain integration
   - Tests with real user data from database

**Key Concepts**:
- Servlet Filter testing
- HTTP Basic Authentication
- Request/Response mocking
- Filter chain control
- Security testing patterns
- **Integration: Testing authentication flow end-to-end**

---

## Running the Tests

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=H2DealRepositoryUnitTest
mvn test -Dtest=DealServiceUnitTest
mvn test -Dtest=DealControllerAPITest
mvn test -Dtest=AuthenticationFilterUnitTest
```

### Run Tests by Package

```bash
# Repository unit tests
mvn test -Dtest=com.chapman.edu.commissions.integration.repository.unit.*

# Service unit tests
mvn test -Dtest=com.chapman.edu.commissions.integration.service.unit.*

# API tests
mvn test -Dtest=com.chapman.edu.commissions.integration.controller.api.*
```

### Run External Server Tests (Disabled by Default)

**⚠️ External server tests are disabled by default** because they:
- Require internet connectivity
- Can be slow due to network latency
- May fail if external service is unavailable
- Should not block CI/CD pipelines

**To run external server tests:**

```bash
# Option 1: Run external tests explicitly by class name
mvn test -Dtest=DealControllerExternalServerTest

# Option 2: Use JUnit tags (if configured)
mvn test -Dgroups="external"

# Option 3: Temporarily remove @Disabled annotation from DealControllerExternalServerTest
```

**Note**: External tests have 10-second timeout per test to prevent hanging.

### Run with Coverage

```bash
mvn clean test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

---

## Key Concepts Demonstrated

### 1. Arrange-Act-Assert (AAA) Pattern

All tests follow the AAA pattern:

```java
@Test
void testExample() {
    // ARRANGE: Set up test data and preconditions
    Deal deal = createTestDeal(null, "Test");

    // ACT: Execute the method under test
    Deal saved = repository.save(deal);

    // ASSERT: Verify expected outcomes
    assertNotNull(saved.getId());
}
```

### 2. Test Isolation

Each test is independent:
- `@BeforeEach`: Resets database state
- No shared state between tests
- Tests can run in any order

### 3. Test Fixtures

Reusable setup and teardown:

```java
@BeforeAll
static void setUp() {
    // One-time setup (database init)
}

@BeforeEach
void resetDatabase() {
    // Per-test setup (clean state)
}

@AfterAll
static void tearDown() {
    // Cleanup resources
}
```

### 4. Mockito Patterns

**Stubbing**:
```java
when(mockRepository.save(any(Deal.class))).thenReturn(savedDeal);
```

**Verification**:
```java
verify(mockRepository, times(1)).save(any(Deal.class));
```

**Exception Simulation**:
```java
when(mockStatement.executeQuery()).thenThrow(new SQLException("Connection timeout"));
```

**Argument Capture**:
```java
ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
verify(mockRequest).setAttribute(eq("user"), captor.capture());
User capturedUser = captor.getValue();
```

### 5. RestAssured Patterns

**Given-When-Then**:
```java
given()
    .auth().basic(user, pass)
    .contentType(ContentType.JSON)
    .body(requestJson)
.when()
    .post("/deals")
.then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("title", equalTo("Test Deal"));
```

### 6. Test Naming Conventions

Tests use descriptive names explaining what is tested:

```java
@DisplayName("Should return 401 for unauthenticated request")
void testUnauthenticatedRequest() { ... }

@DisplayName("Should reject deal without title (Business Validation)")
void testCreateDealWithoutTitle() { ... }
```

---

## Testing Frameworks Used

### JUnit 5 (Jupiter)

- **Purpose**: Test execution framework
- **Features Used**:
  - `@Test`: Mark test methods
  - `@BeforeAll`, `@BeforeEach`, `@AfterAll`: Test lifecycle
  - `@DisplayName`: Human-readable test names
  - `@Order`: Test execution order
  - Assertions: `assertEquals`, `assertTrue`, `assertThrows`, etc.

### Mockito

- **Purpose**: Mocking framework
- **Features Used**:
  - `@Mock`: Create mock objects
  - `@ExtendWith(MockitoExtension.class)`: Enable Mockito
  - `when()...thenReturn()`: Stub method behavior
  - `verify()`: Verify method calls
  - `doThrow()`: Simulate exceptions
  - `ArgumentCaptor`: Capture method arguments

### RestAssured

- **Purpose**: HTTP client for API testing
- **Features Used**:
  - `given()`: Setup preconditions
  - `when()`: Execute HTTP request
  - `then()`: Assert response
  - `.auth().basic()`: HTTP Basic Auth
  - `.contentType()`: Set Content-Type header
  - `.body()`: Set request/assert response body
  - `.statusCode()`: Assert HTTP status

### Hamcrest

- **Purpose**: Matcher library for assertions
- **Features Used**:
  - `equalTo()`: Value equality
  - `notNullValue()`: Null check
  - `hasSize()`: Collection size
  - `containsString()`: String contains
  - `instanceOf()`: Type checking

---

## Best Practices

### 1. Test One Thing Per Test

❌ **Bad**:
```java
@Test
void testEverything() {
    repository.save(deal);
    repository.findById(id);
    repository.update(deal);
    repository.delete(id);
}
```

✅ **Good**:
```java
@Test
void testSaveNewDeal() { /* Test save only */ }

@Test
void testFindById() { /* Test find only */ }
```

### 2. Use Descriptive Test Names

❌ **Bad**:
```java
@Test
void test1() { ... }
```

✅ **Good**:
```java
@Test
@DisplayName("Should return 401 for invalid credentials")
void testInvalidCredentials() { ... }
```

### 3. Arrange-Act-Assert Pattern

Always structure tests clearly:
```java
// Arrange
Deal deal = createTestDeal();

// Act
Deal saved = repository.save(deal);

// Assert
assertNotNull(saved.getId());
```

### 4. Test Both Success and Failure Cases

✅ Test valid input
✅ Test invalid input
✅ Test edge cases (null, empty, boundary values)
✅ Test error conditions

### 5. Keep Tests Fast

- Unit tests: < 100ms
- Integration tests: < 1s
- API tests: < 5s

Use `@BeforeEach` to reset state quickly rather than recreating everything.

### 6. Test Behavior, Not Implementation

❌ **Bad** (implementation detail):
```java
verify(repository, times(3)).someInternalMethod();
```

✅ **Good** (behavior):
```java
assertEquals(3, result.size());
```

### 7. Use Test Data Builders

```java
private Deal createTestDeal(String id, String title) {
    Deal deal = new Deal();
    deal.setId(id);
    deal.setTitle(title);
    // ... set other fields
    return deal;
}
```

---

## Diagram

See the PlantUML diagram for visual representation:

📄 `src/main/java/com/chapman/edu/commissions/integration/testing-implementation.puml`

The diagram shows:
- Production code structure
- Test structure by layer
- Test type relationships
- Framework usage
- Testing pyramid

---

## Summary

This testing implementation demonstrates:

✅ **Comprehensive Coverage**: Unit, Mock, Integration, and API tests
✅ **Layered Testing**: Each architectural layer tested appropriately
✅ **Best Practices**: AAA pattern, test isolation, descriptive names
✅ **Modern Frameworks**: JUnit 5, Mockito, RestAssured
✅ **Educational Value**: Extensive comments explaining concepts
✅ **Professional Quality**: Production-grade test patterns

### Test Metrics:

| Layer          | Unit Tests | Mock Tests | Integration Tests | API/External Tests | Total |
|----------------|------------|------------|-------------------|--------------------|-------|
| Model          | 36         | -          | -                 | -                  | 36    |
| Repository     | 13         | 10         | 10                | -                  | 33    |
| Service        | 15         | 12         | 9                 | -                  | 36    |
| Controller     | 10         | 11         | 9                 | 34                 | 64    |
| Security       | 11         | -          | 7                 | -                  | 18    |
| **Total**      | **85**     | **33**     | **35**            | **34**             | **187**|

**Breakdown by Test Type:**
- **Unit Tests (85)**: Fast, isolated tests with minimal mocking
- **Mock Tests (33)**: Complete isolation with all dependencies mocked
- **Integration Tests (35)**: Multiple layers working together
- **API/External Tests (34)**: HTTP endpoint testing (14 API + 10 MockServer + 10 External)

**Test Coverage Distribution:**
- Model Layer: 36 tests (domain logic)
- Repository Layer: 33 tests (data access)
- Service Layer: 36 tests (business logic)
- Controller Layer: 64 tests (presentation + HTTP)
- Security Layer: 18 tests (authentication/authorization)

**Test File Count: 17 test classes**

---

## Further Reading

- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **RestAssured Documentation**: https://rest-assured.io/
- **Testing Best Practices**: https://martinfowler.com/articles/practical-test-pyramid.html

---

## Questions and Learning Objectives

After studying these tests, you should be able to answer:

1. What is the difference between Unit, Mock, Integration, and API tests?
2. When should you use mocks vs real dependencies?
3. How do you test servlet filters?
4. How do you test RESTful APIs with RestAssured?
5. What is the Testing Pyramid and why is it important?
6. How do you test business validation rules?
7. How do you test database constraints?
8. What is the Arrange-Act-Assert pattern?
9. How do you mock JDBC components?
10. How do you test HTTP authentication?

---

**Last Updated**: 2025-01-30

**Author**: Educational Testing Implementation for Commission Calculator

**License**: Educational Use - Chapman University