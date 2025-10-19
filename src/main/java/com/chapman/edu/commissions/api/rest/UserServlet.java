package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet for handling User-related HTTP requests.
 *
 * Implements RESTful endpoints for User management:
 * - GET /api/v1/users - List all users
 * - GET /api/v1/users/{id} - Get specific user
 * - POST /api/v1/users - Create new user
 * - PUT /api/v1/users/{id} - Update existing user
 * - DELETE /api/v1/users/{id} - Delete user
 *
 * Demonstrates consistent REST API patterns across different resources.
 */
public class UserServlet extends BaseServlet {

    private final Repository<User> userRepository;

    public UserServlet(Repository<User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = extractResourceId(request);

        if (userId == null) {
            handleGetAllUsers(request, response);
        } else {
            handleGetUserById(userId, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String requestBody = readRequestBody(request);
            User user = JsonHelper.fromJson(requestBody, User.class);

            User savedUser = userRepository.save(user);
            sendJsonResponse(response, savedUser, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid user data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = extractResourceId(request);

        if (userId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "User ID is required for update");
            return;
        }

        try {
            Optional<User> existingUser = userRepository.findById(userId);
            if (existingUser.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "User not found: " + userId);
                return;
            }

            String requestBody = readRequestBody(request);
            User user = JsonHelper.fromJson(requestBody, User.class);
            user.setId(userId);

            User updatedUser = userRepository.save(user);
            sendJsonResponse(response, updatedUser, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid user data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = extractResourceId(request);

        if (userId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "User ID is required for deletion");
            return;
        }

        boolean deleted = userRepository.deleteById(userId);

        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "User not found: " + userId);
        }
    }

    /**
     * Handle GET all users with optional role filtering.
     */
    private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> users = userRepository.findAll();

        String roleParam = request.getParameter("role");
        if (roleParam != null && !roleParam.isEmpty()) {
            try {
                UserRole role = UserRole.valueOf(roleParam.toUpperCase());
                users = users.stream()
                        .filter(user -> user.getRoles().contains(role))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid role value: " + roleParam);
                return;
            }
        }

        sendJsonResponse(response, users, HttpServletResponse.SC_OK);
    }

    private void handleGetUserById(String userId, HttpServletResponse response) throws IOException {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            sendJsonResponse(response, user.get(), HttpServletResponse.SC_OK);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "User not found: " + userId);
        }
    }
}
