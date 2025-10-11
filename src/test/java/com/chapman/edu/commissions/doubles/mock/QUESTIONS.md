# Mockito Mocking Concepts - Questions

Test your understanding of Mockito mocking concepts with these questions. Answers are provided in `ANSWERS.md`.

---

## Section 1: Basic Concepts

### Question 1: What is Mocking?
What is a mock object, and why would you use one in unit testing?

### Question 2: Mock vs Real Object
What are the key differences between a mock object and a real object? What does a mock return by default?

### Question 3: When to Use Mocks
In what scenarios should you use mocks? When should you avoid using them?

---

## Section 2: Creating Mocks

### Question 4: Mock Creation Methods
What are three different ways to create a mock object in Mockito?

### Question 5: @Mock Annotation
What is required to use the `@Mock` annotation? What must you do in your test setup?

### Question 6: Mock Initialization
What happens if you forget to call `MockitoAnnotations.openMocks(this)` in your `@BeforeEach` method?

---

## Section 3: Stubbing

### Question 7: Basic Stubbing
What does stubbing mean in Mockito? Write the syntax for stubbing a method to return a specific value.

### Question 8: Multiple Return Values
How can you stub a mock method to return different values on successive calls?

### Question 9: Stubbing void Methods
Why can't you use `when().thenReturn()` syntax for void methods? What should you use instead?

### Question 10: Default Behavior
What does an unstubbed mock method return by default for:
- Reference types (String, BigDecimal)
- Primitive types (int, boolean)
- Collections (List, Set)

---

## Section 4: Verification

### Question 11: Basic Verification
What is the purpose of verification in Mockito? Write the syntax to verify a method was called.

### Question 12: Verification Modes
Explain the difference between:
- `verify(mock).method()`
- `verify(mock, times(3)).method()`
- `verify(mock, never()).method()`
- `verify(mock, atLeast(2)).method()`

### Question 13: Verification vs Stubbing
What's the difference between stubbing and verification? Can you use them together in the same test?

---

## Section 5: Argument Matchers

### Question 14: Why Use Matchers?
Why would you use argument matchers like `any()` instead of specific values in stubbing or verification?

### Question 15: Common Matchers
What's the difference between:
- `any()`
- `anyString()`
- `eq(value)`
- `isNull()` / `isNotNull()`

### Question 16: Matcher Rules
What important rule must you follow when mixing argument matchers with specific values? What happens if you break this rule?

---

## Section 6: Argument Captors

### Question 17: Purpose of Captors
What problem do Argument Captors solve that regular verification doesn't?

### Question 18: Captor Usage
Write the code to capture an argument passed to a method and verify its value.

### Question 19: Multiple Captures
If a method is called 3 times, how do you capture all the arguments passed in each call?

---

## Section 7: Spy Objects

### Question 20: Mock vs Spy
What's the fundamental difference between a mock and a spy?

### Question 21: When to Use Spies
In what scenarios would you use a spy instead of a mock?

### Question 22: Spy Behavior
If you create a spy of a real object and call a method without stubbing it, what happens?

### Question 23: Stubbing Spies
What's the difference between these two approaches when stubbing a spy?
```java
when(spy.method()).thenReturn(value);
doReturn(value).when(spy).method();
```

---

## Section 8: Exception Handling

### Question 24: Throwing Exceptions
How do you stub a mock method to throw an exception? Show syntax for both regular and void methods.

### Question 25: Testing Error Handling
Why is mocking particularly useful for testing exception handling code?

---

## Section 9: Custom Answers

### Question 26: What is an Answer?
What is a custom Answer in Mockito, and when would you use one?

### Question 27: Answer Implementation
How do you access method arguments inside a custom Answer?

### Question 28: Answer vs thenReturn
When should you use `thenAnswer()` instead of `thenReturn()`?

---

## Section 10: InOrder Verification

### Question 29: Why InOrder?
Why would you need to verify the order of method calls? Give a real-world example.

### Question 30: InOrder Syntax
Write the code to verify that methods were called in a specific sequence.

### Question 31: Multiple Mocks
Can you use InOrder verification across multiple mock objects? How?

---

