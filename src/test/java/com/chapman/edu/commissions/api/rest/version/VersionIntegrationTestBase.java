package com.chapman.edu.commissions.api.rest.version;

import com.chapman.edu.commissions.api.rest.ApiServer;
import com.chapman.edu.commissions.api.rest.JsonHelper;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Base class for API versioning integration tests.
 *
 * This class demonstrates:
 * - Testing multiple API versions
 * - Version-specific endpoints
 * - Version header validation
 * - Path-based versioning testing
 *
 * Test Infrastructure:
 * - Provides methods for calling different API versions
 * - Validates version-specific behavior
 * - Tests backward compatibility
 */
public abstract class VersionIntegrationTestBase {

    protected static ApiServer server;
    protected static HttpClient httpClient;
    protected static final int TEST_PORT = 9997; // Different port for version tests
    protected static final String BASE_URL = "http://localhost:" + TEST_PORT + "/api";

    @BeforeAll
    public static void startServer() throws Exception {
        System.out.println("Starting version test server on port " + TEST_PORT);

        // Create and start server
        server = new ApiServer(TEST_PORT);
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start version test server", e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server to be ready
        Thread.sleep(2000);

        // Seed test users for V2 authentication
        seedTestUsers();

        // Create HTTP client
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        System.out.println("Version test server started successfully");
    }

    /**
     * Seeds test users for V2 authentication testing.
     */
    private static void seedTestUsers() {
        // Test user: jsmith / password
        User jsmith = new User();
        jsmith.setUsername("jsmith");
        jsmith.setEmail("jsmith@company.com");
        jsmith.setRoles(java.util.Set.of(UserRole.SALES_REP));
        jsmith.setActive(true);
        server.getUserRepository().save(jsmith);

        System.out.println("✓ Test users seeded for V2 authentication");
    }

    @AfterAll
    public static void stopServer() throws Exception {
        if (server != null) {
            System.out.println("Stopping version test server");
            server.stop();
            System.out.println("Version test server stopped");
        }
    }

    // ========== Version 1 (V1) Requests ==========

    /**
     * Makes a GET request to V1 endpoint.
     */
    protected HttpResponse<String> getV1(String path) throws IOException, InterruptedException {
        return makeRequest("GET", "/v1" + path, null);
    }

    /**
     * Makes a POST request to V1 endpoint.
     */
    protected HttpResponse<String> postV1(String path, String jsonBody)
            throws IOException, InterruptedException {
        return makeRequest("POST", "/v1" + path, jsonBody);
    }

    /**
     * Makes a PUT request to V1 endpoint.
     */
    protected HttpResponse<String> putV1(String path, String jsonBody)
            throws IOException, InterruptedException {
        return makeRequest("PUT", "/v1" + path, jsonBody);
    }

    /**
     * Makes a DELETE request to V1 endpoint.
     */
    protected HttpResponse<String> deleteV1(String path) throws IOException, InterruptedException {
        return makeRequest("DELETE", "/v1" + path, null);
    }

    // ========== Version 2 (V2) Requests ==========

    /**
     * Makes a GET request to V2 endpoint.
     */
    protected HttpResponse<String> getV2(String path) throws IOException, InterruptedException {
        return makeRequest("GET", "/v2" + path, null);
    }

    /**
     * Makes a POST request to V2 endpoint.
     */
    protected HttpResponse<String> postV2(String path, String jsonBody)
            throws IOException, InterruptedException {
        return makeRequest("POST", "/v2" + path, jsonBody);
    }

    /**
     * Makes a PUT request to V2 endpoint.
     */
    protected HttpResponse<String> putV2(String path, String jsonBody)
            throws IOException, InterruptedException {
        return makeRequest("PUT", "/v2" + path, jsonBody);
    }

    /**
     * Makes a DELETE request to V2 endpoint.
     */
    protected HttpResponse<String> deleteV2(String path) throws IOException, InterruptedException {
        return makeRequest("DELETE", "/v2" + path, null);
    }

    // ========== V2 Authenticated Requests (Required for V2) ==========

