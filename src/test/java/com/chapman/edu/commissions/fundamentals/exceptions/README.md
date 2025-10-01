# JUnit Exception Testing

This directory contains examples of how to test exceptions using JUnit 5 with the commission calculator model classes.

## Overview

Exception testing is a crucial part of unit testing. It ensures that your code fails in a predictable and controlled way when it encounters error conditions. Proper exception testing helps verify that:

1. The code throws the expected exceptions under specific conditions
2. The exceptions contain the correct error messages
3. The code handles exceptions appropriately

## Exception Testing Methods in JUnit 5

JUnit 5 provides several ways to test exceptions:

### 1. assertThrows()

The `assertThrows()` method verifies that a specific exception is thrown when executing a piece of code. It takes the expected exception class and a lambda expression containing the code that should throw the exception.

```java
assertThrows(NullPointerException.class, () -> {
    nullObject.someMethod();
}, "Optional failure message");
```

The method returns the thrown exception, which can be used for further assertions:

```java
Exception exception = assertThrows(IllegalArgumentException.class, () -> {
    validatePositiveAmount(new BigDecimal("-100.00"));
});
assertEquals("Amount must be positive", exception.getMessage());
```

### 2. assertDoesNotThrow()

The `assertDoesNotThrow()` method verifies that no exception is thrown when executing a piece of code:

```java
assertDoesNotThrow(() -> {
    validObject.someMethod();
}, "Optional failure message");
```

### 3. Try-Catch Blocks

Sometimes you need more control over the exception handling or need to perform additional actions after catching the exception. In these cases, you can use traditional try-catch blocks:

```java
try {
    someMethodThatShouldThrowException();
    fail("Expected exception was not thrown");
} catch (SomeException e) {
    // Verify the exception properties
    assertEquals("Expected message", e.getMessage());
}
```

## Common Exceptions Tested

The examples in this directory demonstrate testing for several common exceptions:

1. **NullPointerException**: Thrown when attempting to use a null reference
2. **IllegalArgumentException**: Thrown when a method receives an argument that is not valid
3. **IndexOutOfBoundsException**: Thrown when attempting to access an element at an invalid index in a list or array
4. **ClassCastException**: Thrown when attempting to cast an object to an incompatible type
5. **ArithmeticException**: Thrown when an arithmetic operation fails, such as division by zero
6. **NumberFormatException**: Thrown when attempting to convert a string to a numeric type but the string does not have the appropriate format

## Best Practices for Exception Testing

1. **Be specific about the exception type**: Test for the most specific exception type possible, not just `Exception`.
2. **Verify the exception message**: When possible, check that the exception message contains the expected information.
3. **Test both positive and negative cases**: Verify that exceptions are thrown when expected and not thrown when not expected.
4. **Keep test methods focused**: Each test method should focus on testing a specific exception scenario.
5. **Use descriptive test method names**: The name should clearly indicate what exception is being tested and under what conditions.

## Examples

The `JUnitExceptionsTest.java` file in this directory contains examples of testing various exceptions using the commission calculator model classes. Each test method demonstrates a different approach to exception testing and includes detailed comments explaining the purpose and approach.