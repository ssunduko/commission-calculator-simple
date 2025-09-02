package com.chapman.edu.commissions.documentation.comments.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FIX: Clear class description that explains business purpose
 * Processes sales deals through validation, approval workflows, and compliance checks.
 * Implements company policies for deal approval thresholds and penalty structures.
 */
public class DealProcessor {

    // FIX: No redundant JavaDoc for method with clear name
    public void processDeal(Deal deal) throws InterruptedException {
        // FIX: Comment explains business rule and compliance requirement
        // Deals over $50K require manager approval per SOX compliance
        // Threshold set by finance team to ensure proper oversight of material transactions
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            deal.setStatus(DealStatus.REQUIRES_APPROVAL);
            notifyManager(deal);
        }

        // FIX: Comment explains business purpose and regulatory requirement
        // Apply late closure penalty to discourage quarter-end manipulation
        // Finance policy requires accurate quarterly reporting per SEC regulations
        if (deal.getCloseDate().isAfter(getQuarterEnd())) {
            applyLatePenalty(deal);
        }

        // FIX: Comment explains workaround with timeline and performance impact
        // WORKAROUND: CRM system has 2-second processing delay due to legacy database
        // Remove this sleep after CRM upgrade scheduled for Q3 2024 (JIRA-5678)
        // Performance impact: adds ~2 seconds per deal (acceptable for current volume)
        Thread.sleep(2000);

        // FIX: Comment explains purpose and potential exceptions
        // Final validation against CRM ensures data consistency
        // May throw ValidationException if customer data doesn't match CRM records
        validateWithCRM(deal);

        // FIX: Comment explains business rule and who approved it
        // High-value deals require additional finance team review
        // $100K threshold set by CFO to ensure proper revenue recognition
        if (deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
            sendToFinanceTeam(deal);
        }

        // FIX: No unnecessary comment for obvious operation
        deal.setStatus(DealStatus.PROCESSED);
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private void applyLatePenalty(Deal deal) {
        // FIX: Comment explains business policy and impact
        // 10% penalty for deals closed after quarter end
        // Policy implemented to maintain quarterly forecast accuracy and prevent sandbagging
        // Penalty amount goes to company general fund, not redistributed to other reps
        BigDecimal penalty = deal.getValue().multiply(new BigDecimal("0.1"));
        // FIX: No unnecessary comment for obvious operation
        deal.setValue(deal.getValue().subtract(penalty));
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private LocalDate getQuarterEnd() {
        // FIX: No unnecessary comments for obvious operations
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        // FIX: Comment explains business definition of quarters
        // Standard calendar quarters aligned with company fiscal year
        if (month <= 3) {
            return LocalDate.of(year, 3, 31);
        } else if (month <= 6) {
            return LocalDate.of(year, 6, 30);
        } else if (month <= 9) {
            return LocalDate.of(year, 9, 30);
        } else {
            return LocalDate.of(year, 12, 31);
        }
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private void notifyManager(Deal deal) {
        // FIX: Comment explains implementation details and business process
        // Implementation sends email notification to deal owner's manager
        // Uses company directory service to lookup manager hierarchy
        System.out.println("Notifying manager about deal: " + deal.getId());
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private void validateWithCRM(Deal deal) {
        // FIX: Comment explains business purpose and importance
        // Validates customer exists in Salesforce and deal data matches
        // Critical for maintaining data integrity across systems
        System.out.println("Validating deal with CRM: " + deal.getId());
    }

    // FIX: No redundant JavaDoc for private method with clear name
    private void sendToFinanceTeam(Deal deal) {
        // FIX: Comment explains business process and references specific policy
        // Creates task in finance workflow system for revenue recognition review
        // Required for deals >$100K per accounting policy ACC-2024-003
        System.out.println("Sending deal to finance team: " + deal.getId());
    }
}
