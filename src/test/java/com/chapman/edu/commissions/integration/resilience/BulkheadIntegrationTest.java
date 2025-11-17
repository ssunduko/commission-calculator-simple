package com.chapman.edu.commissions.integration.resilience;

import com.chapman.edu.commissions.integration.resilience.bulkhead.BulkheadStructure;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Bulkhead pattern
 *
 * These tests verify that the bulkhead correctly integrates with
 * commission calculation functionality:
 * - Limits concurrent executions
 * - Rejects calls when at capacity
 * - Allows calls after resources are freed
 * - Works with commission calculation methods
 * - Isolates critical vs non-critical services
 */
class BulkheadIntegrationTest {

    private Bulkhead bulkhead;
    private AtomicInteger activeCount;

    @BeforeEach
    void setUp() {
        // Use BulkheadStructure to create bulkhead
        bulkhead = BulkheadStructure.createSemaphoreBulkhead();
        activeCount = new AtomicInteger(0);
    }

    @Test
    void testBulkheadLimitsConcurrentCalls() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(3);
        CountDownLatch completionLatch = new CountDownLatch(1);

        Supplier<String> longRunningTask = Bulkhead.decorateSupplier(
                bulkhead,
                () -> {
                    try {
                        int current = activeCount.incrementAndGet();
                        latch.countDown();
                        // Wait until test signals completion
                        completionLatch.await();
                        return "Complete";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } finally {
                        activeCount.decrementAndGet();
                    }
                }
        );

