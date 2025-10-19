# Integration Testing - Study Answers

This document provides detailed answers to the questions in QUESTIONS.md.

## Section 1: Integration Testing Fundamentals

### Answer 1.1: Unit vs Integration Tests

**Unit Test:**
Tests a single class in isolation with all dependencies mocked.

```java
// Unit test for DealServlet (hypothetical)
@Test
void testDoGet_withValidId_callsRepository() {
    // Arrange - Mock dependencies
    Repository<Deal> mockRepo = mock(Repository.class);
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);

    when(mockRequest.getPathInfo()).thenReturn("/DEAL-001");
    when(mockRepo.findById("DEAL-001")).thenReturn(Optional.of(testDeal));

    DealServlet servlet = new DealServlet(mockRepo);

    // Act
    servlet.doGet(mockRequest, mockResponse);

    // Assert
    verify(mockRepo).findById("DEAL-001");
}
```

**Integration Test:**
Tests multiple components working together with real implementations.

```java
// Integration test (from our code)
@Test
void getDealById_existingId_returns200() throws Exception {
    // Arrange - Real HTTP request to real server
    HttpResponse<String> createResponse = post("/deals", dealJson);
    Deal createdDeal = parseResponse(createResponse, Deal.class);

    // Act - Actual HTTP GET request
    HttpResponse<String> response = get("/deals/" + createdDeal.getId());

    // Assert - Full stack tested: HTTP → Servlet → Repository → JSON
    assertStatus(response, 200);
    Deal retrievedDeal = parseResponse(response, Deal.class);
    assertEquals("Hardware Purchase", retrievedDeal.getTitle());
}
```

**Key Differences:**

| Aspect | Unit Test | Integration Test |
|--------|-----------|------------------|
| **Scope** | Single class | Multiple components |
| **Dependencies** | Mocked | Real |
| **Speed** | Fast (milliseconds) | Slower (seconds) |
| **Network** | No real HTTP | Real HTTP over localhost |
| **Database** | Mocked | In-memory or test DB |
| **What it tests** | Logic correctness | Components work together |
| **Failure indicates** | Bug in specific class | Integration problem |

### Answer 1.2: Test Scope

**Our Approach: Real Server + Real HTTP**

From `ApiIntegrationTestBase.java:54-68`:
```java
@BeforeAll
public static void startServer() throws Exception {
    server = new ApiServer(TEST_PORT);  // Real Tomcat server

    Thread serverThread = new Thread(() -> {
        server.start();  // Actually starts Tomcat
    });
    serverThread.start();

    httpClient = HttpClient.newHttpClient();  // Real HTTP client
}
```

**Advantages:**

1. **Realistic Testing:**
   - Tests actual HTTP routing
   - Verifies URL mapping (`/api/v1/deals/*`)
   - Tests real JSON serialization/deserialization
   - Validates HTTP headers and status codes

2. **Confidence:**
   - If tests pass, you know the API actually works
   - Catches integration issues between components
   - Tests the full request/response cycle

3. **No Mocking Complexity:**
   - No need to mock servlet API
   - Don't have to simulate HTTP behavior
   - Simpler test code

**Disadvantages:**

1. **Slower:**
   - Server startup takes ~2 seconds
   - Real HTTP has network overhead (even on localhost)
   - Integration tests take longer to run

2. **Resource Usage:**
   - Uses a real port (9999)
   - Runs full Tomcat instance
   - More memory consumption

3. **Complexity:**
   - Need to manage server lifecycle
   - Port conflicts possible
   - Thread management required

**Alternative: Mocking HTTP Layer**

```java
// Using MockMvc (Spring example)
mockMvc.perform(get("/api/v1/deals/DEAL-001"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.title").value("Enterprise License"));
```

**Mocking Advantages:**
- Faster execution
- No server startup needed
- No port management

**Mocking Disadvantages:**
- Doesn't test real HTTP
- Doesn't catch routing issues
- Framework-specific (e.g., Spring MockMvc)

**Verdict:** Our approach is appropriate for integration tests because we want to verify the entire stack works, not just the servlet logic.

### Answer 1.3: Test Independence

**Why Test Independence Matters:**

**Independent Tests:**
```java
@Test
void testA() {
    // Creates DEAL-001
    post("/deals", createDealJson("Test Deal A"));
}

@Test
void testB() {
    // Creates its own DEAL-002
    post("/deals", createDealJson("Test Deal B"));
    // Doesn't depend on testA
}
```

**Dependent Tests (BAD):**
```java
private static String sharedDealId;  // Shared state!

@Test
@Order(1)
void createDeal() {
    HttpResponse<String> response = post("/deals", dealJson);
    sharedDealId = parseResponse(response, Deal.class).getId();
}

@Test
@Order(2)
void updateDeal() {
    // DEPENDS on createDeal running first!
    put("/deals/" + sharedDealId, updateJson);
}
```

**Problems with Dependent Tests:**

1. **Cascading Failures:**
```
createDeal() fails
    ↓
updateDeal() fails (no deal to update)
    ↓
deleteDeal() fails (no deal to delete)

Result: 3 test failures, but only 1 real bug!
```

2. **Can't Run Tests Individually:**
```bash
# This will fail because createDeal() didn't run
mvn test -Dtest=DealServletIntegrationTest#updateDeal
```

3. **Order Sensitivity:**
```java
// If JUnit decides to run tests in different order:
updateDeal() runs first → FAILS (no deal exists yet)
createDeal() runs second → passes
```

4. **Maintenance Nightmare:**
```java
// Adding new test in middle breaks everything
@Test
@Order(1)
void createDeal() { ... }

@Test
@Order(2)  // NEW TEST
void validateDeal() { ... }

@Test
@Order(3)  // Was Order(2), now breaks!
void updateDeal() { ... }
```

**Our Approach:**

We use `@Order` but maintain independence:

```java
@Test
@Order(5)
void updateDeal_existingDeal_returns200() {
    // CREATES its own deal first
    HttpResponse<String> createResponse = post("/deals", createJson);
    String dealId = parseResponse(createResponse, Deal.class).getId();

    // Then updates it
    HttpResponse<String> response = put("/deals/" + dealId, updateJson);

    // Independent: can run alone!
}
```

**Best Practice:**
- Each test creates its own test data
- @Order is for readability, not dependency
- Tests can run in any order
- Can run single test in isolation

### Answer 1.4: Test Data Management

**Our Approach: In-Memory Storage**

From `ApiIntegrationTestBase.java:47`:
```java
server = new ApiServer(TEST_PORT);  // Uses InMemoryRepository
```

