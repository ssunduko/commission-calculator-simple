package com.chapman.edu.commissions.integration.controller.mockserver;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * MOCK-SERVER TESTING - DealController (HTTP Endpoint Mocking)
 *
 * PURPOSE:
 * MockServer tests verify client-side behavior by mocking HTTP endpoints.
 * This is useful for testing how your application interacts with external APIs
 * or for testing client code that makes HTTP requests.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. MOCK-SERVER FRAMEWORK:
 *    - Start embedded MockServer on specific port
 *    - Define HTTP endpoint expectations
 *    - Mock HTTP responses (status, headers, body)
 *    - Verify HTTP requests made to mocked endpoints
 *    - Test client-side error handling
 *
 * 2. HTTP MOCKING PATTERNS:
 *    - Mock REST API endpoints
 *    - Define response templates
 *    - Simulate HTTP errors (404, 500, etc.)
 *    - Test timeouts and network failures
 *    - Verify request parameters and headers
 *
 * 3. CLIENT TESTING:
 *    - Test how client handles successful responses
 *    - Test error handling (4xx, 5xx)
 *    - Test JSON parsing
 *    - Test request construction
 *    - Test authentication headers
 *
 * 4. DIFFERENCE FROM OTHER TEST TYPES:
 *    - MockServer: Mock external HTTP endpoints
 *    - Mockito: Mock Java objects/dependencies
 *    - Integration: Test with real HTTP server
 *    - API: Test actual application endpoints
 *
 * LAYER: Controller Layer (Client-Side Testing)
 * TEST TYPE: Mock-Server Test (HTTP Endpoint Mocking)
 *
 * WHEN TO USE:
 * - Testing REST clients or HTTP consumers
 * - Simulating external API responses
 * - Testing error handling without external dependencies
 * - Testing client-side logic in isolation
 *
 * NOTE: MockServer is typically used to mock EXTERNAL APIs, not your own controller.
 * In this example, we demonstrate how to use MockServer to test a hypothetical
 * client that makes requests to a Deal API (simulating an external service).
 */
