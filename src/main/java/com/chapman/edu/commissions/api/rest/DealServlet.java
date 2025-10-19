package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet for handling Deal-related HTTP requests.
 *
 * This servlet implements a RESTful API for Deal resources following standard
 * HTTP conventions:
 * - GET /api/v1/deals - List all deals
 * - GET /api/v1/deals/{id} - Get specific deal
 * - POST /api/v1/deals - Create new deal
 * - PUT /api/v1/deals/{id} - Update existing deal
 * - DELETE /api/v1/deals/{id} - Delete deal
 *
 * Concepts demonstrated:
 * - RESTful API design principles
 * - HTTP method semantics (GET, POST, PUT, DELETE)
 * - Query parameter filtering
 * - Proper HTTP status codes (200, 201, 404, etc.)
 * - Separation of concerns (servlet handles HTTP, repository handles data)
 */
public class DealServlet extends BaseServlet {

    // Repository for managing Deal entities (Dependency Injection)
    private final Repository<Deal> dealRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param dealRepository The repository for Deal entities
     */
    public DealServlet(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    /**
     * Handle GET requests.
     * - Without ID: Return all deals (optionally filtered)
     * - With ID: Return specific deal
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        if (dealId == null) {
            // GET /api/v1/deals - List all deals
            handleGetAllDeals(request, response);
        } else {
            // GET /api/v1/deals/{id} - Get specific deal
            handleGetDealById(dealId, response);
        }
    }

    /**
     * Handle POST requests to create a new deal.
     * POST /api/v1/deals
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Read and parse the request body
            String requestBody = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(requestBody, Deal.class);

            // Save the deal (repository will generate ID if needed)
            Deal savedDeal = dealRepository.save(deal);

            // Return 201 Created with the saved deal
            sendJsonResponse(response, savedDeal, HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid deal data: " + e.getMessage());
        }
    }

    /**
     * Handle PUT requests to update an existing deal.
     * PUT /api/v1/deals/{id}
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        if (dealId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Deal ID is required for update");
            return;
        }

        try {
            // Check if deal exists
            Optional<Deal> existingDeal = dealRepository.findById(dealId);
            if (existingDeal.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                        "Deal not found: " + dealId);
                return;
            }

            // Read and parse the request body
            String requestBody = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(requestBody, Deal.class);

            // Ensure the ID matches the path parameter
            deal.setId(dealId);

            // Update the deal
            Deal updatedDeal = dealRepository.save(deal);

            // Return 200 OK with the updated deal
            sendJsonResponse(response, updatedDeal, HttpServletResponse.SC_OK);
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid deal data: " + e.getMessage());
        }
    }

    /**
     * Handle DELETE requests to delete a deal.
     * DELETE /api/v1/deals/{id}
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);

        if (dealId == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Deal ID is required for deletion");
            return;
        }

        boolean deleted = dealRepository.deleteById(dealId);

        if (deleted) {
            // Return 204 No Content (successful deletion, no body)
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Deal not found: " + dealId);
        }
    }

    /**
     * Handle GET all deals with optional filtering.
     * Supports query parameters:
     * - status: Filter by deal status (OPEN, WON, LOST, CANCELLED)
     * - salesRepId: Filter by sales representative ID
     */
    private void handleGetAllDeals(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Deal> deals = dealRepository.findAll();

        // Apply filters based on query parameters
        String statusParam = request.getParameter("status");
        String salesRepIdParam = request.getParameter("salesRepId");

        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                DealStatus status = DealStatus.valueOf(statusParam.toUpperCase());
                deals = deals.stream()
                        .filter(deal -> deal.getStatus() == status)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid status value: " + statusParam);
                return;
            }
        }

        if (salesRepIdParam != null && !salesRepIdParam.isEmpty()) {
            deals = deals.stream()
                    .filter(deal -> salesRepIdParam.equals(deal.getSalesRepId()))
                    .collect(Collectors.toList());
        }

        sendJsonResponse(response, deals, HttpServletResponse.SC_OK);
    }

    /**
     * Handle GET single deal by ID.
     */
    private void handleGetDealById(String dealId, HttpServletResponse response) throws IOException {
        Optional<Deal> deal = dealRepository.findById(dealId);

        if (deal.isPresent()) {
            sendJsonResponse(response, deal.get(), HttpServletResponse.SC_OK);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                    "Deal not found: " + dealId);
        }
    }
}