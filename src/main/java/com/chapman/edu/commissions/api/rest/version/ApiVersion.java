package com.chapman.edu.commissions.api.rest.version;

/**
 * Enumeration representing different versions of the REST API.
 *
 * This enum supports path-based versioning strategy where version
 * information is embedded in the URL path (e.g., /api/v1/deals, /api/v2/deals).
 *
 * Design Pattern: Strategy Pattern
 * Each version can have different implementations of the same API endpoints.
 */
public enum ApiVersion {
    /**
     * Version 1.0 - Initial API release
     * Features:
     * - Basic CRUD operations
     * - Simple filtering
     * - No pagination
     */
    V1("v1", "1.0", "Initial API release with basic functionality"),

    /**
     * Version 2.0 - Enhanced API
     * Features:
     * - All V1 features
     * - Pagination support
     * - Advanced filtering
     * - Enhanced error responses
     * - Additional computed fields
     */
    V2("v2", "2.0", "Enhanced API with pagination and advanced features"),

    /**
     * Version 3.0 - Latest API (Future)
     * Features:
     * - All V2 features
     * - GraphQL-style field selection
     * - Batch operations
     * - WebSocket support
     */
    V3("v3", "3.0", "Latest API with GraphQL-style features");

    private final String pathSegment;
    private final String versionNumber;
    private final String description;

    /**
     * Constructs an API version.
     *
     * @param pathSegment The URL path segment (e.g., "v1", "v2")
     * @param versionNumber The semantic version number (e.g., "1.0", "2.0")
     * @param description Human-readable description of this version
     */
    ApiVersion(String pathSegment, String versionNumber, String description) {
        this.pathSegment = pathSegment;
        this.versionNumber = versionNumber;
        this.description = description;
    }

    /**
     * Gets the URL path segment for this version.
     *
     * @return The path segment (e.g., "v1")
     */
    public String getPathSegment() {
        return pathSegment;
    }

    /**
     * Gets the semantic version number.
     *
     * @return The version number (e.g., "1.0")
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Gets the description of this version.
     *
     * @return Human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Parses a path segment into an ApiVersion.
     *
     * @param pathSegment The path segment to parse (e.g., "v1", "v2")
     * @return The corresponding ApiVersion, or null if not found
     */
    public static ApiVersion fromPathSegment(String pathSegment) {
        if (pathSegment == null) {
            return null;
        }

        for (ApiVersion version : values()) {
            if (version.pathSegment.equalsIgnoreCase(pathSegment)) {
                return version;
            }
        }

        return null;
    }

    /**
     * Gets the default API version (latest stable).
     *
     * @return The default version (currently V2)
     */
    public static ApiVersion getDefault() {
        return V2; // V2 is the latest stable version
    }

    /**
     * Checks if this version is deprecated.
     *
     * @return true if deprecated, false otherwise
     */
    public boolean isDeprecated() {
        // V1 is deprecated
        return this == V1;
    }

    /**
     * Checks if this version is experimental/beta.
     *
     * @return true if experimental, false otherwise
     */
    public boolean isExperimental() {
        // V3 is experimental
        return this == V3;
    }
}