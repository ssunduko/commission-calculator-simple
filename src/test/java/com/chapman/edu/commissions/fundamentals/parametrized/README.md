# JUnit 5 Parameterized Tests Implementation

## Overview

This directory contains comprehensive examples of JUnit 5 parameterized testing techniques applied to the Commission Calculator model classes. The implementation demonstrates how to write data-driven tests that improve test coverage, reduce code duplication, and enhance maintainability.

## What Was Implemented

### JUnitParametrizedTest.java

A comprehensive test class that demonstrates all major parameterized testing annotations in JUnit 5:

1. **@ValueSource** - Testing with simple primitive values and strings
2. **@CsvSource** - Testing with CSV-formatted input data for multiple parameters  
3. **@MethodSource** - Testing with complex objects created by static methods
4. **@EnumSource** - Testing with enum values automatically provided
5. **@NullAndEmptySource** - Testing with null and empty values for edge cases

## Key Features Demonstrated

### 1. @ValueSource Examples
- **Deal Creation with Titles**: Tests deal creation with various valid titles
- **Deal Value Validation**: Tests numeric validation with different monetary amounts
- **User Creation with Usernames**: Tests string validation in user creation

### 2. @EnumSource Examples
- **Deal Status Transitions**: Tests all possible deal status values automatically
- **User Role Functionality**: Tests user roles with filtering to specific subsets
- **Commission Status Handling**: Tests commission statuses with exclusions

### 3. @CsvSource Examples
- **Deal Creation Combinations**: Tests multiple parameters (title, value, sales rep ID, validity)
- **User Creation Combinations**: Tests user construction with multiple field combinations
- **Commission Calculation Scenarios**: Tests business logic with multiple input parameters

### 4. @MethodSource Examples
- **Deal Equality Testing**: Tests object equality with complex ID combinations
- **User Role Combinations**: Tests complex role assignment scenarios
- **Commission with Bonuses**: Tests business calculations with variable bonus structures

### 5. @NullAndEmptySource Examples
- **Deal with Invalid Titles**: Tests edge cases with null and empty titles
- **User with Invalid Email**: Tests validation logic for required fields

## Benefits of Parameterized Testing

### Code Reduction
- Eliminates duplicate test methods for similar scenarios
- Centralizes test data management
- Reduces maintenance overhead

### Improved Coverage
- Systematically tests multiple input combinations
- Ensures edge cases are covered
- Tests enum values comprehensively

### Better Readability
- Clear test names describe what is being tested
- Test data is visible and organized
- Easy to add new test cases

### Data-Driven Approach
- Separates test logic from test data
- Enables easy expansion of test scenarios
- Supports business rule validation

## Test Categories Covered

### Model Validation Tests
- Object construction with various parameters
- Field validation and edge cases
- Business rule enforcement

### Equality and Hash Code Tests
- Object identity based on business keys
- Null value handling
- Hash code consistency

### Enum Handling Tests
- All enum values tested automatically
- Display name validation
- Status transition logic

### Business Logic Tests
- Commission calculations with bonuses
- Deal value calculations
- User role-based functionality

## Helper Methods

The implementation includes several helper methods that demonstrate best practices:

- **createDealWithId()**: Simplifies deal creation for equality testing
- **createUserWithRoles()**: Creates users with specific role configurations
- **createCommissionWithBonuses()**: Builds commission scenarios with variable bonuses

## Running the Tests

To execute these parameterized tests:

```bash
# Run all parameterized tests
mvn test -Dtest=JUnitParametrizedTest

# Run specific test methods
mvn test -Dtest=JUnitParametrizedTest#testDealCreationWithTitles
```

## Test Naming Convention

The parameterized tests use descriptive names that include parameter values:
- `@ParameterizedTest(name = "Creating deal with title: '{0}'")`
- `@ParameterizedTest(name = "Deal: '{0}', Value: ${1}, Rep: {2}, Valid: {3}")`

This provides clear output showing exactly what scenario each test execution covers.

## Extension Points

The framework can be easily extended to:
- Add new model classes to test
- Include additional validation scenarios
- Test complex business workflows
- Validate database interactions
- Test service layer logic

## Best Practices Demonstrated

1. **Comprehensive Parameter Coverage**: Tests include boundary values, typical values, and edge cases
2. **Clear Test Documentation**: Each test method includes detailed comments explaining the approach
3. **Reusable Helper Methods**: Common test setup logic is extracted into helper methods
4. **Meaningful Assertions**: Assertions include descriptive messages for better failure diagnosis
5. **Business Logic Focus**: Tests validate actual business requirements, not just technical functionality

This implementation serves as a reference for writing effective parameterized tests in Java applications using JUnit 5.