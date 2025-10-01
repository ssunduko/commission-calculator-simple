# Questions about JUnit Lifecycle and Testing Fundamentals

## Lifecycle Questions

1. What is the purpose of the `@BeforeAll` annotation in JUnit 5?
2. What is the difference between `@BeforeAll` and `@BeforeEach`?
3. Why must methods annotated with `@BeforeAll` and `@AfterAll` be static?
4. In what order are JUnit lifecycle methods executed when running a test class?
5. What happens if an exception is thrown in a `@BeforeEach` method?
6. How do you clean up resources after each test in JUnit 5?
7. How do you clean up resources after all tests in JUnit 5?
8. What is the main difference between setup and teardown methods in JUnit 5?
9. When would you use `@BeforeAll` instead of `@BeforeEach`?
10. Can you have multiple `@BeforeEach` methods in a single test class?

## Assertions Questions

11. What is the purpose of assertions in JUnit tests?
12. What is the difference between `assertEquals` and `assertTrue`?
13. What is the purpose of the message parameter in assertion methods?
14. How can you test that a specific condition is true in JUnit 5?
15. How can you verify that a method was called with specific parameters?

## Best Practices Questions

16. Why is it important to keep tests independent from each other?
17. What are some strategies for dealing with shared test data?
18. How should you name your test methods to make them more readable?
19. What is the Test Driven Development (TDD) approach and how does it relate to JUnit?
20. How can you ensure that your tests are maintainable as your codebase evolves?
