package com.chapman.edu.commissions.patterns.behavioral.template;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TEMPLATE METHOD PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Template Method Pattern defines the skeleton of an algorithm in a base class,
 * but lets subclasses override specific steps of the algorithm without changing its structure.
 * The template method calls a series of steps, some of which are abstract and must be
 * implemented by subclasses, while others have default implementations.
 *
 * PROBLEM IT SOLVES:
 * - Eliminates code duplication when multiple algorithms share the same structure
 * - Enforces a consistent algorithm structure across related classes
 * - Allows controlled variation points where subclasses can customize behavior
 * - Makes the algorithm's invariant parts explicit (what MUST happen)
 * - Makes the variant parts explicit (what CAN be customized)
 *
 * WHEN TO USE:
 * - Multiple classes implement similar algorithms with the same structure
 * - You want to control which parts of an algorithm can be overridden
 * - You want to avoid code duplication across similar operations
 * - You need to enforce a specific sequence of operations
 * - Common behavior should be localized in a single class
 *
 * COMPONENTS:
 * 1. Abstract Class: Defines the template method and declares abstract/hook methods
 * 2. Template Method: Defines the algorithm skeleton (final to prevent override)
 * 3. Abstract Methods: Steps that MUST be implemented by subclasses (required variation)
 * 4. Hook Methods: Steps that MAY be overridden by subclasses (optional variation)
 * 5. Concrete Methods: Steps with default implementation (invariant behavior)
 * 6. Concrete Subclasses: Implement abstract methods and optionally override hooks
 *
 * KEY PRINCIPLE:
 * "Hollywood Principle" - Don't call us, we'll call you.
 * The parent class calls the subclass methods, not the other way around.
 */
public class TemplateStructure {

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
}