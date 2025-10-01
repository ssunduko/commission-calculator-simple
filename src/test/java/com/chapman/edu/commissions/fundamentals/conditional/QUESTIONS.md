# Questions about JUnit 5 Conditional Tests

## Basic Concepts

1. **What are conditional tests in JUnit 5?**

2. **What is the main difference between conditional tests and regular tests?**

3. **List the main categories of conditional annotations provided by JUnit 5.**

4. **What happens when a conditional test's condition is not met?**

5. **Can you apply multiple conditional annotations to a single test method?**

6. **How do conditional tests differ from the `@Disabled` annotation?**

7. **What is the relationship between conditional tests and test assumptions?**

8. **Can conditional annotations be applied at the class level?**

## Operating System Conditions

9. **What are the two main annotations for OS-based conditional testing?**

10. **Which operating systems are supported by the `OS` enum in JUnit 5?**

11. **How would you write a test that only runs on Linux or Mac?**

12. **What's the difference between `@EnabledOnOs` and `@DisabledOnOs`?**

13. **Can you specify multiple operating systems in a single annotation?**

14. **How do you test platform-specific file system behaviors?**

15. **What are some real-world scenarios where OS-specific testing is necessary?**

## Java Runtime Conditions

16. **What annotations are used for Java version-based conditional testing?**

17. **Which Java versions are supported by the `JRE` enum?**

18. **How would you write a test that runs on Java 11 or higher?**

19. **What's the purpose of testing different Java versions?**

20. **How do you handle tests for features only available in newer Java versions?**

21. **Can you test for Java version ranges using conditional annotations?**

22. **How do JRE-based conditions help with backward compatibility testing?**

## System Properties and Environment Variables

23. **What's the difference between system properties and environment variables in conditional testing?**

24. **How do you set system properties for testing purposes?**

25. **What annotation would you use to run a test only when a specific environment variable is set?**

26. **Can you use regular expressions in the `matches` parameter?**

27. **How would you test for the absence of a system property or environment variable?**

28. **What are some common system properties used in conditional testing?**

29. **How do you handle sensitive information in environment variable conditions?**

30. **Can you combine system property and environment variable conditions?**

## Custom Conditions

31. **What annotations are used for custom conditional logic?**

32. **What are the requirements for custom condition methods?**

33. **Can custom condition methods accept parameters?**

34. **How do you access test context information in custom condition methods?**

35. **What should custom condition methods return if the condition cannot be determined?**

36. **Can custom condition methods access instance variables?**

37. **How do you test the custom condition methods themselves?**

38. **What are some examples of complex custom conditions?**

## Practical Applications

39. **When would you use conditional tests instead of separate test profiles?**

40. **How can conditional tests help with cross-platform development?**

41. **What are some examples of environment-specific testing scenarios?**

42. **How do conditional tests relate to continuous integration pipelines?**

43. **When might you use conditional tests for performance testing?**

44. **How do conditional tests support feature flag testing?**

45. **What role do conditional tests play in integration testing?**

## Commission Calculator Context

46. **In the commission calculator example, why might you conditionally test user roles?**

47. **How could you use conditional tests for different commission calculation algorithms?**

48. **What scenarios would require OS-specific testing in a commission calculator?**

49. **How might environment variables be used to control commission calculation testing?**

50. **What custom conditions might be relevant for commission calculator testing?**

51. **How would you conditionally test multi-currency commission calculations?**

52. **What conditions might determine whether to test external payment integrations?**

## Best Practices

53. **What are the advantages of conditional tests over manual test selection?**

54. **How should you document conditional test requirements?**

55. **What's the recommended approach for tests that need multiple conditions?**

56. **How do you ensure conditional tests are still discoverable and maintainable?**

57. **What are some common pitfalls when using conditional tests?**

58. **How do you balance conditional complexity with test readability?**

59. **What testing strategies work well with conditional execution?**

60. **How do you handle conditional tests in code reviews?**

## Advanced Topics

61. **How do conditional tests interact with parameterized tests?**

62. **Can you use conditional tests with dynamic tests?**

63. **How do conditional tests affect test execution order?**

64. **What's the performance impact of evaluating many conditions?**

65. **How do you debug issues with conditional test execution?**

66. **Can conditional tests be nested within other conditional contexts?**

67. **How do conditional tests work with test extensions and listeners?**

## Integration and Tooling

68. **How do IDEs handle conditional test execution and reporting?**

69. **How can build tools be configured to work with conditional tests?**

70. **What metrics should you track for conditional test execution?**

71. **How do conditional tests appear in test coverage reports?**

72. **What are the implications of conditional tests for test automation?**

73. **How do you ensure conditional tests run in appropriate CI/CD environments?**

## Troubleshooting

74. **What should you do when a conditional test never runs?**

75. **How do you verify that conditional logic is working correctly?**

76. **What are common mistakes in condition evaluation?**

77. **How do you handle flaky conditional tests?**

78. **What debugging techniques work best for conditional test issues?**

79. **How do you test the conditions themselves?**

80. **What documentation helps troubleshoot conditional test problems?**