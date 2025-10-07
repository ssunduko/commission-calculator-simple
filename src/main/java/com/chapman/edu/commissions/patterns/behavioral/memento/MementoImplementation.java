package com.chapman.edu.commissions.patterns.behavioral.memento;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.memento.MementoStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
 */
public class MementoImplementation {

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
}