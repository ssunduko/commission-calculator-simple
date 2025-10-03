package com.chapman.edu.commissions.patterns.behavioral.template;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TEMPLATE METHOD PATTERN - COMMISSION CALCULATION IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation demonstrates commission calculation for different types of sales deals.
 * All commission calculations follow the same overall process (validate, calculate base,
 * apply modifiers, apply bonuses, log), but the specific calculations vary by deal type.
 *
 * BUSINESS CONTEXT:
 * Different product types (Software, Hardware, Services, Training) have different commission
 * structures, but they all follow the same calculation workflow. The Template Method pattern
 * eliminates code duplication while allowing each product type to have its unique calculation logic.
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Ensures all commission calculations follow the same validation and logging process
 * 2. Eliminates duplicate code across different commission calculators
 * 3. Makes it easy to add new commission types without changing existing code
 * 4. Enforces consistent calculation workflow across the system
 * 5. Separates invariant workflow from variant calculation logic
 *
 * WORKFLOW STEPS:
 * 1. Validate deal (concrete - same for all)
 * 2. Calculate base commission (abstract - varies by type)
 * 3. Apply time-based modifiers (hook - optional)
 * 4. Apply performance bonuses (abstract - varies by type)
 * 5. Apply caps and floors (hook - optional)
 * 6. Log calculation details (concrete - same for all)
 *
 * @author Commission Calculator Educational Project
 */
public class TemplateImplementation {

    /**
     * COMMISSION CALCULATION RESULT
     *
     * Value object to hold the result of a commission calculation.
     */
    public static class CommissionResult {
        private final BigDecimal baseCommission;
        private final BigDecimal modifiers;
        private final BigDecimal bonuses;
        private final BigDecimal adjustments;
        private final BigDecimal finalCommission;
        private final List<String> calculationSteps;

        public CommissionResult(BigDecimal baseCommission, BigDecimal modifiers,
                              BigDecimal bonuses, BigDecimal adjustments,
                              BigDecimal finalCommission, List<String> calculationSteps) {
            this.baseCommission = baseCommission;
            this.modifiers = modifiers;
            this.bonuses = bonuses;
            this.adjustments = adjustments;
            this.finalCommission = finalCommission;
            this.calculationSteps = new ArrayList<>(calculationSteps);
        }

        public BigDecimal getFinalCommission() {
            return finalCommission;
        }

