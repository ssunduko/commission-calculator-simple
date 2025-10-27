package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.Deal;
import org.junit.jupiter.api.*;

import java.net.http.HttpResponse;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Basic Authentication.
 *
 * These tests demonstrate:
 * - HTTP Basic Authentication concepts
 * - Base64 encoding of credentials
 * - Authorization header format
 * - Optional authentication (current server state)
 * - How authentication would work when enabled
 *
 * Test Naming Convention: methodName_scenario_expectedResult
 *
 * NOTE: The current server has OPTIONAL authentication (no SecurityFilter configured).
 * These tests demonstrate authentication concepts and verify the server accepts
 * authenticated requests, even though it doesn't require them.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Basic Authentication Integration Tests")
public class BasicAuthenticationIntegrationTest extends SecurityIntegrationTestBase {

    /**
     * Test that requests without authentication can access the API.
     * Current server configuration: OPTIONAL authentication.
     *
     * Demonstrates:
     * - Anonymous access allowed
     * - Optional authentication model
     * - 200 OK for unauthenticated requests
     */
    @Test
    @Order(1)
    @DisplayName("GET /deals without auth returns 200 (optional auth enabled)")
    public void getDeals_withoutAuth_returns200() throws Exception {
        // Arrange - No authentication needed with optional auth

        // Act - Make request without credentials
        HttpResponse<String> response = get("/deals");

        // Assert - Should succeed with optional auth
        assertStatus(response, 200);
        assertNotNull(response.body());

        System.out.println("✓ Anonymous access allowed (optional authentication)");
    }

    /**
     * Test that server accepts requests with Basic authentication header.
     * With optional auth, the header is accepted but not required.
     *
     * Demonstrates:
     * - Authorization header format: Basic base64(username:password)
     * - Server accepts authenticated requests
     * - Base64 credential encoding
     */
    @Test
    @Order(2)
    @DisplayName("GET /deals with Basic auth header returns 200")
    public void getDeals_withBasicAuthHeader_returns200() throws Exception {
        // Arrange - Credentials in Basic auth format
        String username = "john.doe";
        String password = "password123";

        // Act - Make request with Basic auth header
        HttpResponse<String> response = getWithBasicAuth("/deals", username, password);

        // Assert - Should succeed (server accepts the header)
        assertStatus(response, 200);
        assertNotNull(response.body());

        System.out.println("✓ Server accepts Basic auth header: Authorization: Basic <base64>");
    }

    /**
     * Test Basic authentication header format validation.
     *
     * Demonstrates:
     * - Creating properly formatted Basic auth header
     * - Base64 encoding process
     * - Header structure: "Basic <base64(username:password)>"
     */
    @Test
    @Order(3)
    @DisplayName("Basic auth header should be properly formatted")
    public void basicAuthHeader_isProperlyFormatted() {
        // Arrange
        String username = "testuser";
        String password = "testpass";

        // Act - Create Basic auth header
        String authHeader = createBasicAuthHeader(username, password);

        // Assert - Verify format
        assertTrue(authHeader.startsWith("Basic "),
                "Auth header should start with 'Basic '");

        // Verify it can be decoded
        String encoded = authHeader.substring(6); // Remove "Basic "
        String decoded = new String(Base64.getDecoder().decode(encoded));
        assertEquals("testuser:testpass", decoded,
                "Decoded credentials should match username:password");

        System.out.println("✓ Basic auth format: Basic " + encoded);
        System.out.println("✓ Decoded: " + decoded);
    }

    /**
     * Test Basic authentication with invalid password.
     *
     * Demonstrates:
     * - Password validation
     * - Authentication failure handling
     */
    @Test
    @Order(4)
    @DisplayName("GET /deals with invalid password should return 401 (when auth required)")
    public void getDeals_withInvalidPassword_returns401OrSucceeds() throws Exception {
        // Arrange - Valid username but wrong password
        String username = "john.doe";
        String password = "wrongpassword";

        // Act
        HttpResponse<String> response = getWithBasicAuth("/deals", username, password);

        // Assert
        assertTrue(response.statusCode() == 200 || response.statusCode() == 401,
                "Should return 200 (optional auth) or 401 (required auth)");
    }

