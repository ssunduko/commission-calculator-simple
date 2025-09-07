package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder Pattern Implementation
 * 
 * This class demonstrates a concrete implementation of the Builder pattern using the Deal class.
 * The Builder pattern is used when:
 * 1. The algorithm for creating a complex object should be independent of the parts that make up the object
 * 2. The construction process must allow different representations for the object that's constructed
 * 3. You need to construct objects that contain a lot of parameters, some optional
 * 
 * IMPLEMENTATION DETAILS:
 * - DealBuilder: A concrete builder for creating Deal objects
 * - The builder provides methods for setting each property of the Deal
 * - Each method returns the builder itself to allow method chaining
 * - The build() method returns the final Deal object
 * 
 * ADVANTAGES DEMONSTRATED:
 * - Avoids "telescoping constructor" anti-pattern (multiple constructors with different parameter combinations)
 * - More readable than constructors with many parameters
 * - Allows creation of immutable objects without complex constructors
 * - Provides clear separation between construction and representation
 */
public class BuilderPatternImplementation {

    /**
     * DealBuilder is a concrete builder for creating Deal objects.
     * It provides a fluent interface for setting Deal properties.
     */
    public static class DealBuilder {
        private String id;
        private String title;
        private BigDecimal value;
        private DealStatus status = DealStatus.OPEN; // Default value
        private String salesRepId;
        private List<DealProduct> products = new ArrayList<>();
        private LocalDate closeDate;
        private LocalDate createdDate = LocalDate.now(); // Default value
        private LocalDate lastModifiedDate = LocalDate.now(); // Default value
        
        /**
         * Constructor with required fields.
         * This ensures that essential properties are always set.
         */
        public DealBuilder(String title, BigDecimal value, String salesRepId) {
            this.title = title;
            this.value = value;
            this.salesRepId = salesRepId;
        }
        
        /**
         * Set the deal ID.
         * @param id the deal ID
         * @return the builder instance
         */
        public DealBuilder withId(String id) {
            this.id = id;
            return this;
        }
        
        /**
         * Set the deal status.
         * @param status the deal status
         * @return the builder instance
         */
        public DealBuilder withStatus(DealStatus status) {
            this.status = status;
            return this;
        }
        
        /**
         * Add a product to the deal.
         * @param product the product to add
         * @return the builder instance
         */
        public DealBuilder addProduct(DealProduct product) {
            this.products.add(product);
            return this;
        }
        
        /**
         * Set all products for the deal.
         * @param products the list of products
         * @return the builder instance
         */
        public DealBuilder withProducts(List<DealProduct> products) {
            this.products = new ArrayList<>(products); // Create a copy to avoid external modification
            return this;
        }
        
        /**
         * Set the close date.
         * @param closeDate the close date
         * @return the builder instance
         */
        public DealBuilder withCloseDate(LocalDate closeDate) {
            this.closeDate = closeDate;
            return this;
        }
        
        /**
         * Set the created date.
         * @param createdDate the created date
         * @return the builder instance
         */
        public DealBuilder withCreatedDate(LocalDate createdDate) {
            this.createdDate = createdDate;
            return this;
        }
        
        /**
         * Set the last modified date.
         * @param lastModifiedDate the last modified date
         * @return the builder instance
         */
        public DealBuilder withLastModifiedDate(LocalDate lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }
        
        /**
         * Build the Deal object.
         * @return a new Deal instance with all the properties set
         */
        public Deal build() {
            // Create a new Deal object
            Deal deal = new Deal(title, value, salesRepId);
            
            // Set optional properties
            if (id != null) {
                deal.setId(id);
            }
            
            deal.setStatus(status);
            deal.setProducts(products);
            
            if (closeDate != null) {
                deal.setCloseDate(closeDate);
            }
            
            deal.setCreatedDate(createdDate);
            deal.setLastModifiedDate(lastModifiedDate);
            
            return deal;
        }
    }
}