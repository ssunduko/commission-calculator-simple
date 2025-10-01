package com.chapman.edu.commissions.patterns.builder;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder class for creating User objects using the Builder Pattern.
 * 
 * This builder demonstrates how the Builder Pattern can be used to construct
 * complex objects with many optional parameters in a readable and maintainable way.
 * The User class has many fields, making it a perfect candidate for the Builder Pattern.
 */
public class UserBuilder {
    
    // Private fields to hold the state during construction
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private Set<UserRole> roles;
    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDate createdDate;
    private String createdBy;
    private String managerId;
    private String department;
    private String territory;
    
    /**
     * Private constructor to prevent direct instantiation.
     */
    private UserBuilder() {
        // Initialize with default values
        this.roles = new HashSet<>();
        this.active = true;
        this.createdDate = LocalDate.now();
    }
    
    /**
     * Static factory method to create a new UserBuilder instance.
     * 
     * @return a new UserBuilder instance
     */
    public static UserBuilder create() {
        return new UserBuilder();
    }
    
    /**
     * Sets the ID for the user being built.
     * 
     * @param id the unique identifier for the user
     * @return this builder instance for method chaining
     */
    public UserBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * Sets the username for the user being built.
     * 
     * @param username the username for login
     * @return this builder instance for method chaining
     */
    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }
    
    /**
     * Sets the email for the user being built.
     * 
     * @param email the email address
     * @return this builder instance for method chaining
     */
    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    /**
     * Sets the first name for the user being built.
     * 
     * @param firstName the first name
     * @return this builder instance for method chaining
     */
    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    /**
     * Sets the last name for the user being built.
     * 
     * @param lastName the last name
     * @return this builder instance for method chaining
     */
    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    /**
     * Convenience method to set both first and last name at once.
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @return this builder instance for method chaining
     */
    public UserBuilder withName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }
    
    /**
     * Sets the password hash for the user being built.
     * 
     * @param passwordHash the hashed password
     * @return this builder instance for method chaining
     */
    public UserBuilder withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }
    
    /**
     * Adds a single role to the user being built.
     * 
     * @param role the role to add
     * @return this builder instance for method chaining
     */
    public UserBuilder withRole(UserRole role) {
        this.roles.add(role);
        return this;
    }
    
    /**
     * Sets the complete set of roles for the user being built.
     * 
     * @param roles the set of roles
     * @return this builder instance for method chaining
     */
    public UserBuilder withRoles(Set<UserRole> roles) {
        this.roles = new HashSet<>(roles);
        return this;
    }
    
    /**
     * Convenience method to add multiple roles at once.
     * 
     * @param roles the roles to add
     * @return this builder instance for method chaining
     */
    public UserBuilder withRoles(UserRole... roles) {
        for (UserRole role : roles) {
            this.roles.add(role);
        }
        return this;
    }
    
    /**
     * Sets the active status for the user being built.
     * 
     * @param active whether the user is active
     * @return this builder instance for method chaining
     */
    public UserBuilder withActive(boolean active) {
        this.active = active;
        return this;
    }
    
    /**
     * Convenience method to set the user as inactive.
     * 
     * @return this builder instance for method chaining
     */
    public UserBuilder inactive() {
        this.active = false;
        return this;
    }
    
    /**
     * Sets the last login time for the user being built.
     * 
     * @param lastLogin the last login timestamp
     * @return this builder instance for method chaining
     */
    public UserBuilder withLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }
    
    /**
     * Sets the creation date for the user being built.
     * 
     * @param createdDate the date when the user was created
     * @return this builder instance for method chaining
     */
    public UserBuilder withCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    
    /**
     * Sets the creator ID for the user being built.
     * 
     * @param createdBy the ID of who created this user
     * @return this builder instance for method chaining
     */
    public UserBuilder withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }
    
    /**
     * Sets the manager ID for the user being built.
     * 
     * @param managerId the ID of the user's manager
     * @return this builder instance for method chaining
     */
    public UserBuilder withManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }
    
    /**
     * Sets the department for the user being built.
     * 
     * @param department the department name
     * @return this builder instance for method chaining
     */
    public UserBuilder withDepartment(String department) {
        this.department = department;
        return this;
    }
    
    /**
     * Sets the territory for the user being built.
     * 
     * @param territory the territory name
     * @return this builder instance for method chaining
     */
    public UserBuilder withTerritory(String territory) {
        this.territory = territory;
        return this;
    }
    
    /**
     * Builds and returns the final User object.
     * 
     * @return a new User object with all the specified properties
     * @throws IllegalStateException if required fields are missing
     */
    public User build() {
        // Validate required fields
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalStateException("Email is required");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalStateException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalStateException("Last name is required");
        }
        
        // Create the User object
        User user = new User();
        
        // Set all the properties
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(passwordHash);
        user.setRoles(roles);
        user.setActive(active);
        user.setLastLogin(lastLogin);
        user.setCreatedDate(createdDate);
        user.setCreatedBy(createdBy);
        user.setManagerId(managerId);
        user.setDepartment(department);
        user.setTerritory(territory);
        
        return user;
    }
}