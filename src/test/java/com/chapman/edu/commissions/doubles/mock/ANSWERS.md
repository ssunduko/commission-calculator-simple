# Mockito Mocking Concepts - Answers

Detailed answers to all questions in `QUESTIONS.md`.

---

## Section 1: Basic Concepts

### Answer 1: What is Mocking?
A **mock object** is a simulated object that mimics the behavior of a real object in controlled ways. It's used in unit testing to:
- Isolate the code under test from its dependencies
- Control the behavior of collaborating objects
- Verify interactions between objects
- Test edge cases and error scenarios without complex setup

Example:
```java
Deal mockDeal = mock(Deal.class);
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));
```

### Answer 2: Mock vs Real Object
**Key Differences:**

| Aspect | Mock Object | Real Object |
|--------|------------|-------------|
| Behavior | Empty shell, no real logic | Full implementation |
| Dependencies | None | May have complex dependencies |
| Speed | Very fast | May be slow (I/O, network) |
| State | Controlled by test | Actual state changes |

**Default Return Values:**
- Reference types (String, Objects): `null`
- Primitives: `0`, `false`, `0.0`, etc.
- Collections: `null` (not empty collections)

### Answer 3: When to Use Mocks

**✅ Use Mocks When:**
- Testing business logic that depends on external systems (databases, APIs)
- Dependencies are slow, unreliable, or unavailable
- You need to test error scenarios (exceptions, timeouts)
- Testing interactions between objects
- Dependencies aren't implemented yet

**❌ Avoid Mocks When:**
- Testing simple POJOs with no dependencies
- Integration testing (use real objects)
- Testing the mock itself (test behavior, not implementation)
- The "mock" is simpler than the real object

---

## Section 2: Creating Mocks

### Answer 4: Mock Creation Methods

**Three ways to create mocks:**

1. **Using @Mock annotation:**
```java
@Mock
private Deal mockDeal;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```

2. **Using mock() method:**
```java
Deal mockDeal = mock(Deal.class);
```

3. **Using @ExtendWith (JUnit 5):**
```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private Deal mockDeal;  // Automatically initialized
}
```

### Answer 5: @Mock Annotation

**Requirements:**
- Must call `MockitoAnnotations.openMocks(this)` in `@BeforeEach`
- OR use `@ExtendWith(MockitoExtension.class)` at class level
- The field must not be final
- The field must not be static

**Example:**
```java
class MyTest {
    @Mock
    private Deal mockDeal;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
```

### Answer 6: Mock Initialization

If you forget to initialize mocks, you'll get a **NullPointerException** when trying to use `@Mock` annotated fields because they remain null.

**Error:**
```
java.lang.NullPointerException: Cannot invoke "Deal.getValue()" because "this.mockDeal" is null
```

**Solution:** Always initialize mocks in `@BeforeEach` or use `@ExtendWith(MockitoExtension.class)`.

---

## Section 3: Stubbing

### Answer 7: Basic Stubbing

**Stubbing** means defining what a mock should return when its methods are called.

**Syntax:**
```java
when(mock.method()).thenReturn(value);
```

**Example:**
```java
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));
when(mockUser.isActive()).thenReturn(true);
when(mockPlan.getName()).thenReturn("Standard Plan");
```

### Answer 8: Multiple Return Values

Use multiple `thenReturn()` calls for successive invocations:

```java
when(mockDeal.getValue())
    .thenReturn(new BigDecimal("100000"))   // First call
    .thenReturn(new BigDecimal("200000"))   // Second call
    .thenReturn(new BigDecimal("300000"));  // Third call

assertEquals(new BigDecimal("100000"), mockDeal.getValue());
assertEquals(new BigDecimal("200000"), mockDeal.getValue());
assertEquals(new BigDecimal("300000"), mockDeal.getValue());
```

### Answer 9: Stubbing void Methods

**Why not when().thenReturn()?**
Void methods don't return anything, so `when()` syntax doesn't work.

**Use doXxx() syntax instead:**
```java
// For void methods
doNothing().when(mockDeal).setStatus(DealStatus.WON);
doThrow(new IllegalArgumentException()).when(mockDeal).setStatus(DealStatus.INVALID);

// Regular methods can also use doReturn()
doReturn(new BigDecimal("100000")).when(mockDeal).getValue();
```

### Answer 10: Default Behavior

**Unstubbed mock methods return:**

| Type | Default Value |
|------|---------------|
| Reference types | `null` |
| String | `null` |
| BigDecimal | `null` |
| int | `0` |
| boolean | `false` |
| double | `0.0` |
| List, Set, Map | `null` (not empty!) |
| Enums | `null` |

**Important:** Collections return `null`, not empty collections!

---

## Section 4: Verification

### Answer 11: Basic Verification

**Purpose:** Verification checks that specific methods were called on mocks with expected arguments.

**Syntax:**
```java
verify(mock).method(arguments);
```

**Example:**
```java
mockDeal.setStatus(DealStatus.WON);
verify(mockDeal).setStatus(DealStatus.WON);  // Passes

mockDeal.setValue(new BigDecimal("50000"));
verify(mockDeal).setValue(new BigDecimal("50000"));  // Passes
```

### Answer 12: Verification Modes

**Different verification modes:**

```java
// Verify exactly once (default)
verify(mockDeal).getValue();

// Verify exactly N times
verify(mockDeal, times(3)).getValue();

// Verify never called
verify(mockDeal, never()).setStatus(DealStatus.LOST);

// Verify at least N times
verify(mockDeal, atLeast(2)).getValue();

// Verify at most N times
verify(mockDeal, atMost(5)).getValue();

// Verify once or never (0 or 1)
verify(mockDeal, atMostOnce()).getValue();
```

### Answer 13: Verification vs Stubbing

**Stubbing:**
- Defines what mock RETURNS
- Used in ARRANGE phase
- Controls mock behavior

