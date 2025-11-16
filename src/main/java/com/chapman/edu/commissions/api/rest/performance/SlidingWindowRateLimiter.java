package com.chapman.edu.commissions.api.rest.performance;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Sliding Window rate limiting algorithm implementation.
 *
 * The Sliding Window algorithm:
 * 1. Maintains a queue of request timestamps
 * 2. On each request, removes timestamps older than window duration
 * 3. Checks if remaining count exceeds limit
 * 4. More accurate than fixed window, no boundary issues
 *
 * Advantages:
 * - No boundary reset issues (unlike fixed window)
 * - Accurate request counting
 * - Prevents burst at window boundaries
 *
 * Trade-offs:
 * - Higher memory usage (stores all timestamps)
 * - O(n) cleanup operation per request
 *
 * Concepts demonstrated:
 * - Sliding Window Algorithm: Accurate time-based limiting
 * - Thread Safety: Per-key locking
 * - Memory Management: Automatic cleanup of old entries
 * - Performance vs Accuracy: Different trade-offs than token bucket
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    private final RateLimitConfig config;
    private final Map<String, RequestWindow> windows;

    public SlidingWindowRateLimiter(RateLimitConfig config) {
        this.config = config;
        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public boolean tryAcquire(String key) {
        RequestWindow window = windows.computeIfAbsent(key, k -> new RequestWindow(config));
        return window.tryAddRequest();
    }

    @Override
    public boolean tryAcquire(String key, Duration timeout) {
        if (!config.isBlockOnExceeded()) {
            return tryAcquire(key);
        }

        RequestWindow window = windows.computeIfAbsent(key, k -> new RequestWindow(config));
        long startTime = System.nanoTime();
        long timeoutNanos = timeout.toNanos();

        while (System.nanoTime() - startTime < timeoutNanos) {
            if (window.tryAddRequest()) {
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
        RequestWindow window = windows.get(key);
        if (window == null) {
            return config.getRequestsPerWindow();
        }
        return window.getAvailableRequests();
    }

    @Override
    public Duration getTimeUntilNextPermit(String key) {
        RequestWindow window = windows.get(key);
        if (window == null) {
            return Duration.ZERO;
        }
        return window.getTimeUntilNextSlot();
    }

    @Override
    public void reset(String key) {
        windows.remove(key);
    }

    @Override
    public void resetAll() {
        windows.clear();
    }

    /**
     * Internal sliding window state for a single key.
     *
     * Maintains a queue of request timestamps and automatically
     * removes expired entries.
     */
    private static class RequestWindow {
        private final long limit;
        private final Duration windowDuration;
        private final Queue<Instant> requestTimestamps;
        private final Lock lock;

        public RequestWindow(RateLimitConfig config) {
            this.limit = config.getRequestsPerWindow();
            this.windowDuration = config.getWindowDuration();
            this.requestTimestamps = new ConcurrentLinkedQueue<>();
            this.lock = new ReentrantLock();
        }

        public boolean tryAddRequest() {
            lock.lock();
            try {
                removeExpiredRequests();

                if (requestTimestamps.size() < limit) {
                    requestTimestamps.offer(Instant.now());
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public long getAvailableRequests() {
            lock.lock();
            try {
                removeExpiredRequests();
                return limit - requestTimestamps.size();
            } finally {
                lock.unlock();
            }
        }

        public Duration getTimeUntilNextSlot() {
            lock.lock();
            try {
                removeExpiredRequests();

                if (requestTimestamps.size() < limit) {
                    return Duration.ZERO;
                }

                // Next slot available when oldest request expires
                Instant oldest = requestTimestamps.peek();
                if (oldest == null) {
                    return Duration.ZERO;
                }

                Instant nextSlotTime = oldest.plus(windowDuration);
                return Duration.between(Instant.now(), nextSlotTime);
            } finally {
                lock.unlock();
            }
        }

        /**
         * Remove request timestamps older than the window duration.
         * Must be called with lock held.
         */
        private void removeExpiredRequests() {
            Instant cutoff = Instant.now().minus(windowDuration);

            while (!requestTimestamps.isEmpty()) {
                Instant oldest = requestTimestamps.peek();
                if (oldest != null && oldest.isBefore(cutoff)) {
                    requestTimestamps.poll();
                } else {
                    break;
                }
            }
        }
    }
}