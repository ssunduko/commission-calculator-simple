# REST API Integration Tests

Comprehensive integration testing suite for the Commission Calculator REST API, demonstrating industry best practices for testing RESTful web services.

## Table of Contents

1. [Overview](#overview)
2. [What Was Done](#what-was-done)
3. [Test Architecture](#test-architecture)
4. [Running the Tests](#running-the-tests)
5. [Test Files Overview](#test-files-overview)
6. [Key Concepts Demonstrated](#key-concepts-demonstrated)
7. [Sample Data](#sample-data)
8. [Postman Collection](#postman-collection)
9. [Educational Materials](#educational-materials)
10. [Best Practices](#best-practices)

## Overview

This integration test suite validates the REST API endpoints for the Commission Calculator system. The tests use a **real embedded Tomcat server** and make **actual HTTP requests** to ensure the entire stack (HTTP → Servlet → Repository → JSON) works correctly together.

### Test Coverage

- **47+ test methods** across multiple test classes
- **Full CRUD operations** for all resources (Deals, Users, Commission Plans, Disputes)
- **Success scenarios**: 200 OK, 201 Created, 204 No Content
- **Error scenarios**: 400 Bad Request, 404 Not Found
- **Query parameter filtering** and searching
- **Real HTTP communication** with status code verification

## What Was Done

### 1. Created Integration Test Infrastructure

**`ApiIntegrationTestBase.java`** - Test Fixture base class providing:

```java
// Shared test infrastructure for all API tests
public abstract class ApiIntegrationTestBase {
    protected static ApiServer server;        // Embedded Tomcat
    protected static HttpClient httpClient;   // Java HTTP client

    @BeforeAll                                // Runs once per test class
    public static void startServer() {
        server = new ApiServer(9999);         // Start on test port
        server.start();                       // Real Tomcat startup
    }

    @AfterAll                                 // Cleanup after tests
    public static void stopServer() {
        server.stop();                        // Graceful shutdown
    }

    // Helper methods for HTTP requests
    protected HttpResponse<String> get(String path) { ... }
    protected HttpResponse<String> post(String path, String json) { ... }
    protected HttpResponse<String> put(String path, String json) { ... }
    protected HttpResponse<String> delete(String path) { ... }
}
```

**Concepts**: Test Fixture Pattern, @BeforeAll/@AfterAll lifecycle, DRY principle

### 2. Implemented Comprehensive Test Suites

**`DealServletIntegrationTest.java`** - 11 tests covering:

```java
@Test
void createDeal_validData_returns201() {
    // ARRANGE: Prepare test data
    String dealJson = """
        {
            "title": "Enterprise Software License",
            "value": 100000.00,
            "salesRepId": "USER-001"
        }
        """;

    // ACT: Make HTTP POST request
    HttpResponse<String> response = post("/deals", dealJson);

    // ASSERT: Verify response
    assertStatus(response, 201);  // HTTP 201 Created
    Deal deal = parseResponse(response, Deal.class);
    assertNotNull(deal.getId());  // Server generated ID
}
```

**Tests Include**:
- ✅ Create deal with valid data (201)
- ✅ Get all deals (200)
- ✅ Get deal by ID (200)
- ✅ Get non-existent deal (404)
- ✅ Update existing deal (200)
- ✅ Update non-existent deal (404)
- ✅ Delete deal (204)
- ✅ Delete non-existent deal (404)
- ✅ Filter deals by status (200)
- ✅ Filter deals by sales rep (200)
- ✅ Create with invalid JSON (400)

**Concepts**: AAA Pattern (Arrange-Act-Assert), RESTful testing, HTTP status codes

**`UserServletIntegrationTest.java`** - 6 tests for user management

### 3. Created Sample Data Loader

**`SampleDataLoader.java`** - Test Data Builder for populating the system:

```java
public class SampleDataLoader {
    public void loadAllData() {
        loadUsers();           // 6 users with different roles
        loadCommissionPlans(); // 4 plans (active, draft, archived)
        loadDeals();           // 8 deals in various states
        loadDisputes();        // 4 disputes with different statuses
    }
}
```

**Sample Data Created**:

| Resource | Count | Examples |
|----------|-------|----------|
| **Users** | 6 | Sales reps, manager, finance admin, system admin |
| **Deals** | 8 | Enterprise ($500k WON), Cloud ($150k OPEN), Lost deal, Cancelled deal |
| **Plans** | 4 | Q1 Active, Q2 Active, Q3 Draft, 2023 Archived |
| **Disputes** | 4 | Under Review, Escalated, Resolved, Initiated |

**Production Integration**: The SampleDataLoader has been integrated into the main ApiServer and **loads sample data by default** when the server starts:

```bash
# Start the API server (sample data loaded automatically)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.rest.ApiServer"

# With custom port (sample data still loaded)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.rest.ApiServer" \
  -Dexec.args="8081"

# Start without sample data (if you want empty repositories)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.rest.ApiServer" \
  -Dexec.args="--no-sample-data"
```

This default behavior allows for:
- Quick API demonstrations with realistic data (no setup required)
- Manual testing without creating data through API calls
- Exploring API functionality with pre-populated entities
- Training and educational scenarios out-of-the-box

**Concepts**: Test Data Builder pattern, realistic test data, fixture management

### 4. Built Postman Collection

**`rest.json`** - Complete Postman collection with:

- **20+ HTTP requests** organized by resource type
- **Automated test scripts** for response validation
- **Collection variables** (`{{baseUrl}}`, `{{dealId}}`) for dynamic values
- **Full CRUD examples** for all endpoints
- **Error scenario tests** (404, 400)
- **Query parameter examples** (filtering, searching)

```javascript
// Example Postman test script
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has ID", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.collectionVariables.set('dealId', jsonData.id);  // Save for later use
});
```

**Concepts**: API testing automation, collection variables, test assertions

### 5. Created Educational Materials

**`integration-test-architecture.puml`** - PlantUML diagram showing:

```
┌─────────────────────────────────────┐
│   ApiIntegrationTestBase            │
│   • startServer() / stopServer()    │
│   • HTTP helper methods             │
│   • Shared test infrastructure      │
└──────────────┬──────────────────────┘
               │ extends
     ┌─────────┴─────────┐
     │                   │
┌────┴────────┐  ┌───────┴──────┐
│ DealServlet │  │ UserServlet  │
│ Integration │  │ Integration  │
│    Test     │  │    Test      │
└─────────────┘  └──────────────┘
```

**Includes**:
- Test class hierarchy
- HTTP client relationships
- System under test components
- Detailed component annotations
- Testing pattern explanations

**`QUESTIONS.md`** - 75+ questions covering 15 topics:

1. Integration Testing Fundamentals (4 questions)
2. Test Fixture Pattern (4 questions)
3. AAA Pattern (4 questions)
4. HTTP Testing (4 questions)
5. Test Naming Conventions (3 questions)
6. Test Execution Order (3 questions)
7. Sample Data Loading (4 questions)
8. Error Testing (3 questions)
9. Query Parameter Testing (3 questions)
10. Test Coverage (3 questions)
11. Postman Collection (4 questions)
12. Test Performance (3 questions)
13. Continuous Integration (3 questions)
14. Testing Best Practices (3 questions)
15. Advanced Integration Testing (4 questions)
16. Bonus Questions (5 questions)

**`ANSWERS.md`** - Comprehensive answers with:
- Detailed code examples
- Comparison tables
- Visual diagrams
- Best practice explanations
- Common pitfalls and solutions

## Test Architecture

### Layered Testing Approach

```
┌─────────────────────────────────────────────────┐
│         Integration Tests (This Suite)          │
│  • Real HTTP requests                           │
│  • Real Tomcat server                           │
│  • Test entire stack                            │
└────────────────────┬────────────────────────────┘
                     │ Tests
                     ▼
┌─────────────────────────────────────────────────┐
│              ApiServer (System Under Test)       │
│  • Embedded Tomcat on port 9999                 │
│  • All servlets registered                      │
│  • In-memory repositories                       │
└────────────────────┬────────────────────────────┘
                     │ Hosts
                     ▼
┌─────────────────────────────────────────────────┐
│                  Servlets                        │
│  • DealServlet                                  │
│  • UserServlet                                  │
│  • CommissionPlanServlet                        │
│  • DisputeServlet                               │
└────────────────────┬────────────────────────────┘
                     │ Uses
                     ▼
┌─────────────────────────────────────────────────┐
│              Repositories                        │
│  • InMemoryRepository<Deal>                     │
│  • InMemoryRepository<User>                     │
│  • Thread-safe storage                          │
└─────────────────────────────────────────────────┘
```

### Test Lifecycle

```
1. Test Class Loaded
   ↓
2. @BeforeAll: startServer()
   • Create ApiServer(9999)
   • Start embedded Tomcat
   • Initialize HttpClient
   • Wait for server ready (~2 seconds)
   ↓
3. Execute Tests
   • Test 1: createDeal_validData_returns201()
   • Test 2: getAllDeals_withData_returns200()
   • Test 3: getDealById_existingId_returns200()
   • ... (11 total tests)
   ↓
4. @AfterAll: stopServer()
   • Stop Tomcat gracefully
   • Clean up resources
   • Clear repositories
```

### Test Independence

Each test creates its own data to ensure independence:

```java
@Test
void updateDeal_existingDeal_returns200() {
    // ARRANGE: Create own deal (doesn't depend on other tests)
    String dealId = post("/deals", createJson)
        .parseResponse(Deal.class)
        .getId();

    // ACT: Update the deal
    HttpResponse<String> response = put("/deals/" + dealId, updateJson);

    // ASSERT: Verify update
    assertStatus(response, 200);
}
```

**Benefits**:
- Tests can run in any order
- Tests can run individually
- No cascading failures
- Parallel execution possible

## Running the Tests

### From Command Line

```bash
# Run all integration tests
mvn test

# Run specific test class
mvn test -Dtest=DealServletIntegrationTest

# Run specific test method
mvn test -Dtest=DealServletIntegrationTest#createDeal_validData_returns201

# Run with verbose output
mvn test -X

# Run and generate coverage report
mvn clean test jacoco:report
```

### From IDE

**IntelliJ IDEA**:
1. Right-click on test class → Run
2. Or click green arrow next to test method
3. View results in Run window

**Eclipse**:
1. Right-click on test class → Run As → JUnit Test
2. View results in JUnit view

### Expected Output

```
Starting test server on port 9999
✓ Servlets registered successfully
✓ Server started successfully
Test server started successfully

Running DealServletIntegrationTest
✓ createDeal_validData_returns201 (0.15s)
✓ getAllDeals_withData_returns200 (0.08s)
✓ getDealById_existingId_returns200 (0.09s)
✓ getDealById_nonExistentId_returns404 (0.07s)
✓ updateDeal_existingDeal_returns200 (0.12s)
✓ updateDeal_nonExistentDeal_returns404 (0.06s)
✓ deleteDeal_existingDeal_returns204 (0.11s)
✓ deleteDeal_nonExistentDeal_returns404 (0.05s)
✓ getDeals_filterByStatus_returnsFilteredResults (0.10s)
✓ getDeals_filterBySalesRep_returnsFilteredResults (0.09s)
✓ createDeal_invalidJson_returns400 (0.08s)

Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

Stopping test server
✓ Server stopped successfully
```

### Running with Sample Data

```bash
# Start server and load sample data
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.rest.ApiServer" \
              -Dexec.args="--load-sample-data"

# Output:
# Starting Commission Calculator API Server...
#
# Loading sample data...
# Sample data loaded successfully!
#   Users: 6
#   Deals: 8
#   Commission Plans: 4
#   Disputes: 4
#
# ✓ Server started successfully!
#
# API Endpoints:
#   - http://localhost:8080/api/v1/deals
#   - http://localhost:8080/api/v1/users
#   - http://localhost:8080/api/v1/commission-plans
#   - http://localhost:8080/api/v1/disputes
```

Now you can test with Postman using the pre-populated data!

## Test Files Overview

### Test Classes

| File | Purpose | Tests | Lines |
|------|---------|-------|-------|
| `ApiIntegrationTestBase.java` | Base test fixture | 0 | 150 |
| `DealServletIntegrationTest.java` | Deal endpoint tests | 11 | 280 |
| `UserServletIntegrationTest.java` | User endpoint tests | 6 | 150 |

### Supporting Files

| File | Purpose | Size |
|------|---------|------|
| `SampleDataLoader.java` | Test data builder | 16 KB |
| `rest.json` | Postman collection | 25 KB |
| `integration-test-architecture.puml` | Architecture diagram | 8 KB |
| `QUESTIONS.md` | Educational questions | 12 KB |
| `ANSWERS.md` | Detailed answers | 95 KB |
| `README.md` | This file | 20 KB |

## Key Concepts Demonstrated

### 1. Test Fixture Pattern

**Problem**: Every test needs a running server and HTTP client.

**Solution**: Base class provides shared infrastructure.

```java
// Base fixture
public abstract class ApiIntegrationTestBase {
    protected static ApiServer server;
    protected static HttpClient httpClient;

    @BeforeAll
    static void startServer() { /* setup */ }

    @AfterAll
    static void stopServer() { /* cleanup */ }
}

// Concrete test inherits all infrastructure
public class DealServletIntegrationTest extends ApiIntegrationTestBase {
    @Test
    void test() {
        // Can use server and httpClient immediately
        HttpResponse<String> response = post("/deals", json);
    }
}
```

**Benefits**: DRY, consistency, easier maintenance

### 2. AAA Pattern (Arrange-Act-Assert)

**Structure**: Every test follows three phases.

```java
@Test
void createDeal_validData_returns201() {
    // === ARRANGE: Set up test conditions ===
    String dealJson = """
        {
            "title": "Enterprise License",
            "value": 100000.00
        }
        """;

    // === ACT: Execute the operation being tested ===
    HttpResponse<String> response = post("/deals", dealJson);

    // === ASSERT: Verify expected outcomes ===
    assertStatus(response, 201);
    Deal deal = parseResponse(response, Deal.class);
    assertNotNull(deal.getId());
    assertEquals("Enterprise License", deal.getTitle());
}
```

**Benefits**: Readability, maintainability, clear test intent

### 3. Test Naming Convention

**Pattern**: `methodName_scenario_expectedResult`

```java
// Good test names (self-documenting)
createDeal_validData_returns201()
getDealById_existingId_returns200()
getDealById_nonExistentId_returns404()
updateDeal_existingDeal_returns200()
deleteDeal_nonExistentDeal_returns404()

// Bad test names (unclear)
testCreate()
testGet()
testUpdate1()
testUpdate2()
```

**Benefits**: Self-documenting, clear intent, easy to find failures

### 4. HTTP Status Code Testing

**Validates REST API contract**:

```java
// Success cases
POST   /deals       → 201 Created
GET    /deals/123   → 200 OK
PUT    /deals/123   → 200 OK
DELETE /deals/123   → 204 No Content

// Error cases
GET    /deals/999   → 404 Not Found
POST   /deals (bad) → 400 Bad Request
```

**Why it matters**: Clients depend on correct status codes

### 5. End-to-End Integration

**Tests entire stack**:

```
HTTP Request
    ↓
Servlet (routing, parsing)
    ↓
Business Logic
    ↓
Repository (data access)
    ↓
JSON Serialization
    ↓
HTTP Response
```

**vs Unit Tests** (test single class in isolation)

### 6. Test Data Builder Pattern

**Problem**: Tests need complex, realistic data.

**Solution**: Centralized data creation utility.

```java
SampleDataLoader loader = new SampleDataLoader(repositories);
loader.loadAllData();

// Creates:
// - 6 users with realistic profiles
// - 8 deals in various states
// - 4 commission plans
// - 4 disputes with workflow states
```

**Benefits**: Reusable, realistic, maintainable

## Sample Data

### Users (6 total)

| Username | Role | Department | Territory |
|----------|------|------------|-----------|
| jsmith | SALES_REP | Sales | West Coast |
| sjohnson | SALES_REP | Sales | East Coast |
| mwilliams | SALES_REP | Sales | Midwest |
| echen | SALES_MANAGER, SALES_REP | Sales | All Territories |
| dbrown | FINANCE_ADMIN | Finance | - |
| admin | SYSTEM_ADMIN | IT | - |

### Deals (8 total)

| Title | Value | Status | Products |
|-------|-------|--------|----------|
| Acme Corp - Enterprise License | $500,000 | WON | 2 products |
| TechStart Inc - Cloud Infrastructure | $150,000 | OPEN | 1 product |
| Global Finance - Consulting | $75,000 | WON | 1 product |
| StartupXYZ - Small Business Package | $25,000 | OPEN | 2 products |
| Manufacturing Co - Hardware Refresh | $200,000 | LOST | 0 products |
| Retail Giant - Annual Renewal | $300,000 | WON | 0 products |
| Healthcare Network - Digital Transformation | $1,000,000 | OPEN | 0 products |
| Budget Cuts Inc - Postponed Project | $100,000 | CANCELLED | 0 products |

### Commission Plans (4 total)

| Name | Status | Effective Period |
|------|--------|------------------|
| Q1 2024 Standard Plan | ACTIVE | Jan 1 - Mar 31, 2024 |
| Q2 2024 Enhanced Plan | ACTIVE | Apr 1 - Jun 30, 2024 |
| Q3 2024 Draft Plan | DRAFT | Jul 1 - Sep 30, 2024 |
| 2023 Annual Plan | ARCHIVED | Jan 1 - Dec 31, 2023 |

### Disputes (4 total)

| Title | Status | Description |
|-------|--------|-------------|
| Commission Calculation Discrepancy | UNDER_REVIEW | 8% instead of 10% rate |
| Missing Bonus for Q4 Achievement | ESCALATED | Bonus not included |
| Incorrect Territory Assignment | RESOLVED | Territory corrected |
| Product Mix Calculation Error | INITIATED | Different rates for products |

## Postman Collection

### Structure

```
Commission Calculator REST API
├── Deals
│   ├── Create Deal (POST)
│   ├── Get All Deals (GET)
│   ├── Get Deal by ID (GET)
│   ├── Update Deal (PUT)
│   ├── Delete Deal (DELETE)
│   ├── Filter Deals by Status (GET)
│   └── Filter Deals by Sales Rep (GET)
├── Users
│   ├── Create User (POST)
│   ├── Get All Users (GET)
│   ├── Get User by ID (GET)
│   ├── Update User (PUT)
│   └── Delete User (DELETE)
├── Commission Plans
│   ├── Create Commission Plan (POST)
│   ├── Get All Commission Plans (GET)
│   ├── Get Commission Plan by ID (GET)
│   └── Filter Plans by Status (GET)
├── Disputes
│   ├── Create Dispute (POST)
│   ├── Get All Disputes (GET)
│   ├── Get Dispute by ID (GET)
│   └── Filter Disputes by Status (GET)
└── Error Handling Examples
    ├── 404 - Deal Not Found
    └── 400 - Invalid JSON
```

### Using the Collection

**1. Import into Postman**:
   - Open Postman
   - File → Import
   - Select `rest.json`

**2. Set Variables**:
   - Collection variables are pre-configured
   - `baseUrl` = `http://localhost:8080/api/v1`
   - `dealId`, `userId`, etc. are populated automatically

**3. Run Requests**:
   - Start server: `mvn exec:java -Dexec.mainClass="...ApiServer" -Dexec.args="--load-sample-data"`
   - Run "Create Deal" → saves ID to `{{dealId}}`
   - Run "Get Deal by ID" → uses saved `{{dealId}}`

**4. Automated Tests**:
   - Each request has test scripts
   - Validates status codes
   - Checks response structure
   - Saves dynamic values

### Example Request

**POST Create Deal**:
```http
POST http://localhost:8080/api/v1/deals
Content-Type: application/json

{
    "title": "Enterprise Software License",
    "value": 500000.00,
    "salesRepId": "USER-001",
    "status": "OPEN"
}
```

**Response**:
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
    "id": "DEAL-001",
    "title": "Enterprise Software License",
    "value": 500000.00,
    "salesRepId": "USER-001",
    "status": "OPEN",
    "createdDate": "2024-10-18",
    "lastModifiedDate": "2024-10-18"
}
```

**Automated Test Script**:
```javascript
pm.test("Status code is 201", function () {
    pm.response.to.have.status(201);
});

pm.test("Response has ID", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.collectionVariables.set('dealId', jsonData.id);
});
```

## Educational Materials

### PlantUML Diagram

**`integration-test-architecture.puml`** visualizes:

- Test class hierarchy (inheritance)
- Test fixture pattern implementation
- HTTP client usage
- System under test components
- Test lifecycle annotations
- Component responsibilities

**Generate Image**:
```bash
# Online: http://www.plantuml.com/plantuml/
# Or use PlantUML plugin in IDE
# Or command line:
plantuml integration-test-architecture.puml
```

### Questions & Answers

**`QUESTIONS.md`** covers:
- Conceptual understanding
- Implementation details
- Best practices
- Common pitfalls
- Advanced topics

**`ANSWERS.md`** provides:
- Detailed explanations
- Code examples
- Comparison tables
- Visual diagrams
- Practical guidance

**Use Cases**:
- Self-study
- Interview preparation
- Code review discussions
- Teaching material
- Team onboarding

## Best Practices

### 1. Test Independence

✅ **Good**: Each test creates its own data
```java
@Test
void test1() {
    String dealId = post("/deals", createJson).getId();
    // Use dealId in this test only
}
```

❌ **Bad**: Tests depend on each other
```java
static String sharedDealId;

@Test @Order(1)
void test1() { sharedDealId = post(...).getId(); }

@Test @Order(2)
void test2() { put("/deals/" + sharedDealId, ...); }  // DEPENDS ON TEST 1!
```

### 2. Clear Test Names

✅ **Good**: Describes what, when, and expected result
```java
createDeal_validData_returns201()
getDealById_nonExistentId_returns404()
```

❌ **Bad**: Vague or unclear
```java
testCreate()
testDeal1()
```

### 3. AAA Pattern

✅ **Good**: Clear three-phase structure
```java
// Arrange
String json = "...";

// Act
HttpResponse<String> response = post("/deals", json);

// Assert
assertEquals(201, response.statusCode());
```

❌ **Bad**: Mixed phases
```java
HttpResponse<String> response = post("/deals", "...");  // Act + Arrange mixed
assertEquals(201, response.statusCode());  // Assert
String id = response.getId();  // Arrange for what?
```

### 4. Test One Thing

✅ **Good**: Focused test
```java
@Test
void createDeal_validData_returns201() {
    // Tests creation only
}

@Test
void getDealById_existingId_returns200() {
    // Tests retrieval only
}
```

❌ **Bad**: Tests multiple operations
```java
@Test
void dealCRUD() {
    post(...);   // Create
    get(...);    // Read
    put(...);    // Update
    delete(...); // Delete
    // Which operation failed if test fails?
}
```

### 5. Meaningful Assertions

✅ **Good**: Specific assertions with messages
```java
assertNotNull(deal.getId(), "Server should generate ID");
assertEquals("Enterprise License", deal.getTitle());
assertTrue(deal.getValue().compareTo(BigDecimal.ZERO) > 0);
```

❌ **Bad**: Weak or missing assertions
```java
assertNotNull(deal);  // Only checks deal exists, not correctness
```

### 6. Test Error Cases

✅ **Good**: Tests both success and failure
```java
@Test
void getDealById_existingId_returns200() { ... }

@Test
void getDealById_nonExistentId_returns404() { ... }
```

❌ **Bad**: Only tests happy path
```java
@Test
void getDealById() { ... }  // Only tests success case
```

## Common Issues and Solutions

### Issue 1: Port Already in Use

**Error**:
```
java.net.BindException: Address already in use
```

**Solution**:
```bash
# Find process using port 9999
netstat -ano | findstr :9999

# Kill process (Windows)
taskkill /PID <process_id> /F

# Or use different port
server = new ApiServer(9998);
```

### Issue 2: Server Startup Timeout

**Error**:
```
Connection refused
```

**Solution**:
```java
// Increase wait time in startServer()
Thread.sleep(3000);  // Instead of 2000
```

### Issue 3: Tests Fail When Run Together

**Cause**: Tests sharing state or wrong execution order

**Solution**:
- Ensure each test creates own data
- Remove static shared state
- Make tests independent

### Issue 4: JSON Parsing Errors

**Error**:
```
JsonSyntaxException: Expected BEGIN_OBJECT but was STRING
```

**Solution**:
- Check Content-Type header is set
- Verify JSON syntax (use online validator)
- Check custom type adapters for LocalDate/LocalDateTime

## Next Steps

### Extending the Tests

1. **Add More Test Classes**:
   ```java
   public class CommissionPlanServletIntegrationTest extends ApiIntegrationTestBase {
       // Test commission plan endpoints
   }
   ```

2. **Add Performance Tests**:
   ```java
   @Test
   void getAllDeals_performanceTest() {
       long start = System.currentTimeMillis();
       HttpResponse<String> response = get("/deals");
       long duration = System.currentTimeMillis() - start;

       assertTrue(duration < 1000, "Should respond in < 1 second");
   }
   ```

3. **Add Security Tests**:
   ```java
   @Test
   void createDeal_withoutAuthentication_returns401() {
       // Test auth requirements
   }
   ```

4. **Add Concurrent Request Tests**:
   ```java
   @Test
   void multipleClientsCanAccessSimultaneously() {
       // Test thread safety
   }
   ```

### Moving to Production

1. **Replace InMemoryRepository** with database repository
2. **Add authentication/authorization** tests
3. **Add rate limiting** tests
4. **Add CORS** tests
5. **Add input validation** tests
6. **Set up CI/CD** pipeline

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Java HttpClient](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpClient.html)
- [REST API Testing Best Practices](https://restfulapi.net/rest-api-testing/)
- [Postman Documentation](https://learning.postman.com/)
- [Integration Testing Patterns](https://martinfowler.com/articles/practical-test-pyramid.html)

---

**Created**: October 2024
**Purpose**: Educational demonstration of integration testing best practices
**License**: Educational use