package com.chapman.edu.commissions.integration.resilience.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Structure and Configuration for Rate Limiter Pattern
 *
 * This class contains:
 * - Rate limiter configuration builders
 * - Simulated commission calculation service
 * - Helper utilities
 */
public class RateLimitStructure {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitStructure.class);

    /**
     * Creates a RateLimiter with basic configuration
     *
     * This configuration allows 5 requests per 10-second period
     *
     * @return Configured RateLimiter instance
     */
    public static RateLimiter createBasicRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                // Maximum number of permissions available during one limit refresh period
                .limitForPeriod(5)

                // Period of time in which the limit resets
                // After this period, limitForPeriod permissions are available again
                .limitRefreshPeriod(Duration.ofSeconds(10))

                // Maximum time a thread will wait for permission
                // If timeout expires, RequestNotPermitted exception is thrown
                .timeoutDuration(Duration.ofSeconds(1))

                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("commissionApiRateLimiter");
    }

    /**
     * Creates a RateLimiter suitable for high-throughput scenarios
     *
     * This configuration allows 100 requests per second
     *
     * @return Configured RateLimiter instance for high throughput
     */
    public static RateLimiter createHighThroughputRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("highThroughputRateLimiter");
    }

    /**
     * Creates a RateLimiter that mimics external API constraints
     * For example, third-party APIs often limit requests per minute
     *
     * @return Configured RateLimiter instance for external API
     */
    public static RateLimiter createExternalApiRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                // Allow 60 requests per minute (simulating typical API limits)
                .limitForPeriod(60)
                .limitRefreshPeriod(Duration.ofMinutes(1))

                // Don't wait - fail fast if limit is exceeded
                .timeoutDuration(Duration.ofMillis(100))

                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        return registry.rateLimiter("externalApiRateLimiter");
    }

    /**
     * Simulates calculating commission for a sales deal
     *
     * @param dealId The deal identifier
     * @param amount The sales amount
     * @return Calculated commission
     */
    public static double calculateCommission(String dealId, double amount) {
        logger.info("Processing commission calculation for deal: {} with amount: ${}", dealId, amount);

        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        double commission = amount * 0.10; // 10% commission
        logger.info("Commission calculated: ${} for deal: {}", commission, dealId);
        return commission;
    }

    /**
     * Simulates calling an external commission validation API
     *
     * @param dealId The deal to validate
     * @return Validation result
     */
    public static String callExternalCommissionValidationApi(String dealId) {
        logger.debug("Calling external validation API for deal: {}", dealId);
        // Simulate API call
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Validated: " + dealId;
    }
}
