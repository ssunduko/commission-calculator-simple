package com.chapman.edu.commissions.integration.resilience.retry;

import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Retry Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * The Retry pattern automatically re-attempts failed operations a specified number of times
 * before giving up. It handles transient failures that may resolve themselves if given
 * another chance, like temporary network glitches or brief service unavailability.
 *
 * ANALOGY:
 * Like knocking on a door multiple times before assuming nobody's home. The first knock
 * might not be heard, but subsequent knocks might get a response. However, after knocking
 * too many times (max retries), you give up and try something else (fallback).
 *
 * KEY CONCEPTS:
 *
 * 1. TRANSIENT FAILURES:
 *    - Temporary network issues
 *    - Brief service outages
 *    - Database connection timeouts
 *    - Momentary resource unavailability
 *
 * 2. RETRY STRATEGIES:
 *    - Fixed Delay: Wait the same time between each retry
 *    - Exponential Backoff: Increase wait time exponentially (1s, 2s, 4s, 8s...)
 *    - Random Jitter: Add randomness to prevent thundering herd
 *
 * 3. SYNCHRONOUS vs ASYNCHRONOUS:
 *    - Sync: Blocks the calling thread during retries
 *    - Async: Returns CompletableFuture for non-blocking retries
 *
 * PURPOSE:
 * - Handle transient failures automatically
 * - Improve reliability without manual intervention
 * - Reduce false alarms from temporary issues
 * - Provide time for resources to recover
 * - Smooth over network hiccups
 *
 * WHEN TO USE:
 * - Network calls that may timeout temporarily
 * - Database operations during high load
 * - Microservice-to-microservice communication
 * - External API calls with intermittent failures
 * - Cloud service calls (S3, DynamoDB, etc.)
 *
 * WHEN NOT TO USE:
 * - Validation errors (won't fix themselves)
 * - Authentication failures (credentials won't change)
 * - Permanent errors (404, 403, etc.)
 * - Client errors (400 Bad Request)
 */
public class RetryDemo {

    private static final Logger logger = LoggerFactory.getLogger(RetryDemo.class);

    /**
     * Demonstrates basic retry with fixed delay
     */
    public static void demonstrateBasicRetry() {
        logger.info("=== Basic Retry Demo Started ===");
        logger.info("Configuration: 3 max attempts, 1 second fixed delay");

        Retry retry = RetryStructure.createBasicRetry();

        // Register event listeners
        retry.getEventPublisher()
                .onRetry(event ->
                    logger.warn("Retry attempt #{} - Previous failure: {}",
                            event.getNumberOfRetryAttempts(),
                            event.getLastThrowable().getMessage()))
                .onSuccess(event ->
                    logger.info("Call succeeded after {} attempts",
                            event.getNumberOfRetryAttempts() + 1))
                .onError(event ->
                    logger.error("All retry attempts failed. Total attempts: {}",
                            event.getNumberOfRetryAttempts()));

        // Service that fails twice, then succeeds
        RetryStructure.FlakyCommissionService service = new RetryStructure.FlakyCommissionService(2);

        // Decorate the supplier with retry
        Supplier<Double> decoratedSupplier = Retry.decorateSupplier(
                retry,
                () -> service.calculateCommission("DEAL-001", 5000)
        );

        try {
            Double commission = decoratedSupplier.get();
            logger.info("Final Result: Commission = ${}", commission);
        } catch (Exception e) {
            logger.error("Operation failed after all retries: {}", e.getMessage());
        }

        logger.info("=== Basic Retry Demo Completed ===\n");
    }

    /**
     * Demonstrates exponential backoff retry strategy
     */
    public static void demonstrateExponentialBackoff() {
        logger.info("=== Exponential Backoff Retry Demo Started ===");
        logger.info("Configuration: 5 max attempts, exponential backoff (1s, 2s, 4s, 8s, 16s)");

        Retry retry = RetryStructure.createExponentialBackoffRetry();

        // Track retry attempts
        retry.getEventPublisher()
                .onRetry(event ->
                    logger.warn("Retry attempt #{} - Previous failure: {}",
                            event.getNumberOfRetryAttempts(),
                            event.getLastThrowable().getMessage()));

        // Service that fails 3 times, then succeeds
        RetryStructure.FlakyCommissionService service = new RetryStructure.FlakyCommissionService(3);

        Supplier<Double> decoratedSupplier = Retry.decorateSupplier(
                retry,
                () -> service.calculateCommission("DEAL-002", 10000)
        );

        try {
            long startTime = System.currentTimeMillis();
            Double commission = decoratedSupplier.get();
            long totalTime = System.currentTimeMillis() - startTime;

            logger.info("Final Result: Commission = ${}", commission);
            logger.info("Total time including retries: {}ms", totalTime);
        } catch (Exception e) {
            logger.error("Operation failed after all retries: {}", e.getMessage());
        }

        logger.info("=== Exponential Backoff Retry Demo Completed ===\n");
    }

    /**
     * Demonstrates async retry using CompletableFuture
     */
    public static void demonstrateAsyncRetry() {
        logger.info("=== Async Retry Demo Started ===");
        logger.info("Configuration: Non-blocking retries with CompletableFuture");

        Retry retry = RetryStructure.createBasicRetry();

        retry.getEventPublisher()
                .onRetry(event ->
                    logger.warn("Async Retry attempt #{}", event.getNumberOfRetryAttempts()));

        RetryStructure.FlakyCommissionService service = new RetryStructure.FlakyCommissionService(2);

        // Decorate a synchronous call (async retry requires ScheduledExecutorService)
        // For true async retry, use decorateCompletionStage with executor
        Supplier<Double> decoratedSupplier = Retry.decorateSupplier(
                retry,
                () -> service.calculateCommission("ASYNC-DEAL-001", 7500)
        );

        // Execute (wrapped in CompletableFuture for async processing)
        CompletableFuture<Double> future = CompletableFuture.supplyAsync(decoratedSupplier);

        future
            .thenAccept(commission ->
                logger.info("Async Result: Commission = ${}", commission))
            .exceptionally(throwable -> {
                logger.error("Async operation failed: {}", throwable.getMessage());
                return null;
            })
            .join(); // Wait for completion (in real app, you wouldn't block)

        logger.info("=== Async Retry Demo Completed ===\n");
    }

    /**
     * Demonstrates selective retry with exception filtering
     */
    public static void demonstrateSelectiveRetry() {
        logger.info("=== Selective Retry Demo Started ===");
        logger.info("Configuration: Only retry on specific exceptions");

        Retry retry = RetryStructure.createSelectiveRetry();

        AtomicInteger callCount = new AtomicInteger(0);

        // Test 1: Retryable exception (contains "temporary")
        logger.info("\nTest 1: Transient failure (should retry)");
        Supplier<Double> retryableSupplier = Retry.decorateSupplier(
                retry,
                () -> {
                    int attempt = callCount.incrementAndGet();
                    if (attempt < 3) {
                        throw new RuntimeException("Temporary network glitch");
                    }
                    return 500.0;
                }
        );

        try {
            Double result = retryableSupplier.get();
            logger.info("Test 1 Result: ${}", result);
        } catch (Exception e) {
            logger.error("Test 1 Failed: {}", e.getMessage());
        }

        callCount.set(0);

        // Test 2: Non-retryable exception (IllegalArgumentException)
        logger.info("\nTest 2: Validation error (should NOT retry)");
        Supplier<Double> nonRetryableSupplier = Retry.decorateSupplier(
                retry,
                () -> {
                    callCount.incrementAndGet();
                    throw new IllegalArgumentException("Invalid deal amount");
                }
        );

        try {
            Double result = nonRetryableSupplier.get();
            logger.info("Test 2 Result: ${}", result);
        } catch (IllegalArgumentException e) {
            logger.info("Test 2: Failed immediately (no retries) - {}", e.getMessage());
            logger.info("Number of attempts made: {}", callCount.get());
        }

        logger.info("=== Selective Retry Demo Completed ===\n");
    }

    /**
     * Demonstrates retry with fallback
     */
    public static void demonstrateRetryWithFallback() {
        logger.info("=== Retry with Fallback Demo Started ===");

        Retry retry = RetryStructure.createBasicRetry();

        // Service that always fails
        RetryStructure.FlakyCommissionService service = new RetryStructure.FlakyCommissionService(10); // More failures than max attempts

        Supplier<Double> decoratedSupplier = Retry.decorateSupplier(
                retry,
                () -> service.calculateCommission("DEAL-003", 8000)
        );

        try {
            Double commission = decoratedSupplier.get();
            logger.info("Result: ${}", commission);
        } catch (Exception e) {
            // After all retries failed, use fallback
            logger.warn("All retries exhausted. Using fallback calculation.");
            Double fallbackCommission = 8000 * 0.05; // Conservative 5% commission
            logger.info("Fallback Result: ${}", fallbackCommission);
        }

        logger.info("=== Retry with Fallback Demo Completed ===\n");
    }

    public static void main(String[] args) {
        // Demo 1: Basic retry with fixed delay
        demonstrateBasicRetry();

        logger.info("=".repeat(80) + "\n");

        // Demo 2: Exponential backoff
        demonstrateExponentialBackoff();

        logger.info("=".repeat(80) + "\n");

        // Demo 3: Async retry
        demonstrateAsyncRetry();

        logger.info("=".repeat(80) + "\n");

        // Demo 4: Selective retry
        demonstrateSelectiveRetry();

        logger.info("=".repeat(80) + "\n");

        // Demo 5: Retry with fallback
        demonstrateRetryWithFallback();
    }
}
