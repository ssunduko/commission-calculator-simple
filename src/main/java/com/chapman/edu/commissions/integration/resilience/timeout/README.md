# Time Limiter Pattern

## Overview
The Time Limiter pattern enforces maximum duration for operations to complete. If an operation exceeds the timeout, it's cancelled and a TimeoutException is thrown, preventing indefinite resource consumption.

## Implementation Details

### Files Created
- `TimeLimiterDemo.java`: Comprehensive demonstration of timeout handling with various scenarios

### Key Concepts

#### Timeout Enforcement
TimeLimiter differs from simple timeouts by:
- **Active Cancellation**: Interrupts running operations
- **Resource Liberation**: Frees threads and connections
- **CompletableFuture Integration**: Works with async operations
- **Guaranteed Cleanup**: Ensures resources are released

#### When to Use Timeouts

**Good Candidates:**
- External API calls (unpredictable response times)
- Database queries (complex queries might run long)
- File I/O on network drives (network latency)
- Microservice calls (network issues)
- Long-running calculations (should have reasonable limits)

**Not Needed:**
- In-memory operations (usually fast)
- Operations with guaranteed completion time
- Simple calculations

### Configuration Parameters

| Parameter | Purpose | Example Values |
|-----------|---------|----------------|
| `timeoutDuration` | Maximum allowed operation time | 500ms, 3s, 10s |
| `cancelRunningFuture` | Whether to interrupt operation on timeout | true (recommended) |

### Three Timeout Configurations

1. **Quick Operations (500ms)**
   - In-memory cache lookups
   - Simple calculations
   - Fast database queries

2. **Standard Operations (3s)**
   - External API calls
   - Standard database queries
   - File operations

3. **Long Operations (10s)**
   - Complex calculations
   - Batch processing
   - Report generation

### Features Demonstrated

1. **Basic Timeout**
   - Operations completing within timeout
   - Successful execution and result retrieval

2. **Timeout Cancellation**
   - Operations exceeding timeout
   - Automatic interruption
   - Resource cleanup

3. **Multiple Configurations**
   - Different timeouts for different operation types
   - Appropriate limits per use case

4. **Fallback Handling**
   - Graceful degradation on timeout
   - Cached or conservative values
   - Alternative calculation methods

5. **Event Monitoring**
   - Success tracking
   - Timeout detection
   - Error handling

### Use Cases in Commission System

1. **External Commission Validation API**
   ```java
   TimeLimiter limiter = TimeLimiter.of("apiTimeout",
       TimeLimiterConfig.custom()
           .timeoutDuration(Duration.ofSeconds(5))
           .cancelRunningFuture(true)
           .build());
   ```

2. **Complex Commission Calculations**
   ```java
   TimeLimiter limiter = TimeLimiter.of("calcTimeout",
       TimeLimiterConfig.custom()
           .timeoutDuration(Duration.ofSeconds(10))
           .cancelRunningFuture(true)
           .build());
   ```

3. **Database Report Generation**
   ```java
   TimeLimiter limiter = TimeLimiter.of("reportTimeout",
       TimeLimiterConfig.custom()
           .timeoutDuration(Duration.ofSeconds(30))
           .cancelRunningFuture(true)
           .build());
   ```

4. **Quick Cache Lookups**
   ```java
   TimeLimiter limiter = TimeLimiter.of("cacheTimeout",
       TimeLimiterConfig.custom()
           .timeoutDuration(Duration.ofMillis(500))
           .cancelRunningFuture(true)
           .build());
   ```

## Benefits

1. **Resource Protection**: Prevents thread pool exhaustion
2. **Predictable Performance**: Enforces SLAs and response times
3. **Better User Experience**: Fail fast instead of hanging
4. **Resource Cleanup**: Automatically cancels stuck operations
5. **System Stability**: Prevents cascading delays

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.timeout.TimeLimiterDemo"
```

## Expected Output

### Demo 1: Successful Operation
- Operation takes 2 seconds
- Timeout is 3 seconds
- Operation completes successfully
- Result returned normally

### Demo 2: Timeout Operation
- Operation takes 5 seconds
- Timeout is 3 seconds
- TimeoutException thrown after 3 seconds
- Operation is interrupted and cancelled
- Resources are freed

### Demo 3: Multiple Configurations
- Test 1: 400ms operation with 500ms timeout ✓
- Test 2: 2s operation with 3s timeout ✓
- Test 3: 8s operation with 10s timeout ✓
- Test 4: 1s operation with 500ms timeout ✗

### Demo 4: Timeout with Fallback
- Operation times out after 3 seconds
- Fallback provides conservative estimate
- User receives result (degraded but functional)

### Demo 5: Event Listeners
- Events published on success/timeout/error
- Monitoring and logging integration
- Metrics collection

## Common Patterns

### Pattern 1: Basic Timeout
```java
TimeLimiter limiter = TimeLimiter.ofDefaults("timeout");

