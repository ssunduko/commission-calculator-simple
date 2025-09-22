package com.chapman.edu.commissions.encapsulation.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserRepository is a package-private class that handles user data storage.
 * 
 * This class is not accessible outside the package, demonstrating module encapsulation.
 * It is an implementation detail that is hidden from clients outside the package.
 */
class UserRepository {
    // In-memory storage for users
    private final Map<String, User> users = new HashMap<>();

    /**
     * Saves a user to the repository.
     * 
     * @param user the user to save
     * @return the saved user
     */
    User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Finds a user by ID.
     * 
     * @param id the user ID
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username) {
        return users.values().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }

    /**
     * Finds a user by email.
     * 
     * @param email the email
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email) {
        return users.values().stream()
            .filter(user -> user.getEmail().equals(email))
            .findFirst();
    }

    /**
     * Gets all users.
     * 
     * @return a list of all users
     */
    List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Deletes a user by ID.
     * 
     * @param id the user ID
     * @return true if the user was deleted, false if not found
     */
    boolean deleteById(String id) {
        return users.remove(id) != null;
    }

    /**
     * Checks if a username is already taken.
     * 
     * @param username the username to check
     * @return true if the username is taken, false otherwise
     */
    boolean isUsernameTaken(String username) {
        return users.values().stream()
            .anyMatch(user -> user.getUsername().equals(username));
    }

    /**
     * Checks if an email is already taken.
     * 
     * @param email the email to check
     * @return true if the email is taken, false otherwise
     */
    boolean isEmailTaken(String email) {
        return users.values().stream()
            .anyMatch(user -> user.getEmail().equals(email));
    }
}
