package com.chapman.edu.commissions.integration.dto;

import com.chapman.edu.commissions.model.DealStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * UpdateDealRequest - DTO for updating an existing deal.
 *
 * This is similar to CreateDealRequest but represents update operations.
 * In this simple implementation, they're nearly identical, but in real-world
 * scenarios, update requests often have different requirements:
 *
 * **Common differences between Create and Update requests:**
 * - Create: All fields required
 * - Update: Fields are optional (only send what you want to change)
 *
 * - Create: No ID in request body
 * - Update: ID in URL path, optionally in body for validation
 *
 * - Create: Sets defaults for missing fields
 * - Update: Preserves existing values for missing fields
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class UpdateDealRequest {

    private String title;
    private String salesRepId;
    private String status;
    private List<DealProductDTO> products;

    // Default constructor
    public UpdateDealRequest() {
    }

    public UpdateDealRequest(String title, String salesRepId, List<DealProductDTO> products) {
        this.title = title;
        this.salesRepId = salesRepId;
        this.products = products;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSalesRepId() {
        return salesRepId;
    }

    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DealProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<DealProductDTO> products) {
        this.products = products;
    }
}