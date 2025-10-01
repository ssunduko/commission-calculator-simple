# Answers to JUnit 5 Conditional Tests Questions

## Basic Concepts

1. **What are conditional tests in JUnit 5?**
   
   Conditional tests are tests that execute only when certain conditions are met. They use annotations to evaluate conditions at runtime and skip test execution if conditions are not satisfied, rather than failing the test.

2. **What is the main difference between conditional tests and regular tests?**
   
   Regular tests always execute when the test suite runs. Conditional tests evaluate specified conditions first and only execute if those conditions are true. If conditions are false, the test is skipped rather than failed.

3. **List the main categories of conditional annotations provided by JUnit 5.**
   
   - Operating System conditions (`@EnabledOnOs`, `@DisabledOnOs`)
   - Java Runtime Environment conditions (`@EnabledOnJre`, `@DisabledOnJre`)
   - System Property conditions (`@EnabledIfSystemProperty`, `@DisabledIfSystemProperty`)
   - Environment Variable conditions (`@EnabledIfEnvironmentVariable`, `@DisabledIfEnvironmentVariable`)
   - Custom conditions (`@EnabledIf`, `@DisabledIf`)

4. **What happens when a conditional test's condition is not met?**
   
   The test is skipped and marked as "disabled" or "skipped" in test results. It doesn't count as a failure or success, and any test logic inside the method is not executed.

5. **Can you apply multiple conditional annotations to a single test method?**
   
   Yes, you can apply multiple conditional annotations. The test will only execute if ALL conditions are satisfied (logical AND operation).

6. **How do conditional tests differ from the `@Disabled` annotation?**
   
   `@Disabled` statically disables tests - they never run. Conditional tests dynamically evaluate conditions at runtime - they may or may not run depending on the current environment and conditions.

7. **What is the relationship between conditional tests and test assumptions?**
   
   Both can skip tests, but conditional tests evaluate conditions before test execution starts, while assumptions are evaluated during test execution. Conditional tests are declarative (annotation-based), while assumptions are imperative (code-based).

8. **Can conditional annotations be applied at the class level?**
   
   Yes, conditional annotations can be applied at the class level, affecting all test methods in the class. If the class-level condition is not met, no tests in the class will execute.

## Operating System Conditions

9. **What are the two main annotations for OS-based conditional testing?**
   
   - `@EnabledOnOs` - runs tests only on specified operating systems
   - `@DisabledOnOs` - skips tests on specified operating systems

10. **Which operating systems are supported by the `OS` enum in JUnit 5?**
    
    - `OS.WINDOWS`
    - `OS.LINUX`
    - `OS.MAC`
    - `OS.SOLARIS`
    - `OS.AIX`
    - `OS.OTHER`

11. **How would you write a test that only runs on Linux or Mac?**
    
    ```java
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testOnLinuxOrMac() {
        // Test implementation
    }
    ```

12. **What's the difference between `@EnabledOnOs` and `@DisabledOnOs`?**
    
    `@EnabledOnOs` specifies which operating systems the test SHOULD run on (whitelist approach). `@DisabledOnOs` specifies which operating systems the test should NOT run on (blacklist approach).

13. **Can you specify multiple operating systems in a single annotation?**
    
    Yes, both annotations accept arrays: `@EnabledOnOs({OS.WINDOWS, OS.LINUX})` or `@DisabledOnOs({OS.MAC, OS.SOLARIS})`.

14. **How do you test platform-specific file system behaviors?**
    
    ```java
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testWindowsFilePathHandling() {
        // Test Windows-specific path separators, drive letters, etc.
    }
    
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testUnixFilePermissions() {
        // Test Unix-style file permissions
    }
    ```

15. **What are some real-world scenarios where OS-specific testing is necessary?**
    
    - File system path handling (Windows vs Unix separators)
    - Process management and system calls
    - Network interface behaviors
    - Security and permission models
    - Performance characteristics
    - Native library integrations

## Java Runtime Conditions

16. **What annotations are used for Java version-based conditional testing?**
    
    - `@EnabledOnJre` - runs tests only on specified Java versions
    - `@DisabledOnJre` - skips tests on specified Java versions

17. **Which Java versions are supported by the `JRE` enum?**
    
    `JRE.JAVA_8`, `JRE.JAVA_9`, `JRE.JAVA_10`, `JRE.JAVA_11`, `JRE.JAVA_17`, `JRE.JAVA_21`, and `JRE.OTHER` for newer versions.

