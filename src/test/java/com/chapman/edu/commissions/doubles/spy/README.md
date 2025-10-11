# Mockito Spy Patterns

## Overview

This module demonstrates comprehensive Mockito Spy capabilities using the Commission Calculator domain model. A Spy is a special type of test double that wraps a **real object**, allowing you to test actual implementations while selectively stubbing specific methods for controlled testing scenarios.

## What is a Spy?

A **Spy** in Mockito is a wrapper around a real object that:

1. **Calls real methods by default** - Unlike mocks that return null/0/false, spies execute actual code
2. **Allows selective stubbing** - You can override specific methods while keeping others real
3. **Shares state with the real object** - Changes made through the spy affect the underlying object
4. **Supports verification** - You can verify method calls just like with mocks

Think of a spy as "partial mocking" - you get the best of both worlds: real behavior where you want it, controlled behavior where you need it.

## Mock vs Spy - The Key Difference

| Aspect | Mock | Spy |
|--------|------|-----|
| **Base** | Empty shell (no implementation) | Real object wrapper |
| **Default Behavior** | Returns null/0/false | Calls real methods |
| **Use Case** | Complete control over behavior | Partial control, mostly real |
| **Performance** | Fast (no logic execution) | Slower (executes real code) |
| **Creation** | `mock(Class.class)` | `spy(realObject)` |
| **State** | No real state | Real object state |
| **Best For** | Pure unit tests | Semi-integration tests |

### Visual Comparison

```
MOCK:                          SPY:
┌──────────────┐              ┌──────────────┐
│  Mock Shell  │              │  Real Object │
│              │              │   ┌──────┐   │
│  No logic    │              │   │Logic │   │
│              │              │   └──────┘   │
│  Returns     │              │              │
│  defaults    │              │  Wrappedby   │
└──────────────┘              │   Spy Layer  │
                              └──────────────┘
```

## When to Use Spies

### ✓ Good Use Cases

**1. Legacy Code Testing**
- Testing old code with complex dependencies
- Stubbing problematic parts (database, network) while testing logic

**2. Partial Mocking**
- Need real behavior for most methods
- Override only specific methods for edge cases

**3. Testing Abstract Classes**
- Test concrete methods in classes with abstract methods
- Stub abstract methods while testing concrete implementations

**4. Verifying Real Object Behavior**
- Ensure real methods are called correctly
- Track interactions with actual implementations

**5. Semi-Integration Tests**
- Test integration between components
- Control specific external dependencies

### ✗ When NOT to Use Spies

**1. Pure Unit Tests**
- Use mocks for complete isolation
- Faster and more predictable

**2. Simple Objects**
- No need to spy on POJOs
- Use real objects directly

**3. Side Effects**
- Methods that modify databases
- Network calls or file I/O
- Use mocks instead

**4. Final Classes**
- Cannot spy on final classes without mockito-inline
- Interfaces without implementations

## Core Concepts Demonstrated

### 1. Creating Spies

Three ways to create spy objects:

```java
// Method 1: spy() function
User realUser = new User("jdoe", "john@example.com", "John", "Doe");
User spyUser = spy(realUser);

// Method 2: @Spy annotation
@Spy
private User spyUser = new User("jdoe", "john@example.com", "John", "Doe");

// Method 3: Inline creation
User spyUser = spy(new User("jdoe", "john@example.com", "John", "Doe"));
```

### 2. Real Method Calls

By default, spies call actual method implementations:

```java
User spyUser = spy(new User("jsmith", "john@example.com", "John", "Smith"));

// Real method is called
String fullName = spyUser.getFullName();  // Returns "John Smith"

// Real setter modifies state
spyUser.setActive(true);
assertTrue(spyUser.isActive());  // Real getter returns true
```

### 3. State Sharing

Spies and their underlying real objects share the same state:

```java
Deal realDeal = new Deal("Original", new BigDecimal("10000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

assertEquals("Modified", spyDeal.getTitle());    // Via spy
assertEquals("Modified", realDeal.getTitle());   // Same object!
```

### 4. Selective Stubbing

Override specific methods while keeping others real:

```java
User spyUser = spy(new User("jdoe", "john@example.com", "John", "Doe"));

// Stub only email
doReturn("fake@example.com").when(spyUser).getEmail();

assertEquals("fake@example.com", spyUser.getEmail());  // Stubbed
assertEquals("jdoe", spyUser.getUsername());           // Real
assertEquals("John Doe", spyUser.getFullName());       // Real
```

### 5. doReturn() vs when()

**Critical difference for spies:**

