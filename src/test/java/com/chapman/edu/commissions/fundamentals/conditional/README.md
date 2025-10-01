# JUnit 5 Conditional Tests

This package demonstrates the use of JUnit 5 conditional tests with the commission calculator model classes.

## What are Conditional Tests?

Conditional tests in JUnit 5 allow you to execute tests only if certain conditions are met. This is useful for:

1. Running tests only on specific operating systems
2. Running tests only on specific Java versions
3. Running tests only when certain system properties or environment variables are set
4. Running tests based on custom conditions

## Conditional Annotations in JUnit 5

JUnit 5 provides several annotations for conditional test execution:

### Operating System Conditions

- `@EnabledOnOs` - Run a test only on specific operating systems
- `@DisabledOnOs` - Skip a test on specific operating systems

Example:
```java
@Test
@EnabledOnOs(OS.WINDOWS)
void testEnabledOnWindows() {
    // This test will only run on Windows
}

@Test
@DisabledOnOs(OS.WINDOWS)
void testDisabledOnWindows() {
    // This test will not run on Windows
}
```

### Java Runtime Conditions

- `@EnabledOnJre` - Run a test only on specific Java Runtime Environment versions
- `@DisabledOnJre` - Skip a test on specific Java Runtime Environment versions

Example:
```java
@Test
@EnabledOnJre(JRE.JAVA_11)
void testEnabledOnJava11() {
    // This test will only run on Java 11
}

@Test
@DisabledOnJre(JRE.JAVA_8)
void testDisabledOnJava8() {
    // This test will not run on Java 8
}
```

### System Property Conditions

- `@EnabledIfSystemProperty` - Run a test only when a system property has a specific value
- `@DisabledIfSystemProperty` - Skip a test when a system property has a specific value

Example:
```java
@Test
@EnabledIfSystemProperty(named = "env", matches = "test")
void testEnabledInTestEnvironment() {
    // This test will only run if -Denv=test
}

@Test
@DisabledIfSystemProperty(named = "env", matches = "prod")
void testDisabledInProductionEnvironment() {
    // This test will not run if -Denv=prod
}
```

### Environment Variable Conditions

- `@EnabledIfEnvironmentVariable` - Run a test only when an environment variable has a specific value
- `@DisabledIfEnvironmentVariable` - Skip a test when an environment variable has a specific value

Example:
```java
@Test
@EnabledIfEnvironmentVariable(named = "TEST_MODE", matches = "enabled")
void testEnabledWhenTestModeEnabled() {
    // This test will only run if TEST_MODE=enabled
}

@Test
@DisabledIfEnvironmentVariable(named = "TEST_MODE", matches = "disabled")
void testDisabledWhenTestModeDisabled() {
    // This test will not run if TEST_MODE=disabled
}
```

### Custom Conditions

- `@EnabledIf` - Run a test only if a custom condition method returns true
- `@DisabledIf` - Skip a test if a custom condition method returns true

Example:
```java
@Test
@EnabledIf("isUserASalesRep")
void testEnabledIfUserIsSalesRep() {
    // This test will only run if isUserASalesRep() returns true
}

@Test
@DisabledIf("isDealWon")
void testDisabledIfDealIsWon() {
    // This test will not run if isDealWon() returns true
}

// Custom condition methods
static boolean isUserASalesRep() {
    // Custom logic to determine if user is a sales rep
    return true;
}

static boolean isDealWon() {
    // Custom logic to determine if deal is won
    return false;
}
```

## Combining Conditions

You can apply multiple conditional annotations to a test method. The test will only run if all conditions are satisfied.

Example:
```java
@Test
@EnabledOnOs(OS.WINDOWS)
@EnabledIfSystemProperty(named = "env", matches = "test")
void testEnabledOnWindowsInTestEnvironment() {
    // This test will only run on Windows with -Denv=test
}
```

## Practical Use Cases

Conditional tests are useful in many scenarios:

1. **Platform-specific tests**: Test features that only work on specific operating systems
2. **Version-specific tests**: Test features that are only available in certain Java versions
3. **Environment-specific tests**: Run tests differently in development, test, and production environments
4. **Performance tests**: Skip resource-intensive tests in certain environments
5. **Integration tests**: Run integration tests only when external systems are available

## Example in the Commission Calculator

In the commission calculator example, we use conditional tests to:

1. Test user roles conditionally based on the user's role
2. Test deal status conditionally based on the deal's status
3. Test commission plan activation conditionally based on the plan's status and dates

These examples demonstrate how conditional tests can be used to test business logic that depends on various conditions.