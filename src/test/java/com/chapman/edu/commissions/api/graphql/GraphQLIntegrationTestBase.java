package com.chapman.edu.commissions.api.graphql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Base class for GraphQL integration tests.
 * Provides shared test infrastructure including server lifecycle and HTTP client.
 */
public abstract class GraphQLIntegrationTestBase {

    protected static GraphQLServer server;
    protected static HttpClient httpClient;
    protected static final int TEST_PORT = 9998;
    protected static final String BASE_URL = "http://localhost:" + TEST_PORT;
    protected static final String GRAPHQL_ENDPOINT = BASE_URL + "/graphql";
    protected static Gson gson;

    @BeforeAll
    public static void startServer() throws Exception {
        System.out.println("Starting test server on port " + TEST_PORT);
        server = new GraphQLServer(TEST_PORT);
        
        Thread serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();
        
        Thread.sleep(2000);
        
        httpClient = HttpClient.newBuilder().build();
        gson = new GsonBuilder().setPrettyPrinting().create();
        
        System.out.println("Test server started successfully");
    }

    @AfterAll
    public static void stopServer() throws Exception {
        System.out.println("Stopping test server");
        server.stop();
        System.out.println("Test server stopped successfully");
    }

    protected HttpResponse<String> executeGraphQL(String query) throws Exception {
        return executeGraphQL(query, null);
    }

    protected HttpResponse<String> executeGraphQL(String query, Map<String, Object> variables) throws Exception {
        Map<String, Object> requestBody = variables != null
                ? Map.of("query", query, "variables", variables)
                : Map.of("query", query);
        
        String jsonBody = gson.toJson(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GRAPHQL_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();
        
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected <T> T parseData(HttpResponse<String> response, String dataKey, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = gson.fromJson(response.body(), Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        if (data == null) {
            Object errors = responseMap.get("errors");
            throw new AssertionError("GraphQL query failed - no data returned. Errors: " + errors);
        }
        Object value = data.get(dataKey);
        if (value == null) {
            return null;
        }
        return gson.fromJson(gson.toJson(value), clazz);
    }

    protected void assertNoErrors(HttpResponse<String> response) {
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = gson.fromJson(response.body(), Map.class);
        Object errors = responseMap.get("errors");
        if (errors != null) {
            throw new AssertionError("GraphQL query returned errors: " + errors);
        }
    }
}
