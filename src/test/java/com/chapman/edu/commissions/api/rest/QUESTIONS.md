# Integration Testing - Study Questions

This document contains questions to test your understanding of the integration testing concepts demonstrated in the REST API tests.

## Section 1: Integration Testing Fundamentals

### Question 1.1: Unit vs Integration Tests
What is the difference between unit tests and integration tests? Give specific examples from our codebase of what would be a unit test vs an integration test for the DealServlet.

### Question 1.2: Test Scope
Our integration tests start an actual Tomcat server and make real HTTP requests. What are the advantages and disadvantages of this approach compared to mocking the HTTP layer?

### Question 1.3: Test Independence
Why is it important that each integration test can run independently? What problems could occur if tests depend on each other's execution order or state?

### Question 1.4: Test Data Management
How do our integration tests handle test data? What would be the challenges if we used a real database instead of in-memory storage?

## Section 2: Test Fixture Pattern

### Question 2.1: Test Fixture Purpose
What is the Test Fixture pattern, and how is `ApiIntegrationTestBase` an example of this pattern?

### Question 2.2: @BeforeAll and @AfterAll
Why do we use `@BeforeAll` and `@AfterAll` instead of `@BeforeEach` and `@AfterEach` for server lifecycle management?

### Question 2.3: Static Methods and Fields
Why are the server startup/shutdown methods and fields declared as `static` in `ApiIntegrationTestBase`?

### Question 2.4: Test Fixture Inheritance
What are the benefits of having all integration test classes extend `ApiIntegrationTestBase`? What would be the drawbacks of duplicating this code in each test class?

## Section 3: AAA Pattern (Arrange-Act-Assert)

### Question 3.1: AAA Structure
Explain the AAA (Arrange-Act-Assert) pattern. Identify the three sections in the `createDeal_validData_returns201()` test method.

### Question 3.2: Arrange Phase
In the "Arrange" phase of `updateDeal_existingDeal_returns200()`, why do we create a deal first before testing the update?

### Question 3.3: Act Phase
What happens in the "Act" phase? Why is it important to keep this phase minimal (ideally one method call)?

### Question 3.4: Assert Phase
Our tests use multiple assertions. Is this a violation of the "one assertion per test" guideline? Why or why not?

## Section 4: HTTP Testing

### Question 4.1: Java HttpClient
Why do we use Java's built-in `HttpClient` instead of libraries like Apache HttpClient or OkHttp?

### Question 4.2: Request Building
Examine the `post()` helper method in `ApiIntegrationTestBase`. What headers does it set, and why are they necessary?

### Question 4.3: Response Handling
How does `parseResponse()` convert the JSON response body into a Java object? What library is being used?

### Question 4.4: Status Code Verification
Why is verifying HTTP status codes important in integration tests? Give examples of what different status codes indicate.

## Section 5: Test Naming Conventions

### Question 5.1: Naming Pattern
Our tests follow the naming pattern `methodName_scenario_expectedResult`. Explain each part of this pattern using the test name `getDealById_nonExistentId_returns404`.

### Question 5.2: @DisplayName Annotation
What is the purpose of the `@DisplayName` annotation? How does it differ from the method name?

### Question 5.3: Self-Documenting Tests
How do good test names serve as documentation? What information should a test name convey?

## Section 6: Test Execution Order

### Question 6.1: @Order Annotation
Why do our tests use `@Order` annotations? Isn't test order independence a best practice?

### Question 6.2: Ordered Execution Benefits
What are the practical benefits of ordering tests, especially for integration tests? When is it acceptable to violate test independence?

### Question 6.3: State Dependencies
Some tests create data that later tests rely on. How does this affect test reliability? What could go wrong?

## Section 7: Sample Data Loading

### Question 7.1: SampleDataLoader Purpose
What is the purpose of the `SampleDataLoader` class? When would you use it?

### Question 7.2: Test Data Builder Pattern
How does `SampleDataLoader` implement the Test Data Builder pattern? What makes this pattern useful?

### Question 7.3: Realistic vs Minimal Data
`SampleDataLoader` creates realistic, complex test data. When would you prefer minimal test data instead?

### Question 7.4: Data Cleanup
Should integration tests clean up their test data after execution? What are the tradeoffs?

## Section 8: Error Testing

