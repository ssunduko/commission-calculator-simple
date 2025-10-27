package com.chapman.edu.commissions.api.graphql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * GraphQL Servlet - HTTP endpoint for GraphQL API.
 *
 * WHAT IS A GRAPHQL SERVLET?
 * --------------------------
 * Unlike REST which uses multiple endpoints (/deals, /users, etc.),
 * GraphQL uses a SINGLE endpoint that handles all queries and mutations.
 *
 * Typically: POST /graphql
 *
 * REQUEST FORMAT:
 * --------------
 * GraphQL requests are JSON with this structure:
 * {
 *   "query": "query { deals { id title } }",
 *   "variables": { "id": "DEAL-001" },
 *   "operationName": "GetDeals"
 * }
 *
 * RESPONSE FORMAT:
 * ---------------
 * GraphQL responses always return 200 OK (even for errors):
 * {
 *   "data": { ... },      // Successful data
 *   "errors": [ ... ]     // Any errors that occurred
 * }
 *
 * WHY POST INSTEAD OF GET?
 * -----------------------
 * - Queries can be large and complex
 * - Variables are easier to send in request body
 * - Consistency (queries and mutations both use POST)
 * - Some servers support GET for queries only (not implemented here)
 *
 * GRAPHQL VS REST:
 * ---------------
 * REST:
 * - Multiple endpoints: GET /deals, POST /deals, GET /users
 * - Fixed response structure
 * - Over-fetching or under-fetching data
 *
 * GraphQL:
 * - Single endpoint: POST /graphql
 * - Client specifies exactly what data it needs
 * - Single request can fetch related data (deals + users + plans)
 *
 * INTROSPECTION:
 * -------------
 * GraphQL supports introspection - clients can query the schema itself:
 * query {
 *   __schema {
 *     types { name }
 *   }
 * }
 *
 * This enables tools like GraphiQL, GraphQL Playground, etc.
 */
public class GraphQLServlet extends HttpServlet {

    private final GraphQL graphQL;
    private final Gson gson;

    /**
     * Constructor with dependency injection.
     *
     * @param graphQLProvider The GraphQL provider with initialized schema
     */
    public GraphQLServlet(GraphQLProvider graphQLProvider) {
        this.graphQL = graphQLProvider.getGraphQL();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Handle POST requests - the primary GraphQL endpoint.
     *
     * FLOW:
     * ----
     * 1. Read JSON request body
     * 2. Parse query, variables, operationName
     * 3. Execute GraphQL query
     * 4. Return JSON response
     *
     * ERROR HANDLING:
     * --------------
     * - GraphQL errors are returned in the "errors" field
     * - HTTP 500 only for system errors (can't parse request, etc.)
     * - HTTP 200 for all valid GraphQL requests (even if query has errors)
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Step 1: Read request body
        String requestBody = readRequestBody(request);

        try {
            // Step 2: Parse GraphQL request
            @SuppressWarnings("unchecked")
            Map<String, Object> requestMap = gson.fromJson(requestBody, Map.class);

            String query = (String) requestMap.get("query");
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) requestMap.get("variables");
            String operationName = (String) requestMap.get("operationName");

            // Validate query
            if (query == null || query.trim().isEmpty()) {
                sendErrorResponse(response, "Query is required", 400);
                return;
            }

            // Step 3: Build execution input
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables != null ? variables : Map.of())
                    .operationName(operationName)
                    .build();

            // Step 4: Execute GraphQL query
            ExecutionResult executionResult = graphQL.execute(executionInput);

            // Step 5: Build response
            Map<String, Object> responseMap = executionResult.toSpecification();

            // Step 6: Send JSON response
            sendJsonResponse(response, responseMap, 200);

        } catch (Exception e) {
            // System error (not GraphQL error)
            System.err.println("GraphQL execution error: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle GET requests - for simple queries without variables.
     *
     * QUERY PARAMETER FORMAT:
     * ----------------------
     * GET /graphql?query={deals{id title}}
     *
     * This is less common than POST but useful for:
     * - Browser testing
     * - Caching (GET requests are cacheable)
     * - Simple queries
     *
     * LIMITATIONS:
     * -----------
     * - URL length limits
     * - Harder to send variables
     * - Mutations should not use GET (HTTP semantics)
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String query = request.getParameter("query");
        String variablesParam = request.getParameter("variables");
        String operationName = request.getParameter("operationName");

        if (query == null || query.trim().isEmpty()) {
            // No query provided - return introspection help
            sendHelpResponse(response);
            return;
        }

        try {
            // Parse variables if provided
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = variablesParam != null
                    ? gson.fromJson(variablesParam, Map.class)
                    : Map.of();

            // Build execution input
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(query)
                    .variables(variables)
                    .operationName(operationName)
                    .build();

            // Execute GraphQL query
            ExecutionResult executionResult = graphQL.execute(executionInput);

            // Build and send response
            Map<String, Object> responseMap = executionResult.toSpecification();
            sendJsonResponse(response, responseMap, 200);

        } catch (Exception e) {
            System.err.println("GraphQL execution error: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle OPTIONS requests for CORS preflight.
     *
     * CORS (Cross-Origin Resource Sharing):
     * -------------------------------------
     * Allows frontend apps on different domains to call this API.
     * Required for modern web applications.
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Read the request body as a string.
     *
     * @param request HTTP request
     * @return Request body as string
     * @throws IOException if reading fails
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    /**
     * Send JSON response to client.
     *
     * @param response HTTP response
     * @param data Data to serialize as JSON
     * @param statusCode HTTP status code
     * @throws IOException if writing fails
     */
    private void sendJsonResponse(HttpServletResponse response, Object data, int statusCode)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");

        String json = gson.toJson(data);
        response.getWriter().write(json);
    }

    /**
     * Send error response.
     *
     * @param response HTTP response
     * @param message Error message
     * @param statusCode HTTP status code
     * @throws IOException if writing fails
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode)
            throws IOException {
        Map<String, Object> errorResponse = Map.of(
                "errors", java.util.List.of(
                        Map.of("message", message)
                )
        );
        sendJsonResponse(response, errorResponse, statusCode);
    }

    /**
     * Send help response with API information.
     *
     * GRAPHQL ENDPOINT DOCUMENTATION:
     * ------------------------------
     * When accessing the endpoint without a query, provide helpful information
     * about how to use the API.
     *
     * @param response HTTP response
     * @throws IOException if writing fails
     */
    private void sendHelpResponse(HttpServletResponse response) throws IOException {
        Map<String, Object> helpResponse = Map.of(
                "message", "Commission Calculator GraphQL API",
                "endpoint", "/graphql",
                "methods", java.util.List.of("GET", "POST"),
                "example", Map.of(
                        "post", Map.of(
                                "url", "/graphql",
                                "method", "POST",
                                "body", Map.of(
                                        "query", "query { deals { id title value } }",
                                        "variables", Map.of()
                                )
                        ),
                        "get", Map.of(
                                "url", "/graphql?query={deals{id title}}",
                                "method", "GET"
                        )
                ),
                "introspection", Map.of(
                        "description", "Query the schema itself",
                        "query", "query { __schema { types { name } } }"
                ),
                "documentation", "See README.md for full API documentation"
        );
        sendJsonResponse(response, helpResponse, 200);
    }
}