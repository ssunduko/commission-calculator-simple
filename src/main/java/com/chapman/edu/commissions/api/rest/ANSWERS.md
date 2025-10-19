# REST API Implementation - Study Answers

This document provides detailed answers to the questions in QUESTIONS.md.

## Section 1: REST API Fundamentals

### Answer 1.1: HTTP Methods and CRUD

**Relationship between HTTP methods and CRUD:**

| CRUD Operation | HTTP Method | Idempotent? |
|----------------|-------------|-------------|
| **C**reate | POST | No |
| **R**ead | GET | Yes |
| **U**pdate | PUT | Yes |
| **D**elete | DELETE | Yes |

**Why use correct HTTP methods:**

1. **Semantic clarity**: The method itself indicates the operation's intent
2. **Caching**: Browsers and proxies know GET is safe to cache, but not POST
3. **Idempotency**: PUT and DELETE can be safely retried, POST cannot
4. **RESTful conventions**: Following standards makes APIs predictable and easier to use
5. **HTTP tooling**: Many frameworks and libraries have method-specific behaviors

**Example from our code** (`DealServlet.java`):
- `doGet()` - Read operations (list all, get by ID)
- `doPost()` - Create new deal
- `doPut()` - Update existing deal
- `doDelete()` - Delete deal

### Answer 1.2: HTTP Status Codes

**200 OK vs 201 Created:**
- **200 OK**: Request succeeded. Used for GET (retrieved successfully) and PUT (updated successfully)
- **201 Created**: Resource was successfully created. Used for POST operations
- **Distinction**: 201 specifically indicates a new resource was created, 200 is more general success

**Why DELETE returns 204 No Content:**
- **204 No Content**: Success, but no response body to return
- Deletion doesn't need to return the deleted object
- Signals "operation succeeded, nothing to show you"
- More efficient than returning empty body with 200

**400 Bad Request vs 404 Not Found:**
- **400 Bad Request**: Client sent invalid data (malformed JSON, invalid field values, missing required fields)
  - Example: `POST /deals` with invalid JSON
  - See `DealServlet.java:82-86`
- **404 Not Found**: Resource with specified ID doesn't exist
  - Example: `GET /deals/DEAL-999` where DEAL-999 doesn't exist
  - See `DealServlet.java:172-174`

**Other common codes:**
- **500 Internal Server Error**: Unexpected server-side error (not used in our basic implementation)
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Authenticated but not authorized

### Answer 1.3: Resource Naming

**Why resource-based URLs are RESTful:**

❌ **Action-based (non-RESTful):**
```
POST /api/v1/createDeal
GET  /api/v1/getDeal?id=123
POST /api/v1/updateDeal
POST /api/v1/deleteDeal
```

✅ **Resource-based (RESTful):**
```
POST   /api/v1/deals
GET    /api/v1/deals/123
PUT    /api/v1/deals/123
DELETE /api/v1/deals/123
```

**Advantages:**

1. **Nouns vs Verbs**: Resources (nouns) are cleaner than actions (verbs). HTTP methods already indicate the action
2. **Consistency**: All resources follow same pattern
3. **Predictability**: If you know `/deals`, you can guess `/users`, `/commission-plans`
4. **HTTP semantics**: Leverages HTTP methods for their intended purpose
5. **Standard tooling**: REST frameworks work better with resource-based design

**Key principle**: URL identifies *what* (the resource), HTTP method indicates *how* (the operation).

### Answer 1.4: Statelessness

**What statelessness means:**

Each HTTP request contains all information needed to process it. The server doesn't remember previous requests.

**How our implementation demonstrates this:**

1. **No session storage**: Servlets don't store client state between requests
2. **Complete requests**: Each request includes all needed data (see `DealServlet.doPost()` - entire deal in request body)
3. **No server-side state**: `InMemoryRepository` stores data, not session state
4. **Scalability**: Any server instance can handle any request (enables horizontal scaling)

**Example:**
```java
// Each POST request contains complete deal information
POST /api/v1/deals
{
  "title": "Enterprise License",
  "value": 100000.00,
  "salesRepId": "USER-001"  // All needed info in THIS request
}
```

**What would violate statelessness:**
- Storing "current user" in servlet instance variable
- Requiring sequence of requests (login → get token → use token without sending it)
- Server remembering previous requests

## Section 2: Design Patterns

### Answer 2.1: Repository Pattern

**Problems the Repository pattern solves:**

1. **Separation of Concerns**: Data access logic separated from business logic
2. **Abstraction**: Hides data storage details from servlets
3. **Testability**: Easy to swap real repository with mock for testing
4. **Flexibility**: Can change storage mechanism (in-memory → database) without changing servlets

**How it improves testability:**

```java
// Easy to create mock repository for testing
class MockRepository<T> implements Repository<T> {
    private List<T> testData = new ArrayList<>();

    @Override
    public List<T> findAll() {
        return testData;
    }
    // ... other methods
}

// Test servlet with mock data
@Test
void testGetAllDeals() {
    Repository<Deal> mockRepo = new MockRepository<>();
    mockRepo.save(createTestDeal());
    DealServlet servlet = new DealServlet(mockRepo);
    // ... test servlet behavior
}
```

**Why generic `Repository<T>`:**

Instead of this (repetitive):
```java
interface DealRepository {
    List<Deal> findAll();
    Optional<Deal> findById(String id);
    // ...
}

interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    // ...
}
```

We have this (DRY):
```java
interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    // ...
}

// Used as:
Repository<Deal> dealRepo;
Repository<User> userRepo;
```

