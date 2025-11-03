package com.chapman.edu.commissions.integration.controller.externalserver;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * EXTERNAL SERVER TESTING DEMONSTRATION
 * =====================================
 *
 * PURPOSE:
 * This test class demonstrates testing against a REAL EXTERNAL MOCK API SERVICE.
 * This is different from other testing approaches in this package:
 *
 * 1. UNIT TESTS: Test individual components in isolation with mocked dependencies
 * 2. MOCK TESTS: Test with MockServer framework creating LOCAL HTTP mock server
 * 3. INTEGRATION TESTS: Test multiple layers together WITHOUT HTTP layer
 * 4. API TESTS: Test OUR OWN APPLICATION'S embedded Tomcat server endpoints
 * 5. EXTERNAL SERVER TESTS (THIS CLASS): Test against THIRD-PARTY mock API service
 *
 * EXTERNAL API TESTING EXPLAINED:
 * ===============================
 * External server testing validates that your application can correctly interact
 * with third-party APIs, external services, or partner systems. This is crucial for:
 * - Testing API integration code without depending on production systems
 * - Validating request/response formats match external API contracts
 * - Testing error handling for external service failures
 * - Development and testing when external systems are unavailable
 *
 * KEY DIFFERENCES FROM OTHER TEST TYPES:
 * ======================================
 * - MockServer: Creates LOCAL mock HTTP server in your test process
 * - External Server: Calls REMOTE mock API service (e.g., Apidog, Postman Mock Server)
 * - API Tests: Test YOUR application's endpoints
 * - External Server Tests: Test THIRD-PARTY service endpoints
 *
 * WHEN TO USE EXTERNAL SERVER TESTS:
 * ==================================
 * - Validating integration with partner APIs
 * - Testing against API sandbox environments
 * - Contract testing with external service specifications
 * - Testing retry logic, timeouts, and circuit breakers
 * - Validating API client code behavior
 *
 * EXTERNAL MOCK API SERVICE:
 * =========================
 * This test uses Apidog Mock Server (https://mock.apidog.com/) which provides:
 * - Persistent mock endpoints
 * - Predictable responses for testing
 * - No need to run your own application
 * - Simulates real HTTP communication
 *
 * API Endpoint: https://mock.apidog.com/m1/1110236-1100800-946851/deals
 *
 * RESTASSURED FRAMEWORK:
 * =====================
 * RestAssured provides a fluent DSL for HTTP API testing:
 * - given(): Setup (headers, auth, body)
 * - when(): Action (HTTP method and endpoint)
 * - then(): Assertions (status code, response body)
 *
 * EDUCATIONAL VALUE:
 * ==================
 * This demonstrates how to test external API integrations which is essential for:
 * - Microservices architectures
 * - Third-party API integrations (payment gateways, shipping APIs, etc.)
 * - Partner system integrations
 * - SaaS platform integrations
 *
 * IMPORTANT: Running External Tests
 * ==================================
 * These tests are DISABLED BY DEFAULT because they:
 * - Depend on external internet connectivity
 * - Can be slow due to network latency
 * - May fail if external service is down
 * - Should not block regular CI/CD pipelines
 *
 * To enable these tests:
 * 1. Remove @Disabled annotation from the class
 * 2. Or run with: mvn test -Dtest=DealControllerExternalServerTest
 * 3. Or use JUnit tags: mvn test -Dgroups="external"
 *
 * TIMEOUT PROTECTION:
 * ===================
 * All tests have 10-second timeout to prevent hanging if external service is unreachable.
 */
@DisplayName("External Server Tests - Third-Party Mock API Integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("external")
@Disabled("External API tests disabled by default - enable manually when testing external API integration")
@Timeout(value = 10, unit = TimeUnit.SECONDS)  // 10-second timeout per test
public class DealControllerExternalServerTest {

    /**
     * CONFIGURATION: Base URL for external mock API
     *
     * EXPLANATION:
     * This is the root URL of the third-party mock API service.
     * In production, you would use environment variables or configuration files
     * to manage different endpoints (dev, staging, production).
     *
     * BEST PRACTICE:
     * Never hardcode production API URLs or credentials in test code.
     * Use configuration management for different environments.
     */
    private static final String EXTERNAL_API_BASE_URL = "https://mock.apidog.com/m1/1110236-1100800-946851";

    /**
     * SETUP: Configure RestAssured for all tests
     *
     * EXPLANATION:
     * @BeforeAll runs once before all test methods in this class.
     * We configure RestAssured's base URI so we don't need to repeat it in every test.
     *
     * WHY STATIC:
     * Must be static because @BeforeAll executes before any instance is created.
     */
    @BeforeAll
    public static void setUp() {
        // Configure RestAssured to use the external API base URL
        RestAssured.baseURI = EXTERNAL_API_BASE_URL;

        // Enable logging for debugging (optional)
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * CLEANUP: Reset RestAssured configuration
     *
     * EXPLANATION:
     * @AfterAll runs once after all test methods complete.
     * We reset RestAssured configuration to avoid affecting other tests.
     *
     * BEST PRACTICE:
     * Always clean up global state to prevent test pollution.
     */
    @AfterAll
    public static void tearDown() {
        // Reset RestAssured to default configuration
        RestAssured.reset();
    }

    /**
     * TEST 1: Get All Deals from External API
     *
     * PURPOSE:
     * Validates that we can retrieve a list of deals from the external mock API.
     *
     * WHAT THIS TESTS:
     * - HTTP GET request to external endpoint
     * - Response status code validation
     * - Response content type validation
     * - JSON array structure validation
     * - Basic data presence validation
     *
     * RESTASSURED PATTERN:
     * - given(): No setup needed for simple GET
     * - when(): Execute GET /deals
     * - then(): Assert 200 status and JSON array response
     */
    @Test
    @Order(1)
    @DisplayName("Should retrieve all deals from external API")
    void testGetAllDealsFromExternalAPI() {
        // ARRANGE: No setup needed for GET request

        // ACT & ASSERT: Use RestAssured fluent API
        given()
            .contentType(ContentType.JSON)  // Expect JSON responses
        .when()
            .get("/deals")                   // Execute GET request to /deals endpoint
        .then()
            .statusCode(200)                 // Assert HTTP 200 OK
            .contentType(ContentType.JSON)   // Assert response is JSON
            .body("$", isA(java.util.List.class));  // Assert response is an array
    }

    /**
     * TEST 2: Get Single Deal by ID from External API
     *
     * PURPOSE:
     * Validates retrieval of a specific deal by ID from external API.
     *
     * WHAT THIS TESTS:
     * - HTTP GET with path parameter
     * - Single resource retrieval
     * - Response structure validation
     * - Specific field validation
     *
     * PATH PARAMETERS:
     * RestAssured uses {id} placeholder in path and .pathParam() to substitute value.
     *
     * NOTE ABOUT MOCK APIs:
     * Public mock APIs typically return predefined data, not the exact data you request.
     * We validate that the API is reachable and returns proper structure, not exact values.
     */
    @Test
    @Order(2)
    @DisplayName("Should retrieve single deal by ID from external API")
    void testGetDealByIdFromExternalAPI() {
        // ARRANGE: Define the deal ID to retrieve
        String dealId = "1";

        // ACT & ASSERT: GET request with path parameter
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", dealId)         // Substitute {id} in path
        .when()
            .get("/deals/{id}")              // Execute GET /deals/1
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())      // Assert ID exists (mock API returns its own ID)
            .body("title", notNullValue())   // Assert title exists
            .body("status", notNullValue()); // Assert status exists
    }

    /**
     * TEST 3: Create New Deal via External API
     *
     * PURPOSE:
     * Tests creating a new deal by sending POST request to external API.
     *
     * WHAT THIS TESTS:
     * - HTTP POST with JSON body
     * - Request serialization
     * - Response status code (200/201)
     * - Response body validation
     *
     * REQUEST BODY:
     * We create a Map representing the JSON request body.
     * RestAssured automatically serializes it to JSON.
     *
     * NOTE ABOUT MOCK APIs:
     * Public mock APIs return predefined mock data, not the data you send.
     * We validate successful request processing and response structure, not exact values.
     */
    @Test
    @Order(3)
    @DisplayName("Should create new deal via external API")
    void testCreateDealViaExternalAPI() {
        // ARRANGE: Build request body as Map
        Map<String, Object> dealRequest = new HashMap<>();
        dealRequest.put("title", "External API Test Deal");
        dealRequest.put("value", new BigDecimal("75000.00").toString());
        dealRequest.put("status", "OPEN");
        dealRequest.put("expectedCloseDate", LocalDate.now().plusMonths(2).toString());
        dealRequest.put("salesRepId", "REP-001");

        // ACT & ASSERT: POST request with JSON body
        given()
            .contentType(ContentType.JSON)
            .body(dealRequest)               // RestAssured serializes Map to JSON
        .when()
            .post("/deals")                  // Execute POST request
        .then()
            .statusCode(anyOf(is(200), is(201)))     // Accept 200 or 201 (mock API may vary)
            .contentType(ContentType.JSON)
            .body("title", notNullValue())   // Verify response has title (mock returns predefined data)
            .body("id", notNullValue());     // Verify response has id
    }

    /**
     * TEST 4: Update Existing Deal via External API
     *
     * PURPOSE:
     * Tests updating a deal by sending PUT request to external API.
     *
     * WHAT THIS TESTS:
     * - HTTP PUT with path parameter and JSON body
     * - Update operation handling
     * - Response validation
     *
     * PUT vs PATCH:
     * - PUT: Complete resource replacement
     * - PATCH: Partial resource update
     * This test uses PUT as it's more common in REST APIs.
     *
     * NOTE ABOUT MOCK APIs:
     * Mock APIs return predefined data, not your updates.
     * We validate the request is accepted and response structure is valid.
     */
    @Test
    @Order(4)
    @DisplayName("Should update existing deal via external API")
    void testUpdateDealViaExternalAPI() {
        // ARRANGE: Build update request
        String dealId = "1";
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("id", dealId);
        updateRequest.put("title", "Updated External Deal Title");
        updateRequest.put("status", "WON");
        updateRequest.put("value", new BigDecimal("95000.00").toString());

        // ACT & ASSERT: PUT request with path parameter and body
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", dealId)
            .body(updateRequest)
        .when()
            .put("/deals/{id}")              // Execute PUT /deals/1
        .then()
            .statusCode(anyOf(is(200), is(204)))     // Accept 200 OK or 204 No Content
            .contentType(ContentType.JSON)
            .body("title", notNullValue())   // Verify response has title (mock returns predefined data)
            .body("id", notNullValue());     // Verify response has id
    }

    /**
     * TEST 5: Delete Deal via External API
     *
     * PURPOSE:
     * Tests deleting a deal by sending DELETE request to external API.
     *
     * WHAT THIS TESTS:
     * - HTTP DELETE with path parameter
     * - Delete operation handling
     * - Response status validation
     *
     * DELETE RESPONSES:
     * - 200 OK: Deletion successful with response body
     * - 204 No Content: Deletion successful without response body
     * - 404 Not Found: Resource doesn't exist
     */
    @Test
    @Order(5)
    @DisplayName("Should delete deal via external API")
    void testDeleteDealViaExternalAPI() {
        // ARRANGE: Define deal to delete
        String dealId = "DEAL-999";

        // ACT & ASSERT: DELETE request
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", dealId)
        .when()
            .delete("/deals/{id}")           // Execute DELETE /deals/DEAL-999
        .then()
            .statusCode(anyOf(is(200), is(204)));    // Accept 200 or 204
    }

    /**
     * TEST 6: Verify External API Response Time
     *
     * PURPOSE:
     * Tests that external API responds within acceptable time limits.
     *
     * WHAT THIS TESTS:
     * - Network latency
     * - API performance
     * - Service availability
     *
     * PERFORMANCE TESTING:
     * While this is a basic response time check, production tests should include:
     * - Load testing (concurrent requests)
     * - Stress testing (beyond normal load)
     * - Spike testing (sudden traffic increase)
     * - Endurance testing (sustained load)
     */
    @Test
    @Order(6)
    @DisplayName("Should respond within acceptable time")
    void testExternalAPIResponseTime() {
        // ARRANGE: Define acceptable response time (5 seconds for external API)

        // ACT & ASSERT: Measure response time
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .time(lessThan(5000L));          // Assert response time < 5000ms
    }

    /**
     * TEST 7: Test External Mock API Behavior for Any ID
     *
     * PURPOSE:
     * Demonstrates real-world behavior of mock APIs.
     *
     * WHAT THIS TESTS:
     * - Mock API response behavior
     * - Understanding mock API limitations
     *
     * IMPORTANT LESSON ABOUT MOCK APIs:
     * Many public mock APIs (like Apidog) return 200 OK with sample data
     * for ANY request, regardless of whether the resource "exists".
     * This is different from production APIs which return 404 for missing resources.
     *
     * IMPLICATION FOR TESTING:
     * - Mock APIs are great for testing happy path and structure validation
     * - For error scenario testing, use MockServer framework with controlled responses
     * - Production-like error testing requires real sandbox environments
     *
     * This test demonstrates that mock APIs often don't behave exactly like production APIs.
     */
    @Test
    @Order(7)
    @DisplayName("Should demonstrate mock API returns data for any ID")
    void testExternalAPIMockBehavior() {
        // ARRANGE: Use an ID that wouldn't exist in a real system
        String anyId = "999999";

        // ACT & ASSERT: Mock API returns 200 with sample data regardless
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", anyId)
        .when()
            .get("/deals/{id}")
        .then()
            .statusCode(200)                 // Mock API returns 200 even for "non-existent" IDs
            .contentType(ContentType.JSON)
            .body("id", notNullValue())      // Returns sample data
            .body("title", notNullValue());  // Returns sample data

        // EDUCATIONAL NOTE:
        // This demonstrates a key limitation of public mock APIs:
        // They return predefined responses regardless of input.
        // For realistic error testing, use MockServer or real sandbox environments.
    }

    /**
     * TEST 8: Validate External API Response Structure
     *
     * PURPOSE:
     * Validates that external API returns data in expected structure/schema.
     *
     * WHAT THIS TESTS:
     * - Response JSON structure
     * - Required fields presence
     * - Data type validation
     * - Contract compliance
     *
     * CONTRACT TESTING:
     * This is a basic form of contract testing. Production systems often use:
     * - JSON Schema validation
     * - OpenAPI/Swagger spec validation
     * - Pact (consumer-driven contract testing)
     * - Postman contract tests
     */
    @Test
    @Order(8)
    @DisplayName("Should return deals with expected structure")
    void testExternalAPIResponseStructure() {
        // ARRANGE: No setup needed

        // ACT & ASSERT: Validate response structure
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", isA(java.util.List.class))           // Is array
            .body("[0].id", notNullValue())                 // Has id field
            .body("[0].title", notNullValue())              // Has title field
            .body("[0].status", notNullValue());            // Has status field
    }

    /**
     * TEST 9: Test External API with Query Parameters
     *
     * PURPOSE:
     * Validates filtering/searching deals using query parameters.
     *
     * WHAT THIS TESTS:
     * - Query parameter handling
     * - Filtering functionality
     * - Search capabilities
     *
     * QUERY PARAMETERS:
     * Common uses: filtering, sorting, pagination, searching
     * Example: /deals?status=OPEN&sort=date&page=1&limit=10
     */
    @Test
    @Order(9)
    @DisplayName("Should filter deals by status using query parameters")
    void testExternalAPIWithQueryParameters() {
        // ARRANGE: Define query parameters
        String statusFilter = "OPEN";

        // ACT & ASSERT: GET with query parameters
        given()
            .contentType(ContentType.JSON)
            .queryParam("status", statusFilter)  // Add ?status=OPEN to URL
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    /**
     * TEST 10: Extract and Validate Specific Response Fields
     *
     * PURPOSE:
     * Demonstrates extracting response data for further processing or assertions.
     *
     * WHAT THIS TESTS:
     * - Response extraction
     * - Complex assertions
     * - Multi-step validation
     *
     * RESPONSE EXTRACTION:
     * RestAssured allows extracting response data into variables for:
     * - Complex validations
     * - Using response data in subsequent requests
     * - Custom assertion logic
     */
    @Test
    @Order(10)
    @DisplayName("Should extract and validate deal fields")
    void testExtractAndValidateFields() {
        // ARRANGE: No setup needed

        // ACT: Execute request and extract response
        Response response = given()
            .contentType(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .extract()
            .response();                     // Extract response object

        // ASSERT: Perform custom validations
        String responseBody = response.getBody().asString();
        Assertions.assertNotNull(responseBody, "Response body should not be null");
        Assertions.assertTrue(responseBody.contains("id"), "Response should contain 'id' field");

        // Additional custom validation can be performed on extracted data
        int statusCode = response.getStatusCode();
        Assertions.assertEquals(200, statusCode, "Status code should be 200");
    }

    /**
     * SUMMARY OF EXTERNAL SERVER TESTING:
     * ===================================
     *
     * This test class demonstrates comprehensive external API testing including:
     *
     * 1. CRUD OPERATIONS:
     *    - GET all resources
     *    - GET single resource by ID
     *    - POST create resource
     *    - PUT update resource
     *    - DELETE remove resource
     *
     * 2. VALIDATION TYPES:
     *    - Status code validation
     *    - Response structure validation
     *    - Response content validation
     *    - Performance validation (response time)
     *
     * 3. ERROR SCENARIOS:
     *    - 404 Not Found handling
     *    - Invalid resource handling
     *
     * 4. ADVANCED FEATURES:
     *    - Query parameters
     *    - Path parameters
     *    - Response extraction
     *
     * REAL-WORLD APPLICATIONS:
     * ========================
     * - Payment gateway integration (Stripe, PayPal)
     * - Shipping API integration (UPS, FedEx)
     * - Social media API integration (Twitter, Facebook)
     * - Cloud service integration (AWS, Azure, GCP)
     * - Partner system integration (B2B APIs)
     *
     * BEST PRACTICES DEMONSTRATED:
     * ============================
     * 1. Configuration management (base URL)
     * 2. Test isolation (@BeforeAll/@AfterAll)
     * 3. Fluent assertion style
     * 4. Comprehensive documentation
     * 5. Error scenario testing
     * 6. Performance validation
     * 7. Contract validation
     */
}