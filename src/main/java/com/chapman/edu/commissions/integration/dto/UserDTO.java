package com.chapman.edu.commissions.integration.dto;

import com.chapman.edu.commissions.model.UserRole;
import java.util.Set;

/**
 * UserDTO - Data Transfer Object for User information.
 *
 * **Security Consideration in DTOs:**
 *
 * This DTO demonstrates an important security principle: NEVER expose sensitive
 * data in API responses.
 *
 * Notice that this DTO does NOT include:
 * - password / passwordHash field
 *
 * Even though the User domain entity has a passwordHash field, we deliberately
 * exclude it from the DTO. Passwords should NEVER be returned in API responses,
 * even if hashed.
 *
 * **DTO Pattern for Security:**
 * DTOs act as a security boundary, allowing us to control exactly what data
 * leaves our system. Without DTOs, if we accidentally serialize a User entity
 * directly, we might leak the password hash.
 *
 * **Real-world example:**
 * GitHub API vulnerability in 2012: accidentally exposed password hashes in
 * some API responses. DTOs prevent this by making it impossible to accidentally
 * include fields that shouldn't be exposed.
 *
 * @see com.chapman.edu.commissions.model.User The domain entity (contains password)
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class UserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<UserRole> roles;
    private boolean active;
    private String createdDate;
    private String lastModifiedDate;

    // Default constructor
    public UserDTO() {
    }

    public UserDTO(String id, String firstName, String lastName, String email,
                   Set<UserRole> roles, boolean active, String createdDate,
                   String lastModifiedDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
        this.active = active;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}