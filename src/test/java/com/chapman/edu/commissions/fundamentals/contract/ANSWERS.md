# Answers to Design by Contract Questions

## Conceptual Questions

### 1. What is the main difference between pre-conditions and post-conditions in Design by Contract?

**Pre-conditions** specify what must be true before a method can execute. They are the client's responsibility and represent the obligations that a client must fulfill before calling the method.

**Post-conditions** specify what must be true after a method has executed. They are the method's responsibility and represent the guarantees that the method makes to its clients.

For example, in the `Deal.addProduct` method:
- Pre-condition: The product parameter must not be null.
- Post-condition: After execution, the products list contains the added product.

### 2. How do class invariants differ from pre-conditions and post-conditions?

**Class invariants** are conditions that must be true for all instances of a class at all stable states (i.e., before and after any public method execution). Unlike pre-conditions and post-conditions which apply to specific methods, invariants apply to the entire class.

For example, in the `Deal` class:
- Invariant: The deal's status must always be a valid DealStatus.
- Invariant: The deal's title must never be null.

Pre-conditions and post-conditions can change from method to method, but invariants must be maintained by all methods of the class.

### 3. In Design by Contract, who is responsible for ensuring that pre-conditions are met? The caller or the method being called?

In Design by Contract, the **caller** is responsible for ensuring that pre-conditions are met before calling a method. The method being called can assume that its pre-conditions are satisfied and doesn't need to check them again.

This is different from defensive programming, where the method would check its inputs regardless of what the caller does.

### 4. What happens if a pre-condition is violated? What about a post-condition?

If a **pre-condition** is violated, it indicates a bug in the caller's code. The method is not obligated to handle this case and can behave unpredictably, throw an exception, or terminate the program.

If a **post-condition** is violated, it indicates a bug in the method's implementation. The method has failed to fulfill its contract and the caller cannot rely on the expected results.

In practice, both violations often result in exceptions being thrown, but they indicate different types of bugs.

### 5. How does Design by Contract relate to defensive programming? Are they complementary or contradictory approaches?

Design by Contract and defensive programming are somewhat contradictory approaches:

- **Design by Contract** assumes that clients will fulfill their obligations (pre-conditions) and focuses on ensuring that the method fulfills its guarantees (post-conditions).

- **Defensive programming** assumes that clients might not fulfill their obligations and focuses on handling all possible inputs, even invalid ones.

However, they can be complementary in some contexts:

- Use contracts to clearly define the expected behavior
- Use defensive programming in public APIs or when interacting with external systems where you can't control the caller's behavior

## Implementation Questions

### 6. How would you implement Design by Contract in a language that doesn't have built-in support for it (like Java)?

In Java, you can implement Design by Contract using:

1. **Assertions**: Use `assert` statements to check conditions
2. **Explicit checks**: Use `if` statements with exceptions
3. **Annotations**: Create custom annotations to document contracts
4. **AspectJ or other AOP tools**: Implement contract checking as cross-cutting concerns

Example using explicit checks:

```java
public void addProduct(DealProduct product) {
    // Pre-condition
    Objects.requireNonNull(product, "Product cannot be null");

    // Method implementation
    this.products.add(product);

    // Post-condition
    assert this.products.contains(product) : "Product was not added to the list";
}
```

### 7. What are some common patterns or techniques for enforcing contracts in code?

1. **Guard clauses**: Check pre-conditions at the beginning of methods and return early or throw exceptions if they're not met

```java
public void setQuantity(int quantity) {
    // Guard clause for pre-condition
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    this.quantity = quantity;
}
```

2. **Result validation**: Check post-conditions before returning results

```java
public BigDecimal calculateTotalValue() {
    BigDecimal result = products.stream()
            .map(product -> product.getPrice().multiply(new BigDecimal(product.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Post-condition check
    assert result.compareTo(BigDecimal.ZERO) >= 0 : "Total value cannot be negative";
    return result;
}
```

3. **State validation**: Check invariants at the end of methods that modify state

```java
private void checkInvariants() {
    assert status != null : "Status cannot be null";
    assert title != null : "Title cannot be null";
    assert salesRepId != null : "Sales rep ID cannot be null";
}
```

### 8. How would you handle contract violations in a production environment? Should they throw exceptions, log errors, or something else?

In a production environment, contract violations should be handled based on their severity and the application's requirements:

1. **Pre-condition violations**: Throw checked or unchecked exceptions depending on whether the caller is expected to recover