**How Test Data Works:**

```java
@BeforeAll
static void startServer() {
    server = new ApiServer(9999);  // Fresh repositories
    server.start();
}

@Test
void test1() {
    post("/deals", dealJson);  // Creates DEAL-001 in memory
}

@Test
void test2() {
    post("/deals", dealJson);  // Creates DEAL-002 in memory
}

@AfterAll
static void stopServer() {
    server.stop();  // All data disappears
}
```

**Data Lifecycle:**
```
Server Start
    ↓
Repositories empty
    ↓
Test 1 creates data → In-memory storage
Test 2 creates data → In-memory storage
Test 3 creates data → In-memory storage
    ↓
Server Stop
    ↓
All data gone (no cleanup needed!)
```

**Real Database Challenges:**

**1. Data Cleanup:**
```java
@AfterEach
void cleanupDatabase() {
    // Must delete all test data
    database.execute("DELETE FROM deals");
    database.execute("DELETE FROM users");
    database.execute("DELETE FROM commission_plans");
    database.execute("DELETE FROM disputes");
    // Tedious and error-prone!
}
```

**2. ID Generation:**
```java
// In-memory: IDs start fresh each time
Test 1: Creates DEAL-001
Test 2: Creates DEAL-001 (new server, fresh counter)

// Real database: IDs persist
Test 1: Creates DEAL-001
Test 2: Creates DEAL-002 (database remembers DEAL-001 exists)
    ↓
Tests may fail if they expect DEAL-001!
```

**3. Transaction Management:**
```java
@Test
void testDealCreation() {
    // Database transaction must be managed
    database.beginTransaction();
    try {
        post("/deals", dealJson);
        // ... assertions
        database.rollback();  // Undo changes
    } catch (Exception e) {
        database.rollback();
        throw e;
    }
}
```

**4. Test Isolation:**
```java
// Parallel tests conflict on real database
Test Thread 1: INSERT INTO deals (id='DEAL-001')
Test Thread 2: INSERT INTO deals (id='DEAL-001')  // CONFLICT!
    ↓
Unique constraint violation
```

**5. Performance:**
```java
// In-memory: ~0.01ms per operation
dealRepository.save(deal);  // Pure Java object manipulation

// Real database: ~10-100ms per operation
dealRepository.save(deal);  // Network round-trip, disk I/O
```

**Solutions for Real Database Testing:**

1. **Test Database:**
```java
@BeforeAll
void setupTestDatabase() {
    database.connect("jdbc:postgresql://localhost/commission_test");
    database.execute("CREATE SCHEMA test_" + UUID.randomUUID());
}
```

2. **Transaction Rollback:**
```java
@BeforeEach
void beginTransaction() {
    entityManager.getTransaction().begin();
}

@AfterEach
void rollbackTransaction() {
    entityManager.getTransaction().rollback();  // Undo all changes
}
```

3. **Database Containers (TestContainers):**
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withDatabaseName("test");
// Starts fresh PostgreSQL in Docker for each test run
```

**Why We Use In-Memory:**

| Reason | Benefit |
|--------|---------|
| **Speed** | 100x faster than real DB |
| **Simplicity** | No cleanup needed |
| **Isolation** | Each test class gets fresh data |
| **No Dependencies** | No database server required |
| **Deterministic** | Same initial state every time |

## Section 2: Test Fixture Pattern

### Answer 2.1: Test Fixture Purpose

**Test Fixture Pattern:**
A test fixture is the fixed state used as a baseline for running tests.

**Definition:**
- **Fixture** = The setup (and teardown) code that prepares the test environment
- **Pattern** = Reusable structure for consistent test infrastructure

**ApiIntegrationTestBase as Test Fixture:**

From `ApiIntegrationTestBase.java`:
```java
public abstract class ApiIntegrationTestBase {
    // FIXTURE STATE - shared across all tests
    protected static ApiServer server;
    protected static HttpClient httpClient;
    protected static final int TEST_PORT = 9999;
    protected static final String BASE_URL = "http://localhost:9999/api/v1";

    // FIXTURE SETUP
    @BeforeAll
    public static void startServer() throws Exception {
        server = new ApiServer(TEST_PORT);
        // Start server, wait for ready
        httpClient = HttpClient.newHttpClient();
    }

    // FIXTURE TEARDOWN
    @AfterAll
    public static void stopServer() throws Exception {
        server.stop();
    }

    // FIXTURE HELPERS - make tests easier to write
    protected HttpResponse<String> get(String path) { ... }
    protected HttpResponse<String> post(String path, String json) { ... }
    protected void assertStatus(...) { ... }
}
```

**How It's Used:**

```java
// Concrete test extends fixture
public class DealServletIntegrationTest extends ApiIntegrationTestBase {

    @Test
    void createDeal_validData_returns201() {
        // Uses fixture helper methods
        HttpResponse<String> response = post("/deals", dealJson);  // From fixture
        assertStatus(response, 201);  // From fixture

        // No need to:
        // - Start server manually
        // - Create HTTP client
        // - Build HTTP requests from scratch
        // - Stop server
    }
}
```

**Test Fixture Components:**

1. **Setup (`@BeforeAll`):**
   - Start embedded Tomcat
   - Initialize HTTP client
   - Wait for server to be ready

2. **Teardown (`@AfterAll`):**
   - Stop server gracefully
   - Clean up resources

3. **Helper Methods:**
   - `get()`, `post()`, `put()`, `delete()` - simplify HTTP requests
   - `parseResponse()` - JSON deserialization
   - `assertStatus()` - common assertion

4. **Shared State:**
   - Server instance
   - HTTP client
   - Base URL constant

**Benefits:**

1. **DRY (Don't Repeat Yourself):**
```java
// WITHOUT fixture - repetition in every test
@Test
void testA() {
    ApiServer server = new ApiServer(9999);
    server.start();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()...
    server.stop();
}

@Test
void testB() {
    // DUPLICATE same setup code!
    ApiServer server = new ApiServer(9999);
    server.start();
    ...
}

// WITH fixture - write once, use everywhere
@Test
void testA() {
    HttpResponse<String> response = post("/deals", json);  // Simple!
}

@Test
void testB() {
    HttpResponse<String> response = get("/deals");  // Simple!
}
```

2. **Consistency:**
   - All tests use same server configuration
   - Same port, same setup, same helpers
   - Reduces variability

3. **Maintainability:**
   - Change server port in one place
   - Update HTTP client configuration once
   - Add new helper method benefits all tests

4. **Readability:**
   - Tests focus on what they're testing, not infrastructure
   - Helper methods hide HTTP complexity

**Pattern Structure:**

```
┌─────────────────────────────────────┐
│   ApiIntegrationTestBase            │
│   (Abstract Test Fixture)           │
│                                     │
│   + Shared server and client        │
│   + Setup/teardown methods          │
│   + HTTP helper methods             │
└─────────────────────────────────────┘
              ▲
              │ extends
              │
    ┌─────────┴─────────┐
    │                   │
