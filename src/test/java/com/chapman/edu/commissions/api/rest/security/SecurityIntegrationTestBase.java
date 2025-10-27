package com.chapman.edu.commissions.api.rest.security;

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
 * Base class for security-related integration tests.
 *
 * This class demonstrates:
 * - Test Fixture Pattern with security-enabled server
 * - Helper methods for authenticated HTTP requests
 * - Support for multiple authentication schemes
 * - Header manipulation for testing
 *
 * Test Infrastructure:
 * - Starts server on test port with security enabled
 * - Provides authentication helper methods
 * - Supports Basic, Bearer, and API Key authentication
 */
public abstract class SecurityIntegrationTestBase {

    protected static ApiServer server;
    protected static HttpClient httpClient;
    protected static final int TEST_PORT = 9998; // Different port from regular tests
    protected static final String BASE_URL = "http://localhost:" + TEST_PORT + "/api/v1";
    protected static final String BASE_URL_V2 = "http://localhost:" + TEST_PORT + "/api/v2";

    @BeforeAll
    public static void startServer() throws Exception {
        System.out.println("Starting security test server on port " + TEST_PORT);

        // Create and start server
        server = new ApiServer(TEST_PORT);
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start security test server", e);
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server to be ready
        Thread.sleep(2000);

        // Seed test users for authentication
        seedTestUsers();

        // Create HTTP client
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        System.out.println("Security test server started successfully");
    }

    /**
     * Seeds test users for authentication testing.
     */
    private static void seedTestUsers() {
        // Test user: john.doe / password123
        User john = new User();
        john.setUsername("john.doe");
        john.setEmail("john.doe@example.com");
        john.setRoles(java.util.Set.of(UserRole.SALES_REP));
        john.setActive(true);
        server.getUserRepository().save(john);

        // Test user: jane.admin / admin123
        User jane = new User();
        jane.setUsername("jane.admin");
        jane.setEmail("jane.admin@example.com");
        jane.setRoles(java.util.Set.of(UserRole.SYSTEM_ADMIN));
        jane.setActive(true);
        server.getUserRepository().save(jane);

        System.out.println("✓ Test users seeded for authentication");
    }

    @AfterAll
    public static void stopServer() throws Exception {
        if (server != null) {
            System.out.println("Stopping security test server");
            server.stop();
            System.out.println("Security test server stopped");
        }
    }

    // ========== Unauthenticated Requests ==========

    /**
     * Makes a GET request without authentication.
     */
    protected HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a POST request without authentication.
     */
    protected HttpResponse<String> post(String path, String jsonBody)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a PUT request without authentication.
     */
    protected HttpResponse<String> put(String path, String jsonBody)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a DELETE request without authentication.
     */
    protected HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ========== Basic Authentication ==========

    /**
     * Makes a GET request with Basic authentication.
     *
     * @param path URL path
     * @param username Username
     * @param password Password
     */
    protected HttpResponse<String> getWithBasicAuth(String path, String username, String password)
            throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a POST request with Basic authentication.
     */
    protected HttpResponse<String> postWithBasicAuth(String path, String jsonBody,
            String username, String password) throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a PUT request with Basic authentication.
     */
    protected HttpResponse<String> putWithBasicAuth(String path, String jsonBody,
            String username, String password) throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a DELETE request with Basic authentication.
     */
    protected HttpResponse<String> deleteWithBasicAuth(String path, String username, String password)
            throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ========== Bearer Token (JWT) Authentication ==========

    /**
     * Makes a GET request with Bearer token authentication.
     */
    protected HttpResponse<String> getWithBearerToken(String path, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a POST request with Bearer token authentication.
     */
    protected HttpResponse<String> postWithBearerToken(String path, String jsonBody, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ========== API Key Authentication ==========

    /**
     * Makes a GET request with API Key authentication.
     */
    protected HttpResponse<String> getWithApiKey(String path, String apiKey)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .header("Accept", "application/json")
                .header("X-API-Key", apiKey)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ========== V2 Endpoints (Require Authentication) ==========

    /**
     * Makes a GET request to V2 endpoint without authentication (should fail).
     */
    protected HttpResponse<String> getV2(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + path))
                .GET()
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a GET request to V2 endpoint with Basic authentication.
     */
    protected HttpResponse<String> getV2WithBasicAuth(String path, String username, String password)
            throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + path))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a POST request to V2 endpoint with Basic authentication.
     */
    protected HttpResponse<String> postV2WithBasicAuth(String path, String jsonBody,
            String username, String password) throws IOException, InterruptedException {
        String authHeader = createBasicAuthHeader(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", authHeader)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a GET request to V2 endpoint with Bearer token.
     */
    protected HttpResponse<String> getV2WithBearerToken(String path, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + path))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Makes a POST request to V2 endpoint with Bearer token.
     */
    protected HttpResponse<String> postV2WithBearerToken(String path, String jsonBody, String token)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL_V2 + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ========== Helper Methods ==========

    /**
     * Creates Basic authentication header value.
     *
     * @param username Username
     * @param password Password
     * @return Authorization header value (e.g., "Basic base64(user:pass)")
     */
    protected String createBasicAuthHeader(String username, String password) {
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
}