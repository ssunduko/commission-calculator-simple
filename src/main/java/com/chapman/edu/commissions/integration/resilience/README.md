# Resilience Patterns

## Overview

This module demonstrates six fundamental resilience patterns using the Resilience4j library. These patterns help build fault-tolerant, robust systems that gracefully handle failures and provide predictable behavior under stress.

## What is Resilience?

**Resilience** is the ability of a system to handle and recover from failures. A resilient system:
- Continues to function even when parts fail
- Degrades gracefully under stress
- Recovers quickly from failures
- Protects itself from cascading failures
- Provides predictable behavior

## Implemented Patterns

### 1. Circuit Breaker
**Location:** `circuit/CircuitBreakerDemo.java`

**Purpose:** Prevents cascading failures by stopping requests to failing services.

**Key Concepts:**
- Three states: CLOSED, OPEN, HALF_OPEN
- Fails fast when service is down
- Automatic recovery testing

**When to Use:**
- External service calls
- Database operations
- Any operation that might fail repeatedly

**Example:**
```java
CircuitBreaker cb = CircuitBreaker.ofDefaults("service");
Supplier<T> decorated = CircuitBreaker.decorateSupplier(cb, () -> service.call());
T result = decorated.get();
```

### 2. Rate Limiter
**Location:** `ratelimit/RateLimiterDemo.java`

**Purpose:** Controls the rate of requests to prevent system overload.

**Key Concepts:**
- Token bucket algorithm
- Configurable requests per time period
- Request rejection or waiting

**When to Use:**
- Public APIs
- Third-party service integration
- Resource-intensive operations

**Example:**
```java
RateLimiter limiter = RateLimiter.ofDefaults("api");
Supplier<T> decorated = RateLimiter.decorateSupplier(limiter, () -> apiCall());
T result = decorated.get();
```

---

### 3. Bulkhead
**Location:** `bulkhead/BulkheadDemo.java`

**Purpose:** Isolates resources to prevent one failing component from consuming all resources.

**Key Concepts:**
- Semaphore-based: Limits concurrent calls
- Thread pool-based: True isolation with dedicated threads
- Service isolation

**When to Use:**
- Multiple services sharing resources
- Critical vs. non-critical operations
- Multi-tenant systems

**Example:**
```java
Bulkhead bulkhead = Bulkhead.ofDefaults("service");
Supplier<T> decorated = Bulkhead.decorateSupplier(bulkhead, () -> operation());
T result = decorated.get();
```

---

### 4. Retry
**Location:** `retry/RetryDemo.java`

**Purpose:** Automatically retries failed operations to handle transient failures.

**Key Concepts:**
- Fixed delay vs. exponential backoff
- Selective retry (only certain exceptions)
- Maximum attempt limits

**When to Use:**
- Network calls
- Transient failures
- Temporary service unavailability

**Example:**
```java
Retry retry = Retry.ofDefaults("service");
Supplier<T> decorated = Retry.decorateSupplier(retry, () -> unreliableCall());
T result = decorated.get();
```

---

### 5. Time Limiter
**Location:** `timeout/TimeLimiterDemo.java`

**Purpose:** Enforces maximum duration for operations to prevent indefinite waits.

**Key Concepts:**
- Timeout threshold
- Operation cancellation
- Works with CompletableFuture

**When to Use:**
- External API calls
- Long-running operations
- Enforcing SLAs

**Example:**
```java
TimeLimiter limiter = TimeLimiter.ofDefaults("service");
Callable<T> decorated = TimeLimiter.decorateFutureSupplier(
    limiter,
    () -> CompletableFuture.supplyAsync(() -> operation())
);
T result = decorated.call();
```

---

### 6. Cache
**Location:** `cache/CacheDemo.java`

**Purpose:** Stores results of expensive operations for fast retrieval.

**Key Concepts:**
- Cache hit vs. miss
- TTL (Time To Live)
- Cache invalidation

**When to Use:**
- Expensive calculations
- Frequently accessed data
- External API responses

**Example:**
```java
Cache<K, V> cache = Cache.of(jCache);
Function<K, V> decorated = Cache.decorateSupplier(
    cache,
    () -> expensiveOperation()
);
V result = decorated.apply(key);
```
---

## Pattern Combinations

Resilience patterns are most effective when combined. Here are common combinations:

### 1. Retry + Circuit Breaker
```
Retry → Circuit Breaker → Service
```
- Retry handles transient failures
- Circuit breaker handles sustained failures
- Prevents retry storm when service is down

### 2. Bulkhead + Rate Limiter
```
Rate Limiter → Bulkhead → Service
```
- Rate limiter controls incoming traffic
- Bulkhead isolates resource pools
- Complete traffic and resource management

### 3. Cache + Circuit Breaker
```
Cache Check → Circuit Breaker → Service
```
- Cache provides fast responses
- Circuit breaker protects failing service
- Fallback to cached data when service is down

### 4. Time Limiter + Retry
```
Retry(Time Limiter → Service)
```
- Time limiter prevents slow operations
- Retry handles timeouts on slow calls
- Exponential backoff prevents overwhelming service

### 5. Complete Stack
```
Rate Limiter → Bulkhead → Cache → Time Limiter → Retry → Circuit Breaker → Service
```
- Comprehensive resilience strategy
- Each layer handles specific failure mode
- Robust, production-ready architecture

