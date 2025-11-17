package com.chapman.edu.commissions.integration.resilience.timeout;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Time Limiter Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * The Time Limiter pattern sets a maximum duration for an operation to complete.
 * If the operation takes longer than the specified timeout, it's cancelled and
 * a TimeoutException is thrown. This prevents operations from running indefinitely
 * and consuming resources.
 *
 * ANALOGY:
 * Like a cooking timer - you set it for 30 minutes and if the food isn't ready by then,
 * you stop cooking and try something else. You don't wait forever for a dish that might
 * never finish. Similarly, we don't wait forever for an operation that might be stuck.
 *
 * KEY CONCEPTS:
 *
 * 1. TIMEOUT THRESHOLD:
 *    - Maximum allowed duration for operation
 *    - Enforced at runtime
 *    - Configurable per operation type
 *
 * 2. CANCELLATION:
 *    - Interrupts long-running operations
 *    - Frees up resources (threads, connections)
 *    - Prevents resource exhaustion
 *
 * 3. ASYNC EXECUTION:
 *    - TimeLimiter works with CompletableFuture
 *    - Non-blocking timeout enforcement
 *    - Integrates with async programming model
 *
 * PURPOSE:
 * - Prevent operations from running indefinitely
 * - Protect against slow or hanging operations
 * - Enforce SLAs (Service Level Agreements)
 * - Free up resources from stuck operations
 * - Provide predictable response times
 * - Prevent thread pool exhaustion
 *
 * WHEN TO USE:
 * - External API calls with no guaranteed response time
 * - Database queries that might run too long
 * - File I/O operations on network drives
 * - Any operation that could potentially hang
 * - Microservice calls across network
 * - Long-running calculations that should have limits
 *
 * COMPARISON WITH THREAD TIMEOUT:
 * - Thread.join(timeout): Waits but doesn't cancel
 * - Future.get(timeout): Throws exception but may not cancel
 * - TimeLimiter: Enforces timeout AND cancels operation
 */
public class TimeLimiterDemo {

    private static final Logger logger = LoggerFactory.getLogger(TimeLimiterDemo.class);

    /**
     * Demonstrates basic time limiter with operations that complete within timeout
     */
    public static void demonstrateSuccessfulOperation() {
        logger.info("=== Successful Operation Demo ===");
        logger.info("Configuration: 3-second timeout");

        TimeLimiter timeLimiter = TimeoutStructure.createBasicTimeLimiter();

        // Create an executor service for async execution
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            // Operation that completes in 2 seconds (within 3-second timeout)
            String dealId = "QUICK-DEAL-001";
            double amount = 5000;

            logger.info("Executing operation with 2-second duration (timeout: 3s)");

            // Create a supplier that returns CompletableFuture
            Supplier<CompletableFuture<Double>> futureSupplier = () ->
                    CompletableFuture.supplyAsync(
                            () -> TimeoutStructure.calculateCommission(dealId, amount, 2000),
                            executorService
                    );

            // Decorate with time limiter
            Callable<Double> decoratedCallable = TimeLimiter.decorateFutureSupplier(
                    timeLimiter,
                    futureSupplier
            );

            // Execute and get result
            Double commission = decoratedCallable.call();
            logger.info("SUCCESS: Commission calculated: ${}", commission);

        } catch (TimeoutException e) {
            logger.error("TIMEOUT: Operation did not complete in time");
        } catch (Exception e) {
            logger.error("ERROR: {}", e.getMessage());
        } finally {
            executorService.shutdown();
        }