**Benefits:**
- Single implementation (`InMemoryRepository<T>`)
- Type safety (can't put User in Deal repository)
- Consistency across all entity types

### Answer 2.2: Template Method Pattern

**What is Template Method pattern:**

Define the skeleton of an algorithm in a base class, with specific steps implemented by subclasses.

**Three methods demonstrating this in `BaseServlet`:**

1. **`readRequestBody(HttpServletRequest request)`** (`BaseServlet.java:30`)
   - Common logic to read JSON from request
   - All servlets need this, implemented once

2. **`sendJsonResponse(HttpServletResponse response, Object object, int statusCode)`** (`BaseServlet.java:56`)
   - Common logic to send JSON response with status code
   - Handles content-type, encoding, serialization

3. **`extractResourceId(HttpServletRequest request)`** (`BaseServlet.java:84`)
   - Common logic to parse ID from URL path
   - Works for any resource type

**How this supports DRY:**

Without BaseServlet, every servlet would duplicate:
```java
// Duplicated in DealServlet, UserServlet, etc.
String json = "";
BufferedReader reader = request.getReader();
String line;
while ((line = reader.readLine()) != null) {
    json += line;
}
```

With BaseServlet:
```java
// Used in all servlets, defined once
String json = readRequestBody(request);
```

**Pattern structure:**
```
BaseServlet (Abstract)
    ↓ extends
DealServlet (Concrete)
    → uses readRequestBody() from parent
    → implements doGet(), doPost(), etc.
```

### Answer 2.3: Dependency Injection

**What is Dependency Injection:**

A class receives its dependencies from external source rather than creating them internally.

❌ **Without DI (tight coupling):**
```java
public class DealServlet {
    private Repository<Deal> repository;

    public DealServlet() {
        // Servlet creates its own dependency
        this.repository = new InMemoryRepository<>("DEAL-", ...);
    }
}
```

✅ **With DI (loose coupling):**
```java
public class DealServlet {
    private final Repository<Deal> repository;

    // Dependency injected via constructor
    public DealServlet(Repository<Deal> dealRepository) {
        this.repository = dealRepository;
    }
}
```

**Advantages of constructor injection over field injection:**

1. **Immutability**: Can make field `final`, ensuring it's never changed
2. **Testability**: Easy to pass mock in tests
3. **Explicit dependencies**: Constructor signature shows what's required
4. **No reflection needed**: Plain Java, no framework magic
5. **Fail-fast**: If dependency is null, constructor fails immediately

**How DI supports Dependency Inversion Principle:**

```
High-level module: DealServlet
        ↓ depends on (abstraction)
    Repository<T> (interface)
        ↑ implements
Low-level module: InMemoryRepository<T>
```

Both depend on the abstraction (`Repository`), not concrete implementation. DealServlet doesn't know or care that it's getting an `InMemoryRepository`.

**See `ApiServer.java:120-121`:**
```java
String dealServletName = "DealServlet";
Tomcat.addServlet(context, dealServletName, new DealServlet(dealRepository));
```

ApiServer creates the repository and injects it into the servlet.

### Answer 2.4: Singleton Pattern

**Why `private static final`:**

```java
private static final Gson GSON = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(LocalDate.class, ...)
    .create();
```

- **`private`**: Only accessible within JsonHelper class
- **`static`**: One instance shared across all JsonHelper usage
- **`final`**: Cannot be reassigned, truly immutable reference

**Benefits of singleton for JSON conversion:**

1. **Configuration consistency**: All JSON uses same date formats, null handling, etc.
2. **Performance**: Creating Gson is expensive (reflection, type adapter registration), do it once
3. **Memory efficiency**: Single instance vs creating thousands during request handling
4. **Thread-safety**: Gson is thread-safe, one instance can serve all requests

**Drawback of creating new Gson each time:**

```java
// BAD: Creating new instance for each operation
public static String toJson(Object obj) {
    Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate.class, ...)
        .create();  // Expensive!
    return gson.toJson(obj);
}
```

**Problems:**
- Wasteful object creation
- Repeated type adapter registration (reflection overhead)
- Garbage collection pressure
- Inconsistent configuration if you forget settings

### Answer 2.5: Embedded Server Pattern

**Three advantages of embedded server:**

1. **Self-contained deployment**:
   - Single JAR contains application + server
   - No separate Tomcat installation needed
   - Easier for developers and DevOps

2. **Simplified configuration**:
   - Programmatic configuration (Java code) vs XML files
   - Version control friendly
   - Type-safe configuration

3. **Development efficiency**:
   - Start server with `main()` method
   - No deployment steps
   - Faster iteration cycle

**Potential disadvantages:**

1. **Resource control**: Harder to share one server among multiple apps
2. **Centralized management**: Can't manage all apps from one admin console
3. **Updates**: To update Tomcat, must rebuild/redeploy app
4. **Size**: JAR includes full server (larger artifact)

**When to choose embedded vs external:**

**Choose embedded when:**
- Microservices architecture (each service self-contained)
- Cloud/container deployment (Docker, Kubernetes)
- Development/testing
- Simple deployment requirements

**Choose external when:**
- Multiple apps on one server
- Enterprise environment with centralized Tomcat management
- Need to update server without touching apps
- Very large applications (share server resources)

**Our implementation** (`ApiServer.java:50`):
```java
this.tomcat = new Tomcat();
tomcat.setPort(port);
// ... configure programmatically
tomcat.start();
```

## Section 3: SOLID Principles

### Answer 3.1: Single Responsibility Principle (SRP)

**Definition**: A class should have only one reason to change.

**Three classes with single responsibilities:**

1. **`JsonHelper`** (`JsonHelper.java:19`):
   - **Responsibility**: JSON serialization/deserialization
   - **One reason to change**: JSON format/library changes

2. **`InMemoryRepository<T>`** (`InMemoryRepository.java:20`):
   - **Responsibility**: Data storage and retrieval
   - **One reason to change**: Storage mechanism changes

3. **`DealServlet`** (`DealServlet.java:22`):
   - **Responsibility**: Handle HTTP requests for Deal resources
   - **One reason to change**: Deal API endpoints change

**SRP violation example:**

❌ **Bad (multiple responsibilities):**
```java
public class DealServlet extends HttpServlet {
    private Map<String, Deal> deals = new HashMap<>();  // Data storage
    private Gson gson = new Gson();  // JSON handling

    protected void doGet(...) {
        // HTTP handling
        Deal deal = deals.get(id);  // Data access
        String json = gson.toJson(deal);  // JSON conversion
        response.getWriter().write(json);
    }
}
```

This class has THREE responsibilities:
1. HTTP request handling
2. Data storage
3. JSON serialization

**Why SRP makes code easier to maintain:**

1. **Focused changes**: Changing JSON library only affects JsonHelper
2. **Easier testing**: Test one responsibility at a time
3. **Reusability**: JsonHelper can be used by any servlet
4. **Understanding**: Smaller, focused classes are easier to understand
5. **Reduced coupling**: Classes depend on fewer things

### Answer 3.2: Open/Closed Principle (OCP)

**Definition**: Software entities should be open for extension, but closed for modification.

**How `BaseServlet` demonstrates OCP:**

```java
// BaseServlet is CLOSED for modification
abstract class BaseServlet extends HttpServlet {
    protected String readRequestBody(...) { }
    protected void sendJsonResponse(...) { }
    // These methods don't change when adding new servlets
}

// OPEN for extension - create new servlet without modifying BaseServlet
class DealServlet extends BaseServlet {
    protected void doGet(...) {
        // Use inherited methods
        String json = readRequestBody(request);
        sendJsonResponse(response, deal);
    }
}

class UserServlet extends BaseServlet {
    // Another extension, BaseServlet unchanged
}
```

**Adding new resource (PaymentServlet):**

```java
// 1. Create new servlet (extension)
public class PaymentServlet extends BaseServlet {
    private Repository<Payment> paymentRepository;

    public PaymentServlet(Repository<Payment> repo) {
        this.paymentRepository = repo;
    }

    protected void doGet(...) { /* implementation */ }
}

// 2. Register in ApiServer (only change needed)
Tomcat.addServlet(context, "PaymentServlet",
                  new PaymentServlet(paymentRepository));
context.addServletMappingDecoded("/api/v1/payments/*", "PaymentServlet");
```

**What we DON'T need to change:**
- BaseServlet (closed for modification)
- JsonHelper
- Repository interface
- Other servlets

**This demonstrates OCP because:**
- Added new functionality (PaymentServlet)
- Without modifying existing classes (BaseServlet, etc.)
- Through extension (subclassing)

### Answer 3.3: Liskov Substitution Principle (LSP)

**Definition**: Objects of a superclass should be replaceable with objects of a subclass without breaking the application.

**Can any `Repository<Deal>` replace `InMemoryRepository<Deal>`?**

Yes! Example:

```java
// Servlet depends on Repository interface
public class DealServlet {
    private Repository<Deal> repository;

    public DealServlet(Repository<Deal> dealRepository) {
        this.repository = dealRepository;  // Any Repository<Deal> works
    }
}

// Can substitute with any implementation
Repository<Deal> repo1 = new InMemoryRepository<>(...);
Repository<Deal> repo2 = new DatabaseRepository<>(...);
Repository<Deal> repo3 = new RedisRepository<>(...);

// All work with DealServlet
DealServlet servlet = new DealServlet(repo1);  // Works
DealServlet servlet = new DealServlet(repo2);  // Works
DealServlet servlet = new DealServlet(repo3);  // Works
```

**Contract all Repository implementations must fulfill:**

From `Repository.java:13`:
```java
interface Repository<T> {
    List<T> findAll();         // Must return all entities
    Optional<T> findById(String id);  // Must return entity if found, empty if not
    T save(T entity);          // Must save and return the entity
    boolean deleteById(String id);    // Must delete, return true if existed
    String generateId();       // Must return unique ID
}
```

**Example violation of LSP:**

```java
class BrokenRepository<T> implements Repository<T> {
    @Override
    public Optional<T> findById(String id) {
        return null;  // VIOLATION! Should return Optional, not null
    }

    @Override
    public T save(T entity) {
        throw new UnsupportedOperationException("Read-only!");
        // VIOLATION! Interface says save should work
    }
}

// This breaks code expecting Repository behavior
Repository<Deal> repo = new BrokenRepository<>();
DealServlet servlet = new DealServlet(repo);
servlet.doPost(...);  // Calls save(), unexpectedly throws exception!
```

**Why this matters:**
- Servlets trust the Repository contract
- Substituting broken implementation breaks that trust
- LSP ensures polymorphism actually works

### Answer 3.4: Interface Segregation Principle (ISP)

**Definition**: No client should be forced to depend on methods it doesn't use.

**Does `Repository<T>` follow ISP?**

**Yes, mostly.** The interface provides only essential CRUD operations:
```java
interface Repository<T> {
    List<T> findAll();         // Read all
    Optional<T> findById(...);  // Read one
    T save(...);               // Create/Update
    boolean deleteById(...);    // Delete
    String generateId();        // Utility
}
```

All servlets use all these methods, so there's no "fat interface" problem.

**What would violate ISP:**

```java
// TOO MANY methods, clients don't need all of them
interface FatRepository<T> {
    // CRUD - everyone needs these
    List<T> findAll();
    T save(T entity);

    // Querying - maybe not needed
    List<T> findByProperty(String property, Object value);
    List<T> search(String query);

    // Pagination - maybe not needed
    Page<T> findPage(int pageNum, int pageSize);

    // Caching - implementation detail, shouldn't be in interface
    void clearCache();
    void warmCache();

    // Transactions - maybe not needed
    void beginTransaction();
    void commit();
    void rollback();
}

// Servlet forced to know about methods it doesn't use
Repository<Deal> repo = ...;
repo.clearCache();  // Why should servlet care about cache?
```

**How to refactor for better ISP:**

```java
// Minimal interface for basic operations
interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    T save(T entity);
    boolean deleteById(String id);
}

// Separate interface for searching
interface SearchableRepository<T> extends Repository<T> {
    List<T> findByProperty(String property, Object value);
    List<T> search(String query);
}

// Separate interface for pagination
interface PageableRepository<T> extends Repository<T> {
    Page<T> findPage(int pageNum, int pageSize);
}

// Clients choose what they need
Repository<Deal> basicRepo = ...;           // Just CRUD
SearchableRepository<Deal> searchRepo = ...; // CRUD + Search
```

### Answer 3.5: Dependency Inversion Principle (DIP)

**Definition**:
1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
2. Abstractions should not depend on details. Details should depend on abstractions.

**How servlets depend on abstractions:**

```java
// HIGH-LEVEL MODULE
public class DealServlet extends BaseServlet {
    private final Repository<Deal> repository;  // Depends on ABSTRACTION

    public DealServlet(Repository<Deal> dealRepository) {
        this.repository = dealRepository;  // Not InMemoryRepository!
    }
}

// ABSTRACTION
interface Repository<T> { ... }

// LOW-LEVEL MODULE
public class InMemoryRepository<T> implements Repository<T> { ... }
```

**Dependency diagram:**

```
┌─────────────┐
│ DealServlet │ (High-level module)
└──────┬──────┘
       │ depends on
       ▼
┌─────────────────┐
│ Repository<T>   │ (Abstraction/Interface)
└─────────────────┘
       ▲ implements
       │
┌──────┴────────────────┐
│ InMemoryRepository<T> │ (Low-level module)
└───────────────────────┘
```

**Key point**: Arrow points UP (dependency inversion)
- Traditional: High-level depends on low-level (downward arrow)
- DIP: Both depend on abstraction (arrows point to interface)

**What if DealServlet directly instantiated InMemoryRepository:**

```java
// VIOLATION of DIP
public class DealServlet extends BaseServlet {
    private final InMemoryRepository<Deal> repository;

    public DealServlet() {
        this.repository = new InMemoryRepository<>("DEAL-", ...);
    }
}
```

**Problems:**
1. **Tight coupling**: Can't use different repository implementation
2. **Hard to test**: Can't inject mock repository
3. **Violates DIP**: High-level module depends on low-level details
4. **Inflexible**: Changing to database requires modifying DealServlet

**Benefits of DIP in our code:**
```java
// Can swap implementations without touching DealServlet
ApiServer server = new ApiServer();

// Option 1: In-memory
Repository<Deal> repo = new InMemoryRepository<>(...);

// Option 2: Database (when implemented)
Repository<Deal> repo = new DatabaseRepository<>(...);

// Option 3: Mock for testing
Repository<Deal> repo = new MockRepository<>(...);

// DealServlet doesn't care which!
DealServlet servlet = new DealServlet(repo);
```

## Section 4: Thread Safety and Concurrency

### Answer 4.1: ConcurrentHashMap

**Why necessary in web server environment:**

Web servers handle **multiple requests simultaneously**. Multiple threads may access the repository at the same time.

**Problems with regular HashMap:**

Scenario: Two requests try to save deals simultaneously

```java
// Thread 1                      // Thread 2
map.put("DEAL-001", deal1);
                                 map.put("DEAL-002", deal2);
// HashMap internal state corrupted!
```

**Specific problems:**
1. **Race conditions**: Lost updates
2. **Inconsistent state**: Iterator may throw `ConcurrentModificationException`
3. **Infinite loops**: HashMap resize during concurrent modification
4. **Data corruption**: Broken hash table structure

**What ConcurrentHashMap provides:**

From `InMemoryRepository.java:25`:
```java
private final Map<String, T> storage = new ConcurrentHashMap<>();
```

**Guarantees:**
1. **Thread-safe reads**: Multiple threads can read without blocking
2. **Thread-safe writes**: Write operations are atomic
3. **Lock-free reads**: Better performance than synchronized HashMap
4. **Consistent iteration**: No `ConcurrentModificationException`
5. **Fine-grained locking**: Locks segments, not entire map

**Example safe concurrent access:**
```java
// Thread 1: POST /api/v1/deals (saving)
repository.save(deal1);  // Thread-safe

// Thread 2: GET /api/v1/deals (reading) - executes safely at same time
repository.findAll();    // Thread-safe

// Thread 3: DELETE /api/v1/deals/DEAL-001 (deleting)
repository.deleteById("DEAL-001");  // Thread-safe
```

### Answer 4.2: AtomicLong

**Why not regular `long` or `Long`:**

From `InMemoryRepository.java:28`:
```java
private final AtomicLong idCounter = new AtomicLong(1);

public String generateId() {
    return idPrefix + String.format("%03d", idCounter.getAndIncrement());
}
```

**Race condition without atomic operations:**

```java
// NON-ATOMIC (broken)
private long idCounter = 1;

public String generateId() {
    return idPrefix + String.format("%03d", idCounter++);  // RACE CONDITION!
}
```

**The problem (idCounter++):**
```
1. Read idCounter (value = 5)
2. Add 1 to value (value = 6)
3. Write back to idCounter (6)
```

**Race condition scenario:**
```
Thread 1: Reads 5
Thread 2: Reads 5  (before Thread 1 writes back)
Thread 1: Increments to 6, writes 6
Thread 2: Increments to 6, writes 6  (DUPLICATE!)
```

**Result**: Two deals get the same ID "DEAL-006"!

**How AtomicLong solves this:**

```java
idCounter.getAndIncrement()  // ATOMIC operation
```

This is a **single, uninterruptible operation**:
- Read current value
- Increment
- Return old value
- All happens atomically (no thread can interrupt)

**Alternative approaches:**

1. **Synchronized block**:
```java
private long idCounter = 1;

public synchronized String generateId() {
    return idPrefix + String.format("%03d", idCounter++);
}
```
- Works but slower (locks entire method)
- AtomicLong uses lock-free algorithms (faster)

2. **UUID**:
```java
public String generateId() {
    return idPrefix + UUID.randomUUID().toString();
}
```
- No synchronization needed
- IDs are much longer
- Not sequential

3. **Database sequence**:
```java
public String generateId() {
    return idPrefix + database.getNextSequence("deal_id");
}
```
- Database ensures uniqueness
- Requires database call

### Answer 4.3: Thread Safety in Servlets

**Are servlets thread-safe by default?**

**No!** Servlet instances are **shared across all requests**.

From Servlet specification:
- Container creates **one instance** of each servlet
- Multiple request threads call methods on the **same instance**
- Servlets must be thread-safe

**Implications for instance variables:**

❌ **Dangerous (thread-unsafe):**
```java
public class DealServlet extends BaseServlet {
    private Deal currentDeal;  // SHARED across all requests!

    protected void doGet(...) {
        String id = extractResourceId(request);
        currentDeal = repository.findById(id).orElse(null);  // RACE CONDITION!
        // Another thread might change currentDeal here!
        sendJsonResponse(response, currentDeal);
    }
}
```

**Problem:**
- Thread 1 sets `currentDeal = dealA`
- Thread 2 sets `currentDeal = dealB` (overwrites!)
- Thread 1 sends response with dealB (wrong deal!)

✅ **Safe:**
```java
public class DealServlet extends BaseServlet {
    private final Repository<Deal> repository;  // Immutable, thread-safe

    protected void doGet(...) {
        // Local variable - each thread has its own
        Optional<Deal> deal = repository.findById(id);
        sendJsonResponse(response, deal.orElse(null));
    }
}
```

**Thread-safe patterns in our code:**

1. **Immutable fields** (`final`):
```java
private final Repository<Deal> dealRepository;  // Set once, never changes
```

2. **Local variables**:
```java
protected void doPost(...) {
    String requestBody = readRequestBody(request);  // Local to this thread
    Deal deal = JsonHelper.fromJson(requestBody, Deal.class);  // Local
}
```

3. **Thread-safe dependencies**:
```java
private final Repository<Deal> repository;  // Repository is thread-safe
```

**Key rules:**
- Instance variables must be `final` or thread-safe
- Use local variables for request-specific data
- Never store request/response in instance variables

## Section 5: JSON Serialization

### Answer 5.1: Custom Type Adapters

**Why custom adapters are necessary:**

Java's `LocalDate` and `LocalDateTime` are not standard JSON types. Gson doesn't know how to serialize them by default.

**From `JsonHelper.java:29`:**
```java
.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
    (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
```

**What format dates are serialized to:**

- `LocalDate` → `"2024-01-15"` (ISO-8601: yyyy-MM-dd)
- `LocalDateTime` → `"2024-01-15T10:30:00"` (ISO-8601: yyyy-MM-ddTHH:mm:ss)

**Example:**
```java
Deal deal = new Deal();
deal.setCloseDate(LocalDate.of(2024, 1, 15));

String json = JsonHelper.toJson(deal);
// {
//   "closeDate": "2024-01-15",  // Clean, standardized format
//   ...
// }
```

**What happens without custom adapters:**

**Option 1: Default Gson behavior (error)**:
```
com.google.gson.JsonIOException:
Failed to serialize java.time.LocalDate
```

**Option 2: Gson with setLenient() (ugly output)**:
```json
{
  "closeDate": {
    "year": 2024,
    "month": 1,
    "day": 15
  }
}
```

**Why ISO-8601 format:**
- International standard
- Unambiguous (no MM/DD vs DD/MM confusion)
- Sortable as strings
- Widely supported by JSON parsers
- Human-readable

### Answer 5.2: Serialization Configuration

**From `JsonHelper.java:26-27`:**
```java
.setPrettyPrinting()  // Makes JSON output human-readable
.serializeNulls()     // Include null fields in JSON output
```

**`setPrettyPrinting()` effect:**

**Without pretty printing:**
```json
{"id":"DEAL-001","title":"Enterprise License","value":100000.00,"status":"WON","salesRepId":"USER-001","products":[],"closeDate":"2024-01-15"}
```

**With pretty printing:**
```json
{
  "id": "DEAL-001",
  "title": "Enterprise License",
  "value": 100000.00,
  "status": "WON",
  "salesRepId": "USER-001",
  "products": [],
  "closeDate": "2024-01-15"
}
```

**Tradeoffs in production:**

**Advantages:**
- Debugging: Easier to read logs
- Development: Better developer experience
- Documentation: Clearer API examples

**Disadvantages:**
- Bandwidth: Larger response size (~30-40% bigger)
- Parsing: Slightly slower to parse
- Cache: More memory for cached responses

**Best practice:**
- Development: Pretty print enabled
- Production: Disabled, but offer `?pretty=true` query parameter

**`serializeNulls()` effect:**

**Without serializeNulls (default):**
```json
{
  "id": "DEAL-001",
  "title": "New Deal"
  // closeDate omitted because it's null
}
```

**With serializeNulls:**
```json
{
  "id": "DEAL-001",
  "title": "New Deal",
  "closeDate": null  // Explicitly null
}
```

**When to exclude nulls:**
- Reduce response size
- Client handles missing fields as null
- Optional fields with defaults

**When to include nulls:**
- Distinguish between "not set" and "missing field"
- Client expects all fields
- API documentation clarity

## Section 6: Servlet Architecture

### Answer 6.1: Servlet Lifecycle

**What happens when ApiServer starts:**

From `ApiServer.java:115-121`:
```java
// 1. Create servlet instance
DealServlet dealServlet = new DealServlet(dealRepository);

// 2. Register with Tomcat
Tomcat.addServlet(context, "DealServlet", dealServlet);

// 3. Map URL pattern
context.addServletMappingDecoded("/api/v1/deals/*", "DealServlet");

// 4. Tomcat calls servlet.init() (inherited from HttpServlet)
//    Servlet is now ready to handle requests
```

**How many instances of DealServlet exist:**

**Exactly ONE** instance for the entire application lifecycle.

```
Application Start:
    → new DealServlet(repo)  // Created once

Request 1: GET /api/v1/deals
    → dealServlet.doGet(request, response)  // Uses same instance

Request 2: POST /api/v1/deals
    → dealServlet.doPost(request, response)  // Uses same instance

Request 3 (concurrent): GET /api/v1/deals/DEAL-001
    → dealServlet.doGet(request, response)  // Same instance, different thread
```

**When are doGet/doPost/doPut/doDelete called:**

Tomcat's request handling:
```java
// Pseudocode of what Tomcat does
void handleRequest(HttpServletRequest request, HttpServletResponse response) {
    String method = request.getMethod();

    switch (method) {
        case "GET":    servlet.doGet(request, response); break;
        case "POST":   servlet.doPost(request, response); break;
        case "PUT":    servlet.doPut(request, response); break;
        case "DELETE": servlet.doDelete(request, response); break;
    }
}
```

**Full lifecycle:**
```
1. Application Start
   → new DealServlet(repo)
   → Tomcat.addServlet()
   → servlet.init()  (called by Tomcat)

2. Request Processing (many times)
   → HTTP GET → servlet.doGet()
   → HTTP POST → servlet.doPost()
   → HTTP PUT → servlet.doPut()
   → HTTP DELETE → servlet.doDelete()

3. Application Shutdown
   → servlet.destroy()  (called by Tomcat)
   → Servlet instance eligible for garbage collection
```

### Answer 6.2: URL Mapping

**What does `/*` wildcard mean:**

From `ApiServer.java:121`:
```java
context.addServletMappingDecoded("/api/v1/deals/*", "DealServlet");
```

**Breakdown:**
- `/api/v1/deals` - exact prefix match
- `/*` - match anything after

**URLs that match:**
```
✅ /api/v1/deals
✅ /api/v1/deals/
✅ /api/v1/deals/DEAL-001
✅ /api/v1/deals/DEAL-001/products
✅ /api/v1/deals/anything/here
```

**URLs that DON'T match:**
```
❌ /api/v1/deal (no 's')
❌ /api/v1/deals-list (different word)
❌ /api/v1/users
❌ /api/v2/deals (different version)
```

**How `extractResourceId()` parses ID:**

From `BaseServlet.java:84-96`:
```java
protected String extractResourceId(HttpServletRequest request) {
    String pathInfo = request.getPathInfo();
    if (pathInfo == null || pathInfo.equals("/")) {
        return null;  // No ID in URL
    }

    // Remove leading slash and extract ID
    String[] parts = pathInfo.substring(1).split("/");
    return parts.length > 0 ? parts[0] : null;
}
```

**Example:**

| Full URL | Servlet Path | Path Info | Extracted ID |
|----------|--------------|-----------|--------------|
| `/api/v1/deals` | `/api/v1/deals` | `null` | `null` |
| `/api/v1/deals/` | `/api/v1/deals` | `/` | `null` |
| `/api/v1/deals/DEAL-001` | `/api/v1/deals` | `/DEAL-001` | `DEAL-001` |
| `/api/v1/deals/USER-123/x` | `/api/v1/deals` | `/USER-123/x` | `USER-123` |

**Parsing steps for `/api/v1/deals/DEAL-001`:**
```java
pathInfo = "/DEAL-001"
pathInfo.substring(1) = "DEAL-001"  // Remove leading /
parts = ["DEAL-001"]                 // Split by /
parts[0] = "DEAL-001"                // First element
```

### Answer 6.3: Request/Response Handling

**Purpose of setting `Content-Type: application/json`:**

From `BaseServlet.java:56-58`:
```java
response.setContentType("application/json");
response.setCharacterEncoding("UTF-8");
```

**Why it matters:**

1. **Client knows how to parse response**:
```http
HTTP/1.1 200 OK
Content-Type: application/json

{"id": "DEAL-001", "title": "..."}
```
Browser/client sees `application/json` and knows to parse as JSON, not HTML or plain text.

2. **Character encoding**:
- `UTF-8` ensures international characters display correctly
- Without it, "café" might become "cafÃ©"

3. **Browser behavior**:
- `text/html` → Browser renders as web page
- `application/json` → Browser shows raw JSON or offers download
- `application/octet-stream` → Browser prompts to download file

**Why call `response.getWriter().flush()`:**

From `BaseServlet.java:61-62`:
```java
PrintWriter out = response.getWriter();
out.print(JsonHelper.toJson(object));
out.flush();  // Force data to be sent immediately
```

**What `flush()` does:**
- Empties buffer, sends data over network immediately
- Without it, data might stay in memory buffer
- Ensures client receives response promptly

**When it matters:**
```java
// Without flush (buffered)
out.print(json);  // Stays in memory buffer
// ... method ends, servlet finishes
// Tomcat eventually sends buffered data

// With flush (immediate)
out.print(json);
out.flush();  // Sends immediately
```

For small responses, doesn't matter much (Tomcat auto-flushes). For streaming or large responses, very important.

**What happens without setting response status code:**

```java
// Missing: response.setStatus(HttpServletResponse.SC_OK);
sendJsonResponse(response, deal);
```

**Default behavior:**
- If you don't explicitly set status, Tomcat defaults to **200 OK**
- Usually works, but not explicit
- Better to be explicit:

```java
// Clear intent
sendJsonResponse(response, deal, HttpServletResponse.SC_OK);  // Explicit 200
sendJsonResponse(response, deal, HttpServletResponse.SC_CREATED);  // Explicit 201
```

**Why explicit is better:**
1. Code is self-documenting
2. No assumptions about defaults
3. Prevents accidental wrong status codes
4. Easier to review and understand

## Section 7: Error Handling

### Answer 7.1: HTTP Status Codes for Errors

**When to return 400 vs 500:**

**400 Bad Request (Client error):**
- Client sent invalid data
- It's the **client's fault**
- Client should fix request and retry

**500 Internal Server Error (Server error):**
- Server encountered unexpected condition
- It's the **server's fault**
- Client can't fix it by changing request

**Examples from our code:**

**400 Bad Request** (`DealServlet.java:82-86`):
```java
try {
    String requestBody = readRequestBody(request);
    Deal deal = JsonHelper.fromJson(requestBody, Deal.class);
    // ...
} catch (Exception e) {
    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
            "Invalid deal data: " + e.getMessage());
}
```

**Why 400:**
- JSON parsing failed → Client sent malformed JSON
- Client can fix by sending valid JSON
- Retrying same request will fail again

**What should be 500:**

```java
try {
    Deal savedDeal = dealRepository.save(deal);
    sendJsonResponse(response, savedDeal, 201);
} catch (OutOfMemoryError e) {
    // Server problem, not client's fault
    sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Server error: " + e.getMessage());
}
```

**Full error code guide:**

| Code | Name | When to Use |
|------|------|-------------|
| 400 | Bad Request | Invalid JSON, missing required fields, validation failures |
| 401 | Unauthorized | Missing or invalid authentication token |
| 403 | Forbidden | Authenticated but not authorized for this resource |
| 404 | Not Found | Resource ID doesn't exist |
| 409 | Conflict | Resource already exists (duplicate creation) |
| 422 | Unprocessable Entity | Valid JSON but semantically invalid (e.g., negative price) |
| 500 | Internal Server Error | Database down, OutOfMemoryError, bugs |
| 503 | Service Unavailable | Temporarily down for maintenance |

### Answer 7.2: Error Response Format

**Why structured errors instead of plain text:**

❌ **Plain text (bad):**
```http
HTTP/1.1 404 Not Found

Deal not found: DEAL-999
```

Client gets:
- String "Deal not found: DEAL-999"
- Must parse string to extract info
- No machine-readable structure

✅ **Structured JSON (good):**

From `BaseServlet.java:75-95`:
```java
private static class ErrorResponse {
    private final int status;
    private final String message;
    private final long timestamp;
}
```

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "message": "Deal not found: DEAL-999",
  "timestamp": 1705334400000
}
```

**Benefits:**

1. **Machine-readable**:
```javascript
// Client can easily access fields
if (response.status === 404) {
    console.log(`Error: ${response.message}`);
    console.log(`Occurred at: ${new Date(response.timestamp)}`);
}
```

2. **Consistent format**:
All errors have same structure, simplifies client error handling

3. **Extensible**:
```java
class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private String errorCode;  // Add custom error codes
    private List<FieldError> fieldErrors;  // Validation errors
    private String requestId;  // For debugging
}
```

**What information error responses should include:**

**Minimum:**
- `status`: HTTP status code (400, 404, etc.)
- `message`: Human-readable error description

**Recommended:**
- `timestamp`: When error occurred
- `path`: Which endpoint failed (`/api/v1/deals/DEAL-999`)
- `method`: HTTP method (`GET`, `POST`, etc.)

**Advanced:**
- `errorCode`: Machine-readable code (`DEAL_NOT_FOUND`, `INVALID_JSON`)
- `requestId`: Unique ID for this request (for support/debugging)
- `fields`: For validation errors, which fields failed

**Example production-ready error:**
```json
{
  "status": 400,
  "errorCode": "VALIDATION_ERROR",
  "message": "Invalid deal data",
  "timestamp": 1705334400000,
  "path": "/api/v1/deals",
  "method": "POST",
  "requestId": "abc123",
  "errors": [
    {
      "field": "value",
      "message": "Value must be positive",
      "rejectedValue": -100
    },
    {
      "field": "salesRepId",
      "message": "Sales rep ID is required",
      "rejectedValue": null
    }
  ]
}
```

**How this helps API clients:**
- Clear error identification
- Actionable information (which fields to fix)
- Debugging support (requestId to report to support)
- Consistent error handling logic

## Section 8: Generic Programming

### Answer 8.1: Generic Repository

**Benefits of making Repository generic:**

**Without generics (repetitive):**
```java
interface DealRepository {
    List<Deal> findAll();
    Optional<Deal> findById(String id);
    Deal save(Deal entity);
    boolean deleteById(String id);
    String generateId();
}

interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    User save(User entity);
    boolean deleteById(String id);
    String generateId();
}

// Same for CommissionPlan, Dispute, etc.
// Must also implement InMemoryDealRepository, InMemoryUserRepository, etc.
```

**With generics (DRY):**
```java
interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(String id);
    T save(T entity);
    boolean deleteById(String id);
    String generateId();
}

// Single implementation works for all types
class InMemoryRepository<T> implements Repository<T> { ... }

// Use with any entity type
Repository<Deal> dealRepo = new InMemoryRepository<>(...);
Repository<User> userRepo = new InMemoryRepository<>(...);
```

**Benefits:**

1. **Code reuse**: Write once, use for all entity types
2. **Type safety**: Compiler prevents `userRepo.save(deal)`
3. **Maintainability**: Bug fixes apply to all types
4. **Consistency**: All repositories behave identically

**How type safety works:**

```java
Repository<Deal> dealRepo = new InMemoryRepository<>(...);
Repository<User> userRepo = new InMemoryRepository<>(...);

Deal deal = new Deal();
User user = new User();

dealRepo.save(deal);  // ✅ Compiles
dealRepo.save(user);  // ❌ Compilation error: incompatible types

List<Deal> deals = dealRepo.findAll();  // ✅ Returns List<Deal>
List<User> users = dealRepo.findAll();  // ❌ Compilation error
```

**Could you create `Repository<String>`?**

**Technically yes:**
```java
Repository<String> stringRepo = new InMemoryRepository<>(
    "STR-",
    s -> s,  // ID extractor: String itself is the ID
    (s, id) -> { }  // ID setter: can't set, String is immutable
);

