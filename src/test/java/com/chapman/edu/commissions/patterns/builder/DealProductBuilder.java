package com.chapman.edu.commissions.patterns.builder;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * Builder class for creating DealProduct objects using the Builder Pattern.
 * 
 * This builder demonstrates how even simpler objects can benefit from the Builder Pattern,
 * especially when they have optional parameters or when you want to provide a fluent
 * interface for object construction.
 */
public class DealProductBuilder {
    
    // Private fields to hold the state during construction
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private String dealId;
    
    /**
     * Private constructor to prevent direct instantiation.
     */
    private DealProductBuilder() {
        // Initialize with default values
        this.quantity = 1;
        this.discount = BigDecimal.ZERO;
    }
    
    /**
     * Static factory method to create a new DealProductBuilder instance.
     * 
     * @return a new DealProductBuilder instance
     */
    public static DealProductBuilder create() {
        return new DealProductBuilder();
    }
    
    /**
     * Sets the ID for the deal product being built.
     * 
     * @param id the unique identifier for the deal product
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * Sets the product ID for the deal product being built.
     * 
     * @param productId the product identifier
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withProductId(String productId) {
        this.productId = productId;
        return this;
    }
    
    /**
     * Sets the product name for the deal product being built.
     * 
     * @param productName the name of the product
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withProductName(String productName) {
        this.productName = productName;
        return this;
    }
    
    /**
     * Convenience method to set both product ID and name at once.
     * 
     * @param productId the product identifier
     * @param productName the name of the product
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withProduct(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
        return this;
    }
    
    /**
     * Sets the quantity for the deal product being built.
     * 
     * @param quantity the quantity of the product
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
    
    /**
     * Sets the price for the deal product being built.
     * 
     * @param price the price per unit
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    
    /**
     * Convenience method to set the price using a double.
     * 
     * @param price the price per unit as a double
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withPrice(double price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }
    
    /**
     * Sets the discount for the deal product being built.
     * 
     * @param discount the discount amount
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withDiscount(BigDecimal discount) {
        this.discount = discount;
        return this;
    }
    
    /**
     * Convenience method to set the discount using a double.
     * 
     * @param discount the discount amount as a double
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withDiscount(double discount) {
        this.discount = BigDecimal.valueOf(discount);
        return this;
    }
    
    /**
     * Sets the deal ID for the deal product being built.
     * 
     * @param dealId the ID of the deal this product belongs to
     * @return this builder instance for method chaining
     */
    public DealProductBuilder withDealId(String dealId) {
        this.dealId = dealId;
        return this;
    }
    
    /**
     * Builds and returns the final DealProduct object.
     * 
     * @return a new DealProduct object with all the specified properties
     * @throws IllegalStateException if required fields are missing
     */
    public DealProduct build() {
        // Validate required fields
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalStateException("Product ID is required");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalStateException("Product name is required");
        }
        if (price == null) {
            throw new IllegalStateException("Price is required");
        }
        if (quantity <= 0) {
            throw new IllegalStateException("Quantity must be greater than zero");
        }
        
        // Create the DealProduct object
        DealProduct dealProduct = new DealProduct();
        
        // Set all the properties
        dealProduct.setId(id);
        dealProduct.setProductId(productId);
        dealProduct.setProductName(productName);
        dealProduct.setQuantity(quantity);
        dealProduct.setPrice(price);
        dealProduct.setDiscount(discount);
        dealProduct.setDealId(dealId);
        
        return dealProduct;
    }
}