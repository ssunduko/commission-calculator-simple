# Answers to JUnit 5 Disabled Tests Questions

## Basic Concepts

1. **What is the purpose of the `@Disabled` annotation in JUnit 5?**
   
   The `@Disabled` annotation is used to disable test methods or entire test classes so they won't be executed during test runs. It allows you to temporarily or permanently exclude tests while keeping them in the codebase for documentation, future reference, or later re-enabling.

2. **What happens when a test method is annotated with `@Disabled`?**
   
   When a test method is annotated with `@Disabled`, the JUnit engine skips its execution entirely. The test appears in test reports as "disabled" or "skipped" rather than "passed" or "failed", and any reason provided is displayed in the test results.

3. **Can you disable an entire test class? If so, how?**
   
   Yes, you can disable an entire test class by placing the `@Disabled` annotation at the class level:
   ```java
   @Disabled("Entire class disabled during major refactoring")
   public class CommissionCalculatorTest {
       // All tests in this class will be disabled
   }
   ```

4. **What's the difference between a disabled test and a deleted test?**
   
   A disabled test remains in the codebase and appears in test reports (as skipped), maintaining test history and documentation. A deleted test is completely removed from the codebase and provides no future reference. Disabled tests can be easily re-enabled later.

5. **How do disabled tests appear in test execution reports?**
   
   Disabled tests appear as "skipped", "disabled", or "ignored" in test reports, typically with a different color or icon than passed/failed tests. The reason for disabling (if provided) is usually displayed alongside the test name.

## Usage and Syntax

6. **What are the two ways to use the `@Disabled` annotation?**
   
   - Without a reason: `@Disabled`
   - With a reason: `@Disabled("Reason for disabling the test")`
   
   The second approach is strongly recommended as it provides context for why the test was disabled.

7. **Why is it recommended to provide a reason when disabling a test?**
   
   Providing a reason helps team members understand why the test was disabled, when it might be re-enabled, and what conditions need to be met for re-enabling. It also helps prevent tests from being disabled indefinitely without clear justification.

8. **Can you combine `@Disabled` with other JUnit annotations like `@DisplayName`?**
   
   Yes, `@Disabled` can be combined with other JUnit annotations:
   ```java
   @Test
   @Disabled("Feature not yet implemented")
   @DisplayName("Calculate commission with performance bonuses")
   void testCalculateCommissionWithBonuses() {
       // Test implementation
   }
   ```

9. **What happens if you put `@Disabled` on a `@BeforeEach` or `@AfterEach` method?**
   
   `@Disabled` is not applicable to lifecycle methods like `@BeforeEach` or `@AfterEach`. These annotations are only meant for test methods and test classes. Lifecycle methods will still execute for non-disabled tests in the class.

10. **Is there a way to disable tests conditionally based on runtime conditions?**
    
    While `@Disabled` provides static disabling, you can use conditional execution annotations for dynamic disabling:
    - `@EnabledOnOs` / `@DisabledOnOs`
    - `@EnabledIfSystemProperty` / `@DisabledIfSystemProperty`
    - `@EnabledIfEnvironmentVariable` / `@DisabledIfEnvironmentVariable`
    - `@EnabledIf` / `@DisabledIf`

## Common Use Cases

11. **List five common scenarios where you would use `@Disabled` annotation.**
    
    - Temporarily disabling failing tests during bug fixes
    - Marking tests as not yet implemented during TDD
    - Disabling resource-intensive tests for regular CI runs
    - Disabling tests that depend on unavailable external services
    - Marking tests for deprecated functionality

12. **When should you disable a test versus deleting it entirely?**
    
    Disable when:
    - The test will be re-enabled later
    - You want to maintain test documentation
    - The functionality might return in the future
    - You're temporarily working around issues
    
    Delete when:
    - The functionality is permanently removed
    - The test is completely obsolete
    - There's no value in keeping it for reference

13. **How would you handle a test that fails intermittently due to timing issues?**
    
    ```java
    @Test
    @Disabled("Intermittent failure - investigating race condition - ticket #789")
    void testConcurrentProcessing() {
        // Disable while investigating and fixing the timing issue
    }
    ```

14. **What's the best approach for tests that require external services not available in all environments?**
    
    ```java
    @Test
    @Disabled("Requires external payment service - not available in test environment")
    void testProcessPayment() {
        // Disable for environments without external service access
    }
    ```
    
    Alternatively, use conditional execution based on environment variables.

15. **How should you handle tests for deprecated functionality that will be removed in the future?**
    
    ```java
    @Test
    @Disabled("Testing deprecated method - will be removed in v2.0")
    void testDeprecatedCalculation() {
        // Keep disabled until functionality is completely removed
    }
    ```

## Best Practices

16. **What information should you include in the reason parameter of `@Disabled`?**
    
    Include:
    - Why the test is disabled
    - When it might be re-enabled
    - Ticket numbers or references
    - Timeline information if applicable
    
    Example: `"Failing due to database issue - re-enable after infrastructure fix - ticket #123"`

17. **How can you track and manage disabled tests in a large codebase?**
    
    - Use consistent naming conventions for reasons
    - Include ticket numbers in disable reasons
    - Regular code reviews to check disabled tests
    - Automated reporting on disabled test counts
    - Periodic cleanup sessions to review disabled tests

18. **When should you consider re-enabling a disabled test?**
    
    - When the underlying issue is fixed
    - When external dependencies become available
    - When deprecated functionality is updated
    - During regular disabled test review sessions
    - When the feature being tested is implemented

19. **What's the difference between using `@Disabled` and conditional test execution annotations?**
    
    `@Disabled` provides static disabling - the test never runs. Conditional annotations provide dynamic disabling based on runtime conditions - the test may or may not run depending on the environment.

