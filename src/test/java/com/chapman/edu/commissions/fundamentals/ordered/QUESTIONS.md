# Questions about JUnit Test Execution Order

## Basic Concepts

1. What is the default behavior of JUnit 5 regarding test execution order?
2. Why might you want to control the order of test execution in JUnit?
3. What annotation is used at the class level to specify the test method order?
4. What annotation is used at the method level to specify the order of a test method?
5. What happens if two test methods have the same order value?

## Method Orderers

6. What are the built-in MethodOrderer implementations provided by JUnit 5?
7. How does MethodOrderer.OrderAnnotation work?
8. How does MethodOrderer.DisplayName work?
9. How does MethodOrderer.MethodName work?
10. What is the purpose of MethodOrderer.Random?

## Advanced Concepts

11. How can you create a custom MethodOrderer?
12. What is the MethodOrdererContext and what information does it provide?
13. How can you access test method metadata in a custom MethodOrderer?
14. Can you combine different ordering strategies in a single test class?
15. How does test execution order interact with @Nested test classes?

## Best Practices

16. What are the potential drawbacks of having tests that depend on execution order?
17. How can you design ordered tests to minimize dependencies between them?
18. When should you use ordered tests versus using setup methods like @BeforeEach?
19. How can you document dependencies between ordered tests?
20. What alternatives exist to using ordered tests for complex test scenarios?