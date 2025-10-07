package com.chapman.edu.commissions.patterns.behavioral.template;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.template.TemplateImplementation.*;
import com.chapman.edu.commissions.patterns.behavioral.template.TemplateStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * TEMPLATE METHOD PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Template Method Pattern in the context of commission calculations.
 *
 * DEMONSTRATES:
 * 1. Using the template method with different concrete implementations
 * 2. How the algorithm structure remains consistent across implementations
 * 3. How hooks provide optional customization points
 * 4. Polymorphic usage of calculators
 * 5. Comparing results across different calculator types
 * 6. Error handling and validation
 *
 * KEY LEARNING POINTS:
 * - Same algorithm structure produces different results based on implementation
 * - Template method enforces process consistency
 * - Hooks allow flexible customization without breaking the template
 * - Easy to add new calculator types without changing existing code
 *
 */
public class TemplateUsage {

    /**
     * EXAMPLE 1: Standard Commission Calculation Flow
     *
     * Demonstrates the typical usage of each calculator type with
     * standard deals that trigger all calculation steps.
     */
    public static void exampleStandardFlow() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 1: Standard Commission Calculations         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create deals that will showcase each calculator's features
        Deal softwareDeal = createDeal("CRM Software Suite",
                                      new BigDecimal("85000"),
                                      LocalDate.of(2024, 3, 28),
                                      DealStatus.WON);

        Deal hardwareDeal = createDeal("Server Infrastructure",
                                      new BigDecimal("150000"),
                                      LocalDate.of(2024, 6, 15),
                                      DealStatus.WON);

        System.out.println("📊 CALCULATION 1: Software Deal ($85,000)\n");
        CommissionCalculator softwareCalc = new SoftwareCommissionCalculator();
        CommissionResult softwareResult = softwareCalc.calculateCommission(softwareDeal);
        softwareResult.displayReport();