@DisplayName("MockServer Tests - DealController (HTTP Endpoint Mocking)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DealControllerMockServerTest {

    private static ClientAndServer mockServer;
    private static MockServerClient mockServerClient;

    // MockServer will run on this port
    private static final int MOCK_SERVER_PORT = 9999;
    private static final String MOCK_BASE_URL = "http://localhost:" + MOCK_SERVER_PORT;

    /**
     * START MOCK-SERVER
     *
     * Initializes MockServer before running tests.
     * MockServer creates a real HTTP server that listens on the specified port
     * and responds according to our expectations.
     */
    @BeforeAll
    static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
        mockServerClient = new MockServerClient("localhost", MOCK_SERVER_PORT);

        // Configure RestAssured to use MockServer
        RestAssured.baseURI = MOCK_BASE_URL;
        RestAssured.port = MOCK_SERVER_PORT;
        RestAssured.basePath = "/api/v1/integration";
    }

    /**
     * STOP MOCK-SERVER
     *
     * Cleanup: Stop MockServer after all tests complete
     */
    @AfterAll
    static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    /**
     * RESET EXPECTATIONS
     *
     * Clear all expectations between tests to ensure test isolation
     */
    @BeforeEach
    void resetExpectations() {
        mockServerClient.reset();
    }

    // ============================================================
    // MOCK-SERVER: SUCCESSFUL RESPONSES
    // ============================================================

    /**
     * TEST: Mock GET all deals
     *
     * MOCK-SERVER PATTERN:
     * 1. Define expected HTTP request (GET /api/v1/integration/deals)
     * 2. Define mocked HTTP response (200 OK with JSON array)
     * 3. Make request using RestAssured
     * 4. Verify response matches mocked response
     *
     * DEMONSTRATES:
     * - How to mock a GET endpoint
     * - How to return JSON array
     * - How to test client consumption of the response
     */
    @Test
    @Order(1)
    @DisplayName("MockServer: Should mock GET /deals with array response")
    void testMockGetAllDeals() {
        // Arrange: Define MockServer expectation
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        [
                            {
                                "id": "DEAL-mock-1",
                                "title": "Mocked Deal 1",
                                "status": "OPEN",
                                "salesRepId": "USER-123"
                            },
                            {
                                "id": "DEAL-mock-2",
                                "title": "Mocked Deal 2",
                                "status": "WON",
                                "salesRepId": "USER-456"
                            }
                        ]
                        """)
            );

        // Act & Assert: Client makes request to mocked endpoint
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(2))
            .body("[0].id", equalTo("DEAL-mock-1"))
            .body("[0].title", equalTo("Mocked Deal 1"))
            .body("[1].id", equalTo("DEAL-mock-2"))
            .body("[1].status", equalTo("WON"));
    }

    /**
     * TEST: Mock GET deal by ID
     *
     * DEMONSTRATES:
     * - Mocking endpoint with path parameters
     * - Returning single object response
     */
    @Test
    @Order(2)
    @DisplayName("MockServer: Should mock GET /deals/{id} with single object")
    void testMockGetDealById() {
        // Arrange: Mock specific deal endpoint
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals/DEAL-specific-123")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "id": "DEAL-specific-123",
                            "title": "Specific Mocked Deal",
                            "status": "OPEN",
                            "salesRepId": "USER-789",
                            "products": [
                                {
                                    "productId": "PROD-A",
                                    "productName": "Product A",
                                    "quantity": 3,
                                    "price": 5000.00
                                }
                            ]
                        }
                        """)
            );

        // Act & Assert
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/deals/DEAL-specific-123")
        .then()
            .statusCode(200)
            .body("id", equalTo("DEAL-specific-123"))
            .body("title", equalTo("Specific Mocked Deal"))
            .body("products", hasSize(1))
            .body("products[0].productName", equalTo("Product A"));
    }

    /**
     * TEST: Mock POST create deal
     *
     * DEMONSTRATES:
     * - Mocking POST endpoint
     * - Verifying request body
     * - Returning 201 Created
     */
    @Test
    @Order(3)
    @DisplayName("MockServer: Should mock POST /deals with 201 Created")
    void testMockCreateDeal() {
        // Arrange: Mock POST endpoint
        mockServerClient
            .when(
                request()
                    .withMethod("POST")
                    .withPath("/api/v1/integration/deals")
                    .withHeader("Content-Type", "application/json")
            )
            .respond(
                response()
                    .withStatusCode(201)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "id": "DEAL-created-999",
                            "title": "Newly Created Deal",
                            "status": "OPEN",
                            "salesRepId": "USER-new"
                        }
                        """)
            );

        // Act & Assert: Client posts to mocked endpoint
        String requestBody = """
            {
                "title": "Newly Created Deal",
                "status": "OPEN",
                "salesRepId": "USER-new"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
            .body("id", equalTo("DEAL-created-999"))
            .body("title", equalTo("Newly Created Deal"));
    }

    // ============================================================
    // MOCK-SERVER: ERROR RESPONSES
    // ============================================================

    /**
     * TEST: Mock 404 Not Found
     *
     * DEMONSTRATES:
     * - Simulating error responses
     * - Testing client error handling
     */
    @Test
    @Order(4)
    @DisplayName("MockServer: Should mock 404 Not Found")
    void testMock404NotFound() {
        // Arrange: Mock 404 response
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals/DEAL-nonexistent")
            )
            .respond(
                response()
                    .withStatusCode(404)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "error": "Deal not found",
                            "dealId": "DEAL-nonexistent"
                        }
                        """)
            );

        // Act & Assert: Client handles 404
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/deals/DEAL-nonexistent")
        .then()
            .statusCode(404)
            .body("error", equalTo("Deal not found"))
            .body("dealId", equalTo("DEAL-nonexistent"));
    }

    /**
     * TEST: Mock 400 Bad Request
     *
     * DEMONSTRATES:
     * - Simulating validation errors
     * - Testing error message parsing
     */
    @Test
    @Order(5)
    @DisplayName("MockServer: Should mock 400 Bad Request for invalid data")
    void testMock400BadRequest() {
        // Arrange: Mock validation error
        mockServerClient
            .when(
                request()
                    .withMethod("POST")
                    .withPath("/api/v1/integration/deals")
            )
            .respond(
                response()
                    .withStatusCode(400)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "error": "Validation failed",
                            "message": "Deal title is required and cannot be empty"
                        }
                        """)
            );

        // Act & Assert: Client handles validation error
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/deals")
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation failed"))
            .body("message", containsString("title"));
    }

    /**
     * TEST: Mock 500 Internal Server Error
     *
     * DEMONSTRATES:
     * - Simulating server errors
     * - Testing client resilience
     */
    @Test
    @Order(6)
    @DisplayName("MockServer: Should mock 500 Internal Server Error")
    void testMock500ServerError() {
        // Arrange: Mock server error
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals")
            )
            .respond(
                response()
                    .withStatusCode(500)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "error": "Internal server error",
                            "message": "Database connection failed"
                        }
                        """)
            );

        // Act & Assert: Client handles server error
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(500)
            .body("error", equalTo("Internal server error"));
    }

    // ============================================================
    // MOCK-SERVER: AUTHENTICATION
    // ============================================================

    /**
     * TEST: Mock authentication requirement
     *
     * DEMONSTRATES:
     * - Mocking authentication headers
     * - Testing 401 Unauthorized
     * - Verifying WWW-Authenticate header
     */
    @Test
    @Order(7)
    @DisplayName("MockServer: Should mock 401 Unauthorized for missing auth")
    void testMock401Unauthorized() {
        // Arrange: Mock 401 response for unauthenticated requests
        // NOTE: We don't specify Authorization header requirements, so this matches any request
        // In a real scenario, you'd configure MockServer to check for missing/invalid auth
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals")
                    // Not checking for Authorization header - matches all requests to this path
            )
            .respond(
                response()
                    .withStatusCode(401)
                    .withHeader("WWW-Authenticate", "Basic realm=\"API\"")
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        {
                            "error": "Authentication required"
                        }
                        """)
            );

        // Act & Assert: Client handles auth error
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(401)
            .header("WWW-Authenticate", "Basic realm=\"API\"")
            .body("error", equalTo("Authentication required"));
    }

    /**
     * TEST: Mock successful authentication
     *
     * DEMONSTRATES:
     * - Verifying auth headers sent by client
     * - Testing authenticated requests
     */
    @Test
    @Order(8)
    @DisplayName("MockServer: Should accept valid authentication")
    void testMockValidAuthentication() {
        // Arrange: Mock endpoint that requires auth
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals")
                    .withHeader("Authorization", "Basic dXNlcjpwYXNz") // user:pass
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("[]")
            );

        // Act & Assert: Client sends auth header
        given()
            .auth().preemptive().basic("user", "pass")
            .accept(ContentType.JSON)
        .when()
            .get("/deals")
        .then()
            .statusCode(200);
    }

    // ============================================================
    // MOCK-SERVER: ADVANCED PATTERNS
    // ============================================================

    /**
     * TEST: Mock with query parameters
     *
     * DEMONSTRATES:
     * - Mocking endpoints with query params
     * - Filtering behavior simulation
     */
    @Test
    @Order(9)
    @DisplayName("MockServer: Should mock endpoint with query parameters")
    void testMockQueryParameters() {
        // Arrange: Mock filtered endpoint
        mockServerClient
            .when(
                request()
                    .withMethod("GET")
                    .withPath("/api/v1/integration/deals")
                    .withQueryStringParameter("status", "OPEN")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody("""
                        [
                            {
                                "id": "DEAL-open-1",
                                "title": "Open Deal 1",
                                "status": "OPEN"
                            }
                        ]
                        """)
            );

        // Act & Assert: Client uses query params
        given()
            .queryParam("status", "OPEN")
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .body("$", hasSize(1))
            .body("[0].status", equalTo("OPEN"));
    }

    /**
     * TEST: Mock DELETE with verification
     *
     * DEMONSTRATES:
     * - Mocking DELETE endpoint
     * - Testing 204 No Content
     */
    @Test
    @Order(10)
    @DisplayName("MockServer: Should mock DELETE with 204 No Content")
    void testMockDelete() {
        // Arrange: Mock DELETE endpoint
        mockServerClient
            .when(
                request()
                    .withMethod("DELETE")
                    .withPath("/api/v1/integration/deals/DEAL-to-delete")
            )
            .respond(
                response()
                    .withStatusCode(204) // No Content
            );

        // Act & Assert: Client deletes
        given()
        .when()
            .delete("/deals/DEAL-to-delete")
        .then()
            .statusCode(204);
    }

    /**
     * KEY TAKEAWAYS - MOCK-SERVER TESTING:
     *
     * WHAT WE DEMONSTRATED:
     * ✓ Mocking HTTP endpoints without real server
     * ✓ Simulating successful responses (200, 201, 204)
     * ✓ Simulating error responses (400, 404, 500)
     * ✓ Testing authentication (401, WWW-Authenticate)
     * ✓ Mocking query parameters
     * ✓ Testing JSON request/response parsing
     * ✓ Verifying HTTP headers
     *
     * MOCK-SERVER BENEFITS:
     * - Test client code without external dependencies
     * - Simulate error conditions easily
     * - Control exact response timing
     * - Test edge cases (timeouts, malformed responses)
     * - Faster than integration tests
     * - Deterministic behavior
     *
     * WHEN TO USE MOCK-SERVER:
     * - Testing REST clients
     * - Simulating external APIs
     * - Testing error handling
     * - Contract testing
     * - Testing without external services
     *
     * DIFFERENCE FROM OTHER MOCKING:
     * - MockServer: Real HTTP server, mock endpoints
     * - Mockito: Mock Java objects
     * - Integration: Real application server
     * - API: Full application stack
     *
     * BEST PRACTICES:
     * - Define clear expectations
     * - Reset expectations between tests
     * - Test both success and error paths
     * - Verify request headers/params
     * - Use realistic response data
     * - Combine with integration tests
     */
}