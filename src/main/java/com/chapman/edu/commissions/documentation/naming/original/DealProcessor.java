package com.chapman.edu.commissions.documentation.naming.original;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Processor class
 */
public class DealProcessor {

    // VIOLATION: Vague method name "process" instead of descriptive "processDealForApproval"
    // VIOLATION: Single-letter parameter name "d" instead of descriptive "submittedDeal"
    public void process(Deal d) throws InterruptedException {
        // VIOLATION: Magic number without constant or explanation
        // VIOLATION: Abbreviated method names "getVal" and "setStat"
        // VIOLATION: Enum value abbreviation "RA" instead of descriptive "REQUIRES_APPROVAL"
        if (d.getVal().compareTo(new BigDecimal("50000")) > 0) {
            d.setStat(DealStatus.RA);
            // VIOLATION: Vague method name "notify" instead of descriptive "notifyManagerOfPendingApproval"
            notify(d);
        }

        // VIOLATION: Abbreviated method name "getQE" instead of descriptive "calculateQuarterEndDate"
        if (d.getDate().isAfter(getQE())) {
            // VIOLATION: Vague method name "apply" instead of descriptive "applyLateClosurePenalty"
            apply(d);
        }

        // VIOLATION: No comment explaining the purpose of this sleep
        Thread.sleep(2000);

        // VIOLATION: Vague method name "validate" instead of descriptive "validateDealWithExternalSystems"
        validate(d);

        // VIOLATION: Magic number without constant or explanation
        if (d.getVal().compareTo(new BigDecimal("100000")) > 0) {
            // VIOLATION: Vague method name "send" instead of descriptive "routeDealToFinanceTeam"
            send(d);
        }

        // VIOLATION: Enum value abbreviation "P" instead of descriptive "PROCESSED"
        d.setStat(DealStatus.P);
    }

    // VIOLATION: Vague method name "processAll" instead of descriptive "processMultipleDeals"
    // VIOLATION: Generic parameter name "deals" instead of descriptive "pendingDeals"
    public void processAll(List<Deal> deals) {
        // VIOLATION: Single-letter variable name "d" instead of descriptive "currentDeal"
        for (Deal d : deals) {
            try {
                process(d);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // VIOLATION: Vague method name "apply" instead of descriptive "applyLateClosurePenalty"
    // VIOLATION: Single-letter parameter name "d" instead of descriptive "lateClosedDeal"
    private void apply(Deal d) {
        // VIOLATION: Single-letter variable name "p" instead of descriptive "penaltyAmount"
        // VIOLATION: Magic number without constant or explanation
        BigDecimal p = d.getVal().multiply(new BigDecimal("0.1"));
        d.setVal(d.getVal().subtract(p));
    }

    // VIOLATION: Abbreviated method name "getQE" instead of descriptive "calculateQuarterEndDate"
    private LocalDate getQE() {
        // VIOLATION: Generic variable name "now" instead of descriptive "currentDate"
        LocalDate now = LocalDate.now();
        // VIOLATION: Single-letter variable names "m" and "y" instead of descriptive names
        int m = now.getMonthValue();
        int y = now.getYear();

        if (m <= 3) {
            return LocalDate.of(y, 3, 31);
        } else if (m <= 6) {
            return LocalDate.of(y, 6, 30);
        } else if (m <= 9) {
            return LocalDate.of(y, 9, 30);
        } else {
            return LocalDate.of(y, 12, 31);
        }
    }

    // VIOLATION: Vague method name "notify" instead of descriptive "notifyManagerOfPendingApproval"
    // VIOLATION: Single-letter parameter name "d" instead of descriptive "highValueDeal"
    private void notify(Deal d) {
        // VIOLATION: Generic message without context
        System.out.println("Alert: " + d.getId());
    }

    // VIOLATION: Vague method name "validate" instead of descriptive "validateDealWithExternalSystems"
    // VIOLATION: Single-letter parameter name "d" instead of descriptive "dealToValidate"
    private void validate(Deal d) {
        // VIOLATION: Generic message without context
        System.out.println("Check: " + d.getId());
    }

    // VIOLATION: Vague method name "send" instead of descriptive "routeDealToFinanceTeam"
    // VIOLATION: Single-letter parameter name "d" instead of descriptive "highValueDeal"
    private void send(Deal d) {
        // VIOLATION: Generic message without context
        System.out.println("Send: " + d.getId());
    }

    // VIOLATION: Vague method name "check" instead of descriptive "isEligibleForCommissionCalculation"
    // VIOLATION: Single-letter parameter names "d" and "u" instead of descriptive names
    public boolean check(Deal d, User u) {
        // VIOLATION: Magic number without constant or explanation
        // VIOLATION: Vague method name "check" in User class
        return d.getVal().compareTo(new BigDecimal("10000")) > 0 && u.check();
    }

    // VIOLATION: Extremely vague method name "doStuff" instead of descriptive "markDealAsCommissionEligible"
    // VIOLATION: Single-letter parameter names "d" and "u" instead of descriptive names
    public void doStuff(Deal d, User u) {
        if (check(d, u)) {
            // VIOLATION: Vague method name "setFlag" instead of descriptive "setCommissionEligible"
            d.setFlag(true);
        }
    }

    // VIOLATION: Abbreviated method name "calc" instead of descriptive "calculateTotalValueOfClosedDeals"
    // VIOLATION: Generic parameter name "deals" instead of descriptive "dealPortfolio"
    public BigDecimal calc(List<Deal> deals) {
        // VIOLATION: Generic variable name "total" instead of descriptive "totalClosedValue"
        BigDecimal total = BigDecimal.ZERO;
        // VIOLATION: Single-letter variable name "d" instead of descriptive "currentDeal"
        for (Deal d : deals) {
            // VIOLATION: Enum value abbreviation "C" instead of descriptive "CLOSED"
            if (d.getStat() == DealStatus.C) {
                total = total.add(d.getVal());
            }
        }
        return total;
    }
}
