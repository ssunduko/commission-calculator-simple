package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternImplementation.DealBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * BUILDER PATTERN USAGE EXAMPLES
 *
 * This class demonstrates practical usage of the Builder pattern for creating Deal objects.
 *
 * LEARNING OBJECTIVES:
 * 1. See how Builder simplifies object creation
 * 2. Understand fluent API benefits
 * 3. Compare minimal vs. complete object construction
 * 4. Learn when to use Builder pattern
 *
 * EXAMPLES DEMONSTRATED:
 * - Minimal Deal: Only required fields
 * - Complete Deal: All fields populated
 * - Custom Deal: Mix of required and selected optional fields
 *
 * KEY BENEFITS SHOWN:
 * - Code readability: Clear what each parameter represents
 * - Flexibility: Only set fields you need
 * - Type safety: Compile-time checking of parameter types
 * - Maintainability: Easy to add new optional fields
 *
 * RUN THIS CLASS:
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternUsage"
 */
public class BuilderPatternUsage {

    /**
     * Main method demonstrating various Builder pattern usage scenarios.
     *
     * Compares different ways of constructing Deal objects:
     * 1. Minimal construction (required fields only)
     * 2. Complete construction (all fields)
     * 3. Custom construction (selective fields)
     */
    public static void main(String[] args) {
        // Example 1: Creating a minimal Deal with only required fields
        Deal minimalDeal = createMinimalDeal();
        System.out.println("Minimal Deal:");
        printDealDetails(minimalDeal);

        // Example 2: Creating a Deal with all fields set
        Deal completeDeal = createCompleteDeal();
        System.out.println("\nComplete Deal:");
        printDealDetails(completeDeal);

        // Example 3: Creating a Deal with some optional fields
        Deal customDeal = createCustomDeal();
        System.out.println("\nCustom Deal:");
        printDealDetails(customDeal);
    }

    /**
     * EXAMPLE 1: Minimal Deal Construction
     *
     * Creates a Deal with ONLY the required fields (title, value, salesRepId).
     *
     * BUILDER BENEFIT: Even with minimal fields, the code is highly readable.
     * Compare to constructor: new Deal("Basic Deal", new BigDecimal("5000.00"), "SALES-001")
     * The builder version makes it clear we're building an object and finalizing it.
     *
     * DEFAULT VALUES APPLIED:
     * - status: OPEN (builder default)
     * - products: empty list (builder default)
     * - createdDate: LocalDate.now() (builder default)
     * - lastModifiedDate: LocalDate.now() (builder default)
     * - id: null (not set)
     * - closeDate: null (not set)
     *
     * @return a minimal Deal with defaults
     */
    private static Deal createMinimalDeal() {
        // Simplest possible builder usage - just required fields and build()
        return new DealBuilder("Basic Deal", new BigDecimal("5000.00"), "SALES-001")
                .build(); // Terminal operation - creates and returns Deal
    }

    /**
     * EXAMPLE 2: Complete Deal Construction
     *
     * Creates a Deal with ALL fields explicitly set.
     *
     * FLUENT API DEMONSTRATION:
     * Notice how each method call chains to the next, creating a
     * readable, top-to-bottom flow that's easy to understand.
     *
     * READABILITY BENEFIT:
     * Without Builder, this would require either:
     * 1. A constructor with 9 parameters (error-prone, hard to read)
     * 2. Multiple setter calls (verbose, allows invalid intermediate states)
     *
     * MULTI-LINE CHAINING:
     * Each withXxx() call is on its own line for maximum readability.
     * This is a common pattern in builder usage.
     *
     * @return a fully-populated Deal
     */
    private static Deal createCompleteDeal() {
        // Create product objects to add to the deal
        DealProduct product1 = new DealProduct("PROD-001", "Software License", 2, new BigDecimal("1000.00"));
        DealProduct product2 = new DealProduct("PROD-002", "Hardware", 1, new BigDecimal("1500.00"));

        // FLUENT API in action - method chaining creates readable construction flow
        return new DealBuilder("Complete Deal", new BigDecimal("10000.00"), "SALES-002")
                .withId("DEAL-002")                                     // Set ID
                .withStatus(DealStatus.WON)                            // Override default OPEN status
                .withProducts(Arrays.asList(product1, product2))       // Set products as list
                .withCloseDate(LocalDate.now().plusDays(30))           // Set future close date
                .withCreatedDate(LocalDate.now().minusDays(10))        // Override creation date
                .withLastModifiedDate(LocalDate.now())                 // Set modified date
                .build();                                               // Terminal operation
    }

    /**
     * EXAMPLE 3: Custom Deal Construction (Selective Fields)
     *
     * Creates a Deal with only SOME optional fields set.
     *
     * FLEXIBILITY BENEFIT:
     * This demonstrates the Builder's key advantage: you only set what you need.
     * No need to pass null for unused optional parameters.
     *
     * addProduct() vs withProducts():
     * - addProduct(): Adds single product (incremental building)
     * - withProducts(): Replaces entire product list
     *
     * Both methods support the fluent interface and return the builder.
     *
     * REAL-WORLD SCENARIO:
     * This is the most common usage pattern - some required fields,
     * a few optional fields, and many defaults accepted.
     *
     * @return a custom Deal with selective fields
     */
    private static Deal createCustomDeal() {
        // Create a single product
        DealProduct product = new DealProduct("PROD-003", "Consulting Services", 1, new BigDecimal("2000.00"));

        // Selective field setting - only what's needed for this scenario
        return new DealBuilder("Custom Deal", new BigDecimal("7500.00"), "SALES-003")
                .withId("DEAL-003")                                     // Set ID
                .addProduct(product)                                    // Add single product (note: not withProducts)
                .withCloseDate(LocalDate.now().plusDays(15))           // Set close date
                .build();                                               // Terminal operation

        // Fields not set will use defaults:
        // - status: OPEN (default)
        // - createdDate: LocalDate.now() (default)
        // - lastModifiedDate: LocalDate.now() (default)
    }

    /**
     * Utility method to print Deal details to console.
     *
     * Used to demonstrate the state of objects created by the builder.
     *
     * @param deal the Deal object to print
     */
    private static void printDealDetails(Deal deal) {
        System.out.println("  ID: " + deal.getId());
        System.out.println("  Title: " + deal.getTitle());
        System.out.println("  Value: " + deal.getValue());
        System.out.println("  Status: " + deal.getStatus());
        System.out.println("  Sales Rep ID: " + deal.getSalesRepId());
        System.out.println("  Products: " + deal.getProducts().size());
        System.out.println("  Close Date: " + deal.getCloseDate());
        System.out.println("  Created Date: " + deal.getCreatedDate());
        System.out.println("  Last Modified Date: " + deal.getLastModifiedDate());
    }
}
