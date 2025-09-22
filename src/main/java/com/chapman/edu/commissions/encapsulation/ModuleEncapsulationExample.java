package com.chapman.edu.commissions.encapsulation;

import com.chapman.edu.commissions.encapsulation.module.UserManager;
import com.chapman.edu.commissions.encapsulation.module.UserManager.UserDTO;
import com.chapman.edu.commissions.encapsulation.module.UserManager.ValidationException;
import com.chapman.edu.commissions.encapsulation.module.UserManager.NotFoundException;

import java.util.List;

/**
 * Module Encapsulation Example
 * 
 * This class demonstrates module encapsulation by using package-level access control.
 * Module encapsulation allows:
 * 1. Hiding internal implementation details within a package
 * 2. Exposing only public classes and interfaces to clients
 * 3. Controlling access to module functionality through well-defined public APIs
 * 4. Creating clear boundaries between different parts of the system
 * 
 * In this example:
 * - UserManager is public and accessible to clients
 * - User, UserRepository, and UserValidator are package-private and hidden from clients
 * - Clients can only interact with the module through the UserManager facade
 */
public class ModuleEncapsulationExample {

    /**
     * Main method to demonstrate module encapsulation
     */
    public static void main(String[] args) {
        // Create a user manager
        UserManager userManager = new UserManager();
        
        try {
            // Create users
            System.out.println("Creating users...");
            UserDTO user1 = userManager.createUser("jsmith", "john.smith@example.com", 
                                                  "John", "Smith", "password123");
            UserDTO user2 = userManager.createUser("mjohnson", "mary.johnson@example.com", 
                                                  "Mary", "Johnson", "password456");
            
            // Display created users
            System.out.println("Created users:");
            System.out.println("- " + user1);
            System.out.println("- " + user2);
            
            // Add roles to users
            System.out.println("\nAdding roles...");
            userManager.addRoleToUser(user1.getId(), "ADMIN");
            userManager.addRoleToUser(user1.getId(), "USER");
            userManager.addRoleToUser(user2.getId(), "USER");
            
            // Get updated users
            user1 = userManager.getUserById(user1.getId());
            user2 = userManager.getUserById(user2.getId());
            
            // Display updated users
            System.out.println("Users with roles:");
            System.out.println("- " + user1);
            System.out.println("- " + user2);
            
            // Update a user
            System.out.println("\nUpdating user...");
            UserDTO updatedUser = userManager.updateUser(user1.getId(), "johnsmith", 
                                                       user1.getEmail(), 
                                                       user1.getFirstName(), 
                                                       user1.getLastName());
            System.out.println("Updated user: " + updatedUser);
            
            // Record login
            System.out.println("\nRecording login...");
            userManager.recordLogin(user1.getId());
            user1 = userManager.getUserById(user1.getId());
            System.out.println("User after login: " + user1);
            
            // Get all users
            System.out.println("\nAll users:");
            List<UserDTO> allUsers = userManager.getAllUsers();
            for (UserDTO user : allUsers) {
                System.out.println("- " + user);
            }
            
            // Try to create a user with invalid data
            System.out.println("\nTrying to create a user with invalid data...");
            try {
                userManager.createUser("a", "invalid-email", "", "", "");
            } catch (ValidationException e) {
                System.out.println("Validation failed as expected:");
                for (String error : e.getErrors()) {
                    System.out.println("- " + error);
                }
            }
            
            // Delete a user
            System.out.println("\nDeleting user...");
            userManager.deleteUser(user2.getId());
            System.out.println("User deleted successfully");
            
            // Try to get the deleted user
            System.out.println("\nTrying to get the deleted user...");
            try {
                userManager.getUserById(user2.getId());
            } catch (NotFoundException e) {
                System.out.println("User not found as expected: " + e.getMessage());
            }
            
            // Get all users after deletion
            System.out.println("\nAll users after deletion:");
            allUsers = userManager.getAllUsers();
            for (UserDTO user : allUsers) {
                System.out.println("- " + user);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Note that the client code never interacts directly with:
        // - User (package-private class)
        // - UserRepository (package-private class)
        // - UserValidator (package-private class)
        // These internal classes are encapsulated within the module and hidden from clients.
        // Clients can only interact with the module through the public UserManager facade.
        
        // The following code would not compile:
        // User user = new User(); // Error: User is not accessible
        // UserRepository repository = new UserRepository(); // Error: UserRepository is not accessible
        // UserValidator validator = new UserValidator(repository); // Error: UserValidator is not accessible
    }
}