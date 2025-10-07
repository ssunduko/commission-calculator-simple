package com.chapman.edu.commissions.patterns.behavioral.state;

import java.math.BigDecimal;
/**
 * STATE PATTERN - STRUCTURAL DEMONSTRATION
 * PURPOSE:
 * The State Pattern allows an object to alter its behavior when its internal state changes.
 * The object will appear to change its class. This pattern encapsulates state-specific behavior
 * into separate state classes and delegates behavior to the current state object.
 *
 * PROBLEM IT SOLVES:
 * - Eliminates large conditional statements (if/else, switch) based on object state
 * - Makes state transitions explicit and easier to manage
 * - Encapsulates state-specific behavior, making it easier to add new states
 * - Makes state transition logic more maintainable and testable
 *
 * WHEN TO USE:
 * - An object's behavior depends on its state, and it must change behavior at runtime
 * - Operations have large, multipart conditional statements based on object state
 * - State transitions are complex and need to be made explicit
 * - You want to avoid duplicate code across similar states
 *
 * COMPONENTS:
 * 1. DisputeState (Interface): Defines the interface for encapsulating state-specific behavior
 * 2. Concrete States (in StateImplementation): SubmittedState, UnderReviewState, EscalatedState, ResolvedState, ClosedState
 * 3. DisputeContext (in StateImplementation): Maintains current state and delegates actions to state object
 *
 * STATE TRANSITIONS:
 * SUBMITTED → UNDER_REVIEW (when reviewer assigned)
 * SUBMITTED → ESCALATED (when escalated)
 * UNDER_REVIEW → RESOLVED (when approved/rejected)
 * UNDER_REVIEW → ESCALATED (when escalated)
 * ESCALATED → UNDER_REVIEW (when reassigned to reviewer)
 * ESCALATED → RESOLVED (when approved/rejected by management)
 * RESOLVED → ESCALATED (when re-escalated)
 * RESOLVED → CLOSED (when closed)
 */
public class StateStructure {

    /**
     * DISPUTE STATE INTERFACE
     *
     * Defines all possible actions that can be taken on a dispute.
     * Not all actions are valid in all states - each state decides what's allowed.
     */
    public interface DisputeState {
        /**
         * Add a comment to the dispute.
         * Some states may restrict who can comment.
         */
        void addComment(StateImplementation.DisputeContext context, String comment, String author);

        /**
         * Assign the dispute to a reviewer.
         * Only valid in certain states.
         */
        void assignReviewer(StateImplementation.DisputeContext context, String reviewerName);

        /**
         * Approve the dispute and adjust the commission.
         * Only valid when the dispute is being reviewed.
         */
        void approve(StateImplementation.DisputeContext context, BigDecimal adjustedAmount);

        /**
         * Reject the dispute with a reason.
         * Only valid when the dispute is being reviewed.
         */
        void reject(StateImplementation.DisputeContext context, String reason);

        /**
         * Escalate the dispute to higher management.
         * May be done from multiple states.
         */
        void escalate(StateImplementation.DisputeContext context, String reason);

        /**
         * Close the dispute.
         * Only valid in terminal states.
         */
        void close(StateImplementation.DisputeContext context);

        /**
         * Get the name of the current state.
         */
        String getStateName();

        /**
         * Get help text showing what actions are available in this state.
         */
        String getAvailableActions();
    }
}