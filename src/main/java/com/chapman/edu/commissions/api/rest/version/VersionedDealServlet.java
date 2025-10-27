package com.chapman.edu.commissions.api.rest.version;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.Deal;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet that routes requests to version-specific Deal endpoint implementations.
 *
 * This servlet demonstrates the path-based versioning strategy by:
 * 1. Extracting version from URL path (/api/v1/deals vs /api/v2/deals)
 * 2. Routing to appropriate version handler
 * 3. Handling version negotiation and errors
 *
 * Design Pattern: Router Pattern + Strategy Pattern
 * - Router: Directs requests based on version
 * - Strategy: Different version implementations provide different behavior
 *
 * URL Mapping: /api/*
 * Supported paths:
 *   - /api/v1/deals
 *   - /api/v2/deals
 */
public class VersionedDealServlet extends HttpServlet {

    private final VersionRouter router;

    /**
     * Constructs the versioned deal servlet.
     *
     * @param dealRepository The deal repository to inject into version handlers
     */
    public VersionedDealServlet(Repository<Deal> dealRepository) {
        // Initialize router for "deals" resource
        this.router = new VersionRouter("deals");

        // Register version-specific endpoint implementations
        router.register(ApiVersion.V1, new DealEndpointV1(dealRepository));
        router.register(ApiVersion.V2, new DealEndpointV2(dealRepository));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        routeRequest(request, response, VersionedEndpoint::handleGet);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        routeRequest(request, response, VersionedEndpoint::handlePost);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        routeRequest(request, response, VersionedEndpoint::handlePut);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        routeRequest(request, response, VersionedEndpoint::handleDelete);
    }

    /**
     * Routes the request to the appropriate versioned endpoint.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param handler The method reference to call on the endpoint
     */
    private void routeRequest(HttpServletRequest request, HttpServletResponse response,
            EndpointHandler handler) throws IOException {

        // Extract version from request path
        ApiVersion version = router.extractVersion(request);

        // Add version info to response headers
        response.setHeader("API-Version", version.getVersionNumber());

        // Add deprecation warning if applicable
        if (version.isDeprecated()) {
            response.setHeader("Warning", "299 - \"API version " +
                version.getPathSegment() + " is deprecated. Please migrate to v2.\"");
        }

        // Add experimental warning if applicable
        if (version.isExperimental()) {
            response.setHeader("Warning", "199 - \"API version " +
                version.getPathSegment() + " is experimental and subject to change.\"");
        }

        // Get the versioned endpoint
        var endpoint = router.getEndpoint(version);

        if (endpoint.isEmpty()) {
            sendUnsupportedVersionError(response, version);
            return;
        }

        // Route to version-specific handler
        try {
            handler.handle(endpoint.get(), request, response);
        } catch (Exception e) {
            sendInternalServerError(response, e);
        }
    }

    /**
     * Sends an unsupported version error response.
     */
    private void sendUnsupportedVersionError(HttpServletResponse response, ApiVersion version)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiVersion[] supported = router.getSupportedVersions();
        StringBuilder supportedVersions = new StringBuilder();
        for (int i = 0; i < supported.length; i++) {
            supportedVersions.append(supported[i].getPathSegment());
            if (i < supported.length - 1) {
                supportedVersions.append(", ");
            }
        }

        String errorJson = String.format(
            "{\"error\": \"Unsupported API version: %s\", " +
            "\"supportedVersions\": [%s], " +
            "\"status\": 404}",
            version.getPathSegment(),
            supportedVersions.toString()
        );

        response.getWriter().write(errorJson);
    }

    /**
     * Sends an internal server error response.
     */
    private void sendInternalServerError(HttpServletResponse response, Exception e)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
            "{\"error\": \"Internal server error: %s\", \"status\": 500}",
            e.getMessage()
        );

        response.getWriter().write(errorJson);
    }

    /**
     * Functional interface for endpoint handler method references.
     */
    @FunctionalInterface
    private interface EndpointHandler {
        void handle(VersionedEndpoint endpoint, HttpServletRequest request,
                   HttpServletResponse response) throws Exception;
    }
}