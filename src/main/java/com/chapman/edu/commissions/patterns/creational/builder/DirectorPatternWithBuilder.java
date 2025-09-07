package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternImplementation.DealBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Director Pattern with Builder
 * 
 * This class demonstrates how the Director pattern works with the Builder pattern.
 * The Director defines the order in which to execute the building steps, while the
 * Builder provides the implementation for those steps.
 * 
 * DIRECTOR PATTERN BENEFITS:
 * - Encapsulates complex construction logic in one place
 * - Provides reusable construction processes
 * - Isolates client code from construction details
 * - Allows for different construction processes using the same builder
 * 
 * WHEN TO USE A DIRECTOR:
 * - When you have common ways to construct a product
 * - When you want to hide construction details from the client
 * - When you need to create products in a specific sequence or with specific configurations
 * 
 * REAL-WORLD ANALOGY:
 * Think of a house construction. The architect (Director) provides the blueprint and
 * specifies how the house should be built. The construction company (Builder) follows
 * those specifications to build the actual house. Different architects can create different
 * blueprints, but they all work with the same construction company.
 */
public class DirectorPatternWithBuilder {

    /**
     * The DealDirector class encapsulates the construction process for different types of deals.
     * It works with any builder that follows the DealBuilder interface.
     */
    public static class DealDirector {
        private DealBuilder builder;
        
        /**
         * Constructor that takes a builder.
         * @param builder the builder to use
         */
        public DealDirector(DealBuilder builder) {
            this.builder = builder;
        }
        
        /**
         * Set a different builder.
         * @param builder the new builder to use
         */
        public void changeBuilder(DealBuilder builder) {
            this.builder = builder;
        }
        
        /**
         * Construct a standard deal with default settings.
         * @return the constructed Deal
         */
        public Deal constructStandardDeal() {
            return builder
                    .withStatus(DealStatus.OPEN)
                    .withCloseDate(LocalDate.now().plusDays(30))
                    .build();
        }
        
        /**
         * Construct a premium deal with high-value products.
         * @return the constructed Deal
         */
        public Deal constructPremiumDeal() {
            // Create premium products
            DealProduct premiumSoftware = new DealProduct("PROD-101", "Premium Software Suite", 1, new BigDecimal("5000.00"));
            DealProduct premiumSupport = new DealProduct("PROD-102", "Premium Support Package", 1, new BigDecimal("2000.00"));
            
            return builder
                    .withStatus(DealStatus.OPEN)
                    .withProducts(Arrays.asList(premiumSoftware, premiumSupport))
                    .withCloseDate(LocalDate.now().plusDays(15))
                    .build();
        }
        
        /**
         * Construct a deal that has already been won.
         * @return the constructed Deal
         */
        public Deal constructWonDeal() {
            return builder
                    .withStatus(DealStatus.WON)
                    .withCloseDate(LocalDate.now().minusDays(5))
                    .build();
        }
        
        /**
         * Construct a deal that has been lost.
         * @return the constructed Deal
         */
        public Deal constructLostDeal() {
            return builder
                    .withStatus(DealStatus.LOST)
                    .withCloseDate(LocalDate.now().minusDays(5))
                    .build();
        }
    }
    
    /**
     * Main method to demonstrate the usage of the Director pattern with Builder
     */
    public static void main(String[] args) {
        // Create a builder for a basic deal
        DealBuilder builder = new DealBuilder("Director Demo Deal", new BigDecimal("15000.00"), "SALES-004");
        
        // Create a director with the builder
        DealDirector director = new DealDirector(builder);
        
        // Use the director to construct different types of deals
        System.out.println("Standard Deal:");
        Deal standardDeal = director.constructStandardDeal();
        printDealDetails(standardDeal);
        
        System.out.println("\nPremium Deal:");
        Deal premiumDeal = director.constructPremiumDeal();
        printDealDetails(premiumDeal);
        
        System.out.println("\nWon Deal:");
        Deal wonDeal = director.constructWonDeal();
        printDealDetails(wonDeal);
        
        System.out.println("\nLost Deal:");
        Deal lostDeal = director.constructLostDeal();
        printDealDetails(lostDeal);
        
        // Demonstrate changing the builder
        DealBuilder newBuilder = new DealBuilder("New Builder Deal", new BigDecimal("20000.00"), "SALES-005")
                .withId("DEAL-005");
        
        director.changeBuilder(newBuilder);
        
        System.out.println("\nDeal with New Builder:");
        Deal newBuilderDeal = director.constructStandardDeal();
        printDealDetails(newBuilderDeal);
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
    }
}