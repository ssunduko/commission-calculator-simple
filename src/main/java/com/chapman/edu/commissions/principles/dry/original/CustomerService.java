package com.chapman.edu.commissions.principles.dry.original;

import java.util.regex.Pattern;

/**
 * A service class for customer operations that violates the DRY principle
 * by duplicating validation logic and string formatting across methods.
 */
public class CustomerService {
    
    /**
     * Registers a new customer with the given details
     * 
     * @param name the customer's name
     * @param email the customer's email
     * @param phone the customer's phone number
     * @return a success message if registration is successful
     * @throws IllegalArgumentException if any validation fails
     */
    public String registerCustomer(String name, String email, String phone) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Customer name must be at least 2 characters long");
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be empty");
        }
        // Simple email validation using regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Validate phone
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer phone cannot be empty");
        }
        // Simple phone validation - must be digits, possibly with hyphens, parentheses, or spaces
        if (!phone.matches("^[0-9\\-\\(\\)\\s]+$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        
        // Registration logic (simplified)
        return "Customer " + name + " registered successfully with email " + email + " and phone " + phone;
    }
    
    /**
     * Updates an existing customer's details
     * 
     * @param customerId the customer ID
     * @param name the customer's new name
     * @param email the customer's new email
     * @param phone the customer's new phone number
     * @return a success message if update is successful
     * @throws IllegalArgumentException if any validation fails
     */
    public String updateCustomer(String customerId, String name, String email, String phone) {
        // Validate customer ID
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty");
        }
        
        // Validate name - DUPLICATED from registerCustomer
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException("Customer name must be at least 2 characters long");
        }
        
        // Validate email - DUPLICATED from registerCustomer
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be empty");
        }
        // Simple email validation using regex - DUPLICATED from registerCustomer
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Validate phone - DUPLICATED from registerCustomer
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer phone cannot be empty");
        }
        // Simple phone validation - DUPLICATED from registerCustomer
        if (!phone.matches("^[0-9\\-\\(\\)\\s]+$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        
        // Update logic (simplified)
        return "Customer " + name + " updated successfully with email " + email + " and phone " + phone;
    }
    
    /**
     * Subscribes a customer to a newsletter
     * 
     * @param email the customer's email
     * @return a success message if subscription is successful
     * @throws IllegalArgumentException if email validation fails
     */
    public String subscribeToNewsletter(String email) {
        // Validate email - DUPLICATED from registerCustomer and updateCustomer
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email cannot be empty");
        }
        // Simple email validation using regex - DUPLICATED from registerCustomer and updateCustomer
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Subscription logic (simplified)
        return "Email " + email + " subscribed to newsletter successfully";
    }
    
    /**
     * Formats a customer record for display
     * 
     * @param name the customer's name
     * @param email the customer's email
     * @param phone the customer's phone number
     * @return a formatted string with customer details
     */
    public String formatCustomerRecord(String name, String email, String phone) {
        // String concatenation - DUPLICATED in multiple return statements
        return "Customer: " + name + "\nEmail: " + email + "\nPhone: " + phone;
    }
    
    /**
     * Formats a customer record for a report
     * 
     * @param name the customer's name
     * @param email the customer's email
     * @param phone the customer's phone number
     * @return a formatted string with customer details for a report
     */
    public String formatCustomerForReport(String name, String email, String phone) {
        // Similar string concatenation - DUPLICATED from formatCustomerRecord
        return "Customer: " + name + " | Email: " + email + " | Phone: " + phone;
    }
}