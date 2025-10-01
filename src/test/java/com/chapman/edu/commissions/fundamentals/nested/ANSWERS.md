# Answers to Questions about JUnit 5 Nested Tests

## 1. What is the purpose of using nested tests in JUnit 5?

The primary purpose of nested tests in JUnit 5 is to express the relationship between groups of tests and to organize tests in a hierarchical structure. This helps in:
- Grouping related tests together
- Creating a more readable and maintainable test suite
- Expressing the context and preconditions for tests more clearly
- Sharing setup code between related tests
- Creating more focused and isolated test scenarios

## 2. How do you create a nested test class in JUnit 5?

To create a nested test class in JUnit 5:
1. Define a non-static inner class within your test class
2. Annotate the inner class with `@Nested`
3. Optionally, add a `@DisplayName` annotation to provide a descriptive name
4. Add test methods and setup methods as needed

Example:
```java
public class OuterTest {
    @Nested
    @DisplayName("When user is created")
    class UserCreationTests {
        // Test methods go here
    }
}
```

## 3. Can a nested test class have its own setup methods (`@BeforeEach`, `@AfterEach`)? If yes, how do they interact with the setup methods of the outer class?

Yes, nested test classes can have their own setup methods. The interaction works as follows:
- When a test in a nested class is executed, the `@BeforeEach` methods of the outer class are executed first, followed by the `@BeforeEach` methods of the nested class.
- After the test, the `@AfterEach` methods of the nested class are executed first, followed by the `@AfterEach` methods of the outer class.
- This allows the nested class to build upon the setup provided by the outer class, adding more specific setup for its own tests.

## 4. What is the `@DisplayName` annotation used for in nested tests?

The `@DisplayName` annotation is used to provide a custom, human-readable name for a test class or method. In the context of nested tests, it's particularly useful for:
- Creating a narrative structure for your tests
- Making test reports more readable and understandable
- Expressing the context or scenario being tested
- Using spaces, special characters, and even emojis in test names

## 5. How many levels of nesting can you have in JUnit 5 nested tests?

JUnit 5 does not impose a specific limit on the number of nesting levels. You can create as many levels of nesting as needed to express the structure of your tests. However, as a practical matter, too many levels of nesting can make tests harder to understand and maintain. Generally, 2-3 levels of nesting are sufficient for most testing scenarios.

## 6. What are the advantages of using nested tests compared to regular (non-nested) tests?

Advantages of nested tests include:
- **Better organization**: Tests are grouped by functionality or scenario
- **Shared setup**: Inner classes can reuse and build upon setup from outer classes
- **Improved readability**: The hierarchical structure makes relationships between tests clearer
- **Context-specific testing**: Tests can be written in a way that reflects their specific context
- **Reduced duplication**: Common setup code can be shared among related tests
- **Better test reports**: Test results show the hierarchical structure, making them easier to interpret

## 7. Are there any limitations or drawbacks to using nested tests?

Some limitations and drawbacks include:
- **Increased complexity**: Deep nesting can make tests harder to understand
- **Non-static limitation**: Nested test classes must be non-static, which can be a limitation in some cases
- **IDE support**: Some older IDEs may have limited support for nested tests
- **Learning curve**: Developers unfamiliar with the concept may need time to understand the structure
- **Potential overuse**: It's possible to create overly complex test structures that are harder to maintain than simpler alternatives

## 8. How do nested tests affect test execution order?

JUnit 5 executes tests in a deterministic but not necessarily predictable order. For nested tests:
- Tests in the outer class are executed before tests in nested classes
- Within each class (outer or nested), tests are executed in a deterministic order based on method name, unless specified otherwise
- You can use the `@TestMethodOrder` annotation to specify a custom execution order for methods within a class

## 9. Can you use other JUnit 5 features like parameterized tests or assumptions with nested tests?

