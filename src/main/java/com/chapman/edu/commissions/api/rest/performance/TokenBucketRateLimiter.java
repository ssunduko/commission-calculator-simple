package com.chapman.edu.commissions.api.rest.performance;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Token Bucket rate limiting algorithm implementation.
 *
 * The Token Bucket algorithm:
 * 1. Tokens are added to a bucket at a fixed rate
 * 2. Each request consumes a token
 * 3. If no tokens available, request is denied
 * 4. Bucket has maximum capacity (allows bursts)
 *
 * Advantages:
 * - Allows burst traffic (up to bucket capacity)
 * - Simple and efficient
 * - Smooth long-term rate limiting
 *
 * Concepts demonstrated:
 * - Token Bucket Algorithm: Industry-standard rate limiting
 * - Thread Safety: ConcurrentHashMap + ReentrantLock
 * - Performance: O(1) acquire operation
 * - Memory Efficiency: Per-key state tracking
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private final RateLimitConfig config;
    private final Map<String, TokenBucket> buckets;

    public TokenBucketRateLimiter(RateLimitConfig config) {
        this.config = config;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean tryAcquire(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(config));
        return bucket.tryConsume();
    }

    @Override
    public boolean tryAcquire(String key, Duration timeout) {
        if (!config.isBlockOnExceeded()) {
            return tryAcquire(key);
        }

        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(config));
        long startTime = System.nanoTime();
        long timeoutNanos = timeout.toNanos();

        while (System.nanoTime() - startTime < timeoutNanos) {
            if (bucket.tryConsume()) {
                return true;
            }

            // Sleep briefly before retrying
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    @Override
    public long getAvailablePermits(String key) {
        TokenBucket bucket = buckets.get(key);
        return bucket != null ? bucket.getAvailableTokens() : config.getRequestsPerWindow();
    }

    @Override
    public Duration getTimeUntilNextPermit(String key) {
        TokenBucket bucket = buckets.get(key);
        if (bucket == null || bucket.getAvailableTokens() > 0) {
            return Duration.ZERO;
        }
        return bucket.getTimeUntilNextToken();
    }

    @Override
    public void reset(String key) {
        buckets.remove(key);
    }

    @Override
    public void resetAll() {
        buckets.clear();
    }

    /**
     * Internal token bucket state for a single key.
     *
     * Thread-safe using ReentrantLock.
     */
    private static class TokenBucket {
        private final long capacity;
        private final double refillRate; // tokens per nanosecond
        private final Lock lock;

        private double tokens;
        private Instant lastRefill;

        public TokenBucket(RateLimitConfig config) {
            this.capacity = config.getRequestsPerWindow();
            this.tokens = capacity; // Start with full bucket
            this.refillRate = (double) config.getRequestsPerWindow() / config.getWindowDuration().toNanos();
            this.lastRefill = Instant.now();
            this.lock = new ReentrantLock();
        }

        public boolean tryConsume() {
            lock.lock();
            try {
                refill();

                if (tokens >= 1.0) {
                    tokens -= 1.0;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public long getAvailableTokens() {
            lock.lock();
            try {
                refill();
                return (long) tokens;
            } finally {
                lock.unlock();
            }
        }

        public Duration getTimeUntilNextToken() {
            lock.lock();
            try {
                refill();

                if (tokens >= 1.0) {
                    return Duration.ZERO;
                }

                double tokensNeeded = 1.0 - tokens;
                long nanosNeeded = (long) (tokensNeeded / refillRate);
                return Duration.ofNanos(nanosNeeded);
            } finally {
                lock.unlock();
            }
        }

        /**
         * Refill tokens based on elapsed time since last refill.
         * Must be called with lock held.
         */
        private void refill() {
            Instant now = Instant.now();
            long nanosSinceLastRefill = Duration.between(lastRefill, now).toNanos();

            if (nanosSinceLastRefill > 0) {
                double tokensToAdd = refillRate * nanosSinceLastRefill;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefill = now;
            }
        }
    }
}