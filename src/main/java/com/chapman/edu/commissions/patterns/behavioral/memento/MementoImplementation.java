package com.chapman.edu.commissions.patterns.behavioral.memento;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * MEMENTO PATTERN - COMMISSION SYSTEM IMPLEMENTATION
 *
 * This implementation demonstrates the Memento Pattern through commission plan versioning
 * and deal state management, showing how to implement undo/redo, checkpoints, and
 * state restoration without violating encapsulation.
 *
 * REAL-WORLD SCENARIOS:
 * 1. Commission Plan Editing: Save draft versions, restore previous configurations
 * 2. Deal State Management: Track deal changes, rollback unsuccessful modifications
 * 3. Dispute Resolution: Capture states before/after adjustments
 * 4. Audit Trail: Complete history of all state changes
 * 5. Transaction Management: Commit/rollback for complex operations
 *
 * KEY BENEFITS:
 * - Undo/redo functionality for commission plans
 * - Versioning and audit trails
 * - Transactional editing (commit/rollback)
 * - State snapshots for testing and comparison
 * - Preserve encapsulation of internal state
 *
 * @author Commission Calculator Educational Project
 */
public class MementoImplementation {

    /**
     * MEMENTO: Commission Plan Memento
     *
     * Captures complete state of a commission plan including all rules,
     * tiers, and bonus rules. Immutable snapshot.
     *
     * DESIGN NOTES:
     * - Deep copy of all mutable objects to prevent external modification
     * - Metadata for tracking (timestamp, version label, who created)
     * - Serializable for persistence (if needed)
     */
    public static class CommissionPlanMemento {
        // State data (immutable)
        private final String planId;
        private final String planName;
        private final PlanStatus status;
        private final LocalDate effectiveDate;
        private final LocalDate expiryDate;
        private final List<CommissionRule> rules;  // Deep copy
        private final List<CommissionTier> tiers;  // Deep copy
        private final List<BonusRule> bonusRules;  // Deep copy

        // Metadata
        private final LocalDateTime timestamp;
        private final String label;
        private final String createdBy;

        /**
         * Private constructor - only CommissionPlanEditor can create.
         */
        private CommissionPlanMemento(CommissionPlan plan, String label, String createdBy) {
            // Copy state
            this.planId = plan.getId();
            this.planName = plan.getName();
            this.status = plan.getStatus();
            this.effectiveDate = plan.getEffectiveStartDate();
            this.expiryDate = plan.getEffectiveEndDate();

            // Deep copy collections to ensure immutability
            this.rules = new ArrayList<>(plan.getRules());
            this.tiers = new ArrayList<>(plan.getTiers());
            this.bonusRules = new ArrayList<>(plan.getBonuses());

            // Metadata
            this.timestamp = LocalDateTime.now();
            this.label = label;
            this.createdBy = createdBy;
        }

        // Getters (package-private - only originator should access)
        String getPlanId() { return planId; }
        String getPlanName() { return planName; }
        PlanStatus getStatus() { return status; }
        LocalDate getEffectiveDate() { return effectiveDate; }
        LocalDate getExpiryDate() { return expiryDate; }
        List<CommissionRule> getRules() { return new ArrayList<>(rules); }  // Defensive copy
        List<CommissionTier> getTiers() { return new ArrayList<>(tiers); }
        List<BonusRule> getBonusRules() { return new ArrayList<>(bonusRules); }

        public LocalDateTime getTimestamp() { return timestamp; }
        public String getLabel() { return label; }
        public String getCreatedBy() { return createdBy; }

        @Override
        public String toString() {
            return String.format("CommissionPlanMemento[%s, plan='%s', rules=%d, tiers=%d, by=%s, time=%s]",
                    label, planName, rules.size(), tiers.size(), createdBy, timestamp);
        }
    }

