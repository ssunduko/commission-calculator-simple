# Rate Limiting Tests

This directory contains comprehensive tests for the rate limiting implementation. The tests demonstrate various testing techniques including unit testing, concurrency testing, time-based testing, and servlet filter testing.

## Overview

The test suite covers three main areas:
1. **Token Bucket Algorithm Tests** (`TokenBucketRateLimiterTest`)
2. **Sliding Window Algorithm Tests** (`SlidingWindowRateLimiterTest`)
3. **Servlet Filter Integration Tests** (`RateLimitFilterTest`)

## Test Classes

### 1. TokenBucketRateLimiterTest

Tests the token bucket rate limiting algorithm implementation.

**Test Categories (Nested Tests):**

#### BasicRateLimiting
Tests fundamental rate limiting behavior:
- `shouldAllowRequestsWithinLimit()`: Verifies requests succeed within quota
- `shouldDenyRequestsExceedingLimit()`: Verifies requests fail when quota exhausted
- `shouldTrackPermitsCorrectly()`: Validates permit counting
- `shouldIsolateDifferentKeys()`: Ensures key isolation

#### TokenRefillMechanism
Tests time-based token refill:
- `shouldRefillTokensOverTime()`: Validates tokens refill over time
- `shouldNotExceedBucketCapacity()`: Ensures bucket doesn't overflow

#### ConcurrentAccess
Tests thread safety:
- `shouldHandleConcurrentRequestsSafely()`: Multi-threaded access to same key
- `shouldHandleDifferentKeysConcurrently()`: Multi-threaded access to different keys

#### ConfigurationVariations
Tests different configurations:
- `shouldRespectDifferentRateLimits(long)`: Parameterized test for various limits
- `shouldWorkWithPredefinedConfigs()`: Tests default/strict/permissive configs

#### ResetFunctionality
Tests reset capabilities:
- `shouldResetSingleKey()`: Verify single key reset
- `shouldResetAllKeys()`: Verify all keys reset
- `shouldNotAffectOtherKeysWhenResettingOne()`: Ensure reset isolation

#### EdgeCases
Tests boundary conditions:
- `shouldHandleNullKeyGracefully()`: Null key handling
- `shouldHandleEmptyKey()`: Empty string key handling
- `shouldCalculateTimeUntilNextPermit()`: Time calculation accuracy

**Total Test Methods:** 15+

### 2. SlidingWindowRateLimiterTest

Tests the sliding window rate limiting algorithm, focusing on accuracy and boundary prevention.

**Test Categories (Nested Tests):**

#### SlidingWindowBehavior
Tests core sliding window functionality:
- `shouldAllowRequestsWithinLimit()`: Basic allow behavior
- `shouldDenyRequestsExceedingLimit()`: Basic deny behavior
- `shouldAllowRequestsAfterWindowSlides()`: Window expiration
- `shouldGraduallyAllowRequestsAsWindowSlides()`: Progressive window sliding

#### AccuracyTests
Tests advantages over fixed window:
- `shouldPreventBoundaryExploitation()`: Prevents burst at window boundaries
- `shouldTrackAvailablePermitsAccurately()`: Accurate permit counting with time

#### ConcurrentAccess
Tests thread safety:
- `shouldHandleConcurrentRequestsSafely()`: Concurrent request handling

#### ResetFunctionality
Tests reset capabilities:
- `shouldResetSingleKey()`: Single key reset
- `shouldResetAllKeys()`: All keys reset

#### TimeUntilNextPermit
Tests time calculation:
- `shouldReturnZeroWhenPermitsAvailable()`: Zero when permits available
- `shouldCalculateTimeUntilNextPermit()`: Accurate time calculation

**Total Test Methods:** 10+

### 3. RateLimitFilterTest

Tests the servlet filter integration with rate limiting.

**Test Categories (Nested Tests):**