        System.out.println("\n📊 CALCULATION 2: Hardware Deal ($150,000)\n");
        CommissionCalculator hardwareCalc = new HardwareCommissionCalculator();
        CommissionResult hardwareResult = hardwareCalc.calculateCommission(hardwareDeal);
        hardwareResult.displayReport();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Both calculations followed the same 6-step workflow,");
        System.out.println("but produced different results based on their specific logic.");
        System.out.println();
    }

    /**
     * EXAMPLE 2: Time-Based Modifier Scenarios
     *
     * Shows how different calculators handle time-based modifiers differently.
     */
    public static void exampleTimeBasedModifiers() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 2: Time-Based Modifiers (Quarter-End)       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create quarter-end deals
        Deal servicesQ4Deal = createDeal("Year-End Consulting",
                                        new BigDecimal("50000"),
                                        LocalDate.of(2024, 12, 28), // Last week of Q4
                                        DealStatus.WON);

        Deal hardwareQ4Deal = createDeal("Hardware Refresh",
                                        new BigDecimal("50000"),
                                        LocalDate.of(2024, 12, 28), // Same date
                                        DealStatus.WON);

        System.out.println("📅 Scenario: Two $50k deals closed on Dec 28 (quarter-end)\n");

        System.out.println("🔹 SERVICES DEAL (Enhanced Time Modifiers):\n");
        CommissionCalculator servicesCalc = new ServicesCommissionCalculator();
        CommissionResult servicesResult = servicesCalc.calculateCommission(servicesQ4Deal);
        servicesResult.displayReport();

        System.out.println("\n🔹 HARDWARE DEAL (No Time Modifiers):\n");
        CommissionCalculator hardwareCalc = new HardwareCommissionCalculator();
        CommissionResult hardwareResult = hardwareCalc.calculateCommission(hardwareQ4Deal);
        hardwareResult.displayReport();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Services calculator applied 10% quarter-end + 5% year-end bonuses");
        System.out.println("Hardware calculator disabled time modifiers (hook override)");
        System.out.println("Same deal timing, different results due to hook customization!");
        System.out.println();
    }

    /**
     * EXAMPLE 3: Caps and Floors Behavior
     *
     * Demonstrates how different calculators apply (or don't apply) caps and floors.
     */
    public static void exampleCapsAndFloors() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 3: Caps and Floors Application            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Small deal that will hit floor
        Deal smallDeal = createDeal("Small Training Package",
                                   new BigDecimal("500"),
                                   LocalDate.now(),
                                   DealStatus.WON);

        // Large deal that might hit cap
        Deal hugeDeal = createDeal("Enterprise-Wide Services",
                                  new BigDecimal("500000"),
                                  LocalDate.now(),
                                  DealStatus.WON);

        System.out.println("Scenario A: Small Deal - Will Floor Apply?\n");

        System.out.println("🔹 Training Calculator (Custom Floor: $500):\n");
        CommissionCalculator trainingCalc = new TrainingCommissionCalculator();
        CommissionResult trainingResult = trainingCalc.calculateCommission(smallDeal);
        System.out.println("Final Commission: $" + trainingResult.getFinalCommission());

        System.out.println("\n" + "=".repeat(60) + "\n");
        System.out.println("Scenario B: Huge Deal - Will Cap Apply?\n");

        System.out.println("🔹 Software Calculator (Cap: 50% of deal value):\n");
        CommissionCalculator softwareCalc = new SoftwareCommissionCalculator();
        CommissionResult softwareResult = softwareCalc.calculateCommission(hugeDeal);
        System.out.println("Deal Value: $" + hugeDeal.getValue());
        System.out.println("Final Commission: $" + softwareResult.getFinalCommission());
        System.out.println("Cap (50% of deal): $" + hugeDeal.getValue().multiply(new BigDecimal("0.50")));

        System.out.println("\n🔹 Services Calculator (NO CAPS):\n");
        CommissionCalculator servicesCalc = new ServicesCommissionCalculator();
        CommissionResult servicesResult = servicesCalc.calculateCommission(hugeDeal);
        System.out.println("Final Commission: $" + servicesResult.getFinalCommission());

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Services calculator disabled caps (hook returns false)");
        System.out.println("Software calculator applied cap at 50% of deal value");
        System.out.println("Same input, vastly different results due to hook customization!");
        System.out.println();
    }

    /**
     * EXAMPLE 4: Polymorphic Usage Pattern
     *
     * Shows how to use the template method polymorphically with a factory approach.
     */
    public static void examplePolymorphicUsage() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 4: Polymorphic Usage (Factory Pattern)     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create a map of calculators (factory approach)
        Map<String, CommissionCalculator> calculatorFactory = new HashMap<>();
        calculatorFactory.put("SOFTWARE", new SoftwareCommissionCalculator());
        calculatorFactory.put("HARDWARE", new HardwareCommissionCalculator());
        calculatorFactory.put("SERVICES", new ServicesCommissionCalculator());
        calculatorFactory.put("TRAINING", new TrainingCommissionCalculator());

        // Create deals with type metadata
        String[] dealTypes = {"SOFTWARE", "HARDWARE", "SERVICES", "TRAINING"};
        BigDecimal[] dealValues = {
            new BigDecimal("60000"),
            new BigDecimal("80000"),
            new BigDecimal("40000"),
            new BigDecimal("12000")
        };

        System.out.println("Processing batch of deals with appropriate calculators:\n");

        for (int i = 0; i < dealTypes.length; i++) {
            String type = dealTypes[i];
            BigDecimal value = dealValues[i];

            Deal deal = createDeal(type + " Deal", value, LocalDate.now(), DealStatus.WON);

            // Polymorphic call - don't know which concrete calculator at compile time
            CommissionCalculator calculator = calculatorFactory.get(type);
            CommissionResult result = calculator.calculateCommission(deal);

            System.out.println("  " + type + " ($" + value + ") → Commission: $" +
                             result.getFinalCommission());
        }

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("The same method call (calculateCommission) works for all types");
        System.out.println("Client code doesn't need to know which concrete calculator it's using");
        System.out.println("This is polymorphism + Template Method working together!");
        System.out.println();
    }

    /**
     * EXAMPLE 5: Validation and Error Handling
     *
     * Demonstrates how the template method handles invalid inputs.
     */
    public static void exampleValidationErrorHandling() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 5: Validation and Error Handling             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        CommissionCalculator calculator = new SoftwareCommissionCalculator();

        // Test Case 1: Null deal
        System.out.println("Test Case 1: Null Deal\n");
        try {
            calculator.calculateCommission(null);
            System.out.println("❌ Should have thrown exception!");
        } catch (NullPointerException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }

        // Test Case 2: Deal not won
        System.out.println("\nTest Case 2: Deal Not Won\n");
        Deal openDeal = createDeal("Pending Deal", new BigDecimal("50000"),
                                  LocalDate.now(), DealStatus.OPEN);
        try {
            calculator.calculateCommission(openDeal);
            System.out.println("❌ Should have thrown exception!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }

        // Test Case 3: Zero value deal
        System.out.println("\nTest Case 3: Zero Value Deal\n");
        Deal zeroDeal = createDeal("Invalid Deal", BigDecimal.ZERO,
                                  LocalDate.now(), DealStatus.WON);
        try {
            calculator.calculateCommission(zeroDeal);
            System.out.println("❌ Should have thrown exception!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Correctly rejected: " + e.getMessage());
        }

        // Test Case 4: Valid deal
        System.out.println("\nTest Case 4: Valid Deal\n");
        Deal validDeal = createDeal("Valid Deal", new BigDecimal("50000"),
                                   LocalDate.now(), DealStatus.WON);
        try {
            CommissionResult result = calculator.calculateCommission(validDeal);
            System.out.println("✓ Successfully calculated: $" + result.getFinalCommission());
        } catch (Exception e) {
            System.out.println("❌ Should not have thrown exception: " + e.getMessage());
        }

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Validation is performed in the template method (concrete step)");
        System.out.println("ALL calculator types get the same validation for free");
        System.out.println("This is code reuse - write once, benefit everywhere!");
        System.out.println();
    }

    /**
     * EXAMPLE 6: Comparing Calculator Behaviors
     *
     * Shows side-by-side comparison of how different calculators handle the same deal.
     */
    public static void exampleComparingCalculators() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 6: Side-by-Side Calculator Comparison        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create one deal to process with multiple calculators
        Deal testDeal = createDeal("Multi-Product Deal",
                                  new BigDecimal("75000"),
                                  LocalDate.of(2024, 6, 28), // Quarter-end
                                  DealStatus.WON);

        System.out.println("Scenario: Same $75k deal, quarter-end close\n");
        System.out.println("How would different calculators handle it?\n");
        System.out.println("=".repeat(60));

        // Try all calculators
        CommissionCalculator[] calculators = {
            new SoftwareCommissionCalculator(),
            new HardwareCommissionCalculator(),
            new ServicesCommissionCalculator(),
            new TrainingCommissionCalculator()
        };

        String[] names = {"Software", "Hardware", "Services", "Training"};

        System.out.println("\nCOMPARISON TABLE:");
        System.out.println("-".repeat(60));
        System.out.printf("%-15s | %-20s%n", "Calculator", "Final Commission");
        System.out.println("-".repeat(60));

        for (int i = 0; i < calculators.length; i++) {
            CommissionResult result = calculators[i].calculateCommission(testDeal);
            System.out.printf("%-15s | $%-19s%n", names[i],
                            result.getFinalCommission().toString());
        }

        System.out.println("-".repeat(60));

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Same deal value ($75k), same close date (quarter-end)");
        System.out.println("But commissions vary widely based on:");
        System.out.println("  • Base calculation method (abstract method implementation)");
        System.out.println("  • Bonus rules (abstract method implementation)");
        System.out.println("  • Time modifier handling (hook override)");
        System.out.println("  • Cap/floor application (hook override)");
        System.out.println();
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples to show different usage patterns.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║    TEMPLATE METHOD PATTERN - COMPREHENSIVE USAGE          ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates real-world commission calculation patterns  ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        // Run all examples
        exampleStandardFlow();
        pause();

        exampleTimeBasedModifiers();
        pause();

        exampleCapsAndFloors();
        pause();

        examplePolymorphicUsage();
        pause();

        exampleValidationErrorHandling();
        pause();

        exampleComparingCalculators();

        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. ALGORITHM CONSISTENCY");
        System.out.println("   → All calculators follow the same 6-step process");
        System.out.println("   → Template method enforces this structure (it's final)");
        System.out.println();
        System.out.println("2. CONTROLLED VARIATION");
        System.out.println("   → Abstract methods = required customization points");
        System.out.println("   → Hook methods = optional customization points");
        System.out.println("   → Concrete methods = shared behavior (no duplication)");
        System.out.println();
        System.out.println("3. HOLLYWOOD PRINCIPLE");
        System.out.println("   → \"Don't call us, we'll call you\"");
        System.out.println("   → Parent class controls flow, calls child methods");
        System.out.println("   → Inversion of control");
        System.out.println();
        System.out.println("4. CODE REUSE");
        System.out.println("   → Validation logic written once, used everywhere");
        System.out.println("   → Logging logic written once, used everywhere");
        System.out.println("   → Default hooks provide sensible behavior");
        System.out.println();
        System.out.println("5. OPEN/CLOSED PRINCIPLE");
        System.out.println("   → Easy to add new calculator types (extend)");
        System.out.println("   → Don't modify existing calculators (closed)");
        System.out.println("   → Template method itself is final (protected from change)");
        System.out.println();
        System.out.println("6. POLYMORPHISM");
        System.out.println("   → Client code works with base class reference");
        System.out.println("   → Actual behavior determined by concrete type");
        System.out.println("   → Perfect for factory patterns");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Helper method to create deals for examples
     */
    private static Deal createDeal(String title, BigDecimal value, LocalDate closeDate, DealStatus status) {
        Deal deal = new Deal(title, value, "REP-123");
        deal.setId("DEAL-" + System.currentTimeMillis());
        deal.setCloseDate(closeDate);
        deal.setStatus(status);
        return deal;
    }

    /**
     * Pause between examples for readability
     */
    private static void pause() {
        System.out.println("\n[Press Enter to continue to next example...]");
        System.out.println("─".repeat(60) + "\n");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}