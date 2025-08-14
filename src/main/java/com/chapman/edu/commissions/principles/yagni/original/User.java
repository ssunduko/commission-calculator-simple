package com.chapman.edu.commissions.principles.yagni.original;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user in the system with YAGNI violations.
 * This class contains many fields and methods that aren't needed for the basic functionality.
 */
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;  // YAGNI: Not needed for basic functionality
    private Set<String> roles;
    private boolean active;  // YAGNI: Not needed for basic functionality
    private LocalDateTime lastLogin;  // YAGNI: Not needed for basic functionality
    private LocalDate createdDate;  // YAGNI: Not needed for basic functionality
    private String createdBy;  // YAGNI: Not needed for basic functionality
    private String managerId;  // YAGNI: Not needed for basic functionality
    private String department;  // YAGNI: Not needed for basic functionality
    private String territory;  // YAGNI: Not needed for basic functionality
    
    /**
     * Default constructor
     */
    public User() {
        this.roles = new HashSet<>();
        this.active = true;  // YAGNI: Setting a field that isn't needed
        this.createdDate = LocalDate.now();  // YAGNI: Setting a field that isn't needed
    }
    
    /**
     * Constructor with essential fields
     */
    public User(String username, String email, String firstName, String lastName) {
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    // YAGNI: Methods for fields that aren't needed for basic functionality
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public void addRole(String role) {
        this.roles.add(role);
    }
    
    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getManagerId() {
        return managerId;
    }
    
    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getTerritory() {
        return territory;
    }
    
    public void setTerritory(String territory) {
        this.territory = territory;
    }
    
    /**
     * Get the full name of the user
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * YAGNI: Specific role-checking methods when the generic hasRole() is sufficient
     */
    public boolean isSalesRep() {
        return hasRole("SALES_REP");
    }
    
    public boolean isSalesManager() {
        return hasRole("SALES_MANAGER");
    }
    
    public boolean isFinanceAdmin() {
        return hasRole("FINANCE_ADMIN");
    }
    
    public boolean isSystemAdmin() {
        return hasRole("SYSTEM_ADMIN");
    }
    
    // YAGNI: Complex validation method that isn't needed for basic functionality
    public boolean validatePassword(String password) {
        // Simulate password validation
        return password != null && password.length() >= 8;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                '}';
    }
}