20. **How can you prevent disabled tests from being forgotten indefinitely?**
    
    - Include timeline information in disable reasons
    - Regular team reviews of disabled tests
    - Automated reports on long-disabled tests
    - Link disabled tests to tickets or issues
    - Set calendar reminders for review dates

## Commission Calculator Context

21. **In the commission calculator example, why might you disable a test for "AI-powered commission optimization"?**
    
    The AI feature might not yet be implemented, enabled for production, or available in the test environment. It could be disabled until the feature is fully developed and ready for testing.

22. **What would be a good reason to disable a test that calculates commissions for multiple currencies?**
    
    ```java
    @Disabled("Feature disabled for current client - may be re-enabled later")
    ```
    The current client might only use a single currency, making multi-currency tests irrelevant for the current deployment.

23. **How would you handle a test that requires database connectivity when the database is temporarily unavailable?**
    
    ```java
    @Disabled("Failing due to database connection issue - ticket #123")
    ```
    Disable temporarily while database issues are resolved, with a reference to the tracking ticket.

24. **Why might performance tests for processing large numbers of deals be disabled in regular CI runs?**
    
    Performance tests often take significant time and resources. They might be disabled for regular CI runs but enabled for nightly builds or specific performance testing sessions to avoid slowing down development workflows.

25. **What's the appropriate way to handle tests for features that are implemented but not yet enabled for production?**
    
    ```java
    @Disabled("Advanced feature not yet enabled for production")
    ```
    This indicates the feature exists but isn't active in the production environment yet.

## Advanced Topics

26. **Can you programmatically determine if a test is disabled at runtime?**
    
    Yes, using JUnit's reflection capabilities and the `TestInfo` parameter, you can check for the presence of the `@Disabled` annotation, though this is rarely needed in practice.

27. **How do disabled tests interact with test execution order annotations like `@TestMethodOrder`?**
    
    Disabled tests are completely skipped and don't participate in test ordering. They don't affect the execution order of enabled tests.

28. **What's the relationship between `@Disabled` and JUnit 5's dynamic tests?**
    
    `@Disabled` can be applied to methods that generate dynamic tests, disabling the entire dynamic test factory. Individual dynamic tests within a factory cannot be disabled using `@Disabled`.

29. **How can you use build tools (Maven/Gradle) to manage disabled tests?**
    
    Build tools can:
    - Generate reports on disabled tests
    - Exclude disabled tests from coverage calculations
    - Provide profiles that enable/disable certain test categories
    - Fail builds if too many tests are disabled

30. **What are some alternatives to `@Disabled` for managing tests that shouldn't run in certain conditions?**
    
    - Conditional execution annotations (`@EnabledIf`, `@DisabledIf`)
    - Test assumptions (`assumeTrue()`, `assumeFalse()`)
    - Test tags with selective execution
    - Separate test profiles or modules
    - Environment-specific test suites

## Integration and Tooling

31. **How do popular IDEs (IntelliJ IDEA, Eclipse, VS Code) display disabled tests?**
    
    IDEs typically show disabled tests with:
    - Special icons (often grayed out or with a "disabled" symbol)
    - Different text formatting (grayed out, strikethrough)
    - Tooltips showing the disable reason
    - Separate sections in test result views

32. **How can CI/CD pipelines report on disabled tests?**
    
    CI/CD systems can:
    - Include disabled test counts in build reports
    - Track trends in disabled tests over time
    - Alert when disabled test counts exceed thresholds
    - Generate reports on long-disabled tests
    - Fail builds if critical tests are disabled

33. **What metrics should you track regarding disabled tests in your project?**
    
    - Total number of disabled tests
    - Percentage of disabled tests vs. total tests
    - Duration tests have been disabled
    - Reasons for disabling (categorized)
    - Trends over time
    - Disabled tests per module/package

34. **How can you ensure that disabled tests are reviewed regularly?**
    
    - Schedule regular team reviews
    - Include disabled test review in sprint planning
    - Set up automated reminders for old disabled tests
    - Make disabled test review part of code review process
    - Track disabled tests in project management tools

35. **What's the impact of disabled tests on code coverage metrics?**
    
    Disabled tests typically don't contribute to code coverage metrics since they don't execute. This can artificially lower coverage percentages, so teams should account for this when setting coverage targets.

## Troubleshooting

36. **What should you do if a disabled test is accidentally re-enabled and starts failing?**
    
    - Check the disable reason to understand why it was disabled
    - Verify if the underlying issue is resolved
    - If not resolved, re-disable with updated reason
    - If resolved, fix any remaining test issues
    - Update documentation about the re-enabling

37. **How can you find all disabled tests in a large project?**
    
    - Use IDE search for `@Disabled` annotation
    - Use command-line tools like `grep` or `ripgrep`
    - Generate test reports that list disabled tests
    - Use static analysis tools
    - Create custom scripts to scan for disabled annotations

38. **What's the best way to communicate about disabled tests to team members?**
    
    - Include clear reasons in the annotation
    - Document in commit messages
    - Mention in code reviews
    - Track in issue management systems
    - Discuss in team meetings
    - Maintain a disabled tests log

39. **How should you handle disabled tests when merging code branches?**
    
    - Review disabled tests during merge reviews
    - Check if disable reasons are still valid
    - Resolve conflicts in disabled test reasons
    - Consider if branch-specific disables should be kept
    - Update disable reasons if context has changed

40. **What documentation should accompany disabled tests in a professional development environment?**
    
    - Clear reason for disabling
    - Timeline for re-enabling (if known)
    - Reference to related tickets/issues
    - Contact person or team responsible
    - Dependencies that need to be resolved
    - Impact assessment of the disabled functionality