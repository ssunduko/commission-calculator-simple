package com.chapman.edu.commissions.documentation.comments.original;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Commission calculator class that calculates commissions
 */
public class CommissionCalculator {

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method calculates commission for a deal
     * @param deal the deal to calculate commission for
     * @param plan the commission plan to use
     * @return the calculated commission amount
     */
    public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
        // VIOLATION: Redundant comment that states the obvious
        // Get the deal value
        BigDecimal dealValue = deal.getValue();

        // VIOLATION: Comment that just restates what the code does
        // Check if deal value is null
        if (dealValue == null) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return zero if null
            return BigDecimal.ZERO;
        }

        // VIOLATION: Comment that just restates what the code does
        // Check if deal value is negative
        if (dealValue.compareTo(BigDecimal.ZERO) < 0) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return zero for negative values
            return BigDecimal.ZERO;
        }

        // VIOLATION: Comment that explains WHAT without WHY
        // Apply penalty for late deals
        if (isAfterQuarterEnd(deal.getCloseDate())) {
            // VIOLATION: Comment that just describes the calculation without business context
            // Multiply by 0.85 to apply 15% penalty
            dealValue = dealValue.multiply(new BigDecimal("0.85"));
        }

        // VIOLATION: Unnecessary comment for obvious operation
        // Get base rate from plan
        BigDecimal baseRate = plan.getBaseRate();

        // VIOLATION: Unnecessary comment for obvious operation
        // Calculate base commission
        BigDecimal baseCommission = dealValue.multiply(baseRate);

        // VIOLATION: Comment that explains WHAT without WHY
        // Apply tier multipliers
        if (dealValue.compareTo(new BigDecimal("100000")) > 0) {
            // VIOLATION: Comment that just describes the calculation without business context
            // Multiply by 1.5 for deals over 100K
            baseCommission = baseCommission.multiply(new BigDecimal("1.5"));
        } else if (dealValue.compareTo(new BigDecimal("50000")) > 0) {
            // VIOLATION: Comment that just describes the calculation without business context
            // Multiply by 1.2 for deals over 50K
            baseCommission = baseCommission.multiply(new BigDecimal("1.2"));
        }

        // VIOLATION: Unnecessary comment for obvious operation
        // Return the calculated commission
        return baseCommission;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method checks if date is after quarter end
     * @param date the date to check
     * @return true if after quarter end, false otherwise
     */
    private boolean isAfterQuarterEnd(LocalDate date) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Get the month from date
        Month month = date.getMonth();
        // VIOLATION: Unnecessary comment for obvious operation
        // Get the day from date
        int day = date.getDayOfMonth();

        // VIOLATION: Comments that just restate what the code does without business context
        // Check if it's after Q1 end (March 31)
        if (month == Month.APRIL && day >= 1) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return true if April 1st or later
            return true;
        }
        // VIOLATION: Comments that just restate what the code does without business context
        // Check if it's after Q2 end (June 30)
        if (month == Month.JULY && day >= 1) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return true if July 1st or later
            return true;
        }
        // VIOLATION: Comments that just restate what the code does without business context
        // Check if it's after Q3 end (September 30)
        if (month == Month.OCTOBER && day >= 1) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return true if October 1st or later
            return true;
        }
        // VIOLATION: Comments that just restate what the code does without business context
        // Check if it's after Q4 end (December 31)
        if (month == Month.JANUARY && day >= 1) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return true if January 1st or later (next year)
            return true;
        }

        // VIOLATION: Unnecessary comment for obvious operation
        // Return false if not after quarter end
        return false;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method calculates bonus commission
     * @param deal the deal
     * @param user the user
     * @return the bonus amount
     */
    public BigDecimal calculateBonus(Deal deal, User user) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Initialize bonus to zero
        BigDecimal bonus = BigDecimal.ZERO;

        // VIOLATION: Comments that just restate what the code does without business context
        // Check if user is sales rep
        if (user.isSalesRep()) {
            // VIOLATION: Comments that just restate what the code does without business context
            // Check if deal is over 25K
            if (deal.getValue().compareTo(new BigDecimal("25000")) > 0) {
                // VIOLATION: Unnecessary comment for obvious operation
                // Add 1000 bonus
                bonus = bonus.add(new BigDecimal("1000"));
            }
        }

        // VIOLATION: Comments that just restate what the code does without business context
        // Check if user is manager
        if (user.isSalesManager()) {
            // VIOLATION: Comments that just restate what the code does without business context
            // Check if deal is over 50K
            if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
                // VIOLATION: Unnecessary comment for obvious operation
                // Add 2000 bonus
                bonus = bonus.add(new BigDecimal("2000"));
            }
        }

        // VIOLATION: Unnecessary comment for obvious operation
        // Return the bonus
        return bonus;
    }
}
