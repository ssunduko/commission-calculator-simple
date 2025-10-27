package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternStructure.*;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.*;

import java.math.BigDecimal;

/**
 * BridgePatternUsage demonstrates practical applications of the Bridge Pattern
 * in the commission calculator domain.
 *
 * This class shows how the Bridge Pattern enables decoupling abstractions from
 * implementations, allowing them to vary independently and be combined flexibly.
 *
 * ### What This Example Demonstrates:
 * 1. Creating different implementors (calculation strategies)
 * 2. Creating different abstractions (commission processors)
 * 3. Combining abstractions with implementations freely (the "bridge")
 * 4. Changing implementations at runtime
 * 5. Extending abstractions and implementations independently
 * 6. Avoiding class explosion from coupled hierarchies
 *
 * ### Real-World Scenario:
 * Imagine a commission system that needs to support:
 * - Different calculation algorithms (flat rate, tiered, product-based)
 * - Different processing workflows (sales rep, manager override, partner)
 * - Runtime flexibility to change calculations without code changes
 *
 * Without the Bridge Pattern, you'd need separate classes for each combination:
 * - SalesRepFlatRateProcessor
 * - SalesRepTieredProcessor
 * - SalesRepProductBasedProcessor
 * - ManagerFlatRateProcessor
 * - ManagerTieredProcessor
 * - ManagerProductBasedProcessor
 * ... = 3 strategies × 2 processors = 6 classes (and growing!)
 *
 * With the Bridge Pattern, you have:
 * - 3 strategy classes
 * - 2 processor classes
 * ... = 5 classes total (M + N instead of M × N)
 *
 * @see BridgePatternStructure for interface and abstract class definitions
 * @see BridgePatternImplementation for concrete implementations
 */
public class BridgePatternUsage {

