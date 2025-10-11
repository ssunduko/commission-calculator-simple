# Mockito Spy Concepts - Questions

Test your understanding of Mockito Spy concepts with these questions. Answers are provided in `ANSWERS.md`.

---

## Section 1: Basic Concepts

### Question 1: What is a Spy?
What is a spy in Mockito, and how does it differ from a mock?

### Question 2: Real vs Empty
What happens when you call a method on a spy vs a mock without any stubbing?

### Question 3: State Sharing
If you create a spy from a real object, do they share the same state? What are the implications?

---

## Section 2: Creating Spies

### Question 4: Creation Methods
What are the three ways to create a spy in Mockito? Provide code examples.

### Question 5: Spy Requirements
Can you create a spy without a real object? Why or why not?

### Question 6: @Spy Annotation
What is required when using the `@Spy` annotation on a field?

---

## Section 3: Real Method Calls

### Question 7: Default Behavior
What is the default behavior when you call a method on a spy that hasn't been stubbed?

### Question 8: Real Method Execution
Write code that demonstrates a spy calling a real method and returning an actual computed value.

### Question 9: Side Effects
If a real method on a spy has side effects (like database writes), do those side effects occur when the method is called?

---

## Section 4: Selective Stubbing

### Question 10: Partial Mocking
What does "partial mocking" mean, and why are spies ideal for it?

### Question 11: Mixing Real and Stubbed
Can you have some methods on a spy return real values while others return stubbed values? How?

### Question 12: Void Method Stubbing
How do you prevent a void method on a spy from executing its real implementation?

---

## Section 5: doReturn() vs when()

### Question 13: Critical Difference
What is the critical difference between these two stubbing approaches for spies?
```java
when(spy.method()).thenReturn("value");
doReturn("value").when(spy).method();
```

### Question 14: Side Effect Risk
Why can `when().thenReturn()` be dangerous when used with spies?

### Question 15: Best Practice
Which stubbing approach should you use for spies and why?

---

## Section 6: Verification

### Question 16: Verifying Spies
Can you verify method calls on a spy? Can you verify both stubbed and real methods?

### Question 17: Argument Captors
Can you use ArgumentCaptors with spies? If so, do they capture arguments for both real and stubbed methods?

### Question 18: Verification Modes
Do verification modes (`times()`, `never()`, `atLeast()`) work with spies the same way as mocks?

---

## Section 7: Spy vs Mock

### Question 19: Performance
Which is typically faster: a mock or a spy? Why?

### Question 20: When to Use Each
In what scenarios would you choose a spy over a mock, and vice versa?

### Question 21: Isolation
Which provides better test isolation: mocks or spies? Explain.

---

## Section 8: State Sharing

### Question 22: Shared State Implications
What happens if you modify a spy's state through a setter? Does it affect the original real object?

### Question 23: State Sharing Example
Given this code, what does `realUser.isActive()` return?
```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spyUser = spy(realUser);
spyUser.setActive(true);
```

### Question 24: Independent State
Can you create a spy that doesn't share state with the original object?

---

## Section 9: Spy Limitations

### Question 25: Final Classes
Can you spy on final classes like `String`? What happens if you try?

### Question 26: Null Objects
What happens if you try to create a spy from a null object?

### Question 27: Primitives
Can you spy on primitive types like `int` or `boolean`?

### Question 28: Interfaces
Can you spy on an interface without providing a concrete implementation?

---

## Section 10: Collections

### Question 29: Spying on Lists
Can you spy on collection classes like `ArrayList`? What's a use case for this?

### Question 30: Collection Behavior
If you spy on an `ArrayList` and stub the `size()` method to return 10, what happens when you actually add elements to the list?

---

## Section 11: Advanced Topics

### Question 31: InOrder Verification
Can you use InOrder verification with spies? Does it verify the order of both real and stubbed method calls?

### Question 32: Multiple Spies
Can you create spies from multiple objects and verify their interaction order?

### Question 33: Spy Reset
What does `Mockito.reset(spy)` do? Does it affect the real object's state?

---

## Section 12: Use Cases

### Question 34: Legacy Code
Why are spies particularly useful for testing legacy code?

### Question 35: Abstract Classes
How can spies help test abstract classes with both concrete and abstract methods?

### Question 36: Partial Implementation
You have a class with 10 methods, and 9 work perfectly but 1 has issues. How can spies help?

---

## Section 13: Best Practices

### Question 37: Spy Overuse
Is it a good practice to use spies extensively throughout your test suite? Why or why not?

