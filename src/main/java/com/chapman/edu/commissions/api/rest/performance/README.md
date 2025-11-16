# Rate Limiting - Performance Package

This package implements **rate limiting** functionality for the Commission Calculator REST API. Rate limiting is a critical performance and security technique that controls the rate of requests a client can make to prevent abuse, ensure fair resource allocation, and protect against denial-of-service attacks.

## Overview

Rate limiting helps:
- **Prevent API abuse**: Limit excessive requests from a single client
- **Ensure fair usage**: Prevent one client from monopolizing resources
- **Protect infrastructure**: Guard against DDoS attacks
- **Control costs**: Limit expensive operations
- **Maintain SLA**: Ensure consistent performance for all users

## Architecture

### Design Patterns

This implementation demonstrates several key design patterns:

1. **Strategy Pattern**: `RateLimiter` interface with multiple interchangeable algorithms
2. **Builder Pattern**: `RateLimitConfig.Builder` for flexible configuration
3. **Intercepting Filter**: `RateLimitFilter` for cross-cutting concerns
4. **Value Object**: Immutable `RateLimitConfig`
5. **Thread-Local Storage**: Per-key state management

### Class Structure

```
RateLimiter (interface)
├── TokenBucketRateLimiter
│   └── TokenBucket (inner class)
└── SlidingWindowRateLimiter
    └── RequestWindow (inner class)

RateLimitConfig
└── Builder (inner class)

RateLimitFilter (Servlet Filter)

RateLimitExceededException (Exception)
```

## Components

### 1. RateLimiter Interface

The core abstraction that defines rate limiting operations:

```java
public interface RateLimiter {
    boolean tryAcquire(String key);
    boolean tryAcquire(String key, Duration timeout);
    long getAvailablePermits(String key);
    Duration getTimeUntilNextPermit(String key);
    void reset(String key);
    void resetAll();
}
```

**Key Methods:**
- `tryAcquire(key)`: Attempt to acquire permission for a request
- `getAvailablePermits(key)`: Check remaining quota
- `reset(key)`: Clear rate limit for specific key (admin/testing)

### 2. TokenBucketRateLimiter

Implementation using the **Token Bucket algorithm**, the industry-standard approach used by AWS, Google Cloud, and other major platforms.

**Algorithm:**
1. Bucket holds tokens (up to capacity)
2. Tokens refill at a constant rate
3. Each request consumes 1 token
4. If no tokens available, request is denied

**Advantages:**
- Allows burst traffic (up to bucket capacity)
- Simple and efficient: O(1) operations
- Smooth long-term rate limiting
- Memory efficient: O(1) per key

**Trade-offs:**
- Less accurate than sliding window
- Possible burst at refill boundaries

**Example Usage:**
```java
RateLimitConfig config = RateLimitConfig.builder()
    .requestsPerWindow(100)
    .windowDuration(Duration.ofMinutes(1))
    .build();

RateLimiter limiter = new TokenBucketRateLimiter(config);

if (limiter.tryAcquire("user123")) {
    // Process request
} else {
    // Rate limit exceeded
}
```

### 3. SlidingWindowRateLimiter

Implementation using the **Sliding Window algorithm** for more accurate rate limiting.

**Algorithm:**
1. Store timestamp of each request
2. On new request, remove expired timestamps
3. Count remaining requests in window
4. Allow if under limit

**Advantages:**
- No boundary reset issues (unlike fixed window)
- Accurate request counting
- Prevents burst at window boundaries

**Trade-offs:**
- Higher memory usage: O(limit) per key
- O(n) cleanup operation per request

**Example Usage:**
```java
RateLimitConfig config = RateLimitConfig.strictConfig(); // 10 req/min

RateLimiter limiter = new SlidingWindowRateLimiter(config);

if (limiter.tryAcquire(clientIp)) {
    // Process request
}
```

### 4. RateLimitConfig

Immutable configuration object built using the **Builder pattern**.

**Configuration Options:**
- `requestsPerWindow`: Maximum requests allowed in the time window
- `windowDuration`: Duration of the time window (e.g., 1 minute)
- `blockOnExceeded`: Whether to block and wait or immediately reject
- `maxWaitTime`: Maximum time to wait if blocking is enabled

**Predefined Configurations:**

```java
// Default: 100 requests per minute, non-blocking
RateLimitConfig.defaultConfig()

// Strict: 10 requests per minute, blocking up to 5 seconds
RateLimitConfig.strictConfig()

// Permissive: 1000 requests per minute, non-blocking
RateLimitConfig.permissiveConfig()

// Custom configuration
RateLimitConfig config = RateLimitConfig.builder()
    .requestsPerWindow(50)
    .windowDuration(Duration.ofSeconds(30))
    .blockOnExceeded(true)
    .maxWaitTime(Duration.ofSeconds(2))
    .build();
```

### 5. RateLimitFilter

Servlet filter that applies rate limiting to HTTP requests using the **Intercepting Filter pattern**.

**Features:**
- Multiple key extraction strategies (IP, User ID, API Key)
- Standard HTTP rate limit headers
- JSON error responses
- Integration with any `RateLimiter` implementation

**HTTP Headers:**
- `X-RateLimit-Limit`: Maximum requests allowed
- `X-RateLimit-Remaining`: Requests remaining in current window
- `X-RateLimit-Reset`: Seconds until limit resets
- `Retry-After`: Time to wait before retrying (when exceeded)

**Factory Methods:**