18. **How would you write a test that runs on Java 11 or higher?**
    
    ```java
    @Test
    @EnabledOnJre({JRE.JAVA_11, JRE.JAVA_17, JRE.JAVA_21})
    void testJava11Features() {
        // Test Java 11+ features
    }
    ```

19. **What's the purpose of testing different Java versions?**
    
    - Ensure backward compatibility
    - Test version-specific features
    - Verify performance characteristics across versions
    - Test deprecated API usage
    - Validate migration paths

20. **How do you handle tests for features only available in newer Java versions?**
    
    ```java
    @Test
    @EnabledOnJre({JRE.JAVA_17, JRE.JAVA_21})
    void testRecordFeatures() {
        // Test Java 17+ record features
    }
    ```

21. **Can you test for Java version ranges using conditional annotations?**
    
    Not directly with a single annotation, but you can combine conditions or use custom conditions to achieve range testing.

22. **How do JRE-based conditions help with backward compatibility testing?**
    
    They allow you to maintain tests for older Java versions while adding new tests for newer features, ensuring your code works across supported Java versions.

## System Properties and Environment Variables

23. **What's the difference between system properties and environment variables in conditional testing?**
    
    System properties are Java-specific settings passed with `-D` flags, while environment variables are OS-level variables. System properties are more commonly used for Java application configuration.

24. **How do you set system properties for testing purposes?**
    
    - Command line: `java -Denv=test MyTest`
    - Maven: `mvn test -Denv=test`
    - Gradle: `gradle test -Denv=test`
    - IDE run configurations
    - Programmatically: `System.setProperty("env", "test")`

25. **What annotation would you use to run a test only when a specific environment variable is set?**
    
    ```java
    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_MODE", matches = "enabled")
    void testWhenEnabled() {
        // Test implementation
    }
    ```

26. **Can you use regular expressions in the `matches` parameter?**
    
    Yes, the `matches` parameter accepts regular expressions for pattern matching.

27. **How would you test for the absence of a system property or environment variable?**
    
    Use `@DisabledIfSystemProperty` or `@DisabledIfEnvironmentVariable` with a pattern that matches any value, or use custom conditions for more complex logic.

28. **What are some common system properties used in conditional testing?**
    
    - `env` (environment: dev, test, prod)
    - `os.name`, `os.version`
    - `java.version`
    - `user.home`, `user.dir`
    - Custom application properties

29. **How do you handle sensitive information in environment variable conditions?**
    
    - Use boolean flags instead of sensitive values
    - Check for presence rather than specific values
    - Use encrypted or encoded values
    - Implement custom conditions with secure handling

30. **Can you combine system property and environment variable conditions?**
    
    Yes, you can apply multiple conditional annotations to the same test method.

## Custom Conditions

31. **What annotations are used for custom conditional logic?**
    
    - `@EnabledIf` - runs test if custom method returns true
    - `@DisabledIf` - skips test if custom method returns true

32. **What are the requirements for custom condition methods?**
    
    - Must be `static`
    - Must return `boolean`
    - Must be accessible from the test class
    - Should not throw exceptions
    - Should be deterministic

33. **Can custom condition methods accept parameters?**
    
    No, custom condition methods cannot accept parameters. They must be parameterless static methods.

34. **How do you access test context information in custom condition methods?**
    
    Custom condition methods cannot directly access test context. You need to use system properties, environment variables, or static state to share information.

35. **What should custom condition methods return if the condition cannot be determined?**
    
    Return `false` for `@EnabledIf` (skip the test) or `true` for `@DisabledIf` (skip the test) to err on the side of caution.

36. **Can custom condition methods access instance variables?**
    
    No, because custom condition methods must be static. They cannot access instance variables or non-static methods.

37. **How do you test the custom condition methods themselves?**
    
    Write separate unit tests for the condition methods, testing them with different system states and configurations.

38. **What are some examples of complex custom conditions?**
    
    ```java
    static boolean isDatabaseAvailable() {
        // Check database connectivity
    }
    
    static boolean isFeatureFlagEnabled() {
        // Check feature flag service
    }
    
    static boolean hasRequiredPermissions() {
        // Check user permissions
    }
    ```