    /**
     * ORIGINATOR: Commission Plan Editor
     *
     * Manages commission plan state and creates/restores mementos.
     * Provides transactional editing with commit/rollback capabilities.
     *
     * RESPONSIBILITIES:
     * - Edit commission plan
     * - Create snapshots (mementos)
     * - Restore from snapshots
     * - Track dirty state (unsaved changes)
     */
    public static class CommissionPlanEditor {
        private CommissionPlan plan;
        private boolean isDirty;
        private final String editorUsername;

        public CommissionPlanEditor(CommissionPlan plan, String editorUsername) {
            this.plan = plan;
            this.editorUsername = editorUsername;
            this.isDirty = false;
        }

        /**
         * Create a memento containing current plan state.
         *
         * @param label Description of this save point
         * @return Memento with current state
         */
        public CommissionPlanMemento save(String label) {
            System.out.println("  → Saving snapshot: " + label);
            isDirty = false;
            return new CommissionPlanMemento(plan, label, editorUsername);
        }

        /**
         * Restore plan state from memento.
         *
         * @param memento The memento to restore from
         */
        public void restore(CommissionPlanMemento memento) {
            System.out.println("  ← Restoring from: " + memento.getLabel());

            // Restore all fields
            plan.setId(memento.getPlanId());
            plan.setName(memento.getPlanName());
            plan.setStatus(memento.getStatus());
            plan.setEffectiveStartDate(memento.getEffectiveDate());
            plan.setEffectiveEndDate(memento.getExpiryDate());

            // Restore collections
            plan.getRules().clear();
            plan.getRules().addAll(memento.getRules());

            plan.getTiers().clear();
            plan.getTiers().addAll(memento.getTiers());

            plan.getBonuses().clear();
            plan.getBonuses().addAll(memento.getBonusRules());

            isDirty = false;
        }

        // Editing operations (mark as dirty)

        public void setPlanName(String name) {
            System.out.println("    Editing: Set plan name to '" + name + "'");
            plan.setName(name);
            isDirty = true;
        }

        public void setStatus(PlanStatus status) {
            System.out.println("    Editing: Set status to " + status);
            plan.setStatus(status);
            isDirty = true;
        }

        public void setEffectiveDate(LocalDate date) {
            System.out.println("    Editing: Set effective date to " + date);
            plan.setEffectiveStartDate(date);
            isDirty = true;
        }

        public void addRule(CommissionRule rule) {
            System.out.println("    Editing: Add rule - " + rule.getName());
            plan.getRules().add(rule);
            isDirty = true;
        }

        public void removeRule(CommissionRule rule) {
            System.out.println("    Editing: Remove rule - " + rule.getName());
            plan.getRules().remove(rule);
            isDirty = true;
        }

        public void addTier(CommissionTier tier) {
            System.out.println("    Editing: Add tier - " + tier.getName());
            plan.getTiers().add(tier);
            isDirty = true;
        }

        public void addBonusRule(BonusRule bonus) {
            System.out.println("    Editing: Add bonus - " + bonus.getName());
            plan.getBonuses().add(bonus);
            isDirty = true;
        }

        // State queries

        public CommissionPlan getPlan() {
            return plan;
        }

        public boolean isDirty() {
            return isDirty;
        }

        public void display() {
            System.out.println("Current Plan: " + plan.getName() +
                             " [" + plan.getStatus() + "]" +
                             (isDirty ? " *MODIFIED*" : " (saved)"));
            System.out.println("  Rules: " + plan.getRules().size());
            System.out.println("  Tiers: " + plan.getTiers().size());
            System.out.println("  Bonus Rules: " + plan.getBonuses().size());
        }
    }

    /**
     * CARETAKER: Version History Manager
     *
     * Manages multiple versions of commission plans with undo/redo support.
     * Provides named checkpoints and version comparison.
     *
     * FEATURES:
     * - Undo/redo stack
     * - Named checkpoints
     * - Version comparison
     * - History browsing
     * - Auto-save capability
     */
    public static class VersionHistoryManager {
        private final CommissionPlanEditor editor;
        private final List<CommissionPlanMemento> history;
        private int currentIndex;  // For undo/redo
        private final int maxHistorySize;

