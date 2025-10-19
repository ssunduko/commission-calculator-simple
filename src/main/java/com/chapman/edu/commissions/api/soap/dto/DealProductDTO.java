package com.chapman.edu.commissions.api.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

/**
 * Data Transfer Object for DealProduct entity in SOAP web services.
 */
@XmlRootElement(name = "DealProduct")
@XmlAccessorType(XmlAccessType.FIELD)
public class DealProductDTO {

    @XmlElement
    private String id;

    @XmlElement(required = true)
    private String productId;

    @XmlElement(required = true)
    private String productName;

    @XmlElement(required = true)
    private Integer quantity;

    @XmlElement(required = true)
    private BigDecimal price;

    @XmlElement
    private BigDecimal discount;

    @XmlElement
    private String dealId;

    @XmlElement
    private BigDecimal totalPrice;

    public DealProductDTO() {
    }

    public DealProductDTO(String productId, String productName, Integer quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public String getDealId() { return dealId; }
    public void setDealId(String dealId) { this.dealId = dealId; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}