## Project Structure

```
resilience/
├── circuit/
│   ├── CircuitBreakerDemo.java
│   └── README.md
├── ratelimit/
│   ├── RateLimiterDemo.java
│   └── README.md
├── bulkhead/
│   ├── BulkheadDemo.java
│   └── README.md
├── retry/
│   ├── RetryDemo.java
│   └── README.md
├── timeout/
│   ├── TimeLimiterDemo.java
│   └── README.md
├── cache/
│   ├── CacheDemo.java
│   └── README.md
└── README.md (this file)
```

## Running the Demos

Each pattern has a standalone demo with a `main()` method:

```bash
# Circuit Breaker
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.circuit.CircuitBreakerDemo"

# Rate Limiter
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.ratelimit.RateLimiterDemo"

# Bulkhead
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.bulkhead.BulkheadDemo"

# Retry
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.retry.RetryDemo"

# Time Limiter
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.timeout.TimeLimiterDemo"

# Cache
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.cache.CacheDemo"
```

## Running Tests

Integration tests verify the behavior of each pattern:

```bash
# Run all resilience pattern tests
mvn test -Dtest="*IntegrationTest"

# Run specific pattern tests
mvn test -Dtest=CircuitBreakerIntegrationTest
mvn test -Dtest=RateLimiterIntegrationTest
mvn test -Dtest=BulkheadIntegrationTest
mvn test -Dtest=RetryIntegrationTest
mvn test -Dtest=ResiliencePatternsIntegrationTest
```

## Dependencies

The following Resilience4j dependencies are included in `pom.xml`:

```xml
<!-- Circuit Breaker -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-circuitbreaker</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Rate Limiter -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Bulkhead -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-bulkhead</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Retry -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Time Limiter -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-timelimiter</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Cache -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-cache</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- JCache API -->
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
    <version>1.1.1</version>
</dependency>

<!-- JCache implementation (Caffeine) -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.8</version>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>jcache</artifactId>
    <version>3.1.8</version>
</dependency>
```

## Commission System Use Cases

### Commission Calculation Service

**Scenario:** External commission validation API

**Solution:**
```java
// Combine multiple patterns for robust API integration
RateLimiter rateLimiter = RateLimiter.of("validation-api",
    RateLimiterConfig.custom().limitForPeriod(60).build());

CircuitBreaker circuitBreaker = CircuitBreaker.of("validation-api",
    CircuitBreakerConfig.ofDefaults());

Retry retry = Retry.of("validation-api",
    RetryConfig.custom().maxAttempts(3).build());

// Stack patterns
Supplier<ValidationResult> resilientCall = Retry.decorateSupplier(
    retry,
    CircuitBreaker.decorateSupplier(
        circuitBreaker,
        RateLimiter.decorateSupplier(
            rateLimiter,
            () -> externalApi.validate(deal)
        )
    )
);
```

### Complex Commission Calculations

**Scenario:** Resource-intensive calculations that should have limits

**Solution:**
```java
// Limit concurrent calculations and cache results
Bulkhead bulkhead = Bulkhead.of("calculations",
    BulkheadConfig.custom().maxConcurrentCalls(5).build());

Cache<String, Double> cache = Cache.of(jCache);

Function<Deal, Double> calculate = deal -> {
    String cacheKey = deal.getId() + "-" + deal.getDate();
    return Cache.decorateSupplier(
        cache,
        Bulkhead.decorateSupplier(
            bulkhead,
            () -> expensiveCalculation(deal)
        )
    ).get();
};
```

### Database Operations

**Scenario:** Database queries with potential failures

**Solution:**
```java
// Retry transient failures, use bulkhead for isolation
Retry retry = Retry.of("database",
    RetryConfig.custom()
        .maxAttempts(3)
        .retryExceptions(SQLException.class)
        .build());

Bulkhead bulkhead = Bulkhead.of("database",
    BulkheadConfig.custom().maxConcurrentCalls(10).build());

Supplier<List<Commission>> query = Retry.decorateSupplier(
    retry,
    Bulkhead.decorateSupplier(
        bulkhead,
        () -> database.getCommissions(userId)
    )
);
```

## Best Practices

### 1. Pattern Selection
- **Circuit Breaker**: Always use for external dependencies
- **Retry**: Use for transient failures only
- **Bulkhead**: Use to isolate critical from non-critical services
- **Rate Limiter**: Use to protect your APIs and respect external limits
- **Time Limiter**: Use for any operation without guaranteed completion time
- **Cache**: Use for expensive, frequently accessed data

### 2. Configuration
- Base configurations on actual system behavior
- Use metrics to tune parameters
- Test under realistic load
- Document configuration decisions

### 3. Monitoring
- Track pattern metrics (failure rates, rejections, etc.)
- Set up alerts for unusual patterns
- Review metrics regularly
- Adjust configurations based on data

### 4. Testing
- Test each pattern independently
- Test pattern combinations
- Include chaos engineering tests
- Verify fallback behavior

### 5. Documentation
- Document which patterns protect which services
- Explain configuration choices
- Provide runbooks for pattern-related issues
- Keep architecture diagrams updated