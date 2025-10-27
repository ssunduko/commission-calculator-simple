package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.User;

import java.util.Optional;

/**
 * Authenticator implementation for HTTP Basic Authentication.
 *
 * This authenticator validates username/password credentials against
 * the user repository. In a production system, passwords would be hashed.
 *
 * Design Pattern: Strategy Pattern
 * Concrete strategy for Basic authentication.
 *
 * Security Note: This is a simplified implementation for educational purposes.
 * Production systems should:
 * - Hash passwords (bcrypt, Argon2, etc.)
 * - Implement rate limiting
 * - Log authentication attempts
 * - Use HTTPS only
 */
public class BasicAuthenticator implements Authenticator {

    private final Repository<User> userRepository;

    /**
     * Constructs a Basic authenticator.
     *
     * @param userRepository The user repository for credential validation
     */
    public BasicAuthenticator(Repository<User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserPrincipal> authenticate(AuthenticationToken token) {
        if (!supports(token)) {
            return Optional.empty();
        }

        String username = token.getUsername();
        String password = token.getPassword();

        if (username == null || password == null) {
            return Optional.empty();
        }

        // Find user by username
        Optional<User> userOpt = userRepository.findAll().stream()
            .filter(u -> username.equals(u.getUsername()))
            .findFirst();

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Validate password (simplified - production should use hashing)
        // In this educational example, we'll accept any password for demo purposes
        // TODO: In production, use: passwordEncoder.matches(password, user.getPasswordHash())
        if (!validatePassword(password, user)) {
            return Optional.empty();
        }

        // Check if user is active
        if (!user.isActive()) {
            return Optional.empty();
        }

        // Create and return principal
        return Optional.of(new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
        ));
    }

    @Override
    public String getSupportedScheme() {
        return "Basic";
    }

    /**
     * Validates the password for the user.
     *
     * Educational Implementation: Accepts any non-empty password for demo purposes.
     *
     * Production Implementation would:
     * 1. Retrieve hashed password from user record
     * 2. Use password encoder (bcrypt, Argon2) to compare
     * 3. Implement timing-attack protection
     *
     * @param password The plain text password
     * @param user The user to validate against
     * @return true if password is valid, false otherwise
     */
    private boolean validatePassword(String password, User user) {
        // Educational implementation: accept any non-empty password
        // This allows demo/testing without setting up password hashing
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Production implementation would be:
        // return passwordEncoder.matches(password, user.getPasswordHash());

        // For educational purposes, we accept any password
        return true;
    }
}