# Mockito Spy Concepts - Answers

Detailed answers to all questions in `QUESTIONS.md`.

---

## Section 1: Basic Concepts

### Answer 1: What is a Spy?

A **spy** in Mockito is a wrapper around a **real object** that delegates to actual method implementations by default, but allows selective stubbing of specific methods.

**Key differences from mocks:**

| Aspect | Mock | Spy |
|--------|------|-----|
| Base | Empty shell (no implementation) | Wraps real object |
| Default | Returns null/0/false | Calls real methods |
| Use | Complete control | Partial control |

**Example:**
```java
// Mock - empty shell
User mockUser = mock(User.class);
mockUser.getFullName();  // Returns null

// Spy - wraps real object
User realUser = new User("jdoe", "john@test.com", "John", "Doe");
User spyUser = spy(realUser);
spyUser.getFullName();   // Returns "John Doe" (real method)
```

### Answer 2: Real vs Empty

**Mock (without stubbing):**
- Returns default values: `null` for objects, `0` for numbers, `false` for booleans
- No real logic executes

**Spy (without stubbing):**
- Calls the **real method implementation**
- Executes actual business logic
- Returns computed/actual values

```java
User mockUser = mock(User.class);
User spyUser = spy(new User("test", "test@test.com", "Test", "User"));

mockUser.getFullName();  // null (no implementation)
spyUser.getFullName();   // "Test User" (real method called)
```

### Answer 3: State Sharing

**Yes, spy and real object share the SAME state.**

**Implications:**
1. Changes through spy affect the real object
2. Changes to real object are visible in spy
3. They reference the same memory location
4. State modifications are bidirectional

```java
Deal realDeal = new Deal("Original", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

// Both changed!
assertEquals("Modified", spyDeal.getTitle());
assertEquals("Modified", realDeal.getTitle());
```

---

## Section 2: Creating Spies

### Answer 4: Creation Methods

**Three ways to create spies:**

```java
// Method 1: spy() function
User realUser = new User("jdoe", "john@test.com", "John", "Doe");
User spy1 = spy(realUser);

// Method 2: @Spy annotation
@Spy
private User spy2 = new User("jdoe", "john@test.com", "John", "Doe");

// Method 3: Inline creation
User spy3 = spy(new User("jdoe", "john@test.com", "John", "Doe"));
```

### Answer 5: Spy Requirements

**No, you CANNOT create a spy without a real object.**

**Reason:** A spy is a wrapper around a real object. It needs an actual instance to delegate method calls to.

```java
// ✗ WRONG - Cannot spy on a class
User spy = spy(User.class);  // Compilation error

// ✗ WRONG - Cannot spy on null
User nullUser = null;
User spy = spy(nullUser);  // NullPointerException

// ✓ CORRECT - Need real instance
User realUser = new User("test", "test@test.com", "Test", "User");
User spy = spy(realUser);
```

### Answer 6: @Spy Annotation

**Requirements:**
1. **Field must be initialized** with a real object
2. Must call `MockitoAnnotations.openMocks(this)` in `@BeforeEach`
3. OR use `@ExtendWith(MockitoExtension.class)` at class level

```java
class MyTest {
    // ✓ CORRECT - initialized
    @Spy
    private User spyUser = new User("test", "test@test.com", "Test", "User");

    // ✗ WRONG - not initialized
    @Spy
    private User spyUser;  // NullPointerException

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Required!
    }
}
```

---

## Section 3: Real Method Calls

### Answer 7: Default Behavior

**Default behavior: The real method is called and executes its actual implementation.**

This is the fundamental characteristic of spies.

```java
User spyUser = spy(new User("jsmith", "john@test.com", "John", "Smith"));

// Real methods execute
String username = spyUser.getUsername();     // "jsmith"
String email = spyUser.getEmail();           // "john@test.com"
String fullName = spyUser.getFullName();     // "John Smith" (computed)
```

### Answer 8: Real Method Execution

```java
@Test
void demonstrateRealMethodExecution() {
    // Create spy
    CommissionPlan realPlan = new CommissionPlan("Test", Currency.getInstance("USD"));
    CommissionPlan spyPlan = spy(realPlan);

    // Set date range
    LocalDate start = LocalDate.now().minusMonths(1);
    LocalDate end = LocalDate.now().plusMonths(11);
    spyPlan.setEffectiveStartDate(start);
    spyPlan.setEffectiveEndDate(end);

    // Call real method that performs computation
    boolean isActive = spyPlan.isActiveOn(LocalDate.now());

    // Real date logic executed
    assertTrue(isActive);  // Within range

    // Verify real method was called
    verify(spyPlan).isActiveOn(LocalDate.now());
}
```

