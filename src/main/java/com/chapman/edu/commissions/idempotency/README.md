# Idempotency Examples

This package demonstrates three types of idempotency using the commission calculator model classes:

1. **Natural Idempotency**: Operations that are inherently idempotent by their nature
2. **Implementation Idempotency**: Operations made idempotent through design and implementation
3. **Infrastructure Idempotency**: Idempotency provided by the system or framework

## What is Idempotency?

Idempotency is a property of certain operations where applying the operation multiple times has the same effect as applying it once. In mathematical terms, if f(x) = f(f(x)), then f is idempotent.

In software systems, idempotency is crucial for ensuring reliability, especially in distributed systems where operations may be retried due to network failures, timeouts, or other issues.

## Examples Overview

### Natural Idempotency

Natural idempotency occurs when an operation is inherently idempotent by its mathematical or logical nature. No special implementation is needed to make these operations idempotent.

Examples in `NaturalIdempotency.java`:
- Setting a deal status (setting a value is naturally idempotent)
- Mathematical operations like max (max(max(a,b),a) = max(a,b))
- Set operations (adding the same element to a set multiple times)
- Absolute value (abs(abs(x)) = abs(x))

### Implementation Idempotency

Implementation idempotency is achieved by designing operations to be idempotent through specific implementation techniques, even when the underlying operation is not naturally idempotent.

Examples in `ImplementationIdempotency.java`:
- Using request IDs to track processed requests
- Conditional execution based on current state
- Caching operation results with idempotency keys
- Safe updates with optimistic concurrency control

### Infrastructure Idempotency

Infrastructure idempotency is provided by the system or framework rather than being implemented in the application code.

Examples in `InfrastructureIdempotency.java` (simulated):
- Database transaction idempotency
- Message queue deduplication
- API gateway request deduplication for idempotent HTTP methods

## Class Diagram

The `idempotency_diagram.puml` file contains a PlantUML diagram showing the relationships between the model classes and the idempotency examples. The diagram includes notes explaining each type of idempotency.

## Running the Examples

Each example class has a `main` method that demonstrates the idempotency concepts. To run an example:

```java
// Run Natural Idempotency examples
java com.chapman.edu.commissions.idempotency.NaturalIdempotency

// Run Implementation Idempotency examples
java com.chapman.edu.commissions.idempotency.ImplementationIdempotency

// Run Infrastructure Idempotency examples
java com.chapman.edu.commissions.idempotency.InfrastructureIdempotency
```

## Key Takeaways

1. **Idempotency is essential for reliability**: In distributed systems, operations may be retried, and idempotency ensures consistent results.

2. **Different types of idempotency**: Some operations are naturally idempotent, others need to be designed for idempotency, and some rely on infrastructure support.

3. **Implementation techniques**: Common techniques include request IDs, conditional execution, caching, and optimistic concurrency control.

4. **Infrastructure support**: Many systems and frameworks provide built-in support for idempotency, such as database transactions, message queues, and API gateways.

5. **Design consideration**: Idempotency should be considered early in the design process, especially for distributed systems and APIs.