package com.chapman.edu.commissions.integration.resilience.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Bulkhead Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * The Bulkhead pattern isolates different parts of an application into pools so that if one fails,
 * the others continue to function. The name comes from ship compartments (bulkheads) that prevent
 * the entire ship from sinking if one compartment is breached.
 *
 * ANALOGY:
 * Think of a ship divided into watertight compartments. If water enters one compartment,
 * the bulkhead doors seal it off, preventing water from flooding the entire ship.
 * Similarly, in software, we limit resources allocated to each operation to prevent
 * one failing operation from consuming all resources.
 *
 * TWO TYPES:
 *
 * 1. SEMAPHORE-BASED BULKHEAD:
 *    - Controls concurrent calls using semaphores
 *    - Lightweight, no additional threads
 *    - Good for protecting in-memory operations
 *    - Executes on the calling thread
 *
 * 2. THREAD POOL BULKHEAD:
 *    - Uses a bounded queue and fixed thread pool
 *    - Provides true isolation with separate thread pool
 *    - Better for I/O operations
 *    - Returns CompletableFuture for async operations
 *
 * PURPOSE:
 * - Prevent resource exhaustion from cascading failures
 * - Isolate failures to specific components
 * - Ensure critical services remain available even if others fail
 * - Control concurrent execution and thread pool sizes
 * - Prevent thread pool exhaustion
 *
 * WHEN TO USE:
 * - Multiple services sharing a common thread pool
 * - Need to isolate slow or failing operations
 * - Protecting critical resources from being monopolized
 * - Preventing cascading failures across microservices
 * - Managing resource allocation in multi-tenant systems
 */
public class BulkheadDemo {

    private static final Logger logger = LoggerFactory.getLogger(BulkheadDemo.class);