┌───┴──────────┐  ┌─────┴────────────┐
│ DealServlet  │  │ UserServlet      │
│ IntegrationTest  │  IntegrationTest │
│              │  │                  │
│ Inherits all │  │ Inherits all     │
│ fixture code │  │ fixture code     │
└──────────────┘  └──────────────────┘
```

### Answer 2.2: @BeforeAll and @AfterAll

**@BeforeAll vs @BeforeEach:**

From `ApiIntegrationTestBase.java:54`:
```java
@BeforeAll  // Runs ONCE before all tests
public static void startServer() throws Exception {
    server = new ApiServer(TEST_PORT);
    server.start();  // Expensive operation!
}
```

**@BeforeAll Execution:**
```
Test Class Execution:
├─ @BeforeAll startServer()  ← Runs ONCE
├─ @Test test1()
├─ @Test test2()
├─ @Test test3()
├─ @Test test4()
└─ @AfterAll stopServer()    ← Runs ONCE

Total server starts: 1
```

**@BeforeEach Alternative (BAD for our use case):**
```java
@BeforeEach  // Runs BEFORE EACH test
public void startServer() throws Exception {
    server = new ApiServer(TEST_PORT);
    server.start();  // Expensive!
}

@AfterEach  // Runs AFTER EACH test
public void stopServer() throws Exception {
    server.stop();
}
```

**@BeforeEach Execution:**
```
Test Class Execution:
├─ @BeforeEach startServer()  ← Test 1
├─ @Test test1()
├─ @AfterEach stopServer()
├─ @BeforeEach startServer()  ← Test 2
├─ @Test test2()
├─ @AfterEach stopServer()
├─ @BeforeEach startServer()  ← Test 3
├─ @Test test3()
├─ @AfterEach stopServer()
└─ ...

Total server starts: 4 (one per test!)
```

**Why @BeforeAll for Server:**

**1. Performance:**
```
@BeforeAll approach:
    Server startup: 2 seconds (once)
    Test execution: 0.1 seconds each × 10 tests = 1 second
    Total: 3 seconds

@BeforeEach approach:
    Server startup: 2 seconds × 10 tests = 20 seconds
    Test execution: 0.1 seconds each × 10 tests = 1 second
    Total: 21 seconds (7x slower!)
```

**2. Resource Usage:**
```java
// With @BeforeAll: One server instance
ApiServer server (runs entire test class)
    Port 9999 bound once
    Tomcat started once
    Memory allocated once

// With @BeforeEach: Multiple server instances
ApiServer server1 (test1)
ApiServer server2 (test2)
    ↓
Potential port conflicts
Excessive memory allocation
```

**3. State Management:**
```java
// In-memory repository is perfect for @BeforeAll
@BeforeAll
void startServer() {
    server = new ApiServer(9999);  // Empty repositories
}

@Test
void test1() {
    post("/deals", json);  // Creates DEAL-001
}

@Test
void test2() {
    post("/deals", json);  // Creates DEAL-002
    // Repository still has DEAL-001 from test1
    // But tests are independent (each creates own data)
}
```

**When to Use @BeforeEach:**

Use @BeforeEach when:
1. Setup is fast (< 100ms)
2. Tests modify shared state that must be reset
3. Complete isolation required

```java
// Example: Database tests
@BeforeEach
void setupTransaction() {
    entityManager.getTransaction().begin();  // Fast
}

@AfterEach
void rollbackTransaction() {
    entityManager.getTransaction().rollback();  // Clean state
}
```

**Our Approach:**
- @BeforeAll: Server lifecycle (expensive, shared)
- No @BeforeEach: Tests create own data (cheap, independent)

### Answer 2.3: Static Methods and Fields

**Why Static:**

From `ApiIntegrationTestBase.java:40-45`:
```java
// Static fields
protected static ApiServer server;
protected static HttpClient httpClient;

// Static methods
@BeforeAll
public static void startServer() { ... }

@AfterAll
public static void stopServer() { ... }
```

**JUnit 5 Requirement:**

`@BeforeAll` and `@AfterAll` methods **must be static** (unless using `@TestInstance(Lifecycle.PER_CLASS)`).

**Why JUnit Requires Static:**

**JUnit Test Instance Lifecycle:**
```java
public class DealServletIntegrationTest {

    @Test
    void test1() { ... }

    @Test
    void test2() { ... }
}

// What JUnit does (default):
new DealServletIntegrationTest().test1();  // New instance
new DealServletIntegrationTest().test2();  // New instance (different!)
```

**Each test method gets a fresh test class instance!**

**Problem with Non-Static:**
```java
// BAD: Instance fields
private ApiServer server;  // Not static

@BeforeAll  // ERROR: Must be static!
public void startServer() {
    this.server = new ApiServer(9999);
    // Which instance does 'this' refer to?
    // test1's instance or test2's instance?
}

@Test
void test1() {
    // This is instance 1
    // this.server was set in... which instance?
}

@Test
void test2() {
    // This is instance 2 (different from test1!)
    // this.server is null here!
}
```

**Solution: Static = Shared Across All Instances:**
```java
// GOOD: Static fields
protected static ApiServer server;  // Shared!

@BeforeAll
public static void startServer() {
    server = new ApiServer(9999);
    // Sets class-level static field
    // All test instances see same server
}

@Test
void test1() {
    post("/deals", json);  // Uses static server
}

@Test
void test2() {
    post("/deals", json);  // Uses same static server
}
```

**Static Lifecycle:**
```
Class Loading:
    ↓
Static fields initialized (server = null, httpClient = null)
    ↓
@BeforeAll startServer() runs
    ↓
server = new ApiServer(9999)  // Static field set
httpClient = HttpClient.newHttpClient()  // Static field set
    ↓
Test Instance 1 created
    test1() runs → uses static server ✓
    Instance 1 discarded
    ↓
Test Instance 2 created
    test2() runs → uses static server ✓
    Instance 2 discarded
    ↓
...all tests complete...
    ↓
@AfterAll stopServer() runs
    ↓
server.stop()  // Static field accessed
```

**Alternative: @TestInstance(PER_CLASS):**

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DealServletIntegrationTest {
    // Now can be non-static!
    private ApiServer server;

    @BeforeAll  // Non-static OK with PER_CLASS
    public void startServer() {
        this.server = new ApiServer(9999);
    }

    // Single instance used for all tests
}
```

