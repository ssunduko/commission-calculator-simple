package com.chapman.edu.commissions.api.rest.security;

import java.util.Optional;

/**
 * Interface for authentication strategies.
 *
 * This interface defines the contract for authenticating users with different
 * authentication mechanisms (Basic, JWT, API Key, etc.).
 *
 * Design Pattern: Strategy Pattern
 * Different implementations provide different authentication algorithms.
 *
 * Design Principle: Dependency Inversion Principle (DIP)
 * High-level security components depend on this abstraction, not concrete implementations.
 */
public interface Authenticator {

    /**
     * Authenticates a user based on the provided token.
     *
     * @param token The authentication token containing credentials
     * @return Optional containing the authenticated user principal, or empty if authentication fails
     */
    Optional<UserPrincipal> authenticate(AuthenticationToken token);

    /**
     * Gets the authentication scheme this authenticator supports.
     *
     * @return The supported scheme (e.g., "Basic", "Bearer")
     */
    String getSupportedScheme();

    /**
     * Checks if this authenticator supports the given authentication token.
     *
     * @param token The token to check
     * @return true if this authenticator can handle the token, false otherwise
     */
    default boolean supports(AuthenticationToken token) {
        return token != null && getSupportedScheme().equalsIgnoreCase(token.getScheme());
    }
}