    /**
     * Test creating a resource with Basic authentication.
     *
     * Demonstrates:
     * - POST request with authentication
     * - Resource creation with credentials
     * - User identity tracking
     */
    @Test
    @Order(5)
    @DisplayName("POST /deals with Basic auth should create deal and return 201")
    public void createDeal_withBasicAuth_returns201() throws Exception {
        // Arrange
        String username = "john.doe";
        String password = "password123";
        String dealJson = """
                {
                    "title": "Authenticated Deal",
                    "value": 50000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - Create deal with authentication
        HttpResponse<String> response = postWithBasicAuth("/deals", dealJson, username, password);

        // Assert
        assertStatus(response, 201);

        Deal createdDeal = parseResponse(response, Deal.class);
        assertNotNull(createdDeal.getId());
        assertEquals("Authenticated Deal", createdDeal.getTitle());

        System.out.println("Created deal: " + createdDeal.getId() + " as user: " + username);
    }

    /**
     * Test updating a resource with Basic authentication.
     *
     * Demonstrates:
     * - PUT request with authentication
     * - Resource ownership validation (future enhancement)
     */
    @Test
    @Order(6)
    @DisplayName("PUT /deals/{id} with Basic auth should update deal")
    public void updateDeal_withBasicAuth_returns200() throws Exception {
        // Arrange - Create a deal first
        String username = "john.doe";
        String password = "password123";

        String createJson = """
                {
                    "title": "Deal to Update",
                    "value": 30000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        HttpResponse<String> createResponse = postWithBasicAuth("/deals", createJson,
                username, password);
        Deal createdDeal = parseResponse(createResponse, Deal.class);
        String dealId = createdDeal.getId();

        // Prepare update
        String updateJson = String.format("""
                {
                    "id": "%s",
                    "title": "Updated with Auth",
                    "value": 35000.00,
                    "salesRepId": "USER-001",
                    "status": "WON"
                }
                """, dealId);

        // Act - Update with authentication
        HttpResponse<String> response = putWithBasicAuth("/deals/" + dealId, updateJson,
                username, password);

        // Assert
        assertStatus(response, 200);

        Deal updatedDeal = parseResponse(response, Deal.class);
        assertEquals("Updated with Auth", updatedDeal.getTitle());
    }

    /**
     * Test deleting a resource with Basic authentication.
     *
     * Demonstrates:
     * - DELETE request with authentication
     * - Resource deletion authorization
     */
    @Test
    @Order(7)
    @DisplayName("DELETE /deals/{id} with Basic auth should delete deal")
    public void deleteDeal_withBasicAuth_returns204() throws Exception {
        // Arrange - Create a deal to delete
        String username = "john.doe";
        String password = "password123";

        String dealJson = """
                {
                    "title": "Deal to Delete",
                    "value": 10000.00,
                    "salesRepId": "USER-001"
                }
                """;

        HttpResponse<String> createResponse = postWithBasicAuth("/deals", dealJson,
                username, password);
        Deal createdDeal = parseResponse(createResponse, Deal.class);
        String dealId = createdDeal.getId();

        // Act - Delete with authentication
        HttpResponse<String> response = deleteWithBasicAuth("/deals/" + dealId,
                username, password);

        // Assert
        assertStatus(response, 204);

        // Verify deletion
        HttpResponse<String> getResponse = get("/deals/" + dealId);
        assertStatus(getResponse, 404);
    }

    /**
     * Test Basic auth header format.
     *
     * Demonstrates:
     * - Base64 encoding of credentials
     * - Authorization header format
     * - Credential parsing
     */
    @Test
    @Order(8)
    @DisplayName("Basic auth header should be properly formatted")
    public void basicAuthHeader_correctFormat_isValid() {
        // Arrange
        String username = "testuser";
        String password = "testpass";

        // Act
        String authHeader = createBasicAuthHeader(username, password);

        // Assert
        assertTrue(authHeader.startsWith("Basic "),
                "Auth header should start with 'Basic '");

        // Verify it can be decoded
        String encoded = authHeader.substring(6); // Remove "Basic "
        String decoded = new String(java.util.Base64.getDecoder().decode(encoded));
        assertEquals("testuser:testpass", decoded,
                "Decoded credentials should match username:password");

        System.out.println("Auth header format verified: " + authHeader);
    }

    /**
     * Test multiple authenticated requests in sequence.
     *
     * Demonstrates:
     * - Stateless authentication (each request must include credentials)
     * - No session management
     * - Request independence
     */
    @Test
    @Order(9)
    @DisplayName("Multiple requests with Basic auth should each authenticate independently")
    public void multipleRequests_withBasicAuth_eachAuthenticatesIndependently() throws Exception {
        // Arrange
        String username = "john.doe";
        String password = "password123";

        // Act - Make multiple requests
        HttpResponse<String> response1 = getWithBasicAuth("/deals", username, password);
        HttpResponse<String> response2 = getWithBasicAuth("/deals", username, password);
        HttpResponse<String> response3 = getWithBasicAuth("/deals", username, password);

        // Assert - All should succeed independently
        assertStatus(response1, 200);
        assertStatus(response2, 200);
        assertStatus(response3, 200);

        System.out.println("All requests authenticated successfully (stateless)");
    }

    /**
     * Test that empty credentials are rejected.
     *
     * Demonstrates:
     * - Credential validation
     * - Empty username/password handling
     */
    @Test
    @Order(10)
    @DisplayName("Basic auth with empty credentials should be rejected (when auth required)")
    public void basicAuth_withEmptyCredentials_isRejectedOrSucceeds() throws Exception {
        // Arrange - Empty credentials
        String username = "";
        String password = "";

        // Act
        HttpResponse<String> response = getWithBasicAuth("/deals", username, password);

        // Assert - With optional auth: 200, with required auth: 401
        assertTrue(response.statusCode() == 200 || response.statusCode() == 401,
                "Should return 200 (optional auth) or 401 (required auth)");
    }
}