Yes, you can use other JUnit 5 features with nested tests. For example:
- **Parameterized tests**: You can use `@ParameterizedTest` in nested test classes
- **Assumptions**: You can use assumptions (`assumeTrue`, `assumeFalse`, etc.) in nested tests
- **Tags**: You can tag nested test classes or methods for selective execution
- **Timeouts**: You can apply timeouts to nested test methods
- **Disabled tests**: You can disable specific nested tests or entire nested classes

## 10. How do nested tests appear in test reports? How does this improve test result readability?

In test reports, nested tests appear in a hierarchical structure that reflects their nesting in the code. For example:
```
JUnit Nested Tests Demo
├── Simple test in outer class
├── User Tests
│   ├── Test user properties
│   └── User Role Tests
│       ├── Test user has SALES_REP role
│       └── Test user does not have other roles
└── Deal Tests
    ├── Test deal properties
    ├── Deal Product Tests
    │   ├── Test products were added to deal
    │   └── Test deal total value calculation
    └── Deal Status Tests
        └── Test changing deal status
```

This improves readability by:
- Showing the relationships between tests
- Grouping related tests together
- Providing context for test failures
- Making it easier to understand the structure of the test suite
- Allowing for more descriptive test names within each context

## 11. In the `JUnitNestedTest.java` example, why is the `UserRoleTests` class nested inside the `UserTests` class instead of being a separate top-level nested class?

The `UserRoleTests` class is nested inside the `UserTests` class because:
1. It represents a more specific aspect of user testing that builds upon the general user testing context
2. It can reuse the user object created in the `UserTests` setup method
3. It logically belongs to the user testing domain
4. It creates a clear hierarchical relationship: testing user roles is a subset of testing users
5. It allows for a more focused and contextual testing approach

## 12. How does the hierarchical structure of nested tests help in organizing tests for complex domain models like those in the commission calculator?

For complex domain models like those in the commission calculator, the hierarchical structure helps by:
- **Reflecting domain relationships**: Tests can mirror the relationships between domain objects (e.g., Deal contains Products)
- **Isolating test scenarios**: Each nested class can focus on a specific aspect or scenario
- **Progressive setup**: Tests can build upon each other, starting with simple cases and progressing to more complex ones
- **Contextual testing**: Tests can be written in a way that reflects their specific business context
- **Improved maintainability**: Changes to one aspect of the domain model only affect the relevant nested tests
- **Better documentation**: The test structure serves as documentation of the domain model's structure and behavior

## 13. When would you choose to use nested tests versus using separate test classes?

You would choose nested tests when:
- Tests share common setup or context
- Tests are logically related and build upon each other
- You want to express a hierarchical relationship between test scenarios
- You want to reduce duplication in setup code
- The domain model has natural hierarchical relationships

You would choose separate test classes when:
- Tests are completely independent
- Tests focus on different components or subsystems
- The relationship between tests is not hierarchical
- You want to run tests in parallel
- You need to use static setup methods (`@BeforeAll`, `@AfterAll`)

## 14. Can static nested classes be used with the `@Nested` annotation?

No, static nested classes cannot be used with the `@Nested` annotation. The `@Nested` annotation can only be applied to non-static inner classes. This is because nested tests are designed to share the instance state of the outer test class, which is not possible with static nested classes.

If you try to use `@Nested` with a static class, you'll get a JUnitException with a message like: "The nested test class must be non-static."

## 15. How do lifecycle methods (`@BeforeAll`, `@AfterAll`) work with nested test classes?

In JUnit 5:
- `@BeforeAll` and `@AfterAll` methods must be static (by default)
- Nested test classes cannot contain static members (as they are inner classes)
- Therefore, nested test classes cannot directly contain `@BeforeAll` and `@AfterAll` methods

However, there are workarounds:
1. Use `@BeforeAll` and `@AfterAll` in the outer class to set up and tear down resources needed by all nested tests
2. Use the `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` annotation on the nested class, which allows non-static `@BeforeAll` and `@AfterAll` methods
3. Use extension mechanisms like `@RegisterExtension` for more complex setup and teardown scenarios