# Mockito Mocking Patterns

## Overview

This module demonstrates comprehensive Mockito mocking capabilities using the Commission Calculator domain model. Mockito is the most popular mocking framework for Java, enabling developers to create test doubles that simulate the behavior of real objects in controlled ways.

## What is Mocking?

**Mocking** is a testing technique where you create fake objects (mocks) that simulate the behavior of real objects. This allows you to:

1. **Isolate the code under test** - Test a single unit without dependencies
2. **Control behavior** - Define exactly what methods return
3. **Verify interactions** - Ensure code calls the right methods with correct arguments
4. **Test edge cases** - Simulate errors and exceptional scenarios
5. **Speed up tests** - Avoid slow operations like database calls or network requests

## Why Use Mockito?

### Benefits

- **Simple and intuitive API** - Easy to learn and use
- **Type-safe** - Compile-time checking of method calls
- **Flexible** - Supports various stubbing and verification patterns
- **Well-documented** - Extensive documentation and community support
- **Industry standard** - Most widely used mocking framework in Java

### When to Use Mocks

✅ **Good Use Cases:**
- Testing business logic that depends on external services
- Verifying interactions between objects
- Testing error handling without actual errors
- Isolating unit tests from slow operations
- Testing code that depends on unfinished components

❌ **Avoid When:**
- Testing simple POJOs with no dependencies
- Integration testing (use real objects instead)
- Over-mocking leads to brittle tests

## Mockito Concepts Demonstrated

### 1. Basic Mock Creation

```java
@Mock
private Deal mockDeal;

// Or programmatically
Deal mockDeal = mock(Deal.class);
```

Mocks are empty shells that return default values (null, 0, false) unless stubbed.

### 2. Stubbing

**Define what mocks should return:**

```java
when(mockDeal.getValue()).thenReturn(new BigDecimal("100000"));
```

Stubbing allows you to control the behavior of dependencies to test specific scenarios.

### 3. Verification

**Ensure methods were called:**

```java
verify(mockDeal).setStatus(DealStatus.WON);
verify(mockDeal, times(3)).getValue();
verify(mockDeal, never()).setStatus(DealStatus.LOST);
```

Verification confirms your code interacts correctly with dependencies.

### 4. Argument Matchers

**Flexible argument matching:**

```java
when(mockUser.hasRole(any(UserRole.class))).thenReturn(true);
verify(mockDeal).setValue(anyBigDecimal());
```

Useful when exact argument values don't matter or you want to match patterns.

### 5. Argument Captors

**Capture arguments for detailed inspection:**

```java
ArgumentCaptor<BigDecimal> captor = ArgumentCaptor.forClass(BigDecimal.class);
verify(mockDeal).setValue(captor.capture());
assertEquals(new BigDecimal("50000"), captor.getValue());
```

Perfect for verifying complex argument values or state.

### 6. Spy Objects

**Partial mocking of real objects:**

```java
User realUser = new User("jsmith", "john@example.com", "John", "Smith");
User spyUser = spy(realUser);

// Real methods work unless stubbed
assertEquals("John Smith", spyUser.getFullName());

// Override specific methods
when(spyUser.getEmail()).thenReturn("fake@example.com");
```

Spies call real methods by default but allow selective stubbing.

### 7. Exception Handling

**Test error scenarios:**

```java
when(mockDeal.calculateTotalValue())
    .thenThrow(new IllegalStateException("Error"));

doThrow(new IllegalArgumentException())
    .when(mockDeal).setStatus(DealStatus.INVALID);
```

Essential for testing error handling logic.

### 8. Custom Answers

**Dynamic behavior based on arguments:**

```java
when(mockUser.hasRole(any(UserRole.class))).thenAnswer(invocation -> {
    UserRole role = invocation.getArgument(0);
    return role == UserRole.SALES_REP;
});
```

Useful when return values depend on input arguments.

### 9. InOrder Verification

**Verify method call sequence:**

```java
InOrder inOrder = inOrder(mockDeal);
inOrder.verify(mockDeal).setStatus(DealStatus.OPEN);
inOrder.verify(mockDeal).setStatus(DealStatus.WON);
```

Critical for state machines and workflows where order matters.

