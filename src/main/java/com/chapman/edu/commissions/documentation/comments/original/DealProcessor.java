package com.chapman.edu.commissions.documentation.comments.original;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Deal processor class that processes deals
 */
public class DealProcessor {

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method processes a deal
     * @param deal the deal to process
     */
    public void processDeal(Deal deal) throws InterruptedException {
        // VIOLATION: Comment that just restates what the code does without business context
        // Check if deal value is greater than 50000
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Set status to requires approval
            deal.setStatus(DealStatus.REQUIRES_APPROVAL);
            // VIOLATION: Unnecessary comment for obvious operation
            // Notify manager
            notifyManager(deal);
        }

        // VIOLATION: Comment that just restates what the code does without business context
        // Check if close date is after quarter end
        if (deal.getCloseDate().isAfter(getQuarterEnd())) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Apply late penalty
            applyLatePenalty(deal);
        }

        // VIOLATION: Comment that doesn't explain the business impact or timeline for removal
        // Sleep for 2 seconds
        Thread.sleep(2000);

        // VIOLATION: Comment that just restates what the code does without business context
        // Validate with CRM
        validateWithCRM(deal);

        // VIOLATION: Comment that just restates what the code does without business context
        // Check if deal is high value
        if (deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Send to finance team
            sendToFinanceTeam(deal);
        }

        // VIOLATION: Unnecessary comment for obvious operation
        // Update deal status
        deal.setStatus(DealStatus.PROCESSED);
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method applies late penalty to a deal
     * @param deal the deal to apply penalty to
     */
    private void applyLatePenalty(Deal deal) {
        // VIOLATION: Comment that just describes the calculation without business context
        // Calculate penalty as 10% of deal value
        BigDecimal penalty = deal.getValue().multiply(new BigDecimal("0.1"));
        // VIOLATION: Unnecessary comment for obvious operation
        // Subtract penalty from deal value
        deal.setValue(deal.getValue().subtract(penalty));
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method gets the quarter end date
     * @return the quarter end date
     */
    private LocalDate getQuarterEnd() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Get current date
        LocalDate now = LocalDate.now();
        // VIOLATION: Unnecessary comment for obvious operation
        // Get current month
        int month = now.getMonthValue();
        // VIOLATION: Unnecessary comment for obvious operation
        // Get current year
        int year = now.getYear();

        // VIOLATION: Comment that just restates what the code does without business context
        // Check which quarter we're in
        if (month <= 3) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return March 31st for Q1
            return LocalDate.of(year, 3, 31);
        } else if (month <= 6) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return June 30th for Q2
            return LocalDate.of(year, 6, 30);
        } else if (month <= 9) {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return September 30th for Q3
            return LocalDate.of(year, 9, 30);
        } else {
            // VIOLATION: Unnecessary comment for obvious operation
            // Return December 31st for Q4
            return LocalDate.of(year, 12, 31);
        }
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method notifies the manager
     * @param deal the deal to notify about
     */
    private void notifyManager(Deal deal) {
        // VIOLATION: Comment that just restates what the code does without business context
        // Implementation would send notification
        System.out.println("Notifying manager about deal: " + deal.getId());
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method validates deal with CRM
     * @param deal the deal to validate
     */
    private void validateWithCRM(Deal deal) {
        // VIOLATION: Comment that just restates what the code does without business context
        // Implementation would validate with external CRM system
        System.out.println("Validating deal with CRM: " + deal.getId());
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * This method sends deal to finance team
     * @param deal the deal to send
     */
    private void sendToFinanceTeam(Deal deal) {
        // VIOLATION: Comment that just restates what the code does without business context
        // Implementation would send to finance team
        System.out.println("Sending deal to finance team: " + deal.getId());
    }
}
