# Answers to JUnit Lifecycle and Testing Fundamentals Questions

## Lifecycle Questions

### 1. What is the purpose of the `@BeforeAll` annotation in JUnit 5?
The `@BeforeAll` annotation is used to mark a method that should be executed once before all test methods in the test class. It's useful for setup operations that are expensive and shared by all tests, such as connecting to a database or starting a server.

### 2. What is the difference between `@BeforeAll` and `@BeforeEach`?
`@BeforeAll` is executed once before all test methods in the class, while `@BeforeEach` is executed before each individual test method. `@BeforeAll` is used for shared setup that only needs to happen once, while `@BeforeEach` is used for setup that needs to be fresh for each test.

### 3. Why must methods annotated with `@BeforeAll` and `@AfterAll` be static?
These methods must be static because they are executed at the class level, before any instance of the test class is created. Since they run before any instance exists, they cannot access instance variables or methods.

### 4. In what order are JUnit lifecycle methods executed when running a test class?
The order is:
1. `@BeforeAll` methods
2. For each test:
   a. `@BeforeEach` methods
   b. `@Test` method
   c. `@AfterEach` methods
3. `@AfterAll` methods

### 5. What happens if an exception is thrown in a `@BeforeEach` method?
If an exception is thrown in a `@BeforeEach` method, the test method that would have run after it is marked as failed, and JUnit moves on to the next test method (after running `@AfterEach` methods if they exist). The exception is reported as the cause of the test failure.

### 6. How do you clean up resources after each test in JUnit 5?
You use the `@AfterEach` annotation to mark a method that should be executed after each test method. This method can be used to clean up resources, reset state, or perform any other cleanup operations needed after a test runs.

### 7. How do you clean up resources after all tests in JUnit 5?
You use the `@AfterAll` annotation to mark a static method that should be executed once after all test methods in the class have been run. This method can be used to clean up shared resources or perform final cleanup operations.

### 8. What is the main difference between setup and teardown methods in JUnit 5?
Setup methods (`@BeforeAll` and `@BeforeEach`) are used to prepare the test environment and initialize resources, while teardown methods (`@AfterEach` and `@AfterAll`) are used to clean up resources and reset state. Setup methods run before tests, while teardown methods run after tests.

### 9. When would you use `@BeforeAll` instead of `@BeforeEach`?
You would use `@BeforeAll` when you have setup operations that are expensive or time-consuming and can be shared by all tests, such as connecting to a database, starting a server, or loading a large dataset. `@BeforeEach` is more appropriate for creating fresh test data or resetting state for each individual test.

### 10. Can you have multiple `@BeforeEach` methods in a single test class?
Yes, you can have multiple `@BeforeEach` methods in a single test class. All methods annotated with `@BeforeEach` will be executed before each test method, in an order that is not guaranteed by JUnit. The same applies to `@AfterEach`, `@BeforeAll`, and `@AfterAll` methods.

## Assertions Questions

### 11. What is the purpose of assertions in JUnit tests?
Assertions are used to verify that the code being tested behaves as expected. They check that certain conditions are met during test execution, and if an assertion fails, the test is marked as failed. Assertions are the primary way to validate that your code works correctly.

### 12. What is the difference between `assertEquals` and `assertTrue`?
`assertEquals` compares two values for equality, while `assertTrue` checks if a condition is true. `assertEquals` is more specific and provides better error messages when the assertion fails, showing both the expected and actual values. `assertTrue` is more general and can be used for any boolean condition.

### 13. What is the purpose of the message parameter in assertion methods?
The message parameter provides a custom message that is displayed when the assertion fails. It helps clarify what went wrong and what was expected, making it easier to diagnose test failures. A good error message can save time when debugging test failures.

### 14. How can you test that a specific condition is true in JUnit 5?
You can use the `assertTrue` method to test that a specific condition is true. For example, you can check that a user has a specific role by calling `assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role")`. This checks that the condition is true, and if it's not, the test fails with the specified message.

### 15. How can you verify that a method was called with specific parameters?
In plain JUnit, you can't directly verify method calls. For this, you would typically use a mocking framework like Mockito. With Mockito, you can create a mock object, use it in your test, and then verify that certain methods were called with specific parameters. This is useful for testing interactions between components.

## Best Practices Questions

### 16. Why is it important to keep tests independent from each other?
Independent tests:
- Can be run in any order
- Don't depend on the state from other tests
- Are easier to debug when they fail
- Can be run in parallel
- Are more reliable and less prone to cascading failures

### 17. What are some strategies for dealing with shared test data?
Strategies include:
- Using `@BeforeEach` to create fresh data for each test
- Using builder patterns to create test data
- Using test fixtures or factory methods
- Using in-memory databases for integration tests
- Using mocks or stubs for external dependencies

### 18. How should you name your test methods to make them more readable?
Test methods should:
- Clearly describe what they're testing
- Include the expected behavior
- Include the scenario or context
- Use a consistent naming convention (e.g., `shouldDoSomethingWhenCondition` or `givenConditionWhenActionThenResult`)
- Be descriptive enough to understand the test's purpose without looking at the implementation

### 19. What is the Test Driven Development (TDD) approach and how does it relate to JUnit?
Test Driven Development is an approach where you:
1. Write a failing test for a feature that doesn't exist yet
2. Implement the minimum code needed to make the test pass
3. Refactor the code while keeping the tests passing

JUnit is a testing framework that facilitates TDD by providing the tools to write and run tests. It's commonly used in TDD workflows to verify that code behaves as expected.

### 20. How can you ensure that your tests are maintainable as your codebase evolves?
To ensure maintainable tests:
- Focus on testing behavior, not implementation details
- Use abstraction layers to isolate tests from implementation changes
- Keep tests simple and focused on a single responsibility
- Use helper methods and test utilities to reduce duplication
- Regularly refactor tests alongside production code
- Use meaningful names and comments to document test intent
- Follow the same code quality standards for tests as for production code