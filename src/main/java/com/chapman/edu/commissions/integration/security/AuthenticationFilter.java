package com.chapman.edu.commissions.integration.security;

import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * AuthenticationFilter - Servlet Filter for HTTP Basic Authentication.
 *
 * This filter demonstrates:
 * - Servlet Filter pattern for cross-cutting concerns
 * - HTTP Basic Authentication implementation
 * - Request interception and validation
 * - Security best practices (authentication before authorization)
 *
 * How it works:
 * 1. Intercepts all requests to protected endpoints
 * 2. Checks for Authorization header with Basic Auth credentials
 * 3. Validates credentials against UserService
 * 4. Allows or rejects the request based on authentication
 * 5. Sets authenticated user in request attribute for downstream use
 *
 * Concepts:
 * - Filter Chain pattern: Allows request to proceed or blocks it
 * - HTTP Basic Auth: "Authorization: Basic base64(email:password)"
 * - Stateless authentication: Each request must include credentials
 * - WWW-Authenticate header: Prompts browser for credentials
 *
 * Public endpoints (no authentication required):
 * - / - Root index page
 * - /index.html - Main index page
 * - /dashboard.html - Public dashboard
 * - /webjars/* - Static web resources
 *
 * Protected endpoints (authentication required):
 * - /api/v1/integration/* - All integration API endpoints
 * - /swagger-ui/* - Swagger UI interface (SECURED)
 * - /api-docs/* - OpenAPI documentation (SECURED)
 * - /h2-console/* - H2 database console (SECURED)
 */
public class AuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    // User attribute key for storing authenticated user in request
    public static final String USER_ATTRIBUTE = "authenticated.user";

    private final UserService userService;

    /**
     * Constructor with dependency injection.
     *
     * @param userService The service for user authentication
     */
    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Initializes the filter.
     * Called once when the filter is first loaded.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AuthenticationFilter initialized");
    }

    /**
     * Main filter method that intercepts requests.
     * Implements the authentication logic.
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filter chain
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Allow public endpoints without authentication
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint accessed: {}", path);
            chain.doFilter(request, response);
            return;
        }

        // Get Authorization header
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null) {
            // No credentials provided - challenge for authentication
            logger.debug("No Authorization header found for: {}", path);
            sendAuthenticationChallenge(httpResponse);
            return;
        }

        // Authenticate using Basic Auth
        Optional<User> userOpt = userService.authenticateBasic(authHeader);

        if (userOpt.isEmpty()) {
            // Invalid credentials
            logger.warn("Authentication failed for: {}", path);
            sendAuthenticationChallenge(httpResponse);
            return;
        }

        // Authentication successful
        User user = userOpt.get();
        logger.info("User authenticated: {} for path: {}", user.getEmail(), path);

        // Store authenticated user in request attribute
        // This allows servlets to access the user without re-authenticating
        httpRequest.setAttribute(USER_ATTRIBUTE, user);

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Checks if the endpoint is public (no authentication required).
     *
     * Security Policy:
     * - All development/admin tools require authentication (Swagger, H2 Console)
     * - Public web UI pages are accessible without authentication
     * - Static resources (CSS, JS) are public
     * - All API endpoints require authentication
     *
     * @param path The request path
     * @return true if endpoint is public
     */
    private boolean isPublicEndpoint(String path) {
        // Public web pages
        if (path.equals("/") ||
            path.equals("/index.html") ||
            path.equals("/dashboard.html") ||
            path.startsWith("/ui") ||
            path.startsWith("/jsp")) {
            return true;
        }

        // Static resources
        if (path.startsWith("/webjars") ||
            path.endsWith(".css") ||
            path.endsWith(".js") ||
            path.endsWith(".png") ||
            path.endsWith(".jpg") ||
            path.endsWith(".ico")) {
            return true;
        }

        // All other endpoints require authentication:
        // - /api/v1/integration/* (API endpoints)
        // - /swagger-ui/* (Swagger UI - SECURED)
        // - /api-docs (OpenAPI spec - SECURED)
        // - /h2-console/* (Database console - SECURED)
        return false;
    }

    /**
     * Sends HTTP 401 Unauthorized response with WWW-Authenticate header.
     * This prompts the browser to show a login dialog.
     *
     * @param response The HTTP response
     */
    private void sendAuthenticationChallenge(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Basic realm=\"Commission Calculator API\"");
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Authentication required\"}");
    }

    /**
     * Destroys the filter.
     * Called when the filter is being taken out of service.
     */
    @Override
    public void destroy() {
        logger.info("AuthenticationFilter destroyed");
    }

    /**
     * Helper method to extract authenticated user from request.
     * Can be used by servlets to get the current user.
     *
     * @param request The HTTP request
     * @return Optional containing the authenticated user
     */
    public static Optional<User> getAuthenticatedUser(HttpServletRequest request) {
        User user = (User) request.getAttribute(USER_ATTRIBUTE);
        return Optional.ofNullable(user);
    }
}