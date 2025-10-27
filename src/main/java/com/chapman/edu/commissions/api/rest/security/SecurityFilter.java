package com.chapman.edu.commissions.api.rest.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

/**
 * Servlet filter that handles authentication for all HTTP requests.
 *
 * This filter:
 * 1. Extracts authentication credentials from request headers
 * 2. Validates credentials using the AuthenticationManager
 * 3. Sets the SecurityContext for authenticated users
 * 4. Rejects unauthorized requests (optional based on configuration)
 *
 * Design Pattern: Intercepting Filter Pattern
 * Intercepts requests to perform authentication before they reach servlets.
 *
 * Design Principle: Separation of Concerns
 * Authentication logic is separated from business logic in servlets.
 */
public class SecurityFilter implements Filter {

    private final AuthenticationManager authenticationManager;
    private final boolean requireAuthentication;

    /**
     * Constructs a security filter.
     *
     * @param authenticationManager The authentication manager
     * @param requireAuthentication If true, reject unauthenticated requests
     */
    public SecurityFilter(AuthenticationManager authenticationManager, boolean requireAuthentication) {
        this.authenticationManager = authenticationManager;
        this.requireAuthentication = requireAuthentication;
    }

    /**
     * Constructs a security filter that allows anonymous access.
     *
     * @param authenticationManager The authentication manager
     */
    public SecurityFilter(AuthenticationManager authenticationManager) {
        this(authenticationManager, false);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract authentication token from request
            Optional<AuthenticationToken> tokenOpt = extractAuthenticationToken(httpRequest);

            if (tokenOpt.isPresent()) {
                // Attempt authentication
                Optional<UserPrincipal> principalOpt = authenticationManager.authenticate(tokenOpt.get());

                if (principalOpt.isPresent()) {
                    // Authentication successful - set security context
                    SecurityContext.setCurrentUser(principalOpt.get());
                } else if (requireAuthentication) {
                    // Authentication failed and authentication is required
                    sendAuthenticationError(httpResponse, "Invalid credentials");
                    return;
                }
            } else if (requireAuthentication) {
                // No credentials provided and authentication is required
                sendAuthenticationError(httpResponse, "Authentication required");
                return;
            }

            // Proceed with the request
            chain.doFilter(request, response);

        } finally {
            // Always clear security context after request completes
            SecurityContext.clear();
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }

    /**
     * Extracts authentication token from HTTP request headers.
     *
     * Supports:
     * - Authorization: Basic base64(username:password)
     * - Authorization: Bearer jwt-token
     * - X-API-Key: api-key
     *
     * @param request The HTTP request
     * @return Optional containing the authentication token, or empty if not found
     */
    private Optional<AuthenticationToken> extractAuthenticationToken(HttpServletRequest request) {
        // Check Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isEmpty()) {
            if (authHeader.startsWith("Basic ")) {
                return parseBasicAuth(authHeader.substring(6));
            } else if (authHeader.startsWith("Bearer ")) {
                return Optional.of(AuthenticationToken.bearer(authHeader.substring(7)));
            }
        }

        // Check X-API-Key header
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return Optional.of(AuthenticationToken.apiKey(apiKey));
        }

        return Optional.empty();
    }

    /**
     * Parses Basic authentication credentials.
     *
     * Format: base64(username:password)
     *
     * @param encodedCredentials The base64-encoded credentials
     * @return Optional containing the authentication token, or empty if invalid
     */
    private Optional<AuthenticationToken> parseBasicAuth(String encodedCredentials) {
        try {
            String decoded = new String(Base64.getDecoder().decode(encodedCredentials));
            int colonIndex = decoded.indexOf(':');

            if (colonIndex > 0) {
                String username = decoded.substring(0, colonIndex);
                String password = decoded.substring(colonIndex + 1);
                return Optional.of(AuthenticationToken.basic(username, password));
            }
        } catch (IllegalArgumentException e) {
            // Invalid base64 encoding
        }

        return Optional.empty();
    }

    /**
     * Sends an authentication error response.
     *
     * @param response The HTTP response
     * @param message The error message
     */
    private void sendAuthenticationError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Add WWW-Authenticate header to indicate supported schemes
        String[] schemes = authenticationManager.getSupportedSchemes();
        if (schemes.length > 0) {
            response.setHeader("WWW-Authenticate", String.join(", ", schemes));
        }

        String errorJson = String.format(
            "{\"error\": \"%s\", \"status\": 401, \"timestamp\": %d}",
            message,
            System.currentTimeMillis()
        );

        response.getWriter().write(errorJson);
    }
}