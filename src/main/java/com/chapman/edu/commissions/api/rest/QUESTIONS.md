# REST API Implementation - Study Questions

This document contains questions to test your understanding of the REST API implementation concepts demonstrated in this package.

## Section 1: REST API Fundamentals

### Question 1.1: HTTP Methods and CRUD
What is the relationship between HTTP methods (GET, POST, PUT, DELETE) and CRUD operations? Why is it important to use the correct HTTP method for each operation?

### Question 1.2: HTTP Status Codes
In the API implementation, different HTTP status codes are returned based on the operation outcome:
- What is the difference between 200 OK and 201 Created?
- Why does DELETE return 204 No Content instead of 200 OK?
- When should you return 400 Bad Request vs 404 Not Found?

### Question 1.3: Resource Naming
The API uses resource-based URLs like `/api/v1/deals` rather than action-based URLs like `/api/v1/getDeals` or `/api/v1/createDeal`. Why is this considered a RESTful best practice?

### Question 1.4: Statelessness
REST APIs should be stateless. What does this mean, and how is it demonstrated in our servlet implementation?

## Section 2: Design Patterns

### Question 2.1: Repository Pattern
The code uses a Repository pattern with a generic `Repository<T>` interface.
- What problem does the Repository pattern solve?
- How does it improve testability?
- Why is the repository implemented as a generic type `Repository<T>` instead of separate interfaces for each entity?

### Question 2.2: Template Method Pattern
`BaseServlet` provides common functionality for all concrete servlets.
- What is the Template Method pattern?
- Identify three methods in `BaseServlet` that demonstrate this pattern
- How does this pattern support the DRY principle?

### Question 2.3: Dependency Injection
Servlets receive their repositories through constructor injection.
- What is Dependency Injection?
- What are the advantages of constructor injection over field injection?
- How does DI support the Dependency Inversion Principle?

### Question 2.4: Singleton Pattern
`JsonHelper` uses a single `Gson` instance.
- Why is the Gson instance declared as `private static final`?
- What are the benefits of using a singleton for JSON conversion?
- What would be the drawback of creating a new Gson instance for each JSON operation?

### Question 2.5: Embedded Server Pattern
The application uses an embedded Tomcat server rather than deploying to an external server.
- What are three advantages of the embedded server approach?
- What are potential disadvantages?
- When would you choose embedded vs external deployment?

## Section 3: SOLID Principles

### Question 3.1: Single Responsibility Principle (SRP)
- Identify three classes in the implementation and describe the single responsibility of each
- What would violate SRP? Give an example of bad design
- How does SRP make code easier to maintain?

### Question 3.2: Open/Closed Principle (OCP)
- How is `BaseServlet` open for extension but closed for modification?
- If you needed to add a new resource type (e.g., PaymentServlet), what would you need to change in the existing code?
- How does this demonstrate OCP?

### Question 3.3: Liskov Substitution Principle (LSP)
- Can any `Repository<Deal>` implementation be substituted for `InMemoryRepository<Deal>` without breaking the code?
- What contract must all Repository implementations fulfill?
- Give an example of a Repository implementation that would violate LSP

### Question 3.4: Interface Segregation Principle (ISP)
- Does the `Repository<T>` interface follow ISP? Why or why not?
- What would be a violation of ISP in this context?
- How would you refactor if a client only needs read operations?

### Question 3.5: Dependency Inversion Principle (DIP)
- How do servlets depend on abstractions rather than concrete implementations?
- Draw the dependency diagram showing how DealServlet, Repository, and InMemoryRepository relate
- What would happen if DealServlet directly instantiated InMemoryRepository?

## Section 4: Thread Safety and Concurrency

### Question 4.1: ConcurrentHashMap
`InMemoryRepository` uses `ConcurrentHashMap` instead of `HashMap`.
- Why is this necessary in a web server environment?
- What problems could occur with a regular HashMap?
- What guarantees does ConcurrentHashMap provide?

### Question 4.2: AtomicLong
The repository uses `AtomicLong` for ID generation.
- Why not use a regular `long` or `Long`?
- What race condition could occur without atomic operations?
- What other approaches could ensure unique ID generation?

### Question 4.3: Thread Safety in Servlets
Are servlets thread-safe by default? What implications does this have for instance variables in servlet classes?

## Section 5: JSON Serialization

### Question 5.1: Custom Type Adapters
`JsonHelper` registers custom adapters for `LocalDate` and `LocalDateTime`.
- Why are these custom adapters necessary?
- What format are dates serialized to?
- What would happen without these adapters?

### Question 5.2: Serialization Configuration
The Gson instance is configured with `setPrettyPrinting()` and `serializeNulls()`.
- What does each configuration do?
- What are the tradeoffs of pretty printing in production?
- When would you want to exclude nulls from JSON output?

