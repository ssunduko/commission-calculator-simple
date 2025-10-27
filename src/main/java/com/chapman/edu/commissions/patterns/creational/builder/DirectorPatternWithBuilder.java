package com.chapman.edu.commissions.patterns.creational.builder;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternImplementation.DealBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * DIRECTOR PATTERN WITH BUILDER
 *
 * This class demonstrates the Director pattern, which works alongside the Builder pattern
 * to encapsulate common construction sequences.
 *
 * PATTERN PURPOSE:
 * While the Builder pattern handles HOW to construct objects step-by-step,
 * the Director pattern handles WHAT construction sequences are common and reusable.
 *
 * PROBLEM SOLVED:
 * Without a Director, clients would repeatedly write the same builder sequences:
 *   builder.withStatus(OPEN).withCloseDate(...).build()  // Standard deal
 *   builder.withStatus(OPEN).withCloseDate(...).build()  // Again, same code
 *   builder.withStatus(OPEN).withCloseDate(...).build()  // Code duplication!
 *
 * DIRECTOR SOLUTION:
 * Encapsulates common construction sequences in named methods:
 *   director.constructStandardDeal()  // Same logic, reusable
 *   director.constructPremiumDeal()   // Different logic, still reusable
 *
 * KEY BENEFITS:
 * - Eliminates duplication of construction logic
 * - Provides semantic names for construction types (standard, premium, etc.)
 * - Hides complexity from clients
 * - Makes code more maintainable (change logic in one place)
 * - Enables consistent object configurations
 *
 * RELATIONSHIP WITH BUILDER:
 * - Builder: Knows HOW to build Deal objects step-by-step
 * - Director: Knows WHAT steps to use for specific Deal types
 * - Builder can be used without Director (direct client control)
 * - Director cannot work without Builder (depends on builder interface)
 *
 * WHEN TO USE DIRECTOR:
 * - Multiple clients need same construction sequence
 * - Complex construction logic should be hidden
 * - Want to standardize object configurations
 * - Need to create "templates" or "presets" for objects
 *
 * WHEN NOT TO USE DIRECTOR:
 * - Only one way to construct objects
 * - Construction is always unique per client
 * - Added abstraction doesn't justify the complexity
 *
 * RUN THIS CLASS:
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.builder.DirectorPatternWithBuilder"
 */
public class DirectorPatternWithBuilder {

    /**
     * DIRECTOR CLASS: DealDirector
     *
     * Encapsulates common Deal construction sequences using a DealBuilder.
     *
     * DESIGN PATTERN: Strategy + Template Method hybrid
     * - Uses DealBuilder (strategy) to perform construction steps
     * - Defines template methods (constructXxx) for standard sequences
     *
     * FLEXIBILITY:
     * - Can work with any DealBuilder instance
     * - Builder can be changed at runtime via changeBuilder()
     * - Same construction logic applied to different builders
     *
     * RESPONSIBILITIES:
     * - Define standard construction sequences
     * - Apply business logic for different deal types
     * - Hide construction complexity from clients
     * - Ensure consistent object configurations
     */
    public static class DealDirector {
        // Reference to the builder used for construction
        // This can be changed at runtime via changeBuilder()
        private DealBuilder builder;

        /**
         * Constructor accepting a DealBuilder instance.
         *
         * DEPENDENCY INJECTION: The builder is injected via constructor,
         * making the Director flexible and testable.
         *
         * @param builder the DealBuilder to use for constructing deals
         */
        public DealDirector(DealBuilder builder) {
            this.builder = builder;
        }

        /**
         * Changes the builder instance used by this director.
         *
         * RUNTIME FLEXIBILITY: Allows the same director to work with
         * different builders, applying its construction logic to
         * different base configurations.
         *
         * USE CASE: When you want to reuse construction logic but
         * with different required fields (title, value, salesRepId).
         *
         * @param builder the new DealBuilder to use
         */
        public void changeBuilder(DealBuilder builder) {
            this.builder = builder;
        }

        /**
         * CONSTRUCTION TEMPLATE: Standard Deal
         *
         * Creates a standard deal configuration commonly used for
         * regular sales opportunities.
         *
         * CONFIGURATION:
         * - Status: OPEN (active opportunity)
         * - Close Date: 30 days from now (standard sales cycle)
         * - Uses whatever required fields were set in the builder
         *
         * BUSINESS LOGIC ENCAPSULATION:
         * The "30 days" business rule is encapsulated here.
         * If the standard sales cycle changes, we update it once here.
         *
         * @return a Deal configured as a standard opportunity
         */
        public Deal constructStandardDeal() {
            return builder
                    .withStatus(DealStatus.OPEN)                    // New opportunity
                    .withCloseDate(LocalDate.now().plusDays(30))    // Standard 30-day cycle
                    .build();
        }

