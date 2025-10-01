# JUnit Assertions

This directory contains examples demonstrating the use of JUnit 5 assertions with the commission calculator model classes.

## What are JUnit Assertions?

Assertions in JUnit are used to verify that the code under test behaves as expected. When an assertion fails, the test fails, indicating that there is a problem with the code. Assertions are the primary way to validate that your code is working correctly.

## Main Assertion Methods

JUnit 5 provides a rich set of assertion methods in the `org.junit.jupiter.api.Assertions` class:

1. **assertEquals(expected, actual, [message])** - Verifies that two values are equal
2. **assertNotEquals(expected, actual, [message])** - Verifies that two values are not equal
3. **assertTrue(condition, [message])** - Verifies that a condition is true
4. **assertFalse(condition, [message])** - Verifies that a condition is false
5. **assertNull(object, [message])** - Verifies that an object is null
6. **assertNotNull(object, [message])** - Verifies that an object is not null
7. **assertSame(expected, actual, [message])** - Verifies that two references point to the same object
8. **assertNotSame(expected, actual, [message])** - Verifies that two references do not point to the same object
9. **assertThrows(exceptionType, executable, [message])** - Verifies that a specific exception is thrown
10. **assertDoesNotThrow(executable, [message])** - Verifies that no exception is thrown
11. **assertAll(executables...)** - Groups multiple assertions together
12. **assertArrayEquals(expected, actual, [message])** - Verifies that two arrays are equal
13. **assertIterableEquals(expected, actual, [message])** - Verifies that two iterables are deeply equal

## Examples in This Directory

The `JUnitAssertionsTest.java` file contains several examples of using JUnit assertions with the commission calculator model classes:

- **testAssertEquals()** - Demonstrates using `assertEquals()` with different types of values
- **testAssertNotEquals()** - Demonstrates using `assertNotEquals()` to verify inequality
- **testAssertTrue()** - Demonstrates using `assertTrue()` with boolean conditions
- **testAssertFalse()** - Demonstrates using `assertFalse()` with boolean conditions
- **testAssertNullAndNotNull()** - Demonstrates using `assertNull()` and `assertNotNull()`
- **testAssertSameAndNotSame()** - Demonstrates using `assertSame()` and `assertNotSame()`
- **testAssertThrows()** - Demonstrates using `assertThrows()` to verify exceptions
- **testAssertDoesNotThrow()** - Demonstrates using `assertDoesNotThrow()` to verify no exceptions
- **testAssertAll()** - Demonstrates using `assertAll()` to group assertions
- **testAssertArrayEquals()** - Demonstrates using `assertArrayEquals()` with arrays
- **testAssertIterableEquals()** - Demonstrates using `assertIterableEquals()` with collections

## When to Use Different Assertions

- **assertEquals/assertNotEquals**: Use when you want to compare values for equality or inequality
- **assertTrue/assertFalse**: Use when you want to verify boolean conditions
- **assertNull/assertNotNull**: Use when you want to check if an object is null or not
- **assertSame/assertNotSame**: Use when you want to check if two references point to the same object
- **assertThrows/assertDoesNotThrow**: Use when you want to verify exception behavior
- **assertAll**: Use when you want to group multiple assertions together
- **assertArrayEquals**: Use when you want to compare arrays
- **assertIterableEquals**: Use when you want to compare collections

## Best Practices for Assertions

1. **Include meaningful messages**: Always include a descriptive message in your assertions to make it clear what's being tested and why it failed.
2. **Test one concept per test method**: Each test method should focus on testing a single concept or behavior.
3. **Use assertAll for multiple related assertions**: When you have multiple related assertions, use `assertAll` to group them together.
4. **Be specific with assertions**: Use the most specific assertion method for your needs (e.g., use `assertNull` instead of `assertEquals(null, object)`).
5. **Test both positive and negative cases**: Don't just test that things work correctly; also test that they fail correctly.
6. **Use assertThrows for exception testing**: When testing that code throws exceptions, use `assertThrows` instead of try-catch blocks.

## Understanding Test Output

When an assertion fails, JUnit provides detailed information about what went wrong:

```
org.opentest4j.AssertionFailedError: User should have SALES_REP role ==> expected: <true> but was: <false>
```

This indicates that the assertion `assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role")` failed because the user did not have the SALES_REP role.

## Practical Applications

In the commission calculator application, assertions can be useful for:
- Verifying that user roles are correctly assigned
- Ensuring that deal values are calculated correctly
- Checking that commission plans are active during the expected date range
- Validating that products are correctly added to deals
- Testing that exceptions are thrown for invalid inputs