## Section 11: BDD Style

### Question 32: BDD Mocking
What does BDD stand for? How does BDD style differ from traditional Mockito syntax?

### Question 33: Given-When-Then
Map these traditional Mockito methods to their BDD equivalents:
- `when().thenReturn()`
- `verify()`

### Question 34: BDD Benefits
What are the benefits of using BDD style testing?

---

## Section 12: Advanced Topics

### Question 35: Mock Reset
What does `Mockito.reset(mock)` do? When should you use it (and when shouldn't you)?

### Question 36: Verification Timeout
What is `verify(mock, timeout(millis)).method()` used for? In what scenarios is it helpful?

### Question 37: Final Classes
Can Mockito mock final classes and final methods? What limitations exist?

### Question 38: Partial Mocking
Besides spies, are there other ways to partially mock an object?

---

## Section 13: Best Practices

### Question 39: What to Mock
Should you mock:
- The class under test?
- Simple value objects (POJOs)?
- External dependencies (databases, APIs)?
- Complex collaborators?

Explain your answers.

### Question 40: Over-Mocking
What is over-mocking, and why is it a problem? How can you avoid it?

### Question 41: Brittle Tests
How can excessive use of verification lead to brittle tests? What's the balance?

### Question 42: Mock Naming
What naming conventions should you follow for mock objects in tests?

---

## Section 14: Real-World Scenarios

### Question 43: Database Testing
You have a service that depends on a database repository. How would you use Mockito to test the service without a real database?

### Question 44: Chain of Calls
How do you mock a chain of method calls like `object.getA().getB().getC()`?

### Question 45: State Verification vs Interaction Verification
What's the difference between:
- State verification (checking return values)
- Interaction verification (checking method calls)

When should you use each approach?

### Question 46: Complex Scenario
You're testing a commission calculation service that depends on:
- A Deal repository (to fetch deals)
- A User repository (to fetch sales rep info)
- A CommissionPlan repository (to fetch calculation rules)

Design a test using mocks to verify the service correctly calculates commission for a won deal.

---

## Section 15: Troubleshooting

### Question 47: NullPointerException
You get a NPE when calling a method on a mock. What are the likely causes?

### Question 48: Verification Failure
Your test fails with "Wanted but not invoked". What does this mean, and how do you debug it?

### Question 49: Stubbing Not Working
You stubbed a method, but the mock returns null instead of your stubbed value. What could be wrong?

### Question 50: Unnecessary Stubbing
What does the warning "Unnecessary stubbings detected" mean, and how do you fix it?

---

## Bonus Questions

### Question 51: Mockito vs Other Frameworks
How does Mockito compare to other mocking frameworks like EasyMock or JMockit?

### Question 52: Mockito Internals
How does Mockito create mocks at runtime? What Java features does it use?

### Question 53: Performance
Do mocks have any performance implications? Are there scenarios where mocks could slow down tests?

### Question 54: Integration with Frameworks
How does Mockito integrate with:
- JUnit 5
- Spring Framework
- Dependency Injection containers

### Question 55: Future of Mocking
With modern Java features (records, sealed classes, etc.), how might mocking evolve? What challenges do new language features present?

---

## Challenge Problems

### Challenge 1: Complex Mock Chain
Create a test that mocks a `CommissionCalculator` that:
1. Fetches a deal from a repository
2. Validates the deal is in WON status
3. Fetches the sales rep from a user repository
4. Validates the sales rep is active
5. Fetches an active commission plan
6. Calculates commission based on the plan
7. Saves the calculation result

Use appropriate stubbing, verification, and InOrder verification.

### Challenge 2: Error Cascading
Test a service that makes three external API calls. If any call fails, the service should:
1. Log the error
2. Attempt a fallback operation
3. Return a default value

Mock the dependencies and verify all error handling paths.

### Challenge 3: State Machine Testing
Test a Deal object that transitions through states:
OPEN → NEGOTIATION → PROPOSAL → WON/LOST

Use spies and InOrder verification to ensure:
- Transitions happen in correct order
- Invalid transitions are rejected
- State change callbacks are invoked

---

*Check `ANSWERS.md` for detailed explanations and solutions!*