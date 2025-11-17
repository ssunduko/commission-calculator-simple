# Retry Pattern

## Overview
The Retry pattern automatically re-attempts failed operations before giving up. It handles transient failures that may resolve themselves, like temporary network issues or brief service unavailability.

## Implementation Details

### Files Created
- `RetryDemo.java`: Comprehensive demonstration of retry strategies including sync and async retries

### Key Concepts

#### When to Retry
Retry is effective for **transient failures** - temporary problems that may resolve quickly:
- Network timeouts
- Database connection issues
- Temporary service unavailability
- Rate limit exceeded (with backoff)
- Cloud service throttling

#### When NOT to Retry
Don't retry **permanent failures** - problems that won't fix themselves:
- Validation errors (400 Bad Request)
- Authentication failures (401 Unauthorized)
- Authorization failures (403 Forbidden)
- Resource not found (404 Not Found)
- Business logic errors

### Retry Strategies

**1. Fixed Delay**
- Wait the same amount of time between retries
- Simple and predictable
- Good for: Quick recovery scenarios

```
Attempt 1 → Wait 1s → Attempt 2 → Wait 1s → Attempt 3
```

**2. Exponential Backoff**
- Wait time increases exponentially
- Prevents overwhelming recovering services
- Good for: Services under load

```
Attempt 1 → Wait 1s → Attempt 2 → Wait 2s → Attempt 3 → Wait 4s → Attempt 4
```

**3. Random Jitter**
- Adds randomness to wait times
- Prevents thundering herd problem
- Good for: Distributed systems with many clients

```
Attempt 1 → Wait 0.8s → Attempt 2 → Wait 1.3s → Attempt 3
```

### Configuration Options

| Parameter | Purpose | Example Values |
|-----------|---------|----------------|
| `maxAttempts` | Total number of attempts (including initial) | 3, 5, 10 |
| `waitDuration` | Base wait time between retries | 1s, 500ms |
| `exponentialBackoffMultiplier` | Growth factor for exponential backoff | 2 (default) |
| `retryOnException` | Predicate to determine if exception is retryable | RuntimeException.class |
| `ignoreExceptions` | Exceptions that should never be retried | IllegalArgumentException.class |

### Features Demonstrated

1. **Basic Retry (Fixed Delay)**
   - 3 maximum attempts
   - 1 second fixed delay
   - Simple retry logic

2. **Exponential Backoff**
   - 5 maximum attempts
   - Increasing delays (1s, 2s, 4s, 8s, 16s)
   - Prevents service overload during recovery

3. **Selective Retry**
   - Exception filtering
   - Only retry specific error types
   - Ignore permanent failures

4. **Async Retry**
   - Non-blocking retries with CompletableFuture
   - Parallel execution support
   - Event-driven completion

5. **Retry with Fallback**
   - Graceful degradation after exhausting retries
   - Default/cached values
   - Conservative estimates

### Use Cases in Commission System

1. **Database Operations**
   ```java
   // Retry on transient database failures
   Retry retry = Retry.of("dbRetry", RetryConfig.custom()
       .maxAttempts(3)
       .retryExceptions(SQLException.class)
       .build());
   ```

2. **External API Calls**
   ```java
   // Retry with exponential backoff for external services
   Retry retry = Retry.of("apiRetry", RetryConfig.custom()
       .maxAttempts(5)
       .waitDuration(Duration.ofSeconds(1))
       .enableExponentialBackoff()
       .build());
   ```

3. **Microservice Communication**
   ```java
   // Retry service calls with jitter
   Retry retry = Retry.of("serviceRetry", RetryConfig.custom()
       .maxAttempts(4)
       .waitDuration(Duration.ofMillis(500))
       .enableRandomizedWait()
       .randomizedWaitFactor(0.5)
       .build());
   ```

4. **Commission Validation**
   ```java
   // Retry validation calls but not business logic errors
   Retry retry = Retry.of("validationRetry", RetryConfig.custom()
       .maxAttempts(3)
       .retryExceptions(TimeoutException.class, ConnectException.class)
       .ignoreExceptions(ValidationException.class)
       .build());
   ```

## Benefits