```java
User spyUser = spy(new User("test", "test@example.com", "Test", "User"));

// ✗ BAD: when().thenReturn() calls real method FIRST
when(spyUser.getEmail()).thenReturn("stubbed@example.com");

// ✓ GOOD: doReturn().when() does NOT call real method
doReturn("stubbed@example.com").when(spyUser).getEmail();
```

**Why it matters:**
- `when().thenReturn()` calls the real method before stubbing (can trigger side effects)
- `doReturn().when()` stubs directly without calling the real method (safe)

**Always use `doReturn().when()` for spies!**

### 6. Stubbing Void Methods

Use `doNothing()`, `doThrow()` for void methods:

```java
Deal spyDeal = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

// Prevent real execution
doNothing().when(spyDeal).setStatus(DealStatus.WON);

spyDeal.setStatus(DealStatus.WON);  // Called but does nothing
assertNull(spyDeal.getStatus());     // Status not actually set
```

### 7. Verification

Verify both stubbed and real method calls:

```java
User spyUser = spy(new User("verify", "verify@test.com", "Ver", "Ify"));

doReturn("stubbed@test.com").when(spyUser).getEmail();

spyUser.getEmail();      // Stubbed
spyUser.getUsername();   // Real

verify(spyUser).getEmail();      // Verify stubbed method
verify(spyUser).getUsername();   // Verify real method
verify(spyUser, times(1)).getEmail();
```

### 8. Spy on Collections

Spies work with collections too:

```java
List<String> realList = new ArrayList<>(Arrays.asList("A", "B", "C"));
List<String> spyList = spy(realList);

// Real methods work
assertEquals(3, spyList.size());
assertTrue(spyList.contains("B"));

// Can stub specific operations
doReturn(true).when(spyList).isEmpty();
assertTrue(spyList.isEmpty());    // Stubbed
assertEquals(3, spyList.size());  // Real
```

### 9. InOrder Verification

Verify method call sequence with spies:

```java
Deal spyDeal = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));

spyDeal.setStatus(DealStatus.OPEN);
spyDeal.setValue(new BigDecimal("5000"));
spyDeal.setStatus(DealStatus.WON);

InOrder inOrder = inOrder(spyDeal);
inOrder.verify(spyDeal).setStatus(DealStatus.OPEN);
inOrder.verify(spyDeal).setValue(new BigDecimal("5000"));
inOrder.verify(spyDeal).setStatus(DealStatus.WON);
```

### 10. Spy Reset

Clear stubbing while keeping the real object:

```java
User spyUser = spy(new User("test", "test@test.com", "Test", "User"));

doReturn("stubbed@test.com").when(spyUser).getEmail();
assertEquals("stubbed@test.com", spyUser.getEmail());

Mockito.reset(spyUser);

assertEquals("test@test.com", spyUser.getEmail());  // Back to real
```

## Test Structure

### MockitoSpyTest.java

The test class demonstrates all spy features:

1. **Creating Spies** - Three different creation methods
2. **Real Method Calls** - Default spy behavior
3. **State Sharing** - Understanding shared state
4. **Selective Stubbing** - Partial mocking in action
5. **doReturn() vs when()** - Critical difference for safety
6. **Verification** - Tracking method calls
7. **Spy vs Mock Comparison** - Side-by-side examples
8. **Partial Mocking Use Cases** - Real-world scenarios
9. **Spy on Collections** - Lists, Sets, Maps
10. **Spy Limitations** - What you cannot do
11. **Advanced Techniques** - InOrder, chaining
12. **Real-World Scenario** - Complete business logic example
13. **Spy Reset** - Clearing stubbing
14. **Best Practices** - Guidelines for effective usage

## Running the Tests

```bash
# Run all spy tests
mvn test -Dtest=MockitoSpyTest

# Run specific test
mvn test -Dtest=MockitoSpyTest#testRealMethodCalls

# Run with coverage
mvn clean test jacoco:report
```

## Domain Model Usage

This implementation uses these domain models from `com.chapman.edu.commissions.model`:

- **Deal** - Sales deals with products and calculations
- **User** - System users with roles and authentication
- **CommissionPlan** - Commission rules and date ranges
- **DealProduct** - Products within deals
- **DealStatus** - Enum for deal lifecycle
- **UserRole** - Enum for user permissions
- **PlanStatus** - Enum for plan states

## Spy Limitations

### What You CANNOT Do

1. **Cannot spy on final classes** (without mockito-inline)
   ```java
   String spy = spy("test");  // ✗ ERROR - String is final
   ```

2. **Cannot spy on null objects**
   ```java
   User nullUser = null;
   User spy = spy(nullUser);  // ✗ NullPointerException
   ```