stringRepo.save("hello");
stringRepo.save("world");
stringRepo.findAll();  // ["hello", "world"]
```

**Would it make sense?**

**Not really:**
- Repository is designed for entities with IDs
- Strings don't have separate ID field
- No business logic for String entities
- Better to just use `Set<String>` or `List<String>`

**Repository makes sense for:**
- Domain objects (Deal, User, etc.)
- Objects with unique identifiers
- Objects with business logic
- Objects that might be persisted to database

### Answer 8.2: Functional Interfaces

**What are these functional interfaces used for:**

From `InMemoryRepository.java:30-38`:
```java
private final Function<T, String> idExtractor;
private final BiConsumer<T, String> idSetter;

public InMemoryRepository(String idPrefix,
                          Function<T, String> idExtractor,
                          BiConsumer<T, String> idSetter) {
    this.idExtractor = idExtractor;
    this.idSetter = idSetter;
}
```

**`Function<T, String> idExtractor`:**
- Takes entity of type `T`
- Returns its ID as `String`
- Used to get ID from entity

**`BiConsumer<T, String> idSetter`:**
- Takes entity of type `T` and String ID
- Sets the ID on the entity
- Returns nothing (void)

**How they're used** (`InMemoryRepository.java:51-59`):
```java
public T save(T entity) {
    String id = idExtractor.apply(entity);  // Get current ID

    if (id == null || id.empty()) {
        id = generateId();
        idSetter.accept(entity, id);  // Set new ID
    }

    storage.put(id, entity);
    return entity;
}
```

**Example usage** (`ApiServer.java:61-65`):
```java
this.dealRepository = new InMemoryRepository<>(
    "DEAL-",              // ID prefix
    Deal::getId,          // ID extractor (method reference)
    Deal::setId           // ID setter (method reference)
);
```

**Expanded form (lambda):**
```java
this.dealRepository = new InMemoryRepository<>(
    "DEAL-",
    (Deal deal) -> deal.getId(),           // Extractor
    (Deal deal, String id) -> deal.setId(id)  // Setter
);
```

**Why pass functions as parameters instead of using reflection:**

**With reflection (slower, fragile):**
```java
public class InMemoryRepository<T> {
    public T save(T entity) {
        // Get ID using reflection
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        String id = (String) idField.get(entity);

        // Set ID using reflection
        if (id == null) {
            id = generateId();
            idField.set(entity, id);
        }
    }
}
```

**Problems with reflection:**
1. **Performance**: Much slower than direct method calls
2. **Compilation safety**: Errors only at runtime
3. **Fragile**: Breaks if field name changes
4. **Security**: Requires breaking encapsulation (setAccessible)
5. **Complexity**: Harder to read and maintain

**With functional interfaces (fast, type-safe):**
```java
public class InMemoryRepository<T> {
    private final Function<T, String> idExtractor;

    public T save(T entity) {
        String id = idExtractor.apply(entity);  // Direct method call
        // Fast, type-safe, clean
    }
}
```

**How this supports different entity types:**

```java
// Deal: ID is "id" field accessed via getId()/setId()
Repository<Deal> dealRepo = new InMemoryRepository<>(
    "DEAL-",
    Deal::getId,
    Deal::setId
);

// User: Also uses getId()/setId()
Repository<User> userRepo = new InMemoryRepository<>(
    "USER-",
    User::getId,
    User::setId
);

// Hypothetical Product with different ID accessor
Repository<Product> productRepo = new InMemoryRepository<>(
    "PROD-",
    Product::getProductCode,  // Different getter
    Product::setProductCode   // Different setter
);
```

**Key advantage**: Repository doesn't need to know field names or use reflection. It just calls the provided functions.

## Section 9: API Design Best Practices

### Answer 9.1: Filtering and Query Parameters

**Why use query parameters for filtering:**

✅ **Query parameters (correct):**
```
GET /api/v1/deals?status=WON&salesRepId=USER-001
```

❌ **Path parameters (wrong for filtering):**
```
GET /api/v1/deals/status/WON/salesRepId/USER-001
```

**Reasons:**

1. **Optional filtering**: Can omit parameters
```
GET /api/v1/deals              # All deals
GET /api/v1/deals?status=WON   # Only won deals
```

With path parameters, you'd need multiple endpoints:
```
GET /api/v1/deals
GET /api/v1/deals/by-status/{status}
GET /api/v1/deals/by-sales-rep/{repId}
GET /api/v1/deals/by-status-and-rep/{status}/{repId}
```

2. **Multiple values**: Easy to support
```
GET /api/v1/deals?status=WON&status=OPEN  # Multiple statuses
```

3. **RESTful semantics**: Path identifies resource, query modifies retrieval
```
/api/v1/deals  ← Resource (noun)
?status=WON    ← Filter (adjective)
```

4. **Caching**: Query parameters handled well by HTTP caches

**Should filtering be in servlet or repository layer:**

**Current implementation (servlet layer)** (`DealServlet.java:162-177`):
```java
private void handleGetAllDeals(HttpServletRequest request, HttpServletResponse response) {
    List<Deal> deals = dealRepository.findAll();  // Get all

    // Filter in servlet
    String statusParam = request.getParameter("status");
    if (statusParam != null) {
        deals = deals.stream()
                .filter(deal -> deal.getStatus() == status)
                .collect(Collectors.toList());
    }
}
```

**Alternative (repository layer):**
```java
interface Repository<T> {
    List<T> findAll();
    List<T> findByStatus(DealStatus status);  // Specific query
}

// In servlet
List<Deal> deals = statusParam != null
    ? repository.findByStatus(status)
    : repository.findAll();
```

**Which is better?**

**In-memory (current): Servlet filtering is fine**
- Simple implementation
- All data already in memory
- Performance difference negligible

**Database-backed: Repository filtering is better**
```java
// Repository translates to SQL
List<Deal> findByStatus(DealStatus status);

// SQL: SELECT * FROM deals WHERE status = ?
// Only retrieves matching records, not all records
```

**Best practice for production:**
- **Simple filters**: Repository layer (database does filtering)
- **Complex business logic**: Service layer between servlet and repository
- **Dynamic filters**: Use specification or criteria pattern

**How to add pagination support:**

**Query parameters:**
```
GET /api/v1/deals?page=2&size=20&sort=closeDate,desc
```

**Repository interface:**
```java
interface Repository<T> {
    Page<T> findAll(Pageable pageable);
}

class Page<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

class Pageable {
    private int pageNumber;
    private int pageSize;
    private Sort sort;
}
```

**Servlet implementation:**
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    int page = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "0");
    int size = Integer.parseInt(request.getParameter("size") != null ? request.getParameter("size") : "20");

    Pageable pageable = new Pageable(page, size);
    Page<Deal> dealPage = repository.findAll(pageable);

    sendJsonResponse(response, dealPage);
}
```

**Response:**
```json
{
  "content": [ /* deals */ ],
  "pageNumber": 2,
  "pageSize": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": false,
  "last": false
}
```

### Answer 9.2: API Versioning

**Why include version in URL:**

From our implementation: `/api/v1/deals`

**Reasons:**

1. **Breaking changes**: Can introduce v2 without breaking v1 clients
2. **Gradual migration**: Clients upgrade at their own pace
3. **Compatibility**: Support multiple versions simultaneously
4. **Clear expectations**: Clients know what to expect from v1

**What happens when you need breaking changes:**

**Scenario**: Need to change Deal JSON structure

**v1 (current):**
```json
{
  "id": "DEAL-001",
  "value": 100000,
  "salesRepId": "USER-001"
}
```

**v2 (new):**
```json
{
  "id": "DEAL-001",
  "totalValue": 100000,  // Renamed from "value"
  "salesRepresentative": {  // Nested object instead of just ID
    "id": "USER-001",
    "name": "John Smith"
  }
}
```

**Implementation:**
```java
// v1 servlet (unchanged)
context.addServletMappingDecoded("/api/v1/deals/*", "DealServletV1");

// v2 servlet (new)
context.addServletMappingDecoded("/api/v2/deals/*", "DealServletV2");
```

**Both versions run simultaneously:**
- Old clients use `/api/v1/deals` (still works)
- New clients use `/api/v2/deals` (new features)
- Eventually deprecate v1: "v1 will be removed on 2025-12-31"

**Alternative versioning strategies:**

