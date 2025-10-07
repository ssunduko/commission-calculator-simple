package com.chapman.edu.commissions.patterns.behavioral.memento;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.memento.MementoImplementation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MEMENTO PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Memento Pattern in commission plan management and deal editing.
 *
 * DEMONSTRATES:
 * 1. Basic undo/redo functionality
 * 2. Named checkpoints and version management
 * 3. Draft editing with discard changes
 * 4. Version comparison and diff analysis
 * 5. Transactional editing (begin/commit/rollback)
 * 6. Auto-save and crash recovery simulation
 *
 * KEY LEARNING POINTS:
 * - Memento preserves encapsulation while capturing state
 * - Caretaker manages mementos without examining contents
 * - Enables powerful undo/redo and versioning features
 * - Perfect for complex editing workflows
 * - Supports transactional operations
 *
 */
public class MementoUsage {

    /**
     * EXAMPLE 1: Basic Undo/Redo Operations
     *
     * Demonstrates simple undo/redo functionality for commission plan editing.
     */
    public static void exampleBasicUndoRedo() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        EXAMPLE 1: Basic Undo/Redo Operations              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: User makes several edits, then undoes and redoes\\n");

        // Create initial plan
        CommissionPlan plan = createBasicPlan("Sales Plan 2024");
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "alice");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Save initial state
        versionManager.checkpoint("Initial state");
        System.out.println("✓ Initial state saved\n");
        editor.display();

        // Edit 1
        System.out.println("\n--- Edit 1: Change plan name ---");
        editor.setPlanName("Updated Sales Plan 2024");
        versionManager.checkpoint("Renamed plan");
        editor.display();

        // Edit 2
        System.out.println("\n--- Edit 2: Activate plan ---");
        editor.setStatus(PlanStatus.ACTIVE);
        versionManager.checkpoint("Activated");
        editor.display();

        // Edit 3
        System.out.println("\n--- Edit 3: Add rule ---");
        CommissionRule rule = new CommissionRule();
        rule.setName("Standard Rule");
        editor.addRule(rule);
        versionManager.checkpoint("Added rule");
        editor.display();

        // Now undo twice
        System.out.println("\n--- Undo #1 ---");
        versionManager.undo();
        editor.display();

        System.out.println("\n--- Undo #2 ---");
        versionManager.undo();
        editor.display();

        // Redo once
        System.out.println("\n--- Redo #1 ---");
        versionManager.redo();
        editor.display();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Undo/redo maintains complete state history.");
        System.out.println("User can navigate backward and forward through changes.\\n");
    }

    /**
     * EXAMPLE 2: Named Checkpoints
     * Shows using named checkpoints for major milestones.
     */
    public static void exampleNamedCheckpoints() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 2: Named Checkpoints                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: User creates checkpoints at important stages\\n");
        CommissionPlan plan = createBasicPlan("Q1 Plan");
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "bob");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Checkpoint 1: Draft
        versionManager.checkpoint("DRAFT_V1 - Initial draft");
        System.out.println("✓ Checkpoint: DRAFT_V1\n");

        // Add some rules
        editor.addRule(createRule("Software Sales", new BigDecimal("0.10")));
        editor.addRule(createRule("Hardware Sales", new BigDecimal("0.08")));

        // Checkpoint 2: Rules complete
        versionManager.checkpoint("RULES_COMPLETE - All rules added");
        System.out.println("✓ Checkpoint: RULES_COMPLETE\n");

        // Add tiers
        editor.addTier(createTier(1, new BigDecimal("0"), new BigDecimal("25000")));
        editor.addTier(createTier(2, new BigDecimal("25000"), new BigDecimal("100000")));

        // Checkpoint 3: Ready for review
        versionManager.checkpoint("REVIEW_READY - Ready for manager review");
        System.out.println("✓ Checkpoint: REVIEW_READY\n");

        // Activate
        editor.setStatus(PlanStatus.ACTIVE);

        // Checkpoint 4: Activated
        versionManager.checkpoint("ACTIVATED - Plan now active");
        System.out.println("✓ Checkpoint: ACTIVATED\n");

        // Show all checkpoints
        versionManager.showHistory();

        // Restore to specific checkpoint
        System.out.println("--- Restoring to RULES_COMPLETE ---");
        versionManager.restoreCheckpoint("RULES_COMPLETE - All rules added");
        editor.display();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Named checkpoints provide meaningful milestones.");
        System.out.println("Easy to restore to specific stages of editing process.\\n");
    }

    /**
     * EXAMPLE 3: Draft Editing with Discard
     *
     * Demonstrates editing a draft with the option to discard all changes.
     */
    public static void exampleDraftEditingWithDiscard() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     EXAMPLE 3: Draft Editing with Discard Changes        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: User edits draft, then discards all changes\\n");

        CommissionPlan plan = createBasicPlan("Draft Plan");
        plan.setStatus(PlanStatus.ACTIVE);
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "carol");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Save published version
        versionManager.checkpoint("PUBLISHED - Live version");
        System.out.println("Published version:");
        editor.display();
        System.out.println("  (This is the production version in use)\n");

        // Start editing
        System.out.println("--- User starts editing ---");
        editor.setPlanName("EXPERIMENTAL Draft Plan");
        editor.setStatus(PlanStatus.DRAFT);
        System.out.println("Making experimental changes:");
        editor.display();

        System.out.println("\n--- More experimental changes ---");
        editor.addRule(createRule("Test Rule 1", new BigDecimal("0.50")));
        editor.addRule(createRule("Test Rule 2", new BigDecimal("0.99")));
        editor.addBonusRule(createBonus("Crazy Bonus"));
        System.out.println("Current draft state:");
        editor.display();

        // User decides to discard
        System.out.println("\n--- User decides these changes are no good ---");
        System.out.println("Discarding all changes and restoring published version...");
        versionManager.restoreCheckpoint("PUBLISHED - Live version");

        System.out.println("\nRestored to published version:");
        editor.display();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Can make risky edits without fear of losing good version.");
        System.out.println("Easy to discard experimental changes and restore stable state.\\n");
    }

    /**
     * EXAMPLE 4: Version Comparison
     *
     * Shows comparing different versions to see what changed.
     */
    public static void exampleVersionComparison() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        EXAMPLE 4: Version Comparison                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: User compares different versions to see changes\\n");

        CommissionPlan plan = createBasicPlan("Evolving Plan");
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "dave");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Version 0: Initial
        versionManager.checkpoint("V0 - Initial");

        // Version 1: Add 2 rules
        editor.addRule(createRule("Rule A", new BigDecimal("0.10")));
        editor.addRule(createRule("Rule B", new BigDecimal("0.15")));
        versionManager.checkpoint("V1 - Added 2 rules");

        // Version 2: Add 3 tiers
        editor.addTier(createTier(1, BigDecimal.ZERO, new BigDecimal("10000")));
        editor.addTier(createTier(2, new BigDecimal("10000"), new BigDecimal("50000")));
        editor.addTier(createTier(3, new BigDecimal("50000"), new BigDecimal("100000")));
        versionManager.checkpoint("V2 - Added 3 tiers");

        // Version 3: Activate and rename
        editor.setPlanName("Production Plan");
        editor.setStatus(PlanStatus.ACTIVE);
        versionManager.checkpoint("V3 - Activated");

        versionManager.showHistory();

        // Compare versions
        System.out.println("Comparing different version pairs:\n");
        versionManager.compareVersions(0, 1);
        versionManager.compareVersions(1, 2);
        versionManager.compareVersions(0, 3);

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Version comparison shows evolution of the plan.");
        System.out.println("Easy to audit what changed between any two points.\\n");
    }

    /**
     * EXAMPLE 5: Transactional Editing (All-or-Nothing)
     *
     * Demonstrates begin/commit/rollback pattern for complex edits.
     */
    public static void exampleTransactionalEditing() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 5: Transactional Editing                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Complex edits with commit/rollback\\n");

        Deal deal = new Deal("Software License", new BigDecimal("50000"), "REP-123");
        deal.setStatus(DealStatus.OPEN);

        DealTransactionManager txManager = new DealTransactionManager(deal);

        System.out.println("Initial deal state:");
        txManager.display();

        // Transaction 1: Success scenario
        System.out.println("\n=== Transaction 1: Valid Changes (will commit) ===");
        txManager.beginTransaction();
        System.out.println("Making changes within transaction...");
        txManager.setTitle("Enterprise Software License");
        txManager.setValue(new BigDecimal("75000"));
        txManager.display();

        System.out.println("\nValidation passed! Committing...");
        txManager.commit();
        System.out.println("✓ Transaction committed\n");
        txManager.display();

        // Transaction 2: Failure scenario
        System.out.println("\n=== Transaction 2: Invalid Changes (will rollback) ===");
        txManager.beginTransaction();
        System.out.println("Making changes within transaction...");
        txManager.setTitle("ERROR: This is wrong!");
        txManager.setValue(new BigDecimal("999999999"));
        txManager.setStatus(DealStatus.CANCELLED);
        txManager.display();

        System.out.println("\n❌ Validation failed! Rolling back...");
        txManager.rollback();
        System.out.println("✓ Transaction rolled back\n");
        txManager.display();

        // Transaction 3: Partial changes with rollback
        System.out.println("\n=== Transaction 3: Partial Changes (will rollback) ===");
        txManager.beginTransaction();
        txManager.setTitle("Updated Title");
        System.out.println("After first change:");
        txManager.display();

        txManager.setValue(new BigDecimal("100000"));
        System.out.println("\nAfter second change:");
        txManager.display();

        System.out.println("\nDeciding to abort entire transaction...");
        txManager.rollback();
        System.out.println("✓ All changes rolled back\n");
        txManager.display();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Transactions ensure all-or-nothing semantics.");
        System.out.println("Complex multi-step edits can be safely rolled back.\\n");
    }

    /**
     * EXAMPLE 6: Auto-Save and Crash Recovery
     *
     * Simulates auto-save functionality for crash recovery.
     */
    public static void exampleAutoSaveAndRecovery() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     EXAMPLE 6: Auto-Save and Crash Recovery              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: System auto-saves periodically, recovers after crash\\n");

        CommissionPlan plan = createBasicPlan("Important Plan");
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "eve");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Initial save
        versionManager.checkpoint("AUTOSAVE_0 - Initial");
        System.out.println("✓ Auto-save #0\n");

        // Simulate editing with periodic auto-saves
        System.out.println("--- User is editing... ---");
        editor.setPlanName("Important Plan v2");
        editor.addRule(createRule("Rule 1", new BigDecimal("0.10")));

        versionManager.checkpoint("AUTOSAVE_1 - Auto-saved");
        System.out.println("✓ Auto-save #1\n");

        editor.addRule(createRule("Rule 2", new BigDecimal("0.15")));
        editor.addTier(createTier(1, BigDecimal.ZERO, new BigDecimal("25000")));

        versionManager.checkpoint("AUTOSAVE_2 - Auto-saved");
        System.out.println("✓ Auto-save #2\n");

        editor.addBonusRule(createBonus("Q1 Bonus"));
        editor.setStatus(PlanStatus.ACTIVE);

        versionManager.checkpoint("AUTOSAVE_3 - Auto-saved");
        System.out.println("✓ Auto-save #3\n");

        System.out.println("Current state before crash:");
        editor.display();

        // Simulate crash
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💥 APPLICATION CRASHED!");
        System.out.println("=".repeat(60));

        // Simulate recovery
        System.out.println("\n--- Application restarted ---");
        System.out.println("Looking for auto-save...\n");

        versionManager.showHistory();

        // Recover to last auto-save
        System.out.println("Recovering to last auto-save (AUTOSAVE_3)...");
        versionManager.restoreCheckpoint("AUTOSAVE_3 - Auto-saved");

        System.out.println("\n✓ Recovered successfully!");
        editor.display();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Auto-save with mementos enables crash recovery.");
        System.out.println("User doesn't lose work even if application crashes.\\n");
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║       MEMENTO PATTERN - COMPREHENSIVE EXAMPLES            ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates undo/redo and versioning patterns          ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        exampleBasicUndoRedo();
        pause();

        exampleNamedCheckpoints();
        pause();

        exampleDraftEditingWithDiscard();
        pause();

        exampleVersionComparison();
        pause();

        exampleTransactionalEditing();
        pause();

        exampleAutoSaveAndRecovery();

        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. ENCAPSULATION PRESERVED");
        System.out.println("   → Memento stores state without exposing internals");
        System.out.println("   → Caretaker doesn't examine memento contents");
        System.out.println();
        System.out.println("2. UNDO/REDO CAPABILITY");
        System.out.println("   → Navigate backward/forward through changes");
        System.out.println("   → Restore to any previous state");
        System.out.println();
        System.out.println("3. VERSIONING & CHECKPOINTS");
        System.out.println("   → Named checkpoints for important milestones");
        System.out.println("   → Compare versions to see differences");
        System.out.println();
        System.out.println("4. TRANSACTIONAL EDITING");
        System.out.println("   → Begin/commit/rollback pattern");
        System.out.println("   → All-or-nothing semantics for complex edits");
        System.out.println();
        System.out.println("5. DISASTER RECOVERY");
        System.out.println("   → Auto-save with mementos");
        System.out.println("   → Recover from crashes without data loss");
        System.out.println();
        System.out.println("6. SAFE EXPERIMENTATION");
        System.out.println("   → Make risky changes without fear");
        System.out.println("   → Easy to discard and restore stable state");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    // Helper methods

    private static CommissionPlan createBasicPlan(String name) {
        CommissionPlan plan = new CommissionPlan();
        plan.setId("PLAN-" + System.currentTimeMillis());
        plan.setName(name);
        plan.setStatus(PlanStatus.DRAFT);
        plan.setEffectiveStartDate(LocalDate.now());
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(3));
        return plan;
    }

    private static CommissionRule createRule(String name, BigDecimal rate) {
        CommissionRule rule = new CommissionRule();
        rule.setName(name);
        rule.setRate(rate);
        return rule;
    }

    private static CommissionTier createTier(int level, BigDecimal min, BigDecimal max) {
        CommissionTier tier = new CommissionTier();
        tier.setName("Tier " + level);
        tier.setLowerBound(min);
        tier.setUpperBound(max);
        return tier;
    }

    private static BonusRule createBonus(String name) {
        BonusRule bonus = new BonusRule();
        bonus.setName(name);
        bonus.setAmount(new BigDecimal("5000"));
        return bonus;
    }

    private static void pause() {
        System.out.println("\n[Press Enter to continue...]");
        System.out.println("─".repeat(60) + "\n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}