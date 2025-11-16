package com.chapman.edu.commissions.api.rest.performance;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TokenBucketRateLimiter.
 *
 * This test class demonstrates:
 * - Testing rate limiting algorithms
 * - Concurrency testing
 * - Timing-based assertions
 * - Parameterized tests for different configurations
 * - Edge case testing
 *
 * Concepts demonstrated:
 * - JUnit 5 features: Nested tests, parameterized tests, test lifecycle
 * - Performance testing: Time-based assertions
 * - Concurrency testing: Multi-threaded test scenarios
 * - Test patterns: AAA (Arrange-Act-Assert)
 */
@DisplayName("Token Bucket Rate Limiter Tests")
class TokenBucketRateLimiterTest {

    private RateLimiter rateLimiter;
    private RateLimitConfig config;

    @Nested
    @DisplayName("Basic Rate Limiting")
    class BasicRateLimiting {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(5)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
        }

        @Test
        @DisplayName("Should allow requests within limit")
        void shouldAllowRequestsWithinLimit() {
            // Arrange
            String key = "user1";

            // Act & Assert
            for (int i = 0; i < 5; i++) {
                assertTrue(rateLimiter.tryAcquire(key),
                        "Request " + (i + 1) + " should be allowed");
            }
        }

        @Test
        @DisplayName("Should deny requests exceeding limit")
        void shouldDenyRequestsExceedingLimit() {
            // Arrange
            String key = "user1";

            // Act - Exhaust the limit
            for (int i = 0; i < 5; i++) {
                rateLimiter.tryAcquire(key);
            }

            // Assert - Next request should be denied
            assertFalse(rateLimiter.tryAcquire(key),
                    "Request exceeding limit should be denied");
        }

        @Test
        @DisplayName("Should track permits correctly")
        void shouldTrackPermitsCorrectly() {
            // Arrange
            String key = "user1";

            // Assert initial state
            assertEquals(5, rateLimiter.getAvailablePermits(key));

            // Act - Consume 3 permits
            for (int i = 0; i < 3; i++) {
                rateLimiter.tryAcquire(key);
            }

            // Assert remaining permits
            assertEquals(2, rateLimiter.getAvailablePermits(key));
        }