        public void displayReport() {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("COMMISSION CALCULATION REPORT");
            System.out.println("=".repeat(60));
            System.out.println("Base Commission:    $" + baseCommission);
            System.out.println("Modifiers:          $" + modifiers);
            System.out.println("Bonuses:            $" + bonuses);
            System.out.println("Adjustments:        $" + adjustments);
            System.out.println("-".repeat(60));
            System.out.println("FINAL COMMISSION:   $" + finalCommission);
            System.out.println("=".repeat(60));
            System.out.println("\nCalculation Steps:");
            for (int i = 0; i < calculationSteps.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + calculationSteps.get(i));
            }
            System.out.println();
        }
    }

    /**
     * ABSTRACT COMMISSION CALCULATOR - TEMPLATE CLASS
     *
     * Defines the template method for commission calculation and declares
     * abstract/hook methods for customization points.
     *
     * This is the "Abstract Class" in the Template Method pattern.
     */
    public abstract static class CommissionCalculator {
        protected List<String> calculationLog;
        protected BigDecimal runningTotal;

        /**
         * TEMPLATE METHOD - Commission Calculation Workflow
         *
         * This method defines the skeleton of the commission calculation algorithm.
         * It cannot be overridden (final), ensuring all calculators follow the same process.
         *
         * The workflow:
         * 1. Validate the deal
         * 2. Calculate base commission (varies by type)
         * 3. Apply time-based modifiers if applicable (optional)
         * 4. Apply performance bonuses (varies by type)
         * 5. Apply caps/floors if applicable (optional)
         * 6. Log the calculation details
         */
        public final CommissionResult calculateCommission(Deal deal) {
            // Initialize calculation tracking
            calculationLog = new ArrayList<>();
            runningTotal = BigDecimal.ZERO;

            calculationLog.add("Starting commission calculation for: " + getCalculatorType());
            calculationLog.add("Deal: " + deal.getTitle() + " | Value: $" + deal.getValue());

            // Step 1: Validate deal (concrete method - same for all)
            validateDeal(deal);

            // Step 2: Calculate base commission (abstract method - MUST be implemented)
            BigDecimal baseCommission = calculateBaseCommission(deal);
            runningTotal = baseCommission;
            calculationLog.add("Base commission calculated: $" + baseCommission);

            // Step 3: Apply time-based modifiers (hook - optional customization)
            BigDecimal modifiers = BigDecimal.ZERO;
            if (shouldApplyTimeBasedModifiers(deal)) {
                modifiers = applyTimeBasedModifiers(deal, runningTotal);
                runningTotal = runningTotal.add(modifiers);
                calculationLog.add("Time-based modifiers applied: $" + modifiers +
                                 " | Running total: $" + runningTotal);
            }

            // Step 4: Apply performance bonuses (abstract method - MUST be implemented)
            BigDecimal bonuses = applyPerformanceBonuses(deal, runningTotal);
            runningTotal = runningTotal.add(bonuses);
            calculationLog.add("Performance bonuses applied: $" + bonuses +
                             " | Running total: $" + runningTotal);

            // Step 5: Apply caps and floors (hook - optional customization)
            BigDecimal adjustments = BigDecimal.ZERO;
            if (shouldApplyCapsAndFloors()) {
                BigDecimal beforeCaps = runningTotal;
                runningTotal = applyCapsAndFloors(deal, runningTotal);
                adjustments = runningTotal.subtract(beforeCaps);
                if (adjustments.compareTo(BigDecimal.ZERO) != 0) {
                    calculationLog.add("Caps/floors applied: $" + adjustments +
                                     " | Running total: $" + runningTotal);
                }
            }

            // Step 6: Finalize and log (concrete method - same for all)
            BigDecimal finalCommission = finalizeCalculation(runningTotal);
            logCalculation(deal, finalCommission);

            return new CommissionResult(baseCommission, modifiers, bonuses,
                                      adjustments, finalCommission, calculationLog);
        }

        /**
         * CONCRETE METHOD - Deal Validation
         *
         * Validates that the deal is eligible for commission calculation.
         * Same validation logic for all calculator types.
         */
        protected void validateDeal(Deal deal) {
            if (deal == null) {
                throw new IllegalArgumentException("Deal cannot be null");
            }
            if (deal.getStatus() != DealStatus.WON) {
                throw new IllegalStateException("Commission can only be calculated for WON deals. " +
                                              "Current status: " + deal.getStatus());
            }
            if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deal value must be positive");
            }
            calculationLog.add("✓ Deal validation passed");
        }

        /**
         * ABSTRACT METHOD - Calculate Base Commission
         *
         * Each calculator type must implement its own base commission calculation.
         * This is where the main calculation logic varies.
         */
        protected abstract BigDecimal calculateBaseCommission(Deal deal);

        /**
         * HOOK METHOD - Should Apply Time-Based Modifiers
         *
         * Determines whether time-based modifiers should be applied.
         * Default is true, but subclasses can override.
         */
        protected boolean shouldApplyTimeBasedModifiers(Deal deal) {
            return true; // Default: apply modifiers
        }

        /**
         * HOOK METHOD - Apply Time-Based Modifiers
         *
         * Applies modifiers based on when the deal closed (quarter-end bonuses, etc.).
         * Default implementation provides basic quarter-end bonus.
         * Subclasses can override for custom logic.
         */
        protected BigDecimal applyTimeBasedModifiers(Deal deal, BigDecimal currentCommission) {
            // Default: 5% bonus if deal closed in last week of quarter
            if (deal.getCloseDate() != null && isLastWeekOfQuarter(deal.getCloseDate())) {
                BigDecimal bonus = currentCommission.multiply(new BigDecimal("0.05"));
                calculationLog.add("  → Quarter-end bonus (5%): $" + bonus);
                return bonus;
            }
            return BigDecimal.ZERO;
        }

        /**
         * Helper method to check if date is in last week of quarter
         */
        private boolean isLastWeekOfQuarter(LocalDate date) {
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            // Check if it's the last week of Q1, Q2, Q3, or Q4
            return (month == 3 || month == 6 || month == 9 || month == 12) && day >= 24;
        }

        /**
         * ABSTRACT METHOD - Apply Performance Bonuses
         *
         * Each calculator type must implement its own bonus logic.
         * Bonuses might be based on quota attainment, deal size, etc.
         */
        protected abstract BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal currentCommission);

        /**
         * HOOK METHOD - Should Apply Caps and Floors
         *
         * Determines whether caps and floors should be applied.
         * Default is true, but subclasses can disable.
         */
        protected boolean shouldApplyCapsAndFloors() {
            return true; // Default: apply caps and floors
        }

        /**
         * HOOK METHOD - Apply Caps and Floors
         *
         * Applies minimum and maximum commission limits.
         * Default implementation provides basic limits.
         * Subclasses can override for custom limits.
         */
        protected BigDecimal applyCapsAndFloors(Deal deal, BigDecimal commission) {
            // Default caps and floors
            BigDecimal minCommission = new BigDecimal("100.00");
            BigDecimal maxCommission = deal.getValue().multiply(new BigDecimal("0.50")); // Max 50% of deal

            if (commission.compareTo(minCommission) < 0) {
                calculationLog.add("  → Applied floor: $" + minCommission + " (was $" + commission + ")");
                return minCommission;
            }
            if (commission.compareTo(maxCommission) > 0) {
                calculationLog.add("  → Applied cap: $" + maxCommission + " (was $" + commission + ")");
                return maxCommission;
            }
            return commission;
        }

        /**
         * CONCRETE METHOD - Finalize Calculation
         *
         * Rounds the final commission to 2 decimal places.
         * Same for all calculator types.
         */
        protected BigDecimal finalizeCalculation(BigDecimal commission) {
            return commission.setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * CONCRETE METHOD - Log Calculation
         *
         * Logs the calculation for audit purposes.
         * Same logging format for all calculator types.
         */
        protected void logCalculation(Deal deal, BigDecimal finalCommission) {
            calculationLog.add("✓ Calculation completed");
            calculationLog.add("Deal ID: " + deal.getId() + " | Final Commission: $" + finalCommission);
        }

        /**
         * Get the type name of this calculator (for logging).
         */
        protected abstract String getCalculatorType();
    }

    /**
     * SOFTWARE COMMISSION CALCULATOR
     *
     * Calculates commissions for software deals.
     * - Base: 15% of deal value
     * - Bonus: 3% extra for deals over $50,000
     * - Uses standard time modifiers and caps
     */
    public static class SoftwareCommissionCalculator extends CommissionCalculator {

        @Override
        protected BigDecimal calculateBaseCommission(Deal deal) {
            // Software deals: 15% commission rate
            BigDecimal rate = new BigDecimal("0.15");
            BigDecimal commission = deal.getValue().multiply(rate);
            calculationLog.add("  → Software rate: 15% of $" + deal.getValue() + " = $" + commission);
            return commission;
        }

        @Override
        protected BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal currentCommission) {
            // Bonus: 3% extra for large deals (over $50k)
            BigDecimal largeRealThreshold = new BigDecimal("50000");
            if (deal.getValue().compareTo(largeRealThreshold) > 0) {
                BigDecimal bonus = deal.getValue().multiply(new BigDecimal("0.03"));
                calculationLog.add("  → Large deal bonus (3%): $" + bonus);
                return bonus;
            }
            return BigDecimal.ZERO;
        }

        @Override
        protected String getCalculatorType() {
            return "Software Commission Calculator";
        }
    }

    /**
     * HARDWARE COMMISSION CALCULATOR
     *
     * Calculates commissions for hardware deals.
     * - Base: 8% of deal value (lower margin product)
     * - Bonus: Tiered based on deal size
     * - No time modifiers (hardware has consistent pricing)
     * - Custom caps (hardware has different limits)
     */
    public static class HardwareCommissionCalculator extends CommissionCalculator {

        @Override
        protected BigDecimal calculateBaseCommission(Deal deal) {
            // Hardware deals: 8% commission rate (lower margin)
            BigDecimal rate = new BigDecimal("0.08");
            BigDecimal commission = deal.getValue().multiply(rate);
            calculationLog.add("  → Hardware rate: 8% of $" + deal.getValue() + " = $" + commission);
            return commission;
        }

        @Override
        protected boolean shouldApplyTimeBasedModifiers(Deal deal) {
            // Hardware pricing is consistent year-round, no time-based modifiers
            calculationLog.add("  → Hardware deals: time modifiers disabled");
            return false;
        }

        @Override
        protected BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal currentCommission) {
            // Tiered bonuses based on deal size
            BigDecimal value = deal.getValue();
            BigDecimal bonus = BigDecimal.ZERO;

            if (value.compareTo(new BigDecimal("100000")) > 0) {
                // Over $100k: $2000 bonus
                bonus = new BigDecimal("2000");
                calculationLog.add("  → Tier 3 bonus (>$100k): $" + bonus);
            } else if (value.compareTo(new BigDecimal("50000")) > 0) {
                // $50k-$100k: $1000 bonus
                bonus = new BigDecimal("1000");
                calculationLog.add("  → Tier 2 bonus ($50k-$100k): $" + bonus);
            } else if (value.compareTo(new BigDecimal("25000")) > 0) {
                // $25k-$50k: $500 bonus
                bonus = new BigDecimal("500");
                calculationLog.add("  → Tier 1 bonus ($25k-$50k): $" + bonus);
            }

            return bonus;
        }

        @Override
        protected BigDecimal applyCapsAndFloors(Deal deal, BigDecimal commission) {
            // Hardware has different caps: min $50, max 30% of deal
            BigDecimal minCommission = new BigDecimal("50.00");
            BigDecimal maxCommission = deal.getValue().multiply(new BigDecimal("0.30"));

            if (commission.compareTo(minCommission) < 0) {
                calculationLog.add("  → Applied floor: $" + minCommission + " (was $" + commission + ")");
                return minCommission;
            }
            if (commission.compareTo(maxCommission) > 0) {
                calculationLog.add("  → Applied cap: $" + maxCommission + " (was $" + commission + ")");
                return maxCommission;
            }
            return commission;
        }

        @Override
        protected String getCalculatorType() {
            return "Hardware Commission Calculator";
        }
    }

    /**
     * SERVICES COMMISSION CALCULATOR
     *
     * Calculates commissions for professional services deals.
     * - Base: 20% of deal value (high margin)
     * - Bonus: Based on strategic importance
     * - Enhanced time modifiers (services have seasonal demand)
     * - No caps (uncapped earning potential for services)
     */
    public static class ServicesCommissionCalculator extends CommissionCalculator {

        @Override
        protected BigDecimal calculateBaseCommission(Deal deal) {
            // Services deals: 20% commission rate (high margin)
            BigDecimal rate = new BigDecimal("0.20");
            BigDecimal commission = deal.getValue().multiply(rate);
            calculationLog.add("  → Services rate: 20% of $" + deal.getValue() + " = $" + commission);
            return commission;
        }

        @Override
        protected BigDecimal applyTimeBasedModifiers(Deal deal, BigDecimal currentCommission) {
            // Enhanced time modifiers for services
            BigDecimal modifier = BigDecimal.ZERO;

            // Quarter-end bonus: 10% (higher than default 5%)
            if (deal.getCloseDate() != null && isLastWeekOfQuarter(deal.getCloseDate())) {
                BigDecimal bonus = currentCommission.multiply(new BigDecimal("0.10"));
                modifier = modifier.add(bonus);
                calculationLog.add("  → Enhanced quarter-end bonus (10%): $" + bonus);
            }

            // Year-end bonus: additional 5%
            if (deal.getCloseDate() != null && deal.getCloseDate().getMonthValue() == 12) {
                BigDecimal bonus = currentCommission.multiply(new BigDecimal("0.05"));
                modifier = modifier.add(bonus);
                calculationLog.add("  → Year-end bonus (5%): $" + bonus);
            }

            return modifier;
        }

        private boolean isLastWeekOfQuarter(LocalDate date) {
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            return (month == 3 || month == 6 || month == 9 || month == 12) && day >= 24;
        }

        @Override
        protected BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal currentCommission) {
            // Strategic account bonus: 10% for deals over $30k
            if (deal.getValue().compareTo(new BigDecimal("30000")) > 0) {
                BigDecimal bonus = currentCommission.multiply(new BigDecimal("0.10"));
                calculationLog.add("  → Strategic account bonus (10%): $" + bonus);
                return bonus;
            }
            return BigDecimal.ZERO;
        }

        @Override
        protected boolean shouldApplyCapsAndFloors() {
            // Services have uncapped earning potential
            calculationLog.add("  → Services deals: caps and floors disabled");
            return false;
        }

        @Override
        protected String getCalculatorType() {
            return "Services Commission Calculator";
        }
    }

    /**
     * TRAINING COMMISSION CALCULATOR
     *
     * Calculates commissions for training/education deals.
     * - Base: Fixed amount per deal ($500)
     * - Bonus: Volume-based (number of participants)
     * - Standard time modifiers
     * - Custom caps based on training type
     */
    public static class TrainingCommissionCalculator extends CommissionCalculator {

        @Override
        protected BigDecimal calculateBaseCommission(Deal deal) {
            // Training deals: flat $500 base commission per deal
            BigDecimal baseCommission = new BigDecimal("500.00");
            calculationLog.add("  → Training base: $" + baseCommission + " (flat rate)");
            return baseCommission;
        }

        @Override
        protected BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal currentCommission) {
            // Bonus based on deal value tiers (represents participant count)
            BigDecimal value = deal.getValue();
            BigDecimal bonus = BigDecimal.ZERO;

            // Small training (up to $5k): $100
            if (value.compareTo(new BigDecimal("5000")) <= 0) {
                bonus = new BigDecimal("100");
                calculationLog.add("  → Small training bonus: $" + bonus);
            }
            // Medium training ($5k-$15k): $300
            else if (value.compareTo(new BigDecimal("15000")) <= 0) {
                bonus = new BigDecimal("300");
                calculationLog.add("  → Medium training bonus: $" + bonus);
            }
            // Large training (over $15k): $600
            else {
                bonus = new BigDecimal("600");
                calculationLog.add("  → Large training bonus: $" + bonus);
            }

            return bonus;
        }

        @Override
        protected BigDecimal applyCapsAndFloors(Deal deal, BigDecimal commission) {
            // Training has specific caps: min $500, max $2000
            BigDecimal minCommission = new BigDecimal("500.00");
            BigDecimal maxCommission = new BigDecimal("2000.00");

            if (commission.compareTo(minCommission) < 0) {
                calculationLog.add("  → Applied floor: $" + minCommission);
                return minCommission;
            }
            if (commission.compareTo(maxCommission) > 0) {
                calculationLog.add("  → Applied cap: $" + maxCommission + " (was $" + commission + ")");
                return maxCommission;
            }
            return commission;
        }

        @Override
        protected String getCalculatorType() {
            return "Training Commission Calculator";
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Template Method Pattern works with different commission calculators.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║    TEMPLATE METHOD - COMMISSION CALCULATION DEMO          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create sample deals
        Deal softwareDeal = createDeal("Enterprise Software License", new BigDecimal("75000"),
                                      LocalDate.of(2024, 3, 28), DealStatus.WON);

        Deal hardwareDeal = createDeal("Data Center Equipment", new BigDecimal("120000"),
                                      LocalDate.of(2024, 5, 15), DealStatus.WON);

        Deal servicesDeal = createDeal("Consulting Services", new BigDecimal("45000"),
                                      LocalDate.of(2024, 12, 27), DealStatus.WON);

        Deal trainingDeal = createDeal("Corporate Training Program", new BigDecimal("18000"),
                                      LocalDate.of(2024, 6, 29), DealStatus.WON);

        // Calculate commissions using different calculators
        System.out.println("SCENARIO 1: Software Deal");
        System.out.println("=".repeat(60));
        CommissionCalculator softwareCalc = new SoftwareCommissionCalculator();
        CommissionResult result1 = softwareCalc.calculateCommission(softwareDeal);
        result1.displayReport();

        System.out.println("\n\nSCENARIO 2: Hardware Deal");
        System.out.println("=".repeat(60));
        CommissionCalculator hardwareCalc = new HardwareCommissionCalculator();
        CommissionResult result2 = hardwareCalc.calculateCommission(hardwareDeal);
        result2.displayReport();

        System.out.println("\n\nSCENARIO 3: Services Deal");
        System.out.println("=".repeat(60));
        CommissionCalculator servicesCalc = new ServicesCommissionCalculator();
        CommissionResult result3 = servicesCalc.calculateCommission(servicesDeal);
        result3.displayReport();

        System.out.println("\n\nSCENARIO 4: Training Deal");
        System.out.println("=".repeat(60));
        CommissionCalculator trainingCalc = new TrainingCommissionCalculator();
        CommissionResult result4 = trainingCalc.calculateCommission(trainingDeal);
        result4.displayReport();

        // Summary
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    SUMMARY                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Software Deal:  $" + result1.getFinalCommission());
        System.out.println("Hardware Deal:  $" + result2.getFinalCommission());
        System.out.println("Services Deal:  $" + result3.getFinalCommission());
        System.out.println("Training Deal:  $" + result4.getFinalCommission());
        System.out.println();
        System.out.println("All calculations followed the same workflow:");
        System.out.println("  1. Validate → 2. Base → 3. Modifiers → 4. Bonuses → 5. Caps → 6. Log");
        System.out.println();
    }

    /**
     * Helper method to create deals for demo
     */
    private static Deal createDeal(String title, BigDecimal value, LocalDate closeDate, DealStatus status) {
        Deal deal = new Deal(title, value, "REP-123");
        deal.setId("DEAL-" + System.currentTimeMillis());
        deal.setCloseDate(closeDate);
        deal.setStatus(status);
        return deal;
    }
}