        /**
         * CONSTRUCTION TEMPLATE: Premium Deal
         *
         * Creates a premium deal with high-value products and
         * accelerated timeline.
         *
         * CONFIGURATION:
         * - Status: OPEN (active opportunity)
         * - Products: Premium software suite + support package
         * - Close Date: 15 days from now (faster sales cycle for premium)
         *
         * BUSINESS LOGIC:
         * Premium deals get priority treatment, hence shorter close cycle.
         * Automatically includes standard premium product bundle.
         *
         * PATTERN ADVANTAGE:
         * Client doesn't need to know what products go into a premium deal
         * or what the accelerated timeline is. Director encapsulates this.
         *
         * @return a Deal configured as a premium opportunity
         */
        public Deal constructPremiumDeal() {
            // Standard premium product bundle - encapsulated business knowledge
            DealProduct premiumSoftware = new DealProduct("PROD-101", "Premium Software Suite", 1, new BigDecimal("5000.00"));
            DealProduct premiumSupport = new DealProduct("PROD-102", "Premium Support Package", 1, new BigDecimal("2000.00"));

            return builder
                    .withStatus(DealStatus.OPEN)                                    // New opportunity
                    .withProducts(Arrays.asList(premiumSoftware, premiumSupport))  // Standard bundle
                    .withCloseDate(LocalDate.now().plusDays(15))                   // Accelerated cycle
                    .build();
        }

        /**
         * CONSTRUCTION TEMPLATE: Won Deal
         *
         * Creates a deal that has already been won (closed-won).
         *
         * CONFIGURATION:
         * - Status: WON (successfully closed)
         * - Close Date: 5 days ago (in the past, deal already closed)
         *
         * USE CASE:
         * - Creating historical deals for reporting
         * - Importing legacy data
         * - Testing scenarios with completed deals
         *
         * BUSINESS LOGIC:
         * Won deals have close dates in the past and WON status.
         *
         * @return a Deal configured as a won (closed) opportunity
         */
        public Deal constructWonDeal() {
            return builder
                    .withStatus(DealStatus.WON)                     // Successfully closed
                    .withCloseDate(LocalDate.now().minusDays(5))    // Close date in past
                    .build();
        }

        /**
         * CONSTRUCTION TEMPLATE: Lost Deal
         *
         * Creates a deal that was lost to competitor or other reason.
         *
         * CONFIGURATION:
         * - Status: LOST (unsuccessfully closed)
         * - Close Date: 5 days ago (in the past, deal already closed)
         *
         * USE CASE:
         * - Creating historical data for loss analysis
         * - Testing reporting scenarios
         * - Importing legacy data
         *
         * BUSINESS LOGIC:
         * Lost deals, like won deals, have close dates in the past.
         *
         * @return a Deal configured as a lost opportunity
         */
        public Deal constructLostDeal() {
            return builder
                    .withStatus(DealStatus.LOST)                    // Lost to competitor
                    .withCloseDate(LocalDate.now().minusDays(5))    // Close date in past
                    .build();
        }
    }

    /**
     * DEMONSTRATION: Director Pattern Usage
     *
     * Shows how Director simplifies object construction by encapsulating
     * common construction sequences.
     *
     * KEY DEMONSTRATIONS:
     * 1. Creating multiple deal types from same builder
     * 2. Semantic method names (constructStandardDeal, constructPremiumDeal)
     * 3. Changing builder at runtime (same director, different base data)
     * 4. Consistent configurations across all constructions
     */
    public static void main(String[] args) {
        // STEP 1: Create a DealBuilder with required fields
        // This builder will be used as the base for all director constructions
        DealBuilder builder = new DealBuilder("Director Demo Deal", new BigDecimal("15000.00"), "SALES-004");

        // STEP 2: Create a DealDirector with the builder
        // Director will apply various construction templates to this builder
        DealDirector director = new DealDirector(builder);

        // STEP 3: Use director to construct different deal types
        // Notice how simple the client code is - just call constructXxx()
        // No need to know what fields to set or what values to use

        System.out.println("Standard Deal:");
        Deal standardDeal = director.constructStandardDeal();  // 30-day cycle, OPEN status
        printDealDetails(standardDeal);

        System.out.println("\nPremium Deal:");
        Deal premiumDeal = director.constructPremiumDeal();    // 15-day cycle, premium products
        printDealDetails(premiumDeal);

        System.out.println("\nWon Deal:");
        Deal wonDeal = director.constructWonDeal();            // WON status, past close date
        printDealDetails(wonDeal);

        System.out.println("\nLost Deal:");
        Deal lostDeal = director.constructLostDeal();          // LOST status, past close date
        printDealDetails(lostDeal);

        // STEP 4: Demonstrate changing the builder
        // Same director, different builder - construction logic is reused
        DealBuilder newBuilder = new DealBuilder("New Builder Deal", new BigDecimal("20000.00"), "SALES-005")
                .withId("DEAL-005");  // This builder has ID set, previous one didn't

        director.changeBuilder(newBuilder);  // Swap in new builder

        System.out.println("\nDeal with New Builder:");
        Deal newBuilderDeal = director.constructStandardDeal();  // Same construction logic
        printDealDetails(newBuilderDeal);                         // But different base data

        // KEY TAKEAWAY:
        // Director provides semantic, reusable construction methods.
        // Client code is clean and doesn't need to know construction details.
        // Business logic (e.g., "standard deals have 30-day cycle") is centralized.
    }

    /**
     * Utility method to print Deal details to console.
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
    }
}