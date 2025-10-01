# JUnit 5 Disabled Tests

This package demonstrates the use of JUnit 5 `@Disabled` annotation with the commission calculator model classes.

## What are Disabled Tests?

The `@Disabled` annotation is used to disable tests that should not be executed during test runs. Disabled tests are not executed but are still reported in test results, helping maintain test history and documentation.

## When to Use @Disabled

Disabled tests are useful for:

1. **Temporarily disabling failing tests** during development or bug fixes
2. **Marking tests as not yet implemented** during test-driven development
3. **Disabling tests that are no longer relevant** but you want to keep for reference
4. **Disabling resource-intensive tests** that are too slow for regular execution
5. **Disabling tests that depend on external resources** not available in all environments
6. **Marking deprecated functionality tests** that will be removed in future versions

## Basic Usage

### Simple Disabled Test
```java
@Test
@Disabled
void testBasicDisabled() {
    // This test will not run
}
```

### Disabled Test with Reason
```java
@Test
@Disabled("This test is temporarily disabled during refactoring")
void testDisabledWithReason() {
    // The reason will be shown in test results
}
```

## Common Use Cases

### 1. Tests Not Yet Implemented

During test-driven development, you might write test method signatures before implementing the logic:

```java
@Test
@Disabled("Test not yet implemented - waiting for commission calculation logic")
void testCalculateComplexCommission() {
    // TODO: Implement this test when feature is ready
    fail("Test not implemented yet");
}
```

### 2. Temporarily Failing Tests

When tests fail due to temporary issues:

```java
@Test
@Disabled("Failing due to database connection issue - ticket #123")
void testSaveCommissionCalculation() {
    // Disabled while infrastructure issues are resolved
}
```

### 3. Performance/Resource-Intensive Tests

Tests that are too slow or resource-intensive for regular runs:

```java
@Test
@Disabled("Performance test - too slow for regular CI runs")
void testLargeDealProcessingPerformance() {
    // This test takes several minutes to complete
}
```

### 4. External Dependency Tests

Tests that require external services or resources:

```java
@Test
@Disabled("Requires external payment service - not available in test environment")
void testProcessCommissionPayment() {
    // Disabled because external service is not available
}
```

### 5. Deprecated Functionality Tests

Tests for functionality that will be removed:

```java
@Test
@Disabled("Testing deprecated commission calculation method - will be removed in v2.0")
void testDeprecatedCommissionCalculation() {
    // Test for deprecated functionality
}
```

## Class-Level Disabling

You can also disable entire test classes:

```java
@Disabled("Entire class disabled during major refactoring")
public class CommissionCalculatorTest {
    // All tests in this class will be disabled
}
```

## Best Practices

### 1. Always Provide a Reason
```java
// Good - provides context
@Disabled("Waiting for API v2 implementation - ticket #456")

// Bad - no context
@Disabled
```

### 2. Include Ticket Numbers or References
```java
@Disabled("Intermittent failure - investigating race condition - ticket #789")
```

### 3. Set Reminders for Re-enabling
```java
@Disabled("TODO: Re-enable after Q2 2024 infrastructure upgrade")
```

### 4. Use Descriptive Display Names
```java
@Test
@Disabled("Feature not yet implemented")
@DisplayName("Calculate commission with performance bonuses")
void testCalculateCommissionWithBonuses() {
    // Clear description of what the test should do
}
```

## Alternatives to @Disabled

### Conditional Tests
Instead of disabling, consider using conditional execution:

```java
@Test
@EnabledIfSystemProperty(named = "integration.tests", matches = "true")
void testExternalIntegration() {
    // Only runs when system property is set
}
```

### Assumptions
Use assumptions to skip tests based on runtime conditions:

```java
@Test
void testWithAssumption() {
    assumeTrue(isExternalServiceAvailable());
    // Test only runs if assumption is true
}
```

## Monitoring Disabled Tests

### Test Reports
Disabled tests appear in test reports with their reasons, helping track:
- Why tests were disabled
- How long they've been disabled
- Whether they should be re-enabled

### IDE Integration
Most IDEs show disabled tests with special icons and display the reason when hovering over the test name.

### CI/CD Integration
Build systems can report on disabled tests, helping teams track technical debt and ensure disabled tests don't remain disabled indefinitely.

## Commission Calculator Examples

In the commission calculator context, disabled tests might be used for:

1. **Complex commission calculations** not yet implemented
2. **Integration tests** requiring external payment services
3. **Performance tests** for processing large numbers of deals
4. **Multi-currency features** not yet enabled for the current client
5. **AI-powered optimization** features still in development

## Summary

The `@Disabled` annotation is a powerful tool for managing test suites during development and maintenance. It helps maintain test history, document incomplete features, and manage tests that can't run in all environments. The key is to always provide clear reasons and regularly review disabled tests to determine if they should be re-enabled or removed.