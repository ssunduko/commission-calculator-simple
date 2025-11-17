package com.chapman.edu.commissions.integration.resilience.timeout;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Structure and Configuration for Time Limiter Pattern
 *
 * This class contains:
 * - Time limiter configuration builders
 * - Simulated commission calculation service
 * - Helper utilities
 */
public class TimeoutStructure {

    private static final Logger logger = LoggerFactory.getLogger(TimeoutStructure.class);

    /**
     * Creates a basic TimeLimiter with 3-second timeout
     *
     * @return Configured TimeLimiter instance
     */
    public static TimeLimiter createBasicTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                // Maximum time allowed for operation to complete
                .timeoutDuration(Duration.ofSeconds(3))

                // Whether to cancel the running Future on timeout
                // true = interrupt the thread (recommended)
                // false = just throw TimeoutException but let operation continue
                .cancelRunningFuture(true)

                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        return registry.timeLimiter("basicTimeLimiter");
    }

    /**
     * Creates a TimeLimiter for quick operations (500ms timeout)
     *
     * Suitable for operations that should complete quickly
     *
     * @return Configured TimeLimiter for quick operations
     */
    public static TimeLimiter createQuickOperationTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(500))
                .cancelRunningFuture(true)
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        return registry.timeLimiter("quickOperationTimeLimiter");
    }

    /**
     * Creates a TimeLimiter for long operations (10 second timeout)
     *
     * Suitable for complex calculations or batch processing
     *
     * @return Configured TimeLimiter for long operations
     */
    public static TimeLimiter createLongOperationTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .cancelRunningFuture(true)
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        return registry.timeLimiter("longOperationTimeLimiter");
    }

    /**
     * Simulates a commission calculation that takes a specific amount of time
     *
     * @param dealId Deal identifier
     * @param amount Sales amount
     * @param processingTimeMs How long the calculation takes
     * @return Calculated commission
     */
    public static double calculateCommission(String dealId, double amount, long processingTimeMs) {
        logger.info("Started commission calculation for deal: {} (will take {}ms)",
                dealId, processingTimeMs);

        long startTime = System.currentTimeMillis();

        try {
            // Simulate processing time
            // Check for interruption periodically (important for cancellation)
            long sleepInterval = 100;
            long elapsed = 0;

            while (elapsed < processingTimeMs) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.warn("Commission calculation interrupted for deal: {}", dealId);
                    throw new InterruptedException("Calculation was interrupted");
                }

                Thread.sleep(Math.min(sleepInterval, processingTimeMs - elapsed));
                elapsed = System.currentTimeMillis() - startTime;
            }

            // Calculation complete
            double commission = amount * 0.10;
            long actualTime = System.currentTimeMillis() - startTime;
            logger.info("Completed commission calculation for deal: {} in {}ms - Commission: ${}",
                    dealId, actualTime, commission);

            return commission;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long actualTime = System.currentTimeMillis() - startTime;
            logger.error("Commission calculation interrupted after {}ms for deal: {}",
                    actualTime, dealId);
            throw new RuntimeException("Calculation was interrupted", e);
        }
    }
}
