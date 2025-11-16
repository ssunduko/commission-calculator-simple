package com.chapman.edu.commissions.api.rest.performance;

import com.chapman.edu.commissions.api.rest.JsonHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.function.Function;

/**
 * Servlet Filter for applying rate limiting to HTTP requests.
 *
 * This filter intercepts all requests and applies rate limiting based on
 * a configurable key extractor (e.g., IP address, user ID, API key).
 *
 * HTTP Headers set:
 * - X-RateLimit-Limit: Maximum requests allowed
 * - X-RateLimit-Remaining: Requests remaining in current window
 * - X-RateLimit-Reset: Time until limit resets (seconds)
 * - Retry-After: When exceeded, time until retry allowed
 *
 * Concepts demonstrated:
 * - Intercepting Filter Pattern: Cross-cutting concern
 * - Rate Limiting: Performance and abuse prevention
 * - HTTP Standards: Standard rate limit headers
 * - Strategy Pattern: Pluggable key extraction and rate limiters
 * - Single Responsibility: Filter handles only rate limiting
 */
public class RateLimitFilter implements Filter {

    private final RateLimiter rateLimiter;
    private final Function<HttpServletRequest, String> keyExtractor;
    private final RateLimitConfig config;

    /**
     * Create filter with custom rate limiter and key extractor.
     *
     * @param rateLimiter The rate limiting strategy
     * @param keyExtractor Function to extract rate limit key from request
     * @param config Rate limit configuration
     */
    public RateLimitFilter(
            RateLimiter rateLimiter,
            Function<HttpServletRequest, String> keyExtractor,
            RateLimitConfig config) {
        this.rateLimiter = rateLimiter;
        this.keyExtractor = keyExtractor;
        this.config = config;
    }

    /**
     * Create filter with default IP-based rate limiting.
     */
    public static RateLimitFilter createIpBasedFilter(RateLimitConfig config) {
        RateLimiter limiter = new TokenBucketRateLimiter(config);
        return new RateLimitFilter(limiter, RateLimitFilter::extractIpAddress, config);
    }

    /**
     * Create filter with user-based rate limiting (requires authentication).
     */
    public static RateLimitFilter createUserBasedFilter(RateLimitConfig config) {
        RateLimiter limiter = new TokenBucketRateLimiter(config);
        return new RateLimitFilter(limiter, RateLimitFilter::extractUserId, config);
    }

    /**
     * Create filter with API key-based rate limiting.
     */
    public static RateLimitFilter createApiKeyBasedFilter(RateLimitConfig config) {
        RateLimiter limiter = new TokenBucketRateLimiter(config);
        return new RateLimitFilter(limiter, RateLimitFilter::extractApiKey, config);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String key = keyExtractor.apply(httpRequest);

        // Try to acquire permission
        boolean allowed = rateLimiter.tryAcquire(key);

        // Set rate limit headers
        setRateLimitHeaders(httpResponse, key);

        if (allowed) {
            // Request allowed, proceed
            chain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            handleRateLimitExceeded(httpResponse, key);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }

    /**
     * Set standard rate limit headers on the response.
     */
    private void setRateLimitHeaders(HttpServletResponse response, String key) {
        long limit = config.getRequestsPerWindow();
        long remaining = rateLimiter.getAvailablePermits(key);
        Duration resetTime = rateLimiter.getTimeUntilNextPermit(key);

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
        response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime.getSeconds()));
    }

    /**
     * Handle rate limit exceeded - send 429 Too Many Requests.
     */
    private void handleRateLimitExceeded(HttpServletResponse response, String key) throws IOException {
        Duration retryAfter = rateLimiter.getTimeUntilNextPermit(key);

        response.setStatus(429); // Too Many Requests
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(retryAfter.getSeconds()));

        RateLimitErrorResponse error = new RateLimitErrorResponse(
            429,
            "Rate limit exceeded",
            config.getRequestsPerWindow(),
            retryAfter.getSeconds()
        );

        PrintWriter out = response.getWriter();
        out.print(JsonHelper.toJson(error));
        out.flush();
    }

    /**
     * Extract IP address from request.
     * Package-private for testing.
     */
    static String extractIpAddress(HttpServletRequest request) {
        // Check for X-Forwarded-For header (proxy/load balancer)
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }

        // Fall back to remote address
        return request.getRemoteAddr();
    }

    /**
     * Extract user ID from request (assumes SecurityContext is set).
     * Package-private for testing.
     */
    static String extractUserId(HttpServletRequest request) {
        // This would integrate with SecurityContext from security package
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            return userId;
        }

        // Fall back to IP if no user context
        return extractIpAddress(request);
    }

    /**
     * Extract API key from request headers.
     * Package-private for testing.
     */
    static String extractApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }

        // Fall back to IP if no API key
        return extractIpAddress(request);
    }

    /**
     * Error response object for rate limit violations.
     */
    private static class RateLimitErrorResponse {
        private final int status;
        private final String message;
        private final long limit;
        private final long retryAfter;
        private final long timestamp;

        public RateLimitErrorResponse(int status, String message, long limit, long retryAfter) {
            this.status = status;
            this.message = message;
            this.limit = limit;
            this.retryAfter = retryAfter;
            this.timestamp = System.currentTimeMillis();
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public long getLimit() { return limit; }
        public long getRetryAfter() { return retryAfter; }
        public long getTimestamp() { return timestamp; }
    }
}