        // Act: Start 3 concurrent tasks (at limit)
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(() -> {
                try {
                    longRunningTask.get();
                } catch (Exception e) {
                    // Handle exception
                }
            });
            threads[i].start();
        }

        // Wait for all tasks to be running
        latch.await();

        // Assert: All 3 slots should be occupied
        assertEquals(3, activeCount.get());
        assertEquals(0, bulkhead.getMetrics().getAvailableConcurrentCalls());

        // Cleanup
        completionLatch.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    void testBulkheadRejectsWhenFull() throws InterruptedException {
        // Arrange: Fill the bulkhead
        CountDownLatch latch = new CountDownLatch(3);
        CountDownLatch completionLatch = new CountDownLatch(1);

        Supplier<String> blockingTask = Bulkhead.decorateSupplier(
                bulkhead,
                () -> {
                    try {
                        latch.countDown();
                        completionLatch.await();
                        return "Complete";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
        );

        // Start 3 threads (fill bulkhead)
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(() -> {
                try {
                    blockingTask.get();
                } catch (Exception e) {
                    // Handle exception
                }
            });
            threads[i].start();
        }

        // Wait for bulkhead to be full
        latch.await();

        // Act: Try to execute when bulkhead is full
        boolean rejected = false;
        try {
            blockingTask.get();
        } catch (BulkheadFullException e) {
            rejected = true;
        }

        // Assert: Call should be rejected
        assertTrue(rejected);

        // Cleanup
        completionLatch.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    void testBulkheadAllowsCallsAfterResourcesFreed() throws InterruptedException {
        // Arrange
        CountDownLatch firstBatchLatch = new CountDownLatch(1);
        AtomicInteger completedCalls = new AtomicInteger(0);

        Supplier<String> task = Bulkhead.decorateSupplier(
                bulkhead,
                () -> {
                    try {
                        firstBatchLatch.await();
                        Thread.sleep(100);
                        completedCalls.incrementAndGet();
                        return "Complete";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
        );

        // Act: Start first batch of tasks
        Thread[] firstBatch = new Thread[3];
        for (int i = 0; i < 3; i++) {
            firstBatch[i] = new Thread(() -> {
                try {
                    task.get();
                } catch (Exception e) {
                    // Handle exception
                }
            });
            firstBatch[i].start();
        }

        // Wait a bit for threads to start
        Thread.sleep(100);

        // Release first batch
        firstBatchLatch.countDown();

        // Wait for first batch to complete
        for (Thread thread : firstBatch) {
            thread.join();
        }

        // Assert: Resources should be freed, new calls should succeed
        assertEquals(3, completedCalls.get());
        assertEquals(3, bulkhead.getMetrics().getAvailableConcurrentCalls());

        // Start second batch
        CountDownLatch secondBatchLatch = new CountDownLatch(1);
        Thread secondTask = new Thread(() -> {
            try {
                Bulkhead.decorateSupplier(bulkhead, () -> {
                    secondBatchLatch.countDown();
                    return "Success";
                }).get();
            } catch (Exception e) {
                // Handle exception
            }
        });
        secondTask.start();
        secondBatchLatch.await();

        // Second task should succeed - available calls should be less than max
        assertTrue(bulkhead.getMetrics().getAvailableConcurrentCalls() >= 2);
        assertTrue(bulkhead.getMetrics().getAvailableConcurrentCalls() <= 3);
    }

    @Test
    void testBulkheadMetrics() throws InterruptedException {
        // Arrange
        Supplier<Double> commissionCalculation = Bulkhead.decorateSupplier(
                bulkhead,
                () -> {
                    try {
                        Thread.sleep(100);
                        return 500.0;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
        );

        // Act: Check initial metrics
        assertEquals(3, bulkhead.getMetrics().getAvailableConcurrentCalls());
        assertEquals(3, bulkhead.getMetrics().getMaxAllowedConcurrentCalls());

        // Start one task
        Thread task = new Thread(() -> {
            try {
                commissionCalculation.get();
            } catch (Exception e) {
                // Handle exception
            }
        });
        task.start();

        // Wait a bit for task to start
        Thread.sleep(50);

        // Assert: Metrics should reflect one active call
        assertEquals(2, bulkhead.getMetrics().getAvailableConcurrentCalls());

        // Wait for task to complete
        task.join();

        // Assert: Metrics should show resources freed
        assertEquals(3, bulkhead.getMetrics().getAvailableConcurrentCalls());
    }

    @Test
    void testBulkheadWithCommissionCalculation() throws InterruptedException {
        // Arrange: Use actual commission calculation from structure
        Supplier<Double> commissionCalculator = Bulkhead.decorateSupplier(
                bulkhead,
                () -> BulkheadStructure.calculateCommission("DEAL-001", 5000.0, 500)
        );

        // Act: Make concurrent calls
        CountDownLatch latch = new CountDownLatch(3);
        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Double commission = commissionCalculator.get();
                    assertEquals(500.0, commission);
                    latch.countDown();
                } catch (Exception e) {
                    // Handle exception
                }
            });
            threads[i].start();
        }

        // Wait for all to complete
        latch.await();

        // Assert: All calculations should complete successfully
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    void testIsolationBetweenBulkheads() throws InterruptedException {
        // Arrange: Use structure methods to create bulkheads
        Bulkhead criticalBulkhead = BulkheadStructure.createCriticalServiceBulkhead();
        Bulkhead nonCriticalBulkhead = BulkheadStructure.createNonCriticalServiceBulkhead();

        CountDownLatch latch = new CountDownLatch(1);

        // Act: Fill non-critical bulkhead
        Thread[] nonCriticalThreads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            nonCriticalThreads[i] = new Thread(() -> {
                try {
                    Bulkhead.decorateSupplier(nonCriticalBulkhead, () -> {
                        try {
                            latch.await();
                            return "Complete";
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    }).get();
                } catch (Exception e) {
                    // Handle exception
                }
            });
            nonCriticalThreads[i].start();
        }

        Thread.sleep(100);

        // Assert: Non-critical bulkhead is full, but critical is still available
        assertEquals(0, nonCriticalBulkhead.getMetrics().getAvailableConcurrentCalls());
        assertEquals(5, criticalBulkhead.getMetrics().getAvailableConcurrentCalls());

        // Critical service should still work
        Double result = Bulkhead.decorateSupplier(criticalBulkhead, () -> 1000.0).get();
        assertEquals(1000.0, result);

        // Cleanup
        latch.countDown();
        for (Thread thread : nonCriticalThreads) {
            thread.join();
        }
    }
}
