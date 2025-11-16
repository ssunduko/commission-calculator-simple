package com.chapman.edu.commissions.api.rest.performance;

import java.time.Duration;

/**
 * Configuration for rate limiting.
 *
 * This class encapsulates rate limit settings, demonstrating:
 * - Value Object Pattern: Immutable configuration
 * - Builder Pattern: Fluent configuration API
 * - Sensible Defaults: Pre-configured for common use cases
 *
 * Concepts demonstrated:
 * - Immutability: Thread-safe configuration
 * - Builder Pattern: Flexible construction
 * - Performance Tuning: Configurable thresholds
 */
public class RateLimitConfig {

    private final long requestsPerWindow;
    private final Duration windowDuration;
    private final boolean blockOnExceeded;
    private final Duration maxWaitTime;

    private RateLimitConfig(Builder builder) {
        this.requestsPerWindow = builder.requestsPerWindow;
        this.windowDuration = builder.windowDuration;
        this.blockOnExceeded = builder.blockOnExceeded;
        this.maxWaitTime = builder.maxWaitTime;
    }

    /**
     * Default configuration: 100 requests per minute, non-blocking.
     */
    public static RateLimitConfig defaultConfig() {
        return builder()
                .requestsPerWindow(100)
                .windowDuration(Duration.ofMinutes(1))
                .blockOnExceeded(false)
                .build();
    }

    /**
     * Strict configuration: 10 requests per minute, blocking up to 5 seconds.
     */
    public static RateLimitConfig strictConfig() {
        return builder()
                .requestsPerWindow(10)
                .windowDuration(Duration.ofMinutes(1))
                .blockOnExceeded(true)
                .maxWaitTime(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Permissive configuration: 1000 requests per minute, non-blocking.
     */
    public static RateLimitConfig permissiveConfig() {
        return builder()
                .requestsPerWindow(1000)
                .windowDuration(Duration.ofMinutes(1))
                .blockOnExceeded(false)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getRequestsPerWindow() {
        return requestsPerWindow;
    }

    public Duration getWindowDuration() {
        return windowDuration;
    }

    public boolean isBlockOnExceeded() {
        return blockOnExceeded;
    }

    public Duration getMaxWaitTime() {
        return maxWaitTime;
    }

    /**
     * Calculate effective rate limit in requests per second.
     */
    public double getRequestsPerSecond() {
        return (double) requestsPerWindow / windowDuration.getSeconds();
    }

    @Override
    public String toString() {
        return String.format("RateLimitConfig{%d requests per %s, block=%s, maxWait=%s}",
                requestsPerWindow, windowDuration, blockOnExceeded, maxWaitTime);
    }

    /**
     * Builder for constructing RateLimitConfig instances.
     */
    public static class Builder {
        private long requestsPerWindow = 100;
        private Duration windowDuration = Duration.ofMinutes(1);
        private boolean blockOnExceeded = false;
        private Duration maxWaitTime = Duration.ofSeconds(1);

        public Builder requestsPerWindow(long requestsPerWindow) {
            if (requestsPerWindow <= 0) {
                throw new IllegalArgumentException("Requests per window must be positive");
            }
            this.requestsPerWindow = requestsPerWindow;
            return this;
        }

        public Builder windowDuration(Duration windowDuration) {
            if (windowDuration.isNegative() || windowDuration.isZero()) {
                throw new IllegalArgumentException("Window duration must be positive");
            }
            this.windowDuration = windowDuration;
            return this;
        }

        public Builder blockOnExceeded(boolean blockOnExceeded) {
            this.blockOnExceeded = blockOnExceeded;
            return this;
        }

        public Builder maxWaitTime(Duration maxWaitTime) {
            if (maxWaitTime.isNegative()) {
                throw new IllegalArgumentException("Max wait time cannot be negative");
            }
            this.maxWaitTime = maxWaitTime;
            return this;
        }

        public RateLimitConfig build() {
            return new RateLimitConfig(this);
        }
    }
}