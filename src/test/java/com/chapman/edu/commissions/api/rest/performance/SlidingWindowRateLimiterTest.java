package com.chapman.edu.commissions.api.rest.performance;

import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SlidingWindowRateLimiter.
 *
 * This test class focuses on the specific behaviors of the sliding window
 * algorithm, particularly its advantages over fixed window approaches.
 *
 * Concepts demonstrated:
 * - Testing time-based algorithms
 * - Verifying sliding window behavior
 * - Edge case testing for window boundaries
 * - Concurrency testing
 */
@DisplayName("Sliding Window Rate Limiter Tests")
class SlidingWindowRateLimiterTest {

    private RateLimiter rateLimiter;
    private RateLimitConfig config;

    @Nested
    @DisplayName("Sliding Window Behavior")
    class SlidingWindowBehavior {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(5)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new SlidingWindowRateLimiter(config);
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
        @DisplayName("Should allow requests after window slides")
        void shouldAllowRequestsAfterWindowSlides() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Exhaust the limit
            for (int i = 0; i < 5; i++) {
                rateLimiter.tryAcquire(key);
            }

            // Wait for window to slide (1.1 seconds to be safe)
            Thread.sleep(1100);

            // Assert - Should allow new requests
            assertTrue(rateLimiter.tryAcquire(key),
                    "Request should be allowed after window slides");
        }

        @Test
        @DisplayName("Should gradually allow requests as window slides")
        void shouldGraduallyAllowRequestsAsWindowSlides() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Make 5 requests with 200ms spacing
            for (int i = 0; i < 5; i++) {
                assertTrue(rateLimiter.tryAcquire(key));
                Thread.sleep(200);
            }

            // At this point, we've made 5 requests over 1 second
            // The oldest should be about 1 second old
            // Wait 200ms more - oldest request should expire
            Thread.sleep(200);

            // Assert - One request should have expired, allowing a new one
            assertTrue(rateLimiter.tryAcquire(key),
                    "Request should be allowed as oldest expired");
        }
    }

    @Nested
    @DisplayName("Accuracy Tests")
    class AccuracyTests {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(10)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new SlidingWindowRateLimiter(config);
        }

        @Test
        @DisplayName("Should prevent boundary exploitation")
        void shouldPreventBoundaryExploitation() throws InterruptedException {
            // This test demonstrates advantage over fixed window
            // With fixed window, you could make 10 requests at 0.9s
            // and 10 more at 1.1s (20 in 0.2s)
            // Sliding window prevents this

            String key = "user1";

            // Make 10 requests
            for (int i = 0; i < 10; i++) {
                assertTrue(rateLimiter.tryAcquire(key));
            }

            // Wait 0.5 seconds (not enough for any to expire)
            Thread.sleep(500);

            // Try to make more requests - should be denied
            assertFalse(rateLimiter.tryAcquire(key),
                    "Should not allow burst at window boundary");

            // Wait another 0.6 seconds (total 1.1s, all should expire)
            Thread.sleep(600);

            // Now should allow new requests
            assertTrue(rateLimiter.tryAcquire(key),
                    "Should allow after full window elapsed");
        }

        @Test
        @DisplayName("Should track available permits accurately")
        void shouldTrackAvailablePermitsAccurately() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Make 3 requests with spacing
            for (int i = 0; i < 3; i++) {
                rateLimiter.tryAcquire(key);
                Thread.sleep(100);
            }

            // Assert - Should have 7 permits remaining
            long available = rateLimiter.getAvailablePermits(key);
            assertEquals(7, available, "Should have 7 permits remaining");

            // Wait for first request to expire (1.1s total from first request to be safe)
            Thread.sleep(900);

            // Should have 8-10 permits (first 1-3 requests may have expired depending on timing)
            available = rateLimiter.getAvailablePermits(key);
            assertTrue(available >= 8 && available <= 10,
                    "Should have 8-10 permits after expiry, got: " + available);
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
            rateLimiter = new SlidingWindowRateLimiter(config);
        }

        @Test
        @DisplayName("Should handle concurrent requests safely")
        void shouldHandleConcurrentRequestsSafely() throws InterruptedException {
            // Arrange
            String key = "user1";
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);

            // Act - Submit concurrent requests
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < 20; j++) {
                            if (rateLimiter.tryAcquire(key)) {
                                successCount.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for completion
            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // Assert - Exactly 100 should succeed
            assertEquals(100, successCount.get(),
                    "Exactly 100 requests should succeed");
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
            rateLimiter = new SlidingWindowRateLimiter(config);
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
    }

    @Nested
    @DisplayName("Time Until Next Permit")
    class TimeUntilNextPermit {

        @BeforeEach
        void setUp() {
            config = RateLimitConfig.builder()
                    .requestsPerWindow(2)
                    .windowDuration(Duration.ofSeconds(1))
                    .build();
            rateLimiter = new SlidingWindowRateLimiter(config);
        }

        @Test
        @DisplayName("Should return zero when permits available")
        void shouldReturnZeroWhenPermitsAvailable() {
            // Arrange
            String key = "user1";

            // Assert
            assertEquals(Duration.ZERO, rateLimiter.getTimeUntilNextPermit(key),
                    "Should return zero when permits available");
        }

        @Test
        @DisplayName("Should calculate time until next permit")
        void shouldCalculateTimeUntilNextPermit() throws InterruptedException {
            // Arrange
            String key = "user1";

            // Act - Exhaust permits
            rateLimiter.tryAcquire(key);
            Thread.sleep(100);
            rateLimiter.tryAcquire(key);

            // Assert
            Duration timeUntilNext = rateLimiter.getTimeUntilNextPermit(key);
            assertTrue(timeUntilNext.toMillis() > 0,
                    "Should have positive time until next permit");
            assertTrue(timeUntilNext.toMillis() <= 1000,
                    "Should be less than window duration");
        }
    }
}