**Verification:**
- Checks what WAS CALLED
- Used in ASSERT phase
- Validates interactions

**Can use together:**
```java
// ARRANGE: Stub
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));

// ACT: Use the mock
BigDecimal value = mockDeal.getValue();

// ASSERT: Verify AND check return value
verify(mockDeal).getValue();
assertEquals(new BigDecimal("100000"), value);
```

---

## Section 5: Argument Matchers

### Answer 14: Why Use Matchers?

**Use matchers when:**
- Exact argument value doesn't matter
- You want to match a pattern or type
- Arguments are complex or dynamic
- Testing general behavior, not specific values

**Example:**
```java
// Don't care which role, just that hasRole was called
when(mockUser.hasRole(any(UserRole.class))).thenReturn(true);

// Verify setValue was called with ANY BigDecimal
verify(mockDeal).setValue(any(BigDecimal.class));
```

### Answer 15: Common Matchers

```java
// any() - matches any object (including null)
when(mockUser.hasRole(any())).thenReturn(true);

// anyString() - matches any String (not null)
when(mockUser.setEmail(anyString())).thenReturn(true);

// eq(value) - matches exact value (used with other matchers)
verify(mockDeal).setValue(eq(new BigDecimal("100000")));

// isNull() - matches only null
verify(mockDeal).setTitle(isNull());

// isNotNull() - matches any non-null value
verify(mockDeal).setTitle(isNotNull());
```

**Type-specific matchers:**
- `anyInt()`, `anyLong()`, `anyDouble()`, `anyBoolean()`
- `anyList()`, `anySet()`, `anyMap()`, `anyCollection()`

### Answer 16: Matcher Rules

**RULE: If you use matchers, ALL arguments must use matchers!**

```java
// ❌ WRONG - mixing matcher and raw value
verify(mockDeal).setValue(new BigDecimal("100000"), any());

// ✅ CORRECT - all arguments use matchers
verify(mockDeal).setValue(eq(new BigDecimal("100000")), any());

// ✅ ALSO CORRECT - no matchers at all
verify(mockDeal).setValue(new BigDecimal("100000"), someValue);
```

**What happens if you break the rule?**
You get an `InvalidUseOfMatchersException`:
```
org.mockito.exceptions.misusing.InvalidUseOfMatchersException:
Invalid use of argument matchers!
```

---

## Section 6: Argument Captors

### Answer 17: Purpose of Captors

**Problem:** Basic verification only checks if a method was called with specific arguments. But what if you need to:
- Verify properties of complex objects
- Check multiple method calls with different arguments
- Perform calculations or comparisons on captured values

**Solution:** Argument Captors capture the actual arguments for detailed inspection.

**Example:**
```java
// Basic verification - only checks exact value
verify(mockDeal).setValue(new BigDecimal("50000"));

// Argument captor - can inspect and assert on the value
ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);
verify(mockDeal).setValue(captor.capture());
BigDecimal captured = captor.getValue();
assertTrue(captured.compareTo(new BigDecimal("40000")) > 0);
assertTrue(captured.compareTo(new BigDecimal("60000")) < 0);
```

### Answer 18: Captor Usage

```java
// Create captor
ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);

// Call method
mockDeal.setValue(new BigDecimal("75000.00"));

// Verify and capture
verify(mockDeal).setValue(captor.capture());

// Get captured value
BigDecimal capturedValue = captor.getValue();

// Assert on captured value
assertEquals(new BigDecimal("75000.00"), capturedValue);
assertTrue(capturedValue.compareTo(new BigDecimal("50000")) > 0);
```

### Answer 19: Multiple Captures

Use `getAllValues()` to get all captured arguments:

```java
ArgumentCaptor<DealStatus> statusCaptor = ArgumentCaptor.forClass(DealStatus.class);

// Call method multiple times
mockDeal.setStatus(DealStatus.OPEN);
mockDeal.setStatus(DealStatus.NEGOTIATION);
mockDeal.setStatus(DealStatus.WON);

// Capture all invocations
verify(mockDeal, times(3)).setStatus(statusCaptor.capture());

// Get all captured values
List<DealStatus> allStatuses = statusCaptor.getAllValues();
assertEquals(3, allStatuses.size());
assertEquals(DealStatus.OPEN, allStatuses.get(0));
assertEquals(DealStatus.NEGOTIATION, allStatuses.get(1));
assertEquals(DealStatus.WON, allStatuses.get(2));
```

---

## Section 7: Spy Objects

### Answer 20: Mock vs Spy

| Aspect | Mock | Spy |
|--------|------|-----|
| Based on | Nothing (empty shell) | Real object |
| Default behavior | Returns null/0/false | Calls real methods |
| Use case | Complete control | Selective stubbing |
| Creation | `mock(Class)` | `spy(Object)` |

**Example:**
```java
// Mock - no real behavior
Deal mockDeal = mock(Deal.class);
mockDeal.calculateTotalValue();  // Returns null

// Spy - real behavior
Deal realDeal = new Deal();
Deal spyDeal = spy(realDeal);
spyDeal.calculateTotalValue();  // Calls real method
```

### Answer 21: When to Use Spies

**Use spies when:**
- You want mostly real behavior with selective overrides
- Testing a class that has some complex methods you want to stub
- Verifying a real object's method calls
- Partial mocking is needed

**Example:**
```java
// Testing real User but overriding one method
User realUser = new User("jsmith", "john@example.com", "John", "Smith");
User spyUser = spy(realUser);

// Real method works
assertEquals("John Smith", spyUser.getFullName());

// Override specific method
when(spyUser.isActive()).thenReturn(false);
assertFalse(spyUser.isActive());
```

**⚠️ Warning:** Spies can lead to fragile tests. Prefer composition over spying when possible.

### Answer 22: Spy Behavior

**Without stubbing, spy calls the REAL method:**

