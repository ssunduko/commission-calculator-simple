package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.UserRole;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an authenticated user (principal) in the security context.
 *
 * This class contains the identity and permissions of the currently
 * authenticated user. It's stored in the SecurityContext after successful
 * authentication.
 *
 * Design Pattern: Principal Pattern
 * Represents the user's identity and roles in the security system.
 */
public class UserPrincipal {

    private final String userId;
    private final String username;
    private final String email;
    private final Set<UserRole> roles;
    private final boolean authenticated;

    /**
     * Constructs an authenticated user principal.
     *
     * @param userId The user's unique identifier
     * @param username The username
     * @param email The user's email address
     * @param roles The set of roles assigned to this user
     */
    public UserPrincipal(String userId, String username, String email, Set<UserRole> roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles != null ? Set.copyOf(roles) : Collections.emptySet();
        this.authenticated = true;
    }

    /**
     * Creates an anonymous (unauthenticated) principal.
     *
     * @return An anonymous principal
     */
    public static UserPrincipal anonymous() {
        return new UserPrincipal(null, "anonymous", null, Collections.emptySet());
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return The user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the email address.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's roles.
     *
     * @return Unmodifiable set of roles
     */
    public Set<UserRole> getRoles() {
        return roles;
    }

    /**
     * Checks if the user is authenticated.
     *
     * @return true if authenticated, false if anonymous
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param role The role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    /**
     * Checks if the user has any of the specified roles.
     *
     * @param requiredRoles The roles to check
     * @return true if user has at least one of the roles, false otherwise
     */
    public boolean hasAnyRole(UserRole... requiredRoles) {
        for (UserRole role : requiredRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the user has all of the specified roles.
     *
     * @param requiredRoles The roles to check
     * @return true if user has all of the roles, false otherwise
     */
    public boolean hasAllRoles(UserRole... requiredRoles) {
        for (UserRole role : requiredRoles) {
            if (!roles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
               "userId='" + userId + '\'' +
               ", username='" + username + '\'' +
               ", roles=" + roles +
               ", authenticated=" + authenticated +
               '}';
    }
}