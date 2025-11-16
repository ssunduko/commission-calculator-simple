package com.chapman.edu.commissions.api.rest.performance;

import java.time.Duration;

/**
 * Exception thrown when rate limit is exceeded.
 *
 * This exception provides detailed information about the rate limit violation,
 * allowing clients to understand when they can retry.
 *
 * Concepts demonstrated:
 * - Custom Exceptions: Domain-specific error handling
 * - Information Rich: Includes retry-after duration
 * - HTTP 429 Mapping: Too Many Requests status code
 */
public class RateLimitExceededException extends RuntimeException {

    private final String key;
    private final long limit;
    private final Duration retryAfter;

    public RateLimitExceededException(String key, long limit, Duration retryAfter) {
        super(formatMessage(key, limit, retryAfter));
        this.key = key;
        this.limit = limit;
        this.retryAfter = retryAfter;
    }

    private static String formatMessage(String key, long limit, Duration retryAfter) {
        return String.format(
            "Rate limit exceeded for key '%s'. Limit: %d requests. Retry after: %s",
            key, limit, formatDuration(retryAfter)
        );
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return (seconds / 60) + " minutes";
        } else {
            return (seconds / 3600) + " hours";
        }
    }

    public String getKey() {
        return key;
    }

    public long getLimit() {
        return limit;
    }

    public Duration getRetryAfter() {
        return retryAfter;
    }

    /**
     * Get the Retry-After header value in seconds.
     * This is the standard HTTP header for rate limiting.
     */
    public long getRetryAfterSeconds() {
        return retryAfter.getSeconds();
    }
}