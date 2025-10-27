package com.chapman.edu.commissions.api.rest.version;

import com.chapman.edu.commissions.model.Deal;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for API Versioning.
 *
 * These tests demonstrate:
 * - Path-based versioning concepts
 * - Version evolution patterns
 * - Testing with existing V1 endpoint
 * - How versioning would work when fully implemented
 *
 * NOTE: Currently tests against existing /api/v1 endpoint.
 * Tests demonstrate versioning patterns and concepts.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("API Versioning Integration Tests")
public class ApiVersioningIntegrationTest extends VersionIntegrationTestBase {

    @Test
    @Order(1)
    @DisplayName("V1 endpoint is accessible")
    public void v1Endpoint_isAccessible() throws Exception {
        // Arrange - V1 is the current existing endpoint

        // Act - Call V1 endpoint
        HttpResponse<String> response = getV1("/deals");

        // Assert - Should be accessible
        assertStatus(response, 200);
        assertNotNull(response.body());

        System.out.println("✓ V1 endpoint accessible: /api/v1/deals");
    }

    @Test
    @Order(2)
    @DisplayName("V1 returns array response format")
    public void v1_returnsArrayResponse() throws Exception {
        // Arrange - Create test deal
        String dealJson = """
                {
                    "title": "V1 Format Test",
                    "value": 10000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;
        postV1("/deals", dealJson);

        // Act - Get all deals
        HttpResponse<String> response = getV1("/deals");

        // Assert - Should be simple array (V1 format)
        assertStatus(response, 200);

        String body = response.body();
        assertTrue(body.startsWith("["), "V1 should return array");

        System.out.println("✓ V1 response format: simple array (no pagination)");
    }

    @Test
    @Order(3)
    @DisplayName("V1 supports basic CRUD operations")
    public void v1_supportsBasicCrud() throws Exception {
        // Arrange - Test data
        String createJson = """
                {
                    "title": "CRUD Test Deal",
                    "value": 30000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - CREATE
        HttpResponse<String> createResponse = postV1("/deals", createJson);
        assertStatus(createResponse, 201);
        Deal created = parseResponse(createResponse, Deal.class);

        // READ
        HttpResponse<String> getResponse = getV1("/deals/" + created.getId());
        assertStatus(getResponse, 200);

        // UPDATE
        String updateJson = String.format("""
                {
                    "id": "%s",
                    "title": "CRUD Test Updated",
                    "value": 35000.00,
                    "salesRepId": "USER-001",
                    "status": "WON"
                }
                """, created.getId());
        HttpResponse<String> updateResponse = putV1("/deals/" + created.getId(), updateJson);
        assertStatus(updateResponse, 200);

        // DELETE
        HttpResponse<String> deleteResponse = deleteV1("/deals/" + created.getId());
        assertStatus(deleteResponse, 204);

        System.out.println("✓ V1 supports full CRUD operations");
    }

    @Test
    @Order(4)
    @DisplayName("V1 supports status filtering")
    public void v1_supportsStatusFiltering() throws Exception {
        // Arrange - Create deals with different statuses
        postV1("/deals", """
                {"title": "Won Deal 1", "value": 10000.00, "salesRepId": "USER-001", "status": "WON"}
                """);
        postV1("/deals", """
                {"title": "Open Deal 1", "value": 20000.00, "salesRepId": "USER-001", "status": "OPEN"}
                """);

        // Act - Filter by status
        HttpResponse<String> response = getV1("/deals?status=WON");

        // Assert - Should have filtering
        assertStatus(response, 200);
        assertTrue(response.body().contains("Won Deal"), "Should include won deals");

        System.out.println("✓ V1 filtering: ?status=WON");
    }

    @Test
    @Order(5)
    @DisplayName("URL structure demonstrates path versioning")
    public void urlStructure_demonstratesPathVersioning() throws Exception {
        // Act - Test both V1 and V2 URLs are accessible
        HttpResponse<String> v1Response = getV1("/deals");
        HttpResponse<String> v2Response = getV2WithAuth("/deals", "jsmith", "password");

        // Assert - Both versions accessible
        assertStatus(v1Response, 200);
        assertStatus(v2Response, 200);

        // Verify version headers
        String v1Version = getHeader(v1Response, "API-Version");
        String v2Version = getHeader(v2Response, "API-Version");

        assertEquals("1.0", v1Version, "V1 should report version 1.0");
        assertEquals("2.0", v2Version, "V2 should report version 2.0");

        // Verify V1 has deprecation warning
        String v1Warning = getHeader(v1Response, "Warning");
        assertNotNull(v1Warning, "V1 should have deprecation warning");
        assertTrue(v1Warning.contains("deprecated"), "Warning should mention deprecation");

        System.out.println("✓ Path-based versioning verified");
        System.out.println("  V1: " + v1Version + " (deprecated)");
        System.out.println("  V2: " + v2Version + " (current)");
    }

    @Test
    @Order(6)
    @DisplayName("V2 includes pagination and metadata")
    public void v2_includesPaginationAndMetadata() throws Exception {
        // Arrange - Create multiple deals
        for (int i = 1; i <= 5; i++) {
            postV2WithAuth("/deals", String.format("""
                    {"title": "Deal %d", "value": %d.00, "salesRepId": "USER-001", "status": "OPEN"}
                    """, i, i * 10000), "jsmith", "password");
        }

        // Act - Request V2 with pagination
        HttpResponse<String> response = getV2WithAuth("/deals?page=1&limit=3", "jsmith", "password");

        // Assert - Should have V2 response structure
        assertStatus(response, 200);

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertTrue(json.has("data"), "V2 should have 'data' field");
        assertTrue(json.has("metadata"), "V2 should have 'metadata' field");

        // Verify metadata structure
        JsonObject metadata = json.getAsJsonObject("metadata");
        assertEquals(1, metadata.get("page").getAsInt(), "Should be page 1");
        assertEquals(3, metadata.get("limit").getAsInt(), "Should have limit 3");
        assertTrue(metadata.has("totalCount"), "Should include totalCount");
        assertTrue(metadata.has("hasNext"), "Should include hasNext");

        System.out.println("✓ V2 pagination verified: " + metadata);
    }

    @Test
    @Order(7)
    @DisplayName("V2 includes computed fields")
    public void v2_includesComputedFields() throws Exception {
        // Arrange
        String dealJson = """
                {"title": "Computed Test", "value": 50000.00, "salesRepId": "USER-001", "status": "OPEN"}
                """;
        HttpResponse<String> createResponse = postV2WithAuth("/deals", dealJson, "jsmith", "password");
        assertStatus(createResponse, 201);

        // Act - Get the created deal
        Deal created = parseResponse(createResponse, Deal.class);
        HttpResponse<String> getResponse = getV2WithAuth("/deals/" + created.getId(), "jsmith", "password");

        // Assert - Should have computed fields
        assertStatus(getResponse, 200);
        JsonObject deal = JsonParser.parseString(getResponse.body()).getAsJsonObject();

        assertTrue(deal.has("estimatedCommission"), "Should have estimatedCommission");
        assertTrue(deal.has("productCount"), "Should have productCount");

        double commission = deal.get("estimatedCommission").getAsDouble();
        assertEquals(5000.0, commission, 0.01, "10% commission on 50000");

        System.out.println("✓ V2 computed fields verified: commission=" + commission);
    }

    @Test
    @Order(8)
    @DisplayName("V1 and V2 share data (backward compatibility)")
    public void v1AndV2_shareData() throws Exception {
        // Arrange - Create deal via V1
        String dealJson = """
                {"title": "Shared Deal", "value": 25000.00, "salesRepId": "USER-001", "status": "OPEN"}
                """;
        HttpResponse<String> v1Create = postV1("/deals", dealJson);
        assertStatus(v1Create, 201);
        Deal created = parseResponse(v1Create, Deal.class);

        // Act - Access same deal via V2
        HttpResponse<String> v2Get = getV2WithAuth("/deals/" + created.getId(), "jsmith", "password");

        // Assert - V2 can read V1-created data
        assertStatus(v2Get, 200);
        JsonObject v2Deal = JsonParser.parseString(v2Get.body()).getAsJsonObject();
        assertEquals(created.getId(), v2Deal.get("id").getAsString(), "IDs should match");
        assertEquals("Shared Deal", v2Deal.get("title").getAsString(), "Should read V1 data");

        // Act - Update via V2
        String updateJson = String.format("""
                {"id": "%s", "title": "Updated via V2", "value": 30000.00, "salesRepId": "USER-001", "status": "WON"}
                """, created.getId());
        HttpResponse<String> v2Update = putV2WithAuth("/deals/" + created.getId(), updateJson, "jsmith", "password");
        assertStatus(v2Update, 200);

        // Act - Read updated data via V1
        HttpResponse<String> v1Get = getV1("/deals/" + created.getId());

        // Assert - V1 can read V2 updates
        assertStatus(v1Get, 200);
        Deal updated = parseResponse(v1Get, Deal.class);
        assertEquals("Updated via V2", updated.getTitle(), "V1 should see V2 updates");

        System.out.println("✓ Backward compatibility verified: shared data layer");
    }

    @Test
    @Order(9)
    @DisplayName("V2 has enhanced error responses")
    public void v2_hasEnhancedErrorResponses() throws Exception {
        // Act - Request non-existent deal from V2
        HttpResponse<String> v2Response = getV2WithAuth("/deals/INVALID-ID", "jsmith", "password");

        // Assert - V2 error has enhanced fields
        assertStatus(v2Response, 404);
        JsonObject error = JsonParser.parseString(v2Response.body()).getAsJsonObject();

        assertTrue(error.has("error"), "Should have error message");
        assertTrue(error.has("errorCode"), "V2 should have errorCode");
        assertTrue(error.has("status"), "Should have status code");
        assertTrue(error.has("timestamp"), "V2 should have timestamp");

        assertEquals("RESOURCE_NOT_FOUND", error.get("errorCode").getAsString(), "Should use error code");
        assertEquals(404, error.get("status").getAsInt(), "Status should be 404");

        // Compare to V1 error (simpler format)
        HttpResponse<String> v1Response = getV1("/deals/INVALID-ID");
        assertStatus(v1Response, 404);
        JsonObject v1Error = JsonParser.parseString(v1Response.body()).getAsJsonObject();

        assertFalse(v1Error.has("errorCode"), "V1 should NOT have errorCode");
        assertFalse(v1Error.has("timestamp"), "V1 should NOT have timestamp");

        System.out.println("✓ V2 enhanced errors verified: " + error.get("errorCode").getAsString());
    }
}