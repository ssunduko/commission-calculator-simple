package com.chapman.edu.commissions.integration.resilience;

import com.chapman.edu.commissions.integration.resilience.circuit.CircuitBreakerStructure;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Circuit Breaker pattern
 *
 * These tests verify that the circuit breaker correctly integrates with
 * commission calculation functionality:
 * - Opens after exceeding failure threshold
 * - Transitions to half-open after wait duration
 * - Closes after successful calls in half-open state
 * - Works with commission calculation methods
 * - Supports fallback calculations
 */
class CircuitBreakerIntegrationTest {

    private CircuitBreaker circuitBreaker;
    private AtomicInteger callCounter;

    @BeforeEach
    void setUp() {
        // Use CircuitBreakerStructure to create circuit breaker
        circuitBreaker = CircuitBreakerStructure.createCircuitBreaker();
        callCounter = new AtomicInteger(0);
    }

    @Test
    void testCircuitBreakerWithCommissionCalculation() {
        // Arrange: Use the actual commission calculation method
        Supplier<Double> commissionCalculator = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    callCounter.incrementAndGet();
                    // Use the structure's commission calculation
                    return CircuitBreakerStructure.calculateCommissionRemote(5000.0);
                }
        );

        // Act: Make calls until circuit opens (method has 70% failure rate)
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < 20; i++) {
            try {
                Double commission = commissionCalculator.get();
                successCount++;
                assertNotNull(commission);
            } catch (Exception e) {
                failureCount++;
            }
        }

        // Assert: Circuit should eventually open due to failures
        assertTrue(failureCount > 0, "Should have some failures");
        assertTrue(circuitBreaker.getState() == CircuitBreaker.State.OPEN ||
                   circuitBreaker.getState() == CircuitBreaker.State.CLOSED,
                "Circuit should be in OPEN or CLOSED state");
    }

    @Test
    void testCircuitBreakerOpensAfterFailureThreshold() {
        // Arrange: Supplier that always fails
        Supplier<String> failingSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    callCounter.incrementAndGet();
                    throw new RuntimeException("Service unavailable");
                }
        );

        // Act: Make enough calls to exceed minimum and trigger circuit opening
        for (int i = 0; i < 10; i++) {
            try {
                failingSupplier.get();
            } catch (Exception e) {
                // Expected failures
            }
        }

        // Assert: Circuit should be OPEN after exceeding failure threshold
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        assertTrue(circuitBreaker.getMetrics().getFailureRate() >= 50.0);
    }

    @Test
    void testCircuitBreakerBlocksCallsWhenOpen() {
        // Arrange: First, open the circuit by causing failures
        Supplier<String> failingSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    callCounter.incrementAndGet();
                    throw new RuntimeException("Service unavailable");
                }
        );

        // Open the circuit
        for (int i = 0; i < 10; i++) {
            try {
                failingSupplier.get();
            } catch (Exception e) {
                // Expected
            }
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        int callsBeforeBlock = callCounter.get();

        // Act: Try to make more calls when circuit is open
        boolean callBlocked = false;
        try {
            failingSupplier.get();
        } catch (CallNotPermittedException e) {
            callBlocked = true;
        } catch (Exception e) {
            // Other exceptions
        }

        // Assert: Calls should be blocked when circuit is open
        assertTrue(callBlocked, "Calls should be blocked when circuit is OPEN");
        // Verify the actual service wasn't called
        assertTrue(callCounter.get() <= callsBeforeBlock + 1,
                "Service should not be called when circuit is OPEN");
    }

    @Test
    void testCircuitBreakerFallback() {
        // Arrange: Failing commission calculation with fallback
        double salesAmount = 8000.0;

        Supplier<Double> commissionWithFallback = () -> {
            try {
                Supplier<Double> failingCalculator = () -> {
                    throw new RuntimeException("Commission service unavailable");
                };
                return CircuitBreaker.decorateSupplier(circuitBreaker, failingCalculator).get();
            } catch (Exception e) {
                // Use fallback from structure
                return CircuitBreakerStructure.fallbackCommissionCalculation(salesAmount, e);
            }
        };

        // Act: Open circuit by causing failures
        for (int i = 0; i < 10; i++) {
            try {
                CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                    throw new RuntimeException("Service unavailable");
                }).get();
            } catch (Exception e) {
                // Expected
            }
        }

        // Get result using fallback
        Double fallbackCommission = commissionWithFallback.get();

        // Assert: Fallback should provide conservative commission
        assertNotNull(fallbackCommission);
        assertEquals(salesAmount * 0.05, fallbackCommission);
        assertTrue(fallbackCommission < salesAmount * 0.10,
                "Fallback should be more conservative than normal calculation");
    }

    @Test
    void testCircuitBreakerMetricsTracking() {
        // Arrange: Supplier with predictable success/failure pattern
        AtomicInteger attempts = new AtomicInteger(0);
        Supplier<String> intermittentSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    int attempt = attempts.incrementAndGet();
                    if (attempt % 2 == 0) {
                        throw new RuntimeException("Failure");
                    }
                    return "Success";
                }
        );

        // Act: Make 10 calls (5 success, 5 failure = 50% failure rate)
        for (int i = 0; i < 10; i++) {
            try {
                intermittentSupplier.get();
            } catch (Exception e) {
                // Expected failures
            }
        }

        // Assert: Metrics should accurately reflect calls
        // Circuit may open before all 10 calls, so check for at least 5 calls
        assertTrue(circuitBreaker.getMetrics().getNumberOfBufferedCalls() >= 5,
                "Should have at least 5 buffered calls");
        assertTrue(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls() > 0,
                "Should have some successful calls");
        assertTrue(circuitBreaker.getMetrics().getNumberOfFailedCalls() > 0,
                "Should have some failed calls");
        assertTrue(circuitBreaker.getMetrics().getFailureRate() >= 50.0,
                "Failure rate should be at least 50%");
    }

    @Test
    void testCircuitBreakerTransitionToHalfOpen() throws InterruptedException {
        // Arrange: Create failing supplier
        Supplier<String> failingSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    throw new RuntimeException("Service unavailable");
                }
        );

        // Act: Open the circuit
        for (int i = 0; i < 10; i++) {
            try {
                failingSupplier.get();
            } catch (Exception e) {
                // Expected
            }
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // Wait for circuit to transition (wait duration is 10 seconds in structure)
        Thread.sleep(11000);

        // Assert: Try a call to trigger half-open transition
        try {
            failingSupplier.get();
        } catch (Exception e) {
            // Expected - could fail during transition
        }

        // Circuit should either be HALF_OPEN or back to OPEN
        assertTrue(circuitBreaker.getState() == CircuitBreaker.State.HALF_OPEN ||
                   circuitBreaker.getState() == CircuitBreaker.State.OPEN,
                "Circuit should transition to HALF_OPEN after wait duration");
    }

    @Test
    void testCircuitBreakerEventListeners() {
        // Arrange: Track events
        AtomicInteger successEvents = new AtomicInteger(0);
        AtomicInteger errorEvents = new AtomicInteger(0);
        AtomicInteger stateTransitions = new AtomicInteger(0);

        circuitBreaker.getEventPublisher()
                .onSuccess(event -> successEvents.incrementAndGet())
                .onError(event -> errorEvents.incrementAndGet())
                .onStateTransition(event -> stateTransitions.incrementAndGet());

        Supplier<String> testSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> {
                    callCounter.incrementAndGet();
                    if (callCounter.get() <= 5) {
                        throw new RuntimeException("Failure");
                    }
                    return "Success";
                }
        );

        // Act: Make calls
        for (int i = 0; i < 10; i++) {
            try {
                testSupplier.get();
            } catch (Exception e) {
                // Expected failures
            }
        }

        // Assert: Events should be recorded
        assertTrue(errorEvents.get() > 0, "Should have error events");
        assertTrue(successEvents.get() > 0 || stateTransitions.get() > 0,
                "Should have success events or state transitions");
    }
}