```java
User realUser = new User("jsmith", "john@example.com", "John", "Smith");
User spyUser = spy(realUser);

// Real method is called
assertEquals("jsmith", spyUser.getUsername());
assertEquals("John Smith", spyUser.getFullName());
assertEquals("john@example.com", spyUser.getEmail());
```

### Answer 23: Stubbing Spies

**Two approaches:**

```java
User spyUser = spy(new User("jsmith", "john@example.com", "John", "Smith"));

// 1. when().thenReturn() - calls real method before stubbing
when(spyUser.getEmail()).thenReturn("fake@example.com");
// ⚠️ Problem: Real getEmail() is called, might have side effects

// 2. doReturn().when() - doesn't call real method (PREFERRED for spies)
doReturn("fake@example.com").when(spyUser).getEmail();
// ✅ Better: Real method is NOT called
```

**Rule:** For spies, prefer `doReturn().when()` to avoid calling real methods during stubbing.

---

## Section 8: Exception Handling

### Answer 24: Throwing Exceptions

**For regular methods:**
```java
when(mockDeal.calculateTotalValue())
    .thenThrow(new IllegalStateException("Products list corrupted"));
```

**For void methods:**
```java
doThrow(new IllegalArgumentException("Invalid status"))
    .when(mockDeal).setStatus(DealStatus.INVALID);
```

**Multiple exceptions:**
```java
when(mockDeal.getValue())
    .thenThrow(new IllegalStateException("First error"))
    .thenThrow(new IllegalArgumentException("Second error"))
    .thenReturn(new BigDecimal("100"));  // Third call succeeds
```

### Answer 25: Testing Error Handling

**Mocking is ideal for exception testing because:**

1. **Simulate rare errors** without actually causing them
2. **Test all error paths** (network failure, database error, timeout)
3. **Fast execution** - no need to set up failure conditions
4. **Deterministic** - errors happen consistently

**Example:**
```java
// Test repository error handling
DealRepository mockRepo = mock(DealRepository.class);
when(mockRepo.findById("DEAL-001"))
    .thenThrow(new DatabaseException("Connection lost"));

// Service should handle the exception
CommissionService service = new CommissionService(mockRepo);
assertThrows(ServiceException.class,
    () -> service.calculateCommission("DEAL-001"));

// Verify error was logged
verify(mockLogger).error("Failed to fetch deal: DEAL-001");
```

---

## Section 9: Custom Answers

### Answer 26: What is an Answer?

An **Answer** is a custom implementation that defines dynamic behavior for mock method calls.

**Use when:**
- Return value depends on arguments
- Complex logic is needed
- Different behavior for different argument values
- Need access to invocation context

**Example:**
```java
when(mockUser.hasRole(any(UserRole.class))).thenAnswer(invocation -> {
    UserRole role = invocation.getArgument(0);
    return role == UserRole.SALES_REP || role == UserRole.SALES_MANAGER;
});
```

### Answer 27: Answer Implementation

**Access arguments via `InvocationOnMock`:**

```java
when(mockDeal.calculateCommission(any(BigDecimal.class), any(BigDecimal.class)))
    .thenAnswer(invocation -> {
        BigDecimal dealValue = invocation.getArgument(0);
        BigDecimal rate = invocation.getArgument(1);
        return dealValue.multiply(rate);
    });

// Test
BigDecimal commission = mockDeal.calculateCommission(
    new BigDecimal("100000"),
    new BigDecimal("0.10")
);
assertEquals(new BigDecimal("10000"), commission);
```

**Other useful InvocationOnMock methods:**
- `getArguments()` - get all arguments as Object[]
- `getMock()` - get the mock object
- `getMethod()` - get the Method being invoked

### Answer 28: Answer vs thenReturn

| Use Case | thenReturn | thenAnswer |
|----------|-----------|------------|
| Static value | ✅ Preferred | ❌ Overkill |
| Value depends on args | ❌ Can't do | ✅ Required |
| Complex calculation | ❌ Can't do | ✅ Required |
| Performance | ✅ Fast | ⚠️ Slower |

**Examples:**
```java
// Static value - use thenReturn
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));

// Dynamic value - use thenAnswer
when(mockDeal.calculateDiscount(any(BigDecimal.class)))
    .thenAnswer(inv -> {
        BigDecimal value = inv.getArgument(0);
        return value.compareTo(new BigDecimal("50000")) > 0
            ? new BigDecimal("0.10")  // 10% discount for > 50k
            : new BigDecimal("0.05"); // 5% discount otherwise
    });
```

---

## Section 10: InOrder Verification

### Answer 29: Why InOrder?

**Use InOrder when the sequence of operations matters:**

**Real-world examples:**
1. **State machines** - Deal: OPEN → NEGOTIATION → WON
2. **Workflows** - Validate → Process → Save → Notify
3. **Resource management** - Open → Use → Close
4. **Transaction boundaries** - Begin → Execute → Commit

**Example:**
```java
// Deal lifecycle must follow specific order
InOrder inOrder = inOrder(mockDeal);
inOrder.verify(mockDeal).setStatus(DealStatus.OPEN);
inOrder.verify(mockDeal).setStatus(DealStatus.NEGOTIATION);
inOrder.verify(mockDeal).setStatus(DealStatus.PROPOSAL);
inOrder.verify(mockDeal).setStatus(DealStatus.WON);
```

### Answer 30: InOrder Syntax

```java
// Create InOrder verifier
InOrder inOrder = inOrder(mockObject);

// Verify methods in sequence
inOrder.verify(mockObject).firstMethod();
inOrder.verify(mockObject).secondMethod();
inOrder.verify(mockObject).thirdMethod();

// Can also use verification modes
inOrder.verify(mockObject, times(2)).repeatedMethod();
```

### Answer 31: Multiple Mocks

**Yes! InOrder works across multiple mocks:**

