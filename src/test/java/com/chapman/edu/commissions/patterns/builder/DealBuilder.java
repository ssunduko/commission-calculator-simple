package com.chapman.edu.commissions.patterns.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for creating Deal objects using the Builder Pattern.
 * 
 * The Builder Pattern is a creational design pattern that provides a flexible solution
 * for constructing complex objects step by step. It separates the construction of a
 * complex object from its representation, allowing the same construction process to
 * create different representations.
 * 
 * Benefits of the Builder Pattern:
 * 1. Provides a clear way to construct complex objects with many optional parameters
 * 2. Makes the code more readable and maintainable
 * 3. Allows for method chaining (fluent interface)
 * 4. Ensures object immutability if desired
 * 5. Provides better control over the construction process
 */
public class DealBuilder {
    
    // Private fields to hold the state during construction
    private String id;
    private String title;
    private BigDecimal value;
    private DealStatus status;
    private String salesRepId;
    private List<DealProduct> products;
    private LocalDate closeDate;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    
    /**
     * Private constructor to prevent direct instantiation.
     * Use the static factory method create() instead.
     */
    private DealBuilder() {
        // Initialize with default values
        this.products = new ArrayList<>();
        this.status = DealStatus.OPEN;
        this.createdDate = LocalDate.now();
        this.lastModifiedDate = LocalDate.now();
    }
    
    /**
     * Static factory method to create a new DealBuilder instance.
     * This is the entry point for using the builder.
     * 
     * @return a new DealBuilder instance
     */
    public static DealBuilder create() {
        return new DealBuilder();
    }
    
    /**
     * Sets the ID for the deal being built.
     * 
     * @param id the unique identifier for the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withId(String id) {
        this.id = id;
        return this;
    }
    
    /**
     * Sets the title for the deal being built.
     * 
     * @param title the title/name of the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    /**
     * Sets the value for the deal being built.
     * 
     * @param value the monetary value of the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withValue(BigDecimal value) {
        this.value = value;
        return this;
    }
    
    /**
     * Convenience method to set the value using a double.
     * 
     * @param value the monetary value of the deal as a double
     * @return this builder instance for method chaining
     */
    public DealBuilder withValue(double value) {
        this.value = BigDecimal.valueOf(value);
        return this;
    }
    
    /**
     * Sets the status for the deal being built.
     * 
     * @param status the current status of the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withStatus(DealStatus status) {
        this.status = status;
        return this;
    }
    
    /**
     * Sets the sales representative ID for the deal being built.
     * 
     * @param salesRepId the ID of the sales representative
     * @return this builder instance for method chaining
     */
    public DealBuilder withSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
        return this;
    }
    
    /**
     * Adds a single product to the deal being built.
     * 
     * @param product the product to add to the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withProduct(DealProduct product) {
        this.products.add(product);
        return this;
    }
    
    /**
     * Sets the complete list of products for the deal being built.
     * 
     * @param products the list of products for the deal
     * @return this builder instance for method chaining
     */
    public DealBuilder withProducts(List<DealProduct> products) {
        this.products = new ArrayList<>(products);
        return this;
    }
    
    /**
     * Sets the close date for the deal being built.
     * 
     * @param closeDate the date when the deal was closed
     * @return this builder instance for method chaining
     */
    public DealBuilder withCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
        return this;
    }
    
    /**
     * Sets the creation date for the deal being built.
     * 
     * @param createdDate the date when the deal was created
     * @return this builder instance for method chaining
     */
    public DealBuilder withCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        return this;
    }
    
    /**
     * Sets the last modified date for the deal being built.
     * 
     * @param lastModifiedDate the date when the deal was last modified
     * @return this builder instance for method chaining
     */
    public DealBuilder withLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        return this;
    }
    
    /**
     * Builds and returns the final Deal object.
     * This method performs the actual construction of the Deal object
     * using all the parameters that have been set through the builder methods.
     * 
     * @return a new Deal object with all the specified properties
     * @throws IllegalStateException if required fields are missing
     */
    public Deal build() {
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalStateException("Deal title is required");
        }
        if (salesRepId == null || salesRepId.trim().isEmpty()) {
            throw new IllegalStateException("Sales representative ID is required");
        }
        
        // Create the Deal object using the constructor
        Deal deal = new Deal();
        
        // Set all the properties
        deal.setId(id);
        deal.setTitle(title);
        deal.setValue(value);
        deal.setStatus(status);
        deal.setSalesRepId(salesRepId);
        deal.setProducts(products);
        deal.setCloseDate(closeDate);
        deal.setCreatedDate(createdDate);
        deal.setLastModifiedDate(lastModifiedDate);
        
        return deal;
    }
}