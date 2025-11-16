package com.chapman.edu.commissions.integration.dto;

import java.math.BigDecimal;

/**
 * DealProductDTO - Data Transfer Object for product items within a deal.
 *
 * This is a nested DTO used within DealDTO to represent individual products.
 * It demonstrates that DTOs can be composed of other DTOs to represent
 * complex hierarchical data structures.
 *
 * **Design Decision:**
 * We use a separate DTO for DealProduct rather than reusing the domain entity
 * to maintain complete separation between API and domain layers.
 *
 * @see DealDTO Parent DTO that contains a list of these
 * @see com.chapman.edu.commissions.model.DealProduct The domain entity this DTO represents
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class DealProductDTO {

    /**
     * Product ID reference.
     */
    private String productId;

    /**
     * Product name or description.
     */
    private String productName;

    /**
     * Unit price for this product.
     */
    private BigDecimal price;

    /**
     * Quantity of this product in the deal.
     */
    private int quantity;

    /**
     * Discount applied to this product (optional).
     */
    private BigDecimal discount;

    // Default constructor for JSON deserialization
    public DealProductDTO() {
    }

    public DealProductDTO(String productId, String productName, BigDecimal price, int quantity, BigDecimal discount) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}