But we don't use this because:
- Static is more explicit
- Matches JUnit default behavior
- Clear that server is shared

**Summary:**

| Static | Non-Static (with PER_CLASS) |
|--------|-------------|
| Required by default JUnit | Requires @TestInstance annotation |
| Explicit sharing | Implicit sharing |
| Class-level scope | Instance-level scope |
| Can't use instance fields | Can use instance fields |
| Standard approach | Alternative approach |

We use static because it's the standard, explicit, and doesn't require special configuration.

### Answer 2.4: Test Fixture Inheritance

**Benefits of Inheritance:**

**With Inheritance (Our Approach):**

```java
// Base class - write once
public abstract class ApiIntegrationTestBase {
    protected static ApiServer server;
    protected static HttpClient httpClient;

    @BeforeAll
    public static void startServer() { /* 20 lines of setup */ }

    @AfterAll
    public static void stopServer() { /* 5 lines of teardown */ }

    protected HttpResponse<String> get(String path) { /* 10 lines */ }
    protected HttpResponse<String> post(String path, String json) { /* 15 lines */ }
    protected HttpResponse<String> put(String path, String json) { /* 15 lines */ }
    protected HttpResponse<String> delete(String path) { /* 10 lines */ }
    protected <T> T parseResponse(...) { /* 3 lines */ }
    protected void assertStatus(...) { /* 5 lines */ }
}

// Test class - just tests!
public class DealServletIntegrationTest extends ApiIntegrationTestBase {
    @Test
    void createDeal_validData_returns201() {
        HttpResponse<String> response = post("/deals", dealJson);
        assertStatus(response, 201);
        // Clean, focused on testing logic
    }
}

// Another test class - same benefits!
public class UserServletIntegrationTest extends ApiIntegrationTestBase {
    @Test
    void createUser_validData_returns201() {
        HttpResponse<String> response = post("/users", userJson);
        assertStatus(response, 201);
        // Reuses all base class infrastructure
    }
}
```

**Total Code:**
- Base class: ~80 lines (written once)
- Deal tests: ~200 lines (pure test logic)
- User tests: ~100 lines (pure test logic)
- **Total: ~380 lines**

**Without Inheritance (Duplication):**

```java
public class DealServletIntegrationTest {
    // Copy-paste server setup
    protected static ApiServer server;
    protected static HttpClient httpClient;

    @BeforeAll
    public static void startServer() { /* duplicate 20 lines */ }

    @AfterAll
    public static void stopServer() { /* duplicate 5 lines */ }

    protected HttpResponse<String> get(String path) { /* duplicate 10 lines */ }
    protected HttpResponse<String> post(String path, String json) { /* duplicate 15 lines */ }
    // ... duplicate all helper methods

    @Test
    void createDeal_validData_returns201() { /* test code */ }
}

public class UserServletIntegrationTest {
    // Copy-paste AGAIN!
    protected static ApiServer server;
    protected static HttpClient httpClient;

    @BeforeAll
    public static void startServer() { /* duplicate 20 lines AGAIN */ }
    // ... duplicate ALL helper methods AGAIN

    @Test
    void createUser_validData_returns201() { /* test code */ }
}
```

**Total Code:**
- Deal tests: ~280 lines (80 infrastructure + 200 tests)
- User tests: ~180 lines (80 infrastructure + 100 tests)
- **Total: ~460 lines (21% more code!)**

**Benefits Summary:**

1. **DRY Principle:**
   - Infrastructure code written once
   - Bug fixes apply to all tests
   - Consistency guaranteed

2. **Maintainability:**
```java
// Change server port - ONE place
public abstract class ApiIntegrationTestBase {
    protected static final int TEST_PORT = 9999;  // Change here
}
// All test classes automatically updated!

// Without inheritance - change in EVERY class
```

3. **Easier to Write Tests:**
```java
// Complex HTTP request hidden by helper
HttpResponse<String> response = post("/deals", dealJson);

// vs building from scratch every time
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:9999/api/v1/deals"))
    .POST(HttpRequest.BodyPublishers.ofString(dealJson))
    .header("Content-Type", "application/json")
    .header("Accept", "application/json")
    .build();
HttpResponse<String> response = httpClient.send(request,
    HttpResponse.BodyHandlers.ofString());
```

4. **Consistent Behavior:**
   - All tests use same server configuration
   - Same HTTP client settings
   - Same timeout behavior
   - Same error handling

5. **Extensibility:**
```java
// Add feature to base class
protected <T> T postAndParse(String path, String json, Class<T> clazz) {
    HttpResponse<String> response = post(path, json);
    return parseResponse(response, clazz);
}

// All test classes get it for free!
@Test
void createDeal() {
    Deal deal = postAndParse("/deals", dealJson, Deal.class);
    assertEquals("Enterprise License", deal.getTitle());
}
```

**Potential Drawbacks:**

1. **Tight Coupling:**
   - Test classes depend on base class
   - Changes to base class affect all tests
   - But: This is acceptable for test infrastructure

2. **Less Flexibility:**
   - All tests get same setup
   - What if one test needs different configuration?
   - Solution: Override methods or use composition

```java
// Override for special case
public class SpecialTest extends ApiIntegrationTestBase {
    @BeforeAll
    public static void setupSpecialServer() {
        // Custom setup
        server = new ApiServer(8888);  // Different port
        server.start();
    }
}
```

3. **Hidden Complexity:**
   - New developers must understand base class
   - Setup/teardown not visible in test class
   - Mitigation: Good documentation, simple base class

**Verdict:**
Benefits far outweigh drawbacks for integration test fixtures. Standard practice in industry.

## Section 3: AAA Pattern (Arrange-Act-Assert)

### Answer 3.1: AAA Structure

**AAA Pattern:**
A structure for organizing test code into three distinct sections:

1. **Arrange:** Set up test conditions
2. **Act:** Execute the operation being tested
3. **Assert:** Verify the expected outcome

**Example from `DealServletIntegrationTest.java:38-61`:**