3. **Cannot spy on primitives**
   ```java
   int spy = spy(5);  // ✗ ERROR - primitives not supported
   ```

4. **Cannot spy on interfaces** (without implementation)
   ```java
   List<String> spy = spy(List.class);  // ✗ ERROR - need concrete class
   ```

5. **Side effects are real**
   - Spies execute real code
   - Database calls, network requests, file I/O all happen
   - Use doReturn() to avoid unwanted side effects

## Best Practices

### DO:

✅ Use `doReturn().when()` instead of `when().thenReturn()` for spies
✅ Spy on concrete classes with real implementations
✅ Use spies for legacy code or partial mocking needs
✅ Verify both stubbed and real method calls
✅ Be aware of state sharing between spy and real object
✅ Use spies sparingly - prefer mocks for unit tests

### DON'T:

❌ Use `when().thenReturn()` on methods with side effects
❌ Spy on final classes (without special configuration)
❌ Over-use spies - they may indicate design issues
❌ Forget that spies execute real code (slower, side effects)
❌ Spy on null objects or primitives
❌ Ignore verification - track what methods are called

## Common Pitfalls

### 1. Using when() Instead of doReturn()

```java
// ✗ WRONG - calls real method first
when(spy.methodWithSideEffect()).thenReturn("value");

// ✓ CORRECT - stubs without calling
doReturn("value").when(spy).methodWithSideEffect();
```

### 2. Forgetting State is Shared

```java
Deal realDeal = new Deal("Test", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

// Both changed!
assertEquals("Modified", spyDeal.getTitle());
assertEquals("Modified", realDeal.getTitle());
```

### 3. Expecting Mock Behavior

```java
User spyUser = spy(new User("test", "test@test.com", "Test", "User"));

// ✗ WRONG - expects null like mock
assertNull(spyUser.getUsername());  // FAILS - returns "test"

// ✓ CORRECT - spy calls real method
assertEquals("test", spyUser.getUsername());
```

### 4. Spying on Final Classes

```java
// ✗ WRONG - String is final
String spy = spy("test");  // MockitoException

// ✓ CORRECT - spy on mutable class
StringBuilder spy = spy(new StringBuilder("test"));
```

## Real-World Example

Testing commission calculation with partial mocking:

```java
@Test
void testCommissionCalculation() {
    // Real objects
    Deal realDeal = new Deal("Enterprise", new BigDecimal("250000"), "USER-100");
    User realUser = new User("sjohnson", "sarah@example.com", "Sarah", "Johnson");
    CommissionPlan realPlan = new CommissionPlan("2024 Plan", Currency.getInstance("USD"));

    // Create spies
    Deal spyDeal = spy(realDeal);
    User spyUser = spy(realUser);
    CommissionPlan spyPlan = spy(realPlan);

    // Configure real objects
    spyDeal.setStatus(DealStatus.WON);
    spyUser.setActive(true);
    spyPlan.setStatus(PlanStatus.ACTIVE);

    // Stub only complex calculation
    doReturn(new BigDecimal("0.12")).when(spyPlan).getCommissionRate();

    // Test real business logic
    BigDecimal commission = spyDeal.getValue()          // Real
                                   .multiply(spyPlan.getCommissionRate());  // Stubbed

    // Verify workflow
    verify(spyDeal).getValue();
    verify(spyPlan).getCommissionRate();
    assertEquals(new BigDecimal("30000.00"), commission);
}
```

## Integration with JUnit 5

```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Spy
    private User spyUser = new User("test", "test@test.com", "Test", "User");

    @Test
    void testWithSpy() {
        // Spy is automatically initialized
        assertEquals("Test User", spyUser.getFullName());
    }
}
```

## Additional Resources

- [Mockito Spy Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#spy)
- [Mockito GitHub](https://github.com/mockito/mockito)
- [Spy vs Mock Discussion](https://stackoverflow.com/questions/15052984/what-is-the-difference-between-mocking-and-spying-when-using-mockito)
- [Effective Testing with Spies](https://www.baeldung.com/mockito-spy)

## Related Patterns

- **Mock** - Complete test double (see `../mock/`)
- **Test Fixtures** - Setting up test data
- **Partial Mocking** - Alternative approaches
- **Integration Testing** - Testing with real components

## See Also

- `QUESTIONS.md` - Test your understanding of spy concepts
- `ANSWERS.md` - Detailed explanations and solutions
- `spy-concepts.puml` - Visual diagram of spy patterns
- `../mock/` - Full mocking patterns
- `../../fundamentals/` - JUnit testing fundamentals