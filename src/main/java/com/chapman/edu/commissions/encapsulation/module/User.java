package com.chapman.edu.commissions.encapsulation.module;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User is a package-private class that represents a user in the system.
 * 
 * This class is not accessible outside the package, demonstrating module encapsulation.
 * It is an implementation detail that is hidden from clients outside the package.
 */
class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private Set<String> roles;
    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDateTime createdDate;
    
    /**
     * Default constructor
     */
    User() {
        this.id = UUID.randomUUID().toString();
        this.roles = new HashSet<>();
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     */
    User(String username, String email, String firstName, String lastName, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
    }
    
    // Getters and Setters
    
    String getId() {
        return id;
    }
    
    void setId(String id) {
        this.id = id;
    }
    
    String getUsername() {
        return username;
    }
    
    void setUsername(String username) {
        this.username = username;
    }
    
    String getEmail() {
        return email;
    }
    
    void setEmail(String email) {
        this.email = email;
    }
    
    String getFirstName() {
        return firstName;
    }
    
    void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    String getLastName() {
        return lastName;
    }
    
    void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    String getPasswordHash() {
        return passwordHash;
    }
    
    void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    Set<String> getRoles() {
        return new HashSet<>(roles);
    }
    
    void setRoles(Set<String> roles) {
        this.roles = new HashSet<>(roles);
    }
    
    void addRole(String role) {
        this.roles.add(role);
    }
    
    boolean hasRole(String role) {
        return this.roles.contains(role);
    }
    
    boolean isActive() {
        return active;
    }
    
    void setActive(boolean active) {
        this.active = active;
    }
    
    LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    /**
     * Get the full name of the user
     */
    String getFullName() {
        return firstName + " " + lastName;
    }
}