## Practical Applications

39. **When would you use conditional tests instead of separate test profiles?**
    
    - When conditions are simple and environment-based
    - When you want tests in the same class to run conditionally
    - When conditions are dynamic and can't be predetermined
    - For fine-grained control over individual tests

40. **How can conditional tests help with cross-platform development?**
    
    They allow you to maintain a single test suite that adapts to different platforms, testing platform-specific behaviors while sharing common test logic.

41. **What are some examples of environment-specific testing scenarios?**
    
    - Integration tests that require external services
    - Performance tests that need specific hardware
    - Security tests that require elevated permissions
    - UI tests that depend on display capabilities

42. **How do conditional tests relate to continuous integration pipelines?**
    
    CI pipelines can set environment variables or system properties to control which tests run in different stages or environments.

43. **When might you use conditional tests for performance testing?**
    
    - Skip resource-intensive tests in development environments
    - Run performance tests only on dedicated performance testing infrastructure
    - Test different performance characteristics on different platforms

44. **How do conditional tests support feature flag testing?**
    
    ```java
    @Test
    @EnabledIfSystemProperty(named = "feature.newAlgorithm", matches = "true")
    void testNewAlgorithm() {
        // Test new feature when flag is enabled
    }
    ```

45. **What role do conditional tests play in integration testing?**
    
    They allow integration tests to run only when external dependencies are available, preventing false failures due to infrastructure issues.

## Commission Calculator Context

46. **In the commission calculator example, why might you conditionally test user roles?**
    
    Different user roles might have access to different features or calculation methods. Conditional tests ensure role-specific functionality is tested appropriately.

47. **How could you use conditional tests for different commission calculation algorithms?**
    
    ```java
    @Test
    @EnabledIfSystemProperty(named = "commission.algorithm", matches = "v2")
    void testNewCommissionAlgorithm() {
        // Test new algorithm when enabled
    }
    ```

48. **What scenarios would require OS-specific testing in a commission calculator?**
    
    - File system operations for report generation
    - Integration with OS-specific payment systems
    - Platform-specific security requirements
    - Different decimal precision handling

49. **How might environment variables be used to control commission calculation testing?**
    
    ```java
    @Test
    @EnabledIfEnvironmentVariable(named = "PAYMENT_GATEWAY", matches = "live")
    void testLivePaymentIntegration() {
        // Test with live payment gateway
    }
    ```

50. **What custom conditions might be relevant for commission calculator testing?**
    
    ```java
    static boolean isQuarterEnd() {
        // Check if it's end of quarter for special calculations
    }
    
    static boolean hasMultiCurrencySupport() {
        // Check if multi-currency feature is enabled
    }
    ```

51. **How would you conditionally test multi-currency commission calculations?**
    
    ```java
    @Test
    @EnabledIf("hasMultiCurrencySupport")
    void testMultiCurrencyCommissions() {
        // Test multi-currency calculations
    }
    ```

52. **What conditions might determine whether to test external payment integrations?**
    
    - Availability of external payment service
    - Test environment configuration
    - API credentials availability
    - Network connectivity

## Best Practices

53. **What are the advantages of conditional tests over manual test selection?**
    
    - Automatic adaptation to environment
    - Consistent test execution across teams
    - Reduced human error in test selection
    - Better integration with CI/CD pipelines
    - Self-documenting test requirements

54. **How should you document conditional test requirements?**
    
    - Use descriptive `@DisplayName` annotations
    - Include comments explaining conditions
    - Document required environment setup
    - Maintain README files with condition explanations

55. **What's the recommended approach for tests that need multiple conditions?**
    
    Apply multiple conditional annotations or create custom condition methods that combine multiple checks for better readability.

56. **How do you ensure conditional tests are still discoverable and maintainable?**
    
    - Use clear naming conventions
    - Group related conditional tests
    - Document condition requirements
    - Regular review of conditional logic
    - Monitor test execution patterns

57. **What are some common pitfalls when using conditional tests?**
    
    - Over-complicating conditions
    - Tests that never run due to impossible conditions
    - Inconsistent condition evaluation
    - Poor documentation of requirements
    - Dependency on external state

58. **How do you balance conditional complexity with test readability?**
    
    - Keep conditions simple and focused
    - Use descriptive method names for custom conditions
    - Prefer built-in annotations over custom conditions when possible
    - Document complex conditional logic
    - Group related conditional tests