```java
public void setPrice(BigDecimal price) {
    if (price == null) {
        throw new NullPointerException("Price cannot be null");
    }
    if (price.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Price cannot be negative");
    }
    this.price = price;
}
```

2. **Post-condition violations**: Log the error, throw an exception, and possibly alert operations

```java
public BigDecimal calculateTotalPrice() {
    BigDecimal result = price.multiply(new BigDecimal(quantity)).subtract(discount);
    if (result.compareTo(BigDecimal.ZERO) < 0) {
        logger.error("Post-condition violation: Total price is negative");
        throw new IllegalStateException("Contract violation: Total price cannot be negative");
    }
    return result;
}
```

3. **Invariant violations**: These are serious bugs and should throw exceptions, log detailed information, and possibly terminate the application

### 9. How do you test that your contract implementations are working correctly?

To test contract implementations:

1. **Test pre-conditions** by providing invalid inputs and verifying that appropriate exceptions are thrown

```java
@Test
void testSetQuantity_PreCondition() {
    DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00"));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        product.setQuantity(0);
    });

    assertEquals("Quantity must be greater than zero", exception.getMessage());
}
```

2. **Test post-conditions** by calling methods with valid inputs and verifying that the expected state changes occur

```java
@Test
void testAddProduct_PostCondition() {
    Deal deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");
    DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00"));

    deal.addProduct(product);

    assertTrue(deal.getProducts().contains(product));
}
```

3. **Test invariants** by calling methods that might violate them and verifying that appropriate exceptions are thrown

### 10. Can Design by Contract be applied to interfaces and abstract classes? If so, how?

Yes, Design by Contract can be applied to interfaces and abstract classes:

1. **Document contracts in interface method Javadoc**:

```java
/**
 * Calculates the total value of the deal.
 * 
 * @return the total value of all products in the deal
 * @throws NullPointerException if the products list is null
 * @ensures result >= 0
 */
BigDecimal calculateTotalValue();
```

2. **Use default methods in interfaces to enforce common pre-conditions**:

```java
public interface DealOperations {
    default void validateProduct(DealProduct product) {
        Objects.requireNonNull(product, "Product cannot be null");
    }

    void addProduct(DealProduct product);
}
```

3. **Implement contract checking in abstract classes**:

```java
public abstract class AbstractDeal {
    protected void checkInvariants() {
        // Check common invariants
    }

    public final void addProduct(DealProduct product) {
        // Check pre-conditions
        Objects.requireNonNull(product, "Product cannot be null");

        // Call template method
        doAddProduct(product);

        // Check post-conditions and invariants
        checkInvariants();
    }

    protected abstract void doAddProduct(DealProduct product);
}
```

## Application Questions

### 11. How would you apply Design by Contract to the `CommissionCalculation` class in our model? What pre-conditions, post-conditions, and invariants would you define?

For the `CommissionCalculation` class:

**Pre-conditions**:
- The commission plan must not be null
- The deal must not be null
- The deal must be in CLOSED status to calculate commission

**Post-conditions**:
- The calculated commission amount must not be negative
- The calculation date must be set
- The commission amount must be calculated according to the rules in the plan

**Invariants**:
- The commission calculation must always have a valid plan
- The commission calculation must always have a valid deal
- The commission amount must always be non-negative

Example implementation:

```java
public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
    // Pre-conditions
    Objects.requireNonNull(deal, "Deal cannot be null");
    Objects.requireNonNull(plan, "Commission plan cannot be null");
    if (deal.getStatus() != DealStatus.CLOSED) {
        throw new IllegalArgumentException("Deal must be closed to calculate commission");
    }

    // Calculate commission
    BigDecimal commissionAmount = calculateCommissionAmount(deal, plan);

    // Post-conditions
    if (commissionAmount.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalStateException("Commission amount cannot be negative");
    }

    this.calculationDate = LocalDate.now();
    this.commissionAmount = commissionAmount;

    return commissionAmount;
}
```

### 12. Consider the `Dispute` class in our model. What contracts would be appropriate for its methods?

For the `Dispute` class:

**Pre-conditions**:
- When creating a dispute, the commission calculation must not be null
- When adding a comment, the comment text must not be empty
- When changing status, the new status must be a valid transition from the current status

**Post-conditions**:
- After creating a dispute, the status must be OPEN
- After adding a comment, the comments list must contain the new comment
- After changing status, the status must be updated and the last modified date must be updated