1. **Automatic Recovery**: Handles transient failures without manual intervention
2. **Improved Reliability**: Increases success rate for intermittent issues
3. **Reduced False Alarms**: Filters out temporary problems
4. **Better User Experience**: Transparent handling of network hiccups
5. **Resource Protection**: Exponential backoff prevents service overload

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.retry.RetryDemo"
```

## Expected Output

### Demo 1: Basic Retry
- Service fails twice, succeeds on third attempt
- Fixed 1-second delay between attempts
- Shows successful recovery

### Demo 2: Exponential Backoff
- Service fails 3 times, succeeds on fourth attempt
- Increasing delays: 1s, 2s, 4s
- Demonstrates backoff strategy

### Demo 3: Async Retry
- Non-blocking execution with CompletableFuture
- Retries happen asynchronously
- Shows async completion handling

### Demo 4: Selective Retry
- Test 1: Retries transient failures
- Test 2: Fails immediately on validation errors (no retry)
- Demonstrates exception filtering

### Demo 5: Retry with Fallback
- Service fails beyond max attempts
- Fallback provides conservative estimate
- Shows graceful degradation

## Common Patterns

### Pattern 1: Simple Retry
```java
Retry retry = Retry.ofDefaults("simple");
Supplier<T> decorated = Retry.decorateSupplier(
    retry,
    () -> riskyOperation()
);
T result = decorated.get();
```

### Pattern 2: Retry with Custom Config
```java
RetryConfig config = RetryConfig.custom()
    .maxAttempts(5)
    .waitDuration(Duration.ofSeconds(1))
    .enableExponentialBackoff()
    .build();

Retry retry = Retry.of("custom", config);
```

### Pattern 3: Async Retry
```java
Supplier<CompletableFuture<T>> decorated =
    Retry.decorateCompletionStage(
        retry,
        () -> CompletableFuture.supplyAsync(() -> asyncOperation())
    );

decorated.get()
    .thenAccept(result -> process(result))
    .exceptionally(error -> handleError(error));
```

### Pattern 4: Retry with Fallback
```java
try {
    T result = decoratedSupplier.get();
} catch (Exception e) {
    logger.warn("Retries exhausted, using fallback");
    T result = getFallbackValue();
}
```

## Best Practices

1. **Set Appropriate Max Attempts**:
   - Too few: May not overcome transient issues
   - Too many: Wastes resources on permanent failures
   - Typical: 3-5 attempts for most scenarios

2. **Use Exponential Backoff**:
   - For services that may be under load
   - Prevents overwhelming recovering services
   - Add jitter in distributed systems

3. **Filter Exceptions Carefully**:
   - Only retry transient failures
   - Fail fast on validation/auth errors
   - Log all retry attempts for debugging

4. **Combine with Circuit Breaker**:
   - Retry for transient failures
   - Circuit breaker for sustained failures
   - Better together than alone

5. **Implement Fallbacks**:
   - Provide defaults when all retries fail
   - Use cached data if available
   - Return partial results if possible

6. **Monitor and Alert**:
   - Track retry success/failure rates
   - Alert on excessive retries
   - Identify patterns in failures

## Combining Patterns

### Retry + Circuit Breaker
```java
// Circuit breaker prevents retries when service is down
CircuitBreaker cb = CircuitBreaker.ofDefaults("cb");
Retry retry = Retry.ofDefaults("retry");

Supplier<T> decorated = Retry.decorateSupplier(
    retry,
    CircuitBreaker.decorateSupplier(cb, () -> operation())
);
```

### Retry + Bulkhead
```java
// Bulkhead limits concurrent retries
Bulkhead bulkhead = Bulkhead.ofDefaults("bulkhead");
Retry retry = Retry.ofDefaults("retry");

Supplier<T> decorated = Bulkhead.decorateSupplier(
    bulkhead,
    Retry.decorateSupplier(retry, () -> operation())
);
```

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## References
- [Resilience4j Documentation](https://resilience4j.readme.io/docs/retry)
- [Exponential Backoff](https://en.wikipedia.org/wiki/Exponential_backoff)
- [AWS Architecture Blog - Exponential Backoff and Jitter](https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/)