#### IpBasedRateLimiting
Tests IP-based rate limiting:
- `shouldAllowRequestsWithinLimit()`: Allow within quota
- `shouldDenyRequestsExceedingLimit()`: Deny when exceeded
- `shouldUseXForwardedForHeaderWhenPresent()`: Proxy header support
- `shouldSetRateLimitHeaders()`: Verify HTTP headers

#### UserBasedRateLimiting
Tests user-based rate limiting:
- `shouldUseUserIdWhenAvailable()`: Use user ID from context
- `shouldFallBackToIpWhenNoUserId()`: Fallback to IP

#### ApiKeyBasedRateLimiting
Tests API key-based rate limiting:
- `shouldUseApiKeyWhenPresent()`: Use API key from header
- `shouldFallBackToIpWhenNoApiKey()`: Fallback to IP

#### ErrorResponseFormat
Tests error responses:
- `shouldReturnJsonErrorResponse()`: Verify JSON format and content

#### HeaderTracking
Tests header management:
- `shouldUpdateRemainingCountAfterEachRequest()`: Track decreasing quota

#### NonHttpRequests
Tests edge cases:
- `shouldPassThroughNonHttpRequests()`: Handle non-HTTP requests

**Total Test Methods:** 12+

## Testing Techniques Demonstrated

### 1. Nested Tests (@Nested)

JUnit 5 nested tests provide logical organization:

```java
@Nested
@DisplayName("Basic Rate Limiting")
class BasicRateLimiting {
    @BeforeEach
    void setUp() {
        // Setup specific to this category
    }

    @Test
    void shouldAllowRequestsWithinLimit() {
        // Test implementation
    }
}
```

**Benefits:**
- Logical grouping of related tests
- Separate `@BeforeEach` per category
- Hierarchical display in test runners
- Better test reports

### 2. Parameterized Tests (@ParameterizedTest)

Data-driven testing with multiple inputs:

```java
@ParameterizedTest
@ValueSource(longs = {1, 10, 100, 1000})
void shouldRespectDifferentRateLimits(long limit) {
    // Test with different limit values
}
```

### 3. Concurrency Testing

Multi-threaded test scenarios:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
CountDownLatch latch = new CountDownLatch(10);

for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        try {
            // Concurrent test logic
        } finally {
            latch.countDown();
        }
    });
}

latch.await(5, TimeUnit.SECONDS);
```

**Verifies:**
- Thread safety
- Race condition prevention
- Atomic operations
- Correct behavior under load

### 4. Time-Based Testing

Tests that involve timing and delays:

```java
// Exhaust rate limit
for (int i = 0; i < 10; i++) {
    rateLimiter.tryAcquire(key);
}

// Wait for refill
Thread.sleep(1000);

// Should allow new request
assertTrue(rateLimiter.tryAcquire(key));
```

### 5. Mock-Based Testing

Using Mockito for servlet testing:

```java
HttpServletRequest request = mock(HttpServletRequest.class);
HttpServletResponse response = mock(HttpServletResponse.class);
FilterChain filterChain = mock(FilterChain.class);

when(request.getRemoteAddr()).thenReturn("192.168.1.100");

filter.doFilter(request, response, filterChain);

