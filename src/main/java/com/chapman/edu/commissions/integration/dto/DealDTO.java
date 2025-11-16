package com.chapman.edu.commissions.integration.dto;

import com.chapman.edu.commissions.model.DealStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DealDTO (Data Transfer Object) - API representation of a Deal.
 *
 * **DTO PATTERN EXPLAINED:**
 *
 * A DTO is an object specifically designed for transferring data between different layers
 * of an application, particularly between the presentation layer (controllers) and the
 * business logic layer (services).
 *
 * **Purpose of DTOs:**
 * 1. **Decoupling:** Separates the API contract from the internal domain model.
 *    Changes to domain entities don't automatically break the API.
 *
 * 2. **Security:** Controls exactly what data is exposed externally.
 *    Can hide sensitive fields or internal implementation details.
 *
 * 3. **Flexibility:** API representation can differ from database structure.
 *    Can flatten nested objects, rename fields, or combine multiple entities.
 *
 * 4. **Versioning:** Multiple DTO versions can support different API versions
 *    while using the same domain model.
 *
 * 5. **Validation:** DTOs can have different validation rules than domain entities.
 *    API validation might be stricter or more lenient.
 *
 * **Why NOT just use domain entities directly?**
 * - Domain entities contain business logic and database mappings
 * - Exposing them couples your API to your database structure
 * - Changes to domain model ripple through to API consumers
 * - Can't easily hide or transform sensitive data
 * - Harder to version APIs independently
 *
 * **Example Scenario:**
 * If we add a new field "internalNotes" to the Deal entity for internal use only,
 * we DON'T want it exposed in the API. With DTOs, we simply don't include it
 * in DealDTO. Without DTOs, we'd need complex serialization configuration.
 *
 * **This DTO represents:**
 * The complete deal information returned to API consumers.
 * Used for GET endpoints that retrieve deal data.
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 * @see com.chapman.edu.commissions.model.Deal The domain entity this DTO represents
 */
public class DealDTO {

    /**
     * Unique identifier for the deal.
     * Format: "DEAL-{UUID}"
     */
    private String id;

    /**
     * Deal title/name.
     */
    private String title;

    /**
     * Current status of the deal.
     */
    private DealStatus status;

    /**
     * ID of the sales representative assigned to this deal.
     */
    private String salesRepId;

    /**
     * Date the deal closed (null if still open).
     */
    private LocalDate closeDate;

    /**
     * List of products included in this deal.
     * Note: We use a nested DTO (DealProductDTO) rather than the domain entity.
     */
    private List<DealProductDTO> products;

    /**
     * Calculated total value of all products.
     * This is a computed field, not stored in the database.
     */
    private BigDecimal totalValue;

    /**
     * Timestamp when the deal was created.
     */
    private String createdDate;

    /**
     * Timestamp when the deal was last modified.
     */
    private String lastModifiedDate;

    // Default constructor required for JSON deserialization
    public DealDTO() {
    }

    // Constructor with all fields
    public DealDTO(String id, String title, DealStatus status,
                   String salesRepId, LocalDate closeDate,
                   List<DealProductDTO> products, BigDecimal totalValue,
                   String createdDate, String lastModifiedDate) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.salesRepId = salesRepId;
        this.closeDate = closeDate;
        this.products = products;
        this.totalValue = totalValue;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DealStatus getStatus() {
        return status;
    }

    public void setStatus(DealStatus status) {
        this.status = status;
    }

    public String getSalesRepId() {
        return salesRepId;
    }

    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public List<DealProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<DealProductDTO> products) {
        this.products = products;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}