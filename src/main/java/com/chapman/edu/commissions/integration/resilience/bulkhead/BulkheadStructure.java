package com.chapman.edu.commissions.integration.resilience.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Structure and Configuration for Bulkhead Pattern
 *
 * This class contains:
 * - Bulkhead configuration builders (semaphore and thread pool)
 * - Simulated commission calculation service
 * - Helper utilities
 */
public class BulkheadStructure {

    private static final Logger logger = LoggerFactory.getLogger(BulkheadStructure.class);

    /**
     * Creates a Semaphore-based Bulkhead
     *
     * This limits the number of concurrent calls without creating additional threads
     *
     * @return Configured Bulkhead instance
     */
    public static Bulkhead createSemaphoreBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
                // Maximum number of concurrent calls allowed
                .maxConcurrentCalls(3)

                // Maximum time to wait for permission to execute
                .maxWaitDuration(Duration.ofMillis(500))

                .build();

        BulkheadRegistry registry = BulkheadRegistry.of(config);
        return registry.bulkhead("commissionCalculationBulkhead");
    }

    /**
     * Creates a Thread Pool Bulkhead
     *
     * This provides true isolation with a dedicated thread pool and queue
     *
     * @return Configured ThreadPoolBulkhead instance
     */
    public static ThreadPoolBulkhead createThreadPoolBulkhead() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                // Maximum number of threads in the pool
                .maxThreadPoolSize(5)

                // Core number of threads in the pool (always alive)
                .coreThreadPoolSize(2)

                // Capacity of the queue
                // Total capacity = maxThreadPoolSize + queueCapacity
                .queueCapacity(10)

                // Time that excess idle threads will wait for new tasks before terminating
                .keepAliveDuration(Duration.ofMillis(1000))

                .build();

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.of(config);
        return registry.bulkhead("asyncCommissionBulkhead");
    }

    /**
     * Creates a bulkhead for critical services with more resources
     *
     * @return Configured Bulkhead instance for critical services
     */
    public static Bulkhead createCriticalServiceBulkhead() {
        return BulkheadRegistry.of(
                BulkheadConfig.custom()
                        .maxConcurrentCalls(5)  // More resources for critical service
                        .maxWaitDuration(Duration.ofSeconds(2))
                        .build()
        ).bulkhead("criticalCommissionService");
    }

    /**
     * Creates a bulkhead for non-critical services with fewer resources
     *
     * @return Configured Bulkhead instance for non-critical services
     */
    public static Bulkhead createNonCriticalServiceBulkhead() {
        return BulkheadRegistry.of(
                BulkheadConfig.custom()
                        .maxConcurrentCalls(2)  // Fewer resources for non-critical service
                        .maxWaitDuration(Duration.ofMillis(500))
                        .build()
        ).bulkhead("reportingService");
    }

    /**
     * Simulates a commission calculation that takes time
     *
     * @param dealId The deal identifier
     * @param amount Sales amount
     * @param processingTime Time to simulate processing
     * @return Calculated commission
     */
    public static double calculateCommission(String dealId, double amount, long processingTime) {
        logger.info("Started processing commission for deal: {} (will take {}ms)",
                dealId, processingTime);

        try {
            // Simulate processing time
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Calculation interrupted for deal: {}", dealId);
            throw new RuntimeException("Calculation interrupted", e);
        }

        double commission = amount * 0.10; // 10% commission
        logger.info("Completed commission calculation for deal: {} = ${}", dealId, commission);
        return commission;
    }
}
