package com.chapman.edu.commissions.patterns.behavioral.strategy;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.strategy.StrategyImplementation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * STRATEGY PATTERN - USAGE DEMONSTRATION
 *
 * This class demonstrates how to use the Strategy Pattern for commission calculations.
 * It shows how different strategies can be selected and swapped at runtime based on
 * business requirements.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. Runtime strategy selection based on deal characteristics
 * 2. Combining multiple strategies (commission + bonus + payment terms)
 * 3. Strategy composition for complex calculations
 * 4. Easy addition of new strategies without modifying existing code
 *
 * REAL-WORLD SCENARIOS:
 * - Different commission plans for different sales teams
 * - Seasonal bonus strategies
 * - Payment term adjustments based on customer creditworthiness
 * - A/B testing different commission structures
 */
public class StrategyUsage {

    /**
     * Demonstrate basic strategy usage with different commission strategies.
     */
    public static void demonstrateBasicStrategies() {
        System.out.println("=".repeat(80));
        System.out.println("STRATEGY PATTERN DEMO: Basic Commission Strategies");
        System.out.println("=".repeat(80));

        // Create sample deals
        Deal smallDeal = new Deal("Small Software License", new BigDecimal("5000"), "REP-001");
        smallDeal.setId("DEAL-001");
        smallDeal.setCloseDate(LocalDate.now());
        smallDeal.setStatus(DealStatus.WON);

        Deal mediumDeal = new Deal("Medium Enterprise Deal", new BigDecimal("75000"), "REP-002");
        mediumDeal.setId("DEAL-002");
        mediumDeal.setCloseDate(LocalDate.now());
        mediumDeal.setStatus(DealStatus.WON);

        Deal largeDeal = new Deal("Large Strategic Deal", new BigDecimal("250000"), "REP-003");
        largeDeal.setId("DEAL-003");
        largeDeal.setCloseDate(LocalDate.now());
        largeDeal.setStatus(DealStatus.WON);

        // Create calculator
        CommissionCalculator calculator = new CommissionCalculator();

        // Scenario 1: Standard 10% rate
        System.out.println("\n--- Scenario 1: Standard Rate Strategy (10%) ---");
        calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));

        System.out.println("\nDeal: " + smallDeal.getTitle() + " ($" + smallDeal.getValue() + ")");
        BigDecimal commission1 = calculator.calculateTotalCommission(smallDeal, 30);
        System.out.println("Total Commission: $" + commission1);

        System.out.println("\nDeal: " + mediumDeal.getTitle() + " ($" + mediumDeal.getValue() + ")");
        BigDecimal commission2 = calculator.calculateTotalCommission(mediumDeal, 30);
        System.out.println("Total Commission: $" + commission2);

        // Scenario 2: Tiered rate strategy
        System.out.println("\n\n--- Scenario 2: Tiered Rate Strategy ---");
        calculator.setCommissionStrategy(new TieredRateStrategy());

        System.out.println("\nDeal: " + smallDeal.getTitle() + " ($" + smallDeal.getValue() + ")");
        BigDecimal commission3 = calculator.calculateTotalCommission(smallDeal, 30);
        System.out.println("Total Commission: $" + commission3);

        System.out.println("\nDeal: " + mediumDeal.getTitle() + " ($" + mediumDeal.getValue() + ")");
        BigDecimal commission4 = calculator.calculateTotalCommission(mediumDeal, 30);
        System.out.println("Total Commission: $" + commission4);

        System.out.println("\nDeal: " + largeDeal.getTitle() + " ($" + largeDeal.getValue() + ")");
        BigDecimal commission5 = calculator.calculateTotalCommission(largeDeal, 30);
        System.out.println("Total Commission: $" + commission5);

        // Scenario 3: Flat fee strategy
        System.out.println("\n\n--- Scenario 3: Flat Fee Strategy ($500) ---");
        calculator.setCommissionStrategy(new FlatFeeStrategy(new BigDecimal("500")));

        System.out.println("\nDeal: " + smallDeal.getTitle() + " ($" + smallDeal.getValue() + ")");
        BigDecimal commission6 = calculator.calculateTotalCommission(smallDeal, 30);
        System.out.println("Total Commission: $" + commission6);

        System.out.println("\nDeal: " + largeDeal.getTitle() + " ($" + largeDeal.getValue() + ")");
        BigDecimal commission7 = calculator.calculateTotalCommission(largeDeal, 30);
        System.out.println("Total Commission: $" + commission7);

        // Scenario 4: Gross profit strategy
        System.out.println("\n\n--- Scenario 4: Gross Profit Strategy (20% rate, 40% costs) ---");
        calculator.setCommissionStrategy(new GrossProfitStrategy(new BigDecimal("20"), new BigDecimal("40")));

        System.out.println("\nDeal: " + mediumDeal.getTitle() + " ($" + mediumDeal.getValue() + ")");
        BigDecimal commission8 = calculator.calculateTotalCommission(mediumDeal, 30);
        System.out.println("Total Commission: $" + commission8);
    }

    /**
     * Demonstrate bonus strategies combined with commission strategies.
     */
    public static void demonstrateBonusStrategies() {
        System.out.println("\n\n");
        System.out.println("=".repeat(80));
        System.out.println("STRATEGY PATTERN DEMO: Bonus Strategies");
        System.out.println("=".repeat(80));

        // Create deals
        Deal highValueDeal = new Deal("Premium Enterprise Deal", new BigDecimal("150000"), "REP-004");
        highValueDeal.setId("DEAL-004");
        highValueDeal.setCloseDate(LocalDate.of(2024, 2, 15)); // Early in quarter
        highValueDeal.setStatus(DealStatus.WON);

        Deal newCustomerDeal = new Deal("New Customer Acquisition", new BigDecimal("60000"), "REP-005");
        newCustomerDeal.setId("DEAL-005");
        newCustomerDeal.setCloseDate(LocalDate.now());
        newCustomerDeal.setStatus(DealStatus.WON);

        CommissionCalculator calculator = new CommissionCalculator();
        calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));

        // Scenario 1: Quota achievement bonus
        System.out.println("\n--- Scenario 1: Quota Achievement Bonus (50% extra for deals > $100k) ---");
        calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(
                new BigDecimal("100000"), new BigDecimal("50")));

        System.out.println("\nDeal: " + highValueDeal.getTitle() + " ($" + highValueDeal.getValue() + ")");
        BigDecimal commission1 = calculator.calculateTotalCommission(highValueDeal, 30);
        System.out.println("Total Commission: $" + commission1);

        // Scenario 2: Early close bonus
        System.out.println("\n\n--- Scenario 2: Early Close Bonus ($2000 for closing early in quarter) ---");
        calculator.setBonusStrategy(new EarlyCloseBonusStrategy(new BigDecimal("2000")));

        System.out.println("\nDeal: " + highValueDeal.getTitle() +
                " (Closed: " + highValueDeal.getCloseDate() + ")");
        BigDecimal commission2 = calculator.calculateTotalCommission(highValueDeal, 30);
        System.out.println("Total Commission: $" + commission2);

        // Scenario 3: New customer bonus
        System.out.println("\n\n--- Scenario 3: New Customer Bonus (25% extra) ---");
        calculator.setBonusStrategy(new NewCustomerBonusStrategy(new BigDecimal("25")));

        System.out.println("\nDeal: " + newCustomerDeal.getTitle() + " ($" + newCustomerDeal.getValue() + ")");
        BigDecimal commission3 = calculator.calculateTotalCommission(newCustomerDeal, 30);
        System.out.println("Total Commission: $" + commission3);
    }

    /**
     * Demonstrate payment terms strategies.
     */
    public static void demonstratePaymentTermsStrategies() {
        System.out.println("\n\n");
        System.out.println("=".repeat(80));
        System.out.println("STRATEGY PATTERN DEMO: Payment Terms Strategies");
        System.out.println("=".repeat(80));

        Deal deal = new Deal("Enterprise Software License", new BigDecimal("100000"), "REP-006");
        deal.setId("DEAL-006");
        deal.setCloseDate(LocalDate.now());
        deal.setStatus(DealStatus.WON);

        CommissionCalculator calculator = new CommissionCalculator();
        calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));

        // Scenario 1: Immediate payment
        System.out.println("\n--- Scenario 1: Immediate Payment Strategy ---");
        calculator.setPaymentTermsStrategy(new ImmediatePaymentStrategy());

        System.out.println("\nDeal: " + deal.getTitle() + " ($" + deal.getValue() + ")");
        System.out.println("Payment Terms: Net 30");
        BigDecimal commission1 = calculator.calculateTotalCommission(deal, 30);
        System.out.println("Total Commission: $" + commission1);

        // Scenario 2: Deferred payment with discount
        System.out.println("\n\n--- Scenario 2: Deferred Payment Strategy (2% reduction per month) ---");
        calculator.setPaymentTermsStrategy(new DeferredPaymentStrategy(new BigDecimal("2")));

        System.out.println("\nDeal: " + deal.getTitle() + " ($" + deal.getValue() + ")");
        System.out.println("Payment Terms: Net 30");
        BigDecimal commission2 = calculator.calculateTotalCommission(deal, 30);
        System.out.println("Total Commission: $" + commission2);

        System.out.println("\nDeal: " + deal.getTitle() + " ($" + deal.getValue() + ")");
        System.out.println("Payment Terms: Net 90");
        BigDecimal commission3 = calculator.calculateTotalCommission(deal, 90);
        System.out.println("Total Commission: $" + commission3);

        // Scenario 3: Split payment
        System.out.println("\n\n--- Scenario 3: Split Payment Strategy (60% immediate, 40% on collection) ---");
        calculator.setPaymentTermsStrategy(new SplitPaymentStrategy(new BigDecimal("60")));

        System.out.println("\nDeal: " + deal.getTitle() + " ($" + deal.getValue() + ")");
        System.out.println("Payment Terms: Net 60");
        BigDecimal commission4 = calculator.calculateTotalCommission(deal, 60);
        System.out.println("Immediate Commission: $" + commission4);
    }

    /**
     * Demonstrate combining all three types of strategies.
     */
    public static void demonstrateCombinedStrategies() {
        System.out.println("\n\n");
        System.out.println("=".repeat(80));
        System.out.println("STRATEGY PATTERN DEMO: Combined Strategies");
        System.out.println("=".repeat(80));

        Deal premiumDeal = new Deal("New Strategic Partnership", new BigDecimal("200000"), "REP-007");
        premiumDeal.setId("DEAL-007");
        premiumDeal.setCloseDate(LocalDate.of(2024, 1, 20)); // Early in quarter
        premiumDeal.setStatus(DealStatus.WON);

        CommissionCalculator calculator = new CommissionCalculator();

        // Configure all three strategy types
        calculator.setCommissionStrategy(new TieredRateStrategy());
        calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(
                new BigDecimal("100000"), new BigDecimal("50")));
        calculator.setPaymentTermsStrategy(new SplitPaymentStrategy(new BigDecimal("70")));

        System.out.println("\n--- Full Strategy Configuration ---");
        System.out.println(calculator.getStrategySummary());

        System.out.println("\nDeal: " + premiumDeal.getTitle() + " ($" + premiumDeal.getValue() + ")");
        System.out.println("Payment Terms: Net 45");
        BigDecimal totalCommission = calculator.calculateTotalCommission(premiumDeal, 45);
        System.out.println("\n>>> TOTAL COMMISSION: $" + totalCommission + " <<<");
    }

    /**
     * Demonstrate dynamic strategy selection based on deal characteristics.
     */
    public static void demonstrateDynamicStrategySelection() {
        System.out.println("\n\n");
        System.out.println("=".repeat(80));
        System.out.println("STRATEGY PATTERN DEMO: Dynamic Strategy Selection");
        System.out.println("=".repeat(80));

        Deal[] deals = {
                createDeal("DEAL-008", "Small Deal", new BigDecimal("8000"), "REP-008", LocalDate.now()),
                createDeal("DEAL-009", "New Customer Deal", new BigDecimal("45000"), "REP-009", LocalDate.now()),
                createDeal("DEAL-010", "Large Strategic Deal", new BigDecimal("175000"), "REP-010",
                        LocalDate.of(2024, 2, 10))
        };

        System.out.println("\n--- Processing Multiple Deals with Dynamic Strategy Selection ---\n");

        for (Deal deal : deals) {
            System.out.println("\n" + "-".repeat(70));
            System.out.println("Processing: " + deal.getTitle() + " (ID: " + deal.getId() + ")");
            System.out.println("Value: $" + deal.getValue());

            CommissionCalculator calculator = new CommissionCalculator();

            // Select commission strategy based on deal size
            if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
                calculator.setCommissionStrategy(new TieredRateStrategy());
                System.out.println("Selected: Tiered Rate Strategy (large deal)");
            } else if (deal.getValue().compareTo(new BigDecimal("10000")) < 0) {
                calculator.setCommissionStrategy(new FlatFeeStrategy(new BigDecimal("500")));
                System.out.println("Selected: Flat Fee Strategy (small deal)");
            } else {
                calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));
                System.out.println("Selected: Standard Rate Strategy (medium deal)");
            }

            // Add bonus for new customers
            if (deal.getTitle().toLowerCase().contains("new")) {
                calculator.setBonusStrategy(new NewCustomerBonusStrategy(new BigDecimal("25")));
                System.out.println("Applied: New Customer Bonus");
            }

            // Add quota bonus for large deals
            if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
                calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(
                        new BigDecimal("100000"), new BigDecimal("50")));
                System.out.println("Applied: Quota Achievement Bonus");
            }

            // Payment terms based on deal size
            if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
                calculator.setPaymentTermsStrategy(new SplitPaymentStrategy(new BigDecimal("60")));
                System.out.println("Applied: Split Payment (60/40)");
            } else {
                calculator.setPaymentTermsStrategy(new ImmediatePaymentStrategy());
                System.out.println("Applied: Immediate Payment");
            }

            System.out.println("\nCalculating commission:");
            BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
            System.out.println("\n>>> Total Commission: $" + commission + " <<<");
        }
    }

    /**
     * Helper method to create a deal with all properties.
     */
    private static Deal createDeal(String id, String title, BigDecimal value,
                                   String salesRepId, LocalDate closeDate) {
        Deal deal = new Deal(title, value, salesRepId);
        deal.setId(id);
        deal.setCloseDate(closeDate);
        deal.setStatus(DealStatus.WON);
        return deal;
    }

    /**
     * Main method to run all demonstrations.
     */
    public static void main(String[] args) {
        demonstrateBasicStrategies();
        demonstrateBonusStrategies();
        demonstratePaymentTermsStrategies();
        demonstrateCombinedStrategies();
        demonstrateDynamicStrategySelection();

        System.out.println("\n\n");
        System.out.println("=".repeat(80));
        System.out.println("END OF STRATEGY PATTERN DEMONSTRATION");
        System.out.println("=".repeat(80));
        System.out.println("\nKey Takeaways:");
        System.out.println("1. Strategies encapsulate algorithms and make them interchangeable");
        System.out.println("2. New strategies can be added without modifying existing code");
        System.out.println("3. Strategies can be selected dynamically at runtime");
        System.out.println("4. Multiple strategies can be composed for complex calculations");
        System.out.println("5. Strategy pattern follows Open/Closed Principle");
    }
}