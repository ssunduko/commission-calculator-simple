package com.chapman.edu.commissions.api.graphql;

import graphql.schema.idl.SchemaPrinter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GraphQL Schema Servlet - Returns the GraphQL schema in SDL format.
 *
 * WHAT IS SDL?
 * -----------
 * SDL (Schema Definition Language) is GraphQL's type system language.
 * It defines:
 * - Types (Object, Scalar, Enum, Interface, Union)
 * - Queries and Mutations
 * - Field arguments and return types
 *
 * EXAMPLE SDL:
 * -----------
 * type User {
 *   id: ID!
 *   name: String!
 *   email: String
 * }
 *
 * type Query {
 *   user(id: ID!): User
 * }
 *
 * WHY EXPOSE SDL?
 * --------------
 * - Schema documentation
 * - Code generation tools
 * - Schema comparison and versioning
 * - Easier to read than introspection JSON
 *
 * USAGE:
 * -----
 * GET /schema - Returns SDL as text
 * GET /schema?format=json - Returns introspection JSON
 */
public class GraphQLSchemaServlet extends HttpServlet {

    private final GraphQLProvider graphQLProvider;

    public GraphQLSchemaServlet(GraphQLProvider graphQLProvider) {
        this.graphQLProvider = graphQLProvider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String format = request.getParameter("format");

        if ("json".equalsIgnoreCase(format)) {
            // Return introspection JSON
            sendIntrospectionJson(response);
        } else {
            // Return SDL (default)
            sendSchemaSDL(response);
        }
    }

    /**
     * Send schema in SDL (Schema Definition Language) format.
     *
     * SDL FORMAT:
     * ----------
     * Human-readable schema definition language.
     * Easy to read and understand.
     * Used by many GraphQL tools and generators.
     *
     * @param response HTTP response
     * @throws IOException if writing fails
     */
    private void sendSchemaSDL(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            // Print schema in SDL format
            SchemaPrinter schemaPrinter = new SchemaPrinter(
                    SchemaPrinter.Options.defaultOptions()
                            .includeScalarTypes(true)
                            .includeSchemaDefinition(true)
                            .includeDirectives(true)
            );

            String sdl = schemaPrinter.print(graphQLProvider.getGraphQL().getGraphQLSchema());

            response.getWriter().write("# Commission Calculator GraphQL Schema\n");
            response.getWriter().write("# Generated at: " + java.time.LocalDateTime.now() + "\n");
            response.getWriter().write("# \n");
            response.getWriter().write("# This schema defines the API for the Commission Calculator system.\n");
            response.getWriter().write("# \n\n");
            response.getWriter().write(sdl);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error generating schema: " + e.getMessage());
        }
    }

    /**
     * Send schema as introspection JSON.
     *
     * INTROSPECTION JSON:
     * ------------------
     * Machine-readable schema format.
     * Used by GraphQL tools for code generation.
     * Complete schema information including descriptions.
     *
     * @param response HTTP response
     * @throws IOException if writing fails
     */
    private void sendIntrospectionJson(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");

        // Return message directing to use introspection query
        String message = "{\n" +
                "  \"message\": \"For full schema introspection, use the GraphQL endpoint with an introspection query\",\n" +
                "  \"endpoint\": \"/graphql\",\n" +
                "  \"introspectionQuery\": \"{ __schema { types { name kind description } } }\",\n" +
                "  \"fullIntrospectionQuery\": \"See https://github.com/graphql/graphql-js/blob/main/src/utilities/getIntrospectionQuery.ts\"\n" +
                "}";

        response.getWriter().write(message);
    }
}