## Section 6: Servlet Architecture

### Question 6.1: Servlet Lifecycle
- What happens when ApiServer starts and registers a servlet?
- How many instances of DealServlet exist during the application lifecycle?
- When are the doGet/doPost/doPut/doDelete methods called?

### Question 6.2: URL Mapping
Servlets are mapped to URL patterns like `/api/v1/deals/*`.
- What does the `/*` wildcard mean?
- How does `extractResourceId()` parse the ID from the URL?
- What URL would match this pattern? What wouldn't?

### Question 6.3: Request/Response Handling
- What is the purpose of setting `Content-Type: application/json`?
- Why do we need to call `response.getWriter().flush()`?
- What happens if you forget to set the response status code?

## Section 7: Error Handling

### Question 7.1: HTTP Status Codes for Errors
- When should you return 400 vs 500 status codes?
- In `DealServlet.doPost()`, why is a parsing exception a 400 error?
- What errors should result in a 500 Internal Server Error?

### Question 7.2: Error Response Format
The API returns structured error responses with status, message, and timestamp.
- Why provide structured errors instead of plain text?
- What information should error responses include?
- How does this help API clients?

## Section 8: Generic Programming

### Question 8.1: Generic Repository
`Repository<T>` and `InMemoryRepository<T>` use generics.
- What is the benefit of making Repository generic?
- How does type safety work with generics?
- Could you create a `Repository<String>`? Would it make sense?

### Question 8.2: Functional Interfaces
`InMemoryRepository` constructor accepts `Function<T, String>` and `BiConsumer<T, String>`.
- What are these functional interfaces used for?
- Why pass functions as parameters instead of using reflection?
- How does this support different entity types?

## Section 9: API Design Best Practices

### Question 9.1: Filtering and Query Parameters
The API supports filtering like `GET /deals?status=WON&salesRepId=USER-001`.
- Why use query parameters for filtering instead of path parameters?
- Should filtering be implemented in the servlet or repository layer? Why?
- How would you add pagination support?

### Question 9.2: API Versioning
The API is versioned with `/api/v1/`.
- Why include version in the URL?
- What happens when you need to make breaking changes?
- What are alternative versioning strategies?

### Question 9.3: Idempotency
- Which HTTP methods should be idempotent?
- Is our PUT implementation idempotent? Why or why not?
- Why is idempotency important for APIs?

## Section 10: Testing and Quality

### Question 10.1: Unit Testing Servlets
How would you unit test `DealServlet.doGet()` without starting the server?
- What would you mock?
- What test cases should you write?
- How does the repository abstraction help testing?

### Question 10.2: Integration Testing
How would you test the full API end-to-end?
- What tools could you use?
- How would you verify the correct HTTP status codes?
- Should you test with the in-memory repository or a real database?

### Question 10.3: Code Coverage
What parts of the code would be hardest to test? Why?

## Section 11: OpenAPI Specification

### Question 11.1: OpenAPI Purpose
- What is the purpose of the `openapi.yaml` file?
- Who are the consumers of this specification?
- What tools can use OpenAPI specifications?

### Question 11.2: Schema Definition
In OpenAPI, schemas define the structure of request/response bodies.
- How do schemas help API consumers?
- What is the relationship between OpenAPI schemas and Java model classes?
- Could you generate Java code from OpenAPI schemas? Vice versa?

## Section 12: Architecture and Layering

### Question 12.1: Separation of Concerns
The implementation separates HTTP handling, business logic, and data access.
- Why is this separation important?
- What would be wrong with putting all logic in the servlet?
- How does layering support maintainability?

### Question 12.2: Scalability Considerations
The current implementation uses in-memory storage.
- What limitations does this create?
- How would you modify the code to use a database?
- Would you need to change the servlets? Why or why not?

### Question 12.3: Production Readiness
What additional features would be needed for production deployment?
- Security and authentication
- Logging and monitoring
- Rate limiting
- Caching
- Error tracking

## Bonus Questions

### Bonus 1: RESTful Maturity Model
The Richardson Maturity Model defines levels of REST compliance (0-3). What level does this API implementation achieve? What would be needed for the next level?

### Bonus 2: Alternative Architectures
How would this implementation differ if you used:
- Spring Boot instead of plain servlets?
- JAX-RS (Jersey) instead of servlets?
- GraphQL instead of REST?

### Bonus 3: Microservices
If you were to split this into microservices, how would you divide the resources? What challenges would this introduce?

### Bonus 4: Performance Optimization
What performance optimizations could you apply to this implementation?
- Caching strategies
- Connection pooling
- Async request handling
- Response compression
