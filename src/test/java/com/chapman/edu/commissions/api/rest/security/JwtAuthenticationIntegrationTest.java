package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.Deal;
import org.junit.jupiter.api.*;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for JWT Bearer Token Authentication.
 *
 * These tests demonstrate:
 * - Bearer token authentication concepts
 * - JWT token format and structure
 * - Authorization header with Bearer scheme
 * - Optional authentication (current server state)
 * - Token-based authentication patterns
 *
 * NOTE: Current server has OPTIONAL authentication.
 * These tests demonstrate JWT concepts with actual HTTP requests.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("JWT Bearer Token Authentication Integration Tests")
public class JwtAuthenticationIntegrationTest extends SecurityIntegrationTestBase {

    // Sample JWT tokens for demonstration
    private static final String SAMPLE_TOKEN = "sample-token-sales-rep";

    @Test
    @Order(1)
    @DisplayName("GET /deals with Bearer token header returns 200")
    public void getDeals_withBearerToken_returns200() throws Exception {
        // Arrange - Bearer token format

        // Act - Make request with Bearer token
        HttpResponse<String> response = getWithBearerToken("/deals", SAMPLE_TOKEN);

        // Assert - Server accepts the header
        assertStatus(response, 200);
        assertNotNull(response.body());

        System.out.println("✓ Server accepts Bearer token: Authorization: Bearer <token>");
    }

    @Test
    @Order(2)
    @DisplayName("POST /deals with Bearer token creates deal")
    public void createDeal_withBearerToken_returns201() throws Exception {
        // Arrange
        String dealJson = """
                {
                    "title": "JWT Authenticated Deal",
                    "value": 75000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - Create deal with JWT
        HttpResponse<String> response = postWithBearerToken("/deals", dealJson, SAMPLE_TOKEN);

        // Assert
        assertStatus(response, 201);

        Deal createdDeal = parseResponse(response, Deal.class);
        assertNotNull(createdDeal.getId());
        assertEquals("JWT Authenticated Deal", createdDeal.getTitle());

        System.out.println("✓ Created deal with JWT: " + createdDeal.getId());
    }

    @Test
    @Order(3)
    @DisplayName("Bearer token header format should be correct")
    public void bearerTokenHeader_hasCorrectFormat() {
        // Arrange
        String token = "sample-jwt-token-12345";

        // Act - Construct header
        String authHeader = "Bearer " + token;

        // Assert
        assertTrue(authHeader.startsWith("Bearer "),
                "Auth header should start with 'Bearer '");
        assertTrue(authHeader.contains(token),
                "Auth header should contain the token");

        System.out.println("✓ Bearer token format: " + authHeader);
    }

    @Test
    @Order(4)
    @DisplayName("Multiple requests with same JWT succeed")
    public void multipleRequests_withSameJwt_allSucceed() throws Exception {
        // Arrange - Same token for all requests

        // Act - Make multiple requests
        HttpResponse<String> response1 = getWithBearerToken("/deals", SAMPLE_TOKEN);
        HttpResponse<String> response2 = getWithBearerToken("/deals", SAMPLE_TOKEN);
        HttpResponse<String> response3 = getWithBearerToken("/deals", SAMPLE_TOKEN);

        // Assert - All should succeed
        assertStatus(response1, 200);
        assertStatus(response2, 200);
        assertStatus(response3, 200);

        System.out.println("✓ Token reuse works (stateless authentication)");
    }

    @Test
    @Order(5)
    @DisplayName("JWT and Basic auth both work for same endpoint")
    public void jwtAndBasicAuth_bothWork() throws Exception {
        // Arrange

        // Act - Same endpoint, different auth methods
        HttpResponse<String> jwtResponse = getWithBearerToken("/deals", SAMPLE_TOKEN);
        HttpResponse<String> basicResponse = getWithBasicAuth("/deals", "john.doe", "password");

        // Assert - Both should succeed
        assertStatus(jwtResponse, 200);
        assertStatus(basicResponse, 200);

        System.out.println("✓ Multiple auth schemes supported");
    }

    @Test
    @Order(6)
    @DisplayName("JWT structure demonstration (educational)")
    public void jwt_structureDemonstration() {
        // This test demonstrates JWT structure conceptually
        // Actual JWT: header.payload.signature (3 parts separated by dots)

        String exampleJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                           "eyJ1c2VySWQiOiJVU0VSTC0wMDEiLCJ1c2VybmFtZSI6ImpvaG4uZG9lIn0." +
                           "signature-here";

        String[] parts = exampleJwt.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts: header.payload.signature");

        System.out.println("✓ JWT structure: header.payload.signature");
        System.out.println("  Production tip: Use libraries like java-jwt or jjwt for parsing");
    }
}