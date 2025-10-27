package com.chapman.edu.commissions.api.rest.version;

import com.chapman.edu.commissions.api.rest.JsonHelper;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Version 2 implementation of the Deal endpoint.
 *
 * V2 Features (improvements over V1):
 * - All V1 features
 * - Pagination support (page, limit parameters)
 * - Advanced filtering (multiple criteria)
 * - Enhanced error responses with error codes
 * - Metadata in responses (total count, page info)
 * - Computed fields (commission estimates)
 *
 * This is the current stable version.
 */
public class DealEndpointV2 implements VersionedEndpoint {

    private final Repository<Deal> dealRepository;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Constructs a V2 Deal endpoint.
     *
     * @param dealRepository The deal repository
     */
    public DealEndpointV2(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            handleGetAll(request, response);
        } else {
            handleGetById(resourceId, response);
        }
    }

    @Override
    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(body, Deal.class);

            Deal savedDeal = dealRepository.save(deal);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setHeader("Location", "/api/v2/deals/" + savedDeal.getId());
            sendJsonResponse(response, createEnhancedResponse(savedDeal));
        } catch (com.google.gson.JsonSyntaxException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_JSON", "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_DATA", "Invalid deal data: " + e.getMessage());
        }
    }

    @Override
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "MISSING_RESOURCE_ID", "Resource ID is required for update operations");
            return;
        }

        Optional<Deal> existing = dealRepository.findById(resourceId);
        if (existing.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "RESOURCE_NOT_FOUND", "Deal not found with ID: " + resourceId);
            return;
        }

        try {
            String body = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(body, Deal.class);

            Deal updatedDeal = dealRepository.save(deal);
            sendJsonResponse(response, createEnhancedResponse(updatedDeal));
        } catch (com.google.gson.JsonSyntaxException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_JSON", "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_DATA", "Invalid deal data: " + e.getMessage());
        }
    }

    @Override
    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "MISSING_RESOURCE_ID", "Resource ID is required for delete operations");
            return;
        }

        boolean deleted = dealRepository.deleteById(resourceId);
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "RESOURCE_NOT_FOUND", "Deal not found with ID: " + resourceId);
        }
    }

    @Override
    public ApiVersion getVersion() {
        return ApiVersion.V2;
    }

    /**
     * Handles GET all deals request with pagination and advanced filtering.
     * V2 Enhancement: Supports pagination and multiple filter criteria.
     */
    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Deal> deals = dealRepository.findAll();

        // Apply filters
        deals = applyFilters(deals, request);

        // Get pagination parameters
        int page = getIntParameter(request, "page", 1);
        int limit = getIntParameter(request, "limit", DEFAULT_PAGE_SIZE);

        // Validate pagination parameters
        if (page < 1) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_PARAMETER", "Page must be >= 1");
            return;
        }

        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "INVALID_PARAMETER", "Limit must be between 1 and " + MAX_PAGE_SIZE);
            return;
        }

        // Calculate pagination
        int totalCount = deals.size();
        int totalPages = (int) Math.ceil((double) totalCount / limit);
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, totalCount);

        // V2 Enhancement: Return paginated results with metadata
        List<Deal> paginatedDeals = deals.subList(startIndex, endIndex);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", paginatedDeals.stream()
            .map(this::createEnhancedResponse)
            .collect(Collectors.toList()));

        // V2 Enhancement: Include pagination metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("page", page);
        metadata.put("limit", limit);
        metadata.put("totalCount", totalCount);
        metadata.put("totalPages", totalPages);
        metadata.put("hasNext", page < totalPages);
        metadata.put("hasPrevious", page > 1);

        responseData.put("metadata", metadata);

        sendJsonResponse(response, responseData);
    }

    /**
     * V2 Enhancement: Advanced filtering with multiple criteria.
     */
    private List<Deal> applyFilters(List<Deal> deals, HttpServletRequest request) {
        // Status filter
        String statusParam = request.getParameter("status");
        if (statusParam != null) {
            try {
                DealStatus status = DealStatus.valueOf(statusParam.toUpperCase());
                deals = deals.stream()
                    .filter(deal -> deal.getStatus() == status)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
            }
        }

        // Sales rep filter
        String salesRepId = request.getParameter("salesRepId");
        if (salesRepId != null && !salesRepId.isEmpty()) {
            deals = deals.stream()
                .filter(deal -> salesRepId.equals(deal.getSalesRepId()))
                .collect(Collectors.toList());
        }

        // Minimum value filter
        String minValue = request.getParameter("minValue");
        if (minValue != null) {
            try {
                BigDecimal min = new BigDecimal(minValue);
                deals = deals.stream()
                    .filter(deal -> deal.getValue() != null &&
                            deal.getValue().compareTo(min) >= 0)
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Invalid number, ignore filter
            }
        }

        return deals;
    }

    /**
     * V2 Enhancement: Creates an enhanced response with computed fields.
     */
    private Map<String, Object> createEnhancedResponse(Deal deal) {
        Map<String, Object> enhanced = new HashMap<>();
        enhanced.put("id", deal.getId());
        enhanced.put("title", deal.getTitle());
        enhanced.put("value", deal.getValue());
        enhanced.put("status", deal.getStatus());
        enhanced.put("salesRepId", deal.getSalesRepId());
        enhanced.put("products", deal.getProducts());
        enhanced.put("closeDate", deal.getCloseDate());
        enhanced.put("createdDate", deal.getCreatedDate());
        enhanced.put("lastModifiedDate", deal.getLastModifiedDate());

        // V2 Enhancement: Add computed fields
        enhanced.put("estimatedCommission", calculateEstimatedCommission(deal));
        enhanced.put("productCount", deal.getProducts() != null ? deal.getProducts().size() : 0);

        return enhanced;
    }

    /**
     * V2 Enhancement: Calculates estimated commission (simplified).
     */
    private BigDecimal calculateEstimatedCommission(Deal deal) {
        if (deal.getValue() == null) {
            return BigDecimal.ZERO;
        }
        // Simple 10% commission estimate
        return deal.getValue().multiply(new BigDecimal("0.10"));
    }

    /**
     * Handles GET deal by ID request.
     */
    private void handleGetById(String dealId, HttpServletResponse response) throws IOException {
        Optional<Deal> deal = dealRepository.findById(dealId);

        if (deal.isPresent()) {
            sendJsonResponse(response, createEnhancedResponse(deal.get()));
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "RESOURCE_NOT_FOUND", "Deal not found with ID: " + dealId);
        }
    }

    /**
     * Extracts resource ID from path info.
     * Expected pathInfo format: /{id} or /
     */
    private String extractResourceId(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
            return null;
        }

        // Remove leading slash
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        // PathInfo after servlet mapping contains just the ID (if present)
        // Split: [{id}] or [empty]
        String[] segments = pathInfo.split("/");

        // Resource ID is the first segment (index 0)
        if (segments.length > 0 && !segments[0].isEmpty()) {
            return segments[0];
        }

        return null;
    }

    /**
     * Gets an integer parameter from the request.
     */
    private int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Reads the request body as a string.
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
     * Sends a JSON response.
     */
    private void sendJsonResponse(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonHelper.toJson(object));
    }

    /**
     * V2 Enhancement: Enhanced error responses with error codes.
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode,
            String errorCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("errorCode", errorCode);
        error.put("status", statusCode);
        error.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(JsonHelper.toJson(error));
    }
}