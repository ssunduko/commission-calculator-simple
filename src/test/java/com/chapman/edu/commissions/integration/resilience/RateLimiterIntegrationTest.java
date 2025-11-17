package com.chapman.edu.commissions.integration.resilience;

import com.chapman.edu.commissions.integration.resilience.ratelimit.RateLimitStructure;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Rate Limiter pattern
 *
 * These tests verify that the rate limiter correctly integrates with
 * commission calculation functionality:
 * - Limits requests within the configured period
 * - Rejects requests exceeding the limit
 * - Works with commission calculation methods
 * - Refreshes permissions after the period
 * - Tracks metrics accurately
 */
class RateLimiterIntegrationTest {

    private RateLimiter rateLimiter;
    private AtomicInteger callCounter;

    @BeforeEach
    void setUp() {
        // Use RateLimitStructure to create rate limiter
        rateLimiter = RateLimitStructure.createBasicRateLimiter();
        callCounter = new AtomicInteger(0);
    }

    @Test
    void testRateLimiterWithCommissionCalculation() {
        // Arrange: Use actual commission calculation
        Supplier<Double> commissionCalculator = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> {
                    callCounter.incrementAndGet();
                    return RateLimitStructure.calculateCommission("DEAL-001", 5000.0);
                }
        );

        // Act: Make requests up to the limit
        int successCount = 0;
        for (int i = 0; i < 5; i++) {
            try {
                Double commission = commissionCalculator.get();
                assertEquals(500.0, commission);
                successCount++;
            } catch (RequestNotPermitted e) {
                // Unexpected at this point
            }
        }

        // Assert: All requests within limit should succeed
        assertEquals(5, successCount);
        assertEquals(5, callCounter.get());
    }

    @Test
    void testRateLimiterAllowsRequestsWithinLimit() {
        // Arrange
        Supplier<String> decoratedSupplier = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> {
                    callCounter.incrementAndGet();
                    return "Success";
                }
        );

        // Act: Make 5 requests (within limit)
        for (int i = 0; i < 5; i++) {
            String result = decoratedSupplier.get();
            assertEquals("Success", result);
        }

        // Assert: All requests should succeed
        assertEquals(5, callCounter.get());
        assertEquals(0, rateLimiter.getMetrics().getAvailablePermissions());
    }

    @Test
    void testRateLimiterRejectsRequestsExceedingLimit() {
        // Arrange
        Supplier<String> decoratedSupplier = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> {
                    callCounter.incrementAndGet();
                    return "Success";
                }
        );

        // Act: Make 7 requests (exceeds limit of 5)
        int successCount = 0;
        int rejectedCount = 0;

        for (int i = 0; i < 7; i++) {
            try {
                decoratedSupplier.get();
                successCount++;
            } catch (RequestNotPermitted e) {
                rejectedCount++;
            }
        }

        // Assert: First 5 succeed, next 2 are rejected
        assertEquals(5, successCount);
        assertEquals(2, rejectedCount);
        assertEquals(5, callCounter.get());
    }

    @Test
    void testRateLimiterRefreshesPermissions() throws InterruptedException {
        // Arrange
        Supplier<String> decoratedSupplier = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> {
                    callCounter.incrementAndGet();
                    return "Success";
                }
        );

        // Act: Use all permissions
        for (int i = 0; i < 5; i++) {
            decoratedSupplier.get();
        }

        // Verify all permissions used
        assertEquals(0, rateLimiter.getMetrics().getAvailablePermissions());

        // Wait for refresh period (RateLimitStructure uses 10 second refresh period)
        Thread.sleep(10500);

        // Make another request after refresh
        String result = decoratedSupplier.get();

        // Assert: Request should succeed after refresh
        assertEquals("Success", result);
        assertEquals(6, callCounter.get());
    }

    @Test
    void testRateLimiterMetrics() {
        // Arrange
        Supplier<Double> commissionCalculator = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> 500.0
        );

        // Act: Make requests and track metrics
        int initialPermissions = rateLimiter.getMetrics().getAvailablePermissions();
        assertEquals(5, initialPermissions);

        // Use 3 permissions
        for (int i = 0; i < 3; i++) {
            commissionCalculator.get();
        }

        // Assert: Metrics should reflect usage
        assertEquals(2, rateLimiter.getMetrics().getAvailablePermissions());
    }

    @Test
    void testConcurrentRateLimiting() throws InterruptedException {
        // Arrange
        AtomicInteger successfulCalls = new AtomicInteger(0);
        AtomicInteger rejectedCalls = new AtomicInteger(0);

        Supplier<String> decoratedSupplier = RateLimiter.decorateSupplier(
                rateLimiter,
                () -> {
                    callCounter.incrementAndGet();
                    return "Success";
                }
        );

        // Act: Create multiple threads making concurrent requests
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                try {
                    decoratedSupplier.get();
                    successfulCalls.incrementAndGet();
                } catch (RequestNotPermitted e) {
                    rejectedCalls.incrementAndGet();
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert: Only 5 requests should succeed (limit for period)
        assertEquals(5, successfulCalls.get());
        assertEquals(5, rejectedCalls.get());
    }

    @Test
    void testRateLimiterWithFallback() {
        // Arrange: Rate limiter with very low limit
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofMillis(10))
                .build();

        RateLimiter strictLimiter = RateLimiterRegistry.of(config)
                .rateLimiter("strictLimiter");

        Supplier<Double> expensiveCalculation = RateLimiter.decorateSupplier(
                strictLimiter,
                () -> {
                    // Simulate expensive calculation
                    return 1000.0 * 0.10;
                }
        );

        // Act: First call succeeds
        Double result1 = expensiveCalculation.get();
        assertEquals(100.0, result1);

        // Second call is rate limited - use fallback
        Double result2;
        try {
            result2 = expensiveCalculation.get();
        } catch (RequestNotPermitted e) {
            // Fallback: Use cached or conservative value
            result2 = 50.0; // Conservative estimate
        }

        // Assert: Fallback was used
        assertEquals(50.0, result2);
    }
}