59. **What testing strategies work well with conditional execution?**
    
    - Environment-based test organization
    - Feature flag driven testing
    - Progressive test execution (basic → advanced)
    - Platform-specific test suites
    - Integration test isolation

60. **How do you handle conditional tests in code reviews?**
    
    - Verify condition logic is correct
    - Check that conditions are necessary
    - Ensure proper documentation
    - Validate test coverage across conditions
    - Review for potential maintenance issues

## Advanced Topics

61. **How do conditional tests interact with parameterized tests?**
    
    Conditional annotations can be applied to parameterized test methods, affecting all parameter combinations. Individual parameter sets cannot be conditionally executed.

62. **Can you use conditional tests with dynamic tests?**
    
    Yes, conditional annotations can be applied to methods that generate dynamic tests, affecting the entire dynamic test factory.

63. **How do conditional tests affect test execution order?**
    
    Conditional tests that are skipped don't participate in test ordering. Only tests that actually execute are subject to ordering constraints.

64. **What's the performance impact of evaluating many conditions?**
    
    Condition evaluation is generally fast, but complex custom conditions or many conditions can add overhead. Keep conditions simple and efficient.

65. **How do you debug issues with conditional test execution?**
    
    - Enable verbose test output
    - Log condition evaluation results
    - Use debugger to step through condition methods
    - Verify environment setup
    - Check system properties and environment variables

66. **Can conditional tests be nested within other conditional contexts?**
    
    Yes, you can have conditional tests within conditionally executed test classes, creating nested conditional logic.

67. **How do conditional tests work with test extensions and listeners?**
    
    Test extensions and listeners are notified about conditional test skipping, allowing them to react appropriately to skipped tests.

## Integration and Tooling

68. **How do IDEs handle conditional test execution and reporting?**
    
    Most modern IDEs show conditional tests with special icons, display skip reasons, and allow running tests with different conditions through run configurations.

69. **How can build tools be configured to work with conditional tests?**
    
    Build tools can set system properties, environment variables, and profiles to control conditional test execution across different build scenarios.

70. **What metrics should you track for conditional test execution?**
    
    - Percentage of tests skipped vs. executed
    - Condition evaluation frequency
    - Tests that never execute
    - Environment-specific test coverage
    - Condition failure rates

71. **How do conditional tests appear in test coverage reports?**
    
    Skipped tests typically don't contribute to coverage metrics. Some tools can track "conditional coverage" showing coverage across different execution scenarios.

72. **What are the implications of conditional tests for test automation?**
    
    Test automation must account for different execution scenarios and ensure appropriate conditions are set for different test environments.

73. **How do you ensure conditional tests run in appropriate CI/CD environments?**
    
    Configure CI/CD pipelines to set required environment variables and system properties for each environment and test stage.

## Troubleshooting

74. **What should you do when a conditional test never runs?**
    
    - Verify condition logic is correct
    - Check environment setup
    - Review system properties and environment variables
    - Test condition methods independently
    - Add logging to condition evaluation

75. **How do you verify that conditional logic is working correctly?**
    
    - Write tests for condition methods
    - Use logging in condition evaluation
    - Run tests in different environments
    - Monitor test execution reports
    - Use debugger to step through conditions

76. **What are common mistakes in condition evaluation?**
    
    - Incorrect regular expressions in matches
    - Case sensitivity issues
    - Null pointer exceptions in custom conditions
    - Dependency on external state
    - Race conditions in condition evaluation

77. **How do you handle flaky conditional tests?**
    
    - Identify root cause of flakiness
    - Make conditions more robust
    - Add retry logic where appropriate
    - Use more stable condition criteria
    - Consider alternative testing approaches

78. **What debugging techniques work best for conditional test issues?**
    
    - Enable verbose test output
    - Add logging to condition methods
    - Use system property debugging
    - Test conditions in isolation
    - Use IDE debugging capabilities

79. **How do you test the conditions themselves?**
    
    Write unit tests for custom condition methods, testing them with different system states and mock environments.

80. **What documentation helps troubleshoot conditional test problems?**
    
    - Clear condition requirements documentation
    - Environment setup guides
    - Troubleshooting guides for common issues
    - Examples of proper condition usage
    - FAQ for conditional test problems