**1. Query parameter:**
```
GET /api/deals?version=1
GET /api/deals?version=2
```
**Pros**: Clean URLs
**Cons**: Easy to forget, caching issues

**2. Header:**
```
GET /api/deals
Accept: application/vnd.company.v1+json

GET /api/deals
Accept: application/vnd.company.v2+json
```
**Pros**: More RESTful (content negotiation)
**Cons**: Less visible, harder to test

**3. Subdomain:**
```
GET https://api-v1.company.com/deals
GET https://api-v2.company.com/deals
```
**Pros**: Complete isolation
**Cons**: More complex infrastructure

**4. URL path (our choice):**
```
GET /api/v1/deals
GET /api/v2/deals
```
**Pros**: Simple, visible, easy to route
**Cons**: Longer URLs

**Best practice**: URL path versioning for public APIs (what we use).

### Answer 9.3: Idempotency

**Definition**: An operation is idempotent if calling it multiple times has the same effect as calling it once.

**Which HTTP methods should be idempotent:**

| Method | Idempotent? | Why |
|--------|-------------|-----|
| GET | ✅ Yes | Reading doesn't change state |
| PUT | ✅ Yes | Replacing resource with same data = same result |
| DELETE | ✅ Yes | Deleting already-deleted resource = same result |
| POST | ❌ No | Creating same resource twice = two resources |
| PATCH | 🤔 Maybe | Depends on implementation |

**Is our PUT implementation idempotent:**

From `DealServlet.java:98-135`:
```java
protected void doPut(HttpServletRequest request, HttpServletResponse response) {
    // Update existing deal
    Deal deal = JsonHelper.fromJson(requestBody, Deal.class);
    deal.setId(dealId);  // Ensure ID matches
    Deal updatedDeal = dealRepository.save(deal);
}
```

**Yes, it's idempotent:**

```
Initial state:
{ "id": "DEAL-001", "title": "Old Title", "value": 1000 }

PUT /api/v1/deals/DEAL-001
{ "id": "DEAL-001", "title": "New Title", "value": 2000 }

Result:
{ "id": "DEAL-001", "title": "New Title", "value": 2000 }

PUT again with same data:
{ "id": "DEAL-001", "title": "New Title", "value": 2000 }

Result:
{ "id": "DEAL-001", "title": "New Title", "value": 2000 }
// Same result - idempotent!
```

**Why idempotency matters:**

**1. Network reliability:**
```
Client sends: PUT /api/v1/deals/DEAL-001
Network timeout: Did it reach server?
Client retries: PUT /api/v1/deals/DEAL-001 (same request)
Result: Safe! Idempotent operation won't cause problems
```

**2. Safety for retries:**
```javascript
// Client code can safely retry
function updateDeal(deal) {
    let retries = 3;
    while (retries > 0) {
        try {
            return await fetch(`/api/v1/deals/${deal.id}`, {
                method: 'PUT',
                body: JSON.stringify(deal)
            });
        } catch (error) {
            retries--;
            if (retries === 0) throw error;
        }
    }
}
```

**3. Non-idempotent POST example:**

```
POST /api/v1/deals
{ "title": "New Deal", "value": 1000 }

Result: Creates DEAL-001

POST again (retry):
{ "title": "New Deal", "value": 1000 }

Result: Creates DEAL-002  // Different result - NOT idempotent
```

**Making POST idempotent (idempotency key):**

```
POST /api/v1/deals
Idempotency-Key: abc123
{ "title": "New Deal", "value": 1000 }

Result: Creates DEAL-001, remembers key "abc123"

POST again (retry):
Idempotency-Key: abc123  // Same key
{ "title": "New Deal", "value": 1000 }

Result: Returns DEAL-001 (doesn't create duplicate)
```

**Implementation:**
```java
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String idempotencyKey = request.getHeader("Idempotency-Key");

    if (idempotencyKey != null) {
        Deal existingDeal = dealRepository.findByIdempotencyKey(idempotencyKey);
        if (existingDeal != null) {
            // Already created, return existing
            sendJsonResponse(response, existingDeal, 200);
            return;
        }
    }

    // Create new deal
    Deal deal = JsonHelper.fromJson(requestBody, Deal.class);
    deal.setIdempotencyKey(idempotencyKey);
    Deal savedDeal = dealRepository.save(deal);
    sendJsonResponse(response, savedDeal, 201);
}
```

## Section 10: Testing and Quality

### Answer 10.1: Unit Testing Servlets

**How to unit test `DealServlet.doGet()` without starting server:**

**What to mock:**
1. `HttpServletRequest`
2. `HttpServletResponse`
3. `Repository<Deal>`

**Example test:**

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

class DealServletTest {

    @Test
    void testDoGet_withId_returnsSpecificDeal() throws Exception {
        // Arrange - Create mocks
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Repository<Deal> mockRepo = mock(Repository.class);

        // Mock request.getPathInfo() to return "/DEAL-001"
        when(request.getPathInfo()).thenReturn("/DEAL-001");

        // Mock repository to return a deal
        Deal expectedDeal = new Deal("Test Deal", new BigDecimal(1000), "USER-001");
        expectedDeal.setId("DEAL-001");
        when(mockRepo.findById("DEAL-001")).thenReturn(Optional.of(expectedDeal));

        // Mock response writer
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act - Call servlet
        DealServlet servlet = new DealServlet(mockRepo);
        servlet.doGet(request, response);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("DEAL-001"));
        assertTrue(responseBody.contains("Test Deal"));
    }

    @Test
    void testDoGet_withInvalidId_returns404() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Repository<Deal> mockRepo = mock(Repository.class);

        when(request.getPathInfo()).thenReturn("/DEAL-999");
        when(mockRepo.findById("DEAL-999")).thenReturn(Optional.empty());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // Act
        DealServlet servlet = new DealServlet(mockRepo);
        servlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);

        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("Deal not found"));
    }

    @Test
    void testDoGet_withoutId_returnsAllDeals() throws Exception {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Repository<Deal> mockRepo = mock(Repository.class);

        when(request.getPathInfo()).thenReturn(null);

        List<Deal> deals = Arrays.asList(
            createDeal("DEAL-001", "Deal 1"),
            createDeal("DEAL-002", "Deal 2")
        );
        when(mockRepo.findAll()).thenReturn(deals);

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        // Act
        DealServlet servlet = new DealServlet(mockRepo);
        servlet.doGet(request, response);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseBody = stringWriter.toString();
        assertTrue(responseBody.contains("DEAL-001"));
        assertTrue(responseBody.contains("DEAL-002"));
    }
}
```

**Test cases to write:**

**GET tests:**
- Get all deals (empty list)
- Get all deals (with data)
- Get specific deal (found)
- Get specific deal (not found)
- Get deals with status filter
- Get deals with salesRepId filter

**POST tests:**
- Create deal (valid data)
- Create deal (invalid JSON)
- Create deal (missing required fields)

**PUT tests:**
- Update deal (exists)
- Update deal (doesn't exist)
- Update deal (invalid data)

**DELETE tests:**
- Delete deal (exists)
- Delete deal (doesn't exist)

**How repository abstraction helps:**

```java
// Without abstraction - hard to test
public class DealServlet {
    private InMemoryRepository<Deal> repository = new InMemoryRepository<>(...);
    // Can't inject mock!
}

// With abstraction - easy to test
public class DealServlet {
    private Repository<Deal> repository;  // Interface

    public DealServlet(Repository<Deal> repository) {
        this.repository = repository;  // Inject mock in tests
    }
}
```

### Answer 10.2: Integration Testing

**How to test full API end-to-end:**

**Setup:**
```java
class ApiIntegrationTest {
    private ApiServer server;
    private int port = 9999;  // Test port

    @BeforeEach
    void setup() throws Exception {
        server = new ApiServer(port);
        // Start in background thread
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Wait for server to be ready
        Thread.sleep(1000);
    }

    @AfterEach
    void teardown() throws Exception {
        server.stop();
    }

    @Test
    void testCreateAndRetrieveDeal() throws Exception {
        // Create deal
        HttpClient client = HttpClient.newHttpClient();

        String dealJson = """
            {
                "title": "Integration Test Deal",
                "value": 50000.00,
                "salesRepId": "USER-001"
            }
            """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/deals"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(dealJson))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest,
                                                        HttpResponse.BodyHandlers.ofString());

        // Verify creation
        assertEquals(201, postResponse.statusCode());

        // Extract ID from response
        String responseBody = postResponse.body();
        // Parse JSON to get ID (using Gson)
        Deal createdDeal = JsonHelper.fromJson(responseBody, Deal.class);
        String dealId = createdDeal.getId();

        // Retrieve deal
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/deals/" + dealId))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest,
                                                       HttpResponse.BodyHandlers.ofString());

        // Verify retrieval
        assertEquals(200, getResponse.statusCode());
        Deal retrievedDeal = JsonHelper.fromJson(getResponse.body(), Deal.class);
        assertEquals("Integration Test Deal", retrievedDeal.getTitle());
    }
}
```

**Tools to use:**

1. **Java 11+ HttpClient**: Built-in HTTP client
2. **REST Assured**: Fluent API for testing REST services
```java
given()
    .contentType("application/json")
    .body(dealJson)
.when()
    .post("/api/v1/deals")
.then()
    .statusCode(201)
    .body("title", equalTo("Integration Test Deal"));
```

3. **Apache HttpClient**: More features than built-in client
4. **JUnit 5**: Test framework

**How to verify correct HTTP status codes:**

```java
// Java HttpClient
assertEquals(201, response.statusCode());

// REST Assured
.then().statusCode(201)

// Manual
assertTrue(response.statusCode() >= 200 && response.statusCode() < 300);
```

**Should you test with in-memory or real database:**

**For integration tests:**

**In-memory repository (fast, isolated):**
```java
@Test
void testDealCRUD() {
    // Uses InMemoryRepository
    // Fast, no external dependencies
    // But doesn't test database interactions
}
```

**Real database (realistic, slow):**
```java
@Test
void testDealCRUDWithDatabase() {
    // Uses DatabaseRepository
    // Tests actual database operations
    // Tests SQL queries
    // But slower, requires database setup
}
```

**Best practice: Both!**

1. **Fast tests** (in-memory): Run frequently during development
2. **Slow tests** (real database): Run before commit/deployment

```java
@Tag("fast")
@Test
void testWithInMemory() { /* ... */ }

@Tag("slow")
@Test
void testWithDatabase() { /* ... */ }
```

Run:
```bash
mvn test -Dgroups="fast"        # Quick feedback
mvn test -Dgroups="slow"        # Before deployment
mvn test                        # All tests
```

### Answer 10.3: Code Coverage

**Parts hardest to test:**

**1. Embedded Tomcat setup** (`ApiServer.java`):

**Why hard:**
- Requires actual server startup
- Port conflicts
- Cleanup complexity

**How to test:**
```java
@Test
void testServerStartupAndShutdown() throws Exception {
    ApiServer server = new ApiServer(9999);

    Thread serverThread = new Thread(() -> {
        try {
            server.start();
        } catch (Exception e) { }
    });
    serverThread.start();

    Thread.sleep(1000);  // Wait for startup

    // Verify server is running
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9999/api/v1/deals"))
            .build(), HttpResponse.BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    server.stop();
}
```

**2. Error handling paths:**

**Why hard:**
- Need to trigger specific exceptions
- Some errors are rare/random

**Example:**
```java
// Hard to test: Out of memory
try {
    deal = repository.save(deal);
} catch (OutOfMemoryError e) {  // How to trigger this in test?
    sendErrorResponse(response, 500, "Server error");
}
```

**Solution: Inject failure:**
```java
class FailingRepository implements Repository<Deal> {
    @Override
    public Deal save(Deal deal) {
        throw new OutOfMemoryError("Simulated OOM");
    }
}

@Test
void testOutOfMemory() {
    Repository<Deal> failingRepo = new FailingRepository();
    DealServlet servlet = new DealServlet(failingRepo);
    // ... test error handling
}
```

**3. Date/time dependent code:**

**Why hard:**
- Behavior changes based on current time
- Tests become non-deterministic

**Example:**
```java
deal.setCreatedDate(LocalDate.now());  // Different value each day
```

**Solution: Inject clock:**
```java
class Deal {
    private Clock clock = Clock.systemDefaultZone();

