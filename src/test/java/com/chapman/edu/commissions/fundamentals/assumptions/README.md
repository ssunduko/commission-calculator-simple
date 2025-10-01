# JUnit Assumptions

This directory contains examples demonstrating the use of JUnit 5 assumptions with the commission calculator model classes.

## What are JUnit Assumptions?

Assumptions in JUnit are used to run tests only if certain conditions are met. If an assumption fails, the test is skipped rather than marked as failed. This is useful for tests that should only run under specific conditions, such as:

- When a certain environment variable is set
- When running on a specific operating system
- When certain external resources are available
- When specific preconditions are met

## Main Assumption Methods

JUnit 5 provides three main assumption methods:

1. **assumeTrue(condition, [message])** - Continues if the condition is true, otherwise skips the test
2. **assumeFalse(condition, [message])** - Continues if the condition is false, otherwise skips the test
3. **assumingThat(condition, executable)** - Executes the given code block only if the assumption is true, but continues the test regardless

## Examples in This Directory

The `JUnitAssumptionsTest.java` file contains several examples of using JUnit assumptions with the commission calculator model classes:

- **testAssumeTrue()** - Demonstrates using `assumeTrue()` to run a test only if a user has a specific role
- **testAssumeFalse()** - Demonstrates using `assumeFalse()` to run a test only if a deal is not in a specific status
- **testAssumingThat()** - Demonstrates using `assumingThat()` to conditionally execute code based on whether a commission plan is active
- **testEnvironmentSpecificAssumption()** - Shows how to use assumptions with environment-specific conditions
- **testMultipleAssumptions()** - Illustrates using assumptions with multiple conditions

## When to Use Assumptions vs. Assertions

- **Assertions** verify that your code behaves as expected. When an assertion fails, the test fails.
- **Assumptions** check if the test should run at all. When an assumption fails, the test is skipped.

Use assumptions when:
- The test depends on external conditions that may not always be present
- You want to conditionally run tests based on the environment
- You need to skip tests that aren't applicable in certain scenarios

Use assertions when:
- You need to verify the correctness of your code
- You want to ensure specific conditions are met for the test to pass
- You're checking the expected output or behavior of your code

## Benefits of Using Assumptions

1. **Improved Test Reliability**: Tests don't fail due to external factors or preconditions not being met
2. **Better Test Organization**: You can keep all tests in one place but only run them when appropriate
3. **Clearer Test Results**: Failed assumptions are reported differently than failed assertions, making it easier to understand test results
4. **Conditional Testing**: You can conditionally execute parts of tests based on the environment or state

## Understanding Test Output

When an assumption fails, JUnit throws an `org.opentest4j.TestAbortedException` with a message explaining why the test was skipped. This is normal behavior and not an error. For example:

```
org.opentest4j.TestAbortedException: Assumption failed: Skipping test because it should only run in production environment
```

This indicates that the test `testEnvironmentSpecificAssumption()` was skipped because it's designed to run only in a production environment. The test didn't fail - it was intentionally skipped based on the assumption condition.

## Practical Applications

In the commission calculator application, assumptions can be useful for:
- Running certain tests only when specific user roles are present
- Testing commission calculations only when deals are in appropriate statuses
- Executing plan-specific tests only when plans are active
- Running environment-specific tests only in the appropriate environment