```java
Deal mockDeal = mock(Deal.class);
User mockUser = mock(User.class);
CommissionPlan mockPlan = mock(CommissionPlan.class);

// Execute operations
mockDeal.setStatus(DealStatus.OPEN);
mockUser.setLastLogin(LocalDateTime.now());
mockPlan.setStatus(PlanStatus.ACTIVE);
mockDeal.setStatus(DealStatus.WON);

// Verify order across all mocks
InOrder inOrder = inOrder(mockDeal, mockUser, mockPlan);
inOrder.verify(mockDeal).setStatus(DealStatus.OPEN);
inOrder.verify(mockUser).setLastLogin(any(LocalDateTime.class));
inOrder.verify(mockPlan).setStatus(PlanStatus.ACTIVE);
inOrder.verify(mockDeal).setStatus(DealStatus.WON);
```

---

## Section 11: BDD Style

### Answer 32: BDD Mocking

**BDD = Behavior-Driven Development**

**Differences:**

| Traditional | BDD Style |
|------------|-----------|
| when().thenReturn() | given().willReturn() |
| verify() | then().should() |
| Focus on "how" | Focus on "what" |
| Implementation-focused | Behavior-focused |

**BDD makes tests read like specifications.**

### Answer 33: Given-When-Then

**Mapping:**

```java
// Traditional Mockito
when(mockUser.getFullName()).thenReturn("Jane Doe");
verify(mockUser).getFullName();

// BDD Style
given(mockUser.getFullName()).willReturn("Jane Doe");
then(mockUser).should().getFullName();
```

**Complete BDD test:**
```java
@Test
void shouldCalculateCommissionForWonDeal() {
    // GIVEN
    given(mockDeal.getStatus()).willReturn(DealStatus.WON);
    given(mockDeal.getValue()).willReturn(new BigDecimal("100000"));

    // WHEN
    BigDecimal commission = calculator.calculate(mockDeal);

    // THEN
    then(mockDeal).should().getStatus();
    then(mockDeal).should().getValue();
    assertEquals(new BigDecimal("10000"), commission);
}
```

### Answer 34: BDD Benefits

**Benefits:**
1. **Better readability** - Tests read like specifications
2. **Clearer intent** - Focus on behavior, not implementation
3. **Stakeholder-friendly** - Non-developers can understand tests
4. **Natural language** - Given-When-Then matches human thinking
5. **Living documentation** - Tests document expected behavior

**Example - Compare:**

```java
// Traditional - implementation-focused
@Test
void testGetValue() {
    when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));
    assertEquals(new BigDecimal("100000"), mockDeal.getValue());
}

// BDD - behavior-focused
@Test
void dealShouldReturnConfiguredValue() {
    // Given a deal with value 100,000
    given(mockDeal.getValue()).willReturn(new BigDecimal("100000"));

    // When we get the value
    BigDecimal value = mockDeal.getValue();

    // Then it should return 100,000
    assertEquals(new BigDecimal("100000"), value);
}
```

---

## Section 12: Advanced Topics

### Answer 35: Mock Reset

**What it does:**
`Mockito.reset(mock)` clears all stubbing and interactions on a mock.

```java
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));
mockDeal.getValue();

Mockito.reset(mockDeal);

// Stubbing is gone
assertNull(mockDeal.getValue());

// Interactions are cleared
verify(mockDeal, never()).getValue();  // Passes!
```

**When to use:**
- Rarely! Usually indicates poor test design
- Reusing mocks across test methods (prefer fresh mocks)

**When NOT to use:**
- Between test methods (use @BeforeEach instead)
- To "fix" complex test scenarios (refactor the test)

### Answer 36: Verification Timeout

**Purpose:** Wait for async operations to complete.

```java
verify(mockDeal, timeout(500)).setStatus(DealStatus.WON);
```

This **waits up to 500ms** for the method to be called.

**Use cases:**
- Testing asynchronous code
- Multi-threaded operations
- Event-driven systems
- Callbacks and listeners

**Example:**
```java
@Test
void shouldHandleAsyncDealUpdate() throws InterruptedException {
    // Start async operation
    new Thread(() -> {
        Thread.sleep(100);
        mockDeal.setStatus(DealStatus.WON);
    }).start();

    // Wait up to 500ms for the call
    verify(mockDeal, timeout(500)).setStatus(DealStatus.WON);
}
```

### Answer 37: Final Classes

**Can Mockito mock final classes?**

**By default:** No

**With mockito-inline:** Yes (since Mockito 2.1.0)

**Setup:**
Add `mockito-inline` dependency:
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>5.6.0</version>
    <scope>test</scope>
</dependency>
```

**Limitations:**
- Cannot mock final methods (even with mockito-inline)
- Cannot mock static methods (use PowerMock or mockito-inline)
- Cannot mock private methods (refactor or use PowerMock)

### Answer 38: Partial Mocking

**Besides spies:**

1. **@Spy with @InjectMocks:**
```java
@Spy
@InjectMocks
private MyService service;

@Test
void test() {
    // Real methods called, can stub specific ones
    when(service.helperMethod()).thenReturn("mocked");
}
```

2. **CallRealMethod:**
```java
Deal mockDeal = mock(Deal.class);
when(mockDeal.getTitle()).thenReturn("Mocked Title");
when(mockDeal.getValue()).thenCallRealMethod();  // Call real method
```

3. **Spy on anonymous class:**
```java
Deal partialDeal = spy(new Deal() {
    @Override
    public BigDecimal getValue() {
        return new BigDecimal("100000");
    }
});
```

---

## Section 13: Best Practices

### Answer 39: What to Mock

**Should you mock the class under test?**
❌ **NO** - Never mock the class you're testing!

**Should you mock simple value objects (POJOs)?**
❌ **NO** - Use real objects for simple data holders.

**Should you mock external dependencies?**
✅ **YES** - Databases, APIs, file systems, etc.

**Should you mock complex collaborators?**
✅ **YES** - Services, repositories, complex business logic.

**Examples:**
```java
class CommissionServiceTest {
    // ❌ Don't mock the class under test
    // private CommissionService mockService;