### Answer 9: Side Effects

**Yes, side effects occur!** This is a critical consideration when using spies.

If a real method:
- Writes to database → Database is modified
- Calls external API → API call is made
- Writes to file → File is created/modified
- Modifies state → State changes happen

**Example:**
```java
// Real method with side effects
class OrderService {
    public void processOrder(Order order) {
        database.save(order);  // Side effect!
        emailService.send(order.getCustomer());  // Side effect!
    }
}

OrderService spy = spy(new OrderService());

// Real method called - side effects happen!
spy.processOrder(order);  // Database and email affected!
```

**Solution:** Use `doReturn().when()` to stub methods with side effects:
```java
doNothing().when(spy).processOrder(any());  // Prevents real execution
```

---

## Section 4: Selective Stubbing

### Answer 10: Partial Mocking

**Partial mocking** means:
- Most methods use real implementations
- Specific methods are stubbed/overridden
- Best of both worlds: real behavior + controlled testing

**Why spies are ideal:**
- Spies call real methods by default
- You can override only what you need
- Preserves actual business logic while controlling dependencies

```java
User spy = spy(new User("jdoe", "john@test.com", "John", "Doe"));

// Real methods work
assertEquals("John Doe", spy.getFullName());    // Real
assertEquals("jdoe", spy.getUsername());        // Real

// Stub only specific method
doReturn(true).when(spy).hasRole(UserRole.ADMIN);

// Now hasRole is stubbed, others remain real
assertTrue(spy.hasRole(UserRole.ADMIN));        // Stubbed
assertEquals("John Doe", spy.getFullName());    // Still real
```

### Answer 11: Mixing Real and Stubbed

**Yes!** This is the primary use case for spies.

```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

// Stub email and active status
doReturn("fake@test.com").when(spy).getEmail();
doReturn(true).when(spy).isActive();

// Test behavior
assertEquals("fake@test.com", spy.getEmail());  // Stubbed
assertEquals("test", spy.getUsername());         // Real
assertEquals("Test User", spy.getFullName());    // Real
assertTrue(spy.isActive());                      // Stubbed
```

### Answer 12: Void Method Stubbing

Use `doNothing().when(spy).voidMethod()`:

```java
Deal spy = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

// Prevent real execution of void method
doNothing().when(spy).setStatus(DealStatus.WON);

// Call the method - does nothing
spy.setStatus(DealStatus.WON);

// Verify called but didn't execute
verify(spy).setStatus(DealStatus.WON);
assertNull(spy.getStatus());  // Real setter was bypassed
```

---

## Section 5: doReturn() vs when()

### Answer 13: Critical Difference

```java
// Approach A: when().thenReturn()
when(spy.method()).thenReturn("value");
// Calls method() FIRST, then stubs it

// Approach B: doReturn().when()
doReturn("value").when(spy).method();
// Does NOT call method(), stubs directly
```

**Key difference:** `when()` **calls the real method** before stubbing!

### Answer 14: Side Effect Risk

`when().thenReturn()` is dangerous because it **calls the real method first**, which can:

1. **Trigger unwanted side effects**
   - Database writes
   - Network calls
   - File modifications

2. **Cause exceptions**
   - If the real method throws exceptions
   - Before stubbing takes effect

3. **Produce unexpected behavior**
   - Real method logic executes
   - State changes occur

**Example of the problem:**
```java
class PaymentService {
    public void processPayment(Payment payment) {
        creditCardProcessor.charge(payment);  // Side effect!
        database.save(payment);                // Side effect!
    }
}

PaymentService spy = spy(new PaymentService());

// ✗ BAD - calls real method first
when(spy.processPayment(payment)).thenReturn(...);
// Real processPayment() executes, charges card and saves to DB!

// ✓ GOOD - stubs without calling
doNothing().when(spy).processPayment(payment);
// No real execution, no side effects
```

### Answer 15: Best Practice

**Always use `doReturn().when()` for spies!**

```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

// ✓ BEST PRACTICE for spies
doReturn("stubbed@test.com").when(spy).getEmail();

// ✗ AVOID for spies (can cause issues)
when(spy.getEmail()).thenReturn("stubbed@test.com");
```

**Why:**
- Safe - doesn't call real method
- No side effects
- Predictable behavior
- Recommended by Mockito documentation

---

## Section 6: Verification

