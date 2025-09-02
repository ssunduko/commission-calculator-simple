package com.chapman.edu.commissions.documentation.comments.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * FIX: Clear class description that explains business purpose
 * Calculates sales commissions based on deal values and commission plans.
 * Implements tiered commission structure with penalties and bonuses per company policy.
 */
public class CommissionCalculator {

    public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
        BigDecimal dealValue = deal.getValue();

        // FIX: Simplified code with combined conditionals instead of separate checks
        if (dealValue == null || dealValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // FIX: Comment explains WHY (business reason) not just WHAT
        // Apply 15% penalty for deals closed after quarter end
        // per CFO directive to discourage revenue manipulation and sandbagging
        if (isAfterQuarterEnd(deal.getCloseDate())) {
            dealValue = dealValue.multiply(new BigDecimal("0.85"));
        }

        BigDecimal baseRate = plan.getBaseRate();
        BigDecimal baseCommission = dealValue.multiply(baseRate);

        // FIX: Comment provides business context and approval source
        // Apply tiered multipliers to incentivize larger deals
        // Tier structure approved by Sales VP in Q4 2023 planning session
        if (dealValue.compareTo(new BigDecimal("100000")) > 0) {
            // FIX: Comment explains business strategy behind the calculation
            // 50% bonus for enterprise deals (>$100K) to drive high-value sales
            baseCommission = baseCommission.multiply(new BigDecimal("1.5"));
        } else if (dealValue.compareTo(new BigDecimal("50000")) > 0) {
            // FIX: Comment explains business context
            // 20% bonus for mid-market deals ($50K-$100K)
            baseCommission = baseCommission.multiply(new BigDecimal("1.2"));
        }

        return baseCommission;
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private boolean isAfterQuarterEnd(LocalDate date) {
        Month month = date.getMonth();
        int day = date.getDayOfMonth();

        // FIX: Comment explains business definition of quarters
        // Standard fiscal quarters: Q1=Jan-Mar, Q2=Apr-Jun, Q3=Jul-Sep, Q4=Oct-Dec
        // Any deal closed after the last day of its quarter is considered "late"
        // FIX: Concise implementation without redundant if statements
        return (month == Month.APRIL && day >= 1) ||
               (month == Month.JULY && day >= 1) ||
               (month == Month.OCTOBER && day >= 1) ||
               (month == Month.JANUARY && day >= 1); // Next year Q1
    }

    public BigDecimal calculateBonus(Deal deal, User user) {
        BigDecimal bonus = BigDecimal.ZERO;

        // FIX: Comment explains business purpose behind thresholds
        // Individual contributor bonuses for exceeding minimum thresholds
        // Thresholds set to encourage reps to pursue larger opportunities
        // FIX: Simplified conditional with combined checks
        if (user.isSalesRep() && deal.getValue().compareTo(new BigDecimal("25000")) > 0) {
            // FIX: Comment provides business context about average deal size
            // $1K bonus for reps closing deals >$25K (above average deal size)
            bonus = bonus.add(new BigDecimal("1000"));
        }

        // FIX: Comment explains management strategy
        // Management bonuses for team oversight of high-value deals
        // Ensures managers stay engaged with large opportunities
        // FIX: Simplified conditional with combined checks
        if (user.isSalesManager() && deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            // FIX: Clear comment with business value
            // $2K bonus for managers overseeing deals >$50K
            bonus = bonus.add(new BigDecimal("2000"));
        }

        return bonus;
    }
}
