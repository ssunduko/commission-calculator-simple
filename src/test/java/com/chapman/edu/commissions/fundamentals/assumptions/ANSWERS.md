# Answers to Questions about JUnit Assumptions

## 1. What is the main difference between JUnit assertions and JUnit assumptions?

The main difference is in their purpose and behavior:

- **Assertions** verify that your code behaves as expected. When an assertion fails, the test fails, indicating that there's a problem with the code being tested.
- **Assumptions** check if the test should run at all. When an assumption fails, the test is skipped (not failed), indicating that the test wasn't applicable in the current environment or conditions.

In short: assertions verify correctness, while assumptions verify preconditions for running the test.

## 2. When would you use `assumeTrue()` versus `assumeFalse()` in your tests?

- Use `assumeTrue(condition)` when you want the test to run only if a condition is true. For example, `assumeTrue(user.hasRole(UserRole.SALES_REP))` will run the test only if the user has the SALES_REP role.
- Use `assumeFalse(condition)` when you want the test to run only if a condition is false. For example, `assumeFalse(deal.getStatus() == DealStatus.WON)` will run the test only if the deal is not in the WON status.

The choice depends on how you naturally express the precondition. Use whichever makes your test more readable and intuitive.

## 3. How does `assumingThat()` differ from both `assumeTrue()` and `assumeFalse()`?

`assumingThat()` is fundamentally different from the other two methods:

- `assumeTrue()` and `assumeFalse()` will skip the entire test if the assumption fails.
- `assumingThat()` only conditionally executes a block of code if the assumption is true, but the test continues regardless of whether the assumption passes or fails.

This makes `assumingThat()` useful for optional test logic that should only run under certain conditions, without affecting the rest of the test.

## 4. In what scenarios would JUnit assumptions be more appropriate than assertions?

Assumptions are more appropriate when:

1. The test depends on external conditions that may not always be present (e.g., a specific environment, OS, or configuration)
2. You want to conditionally run tests based on the environment (e.g., only in development, not in CI)
3. You need to skip tests that aren't applicable in certain scenarios (e.g., tests for premium features when testing the free version)
4. You're testing functionality that depends on specific preconditions being met (e.g., a user having certain permissions)

Use assertions for verifying the correctness of your code, and assumptions for controlling when tests should run.

## 5. Can you use multiple assumptions in a single test method? If so, how do they interact with each other?

Yes, you can use multiple assumptions in a single test method. They interact in a sequential manner:

- Each assumption is evaluated in order
- If any assumption fails, the test is immediately skipped, and subsequent assumptions are not evaluated
- All assumptions must pass for the test to run

This creates an "AND" relationship between assumptions. For example:

```java
assumeTrue(user.hasRole(UserRole.SALES_REP));
assumeTrue(deal.getStatus() == DealStatus.OPEN);
```

The test will only run if the user is a sales rep AND the deal is open.

## 6. What happens when an assumption fails in a test method? How does this differ from when an assertion fails?

When an assumption fails:
- The test is marked as "skipped" (not failed)
- The rest of the test method is not executed
- JUnit reports the test as skipped with the provided message
- The test suite continues running other tests

When an assertion fails:
- The test is marked as "failed"
- An AssertionError is thrown
- The test execution stops at the point of failure
- The test suite continues running other tests

This difference is important because skipped tests don't indicate problems with your code, while failed tests do.

## 7. How would you use assumptions to handle environment-specific tests (e.g., tests that should only run in development but not production)?

You can use environment variables or system properties to determine the current environment and then use assumptions to conditionally run tests:

```java
@Test
public void testDevelopmentOnlyFeature() {
    String env = System.getProperty("environment", "dev");
    assumeTrue("dev".equals(env), "Skipping test because it should only run in development");
    
    // Test development-only features here
}

@Test
public void testProductionOnlyFeature() {
    String env = System.getProperty("environment", "dev");
    assumeTrue("prod".equals(env), "Skipping test because it should only run in production");
    
    // Test production-only features here
}
```

This approach allows you to maintain a single test suite that adapts to different environments.

## 8. Can assumptions be used in `@BeforeEach` or `@BeforeAll` methods? What would be the effect?

Yes, assumptions can be used in `@BeforeEach` or `@BeforeAll` methods, but with different effects:

- In a `@BeforeEach` method: If an assumption fails, the current test is skipped, but other tests will still run with their own `@BeforeEach` calls.
- In a `@BeforeAll` method: If an assumption fails, all tests in the class are skipped because the setup couldn't be completed.

Using assumptions in setup methods can be useful for skipping entire test classes when preconditions aren't met, but use with caution as it can make test behavior less obvious.

## 9. How do JUnit assumptions help with test reliability and maintainability?

JUnit assumptions improve test reliability and maintainability by:

1. **Reducing flaky tests**: Tests don't fail due to environmental factors or preconditions not being met
2. **Clarifying test intent**: Explicitly stating when tests should or shouldn't run
3. **Simplifying test logic**: No need for complex conditional logic within tests
4. **Improving test reports**: Clearly distinguishing between "failed" tests (code issues) and "skipped" tests (environmental issues)
5. **Enabling environment-specific testing**: Running different tests in different environments without duplicating code
6. **Documenting dependencies**: Making external dependencies explicit in the test code

This leads to a more robust test suite that's easier to understand and maintain.