### Answer 16: Verifying Spies

**Yes**, you can verify method calls on spies.

**Yes**, you can verify both stubbed and real methods.

```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

// Stub one method
doReturn("fake@test.com").when(spy).getEmail();

// Call both
spy.getEmail();      // Stubbed
spy.getUsername();   // Real

// Verify both work
verify(spy).getEmail();       // ✓ Stubbed method
verify(spy).getUsername();    // ✓ Real method
```

### Answer 17: Argument Captors

**Yes**, ArgumentCaptors work with spies and capture arguments for **both** real and stubbed methods.

```java
Deal spy = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);

// Call real method (not stubbed)
spy.setValue(new BigDecimal("5000"));
spy.setValue(new BigDecimal("10000"));

// Capture arguments
verify(spy, times(2)).setValue(captor.capture());

List<BigDecimal> values = captor.getAllValues();
assertEquals(new BigDecimal("5000"), values.get(0));
assertEquals(new BigDecimal("10000"), values.get(1));
```

### Answer 18: Verification Modes

**Yes**, all verification modes work the same with spies and mocks:

```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

spy.getUsername();
spy.getUsername();
spy.getUsername();

verify(spy, times(3)).getUsername();
verify(spy, atLeast(2)).getUsername();
verify(spy, atMost(5)).getUsername();
verify(spy, never()).setActive(anyBoolean());
```

---

## Section 7: Spy vs Mock

### Answer 19: Performance

**Mocks are typically faster than spies.**

**Why:**
- **Mock:** No real logic executes, just returns defaults
- **Spy:** Executes real method implementations

**Performance comparison:**
```java
// Mock - fast (no logic)
User mock = mock(User.class);
mock.getFullName();  // Returns null instantly

// Spy - slower (executes logic)
User spy = spy(new User("test", "test@test.com", "Test", "User"));
spy.getFullName();   // Concatenates firstName + lastName
```

**Impact:** Usually negligible unless:
- Real methods are computationally expensive
- Running thousands of tests
- Real methods have I/O operations

### Answer 20: When to Use Each

**Use SPY when:**
- Testing legacy code with complex dependencies
- Need real behavior for most methods
- Partial mocking (mostly real, few stubs)
- Testing abstract class concrete methods
- Semi-integration tests
- Verifying real object interactions

**Use MOCK when:**
- Pure unit tests (complete isolation)
- Need complete control over behavior
- Testing with no dependencies
- Fast test execution is critical
- All methods should return controlled values
- New code with good design

**Example decisions:**
```java
// Use MOCK: Testing business logic in isolation
@Test
void testCalculateCommission() {
    DealRepository mock = mock(DealRepository.class);  // Complete control
    when(mock.findById("DEAL-1")).thenReturn(deal);
}

// Use SPY: Testing real calculation while stubbing external call
@Test
void testPriceCalculation() {
    PricingService spy = spy(new PricingService());  // Real calc logic
    doReturn(externalPrice).when(spy).fetchExternalPrice();  // Stub external
}
```

### Answer 21: Isolation

**Mocks provide better test isolation.**

**Comparison:**

| Aspect | Mock | Spy |
|--------|------|-----|
| **Isolation** | Complete | Partial |
| **Dependencies** | None (all controlled) | Real methods may have deps |
| **Test scope** | Single unit | Multiple units |
| **Failures** | Point to exact issue | Could fail for multiple reasons |

**Example:**
```java
// Mock - complete isolation
UserService service = new UserService(mock(UserRepository.class));
// Any failure is in UserService, not repository

// Spy - partial isolation
UserService service = new UserService(spy(new UserRepository()));
// Failure could be in service OR real repository method
```

**Conclusion:** For **pure unit tests**, prefer mocks for better isolation.

---

## Section 8: State Sharing

### Answer 22: Shared State Implications

**Yes, modifying spy state affects the original real object.**

They are the **same object** - the spy is just a wrapper.

```java
Deal realDeal = new Deal("Original", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

// Modify through spy
spyDeal.setValue(new BigDecimal("5000"));
spyDeal.setTitle("Modified");

// Real object is affected
assertEquals(new BigDecimal("5000"), realDeal.getValue());
assertEquals("Modified", realDeal.getTitle());
```

### Answer 23: State Sharing Example

`realUser.isActive()` returns `true`.

```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spyUser = spy(realUser);
spyUser.setActive(true);  // Modifies shared state

assertTrue(realUser.isActive());  // ✓ true - same object!
```