```java
@Test
void createDeal_validData_returns201() throws Exception {
    // ===== ARRANGE =====
    // Prepare test data: create JSON payload
    String dealJson = """
            {
                "title": "Enterprise Software License",
                "value": 100000.00,
                "salesRepId": "USER-001",
                "status": "OPEN"
            }
            """;

    // ===== ACT =====
    // Execute the operation: make POST request
    HttpResponse<String> response = post("/deals", dealJson);

    // ===== ASSERT =====
    // Verify expected outcomes
    assertStatus(response, 201);  // 1. Check status code

    Deal createdDeal = parseResponse(response, Deal.class);
    assertNotNull(createdDeal.getId(), "Server should generate ID");  // 2. Has ID
    assertEquals("Enterprise Software License", createdDeal.getTitle());  // 3. Title correct
    assertEquals(new BigDecimal("100000.00"), createdDeal.getValue());  // 4. Value correct
    assertEquals("USER-001", createdDeal.getSalesRepId());  // 5. SalesRep correct
}
```

**Breaking Down Each Section:**

**1. Arrange (Setup):**
```java
String dealJson = """
        {
            "title": "Enterprise Software License",
            "value": 100000.00,
            "salesRepId": "USER-001",
            "status": "OPEN"
        }
        """;
```
- Creates test data
- Prepares inputs
- Sets up preconditions
- **Question answered:** "What conditions do I need?"

**2. Act (Execute):**
```java
HttpResponse<String> response = post("/deals", dealJson);
```
- Calls the method/API being tested
- **Should be ONE operation** (or as few as possible)
- **Question answered:** "What am I testing?"

**3. Assert (Verify):**
```java
assertStatus(response, 201);
Deal createdDeal = parseResponse(response, Deal.class);
assertNotNull(createdDeal.getId());
assertEquals("Enterprise Software License", createdDeal.getTitle());
// ... more assertions
```
- Verifies expected outcomes
- Checks side effects
- **Question answered:** "Did it work correctly?"

**Why AAA Pattern Works:**

1. **Readability:**
   - Clear structure
   - Easy to understand test flow
   - Self-documenting

2. **Maintainability:**
   - Each section has single responsibility
   - Easy to modify one part without affecting others

3. **Debugging:**
```java
// Test fails - where's the problem?
@Test
void test() {
    // Arrange
    String json = createDealJson();  // Problem here? Check test data

    // Act
    HttpResponse<String> response = post("/deals", json);  // Problem here? Check API

    // Assert
    assertEquals(201, response.statusCode());  // Problem here? Check expectations
}
```

**Visual Separation:**

Some teams add comments to make sections explicit:
```java
@Test
void createDeal_validData_returns201() {
    // Arrange
    String dealJson = "...";

    // Act
    HttpResponse<String> response = post("/deals", dealJson);

    // Assert
    assertStatus(response, 201);
    assertEquals("Enterprise Software License", parseResponse(response, Deal.class).getTitle());
}
```

### Answer 3.2: Arrange Phase

**From `DealServletIntegrationTest.java:116-138`:**

```java
@Test
void updateDeal_existingDeal_returns200() throws Exception {
    // ===== ARRANGE =====
    // First, create a deal to update
    String createJson = """
            {
                "title": "Consulting Services",
                "value": 30000.00,
                "salesRepId": "USER-003",
                "status": "OPEN"
            }
            """;
    HttpResponse<String> createResponse = post("/deals", createJson);
    Deal createdDeal = parseResponse(createResponse, Deal.class);
    String dealId = createdDeal.getId();  // Extract ID for update

    // Prepare update payload
    String updateJson = String.format("""
            {
                "id": "%s",
                "title": "Consulting Services - Updated",
                "value": 35000.00,
                "salesRepId": "USER-003",
                "status": "WON"
            }
            """, dealId);

    // ===== ACT =====
    HttpResponse<String> response = put("/deals/" + dealId, updateJson);

    // ===== ASSERT =====
    assertStatus(response, 200);
    Deal updatedDeal = parseResponse(response, Deal.class);
    assertEquals("Consulting Services - Updated", updatedDeal.getTitle());
    assertEquals(DealStatus.WON, updatedDeal.getStatus());
}
```

**Why Create Deal First:**

**1. Test Independence:**
```java
// BAD: Assume deal exists from previous test
@Test
void updateDeal() {
    // What if this runs first? FAILS!
    put("/deals/DEAL-001", updateJson);
}

// GOOD: Create own preconditions
@Test
void updateDeal() {
    // Creates DEAL-001 first
    String dealId = post("/deals", createJson)...getId();
    // Now update is guaranteed to work
    put("/deals/" + dealId, updateJson);
}
```

**2. Clear Preconditions:**
```java
// Arrange phase documents what state is needed
String createJson = """...""";  // Shows what initial state looks like
post("/deals", createJson);  // Establishes that state
String updateJson = """...""";  // Shows what we're changing to

// Anyone reading the test understands:
// - Initial state: "Consulting Services", $30k, OPEN
// - Final state: "Consulting Services - Updated", $35k, WON
```

**3. Realistic Scenarios:**
```java
// Tests real-world workflow
1. Create deal
2. Update deal
3. Verify update worked

// This is what users actually do!
```

**Arrange Complexity:**

Sometimes Arrange is the largest section:
```java
@Test
void complexScenario() {
    // Arrange (30 lines)
    User salesRep = createUser("jsmith");
    CommissionPlan plan = createPlan("Q1 2024");
    Deal deal = createDeal(salesRep.getId(), plan.getId());
    List<DealProduct> products = createProducts(deal.getId());

    // Act (1 line)
    Commission commission = calculateCommission(deal.getId());

    // Assert (5 lines)
    assertEquals(new BigDecimal("10000.00"), commission.getAmount());
}
```

**This is normal and acceptable!**
- Complex tests need complex setup
- Better than hidden/magical setup
- Documents requirements clearly

### Answer 3.3: Act Phase

**The Act Phase:**

The "Act" section executes the single operation being tested.

**Good Example:**
```java
// Act - ONE method call
HttpResponse<String> response = post("/deals", dealJson);
```

**Why Keep It Minimal:**

**1. Clarity - What's Being Tested?**
```java
// GOOD: Clear what we're testing
@Test
void testDealCreation() {
    // Arrange
    String dealJson = "...";

    // Act - THIS is what we're testing
    HttpResponse<String> response = post("/deals", dealJson);

    // Assert
    assertEquals(201, response.statusCode());
}

// BAD: What are we testing?
@Test
void testDealWorkflow() {
    // Act - too many operations!
    HttpResponse<String> createResp = post("/deals", dealJson);
    HttpResponse<String> updateResp = put("/deals/DEAL-001", updateJson);
    HttpResponse<String> getResp = get("/deals/DEAL-001");
    HttpResponse<String> deleteResp = delete("/deals/DEAL-001");

    // Which operation failed if test fails?
}
```

**2. Single Responsibility:**

