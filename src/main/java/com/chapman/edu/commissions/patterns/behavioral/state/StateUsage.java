package com.chapman.edu.commissions.patterns.behavioral.state;

import java.math.BigDecimal;

/**
 * STATE PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the State Pattern
 * in the context of commission dispute resolution.
 *
 * DEMONSTRATES:
 * 1. Happy path - standard approval workflow
 * 2. Rejection scenario - dispute denied
 * 3. Escalation workflow - complex case requiring management
 * 4. Re-escalation from resolved state
 * 5. Edge cases and error handling
 * 6. Testing state transitions
 *
 * KEY LEARNING POINTS:
 * - How state transitions enforce business rules
 * - How invalid operations are prevented by states
 * - How the same method call produces different results based on state
 * - How to test stateful behavior
 *
 * @author Commission Calculator Educational Project
 */
public class StateUsage {

    /**
     * EXAMPLE 1: Happy Path - Straightforward Approval
     *
     * Demonstrates the ideal scenario where a dispute is submitted, reviewed,
     * and approved without complications.
     */
    public static void exampleHappyPath() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 1: Happy Path - Quick Approval            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Sales rep submits a dispute
        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-100",
            "Mike Anderson",
            new BigDecimal("3500.00")
        );

        System.out.println("\n📝 Sales rep adds initial explanation...");
        dispute.addComment(
            "The deal was closed in Q4 but commission was calculated at Q3 rates",
            "Mike Anderson"
        );

        System.out.println("\n👤 Assigning to reviewer...");
        dispute.assignReviewer("Sarah Thompson");

        System.out.println("\n🔍 Reviewer investigates...");
        dispute.addComment("Confirmed - deal closed on Oct 1st, should use Q4 rates", "Sarah Thompson");
        dispute.addComment("Q4 rate is 12% vs Q3 rate of 10%", "Sarah Thompson");

        System.out.println("\n✅ Reviewer approves...");
        dispute.approve(new BigDecimal("4200.00")); // Corrected amount

        System.out.println("\n🔒 Closing dispute...");
        dispute.close();

        dispute.displayHistory();

        System.out.println("Result: Dispute resolved successfully in 4 steps!\n");
    }

    /**
     * EXAMPLE 2: Rejection Scenario
     *
     * Shows what happens when a dispute is investigated and found to be invalid.
     */
    public static void exampleRejection() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 2: Rejection - Invalid Claim              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-101",
            "Tom Baker",
            new BigDecimal("8000.00")
        );

        dispute.addComment(
            "My commission should include the renewal revenue",
            "Tom Baker"
        );

        dispute.assignReviewer("Linda Chen");

        dispute.addComment(
            "Reviewing the commission plan terms...",
            "Linda Chen"
        );

        dispute.addComment(
            "Commission plan clearly states renewals are handled by account management team",
            "Linda Chen"
        );

        System.out.println("\n❌ Reviewer rejects the dispute...");
        dispute.reject("Renewal revenue is not included per Section 4.2 of commission plan");

        System.out.println("\n🔒 Closing dispute...");
        dispute.close();

        dispute.displayHistory();

        System.out.println("Result: Dispute rejected based on commission plan rules.\n");
    }

    /**
     * EXAMPLE 3: Escalation to Management
     *
     * Demonstrates a complex case that requires management review.
     */
    public static void exampleEscalation() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 3: Escalation - High-Value Complex Case      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-102",
            "Jennifer Liu",
            new BigDecimal("25000.00")
        );

        dispute.addComment(
            "Multi-year deal with complex territory split - commission calculation unclear",
            "Jennifer Liu"
        );

        dispute.assignReviewer("David Park");

        dispute.addComment(
            "This involves territory overlap between two regions",
            "David Park"
        );

        dispute.addComment(
            "Deal value exceeds $1M with 3-year term - requires management decision on split",
            "David Park"
        );

        System.out.println("\n⬆️ Escalating to management...");
        dispute.escalate("High-value deal with territory ambiguity - needs executive decision");

        dispute.displayStatus();

        System.out.println("\n👔 Management reviews...");
        dispute.addComment(
            "Reviewed with sales ops and regional VPs",
            "Emma Wilson (VP Sales)"
        );

        dispute.addComment(
            "Decision: 60/40 split based on primary account ownership",
            "Emma Wilson (VP Sales)"
        );

        System.out.println("\n✅ Management approves with adjusted amount...");
        dispute.approve(new BigDecimal("30000.00")); // 60% of total commission

        dispute.close();

        dispute.displayHistory();

        System.out.println("Result: High-value dispute resolved by management.\n");
    }

    /**
     * EXAMPLE 4: Re-escalation from Resolved State
     *
     * Shows how a submitter can challenge a resolution if they disagree.
     */
    public static void exampleReEscalation() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║    EXAMPLE 4: Re-escalation - Challenging Resolution      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-103",
            "Robert Chen",
            new BigDecimal("6000.00")
        );

        dispute.addComment("Deal should qualify for accelerator bonus", "Robert Chen");
        dispute.assignReviewer("Maria Garcia");
        dispute.reject("Accelerator requires 150% of quota - rep is at 145%");

        dispute.displayStatus();

        System.out.println("\n⚠️ Submitter disagrees and re-escalates...");
        dispute.addComment(
            "I hit 148% if you include the pipeline deal that closed yesterday",
            "Robert Chen"
        );

        dispute.escalate("New information: additional deal closed that puts rep over 150% threshold");

        dispute.displayStatus();

        System.out.println("\n👔 Management reviews new information...");
        dispute.addComment(
            "Confirmed - deal #XYZ-789 closed yesterday and was not in original calculation",
            "Tom Wilson (Sales Director)"
        );

        dispute.approve(new BigDecimal("7500.00")); // Original + accelerator bonus

        dispute.close();

        dispute.displayHistory();

        System.out.println("Result: Re-escalation led to reversal of decision.\n");
    }

    /**
     * EXAMPLE 5: Edge Cases and Invalid Operations
     *
     * Demonstrates how the State pattern prevents invalid operations.
     */
    public static void exampleInvalidOperations() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 5: Invalid Operations - State Protection     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-104",
            "Alice Johnson",
            new BigDecimal("4000.00")
        );

        System.out.println("Attempting invalid operations at each state...\n");

        // In SUBMITTED state - try to approve without assigning reviewer
        System.out.println("❌ Try to approve while in SUBMITTED state:");
        dispute.approve(new BigDecimal("5000.00"));

        System.out.println("\n❌ Try to close while in SUBMITTED state:");
        dispute.close();

        // Move to UNDER_REVIEW
        System.out.println("\n✅ Moving to valid state...");
        dispute.assignReviewer("Bob Smith");

        // Approve and move to RESOLVED
        dispute.approve(new BigDecimal("4500.00"));

        // Try invalid operations on RESOLVED dispute
        System.out.println("\n❌ Try to approve again (already resolved):");
        dispute.approve(new BigDecimal("5000.00"));

        System.out.println("\n❌ Try to assign reviewer (already resolved):");
        dispute.assignReviewer("Charlie Brown");

        // Close the dispute
        dispute.close();

        // Try operations on CLOSED dispute
        System.out.println("\n❌ Try to add comment (closed):");
        dispute.addComment("This shouldn't work", "Alice Johnson");

        System.out.println("\n❌ Try to escalate (closed):");
        dispute.escalate("Too late!");

        System.out.println("\n❌ Try to close again (already closed):");
        dispute.close();

        System.out.println("\nResult: State pattern successfully prevented all invalid operations!\n");
    }

    /**
     * EXAMPLE 6: State Transition Testing Pattern
     *
     * Shows how to verify state transitions for testing purposes.
     */
    public static void exampleStateTransitionValidation() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     EXAMPLE 6: Testing State Transitions                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        StateImplementation.DisputeContext dispute = new StateImplementation.DisputeContext(
            "DISP-2024-105",
            "Test User",
            new BigDecimal("1000.00")
        );

        // Verify initial state
        assertState(dispute, "SUBMITTED", "Initial state should be SUBMITTED");

        // Transition to UNDER_REVIEW
        dispute.assignReviewer("Reviewer");
        assertState(dispute, "UNDER_REVIEW", "Should transition to UNDER_REVIEW after assignment");

        // Transition to ESCALATED
        dispute.escalate("Test escalation");
        assertState(dispute, "ESCALATED", "Should transition to ESCALATED");

        // Transition back to UNDER_REVIEW (reassignment)
        dispute.assignReviewer("New Reviewer");
        assertState(dispute, "UNDER_REVIEW", "Should return to UNDER_REVIEW on reassignment");

        // Transition to RESOLVED
        dispute.approve(new BigDecimal("1200.00"));
        assertState(dispute, "RESOLVED", "Should transition to RESOLVED after approval");

        // Transition to CLOSED
        dispute.close();
        assertState(dispute, "CLOSED", "Should transition to CLOSED");

        System.out.println("\n✅ All state transitions validated successfully!\n");

        // Show complete state transition history
        System.out.println("📊 State Transition History:");
        dispute.getCommentHistory().stream()
            .filter(comment -> comment.contains("[STATE CHANGE]"))
            .forEach(System.out::println);
    }

    /**
     * Helper method to assert and verify state transitions (useful for testing).
     */
    private static void assertState(StateImplementation.DisputeContext dispute,
                                   String expectedState,
                                   String message) {
        String actualState = dispute.getStateName();
        if (actualState.equals(expectedState)) {
            System.out.println("✓ PASS: " + message + " [" + actualState + "]");
        } else {
            System.out.println("✗ FAIL: " + message);
            System.out.println("  Expected: " + expectedState + ", Actual: " + actualState);
        }
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples to show different usage patterns.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║        STATE PATTERN - COMPREHENSIVE USAGE EXAMPLES       ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates real-world dispute resolution workflows     ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        // Run all examples
        exampleHappyPath();
        pause();

        exampleRejection();
        pause();

        exampleEscalation();
        pause();

        exampleReEscalation();
        pause();

        exampleInvalidOperations();
        pause();

        exampleStateTransitionValidation();

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. STATE ENCAPSULATION");
        System.out.println("   → Each state encapsulates specific behavior and rules");
        System.out.println("   → No complex if/else chains checking current state");
        System.out.println();
        System.out.println("2. EXPLICIT TRANSITIONS");
        System.out.println("   → State transitions are explicit and documented");
        System.out.println("   → Invalid transitions are prevented by design");
        System.out.println();
        System.out.println("3. OPEN/CLOSED PRINCIPLE");
        System.out.println("   → Easy to add new states without modifying existing code");
        System.out.println("   → Each state is independent and testable");
        System.out.println();
        System.out.println("4. SINGLE RESPONSIBILITY");
        System.out.println("   → Each state class has one reason to change");
        System.out.println("   → Context delegates all state-specific logic");
        System.out.println();
        System.out.println("5. TESTABILITY");
        System.out.println("   → State transitions can be verified programmatically");
        System.out.println("   → Each state can be unit tested independently");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Pause between examples for readability (in demo mode).
     */
    private static void pause() {
        System.out.println("\n[Press Enter to continue to next example...]");
        System.out.println("─".repeat(60) + "\n");
        // In a real interactive demo, you might wait for user input:
        // try { System.in.read(); } catch (Exception e) {}

        // For automated demos, just add spacing
        try {
            Thread.sleep(1000); // 1 second pause
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}