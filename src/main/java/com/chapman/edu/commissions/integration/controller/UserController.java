package com.chapman.edu.commissions.integration.controller;

import com.chapman.edu.commissions.integration.servlet.BaseServlet;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * UserController - REST API Controller for User operations.
 *
 * Provides HTTP endpoints for user management operations.
 * This controller handles listing users and retrieving user details.
 *
 * Endpoints:
 * - GET /api/v1/integration/users - List all users
 * - GET /api/v1/integration/users/{id} - Get specific user
 *
 * Used by the web UI to populate the sales representative dropdown
 * when creating deals.
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class UserController extends BaseServlet {

    private final UserService userService;

    /**
     * Constructor with dependency injection.
     *
     * @param userService The service for User business logic
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handle GET requests.
     * Returns all users or a specific user by ID.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = extractResourceId(request);

        if (userId == null) {
            // GET /api/v1/integration/users - List all users
            handleGetAllUsers(response);
        } else {
            // GET /api/v1/integration/users/{id} - Get specific user
            handleGetUserById(userId, response);
        }
    }

    /**
     * Handles GET all users.
     */
    private void handleGetAllUsers(HttpServletResponse response) throws IOException {
        try {
            List<User> users = userService.getAllUsers();

            // Return 200 OK with users list
            sendJsonResponse(response, users, HttpServletResponse.SC_OK);

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retrieving users: " + e.getMessage());
        }
    }

    /**
     * Handles GET user by ID.
     */
    private void handleGetUserById(String userId, HttpServletResponse response) throws IOException {
        try {
            User user = userService.getUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            // Return 200 OK with user
            sendJsonResponse(response, user, HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retrieving user: " + e.getMessage());
        }
    }
}