### Question 38: Design Smell
What might excessive use of spies in tests indicate about your code design?

### Question 39: Spy vs Refactor
When should you consider refactoring your code instead of using spies?

---

## Section 14: Real-World Scenarios

### Question 40: Database Testing
You have a service that makes database calls. You want to test business logic without hitting the database. Should you use a spy or a mock? Why?

### Question 41: Calculation Logic
You're testing a commission calculator that has complex calculation logic but also calls an external pricing service. How would you use spies?

### Question 42: Integration Testing
When might spies be more appropriate than mocks for integration testing?

---

## Section 15: Common Mistakes

### Question 43: Wrong Stubbing Syntax
What's wrong with this code?
```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));
when(spy.someMethodWithSideEffect()).thenReturn("value");
```

### Question 44: Expecting Mock Behavior
What's the bug in this test?
```java
@Test
void testSpy() {
    User spy = spy(new User("jdoe", "john@test.com", "John", "Doe"));
    assertNull(spy.getUsername());  // Test fails - why?
}
```

### Question 45: State Confusion
What's wrong with this assertion?
```java
Deal realDeal = new Deal("Original", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setTitle("Modified");

assertEquals("Original", realDeal.getTitle());  // Assertion fails - why?
```

---

## Section 16: Comparison Questions

### Question 46: Mock vs Spy Table
Fill in this comparison table:

| Aspect | Mock | Spy |
|--------|------|-----|
| Base | ? | ? |
| Default method behavior | ? | ? |
| State | ? | ? |
| Performance | ? | ? |
| Best for | ? | ? |

### Question 47: Stubbing Comparison
Compare these two stubbing approaches and explain when each is appropriate:
```java
// Approach A
when(spy.method()).thenReturn("value");

// Approach B
doReturn("value").when(spy).method();
```

---

## Section 17: Code Analysis

### Question 48: Predict the Output
What does this code print?
```java
User realUser = new User("test", "test@test.com", "Test", "User");
User spyUser = spy(realUser);

System.out.println(spyUser.getFullName());  // Line 1

doReturn("Stubbed Name").when(spyUser).getFullName();

System.out.println(spyUser.getFullName());  // Line 2
System.out.println(spyUser.getUsername()); // Line 3
```

### Question 49: State Changes
What are the final values?
```java
Deal realDeal = new Deal("Test", new BigDecimal("1000"), "USER-1");
Deal spyDeal = spy(realDeal);

spyDeal.setValue(new BigDecimal("2000"));
doReturn(new BigDecimal("5000")).when(spyDeal).getValue();

BigDecimal spyValue = spyDeal.getValue();    // What is this?
BigDecimal realValue = realDeal.getValue();  // What is this?
```

### Question 50: Verification Puzzle
Will these verifications pass or fail?
```java
User spy = spy(new User("test", "test@test.com", "Test", "User"));

spy.getUsername();
spy.getEmail();

doReturn("stubbed@test.com").when(spy).getEmail();

spy.getEmail();
spy.getEmail();

verify(spy, times(1)).getUsername();  // Pass or fail?
verify(spy, times(3)).getEmail();     // Pass or fail?
```

---

## Challenge Problems

### Challenge 1: Complex Scenario
You're testing a `CommissionCalculator` class that:
1. Fetches deal data (real method you want to test)
2. Validates deal status (real method you want to test)
3. Calls an external pricing API (you want to stub this)
4. Calculates commission (real method you want to test)
5. Saves to database (you want to stub this)

Design a test using spies that tests the real business logic while stubbing external dependencies.

### Challenge 2: State Machine
Test a `Deal` object that transitions through states: OPEN → WON → CLOSED.
Use spies to verify:
- Real state transitions work
- State change callbacks are invoked
- Invalid transitions are rejected
- Can stub specific transition validations

### Challenge 3: Legacy Refactoring
You have a legacy `UserService` with 15 methods. 12 work perfectly, but 3 make slow database calls. You need to test the working methods without hitting the database. Design a testing strategy using spies.

### Challenge 4: Spy Chaining
Create a test with multiple spies (Deal, User, CommissionPlan) where:
- Deal delegates to User for validation
- Deal uses CommissionPlan for calculations
- Most methods should use real implementations
- Only specific integration points are stubbed
- Verify the entire call chain works correctly

### Challenge 5: Collection Spy
Spy on an ArrayList to:
- Track all add() calls
- Let most operations be real
- Stub size() to return a custom value
- Verify the collection behavior
- Understand the implications of stubbing collection methods

---

*Check `ANSWERS.md` for detailed explanations and solutions!*