    /**
     * Makes a GET request to V2 endpoint with Basic authentication.
     * V2 endpoints REQUIRE authentication.
     */
    protected HttpResponse<String> getV2WithAuth(String path, String username, String password)
            throws IOException, InterruptedException {
        return makeAuthenticatedRequest("GET", "/v2" + path, null, username, password);
    }

    /**
     * Makes a POST request to V2 endpoint with Basic authentication.
     * V2 endpoints REQUIRE authentication.
     */
    protected HttpResponse<String> postV2WithAuth(String path, String jsonBody, String username, String password)
            throws IOException, InterruptedException {
        return makeAuthenticatedRequest("POST", "/v2" + path, jsonBody, username, password);
    }

    /**
     * Makes a PUT request to V2 endpoint with Basic authentication.
     * V2 endpoints REQUIRE authentication.
     */
    protected HttpResponse<String> putV2WithAuth(String path, String jsonBody, String username, String password)
            throws IOException, InterruptedException {
        return makeAuthenticatedRequest("PUT", "/v2" + path, jsonBody, username, password);
    }

    /**
     * Makes a DELETE request to V2 endpoint with Basic authentication.
     * V2 endpoints REQUIRE authentication.
     */
    protected HttpResponse<String> deleteV2WithAuth(String path, String username, String password)
            throws IOException, InterruptedException {
        return makeAuthenticatedRequest("DELETE", "/v2" + path, null, username, password);
    }

    // ========== Version 3 (V3) Requests (Future) ==========

    /**
     * Makes a GET request to V3 endpoint (experimental).
     */
    protected HttpResponse<String> getV3(String path) throws IOException, InterruptedException {
        return makeRequest("GET", "/v3" + path, null);
    }

    // ========== Helper Methods ==========

    /**
     * Makes an HTTP request with specified method and body.
     */
    private HttpResponse<String> makeRequest(String method, String path, String jsonBody)
            throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json");

        if (jsonBody != null) {
            builder.header("Content-Type", "application/json");
        }

        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
                break;
            case "PUT":
                builder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes an authenticated HTTP request with Basic auth.
     */
    private HttpResponse<String> makeAuthenticatedRequest(String method, String path, String jsonBody,
            String username, String password) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .header("Authorization", createBasicAuthHeader(username, password));

        if (jsonBody != null) {
            builder.header("Content-Type", "application/json");
        }

        switch (method) {
            case "GET":
                builder.GET();
                break;
            case "POST":
                builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
                break;
            case "PUT":
                builder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));
                break;
            case "DELETE":
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Creates Basic authentication header value.
     */
    private String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encoded;
    }

    /**
     * Parses JSON response body into an object.
     */
    protected <T> T parseResponse(HttpResponse<String> response, Class<T> clazz) {
        return JsonHelper.fromJson(response.body(), clazz);
    }

    /**
     * Asserts that response has expected status code.
     */
    protected void assertStatus(HttpResponse<String> response, int expectedStatus) {
        if (response.statusCode() != expectedStatus) {
            throw new AssertionError(
                    String.format("Expected status %d but got %d. Response body: %s",
                            expectedStatus, response.statusCode(), response.body()));
        }
    }

    /**
     * Gets the value of a response header.
     */
    protected String getHeader(HttpResponse<String> response, String headerName) {
        return response.headers().firstValue(headerName).orElse(null);
    }

    /**
     * Checks if response body contains a specific string.
     */
    protected boolean responseContains(HttpResponse<String> response, String text) {
        return response.body() != null && response.body().contains(text);
    }

    /**
     * Gets the API-Version header value.
     */
    protected String getApiVersionHeader(HttpResponse<String> response) {
        return getHeader(response, "API-Version");
    }

    /**
     * Gets the Warning header value (used for deprecation warnings).
     */
    protected String getWarningHeader(HttpResponse<String> response) {
        return getHeader(response, "Warning");
    }

    /**
     * Checks if response indicates version is deprecated.
     */
    protected boolean isDeprecated(HttpResponse<String> response) {
        String warning = getWarningHeader(response);
        return warning != null && warning.toLowerCase().contains("deprecated");
    }

    /**
     * Checks if response indicates version is experimental.
     */
    protected boolean isExperimental(HttpResponse<String> response) {
        String warning = getWarningHeader(response);
        return warning != null && warning.toLowerCase().contains("experimental");
    }
}