**Explanation:** The spy wraps the real object but doesn't copy it. They share the same memory and state.

### Answer 24: Independent State

**No, you cannot create a spy with independent state.**

That's a fundamental characteristic of spies - they wrap the real object and share its state.

**If you need independent state, consider:**
1. **Use a mock** instead (no real object)
2. **Create a copy** of the object first
3. **Rethink your design** if this is frequently needed

```java
// Cannot do this with spies
Deal original = new Deal("Test", new BigDecimal("1000"), "USER-1");
Deal spy = spy(original);
// Spy and original ALWAYS share state

// Alternative: Use mock for independent behavior
Deal mock = mock(Deal.class);
when(mock.getValue()).thenReturn(new BigDecimal("2000"));
// Mock has its own "state" through stubbing
```

---

## Section 9: Spy Limitations

### Answer 25: Final Classes

**No, by default you CANNOT spy on final classes.**

```java
// ✗ ERROR - String is final
String spy = spy("test");  // MockitoException

// ✗ ERROR - Custom final class
final class FinalUser { }
FinalUser spy = spy(new FinalUser());  // MockitoException
```

**Why:** Mockito uses inheritance/proxies to create spies, which doesn't work with final classes.

**Solution:** Use `mockito-inline` dependency:
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>5.6.0</version>
</dependency>
```

### Answer 26: Null Objects

You get a **NullPointerException**.

```java
User nullUser = null;
User spy = spy(nullUser);  // NullPointerException!
```

**Why:** Spy needs a real object to wrap. Null has no methods to delegate to.

**Solution:** Always ensure the object is non-null before spying:
```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spy = spy(realUser);  // ✓ Works
```

### Answer 27: Primitives

**No, you CANNOT spy on primitives.**

```java
int spy = spy(5);         // ✗ ERROR
boolean spy = spy(true);  // ✗ ERROR
double spy = spy(3.14);   // ✗ ERROR
```

**Why:** Primitives are not objects. Spies wrap objects, not primitives.

**Alternative:** Use wrapper classes if needed:
```java
Integer wrapper = 5;
// But even this doesn't make sense for spying
// Wrapper classes are immutable and final
```

### Answer 28: Interfaces

**No, you CANNOT spy on an interface without a concrete implementation.**

```java
List<String> spy = spy(List.class);  // ✗ ERROR
```

**Why:** Interfaces have no implementation to delegate to.

**Solution:** Provide a concrete implementation:
```java
// ✓ Works - ArrayList is concrete
List<String> realList = new ArrayList<>();
List<String> spy = spy(realList);

// Real methods work
spy.add("item");
assertEquals(1, spy.size());
```

---

## Section 10: Collections

### Answer 29: Spying on Lists

**Yes, you can spy on collections:**

```java
List<String> realList = new ArrayList<>(Arrays.asList("A", "B", "C"));
List<String> spyList = spy(realList);

// Real methods work
assertEquals(3, spyList.size());
assertTrue(spyList.contains("B"));

// Can modify
spyList.add("D");
assertEquals(4, spyList.size());
```

**Use cases:**
1. Track collection modifications
2. Verify add/remove calls
3. Stub specific operations
4. Test code that uses collections

### Answer 30: Collection Behavior

**The real size remains unchanged, but the stubbed method returns 10.**

```java
List<String> realList = new ArrayList<>();
List<String> spy = spy(realList);

// Stub size() to return 10
doReturn(10).when(spy).size();

// Add elements (real operation)
spy.add("A");
spy.add("B");
spy.add("C");

// Stubbed method returns 10
assertEquals(10, spy.size());  // Stubbed

// Real size is 3 (internal state)
// But can't access it through size() because it's stubbed

// Other methods use real state
assertEquals("A", spy.get(0));  // Real get()
assertTrue(spy.contains("B"));   // Real contains()
```

**Implication:** Stubbing collection methods can create confusing state where size() doesn't match actual elements!

---

## Section 11: Advanced Topics

### Answer 31: InOrder Verification

**Yes**, InOrder verification works with spies and verifies **both real and stubbed methods**.

```java
Deal spy = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

// Mix of real and stubbed calls
spy.setStatus(DealStatus.OPEN);    // Real
spy.setValue(new BigDecimal("5000")); // Real
spy.setStatus(DealStatus.WON);     // Real