```java
// IP-based rate limiting (by client IP address)
RateLimitFilter ipFilter = RateLimitFilter.createIpBasedFilter(config);

// User-based rate limiting (by authenticated user ID)
RateLimitFilter userFilter = RateLimitFilter.createUserBasedFilter(config);

// API key-based rate limiting (by API key header)
RateLimitFilter apiKeyFilter = RateLimitFilter.createApiKeyBasedFilter(config);

// Custom key extraction
RateLimitFilter customFilter = new RateLimitFilter(
    rateLimiter,
    request -> extractCustomKey(request),
    config
);
```

**HTTP Response (Rate Limit Exceeded):**

```json
{
  "status": 429,
  "message": "Rate limit exceeded",
  "limit": 100,
  "retryAfter": 45,
  "timestamp": 1699564800000
}
```

### 6. RateLimitExceededException

Custom exception thrown when rate limit is exceeded.

```java
public class RateLimitExceededException extends RuntimeException {
    private final String key;
    private final long limit;
    private final Duration retryAfter;

    // Maps to HTTP 429 Too Many Requests
}
```

## Integration with REST API

### Registering the Filter

```java
public class ApiServer {
    public void start() {
        // Create rate limit configuration
        RateLimitConfig config = RateLimitConfig.defaultConfig();

        // Create and register filter
        RateLimitFilter filter = RateLimitFilter.createIpBasedFilter(config);

        // Add filter to Tomcat context
        context.addFilter("rateLimitFilter", filter)
            .addMappingForUrlPatterns(null, false, "/api/*");
    }
}
```

### Client Response Headers

When a client makes a request, they receive rate limit information:

```
HTTP/1.1 200 OK
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
X-RateLimit-Reset: 42
Content-Type: application/json
```

When rate limit is exceeded:

```
HTTP/1.1 429 Too Many Requests
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 45
Retry-After: 45
Content-Type: application/json

{
  "status": 429,
  "message": "Rate limit exceeded",
  "limit": 100,
  "retryAfter": 45,
  "timestamp": 1699564800000
}
```

## Thread Safety

All rate limiter implementations are **thread-safe**:

- `ConcurrentHashMap` for key-to-state mapping
- `ReentrantLock` per key for state modifications
- No global locks (supports high concurrency)
- Atomic operations for counter updates

## Performance Characteristics

| Algorithm | Acquire Time | Memory per Key | Best For |
|-----------|-------------|----------------|----------|
| Token Bucket | O(1) | O(1) | General purpose, allows bursts |
| Sliding Window | O(n) | O(limit) | Accurate tracking, prevent boundary abuse |

## Use Cases

### 1. Public API Rate Limiting
```java
// Limit public API to 100 requests per minute per IP
RateLimitConfig config = RateLimitConfig.defaultConfig();
RateLimitFilter filter = RateLimitFilter.createIpBasedFilter(config);
```

### 2. Authenticated User Limits
```java
// Different limits per user tier
RateLimitConfig premiumConfig = RateLimitConfig.builder()
    .requestsPerWindow(1000)
    .windowDuration(Duration.ofMinutes(1))
    .build();

RateLimitFilter filter = RateLimitFilter.createUserBasedFilter(premiumConfig);
```

### 3. API Key Management
```java
// Rate limit by API key
RateLimitConfig config = RateLimitConfig.strictConfig();
RateLimitFilter filter = RateLimitFilter.createApiKeyBasedFilter(config);
```

### 4. DDoS Protection
```java
// Aggressive rate limiting for protection
RateLimitConfig ddosConfig = RateLimitConfig.builder()
    .requestsPerWindow(10)
    .windowDuration(Duration.ofSeconds(10))
    .build();
```

## Testing

Comprehensive test suite available in `src/test/java/.../performance/`:

- **TokenBucketRateLimiterTest**: Tests token bucket algorithm
- **SlidingWindowRateLimiterTest**: Tests sliding window algorithm
- **RateLimitFilterTest**: Tests servlet filter integration

Test coverage includes:
- Basic functionality (allow/deny)
- Time-based behavior (refill, window sliding)
- Concurrency (thread safety)
- Configuration variations
- Edge cases (null keys, boundaries)
- HTTP integration (headers, status codes)

## Educational Concepts

This implementation demonstrates:

### Design Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible via new `RateLimiter` implementations
- **Liskov Substitution**: `RateLimiter` implementations are interchangeable
- **Interface Segregation**: Minimal, focused interfaces
- **Dependency Inversion**: Depend on `RateLimiter` abstraction

### Software Engineering Practices
- **Immutability**: Thread-safe configuration objects
- **Builder Pattern**: Flexible object construction
- **Strategy Pattern**: Pluggable algorithms
- **Value Objects**: `RateLimitConfig` as immutable configuration
- **Exception Handling**: Domain-specific exceptions

### Performance Engineering
- **Rate Limiting**: Control request throughput
- **Concurrency**: Thread-safe implementations
- **Memory Efficiency**: Minimal per-key overhead
- **Scalability**: No global locks, per-key locking

## Standards Compliance

### HTTP Standards
- **RFC 6585**: 429 Too Many Requests status code
- **RFC 7231**: Retry-After header
- **X-RateLimit-* headers**: De facto standard used by GitHub, Twitter, etc.

## Further Reading

- [Token Bucket Algorithm](https://en.wikipedia.org/wiki/Token_bucket)
- [Rate Limiting Strategies](https://cloud.google.com/architecture/rate-limiting-strategies-techniques)
- [RFC 6585 - 429 Status Code](https://tools.ietf.org/html/rfc6585#section-4)
- [Best Practices for API Rate Limiting](https://nordicapis.com/everything-you-need-to-know-about-api-rate-limiting/)

## Diagrams

Visual architecture diagrams are available:
- `performance-architecture.puml`: Complete class diagram with relationships and notes
- See generated PDF in documentation folder after running c4builder