        public VersionHistoryManager(CommissionPlanEditor editor) {
            this(editor, 50);  // Default 50 versions
        }

        public VersionHistoryManager(CommissionPlanEditor editor, int maxHistorySize) {
            this.editor = editor;
            this.history = new ArrayList<>();
            this.currentIndex = -1;
            this.maxHistorySize = maxHistorySize;
        }

        /**
         * Save current state as a checkpoint.
         *
         * @param label Description of this checkpoint
         */
        public void checkpoint(String label) {
            System.out.println("VersionHistory: Creating checkpoint '" + label + "'");

            // Clear any forward history if we're not at the end
            if (currentIndex < history.size() - 1) {
                history.subList(currentIndex + 1, history.size()).clear();
            }

            // Save memento
            CommissionPlanMemento memento = editor.save(label);
            history.add(memento);
            currentIndex++;

            // Enforce max history size
            if (history.size() > maxHistorySize) {
                history.remove(0);
                currentIndex--;
            }

            System.out.println("  (History size: " + history.size() + ", current: " + currentIndex + ")");
        }

        /**
         * Undo to previous version.
         */
        public boolean undo() {
            if (currentIndex <= 0) {
                System.out.println("VersionHistory: Nothing to undo");
                return false;
            }

            currentIndex--;
            CommissionPlanMemento memento = history.get(currentIndex);
            System.out.println("VersionHistory: Undo to '" + memento.getLabel() + "'");
            editor.restore(memento);
            return true;
        }

        /**
         * Redo to next version.
         */
        public boolean redo() {
            if (currentIndex >= history.size() - 1) {
                System.out.println("VersionHistory: Nothing to redo");
                return false;
            }

            currentIndex++;
            CommissionPlanMemento memento = history.get(currentIndex);
            System.out.println("VersionHistory: Redo to '" + memento.getLabel() + "'");
            editor.restore(memento);
            return true;
        }

        /**
         * Restore to specific version by index.
         */
        public boolean restoreVersion(int index) {
            if (index < 0 || index >= history.size()) {
                System.out.println("VersionHistory: Invalid version index: " + index);
                return false;
            }

            currentIndex = index;
            CommissionPlanMemento memento = history.get(index);
            System.out.println("VersionHistory: Restore to version " + index +
                             " ('" + memento.getLabel() + "')");
            editor.restore(memento);
            return true;
        }

        /**
         * Restore to specific checkpoint by label.
         */
        public boolean restoreCheckpoint(String label) {
            for (int i = 0; i < history.size(); i++) {
                if (history.get(i).getLabel().equals(label)) {
                    return restoreVersion(i);
                }
            }
            System.out.println("VersionHistory: Checkpoint not found: " + label);
            return false;
        }

        /**
         * Show complete version history.
         */
        public void showHistory() {
            System.out.println("\n=== Version History ===");
            if (history.isEmpty()) {
                System.out.println("  (no history)");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    CommissionPlanMemento m = history.get(i);
                    String marker = (i == currentIndex) ? " ← CURRENT" : "";
                    System.out.println(String.format("  %d. [%s] %s (by %s)%s",
                            i, m.getTimestamp(), m.getLabel(), m.getCreatedBy(), marker));
                }
            }
            System.out.println("=======================\n");
        }

        /**
         * Compare two versions.
         */
        public void compareVersions(int index1, int index2) {
            if (index1 < 0 || index1 >= history.size() ||
                index2 < 0 || index2 >= history.size()) {
                System.out.println("VersionHistory: Invalid version indices");
                return;
            }

            CommissionPlanMemento m1 = history.get(index1);
            CommissionPlanMemento m2 = history.get(index2);

            System.out.println("\n=== Version Comparison ===");
            System.out.println("Version " + index1 + ": " + m1.getLabel());
            System.out.println("Version " + index2 + ": " + m2.getLabel());
            System.out.println();
            System.out.println("Plan Name: '" + m1.getPlanName() + "' → '" + m2.getPlanName() + "'");
            System.out.println("Status: " + m1.getStatus() + " → " + m2.getStatus());
            System.out.println("Rules: " + m1.getRules().size() + " → " + m2.getRules().size());
            System.out.println("Tiers: " + m1.getTiers().size() + " → " + m2.getTiers().size());
            System.out.println("Bonuses: " + m1.getBonusRules().size() + " → " + m2.getBonusRules().size());
            System.out.println("==========================\n");
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public int getHistorySize() {
            return history.size();
        }

        public boolean canUndo() {
            return currentIndex > 0;
        }

        public boolean canRedo() {
            return currentIndex < history.size() - 1;
        }
    }

