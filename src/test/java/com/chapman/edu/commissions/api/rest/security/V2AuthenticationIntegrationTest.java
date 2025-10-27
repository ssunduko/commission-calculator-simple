package com.chapman.edu.commissions.api.rest.security;

import com.chapman.edu.commissions.model.Deal;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for V2 API Authentication Requirement.
 *
 * These tests demonstrate:
 * - V1 endpoints: Optional authentication (backward compatibility)
 * - V2 endpoints: Required authentication (enhanced security)
 * - Multiple authentication schemes for V2 (Basic, JWT)
 * - Proper error responses for unauthenticated V2 requests
 *
 * Key Difference from V1:
 * - V1: Can be accessed without authentication
 * - V2: MUST have valid authentication
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("V2 Authentication Requirement Integration Tests")
public class V2AuthenticationIntegrationTest extends SecurityIntegrationTestBase {

    @Test
    @Order(1)
    @DisplayName("V2 GET /deals without auth returns 401 Unauthorized")
    public void v2GetDeals_withoutAuth_returns401() throws Exception {
        // Arrange - No authentication provided

        // Act - Attempt to access V2 endpoint without auth
        HttpResponse<String> response = getV2("/deals");

        // Assert - Should be rejected
        assertStatus(response, 401);
        assertTrue(responseContains(response, "Authentication required") ||
                   responseContains(response, "error"),
                "Error response should indicate authentication required");

        // Check for WWW-Authenticate header
        String wwwAuth = getHeader(response, "WWW-Authenticate");
        assertNotNull(wwwAuth, "Should include WWW-Authenticate header");

        System.out.println("✓ V2 without auth: 401 Unauthorized");
        System.out.println("  WWW-Authenticate: " + wwwAuth);
    }

    @Test
    @Order(2)
    @DisplayName("V2 GET /deals with valid Basic auth returns 200")
    public void v2GetDeals_withValidBasicAuth_returns200() throws Exception {
        // Arrange - Valid credentials
        String username = "john.doe";
        String password = "password123";

        // Act - Access V2 with authentication
        HttpResponse<String> response = getV2WithBasicAuth("/deals", username, password);

        // Assert - Should succeed
        assertStatus(response, 200);
        assertNotNull(response.body());

        // Check for V2 response format: {data: [], metadata: {}}
        assertTrue(responseContains(response, "\"data\"") ||
                   response.body().startsWith("["),
                "V2 should return enhanced format or array");

        // Check for API-Version header
        String apiVersion = getHeader(response, "API-Version");
        assertEquals("2.0", apiVersion, "Should identify as API version 2.0");

        System.out.println("✓ V2 with Basic auth: 200 OK");
        System.out.println("  API-Version: " + apiVersion);
    }

