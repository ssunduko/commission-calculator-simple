package com.chapman.edu.commissions.patterns.behavioral.template;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;

import com.chapman.edu.commissions.patterns.behavioral.template.TemplateStructure.CommissionCalculator;

import java.math.BigDecimal;
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
 */
public class TemplateImplementation {

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
}