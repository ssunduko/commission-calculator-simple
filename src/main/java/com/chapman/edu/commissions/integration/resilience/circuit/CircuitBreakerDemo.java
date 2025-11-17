package com.chapman.edu.commissions.integration.resilience.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Circuit Breaker Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * The Circuit Breaker pattern prevents an application from repeatedly trying to execute
 * an operation that's likely to fail. It acts like an electrical circuit breaker that
 * trips when there are too many failures, preventing cascading failures in distributed systems.
 *
 * THREE STATES:
 * 1. CLOSED: Normal operation - requests pass through and failures are counted
 * 2. OPEN: Too many failures occurred - requests fail fast without attempting the operation
 * 3. HALF_OPEN: Testing if the problem is resolved - limited requests are allowed through
 *
 * PURPOSE:
 * - Prevent cascading failures in distributed systems
 * - Provide fail-fast behavior when a service is down
 * - Allow the failing service time to recover
 * - Improve system stability and user experience
 *
 * WHEN TO USE:
 * - Making calls to external services (REST APIs, databases, etc.)
 * - Operations that may fail due to network issues
 * - Protecting resources from being overwhelmed
 * - Preventing timeout exhaustion in microservices
 */
public class CircuitBreakerDemo {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerDemo.class);

    /**
     * Demonstrates basic circuit breaker usage
     */
    public static void demonstrateCircuitBreaker() {
        logger.info("=== Circuit Breaker Demo Started ===");

        CircuitBreaker circuitBreaker = CircuitBreakerStructure.createCircuitBreaker();

        // Register event listeners to observe circuit breaker state changes
        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                    logger.info("Circuit Breaker State Transition: {} -> {}",
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState()))
                .onSuccess(event ->
                    logger.debug("Call succeeded. Duration: {} ms",
                            event.getElapsedDuration().toMillis()))
                .onError(event ->
                    logger.debug("Call failed. Duration: {} ms, Error: {}",
                            event.getElapsedDuration().toMillis(),
                            event.getThrowable().getMessage()))
                .onCallNotPermitted(event ->
                    logger.warn("Call not permitted - Circuit is OPEN"));

        // Simulate multiple calls to the commission service
        for (int i = 1; i <= 20; i++) {
            double salesAmount = 1000 + (i * 100);

            try {
                // Decorate the supplier with circuit breaker
                Supplier<Double> decoratedSupplier = CircuitBreaker.decorateSupplier(
                        circuitBreaker,
                        () -> CircuitBreakerStructure.calculateCommissionRemote(salesAmount)
                );

                // Execute the call
                double commission = decoratedSupplier.get();
                logger.info("Request #{}: Commission for ${} is ${}", i, salesAmount, commission);

            } catch (Exception e) {
                // Handle the failure - could use fallback or return error to client
                double fallbackCommission = CircuitBreakerStructure.fallbackCommissionCalculation(salesAmount, e);
                logger.info("Request #{}: Using fallback commission ${} for ${}",
                        i, fallbackCommission, salesAmount);
            }

            // Display current circuit breaker state and metrics
            logger.info("Circuit State: {}, Failure Rate: {}%, Buffered Calls: {}",
                    circuitBreaker.getState(),
                    circuitBreaker.getMetrics().getFailureRate(),
                    circuitBreaker.getMetrics().getNumberOfBufferedCalls());

            // Small delay between calls
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("=== Circuit Breaker Demo Completed ===");
        logger.info("Final State: {}", circuitBreaker.getState());
        logger.info("Total Calls: {}", circuitBreaker.getMetrics().getNumberOfBufferedCalls());
        logger.info("Failed Calls: {}", circuitBreaker.getMetrics().getNumberOfFailedCalls());
        logger.info("Failure Rate: {}%", circuitBreaker.getMetrics().getFailureRate());
    }

    /**
     * Demonstrates circuit breaker with Try.ofSupplier for functional error handling
     */
    public static void demonstrateCircuitBreakerWithTry() {
        logger.info("=== Circuit Breaker with Try Demo ===");

        CircuitBreaker circuitBreaker = CircuitBreakerStructure.createCircuitBreaker();

        // Decorate a supplier
        Supplier<Double> decoratedSupplier = CircuitBreaker.decorateSupplier(
                circuitBreaker,
                () -> CircuitBreakerStructure.calculateCommissionRemote(5000)
        );

        // Using Try for functional error handling (Vavr-style)
        for (int i = 0; i < 10; i++) {
            try {
                Double result = decoratedSupplier.get();
                logger.info("Success: Commission = ${}", result);
            } catch (Exception e) {
                logger.error("Failed: {}", e.getMessage());
                // Use fallback
                Double fallback = CircuitBreakerStructure.fallbackCommissionCalculation(5000, e);
                logger.info("Fallback: Commission = ${}", fallback);
            }
        }
    }

    public static void main(String[] args) {
        demonstrateCircuitBreaker();

        logger.info("\n\n");

        demonstrateCircuitBreakerWithTry();
    }
}
