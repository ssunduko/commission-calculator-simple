# JUnit Repeated Tests

This directory contains examples and explanations of JUnit 5 repeated test functionality. The examples use the model classes from the Commission Calculator application to demonstrate various repeated test concepts.

## Overview

JUnit 5 provides the ability to repeat a test multiple times using the `@RepeatedTest` annotation. This is useful for testing functionality that might behave differently on different runs, or for performance testing, reliability testing, and testing with different data sets.

## Test Classes

### JUnitRepeatedTest

This class demonstrates the basic JUnit 5 repeated test functionality:

- Basic repeated tests with `@RepeatedTest`
- Custom display names for repeated tests
- Using `RepetitionInfo` to access information about the current repetition
- Using `TestInfo` to access information about the test
- Performance testing with repeated tests
- Testing with different data using repeated tests

The test uses the model classes from the Commission Calculator application to demonstrate how to set up and run repeated tests.

## Key Concepts

### Repeated Tests

JUnit 5 provides the `@RepeatedTest` annotation to repeat a test multiple times:

1. `@RepeatedTest(n)`: Repeats the test n times
2. Custom display names: `@RepeatedTest(value = n, name = "pattern")` where pattern can include:
   - `{displayName}`: The display name of the test
   - `{currentRepetition}`: The current repetition (1-based)
   - `{totalRepetitions}`: The total number of repetitions
3. `RepetitionInfo`: Provides information about the current repetition
   - `getCurrentRepetition()`: Returns the current repetition (1-based)
   - `getTotalRepetitions()`: Returns the total number of repetitions

### Use Cases for Repeated Tests

Repeated tests are useful for:

1. **Performance Testing**: Running a test multiple times to measure performance
2. **Reliability Testing**: Running a test multiple times to ensure it consistently passes
3. **Testing with Different Data**: Using the repetition information to test with different data sets
4. **Stress Testing**: Running a test multiple times to stress the system

## Best Practices

1. **Use descriptive display names**: Make it clear which repetition is running
2. **Keep tests independent**: Each repetition should be independent of other repetitions
3. **Use RepetitionInfo**: Access information about the current repetition to customize the test
4. **Consider performance**: Be mindful of the number of repetitions, especially for slow tests
5. **Test edge cases**: Use repetitions to test different edge cases