    // ✅ Mock external dependencies
    @Mock
    private DealRepository mockDealRepo;

    @Mock
    private UserRepository mockUserRepo;

    // ❌ Don't mock simple value objects
    // @Mock private Deal mockDeal;

    // ✅ Create real value objects
    private Deal testDeal = new Deal("Test", new BigDecimal("100000"), "USER-1");

    // ✅ The actual class under test
    private CommissionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CommissionService(mockDealRepo, mockUserRepo);
    }
}
```

### Answer 40: Over-Mocking

**Over-mocking** is mocking too many objects, making tests:
- Brittle (break with implementation changes)
- Hard to understand
- Tightly coupled to implementation details
- Less valuable (testing the mocks, not the code)

**Example of over-mocking:**
```java
// ❌ Over-mocked
@Test
void test() {
    Deal mockDeal = mock(Deal.class);
    when(mockDeal.getId()).thenReturn("DEAL-1");
    when(mockDeal.getTitle()).thenReturn("Test");
    when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));
    when(mockDeal.getStatus()).thenReturn(DealStatus.WON);
    when(mockDeal.getSalesRepId()).thenReturn("USER-1");
    // ... 20 more stubs
}

// ✅ Use real object instead
@Test
void test() {
    Deal deal = new Deal("Test", new BigDecimal("100"), "USER-1");
    deal.setId("DEAL-1");
    deal.setStatus(DealStatus.WON);
}
```

**How to avoid:**
- Mock only external dependencies
- Use real objects for simple data
- Test behavior, not implementation
- If mocking is hard, consider refactoring

### Answer 41: Brittle Tests

**Problem:** Too much verification makes tests break when implementation changes.

**Example - Brittle:**
```java
@Test
void testCalculateCommission() {
    service.calculateCommission(deal);

    // ❌ Over-specified - breaks if implementation changes
    verify(mockRepo).findById("DEAL-1");
    verify(mockValidator).validate(deal);
    verify(mockLogger, times(3)).log(anyString());
    verify(mockCache).get("DEAL-1");
    verify(mockCache).put("DEAL-1", any());
    // ... 10 more verifications
}
```

**Better - Focused:**
```java
@Test
void testCalculateCommission() {
    BigDecimal commission = service.calculateCommission(deal);

    // ✅ Verify only critical interactions
    verify(mockRepo).findById("DEAL-1");
    verify(mockResultRepo).save(any(CommissionResult.class));

    // ✅ Assert on outcome
    assertEquals(new BigDecimal("10000"), commission);
}
```

**Balance:**
- Verify important interactions
- Don't verify every single call
- Focus on behavior, not implementation details

### Answer 42: Mock Naming

**Conventions:**

```java
// ✅ Prefix with "mock"
@Mock
private Deal mockDeal;

@Mock
private DealRepository mockDealRepository;

// ✅ Or use "stub" for stubbed objects
private Deal stubDeal;

// ✅ Descriptive names for test data
private Deal wonDeal;
private Deal lostDeal;

// ❌ Avoid generic names
private Deal d;
private Deal test;
private Deal obj;
```

---

## Section 14: Real-World Scenarios

### Answer 43: Database Testing

**Scenario:** Service depends on database repository.

```java
class CommissionServiceTest {
    @Mock
    private DealRepository mockDealRepo;

    private CommissionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CommissionService(mockDealRepo);
    }

    @Test
    void shouldCalculateCommissionForDeal() {
        // ARRANGE: Mock database call
        Deal deal = new Deal("Test Deal", new BigDecimal("100000"), "USER-1");
        deal.setStatus(DealStatus.WON);

        when(mockDealRepo.findById("DEAL-1")).thenReturn(Optional.of(deal));

        // ACT: Call service (which calls repository)
        BigDecimal commission = service.calculateCommission("DEAL-1");

        // ASSERT: Verify database was called
        verify(mockDealRepo).findById("DEAL-1");
        assertEquals(new BigDecimal("10000"), commission);
    }

    @Test
    void shouldHandleDatabaseError() {
        // ARRANGE: Mock database failure
        when(mockDealRepo.findById("DEAL-1"))
            .thenThrow(new DatabaseException("Connection lost"));

        // ACT & ASSERT: Service should handle error
        assertThrows(ServiceException.class,
            () -> service.calculateCommission("DEAL-1"));
    }
}
```

### Answer 44: Chain of Calls

**Problem:** Mock fluent APIs or chained calls.

**Approach 1: Mock each object in chain**
```java
A mockA = mock(A.class);
B mockB = mock(B.class);
C mockC = mock(C.class);

when(mockA.getB()).thenReturn(mockB);
when(mockB.getC()).thenReturn(mockC);
when(mockC.getValue()).thenReturn("result");

// Now this works:
String result = mockA.getB().getC().getValue();
```

**Approach 2: Use RETURNS_DEEP_STUBS**
```java
A mockA = mock(A.class, RETURNS_DEEP_STUBS);

when(mockA.getB().getC().getValue()).thenReturn("result");

// Works!
String result = mockA.getB().getC().getValue();
```

**⚠️ Warning:** RETURNS_DEEP_STUBS can lead to brittle tests. Use sparingly.

### Answer 45: State vs Interaction Verification

**State Verification:**
- Checks the RESULT of operations
- Verifies return values, final state
- Focuses on "what" the code produces

```java
@Test
void testStateVerification() {
    Deal deal = new Deal("Test", new BigDecimal("100000"), "USER-1");

    BigDecimal commission = calculator.calculate(deal);

    // Verify state (return value)
    assertEquals(new BigDecimal("10000"), commission);
}
```

**Interaction Verification:**
- Checks HOW code behaves
- Verifies method calls on dependencies
- Focuses on "how" the code works

```java
@Test
void testInteractionVerification() {
    calculator.calculate(deal);

    // Verify interactions
    verify(mockRepo).findById("DEAL-1");
    verify(mockLogger).log("Calculating commission");
}
```

**When to use each:**
- **Prefer state verification** - More robust, less brittle
- **Use interaction verification** when:
  - No return value (void methods)
  - Testing integration with external systems
  - Verifying callbacks or events
  - Order of operations matters

### Answer 46: Complex Scenario

**Test commission calculation service:**

```java
class CommissionCalculationServiceTest {
    @Mock
    private DealRepository mockDealRepo;

