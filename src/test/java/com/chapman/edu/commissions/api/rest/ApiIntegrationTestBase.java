package com.chapman.edu.commissions.api.rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Base class for API integration tests.
 *
 * This class demonstrates the Test Fixture pattern by providing:
 * - Setup and teardown of embedded Tomcat server
 * - Shared HTTP client for all tests
 * - Helper methods for making HTTP requests
 * - Common test infrastructure
 *
 * Concepts demonstrated:
 * - Test Fixture Pattern: Shared setup/teardown for tests
 * - Integration Testing: Testing complete HTTP request/response cycle
 * - Resource Management: Proper server lifecycle management
 * - Test Isolation: Each test class gets fresh server instance
 */
public abstract class ApiIntegrationTestBase {

    // Shared server instance for all tests in the class
    protected static ApiServer server;

    // HTTP client for making requests
    protected static HttpClient httpClient;

    // Server configuration
    protected static final int TEST_PORT = 9999;
    protected static final String BASE_URL = "http://localhost:" + TEST_PORT + "/api/v1";

    /**
     * Start the server before any tests run.
     * This method runs once per test class.
     *
     * @BeforeAll ensures this runs before all test methods
     */
    @BeforeAll
    public static void startServer() throws Exception {
        System.out.println("Starting test server on port " + TEST_PORT);

        // Create server instance with test port
        server = new ApiServer(TEST_PORT);

        // Start server in background thread to avoid blocking
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start test server", e);
            }
        });
        serverThread.setDaemon(true);  // Thread dies when JVM exits
        serverThread.start();

        // Wait for server to be ready
        Thread.sleep(2000);

        // Create HTTP client for making requests
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        System.out.println("Test server started successfully");
    }

    /**
     * Stop the server after all tests complete.
     * This method runs once per test class.
     *
     * @AfterAll ensures this runs after all test methods
     */
    @AfterAll
    public static void stopServer() throws Exception {
        if (server != null) {
            System.out.println("Stopping test server");
            server.stop();
            System.out.println("Test server stopped");
        }
    }

    /**
     * Helper method to make GET request.
     *
     * @param path URL path (e.g., "/deals" or "/deals/DEAL-001")
     * @return HTTP response
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
     * Helper method to make POST request.
     *
     * @param path URL path
     * @param jsonBody Request body as JSON string
     * @return HTTP response
     */
    protected HttpResponse<String> post(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Helper method to make PUT request.
     *
     * @param path URL path
     * @param jsonBody Request body as JSON string
     * @return HTTP response
     */
    protected HttpResponse<String> put(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Helper method to make DELETE request.
     *
     * @param path URL path
     * @return HTTP response
     */
    protected HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE()
                .header("Accept", "application/json")
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Parse JSON response body into an object.
     *
     * @param response HTTP response
     * @param clazz Class to deserialize into
     * @return Deserialized object
     */
    protected <T> T parseResponse(HttpResponse<String> response, Class<T> clazz) {
        return JsonHelper.fromJson(response.body(), clazz);
    }

    /**
     * Assert that response has expected status code.
     *
     * @param response HTTP response
     * @param expectedStatus Expected HTTP status code
     */
    protected void assertStatus(HttpResponse<String> response, int expectedStatus) {
        if (response.statusCode() != expectedStatus) {
            throw new AssertionError(
                    String.format("Expected status %d but got %d. Response body: %s",
                            expectedStatus, response.statusCode(), response.body()));
        }
    }

    /**
     * Get base URL for building full URLs.
     *
     * @return Base URL string
     */
    protected String getBaseUrl() {
        return BASE_URL;
    }
}