    public void setClock(Clock clock) {
        this.clock = clock;  // For testing
    }

    public void setCreatedDate() {
        this.createdDate = LocalDate.now(clock);
    }
}

@Test
void testCreatedDate() {
    Clock fixedClock = Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"),
                                   ZoneId.of("UTC"));
    Deal deal = new Deal();
    deal.setClock(fixedClock);
    deal.setCreatedDate();

    assertEquals(LocalDate.of(2024, 1, 15), deal.getCreatedDate());
}
```

**4. Thread race conditions:**

**Why hard:**
- Non-deterministic
- Hard to reproduce
- May pass 99% of time, fail 1%

**Example testing thread safety:**
```java
@Test
void testConcurrentRepositoryAccess() throws Exception {
    InMemoryRepository<Deal> repo = new InMemoryRepository<>(...);

    // Create 100 threads all saving deals simultaneously
    int threadCount = 100;
    CountDownLatch latch = new CountDownLatch(threadCount);
    List<Thread> threads = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
        final int dealNum = i;
        Thread thread = new Thread(() -> {
            Deal deal = new Deal("Deal " + dealNum, new BigDecimal(1000), "USER-001");
            repo.save(deal);
            latch.countDown();
        });
        threads.add(thread);
        thread.start();
    }

    // Wait for all threads
    latch.await(10, TimeUnit.SECONDS);

    // Verify all deals saved
    assertEquals(threadCount, repo.size());
}
```

**Why difficult:**
- May need to run 1000s of times to catch race condition
- Timing-dependent
- Platform-dependent

**Code coverage goals:**

- **80%+**: Good for business logic
- **60%+**: Acceptable for API endpoints
- **Lower**: OK for boilerplate (getters/setters)
- **Focus**: Test critical paths, error handling

## Section 11: OpenAPI Specification

### Answer 11.1: OpenAPI Purpose

**What is the purpose of `openapi.yaml`:**

The OpenAPI specification (`openapi.yaml`) is a **machine-readable API contract** that describes:

1. **Endpoints**: What URLs are available
2. **Operations**: What HTTP methods each endpoint supports
3. **Request format**: What data to send
4. **Response format**: What data you'll receive
5. **Data models**: Structure of entities (Deal, User, etc.)
6. **Error responses**: What errors can occur

**Who are the consumers:**

**1. API Clients (Developers):**
```javascript
// Developer reads spec to understand how to use API
fetch('/api/v1/deals', {
    method: 'POST',
    body: JSON.stringify({
        title: "New Deal",  // From schema in openapi.yaml
        value: 100000.00,   // From schema
        salesRepId: "USER-001"  // From schema
    })
});
```

**2. Code Generators:**
Can generate client libraries automatically:
```bash
# Generate TypeScript client
openapi-generator-cli generate -i openapi.yaml -g typescript-fetch -o ./client

# Use generated client
import { DealsApi } from './client';
const api = new DealsApi();
const deal = await api.createDeal({
    title: "New Deal",
    value: 100000.00,
    salesRepId: "USER-001"
});
```

**3. Documentation Tools:**
- **Swagger UI**: Interactive API documentation
- **Redoc**: Clean, readable docs
- **Postman**: Import to create test collections

**4. Testing Tools:**
- **Dredd**: Validate API matches spec
- **Prism**: Mock server from spec

**5. API Gateways:**
- Can auto-configure based on spec

**What tools can use OpenAPI specifications:**

**Documentation:**
- **Swagger UI** - Interactive docs with "Try it out" button
- **Redoc** - Clean, printable documentation
- **Stoplight** - Collaborative API design

**Code Generation:**
- **OpenAPI Generator** - Generate clients in 50+ languages
- **Swagger Codegen** - Generate server stubs

**Testing:**
- **Dredd** - Contract testing
- **Schemathesis** - Property-based testing
- **Postman** - Import spec → test collection

**Mocking:**
- **Prism** - Mock server from spec
- **Stoplight Prism** - Realistic mock responses

**Validation:**
- **Spectral** - Lint OpenAPI specs
- **OpenAPI Validator** - Ensure correctness

**Example Swagger UI**:
```yaml
# openapi.yaml defines this endpoint
paths:
  /deals:
    post:
      summary: Create a new deal
```

Swagger UI renders:
```
POST /api/v1/deals
Create a new deal

[Try it out]

Request Body:
{
  "title": "",
  "value": 0,
  "salesRepId": ""
}

[Execute Button]
```

### Answer 11.2: Schema Definition

**How schemas help API consumers:**

From `openapi.yaml:461-502`:
```yaml
components:
  schemas:
    Deal:
      type: object
      properties:
        id:
          type: string
          example: "DEAL-001"
        title:
          type: string
          example: "Enterprise Software License"
        value:
          type: number
          format: decimal
          example: 100000.00
```

**Benefits for consumers:**

**1. Know what to send:**
```javascript
// Schema tells you exactly what fields are needed
const dealData = {
    title: "My Deal",       // string, required
    value: 50000.00,        // number, required
    salesRepId: "USER-001"  // string, required
    // id is not required (server generates it)
};
```

**2. Validation:**
```typescript
// TypeScript types generated from schema
interface Deal {
    id?: string;
    title: string;  // Required
    value: number;  // Required
    status?: 'OPEN' | 'WON' | 'LOST' | 'CANCELLED';
    salesRepId: string;  // Required
}

function createDeal(deal: Deal) {  // Type-safe!
    // Compiler prevents sending wrong types
}
```

**3. Documentation:**
```yaml
value:
  type: number
  format: decimal
  description: Total value of the deal
  example: 100000.00
```

Developers see:
- Type: Number
- Format: Decimal (not integer)
- Purpose: Total value of the deal
- Example: 100000.00

**Relationship between OpenAPI schemas and Java classes:**

**Java class** (`Deal.java`):
```java
public class Deal {
    private String id;
    private String title;
    private BigDecimal value;
    private DealStatus status;
    private String salesRepId;
    private List<DealProduct> products;
    private LocalDate closeDate;
}
```

**OpenAPI schema** (`openapi.yaml`):
```yaml
Deal:
  type: object
  properties:
    id:
      type: string
    title:
      type: string
    value:
      type: number
      format: decimal
    status:
      type: string
      enum: [OPEN, WON, LOST, CANCELLED]
    salesRepId:
      type: string
    products:
      type: array
      items:
        $ref: '#/components/schemas/DealProduct'
    closeDate:
      type: string
      format: date
```

**Mapping:**

| Java Type | OpenAPI Type |
|-----------|--------------|
| String | type: string |
| int/Integer | type: integer |
| BigDecimal | type: number, format: decimal |
| boolean/Boolean | type: boolean |
| LocalDate | type: string, format: date |
| LocalDateTime | type: string, format: date-time |
| Enum | type: string, enum: [...] |
| List<T> | type: array, items: ... |
| Object | type: object, properties: ... |

**Could you generate Java code from OpenAPI:**

**Yes!** Using OpenAPI Generator:

```bash
openapi-generator-cli generate \
  -i openapi.yaml \
  -g java \
  -o ./generated-code

# Generates:
# - Deal.java
# - User.java
# - DealProduct.java
# - API client code
```

**Generated code:**
```java
// Generated from OpenAPI schema
public class Deal {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("value")
    private BigDecimal value;

    // Getters, setters, validation
}
```

**Could you generate OpenAPI from Java:**

**Yes!** Using annotations:

```java
// Annotate Java classes
@Schema(description = "Represents a sales deal")
public class Deal {

    @Schema(description = "Unique deal identifier", example = "DEAL-001")
    private String id;

    @Schema(description = "Deal title", required = true, example = "Enterprise License")
    private String title;

    @Schema(description = "Total deal value", required = true, example = "100000.00")
    private BigDecimal value;
}
```

Tools that generate OpenAPI from Java:
- **Springdoc** (for Spring Boot)
- **Swagger annotations**
- **Jakarta Bean Validation** → OpenAPI validation rules

**Best practices:**

**Design-first (recommended):**
1. Write `openapi.yaml` first
2. Generate Java code from spec
3. Implement business logic

**Advantages:**
- API design reviewed before coding
- Consistent documentation
- Client/server generated from same source

**Code-first:**
1. Write Java classes
2. Generate OpenAPI from annotations
3. Publish documentation

**Advantages:**
- Faster initial development
- Code is source of truth

**Our implementation**: Manual (wrote both separately) - Good for learning, not ideal for production.

## Section 12: Architecture and Layering

### Answer 12.1: Separation of Concerns

**Why separation is important:**

Our architecture separates:

**1. HTTP Handling → Servlet Layer**
```java
// DealServlet - Knows about HTTP, not business logic
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String id = extractResourceId(request);  // HTTP concern
    Optional<Deal> deal = repository.findById(id);  // Delegate to repository
    sendJsonResponse(response, deal);  // HTTP concern
}
```

**2. Data Access → Repository Layer**
```java
// InMemoryRepository - Knows about storage, not HTTP
public Optional<T> findById(String id) {
    return Optional.ofNullable(storage.get(id));  // Data concern
}
```

**3. JSON Conversion → JsonHelper**
```java
// JsonHelper - Knows about JSON, not HTTP or data
public static String toJson(Object object) {
    return GSON.toJson(object);  // JSON concern
}
```

**4. Business Logic → Domain Models**
```java
// Deal - Knows about business rules, not HTTP or storage
public BigDecimal calculateTotalValue() {
    return products.stream()  // Business logic
        .map(product -> product.getPrice().multiply(...))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}
```

**What would be wrong with putting all logic in servlet:**

❌ **God Servlet Anti-pattern:**
```java
public class DealServlet extends HttpServlet {
    // Data storage
    private Map<String, Deal> deals = new HashMap<>();
    private long idCounter = 1;

    // JSON handling
    private Gson gson = new Gson();

    // Business logic
    private BigDecimal calculateCommission(Deal deal) {
        return deal.getValue().multiply(new BigDecimal("0.10"));
    }

    // HTTP handling
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // All mixed together!
        String id = request.getParameter("id");
        Deal deal = deals.get(id);  // Data access
        BigDecimal commission = calculateCommission(deal);  // Business logic
        deal.setCommission(commission);
        String json = gson.toJson(deal);  // JSON conversion
        response.getWriter().write(json);  // HTTP response
    }
}
```

**Problems:**

1. **Hard to test**: Must mock HTTP request/response for every test
2. **Not reusable**: Business logic locked in servlet
3. **Hard to change**: Changing data storage requires changing servlet
4. **Violates SRP**: Servlet has 4 responsibilities
5. **Tight coupling**: Everything depends on everything

**How layering supports maintainability:**

**Example: Switching from in-memory to database**

✅ **With layering (our code):**
```java
// 1. Create new repository implementation
class DatabaseRepository<T> implements Repository<T> {
    private EntityManager em;
    // ... database code
}

// 2. Change ApiServer initialization
Repository<Deal> dealRepo = new DatabaseRepository<>(...);  // One line!
// Servlets don't change at all!
```

**Changes:**
- Create DatabaseRepository (new file)
- Change one line in ApiServer
- Servlets untouched ✅
- JsonHelper untouched ✅
- Domain models untouched ✅

❌ **Without layering:**
```java
// Must change every servlet
public class DealServlet {
    // Change from HashMap to database in EVERY servlet
    private EntityManager em;  // Was: Map<String, Deal>

    protected void doGet(...) {
        // Change every method
        Deal deal = em.find(Deal.class, id);  // Was: deals.get(id)
        // ... multiply by 50 endpoints
    }
}

public class UserServlet {
    private EntityManager em;  // Duplicate code!
    // ... same changes
}
```

**Changes:**
- Modify DealServlet
- Modify UserServlet
- Modify CommissionPlanServlet
- Modify DisputeServlet
- High risk of bugs
- Lots of code duplication

**Layering benefits:**

| Benefit | Example |
|---------|---------|
| **Testability** | Test business logic without HTTP |
| **Reusability** | Use repository from batch jobs, not just servlets |
| **Flexibility** | Swap implementations without touching other layers |
| **Understanding** | Each layer has clear purpose |
| **Parallel development** | Different teams work on different layers |

### Answer 12.2: Scalability Considerations

**Limitations of in-memory storage:**

From our `InMemoryRepository`:

**1. Data lost on restart:**
```java
server.start();
// Create 100 deals
server.stop();
server.start();
// All deals gone!
```

**2. Not shared across instances:**
```
Instance 1 (port 8080)     Instance 2 (port 8081)
    InMemory storage           InMemory storage
    - DEAL-001                 - (empty)
    - DEAL-002

