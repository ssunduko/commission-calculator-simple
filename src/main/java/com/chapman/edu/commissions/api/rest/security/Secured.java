package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark endpoints that require authentication and/or specific roles.
 *
 * This annotation can be used on servlet methods to declaratively specify
 * security requirements. In a full implementation, this would be processed
 * by an aspect or filter to enforce security constraints.
 *
 * Design Pattern: Declarative Security
 * Security requirements are declared via annotations rather than imperative code.
 *
 * Example usage:
 * <pre>
 * @Secured(roles = {UserRole.SALES_MANAGER, UserRole.SYSTEM_ADMIN})
 * public void doPost(HttpServletRequest req, HttpServletResponse resp) {
 *     // Only sales managers and admins can access
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {

    /**
     * Roles required to access this endpoint.
     * If empty, only authentication is required (no specific role).
     *
     * @return Array of required roles
     */
    UserRole[] roles() default {};

    /**
     * Whether to require ALL specified roles (true) or ANY role (false).
     * Default is false (requires any of the specified roles).
     *
     * @return true for AND logic, false for OR logic
     */
    boolean requireAll() default false;

    /**
     * Custom error message to return when access is denied.
     *
     * @return The error message
     */
    String deniedMessage() default "Access denied";
}