    /**
     * Entry point that demonstrates the Bridge Pattern usage.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Run all usage examples
        useBridgePattern();
    }

    /**
     * Demonstrates how to use the Bridge Pattern with the Commission Calculator domain model.
     *
     * This method walks through a complete example showing:
     * - Creating strategies (Implementors)
     * - Creating processors (Abstractions)
     * - Combining them in different ways (The Bridge)
     * - Changing strategies at runtime (Dynamic Bridge)
     * - Comparing different combinations
     *
     * **Key Observation:** Notice how the same processor can use different strategies,
     * and the same strategy can be used by different processors. This flexibility is
     * the core benefit of the Bridge Pattern.
     */
    public static void useBridgePattern() {
        System.out.println("=== Bridge Pattern Usage Example ===\n");

        // ========== STEP 1: Create Services ==========
        // These are dependencies needed by the abstractions (processors).
        // They provide access to domain data.

        DealService dealService = new DealServiceImpl();
        UserService userService = new UserServiceImpl();

        // ========== STEP 2: Create Implementors (Strategies) ==========
        // These are the "implementation" side of the bridge.
        // Each provides a different algorithm for calculating commissions.
        System.out.println("--- Creating Calculation Strategies (Implementors) ---");

        CommissionCalculationStrategy flatRateStrategy = new FlatRateStrategy(new BigDecimal("0.05"));
        System.out.println("Created FlatRateStrategy: 5% commission on all deals");

        CommissionCalculationStrategy tieredStrategy = new TieredValueStrategy();
        System.out.println("Created TieredValueStrategy: Variable rates based on deal size");

        CommissionCalculationStrategy productBasedStrategy = new ProductBasedStrategy();
        System.out.println("Created ProductBasedStrategy: Different rates per product type\n");
        // ========== STEP 3: Create Abstractions (Processors) ==========
        // These are the "abstraction" side of the bridge.
        // Each provides a different workflow for processing commissions.
        //
        // **THE BRIDGE:** Notice how we pass a strategy to each processor.
        // This creates the "bridge" - the processor holds a reference to the strategy.
        System.out.println("--- Creating Commission Processors (Abstractions) ---");

        CommissionProcessor salesRepProcessor = new SalesRepCommissionProcessor(
                flatRateStrategy,  // Using flat rate strategy initially
                dealService,
                userService);
        System.out.println("Created SalesRepCommissionProcessor with FlatRateStrategy");

        CommissionProcessor managerProcessor = new ManagerCommissionProcessor(
                tieredStrategy,    // Using tiered strategy initially
                dealService,
                userService,
                new BigDecimal("0.25"));  // Manager gets 25% of rep commission
        System.out.println("Created ManagerCommissionProcessor with TieredValueStrategy\n");

        // ========== STEP 4: Use Processors with Initial Strategies ==========
        // Process commissions using the initially configured strategies.
        System.out.println("--- Example 1: Sales Rep with Flat Rate Strategy ---");
        CommissionCalculation calc1 = salesRepProcessor.processCommission("deal1");
        printCommissionDetails(calc1, dealService, userService);
        // Expected: $5,000 deal × 5% = $250 commission

        System.out.println("\n--- Example 2: Manager Override with Tiered Strategy ---");
        CommissionCalculation calc2 = managerProcessor.processCommission("deal1");
        printCommissionDetails(calc2, dealService, userService);
        // Expected: ($5,000 deal × 3% tier) × 25% override = $37.50 commission

        // ========== STEP 5: Runtime Strategy Change (Dynamic Bridge) ==========
        // **KEY BRIDGE PATTERN FEATURE:** Change the implementation at runtime!
        // The abstraction (processor) can switch to a different strategy without
        // changing its own code or requiring client changes.
        System.out.println("\n--- Example 3: Runtime Strategy Change ---");
        System.out.println("Changing SalesRepProcessor from FlatRate to Tiered strategy...");
        salesRepProcessor.setStrategy(tieredStrategy);

        CommissionCalculation calc3 = salesRepProcessor.processCommission("deal2");
        System.out.println("\nProcessing same deal with new strategy:");
        printCommissionDetails(calc3, dealService, userService);
        // Expected: $15,000 deal at 5% tier = $750 commission

        // ========== STEP 6: Another Runtime Change ==========
        // Change to product-based strategy for the same processor.
        System.out.println("\n--- Example 4: Changing to Product-Based Strategy ---");
        System.out.println("Changing SalesRepProcessor to ProductBased strategy...");
        salesRepProcessor.setStrategy(productBasedStrategy);

        CommissionCalculation calc4 = salesRepProcessor.processCommission("deal2");
        System.out.println("\nProcessing same deal with product-based strategy:");
        printCommissionDetails(calc4, dealService, userService);
        // Expected: Software products at 10% = $15,000 × 10% = $1,500 commission

        // ========== STEP 7: Compare Strategies for Same Deal ==========
        // Demonstrate how the same deal can produce different results with
        // different strategies. This shows the value of the Bridge Pattern's
        // flexibility.

        System.out.println("\n--- Example 5: Strategy Comparison for Deal ---");
        compareStrategiesForDeal("deal3", dealService, userService);

        // ========== STEP 8: Demonstrate Independent Extension ==========
        // Show how we can mix and match any strategy with any processor.
        // This demonstrates the core benefit: M strategies + N processors = M×N combinations
        // without creating M×N classes!

        System.out.println("\n--- Example 6: Mix and Match Demonstration ---");
        System.out.println("Combining different processors with different strategies:\n");

        // Combination 1: SalesRep + FlatRate
        salesRepProcessor.setStrategy(flatRateStrategy);
        CommissionCalculation mix1 = salesRepProcessor.processCommission("deal1");
        System.out.println("1. SalesRep + FlatRate: $" + mix1.getBaseCommission());

        // Combination 2: SalesRep + Tiered
        salesRepProcessor.setStrategy(tieredStrategy);
        CommissionCalculation mix2 = salesRepProcessor.processCommission("deal1");
        System.out.println("2. SalesRep + Tiered: $" + mix2.getBaseCommission());

        // Combination 3: SalesRep + ProductBased
        salesRepProcessor.setStrategy(productBasedStrategy);
        CommissionCalculation mix3 = salesRepProcessor.processCommission("deal1");
        System.out.println("3. SalesRep + ProductBased: $" + mix3.getBaseCommission());

        // Combination 4: Manager + FlatRate
        managerProcessor.setStrategy(flatRateStrategy);
        CommissionCalculation mix4 = managerProcessor.processCommission("deal1");
        System.out.println("4. Manager + FlatRate: $" + mix4.getBaseCommission());

        // Combination 5: Manager + Tiered
        managerProcessor.setStrategy(tieredStrategy);
        CommissionCalculation mix5 = managerProcessor.processCommission("deal1");
        System.out.println("5. Manager + Tiered: $" + mix5.getBaseCommission());

        // Combination 6: Manager + ProductBased
        managerProcessor.setStrategy(productBasedStrategy);
        CommissionCalculation mix6 = managerProcessor.processCommission("deal1");
        System.out.println("6. Manager + ProductBased: $" + mix6.getBaseCommission());

        System.out.println("\nAll 6 combinations work seamlessly without creating 6 different classes!");

        // ========== Summary of Benefits Demonstrated ==========
        System.out.println("\n--- Bridge Pattern Benefits Demonstrated ---");
        System.out.println("1. DECOUPLING: Abstractions and implementations vary independently");
        System.out.println("2. RUNTIME FLEXIBILITY: Can change implementations dynamically");
        System.out.println("3. REDUCED CLASS COUNT: M+N classes instead of M×N classes");
        System.out.println("4. EASY EXTENSION: Add new strategies or processors without affecting others");
        System.out.println("5. SINGLE RESPONSIBILITY: Each class has one reason to change");
        System.out.println("6. TESTABILITY: Can test strategies and processors independently");
    }