POST to Instance 2 → Creates DEAL-001 again (duplicate ID!)
```

**3. Memory limits:**
```java
// After creating 1 million deals
repository.findAll();  // Returns 1 million objects!
// OutOfMemoryError
```

**4. No ACID transactions:**
```java
// What if server crashes here?
Deal deal = repository.save(deal);
// Commission never calculated
calculateCommission(deal);
```

**5. No complex queries:**
```java
// Easy with database:
SELECT * FROM deals
WHERE value > 100000
AND closeDate BETWEEN '2024-01-01' AND '2024-12-31'
AND salesRepId IN (SELECT id FROM users WHERE territory = 'West')

// With InMemory: Load everything, filter in Java (slow!)
List<Deal> deals = repository.findAll();  // All deals!
deals.stream()
    .filter(d -> d.getValue().compareTo(new BigDecimal(100000)) > 0)
    .filter(d -> d.getCloseDate().isAfter(...))
    // ... complex filtering
```

**How to modify code to use database:**

**Step 1: Create DatabaseRepository**

```java
public class DatabaseRepository<T> implements Repository<T> {
    private EntityManager entityManager;
    private Class<T> entityClass;

    public DatabaseRepository(EntityManager em, Class<T> entityClass) {
        this.entityManager = em;
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> query = entityManager
            .getCriteriaBuilder()
            .createQuery(entityClass);
        query.select(query.from(entityClass));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<T> findById(String id) {
        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public T save(T entity) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            if (/* entity has ID */) {
                entity = entityManager.merge(entity);  // Update
            } else {
                entityManager.persist(entity);  // Insert
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    @Override
    public boolean deleteById(String id) {
        EntityTransaction tx = entityManager.getTransaction();
        try {
            tx.begin();
            T entity = entityManager.find(entityClass, id);
            if (entity != null) {
                entityManager.remove(entity);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }
}
```

**Step 2: Add JPA annotations to models**

```java
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "value", precision = 19, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DealStatus status;

    // ... rest of fields
}
```

**Step 3: Configure EntityManager in ApiServer**

```java
public class ApiServer {
    public ApiServer(int port) {
        // Set up database connection
        EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("commission-pu");
        EntityManager em = emf.createEntityManager();

        // Create database repositories
        this.dealRepository = new DatabaseRepository<>(em, Deal.class);
        this.userRepository = new DatabaseRepository<>(em, User.class);
        // ... etc

        // Rest of initialization unchanged
        configureTomcat(port);
    }
}
```

**Step 4: Add persistence.xml**

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">
    <persistence-unit name="commission-pu">
        <class>com.chapman.edu.commissions.model.Deal</class>
        <class>com.chapman.edu.commissions.model.User</class>
        <properties>
            <property name="javax.persistence.jdbc.url"
                      value="jdbc:postgresql://localhost:5432/commissions"/>
            <property name="javax.persistence.jdbc.user" value="admin"/>
            <property name="javax.persistence.jdbc.password" value="password"/>
            <property name="javax.persistence.jdbc.driver"
                      value="org.postgresql.Driver"/>
            <property name="hibernate.dialect"
                      value="org.hibernate.dialect.PostgreSQLDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

**Would you need to change servlets?**

**NO!** Servlets depend on `Repository<T>` interface:

```java
public class DealServlet extends BaseServlet {
    private final Repository<Deal> repository;  // Interface, not implementation

    public DealServlet(Repository<Deal> dealRepository) {
        this.repository = dealRepository;  // Works with any Repository!
    }

    // Methods unchanged - they only call Repository methods
    protected void doGet(...) {
        Optional<Deal> deal = repository.findById(id);  // Same code!
    }
}
```

**This is the power of Dependency Inversion Principle!**

**Summary of changes:**

| Component | Change? | Why |
|-----------|---------|-----|
| DealServlet | ❌ No | Depends on abstraction |
| UserServlet | ❌ No | Depends on abstraction |
| Repository interface | ❌ No | Abstraction stays same |
| InMemoryRepository | ❌ No | Keep for testing |
| DatabaseRepository | ✅ New | New implementation |
| ApiServer | ✅ Yes | Creates different repository |
| Domain models | ✅ Yes | Add JPA annotations |
| pom.xml | ✅ Yes | Add JPA dependencies |

### Answer 12.3: Production Readiness

**Additional features needed for production:**

**1. Security and Authentication:**

Current state: **No authentication!** Anyone can access any endpoint.

**Add JWT authentication:**
```java
public class AuthenticationFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");

        if (token == null || !isValidToken(token)) {
            ((HttpServletResponse) response).setStatus(401);
            return;
        }

        // Extract user from token, set in request
        User user = getUserFromToken(token);
        httpRequest.setAttribute("currentUser", user);

        chain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        // Verify JWT signature, expiration, etc.
    }
}
```

**Usage:**
```http
GET /api/v1/deals
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

**2. Logging and Monitoring:**

Current state: **No logging!** Can't debug production issues.

**Add request logging:**
```java
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        logger.info("Request {}: {} {} from {}",
            requestId,
            httpRequest.getMethod(),
            httpRequest.getRequestURI(),
            httpRequest.getRemoteAddr());

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = ((HttpServletResponse) response).getStatus();

            logger.info("Response {}: status={} duration={}ms",
                requestId, status, duration);
        }
    }
}
```

**Add application monitoring:**
```java
// Metrics
Metrics.counter("api.requests.total", "endpoint", "/deals", "method", "GET").increment();
Metrics.timer("api.response.time", "endpoint", "/deals").record(duration);
Metrics.gauge("repository.size", dealRepository.size());
```

Tools: **Micrometer**, **Prometheus**, **Grafana**

**3. Rate Limiting:**

Current state: **No rate limiting!** Someone could spam requests.

**Add rate limiter:**
```java
public class RateLimitFilter implements Filter {
    private Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = getClientId(httpRequest);  // IP or user ID

        RateLimiter limiter = limiters.computeIfAbsent(clientId,
            k -> RateLimiter.create(100.0));  // 100 requests per second

        if (!limiter.tryAcquire()) {
            ((HttpServletResponse) response).setStatus(429);  // Too Many Requests
            return;
        }

        chain.doFilter(request, response);
    }
}
```

**4. Caching:**

Current state: **No caching!** Every request hits repository.

**Add caching:**
```java
public class CachingRepository<T> implements Repository<T> {
    private Repository<T> delegate;
    private Cache<String, T> cache;

    public CachingRepository(Repository<T> delegate) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();
    }

    @Override
    public Optional<T> findById(String id) {
        T cached = cache.getIfPresent(id);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<T> result = delegate.findById(id);
        result.ifPresent(entity -> cache.put(id, entity));
        return result;
    }
}
```

**5. Error Tracking:**

Current state: **Errors just logged, not tracked.**

**Add error tracking:**
```java
try {
    Deal savedDeal = repository.save(deal);
    sendJsonResponse(response, savedDeal, 201);
} catch (Exception e) {
    // Send to error tracking service
    Sentry.captureException(e);

    logger.error("Failed to save deal", e);
    sendErrorResponse(response, 500, "Internal error");
}
```

Tools: **Sentry**, **Rollbar**, **Bugsnag**

**6. CORS Support:**

Current state: **No CORS headers!** Browser blocks requests from other domains.

**Add CORS filter:**
```java
public class CorsFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods",
                              "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                              "Content-Type, Authorization");

        if ("OPTIONS".equals(((HttpServletRequest) request).getMethod())) {
            httpResponse.setStatus(200);
            return;
        }

        chain.doFilter(request, response);
    }
}
```

**7. Health Check Endpoint:**

```java
public class HealthServlet extends BaseServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HealthStatus status = new HealthStatus();
        status.setStatus("UP");
        status.setDatabase(checkDatabase());
        status.setUptime(getUptime());

        sendJsonResponse(response, status);
    }
}
```

**8. API Documentation UI:**

**Serve Swagger UI:**
```java
// Register static file servlet for Swagger UI
context.addServletMappingDecoded("/docs/*", "SwaggerUIServlet");
```

**9. Input Validation:**

**Add Bean Validation:**
```java
public class Deal {
    @NotNull(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be 1-255 characters")
    private String title;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;
}

// In servlet
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<Deal>> violations = validator.validate(deal);

if (!violations.isEmpty()) {
    // Return 400 with validation errors
}
```

**10. Graceful Shutdown:**

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    logger.info("Shutting down gracefully...");

    // Stop accepting new requests
    tomcat.getConnector().pause();

    // Wait for in-flight requests to complete (max 30 seconds)
    long deadline = System.currentTimeMillis() + 30000;
    while (hasActiveRequests() && System.currentTimeMillis() < deadline) {
        Thread.sleep(100);
    }

    // Close database connections
    entityManager.close();

    // Stop Tomcat
    tomcat.stop();

    logger.info("Shutdown complete");
}));
```

**Production Readiness Checklist:**

- [x] HTTPS/TLS encryption
- [x] Authentication/Authorization
- [x] Rate limiting
- [x] Request logging
- [x] Error tracking
- [x] Metrics and monitoring
- [x] Health checks
- [x] Input validation
- [x] CORS configuration
- [x] Caching strategy
- [x] Database connection pooling
- [x] Graceful shutdown
- [x] API documentation
- [x] Load testing
- [x] Security headers
- [x] Backup and recovery plan

## Bonus Questions

### Bonus 1: RESTful Maturity Model

**Richardson Maturity Model levels:**

**Level 0: The Swamp of POX (Plain Old XML)**
- Single URL, single HTTP method (usually POST)
- Everything in request body

```
POST /api
{
  "action": "getDeals"
}

POST /api
{
  "action": "createDeal",
  "data": {...}
}
```

**Level 1: Resources**
- Multiple URLs (one per resource)
- Still using single HTTP method

```
POST /api/deals/list
POST /api/deals/create
POST /api/deals/delete
```

**Level 2: HTTP Verbs**
- Multiple URLs (resources)
- Proper HTTP methods
- Standard status codes

```
GET    /api/deals
POST   /api/deals
DELETE /api/deals/DEAL-001
```

**Our implementation is Level 2!**

**Level 3: Hypermedia (HATEOAS)**
- Everything from Level 2
- Plus: Responses include links to related resources

```json
{
  "id": "DEAL-001",
  "title": "Enterprise License",
  "value": 100000.00,
  "_links": {
    "self": { "href": "/api/v1/deals/DEAL-001" },
    "update": { "href": "/api/v1/deals/DEAL-001", "method": "PUT" },
    "delete": { "href": "/api/v1/deals/DEAL-001", "method": "DELETE" },
    "salesRep": { "href": "/api/v1/users/USER-001" },
    "products": { "href": "/api/v1/deals/DEAL-001/products" }
  }
}
```

**What we'd need for Level 3:**

```java
public class DealResource {
    private Deal deal;
    private Map<String, Link> links;

    public DealResource(Deal deal, String baseUrl) {
        this.deal = deal;
        this.links = new HashMap<>();

        // Self link
        links.put("self", new Link(baseUrl + "/deals/" + deal.getId()));

        // Related resource links
        links.put("salesRep",
                 new Link(baseUrl + "/users/" + deal.getSalesRepId()));
        links.put("products",
                 new Link(baseUrl + "/deals/" + deal.getId() + "/products"));
    }
}

// In servlet
DealResource resource = new DealResource(deal, "http://localhost:8080/api/v1");
sendJsonResponse(response, resource);
```

**Benefits of Level 3:**
- Self-documenting API
- Client discovers capabilities dynamically
- Looser coupling (client doesn't hardcode URLs)

**Challenges:**
- More complex to implement
- Larger response payloads
- Not widely adopted

### Bonus 2: Alternative Architectures

**Spring Boot vs Plain Servlets:**

**Our implementation (Plain Servlets):**
```java
public class ApiServer {
    private Tomcat tomcat = new Tomcat();

    public ApiServer() {
        // Manual configuration
        context.addServletMappingDecoded("/api/v1/deals/*", "DealServlet");
    }
}

