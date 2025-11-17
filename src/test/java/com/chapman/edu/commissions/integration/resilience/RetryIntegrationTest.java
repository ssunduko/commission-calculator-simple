package com.chapman.edu.commissions.integration.resilience;

import com.chapman.edu.commissions.integration.resilience.retry.RetryStructure;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Retry pattern
 *
 * These tests verify that retry correctly integrates with
 * commission calculation functionality:
 * - Retries failed operations up to max attempts
 * - Uses configured wait duration between retries
 * - Only retries specified exception types
 * - Works with FlakyCommissionService
 * - Supports exponential backoff
 */
class RetryIntegrationTest {

    private Retry retry;
    private AtomicInteger attemptCounter;

    @BeforeEach
    void setUp() {
        // Use RetryStructure to create retry
        retry = RetryStructure.createBasicRetry();
        attemptCounter = new AtomicInteger(0);
    }

    @Test
    void testRetryWithFlakyCommissionService() {
        // Arrange: Use actual FlakyCommissionService that fails 2 times
        RetryStructure.FlakyCommissionService service =
                new RetryStructure.FlakyCommissionService(2);

        Supplier<Double> retryingCalculator = Retry.decorateSupplier(
                retry,
                () -> service.calculateCommission("DEAL-001", 5000.0)
        );

        // Act: Should succeed after 3 attempts (2 failures + 1 success)
        Double commission = retryingCalculator.get();

        // Assert
        assertNotNull(commission);
        assertEquals(500.0, commission); // 5000 * 0.10
    }

    @Test
    void testRetrySucceedsAfterFailures() {
        // Arrange: Supplier that fails twice, then succeeds
        Supplier<String> flakySupplier = Retry.decorateSupplier(
                retry,
                () -> {
                    int attempt = attemptCounter.incrementAndGet();
                    if (attempt < 3) {
                        throw new RuntimeException("Temporary failure");
                    }
                    return "Success";
                }
        );

        // Act
        String result = flakySupplier.get();

        // Assert
        assertEquals("Success", result);
        assertEquals(3, attemptCounter.get());
    }

    @Test
    void testRetryFailsAfterMaxAttempts() {
        // Arrange: Supplier that always fails
        Supplier<String> alwaysFailingSupplier = Retry.decorateSupplier(
                retry,
                () -> {
                    attemptCounter.incrementAndGet();
                    throw new RuntimeException("Permanent failure");
                }
        );

        // Act & Assert: Should throw after max attempts
        assertThrows(RuntimeException.class, alwaysFailingSupplier::get);
        assertEquals(3, attemptCounter.get());
    }

    @Test
    void testRetryIgnoresSpecificExceptions() {
        // Arrange: Retry that ignores IllegalArgumentException
        Supplier<String> validationFailureSupplier = Retry.decorateSupplier(
                retry,
                () -> {
                    attemptCounter.incrementAndGet();
                    throw new IllegalArgumentException("Validation error");
                }
        );

        // Act & Assert: Should fail immediately without retry
        assertThrows(IllegalArgumentException.class, validationFailureSupplier::get);
        assertEquals(1, attemptCounter.get());
    }

    @Test
    void testRetryWithExponentialBackoff() {
        // Arrange: Use exponential backoff retry from structure
        Retry exponentialRetry = RetryStructure.createExponentialBackoffRetry();

        AtomicInteger attempts = new AtomicInteger(0);
        long[] attemptTimes = new long[5];

        Supplier<String> supplier = Retry.decorateSupplier(
                exponentialRetry,
                () -> {
                    int attempt = attempts.getAndIncrement();
                    attemptTimes[attempt] = System.currentTimeMillis();

                    if (attempt < 4) {
                        throw new RuntimeException("Retry needed");
                    }
                    return "Success";
                }
        );

        // Act
        long startTime = System.currentTimeMillis();
        String result = supplier.get();

        // Assert: Should succeed after 5 attempts
        assertEquals("Success", result);
        assertEquals(5, attempts.get());

        // Verify exponential backoff (wait times should increase)
        long totalWait = attemptTimes[4] - attemptTimes[0];
        assertTrue(totalWait >= 1500); // 1s + 2s + 4s minimum
    }

