# JUnit Tags and Testing Fundamentals

This directory contains examples and explanations of JUnit 5 tags. The examples use the model classes from the Commission Calculator application to demonstrate how to use tags to categorize and selectively run tests.

## Overview

JUnit 5 tags are a powerful feature that allows you to categorize tests and selectively run them. Tags can be used to:

1. Group related tests together
2. Run only specific categories of tests
3. Exclude certain categories of tests from execution
4. Configure different test execution settings based on tags

## Test Classes

### JUnitTaggedTest

This class demonstrates the use of JUnit 5 tags with the commission calculator model classes. It covers:

- Tagging individual test methods
- Tagging test classes
- Using multiple tags
- Filtering tests by tags

The test uses various model classes (`User`, `Deal`, `DealProduct`, `CommissionPlan`) to demonstrate how to tag tests based on the components they're testing.

## Key Concepts

### Tags

JUnit 5 tags are used to categorize tests. A tag is a string that you can attach to a test method or class using the `@Tag` annotation. For example:

```java
@Tag("user")
@Test
public void testUserProperties() {
    // Test code
}
```

### Class-Level Tags

You can apply tags at the class level to categorize all tests in a class:

```java
@Tag("model")
public class JUnitTaggedTest {
    // Test methods
}
```

### Multiple Tags

You can apply multiple tags to a test method to categorize it in multiple ways:

```java
@Tag("user")
@Tag("role")
@Test
public void testUserRoles() {
    // Test code
}
```

### Filtering Tests

You can run only tests with specific tags or exclude tests with specific tags. This can be done through:

1. Build tools like Maven or Gradle
2. IDE configurations
3. Command-line arguments

## Best Practices

1. Use meaningful tag names: Choose tag names that clearly indicate the category of the test
2. Be consistent: Use a consistent naming convention for tags
3. Use multiple tags when appropriate: A test can belong to multiple categories
4. Document your tags: Make sure your team knows what tags are available and what they mean
5. Use tags for test organization: Tags can help organize tests by feature, component, or test type
6. Consider using tags for test execution: Tags can be used to run only certain types of tests in specific environments