    /**
     * Print the details of a commission calculation.
     *
     * Helper method to display commission calculation results in a readable format.
     *
     * @param calculation The commission calculation to display
     * @param dealService Service for retrieving deal information
     * @param userService Service for retrieving user information
     */
    private static void printCommissionDetails(CommissionCalculation calculation,
                                               DealService dealService,
                                               UserService userService) {
        Deal deal = dealService.getDealById(calculation.getDealId());
        User user = userService.getUserById(calculation.getSalesRepId());

        System.out.println("Deal: " + deal.getTitle() + " (Value: $" + deal.getValue() + ")");
        System.out.println("User: " + user.getFullName() + " (" + calculation.getSalesRepId() + ")");
        System.out.println("Commission Amount: $" + calculation.getBaseCommission());
        System.out.println("Calculated By: " + calculation.getCalculatedBy());
    }

    /**
     * Compare different calculation strategies for the same deal.
     *
     * This method demonstrates the flexibility of the Bridge Pattern by showing
     * how the same deal can be processed with different strategies to produce
     * different results.
     *
     * **Bridge Pattern Benefit:** We can easily compare algorithms side-by-side
     * because they all implement the same interface. Adding a new strategy
     * automatically makes it available for comparison.
     *
     * @param dealId The ID of the deal to compare strategies for
     * @param dealService Service for retrieving deals
     * @param userService Service for retrieving users
     */
    private static void compareStrategiesForDeal(String dealId,
                                                  DealService dealService,
                                                  UserService userService) {
        Deal deal = dealService.getDealById(dealId);
        User user = userService.getUserById(deal.getSalesRepId());

        System.out.println("Comparing strategies for: " + deal.getTitle() +
                " (Value: $" + deal.getValue() + ")\n");

        // Create all three strategies
        CommissionCalculationStrategy flatRateStrategy =
                new FlatRateStrategy(new BigDecimal("0.05"));
        CommissionCalculationStrategy tieredStrategy =
                new TieredValueStrategy();
        CommissionCalculationStrategy productBasedStrategy =
                new ProductBasedStrategy();

        // Calculate commission using each strategy
        BigDecimal flatRateCommission = flatRateStrategy.calculateCommission(
                deal, user, userService.getCommissionPlan(user.getId()));
        BigDecimal tieredCommission = tieredStrategy.calculateCommission(
                deal, user, userService.getCommissionPlan(user.getId()));
        BigDecimal productBasedCommission = productBasedStrategy.calculateCommission(
                deal, user, userService.getCommissionPlan(user.getId()));

        // Print comparison
        System.out.println("Strategy Results:");
        System.out.println("  • Flat Rate (5%): $" + flatRateCommission);
        System.out.println("  • Tiered Value: $" + tieredCommission);
        System.out.println("  • Product-Based: $" + productBasedCommission);

        // Determine the best strategy for the sales rep
        BigDecimal bestCommission = flatRateCommission;
        String bestStrategy = "Flat Rate (5%)";

        if (tieredCommission.compareTo(bestCommission) > 0) {
            bestCommission = tieredCommission;
            bestStrategy = "Tiered Value";
        }

        if (productBasedCommission.compareTo(bestCommission) > 0) {
            bestCommission = productBasedCommission;
            bestStrategy = "Product-Based";
        }

        System.out.println("\nBest Strategy for Rep: " + bestStrategy + " ($" + bestCommission + ")");

        // Calculate difference
        BigDecimal difference = bestCommission.subtract(flatRateCommission);
        if (difference.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Potential Extra Earnings: $" + difference +
                    " compared to flat rate");
        }
    }
}