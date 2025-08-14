package com.chapman.edu.commissions.principles.oop.encapsulation;

/**
 * This class demonstrates how to use the encapsulated EncapsulatedUser class.
 * It shows:
 * 1. Creating instances of EncapsulatedUser
 * 2. Accessing data through getter and setter methods
 * 3. How encapsulation prevents direct access to private data
 * 4. Validation in action
 */
public class UserDemo {
    
    public static void main(String[] args) {
        // Create a user with the constructor
        EncapsulatedUser user1 = new EncapsulatedUser(
            "jsmith", 
            "john.smith@example.com", 
            "John", 
            "Smith"
        );
        
        // Add a role to the user
        user1.addRole("SALES_REP");
        
        // Display user information using getters
        System.out.println("User Information:");
        System.out.println("Username: " + user1.getUsername());
        System.out.println("Email: " + user1.getEmail());
        System.out.println("Full Name: " + user1.getFullName());
        System.out.println("Is Admin: " + user1.isAdmin());
        System.out.println("Has SALES_REP role: " + user1.hasRole("SALES_REP"));
        
        // Update user information using setters
        System.out.println("\nUpdating user information...");
        user1.setFirstName("Jonathan");
        user1.addRole("ADMIN");
        
        // Display updated information
        System.out.println("\nUpdated User Information:");
        System.out.println("Full Name: " + user1.getFullName());
        System.out.println("Is Admin: " + user1.isAdmin());
        
        // Demonstrate validation
        System.out.println("\nDemonstrating validation:");
        
        try {
            System.out.println("Trying to set an invalid email...");
            user1.setEmail("invalid-email");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
        
        try {
            System.out.println("Trying to set a short username...");
            user1.setUsername("ab");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
        
        // Demonstrate encapsulation - cannot access private fields directly
        // The following line would cause a compilation error if uncommented:
        // user1.email = "hacked@example.com"; // Error: email has private access
        
        // Demonstrate updating last login
        System.out.println("\nUpdating last login time...");
        user1.updateLastLogin();
        System.out.println("Last Login: " + user1.getLastLogin());
        
        // Create another user with default constructor and setters
        System.out.println("\nCreating another user with default constructor...");
        EncapsulatedUser user2 = new EncapsulatedUser();
        user2.setUsername("mjohnson");
        user2.setEmail("mary.johnson@example.com");
        user2.setFirstName("Mary");
        user2.setLastName("Johnson");
        user2.addRole("FINANCE_ADMIN");
        
        // Display second user information
        System.out.println("\nSecond User Information:");
        System.out.println(user2.toString());
        
        System.out.println("\nEncapsulation ensures that:");
        System.out.println("1. Data can only be accessed through defined methods");
        System.out.println("2. Data validation occurs before values are assigned");
        System.out.println("3. Implementation details can change without affecting the public interface");
        System.out.println("4. The class controls how its data is accessed and modified");
    }
}