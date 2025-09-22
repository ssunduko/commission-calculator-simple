package com.chapman.edu.commissions.encapsulation.module;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserManager is a public facade class that provides user management functionality.
 * 
 * This class demonstrates module encapsulation by:
 * 1. Exposing only the necessary functionality to clients
 * 2. Hiding the internal implementation details (User, UserRepository, UserValidator)
 * 3. Providing a clean, public API for clients to use
 * 4. Controlling access to the module's functionality
 */
public class UserManager {
    // Internal components - hidden from clients
    private final UserRepository repository;
    private final UserValidator validator;
    
    /**
     * Constructor
     */
    public UserManager() {
        this.repository = new UserRepository();
        this.validator = new UserValidator(repository);
    }
    
    /**
     * Creates a new user.
     * 
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param password the password (will be hashed)
     * @return a UserDTO representing the created user
     * @throws ValidationException if validation fails
     */
    public UserDTO createUser(String username, String email, String firstName, 
                             String lastName, String password) throws ValidationException {
        // Create internal User object (hidden from clients)
        User user = new User(username, email, firstName, lastName, hashPassword(password));
        
        // Validate the user
        List<String> errors = validator.validateForCreation(user);
        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
        
        // Save the user
        User savedUser = repository.save(user);
        
        // Return a DTO (not the internal User object)
        return convertToDTO(savedUser);
    }
    
    /**
     * Gets a user by ID.
     * 
     * @param id the user ID
     * @return a UserDTO if found
     * @throws NotFoundException if the user is not found
     */
    public UserDTO getUserById(String id) throws NotFoundException {
        // Find the user
        User user = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
        
        // Return a DTO (not the internal User object)
        return convertToDTO(user);
    }
    
    /**
     * Updates a user.
     * 
     * @param id the user ID
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @return a UserDTO representing the updated user
     * @throws NotFoundException if the user is not found
     * @throws ValidationException if validation fails
     */
    public UserDTO updateUser(String id, String username, String email, 
                             String firstName, String lastName) 
                             throws NotFoundException, ValidationException {
        // Find the existing user
        User existingUser = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found: " + id));
        
        // Create a new user with updated fields
        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setUsername(username);
        updatedUser.setEmail(email);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setPasswordHash(existingUser.getPasswordHash());
        updatedUser.setRoles(existingUser.getRoles());
        updatedUser.setActive(existingUser.isActive());
        updatedUser.setCreatedDate(existingUser.getCreatedDate());
        updatedUser.setLastLogin(existingUser.getLastLogin());
        
        // Validate the user
        List<String> errors = validator.validateForUpdate(updatedUser, existingUser);
        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", errors);
        }
        
        // Save the user
        User savedUser = repository.save(updatedUser);
        
        // Return a DTO (not the internal User object)
        return convertToDTO(savedUser);
    }
    
    /**
     * Deletes a user.
     * 
     * @param id the user ID
     * @throws NotFoundException if the user is not found
     */
    public void deleteUser(String id) throws NotFoundException {
        // Check if the user exists
        if (!repository.findById(id).isPresent()) {
            throw new NotFoundException("User not found: " + id);
        }
        
        // Delete the user
        repository.deleteById(id);
    }
    
    /**
     * Gets all users.
     * 
     * @return a list of UserDTOs
     */
    public List<UserDTO> getAllUsers() {
        // Get all users and convert to DTOs
        return repository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Adds a role to a user.
     * 
     * @param userId the user ID
     * @param role the role to add
     * @return the updated UserDTO
     * @throws NotFoundException if the user is not found
     */
    public UserDTO addRoleToUser(String userId, String role) throws NotFoundException {
        // Find the user
        User user = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        
        // Add the role
        user.addRole(role);
        
        // Save the user
        User savedUser = repository.save(user);
        
        // Return a DTO
        return convertToDTO(savedUser);
    }
    
    /**
     * Records a user login.
     * 
     * @param userId the user ID
     * @throws NotFoundException if the user is not found
     */
    public void recordLogin(String userId) throws NotFoundException {
        // Find the user
        User user = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        
        // Update the last login time
        user.setLastLogin(LocalDateTime.now());
        
        // Save the user
        repository.save(user);
    }
    
    /**
     * Converts an internal User object to a public UserDTO.
     * This hides the internal implementation details from clients.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getFullName(),
            user.getRoles(),
            user.isActive(),
            user.getLastLogin(),
            user.getCreatedDate()
        );
    }
    
    /**
     * Hashes a password.
     * In a real system, this would use a secure hashing algorithm.
     */
    private String hashPassword(String password) {
        // This is a simplified example - in a real system, use a secure hashing algorithm
        return "hashed:" + password;
    }
    
    /**
     * UserDTO is a public Data Transfer Object that represents a user.
     * It exposes only the necessary information to clients and hides internal details.
     */
    public static class UserDTO {
        private final String id;
        private final String username;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final String fullName;
        private final Set<String> roles;
        private final boolean active;
        private final LocalDateTime lastLogin;
        private final LocalDateTime createdDate;
        
        public UserDTO(String id, String username, String email, String firstName, 
                      String lastName, String fullName, Set<String> roles, 
                      boolean active, LocalDateTime lastLogin, LocalDateTime createdDate) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = fullName;
            this.roles = roles;
            this.active = active;
            this.lastLogin = lastLogin;
            this.createdDate = createdDate;
        }
        
        public String getId() {
            return id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public String getFirstName() {
            return firstName;
        }
        
        public String getLastName() {
            return lastName;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public Set<String> getRoles() {
            return roles;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public LocalDateTime getLastLogin() {
            return lastLogin;
        }
        
        public LocalDateTime getCreatedDate() {
            return createdDate;
        }
        
        @Override
        public String toString() {
            return "User: " + fullName + " (" + username + ")" +
                   ", Email: " + email +
                   ", Roles: " + roles +
                   ", Active: " + active;
        }
    }
    
    /**
     * ValidationException is thrown when user validation fails.
     */
    public static class ValidationException extends Exception {
        private final List<String> errors;
        
        public ValidationException(String message, List<String> errors) {
            super(message);
            this.errors = errors;
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
    
    /**
     * NotFoundException is thrown when a user is not found.
     */
    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }
}