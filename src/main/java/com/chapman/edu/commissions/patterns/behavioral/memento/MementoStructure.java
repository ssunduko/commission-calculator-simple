package com.chapman.edu.commissions.patterns.behavioral.memento;

import com.chapman.edu.commissions.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MEMENTO PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Memento Pattern captures and externalizes an object's internal state without
 * violating encapsulation, so the object can be restored to this state later.
 *
 * PROBLEM IT SOLVES:
 * - Need to save and restore object state (undo/redo functionality)
 * - Must preserve encapsulation (not expose internal implementation)
 * - Need snapshots of state at different points in time
 * - Implement checkpoints or rollback mechanisms
 * - Create audit trails with state history
 *
 * WHEN TO USE:
 * - Need to implement undo/redo operations
 * - Need to save snapshots of object state
 * - Direct interface to get state would expose implementation details
 * - Want to preserve encapsulation boundaries
 * - Need to restore object to previous state
 * - Implement transactional behavior (commit/rollback)
 *
 * COMPONENTS:
 * 1. Originator: Creates memento containing snapshot of its current state
 * 2. Memento: Stores internal state of Originator, protects against access by others
 * 3. Caretaker: Responsible for memento's safekeeping, never operates on or examines contents
 *
 * KEY CONCEPT:
 * The Memento pattern allows you to save and restore the state of an object without
 * revealing its implementation details. Only the originator can access the memento's state.
 *
 */
public class MementoStructure {

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
        CommissionPlanMemento(CommissionPlan plan, String label, String createdBy) {
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
}