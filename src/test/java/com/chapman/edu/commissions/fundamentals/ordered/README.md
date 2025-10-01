# JUnit Test Execution Order

This directory contains examples and explanations of JUnit 5 test execution order concepts. The examples use the model classes from the Commission Calculator application to demonstrate how to control the order of test execution.

## Overview

By default, JUnit 5 does not guarantee the order of test execution. This is intentional, as tests should ideally be independent of each other. However, there are scenarios where controlling the order of test execution is necessary, such as:

1. Integration tests that build on each other
2. Tests that simulate a workflow or process
3. Performance tests that need to run in a specific sequence

JUnit 5 provides several ways to control the order of test execution, which are demonstrated in this directory.

## Test Classes

### JUnitOrderedTest

This class demonstrates how to control the order of test execution using the `@TestMethodOrder` and `@Order` annotations. The tests simulate a workflow for creating and managing a sales deal:

1. `createDeal()`: Creates a new deal
2. `addProductsToDeal()`: Adds products to the deal
3. `createCommissionPlan()`: Creates a commission plan for the deal
4. `closeDeal()`: Closes the deal
5. `verifyExecutionOrder()`: Verifies that the tests were executed in the correct order

The class uses a static list to track the execution order of the tests, which is verified in the final test.

## Key Concepts

### Test Method Order

JUnit 5 provides several built-in `MethodOrderer` implementations:

1. `MethodOrderer.OrderAnnotation`: Executes tests in the order specified by the `@Order` annotation
2. `MethodOrderer.DisplayName`: Executes tests in ascending order of display names
3. `MethodOrderer.MethodName`: Executes tests in ascending order of method names
4. `MethodOrderer.Random`: Executes tests in a random order
5. `MethodOrderer.Alphanumeric`: Executes tests in alphabetical order (deprecated)

To specify the order, use the `@TestMethodOrder` annotation at the class level:

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyOrderedTest {
    // Test methods
}
```

### Order Annotation

When using `MethodOrderer.OrderAnnotation`, you can specify the order of individual test methods using the `@Order` annotation:

```java
@Test
@Order(1)
public void firstTest() {
    // Test code
}

@Test
@Order(2)
public void secondTest() {
    // Test code
}
```

Lower values indicate higher priority. If two methods have the same order value, their execution order is not guaranteed.

### Custom MethodOrderer

You can also create a custom `MethodOrderer` implementation to define your own ordering logic:

```java
public class CustomMethodOrderer implements MethodOrderer {
    @Override
    public void orderMethods(MethodOrdererContext context) {
        // Custom ordering logic
    }
}
```

## Best Practices

1. **Keep tests independent**: Even when controlling execution order, try to design tests that can run independently.
2. **Document dependencies**: If tests depend on each other, clearly document these dependencies.
3. **Use meaningful order values**: Choose order values that make the sequence clear (e.g., 10, 20, 30 instead of 1, 2, 3).
4. **Consider alternatives**: Before using ordered tests, consider if there are better alternatives, such as:
   - Using `@BeforeEach` or `@BeforeAll` for setup
   - Creating a single test method with multiple steps
   - Using nested tests with `@Nested`
5. **Be cautious with shared state**: When tests share state, be careful about how changes in one test affect others.