**Invariants**:
- The dispute must always have a valid commission calculation
- The dispute must always have a valid status
- The dispute must always have a creation date

Example implementation:

```java
public void addComment(DisputeComment comment) {
    // Pre-conditions
    Objects.requireNonNull(comment, "Comment cannot be null");
    if (comment.getText() == null || comment.getText().trim().isEmpty()) {
        throw new IllegalArgumentException("Comment text cannot be empty");
    }

    // Add comment
    this.comments.add(comment);
    this.lastModifiedDate = LocalDate.now();

    // Post-conditions
    assert this.comments.contains(comment) : "Comment was not added";
}

public void changeStatus(DisputeStatus newStatus) {
    // Pre-conditions
    Objects.requireNonNull(newStatus, "New status cannot be null");
    if (!isValidStatusTransition(this.status, newStatus)) {
        throw new IllegalArgumentException("Invalid status transition from " + this.status + " to " + newStatus);
    }

    // Change status
    this.status = newStatus;
    this.lastModifiedDate = LocalDate.now();

    // Post-conditions
    assert this.status == newStatus : "Status was not updated";
}

private boolean isValidStatusTransition(DisputeStatus currentStatus, DisputeStatus newStatus) {
    // Define valid transitions
    if (currentStatus == DisputeStatus.OPEN) {
        return newStatus == DisputeStatus.IN_REVIEW || newStatus == DisputeStatus.REJECTED;
    } else if (currentStatus == DisputeStatus.IN_REVIEW) {
        return newStatus == DisputeStatus.APPROVED || newStatus == DisputeStatus.REJECTED;
    }
    return false;
}
```

### 13. How would Design by Contract help prevent bugs in the commission calculation process?

Design by Contract would help prevent bugs in the commission calculation process by:

1. **Ensuring valid inputs**: Pre-conditions would ensure that all inputs to the calculation process are valid, preventing null pointer exceptions and calculation errors.

```java
public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
    Objects.requireNonNull(deal, "Deal cannot be null");
    Objects.requireNonNull(plan, "Commission plan cannot be null");
    if (deal.getStatus() != DealStatus.CLOSED) {
        throw new IllegalArgumentException("Deal must be closed to calculate commission");
    }
    // ...
}
```

2. **Validating calculation results**: Post-conditions would ensure that the calculated commission amounts are valid and consistent with business rules.

```java
public BigDecimal applyCommissionRule(CommissionRule rule, BigDecimal dealValue) {
    // Apply rule
    BigDecimal commission = dealValue.multiply(rule.getRate());

    // Ensure commission is valid
    if (commission.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalStateException("Commission cannot be negative");
    }
    if (rule.hasMaximum() && commission.compareTo(rule.getMaximumAmount()) > 0) {
        commission = rule.getMaximumAmount();
    }

    return commission;
}
```

3. **Maintaining consistent state**: Invariants would ensure that the commission calculation objects maintain a consistent state throughout their lifecycle.

4. **Clarifying responsibilities**: Contracts would clearly define the responsibilities of each component in the calculation process, making it easier to identify and fix bugs.

5. **Providing better error messages**: Contract violations would provide specific error messages that help identify the root cause of bugs.

### 14. How might Design by Contract impact the performance of our application? Are there ways to minimize any negative impact?

Design by Contract might impact performance in several ways:

**Potential negative impacts**:
1. Additional runtime checks add overhead
2. Exception creation and throwing is expensive
3. Complex contract validation might be computationally intensive

**Ways to minimize negative impact**:

1. **Use assertions for internal contracts**: Assertions can be disabled in production

```java
private void checkInvariants() {
    assert status != null : "Status cannot be null";
    assert title != null : "Title cannot be null";
}
```

2. **Only check critical pre-conditions in production**: Focus on checks that prevent data corruption or security issues

```java
public void setDiscount(BigDecimal discount) {
    // Always check null (prevents NullPointerException)
    Objects.requireNonNull(discount, "Discount cannot be null");

    // Only check in development or for critical business rules
    assert discount.compareTo(BigDecimal.ZERO) >= 0 : "Discount cannot be negative";

    this.discount = discount;
}
```

3. **Use compile-time contracts where possible**: Leverage the type system and static analysis

4. **Implement contract checking using AspectJ or similar tools**: This allows enabling/disabling contract checking without modifying the code

5. **Cache complex validation results**: If a contract check is expensive, cache the result

### 15. How would you document the contracts in your code? Should they be in comments, separate documentation, or enforced in the code itself?