// Verify order
InOrder inOrder = inOrder(spy);
inOrder.verify(spy).setStatus(DealStatus.OPEN);
inOrder.verify(spy).setValue(new BigDecimal("5000"));
inOrder.verify(spy).setStatus(DealStatus.WON);
```

### Answer 32: Multiple Spies

**Yes**, you can verify interaction order across multiple spies:

```java
Deal spyDeal = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));
User spyUser = spy(new User("test", "test@test.com", "Test", "User"));

// Operations across multiple spies
spyDeal.setStatus(DealStatus.OPEN);
spyUser.setActive(true);
spyDeal.setStatus(DealStatus.WON);

// Verify order across spies
InOrder inOrder = inOrder(spyDeal, spyUser);
inOrder.verify(spyDeal).setStatus(DealStatus.OPEN);
inOrder.verify(spyUser).setActive(true);
inOrder.verify(spyDeal).setStatus(DealStatus.WON);
```

### Answer 33: Spy Reset

`Mockito.reset(spy)` **clears all stubbing and interactions**, but **does NOT affect the real object's state**.

```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spy = spy(realUser);

// Stub and modify state
doReturn("fake@test.com").when(spy).getEmail();
spy.setActive(true);

assertEquals("fake@test.com", spy.getEmail());  // Stubbed
assertTrue(spy.isActive());                      // Real state

// Reset spy
Mockito.reset(spy);

// Stubbing cleared - back to real behavior
assertEquals("test@test.com", spy.getEmail());  // Real again

// Real object state PRESERVED
assertTrue(spy.isActive());                      // Still true
assertTrue(realUser.isActive());                 // Still true
```

---

## Section 12: Use Cases

### Answer 34: Legacy Code

Spies are useful for legacy code because:

**1. Minimal Changes Required**
- Don't need to refactor to make code testable
- Can test existing code as-is

**2. Gradual Testing**
- Test real logic incrementally
- Stub only problematic parts (database, network)

**3. Preserves Real Behavior**
- Test actual business logic
- Only mock external dependencies

**Example:**
```java
class LegacyOrderService {
    public void processOrder(Order order) {
        // Complex business logic you want to test
        validateOrder(order);
        calculateTax(order);
        applyDiscounts(order);

        // External dependency you want to stub
        database.save(order);
        emailService.send(order);
    }
}

@Test
void testLegacyService() {
    LegacyOrderService spy = spy(new LegacyOrderService());

    // Test real business logic
    // Stub only external dependencies
    doNothing().when(spy).saveToDatabase(any());
    doNothing().when(spy).sendEmail(any());

    spy.processOrder(order);

    // Real validation, tax, discount logic executed
    // Database and email stubbed
}
```

### Answer 35: Abstract Classes

Spies help test abstract classes by:

**1. Testing Concrete Methods**
- Real implementations can be tested
- Abstract methods can be stubbed

**2. Template Method Pattern**
- Test template methods (concrete)
- Stub hook methods (abstract)

**Example:**
```java
abstract class BaseService {
    // Concrete method to test
    public void execute() {
        validate();
        process();
        save();
    }

    // Abstract methods
    protected abstract void validate();
    protected abstract void process();
    protected abstract void save();
}

class ConcreteService extends BaseService {
    protected void validate() { /* implementation */ }
    protected void process() { /* implementation */ }
    protected void save() { /* implementation */ }
}

@Test
void testAbstractClass() {
    ConcreteService spy = spy(new ConcreteService());

    // Stub abstract method implementations
    doNothing().when(spy).save();

    // Test real execute() logic
    spy.execute();

    // Verify workflow
    verify(spy).validate();
    verify(spy).process();
    verify(spy).save();
}
```

### Answer 36: Partial Implementation

**Use a spy to test the 9 working methods while stubbing the 1 problematic method:**

```java
class ComplexService {
    public void method1() { /* works */ }
    public void method2() { /* works */ }
    // ... 7 more working methods ...
    public void method10() { /* has issues */ }
}