### 10. BDD Style

**Behavior-Driven Development syntax:**

```java
// Given
given(mockUser.getFullName()).willReturn("Jane Doe");

// When
String name = mockUser.getFullName();

// Then
then(mockUser).should().getFullName();
```

Makes tests read more like specifications.

## Test Structure

### MockitoMockTest.java

The test class demonstrates all major Mockito features organized into sections:

1. **Basic Mock Creation** - Using @Mock annotation and mock() method
2. **Basic Stubbing** - Defining return values
3. **Verification** - Checking method calls
4. **Argument Matchers** - Flexible argument matching
5. **Argument Captors** - Capturing and inspecting arguments
6. **Spy Objects** - Partial mocking
7. **Exception Handling** - Testing error scenarios
8. **Custom Answers** - Dynamic mock behavior
9. **InOrder Verification** - Method call sequencing
10. **BDD Style** - Given-When-Then syntax
11. **Complex Scenarios** - Real-world integration examples
12. **Mock Reset** - Cleaning mock state
13. **Default Return Values** - Understanding mock defaults
14. **Verification Timeout** - Async testing support

## Running the Tests

```bash
# Run all mock tests
mvn test -Dtest=MockitoMockTest

# Run specific test
mvn test -Dtest=MockitoMockTest#testBasicStubbing

# Run with coverage
mvn clean test jacoco:report
```

## Domain Model Usage

This implementation uses the following domain models from `com.chapman.edu.commissions.model`:

- **Deal** - Sales deals with status lifecycle
- **User** - System users with roles and permissions
- **CommissionPlan** - Commission calculation rules
- **DealStatus** - Enum for deal states
- **UserRole** - Enum for user roles
- **PlanStatus** - Enum for plan states

These real domain objects demonstrate how mocking works with actual business entities.

## Best Practices

### DO:

✅ Use mocks to isolate unit tests from dependencies
✅ Stub only what you need for the test
✅ Verify important interactions
✅ Use descriptive test names that explain the scenario
✅ Keep tests focused on one behavior
✅ Use argument matchers when exact values don't matter

### DON'T:

❌ Mock everything (mock dependencies, not the class under test)
❌ Over-specify interactions (only verify what matters)
❌ Use mocks for integration tests
❌ Create brittle tests that break with implementation changes
❌ Mix stubbing syntax (when/thenReturn vs doReturn/when)
❌ Ignore verification - it's a key feature of mocking

## Common Pitfalls

### 1. Stubbing Final Classes/Methods

Mockito cannot mock final classes by default. Use Mockito's inline mock maker or refactor code.

### 2. NPE on Unstubbed Methods

Mocks return null for reference types by default. Always stub methods you'll call.

### 3. Mixing Matcher and Non-Matcher Arguments

```java
// WRONG
verify(mockDeal).setValue(eq(new BigDecimal("100")));

// CORRECT - use matchers for ALL arguments
verify(mockDeal).setValue(eq(new BigDecimal("100")));
```

### 4. Over-Mocking

Don't mock simple value objects. Mock external dependencies and complex collaborators.

## Integration with JUnit 5

```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private Dependency mockDependency;

    @InjectMocks
    private ClassUnderTest classUnderTest;

    @Test
    void testSomething() {
        // Mocks are automatically initialized
    }
}
```

## Additional Resources

- [Mockito Official Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Mockito GitHub](https://github.com/mockito/mockito)
- [Baeldung Mockito Guide](https://www.baeldung.com/mockito-series)
- [Effective Testing with Mockito](https://www.toptal.com/java/a-guide-to-everyday-mockito)

## Related Patterns

- **Test Doubles** - Mocks are one type of test double (also: stubs, fakes, spies, dummies)
- **Dependency Injection** - Makes mocking easier by injecting dependencies
- **Builder Pattern** - Often used with test data setup
- **AAA Pattern** - Arrange-Act-Assert test structure used throughout

## See Also

- `QUESTIONS.md` - Test your understanding of mocking concepts
- `ANSWERS.md` - Detailed explanations of mocking questions
- `mockito-concepts.puml` - Visual diagram of mocking patterns
- `../builder/` - Builder pattern for test data setup
- `../fixture/` - Test fixture patterns