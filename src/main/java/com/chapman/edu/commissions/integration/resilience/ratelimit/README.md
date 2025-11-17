# Rate Limiter Pattern

## Overview
Rate Limiting controls the rate at which operations are executed, preventing system overload by limiting the number of requests within a specific time window. It ensures fair resource allocation and protects against abuse.

## Implementation Details

### Files Created
- `RateLimiterDemo.java`: Comprehensive demonstration of rate limiting with multiple scenarios

### Key Concepts

#### Rate Limiting Strategies
The implementation uses a **Token Bucket** algorithm where:
- Tokens represent permissions to execute requests
- Tokens are replenished at a fixed rate
- Requests consume tokens
- When no tokens are available, requests are rejected or wait

#### Configuration Parameters

| Parameter | Purpose | Example Values |
|-----------|---------|----------------|
| `limitForPeriod` | Max requests allowed in one period | 5, 100, 60 |
| `limitRefreshPeriod` | Time period for limit reset | 10s, 1s, 1m |
| `timeoutDuration` | Max wait time for permission | 1s, 500ms, 100ms |

### Three Rate Limiter Configurations

1. **Basic Rate Limiter**
   - 5 requests per 10 seconds
   - Suitable for: Resource-intensive operations
   - Wait timeout: 1 second

2. **High Throughput Rate Limiter**
   - 100 requests per second
   - Suitable for: High-volume APIs
   - Wait timeout: 500ms

3. **External API Rate Limiter**
   - 60 requests per minute
   - Suitable for: Third-party API integration
   - Wait timeout: 100ms (fail-fast)

### Features Demonstrated

1. **Sequential Rate Limiting**
   - Single-threaded request processing
   - Clear demonstration of rate limit enforcement
   - Permission tracking

2. **Concurrent Rate Limiting**
   - Multi-threaded request handling
   - Thread pool management
   - Race condition handling

3. **External API Integration**
   - Fail-fast behavior
   - Compliance with external rate limits
   - Fallback to cached data

4. **Event Monitoring**
   - Success/failure tracking
   - Available permissions monitoring
   - Waiting threads count

### Use Cases in Commission System

1. **Public Commission API**
   - Prevent API abuse
   - Fair usage across clients
   - Prevent DDoS attacks

2. **Database Query Throttling**
   - Limit expensive commission calculation queries
   - Prevent database overload
   - Queue management

3. **Third-Party Service Integration**
   - Comply with external API limits
   - Avoid service suspension
   - Cost control for metered APIs

4. **User Tier Management**
   - Different limits for free vs. premium users
   - Usage quota enforcement
   - Fair resource allocation

## Benefits

1. **System Protection**: Prevents resource exhaustion and overload
2. **Fair Usage**: Ensures equitable resource distribution
3. **Cost Control**: Manages expenses for metered services
4. **Compliance**: Adheres to external API constraints
5. **Predictability**: Provides consistent performance under load

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.ratelimit.RateLimiterDemo"
```

## Expected Output

The demo will show:
1. **Basic Demo**: 15 sequential requests with 5 allowed per 10 seconds
   - First 5 requests succeed immediately
   - Remaining requests are rejected or wait
   - Permissions refresh after 10 seconds

2. **Concurrent Demo**: 200 concurrent requests with 100 per second limit
   - Thread pool competition for permissions
   - Some requests wait, others are rejected
   - High-throughput handling

3. **External API Demo**: 100 rapid requests with 60 per minute limit
   - First 60 succeed
   - Remaining 40 are rejected
   - Demonstrates fail-fast behavior

## Common Patterns

### Pattern 1: Wait for Permission
```java
Supplier<T> decoratedSupplier = RateLimiter.decorateSupplier(
    rateLimiter,
    () -> expensiveOperation()
);
T result = decoratedSupplier.get(); // Waits for permission
```

### Pattern 2: Fail Fast
```java
try {
    T result = decoratedSupplier.get();
} catch (RequestNotPermitted e) {
    // Use fallback or return error
    return fallbackValue;
}
```

### Pattern 3: Check Before Execute
```java
if (rateLimiter.acquirePermission()) {
    // Permission granted
    executeOperation();
} else {
    // Permission denied
    handleRejection();
}
```

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## Best Practices

1. **Choose Appropriate Limits**: Based on system capacity and SLAs
2. **Set Realistic Timeouts**: Balance between waiting and failing fast
3. **Monitor Metrics**: Track rejection rates and adjust limits
4. **Implement Fallbacks**: Provide alternatives when rate limited
5. **Different Tiers**: Use separate rate limiters for different user levels
6. **Log Rejections**: Track and analyze rate limit violations

## References
- [Resilience4j Documentation](https://resilience4j.readme.io/docs/ratelimiter)
- [Token Bucket Algorithm](https://en.wikipedia.org/wiki/Token_bucket)
- [API Rate Limiting Best Practices](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)