Supplier<CompletableFuture<T>> futureSupplier = () ->
    CompletableFuture.supplyAsync(() -> operation(), executor);

Callable<T> decorated = TimeLimiter.decorateFutureSupplier(
    limiter,
    futureSupplier
);

try {
    T result = decorated.call();
} catch (TimeoutException e) {
    // Handle timeout
}
```

### Pattern 2: With Fallback
```java
try {
    T result = decorated.call();
    return result;
} catch (TimeoutException e) {
    logger.warn("Operation timed out, using fallback");
    return getFallbackValue();
}
```

### Pattern 3: Different Timeouts per Operation
```java
// Quick operations
TimeLimiter quickLimiter = TimeLimiter.of("quick",
    TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofMillis(500))
        .build());

// Slow operations
TimeLimiter slowLimiter = TimeLimiter.of("slow",
    TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(30))
        .build());
```

### Pattern 4: Event Monitoring
```java
timeLimiter.getEventPublisher()
    .onTimeout(event ->
        metrics.recordTimeout(event.getElapsedDuration()))
    .onSuccess(event ->
        metrics.recordSuccess(event.getElapsedDuration()))
    .onError(event ->
        logger.error("Error", event.getThrowable()));
```

## Best Practices

1. **Choose Appropriate Timeouts**:
   - Too short: False timeouts on legitimate operations
   - Too long: Resources tied up unnecessarily
   - Base on percentile response times (e.g., p99)

2. **Always Cancel Running Futures**:
   - Set `cancelRunningFuture(true)`
   - Ensures threads are interrupted
   - Frees resources promptly

3. **Implement Fallbacks**:
   - Provide degraded service instead of total failure
   - Use cached values when available
   - Return partial results if possible

4. **Monitor Timeout Rates**:
   - Track percentage of operations timing out
   - Alert on unusual patterns
   - Adjust timeouts based on actual performance

5. **Make Operations Interruptible**:
   - Check `Thread.isInterrupted()` periodically
   - Respond to interruption quickly
   - Clean up resources on interruption

6. **Combine with Other Patterns**:
   - Use with Retry for transient failures
   - Combine with Circuit Breaker for sustained issues
   - Add Bulkhead to limit concurrent slow operations

## Comparison with Alternatives

| Approach | Timeout | Cancellation | Resource Cleanup |
|----------|---------|--------------|------------------|
| `Thread.join(timeout)` | ✓ | ✗ | ✗ |
| `Future.get(timeout)` | ✓ | Partial | Partial |
| `TimeLimiter` | ✓ | ✓ | ✓ |

## Combining Patterns

### TimeLimiter + Retry
```java
// Timeout individual attempts, retry if they timeout
Retry retry = Retry.ofDefaults("retry");
TimeLimiter limiter = TimeLimiter.ofDefaults("timeout");

Supplier<CompletableFuture<T>> futureSupplier = ...;
Callable<T> decorated = TimeLimiter.decorateFutureSupplier(
    limiter, futureSupplier
);
Callable<T> retryDecorated = Retry.decorateCallable(
    retry, decorated
);
```

### TimeLimiter + Circuit Breaker
```java
// Circuit opens if too many timeouts occur
CircuitBreaker cb = CircuitBreaker.ofDefaults("cb");
TimeLimiter limiter = TimeLimiter.ofDefaults("timeout");

Callable<T> decorated = TimeLimiter.decorateFutureSupplier(
    limiter, futureSupplier
);
Callable<T> cbDecorated = CircuitBreaker.decorateCallable(
    cb, decorated
);
```

### TimeLimiter + Bulkhead
```java
// Limit concurrent slow operations
Bulkhead bulkhead = Bulkhead.ofDefaults("bulkhead");
TimeLimiter limiter = TimeLimiter.ofDefaults("timeout");

// Apply bulkhead to the future supplier
Supplier<CompletableFuture<T>> decoratedSupplier =
    Bulkhead.decorateSupplier(bulkhead, futureSupplier);

Callable<T> timedDecorated = TimeLimiter.decorateFutureSupplier(
    limiter, decoratedSupplier
);
```

## Important Notes

### Thread Interruption
For timeouts to work effectively, operations must be interruptible:

```java
// Good - checks for interruption
while (!done) {
    if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException();
    }
    // do work
}

// Bad - ignores interruption
while (!done) {
    // do work (won't stop on timeout)
}
```

### Executor Service Management
Always provide and manage executor service:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
try {
    // Use executor with TimeLimiter
} finally {
    executor.shutdown();
    executor.awaitTermination(60, TimeUnit.SECONDS);
}
```

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## References
- [Resilience4j Documentation](https://resilience4j.readme.io/docs/timeout)
- [Java CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)
- [Timeout Patterns in Distributed Systems](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/)
