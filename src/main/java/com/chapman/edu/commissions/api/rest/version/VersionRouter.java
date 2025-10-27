package com.chapman.edu.commissions.api.rest.version;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Router that directs requests to the appropriate versioned endpoint handler.
 *
 * This class implements the routing logic for path-based API versioning.
 * It extracts the version from the URL path and delegates to the appropriate
 * version-specific handler.
 *
 * Design Pattern: Strategy Pattern + Registry Pattern
 * - Strategy: Different version handlers provide different implementations
 * - Registry: Maintains a map of versions to their handlers
 *
 * URL Structure: /api/{version}/{resource}/{id}
 * Examples:
 *   - /api/v1/deals/DEAL-001
 *   - /api/v2/deals?page=1&limit=10
 */
public class VersionRouter {

    private final Map<ApiVersion, VersionedEndpoint> endpoints;
    private final String resourceName;

    /**
     * Constructs a version router for a specific resource.
     *
     * @param resourceName The name of the resource (e.g., "deals", "users")
     */
    public VersionRouter(String resourceName) {
        this.resourceName = resourceName;
        this.endpoints = new HashMap<>();
    }

    /**
     * Registers a versioned endpoint handler.
     *
     * @param version The API version
     * @param endpoint The endpoint handler for this version
     * @return This router (for method chaining)
     */
    public VersionRouter register(ApiVersion version, VersionedEndpoint endpoint) {
        endpoints.put(version, endpoint);
        return this;
    }

    /**
     * Gets the endpoint handler for a specific version.
     *
     * @param version The API version
     * @return Optional containing the endpoint, or empty if not found
     */
    public Optional<VersionedEndpoint> getEndpoint(ApiVersion version) {
        return Optional.ofNullable(endpoints.get(version));
    }

    /**
     * Extracts the API version from the request path.
     *
     * Expected path format: /api/{version}/{resource}/...
     * Example: /api/v2/deals/DEAL-001 -> V2
     *
     * @param request The HTTP request
     * @return The extracted API version, or default version if not found
     */
    public ApiVersion extractVersion(HttpServletRequest request) {
        // Use request URI which includes the full path including servlet mapping
        String path = request.getRequestURI();

        if (path == null || path.isEmpty()) {
            return ApiVersion.getDefault();
        }

        // Remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Split path into segments: [api, v1, deals, ...]
        String[] segments = path.split("/");

        // Version is the second segment (after "api")
        if (segments.length > 1) {
            ApiVersion version = ApiVersion.fromPathSegment(segments[1]);
            if (version != null) {
                return version;
            }
        }

        // Return default version if no valid version found
        return ApiVersion.getDefault();
    }

    /**
     * Checks if a version is supported by this router.
     *
     * @param version The API version to check
     * @return true if supported, false otherwise
     */
    public boolean supportsVersion(ApiVersion version) {
        return endpoints.containsKey(version);
    }

    /**
     * Gets all supported versions for this resource.
     *
     * @return Array of supported API versions
     */
    public ApiVersion[] getSupportedVersions() {
        return endpoints.keySet().toArray(new ApiVersion[0]);
    }

    /**
     * Gets the resource name for this router.
     *
     * @return The resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Builds a versioned path for this resource.
     *
     * @param version The API version
     * @param resourceId Optional resource ID
     * @return The constructed path (e.g., "/api/v2/deals" or "/api/v2/deals/DEAL-001")
     */
    public String buildPath(ApiVersion version, String resourceId) {
        StringBuilder path = new StringBuilder("/api/");
        path.append(version.getPathSegment());
        path.append("/");
        path.append(resourceName);

        if (resourceId != null && !resourceId.isEmpty()) {
            path.append("/");
            path.append(resourceId);
        }

        return path.toString();
    }
}