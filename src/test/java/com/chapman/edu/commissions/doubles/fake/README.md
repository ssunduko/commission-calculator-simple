# Test Doubles - Fake Pattern

## Overview

This module demonstrates the **Fake** pattern, a sophisticated test double with working implementations. Fakes have real business logic and maintain state, but use simplified approaches that make them unsuitable for production while being perfect for testing.

## What is a Fake?

A **Fake** is a working implementation that:

1. **Has real business logic** - Actually implements functionality, not just returns values
2. **Maintains state** - Tracks changes like real objects
3. **Uses shortcuts** - Simplified approach (e.g., HashMap vs database)
4. **Works for testing** - Fast, reliable, deterministic
5. **NOT production-ready** - Lacks scalability, persistence, transactions

Think of a fake as a "lite" version of production code - it works the same way but takes shortcuts that make it unsuitable for production but perfect for testing.

## Fake in the Test Double Hierarchy

```
Test Double Types (by complexity):

Dummy    ← Simplest (never used)
  ↓
Stub     ← Returns pre-programmed values
  ↓
Spy      ← Records calls and delegates to real object
  ↓
Mock     ← Verifies expectations and behavior
  ↓
Fake     ← Working implementation (simplified) ← YOU ARE HERE
```

## Common Fake Examples

| Real Implementation | Fake Implementation |
|---------------------|---------------------|
| **PostgreSQL Database** | `HashMap<String, Entity>` |
| **Redis Cache** | `ConcurrentHashMap` with TTL |
| **Kafka Message Queue** | `ArrayList` with listeners |
| **AWS S3** | Local file system or `HashMap` |
| **Elasticsearch** | In-memory list with filtering |
| **Email Service** | List of sent messages |
| **ID Generation (DB Sequence)** | `AtomicLong` counter |
| **Distributed Lock** | `synchronized` block |

## When to Use Fakes

### ✓ Good Use Cases

**1. Avoiding External Dependencies**
```java
// Testing without real database
FakeUserRepository repository = new FakeUserRepository();
User user = new User("test", "test@test.com", "Test", "User");
repository.save(user);
```

**2. Fast Integration Tests**
```java
// Test business logic without slow I/O
FakeDealRepository dealRepo = new FakeDealRepository();
FakeNotificationService notifications = new FakeNotificationService();
// Fast, reliable, deterministic tests
```

**3. Development Without Infrastructure**
```java
// Develop features before infrastructure is ready
FakeCache<String, Deal> cache = new FakeCache<>();
// Can develop caching logic before Redis is configured
```

**4. Demonstrating API Usage**
```java
// Show how to use APIs without complex setup
FakeEventBus eventBus = new FakeEventBus();
eventBus.subscribe("DealCreated", event -> { /* handle */ });
```

### ✗ When NOT to Use Fakes

**1. Production Code**
- Fakes are for testing only
- Lack scalability, persistence, transactions

**2. Simple Test Cases**
- If a stub suffices, don't build a fake
- Fakes require maintenance

**3. Infrastructure-Specific Testing**
- Testing SQL query optimization requires real database
- Testing network errors requires real network

**4. Verifying Exact Method Calls**
- Use mocks for behavior verification
- Fakes focus on functional behavior

## Core Concepts Demonstrated

### 1. In-Memory Repository

A fake repository that acts like a database but uses HashMap:

```java
static class FakeUserRepository {
    private final Map<String, User> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId("USER-" + idGenerator.getAndIncrement());
        }
        storage.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<User> findActiveUsers() {
        return storage.values().stream()
            .filter(User::isActive)
            .collect(Collectors.toList());
    }
}
```

**Usage:**
```java
FakeUserRepository repository = new FakeUserRepository();

User user = new User("jdoe", "john@example.com", "John", "Doe");
User saved = repository.save(user);

assertNotNull(saved.getId());
assertTrue(repository.findById(saved.getId()).isPresent());
```

### 2. Fake Cache with TTL

In-memory cache simulating Redis with time-to-live support:

```java
static class FakeCache<K, V> {
    private final Map<K, CacheEntry<V>> storage = new ConcurrentHashMap<>();

    public void put(K key, V value, long ttlMillis) {
        storage.put(key, new CacheEntry<>(value, ttlMillis));
    }

    public Optional<V> get(K key) {
        CacheEntry<V> entry = storage.get(key);
        if (entry == null || entry.isExpired()) {
            storage.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.value);
    }
}
```

**Usage:**
```java
FakeCache<String, User> cache = new FakeCache<>();

cache.put("user:1", user, 60000); // 60 second TTL
Optional<User> cached = cache.get("user:1");
assertTrue(cached.isPresent());
```

### 3. Fake Notification Service

Records notifications instead of sending them:

```java
static class FakeNotificationService {
    private final List<Notification> sentNotifications = new ArrayList<>();

    public void sendEmail(String to, String subject, String message) {
        sentNotifications.add(new Notification(to, subject, message, EMAIL));
    }

    public List<Notification> getSentNotifications() {
        return new ArrayList<>(sentNotifications);
    }
}
```

**Usage:**
```java
FakeNotificationService notifications = new FakeNotificationService();

notifications.sendEmail("user@test.com", "Welcome", "Welcome!");
assertEquals(1, notifications.getNotificationCount());
```

### 4. Fake Event Bus

In-memory pub/sub messaging:

```java
static class FakeEventBus {
    private final Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public void subscribe(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void publish(String eventType, Object payload) {
        Event event = new Event(eventType, payload);
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.forEach(listener -> listener.onEvent(event));
        }
    }
}
```

**Usage:**
```java
FakeEventBus eventBus = new FakeEventBus();

eventBus.subscribe("UserCreated", event -> {
    User user = (User) event.payload;
    System.out.println("User created: " + user.getUsername());
});

eventBus.publish("UserCreated", newUser);
```

### 5. Fake ID Generator

Simple sequence-based ID generation:

```java
static class FakeIdGenerator {
    private final Map<String, AtomicLong> sequences = new ConcurrentHashMap<>();

    public String generateId(String prefix) {
        AtomicLong sequence = sequences.computeIfAbsent(prefix, k -> new AtomicLong(1));
        return prefix + "-" + sequence.getAndIncrement();
    }
}
```

**Usage:**
```java
FakeIdGenerator idGenerator = new FakeIdGenerator();

String userId = idGenerator.generateId("USER");  // "USER-1"
String dealId = idGenerator.generateId("DEAL");  // "DEAL-1"
```

## Real-World Scenarios

### Scenario 1: User Registration Workflow

Testing complete registration process with multiple fakes:

```java
@Test
void testUserRegistrationWorkflow() {
    // Setup fake services
    FakeUserRepository userRepo = new FakeUserRepository();
    FakeNotificationService notifications = new FakeNotificationService();
    FakeEventBus eventBus = new FakeEventBus();

    // Subscribe to user creation events
    eventBus.subscribe("UserRegistered", event -> {
        User user = (User) event.payload;
        notifications.sendEmail(
            user.getEmail(),
            "Welcome",
            "Welcome " + user.getFullName() + "!"
        );
    });

    // Register new user
    User newUser = new User("sarah", "sarah@test.com", "Sarah", "Johnson");
    User savedUser = userRepo.save(newUser);
    eventBus.publish("UserRegistered", savedUser);

    // Verify workflow
    assertTrue(userRepo.existsById(savedUser.getId()));
    assertEquals(1, notifications.getNotificationCount());
}
```

### Scenario 2: Deal Approval Workflow

Complex business logic with multiple services:

```java
@Test
void testDealApprovalWorkflow() {
    FakeDealRepository dealRepo = new FakeDealRepository();
    FakeUserRepository userRepo = new FakeUserRepository();
    FakeNotificationService notifications = new FakeNotificationService();
    FakeEventBus eventBus = new FakeEventBus();

    // Create sales rep and manager
    User salesRep = new User("rep", "rep@test.com", "Sales", "Rep");
    salesRep.setManagerId("MGR-1");
    User manager = new User("mgr", "mgr@test.com", "Manager", "User");
    manager.setId("MGR-1");

    userRepo.save(salesRep);
    userRepo.save(manager);

    // Subscribe to deal approval events
    eventBus.subscribe("DealApproved", event -> {
        Deal deal = (Deal) event.payload;
        User rep = userRepo.findById(deal.getSalesRepId()).orElse(null);
        if (rep != null && rep.getManagerId() != null) {
            User mgr = userRepo.findById(rep.getManagerId()).orElse(null);
            if (mgr != null) {
                notifications.sendEmail(mgr.getEmail(), "Deal Approved", "...");
            }
        }
    });

    // Approve deal
    Deal deal = new Deal("Large Deal", new BigDecimal("150000"), salesRep.getId());
    deal.setStatus(DealStatus.WON);
    dealRepo.save(deal);
    eventBus.publish("DealApproved", deal);

    // Verify manager was notified
    assertEquals(1, notifications.getNotificationCount());
}
```

### Scenario 3: Cache-Aside Pattern

Testing caching layer behavior:

```java
@Test
void testCacheAsidePattern() {
    FakeDealRepository dealRepo = new FakeDealRepository();
    FakeCache<String, Deal> dealCache = new FakeCache<>();

    Deal deal = new Deal("Cached Deal", new BigDecimal("50000"), "USER-1");
    dealRepo.save(deal);

    // First access - cache miss
    String dealId = deal.getId();
    Optional<Deal> cachedDeal = dealCache.get(dealId);
    if (cachedDeal.isEmpty()) {
        Optional<Deal> fromDb = dealRepo.findById(dealId);
        fromDb.ifPresent(d -> dealCache.put(dealId, d));
    }

    // Second access - cache hit
    Optional<Deal> secondAccess = dealCache.get(dealId);
    assertTrue(secondAccess.isPresent());
}
```

### Scenario 4: Batch Event Processing

Simulating async processing:

```java
@Test
void testBatchEventProcessing() {
    FakeEventBus eventBus = new FakeEventBus();
    List<Deal> processedDeals = new ArrayList<>();

    eventBus.subscribe("DealBatchImported", event -> {
        List<Deal> deals = (List<Deal>) event.payload;
        processedDeals.addAll(deals);
    });

    List<Deal> batchDeals = Arrays.asList(
        new Deal("Batch 1", new BigDecimal("1000"), "USER-1"),
        new Deal("Batch 2", new BigDecimal("2000"), "USER-2"),
        new Deal("Batch 3", new BigDecimal("3000"), "USER-3")
    );

    eventBus.publish("DealBatchImported", batchDeals);

    assertEquals(3, processedDeals.size());
}
```

## Best Practices

### DO:

✅ **Keep fakes simple** - Simpler than production, complex enough to test
```java
// ✓ GOOD - Simple but functional
FakeUserRepository repository = new FakeUserRepository();
repository.save(user);
repository.findById(id);
```

✅ **Maintain state correctly** - Fakes should behave consistently
```java
Deal deal = repository.save(new Deal("Test", value, "USER-1"));
deal.setTitle("Updated");
repository.save(deal);
Deal updated = repository.findById(deal.getId()).get();
assertEquals("Updated", updated.getTitle()); // State persists
```

✅ **Provide clear() method** - Enable test isolation
```java
repository.clear();  // Reset between tests
assertEquals(0, repository.count());
```

✅ **Mirror real behavior** - Fakes should act like real implementations
```java
// Like real JPA, save() returns the saved entity with ID
User saved = repository.save(user);
assertNotNull(saved.getId());
```

✅ **Use thread-safe collections** - For concurrent testing
```java
private final Map<String, User> storage = new ConcurrentHashMap<>();
```

✅ **Implement only needed features** - Don't over-engineer
```java
// Implement findById(), save(), findAll()
// Skip advanced features like pagination unless needed
```

### DON'T:

❌ **Use in production** - Fakes are for testing only
```java
// ✗ WRONG - Never in production
if (isProduction) {
    repository = new FakeUserRepository();  // NO!
}

// ✓ RIGHT - Only in tests
@Test
void testSomething() {
    FakeUserRepository testRepo = new FakeUserRepository();
}
```

❌ **Over-engineer fakes** - Keep them simple
```java
// ✗ WRONG - Too complex for a fake
class FakeDatabase {
    // ACID transactions
    // Connection pooling
    // Query optimization
    // Replication
}

// ✓ RIGHT - Simple fake
class FakeRepository {
    private Map<String, Entity> storage = new HashMap<>();
    public Entity save(Entity e) { ... }
    public Optional<Entity> findById(String id) { ... }
}
```

❌ **Ignore thread safety** - If testing concurrent code
```java
// ✗ WRONG - HashMap not thread-safe
private Map<String, User> storage = new HashMap<>();

// ✓ RIGHT - Use thread-safe collections
private Map<String, User> storage = new ConcurrentHashMap<>();
```

❌ **Let fakes diverge from real implementation** - Keep them consistent
```java
// If real repository returns saved entity with ID, fake should too
public User save(User user) {
    if (user.getId() == null) {
        user.setId(generateId());  // Like real implementation
    }
    storage.put(user.getId(), user);
    return user;  // Return entity like real implementation
}
```

## Comparison with Other Test Doubles

| Test Double | Implementation | State | Behavior | Verification | Example |
|-------------|----------------|-------|----------|--------------|---------|
| **Dummy** | Minimal/none | No state | No behavior | None | `new User()` passed but never used |
| **Stub** | Hardcoded returns | No state | Predetermined | None | `when(repo.findById(id)).thenReturn(user)` |
| **Spy** | Wraps real | Real state | Real behavior | Optional | `spy(new UserRepository())` |
| **Mock** | Framework-generated | Tracked | Stubbed | Required | `verify(repo).save(user)` |
| **Fake** | Working impl | Real state | Real logic | None | `FakeUserRepository` with HashMap |

## Test Structure

### FakeTest.java

The test class demonstrates all fake concepts:

1. **Basic Fake Usage** - Repository CRUD operations and queries
2. **Fake Cache Implementation** - In-memory caching with TTL
3. **Fake Notification Service** - Recording sent messages
4. **Fake Event Bus** - Pub/sub messaging pattern
5. **Fake ID Generator** - Sequence-based ID generation
6. **Real-World Scenarios** - Complete workflows:
   - User registration
   - Deal approval
   - Cache-aside pattern
   - Batch processing
7. **Best Practices** - Implementation guidelines
8. **Common Pitfalls** - What to avoid

### Fake Implementations Included

1. **FakeUserRepository** - User data storage and queries
2. **FakeDealRepository** - Deal data with advanced queries
3. **FakeCache<K,V>** - Generic cache with TTL support
4. **FakeNotificationService** - Email/SMS/Push notifications
5. **FakeEventBus** - Event publishing and subscription
6. **FakeIdGenerator** - ID sequence management

## Running the Tests

```bash
# Run all fake tests
mvn test -Dtest=FakeTest

# Run specific test
mvn test -Dtest=FakeTest#testBasicFakeRepository

# Run with coverage
mvn clean test jacoco:report
```

## Domain Model Usage

This implementation uses these domain models from `com.chapman.edu.commissions.model`:

- **User** - System users with roles and authentication
- **Deal** - Sales deals with products and values
- **DealStatus** - Enum for deal lifecycle states
- **DealProduct** - Products within deals
- **CommissionPlan** - Commission rules and tiers

## Advantages of Fakes

### 1. Fast Execution
- No database I/O
- No network calls
- In-memory operations
- Deterministic behavior

### 2. Test Isolation
- No shared state between tests
- Easy to reset (`clear()` method)
- No cleanup required
- No test interdependencies

### 3. Simplified Testing
- No infrastructure setup
- No configuration
- No external dependencies
- Work offline

### 4. Deterministic Behavior
- Predictable results
- No timing issues
- No flaky tests
- Reproducible failures