Contracts should be documented using a combination of approaches:

1. **Javadoc comments** to describe the contract in human-readable form:

```java
/**
 * Adds a product to this deal.
 * 
 * @param product the product to add, must not be null
 * @throws NullPointerException if product is null
 * @ensures getProducts().contains(product)
 */
public void addProduct(DealProduct product) {
    // Implementation
}
```

2. **Code-level enforcement** to ensure the contract is actually checked:

```java
public void addProduct(DealProduct product) {
    // Pre-condition
    Objects.requireNonNull(product, "Product cannot be null");

    // Implementation
    this.products.add(product);

    // Post-condition (in development mode)
    assert this.products.contains(product) : "Product was not added";
}
```

3. **Unit tests** to verify that contracts are enforced:

```java
@Test
void testAddProduct_PreCondition() {
    Deal deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");

    Exception exception = assertThrows(NullPointerException.class, () -> {
        deal.addProduct(null);
    });

    assertEquals("Product cannot be null", exception.getMessage());
}
```

4. **Separate documentation** for complex contracts or business rules that don't fit well in code comments

The best approach is to have the contracts both documented in comments and enforced in code, with tests to verify the enforcement.

## Advanced Questions

### 16. How does Design by Contract relate to other software design principles like SOLID or DRY?

Design by Contract complements other software design principles:

**Single Responsibility Principle (SRP)**:
- Contracts help define the single responsibility of a class or method
- Clear pre/post-conditions make it easier to identify when a class has too many responsibilities

**Open/Closed Principle (OCP)**:
- Contracts provide a stable interface while allowing implementation details to change
- As long as the contract is maintained, the implementation can be extended without modifying existing code

**Liskov Substitution Principle (LSP)**:
- Contracts formalize the LSP by defining what behaviors subclasses must maintain
- In proper contract inheritance, subclasses can weaken pre-conditions and strengthen post-conditions

**Interface Segregation Principle (ISP)**:
- Contracts help identify when interfaces are too large by revealing unrelated pre/post-conditions
- Smaller, focused interfaces tend to have more cohesive contracts

**Dependency Inversion Principle (DIP)**:
- Contracts define the expectations between high-level and low-level modules
- Abstract contracts allow for dependency inversion without ambiguity

**Don't Repeat Yourself (DRY)**:
- Contract checking code can be centralized to avoid repetition
- Common contract patterns can be extracted into reusable utilities

### 17. Can Design by Contract be used in conjunction with Test-Driven Development (TDD)? If so, how?

Yes, Design by Contract can be used with TDD. Here's how they can work together:

1. **Write contract tests first**: Start by writing tests that verify the contracts

```java
@Test
void testAddProduct_PreCondition() {
    Deal deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");

    Exception exception = assertThrows(NullPointerException.class, () -> {
        deal.addProduct(null);
    });
}
```

2. **Implement the contracts**: Make the tests pass by implementing the contract checks

```java
public void addProduct(DealProduct product) {
    Objects.requireNonNull(product, "Product cannot be null");
    // Stub implementation
}
```

3. **Write functional tests**: Add tests for the actual functionality

```java
@Test
void testAddProduct_AddsProductToList() {
    Deal deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");
    DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00"));

    deal.addProduct(product);

    assertTrue(deal.getProducts().contains(product));
}
```

4. **Implement the functionality**: Complete the implementation to make all tests pass

```java
public void addProduct(DealProduct product) {
    Objects.requireNonNull(product, "Product cannot be null");
    this.products.add(product);
}
```

5. **Refactor**: Improve the code while maintaining the contracts and passing the tests

This approach ensures that both the contracts and the functionality are properly tested and implemented.

### 18. How would you handle contract inheritance in object-oriented programming? For example, if a subclass overrides a method, what happens to the contracts?

In contract inheritance, the Liskov Substitution Principle dictates that:

1. **Pre-conditions can be weakened** in subclasses (accept more inputs)
2. **Post-conditions can be strengthened** in subclasses (provide stronger guarantees)
3. **Invariants must be maintained** in subclasses

Example:

