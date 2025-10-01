# Answers to JUnit Repeated Tests Questions

## Basic Concepts

### 1. What is the purpose of the `@RepeatedTest` annotation in JUnit 5?
The `@RepeatedTest` annotation in JUnit 5 is used to repeat a test method a specified number of times. This is useful for testing functionality that might behave differently on different runs, for performance testing, reliability testing, or for testing with different data sets.

### 2. How do you specify the number of times a test should be repeated?
You specify the number of repetitions as a parameter to the `@RepeatedTest` annotation. For example, `@RepeatedTest(5)` will repeat the test 5 times.

### 3. What is the default display name format for repeated tests?
The default display name format for repeated tests is: `"repetition {currentRepetition} of {totalRepetitions}"`. For example, "repetition 1 of 5".

### 4. How can you customize the display name for repeated tests?
You can customize the display name by providing a name parameter to the `@RepeatedTest` annotation. For example:
```java
@RepeatedTest(value = 5, name = "{displayName} - Repetition {currentRepetition}/{totalRepetitions}")
```

### 5. What placeholders are available for customizing the display name?
The available placeholders are:
- `{displayName}`: The display name of the test method or the custom display name from `@DisplayName`
- `{currentRepetition}`: The current repetition (1-based)
- `{totalRepetitions}`: The total number of repetitions

## RepetitionInfo

### 6. What is `RepetitionInfo` and how do you use it in a repeated test?
`RepetitionInfo` is an interface that provides information about the current repetition of a test. You can use it by adding it as a parameter to your test method:
```java
@RepeatedTest(5)
void repeatedTest(RepetitionInfo repetitionInfo) {
    // Use repetitionInfo here
}
```

### 7. How do you access the current repetition number in a repeated test?
You can access the current repetition number using the `getCurrentRepetition()` method of the `RepetitionInfo` interface:
```java
int currentRepetition = repetitionInfo.getCurrentRepetition();
```
Note that the repetition number is 1-based, meaning the first repetition is 1, not 0.

### 8. How do you access the total number of repetitions in a repeated test?
You can access the total number of repetitions using the `getTotalRepetitions()` method of the `RepetitionInfo` interface:
```java
int totalRepetitions = repetitionInfo.getTotalRepetitions();
```

### 9. Can you use `RepetitionInfo` to conditionally execute code based on the repetition number?
Yes, you can use the current repetition number to conditionally execute code. For example:
```java
if (repetitionInfo.getCurrentRepetition() == 1) {
    // Only execute this code in the first repetition
}
```

### 10. How does `RepetitionInfo` differ from `TestInfo`?
`RepetitionInfo` provides information specific to repeated tests, such as the current repetition number and total repetitions. `TestInfo` provides general information about the test, such as the display name, tags, test class, and test method. They serve different purposes but can be used together in a repeated test.

## Use Cases

### 11. What are some common use cases for repeated tests?
Common use cases for repeated tests include:
- Performance testing: Running a test multiple times to measure performance
- Reliability testing: Running a test multiple times to ensure it consistently passes
- Testing with different data: Using the repetition information to test with different data sets
- Stress testing: Running a test multiple times to stress the system

### 12. How can repeated tests be used for performance testing?
For performance testing, you can:
1. Measure the time it takes to execute a piece of code in each repetition
2. Calculate statistics like average, minimum, maximum, and standard deviation
3. Assert that the performance meets certain criteria
4. Use the repetition information to vary the load or complexity

### 13. How can repeated tests be used for reliability testing?
For reliability testing, you can:
1. Run a test multiple times to ensure it consistently passes
2. Identify flaky tests that pass sometimes but fail other times
3. Test for race conditions or timing issues
4. Verify that the system behaves consistently under the same conditions

### 14. How can repeated tests be used for testing with different data sets?
For testing with different data sets, you can:
1. Use the repetition number to select different test data
2. Create test data that varies based on the repetition number
3. Test boundary conditions by incrementing values with each repetition
4. Test with increasingly complex data as the repetition number increases

### 15. What are the advantages of using `@RepeatedTest` over a loop within a single test?
Advantages of using `@RepeatedTest` over a loop include:
- Each repetition is reported separately in test results
- JUnit handles the repetition logic for you
- You can use custom display names to identify each repetition
- You can access repetition information through the `RepetitionInfo` interface
- Each repetition is treated as a separate test for reporting and execution purposes

## Best Practices

### 16. What are some best practices for using repeated tests?
Best practices for using repeated tests include:
- Use descriptive display names to make it clear which repetition is running
- Keep tests independent so each repetition can run independently
- Use `RepetitionInfo` to customize the test based on the repetition number
- Be mindful of performance, especially for tests with many repetitions
- Use repeated tests for appropriate use cases, not just because you can

### 17. How should you handle test data in repeated tests to ensure independence between repetitions?
To ensure independence between repetitions:
- Reset or recreate test data in a `@BeforeEach` method
- Avoid sharing mutable state between repetitions
- Use the repetition number to create unique test data for each repetition
- Clean up any resources created during the test in an `@AfterEach` method
- Avoid relying on the results of previous repetitions

### 18. When should you use `@RepeatedTest` versus `@ParameterizedTest`?
Use `@RepeatedTest` when:
- You want to run the same test multiple times with the same or similar inputs
- You're testing for reliability or performance
- You want to use the repetition number to vary the test

Use `@ParameterizedTest` when:
- You want to run a test with different inputs
- You have a specific set of test cases to cover
- You want to test with different combinations of parameters

### 19. How can you ensure that repeated tests are efficient and don't waste resources?
To ensure efficiency:
- Use an appropriate number of repetitions (not too many, not too few)
- Use `@BeforeAll` and `@AfterAll` for expensive setup and teardown operations
- Consider using a smaller number of repetitions during development and more in CI
- Profile your tests to identify performance bottlenecks
- Use timeouts to prevent tests from running too long

### 20. How can you debug issues that only occur in specific repetitions of a repeated test?
To debug issues in specific repetitions:
- Use custom display names to identify which repetition is failing
- Add logging statements that include the repetition number
- Use conditional breakpoints based on the repetition number
- Isolate the failing repetition by running only that specific repetition
- Analyze patterns in failures to identify potential causes