public class DealServlet extends BaseServlet {
    private Repository<Deal> repository;

    public DealServlet(Repository<Deal> repository) {
        this.repository = repository;
    }
}
```

**Spring Boot equivalent:**
```java
@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        // Embedded Tomcat auto-configured!
    }
}

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {

    @Autowired  // DI handled by Spring
    private Repository<Deal> repository;

    @GetMapping("/{id}")  // Annotation-based routing
    public ResponseEntity<Deal> getDeal(@PathVariable String id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping  // Spring handles JSON serialization automatically
    public ResponseEntity<Deal> createDeal(@RequestBody Deal deal) {
        Deal saved = repository.save(deal);
        return ResponseEntity.status(201).body(saved);
    }
}
```

**Differences:**

| Aspect | Plain Servlets | Spring Boot |
|--------|---------------|-------------|
| Configuration | Manual | Auto-configuration |
| Routing | `addServletMappingDecoded()` | `@GetMapping`, `@PostMapping` |
| JSON | Manual (`JsonHelper`) | Automatic (`Jackson`) |
| DI | Manual constructor injection | `@Autowired` |
| Validation | Manual | `@Valid` annotation |
| Error handling | Manual | `@ExceptionHandler` |
| Testing | Mock everything | Spring test support |

**JAX-RS (Jersey) vs Servlets:**

```java
@Path("/api/v1/deals")
public class DealResource {

    @Context
    private Repository<Deal> repository;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeal(@PathParam("id") String id) {
        return repository.findById(id)
            .map(deal -> Response.ok(deal).build())
            .orElse(Response.status(404).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDeal(Deal deal) {
        Deal saved = repository.save(deal);
        return Response.status(201).entity(saved).build();
    }
}
```

**GraphQL vs REST:**

**REST (our implementation):**
```
GET /api/v1/deals/DEAL-001
{
  "id": "DEAL-001",
  "title": "...",
  "value": 100000,
  "salesRepId": "USER-001",
  "products": [...]  // Might not need this
}

GET /api/v1/users/USER-001  // Separate request for user details
```

**GraphQL equivalent:**
```graphql
query {
  deal(id: "DEAL-001") {
    id
    title
    value
    salesRep {      # Nested query
      id
      firstName
      lastName
    }
    # Products omitted - only request what you need
  }
}
```

**Single request, custom response:**
```json
{
  "data": {
    "deal": {
      "id": "DEAL-001",
      "title": "...",
      "value": 100000,
      "salesRep": {
        "id": "USER-001",
        "firstName": "John",
        "lastName": "Smith"
      }
    }
  }
}
```

**GraphQL benefits:**
- Single endpoint
- Client specifies exactly what data it needs
- Nested queries in one request
- No over-fetching

**REST benefits:**
- Simpler to implement
- Better caching (HTTP caching)
- Easier to understand
- Widely supported

### Bonus 3: Microservices

**How to split into microservices:**

**Current monolith (single server):**
```
ApiServer
├── DealServlet
├── UserServlet
├── CommissionPlanServlet
└── DisputeServlet
```

**Microservices approach:**

**1. Deal Service** (port 8081)
```
deal-service/
├── DealServlet
├── DealProductServlet
└── DealRepository
```

**2. User Service** (port 8082)
```
user-service/
├── UserServlet
├── UserRepository
└── AuthenticationService
```

**3. Commission Service** (port 8083)
```
commission-service/
├── CommissionPlanServlet
├── CommissionCalculationService
└── PlanRepository
```

**4. Dispute Service** (port 8084)
```
dispute-service/
├── DisputeServlet
├── DisputeRepository
└── DisputeWorkflowService
```

**Challenges this introduces:**

**1. Service communication:**

```java
// DealServlet needs user info
public class DealServlet {
    protected void doGet(...) {
        Deal deal = dealRepository.findById(id);

        // Must call User Service over HTTP!
        User salesRep = userServiceClient.getUserById(deal.getSalesRepId());

        DealResponse response = new DealResponse(deal, salesRep);
        sendJsonResponse(response, response);
    }
}

// Requires HTTP client
public class UserServiceClient {
    public User getUserById(String userId) {
        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI.create("http://user-service:8082/api/v1/users/" + userId))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        );

        return JsonHelper.fromJson(response.body(), User.class);
    }
}
```

**Problems:**
- Network latency
- Service might be down (need circuit breakers)
- Distributed transactions

**2. Data consistency:**

**Monolith (easy):**
```java
@Transactional
public void closeDeal(String dealId) {
    Deal deal = dealRepository.findById(dealId);
    deal.setStatus(DealStatus.WON);
    dealRepository.save(deal);

    // Same database transaction
    Commission commission = commissionService.calculate(deal);
    commissionRepository.save(commission);

    // Both succeed or both fail
}
```

**Microservices (hard):**
```java
public void closeDeal(String dealId) {
    // Deal Service updates deal
    Deal deal = dealRepository.findById(dealId);
    deal.setStatus(DealStatus.WON);
    dealRepository.save(deal);  // Committed to Deal Service DB

    // Call Commission Service
    try {
        commissionServiceClient.createCommission(deal);  // Different DB!
    } catch (Exception e) {
        // Deal already saved! Can't rollback across services!
        // Need saga pattern or event sourcing
    }
}
```

**Solution: Event-driven architecture:**
```java
// Deal Service publishes event
public void closeDeal(String dealId) {
    Deal deal = dealRepository.findById(dealId);
    deal.setStatus(DealStatus.WON);
    dealRepository.save(deal);

    // Publish event
    eventBus.publish(new DealClosedEvent(deal));
}

// Commission Service listens for event
@EventListener
public void onDealClosed(DealClosedEvent event) {
    Commission commission = calculate(event.getDeal());
    commissionRepository.save(commission);
}
```

**3. API Gateway:**

**Without gateway (client calls each service):**
```javascript
// Client makes multiple calls
const deal = await fetch('http://deal-service:8081/api/v1/deals/DEAL-001');
const user = await fetch('http://user-service:8082/api/v1/users/' + deal.salesRepId);
const plan = await fetch('http://commission-service:8083/api/v1/plans/' + deal.planId);
```

**With API Gateway:**
```
Client → API Gateway → Deal Service
                    → User Service
                    → Commission Service
```

```javascript
// Client calls gateway only
const dealDetails = await fetch('http://api-gateway/api/v1/deals/DEAL-001?include=user,plan');
// Gateway orchestrates calls to services
```

**4. Service discovery:**

Services need to find each other:
```java
// Hardcoded (bad)
String userServiceUrl = "http://localhost:8082";

// Service discovery (good)
ServiceInstance instance = discoveryClient.getInstance("user-service");
String userServiceUrl = instance.getUri().toString();
```

Tools: **Eureka**, **Consul**, **Kubernetes Service Discovery**

**5. Distributed tracing:**

**Monolith (easy):**
```
Request ID: abc123
  DealServlet.doGet()
    DealRepository.findById()
    CommissionService.calculate()
  Done in 50ms
```

**Microservices (complex):**
```
Request ID: abc123
  API Gateway (10ms)
    → Deal Service (20ms)
      → Deal Repository (5ms)
    → User Service (15ms)
      → User Repository (5ms)
    → Commission Service (30ms)
      → Commission Repository (10ms)
      → Calculation Engine (15ms)
  Total: 75ms across 3 services
```

Tools: **Jaeger**, **Zipkin**, **OpenTelemetry**

### Bonus 4: Performance Optimization

**Current performance characteristics:**

- In-memory storage: Very fast reads/writes
- No caching: Every request hits repository
- Synchronous: One request at a time per servlet thread
- No connection pooling: N/A for in-memory

**Optimizations:**

**1. Caching strategies:**

**Read-through cache:**
```java
public class CachingRepository<T> implements Repository<T> {
    private Cache<String, T> cache = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(Duration.ofMinutes(10))
        .recordStats()  // Monitor cache hits/misses
        .build();

    @Override
    public Optional<T> findById(String id) {
        T entity = cache.get(id, key -> {
            return delegate.findById(key).orElse(null);
        });
        return Optional.ofNullable(entity);
    }
}
```

**HTTP caching:**
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    Optional<Deal> deal = repository.findById(id);

    if (deal.isPresent()) {
        // Add cache headers
        response.setHeader("Cache-Control", "max-age=300");  // 5 minutes
        response.setHeader("ETag", generateETag(deal.get()));

        // Check if client has cached version
        String clientETag = request.getHeader("If-None-Match");
        if (clientETag != null && clientETag.equals(generateETag(deal.get()))) {
            response.setStatus(304);  // Not Modified
            return;
        }

        sendJsonResponse(response, deal.get());
    }
}
```

**2. Connection pooling:**

**Current (no pooling):**
```java
EntityManager em = emf.createEntityManager();  // New connection each time
try {
    em.find(Deal.class, id);
} finally {
    em.close();  // Close connection
}
```

**With pooling:**
```java
// Configure HikariCP
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/commissions");
config.setMaximumPoolSize(20);  // Reuse 20 connections
config.setMinimumIdle(5);
HikariDataSource ds = new HikariDataSource(config);

// Connections reused, not recreated
EntityManager em = emf.createEntityManager();  // Gets from pool
em.find(Deal.class, id);
em.close();  // Returns to pool, doesn't close
```

**Benefits:**
- Faster (no connection creation overhead)
- Better resource usage
- Configurable max connections

**3. Async request handling:**

**Current (blocking):**
```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    // Thread blocked while waiting for database
    Optional<Deal> deal = repository.findById(id);  // 50ms wait
    sendJsonResponse(response, deal);
}
```

If 100 concurrent requests, need 100 threads (expensive).

**With async (non-blocking):**
```java
@WebServlet(asyncSupported = true)
public class DealServlet extends BaseServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync();

        // Process on separate thread pool
        CompletableFuture.supplyAsync(() -> {
            return repository.findById(id);
        }).thenAccept(deal -> {
            try {
                sendJsonResponse((HttpServletResponse) asyncContext.getResponse(), deal);
            } finally {
                asyncContext.complete();
            }
        });

        // Servlet thread free to handle other requests!
    }
}
```

**Benefits:**
- Handle more concurrent requests with fewer threads
- Better resource utilization
- Improved throughput

**4. Response compression:**

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    Deal deal = repository.findById(id);

    // Check if client accepts compression
    String acceptEncoding = request.getHeader("Accept-Encoding");
    if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
        response.setHeader("Content-Encoding", "gzip");

        try (GZIPOutputStream gzipOut = new GZIPOutputStream(response.getOutputStream())) {
            gzipOut.write(JsonHelper.toJson(deal).getBytes());
        }
    } else {
        sendJsonResponse(response, deal);
    }
}
```

**Benefits:**
- Smaller response size (60-80% reduction)
- Faster network transfer
- Lower bandwidth costs

**5. Database query optimization:**

**N+1 query problem:**
```java
// BAD: Loads deals, then queries for each user separately
List<Deal> deals = repository.findAll();  // 1 query
for (Deal deal : deals) {
    User user = userRepository.findById(deal.getSalesRepId());  // N queries!
    deal.setSalesRep(user);
}
// Total: 1 + N queries
```

**Solution: Join fetch:**
```java
// GOOD: Single query with join
@Query("SELECT d FROM Deal d LEFT JOIN FETCH d.salesRep")
List<Deal> findAllWithSalesRep();  // 1 query!
```

**6. Pagination:**

**BAD: Load everything:**
```java
List<Deal> deals = repository.findAll();  // Loads 1 million rows!
```

**GOOD: Load page:**
```java
Page<Deal> deals = repository.findAll(PageRequest.of(0, 20));  // Loads 20 rows
```

**Performance summary:**

| Optimization | Benefit | Complexity | Impact |
|--------------|---------|------------|--------|
| Caching | 10-100x faster reads | Low | High |
| Connection pooling | 2-5x faster DB access | Low | High |
| Async requests | 2-10x more throughput | Medium | High |
| Compression | 60-80% less bandwidth | Low | Medium |
| Query optimization | 10-1000x faster | High | High |
| Pagination | Constant memory usage | Low | High |

---

**End of Answers**

This completes the comprehensive answers to all questions about the REST API implementation. Each answer explains the concept, provides code examples, and demonstrates how it's applied in our implementation.