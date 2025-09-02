package com.chapman.edu.commissions.documentation.comments.fixed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * FIX: Clear class description that explains business purpose and role hierarchy
 * Represents a system user with authentication credentials and role-based permissions.
 * Users can have multiple roles (sales rep, manager, admin) that determine their
 * access to commission data and system functions.
 */
public class User {
    // FIX: No unnecessary comments for obvious fields
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

    // FIX: No redundant JavaDoc for constructor with clear initialization
    public User() {
        // FIX: No unnecessary comments for obvious operations
        this.roles = new HashSet<>();
        this.active = true;
        this.createdDate = LocalDate.now();
    }

    // FIX: No redundant JavaDoc for constructor with clear parameters
    public User(String username, String email, String firstName, String lastName) {
        // FIX: No unnecessary comments for obvious operations
        this();
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // FIX: Helpful comment that explains a pattern rather than individual methods
    // Standard getters and setters - no comments needed for obvious operations

    // FIX: No redundant JavaDoc for simple getter
    public String getId() {
        // FIX: No unnecessary comment for obvious operation
        return id;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setId(String id) {
        // FIX: No unnecessary comment for obvious operation
        this.id = id;
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getUsername() {
        // FIX: No unnecessary comment for obvious operation
        return username;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setUsername(String username) {
        // FIX: No unnecessary comment for obvious operation
        this.username = username;
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getEmail() {
        // FIX: No unnecessary comment for obvious operation
        return email;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setEmail(String email) {
        // FIX: No unnecessary comment for obvious operation
        this.email = email;
    }

    // FIX: No redundant JavaDoc for method with clear name
    public void addRole(UserRole role) {
        // FIX: No unnecessary comment for obvious operation
        this.roles.add(role);
    }

    // FIX: No redundant JavaDoc for method with clear name
    public boolean hasRole(UserRole role) {
        // FIX: No unnecessary comment for obvious operation
        return this.roles.contains(role);
    }

    // FIX: No redundant JavaDoc for method with clear name
    public String getFullName() {
        // FIX: No unnecessary comment for obvious operation
        return firstName + " " + lastName;
    }

    // FIX: No redundant JavaDoc for method with clear name
    public boolean isSalesRep() {
        // FIX: No unnecessary comment for obvious operation
        return hasRole(UserRole.SALES_REP);
    }

    // FIX: No redundant JavaDoc for method with clear name
    public boolean isSalesManager() {
        // FIX: No unnecessary comment for obvious operation
        return hasRole(UserRole.SALES_MANAGER);
    }

    // FIX: Helpful comment that explains omission rather than documenting every method
    // Additional getters and setters omitted for brevity

    // FIX: No redundant JavaDoc for standard override method
    @Override
    public boolean equals(Object o) {
        // FIX: No unnecessary comments for obvious operations
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    // FIX: No redundant JavaDoc for standard override method
    @Override
    public int hashCode() {
        // FIX: No unnecessary comment for obvious operation
        return Objects.hash(id);
    }
}