Each test should test ONE thing:
```java
// GOOD: Tests creation only
@Test
void createDeal_validData_returns201() {
    HttpResponse<String> response = post("/deals", dealJson);  // ONE operation
    assertEquals(201, response.statusCode());
}

// GOOD: Tests retrieval only
@Test
void getDeal_existingId_returns200() {
    String dealId = createTestDeal();  // Arrange
    HttpResponse<String> response = get("/deals/" + dealId);  // ONE operation
    assertEquals(200, response.statusCode());
}
```

**3. Easier Debugging:**
```java
// Test fails - error is in Act phase
@Test
void updateDeal() {
    String dealId = createTestDeal();

    HttpResponse<String> response = put("/deals/" + dealId, updateJson);
    // ↑ If this fails, we know PUT is broken

    assertEquals(200, response.statusCode());
}

// vs multiple operations - where's the bug?
@Test
void dealWorkflow() {
    post("/deals", createJson);  // Could fail here
    put("/deals/DEAL-001", updateJson);  // Or here
    get("/deals/DEAL-001");  // Or here
    // Which operation has the bug?
}
```

**When Act Has Multiple Lines:**

Sometimes you need multiple lines for context:
```java
// Act
HttpResponse<String> response = post("/deals", dealJson);
Deal createdDeal = parseResponse(response, Deal.class);  // Helper, not core operation
String dealId = createdDeal.getId();  // Extract data for assertions

// This is OK - still testing ONE operation (POST)
// Extra lines are just extracting results
```

**Act + Immediate Assertion:**

Some patterns combine Act and Assert:
```java
// Acceptable for simple cases
assertThrows(ConstraintViolationException.class, () -> {
    repository.save(invalidDeal);  // Act
});  // Assert (exception thrown)
```

**Complex Operations:**

For complex scenarios, consider:
```java
// Option 1: Extract to helper method
@Test
void testComplexWorkflow() {
    Deal deal = createAndSubmitDeal();  // Act (complex, but ONE logical operation)
    assertEquals(DealStatus.SUBMITTED, deal.getStatus());
}

private Deal createAndSubmitDeal() {
    // Complex logic encapsulated
}

// Option 2: Break into multiple tests
@Test
void createDeal() { /* test create */ }

@Test
void submitDeal() { /* test submit */ }

@Test
void approveDeal() { /* test approve */ }
```

**Summary:**
- Act should be ONE logical operation
- Multiple lines OK for extracting results
- If Act is complex, extract to helper or split tests
- Goal: Clear what's being tested

## Section 4: HTTP Testing

### Answer 4.1: Java HttpClient

**Our Choice: Java Built-in `HttpClient` (JDK 11+)**

From `ApiIntegrationTestBase.java:65`:
```java
httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
```

**Why Use Built-in HttpClient:**

**1. No External Dependencies:**
```xml
<!-- DON'T NEED THESE -->
<!-- Apache HttpClient -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>

<!-- OkHttp -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
</dependency>

<!-- Java HttpClient is BUILT-IN (JDK 11+) -->
```

**Benefits:**
- Smaller JAR size
- No version conflicts
- No security vulnerabilities from dependencies
- Always available (if using Java 11+)

**2. Modern API:**
```java
// Old HttpURLConnection (ugly!)
URL url = new URL("http://localhost:8080/api/v1/deals");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("POST");
conn.setRequestProperty("Content-Type", "application/json");
conn.setDoOutput(true);
OutputStream os = conn.getOutputStream();
os.write(json.getBytes());
os.flush();
int status = conn.getResponseCode();
// ... read response

// New HttpClient (clean!)
HttpResponse<String> response = httpClient.send(
    HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8080/api/v1/deals"))
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .header("Content-Type", "application/json")
        .build(),
    HttpResponse.BodyHandlers.ofString()
);
```

**3. Type Safety:**
```java
// Strongly typed request building
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(url))  // Type-safe URI
    .GET()  // Method enum
    .header("Accept", "application/json")  // String keys/values
    .build();

// Response type specified
HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());  // Returns HttpResponse<String>
```

**4. Sync and Async Support:**
```java
// Synchronous (what we use)
HttpResponse<String> response = httpClient.send(request,
    HttpResponse.BodyHandlers.ofString());

// Asynchronous (if needed)
CompletableFuture<HttpResponse<String>> futureResponse =
    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
```

**5. HTTP/2 Support:**
```java
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)  // HTTP/2 if needed
    .build();
```

**Comparison with Libraries:**

| Feature | Java HttpClient | Apache HttpClient | OkHttp |
|---------|----------------|-------------------|--------|
| **Dependency** | Built-in | External | External |
| **API Style** | Fluent builders | Builder pattern | Builder pattern |
| **Async** | ✅ CompletableFuture | ❌ (separate lib) | ✅ Callback |
| **HTTP/2** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Learning Curve** | Low | Medium | Low |
| **Community** | JDK team | Apache | Square |

**When to Use Alternatives:**

Use Apache HttpClient if:
- Stuck on Java 8
- Need advanced features (proxy auth, cookie management)
- Already using in project

Use OkHttp if:
- Android development
- Need advanced interceptors
- Prefer Square's ecosystem

**Our Context:**
- Integration tests in Java 21 project
- Simple HTTP requests
- No special requirements
- **Java HttpClient is perfect!**

### Answer 4.2: Request Building

**From `ApiIntegrationTestBase.java:83-100`:**
```java
protected HttpResponse<String> post(String path, String jsonBody) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + path))  // Build full URL
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))  // Set method and body
            .header("Content-Type", "application/json")  // Header 1
            .header("Accept", "application/json")  // Header 2
            .build();

    return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
}
```

**Headers Set:**

**1. `Content-Type: application/json`**

**Purpose:** Tells server what format the request body is in.

```
Client                          Server
  |                              |
  |  POST /api/v1/deals          |
  |  Content-Type: application/json  <-- "I'm sending JSON"
  |  Body: {"title": "..."}      |
  |  ──────────────────────────> |
  |                              |
  |                         Server knows to
  |                         parse body as JSON
```

**Without this header:**
```java
// Server doesn't know what format body is
// Could be:
// - JSON: {"title": "Deal"}
// - XML: <deal><title>Deal</title></deal>
// - Form data: title=Deal&value=1000
// - Plain text: Just some text

// Server might:
// - Reject request (400 Bad Request)
// - Parse incorrectly
// - Throw exception
```

**In our servlet (`BaseServlet.java`):**
```java
protected String readRequestBody(HttpServletRequest request) throws IOException {
    // Reads body as string
    // Then JsonHelper.fromJson() assumes it's JSON
    // Content-Type header signals this is correct assumption
}
```

