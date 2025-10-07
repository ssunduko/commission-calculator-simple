package com.chapman.edu.commissions.patterns.behavioral.strategy;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.strategy.StrategyStructure.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * STRATEGY PATTERN - COMMISSION CALCULATION IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation demonstrates various commission calculation strategies used in sales.
 * Different deals may have different commission structures based on deal type, size, product,
 * payment terms, and other business rules.
 *
 * BUSINESS CONTEXT:
 * Sales organizations use different commission strategies to incentivize different behaviors:
 * - Standard rate for regular deals
 * - Tiered rates to encourage larger deals
 * - Accelerator bonuses for exceeding quotas
 * - Payment term adjustments to encourage faster collections
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Easy to add new commission calculation methods without modifying existing code
 * 2. Calculation logic is encapsulated and testable independently
 * 3. Commission strategies can be selected at runtime based on business rules
 * 4. Reduces complex conditional logic in the main calculator
 * 5. Strategies can be reused across different contexts
 */
public class StrategyImplementation {

    // ==================== COMMISSION STRATEGIES ====================

    /**
     * STANDARD RATE STRATEGY
     *
     * Applies a fixed percentage rate to the deal value.
     * This is the most common commission structure.
     */
    public static class StandardRateStrategy implements CommissionStrategy {
        private final BigDecimal rate;

        public StandardRateStrategy(BigDecimal rate) {
            this.rate = rate;
        }

