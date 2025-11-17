package com.chapman.edu.commissions.integration.resilience.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Structure and Configuration for Retry Pattern
 *
 * This class contains:
 * - Retry configuration builders
 * - Simulated flaky service
 * - Helper utilities
 */
public class RetryStructure {

    private static final Logger logger = LoggerFactory.getLogger(RetryStructure.class);

    /**
     * Creates a basic Retry with fixed delay between attempts
     *
     * @return Configured Retry instance
     */
    public static Retry createBasicRetry() {
        RetryConfig config = RetryConfig.custom()
                // Maximum number of retry attempts (including initial call)
                .maxAttempts(3)

                // Fixed wait time between retry attempts
                .waitDuration(Duration.ofSeconds(1))

                // Retry only on specific exceptions (RuntimeException and its subclasses)
                .retryOnException(throwable -> throwable instanceof RuntimeException)

                // Do NOT retry on these exceptions
                .ignoreExceptions(IllegalArgumentException.class)

                .build();

        return Retry.of("basicRetry", config);
    }

    /**
     * Creates a Retry with exponential backoff strategy
     *
     * Each retry waits longer: 1s, 2s, 4s, 8s...
     * Prevents overwhelming a recovering service
     *
     * @return Configured Retry instance with exponential backoff
     */
    public static Retry createExponentialBackoffRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)

                // Enable exponential backoff using intervalFunction
                // Each retry waits exponentially longer
                // Wait times: 1s, 2s, 4s, 8s, 16s
                // NOTE: When using intervalFunction, do NOT use waitDuration()
                .intervalFunction(io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff(
                        Duration.ofSeconds(1), // initial interval
                        2.0 // multiplier
                ))

                // Optional: Add random jitter to prevent thundering herd
                // Randomizes wait time slightly to spread out retries
                // This is commented out but shown for reference
                // To use jitter, replace intervalFunction above with:
                // .intervalFunction(io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff(
                //         Duration.ofSeconds(1), 2.0, 0.5))

                .retryOnException(throwable -> throwable instanceof RuntimeException)

                .build();

        // Create retry directly without registry to avoid event timestamp issues
        return Retry.of("exponentialBackoffRetry", config);
    }

    /**
     * Creates a Retry with selective exception handling
     *
     * Only retries on specific types of failures
     *
     * @return Configured Retry instance with exception filtering
     */
    public static Retry createSelectiveRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofMillis(500))

                // Retry only on specific exceptions that indicate transient failures
                .retryExceptions(
                        java.net.SocketTimeoutException.class,
                        java.net.ConnectException.class,
                        java.io.IOException.class
                )

                // Never retry on these exceptions (permanent failures)
                .ignoreExceptions(
                        IllegalArgumentException.class,  // Validation error
                        SecurityException.class          // Authentication error
                )

                // Custom predicate for retry decision
                .retryOnException(throwable -> {
                    // Custom logic: Retry if message contains "temporary"
                    return throwable.getMessage() != null &&
                           throwable.getMessage().toLowerCase().contains("temporary");
                })

                .build();

        return Retry.of("selectiveRetry", config);
    }

    /**
     * Simulates a flaky commission service that fails intermittently
     */
    public static class FlakyCommissionService {
        private static final Logger logger = LoggerFactory.getLogger(FlakyCommissionService.class);
        private final AtomicInteger attemptCount = new AtomicInteger(0);
        private final int failuresBeforeSuccess;

        public FlakyCommissionService(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        public double calculateCommission(String dealId, double amount) {
            int attempt = attemptCount.incrementAndGet();
            logger.info("Attempt #{}: Calculating commission for deal: {} with amount: ${}",
                    attempt, dealId, amount);

            // Fail for the first N attempts, then succeed
            if (attempt <= failuresBeforeSuccess) {
                logger.error("Attempt #{}: Temporary failure - service unavailable", attempt);
                throw new RuntimeException("Temporary failure - service unavailable");
            }

            // Success!
            double commission = amount * 0.10;
            logger.info("Attempt #{}: SUCCESS - Commission calculated: ${}", attempt, commission);
            attemptCount.set(0); // Reset for next call
            return commission;
        }

        public void reset() {
            attemptCount.set(0);
        }
    }
}