    @Test
    void testRetryWithSelectiveRetry() {
        // Arrange: Use selective retry from structure
        Retry selectiveRetry = RetryStructure.createSelectiveRetry();

        // Test 1: Transient error (should retry)
        AtomicInteger transientAttempts = new AtomicInteger(0);
        Supplier<Double> transientSupplier = Retry.decorateSupplier(
                selectiveRetry,
                () -> {
                    int attempt = transientAttempts.incrementAndGet();
                    if (attempt < 2) {
                        throw new RuntimeException("Temporary network issue");
                    }
                    return 500.0;
                }
        );

        Double result1 = transientSupplier.get();
        assertEquals(500.0, result1);
        assertEquals(2, transientAttempts.get()); // Retried once

        // Test 2: Validation error (should not retry)
        AtomicInteger validationAttempts = new AtomicInteger(0);
        Supplier<Double> validationSupplier = Retry.decorateSupplier(
                selectiveRetry,
                () -> {
                    validationAttempts.incrementAndGet();
                    throw new IllegalArgumentException("Invalid deal amount");
                }
        );

        assertThrows(IllegalArgumentException.class, validationSupplier::get);
        assertEquals(1, validationAttempts.get()); // No retries
    }

    @Test
    void testRetryMetrics() {
        // Arrange: Supplier that fails once
        Supplier<Double> commissionCalculator = Retry.decorateSupplier(
                retry,
                () -> {
                    int attempt = attemptCounter.incrementAndGet();
                    if (attempt == 1) {
                        throw new RuntimeException("First attempt fails");
                    }
                    return 500.0;
                }
        );

        // Act
        Double result = commissionCalculator.get();

        // Assert
        assertEquals(500.0, result);
        assertEquals(2, attemptCounter.get());

        // Check metrics
        assertTrue(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt() +
                   retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt() > 0);
    }

    @Test
    void testRetryWithFallback() {
        // Arrange: Supplier that always fails
        Supplier<Double> failingCalculator = Retry.decorateSupplier(
                retry,
                () -> {
                    attemptCounter.incrementAndGet();
                    throw new RuntimeException("Service unavailable");
                }
        );

        // Act: Use fallback on failure
        Double result;
        try {
            result = failingCalculator.get();
        } catch (RuntimeException e) {
            // Fallback to conservative estimate
            result = 50.0;
        }

        // Assert
        assertEquals(50.0, result);
        assertEquals(3, attemptCounter.get()); // All retries exhausted
    }

    @Test
    void testRetryEventListeners() {
        // Arrange: Track retry events
        AtomicInteger retryEvents = new AtomicInteger(0);
        AtomicInteger successEvents = new AtomicInteger(0);

        retry.getEventPublisher()
                .onRetry(event -> retryEvents.incrementAndGet())
                .onSuccess(event -> successEvents.incrementAndGet());

        Supplier<String> supplier = Retry.decorateSupplier(
                retry,
                () -> {
                    int attempt = attemptCounter.incrementAndGet();
                    if (attempt < 2) {
                        throw new RuntimeException("Retry needed");
                    }
                    return "Success";
                }
        );

        // Act
        String result = supplier.get();

        // Assert
        assertEquals("Success", result);
        assertEquals(1, retryEvents.get()); // One retry event
        assertEquals(1, successEvents.get()); // One success event
    }

    @Test
    void testMultipleFlakyServices() {
        // Arrange: Test with different failure counts
        RetryStructure.FlakyCommissionService service1 =
                new RetryStructure.FlakyCommissionService(1); // Fails once
        RetryStructure.FlakyCommissionService service2 =
                new RetryStructure.FlakyCommissionService(2); // Fails twice

        Supplier<Double> calculator1 = Retry.decorateSupplier(
                retry,
                () -> service1.calculateCommission("DEAL-001", 5000.0)
        );

        Supplier<Double> calculator2 = Retry.decorateSupplier(
                retry,
                () -> service2.calculateCommission("DEAL-002", 10000.0)
        );

        // Act
        Double result1 = calculator1.get(); // Should succeed after 2 attempts
        Double result2 = calculator2.get(); // Should succeed after 3 attempts

        // Assert
        assertEquals(500.0, result1);
        assertEquals(1000.0, result2);
    }
}
