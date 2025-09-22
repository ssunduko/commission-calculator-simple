# Code-to-Test Ratio Examples

This directory contains examples of different code-to-test ratios for the `Deal` class. The code-to-test ratio is a measure of the amount of test code relative to the amount of production code.

## What is a Code-to-Test Ratio?

The code-to-test ratio is the ratio between the lines of production code and the lines of test code. For example, a 1:2 ratio means that for every line of production code, there are two lines of test code.

Different ratios indicate different levels of test coverage and thoroughness:

- **1:1 ratio**: Basic testing that covers the main functionality
- **1:2 ratio**: More comprehensive testing with edge cases and more assertions
- **1:3 ratio**: Extremely thorough testing with extensive edge cases, boundary testing, and negative testing

## Examples in this Directory

### 1. DealTest1to1Ratio.java

This test class demonstrates a 1:1 code-to-test ratio. It includes:
- Basic tests for the main functionality of the `Deal` class
- Simple assertions to verify expected behavior
- Minimal setup and test methods

### 2. DealTest1to2Ratio.java

This test class demonstrates a 1:2 code-to-test ratio. It includes:
- More comprehensive testing with more test methods
- More assertions per test
- Edge case testing
- Nested test classes for better organization
- More detailed documentation

### 3. DealTest1to3Ratio.java

This test class demonstrates a 1:3 code-to-test ratio. It includes:
- Extremely comprehensive testing with extensive test methods
- Multiple assertions per test
- Thorough edge case testing
- Boundary value testing
- Negative testing (testing for expected failures)
- Complex test scenarios
- Nested test classes for better organization
- Detailed documentation

## When to Use Different Ratios

The appropriate code-to-test ratio depends on several factors:

- **Criticality of the code**: More critical code (e.g., financial calculations, security features) should have a higher test-to-code ratio
- **Complexity of the code**: More complex code benefits from more thorough testing
- **Stability requirements**: Code that needs to be very stable should have more tests
- **Available resources**: Higher ratios require more time and effort to create and maintain

## Benefits of Higher Test Ratios

- Better code coverage
- More edge cases and boundary conditions tested
- Higher confidence in code correctness
- Better documentation of expected behavior
- Easier refactoring with confidence

## Drawbacks of Higher Test Ratios

- More time required to write tests
- More maintenance effort when production code changes
- Potential for brittle tests that break with minor changes
- Diminishing returns on investment after a certain point

## Conclusion

These examples demonstrate different approaches to testing the same class with varying levels of thoroughness. The appropriate ratio for your project will depend on your specific requirements and constraints.