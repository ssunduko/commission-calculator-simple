package com.chapman.edu.commissions.encapsulation.module;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * UserValidator is a package-private class that validates user data.
 * 
 * This class is not accessible outside the package, demonstrating module encapsulation.
 * It is an implementation detail that is hidden from clients outside the package.
 */
class UserValidator {
    // Regular expression for validating email addresses
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Regular expression for validating usernames (alphanumeric, 3-20 characters)
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9]{3,20}$");
    
    // Reference to the repository for checking uniqueness
    private final UserRepository repository;
    
    /**
     * Constructor
     */
    UserValidator(UserRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Validates a user for creation.
     * 
     * @param user the user to validate
     * @return a list of validation errors, empty if valid
     */
    List<String> validateForCreation(User user) {
        List<String> errors = new ArrayList<>();
        
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Username is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }
        
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            errors.add("Password is required");
        }
        
        // If any required fields are missing, return early
        if (!errors.isEmpty()) {
            return errors;
        }
        
        // Validate username format
        if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            errors.add("Username must be alphanumeric and between 3-20 characters");
        }
        
        // Validate email format
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("Email format is invalid");
        }
        
        // Check uniqueness
        if (repository.isUsernameTaken(user.getUsername())) {
            errors.add("Username is already taken");
        }
        
        if (repository.isEmailTaken(user.getEmail())) {
            errors.add("Email is already taken");
        }
        
        return errors;
    }
    
    /**
     * Validates a user for update.
     * 
     * @param user the user to validate
     * @param existingUser the existing user to compare against
     * @return a list of validation errors, empty if valid
     */
    List<String> validateForUpdate(User user, User existingUser) {
        List<String> errors = new ArrayList<>();
        
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Username is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        }
        
        // If any required fields are missing, return early
        if (!errors.isEmpty()) {
            return errors;
        }
        
        // Validate username format
        if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            errors.add("Username must be alphanumeric and between 3-20 characters");
        }
        
        // Validate email format
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("Email format is invalid");
        }
        
        // Check uniqueness (only if changed)
        if (!user.getUsername().equals(existingUser.getUsername()) && 
            repository.isUsernameTaken(user.getUsername())) {
            errors.add("Username is already taken");
        }
        
        if (!user.getEmail().equals(existingUser.getEmail()) && 
            repository.isEmailTaken(user.getEmail())) {
            errors.add("Email is already taken");
        }
        
        return errors;
    }
}