    @Mock
    private UserRepository mockUserRepo;

    @Mock
    private CommissionPlanRepository mockPlanRepo;

    private CommissionCalculationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CommissionCalculationService(
            mockDealRepo, mockUserRepo, mockPlanRepo
        );
    }

    @Test
    void shouldCalculateCommissionForWonDeal() {
        // ARRANGE: Set up test data
        Deal deal = new Deal("Enterprise Sale", new BigDecimal("150000"), "USER-100");
        deal.setId("DEAL-5000");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now());

        User salesRep = new User("sjohnson", "sarah@example.com", "Sarah", "Johnson");
        salesRep.setId("USER-100");
        salesRep.setActive(true);
        salesRep.addRole(UserRole.SALES_REP);

        CommissionPlan plan = new CommissionPlan("Enterprise Plan", Currency.getInstance("USD"));
        plan.setId("PLAN-001");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(11));

        // ARRANGE: Stub repository calls
        when(mockDealRepo.findById("DEAL-5000")).thenReturn(Optional.of(deal));
        when(mockUserRepo.findById("USER-100")).thenReturn(Optional.of(salesRep));
        when(mockPlanRepo.findActivePlan(any(LocalDate.class))).thenReturn(Optional.of(plan));

        // ACT: Calculate commission
        BigDecimal commission = service.calculateCommission("DEAL-5000");

        // ASSERT: Verify interactions
        verify(mockDealRepo).findById("DEAL-5000");
        verify(mockUserRepo).findById("USER-100");
        verify(mockPlanRepo).findActivePlan(any(LocalDate.class));

        // ASSERT: Verify result (10% of 150,000)
        assertEquals(new BigDecimal("15000.00"), commission);

        // ASSERT: Verify order of operations
        InOrder inOrder = inOrder(mockDealRepo, mockUserRepo, mockPlanRepo);
        inOrder.verify(mockDealRepo).findById("DEAL-5000");
        inOrder.verify(mockUserRepo).findById("USER-100");
        inOrder.verify(mockPlanRepo).findActivePlan(any(LocalDate.class));
    }

    @Test
    void shouldReturnZeroForLostDeal() {
        // ARRANGE
        Deal lostDeal = new Deal("Lost Sale", new BigDecimal("100000"), "USER-100");
        lostDeal.setId("DEAL-5001");
        lostDeal.setStatus(DealStatus.LOST);

        when(mockDealRepo.findById("DEAL-5001")).thenReturn(Optional.of(lostDeal));

        // ACT
        BigDecimal commission = service.calculateCommission("DEAL-5001");

        // ASSERT
        assertEquals(BigDecimal.ZERO, commission);

        // Verify we didn't fetch user or plan (early return)
        verify(mockUserRepo, never()).findById(anyString());
        verify(mockPlanRepo, never()).findActivePlan(any(LocalDate.class));
    }

    @Test
    void shouldThrowExceptionForInactiveUser() {
        // ARRANGE
        Deal deal = new Deal("Test Deal", new BigDecimal("100000"), "USER-100");
        deal.setId("DEAL-5002");
        deal.setStatus(DealStatus.WON);

        User inactiveUser = new User("inactive", "inactive@example.com", "In", "Active");
        inactiveUser.setId("USER-100");
        inactiveUser.setActive(false);

        when(mockDealRepo.findById("DEAL-5002")).thenReturn(Optional.of(deal));
        when(mockUserRepo.findById("USER-100")).thenReturn(Optional.of(inactiveUser));

        // ACT & ASSERT
        assertThrows(InactiveUserException.class,
            () -> service.calculateCommission("DEAL-5002"));
    }
}
```

---

## Section 15: Troubleshooting

### Answer 47: NullPointerException

**Likely causes:**

1. **Forgot to initialize mocks:**
```java
// ❌ Missing MockitoAnnotations.openMocks(this)
@Mock
private Deal mockDeal;  // This is null!

@Test
void test() {
    mockDeal.getValue();  // NPE!
}
```

**Solution:** Add initialization:
```java
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```

2. **Unstubbed method returning null:**
```java
Deal mockDeal = mock(Deal.class);
BigDecimal value = mockDeal.getValue();  // Returns null
value.add(BigDecimal.TEN);  // NPE!
```

**Solution:** Stub the method:
```java
when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));
```

3. **Chained calls:**
```java
mockDeal.getProducts().size();  // NPE if getProducts() returns null
```

**Solution:** Stub the chain:
```java
when(mockDeal.getProducts()).thenReturn(List.of(product1, product2));
```

### Answer 48: Verification Failure

**Error:** "Wanted but not invoked"

```
Wanted but not invoked:
mockDeal.setStatus(DealStatus.WON);
```

**Causes:**

1. **Method wasn't called:**
```java
// Forgot to call the method
// mockDeal.setStatus(DealStatus.WON);

verify(mockDeal).setStatus(DealStatus.WON);  // Fails!
```

2. **Wrong arguments:**
```java
mockDeal.setStatus(DealStatus.LOST);

verify(mockDeal).setStatus(DealStatus.WON);  // Fails!
```

3. **Called on different mock:**
```java
Deal mockDeal1 = mock(Deal.class);
Deal mockDeal2 = mock(Deal.class);

mockDeal1.setStatus(DealStatus.WON);

