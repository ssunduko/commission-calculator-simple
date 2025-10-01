# Questions about JUnit Repeated Tests

## Basic Concepts

1. What is the purpose of the `@RepeatedTest` annotation in JUnit 5?
2. How do you specify the number of times a test should be repeated?
3. What is the default display name format for repeated tests?
4. How can you customize the display name for repeated tests?
5. What placeholders are available for customizing the display name?

## RepetitionInfo

6. What is `RepetitionInfo` and how do you use it in a repeated test?
7. How do you access the current repetition number in a repeated test?
8. How do you access the total number of repetitions in a repeated test?
9. Can you use `RepetitionInfo` to conditionally execute code based on the repetition number?
10. How does `RepetitionInfo` differ from `TestInfo`?

## Use Cases

11. What are some common use cases for repeated tests?
12. How can repeated tests be used for performance testing?
13. How can repeated tests be used for reliability testing?
14. How can repeated tests be used for testing with different data sets?
15. What are the advantages of using `@RepeatedTest` over a loop within a single test?

## Best Practices

16. What are some best practices for using repeated tests?
17. How should you handle test data in repeated tests to ensure independence between repetitions?
18. When should you use `@RepeatedTest` versus `@ParameterizedTest`?
19. How can you ensure that repeated tests are efficient and don't waste resources?
20. How can you debug issues that only occur in specific repetitions of a repeated test?