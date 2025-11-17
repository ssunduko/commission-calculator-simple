package com.chapman.edu.commissions.integration.resilience.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Random;

/**
 * Structure and Configuration for Circuit Breaker Pattern
 *
 * This class contains:
 * - Circuit Breaker configuration builders
 * - Simulated service methods
 * - Fallback implementations
 * - Helper utilities
 */
public class CircuitBreakerStructure {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerStructure.class);
    private static final Random random = new Random();

    /**
     * Creates a CircuitBreaker with custom configuration
     *
     * @return Configured CircuitBreaker instance
     */
    public static CircuitBreaker createCircuitBreaker() {
        // Configure the Circuit Breaker with specific thresholds and behaviors
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // Failure rate threshold: Circuit opens if 50% of calls fail
                .failureRateThreshold(50)

                // Minimum number of calls before circuit breaker can calculate failure rate
                // This prevents the circuit from opening due to a few initial failures
                .minimumNumberOfCalls(5)

                // Number of calls allowed in HALF_OPEN state to test if service recovered
                .permittedNumberOfCallsInHalfOpenState(3)

                // How long the circuit stays OPEN before transitioning to HALF_OPEN
                .waitDurationInOpenState(Duration.ofSeconds(10))

                // Size of the sliding window used to record the outcome of calls
                .slidingWindowSize(10)

                // Type of sliding window: COUNT_BASED (by number of calls) or TIME_BASED
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)

                // Slow call duration threshold - calls taking longer are considered failures
                .slowCallDurationThreshold(Duration.ofSeconds(2))

                // Percentage of slow calls that will open the circuit
                .slowCallRateThreshold(50)

                // Automatically transition from OPEN to HALF_OPEN
                .automaticTransitionFromOpenToHalfOpenEnabled(true)

                .build();

        // Create a registry to manage multiple circuit breakers
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        // Create and return a circuit breaker with a unique name
        return registry.circuitBreaker("commissionServiceCircuitBreaker");
    }

    /**
     * Simulates a remote commission calculation service that may fail
     * @param salesAmount The sales amount to calculate commission for
     * @return Calculated commission
     * @throws RuntimeException When the service fails (simulated failure)
     */
    public static double calculateCommissionRemote(double salesAmount) {
        // Simulate random failures (70% success rate)
        if (random.nextDouble() > 0.7) {
            logger.error("Commission service failed for amount: {}", salesAmount);
            throw new RuntimeException("Remote commission service is unavailable");
        }
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double commission = salesAmount * 0.10; // 10% commission
        logger.info("Commission calculated successfully: {} for sales: {}", commission, salesAmount);
        return commission;
    }
    /**
     * Fallback method called when circuit is OPEN or call fails
     *
     * @param salesAmount The sales amount
     * @param throwable The exception that triggered the fallback
     * @return Default commission value
     */
    public static double fallbackCommissionCalculation(double salesAmount, Throwable throwable) {
        logger.warn("Using fallback commission calculation. Reason: {}", throwable.getMessage());
        // Return a conservative estimate or cached value
        return salesAmount * 0.05; // Conservative 5% commission as fallback
    }
}
