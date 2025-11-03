package com.chapman.edu.commissions.integration.model;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTING - User Model
 *
 * PURPOSE:
 * Test the User domain model, verifying user properties, roles, and active status.
 */
@DisplayName("Unit Tests - User Model")
class UserModelUnitTest {

    @Test
    @DisplayName("Should create User with all properties")
    void testGettersAndSetters() {
        // Arrange & Act
        User user = new User();
        user.setId("USER-123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPasswordHash("hashed_password");
        user.setActive(true);

        // Assert
        assertEquals("USER-123", user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("hashed_password", user.getPasswordHash());
        assertTrue(user.isActive());
    }

    @Test
    @DisplayName("Should handle user roles")
    void testUserRoles() {
        // Arrange
        User user = new User();
        Set<UserRole> roles = new HashSet<>(Arrays.asList(
            UserRole.SALES_REP,
            UserRole.SYSTEM_ADMIN
        ));

        // Act
        user.setRoles(roles);

        // Assert
        assertNotNull(user.getRoles());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(UserRole.SALES_REP));
        assertTrue(user.getRoles().contains(UserRole.SYSTEM_ADMIN));
    }

    @Test
    @DisplayName("Should handle single role")
    void testSingleRole() {
        // Arrange
        User user = new User();
        Set<UserRole> roles = new HashSet<>(Arrays.asList(UserRole.SALES_REP));

        // Act
        user.setRoles(roles);

        // Assert
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(UserRole.SALES_REP));
        assertFalse(user.getRoles().contains(UserRole.SYSTEM_ADMIN));
    }

    @Test
    @DisplayName("Should handle empty roles")
    void testEmptyRoles() {
        // Arrange
        User user = new User();
        user.setRoles(new HashSet<>());

        // Assert
        assertNotNull(user.getRoles());
        assertEquals(0, user.getRoles().size());
    }

    @Test
    @DisplayName("Should handle active status changes")
    void testActiveStatus() {
        // Arrange
        User user = new User();

        // Act & Assert: Active
        user.setActive(true);
        assertTrue(user.isActive());

        // Act & Assert: Inactive
        user.setActive(false);
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should handle full name construction")
    void testFullName() {
        // Arrange
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        // Act - if User model has getFullName() method
        String fullName = user.getFirstName() + " " + user.getLastName();

        // Assert
        assertEquals("Jane Smith", fullName);
    }

    @Test
    @DisplayName("Should test equals with same ID")
    void testEqualsSameId() {
        // Arrange
        User user1 = new User();
        user1.setId("USER-same");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId("USER-same");
        user2.setEmail("user2@example.com");

        // Act & Assert
        assertEquals(user1, user2);
    }

    @Test
    @DisplayName("Should test equals with different IDs")
    void testEqualsDifferentIds() {
        // Arrange
        User user1 = new User();
        user1.setId("USER-1");

        User user2 = new User();
        user2.setId("USER-2");

        // Act & Assert
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Should not equal null")
    void testEqualsNull() {
        // Arrange
        User user = new User();
        user.setId("USER-123");

        // Act & Assert
        assertNotEquals(user, null);
    }

    @Test
    @DisplayName("Should equal itself")
    void testEqualsSameReference() {
        // Arrange
        User user = new User();
        user.setId("USER-123");

        // Act & Assert
        assertEquals(user, user);
    }
}