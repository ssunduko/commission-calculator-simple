package com.chapman.edu.commissions.principles.dry.fixed;

import java.util.regex.Pattern;

/**
 * A service class for customer operations that follows the DRY principle
 * by extracting common validation logic and string formatting into reusable methods.
 */
public class CustomerService {
    
    // Constants for validation error messages
    private static final String ERROR_NAME_EMPTY = "Customer name cannot be empty";
    private static final String ERROR_NAME_TOO_SHORT = "Customer name must be at least 2 characters long";
    private static final String ERROR_EMAIL_EMPTY = "Customer email cannot be empty";
    private static final String ERROR_EMAIL_INVALID = "Invalid email format";
    private static final String ERROR_PHONE_EMPTY = "Customer phone cannot be empty";
    private static final String ERROR_PHONE_INVALID = "Invalid phone number format";
    
    // Email validation regex
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Phone validation regex
    private static final String PHONE_REGEX = "^[0-9\\-\\(\\)\\s]+$";
    
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
        validateName(name);
        validateEmail(email);
        validatePhone(phone);
        
        // Registration logic (simplified)
        return formatSuccessMessage(name, email, phone, "registered");
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
        
        validateName(name);
        validateEmail(email);
        validatePhone(phone);
        
        // Update logic (simplified)
        return formatSuccessMessage(name, email, phone, "updated");
    }
    
    /**
     * Subscribes a customer to a newsletter
     * 
     * @param email the customer's email
     * @return a success message if subscription is successful
     * @throws IllegalArgumentException if email validation fails
     */
    public String subscribeToNewsletter(String email) {
        validateEmail(email);
        
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
        return formatCustomerInfo(name, email, phone, "\n");
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
        return formatCustomerInfo(name, email, phone, " | ");
    }
    
    /**
     * Validates a customer name
     * 
     * @param name the name to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_NAME_EMPTY);
        }
        if (name.length() < 2) {
            throw new IllegalArgumentException(ERROR_NAME_TOO_SHORT);
        }
    }
    
    /**
     * Validates an email address
     * 
     * @param email the email to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMAIL_EMPTY);
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(ERROR_EMAIL_INVALID);
        }
    }
    
    /**
     * Validates a phone number
     * 
     * @param phone the phone number to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_PHONE_EMPTY);
        }
        if (!phone.matches(PHONE_REGEX)) {
            throw new IllegalArgumentException(ERROR_PHONE_INVALID);
        }
    }
    
    /**
     * Formats customer information with the specified separator
     * 
     * @param name the customer's name
     * @param email the customer's email
     * @param phone the customer's phone number
     * @param separator the separator to use between fields
     * @return a formatted string with customer details
     */
    private String formatCustomerInfo(String name, String email, String phone, String separator) {
        return "Customer: " + name + separator + "Email: " + email + separator + "Phone: " + phone;
    }
    
    /**
     * Formats a success message for customer operations
     * 
     * @param name the customer's name
     * @param email the customer's email
     * @param phone the customer's phone number
     * @param action the action performed (e.g., "registered", "updated")
     * @return a formatted success message
     */
    private String formatSuccessMessage(String name, String email, String phone, String action) {
        return "Customer " + name + " " + action + " successfully with email " + email + " and phone " + phone;
    }
}