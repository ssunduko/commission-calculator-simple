package com.chapman.edu.commissions.api.rest.version;

import com.chapman.edu.commissions.api.rest.JsonHelper;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Version 1 implementation of the Deal endpoint.
 *
 * V1 Features:
 * - Basic CRUD operations
 * - Simple status filtering
 * - No pagination
 * - Returns all fields
 *
 * This version is now deprecated but maintained for backward compatibility.
 */
public class DealEndpointV1 implements VersionedEndpoint {

    private final Repository<Deal> dealRepository;

    /**
     * Constructs a V1 Deal endpoint.
     *
     * @param dealRepository The deal repository
     */
    public DealEndpointV1(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        // Extract resource ID after /api/v1/deals/
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            // Get all deals with optional filtering
            handleGetAll(request, response);
        } else {
            // Get specific deal
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
            sendJsonResponse(response, savedDeal);
        } catch (com.google.gson.JsonSyntaxException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid deal data: " + e.getMessage());
        }
    }

    @Override
    public void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Resource ID is required for update operations");
            return;
        }

        Optional<Deal> existing = dealRepository.findById(resourceId);
        if (existing.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "Deal not found with ID: " + resourceId);
            return;
        }

        try {
            String body = readRequestBody(request);
            Deal deal = JsonHelper.fromJson(body, Deal.class);

            Deal updatedDeal = dealRepository.save(deal);
            sendJsonResponse(response, updatedDeal);
        } catch (com.google.gson.JsonSyntaxException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid deal data: " + e.getMessage());
        }
    }

    @Override
    public void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String resourceId = extractResourceId(pathInfo);

        if (resourceId == null || resourceId.isEmpty()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                "Resource ID is required for delete operations");
            return;
        }

        boolean deleted = dealRepository.deleteById(resourceId);
        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "Deal not found with ID: " + resourceId);
        }
    }

    @Override
    public ApiVersion getVersion() {
        return ApiVersion.V1;
    }

    /**
     * Handles GET all deals request with optional filtering.
     * V1 does not support pagination.
     */
    private void handleGetAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Deal> deals = dealRepository.findAll();

        // V1: Simple status filtering only
        String statusParam = request.getParameter("status");
        if (statusParam != null) {
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

        // V1: Returns all results without pagination
        sendJsonResponse(response, deals);
    }

    /**
     * Handles GET deal by ID request.
     */
    private void handleGetById(String dealId, HttpServletResponse response) throws IOException {
        Optional<Deal> deal = dealRepository.findById(dealId);

        if (deal.isPresent()) {
            sendJsonResponse(response, deal.get());
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND,
                "Deal not found with ID: " + dealId);
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
     * Sends an error response.
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String errorJson = String.format("{\"error\": \"%s\", \"status\": %d}",
            message, statusCode);
        response.getWriter().write(errorJson);
    }
}