## Disadvantages of Fakes

### 1. Maintenance Burden
- Must keep in sync with real implementation
- Changes to real code require fake updates
- Extra code to maintain

### 2. Not Production Ready
- Lacks features (transactions, persistence, etc.)
- Not scalable
- Missing edge cases
- No production monitoring

### 3. Potential Divergence
- Fakes may behave differently than real implementations
- Can give false confidence
- May miss real-world issues

## When Fakes Make Sense

### Perfect For:
✅ Unit testing business logic
✅ Integration testing without infrastructure
✅ Rapid development iterations
✅ CI/CD pipelines (fast tests)
✅ Demonstrating API usage

### Not Ideal For:
❌ Production code
❌ Testing infrastructure-specific behavior
❌ Performance testing
❌ Load testing
❌ Testing database-specific features (transactions, locks, etc.)

## Common Pitfalls

### Pitfall 1: Over-Engineering

```java
// ✗ WRONG - Too complex
class FakeDatabase {
    // Full JDBC implementation
    // Transaction management
    // Connection pooling
    // Query optimizer
}

// ✓ RIGHT - Simple and focused
class FakeUserRepository {
    private Map<String, User> storage = new ConcurrentHashMap<>();
    // Basic CRUD methods only
}
```

### Pitfall 2: Using in Production

```java
// ✗ NEVER DO THIS
public class AppConfig {
    public UserRepository userRepository() {
        return new FakeUserRepository();  // NO!
    }
}

// ✓ RIGHT - Only in tests
@Test
void testUserService() {
    FakeUserRepository testRepo = new FakeUserRepository();
    UserService service = new UserService(testRepo);
}
```

### Pitfall 3: Not Maintaining Consistency

```java
// Real repository auto-generates IDs on save
// Fake should do the same

public User save(User user) {
    if (user.getId() == null) {
        user.setId("USER-" + idGenerator.getAndIncrement());  // ✓ Like real
    }
    storage.put(user.getId(), user);
    return user;  // ✓ Return saved entity like real
}
```

## Additional Resources

- [Martin Fowler - Test Doubles](https://martinfowler.com/bliki/TestDouble.html)
- [xUnit Test Patterns - Fake Object](https://xunitpatterns.com/Fake%20Object.html)
- [Fake vs Mock vs Stub](https://stackoverflow.com/questions/346372/whats-the-difference-between-faking-mocking-and-stubbing)
- [Michael Feathers - Working Effectively with Legacy Code](https://www.amazon.com/Working-Effectively-Legacy-Michael-Feathers/dp/0131177052)

## Related Patterns

- **Stub** - Simpler alternative with predetermined responses (see `../stub/`)
- **Mock** - For behavior verification (see `../mock/`)
- **Spy** - Partial mocking of real objects (see `../spy/`)
- **Dummy** - Simplest test double (see `../dummy/`)

## Key Takeaways

### What Makes a Fake

1. **Working implementation** - Real business logic, not just stubs
2. **Simplified approach** - Uses shortcuts (HashMap vs database)
3. **Maintains state** - Tracks changes correctly
4. **Not production-ready** - Lacks scalability, persistence, etc.
5. **Fast and deterministic** - Perfect for testing

### When to Use Fake

- Need real behavior without external dependencies
- Fast integration tests
- Development without infrastructure
- Testing business logic in isolation

### When NOT to Use Fake

- Production code (NEVER)
- Simple cases where stubs suffice
- Infrastructure-specific testing
- When you need to verify method calls (use Mock)

## Summary

The Fake pattern provides working implementations with real business logic but simplified approaches. Fakes maintain state and behave like real objects, making them perfect for testing complex scenarios without external dependencies. They're faster and more deterministic than real implementations but should never be used in production.

**Key Principle:** Fakes are working implementations optimized for testing, not production.

---

**Total Tests:** 30+ comprehensive tests covering all fake patterns
**Fake Implementations:** 6 reusable fake classes
**Real-World Scenarios:** 5 complete workflow examples