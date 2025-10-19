package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DealServlet.
 *
 * These tests demonstrate:
 * - End-to-end HTTP request/response testing
 * - AAA (Arrange-Act-Assert) pattern
 * - Test method naming conventions
 * - HTTP status code verification
 * - JSON serialization/deserialization testing
 *
 * Test naming convention: methodName_scenario_expectedResult
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DealServletIntegrationTest extends ApiIntegrationTestBase {

    /**
     * Test creating a deal via POST request.
     *
     * Demonstrates:
     * - POST request with JSON body
     * - 201 Created status code
     * - Response body contains created entity
     * - Server-generated ID
     */
    @Test
    @Order(1)
    @DisplayName("POST /deals should create new deal and return 201")
    public void createDeal_validData_returns201() throws Exception {
        // Arrange - Prepare test data
        String dealJson = """
                {
                    "title": "Enterprise Software License",
                    "value": 100000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;

        // Act - Make HTTP request
        HttpResponse<String> response = post("/deals", dealJson);

        // Assert - Verify response
        assertStatus(response, 201);  // 201 Created

        Deal createdDeal = parseResponse(response, Deal.class);
        assertNotNull(createdDeal.getId(), "Server should generate ID");
        assertEquals("Enterprise Software License", createdDeal.getTitle());
        assertEquals(new BigDecimal("100000.00"), createdDeal.getValue());
        assertEquals("USER-001", createdDeal.getSalesRepId());
    }

    /**
     * Test retrieving all deals via GET request.
     *
     * Demonstrates:
     * - GET request without ID
     * - Array response
     * - 200 OK status code
     */
    @Test
    @Order(2)
    @DisplayName("GET /deals should return all deals with 200")
    public void getAllDeals_withData_returns200() throws Exception {
        // Arrange - Create a deal first
        String dealJson = """
                {
                    "title": "Cloud Services Contract",
                    "value": 50000.00,
                    "salesRepId": "USER-002",
                    "status": "OPEN"
                }
                """;
        post("/deals", dealJson);

        // Act - Get all deals
        HttpResponse<String> response = get("/deals");

        // Assert
        assertStatus(response, 200);
        assertTrue(response.body().contains("Enterprise Software License") ||
                   response.body().contains("Cloud Services Contract"),
                "Response should contain created deals");
    }

    /**
     * Test retrieving specific deal by ID.
     *
     * Demonstrates:
     * - GET request with path parameter
     * - Resource lookup by ID
     * - Single entity response
     */
    @Test
    @Order(3)
    @DisplayName("GET /deals/{id} should return specific deal with 200")
    public void getDealById_existingId_returns200() throws Exception {
        // Arrange - Create a deal and extract its ID
        String dealJson = """
                {
                    "title": "Hardware Purchase",
                    "value": 75000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """;
        HttpResponse<String> createResponse = post("/deals", dealJson);
        Deal createdDeal = parseResponse(createResponse, Deal.class);
        String dealId = createdDeal.getId();

        // Act - Get the specific deal
        HttpResponse<String> response = get("/deals/" + dealId);

        // Assert
        assertStatus(response, 200);
        Deal retrievedDeal = parseResponse(response, Deal.class);
        assertEquals(dealId, retrievedDeal.getId());
        assertEquals("Hardware Purchase", retrievedDeal.getTitle());
    }

    /**
     * Test retrieving non-existent deal.
     *
     * Demonstrates:
     * - Error handling
     * - 404 Not Found status code
     * - Error response format
     */
    @Test
    @Order(4)
    @DisplayName("GET /deals/{id} should return 404 for non-existent deal")
    public void getDealById_nonExistentId_returns404() throws Exception {
        // Arrange
        String nonExistentId = "DEAL-999999";

        // Act
        HttpResponse<String> response = get("/deals/" + nonExistentId);

        // Assert
        assertStatus(response, 404);
        assertTrue(response.body().contains("Deal not found"),
                "Error message should indicate deal not found");
    }

    /**
     * Test updating a deal via PUT request.
     *
     * Demonstrates:
     * - PUT request for updates
     * - Full resource replacement
     * - 200 OK on successful update
     */
    @Test
    @Order(5)
    @DisplayName("PUT /deals/{id} should update existing deal and return 200")
    public void updateDeal_existingDeal_returns200() throws Exception {
        // Arrange - Create a deal first
        String createJson = """
                {
                    "title": "Consulting Services",
                    "value": 30000.00,
                    "salesRepId": "USER-003",
                    "status": "OPEN"
                }
                """;
        HttpResponse<String> createResponse = post("/deals", createJson);
        Deal createdDeal = parseResponse(createResponse, Deal.class);
        String dealId = createdDeal.getId();

        // Prepare update
        String updateJson = String.format("""
                {
                    "id": "%s",
                    "title": "Consulting Services - Updated",
                    "value": 35000.00,
                    "salesRepId": "USER-003",
                    "status": "WON"
                }
                """, dealId);

        // Act - Update the deal
        HttpResponse<String> response = put("/deals/" + dealId, updateJson);

        // Assert
        assertStatus(response, 200);
        Deal updatedDeal = parseResponse(response, Deal.class);
        assertEquals("Consulting Services - Updated", updatedDeal.getTitle());
        assertEquals(new BigDecimal("35000.00"), updatedDeal.getValue());
        assertEquals(DealStatus.WON, updatedDeal.getStatus());
    }

    /**
     * Test updating non-existent deal.
     *
     * Demonstrates:
     * - Error handling for updates
     * - 404 Not Found for missing resources
     */
    @Test
    @Order(6)
    @DisplayName("PUT /deals/{id} should return 404 for non-existent deal")
    public void updateDeal_nonExistentDeal_returns404() throws Exception {
        // Arrange
        String nonExistentId = "DEAL-999999";
        String updateJson = String.format("""
                {
                    "id": "%s",
                    "title": "Should Fail",
                    "value": 1000.00,
                    "salesRepId": "USER-001"
                }
                """, nonExistentId);

        // Act
        HttpResponse<String> response = put("/deals/" + nonExistentId, updateJson);

        // Assert
        assertStatus(response, 404);
    }

    /**
     * Test deleting a deal via DELETE request.
     *
     * Demonstrates:
     * - DELETE request
     * - 204 No Content on successful deletion
     * - Resource removal verification
     */
    @Test
    @Order(7)
    @DisplayName("DELETE /deals/{id} should delete deal and return 204")
    public void deleteDeal_existingDeal_returns204() throws Exception {
        // Arrange - Create a deal to delete
        String dealJson = """
                {
                    "title": "Deal to Delete",
                    "value": 10000.00,
                    "salesRepId": "USER-001"
                }
                """;
        HttpResponse<String> createResponse = post("/deals", dealJson);
        Deal createdDeal = parseResponse(createResponse, Deal.class);
        String dealId = createdDeal.getId();

        // Act - Delete the deal
        HttpResponse<String> deleteResponse = delete("/deals/" + dealId);

        // Assert deletion successful
        assertStatus(deleteResponse, 204);

        // Verify deal is actually deleted
        HttpResponse<String> getResponse = get("/deals/" + dealId);
        assertStatus(getResponse, 404);
    }

    /**
     * Test deleting non-existent deal.
     *
     * Demonstrates:
     * - Idempotency consideration
     * - 404 Not Found for missing resource
     */
    @Test
    @Order(8)
    @DisplayName("DELETE /deals/{id} should return 404 for non-existent deal")
    public void deleteDeal_nonExistentDeal_returns404() throws Exception {
        // Arrange
        String nonExistentId = "DEAL-999999";

        // Act
        HttpResponse<String> response = delete("/deals/" + nonExistentId);

        // Assert
        assertStatus(response, 404);
    }

    /**
     * Test filtering deals by status query parameter.
     *
     * Demonstrates:
     * - Query parameter handling
     * - Server-side filtering
     * - Multiple resources with different states
     */
    @Test
    @Order(9)
    @DisplayName("GET /deals?status=WON should return only won deals")
    public void getDeals_filterByStatus_returnsFilteredResults() throws Exception {
        // Arrange - Create deals with different statuses
        post("/deals", """
                {
                    "title": "Won Deal 1",
                    "value": 10000.00,
                    "salesRepId": "USER-001",
                    "status": "WON"
                }
                """);

        post("/deals", """
                {
                    "title": "Open Deal 1",
                    "value": 20000.00,
                    "salesRepId": "USER-001",
                    "status": "OPEN"
                }
                """);

        // Act - Get only WON deals
        HttpResponse<String> response = get("/deals?status=WON");

        // Assert
        assertStatus(response, 200);
        String responseBody = response.body();
        assertTrue(responseBody.contains("Won Deal 1"),
                "Response should contain won deal");
        assertFalse(responseBody.contains("Open Deal 1"),
                "Response should not contain open deal");
    }

    /**
     * Test filtering deals by sales rep ID.
     *
     * Demonstrates:
     * - Multiple query parameter support
     * - Filtering by different criteria
     */
    @Test
    @Order(10)
    @DisplayName("GET /deals?salesRepId=USER-001 should return deals for that rep")
    public void getDeals_filterBySalesRep_returnsFilteredResults() throws Exception {
        // Arrange - Create deals for different reps
        post("/deals", """
                {
                    "title": "Rep 001 Deal",
                    "value": 15000.00,
                    "salesRepId": "USER-001"
                }
                """);

        post("/deals", """
                {
                    "title": "Rep 002 Deal",
                    "value": 25000.00,
                    "salesRepId": "USER-002"
                }
                """);

        // Act - Get deals for USER-001
        HttpResponse<String> response = get("/deals?salesRepId=USER-001");

        // Assert
        assertStatus(response, 200);
        String responseBody = response.body();
        assertTrue(responseBody.contains("Rep 001 Deal"),
                "Response should contain USER-001 deals");
    }

    /**
     * Test creating deal with invalid JSON.
     *
     * Demonstrates:
     * - Input validation
     * - 400 Bad Request for invalid input
     * - Error handling
     */
    @Test
    @Order(11)
    @DisplayName("POST /deals with invalid JSON should return 400")
    public void createDeal_invalidJson_returns400() throws Exception {
        // Arrange - Invalid JSON (missing closing brace)
        String invalidJson = """
                {
                    "title": "Invalid Deal",
                    "value": 1000.00
                """;

        // Act
        HttpResponse<String> response = post("/deals", invalidJson);

        // Assert
        assertStatus(response, 400);
        assertTrue(response.body().contains("Invalid deal data") ||
                   response.body().contains("error"),
                "Error message should indicate invalid data");
    }
}