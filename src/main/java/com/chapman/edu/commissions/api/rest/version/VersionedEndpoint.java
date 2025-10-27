package com.chapman.edu.commissions.api.rest.version;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interface for versioned API endpoints.
 *
 * This interface defines the contract that all versioned endpoint implementations
 * must follow. Each version of an endpoint implements this interface differently
 * to provide version-specific behavior.
 *
 * Design Pattern: Strategy Pattern
 * Different implementations provide different algorithms for the same operation.
 *
 * Design Principle: Interface Segregation Principle (ISP)
 * Simple, focused interface with minimal methods.
 */
public interface VersionedEndpoint {

    /**
     * Handles a GET request for this versioned endpoint.
     *
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws Exception if an error occurs during processing
     */
    void handleGet(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Handles a POST request for this versioned endpoint.
     *
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws Exception if an error occurs during processing
     */
    void handlePost(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Handles a PUT request for this versioned endpoint.
     *
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws Exception if an error occurs during processing
     */
    void handlePut(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Handles a DELETE request for this versioned endpoint.
     *
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws Exception if an error occurs during processing
     */
    void handleDelete(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * Gets the API version this endpoint implements.
     *
     * @return The API version
     */
    ApiVersion getVersion();
}