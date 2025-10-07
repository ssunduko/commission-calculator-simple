package com.chapman.edu.commissions.patterns.behavioral.state;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.chapman.edu.commissions.patterns.behavioral.state.StateStructure.*;

/**
 * STATE PATTERN - COMMISSION DISPUTE LIFECYCLE IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation models a commission dispute resolution workflow.
 * A dispute moves through different states (Submitted, Under Review, Resolved, Escalated, Closed),
 * and each state has different allowed actions and business rules.
 *
 * BUSINESS CONTEXT:
 * When a sales representative disputes their commission calculation, the dispute goes through
 * a formal workflow. Each state in the workflow has specific actions that can be performed
 * and specific rules about what can happen next.
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Eliminates complex if/else chains checking dispute status
 * 2. Makes state transitions explicit and enforces valid state changes
 * 3. Encapsulates state-specific validation and business rules
 * 4. Easy to add new states (e.g., "Pending Legal Review") without modifying existing code
 * 5. Each state's behavior is isolated and testable
 *
 */
public class StateImplementation {

    /**
     * SUBMITTED STATE
     *
     * Initial state when a dispute is first submitted.
     * In this state, we can add comments, assign a reviewer, or escalate if urgent.
     * Cannot approve/reject until it's under review.
     */
    public static class SubmittedState implements DisputeState {
        @Override
        public void addComment(DisputeContext context, String comment, String author) {
            context.addCommentToHistory(author, comment);
            System.out.println("✓ Comment added by " + author);
        }

        @Override
        public void assignReviewer(DisputeContext context, String reviewerName) {
            context.setReviewer(reviewerName);
            System.out.println("✓ Assigned to reviewer: " + reviewerName);
            System.out.println("→ Transitioning to Under Review state");
            context.setState(new UnderReviewState());
        }

        @Override
        public void approve(DisputeContext context, BigDecimal adjustedAmount) {
            System.out.println("✗ Cannot approve - dispute must be assigned to a reviewer first");
        }

        @Override
        public void reject(DisputeContext context, String reason) {
            System.out.println("✗ Cannot reject - dispute must be assigned to a reviewer first");
        }

        @Override
        public void escalate(DisputeContext context, String reason) {
            context.addCommentToHistory("System", "Escalated: " + reason);
            System.out.println("⬆ Escalating dispute: " + reason);
            context.setState(new EscalatedState());
        }

        @Override
        public void close(DisputeContext context) {
            System.out.println("✗ Cannot close - dispute must be resolved or rejected first");
        }

        @Override
        public String getStateName() {
            return "SUBMITTED";
        }

        @Override
        public String getAvailableActions() {
            return "Available: addComment, assignReviewer, escalate";
        }
    }

    /**
     * UNDER REVIEW STATE
     *
     * The dispute has been assigned to a reviewer and is being investigated.
     * Reviewer can add comments, approve, reject, or escalate for complex cases.
     */
    public static class UnderReviewState implements DisputeState {
        @Override
        public void addComment(DisputeContext context, String comment, String author) {
            context.addCommentToHistory(author, comment);
            System.out.println("✓ Review comment added by " + author);
        }

        @Override
        public void assignReviewer(DisputeContext context, String reviewerName) {
            // Can reassign during review if needed
            context.setReviewer(reviewerName);
            System.out.println("✓ Reassigned to reviewer: " + reviewerName);
        }

        @Override
        public void approve(DisputeContext context, BigDecimal adjustedAmount) {
            context.setAdjustedAmount(adjustedAmount);
            context.addCommentToHistory(context.getReviewer(),
                "Approved with adjusted amount: $" + adjustedAmount);
            System.out.println("✓ Dispute approved! Adjusted amount: $" + adjustedAmount);
            System.out.println("→ Transitioning to Resolved state");
            context.setState(new ResolvedState());
        }

        @Override
        public void reject(DisputeContext context, String reason) {
            context.addCommentToHistory(context.getReviewer(), "Rejected: " + reason);
            System.out.println("✗ Dispute rejected: " + reason);
            System.out.println("→ Transitioning to Resolved state");
            context.setState(new ResolvedState());
        }

        @Override
        public void escalate(DisputeContext context, String reason) {
            context.addCommentToHistory(context.getReviewer(), "Escalated: " + reason);
            System.out.println("⬆ Escalating to management: " + reason);
            context.setState(new EscalatedState());
        }

        @Override
        public void close(DisputeContext context) {
            System.out.println("✗ Cannot close - dispute must be approved or rejected first");
        }

        @Override
        public String getStateName() {
            return "UNDER_REVIEW";
        }

