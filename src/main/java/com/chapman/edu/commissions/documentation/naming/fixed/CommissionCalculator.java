package com.chapman.edu.commissions.documentation.naming.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * FIX: Clear class description that explains business purpose
 * Calculates sales commissions based on deal values, user roles, and business rules.
 */
public class CommissionCalculator {

    // FIX: Descriptive constants instead of magic numbers
    private static final BigDecimal QUARTER_END_PENALTY_RATE = new BigDecimal("0.85");
    private static final BigDecimal HIGH_VALUE_MULTIPLIER = new BigDecimal("1.5");
    private static final BigDecimal MID_VALUE_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal MID_VALUE_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal SALES_REP_BONUS_THRESHOLD = new BigDecimal("25000");
    private static final BigDecimal SALES_MANAGER_BONUS_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal SALES_REP_BONUS_AMOUNT = new BigDecimal("1000");
    private static final BigDecimal SALES_MANAGER_BONUS_AMOUNT = new BigDecimal("2000");
    private static final BigDecimal WEST_TERRITORY_MULTIPLIER = new BigDecimal("1.1");
    private static final BigDecimal EAST_TERRITORY_MULTIPLIER = new BigDecimal("1.05");

    // FIX: Descriptive method name "calculateBaseCommission" instead of abbreviated "calc"
    // FIX: Descriptive parameter names "closedDeal" and "commissionPlan" instead of single letters
    public BigDecimal calculateBaseCommission(Deal closedDeal, CommissionPlan commissionPlan) {
        // FIX: Descriptive variable name "dealValue" instead of abbreviated "val"
        BigDecimal dealValue = closedDeal.getValue();

        if (dealValue == null || dealValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // FIX: Descriptive method name "isClosedAfterQuarterEnd" instead of vague "check"
        if (isClosedAfterQuarterEnd(closedDeal.getCloseDate())) {
            // FIX: Extracted method with descriptive name instead of inline calculation
            dealValue = applyQuarterEndPenalty(dealValue);
        }

        // FIX: Descriptive variable name "commissionRate" instead of abbreviated "rate"
        BigDecimal commissionRate = commissionPlan.getBaseCommissionRate();
        // FIX: Descriptive variable name "baseCommission" instead of generic "result"
        BigDecimal baseCommission = dealValue.multiply(commissionRate);

        // FIX: Extracted method with descriptive name for better readability
        return applyValueTierMultiplier(baseCommission, dealValue);
    }

    // FIX: Extracted method with descriptive name instead of inline calculation
    private BigDecimal applyQuarterEndPenalty(BigDecimal dealValue) {
        return dealValue.multiply(QUARTER_END_PENALTY_RATE);
    }

    // FIX: Extracted method with descriptive name for better readability
    private BigDecimal applyValueTierMultiplier(BigDecimal baseCommission, BigDecimal dealValue) {
        if (dealValue.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            return baseCommission.multiply(HIGH_VALUE_MULTIPLIER);
        } else if (dealValue.compareTo(MID_VALUE_THRESHOLD) > 0) {
            return baseCommission.multiply(MID_VALUE_MULTIPLIER);
        }
        return baseCommission;
    }

    // FIX: Descriptive method name "isClosedAfterQuarterEnd" instead of vague "check"
    // FIX: Descriptive parameter name "closeDate" instead of abbreviated "dt"
    private boolean isClosedAfterQuarterEnd(LocalDate closeDate) {
        // FIX: Descriptive variable names "month" and "dayOfMonth" instead of single letters
        Month month = closeDate.getMonth();
        int dayOfMonth = closeDate.getDayOfMonth();

        return (month == Month.APRIL && dayOfMonth >= 1) ||
               (month == Month.JULY && dayOfMonth >= 1) ||
               (month == Month.OCTOBER && dayOfMonth >= 1) ||
               (month == Month.JANUARY && dayOfMonth >= 1);
    }

    // FIX: Descriptive method name "calculateRoleBasedBonus" instead of inconsistent "calc2"
    // FIX: Descriptive parameter names "closedDeal" and "salesRepresentative" instead of single letters
    public BigDecimal calculateRoleBasedBonus(Deal closedDeal, User salesRepresentative) {
        // FIX: Descriptive variable name "totalBonus" instead of single letter "b"
        BigDecimal totalBonus = BigDecimal.ZERO;

        // FIX: Descriptive method names "isSalesRepresentative" instead of vague "check"
        if (salesRepresentative.isSalesRepresentative() && 
            closedDeal.getValue().compareTo(SALES_REP_BONUS_THRESHOLD) > 0) {
            totalBonus = totalBonus.add(SALES_REP_BONUS_AMOUNT);
        }

        // FIX: Descriptive method names "isSalesManager" instead of vague "check2"
        if (salesRepresentative.isSalesManager() && 
            closedDeal.getValue().compareTo(SALES_MANAGER_BONUS_THRESHOLD) > 0) {
            totalBonus = totalBonus.add(SALES_MANAGER_BONUS_AMOUNT);
        }

        return totalBonus;
    }

    // FIX: Descriptive method name "calculateTotalCommissionWithBonuses" instead of vague "process"
    // FIX: Descriptive parameter names instead of single letters
    public BigDecimal calculateTotalCommissionWithBonuses(Deal closedDeal, User salesRepresentative, CommissionPlan commissionPlan) {
        // FIX: Descriptive variable names "baseCommission" and "roleBonus" instead of generic names
        BigDecimal baseCommission = calculateBaseCommission(closedDeal, commissionPlan);
        BigDecimal roleBonus = calculateRoleBasedBonus(closedDeal, salesRepresentative);
        return baseCommission.add(roleBonus);
    }

    // FIX: Descriptive method name "isEligibleForSeniorRepresentativeBonus" instead of vague "validate"
    // FIX: Descriptive parameter name "salesRepresentative" instead of single letter "u"
    public boolean isEligibleForSeniorRepresentativeBonus(User salesRepresentative) {
        // FIX: Descriptive method name "isEligibleForSeniorBonus" instead of abbreviated "getCd"
        return salesRepresentative.isEligibleForSeniorBonus();
    }

    // FIX: Descriptive method name "applyTerritoryMultiplier" instead of inconsistent "doCalc"
    // FIX: Descriptive parameter names "commissionAmount" and "territoryCode" instead of "amt" and "type"
    public BigDecimal applyTerritoryMultiplier(BigDecimal commissionAmount, String territoryCode) {
        // FIX: Using constants instead of magic numbers
        if ("WEST".equals(territoryCode)) {
            return commissionAmount.multiply(WEST_TERRITORY_MULTIPLIER);
        } else if ("EAST".equals(territoryCode)) {
            return commissionAmount.multiply(EAST_TERRITORY_MULTIPLIER);
        }
        return commissionAmount;
    }
}