    @Test
    @Order(3)
    @DisplayName("V2 POST /deals without auth returns 401")
    public void v2CreateDeal_withoutAuth_returns401() throws Exception {
        // Arrange
        String dealJson = """
                {
                    "title": "Unauthorized Deal",
                    "value": 10000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - Attempt to create without auth
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + "/deals"))
                .POST(HttpRequest.BodyPublishers.ofString(dealJson))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert - Should be rejected
        assertStatus(response, 401);
        assertTrue(responseContains(response, "Authentication required") ||
                   responseContains(response, "error"));

        System.out.println("✓ V2 POST without auth: 401 Unauthorized");
    }

    @Test
    @Order(4)
    @DisplayName("V2 POST /deals with valid Basic auth creates deal")
    public void v2CreateDeal_withValidBasicAuth_returns201() throws Exception {
        // Arrange
        String username = "john.doe";
        String password = "password123";
        String dealJson = """
                {
                    "title": "V2 Authenticated Deal",
                    "value": 50000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - Create with authentication
        HttpResponse<String> response = postV2WithBasicAuth("/deals", dealJson, username, password);

        // Assert
        assertStatus(response, 201);

        Deal createdDeal = parseResponse(response, Deal.class);
        assertNotNull(createdDeal.getId());
        assertEquals("V2 Authenticated Deal", createdDeal.getTitle());

        System.out.println("✓ V2 POST with Basic auth: 201 Created");
        System.out.println("  Created deal: " + createdDeal.getId());
    }

    @Test
    @Order(5)
    @DisplayName("V2 GET /deals with JWT Bearer token returns 200")
    public void v2GetDeals_withJwtToken_returns200() throws Exception {
        // Arrange - Valid JWT token (from JwtAuthenticator's token cache)
        String jwtToken = "sample-token-sales-rep";

        // Act - Access V2 with JWT
        HttpResponse<String> response = getV2WithBearerToken("/deals", jwtToken);

        // Assert - Should succeed
        assertStatus(response, 200);
        assertNotNull(response.body());

        // Check API-Version header
        String apiVersion = getHeader(response, "API-Version");
        assertEquals("2.0", apiVersion);

        System.out.println("✓ V2 with JWT Bearer token: 200 OK");
        System.out.println("  Authentication: Bearer token (sample-token-sales-rep)");
    }

    @Test
    @Order(6)
    @DisplayName("V2 with any password succeeds (educational auth)")
    public void v2GetDeals_withAnyPassword_succeeds() throws Exception {
        // Arrange - Any password works (educational implementation)
        String username = "john.doe";
        String password = "any-password-works";

        // Act
        HttpResponse<String> response = getV2WithBasicAuth("/deals", username, password);

        // Assert - Should succeed (educational auth accepts any non-empty password)
        assertStatus(response, 200);

        System.out.println("✓ V2 with any password: 200 OK (educational auth)");
        System.out.println("  Note: Production would validate passwords properly");
    }

    @Test
    @Order(7)
    @DisplayName("Comparison: V1 allows anonymous, V2 requires auth")
    public void comparison_v1Anonymous_v2RequiresAuth() throws Exception {
        // Act - Try both V1 and V2 without auth
        HttpResponse<String> v1Response = get("/deals");
        HttpResponse<String> v2Response = getV2("/deals");

        // Assert
        assertStatus(v1Response, 200);  // V1 allows anonymous
        assertStatus(v2Response, 401);  // V2 requires auth

        System.out.println("=== V1 vs V2 Authentication ===");
        System.out.println("V1 (anonymous):  " + v1Response.statusCode() + " - Access granted");
        System.out.println("V2 (anonymous):  " + v2Response.statusCode() + " - Access denied");
        System.out.println();
        System.out.println("✓ V1: Backward compatible (no auth required)");
        System.out.println("✓ V2: Enhanced security (auth required)");
    }

    @Test
    @Order(8)
    @DisplayName("V2 authentication demonstrates security filter")
    public void v2Authentication_demonstratesSecurityFilter() throws Exception {
        // This test demonstrates the SecurityFilter in action
        System.out.println("=== V2 Security Architecture ===");
        System.out.println();
        System.out.println("Components:");
        System.out.println("1. SecurityFilter (Intercepting Filter Pattern)");
        System.out.println("   - Intercepts ALL /api/v2/* requests");
        System.out.println("   - Extracts auth credentials from headers");
        System.out.println("   - Validates using AuthenticationManager");
        System.out.println();
        System.out.println("2. AuthenticationManager (Chain of Responsibility)");
        System.out.println("   - BasicAuthenticator: username/password");
        System.out.println("   - JwtAuthenticator: Bearer tokens");
        System.out.println();
        System.out.println("3. SecurityContext (Thread-Local Storage)");
        System.out.println("   - Stores current user for request");
        System.out.println("   - Cleared after request completes");
        System.out.println();

        // Demonstrate it actually works
        HttpResponse<String> unauthorizedResponse = getV2("/deals");
        HttpResponse<String> authorizedResponse = getV2WithBasicAuth("/deals", "john.doe", "password123");

        assertStatus(unauthorizedResponse, 401);
        assertStatus(authorizedResponse, 200);

        System.out.println("✓ Security filter successfully enforces authentication for V2");
    }
}