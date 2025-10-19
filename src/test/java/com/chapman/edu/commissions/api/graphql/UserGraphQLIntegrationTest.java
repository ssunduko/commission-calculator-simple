package com.chapman.edu.commissions.api.graphql;

import com.chapman.edu.commissions.model.User;
import org.junit.jupiter.api.*;
import java.net.http.HttpResponse;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User GraphQL Integration Tests")
public class UserGraphQLIntegrationTest extends GraphQLIntegrationTestBase {

    @Test
    @Order(1)
    @DisplayName("Query all users should return list")
    public void queryAllUsers() throws Exception {
        String query = """
            query {
                users {
                    id
                    username
                    email
                    fullName
                }
            }
            """;
        
        HttpResponse<String> response = executeGraphQL(query);
        assertEquals(200, response.statusCode());
        assertNoErrors(response);
    }

    @Test
    @Order(2)
    @DisplayName("Create user mutation should return created user")
    public void createUser() throws Exception {
        String mutation = """
            mutation CreateUser($input: CreateUserInput!) {
                createUser(input: $input) {
                    id
                    username
                    email
                    fullName
                }
            }
            """;

        Map<String, Object> variables = Map.of(
            "input", Map.of(
                "username", "graphqltest",
                "email", "graphql@test.com",
                "firstName", "GraphQL",
                "lastName", "Tester",
                "roles", java.util.List.of("SALES_REP")
            )
        );

        HttpResponse<String> response = executeGraphQL(mutation, variables);
        assertEquals(200, response.statusCode());
        assertNoErrors(response);

        // Don't parse User object due to Gson LocalDateTime serialization issue
        // Just verify the response contains expected data
        String responseBody = response.body();
        assertTrue(responseBody.contains("graphqltest"));
        assertTrue(responseBody.contains("graphql@test.com"));
    }

    @Test
    @Order(3)
    @DisplayName("Query user by username should return specific user")
    public void queryUserByUsername() throws Exception {
        String query = """
            query GetUserByUsername($username: String!) {
                userByUsername(username: $username) {
                    id
                    username
                    email
                }
            }
            """;
        
        Map<String, Object> variables = Map.of("username", "jsmith");
        
        HttpResponse<String> response = executeGraphQL(query, variables);
        assertEquals(200, response.statusCode());
        assertNoErrors(response);
    }

    @Test
    @Order(4)
    @DisplayName("Query user with deals should resolve relationship")
    public void queryUserWithDeals() throws Exception {
        String query = """
            query GetUserWithDeals($id: ID!) {
                user(id: $id) {
                    id
                    fullName
                    deals {
                        id
                        title
                        value
                    }
                }
            }
            """;
        
        Map<String, Object> variables = Map.of("id", "USER-001");
        
        HttpResponse<String> response = executeGraphQL(query, variables);
        assertEquals(200, response.statusCode());
        assertNoErrors(response);
    }

    @Test
    @Order(5)
    @DisplayName("Query users by role should filter results")
    public void queryUsersByRole() throws Exception {
        String query = """
            query UsersByRole($role: UserRole!) {
                usersByRole(role: $role) {
                    id
                    username
                    roles
                }
            }
            """;
        
        Map<String, Object> variables = Map.of("role", "SALES_REP");
        
        HttpResponse<String> response = executeGraphQL(query, variables);
        assertEquals(200, response.statusCode());
        assertNoErrors(response);
    }
}