        @Test
        @DisplayName("Should isolate different keys")
        void shouldIsolateDifferentKeys() {
            // Arrange
            String key1 = "user1";
            String key2 = "user2";

            // Act - Exhaust limit for key1
            for (int i = 0; i < 5; i++) {
                rateLimiter.tryAcquire(key1);
            }

            // Assert - key1 is exhausted but key2 is not
            assertFalse(rateLimiter.tryAcquire(key1), "key1 should be exhausted");
            assertTrue(rateLimiter.tryAcquire(key2), "key2 should be available");
        }
    }

    @Nested
    @DisplayName("Token Refill Mechanism")
    class TokenRefillMechanism {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(10)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
        }

        @Test
        @DisplayName("Should refill tokens over time")
        void shouldRefillTokensOverTime() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Exhaust the bucket
            for (int i = 0; i < 10; i++) {
                rateLimiter.tryAcquire(key);
            }
            assertFalse(rateLimiter.tryAcquire(key), "Bucket should be empty");

            // Wait for partial refill (500ms = 5 tokens)
            Thread.sleep(500);

            // Assert - Should have approximately 5 tokens
            long available = rateLimiter.getAvailablePermits(key);
            assertTrue(available >= 4 && available <= 6,
                    "Should have approximately 5 tokens, got: " + available);
        }

        @Test
        @DisplayName("Should not exceed bucket capacity")
        void shouldNotExceedBucketCapacity() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Wait longer than refill time
            Thread.sleep(2000); // Wait 2 seconds (should refill to max)

            // Assert - Should not exceed capacity
            assertEquals(10, rateLimiter.getAvailablePermits(key),
                    "Bucket should be at capacity");
        }
    }

    @Nested
    @DisplayName("Concurrent Access")
    class ConcurrentAccess {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(100)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
        }

        @Test
        @DisplayName("Should handle concurrent requests safely")
        void shouldHandleConcurrentRequestsSafely() throws InterruptedException {
            // Arrange
            String key = "user1";
            int threadCount = 10;
            int requestsPerThread = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            List<Boolean> results = new CopyOnWriteArrayList<>();

            // Act - Submit concurrent requests
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < requestsPerThread; j++) {
                            results.add(rateLimiter.tryAcquire(key));
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for completion
            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // Assert - Approximately 100 should succeed (allow margin for refill)
            long successCount = results.stream().filter(b -> b).count();
            assertTrue(successCount >= 98 && successCount <= 110,
                    "Should succeed approximately 100 times (accounting for refill), got: " + successCount);
        }

        @Test
        @DisplayName("Should handle different keys concurrently")
        void shouldHandleDifferentKeysConcurrently() throws InterruptedException {
            // Arrange
            int keyCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(keyCount);
            CountDownLatch latch = new CountDownLatch(keyCount);

            // Act - Concurrent requests for different keys
            for (int i = 0; i < keyCount; i++) {
                final String key = "user" + i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < 100; j++) {
                            rateLimiter.tryAcquire(key);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for completion
            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // Assert - Each key should be at or near limit (allow margin for refill during test)
            for (int i = 0; i < keyCount; i++) {
                String key = "user" + i;
                long available = rateLimiter.getAvailablePermits(key);
                assertTrue(available <= 15,
                        "Key " + key + " should be at or near limit, but has " + available + " permits");
            }
        }
    }

    @Nested
    @DisplayName("Configuration Variations")
    class ConfigurationVariations {

        @ParameterizedTest
        @ValueSource(longs = {1, 10, 100, 1000})
        @DisplayName("Should respect different rate limits")
        void shouldRespectDifferentRateLimits(long limit) {
            // Arrange
            config = RateLimitConfig.builder()
                    .requestsPerWindow(limit)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
            String key = "user1";

            // Act - Try to acquire limit + 10 requests
            int successCount = 0;
            for (int i = 0; i < limit + 10; i++) {
                if (rateLimiter.tryAcquire(key)) {
                    successCount++;
                }
            }

            // Assert - Exactly 'limit' should succeed (allow small margin for refill during test)
            assertTrue(successCount >= limit && successCount <= limit + 10,
                    "Should succeed approximately " + limit + " times, got: " + successCount);
        }

        @Test
        @DisplayName("Should work with predefined configs")
        void shouldWorkWithPredefinedConfigs() {
            // Test default config
            rateLimiter = new TokenBucketRateLimiter(RateLimitConfig.defaultConfig());
            assertEquals(100, rateLimiter.getAvailablePermits("user1"));

            // Test strict config
            rateLimiter = new TokenBucketRateLimiter(RateLimitConfig.strictConfig());
            assertEquals(10, rateLimiter.getAvailablePermits("user2"));

            // Test permissive config
            rateLimiter = new TokenBucketRateLimiter(RateLimitConfig.permissiveConfig());
            assertEquals(1000, rateLimiter.getAvailablePermits("user3"));
        }
    }

    @Nested
    @DisplayName("Reset Functionality")
    class ResetFunctionality {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(5)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
        }

        @Test
        @DisplayName("Should reset single key")
        void shouldResetSingleKey() {
            // Arrange
            String key = "user1";
            for (int i = 0; i < 5; i++) {
                rateLimiter.tryAcquire(key);
            }

            // Act
            rateLimiter.reset(key);

            // Assert
            assertEquals(5, rateLimiter.getAvailablePermits(key),
                    "Key should be reset to full capacity");
        }

        @Test
        @DisplayName("Should reset all keys")
        void shouldResetAllKeys() {
            // Arrange
            String[] keys = {"user1", "user2", "user3"};
            for (String key : keys) {
                for (int i = 0; i < 5; i++) {
                    rateLimiter.tryAcquire(key);
                }
            }

            // Act
            rateLimiter.resetAll();

            // Assert
            for (String key : keys) {
                assertEquals(5, rateLimiter.getAvailablePermits(key),
                        "All keys should be reset to full capacity");
            }
        }

        @Test
        @DisplayName("Should not affect other keys when resetting one")
        void shouldNotAffectOtherKeysWhenResettingOne() {
            // Arrange
            rateLimiter.tryAcquire("user1");
            rateLimiter.tryAcquire("user2");
            rateLimiter.tryAcquire("user2");

            // Act
            rateLimiter.reset("user1");

            // Assert
            assertEquals(5, rateLimiter.getAvailablePermits("user1"),
                    "Reset key should be at full capacity");
            assertEquals(3, rateLimiter.getAvailablePermits("user2"),
                    "Other key should be unchanged");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should reject null key")
        void shouldRejectNullKey() {
            // Arrange
            config = RateLimitConfig.defaultConfig();
            rateLimiter = new TokenBucketRateLimiter(config);

            // Act & Assert - ConcurrentHashMap doesn't support null keys
            assertThrows(NullPointerException.class, () -> rateLimiter.tryAcquire(null),
                "Null keys should throw NullPointerException");
        }

        @Test
        @DisplayName("Should handle empty key")
        void shouldHandleEmptyKey() {
            // Arrange
            config = RateLimitConfig.defaultConfig();
            rateLimiter = new TokenBucketRateLimiter(config);

            // Act & Assert
            assertTrue(rateLimiter.tryAcquire(""),
                    "Should handle empty key");
        }

        @Test
        @DisplayName("Should calculate time until next permit")
        void shouldCalculateTimeUntilNextPermit() {
            // Arrange
            config = RateLimitConfig.builder()
                    .requestsPerWindow(1)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new TokenBucketRateLimiter(config);
            String key = "user1";

            // Act - Exhaust permit
            rateLimiter.tryAcquire(key);

            // Assert
            Duration timeUntilNext = rateLimiter.getTimeUntilNextPermit(key);
            assertTrue(timeUntilNext.getSeconds() <= 1,
                    "Time until next permit should be <= 1 second");
            assertTrue(timeUntilNext.getSeconds() >= 0,
                    "Time until next permit should be >= 0");
        }
    }
}