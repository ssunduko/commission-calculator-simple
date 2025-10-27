package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.UserRole;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Helper utility for authorization checks in servlets.
 *
 * This class provides convenient methods for checking user permissions
 * and sending authorization error responses.
 *
 * Design Pattern: Helper/Utility Pattern
 * Centralizes common authorization logic to avoid code duplication.
 */
public class AuthorizationHelper {

    /**
     * Private constructor to prevent instantiation.
     */
    private AuthorizationHelper() {
        throw new AssertionError("Cannot instantiate AuthorizationHelper");
    }

    /**
     * Checks if the current user is authenticated.
     * If not, sends a 401 Unauthorized response.
     *
     * @param response The HTTP response
     * @return true if authenticated, false otherwise
     */
    public static boolean requireAuthentication(HttpServletResponse response) throws IOException {
        if (!SecurityContext.isAuthenticated()) {
            sendUnauthorizedError(response, "Authentication required");
            return false;
        }
        return true;
    }

    /**
     * Checks if the current user has the specified role.
     * If not, sends a 403 Forbidden response.
     *
     * @param role The required role
     * @param response The HTTP response
     * @return true if user has the role, false otherwise
     */
    public static boolean requireRole(UserRole role, HttpServletResponse response)
            throws IOException {
        if (!requireAuthentication(response)) {
            return false;
        }

        UserPrincipal user = SecurityContext.getCurrentUser();
        if (!user.hasRole(role)) {
            sendForbiddenError(response, "Insufficient permissions");
            return false;
        }

        return true;
    }

    /**
     * Checks if the current user has any of the specified roles.
     * If not, sends a 403 Forbidden response.
     *
     * @param roles The required roles (any)
     * @param response The HTTP response
     * @return true if user has at least one role, false otherwise
     */
    public static boolean requireAnyRole(HttpServletResponse response, UserRole... roles)
            throws IOException {
        if (!requireAuthentication(response)) {
            return false;
        }

        UserPrincipal user = SecurityContext.getCurrentUser();
        if (!user.hasAnyRole(roles)) {
            sendForbiddenError(response, "Insufficient permissions");
            return false;
        }

        return true;
    }

    /**
     * Checks if the current user has all of the specified roles.
     * If not, sends a 403 Forbidden response.
     *
     * @param roles The required roles (all)
     * @param response The HTTP response
     * @return true if user has all roles, false otherwise
     */
    public static boolean requireAllRoles(HttpServletResponse response, UserRole... roles)
            throws IOException {
        if (!requireAuthentication(response)) {
            return false;
        }

        UserPrincipal user = SecurityContext.getCurrentUser();
        if (!user.hasAllRoles(roles)) {
            sendForbiddenError(response, "Insufficient permissions");
            return false;
        }

        return true;
    }

    /**
     * Checks if the current user is the owner of a resource.
     * If not, sends a 403 Forbidden response.
     *
     * @param resourceOwnerId The ID of the resource owner
     * @param response The HTTP response
     * @return true if user is the owner or has admin role, false otherwise
     */
    public static boolean requireOwnerOrAdmin(String resourceOwnerId, HttpServletResponse response)
            throws IOException {
        if (!requireAuthentication(response)) {
            return false;
        }

        UserPrincipal user = SecurityContext.getCurrentUser();

        // Allow if user is the owner
        if (user.getUserId().equals(resourceOwnerId)) {
            return true;
        }

        // Allow if user is an admin
        if (user.hasAnyRole(UserRole.SYSTEM_ADMIN, UserRole.FINANCE_ADMIN)) {
            return true;
        }

        sendForbiddenError(response, "Access denied: not resource owner");
        return false;
    }

    /**
     * Sends a 401 Unauthorized error response.
     *
     * @param response The HTTP response
     * @param message The error message
     */
    public static void sendUnauthorizedError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
            "{\"error\": \"%s\", \"status\": 401, \"timestamp\": %d}",
            message,
            System.currentTimeMillis()
        );

        response.getWriter().write(errorJson);
    }

    /**
     * Sends a 403 Forbidden error response.
     *
     * @param response The HTTP response
     * @param message The error message
     */
    public static void sendForbiddenError(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format(
            "{\"error\": \"%s\", \"status\": 403, \"timestamp\": %d}",
            message,
            System.currentTimeMillis()
        );

        response.getWriter().write(errorJson);
    }
}