        @Override
        public BigDecimal calculateCommission(Deal deal) {
            if (deal.getValue() == null) {
                return BigDecimal.ZERO;
            }
            return deal.getValue()
                    .multiply(rate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public String getStrategyDescription() {
            return "Standard Rate: " + rate + "% of deal value";
        }
    }

    /**
     * TIERED RATE STRATEGY
     * Applies different rates based on deal size tiers.
     * Larger deals get higher commission rates to incentivize bigger sales.
     */
    public static class TieredRateStrategy implements CommissionStrategy {
        @Override
        public BigDecimal calculateCommission(Deal deal) {
            if (deal.getValue() == null) {
                return BigDecimal.ZERO;
            }

            BigDecimal value = deal.getValue();
            BigDecimal rate;

            // Define tiers
            if (value.compareTo(new BigDecimal("100000")) >= 0) {
                rate = new BigDecimal("15"); // 15% for deals >= $100k
            } else if (value.compareTo(new BigDecimal("50000")) >= 0) {
                rate = new BigDecimal("12"); // 12% for deals >= $50k
            } else if (value.compareTo(new BigDecimal("10000")) >= 0) {
                rate = new BigDecimal("10"); // 10% for deals >= $10k
            } else {
                rate = new BigDecimal("8");  // 8% for deals < $10k
            }

            return value.multiply(rate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public String getStrategyDescription() {
            return "Tiered Rate: 8% (<$10k), 10% ($10k-$50k), 12% ($50k-$100k), 15% (>=$100k)";
        }
    }

    /**
     * FLAT FEE STRATEGY
     *
     * Pays a fixed amount regardless of deal size.
     * Useful for small deals or specific product types.
     */
    public static class FlatFeeStrategy implements CommissionStrategy {
        private final BigDecimal flatFee;

        public FlatFeeStrategy(BigDecimal flatFee) {
            this.flatFee = flatFee;
        }

        @Override
        public BigDecimal calculateCommission(Deal deal) {
            return flatFee;
        }

        @Override
        public String getStrategyDescription() {
            return "Flat Fee: $" + flatFee + " per deal";
        }
    }

    /**
     * GROSS PROFIT STRATEGY
     *
     * Calculates commission based on profit margin rather than revenue.
     * Encourages sales reps to focus on profitable deals.
     */
    public static class GrossProfitStrategy implements CommissionStrategy {
        private final BigDecimal rate;
        private final BigDecimal costPercentage;

        public GrossProfitStrategy(BigDecimal rate, BigDecimal costPercentage) {
            this.rate = rate;
            this.costPercentage = costPercentage;
        }

        @Override
        public BigDecimal calculateCommission(Deal deal) {
            if (deal.getValue() == null) {
                return BigDecimal.ZERO;
            }

            // Calculate gross profit (simplified: revenue - estimated costs)
            BigDecimal revenue = deal.getValue();
            BigDecimal costs = revenue.multiply(costPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal grossProfit = revenue.subtract(costs);

            return grossProfit.multiply(rate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public String getStrategyDescription() {
            return "Gross Profit: " + rate + "% of profit (assuming " + costPercentage + "% costs)";
        }
    }

    // ==================== BONUS STRATEGIES ====================

    /**
     * QUOTA ACHIEVEMENT BONUS STRATEGY
     *
     * Provides bonus when sales exceed a quota threshold.
     */
    public static class QuotaAchievementBonusStrategy implements BonusStrategy {
        private final BigDecimal quotaThreshold;
        private final BigDecimal bonusPercentage;

        public QuotaAchievementBonusStrategy(BigDecimal quotaThreshold, BigDecimal bonusPercentage) {
            this.quotaThreshold = quotaThreshold;
            this.bonusPercentage = bonusPercentage;
        }

        @Override
        public BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal) {
            if (!appliesTo(deal)) {
                return BigDecimal.ZERO;
            }
            // Bonus is a percentage of the base commission
            return baseCommission.multiply(bonusPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public boolean appliesTo(Deal deal) {
            return deal.getValue() != null &&
                    deal.getValue().compareTo(quotaThreshold) > 0;
        }

        @Override
        public String getStrategyName() {
            return "Quota Achievement Bonus (" + bonusPercentage + "% for deals > $" + quotaThreshold + ")";
        }
    }

    /**
     * EARLY CLOSE BONUS STRATEGY
     *
     * Rewards deals closed before the end of the quarter.
     */
    public static class EarlyCloseBonusStrategy implements BonusStrategy {
        private final BigDecimal bonusAmount;

        public EarlyCloseBonusStrategy(BigDecimal bonusAmount) {
            this.bonusAmount = bonusAmount;
        }

        @Override
        public BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal) {
            if (!appliesTo(deal)) {
                return BigDecimal.ZERO;
            }
            return bonusAmount;
        }

        @Override
        public boolean appliesTo(Deal deal) {
            if (deal.getCloseDate() == null) {
                return false;
            }
            // Check if closed in first 2 months of quarter
            int month = deal.getCloseDate().getMonthValue();
            int quarterMonth = month % 3; // 1, 2, or 0 (where 0 = last month of quarter)
            return quarterMonth == 1 || quarterMonth == 2;
        }

        @Override
        public String getStrategyName() {
            return "Early Close Bonus ($" + bonusAmount + " for closing before quarter end)";
        }
    }

    /**
     * NEW CUSTOMER BONUS STRATEGY
     *
     * Provides bonus for deals with new customers.
     */
    public static class NewCustomerBonusStrategy implements BonusStrategy {
        private final BigDecimal bonusPercentage;

        public NewCustomerBonusStrategy(BigDecimal bonusPercentage) {
            this.bonusPercentage = bonusPercentage;
        }

        @Override
        public BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal) {
            if (!appliesTo(deal)) {
                return BigDecimal.ZERO;
            }
            return baseCommission.multiply(bonusPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public boolean appliesTo(Deal deal) {
            // In a real system, this would check if customer is new
            // For demo, we use a simple heuristic: deals with "New" in title
            return deal.getTitle() != null &&
                    deal.getTitle().toLowerCase().contains("new");
        }

        @Override
        public String getStrategyName() {
            return "New Customer Bonus (" + bonusPercentage + "% extra)";
        }
    }

    // ==================== PAYMENT TERMS STRATEGIES ====================

    /**
     * IMMEDIATE PAYMENT STRATEGY
     *
     * No adjustment for standard payment terms (e.g., Net 30).
     */
    public static class ImmediatePaymentStrategy implements PaymentTermsStrategy {
        @Override
        public BigDecimal adjustForPaymentTerms(BigDecimal baseCommission, int paymentTermsInDays) {
            // No adjustment - commission paid immediately
            return baseCommission;
        }

        @Override
        public String getAdjustmentDescription() {
            return "Immediate Payment: Full commission paid immediately";
        }
    }

    /**
     * DEFERRED PAYMENT STRATEGY
     *
     * Commission paid after payment is received from customer.
     * Reduces commission slightly to account for collection risk.
     */
    public static class DeferredPaymentStrategy implements PaymentTermsStrategy {
        private final BigDecimal discountRatePerMonth;

        public DeferredPaymentStrategy(BigDecimal discountRatePerMonth) {
            this.discountRatePerMonth = discountRatePerMonth;
        }

        @Override
        public BigDecimal adjustForPaymentTerms(BigDecimal baseCommission, int paymentTermsInDays) {
            if (paymentTermsInDays <= 0) {
                return baseCommission;
            }

            // Calculate discount based on payment terms
            BigDecimal months = new BigDecimal(paymentTermsInDays)
                    .divide(new BigDecimal("30"), 2, RoundingMode.HALF_UP);
            BigDecimal discount = discountRatePerMonth.multiply(months)
                    .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

            return baseCommission.multiply(BigDecimal.ONE.subtract(discount))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public String getAdjustmentDescription() {
            return "Deferred Payment: " + discountRatePerMonth + "% reduction per month of payment terms";
        }
    }

    /**
     * SPLIT PAYMENT STRATEGY
     *
     * Splits commission: part paid immediately, rest after customer payment.
     */
    public static class SplitPaymentStrategy implements PaymentTermsStrategy {
        private final BigDecimal immediatePercentage;

        public SplitPaymentStrategy(BigDecimal immediatePercentage) {
            this.immediatePercentage = immediatePercentage;
        }

        @Override
        public BigDecimal adjustForPaymentTerms(BigDecimal baseCommission, int paymentTermsInDays) {
            // For this demo, return the immediate portion
            // In a real system, this would track both portions separately
            return baseCommission.multiply(immediatePercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        @Override
        public String getAdjustmentDescription() {
            return "Split Payment: " + immediatePercentage + "% immediate, " +
                    (new BigDecimal("100").subtract(immediatePercentage)) + "% on customer payment";
        }
    }

    // ==================== CONTEXT (CALCULATOR) ====================

    /**
     * COMMISSION CALCULATOR (CONTEXT)
     *
     * Uses strategies to calculate total commission.
     * This is the "Context" in the Strategy pattern - it delegates algorithm execution to strategies.
     */
    public static class CommissionCalculator {
        private CommissionStrategy commissionStrategy;
        private BonusStrategy bonusStrategy;
        private PaymentTermsStrategy paymentTermsStrategy;

        /**
         * Set the commission calculation strategy.
         */
        public void setCommissionStrategy(CommissionStrategy strategy) {
            this.commissionStrategy = strategy;
        }

        /**
         * Set the bonus calculation strategy.
         */
        public void setBonusStrategy(BonusStrategy strategy) {
            this.bonusStrategy = strategy;
        }

        /**
         * Set the payment terms adjustment strategy.
         */
        public void setPaymentTermsStrategy(PaymentTermsStrategy strategy) {
            this.paymentTermsStrategy = strategy;
        }

        /**
         * Calculate total commission for a deal using configured strategies.
         *
         * @param deal The deal to calculate commission for
         * @param paymentTermsInDays Payment terms for the deal
         * @return Total commission including bonuses and adjustments
         */
        public BigDecimal calculateTotalCommission(Deal deal, int paymentTermsInDays) {
            if (commissionStrategy == null) {
                throw new IllegalStateException("Commission strategy must be set");
            }

            // Step 1: Calculate base commission using strategy
            BigDecimal baseCommission = commissionStrategy.calculateCommission(deal);
            System.out.println("  Base commission: $" + baseCommission +
                    " (" + commissionStrategy.getStrategyDescription() + ")");

            // Step 2: Apply bonus if configured and applicable
            BigDecimal bonus = BigDecimal.ZERO;
            if (bonusStrategy != null && bonusStrategy.appliesTo(deal)) {
                bonus = bonusStrategy.calculateBonus(baseCommission, deal);
                System.out.println("  Bonus: $" + bonus + " (" + bonusStrategy.getStrategyName() + ")");
            }

            BigDecimal totalBeforeAdjustment = baseCommission.add(bonus);

            // Step 3: Apply payment terms adjustment if configured
            BigDecimal finalCommission = totalBeforeAdjustment;
            if (paymentTermsStrategy != null) {
                finalCommission = paymentTermsStrategy.adjustForPaymentTerms(
                        totalBeforeAdjustment, paymentTermsInDays);
                System.out.println("  Payment adjustment: $" + totalBeforeAdjustment + " → $" +
                        finalCommission + " (" + paymentTermsStrategy.getAdjustmentDescription() + ")");
            }

            return finalCommission;
        }

        /**
         * Get a summary of the configured strategies.
         */
        public String getStrategySummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("Commission Strategy Configuration:\n");

            if (commissionStrategy != null) {
                summary.append("  Commission: ").append(commissionStrategy.getStrategyDescription()).append("\n");
            } else {
                summary.append("  Commission: Not configured\n");
            }

            if (bonusStrategy != null) {
                summary.append("  Bonus: ").append(bonusStrategy.getStrategyName()).append("\n");
            } else {
                summary.append("  Bonus: None\n");
            }

            if (paymentTermsStrategy != null) {
                summary.append("  Payment Terms: ").append(paymentTermsStrategy.getAdjustmentDescription()).append("\n");
            } else {
                summary.append("  Payment Terms: Not configured\n");
            }

            return summary.toString();
        }
    }
}