        logger.info("=== Successful Operation Demo Completed ===\n");
    }

    /**
     * Demonstrates time limiter with operation that exceeds timeout
     */
    public static void demonstrateTimeoutOperation() {
        logger.info("=== Timeout Operation Demo ===");
        logger.info("Configuration: 3-second timeout");

        TimeLimiter timeLimiter = TimeoutStructure.createBasicTimeLimiter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
            // Operation that takes 5 seconds (exceeds 3-second timeout)
            String dealId = "SLOW-DEAL-001";
            double amount = 10000;

            logger.info("Executing operation with 5-second duration (timeout: 3s)");

            Supplier<CompletableFuture<Double>> futureSupplier = () ->
                    CompletableFuture.supplyAsync(
                            () -> TimeoutStructure.calculateCommission(dealId, amount, 5000),
                            executorService
                    );

            Callable<Double> decoratedCallable = TimeLimiter.decorateFutureSupplier(
                    timeLimiter,
                    futureSupplier
            );

            long startTime = System.currentTimeMillis();
            Double commission = decoratedCallable.call();
            logger.info("SUCCESS: Commission calculated: ${}", commission);

        } catch (TimeoutException e) {
            long elapsedTime = System.currentTimeMillis() - System.currentTimeMillis();
            logger.error("TIMEOUT: Operation exceeded 3-second limit and was cancelled");
            logger.info("Operation was properly interrupted and resources freed");
        } catch (Exception e) {
            logger.error("ERROR: {}", e.getMessage());
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        logger.info("=== Timeout Operation Demo Completed ===\n");
    }

    /**
     * Demonstrates different timeout configurations for different operation types
     */
    public static void demonstrateMultipleTimeoutConfigurations() {
        logger.info("=== Multiple Timeout Configurations Demo ===");

        TimeLimiter quickLimiter = TimeoutStructure.createQuickOperationTimeLimiter();    // 500ms
        TimeLimiter basicLimiter = TimeoutStructure.createBasicTimeLimiter();             // 3s
        TimeLimiter longLimiter = TimeoutStructure.createLongOperationTimeLimiter();      // 10s

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Test 1: Quick operation with quick limiter (should succeed)
        logger.info("\nTest 1: 400ms operation with 500ms timeout");
        executeWithTimeLimiter(quickLimiter, executorService, "QUICK-001", 5000, 400);

        // Test 2: Moderate operation with basic limiter (should succeed)
        logger.info("\nTest 2: 2s operation with 3s timeout");
        executeWithTimeLimiter(basicLimiter, executorService, "BASIC-001", 7000, 2000);

        // Test 3: Long operation with long limiter (should succeed)
        logger.info("\nTest 3: 8s operation with 10s timeout");
        executeWithTimeLimiter(longLimiter, executorService, "LONG-001", 15000, 8000);

        // Test 4: Quick operation with quick limiter (should timeout)
        logger.info("\nTest 4: 1s operation with 500ms timeout (should fail)");
        executeWithTimeLimiter(quickLimiter, executorService, "QUICK-002", 3000, 1000);

        executorService.shutdown();
        try {
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("=== Multiple Timeout Configurations Demo Completed ===\n");
    }

    /**
     * Helper method to execute operation with time limiter
     */
    private static void executeWithTimeLimiter(
            TimeLimiter timeLimiter,
            ExecutorService executorService,
            String dealId,
            double amount,
            long processingTimeMs) {

        try {
            Supplier<CompletableFuture<Double>> futureSupplier = () ->
                    CompletableFuture.supplyAsync(
                            () -> TimeoutStructure.calculateCommission(dealId, amount, processingTimeMs),
                            executorService
                    );

            Callable<Double> decoratedCallable = TimeLimiter.decorateFutureSupplier(
                    timeLimiter,
                    futureSupplier
            );

            Double commission = decoratedCallable.call();
            logger.info("✓ SUCCESS: Deal {} - Commission: ${}", dealId, commission);

        } catch (TimeoutException e) {
            logger.warn("✗ TIMEOUT: Deal {} exceeded time limit", dealId);
        } catch (Exception e) {
            logger.error("✗ ERROR: Deal {} - {}", dealId, e.getMessage());
        }
    }

    /**
     * Demonstrates time limiter with fallback
     */
    public static void demonstrateTimeoutWithFallback() {
        logger.info("=== Timeout with Fallback Demo ===");

        TimeLimiter timeLimiter = TimeoutStructure.createBasicTimeLimiter();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        String dealId = "FALLBACK-DEAL-001";
        double amount = 8000;

        try {
            // Try operation with 5-second duration (exceeds 3-second timeout)
            logger.info("Attempting commission calculation with 3s timeout...");

            Supplier<CompletableFuture<Double>> futureSupplier = () ->
                    CompletableFuture.supplyAsync(
                            () -> TimeoutStructure.calculateCommission(dealId, amount, 5000),
                            executorService
                    );

            Callable<Double> decoratedCallable = TimeLimiter.decorateFutureSupplier(
                    timeLimiter,
                    futureSupplier
            );

            Double commission = decoratedCallable.call();
            logger.info("Result: ${}", commission);

        } catch (TimeoutException e) {
            // Operation timed out - use fallback
            logger.warn("Operation timed out. Using fallback calculation...");

            // Fallback: Use cached value or conservative estimate
            double fallbackCommission = amount * 0.05; // Conservative 5% commission
            logger.info("Fallback Result: ${} (conservative estimate)", fallbackCommission);

            // In production, you might:
            // - Return cached value from previous calculation
            // - Use a simpler/faster calculation method
            // - Return partial results if available
            // - Queue for async processing and notify later

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        } finally {
            executorService.shutdown();
        }

        logger.info("=== Timeout with Fallback Demo Completed ===\n");
    }

    /**
     * Demonstrates combining TimeLimiter with event listeners
     */
    public static void demonstrateWithEventListeners() {
        logger.info("=== Event Listeners Demo ===");

        TimeLimiter timeLimiter = TimeoutStructure.createBasicTimeLimiter();

        // Register event listeners
        timeLimiter.getEventPublisher()
                .onSuccess(event ->
                    logger.info("Event: Operation completed successfully"))
                .onError(event ->
                    logger.warn("Event: Operation failed - {}",
                            event.getThrowable().getClass().getSimpleName()))
                .onTimeout(event ->
                    logger.warn("Event: Operation TIMED OUT"));

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Execute operation that will timeout
        logger.info("Executing operation that will timeout...");
        executeWithTimeLimiter(timeLimiter, executorService, "EVENT-001", 5000, 5000);

        executorService.shutdown();

        logger.info("=== Event Listeners Demo Completed ===\n");
    }

    public static void main(String[] args) {
        // Demo 1: Successful operation within timeout
        demonstrateSuccessfulOperation();

        logger.info("=".repeat(80) + "\n");

        // Demo 2: Operation that exceeds timeout
        demonstrateTimeoutOperation();

        logger.info("=".repeat(80) + "\n");

        // Demo 3: Multiple timeout configurations
        demonstrateMultipleTimeoutConfigurations();

        logger.info("=".repeat(80) + "\n");

        // Demo 4: Timeout with fallback
        demonstrateTimeoutWithFallback();

        logger.info("=".repeat(80) + "\n");

        // Demo 5: Event listeners
        demonstrateWithEventListeners();
    }
}