        @Override
        public String getAvailableActions() {
            return "Available: addComment, assignReviewer, approve, reject, escalate";
        }
    }

    /**
     * ESCALATED STATE
     *
     * The dispute requires management attention.
     * Management can add comments, approve with special authority, or send back to review.
     */
    public static class EscalatedState implements DisputeState {
        @Override
        public void addComment(DisputeContext context, String comment, String author) {
            context.addCommentToHistory(author + " [MANAGEMENT]", comment);
            System.out.println("✓ Management comment added");
        }

        @Override
        public void assignReviewer(DisputeContext context, String reviewerName) {
            // Can send back to review with a different reviewer
            context.setReviewer(reviewerName);
            System.out.println("✓ Reassigned to: " + reviewerName);
            System.out.println("→ Sending back to Under Review state");
            context.setState(new UnderReviewState());
        }

        @Override
        public void approve(DisputeContext context, BigDecimal adjustedAmount) {
            // Management can approve with override authority
            context.setAdjustedAmount(adjustedAmount);
            context.addCommentToHistory("Management",
                "Approved by management override with amount: $" + adjustedAmount);
            System.out.println("✓ MANAGEMENT OVERRIDE: Dispute approved with $" + adjustedAmount);
            System.out.println("→ Transitioning to Resolved state");
            context.setState(new ResolvedState());
        }

        @Override
        public void reject(DisputeContext context, String reason) {
            context.addCommentToHistory("Management", "Rejected by management: " + reason);
            System.out.println("✗ MANAGEMENT DECISION: Dispute rejected - " + reason);
            System.out.println("→ Transitioning to Resolved state");
            context.setState(new ResolvedState());
        }

        @Override
        public void escalate(DisputeContext context, String reason) {
            System.out.println("⚠ Already at highest escalation level");
        }

        @Override
        public void close(DisputeContext context) {
            System.out.println("✗ Cannot close - management must approve or reject first");
        }

        @Override
        public String getStateName() {
            return "ESCALATED";
        }

        @Override
        public String getAvailableActions() {
            return "Available: addComment, assignReviewer (sends back to review), approve, reject";
        }
    }

    /**
     * RESOLVED STATE
     *
     * The dispute has been decided (approved or rejected).
     * Limited actions available - primarily comments and closing.
     * Can escalate if the submitter disagrees with the resolution.
     */
    public static class ResolvedState implements DisputeState {
        @Override
        public void addComment(DisputeContext context, String comment, String author) {
            context.addCommentToHistory(author, comment);
            System.out.println("✓ Comment added to resolved dispute");
        }

        @Override
        public void assignReviewer(DisputeContext context, String reviewerName) {
            System.out.println("✗ Cannot reassign - dispute is already resolved");
        }

        @Override
        public void approve(DisputeContext context, BigDecimal adjustedAmount) {
            System.out.println("✗ Cannot approve - dispute is already resolved");
        }

        @Override
        public void reject(DisputeContext context, String reason) {
            System.out.println("✗ Cannot reject - dispute is already resolved");
        }

        @Override
        public void escalate(DisputeContext context, String reason) {
            // Allow re-escalation if submitter disagrees with resolution
            context.addCommentToHistory("System", "Re-escalated from resolved state: " + reason);
            System.out.println("⬆ Re-escalating resolved dispute: " + reason);
            context.setState(new EscalatedState());
        }

        @Override
        public void close(DisputeContext context) {
            context.addCommentToHistory("System", "Dispute closed");
            System.out.println("✓ Dispute closed successfully");
            System.out.println("→ Transitioning to Closed state (final)");
            context.setState(new ClosedState());
        }

        @Override
        public String getStateName() {
            return "RESOLVED";
        }

        @Override
        public String getAvailableActions() {
            return "Available: addComment, escalate (if disagreeing), close";
        }
    }

    /**
     * CLOSED STATE (Terminal State)
     *
     * Final state - no more changes allowed except viewing comments.
     * This is a terminal state in the state machine.
     */
    public static class ClosedState implements DisputeState {
        @Override
        public void addComment(DisputeContext context, String comment, String author) {
            System.out.println("✗ Cannot add comments - dispute is closed");
        }

        @Override
        public void assignReviewer(DisputeContext context, String reviewerName) {
            System.out.println("✗ Cannot assign reviewer - dispute is closed");
        }

        @Override
        public void approve(DisputeContext context, BigDecimal adjustedAmount) {
            System.out.println("✗ Cannot approve - dispute is closed");
        }

        @Override
        public void reject(DisputeContext context, String reason) {
            System.out.println("✗ Cannot reject - dispute is closed");
        }