    /**
     * BONUS: Deal Transaction Manager
     *
     * Demonstrates transactional editing with commit/rollback for deals.
     * Useful for complex deal modifications that may need to be undone.
     */
    public static class DealTransactionManager {
        private Deal deal;
        private Deal snapshot;  // Simple memento using cloning

        public DealTransactionManager(Deal deal) {
            this.deal = deal;
        }

        /**
         * Begin transaction - save current state.
         */
        public void beginTransaction() {
            System.out.println("Transaction: BEGIN (saving snapshot)");
            snapshot = cloneDeal(deal);
        }

        /**
         * Commit transaction - keep changes, discard snapshot.
         */
        public void commit() {
            System.out.println("Transaction: COMMIT (keeping changes)");
            snapshot = null;
        }

        /**
         * Rollback transaction - restore from snapshot.
         */
        public void rollback() {
            if (snapshot == null) {
                System.out.println("Transaction: No active transaction to rollback");
                return;
            }

            System.out.println("Transaction: ROLLBACK (restoring snapshot)");
            restoreDeal(deal, snapshot);
            snapshot = null;
        }

        // Editing operations

        public void setTitle(String title) {
            System.out.println("  Transaction: Set title to '" + title + "'");
            deal.setTitle(title);
        }

        public void setValue(BigDecimal value) {
            System.out.println("  Transaction: Set value to " + value);
            deal.setValue(value);
        }

        public void setStatus(DealStatus status) {
            System.out.println("  Transaction: Set status to " + status);
            deal.setStatus(status);
        }

        public void display() {
            System.out.println("Current Deal: " + deal.getTitle() +
                             " [$" + deal.getValue() + "] [" + deal.getStatus() + "]");
        }

        // Helper methods for cloning (simplified memento)

        private Deal cloneDeal(Deal original) {
            Deal clone = new Deal(original.getTitle(), original.getValue(),
                                 original.getSalesRepId());
            clone.setId(original.getId());
            clone.setStatus(original.getStatus());
            clone.setCloseDate(original.getCloseDate());
            return clone;
        }