@Test
void testWorkingMethods() {
    ComplexService spy = spy(new ComplexService());

    // Stub only the problematic method
    doReturn("stubbed-result").when(spy).method10();

    // Test all 9 working methods with real implementations
    spy.method1();  // Real
    spy.method2();  // Real
    // ... etc

    // Verify they work
    verify(spy).method1();
    verify(spy).method2();
}
```

---

## Section 13: Best Practices

### Answer 37: Spy Overuse

**No, extensive spy usage is generally NOT a good practice.**

**Problems with overusing spies:**
1. **Design smell** - May indicate poor class design
2. **Fragile tests** - Coupled to implementation details
3. **Unclear intent** - Hard to understand what's being tested
4. **Maintenance burden** - Tests break with internal changes
5. **Performance** - Slower than mocks

**Better approach:**
- Use spies sparingly
- Prefer mocks for unit tests
- Consider refactoring code for better testability

### Answer 38: Design Smell

Excessive spy usage may indicate:

**1. Violation of Single Responsibility Principle**
- Class does too many things
- Need to spy because can't isolate behavior

**2. Tight Coupling**
- Hard dependencies between components
- Can't test without real implementations

**3. Hidden Dependencies**
- Class uses global state or singletons
- Need spies to control behavior

**4. God Objects**
- Class is too large and complex
- Spying is easier than proper testing

**Better solution:** Refactor code to be more testable:
- Extract interfaces
- Dependency injection
- Smaller, focused classes
- Composition over inheritance

### Answer 39: Spy vs Refactor

**Consider refactoring instead of spying when:**

**1. Spying is painful**
- If you're fighting the framework, refactor

**2. Tests are confusing**
- If tests are hard to read/understand

**3. Repeated patterns**
- Same spy setup in many tests

**4. New code**
- Design testability from the start

**5. Clear design improvements**
- Obvious better structure exists

**Example - Before (needs spy):**
```java
class OrderProcessor {
    public void process(Order order) {
        validateOrder(order);
        calculatePrice(order);
        saveToDatabase(order);  // Hard-coded dependency
        sendEmail(order);        // Hard-coded dependency
    }
}
```

**After (testable design):**
```java
class OrderProcessor {
    private final OrderRepository repository;
    private final EmailService emailService;

    // Dependencies injected - easy to mock
    public OrderProcessor(OrderRepository repo, EmailService email) {
        this.repository = repo;
        this.emailService = email;
    }

    public void process(Order order) {
        validateOrder(order);
        calculatePrice(order);
        repository.save(order);
        emailService.send(order);
    }
}

// Easy to test with mocks - no spy needed!
```

---

## Section 14: Real-World Scenarios

### Answer 40: Database Testing

**Use a MOCK for the database dependency.**

**Why:**
- Complete control over data
- No real database needed
- Fast execution
- Predictable results
- Test edge cases easily

```java
class UserService {
    private final UserRepository repository;

    public User createUser(String username, String email) {
        // Business logic to test
        if (repository.existsByUsername(username)) {
            throw new DuplicateUserException();
        }

        User user = new User(username, email);
        return repository.save(user);
    }
}

