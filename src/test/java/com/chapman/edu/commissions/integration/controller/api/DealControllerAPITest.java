package com.chapman.edu.commissions.integration.controller.api;

import com.chapman.edu.commissions.integration.IntegrationApplication;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * API TESTING - DealController (End-to-End HTTP Testing)
 *
 * PURPOSE:
 * API tests verify the entire application stack from HTTP request to HTTP response:
 * - HTTP endpoint routing
 * - Request parsing (JSON deserialization)
 * - Authentication and authorization
 * - Controller logic
 * - Service layer business logic
 * - Repository data access
 * - Database persistence
 * - Response serialization (JSON)
 * - HTTP status codes
 *
 * CONCEPTS DEMONSTRATED:
 * 1. END-TO-END API TESTING:
 *    - Test from client perspective (HTTP requests)
 *    - Verify HTTP methods (GET, POST, PUT, DELETE)
 *    - Validate HTTP status codes (200, 201, 400, 401, 404, 409)
 *    - Test request/response JSON structure
 *    - Test authentication flows
 *
 * 2. REST-ASSURED FRAMEWORK:
 *    - given(): Setup test preconditions (auth, headers, body)
 *    - when(): Execute HTTP request
 *    - then(): Assert response expectations
 *    - Hamcrest matchers for response validation
 *    - JSON path extraction and assertions
 *
 * 3. HTTP TESTING PATTERNS:
 *    - Request/Response cycle verification
 *    - Content negotiation (Content-Type, Accept headers)
 *    - HTTP Basic Authentication
 *    - RESTful endpoint testing (CRUD operations)
 *    - Error response validation
 *
 * 4. LAYERED ARCHITECTURE END-TO-END:
 *    HTTP Request → Filter → Controller → Service → Repository → Database
 *    Database → Repository → Service → Controller → HTTP Response
 *
 * LAYER: All Layers (End-to-End)
 * TEST TYPE: API Test (Black-box, HTTP-based)
 *
 * DIFFERENCE FROM OTHER TEST TYPES:
 * - Unit Tests: Test single class in isolation with mocks
 * - Integration Tests: Test multiple classes working together
 * - API Tests: Test entire application via HTTP interface
 *
 * TOOLS:
 * - RestAssured: HTTP client for API testing
 * - Hamcrest: Matchers for assertions
 * - JUnit 5: Test framework
 *
 * WHEN TO USE:
 * - Validate HTTP API contract
 * - Test authentication and authorization
 * - Verify complete request/response cycle
 * - Test error handling and status codes
 * - Acceptance testing from client perspective
 */
