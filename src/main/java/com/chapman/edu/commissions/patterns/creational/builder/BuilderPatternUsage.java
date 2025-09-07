package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternImplementation.DealBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Builder Pattern Usage
 * 
 * This class demonstrates how to use the Builder pattern in practice.
 * It shows different ways to create Deal objects using the DealBuilder.
 * 
 * CLIENT PERSPECTIVE:
 * This example shows how client code interacts with the Builder pattern:
 * 1. The client creates a builder instance with required parameters
 * 2. The client calls methods to set optional parameters as needed
 * 3. The client calls build() to get the final object
 * 
 * KEY DEMONSTRATION POINTS:
 * - Fluent Interface: Method chaining for a more readable API
 * - Selective Parameter Setting: Only set the parameters you need
 * - Readability: Clear what each parameter represents
 * - Flexibility: Multiple ways to create objects with different configurations
 * 
 * PATTERN BENEFITS SHOWN:
 * - Improved code readability compared to constructors with many parameters
 * - No need for multiple constructors with different parameter combinations
 * - Clear separation between construction and representation
 * - Ability to enforce invariants during construction
 */
public class BuilderPatternUsage {

    /**
     * Main method to demonstrate the usage of the Builder pattern
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
     * Creates a minimal Deal with only the required fields.
     * @return a minimal Deal
     */
    private static Deal createMinimalDeal() {
        return new DealBuilder("Basic Deal", new BigDecimal("5000.00"), "SALES-001")
                .build();
    }

    /**
     * Creates a complete Deal with all fields set.
     * @return a complete Deal
     */
    private static Deal createCompleteDeal() {
        // Create some products for the deal
        DealProduct product1 = new DealProduct("PROD-001", "Software License", 2, new BigDecimal("1000.00"));
        DealProduct product2 = new DealProduct("PROD-002", "Hardware", 1, new BigDecimal("1500.00"));

        // Use the builder to create a deal with all properties set
        return new DealBuilder("Complete Deal", new BigDecimal("10000.00"), "SALES-002")
                .withId("DEAL-002")
                .withStatus(DealStatus.WON)
                .withProducts(Arrays.asList(product1, product2))
                .withCloseDate(LocalDate.now().plusDays(30))
                .withCreatedDate(LocalDate.now().minusDays(10))
                .withLastModifiedDate(LocalDate.now())
                .build();
    }

    /**
     * Creates a custom Deal with some optional fields.
     * @return a custom Deal
     */
    private static Deal createCustomDeal() {
        // Create a product for the deal
        DealProduct product = new DealProduct("PROD-003", "Consulting Services", 1, new BigDecimal("2000.00"));

        // Use the builder to create a deal with some optional properties
        return new DealBuilder("Custom Deal", new BigDecimal("7500.00"), "SALES-003")
                .withId("DEAL-003")
                .addProduct(product)  // Note: using addProduct instead of withProducts
                .withCloseDate(LocalDate.now().plusDays(15))
                .build();
    }

    /**
     * Prints the details of a Deal.
     * @param deal the Deal to print
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