**2. `Accept: application/json`**

**Purpose:** Tells server what format client can handle in response.

```
Client                          Server
  |                              |
  |  GET /api/v1/deals/DEAL-001  |
  |  Accept: application/json    <-- "Send me JSON"
  |  ──────────────────────────> |
  |                              |
  |                         Server formats
  |                         response as JSON
  |  <─────────────────────────  |
  |  Content-Type: application/json
  |  Body: {"id": "DEAL-001", ...}
  |                              |
```

**Without this header:**
```java
// Server might send:
// - JSON (default in our servlet)
// - XML
// - HTML
// - Plain text

// Client must handle whatever server sends
```

**Content Negotiation:**

In a more advanced API:
```java
GET /api/v1/deals/DEAL-001
Accept: application/xml  // Client requests XML

// Server responds:
Content-Type: application/xml
<deal>
  <id>DEAL-001</id>
  <title>Enterprise License</title>
</deal>
```

**Our servlet always returns JSON:**
```java
// From BaseServlet.java:56-58
response.setContentType("application/json");  // Always JSON
response.setCharacterEncoding("UTF-8");
```

**Why These Headers Matter:**

1. **Correctness:**
   - Server knows how to parse request
   - Client knows how to parse response

2. **Error Prevention:**
   - Mismatched formats caught early
   - Clear error messages

3. **API Standards:**
   - RESTful APIs expect these headers
   - Good practice for HTTP

4. **Debugging:**
```http
POST /api/v1/deals
Content-Type: application/xml  <-- Mismatch!
Body: {"title": "Deal"}  <-- This is JSON, not XML

// Server tries to parse as XML, fails
// Returns 400 Bad Request with clear error
```

**Other Common Headers:**

```java
// Authorization
.header("Authorization", "Bearer " + token)

// Custom headers
.header("X-Request-ID", UUID.randomUUID().toString())

// User Agent
.header("User-Agent", "Integration-Tests/1.0")

// CORS
.header("Origin", "http://localhost:3000")
```

**Summary:**
- `Content-Type`: Format of request body
- `Accept`: Desired format of response
- Both signal JSON in our tests
- Standard practice for REST APIs

### Answer 4.3: Response Handling

**From `ApiIntegrationTestBase.java:129-133`:**
```java
protected <T> T parseResponse(HttpResponse<String> response, Class<T> clazz) {
    return JsonHelper.fromJson(response.body(), clazz);
}
```

**How It Works:**

**Step 1: Get Response Body as String**
```java
HttpResponse<String> response = post("/deals", dealJson);
String jsonBody = response.body();
// jsonBody = "{\"id\":\"DEAL-001\",\"title\":\"Enterprise License\", ...}"
```

**Step 2: Pass to JsonHelper**
```java
Deal deal = JsonHelper.fromJson(jsonBody, Deal.class);
```

**Step 3: JsonHelper Uses Gson**

From `JsonHelper.java:29-44`:
```java
private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .registerTypeAdapter(LocalDate.class, ...)  // Custom date handling
        .registerTypeAdapter(LocalDateTime.class, ...)
        .create();

public static <T> T fromJson(String json, Class<T> classOfT) {
    return GSON.fromJson(json, classOfT);  // Gson does the work
}
```

**Gson Deserialization Process:**

```
JSON String:
{
  "id": "DEAL-001",
  "title": "Enterprise License",
  "value": 100000.00,
  "status": "WON",
  "salesRepId": "USER-001",
  "closeDate": "2024-01-15"
}
      ↓ GSON.fromJson()

Deal Object:
Deal {
    id = "DEAL-001"
    title = "Enterprise License"
    value = BigDecimal("100000.00")
    status = DealStatus.WON
    salesRepId = "USER-001"
    closeDate = LocalDate.of(2024, 1, 15)
}
```

**Field Mapping:**

Gson uses reflection to map JSON fields to Java fields:

```java
public class Deal {
    private String id;  // Maps to JSON "id"
    private String title;  // Maps to JSON "title"
    private BigDecimal value;  // Maps to JSON "value" (String → BigDecimal)
    private DealStatus status;  // Maps to JSON "status" (String → Enum)

    // Getters/setters used by Gson
}
```

**Type Conversion:**

Gson handles type conversion automatically:

```
JSON                     Java Type
"DEAL-001"        →      String
100000.00         →      BigDecimal
"WON"             →      DealStatus (enum)
"2024-01-15"      →      LocalDate (via custom adapter)
[...]             →      List<DealProduct>
```

**Custom Type Adapters:**

For LocalDate and LocalDateTime, we registered custom adapters:

```java
// Without adapter: Gson doesn't know how to handle LocalDate
// With adapter: Converts "2024-01-15" → LocalDate.of(2024, 1, 15)
.registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
    (json, typeOfT, context) -> LocalDate.parse(
        json.getAsString(),
        DateTimeFormatter.ISO_LOCAL_DATE))
```

**Complete Flow:**

```
1. HTTP Response arrives
   Content-Type: application/json
   Body: {"id":"DEAL-001",...}

2. HttpClient parses to HttpResponse<String>
   response.body() = JSON string

3. parseResponse() extracts body
   String json = response.body()

4. JsonHelper.fromJson() called
   Deal deal = JsonHelper.fromJson(json, Deal.class)

5. Gson deserializes
   - Reads JSON string
   - Uses reflection on Deal.class
   - Creates new Deal()
   - Sets fields via setters
   - Applies type converters
   - Handles nested objects (DealProduct list)

6. Returns typed object
   Deal deal (fully populated)
```

**Example Usage in Test:**

```java
@Test
void createDeal_validData_returns201() {
    // Make request
    HttpResponse<String> response = post("/deals", dealJson);

    // Parse response (type-safe!)
    Deal createdDeal = parseResponse(response, Deal.class);

    // Now can use Deal methods
    assertNotNull(createdDeal.getId());
    assertEquals("Enterprise License", createdDeal.getTitle());
    assertTrue(createdDeal.getValue().compareTo(BigDecimal.ZERO) > 0);
}
```

**Benefits:**

1. **Type Safety:**
```java
Deal deal = parseResponse(response, Deal.class);  // Compile-time type checking
deal.getTitle();  // IDE autocomplete works!
```

2. **Reusability:**
```java
Deal deal = parseResponse(response, Deal.class);
User user = parseResponse(response, User.class);
CommissionPlan plan = parseResponse(response, CommissionPlan.class);
// Same method works for any type!
```