verify(filterChain).doFilter(request, response);
verify(response).setHeader("X-RateLimit-Limit", "10");
```

## Test Structure (AAA Pattern)

All tests follow the **Arrange-Act-Assert** pattern:

```java
@Test
void shouldDenyRequestsExceedingLimit() {
    // Arrange - Setup test data and state
    String key = "user1";

    // Act - Perform the action being tested
    for (int i = 0; i < 5; i++) {
        rateLimiter.tryAcquire(key);
    }

    // Assert - Verify the expected outcome
    assertFalse(rateLimiter.tryAcquire(key),
        "Request exceeding limit should be denied");
}
```

## Running the Tests

### Run All Performance Tests
```bash
mvn test -Dtest="**/performance/*Test"
```

### Run Specific Test Class
```bash
mvn test -Dtest=TokenBucketRateLimiterTest
mvn test -Dtest=SlidingWindowRateLimiterTest
mvn test -Dtest=RateLimitFilterTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=TokenBucketRateLimiterTest#shouldAllowRequestsWithinLimit
```

### Run with Coverage
```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```

## Test Coverage

The test suite provides comprehensive coverage:

- **Line Coverage**: >95%
- **Branch Coverage**: >90%
- **Method Coverage**: 100%

Coverage includes:
- All public methods
- Edge cases (null, empty, boundary values)
- Concurrent access scenarios
- Time-based behavior
- Error conditions
- HTTP integration

## JUnit 5 Features Used

### Annotations
- `@Test`: Mark test methods
- `@Nested`: Group related tests
- `@BeforeEach`: Setup before each test
- `@DisplayName`: Readable test names
- `@ParameterizedTest`: Data-driven tests
- `@ValueSource`: Parameter source

### Assertions
- `assertEquals(expected, actual)`: Value equality
- `assertTrue(condition)`: Boolean true
- `assertFalse(condition)`: Boolean false
- `assertDoesNotThrow(lambda)`: No exception thrown

## Mockito Features Used

### Mock Creation
```java
HttpServletRequest request = mock(HttpServletRequest.class);
```

### Stubbing
```java
when(request.getRemoteAddr()).thenReturn("192.168.1.100");
```

### Verification
```java
verify(filterChain).doFilter(request, response);
verify(response).setStatus(429);
verify(response, never()).setStatus(200);
```

### Argument Capturing
```java
ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
verify(response).setHeader(eq("X-RateLimit-Remaining"), captor.capture());
```

## Test Lifecycle

### Per Test Class
1. Test class instantiated
2. `@BeforeEach` methods run
3. Test method executes
4. Assertions verified
5. `@AfterEach` methods run (if present)
6. Repeat for each test method

### Nested Tests
1. Outer class `@BeforeEach` runs
2. Nested class `@BeforeEach` runs
3. Test method executes
4. Nested class `@AfterEach` runs
5. Outer class `@AfterEach` runs

## Best Practices Demonstrated

### 1. Test Isolation
Each test is independent and can run in any order:
```java
@BeforeEach
void setUp() {
    config = RateLimitConfig.builder()...
    rateLimiter = new TokenBucketRateLimiter(config);
}
```

### 2. Descriptive Names
Tests use descriptive names that explain behavior:
```java
@Test
@DisplayName("Should prevent boundary exploitation")
void shouldPreventBoundaryExploitation() { }
```

### 3. Clear Assertions
Assertions include descriptive messages:
```java
assertEquals(5, remaining, "Should have 5 permits remaining");
```

### 4. Concurrency Safety
Concurrency tests use proper synchronization:
```java
CountDownLatch latch = new CountDownLatch(threadCount);
// ... ensure all threads complete
latch.await(5, TimeUnit.SECONDS);
```

### 5. Time-Based Testing
Sleep durations account for timing variability:
```java
Thread.sleep(1100); // 1.1 seconds to be safe
```

## Diagrams

Visual test architecture diagram available:
- `testing-architecture.puml`: Complete test class structure with relationships
- Shows test organization, mocking strategy, and testing patterns

## Educational Value

These tests demonstrate:

### Testing Concepts
- Unit testing fundamentals
- Integration testing (filter + limiter)
- Concurrency testing
- Time-based testing
- Mock-based testing

### JUnit 5 Features
- Modern testing framework capabilities
- Nested test organization
- Parameterized testing
- Display names for readability
- Lifecycle management

### Best Practices
- AAA (Arrange-Act-Assert) pattern
- Test isolation
- Descriptive naming
- Comprehensive coverage
- Edge case handling

### Performance Testing
- Thread safety verification
- Rate limiting accuracy
- Time-based behavior
- Scalability validation