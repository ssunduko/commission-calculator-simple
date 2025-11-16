package com.chapman.edu.commissions.integration.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * CreateDealRequest - DTO for creating a new deal.
 *
 * **Request vs Response DTOs:**
 *
 * This demonstrates an important DTO pattern variation: using different DTOs
 * for requests (input) vs responses (output).
 *
 * **Why separate request/response DTOs?**
 * 1. **Different fields:** Requests don't include generated fields (id, createdDate).
 *    Responses include all fields including computed/generated ones.
 *
 * 2. **Validation differences:** Create requests have "required" validations.
 *    Update requests might make some fields optional.
 *    Responses have no validation (already validated data).
 *
 * 3. **Security:** Clients shouldn't be able to set certain fields like 'createdDate'.
 *    These are server-controlled and only appear in responses.
 *
 * 4. **API clarity:** Makes it explicit what fields are expected for each operation.
 *    Developers immediately know what to send vs what to expect back.
 *
 * **This request DTO:**
 * - Does NOT include 'id' (server-generated)
 * - Does NOT include 'totalValue' (computed from products)
 * - Does NOT include 'createdDate' or 'lastModifiedDate' (server-controlled)
 * - Does NOT include 'closeDate' (set by close operation, not creation)
 * - Status is optional (defaults to OPEN if not provided)
 *
 * @see DealDTO The response DTO returned after creation
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class CreateDealRequest {

    /**
     * Deal title (required).
     */
    private String title;

    /**
     * Sales representative ID (required).
     */
    private String salesRepId;

    /**
     * List of products in the deal (required, at least one).
     */
    private List<DealProductDTO> products;

    // Default constructor
    public CreateDealRequest() {
    }

    public CreateDealRequest(String title, String salesRepId, List<DealProductDTO> products) {
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

    public List<DealProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<DealProductDTO> products) {
        this.products = products;
    }
}