## 10. What are some real-world examples where assumptions would be particularly useful in testing a commission calculator application?

In a commission calculator application, assumptions would be useful for:

1. **Role-specific tests**: `assumeTrue(user.hasRole(UserRole.SALES_REP))` for tests that only apply to sales reps
2. **Deal status tests**: `assumeTrue(deal.getStatus() == DealStatus.WON)` for tests that require closed deals
3. **Plan status tests**: `assumeTrue(plan.getStatus() == PlanStatus.ACTIVE)` for tests that require active commission plans
4. **Date-dependent tests**: `assumeTrue(plan.isActiveOn(LocalDate.now()))` for tests that depend on current date
5. **Currency-specific tests**: `assumeTrue("USD".equals(plan.getCurrency().getCurrencyCode()))` for tests that are specific to certain currencies
6. **Feature flag tests**: `assumeTrue(featureFlags.isEnabled("accelerators"))` for tests of features that might be toggled
7. **Database-dependent tests**: `assumeTrue(databaseIsAvailable())` for tests that require database access

These assumptions make tests more robust against changes in the testing environment or data.

## 11. How do assumptions interact with test suites and test runners? Are skipped tests reported differently than failed tests?

Assumptions interact with test suites and runners as follows:

- **Test runners**: Most runners (JUnit Platform, Maven Surefire, Gradle, IDEs) recognize and properly handle skipped tests
- **Test reports**: Skipped tests are typically reported separately from failed tests in reports
- **CI systems**: Most CI systems distinguish between failed and skipped tests in their reporting
- **Test suites**: A skipped test in a suite doesn't affect other tests in the suite

Skipped tests are reported differently than failed tests:
- Failed tests are highlighted as errors that need attention
- Skipped tests are typically shown as informational items, often with the assumption message explaining why they were skipped

This distinction helps teams focus on actual failures rather than tests that were appropriately skipped.

## 12. Is there a way to programmatically check how many tests were skipped due to failed assumptions?

Yes, you can programmatically check skipped tests using JUnit's listener mechanisms:

1. **TestExecutionListener**: Implement and register a TestExecutionListener that counts skipped tests
2. **Custom Runner**: Create a custom test runner that tracks skipped tests
3. **JUnit Platform Reporting**: Use the JUnit Platform Reporting features to generate reports with skip counts
4. **Build Tool Integration**: Both Maven and Gradle provide test result data including skip counts

Example with a simple listener:

```java
public class AssumptionCounterExtension implements TestExecutionListener {
    private int skippedCount = 0;
    
    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        skippedCount++;
    }
    
    public int getSkippedCount() {
        return skippedCount;
    }
}
```

## 13. How do JUnit assumptions compare to similar features in other testing frameworks?

JUnit assumptions are similar to features in other testing frameworks:

- **TestNG**: Has similar `skipIf` conditions and configuration methods
- **NUnit**: Provides the `Assume` class with similar functionality
- **pytest**: Uses decorators like `@pytest.mark.skipif` for conditional test execution
- **RSpec**: Has `skip` and `pending` methods for similar purposes
- **Spock**: Provides `IgnoreIf` and `Requires` annotations

JUnit's approach is generally more flexible and programmatic than some other frameworks, allowing for dynamic decisions about test execution rather than just static annotations.

The core concept is the same across frameworks: provide a way to conditionally skip tests without marking them as failures.

## 14. Can assumptions be combined with other JUnit features like parameterized tests or dynamic tests?

Yes, assumptions work well with other JUnit features:

**With Parameterized Tests**:
```java
@ParameterizedTest
@ValueSource(strings = {"USD", "EUR", "GBP"})
void testCurrencySpecificLogic(String currencyCode) {
    Currency currency = Currency.getInstance(currencyCode);
    plan.setCurrency(currency);
    
    // Skip test for certain currencies
    assumeTrue(!"JPY".equals(currencyCode), "Skipping JPY tests");
    
    // Test logic here
}
```

**With Dynamic Tests**:
```java
@TestFactory
Stream<DynamicTest> dynamicTestsWithAssumptions() {
    return Stream.of("USD", "EUR", "GBP")
        .map(code -> DynamicTest.dynamicTest("Test for " + code, () -> {
            assumeTrue(supportedCurrencies.contains(code));
            // Test logic here
        }));
}
```

This combination provides powerful ways to create flexible, conditional test suites.

## 15. What are some best practices for using assumptions in your test suite?

Best practices for using assumptions:

1. **Be explicit about why tests are skipped**: Always include a descriptive message with assumptions
2. **Use assumptions for external factors only**: Don't use assumptions to hide bugs in your code
3. **Keep assumptions at the beginning of tests**: Place assumptions before any test logic for clarity
4. **Don't overuse assumptions**: If most of your tests are being skipped, reconsider your test design
5. **Document environment requirements**: Make it clear what conditions are needed for all tests to run
6. **Consider custom annotations**: For common assumptions, create custom annotations to improve readability
7. **Use assumingThat() for partial skips**: When only part of a test depends on a condition
8. **Combine with @Disabled when appropriate**: Use @Disabled for tests that are temporarily broken
9. **Monitor skipped tests**: Regularly review which tests are being skipped and why
10. **Test your assumptions**: Ensure your assumptions themselves are correct

Following these practices will help you create a more maintainable and reliable test suite.