    /**
     * Demonstrates Semaphore-based Bulkhead
     *
     * Shows how concurrent calls are limited and excess calls wait or are rejected
     */
    public static void demonstrateSemaphoreBulkhead() {
        logger.info("=== Semaphore Bulkhead Demo Started ===");
        logger.info("Configuration: Max 3 concurrent calls, 500ms wait timeout");

        Bulkhead bulkhead = BulkheadStructure.createSemaphoreBulkhead();

        // Register event listeners
        bulkhead.getEventPublisher()
                .onCallPermitted(event ->
                    logger.debug("Call permitted. Available concurrent calls: {}",
                            bulkhead.getMetrics().getAvailableConcurrentCalls()))
                .onCallRejected(event ->
                    logger.warn("Call REJECTED - Bulkhead is full"))
                .onCallFinished(event ->
                    logger.debug("Call finished"));

        // Create thread pool to simulate concurrent requests
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Submit 10 tasks that each take 2 seconds (more than max concurrent calls)
        for (int i = 1; i <= 10; i++) {
            final int taskNum = i;
            executorService.submit(() -> {
                String dealId = "DEAL-" + String.format("%03d", taskNum);
                double amount = 1000 + (taskNum * 100);

                try {
                    // Decorate the supplier with bulkhead
                    Supplier<Double> decoratedSupplier = Bulkhead.decorateSupplier(
                            bulkhead,
                            () -> BulkheadStructure.calculateCommission(dealId, amount, 2000)
                    );

                    logger.info("Task #{}: Attempting to acquire bulkhead permission...", taskNum);
                    double commission = decoratedSupplier.get();

                    logger.info("Task #{}: SUCCESS - Commission calculated: ${}", taskNum, commission);

                } catch (BulkheadFullException e) {
                    logger.warn("Task #{}: REJECTED - Bulkhead is full. " +
                            "Available concurrent calls: {}",
                            taskNum, bulkhead.getMetrics().getAvailableConcurrentCalls());

                } catch (Exception e) {
                    logger.error("Task #{}: ERROR - {}", taskNum, e.getMessage());
                }
            });

            // Small delay between task submissions
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Shutdown and wait
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("=== Semaphore Bulkhead Demo Completed ===");
        logger.info("Metrics - Max Concurrent Calls: {}, Available: {}",
                bulkhead.getMetrics().getMaxAllowedConcurrentCalls(),
                bulkhead.getMetrics().getAvailableConcurrentCalls());
    }

    /**
     * Demonstrates Thread Pool Bulkhead
     *
     * Shows true isolation with dedicated thread pool and asynchronous execution
     */
    public static void demonstrateThreadPoolBulkhead() {
        logger.info("\n=== Thread Pool Bulkhead Demo Started ===");
        logger.info("Configuration: 5 max threads, 2 core threads, 10 queue capacity");

        ThreadPoolBulkhead bulkhead = BulkheadStructure.createThreadPoolBulkhead();

        // Register event listeners
        bulkhead.getEventPublisher()
                .onCallPermitted(event ->
                    logger.debug("Async call permitted"))
                .onCallRejected(event ->
                    logger.warn("Async call REJECTED - Thread pool is full"))
                .onCallFinished(event ->
                    logger.debug("Async call finished"));

        // Submit 20 async tasks (more than thread pool + queue capacity)
        for (int i = 1; i <= 20; i++) {
            final int taskNum = i;
            String dealId = "ASYNC-DEAL-" + String.format("%03d", taskNum);
            double amount = 1000 + (taskNum * 100);

            try {
                // Decorate the supplier with thread pool bulkhead
                // Returns CompletableFuture for async processing
                Supplier<java.util.concurrent.CompletionStage<Double>> decoratedSupplier =
                        ThreadPoolBulkhead.decorateSupplier(
                                bulkhead,
                                () -> BulkheadStructure.calculateCommission(dealId, amount, 1000)
                        );

                logger.info("Async Task #{}: Submitting to thread pool bulkhead...", taskNum);

                CompletableFuture<Double> future = decoratedSupplier.get().toCompletableFuture();

                // Handle completion asynchronously
                future
                    .thenAccept(commission ->
                        logger.info("Async Task #{}: SUCCESS - Commission: ${}", taskNum, commission))
                    .exceptionally(throwable -> {
                        logger.error("Async Task #{}: FAILED - {}", taskNum, throwable.getMessage());
                        return null;
                    });

                logger.info("Async Task #{}: Submitted. Queue depth: {}, Active threads: {}",
                        taskNum,
                        bulkhead.getMetrics().getQueueDepth(),
                        bulkhead.getMetrics().getThreadPoolSize());

            } catch (BulkheadFullException e) {
                logger.warn("Async Task #{}: REJECTED - Thread pool and queue are full. " +
                        "Queue capacity: {}, Queue depth: {}",
                        taskNum,
                        bulkhead.getMetrics().getQueueCapacity(),
                        bulkhead.getMetrics().getQueueDepth());
            }

            // Small delay between submissions
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Wait for all tasks to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("=== Thread Pool Bulkhead Demo Completed ===");
        logger.info("Metrics - Thread Pool Size: {}, Queue Capacity: {}, Queue Depth: {}",
                bulkhead.getMetrics().getThreadPoolSize(),
                bulkhead.getMetrics().getQueueCapacity(),
                bulkhead.getMetrics().getQueueDepth());
    }

    /**
     * Demonstrates isolating different services using separate bulkheads
     *
     * Shows how critical services remain available even when non-critical services are overloaded
     */
    public static void demonstrateServiceIsolation() {
        logger.info("\n=== Service Isolation Demo Started ===");

        // Create separate bulkheads for different services
        Bulkhead criticalServiceBulkhead = BulkheadStructure.createCriticalServiceBulkhead();
        Bulkhead nonCriticalServiceBulkhead = BulkheadStructure.createNonCriticalServiceBulkhead();

        ExecutorService executorService = Executors.newFixedThreadPool(15);

        // Flood the non-critical service with requests
        for (int i = 1; i <= 10; i++) {
            final int taskNum = i;
            executorService.submit(() -> {
                try {
                    Supplier<String> decoratedSupplier = Bulkhead.decorateSupplier(
                            nonCriticalServiceBulkhead,
                            () -> {
                                try {
                                    Thread.sleep(3000); // Slow operation
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException(e);
                                }
                                return "Report generated";
                            }
                    );
                    String result = decoratedSupplier.get();
                    logger.info("Non-Critical Service #{}: {}", taskNum, result);
                } catch (Exception e) {
                    logger.warn("Non-Critical Service #{}: Rejected - {}", taskNum, e.getMessage());
                }
            });
        }

        // Critical service requests should still work despite non-critical service being flooded
        for (int i = 1; i <= 5; i++) {
            final int taskNum = i;
            executorService.submit(() -> {
                try {
                    Supplier<Double> decoratedSupplier = Bulkhead.decorateSupplier(
                            criticalServiceBulkhead,
                            () -> BulkheadStructure.calculateCommission("CRITICAL-" + taskNum, 5000, 500)
                    );
                    Double commission = decoratedSupplier.get();
                    logger.info("CRITICAL Service #{}: Commission calculated: ${}", taskNum, commission);
                } catch (Exception e) {
                    logger.error("CRITICAL Service #{}: Failed - {}", taskNum, e.getMessage());
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("=== Service Isolation Demo Completed ===");
        logger.info("Critical service remained available despite non-critical service overload");
    }

    public static void main(String[] args) {
        // Demo 1: Semaphore-based bulkhead
        demonstrateSemaphoreBulkhead();

        logger.info("\n" + "=".repeat(80) + "\n");

        // Demo 2: Thread pool bulkhead
        demonstrateThreadPoolBulkhead();

        logger.info("\n" + "=".repeat(80) + "\n");

        // Demo 3: Service isolation
        demonstrateServiceIsolation();
    }
}