verify(mockDeal2).setStatus(DealStatus.WON);  // Fails!
```

**Debug:**
- Add `System.out.println()` to verify code path
- Use `verifyNoInteractions(mock)` to check if mock was used at all
- Use `verify(mock, atLeastOnce()).methodName()` for flexible verification

### Answer 49: Stubbing Not Working

**Problem:** Stubbed method returns null.

**Causes:**

1. **Stubbed wrong method:**
```java
when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));

mockDeal.getTotalValue();  // Wrong method! Returns null
```

2. **Stubbed with wrong arguments:**
```java
when(mockDeal.calculateCommission(new BigDecimal("100")))
    .thenReturn(new BigDecimal("10"));

mockDeal.calculateCommission(new BigDecimal("200"));  // Doesn't match! Returns null
```

**Solution:** Use argument matchers:
```java
when(mockDeal.calculateCommission(any(BigDecimal.class)))
    .thenReturn(new BigDecimal("10"));
```

3. **Stubbing after verification:**
```java
verify(mockDeal).getValue();  // Returns null

when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));  // Too late!
```

**Solution:** Stub in @BeforeEach or before calling the method.

### Answer 50: Unnecessary Stubbing

**Warning:**
```
Unnecessary stubbings detected.
Following stubbings are unnecessary (click to navigate to relevant line of code):
  1. -> at MyTest.test(MyTest.java:42)
```

**Cause:** You stubbed a method but never called it in the test.

```java
@Test
void test() {
    when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));  // Stubbed
    when(mockDeal.getTitle()).thenReturn("Test");  // Stubbed but never used!

    // Only use getValue(), not getTitle()
    assertEquals(new BigDecimal("100"), mockDeal.getValue());
}
```

**Solutions:**

1. **Remove the unused stubbing:**
```java
@Test
void test() {
    when(mockDeal.getValue()).thenReturn(new BigDecimal("100"));
    assertEquals(new BigDecimal("100"), mockDeal.getValue());
}
```

2. **Use lenient stubbing if needed:**
```java
lenient().when(mockDeal.getTitle()).thenReturn("Test");
```

**Why it matters:** Unused stubs indicate test maintenance issues or dead code.

---

## Bonus Questions

### Answer 51: Mockito vs Other Frameworks

**Mockito vs EasyMock:**
| Feature | Mockito | EasyMock |
|---------|---------|----------|
| Syntax | when().thenReturn() | expect().andReturn() |
| Record/Replay | No (automatic) | Yes (manual) |
| Learning curve | Easier | Steeper |
| Popularity | Higher | Lower |

**Mockito vs JMockit:**
| Feature | Mockito | JMockit |
|---------|---------|---------|
| Static methods | Limited | Full support |
| Private methods | No | Yes |
| Constructor mocking | No | Yes |
| Complexity | Simple | Complex |
| Maintenance | Active | Less active |

**Recommendation:** Use Mockito for most projects. Use JMockit only if you need advanced features (static/private mocking).

### Answer 52: Mockito Internals

**How Mockito creates mocks:**

1. **Bytecode manipulation** - Uses ByteBuddy (formerly CGLIB) to create proxy classes
2. **Java Proxy** - For interfaces, uses `java.lang.reflect.Proxy`
3. **Method interception** - Intercepts method calls and delegates to stubbing logic

**Key technologies:**
- **ByteBuddy:** Dynamic class generation
- **Reflection:** Accessing and manipulating objects
- **InvocationHandler:** Intercepting method calls

**Process:**
```
1. Create proxy class extending/implementing target
2. Override all methods to intercept calls
3. Store stubbings in internal map
4. Record invocations for verification
5. Return stubbed values or defaults
```

### Answer 53: Performance

**Mock performance:**
- ✅ **Very fast** - In-memory only, no I/O
- ✅ **Faster than real objects** - No database, network, file system
- ⚠️ **Slight overhead** - Proxy creation and method interception

**Potential issues:**
1. **Creating many mocks:** Proxy creation has small cost
2. **Complex answers:** Heavy computation in `thenAnswer()` can slow tests
3. **Deep stubs:** RETURNS_DEEP_STUBS creates many proxies

**Best practices:**
- Reuse mocks when possible (but prefer fresh mocks per test)
- Avoid complex logic in answers
- Profile tests if they're slow

### Answer 54: Integration with Frameworks

**JUnit 5:**
```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private Deal mockDeal;  // Auto-initialized
}
```

**Spring Framework:**
```java
@SpringBootTest
class MyTest {
    @MockBean  // Spring's annotation for mocking beans
    private DealRepository mockDealRepo;

    @Autowired
    private CommissionService service;  // Injected with mock
}
```

**Dependency Injection:**
```java
@InjectMocks  // Mockito injects mocks into this object
private CommissionService service;

@Mock
private DealRepository mockDealRepo;