### Question 8.1: Testing Error Paths
Why is it important to test error scenarios (404, 400) in addition to success scenarios?

### Question 8.2: Error Response Format
The test `createDeal_invalidJson_returns400()` verifies that invalid JSON returns 400. What else should we verify about the error response?

### Question 8.3: Boundary Conditions
What boundary conditions or edge cases should we test for the Deal API? Give specific examples.

## Section 9: Query Parameter Testing

### Question 9.1: Filter Testing
The test `getDeals_filterByStatus_returnsFilteredResults()` verifies filtering works. What assertions does it make?

### Question 9.2: Multiple Parameters
How would you test filtering by multiple query parameters (e.g., `?status=WON&salesRepId=USER-001`)?

### Question 9.3: Invalid Parameters
What should happen when a client provides an invalid query parameter value? How would you test this?

## Section 10: Test Coverage

### Question 10.1: CRUD Coverage
Our `DealServletIntegrationTest` tests all CRUD operations. Why is complete CRUD coverage important for integration tests?

### Question 10.2: Happy Path vs Edge Cases
What percentage of tests should focus on happy path vs error cases? Why?

### Question 10.3: Integration vs Unit Test Coverage
Should integration tests aim for 100% code coverage? Why or why not? How does this differ from unit test coverage goals?

## Section 11: Postman Collection

### Question 11.1: Postman Collection Purpose
What is the purpose of the `rest.json` Postman collection? Who are the intended users?

### Question 11.2: Collection Variables
The Postman collection uses variables like `{{baseUrl}}` and `{{dealId}}`. What are the benefits of using variables?

### Question 11.3: Automated Tests in Postman
The Postman requests include test scripts (e.g., checking status codes). How do these compare to JUnit integration tests?

### Question 11.4: Postman vs JUnit
When would you use Postman tests vs JUnit integration tests? What are the strengths of each approach?

## Section 12: Test Performance

### Question 12.1: Test Execution Speed
Integration tests that start a real server are slower than unit tests. What techniques can make them faster?

### Question 12.2: Server Startup Cost
Our tests start the server once per test class (`@BeforeAll`). What would be the impact of starting/stopping the server for each test method (`@BeforeEach`/`@AfterEach`)?

### Question 12.3: Parallel Execution
Could we run integration test classes in parallel? What challenges would this introduce?

## Section 13: Continuous Integration

### Question 13.1: CI Pipeline Integration
How would these integration tests fit into a CI/CD pipeline? At what stage should they run?

### Question 13.2: Test Environment
What environment requirements do these tests have? How does this affect CI setup?

### Question 13.3: Flaky Tests
Integration tests can be "flaky" (sometimes pass, sometimes fail). What could cause flakiness in our tests?

## Section 14: Testing Best Practices

### Question 14.1: Test Isolation Techniques
List three techniques our tests use to ensure isolation from production systems.

### Question 14.2: Given-When-Then
How does the AAA pattern relate to the Given-When-Then pattern from BDD?

### Question 14.3: Test Readability
What makes a test readable? Evaluate the readability of `createDeal_validData_returns201()`.

## Section 15: Advanced Integration Testing

### Question 15.1: Testing Concurrent Requests
How would you test that the API handles concurrent requests correctly? What specific scenarios should you test?

### Question 15.2: Performance Testing
Our integration tests verify functionality. How would you add performance assertions (e.g., response time)?

### Question 15.3: Security Testing
What security aspects should integration tests cover? Give examples specific to our API.

### Question 15.4: Database Integration
If we swapped `InMemoryRepository` for a real database repository, what would change in our tests?

## Bonus Questions

### Bonus 1: Contract Testing
What is contract testing, and how does it differ from integration testing? Would contract tests be useful for our API?

### Bonus 2: Test Containers
TestContainers is a library for running Docker containers in tests. How could TestContainers improve our integration tests?

### Bonus 3: Mocking vs Real HTTP
Some teams use MockMvc (Spring) or similar to test HTTP endpoints without starting a server. Compare this approach to our real HTTP approach.

### Bonus 4: API Versioning Tests
If we added `/api/v2` endpoints, how would you structure integration tests to cover both versions?

### Bonus 5: Test Data Factories
Compare the Test Data Builder pattern (SampleDataLoader) with the Factory pattern. When would you use each?