3. **Error Handling:**
```java
try {
    Deal deal = parseResponse(response, Deal.class);
} catch (JsonSyntaxException e) {
    // Invalid JSON
} catch (Exception e) {
    // Other parsing errors
}
```

**Library: Gson**

We use Google's Gson library:
- Simple API
- Good performance
- Handles complex types
- Customizable with type adapters
- No annotations required (works with plain POJOs)

**Alternatives:**

| Library | Pros | Cons |
|---------|------|------|
| **Gson** | Simple, no annotations | Reflection-based (slower) |
| **Jackson** | Faster, more features | More complex, needs annotations |
| **JSON-B** | Java EE standard | Requires Java EE |
| **org.json** | Lightweight | Manual mapping |

For testing, Gson's simplicity is perfect!

### Answer 4.4: Status Code Verification

**Why Verify Status Codes:**

HTTP status codes are the primary way servers communicate operation results.

**From `ApiIntegrationTestBase.java:139-146`:**
```java
protected void assertStatus(HttpResponse<String> response, int expectedStatus) {
    if (response.statusCode() != expectedStatus) {
        throw new AssertionError(
                String.format("Expected status %d but got %d. Response body: %s",
                        expectedStatus, response.statusCode(), response.body()));
    }
}
```

**Status Code Categories:**

**2xx Success:**
- **200 OK:** Successful GET, PUT
- **201 Created:** Successful POST (resource created)
- **204 No Content:** Successful DELETE (no body to return)

**4xx Client Error:**
- **400 Bad Request:** Invalid input (malformed JSON, validation failure)
- **404 Not Found:** Resource doesn't exist
- **409 Conflict:** Resource already exists

**5xx Server Error:**
- **500 Internal Server Error:** Unexpected server problem
- **503 Service Unavailable:** Server temporarily down

**Examples from Our Tests:**

**1. Creating Resource (201 Created):**
```java
@Test
void createDeal_validData_returns201() {
    HttpResponse<String> response = post("/deals", dealJson);
    assertStatus(response, 201);  // Must be 201, not 200!

    // Why 201?
    // - Signals new resource was created
    // - Standard REST convention
    // - Client knows to expect ID in response
}
```

**If server returned 200 instead of 201:**
```java
assertStatus(response, 201);  // FAILS!
// Error: Expected status 201 but got 200

// Problem: Indicates implementation bug
// - Servlet should return 201 for POST
// - Returning 200 is technically wrong
```

**2. Successful Retrieval (200 OK):**
```java
@Test
void getDealById_existingId_returns200() {
    HttpResponse<String> response = get("/deals/DEAL-001");
    assertStatus(response, 200);  // Must be 200

    // Why 200?
    // - Resource exists and was retrieved
    // - Standard for successful GET
}
```

**3. Successful Deletion (204 No Content):**
```java
@Test
void deleteDeal_existingDeal_returns204() {
    HttpResponse<String> response = delete("/deals/DEAL-001");
    assertStatus(response, 204);  // Must be 204

    // Why 204?
    // - Delete succeeded
    // - No response body (deleted resource is gone)
    // - 200 would also be OK, but 204 is more precise
}
```

**4. Resource Not Found (404):**
```java
@Test
void getDealById_nonExistentId_returns404() {
    HttpResponse<String> response = get("/deals/DEAL-999999");
    assertStatus(response, 404);  // Must be 404

    // Why 404?
    // - Resource doesn't exist
    // - Client can distinguish "not found" from other errors
    // - Signals client error (wrong ID)
}
```

**5. Invalid Input (400 Bad Request):**
```java
@Test
void createDeal_invalidJson_returns400() {
    String invalidJson = "{broken json";
    HttpResponse<String> response = post("/deals", invalidJson);
    assertStatus(response, 400);  // Must be 400

    // Why 400?
    // - Client sent bad data
    // - Not 500 (server is fine, client is wrong)
    // - Client should fix and retry
}
```

**Why Status Codes Matter in Tests:**

**1. API Contract Verification:**
```java
// OpenAPI spec says POST /deals returns 201
@Test
void createDeal() {
    assertStatus(response, 201);  // Verifies spec compliance
}
```

**2. Error Handling:**
```java
// Client code depends on status codes
if (response.statusCode() == 404) {
    showNotFoundError();
} else if (response.statusCode() == 400) {
    showValidationErrors();
}

// Tests ensure server sends correct codes
```

**3. Debugging:**
```java
// Test fails: assertStatus(response, 200)
// Error: Expected 200 but got 500
//
// Immediately know: Server error (500)
// Check server logs for exception
```

**4. RESTful Semantics:**
```java
// Proper REST API uses correct status codes
POST   /deals       → 201 Created
GET    /deals/123   → 200 OK or 404 Not Found
PUT    /deals/123   → 200 OK or 404 Not Found
DELETE /deals/123   → 204 No Content or 404 Not Found
```

**What Status Codes Indicate:**

| Code | Meaning | Client Action | Server State |
|------|---------|---------------|--------------|
| **200** | Success | Use response data | Unchanged or modified |
| **201** | Created | Use new resource ID | New resource exists |
| **204** | Success (no content) | Operation complete | Resource deleted/modified |
| **400** | Bad input | Fix request and retry | Unchanged |
| **404** | Not found | Different ID or create | Unchanged |
| **500** | Server error | Report bug or retry later | Unknown |

**Comprehensive Status Testing:**

```java
// Test all status codes for an endpoint
@Test void create_success() { assertStatus(post(...), 201); }
@Test void create_invalid() { assertStatus(post(invalidData), 400); }

@Test void get_success() { assertStatus(get("/deals/123"), 200); }
@Test void get_notFound() { assertStatus(get("/deals/999"), 404); }

@Test void update_success() { assertStatus(put(...), 200); }
@Test void update_notFound() { assertStatus(put("/deals/999"), 404); }
@Test void update_invalid() { assertStatus(put(invalidData), 400); }

@Test void delete_success() { assertStatus(delete(...), 204); }
@Test void delete_notFound() { assertStatus(delete("/deals/999"), 404); }
```

**Benefits:**
- Verifies API behaves correctly
- Tests error handling
- Ensures RESTful compliance
- Makes tests more specific and valuable

## [Continued in Part 2 due to length...]

*Note: This ANSWERS.md file is extensive. The remaining sections (5-15 and Bonus) follow the same detailed pattern, covering Test Naming, Test Order, Sample Data, Error Testing, Query Parameters, Coverage, Postman, Performance, CI/CD, Best Practices, and Advanced Topics. Each answer includes code examples, diagrams, comparisons, and practical explanations.*

**Would you like me to continue with the remaining sections?**