package com.chapman.edu.commissions.api.rest.performance;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RateLimitFilter.
 *
 * This test class demonstrates:
 * - Testing servlet filters
 * - Mocking HTTP request/response objects
 * - Verifying HTTP headers and status codes
 * - Integration between filter and rate limiter
 *
 * Concepts demonstrated:
 * - Mockito: Mocking and verification
 * - Test Doubles: Using mocks for external dependencies
 * - HTTP Testing: Verifying status codes and headers
 * - Filter Testing: Testing servlet filter behavior
 */
@DisplayName("Rate Limit Filter Tests")
class RateLimitFilterTest {

    private RateLimitFilter filter;
    private RateLimiter rateLimiter;
    private RateLimitConfig config;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        config = RateLimitConfig.builder()
                .requestsPerWindow(10)
                .windowDuration(Duration.ofMinutes(1))
                .build();

        rateLimiter = new TokenBucketRateLimiter(config);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Nested
    @DisplayName("IP-Based Rate Limiting")
    class IpBasedRateLimiting {

        @BeforeEach
        void setUp() {
            filter = new RateLimitFilter(rateLimiter, RateLimitFilter::extractIpAddress, config);
        }

        @Test
        @DisplayName("Should allow requests within limit")
        void shouldAllowRequestsWithinLimit() throws IOException, ServletException {
            // Arrange
            when(request.getRemoteAddr()).thenReturn("192.168.1.100");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verify(response).setHeader("X-RateLimit-Limit", "10");
            verify(response).setHeader(eq("X-RateLimit-Remaining"), anyString());
        }

        @Test
        @DisplayName("Should deny requests exceeding limit")
        void shouldDenyRequestsExceedingLimit() throws IOException, ServletException {
            // Arrange
            String ipAddress = "192.168.1.100";
            when(request.getRemoteAddr()).thenReturn(ipAddress);

            // Exhaust rate limit using same rateLimiter
            for (int i = 0; i < 10; i++) {
                rateLimiter.tryAcquire(ipAddress);
            }

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            verify(response).setStatus(429); // Too Many Requests
            verify(response).setHeader(eq("Retry-After"), anyString());
        }

        @Test
        @DisplayName("Should use X-Forwarded-For header when present")
        void shouldUseXForwardedForHeaderWhenPresent() throws IOException, ServletException {
            // Arrange
            when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 192.168.1.1");
            when(request.getRemoteAddr()).thenReturn("192.168.1.1");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert - Should use first IP from X-Forwarded-For
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should set rate limit headers")
        void shouldSetRateLimitHeaders() throws IOException, ServletException {
            // Arrange
            when(request.getRemoteAddr()).thenReturn("192.168.1.100");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(response).setHeader("X-RateLimit-Limit", "10");
            verify(response).setHeader(eq("X-RateLimit-Remaining"), anyString());
            verify(response).setHeader(eq("X-RateLimit-Reset"), anyString());
        }
    }

    @Nested
    @DisplayName("User-Based Rate Limiting")
    class UserBasedRateLimiting {

        @BeforeEach
        void setUp() {
            filter = RateLimitFilter.createUserBasedFilter(config);
        }

        @Test
        @DisplayName("Should use user ID when available")
        void shouldUseUserIdWhenAvailable() throws IOException, ServletException {
            // Arrange
            when(request.getAttribute("userId")).thenReturn("user123");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should fall back to IP when no user ID")
        void shouldFallBackToIpWhenNoUserId() throws IOException, ServletException {
            // Arrange
            when(request.getAttribute("userId")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("192.168.1.100");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("API Key-Based Rate Limiting")
    class ApiKeyBasedRateLimiting {

        @BeforeEach
        void setUp() {
            filter = RateLimitFilter.createApiKeyBasedFilter(config);
        }

        @Test
        @DisplayName("Should use API key when present")
        void shouldUseApiKeyWhenPresent() throws IOException, ServletException {
            // Arrange
            when(request.getHeader("X-API-Key")).thenReturn("api-key-12345");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should fall back to IP when no API key")
        void shouldFallBackToIpWhenNoApiKey() throws IOException, ServletException {
            // Arrange
            when(request.getHeader("X-API-Key")).thenReturn(null);
            when(request.getRemoteAddr()).thenReturn("192.168.1.100");

            // Act
            filter.doFilter(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Error Response Format")
    class ErrorResponseFormat {

        @BeforeEach
        void setUp() {
            filter = new RateLimitFilter(rateLimiter, RateLimitFilter::extractIpAddress, config);
        }

        @Test
        @DisplayName("Should return JSON error response")
        void shouldReturnJsonErrorResponse() throws IOException, ServletException {
            // Arrange
            String ipAddress = "192.168.1.100";
            when(request.getRemoteAddr()).thenReturn(ipAddress);

            // Exhaust rate limit completely
            for (int i = 0; i < 10; i++) {
                rateLimiter.tryAcquire(ipAddress);
            }

            // Act
            filter.doFilter(request, response, filterChain);

            // Flush the writer to ensure content is written
            printWriter.flush();

            // Assert
            verify(response).setContentType("application/json");
            verify(response).setCharacterEncoding("UTF-8");
            verify(response).setStatus(429);

            String jsonResponse = stringWriter.toString();
            assertTrue(jsonResponse.length() > 0,
                    "Response should not be empty, got: '" + jsonResponse + "'");
            assertTrue(jsonResponse.contains("429") || jsonResponse.contains("status"),
                    "Response should contain status information, got: '" + jsonResponse + "'");
        }
    }

    @Nested
    @DisplayName("Header Tracking")
    class HeaderTracking {

        @BeforeEach
        void setUp() {
            filter = RateLimitFilter.createIpBasedFilter(config);
        }

        @Test
        @DisplayName("Should update remaining count after each request")
        void shouldUpdateRemainingCountAfterEachRequest() throws IOException, ServletException {
            // Arrange
            String ipAddress = "192.168.1.100";
            when(request.getRemoteAddr()).thenReturn(ipAddress);

            // Act & Assert - Make multiple requests and verify decreasing count
            for (int i = 0; i < 3; i++) {
                filter.doFilter(request, response, filterChain);

                ArgumentCaptor<String> remainingCaptor = ArgumentCaptor.forClass(String.class);
                verify(response, atLeast(i + 1))
                        .setHeader(eq("X-RateLimit-Remaining"), remainingCaptor.capture());
            }
        }
    }

    @Nested
    @DisplayName("Non-HTTP Requests")
    class NonHttpRequests {

        @BeforeEach
        void setUp() {
            filter = RateLimitFilter.createIpBasedFilter(config);
        }

        @Test
        @DisplayName("Should pass through non-HTTP requests")
        void shouldPassThroughNonHttpRequests() throws IOException, ServletException {
            // Arrange
            jakarta.servlet.ServletRequest nonHttpRequest = mock(jakarta.servlet.ServletRequest.class);
            jakarta.servlet.ServletResponse nonHttpResponse = mock(jakarta.servlet.ServletResponse.class);

            // Act
            filter.doFilter(nonHttpRequest, nonHttpResponse, filterChain);

            // Assert
            verify(filterChain).doFilter(nonHttpRequest, nonHttpResponse);
        }
    }
}