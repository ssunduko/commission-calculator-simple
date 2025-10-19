package com.chapman.edu.commissions.api.soap;

import com.chapman.edu.commissions.api.soap.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserService SOAP web service.
 */
@DisplayName("SOAP UserService Integration Tests")
public class UserSoapIntegrationTest extends SoapIntegrationTestBase {

    @Test
    @DisplayName("Should get all users via SOAP")
    void testGetAllUsers() {
        // Act
        List<UserDTO> users = userService.getAllUsers();

        // Assert
        assertNotNull(users, "Users list should not be null");
        assertTrue(users.size() > 0, "Should have at least one user");

        // Verify first user structure
        UserDTO firstUser = users.get(0);
        assertNotNull(firstUser.getId(), "User ID should not be null");
        assertNotNull(firstUser.getUsername(), "Username should not be null");
        assertNotNull(firstUser.getEmail(), "Email should not be null");
    }

    @Test
    @DisplayName("Should get a specific user by ID via SOAP")
    void testGetUserById() {
        // Arrange
        List<UserDTO> allUsers = userService.getAllUsers();
        String userId = allUsers.get(0).getId();

        // Act
        UserDTO user = userService.getUserById(userId);

        // Assert
        assertNotNull(user, "User should not be null");
        assertEquals(userId, user.getId(), "User ID should match");
        assertNotNull(user.getUsername(), "Username should not be null");
    }

    @Test
    @DisplayName("Should get user by username via SOAP")
    void testGetUserByUsername() {
        // Arrange
        List<UserDTO> allUsers = userService.getAllUsers();
        String username = allUsers.get(0).getUsername();

        // Act
        UserDTO user = userService.getUserByUsername(username);

        // Assert
        assertNotNull(user, "User should not be null");
        assertEquals(username, user.getUsername(), "Username should match");
    }

    @Test
    @DisplayName("Should create a new user via SOAP")
    void testCreateUser() {
        // Arrange
        UserDTO newUser = new UserDTO();
        newUser.setUsername("soaptest");
        newUser.setEmail("soaptest@example.com");
        newUser.setFirstName("SOAP");
        newUser.setLastName("Test");
        newUser.setRoles(Arrays.asList("SALES_REP"));
        newUser.setActive(true);
        newUser.setDepartment("Sales");

        // Act
        UserDTO created = userService.createUser(newUser);

        // Assert
        assertNotNull(created, "Created user should not be null");
        assertNotNull(created.getId(), "Created user should have an ID");
        assertEquals("soaptest", created.getUsername(), "Username should match");
        assertEquals("soaptest@example.com", created.getEmail(), "Email should match");
        assertEquals("SOAP", created.getFirstName(), "First name should match");
        assertEquals("Test", created.getLastName(), "Last name should match");
        assertTrue(created.getActive(), "User should be active");

        // Verify fullName is computed
        assertNotNull(created.getFullName(), "Full name should be computed");
        assertEquals("SOAP Test", created.getFullName(), "Full name should be 'SOAP Test'");
    }

    @Test
    @DisplayName("Should update an existing user via SOAP")
    void testUpdateUser() {
        // Arrange - create a user first
        UserDTO newUser = new UserDTO();
        newUser.setUsername("updatetest");
        newUser.setEmail("update@example.com");
        newUser.setFirstName("Update");
        newUser.setLastName("Test");
        newUser.setRoles(Arrays.asList("SALES_REP"));
        UserDTO created = userService.createUser(newUser);

        // Prepare update
        UserDTO updates = new UserDTO();
        updates.setEmail("newemail@example.com");
        updates.setDepartment("Marketing");

        // Act
        UserDTO updated = userService.updateUser(created.getId(), updates);

        // Assert
        assertNotNull(updated, "Updated user should not be null");
        assertEquals("newemail@example.com", updated.getEmail(), "Email should be updated");
        assertEquals("Marketing", updated.getDepartment(), "Department should be updated");
        // Original fields should remain unchanged
        assertEquals("updatetest", updated.getUsername(), "Username should not change");
    }

    @Test
    @DisplayName("Should delete a user via SOAP")
    void testDeleteUser() {
        // Arrange - create a user first
        UserDTO newUser = new UserDTO();
        newUser.setUsername("deletetest");
        newUser.setEmail("delete@example.com");
        newUser.setFirstName("Delete");
        newUser.setLastName("Test");
        newUser.setRoles(Arrays.asList("SALES_REP"));
        UserDTO created = userService.createUser(newUser);
        String userId = created.getId();

        // Act
        boolean deleted = userService.deleteUser(userId);

        // Assert
        assertTrue(deleted, "Delete operation should return true");

        // Verify it's actually deleted
        UserDTO retrieved = userService.getUserById(userId);
        assertNull(retrieved, "Deleted user should not be retrievable");
    }

    @Test
    @DisplayName("Should get users by role via SOAP")
    void testGetUsersByRole() {
        // Arrange - create a user with specific role
        UserDTO newUser = new UserDTO();
        newUser.setUsername("managertest");
        newUser.setEmail("manager@example.com");
        newUser.setFirstName("Manager");
        newUser.setLastName("Test");
        newUser.setRoles(Arrays.asList("SALES_MANAGER"));
        userService.createUser(newUser);

        // Act
        List<UserDTO> managers = userService.getUsersByRole("SALES_MANAGER");

        // Assert
        assertNotNull(managers, "Managers list should not be null");
        assertTrue(managers.size() > 0, "Should have at least one manager");

        // Verify all users have the SALES_MANAGER role
        for (UserDTO user : managers) {
            assertTrue(user.getRoles().contains("SALES_MANAGER"),
                    "All users should have SALES_MANAGER role");
        }
    }

    @Test
    @DisplayName("Should return null for non-existent username via SOAP")
    void testGetNonExistentUserByUsername() {
        // Act
        UserDTO user = userService.getUserByUsername("nonexistentuser");

        // Assert
        assertNull(user, "Non-existent user should return null");
    }
}