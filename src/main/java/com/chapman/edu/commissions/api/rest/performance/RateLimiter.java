package com.chapman.edu.commissions.api.rest.performance;

import java.time.Duration;

/**
 * Rate limiter interface for controlling request throughput.
 *
 * This interface demonstrates the Strategy Pattern, allowing different
 * rate limiting algorithms to be used interchangeably.
 *
 * Common algorithms:
 * - Token Bucket: Allows burst traffic while maintaining average rate
 * - Sliding Window: Tracks requests in time windows
 * - Fixed Window: Simple counter reset at intervals
 * - Leaky Bucket: Smooths out burst traffic
 *
 * Concepts demonstrated:
 * - Strategy Pattern: Multiple interchangeable algorithms
 * - Interface Segregation: Minimal, focused interface
 * - Performance Engineering: Request throttling
 */
public interface RateLimiter {

    /**
     * Attempt to acquire permission to proceed.
     *
     * @param key The identifier for rate limiting (e.g., IP address, user ID, API key)
     * @return true if request is allowed, false if rate limit exceeded
     */
    boolean tryAcquire(String key);

    /**
     * Attempt to acquire permission, blocking for up to the specified timeout.
     *
     * @param key The identifier for rate limiting
     * @param timeout Maximum time to wait for permission
     * @return true if permission granted, false if timeout elapsed
     */
    boolean tryAcquire(String key, Duration timeout);

    /**
     * Get the number of requests remaining for the key in the current window.
     *
     * @param key The identifier for rate limiting
     * @return Number of remaining requests, or -1 if unlimited
     */
    long getAvailablePermits(String key);

    /**
     * Get the time until the next permit becomes available.
     *
     * @param key The identifier for rate limiting
     * @return Duration until next permit, or Duration.ZERO if permits available
     */
    Duration getTimeUntilNextPermit(String key);

    /**
     * Reset the rate limit for a specific key.
     * Useful for testing or administrative purposes.
     *
     * @param key The identifier to reset
     */
    void reset(String key);

    /**
     * Reset all rate limits.
     * Useful for testing or clearing state.
     */
    void resetAll();
}