        @Override
        public void escalate(DisputeContext context, String reason) {
            System.out.println("✗ Cannot escalate - dispute is closed (contact administrator)");
        }

        @Override
        public void close(DisputeContext context) {
            System.out.println("ℹ Dispute is already closed");
        }

        @Override
        public String getStateName() {
            return "CLOSED";
        }

        @Override
        public String getAvailableActions() {
            return "Available: [None - Terminal State]";
        }
    }

    /**
     * DISPUTE CONTEXT
     *
     * Represents a commission dispute with its associated data.
     * Maintains the current state and delegates all actions to the state object.
     *
     * This is the "Context" in the State pattern - it's the object whose behavior
     * changes based on its state.
     */
    public static class DisputeContext {
        // Dispute data
        private final String disputeId;
        private final String submittedBy;
        private final BigDecimal originalAmount;
        private BigDecimal adjustedAmount;
        private String reviewer;
        private final List<String> commentHistory;
        private final LocalDateTime submittedDate;

        // Current state - this is what makes the State pattern work
        private DisputeState currentState;

        /**
         * Create a new dispute in the SUBMITTED state.
         */
        public DisputeContext(String disputeId, String submittedBy, BigDecimal originalAmount) {
            this.disputeId = disputeId;
            this.submittedBy = submittedBy;
            this.originalAmount = originalAmount;
            this.adjustedAmount = originalAmount;
            this.commentHistory = new ArrayList<>();
            this.submittedDate = LocalDateTime.now();
            this.currentState = new SubmittedState(); // Initial state

            System.out.println("📋 New dispute created:");
            System.out.println("   ID: " + disputeId);
            System.out.println("   Submitted by: " + submittedBy);
            System.out.println("   Original amount: $" + originalAmount);
            System.out.println("   Initial state: " + currentState.getStateName());
        }

        // State transition method - called by state objects
        public void setState(DisputeState newState) {
            this.currentState = newState;
            commentHistory.add(formatTimestamp() + " [STATE CHANGE] → " + newState.getStateName());
        }

        // Delegate methods - these delegate to the current state
        public void addComment(String comment, String author) {
            currentState.addComment(this, comment, author);
        }

        public void assignReviewer(String reviewerName) {
            currentState.assignReviewer(this, reviewerName);
        }

        public void approve(BigDecimal adjustedAmount) {
            currentState.approve(this, adjustedAmount);
        }

        public void reject(String reason) {
            currentState.reject(this, reason);
        }

        public void escalate(String reason) {
            currentState.escalate(this, reason);
        }

        public void close() {
            currentState.close(this);
        }

        // Helper methods for states to use
        public void addCommentToHistory(String author, String comment) {
            commentHistory.add(formatTimestamp() + " [" + author + "] " + comment);
        }

        private String formatTimestamp() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Getters
        public String getStateName() {
            return currentState.getStateName();
        }

        public String getAvailableActions() {
            return currentState.getAvailableActions();
        }

        public String getDisputeId() {
            return disputeId;
        }

        public String getSubmittedBy() {
            return submittedBy;
        }

        public BigDecimal getOriginalAmount() {
            return originalAmount;
        }

        public BigDecimal getAdjustedAmount() {
            return adjustedAmount;
        }

        public String getReviewer() {
            return reviewer;
        }

        public List<String> getCommentHistory() {
            return new ArrayList<>(commentHistory);
        }

        // Setters (used by states)
        void setReviewer(String reviewer) {
            this.reviewer = reviewer;
        }

        void setAdjustedAmount(BigDecimal amount) {
            this.adjustedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        }

        /**
         * Display the current status of the dispute.
         */
        public void displayStatus() {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("DISPUTE STATUS");
            System.out.println("=".repeat(60));
            System.out.println("ID: " + disputeId);
            System.out.println("State: " + currentState.getStateName());
            System.out.println("Submitted by: " + submittedBy);
            System.out.println("Original amount: $" + originalAmount);
            System.out.println("Current amount: $" + adjustedAmount);
            if (reviewer != null) {
                System.out.println("Reviewer: " + reviewer);
            }
            System.out.println(currentState.getAvailableActions());
            System.out.println("=".repeat(60) + "\n");
        }

        /**
         * Display the complete comment history.
         */
        public void displayHistory() {
            System.out.println("\n📜 COMMENT HISTORY:");
            System.out.println("-".repeat(60));
            if (commentHistory.isEmpty()) {
                System.out.println("(No comments yet)");
            } else {
                commentHistory.forEach(System.out::println);
            }
            System.out.println("-".repeat(60) + "\n");
        }
    }
}