// Mockito creates: service = new CommissionService(mockDealRepo)
```

### Answer 55: Future of Mocking

**Challenges with modern Java:**

1. **Records (Java 14+):**
   - Immutable, final classes
   - Mockito can mock with mockito-inline
   - May need to use real records in tests

2. **Sealed classes (Java 17+):**
   - Restricted inheritance
   - Mocking may be limited
   - Design for testability

3. **Pattern matching (Java 16+):**
   - May change how we verify arguments
   - Could enable more powerful matchers

**Future trends:**
- More focus on **integration testing** with Testcontainers
- **Contract testing** for APIs
- **Property-based testing** with tools like jqwik
- Mocking may become less central as architectures evolve

**Recommendation:** Keep mocking for unit tests, but don't over-rely on it. Combine with integration tests for complete coverage.

---

## Challenge Solutions

### Challenge 1: Complex Mock Chain

```java
@Test
void shouldCalculateCommissionWithCompleteWorkflow() {
    // ARRANGE: Create mocks
    DealRepository mockDealRepo = mock(DealRepository.class);
    UserRepository mockUserRepo = mock(UserRepository.class);
    CommissionPlanRepository mockPlanRepo = mock(CommissionPlanRepository.class);
    CommissionResultRepository mockResultRepo = mock(CommissionResultRepository.class);
    CommissionCalculator calculator = new CommissionCalculator(
        mockDealRepo, mockUserRepo, mockPlanRepo, mockResultRepo
    );

    // ARRANGE: Create test data
    Deal deal = new Deal("Big Sale", new BigDecimal("200000"), "USER-1");
    deal.setId("DEAL-1");
    deal.setStatus(DealStatus.WON);

    User salesRep = new User("jdoe", "jdoe@example.com", "John", "Doe");
    salesRep.setId("USER-1");
    salesRep.setActive(true);

    CommissionPlan plan = new CommissionPlan("Standard", Currency.getInstance("USD"));
    plan.setStatus(PlanStatus.ACTIVE);

    // ARRANGE: Stub repository calls
    when(mockDealRepo.findById("DEAL-1")).thenReturn(Optional.of(deal));
    when(mockUserRepo.findById("USER-1")).thenReturn(Optional.of(salesRep));
    when(mockPlanRepo.findActivePlan()).thenReturn(Optional.of(plan));

    // ACT: Execute calculation
    CommissionResult result = calculator.calculate("DEAL-1");

    // ASSERT: Verify workflow order
    InOrder inOrder = inOrder(mockDealRepo, mockUserRepo, mockPlanRepo, mockResultRepo);
    inOrder.verify(mockDealRepo).findById("DEAL-1");  // 1. Fetch deal
    inOrder.verify(mockUserRepo).findById("USER-1");  // 2. Fetch sales rep
    inOrder.verify(mockPlanRepo).findActivePlan();    // 3. Fetch plan
    inOrder.verify(mockResultRepo).save(any(CommissionResult.class));  // 4. Save result

    // ASSERT: Verify result
    assertNotNull(result);
    assertEquals(new BigDecimal("20000"), result.getCommission());  // 10% of 200k
}
```

### Challenge 2: Error Cascading

```java
@Test
void shouldHandleErrorsWithFallbackAndLogging() {
    // ARRANGE: Create mocks
    ExternalApi mockApi1 = mock(ExternalApi.class);
    ExternalApi mockApi2 = mock(ExternalApi.class);
    ExternalApi mockApi3 = mock(ExternalApi.class);
    Logger mockLogger = mock(Logger.class);
    ApiService service = new ApiService(mockApi1, mockApi2, mockApi3, mockLogger);

    // ARRANGE: Simulate failures
    when(mockApi1.fetchData()).thenThrow(new ApiException("API 1 failed"));
    when(mockApi2.fetchData()).thenThrow(new ApiException("API 2 failed"));
    when(mockApi3.fetchData()).thenReturn("fallback data");

    // ACT: Call service
    String result = service.fetchWithFallback();

    // ASSERT: Verify error handling
    verify(mockApi1).fetchData();
    verify(mockLogger).error("API 1 failed");
    verify(mockApi2).fetchData();
    verify(mockLogger).error("API 2 failed");
    verify(mockApi3).fetchData();

    // ASSERT: Verify fallback worked
    assertEquals("fallback data", result);

    // ASSERT: Verify order
    InOrder inOrder = inOrder(mockApi1, mockLogger, mockApi2, mockApi3);
    inOrder.verify(mockApi1).fetchData();
    inOrder.verify(mockLogger).error(contains("API 1"));
    inOrder.verify(mockApi2).fetchData();
    inOrder.verify(mockLogger).error(contains("API 2"));
    inOrder.verify(mockApi3).fetchData();
}
```

### Challenge 3: State Machine Testing

```java
@Test
void shouldTransitionThroughValidStates() {
    // ARRANGE: Create spy of real Deal
    Deal deal = new Deal("Test Deal", new BigDecimal("100000"), "USER-1");
    Deal spyDeal = spy(deal);

    StateChangeListener mockListener = mock(StateChangeListener.class);
    spyDeal.addStateChangeListener(mockListener);

    // ACT: Transition through valid states
    spyDeal.setStatus(DealStatus.OPEN);
    spyDeal.setStatus(DealStatus.NEGOTIATION);
    spyDeal.setStatus(DealStatus.PROPOSAL);
    spyDeal.setStatus(DealStatus.WON);

    // ASSERT: Verify state transitions in order
    InOrder inOrder = inOrder(spyDeal, mockListener);
    inOrder.verify(spyDeal).setStatus(DealStatus.OPEN);
    inOrder.verify(mockListener).onStateChange(DealStatus.OPEN);
    inOrder.verify(spyDeal).setStatus(DealStatus.NEGOTIATION);
    inOrder.verify(mockListener).onStateChange(DealStatus.NEGOTIATION);
    inOrder.verify(spyDeal).setStatus(DealStatus.PROPOSAL);
    inOrder.verify(mockListener).onStateChange(DealStatus.PROPOSAL);
    inOrder.verify(spyDeal).setStatus(DealStatus.WON);
    inOrder.verify(mockListener).onStateChange(DealStatus.WON);

    // ASSERT: Final state is correct
    assertEquals(DealStatus.WON, spyDeal.getStatus());
}

@Test
void shouldRejectInvalidStateTransition() {
    // ARRANGE: Spy on real Deal
    Deal deal = new Deal("Test Deal", new BigDecimal("100000"), "USER-1");
    Deal spyDeal = spy(deal);
    spyDeal.setStatus(DealStatus.OPEN);

    // ACT & ASSERT: Invalid transition should throw
    assertThrows(IllegalStateException.class,
        () -> spyDeal.setStatus(DealStatus.WON));  // Can't jump from OPEN to WON

    // ASSERT: State unchanged
    assertEquals(DealStatus.OPEN, spyDeal.getStatus());
}
```

---

*Congratulations on completing the Mockito Mocking Concepts guide! Practice these concepts to master mocking in your tests.*