# JUnit 5 Nested Tests

This directory contains examples of JUnit 5 nested tests using the commission calculator model classes.

## What are Nested Tests?

Nested tests in JUnit 5 allow you to express the relationship between groups of tests. They provide a way to organize tests in a hierarchical structure, making them more readable and maintainable.

Nested tests are created using the `@Nested` annotation on an inner class. Each nested class can have its own setup methods (`@BeforeEach`, `@AfterEach`) and test methods.

## Benefits of Nested Tests

1. **Better Organization**: Tests can be grouped by functionality or test scenarios, making it easier to understand the purpose of each test.

2. **Shared Setup**: Inner classes can share setup code from outer classes, reducing code duplication. Each level can add its own setup, building on the setup from outer levels.

3. **Readability**: Test reports show the hierarchical structure of tests, making it easier to understand the relationships between tests.

4. **Context**: Tests can be written in a context-specific way, making them more expressive and easier to understand.

5. **Isolation**: Each nested class can focus on testing a specific aspect of the code, making tests more focused and easier to maintain.

## Example: JUnitNestedTest.java

The `JUnitNestedTest.java` file demonstrates the use of nested tests with the commission calculator model classes:

- The outer class contains a common setup method and a simple test.
- The `UserTests` nested class focuses on testing the `User` class.
  - The `UserRoleTests` nested class further focuses on testing user roles.
- The `DealTests` nested class focuses on testing the `Deal` class.
  - The `DealProductTests` nested class focuses on testing deal products.
  - The `DealStatusTests` nested class focuses on testing deal status changes.
- The `CommissionPlanTests` nested class focuses on testing the `CommissionPlan` class.
  - The `PlanStatusTests` nested class focuses on testing plan status changes.
  - The `PlanActivationTests` nested class focuses on testing plan activation.

This hierarchical structure makes it easy to understand the relationships between tests and the aspects of the code they are testing.

## How to Use Nested Tests

To create nested tests:

1. Create an inner class within your test class.
2. Annotate the inner class with `@Nested`.
3. Optionally, add a `@DisplayName` to provide a descriptive name for the nested test group.
4. Add setup methods (`@BeforeEach`, `@AfterEach`) specific to the nested class.
5. Add test methods to the nested class.
6. Repeat for additional levels of nesting as needed.

Example:

```java
@DisplayName("User Tests")
@Nested
class UserTests {
    private User user;

    @BeforeEach
    void setUpUser() {
        user = new User();
        // Initialize user...
    }

    @Test
    @DisplayName("Test user properties")
    void testUserProperties() {
        // Test user properties...
    }

    @Nested
    @DisplayName("User Role Tests")
    class UserRoleTests {
        @BeforeEach
        void setUpRoles() {
            // Add roles to user...
        }

        @Test
        @DisplayName("Test user has role")
        void testUserHasRole() {
            // Test user roles...
        }
    }
}
```

## Conclusion

Nested tests are a powerful feature of JUnit 5 that can help you organize your tests in a more logical and readable way. They are particularly useful for testing complex classes with multiple aspects or behaviors.