package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BUILDER PATTERN IMPLEMENTATION
 *
 * This class demonstrates the Builder design pattern for constructing complex Deal objects.
 *
 * PATTERN PURPOSE:
 * The Builder pattern separates the construction of a complex object from its representation,
 * allowing the same construction process to create different representations. It provides
 * a fluent interface for step-by-step object construction.
 *
 * PROBLEM SOLVED:
 * Deal objects have many fields (9 total), with only 3 required and 6 optional. Without the
 * Builder pattern, we would need either:
 * 1. Telescoping constructors - multiple constructors with different parameter combinations
 *    Example: Deal(title, value, rep), Deal(title, value, rep, status), etc.
 *    Problem: Confusing, hard to maintain, explosion of constructors
 *
 * 2. Setter methods on mutable objects
 *    Problem: Allows incomplete/invalid objects to exist, not thread-safe, verbose
 *
 * BUILDER SOLUTION:
 * - Single constructor for required fields
 * - Fluent methods (withXxx) for optional fields
 * - Method chaining for readability
 * - build() method creates final immutable object
 *
 * KEY CHARACTERISTICS:
 * - Static nested class (DealBuilder inside BuilderPatternImplementation)
 * - Fluent interface - each method returns 'this' for chaining
 * - Separation of concerns - building logic separate from Deal class
 * - Handles default values gracefully
 * - Creates defensive copies to prevent external modification
 *
 * @see DealBuilder The concrete builder class
 */
public class BuilderPatternImplementation {

    /**
     * CONCRETE BUILDER: DealBuilder
     *
     * Provides a fluent API for constructing Deal objects step-by-step.
     *
     * DESIGN DECISIONS:
     * 1. Constructor enforces required parameters (title, value, salesRepId)
     * 2. Optional parameters set via fluent methods (withXxx)
     * 3. Default values initialized in field declarations
     * 4. build() performs final object construction
     *
     * FLUENT INTERFACE PATTERN:
     * Each setter method returns 'this' enabling method chaining:
     *   new DealBuilder(...).withId("123").withStatus(OPEN).build()
     *
     * BUILDER RESPONSIBILITIES:
     * - Store intermediate state during construction
     * - Validate parameters (if needed)
     * - Apply default values
     * - Create and configure final Deal object
     * - Return fully-constructed Deal
     */
    public static class DealBuilder {
        // Fields mirror Deal object fields, storing values until build() is called
        private String id;
        private String title;
        private BigDecimal value;
        private DealStatus status = DealStatus.OPEN; // Default value - new deals are OPEN
        private String salesRepId;
        private List<DealProduct> products = new ArrayList<>(); // Default to empty list
        private LocalDate closeDate;
        private LocalDate createdDate = LocalDate.now(); // Default to current date
        private LocalDate lastModifiedDate = LocalDate.now(); // Default to current date

        /**
         * Constructor with REQUIRED fields only.
         *
         * DESIGN PATTERN: Enforce invariants through constructor
         * By requiring essential fields in the constructor, we ensure that:
         * - No Deal can be created without title, value, and salesRepId
         * - These core business requirements are compiler-enforced
         * - Developers cannot accidentally forget required fields
         *
         * @param title the deal title (required, cannot be null)
         * @param value the deal value (required, cannot be null)
         * @param salesRepId the sales representative ID (required, cannot be null)
         */
        public DealBuilder(String title, BigDecimal value, String salesRepId) {
            this.title = title;
            this.value = value;
            this.salesRepId = salesRepId;
        }
        /**
         * Set the deal ID (optional field).
         *
         * FLUENT INTERFACE: Returns 'this' to enable method chaining
         *
         * @param id the deal ID
         * @return this builder instance for method chaining
         */
        public DealBuilder withId(String id) {
            this.id = id;
            return this; // Enables: builder.withId("123").withStatus(...)
        }
        /**
         * Set the deal status (optional field).
         *
         * Allows overriding default status (OPEN).
         * Common values: OPEN, WON, LOST, PENDING
         *
         * @param status the deal status
         * @return this builder instance for method chaining
         */
        public DealBuilder withStatus(DealStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Add a single product to the deal's product list.
         *
         * INCREMENTAL BUILDING: This method adds to existing products
         * rather than replacing them. Useful when building products
         * one at a time.
         *
         * Can be called multiple times:
         *   builder.addProduct(product1)
         *          .addProduct(product2)
         *          .addProduct(product3)
         *
         * @param product the product to add to the deal
         * @return this builder instance for method chaining
         */
        public DealBuilder addProduct(DealProduct product) {
            this.products.add(product);
            return this;
        }

        /**
         * Set all products for the deal at once.
         *
         * DEFENSIVE COPY: Creates a new ArrayList to prevent external
         * modification of the internal product list. This ensures that
         * changes to the original list don't affect the builder state.
         *
         * REPLACES existing products (unlike addProduct which appends).
         *
         * @param products the list of products to set
         * @return this builder instance for method chaining
         */
        public DealBuilder withProducts(List<DealProduct> products) {
            this.products = new ArrayList<>(products); // Defensive copy prevents external modification
            return this;
        }

        /**
         * Set the expected close date for the deal (optional field).
         *
         * Typically set to future date for open deals.
         *
         * @param closeDate the expected/actual close date
         * @return this builder instance for method chaining
         */
        public DealBuilder withCloseDate(LocalDate closeDate) {
            this.closeDate = closeDate;
            return this;
        }

        /**
         * Set the creation date (optional field).
         *
         * If not set, defaults to LocalDate.now().
         * Useful for recreating historical deals or testing.
         *
         * @param createdDate the date the deal was created
         * @return this builder instance for method chaining
         */
        public DealBuilder withCreatedDate(LocalDate createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        /**
         * Set the last modified date (optional field).
         *
         * If not set, defaults to LocalDate.now().
         * Useful for audit trails or testing.
         *
         * @param lastModifiedDate the date the deal was last modified
         * @return this builder instance for method chaining
         */
        public DealBuilder withLastModifiedDate(LocalDate lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        /**
         * BUILD METHOD: Constructs and returns the final Deal object.
         *
         * This is the terminal operation in the fluent chain.
         * After calling build(), the builder can be reused if needed.
         *
         * CONSTRUCTION PROCESS:
         * 1. Create Deal using required fields (title, value, salesRepId)
         * 2. Set optional fields if they were provided
         * 3. Set default values for fields not explicitly set
         * 4. Return fully-constructed, ready-to-use Deal object
         *
         * NULL SAFETY: Only sets optional fields if they were explicitly provided
         * (e.g., id and closeDate are only set if not null)
         *
         * @return a new, fully-configured Deal instance
         */
        public Deal build() {
            // Step 1: Create Deal with required fields using Deal's constructor
            Deal deal = new Deal(title, value, salesRepId);
            // Step 2: Set optional ID field (only if provided)
            if (id != null) {
                deal.setId(id);
            }
            // Step 3: Set status (uses default OPEN if not overridden)
            deal.setStatus(status);
            // Step 4: Set products list (uses empty list if none added)
            deal.setProducts(products);
            // Step 5: Set optional close date (only if provided)
            if (closeDate != null) {
                deal.setCloseDate(closeDate);
            }
            // Step 6: Set audit dates (use defaults if not overridden)
            deal.setCreatedDate(createdDate);
            deal.setLastModifiedDate(lastModifiedDate);
            // Step 7: Return fully-constructed Deal
            return deal;
        }
    }
}