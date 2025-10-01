# Questions about JUnit 5 Disabled Tests

## Basic Concepts

1. **What is the purpose of the `@Disabled` annotation in JUnit 5?**

2. **What happens when a test method is annotated with `@Disabled`?**

3. **Can you disable an entire test class? If so, how?**

4. **What's the difference between a disabled test and a deleted test?**

5. **How do disabled tests appear in test execution reports?**

## Usage and Syntax

6. **What are the two ways to use the `@Disabled` annotation?**

7. **Why is it recommended to provide a reason when disabling a test?**

8. **Can you combine `@Disabled` with other JUnit annotations like `@DisplayName`?**

9. **What happens if you put `@Disabled` on a `@BeforeEach` or `@AfterEach` method?**

10. **Is there a way to disable tests conditionally based on runtime conditions?**

## Common Use Cases

11. **List five common scenarios where you would use `@Disabled` annotation.**

12. **When should you disable a test versus deleting it entirely?**

13. **How would you handle a test that fails intermittently due to timing issues?**

14. **What's the best approach for tests that require external services not available in all environments?**

15. **How should you handle tests for deprecated functionality that will be removed in the future?**

## Best Practices

16. **What information should you include in the reason parameter of `@Disabled`?**

17. **How can you track and manage disabled tests in a large codebase?**

18. **When should you consider re-enabling a disabled test?**

19. **What's the difference between using `@Disabled` and conditional test execution annotations?**

20. **How can you prevent disabled tests from being forgotten indefinitely?**

## Commission Calculator Context

21. **In the commission calculator example, why might you disable a test for "AI-powered commission optimization"?**

22. **What would be a good reason to disable a test that calculates commissions for multiple currencies?**

23. **How would you handle a test that requires database connectivity when the database is temporarily unavailable?**

24. **Why might performance tests for processing large numbers of deals be disabled in regular CI runs?**

25. **What's the appropriate way to handle tests for features that are implemented but not yet enabled for production?**

## Advanced Topics

26. **Can you programmatically determine if a test is disabled at runtime?**

27. **How do disabled tests interact with test execution order annotations like `@TestMethodOrder`?**

28. **What's the relationship between `@Disabled` and JUnit 5's dynamic tests?**

29. **How can you use build tools (Maven/Gradle) to manage disabled tests?**

30. **What are some alternatives to `@Disabled` for managing tests that shouldn't run in certain conditions?**

## Integration and Tooling

31. **How do popular IDEs (IntelliJ IDEA, Eclipse, VS Code) display disabled tests?**

32. **How can CI/CD pipelines report on disabled tests?**

33. **What metrics should you track regarding disabled tests in your project?**

34. **How can you ensure that disabled tests are reviewed regularly?**

35. **What's the impact of disabled tests on code coverage metrics?**

## Troubleshooting

36. **What should you do if a disabled test is accidentally re-enabled and starts failing?**

37. **How can you find all disabled tests in a large project?**

38. **What's the best way to communicate about disabled tests to team members?**

39. **How should you handle disabled tests when merging code branches?**

40. **What documentation should accompany disabled tests in a professional development environment?**