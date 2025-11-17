package com.chapman.edu.commissions.integration.resilience.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Rate Limiter Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * Rate Limiting controls the rate at which operations are executed, preventing system overload
 * by limiting the number of requests that can be made within a specific time window.
 * It's like a traffic cop that ensures only a certain number of cars pass through per minute.
 *
 * HOW IT WORKS:
 * - Defines a limit on the number of operations allowed per time period
 * - Blocks or rejects requests that exceed the limit
 * - Automatically refreshes the quota after each time period
 * - Can use different algorithms: Fixed Window, Sliding Window, Token Bucket, etc.
 *
 * PURPOSE:
 * - Prevent system overload and resource exhaustion
 * - Ensure fair resource allocation among clients
 * - Protect APIs from abuse or DDoS attacks
 * - Control costs when using metered external services
 * - Comply with third-party API rate limits
 *
 * WHEN TO USE:
 * - Public APIs that need to prevent abuse
 * - Calls to rate-limited third-party services
 * - Resource-intensive operations (database queries, file processing)
 * - Preventing spam or brute-force attacks
 * - Implementing usage quotas for different user tiers
 */
public class RateLimiterDemo {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterDemo.class);


    /**
     * Demonstrates basic rate limiter usage with sequential requests
     */
    public static void demonstrateBasicRateLimiting() {
        logger.info("=== Basic Rate Limiter Demo Started ===");
        logger.info("Configuration: 5 requests per 10 seconds");

        RateLimiter rateLimiter = RateLimitStructure.createBasicRateLimiter();

        // Register event listeners to track rate limiting
        rateLimiter.getEventPublisher()
                .onSuccess(event ->
                    logger.debug("Call succeeded. Available permissions: {}",
                            rateLimiter.getMetrics().getAvailablePermissions()))
                .onFailure(event ->
                    logger.warn("Call rejected - Rate limit exceeded"));

        // Attempt 15 sequential requests (more than the limit)
        for (int i = 1; i <= 15; i++) {
            String dealId = "DEAL-" + String.format("%03d", i);
            double amount = 1000 + (i * 100);

            try {
                // Decorate the supplier with rate limiter
                Supplier<Double> decoratedSupplier = RateLimiter.decorateSupplier(
                        rateLimiter,
                        () -> RateLimitStructure.calculateCommission(dealId, amount)
                );

                // Execute the call
                double commission = decoratedSupplier.get();
                logger.info("Request #{}: SUCCESS - Commission: ${} for deal: {}",
                        i, commission, dealId);

            } catch (RequestNotPermitted e) {
                // Request was rejected due to rate limiting
                logger.warn("Request #{}: REJECTED - Rate limit exceeded for deal: {}. " +
                        "Available permissions: {}",
                        i, dealId, rateLimiter.getMetrics().getAvailablePermissions());
            } catch (Exception e) {
                logger.error("Request #{}: ERROR - {}", i, e.getMessage());
            }

            // Display current metrics
            logger.info("Available permissions: {} / Waiting threads: {}",
                    rateLimiter.getMetrics().getAvailablePermissions(),
                    rateLimiter.getMetrics().getNumberOfWaitingThreads());

            // Small delay between requests
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("=== Basic Rate Limiter Demo Completed ===\n");
    }

    /**
     * Demonstrates rate limiting with concurrent requests
     * Shows how rate limiter handles multiple threads competing for permissions
     */
    public static void demonstrateConcurrentRateLimiting() {
        logger.info("=== Concurrent Rate Limiter Demo Started ===");
        logger.info("Configuration: 100 requests per second");

        RateLimiter rateLimiter = RateLimitStructure.createHighThroughputRateLimiter();

        // Create a thread pool to simulate concurrent requests
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Submit 200 concurrent tasks (more than the rate limit)
        for (int i = 1; i <= 200; i++) {
            final int requestNum = i;
            executorService.submit(() -> {
                String dealId = "CONCURRENT-DEAL-" + String.format("%03d", requestNum);
                double amount = 1000 + (requestNum * 100);

                try {
                    // Try to acquire permission with timeout
                    Supplier<Double> decoratedSupplier = RateLimiter.decorateSupplier(
                            rateLimiter,
                            () -> RateLimitStructure.calculateCommission(dealId, amount)
                    );

                    double commission = decoratedSupplier.get();
                    logger.info("Concurrent Request #{}: SUCCESS - Commission: ${}",
                            requestNum, commission);

                } catch (RequestNotPermitted e) {
                    logger.warn("Concurrent Request #{}: REJECTED - Rate limit exceeded",
                            requestNum);
                } catch (Exception e) {
                    logger.error("Concurrent Request #{}: ERROR - {}",
                            requestNum, e.getMessage());
                }
            });
        }

        // Shutdown executor and wait for completion
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("=== Concurrent Rate Limiter Demo Completed ===\n");
    }

    /**
     * Demonstrates rate limiting for external API calls
     * Shows fail-fast behavior when limit is exceeded
     */
    public static void demonstrateExternalApiRateLimiting() {
        logger.info("=== External API Rate Limiter Demo Started ===");
        logger.info("Configuration: 60 requests per minute (simulating third-party API)");

        RateLimiter rateLimiter = RateLimitStructure.createExternalApiRateLimiter();

        int successCount = 0;
        int rejectedCount = 0;

        // Simulate calling external API 100 times rapidly
        for (int i = 1; i <= 100; i++) {
            String dealId = "API-DEAL-" + String.format("%03d", i);

            try {
                Supplier<String> decoratedSupplier = RateLimiter.decorateSupplier(
                        rateLimiter,
                        () -> RateLimitStructure.callExternalCommissionValidationApi(dealId)
                );

                String result = decoratedSupplier.get();
                successCount++;
                logger.info("API Call #{}: SUCCESS - {}", i, result);

            } catch (RequestNotPermitted e) {
                rejectedCount++;
                logger.warn("API Call #{}: REJECTED - Rate limit reached. " +
                        "Using cached/default validation instead.", i);
                // In real scenario, use cached data or default validation
            }

            // Rapid-fire requests (no delay)
        }

        logger.info("=== External API Rate Limiter Demo Completed ===");
        logger.info("Total Calls: {}, Successful: {}, Rejected: {}",
                successCount + rejectedCount, successCount, rejectedCount);
        logger.info("Success Rate: {}%", (successCount * 100.0) / (successCount + rejectedCount));
    }

    public static void main(String[] args) {
        // Demo 1: Basic sequential rate limiting
        demonstrateBasicRateLimiting();

        logger.info("\n" + "=".repeat(80) + "\n");

        // Demo 2: Concurrent rate limiting
        demonstrateConcurrentRateLimiting();

        logger.info("\n" + "=".repeat(80) + "\n");

        // Demo 3: External API rate limiting
        demonstrateExternalApiRateLimiting();
    }
}