```java
// Base class
public class Deal {
    /**
     * @param product must not be null
     * @ensures getProducts().contains(product)
     */
    public void addProduct(DealProduct product) {
        Objects.requireNonNull(product, "Product cannot be null");
        this.products.add(product);
    }
}

// Subclass
public class SpecialDeal extends Deal {
    /**
     * @param product must not be null
     * @ensures getProducts().contains(product)
     * @ensures getLastModifiedDate() is updated
     */
    @Override
    public void addProduct(DealProduct product) {
        // Call parent implementation (maintains parent's pre/post-conditions)
        super.addProduct(product);

        // Add stronger post-condition
        this.lastModifiedDate = LocalDate.now();
    }
}
```

To enforce this in code:

1. **Use template method pattern** to allow subclasses to extend behavior while maintaining contracts

```java
public class Deal {
    // Final method enforces the contract
    public final void addProduct(DealProduct product) {
        // Pre-condition
        Objects.requireNonNull(product, "Product cannot be null");

        // Template method for subclasses to override
        doAddProduct(product);

        // Post-condition
        assert this.products.contains(product) : "Product was not added";
    }

    // Subclasses override this method
    protected void doAddProduct(DealProduct product) {
        this.products.add(product);
    }
}
```

2. **Use composition over inheritance** when contract compatibility is difficult to maintain

### 19. How might Design by Contract be applied in a microservices architecture where different services are developed by different teams?

In a microservices architecture, Design by Contract can be applied at the service interface level:

1. **API Contracts**: Define clear contracts for REST, gRPC, or message-based APIs

```java
/**
 * Creates a new deal.
 * 
 * @param dealRequest must contain a valid title and salesRepId
 * @return the created deal with a generated ID
 * @throws IllegalArgumentException if the request is invalid
 * @ensures response.id is not null
 */
@POST
@Path("/deals")
public Response createDeal(DealRequest dealRequest) {
    // Validate request
    if (dealRequest.getTitle() == null || dealRequest.getTitle().trim().isEmpty()) {
        throw new IllegalArgumentException("Deal title is required");
    }
    if (dealRequest.getSalesRepId() == null) {
        throw new IllegalArgumentException("Sales rep ID is required");
    }

    // Create deal
    Deal deal = dealService.createDeal(dealRequest);

    // Return response
    return Response.status(Response.Status.CREATED)
            .entity(new DealResponse(deal))
            .build();
}
```

2. **Consumer-Driven Contracts**: Use tools like Pact or Spring Cloud Contract to define and test contracts between services

3. **API Documentation**: Use OpenAPI/Swagger to document the contracts

4. **Schema Validation**: Use JSON Schema or Protocol Buffers to enforce message formats

5. **Circuit Breakers**: Implement circuit breakers to handle contract violations gracefully

6. **Versioning**: Version APIs to manage contract changes

7. **Contract Testing**: Implement automated tests to verify that services adhere to their contracts

Example of a consumer-driven contract test:

```java
@Test
void verifyDealCreationContract() {
    // Define the expected request and response
    mockServer.expect(requestTo("/deals"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.salesRepId").exists())
            .andRespond(withStatus(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"id\":\"deal-1\",\"title\":\"Test Deal\",\"status\":\"OPEN\"}"));

    // Test the client
    DealRequest request = new DealRequest("Test Deal", "sales-rep-1");
    DealResponse response = dealClient.createDeal(request);

    // Verify the response
    assertNotNull(response.getId());
    assertEquals("Test Deal", response.getTitle());
    assertEquals("OPEN", response.getStatus());
}
```

### 20. What are the limitations or potential drawbacks of Design by Contract? When might it not be the best approach?

**Limitations and drawbacks of Design by Contract**:

1. **Increased code complexity**: Adding contract checks makes the code more verbose

2. **Performance overhead**: Runtime contract checking adds overhead

3. **Learning curve**: Developers need to understand the concept and how to apply it

4. **Maintenance burden**: Contracts need to be kept up-to-date as requirements change

5. **False sense of security**: Contracts only check what you explicitly specify

6. **Difficulty with third-party code**: You can't enforce contracts on code you don't control

**When it might not be the best approach**:

1. **Exploratory or prototype development**: When requirements are still evolving rapidly

2. **Simple CRUD applications**: When the business logic is straightforward

3. **Performance-critical code**: When every CPU cycle counts

4. **Legacy code maintenance**: When adding contracts would require extensive refactoring

5. **Dynamic or duck-typed languages**: Where contracts are harder to enforce statically

6. **External-facing APIs with unpredictable clients**: Where defensive programming might be more appropriate

In these cases, lighter-weight approaches like:
- Basic input validation
- Comprehensive testing
- Code reviews
- Static analysis tools

might be more appropriate than full Design by Contract implementation.
