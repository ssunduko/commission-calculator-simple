# Questions about JUnit 5 Nested Tests

1. What is the purpose of using nested tests in JUnit 5?

2. How do you create a nested test class in JUnit 5?

3. Can a nested test class have its own setup methods (`@BeforeEach`, `@AfterEach`)? If yes, how do they interact with the setup methods of the outer class?

4. What is the `@DisplayName` annotation used for in nested tests?

5. How many levels of nesting can you have in JUnit 5 nested tests?

6. What are the advantages of using nested tests compared to regular (non-nested) tests?

7. Are there any limitations or drawbacks to using nested tests?

8. How do nested tests affect test execution order?

9. Can you use other JUnit 5 features like parameterized tests or assumptions with nested tests?

10. How do nested tests appear in test reports? How does this improve test result readability?

11. In the `JUnitNestedTest.java` example, why is the `UserRoleTests` class nested inside the `UserTests` class instead of being a separate top-level nested class?

12. How does the hierarchical structure of nested tests help in organizing tests for complex domain models like those in the commission calculator?

13. When would you choose to use nested tests versus using separate test classes?

14. Can static nested classes be used with the `@Nested` annotation?

15. How do lifecycle methods (`@BeforeAll`, `@AfterAll`) work with nested test classes?