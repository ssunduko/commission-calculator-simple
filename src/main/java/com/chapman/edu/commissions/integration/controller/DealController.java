package com.chapman.edu.commissions.integration.controller;

import com.chapman.edu.commissions.integration.servlet.BaseServlet;
import com.chapman.edu.commissions.integration.servlet.JsonHelper;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * DealController - MVC Controller for Deal operations.
 *
 * This servlet acts as the Controller in the MVC pattern:
 * - Handles HTTP requests (presentation layer)
 * - Delegates business logic to Service layer
 * - Transforms requests/responses between HTTP and domain model
 * - Handles errors and returns appropriate HTTP status codes
 *
 * Layered Architecture:
 * Controller (this) -> Service (DealService) -> Repository (H2DealRepository) -> Database
 *
 * Demonstrates:
 * - Separation of Concerns: Controller only handles HTTP, no business logic
 * - Dependency Injection: Receives service via constructor
 * - Error handling and HTTP status code mapping
 * - RESTful API design patterns
 *
 * Endpoints:
 * - GET /api/v1/integration/deals - List all deals (with optional filters)
 * - GET /api/v1/integration/deals/{id} - Get specific deal
 * - POST /api/v1/integration/deals - Create new deal
 * - PUT /api/v1/integration/deals/{id} - Update existing deal
 * - DELETE /api/v1/integration/deals/{id} - Delete deal
 * - POST /api/v1/integration/deals/{id}/close - Close deal as WON
 *
 * Layer: Presentation Layer (Controller/View)
 */
public class DealController extends BaseServlet {

    private final DealService dealService;

    /**
     * Constructor with dependency injection.
     *
     * @param dealService The service for Deal business logic
     */
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    /**
     * Handle GET requests.
     * Supports filtering by status and salesRepId query parameters.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        // Check for special action endpoints
        if (dealId != null && request.getPathInfo().endsWith("/close")) {
            sendErrorResponse(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Use POST to close a deal");
            return;
        }

        if (dealId == null) {
            // GET /api/v1/integration/deals - List all deals
            handleGetAllDeals(request, response);
        } else {
            // GET /api/v1/integration/deals/{id} - Get specific deal
            handleGetDealById(dealId, response);
        }
    }

    /**
     * Handle POST requests.
     * Creates new deals or performs actions on existing deals.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Check for action endpoints like /deals/{id}/close
        if (pathInfo != null && pathInfo.contains("/close")) {
            String dealId = extractDealIdFromClosePath(pathInfo);
            handleCloseDeal(dealId, response);
            return;
        }

        // Regular POST - create new deal
        handleCreateDeal(request, response);
    }

    /**
     * Handle PUT requests to update an existing deal.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        if (dealId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Deal ID is required for update");
            return;
        }

        handleUpdateDeal(dealId, request, response);
    }

    /**
     * Handle DELETE requests to delete a deal.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        if (dealId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Deal ID is required for deletion");
            return;
        }

        handleDeleteDeal(dealId, response);
    }

    /**
     * Handles GET all deals with optional filtering.
     * Delegates to service layer for business logic.
     */
    private void handleGetAllDeals(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Deal> deals;

            // Apply filters based on query parameters
            String statusParam = request.getParameter("status");
            String salesRepIdParam = request.getParameter("salesRepId");

            if (statusParam != null && !statusParam.isEmpty()) {
                // Filter by status
                try {
                    DealStatus status = DealStatus.valueOf(statusParam.toUpperCase());
                    deals = dealService.getDealsByStatus(status);
                } catch (IllegalArgumentException e) {
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid status value: " + statusParam);
                    return;
                }
            } else if (salesRepIdParam != null && !salesRepIdParam.isEmpty()) {
                // Filter by sales rep
                deals = dealService.getDealsBySalesRep(salesRepIdParam);
            } else {
                // No filters - get all
                deals = dealService.getAllDeals();
            }

            sendJsonResponse(response, deals, HttpServletResponse.SC_OK);

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retrieving deals: " + e.getMessage());
        }
    }

    /**
     * Handles GET single deal by ID.
     */
    private void handleGetDealById(String dealId, HttpServletResponse response) throws IOException {
        try {
            Optional<Deal> deal = dealService.getDealById(dealId);

            if (deal.isPresent()) {
                sendJsonResponse(response, deal.get(), HttpServletResponse.SC_OK);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Deal not found: " + dealId);
            }

        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error retrieving deal: " + e.getMessage());
        }
    }

    /**
     * Handles POST to create a new deal.
     * Demonstrates validation error handling.
     */
    private void handleCreateDeal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Read and parse the request body
            String requestBody = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(requestBody, Deal.class);

            // Delegate to service layer for business logic and validation
            Deal createdDeal = dealService.createDeal(deal);

            // Return 201 Created with the saved deal
            sendJsonResponse(response, createdDeal, HttpServletResponse.SC_CREATED);

        } catch (IllegalArgumentException e) {
            // Business validation failed
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Validation error: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error creating deal: " + e.getMessage());
        }
    }

    /**
     * Handles PUT to update an existing deal.
     */
    private void handleUpdateDeal(String dealId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Read and parse the request body
            String requestBody = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(requestBody, Deal.class);

            // Delegate to service layer
            Deal updatedDeal = dealService.updateDeal(dealId, deal);

            // Return 200 OK with the updated deal
            sendJsonResponse(response, updatedDeal, HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            // Business validation or not found
            if (e.getMessage().contains("not found")) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Validation error: " + e.getMessage());
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error updating deal: " + e.getMessage());
        }
    }

    /**
     * Handles DELETE to remove a deal.
     */
    private void handleDeleteDeal(String dealId, HttpServletResponse response) throws IOException {
        try {
            boolean deleted = dealService.deleteDeal(dealId);

            if (deleted) {
                // Return 204 No Content (successful deletion, no body)
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Deal not found: " + dealId);
            }

        } catch (IllegalArgumentException e) {
            // Business rule violation (e.g., can't delete closed deal)
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT,
                    "Cannot delete deal: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error deleting deal: " + e.getMessage());
        }
    }

    /**
     * Handles POST to close a deal as WON.
     * Example of action-based endpoint (not just CRUD).
     */
    private void handleCloseDeal(String dealId, HttpServletResponse response) throws IOException {
        try {
            Deal closedDeal = dealService.closeDealAsWon(dealId);
            sendJsonResponse(response, closedDeal, HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT,
                        "Cannot close deal: " + e.getMessage());
            }
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error closing deal: " + e.getMessage());
        }
    }

    /**
     * Extracts deal ID from path like "/deals/DEAL-123/close".
     */
    private String extractDealIdFromClosePath(String pathInfo) {
        String[] parts = pathInfo.split("/");
        // Expected format: /deals/{id}/close or /{id}/close
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("close") && i > 0) {
                return parts[i - 1];
            }
        }
        return null;
    }
}