        private void restoreDeal(Deal target, Deal source) {
            target.setId(source.getId());
            target.setTitle(source.getTitle());
            target.setValue(source.getValue());
            target.setStatus(source.getStatus());
            target.setSalesRepId(source.getSalesRepId());
            target.setCloseDate(source.getCloseDate());
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows the Memento pattern in action with commission plans.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║   MEMENTO PATTERN - COMMISSION SYSTEM IMPLEMENTATION      ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates undo/redo and versioning for plans         ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        demonstrateCommissionPlanVersioning();
        demonstrateDealTransactions();

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    PATTERN SUMMARY                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("MEMENTO PATTERN COMPONENTS:");
        System.out.println("  • Memento: CommissionPlanMemento (immutable snapshot)");
        System.out.println("  • Originator: CommissionPlanEditor (creates/restores)");
        System.out.println("  • Caretaker: VersionHistoryManager (manages history)");
        System.out.println();
        System.out.println("KEY BENEFITS:");
        System.out.println("  ✓ Undo/redo functionality");
        System.out.println("  ✓ Version history and checkpoints");
        System.out.println("  ✓ Transactional editing (commit/rollback)");
        System.out.println("  ✓ Encapsulation preserved");
        System.out.println("  ✓ Complete audit trail");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    private static void demonstrateCommissionPlanVersioning() {
        System.out.println("EXAMPLE 1: Commission Plan Versioning with Undo/Redo\n");
        System.out.println("=".repeat(60));

        // Create initial plan
        CommissionPlan plan = new CommissionPlan();
        plan.setId("PLAN-2024-Q1");
        plan.setName("Q1 2024 Sales Plan");
        plan.setStatus(PlanStatus.DRAFT);
        plan.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
        plan.setEffectiveEndDate(LocalDate.of(2024, 3, 31));

        // Create editor and version manager
        CommissionPlanEditor editor = new CommissionPlanEditor(plan, "john.doe");
        VersionHistoryManager versionManager = new VersionHistoryManager(editor);

        // Checkpoint 0: Initial version
        versionManager.checkpoint("Initial Draft");
        editor.display();

        // Edit 1: Add rules
        System.out.println("\n--- Edit Session 1: Add Rules ---");
        CommissionRule rule1 = new CommissionRule();
        rule1.setName("Software Sales Rule");
        editor.addRule(rule1);

        CommissionTier tier1 = new CommissionTier();
        tier1.setName("Tier 1");
        editor.addTier(tier1);

        versionManager.checkpoint("Added rules and tiers");
        editor.display();

        // Edit 2: Change plan name and status
        System.out.println("\n--- Edit Session 2: Update Plan ---");
        editor.setPlanName("Q1 2024 Enterprise Sales Plan");
        editor.setStatus(PlanStatus.ACTIVE);

        versionManager.checkpoint("Activated plan with new name");
        editor.display();

        // Edit 3: Add bonus rules
        System.out.println("\n--- Edit Session 3: Add Bonuses ---");
        BonusRule bonus1 = new BonusRule();
        bonus1.setName("Q1 Accelerator");
        editor.addBonusRule(bonus1);

        versionManager.checkpoint("Added Q1 bonuses");
        editor.display();

        // Show complete history
        versionManager.showHistory();

        // Undo operations
        System.out.println("--- Performing Undo ---");
        versionManager.undo();
        editor.display();

        System.out.println("\n--- Performing Another Undo ---");
        versionManager.undo();
        editor.display();

        // Redo operation
        System.out.println("\n--- Performing Redo ---");
        versionManager.redo();
        editor.display();

        // Restore to specific checkpoint
        System.out.println("\n--- Restore to 'Initial Draft' ---");
        versionManager.restoreCheckpoint("Initial Draft");
        editor.display();

        // Compare versions
        versionManager.compareVersions(0, 3);

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private static void demonstrateDealTransactions() {
        System.out.println("EXAMPLE 2: Deal Transaction Management (Commit/Rollback)\n");
        System.out.println("=".repeat(60));

        // Create deal
        Deal deal = new Deal("Enterprise Software License",
                           new BigDecimal("50000"), "REP-123");
        deal.setStatus(DealStatus.OPEN);

        DealTransactionManager txManager = new DealTransactionManager(deal);

        // Show initial state
        System.out.println("Initial state:");
        txManager.display();

        // Transaction 1: Successful changes (commit)
        System.out.println("\n--- Transaction 1: Update Deal (will commit) ---");
        txManager.beginTransaction();
        txManager.setTitle("Enterprise Software Suite");
        txManager.setValue(new BigDecimal("75000"));
        txManager.display();
        txManager.commit();
        System.out.println("Changes committed!");

        // Transaction 2: Failed changes (rollback)
        System.out.println("\n--- Transaction 2: Risky Changes (will rollback) ---");
        txManager.beginTransaction();
        txManager.setTitle("WRONG TITLE");
        txManager.setValue(new BigDecimal("99999999"));
        txManager.setStatus(DealStatus.WON);
        txManager.display();
        System.out.println("Oops! These changes are wrong...");
        txManager.rollback();
        System.out.println("Changes rolled back!");
        txManager.display();

        System.out.println("\n" + "=".repeat(60) + "\n");
    }
}