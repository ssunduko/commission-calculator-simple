package com.chapman.edu.commissions.api.rest.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central authentication manager that coordinates multiple authenticators.
 *
 * This class manages a chain of authenticators and delegates authentication
 * to the appropriate one based on the authentication scheme.
 *
 * Design Pattern: Chain of Responsibility Pattern
 * Passes authentication request through a chain of authenticators until
 * one handles it successfully.
 *
 * Design Principle: Single Responsibility Principle (SRP)
 * Single responsibility: coordinate authentication across different schemes.
 */
public class AuthenticationManager {

    private final List<Authenticator> authenticators;

    /**
     * Constructs an authentication manager with no authenticators.
     * Use addAuthenticator() to register authentication strategies.
     */
    public AuthenticationManager() {
        this.authenticators = new ArrayList<>();
    }

    /**
     * Adds an authenticator to the chain.
     *
     * @param authenticator The authenticator to add
     * @return This manager (for method chaining)
     */
    public AuthenticationManager addAuthenticator(Authenticator authenticator) {
        authenticators.add(authenticator);
        return this;
    }

    /**
     * Authenticates a user using the appropriate authenticator.
     *
     * This method iterates through registered authenticators and delegates
     * to the first one that supports the given token's scheme.
     *
     * @param token The authentication token
     * @return Optional containing the authenticated principal, or empty if authentication fails
     */
    public Optional<UserPrincipal> authenticate(AuthenticationToken token) {
        if (token == null) {
            return Optional.empty();
        }

        // Find the first authenticator that supports this token
        for (Authenticator authenticator : authenticators) {
            if (authenticator.supports(token)) {
                Optional<UserPrincipal> result = authenticator.authenticate(token);
                if (result.isPresent()) {
                    return result;
                }
            }
        }

        // No authenticator could handle this token, or authentication failed
        return Optional.empty();
    }

    /**
     * Checks if any authenticator supports the given scheme.
     *
     * @param scheme The authentication scheme (e.g., "Basic", "Bearer")
     * @return true if supported, false otherwise
     */
    public boolean supportsScheme(String scheme) {
        return authenticators.stream()
            .anyMatch(auth -> auth.getSupportedScheme().equalsIgnoreCase(scheme));
    }

    /**
     * Gets all supported authentication schemes.
     *
     * @return Array of supported schemes
     */
    public String[] getSupportedSchemes() {
        return authenticators.stream()
            .map(Authenticator::getSupportedScheme)
            .toArray(String[]::new);
    }

    /**
     * Gets the number of registered authenticators.
     *
     * @return The count of authenticators
     */
    public int getAuthenticatorCount() {
        return authenticators.size();
    }
}