@Test
void testCreateUser() {
    // ✓ MOCK the repository
    UserRepository mockRepo = mock(UserRepository.class);
    UserService service = new UserService(mockRepo);

    when(mockRepo.existsByUsername("jdoe")).thenReturn(false);
    when(mockRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    // Test business logic without database
    User user = service.createUser("jdoe", "john@test.com");

    assertNotNull(user);
    verify(mockRepo).existsByUsername("jdoe");
    verify(mockRepo).save(any(User.class));
}
```

**Don't use a spy unless:**
- Testing database logic itself
- Repository has complex query logic to test
- Semi-integration test scenario

### Answer 41: Calculation Logic

**Use a spy on the calculator, stubbing only the external service call:**

```java
class CommissionCalculator {
    public BigDecimal calculate(Deal deal) {
        // Real logic to test
        validateDeal(deal);
        BigDecimal baseValue = deal.getValue();

        // External service call to stub
        BigDecimal priceAdjustment = pricingService.fetchAdjustment(deal);

        // Real calculation logic
        return baseValue.multiply(getRate()).add(priceAdjustment);
    }

    private BigDecimal fetchPriceAdjustment(Deal deal) {
        // External API call - expensive, unreliable
        return externalPricingAPI.getPrice(deal);
    }
}

@Test
void testCalculation() {
    CommissionCalculator spy = spy(new CommissionCalculator());

    // Stub only external call
    doReturn(new BigDecimal("100"))
        .when(spy).fetchPriceAdjustment(any());

    // Test real calculation logic
    BigDecimal commission = spy.calculate(deal);

    // Real business logic executed
    // External call was stubbed
    verify(spy).validateDeal(deal);
    verify(spy).fetchPriceAdjustment(deal);
}
```

### Answer 42: Integration Testing

**Spies are appropriate for integration testing when:**

**1. Testing Component Integration**
- Need real interactions
- Want to verify actual behavior
- But stub external systems

**2. Semi-Integration Tests**
- Test multiple layers together
- Stub only infrastructure

**3. Smoke Testing**
- Quick sanity checks
- Real logic but fake infrastructure

**Example:**
```java
@Test
void integrationTestWithSpies() {
    // Real services
    DealService dealService = new DealService();
    UserService userService = new UserService();

    // Spy to stub only database
    CommissionService spy = spy(new CommissionService(dealService, userService));

    // Stub external dependency
    doReturn(savedResult).when(spy).saveToDatabase(any());

    // Test real integration
    Commission result = spy.calculate(deal, user);

    // Real service interactions happened
    // Only database stubbed
}
```

---

## Section 15: Common Mistakes

### Answer 43: Wrong Stubbing Syntax

**Problem:** Using `when().thenReturn()` instead of `doReturn().when()` for a method with side effects.

```java
// ✗ WRONG
when(spy.someMethodWithSideEffect()).thenReturn("value");
// This calls someMethodWithSideEffect() first, triggers side effects!
```

**Fix:**
```java
// ✓ CORRECT
doReturn("value").when(spy).someMethodWithSideEffect();
// Stubs without calling the method
```

### Answer 44: Expecting Mock Behavior

**Bug:** The test expects the spy to behave like a mock (return null) but spies call real methods.

```java
@Test
void testSpy() {
    User spy = spy(new User("jdoe", "john@test.com", "John", "Doe"));
    assertNull(spy.getUsername());  // ✗ FAILS - returns "jdoe"
}
```

**Fix - Option 1:** Stub the method if you want null:
```java
@Test
void testSpy() {
    User spy = spy(new User("jdoe", "john@test.com", "John", "Doe"));
    doReturn(null).when(spy).getUsername();
    assertNull(spy.getUsername());  // ✓ PASSES
}
```

**Fix - Option 2:** Test the real behavior:
```java
@Test
void testSpy() {
    User spy = spy(new User("jdoe", "john@test.com", "John", "Doe"));
    assertEquals("jdoe", spy.getUsername());  // ✓ PASSES - real method
}
```

### Answer 45: State Confusion

**Bug:** Forgetting that spy and real object share state.

```java
Deal realDeal = new Deal("Original", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

// ✗ FAILS - both have "Modified" because they share state
assertEquals("Original", realDeal.getTitle());
```

**Fix:** Understand state is shared:
```java
Deal realDeal = new Deal("Original", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

// ✓ PASSES - acknowledge shared state
assertEquals("Modified", realDeal.getTitle());
assertEquals("Modified", spyDeal.getTitle());
```

---

## Section 16: Comparison Questions

### Answer 46: Mock vs Spy Table

| Aspect | Mock | Spy |
|--------|------|-----|
| **Base** | Empty shell (no implementation) | Wraps real object |
| **Default method behavior** | Returns null/0/false | Calls real method |
| **State** | No real state | Shares state with real object |
| **Performance** | Fast (no logic execution) | Slower (executes real code) |
| **Best for** | Pure unit tests, complete control | Partial mocking, legacy code |

### Answer 47: Stubbing Comparison

**Approach A: `when().thenReturn()`**
```java
when(spy.method()).thenReturn("value");
```
- Calls `method()` BEFORE stubbing
- Can trigger side effects
- May throw exceptions
- **Use for:** Mocks (safe) or spies with no side effects

**Approach B: `doReturn().when()`**
```java
doReturn("value").when(spy).method();
```
- Does NOT call `method()`
- Safe from side effects
- No exceptions thrown
- **Use for:** Spies (always), void methods, methods with side effects

**When each is appropriate:**
- **Mocks:** Either approach works (prefer when/thenReturn for readability)
- **Spies:** Always use doReturn/when (safer)
- **Void methods:** Must use doReturn/when (no return value)
- **Side effects:** Must use doReturn/when (avoid triggering)

---

## Section 17: Code Analysis

### Answer 48: Predict the Output

```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spyUser = spy(realUser);

System.out.println(spyUser.getFullName());  // Line 1: "Test User"

doReturn("Stubbed Name").when(spyUser).getFullName();

System.out.println(spyUser.getFullName());  // Line 2: "Stubbed Name"
System.out.println(spyUser.getUsername());  // Line 3: "test"
```

**Explanation:**
- Line 1: Real method called (not stubbed yet) → "Test User"
- Line 2: Now stubbed → "Stubbed Name"
- Line 3: Still real method → "test"

### Answer 49: State Changes

```java
Deal realDeal = new Deal("Test", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setValue(new BigDecimal("2000"));  // Real setter - changes state
doReturn(new BigDecimal("5000")).when(spyDeal).getValue();  // Stub getter

BigDecimal spyValue = spyDeal.getValue();    // 5000 (stubbed)
BigDecimal realValue = realDeal.getValue();  // 2000 (real state)
```

**Final values:**
- `spyValue`: **5000** (stubbed return value)
- `realValue`: **2000** (actual state from setter)

**Explanation:**
- Real setter modified state to 2000
- Stubbed getter returns 5000 (ignores real state)
- Real object's getter shows actual state (2000)

### Answer 50: Verification Puzzle

```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

spy.getUsername();  // Call 1
spy.getEmail();     // Call 2

doReturn("stubbed@test.com").when(spy).getEmail();

spy.getEmail();     // Call 3
spy.getEmail();     // Call 4

verify(spy, times(1)).getUsername();  // ✓ PASS (called once)
verify(spy, times(3)).getEmail();     // ✓ PASS (called 3 times: 2 before stub, 1 after)
```

**Explanation:**
- `getUsername()` called once → verification passes
- `getEmail()` called 3 times total (before and after stubbing) → verification passes
- Stubbing doesn't reset call count

---

## Challenge Solutions

### Challenge 1: Complex Scenario

```java
@Test
void testCommissionCalculatorWithSpies() {
    // Create spy of calculator
    CommissionCalculator spy = spy(new CommissionCalculator());

    // Stub external dependencies only
    doReturn(deal).when(spy).fetchDealData("DEAL-001");      // Stub external
    doReturn(price).when(spy).callExternalPricingAPI(deal);  // Stub external
    doNothing().when(spy).saveToDatabase(any());             // Stub external

    // Execute - real business logic runs
    BigDecimal commission = spy.calculate("DEAL-001");

    // Verify workflow
    InOrder inOrder = inOrder(spy);
    inOrder.verify(spy).fetchDealData("DEAL-001");    // Stubbed
    inOrder.verify(spy).validateDealStatus(deal);     // Real - tested!
    inOrder.verify(spy).callExternalPricingAPI(deal); // Stubbed
    inOrder.verify(spy).calculateCommission(deal);    // Real - tested!
    inOrder.verify(spy).saveToDatabase(any());        // Stubbed

    // Assert result
    assertEquals(expectedCommission, commission);
}
```

### Challenge 2: State Machine

```java
@Test
void testDealStateMachine() {
    Deal spy = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

    // Real transitions
    spy.setStatus(DealStatus.OPEN);
    assertEquals(DealStatus.OPEN, spy.getStatus());

    spy.setStatus(DealStatus.WON);
    assertEquals(DealStatus.WON, spy.getStatus());

    spy.setStatus(DealStatus.CLOSED);
    assertEquals(DealStatus.CLOSED, spy.getStatus());

    // Verify order
    InOrder inOrder = inOrder(spy);
    inOrder.verify(spy).setStatus(DealStatus.OPEN);
    inOrder.verify(spy).setStatus(DealStatus.WON);
    inOrder.verify(spy).setStatus(DealStatus.CLOSED);

    // Test invalid transition
    Deal spy2 = spy(new Deal("Test2", new BigDecimal("1000"), "USER-1"));
    spy2.setStatus(DealStatus.OPEN);

    // Stub validation to reject invalid transition
    doThrow(new IllegalStateException("Invalid transition"))
        .when(spy2).validateTransition(DealStatus.OPEN, DealStatus.CLOSED);

    assertThrows(IllegalStateException.class,
        () -> spy2.setStatus(DealStatus.CLOSED));
}
```

### Challenge 3: Legacy Refactoring

```java
@Test
void testLegacyUserService() {
    LegacyUserService spy = spy(new LegacyUserService());

    // Stub only the 3 slow database methods
    doReturn(user).when(spy).fetchUserFromDatabase(anyString());
    doReturn(true).when(spy).saveUserToDatabase(any());
    doReturn(permissions).when(spy).loadPermissionsFromDatabase(anyString());

    // Test all 12 working methods with real implementations
    spy.validateUser(user);           // Real - works!
    spy.calculateUserScore(user);     // Real - works!
    spy.formatUserDisplay(user);      // Real - works!
    // ... 9 more real methods

    // Verify real logic executed
    verify(spy).validateUser(user);
    verify(spy).calculateUserScore(user);

    // Database methods were stubbed
    verify(spy, never()).fetchUserFromDatabase(anyString());
}
```

---

*Congratulations on completing the Mockito Spy Concepts guide! You now understand the power and limitations of spies for partial mocking.*