package com.chapman.edu.commissions.integration.service;

import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserService - Business logic layer for User operations.
 *
 * Demonstrates:
 * - User management business logic
 * - Authentication logic
 * - Role-based authorization checks
 * - Input validation for user data
 *
 * Layer: Service Layer (Business Logic)
 */
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final H2UserRepository userRepository;

    public UserService(H2UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        logger.debug("Retrieving all users");
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The user ID
     * @return Optional containing the user if found
     */
    public Optional<User> getUserById(String id) {
        logger.debug("Retrieving user by id: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Retrieves users by role.
     *
     * @param role The user role to filter by
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        logger.debug("Retrieving users by role: {}", role);
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new user with validation.
     *
     * Validates:
     * - Email is unique
     * - Email format is valid
     * - Password meets minimum requirements
     * - Required fields are present
     *
     * @param user The user to create
     * @return The created user
     * @throws IllegalArgumentException if validation fails
     */
    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getEmail());

        // Validate user data
        validateUser(user);

        // Check email uniqueness
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Set default role if none specified
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(java.util.Set.of(UserRole.SALES_REP));
        }

        // Set active by default
        if (!user.isActive()) {
            user.setActive(true);
        }

        // NOTE: In production, password should be hashed with bcrypt
        // For educational purposes, we're storing plaintext (DO NOT DO THIS IN PRODUCTION!)

        User savedUser = userRepository.save(user);
        logger.info("Created user with id: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Updates an existing user.
     *
     * @param id The user ID
     * @param updatedUser The updated user data
     * @return The updated user
     * @throws IllegalArgumentException if validation fails or user not found
     */
    public User updateUser(String id, User updatedUser) {
        logger.info("Updating user: {}", id);

        // Verify user exists
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + id);
        }

        // Validate updated user
        validateUser(updatedUser);

        // Ensure ID is preserved
        updatedUser.setId(id);

        User saved = userRepository.save(updatedUser);
        logger.info("Updated user: {}", id);

        return saved;
    }

    /**
     * Deletes a user.
     *
     * @param id The user ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(String id) {
        logger.info("Deleting user: {}", id);

        boolean deleted = userRepository.deleteById(id);
        if (deleted) {
            logger.info("Deleted user: {}", id);
        }

        return deleted;
    }

    /**
     * Authenticates a user with email and password.
     * Used by the authentication filter.
     *
     * @param email The user's email
     * @param password The user's password
     * @return Optional containing the user if authenticated
     */
    public Optional<User> authenticate(String email, String password) {
        logger.debug("Authenticating user: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            logger.debug("User not found: {}", email);
            return Optional.empty();
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.isActive()) {
            logger.debug("User is inactive: {}", email);
            return Optional.empty();
        }

        // NOTE: In production, use bcrypt to verify hashed password
        // For educational purposes, simple string comparison
        if (user.getPasswordHash().equals(password)) {
            logger.info("User authenticated successfully: {}", email);
            return Optional.of(user);
        }

        logger.debug("Invalid password for user: {}", email);
        return Optional.empty();
    }

    /**
     * Authenticates using HTTP Basic Auth header.
     * Parses "Basic base64(email:password)" format.
     *
     * @param authHeader The Authorization header value
     * @return Optional containing the user if authenticated
     */
    public Optional<User> authenticateBasic(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return Optional.empty();
        }

        try {
            // Extract and decode credentials
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));

            // Split into email and password
            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) {
                return Optional.empty();
            }

            String email = parts[0];
            String password = parts[1];

            return authenticate(email, password);

        } catch (Exception e) {
            logger.debug("Failed to parse Basic Auth header", e);
            return Optional.empty();
        }
    }

    /**
     * Checks if a user has a specific role.
     *
     * @param user The user to check
     * @param role The required role
     * @return true if user has the role
     */
    public boolean hasRole(User user, UserRole role) {
        return user.getRoles().contains(role);
    }

    /**
     * Validates a user according to business rules.
     *
     * @param user The user to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Basic email validation
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Password strength validation (basic)
        if (user.getPasswordHash().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }
    }
}