package com.chapman.edu.commissions.documentation.naming.original;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Calculator class
 */
public class CommissionCalculator {

    // VIOLATION: Abbreviated method name "calc" instead of descriptive "calculateBaseCommission"
    // VIOLATION: Single-letter parameter names "d" and "cp" instead of descriptive names
    public BigDecimal calc(Deal d, CommissionPlan cp) {
        // VIOLATION: Abbreviated variable name "val" instead of descriptive "dealValue"
        BigDecimal val = d.getVal();

        if (val == null || val.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // VIOLATION: Vague method name "check" instead of descriptive "isClosedAfterQuarterEnd"
        if (check(d.getDate())) {
            // VIOLATION: Magic number without constant or explanation
            val = val.multiply(new BigDecimal("0.85"));
        }

        // VIOLATION: Abbreviated variable name "rate" instead of descriptive "commissionRate"
        BigDecimal rate = cp.getRate();
        // VIOLATION: Generic variable name "result" instead of descriptive "baseCommission"
        BigDecimal result = val.multiply(rate);

        // VIOLATION: Magic numbers without constants or explanation
        if (val.compareTo(new BigDecimal("100000")) > 0) {
            result = result.multiply(new BigDecimal("1.5"));
        } else if (val.compareTo(new BigDecimal("50000")) > 0) {
            result = result.multiply(new BigDecimal("1.2"));
        }

        return result;
    }

    // VIOLATION: Vague method name "check" instead of descriptive "isClosedAfterQuarterEnd"
    // VIOLATION: Abbreviated parameter name "dt" instead of descriptive "closeDate"
    private boolean check(LocalDate dt) {
        // VIOLATION: Single-letter variable names "m" and "d" instead of descriptive names
        Month m = dt.getMonth();
        int d = dt.getDayOfMonth();

        return (m == Month.APRIL && d >= 1) ||
               (m == Month.JULY && d >= 1) ||
               (m == Month.OCTOBER && d >= 1) ||
               (m == Month.JANUARY && d >= 1);
    }

    // VIOLATION: Inconsistent method naming "calc2" instead of descriptive "calculateRoleBasedBonus"
    // VIOLATION: Single-letter parameter names "d" and "u" instead of descriptive names
    public BigDecimal calc2(Deal d, User u) {
        // VIOLATION: Single-letter variable name "b" instead of descriptive "totalBonus"
        BigDecimal b = BigDecimal.ZERO;

        // VIOLATION: Vague method names "check" and "check2" in User class
        // VIOLATION: Magic numbers without constants or explanation
        if (u.check() && d.getVal().compareTo(new BigDecimal("25000")) > 0) {
            b = b.add(new BigDecimal("1000"));
        }

        if (u.check2() && d.getVal().compareTo(new BigDecimal("50000")) > 0) {
            b = b.add(new BigDecimal("2000"));
        }

        return b;
    }

    // VIOLATION: Vague method name "process" instead of descriptive "calculateTotalCommissionWithBonuses"
    // VIOLATION: Single-letter parameter names "d", "u", and "cp" instead of descriptive names
    public BigDecimal process(Deal d, User u, CommissionPlan cp) {
        // VIOLATION: Generic variable names "base" and "bonus" instead of descriptive names
        BigDecimal base = calc(d, cp);
        BigDecimal bonus = calc2(d, u);
        return base.add(bonus);
    }

    // VIOLATION: Vague method name "validate" instead of descriptive "isEligibleForSeniorRepresentativeBonus"
    // VIOLATION: Single-letter parameter name "u" instead of descriptive "salesRepresentative"
    public boolean validate(User u) {
        // VIOLATION: Abbreviated method name "getCd" in User class instead of descriptive name
        // VIOLATION: Vague variable name "start" instead of descriptive "hireDate"
        LocalDate start = u.getCd();
        // VIOLATION: Vague variable name "cutoff" instead of descriptive "seniorityThresholdDate"
        LocalDate cutoff = LocalDate.now().minusYears(2);
        return start.isBefore(cutoff);
    }

    // VIOLATION: Inconsistent method naming "doCalc" instead of descriptive "applyTerritoryMultiplier"
    // VIOLATION: Abbreviated parameter name "amt" instead of descriptive "commissionAmount"
    // VIOLATION: Vague parameter name "type" instead of descriptive "territoryCode"
    public BigDecimal doCalc(BigDecimal amt, String type) {
        // VIOLATION: Magic numbers without constants or explanation
        if ("WEST".equals(type)) {
            return amt.multiply(new BigDecimal("1.1"));
        } else if ("EAST".equals(type)) {
            return amt.multiply(new BigDecimal("1.05"));
        }
        return amt;
    }
}
