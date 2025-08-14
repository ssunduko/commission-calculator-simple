package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.User;

import java.util.List;

/**
 * This interface defines user management operations.
 * It follows the Interface Segregation Principle by focusing only on user management,
 * allowing clients to depend only on the methods they need.
 */
public interface UserManagementService {
    
    /**
     * Creates a new user.
     * 
     * @param user The user to create
     * @return The created user with its ID assigned
     */
    User createUser(User user);
    
    /**
     * Updates an existing user.
     * 
     * @param user The user to update
     * @return The updated user
     */
    User updateUser(User user);
    
    /**
     * Gets a user by their ID.
     * 
     * @param userId The ID of the user
     * @return The user, or null if not found
     */
    User getUserById(String userId);
    
    /**
     * Gets all users in the system.
     * 
     * @return A list of all users
     */
    List<User> getAllUsers();
    
    /**
     * Deletes a user.
     * 
     * @param userId The ID of the user to delete
     */
    void deleteUser(String userId);
    
    /**
     * Authenticates a user with their username and password.
     * 
     * @param username The username
     * @param password The password
     * @return True if authentication is successful, false otherwise
     */
    boolean authenticateUser(String username, String password);
    
    /**
     * Changes a user's password.
     * 
     * @param userId The ID of the user
     * @param oldPassword The old password
     * @param newPassword The new password
     */
    void changePassword(String userId, String oldPassword, String newPassword);
}