package com.chapman.edu.commissions.documentation.naming.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * FIX: Clear class description that explains business purpose
 * Processes sales deals through validation, approval workflows, and business rule application.
 */
public class DealProcessor {

    // FIX: Descriptive constants instead of magic numbers
    private static final BigDecimal MANAGER_APPROVAL_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal FINANCE_REVIEW_THRESHOLD = new BigDecimal("100000");
    private static final BigDecimal LATE_CLOSURE_PENALTY_RATE = new BigDecimal("0.1");
    private static final BigDecimal COMMISSION_ELIGIBILITY_THRESHOLD = new BigDecimal("10000");

    // FIX: Descriptive method name "processDealForApproval" instead of vague "process"
    // FIX: Descriptive parameter name "submittedDeal" instead of single letter "d"
    public void processDealForApproval(Deal submittedDeal) throws InterruptedException {
        // FIX: Extracted method with descriptive name instead of inline comparison
        if (requiresManagerApproval(submittedDeal)) {
            // FIX: Descriptive method name "setStatus" instead of abbreviated "setStat"
            // FIX: Descriptive enum value "REQUIRES_APPROVAL" instead of abbreviated "RA"
            submittedDeal.setStatus(DealStatus.REQUIRES_APPROVAL);
            // FIX: Descriptive method name "notifyManagerOfPendingApproval" instead of vague "notify"
            notifyManagerOfPendingApproval(submittedDeal);
        }

        // FIX: Descriptive method name "isClosedAfterQuarterEnd" instead of abbreviated "getQE"
        if (isClosedAfterQuarterEnd(submittedDeal)) {
            // FIX: Descriptive method name "applyLateClosurePenalty" instead of vague "apply"
            applyLateClosurePenalty(submittedDeal);
        }

        // FIX: Explanatory comment for the sleep operation
        // Temporary workaround for CRM system delay
        Thread.sleep(2000);

        // FIX: Descriptive method name "validateDealWithExternalSystems" instead of vague "validate"
        validateDealWithExternalSystems(submittedDeal);

        // FIX: Extracted method with descriptive name instead of inline comparison
        if (requiresFinanceReview(submittedDeal)) {
            // FIX: Descriptive method name "routeDealToFinanceTeam" instead of vague "send"
            routeDealToFinanceTeam(submittedDeal);
        }

        // FIX: Descriptive enum value "PROCESSED" instead of abbreviated "P"
        submittedDeal.setStatus(DealStatus.PROCESSED);
    }

