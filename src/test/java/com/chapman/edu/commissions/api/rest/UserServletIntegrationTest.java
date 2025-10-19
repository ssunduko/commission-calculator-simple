package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.User;
import org.junit.jupiter.api.*;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserServlet.
 *
 * Demonstrates testing user management endpoints with role-based filtering.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServletIntegrationTest extends ApiIntegrationTestBase {

    @Test
    @Order(1)
    @DisplayName("POST /users should create new user and return 201")
    public void createUser_validData_returns201() throws Exception {
        // Arrange
        String userJson = """
                {
                    "username": "jsmith",
                    "email": "jsmith@example.com",
                    "firstName": "John",
                    "lastName": "Smith",
                    "active": true
                }
                """;

        // Act
        HttpResponse<String> response = post("/users", userJson);

        // Assert
        assertStatus(response, 201);
        User createdUser = parseResponse(response, User.class);
        assertNotNull(createdUser.getId());
        assertEquals("jsmith", createdUser.getUsername());
        assertEquals("jsmith@example.com", createdUser.getEmail());
    }

    @Test
    @Order(2)
    @DisplayName("GET /users should return all users with 200")
    public void getAllUsers_withData_returns200() throws Exception {
        // Arrange - Create another user
        post("/users", """
                {
                    "username": "mjones",
                    "email": "mjones@example.com",
                    "firstName": "Mary",
                    "lastName": "Jones"
                }
                """);

        // Act
        HttpResponse<String> response = get("/users");

        // Assert
        assertStatus(response, 200);
        assertTrue(response.body().contains("jsmith") ||
                   response.body().contains("mjones"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /users/{id} should return specific user with 200")
    public void getUserById_existingId_returns200() throws Exception {
        // Arrange
        HttpResponse<String> createResponse = post("/users", """
                {
                    "username": "bwilliams",
                    "email": "bwilliams@example.com",
                    "firstName": "Bob",
                    "lastName": "Williams"
                }
                """);
        User createdUser = parseResponse(createResponse, User.class);

        // Act
        HttpResponse<String> response = get("/users/" + createdUser.getId());

        // Assert
        assertStatus(response, 200);
        User retrievedUser = parseResponse(response, User.class);
        assertEquals("bwilliams", retrievedUser.getUsername());
    }

    @Test
    @Order(4)
    @DisplayName("GET /users/{id} should return 404 for non-existent user")
    public void getUserById_nonExistentId_returns404() throws Exception {
        // Act
        HttpResponse<String> response = get("/users/USER-999999");

        // Assert
        assertStatus(response, 404);
    }

    @Test
    @Order(5)
    @DisplayName("PUT /users/{id} should update existing user and return 200")
    public void updateUser_existingUser_returns200() throws Exception {
        // Arrange
        HttpResponse<String> createResponse = post("/users", """
                {
                    "username": "sgarcia",
                    "email": "sgarcia@example.com",
                    "firstName": "Sarah",
                    "lastName": "Garcia"
                }
                """);
        User createdUser = parseResponse(createResponse, User.class);

        String updateJson = String.format("""
                {
                    "id": "%s",
                    "username": "sgarcia",
                    "email": "sarah.garcia@newdomain.com",
                    "firstName": "Sarah",
                    "lastName": "Garcia-Johnson",
                    "active": true
                }
                """, createdUser.getId());

        // Act
        HttpResponse<String> response = put("/users/" + createdUser.getId(), updateJson);

        // Assert
        assertStatus(response, 200);
        User updatedUser = parseResponse(response, User.class);
        assertEquals("Garcia-Johnson", updatedUser.getLastName());
        assertEquals("sarah.garcia@newdomain.com", updatedUser.getEmail());
    }

    @Test
    @Order(6)
    @DisplayName("DELETE /users/{id} should delete user and return 204")
    public void deleteUser_existingUser_returns204() throws Exception {
        // Arrange
        HttpResponse<String> createResponse = post("/users", """
                {
                    "username": "tempuser",
                    "email": "temp@example.com",
                    "firstName": "Temp",
                    "lastName": "User"
                }
                """);
        User createdUser = parseResponse(createResponse, User.class);

        // Act
        HttpResponse<String> deleteResponse = delete("/users/" + createdUser.getId());

        // Assert
        assertStatus(deleteResponse, 204);

        // Verify deletion
        HttpResponse<String> getResponse = get("/users/" + createdUser.getId());
        assertStatus(getResponse, 404);
    }
}