@DisplayName("API Tests - DealController (End-to-End HTTP)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DealControllerAPITest {

    private static IntegrationApplication app;
    private static String testUserId;
    private static String testDealId;

    // Test credentials (created during app startup)
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password"; // Matches IntegrationApplication sample data

    /**
     * START THE APPLICATION SERVER
     *
     * This starts the embedded Tomcat server with all layers initialized:
     * - Database schema created
     * - Repositories initialized
     * - Services created
     * - Controllers registered
     * - Authentication filter active
     * - Server listening on port 8080
     *
     * IMPORTANT: Uses startNonBlocking() to avoid test hanging
     */
    @BeforeAll
    static void startServer() throws Exception {
        // Reset database BEFORE starting the application to ensure clean state
        // This allows the IntegrationApplication to load sample data successfully
        com.chapman.edu.commissions.integration.database.DatabaseManager dbManager =
            com.chapman.edu.commissions.integration.database.DatabaseManager.getInstance();
        dbManager.resetDatabase();

        // Initialize and start the application in non-blocking mode
        app = new IntegrationApplication();
        app.startNonBlocking(); // NON-BLOCKING: Returns immediately for tests

        // Configure RestAssured base settings
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/v1/integration";

        // Wait for server to be ready and sample data to load
        Thread.sleep(5000);

        // Get test user ID for foreign key relationships
        testUserId = getUserIdByEmail(TEST_EMAIL);
    }

    /**
     * STOP THE APPLICATION SERVER
     *
     * Cleanup: Stop Tomcat and close database connections
     */
    @AfterAll
    static void stopServer() throws Exception {
        if (app != null) {
            app.stop();
        }
    }

    /**
     * CLEANUP BETWEEN TESTS
     *
     * Note: In a real scenario, you might reset the database between tests.
     * Here we rely on test isolation through unique IDs.
     */
    @BeforeEach
    void setUp() {
        // Optional: Reset database or clear test data
    }

    // ============================================================
    // AUTHENTICATION TESTS
    // ============================================================

    /**
     * TEST: Unauthenticated request returns 401
     *
     * API BEHAVIOR:
     * - AuthenticationFilter intercepts request
     * - No Authorization header present
     * - Filter returns 401 Unauthorized
     * - Controller is never reached
     *
     * DEMONSTRATES:
     * - Security filter chain
     * - HTTP Basic Auth requirement
     * - WWW-Authenticate challenge header
     */
    @Test
    @Order(1)
    @DisplayName("API: Should return 401 for unauthenticated request")
    void testUnauthenticatedRequest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(401)
            .header("WWW-Authenticate", containsString("Basic"));

        // NOTE: Without auth, request never reaches controller
    }

    /**
     * TEST: Invalid credentials return 401
     *
     * API BEHAVIOR:
     * - Authorization header present but credentials invalid
     * - AuthenticationFilter validates credentials
     * - Returns 401 for invalid username/password
     */
    @Test
    @Order(2)
    @DisplayName("API: Should return 401 for invalid credentials")
    void testInvalidCredentials() {
        given()
            .auth().basic("wrong@example.com", "wrongpassword")
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(401);
    }

    /**
     * TEST: Valid credentials allow access
     *
     * API BEHAVIOR:
     * - Valid HTTP Basic Auth credentials
     * - AuthenticationFilter validates and passes request to controller
     * - Controller processes request
     * - Returns 200 OK
     */
    @Test
    @Order(3)
    @DisplayName("API: Should allow access with valid credentials")
    void testValidAuthentication() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    // ============================================================
    // CREATE (POST) TESTS
    // ============================================================

    /**
     * TEST: Create deal via POST
     *
     * API FLOW:
     * 1. Client sends POST request with JSON body
     * 2. AuthenticationFilter validates credentials
     * 3. DealController.doPost() receives request
     * 4. BaseServlet parses JSON to Deal object
     * 5. DealService validates business rules
     * 6. DealRepository persists to database
     * 7. Controller returns 201 Created with deal JSON
     *
     * DEMONSTRATES:
     * - Full POST request/response cycle
     * - JSON request body parsing
     * - Business validation
     * - Database persistence
     * - 201 Created status code
     * - Location header (optional)
     * - Response body contains created entity
     */
    @Test
    @Order(4)
    @DisplayName("API: POST /deals should create new deal")
    void testCreateDeal() {
        // Arrange: Create request body
        Map<String, Object> dealRequest = new HashMap<>();
        dealRequest.put("title", "API Test Deal");
        dealRequest.put("status", "OPEN");
        dealRequest.put("salesRepId", testUserId);
        dealRequest.put("products", Arrays.asList(
            Map.of("name", "Cloud Platform", "price", 25000.00, "quantity", 1),
            Map.of("name", "Support Package", "price", 5000.00, "quantity", 2)
        ));

        // Act & Assert: POST request
        Response response = given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(dealRequest)
        .when()
            .post("/deals")
        .then()
            .statusCode(201) // HTTP 201 Created
            .contentType(ContentType.JSON)
            .body("title", equalTo("API Test Deal"))
            .body("status", equalTo("OPEN"))
            .body("salesRepId", equalTo(testUserId))
            .body("id", notNullValue())
            .body("products", hasSize(2))
        .extract()
            .response();

        // Extract ID for later tests
        testDealId = response.jsonPath().getString("id");
        assertNotNull(testDealId);
        assertTrue(testDealId.startsWith("DEAL-"));
    }

    /**
     * TEST: Create deal with missing required field fails
     *
     * API FLOW:
     * 1. Request with invalid data
     * 2. Service layer validation fails
     * 3. Controller catches IllegalArgumentException
     * 4. Returns 400 Bad Request with error message
     *
     * DEMONSTRATES:
     * - Business validation via API
     * - Error response structure
     * - 400 Bad Request status
     */
    @Test
    @Order(5)
    @DisplayName("API: POST /deals should return 400 for invalid data")
    void testCreateDealWithMissingTitle() {
        // Arrange: Invalid deal (no title)
        Map<String, Object> invalidDeal = new HashMap<>();
        invalidDeal.put("status", "OPEN");
        invalidDeal.put("salesRepId", testUserId);
        invalidDeal.put("products", Arrays.asList(
            Map.of("name", "Product", "price", 1000.00, "quantity", 1)
        ));

        // Act & Assert: Should return 400
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(invalidDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(400)
            .body("message", containsString("title is required"));
    }

    // ============================================================
    // READ (GET) TESTS
    // ============================================================

    /**
     * TEST: Get all deals
     *
     * API FLOW:
     * - GET request without ID
     * - Controller calls service.getAllDeals()
     * - Returns JSON array of deals
     * - Status 200 OK
     */
    @Test
    @Order(6)
    @DisplayName("API: GET /deals should return all deals")
    void testGetAllDeals() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", instanceOf(List.class))
            .body("size()", greaterThanOrEqualTo(1)); // At least the deal we created
    }

    /**
     * TEST: Get deal by ID
     *
     * API FLOW:
     * - GET /deals/{id}
     * - Controller extracts ID from path
     * - Service retrieves from repository
     * - Returns JSON object
     */
    @Test
    @Order(7)
    @DisplayName("API: GET /deals/{id} should return specific deal")
    void testGetDealById() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals/" + testDealId)
        .then()
            .statusCode(200)
            .body("id", equalTo(testDealId))
            .body("title", equalTo("API Test Deal"));
    }

    /**
     * TEST: Get non-existent deal returns 404
     *
     * API FLOW:
     * - Request deal that doesn't exist
     * - Service returns Optional.empty()
     * - Controller returns 404 Not Found
     */
    @Test
    @Order(8)
    @DisplayName("API: GET /deals/{id} should return 404 for non-existent deal")
    void testGetNonExistentDeal() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals/DEAL-nonexistent-id")
        .then()
            .statusCode(404)
            .body("message", containsString("not found"));
    }

    /**
     * TEST: Filter deals by status (query parameter)
     *
     * API FLOW:
     * - GET /deals?status=OPEN
     * - Controller extracts query parameter
     * - Service filters by status
     * - Returns filtered list
     */
    @Test
    @Order(9)
    @DisplayName("API: GET /deals?status=OPEN should filter by status")
    void testFilterDealsByStatus() {
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .queryParam("status", "OPEN")
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .body("$", instanceOf(List.class))
            .body("status", everyItem(equalTo("OPEN")));
    }

    // ============================================================
    // UPDATE (PUT) TESTS
    // ============================================================

    /**
     * TEST: Update deal via PUT
     *
     * API FLOW:
     * - PUT /deals/{id} with JSON body
     * - Controller parses ID and body
     * - Service validates and updates
     * - Repository persists changes
     * - Returns 200 OK with updated deal
     */
    @Test
    @Order(10)
    @DisplayName("API: PUT /deals/{id} should update existing deal")
    void testUpdateDeal() {
        // Arrange: Updated deal data
        Map<String, Object> updatedDeal = new HashMap<>();
        updatedDeal.put("title", "Updated API Deal");
        updatedDeal.put("status", "OPEN");
        updatedDeal.put("salesRepId", testUserId);
        updatedDeal.put("products", Arrays.asList(
            Map.of("name", "Updated Product", "price", 15000.00, "quantity", 1)
        ));

        // Act & Assert: PUT request
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(updatedDeal)
        .when()
            .put("/deals/" + testDealId)
        .then()
            .statusCode(200)
            .body("id", equalTo(testDealId))
            .body("title", equalTo("Updated API Deal"));

        // Verify change persisted
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals/" + testDealId)
        .then()
            .statusCode(200)
            .body("title", equalTo("Updated API Deal"));
    }

    /**
     * TEST: Update non-existent deal returns 404
     */
    @Test
    @Order(11)
    @DisplayName("API: PUT /deals/{id} should return 404 for non-existent deal")
    void testUpdateNonExistentDeal() {
        Map<String, Object> dealData = new HashMap<>();
        dealData.put("title", "Doesn't Matter");
        dealData.put("salesRepId", testUserId);
        dealData.put("products", Arrays.asList(
            Map.of("name", "Product", "price", 1000.00, "quantity", 1)
        ));

        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(dealData)
        .when()
            .put("/deals/DEAL-fake-id")
        .then()
            .statusCode(anyOf(is(404), is(400))); // Could be 404 or 400 depending on implementation
    }

    // ============================================================
    // DELETE TESTS
    // ============================================================

    /**
     * TEST: Delete deal via DELETE
     *
     * API FLOW:
     * - DELETE /deals/{id}
     * - Service validates business rules (can only delete OPEN)
     * - Repository deletes from database
     * - Returns 204 No Content
     */
    @Test
    @Order(12)
    @DisplayName("API: DELETE /deals/{id} should delete OPEN deal")
    void testDeleteDeal() {
        // First, create a deal to delete
        Map<String, Object> dealToDelete = new HashMap<>();
        dealToDelete.put("title", "To Be Deleted");
        dealToDelete.put("status", "OPEN");
        dealToDelete.put("salesRepId", testUserId);
        dealToDelete.put("products", Arrays.asList(
            Map.of("name", "Product", "price", 1000.00, "quantity", 1)
        ));

        String deleteId = given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(dealToDelete)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
        .extract()
            .jsonPath().getString("id");

        // Now delete it
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .delete("/deals/" + deleteId)
        .then()
            .statusCode(204); // No Content

        // Verify it's gone
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals/" + deleteId)
        .then()
            .statusCode(404);
    }

    /**
     * TEST: Cannot delete non-OPEN deal
     *
     * BUSINESS RULE VALIDATION VIA API:
     * - Service enforces "can only delete OPEN deals"
     * - Returns 409 Conflict
     */
    @Test
    @Order(13)
    @DisplayName("API: DELETE should return 409 for WON deal")
    void testCannotDeleteWonDeal() {
        // Create and close a deal
        Map<String, Object> dealData = new HashMap<>();
        dealData.put("title", "Won Deal");
        dealData.put("status", "OPEN");
        dealData.put("salesRepId", testUserId);
        dealData.put("products", Arrays.asList(
            Map.of("name", "Product", "price", 1000.00, "quantity", 1)
        ));

        String wonDealId = given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(dealData)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
        .extract()
            .jsonPath().getString("id");

        // Close it as WON
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/deals/" + wonDealId + "/close")
        .then()
            .statusCode(200);

        // Try to delete (should fail)
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .delete("/deals/" + wonDealId)
        .then()
            .statusCode(409) // Conflict
            .body("message", containsString("Can only delete OPEN deals"));
    }

    // ============================================================
    // CUSTOM ACTION TESTS
    // ============================================================

    /**
     * TEST: Close deal as WON (custom action)
     *
     * API FLOW:
     * - POST /deals/{id}/close
     * - Service.closeDealAsWon() orchestrates:
     *   - Validate deal exists
     *   - Validate status is OPEN
     *   - Set status to WON
     *   - Set close date to today
     *   - Persist changes
     * - Returns 200 OK with updated deal
     */
    @Test
    @Order(14)
    @DisplayName("API: POST /deals/{id}/close should close deal as WON")
    void testCloseDealAsWon() {
        // Create a new OPEN deal
        Map<String, Object> dealData = new HashMap<>();
        dealData.put("title", "Deal to Close");
        dealData.put("status", "OPEN");
        dealData.put("salesRepId", testUserId);
        dealData.put("products", Arrays.asList(
            Map.of("name", "Product", "price", 50000.00, "quantity", 1)
        ));

        String closeTestId = given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
            .contentType(ContentType.JSON)
            .body(dealData)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
        .extract()
            .jsonPath().getString("id");

        // Close the deal
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .post("/deals/" + closeTestId + "/close")
        .then()
            .statusCode(200)
            .body("status", equalTo("WON"))
            .body("closeDate", notNullValue());

        // Verify status changed
        given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/deals/" + closeTestId)
        .then()
            .body("status", equalTo("WON"));
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Helper: Get user ID by email
     * Uses the Users API to find user ID for test data setup
     */
    private static String getUserIdByEmail(String email) {
        Response response = given()
            .auth().basic(TEST_EMAIL, TEST_PASSWORD)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
        .extract()
            .response();

        List<Map<String, Object>> users = response.jsonPath().getList("$");
        for (Map<String, Object> user : users) {
            if (email.equals(user.get("email"))) {
                return (String) user.get("id");
            }
        }

        throw new RuntimeException("Test user not found: " + email);
    }

    /**
     * KEY TAKEAWAYS - API TESTING:
     *
     * WHAT WE TESTED:
     * ✓ HTTP Basic Authentication (401 Unauthorized)
     * ✓ RESTful CRUD operations (POST, GET, PUT, DELETE)
     * ✓ HTTP status codes (200, 201, 204, 400, 401, 404, 409)
     * ✓ Request JSON parsing and validation
     * ✓ Response JSON structure
     * ✓ Query parameters (filtering)
     * ✓ Path parameters (resource IDs)
     * ✓ Business rule enforcement via API
     * ✓ Error responses and error messages
     * ✓ End-to-end request/response cycle
     *
     * TEST PYRAMID LEVEL:
     * - API tests are at the TOP of the pyramid
     * - Slower than unit/integration tests
     * - Test from client perspective
     * - Verify entire application stack
     * - Most realistic tests (black-box)
     *
     * BEST PRACTICES:
     * - Use meaningful test data
     * - Test both success and failure cases
     * - Verify HTTP status codes
     * - Validate response structure
     * - Test authentication/authorization
     * - Keep tests independent (order shouldn't matter ideally)
     * - Clean up test data
     * - Use RestAssured's fluent API for readability
     */
}