    // FIX: Descriptive method name "processMultipleDeals" instead of vague "processAll"
    // FIX: Descriptive parameter name "pendingDeals" instead of generic "deals"
    public void processMultipleDeals(List<Deal> pendingDeals) {
        // FIX: Descriptive variable name "currentDeal" instead of single letter "d"
        for (Deal currentDeal : pendingDeals) {
            try {
                processDealForApproval(currentDeal);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // FIX: Descriptive method name with "requires" prefix for boolean method
    private boolean requiresManagerApproval(Deal deal) {
        // FIX: Using constant instead of magic number
        return deal.getValue().compareTo(MANAGER_APPROVAL_THRESHOLD) > 0;
    }

    // FIX: Descriptive method name with "requires" prefix for boolean method
    private boolean requiresFinanceReview(Deal deal) {
        // FIX: Using constant instead of magic number
        return deal.getValue().compareTo(FINANCE_REVIEW_THRESHOLD) > 0;
    }

    // FIX: Descriptive method name with "is" prefix for boolean method instead of abbreviated "getQE"
    private boolean isClosedAfterQuarterEnd(Deal deal) {
        // FIX: Descriptive variable name "quarterEndDate" instead of inline call
        LocalDate quarterEndDate = calculateQuarterEndDate();
        // FIX: Descriptive method name "getCloseDate" instead of generic "getDate"
        return deal.getCloseDate().isAfter(quarterEndDate);
    }

    // FIX: Descriptive method name "applyLateClosurePenalty" instead of vague "apply"
    // FIX: Descriptive parameter name "lateClosedDeal" instead of single letter "d"
    private void applyLateClosurePenalty(Deal lateClosedDeal) {
        // FIX: Descriptive variable name "penaltyAmount" instead of single letter "p"
        BigDecimal penaltyAmount = lateClosedDeal.getValue().multiply(LATE_CLOSURE_PENALTY_RATE);
        // FIX: Descriptive variable name "adjustedValue" for clarity
        BigDecimal adjustedValue = lateClosedDeal.getValue().subtract(penaltyAmount);
        lateClosedDeal.setValue(adjustedValue);
    }

    // FIX: Descriptive method name "calculateQuarterEndDate" instead of abbreviated "getQE"
    private LocalDate calculateQuarterEndDate() {
        // FIX: Descriptive variable name "currentDate" instead of generic "now"
        LocalDate currentDate = LocalDate.now();
        // FIX: Descriptive variable names "currentMonth" and "currentYear" instead of single letters
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        if (currentMonth <= 3) {
            return LocalDate.of(currentYear, 3, 31);
        } else if (currentMonth <= 6) {
            return LocalDate.of(currentYear, 6, 30);
        } else if (currentMonth <= 9) {
            return LocalDate.of(currentYear, 9, 30);
        } else {
            return LocalDate.of(currentYear, 12, 31);
        }
    }

    // FIX: Descriptive method name "notifyManagerOfPendingApproval" instead of vague "notify"
    // FIX: Descriptive parameter name "highValueDeal" instead of single letter "d"
    private void notifyManagerOfPendingApproval(Deal highValueDeal) {
        // FIX: Descriptive variable name "notificationMessage" for clarity
        String notificationMessage = "High-value deal pending approval: " + highValueDeal.getId();
        System.out.println(notificationMessage);
    }

    // FIX: Descriptive method name "validateDealWithExternalSystems" instead of vague "validate"
    // FIX: Descriptive parameter name "dealToValidate" instead of single letter "d"
    private void validateDealWithExternalSystems(Deal dealToValidate) {
        // FIX: Descriptive variable name "validationMessage" for clarity
        String validationMessage = "Validating deal with CRM system: " + dealToValidate.getId();
        System.out.println(validationMessage);
    }

    // FIX: Descriptive method name "routeDealToFinanceTeam" instead of vague "send"
    // FIX: Descriptive parameter name "highValueDeal" instead of single letter "d"
    private void routeDealToFinanceTeam(Deal highValueDeal) {
        // FIX: Descriptive variable name "routingMessage" for clarity
        String routingMessage = "Routing deal to finance team for review: " + highValueDeal.getId();
        System.out.println(routingMessage);
    }

    // FIX: Descriptive method name with "is" prefix for boolean method instead of vague "check"
    // FIX: Descriptive parameter names instead of single letters
    public boolean isEligibleForCommissionCalculation(Deal closedDeal, User salesRepresentative) {
        // FIX: Using constant instead of magic number
        // FIX: Descriptive method name "isSalesRepresentative" instead of vague "check"
        return closedDeal.getValue().compareTo(COMMISSION_ELIGIBILITY_THRESHOLD) > 0 && 
               salesRepresentative.isSalesRepresentative();
    }

    // FIX: Descriptive method name "markDealAsCommissionEligible" instead of vague "doStuff"
    // FIX: Descriptive parameter names instead of single letters
    public void markDealAsCommissionEligible(Deal qualifyingDeal, User salesRepresentative) {
        if (isEligibleForCommissionCalculation(qualifyingDeal, salesRepresentative)) {
            // FIX: Descriptive method name "setCommissionEligible" instead of vague "setFlag"
            qualifyingDeal.setCommissionEligible(true);
        }
    }

    // FIX: Descriptive method name "calculateTotalValueOfClosedDeals" instead of abbreviated "calc"
    // FIX: Descriptive parameter name "dealPortfolio" instead of generic "deals"
    public BigDecimal calculateTotalValueOfClosedDeals(List<Deal> dealPortfolio) {
        // FIX: Descriptive variable name "totalClosedValue" instead of generic "total"
        BigDecimal totalClosedValue = BigDecimal.ZERO;

        // FIX: Descriptive variable name "currentDeal" instead of single letter "d"
        for (Deal currentDeal : dealPortfolio) {
            // FIX: Descriptive enum value "CLOSED" instead of abbreviated "C"
            if (currentDeal.getStatus() == DealStatus.CLOSED) {
                totalClosedValue = totalClosedValue.add(currentDeal.getValue());
            }
        }

        return totalClosedValue;
    }
}
