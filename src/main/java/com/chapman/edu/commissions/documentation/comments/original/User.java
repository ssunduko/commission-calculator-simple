package com.chapman.edu.commissions.documentation.comments.original;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * User class represents a user
 */
public class User {
    // VIOLATION: Unnecessary comment for obvious field
    // User ID field
    private String id;
    // VIOLATION: Unnecessary comment for obvious field
    // User name field
    private String username;
    // VIOLATION: Unnecessary comment for obvious field
    // User email field
    private String email;
    // VIOLATION: Unnecessary comment for obvious field
    // First name field
    private String firstName;
    // VIOLATION: Unnecessary comment for obvious field
    // Last name field
    private String lastName;
    // VIOLATION: Unnecessary comment for obvious field
    // Password hash field
    private String passwordHash;
    // VIOLATION: Unnecessary comment for obvious field
    // User roles field
    private Set<UserRole> roles;
    // VIOLATION: Unnecessary comment for obvious field
    // Active status field
    private boolean active;
    // VIOLATION: Unnecessary comment for obvious field
    // Last login field
    private LocalDateTime lastLogin;
    // VIOLATION: Unnecessary comment for obvious field
    // Created date field
    private LocalDate createdDate;
    // VIOLATION: Unnecessary comment for obvious field
    // Created by field
    private String createdBy;
    // VIOLATION: Unnecessary comment for obvious field
    // Manager ID field
    private String managerId;
    // VIOLATION: Unnecessary comment for obvious field
    // Department field
    private String department;
    // VIOLATION: Unnecessary comment for obvious field
    // Territory field
    private String territory;

    /**
     * VIOLATION: Method comment that just restates the method name
     * Default constructor that creates a new user
     */
    public User() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Initialize roles set
        this.roles = new HashSet<>();
        // VIOLATION: Unnecessary comment for obvious operation
        // Set active to true
        this.active = true;
        // VIOLATION: Unnecessary comment for obvious operation
        // Set created date to now
        this.createdDate = LocalDate.now();
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Constructor with essential fields that creates a user with parameters
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     */
    public User(String username, String email, String firstName, String lastName) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Call default constructor
        this();
        // VIOLATION: Unnecessary comment for obvious operation
        // Set username field to username parameter
        this.username = username;
        // VIOLATION: Unnecessary comment for obvious operation
        // Set email field to email parameter
        this.email = email;
        // VIOLATION: Unnecessary comment for obvious operation
        // Set firstName field to firstName parameter
        this.firstName = firstName;
        // VIOLATION: Unnecessary comment for obvious operation
        // Set lastName field to lastName parameter
        this.lastName = lastName;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the user ID
     * @return the user ID
     */
    public String getId() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the id field
        return id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the user ID
     * @param id the user ID to set
     */
    public void setId(String id) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the id field to the id parameter
        this.id = id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the username
     * @return the username
     */
    public String getUsername() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the username field
        return username;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the username
     * @param username the username to set
     */
    public void setUsername(String username) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the username field to the username parameter
        this.username = username;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the email
     * @return the email
     */
    public String getEmail() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the email field
        return email;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the email
     * @param email the email to set
     */
    public void setEmail(String email) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the email field to the email parameter
        this.email = email;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Adds a role to the user
     * @param role the role to add
     */
    public void addRole(UserRole role) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Add the role to the roles set
        this.roles.add(role);
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Checks if user has a role
     * @param role the role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(UserRole role) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Check if roles contains the role
        return this.roles.contains(role);
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the full name of the user
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Concatenate first name and last name with space
        return firstName + " " + lastName;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Checks if the user is a sales representative
     * @return true if the user has the SALES_REP role
     */
    public boolean isSalesRep() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Check if user has SALES_REP role
        return hasRole(UserRole.SALES_REP);
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Checks if the user is a sales manager
     * @return true if the user has the SALES_MANAGER role
     */
    public boolean isSalesManager() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Check if user has SALES_MANAGER role
        return hasRole(UserRole.SALES_MANAGER);
    }

    // VIOLATION: Comment that doesn't add any value
    // Additional getters and setters with obvious comments...

    @Override
    public boolean equals(Object o) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Check if same object
        if (this == o) return true;
        // VIOLATION: Unnecessary comment for obvious operation
        // Check if null or different class
        if (o == null || getClass() != o.getClass()) return false;
        // VIOLATION: Unnecessary comment for obvious operation
        // Cast to User
        User user = (User) o;
        // VIOLATION: Unnecessary comment for obvious operation
        // Compare IDs
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return hash of ID
        return Objects.hash(id);
    }
}
