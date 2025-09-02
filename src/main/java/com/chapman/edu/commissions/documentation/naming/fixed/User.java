package com.chapman.edu.commissions.documentation.naming.fixed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a system user with authentication credentials and role-based permissions.
 */
public class User {
    private String id;
    private String username;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private Set<UserRole> assignedRoles;
    private boolean isActive;
    private LocalDateTime lastLoginDate;
    private LocalDate createdDate;
    private String createdByUserId;
    private String managerId;
    private String departmentName;
    private String assignedTerritory;
    private LocalDate startDate;
    
    public User() {
        this.assignedRoles = new HashSet<>();
        this.isActive = true;
        this.createdDate = LocalDate.now();
    }
    
    public User(String username, String emailAddress, String firstName, String lastName) {
        this();
        this.username = username;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and setters with meaningful names
    
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
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
    
    public Set<UserRole> getAssignedRoles() {
        return assignedRoles;
    }
    
    public void setAssignedRoles(Set<UserRole> assignedRoles) {
        this.assignedRoles = assignedRoles;
    }
    
    public void addRole(UserRole role) {
        this.assignedRoles.add(role);
    }
    
    public boolean hasRole(UserRole role) {
        return this.assignedRoles.contains(role);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public String getAssignedTerritory() {
        return assignedTerritory;
    }
    
    public void setAssignedTerritory(String assignedTerritory) {
        this.assignedTerritory = assignedTerritory;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isSalesRepresentative() {
        return hasRole(UserRole.SALES_REP);
    }
    
    public boolean isSalesManager() {
        return hasRole(UserRole.SALES_MANAGER);
    }
    
    public boolean isFinanceAdministrator() {
        return hasRole(UserRole.FINANCE_ADMIN);
    }
    
    public boolean isSystemAdministrator() {
        return hasRole(UserRole.SYSTEM_ADMIN);
    }
    
    public boolean isEligibleForSeniorBonus() {
        if (startDate == null) {
            return false;
        }
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        return startDate.isBefore(twoYearsAgo);
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
}