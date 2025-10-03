package com.chapman.edu.commissions.patterns.behavioral.memento;

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
 * @author Commission Calculator Educational Project
 */
public class MementoStructure {

    /**
     * MEMENTO CLASS
     *
     * Stores the internal state of the Originator object.
     * Protects against access by objects other than the originator.
     *
     * KEY RESPONSIBILITIES:
     * - Store state snapshot
     * - Provide access only to originator
     * - Immutable (state cannot be changed after creation)
     *
     * DESIGN NOTES:
     * - Typically made immutable (final fields, no setters)
     * - May use private/package-private constructor
     * - Only originator should be able to read/write state
     */
    public static class Memento {
        private final String state;
        private final LocalDateTime timestamp;
        private final String label;

        /**
         * Constructor typically called only by Originator.
         *
         * @param state The state to save
         * @param label Human-readable label for this memento
         */
        public Memento(String state, String label) {
            this.state = state;
            this.label = label;
            this.timestamp = LocalDateTime.now();
        }

        /**
         * Get the saved state.
         * In production, this might be package-private or accessed via originator.
         *
         * @return The saved state
         */
        public String getState() {
            return state;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return String.format("Memento[%s, state='%s', time=%s]",
                    label, state, timestamp);
        }
    }

    /**
     * ORIGINATOR CLASS
     *
     * Creates a memento containing a snapshot of its current state.
     * Uses the memento to restore its internal state.
     *
     * KEY RESPONSIBILITIES:
     * - Create mementos of its state
     * - Restore state from mementos
     * - Manage its internal state
     *
     * IMPORTANT:
     * Only the originator has access to the memento's internal state.
     * The caretaker only stores and retrieves mementos without examining them.
     */
    public static class Originator {
        private String state;

        public Originator(String initialState) {
            this.state = initialState;
            System.out.println("Originator initialized with state: " + state);
        }

        /**
         * Modify the state of the originator.
         * This represents business operations that change state.
         *
         * @param newState The new state
         */
        public void setState(String newState) {
            System.out.println("Setting state: " + state + " → " + newState);
            this.state = newState;
        }

        public String getState() {
            return state;
        }

        /**
         * Create a memento containing current state.
         * This is called when you want to save a checkpoint.
         *
         * @param label Human-readable label for this save point
         * @return Memento containing current state
         */
        public Memento save(String label) {
            System.out.println("  → Creating memento: " + label + " (state: " + state + ")");
            return new Memento(state, label);
        }

        /**
         * Restore state from a memento.
         * This is called when you want to undo/rollback.
         *
         * @param memento The memento to restore from
         */
        public void restore(Memento memento) {
            System.out.println("  ← Restoring from memento: " + memento.getLabel() +
                             " (state: " + state + " → " + memento.getState() + ")");
            this.state = memento.getState();
        }

        /**
         * Display current state.
         */
        public void display() {
            System.out.println("Current state: " + state);
        }
    }

    /**
     * CARETAKER CLASS
     *
     * Responsible for the memento's safekeeping.
     * Never operates on or examines the contents of a memento.
     *
     * KEY RESPONSIBILITIES:
     * - Store mementos
     * - Provide mementos back to originator
     * - Manage memento history
     *
     * IMPORTANT:
     * The caretaker doesn't know what's inside the memento.
     * It treats mementos as opaque objects.
     */
    public static class Caretaker {
        private final List<Memento> history;
        private final Originator originator;

        public Caretaker(Originator originator) {
            this.originator = originator;
            this.history = new ArrayList<>();
        }

        /**
         * Save current state.
         *
         * @param label Label for this save point
         */
        public void backup(String label) {
            System.out.println("Caretaker: Backing up state (" + label + ")");
            Memento memento = originator.save(label);
            history.add(memento);
        }

        /**
         * Undo to previous state.
         */
        public void undo() {
            if (history.isEmpty()) {
                System.out.println("Caretaker: Nothing to undo!");
                return;
            }

            // Remove current state
            Memento last = history.remove(history.size() - 1);
            System.out.println("Caretaker: Undoing to previous state");

            // If there's a previous state, restore it
            if (!history.isEmpty()) {
                Memento previous = history.get(history.size() - 1);
                originator.restore(previous);
            } else {
                System.out.println("Caretaker: No more states to undo to");
            }
        }

        /**
         * Restore to a specific point in history.
         *
         * @param index The history index to restore
         */
        public void restoreToPoint(int index) {
            if (index < 0 || index >= history.size()) {
                System.out.println("Caretaker: Invalid history index: " + index);
                return;
            }

            System.out.println("Caretaker: Restoring to point " + index);
            Memento memento = history.get(index);
            originator.restore(memento);
        }

        /**
         * Show complete history.
         */
        public void showHistory() {
            System.out.println("\n=== State History ===");
            if (history.isEmpty()) {
                System.out.println("  (no history)");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    Memento m = history.get(i);
                    System.out.println("  " + i + ". " + m);
                }
            }
            System.out.println("=====================\n");
        }

        public int getHistorySize() {
            return history.size();
        }

        /**
         * Clear all history.
         */
        public void clearHistory() {
            history.clear();
            System.out.println("Caretaker: History cleared");
        }
    }

    /**
     * ADVANCED EXAMPLE: Multi-State Memento
     *
     * Demonstrates saving multiple pieces of state in a single memento.
     */
    public static class ComplexMemento {
        private final String name;
        private final int value;
        private final boolean flag;
        private final LocalDateTime timestamp;

        public ComplexMemento(String name, int value, boolean flag) {
            this.name = name;
            this.value = value;
            this.flag = flag;
            this.timestamp = LocalDateTime.now();
        }

        public String getName() { return name; }
        public int getValue() { return value; }
        public boolean getFlag() { return flag; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("ComplexMemento[name='%s', value=%d, flag=%b]",
                    name, value, flag);
        }
    }

    /**
     * Complex Originator with multiple state fields.
     */
    public static class ComplexOriginator {
        private String name;
        private int value;
        private boolean flag;

        public ComplexOriginator(String name, int value, boolean flag) {
            this.name = name;
            this.value = value;
            this.flag = flag;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        /**
         * Create memento with all state.
         */
        public ComplexMemento save() {
            System.out.println("  → Saving complex state: " + this);
            return new ComplexMemento(name, value, flag);
        }

        /**
         * Restore all state from memento.
         */
        public void restore(ComplexMemento memento) {
            System.out.println("  ← Restoring complex state from: " + memento);
            this.name = memento.getName();
            this.value = memento.getValue();
            this.flag = memento.getFlag();
        }

        @Override
        public String toString() {
            return String.format("ComplexOriginator[name='%s', value=%d, flag=%b]",
                    name, value, flag);
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Memento pattern works.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         MEMENTO PATTERN - DEMONSTRATION                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // SCENARIO 1: Basic Memento Usage
        System.out.println("SCENARIO 1: Basic Save/Restore\n");
        System.out.println("=" .repeat(60));

        Originator originator = new Originator("State A");
        Caretaker caretaker = new Caretaker(originator);

        // Save initial state
        caretaker.backup("Initial State");

        // Make changes
        originator.setState("State B");
        caretaker.backup("After B");

        originator.setState("State C");
        caretaker.backup("After C");

        originator.display();
        caretaker.showHistory();

        // Undo changes
        System.out.println("Performing undo...");
        caretaker.undo();
        originator.display();

        System.out.println("\nPerforming another undo...");
        caretaker.undo();
        originator.display();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 2: Restore to Specific Point
        System.out.println("SCENARIO 2: Restore to Specific Point\n");
        System.out.println("=".repeat(60));

        Originator originator2 = new Originator("Initial");
        Caretaker caretaker2 = new Caretaker(originator2);

        caretaker2.backup("Checkpoint 0");
        originator2.setState("Version 1");
        caretaker2.backup("Checkpoint 1");
        originator2.setState("Version 2");
        caretaker2.backup("Checkpoint 2");
        originator2.setState("Version 3");
        caretaker2.backup("Checkpoint 3");

        caretaker2.showHistory();

        // Jump to checkpoint 1
        System.out.println("Restoring to checkpoint 1...");
        caretaker2.restoreToPoint(1);
        originator2.display();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 3: Complex State
        System.out.println("SCENARIO 3: Complex Multi-Field State\n");
        System.out.println("=".repeat(60));

        ComplexOriginator complex = new ComplexOriginator("Alpha", 100, true);
        System.out.println("Initial: " + complex);

        ComplexMemento snapshot1 = complex.save();

        complex.setName("Beta");
        complex.setValue(200);
        complex.setFlag(false);
        System.out.println("Modified: " + complex);

        ComplexMemento snapshot2 = complex.save();

        complex.setName("Gamma");
        complex.setValue(300);
        System.out.println("Modified again: " + complex);

        // Restore to first snapshot
        System.out.println("\nRestoring to first snapshot...");
        complex.restore(snapshot1);
        System.out.println("Restored: " + complex);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Summary
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. ENCAPSULATION PRESERVED");
        System.out.println("   → Memento stores state without exposing internals");
        System.out.println("   → Caretaker doesn't examine memento contents");
        System.out.println("   → Only originator can read memento state");
        System.out.println();
        System.out.println("2. STATE CAPTURE");
        System.out.println("   → save() creates snapshot of current state");
        System.out.println("   → Memento is immutable (cannot be changed)");
        System.out.println("   → Multiple fields can be captured in one memento");
        System.out.println();
        System.out.println("3. STATE RESTORATION");
        System.out.println("   → restore() returns object to previous state");
        System.out.println("   → Can undo to previous state");
        System.out.println("   → Can jump to specific checkpoint");
        System.out.println();
        System.out.println("4. HISTORY MANAGEMENT");
        System.out.println("   → Caretaker manages memento collection");
        System.out.println("   → Can implement undo/redo stacks");
        System.out.println("   → Can implement checkpoints/savepoints");
        System.out.println();
        System.out.println("5. SEPARATION OF CONCERNS");
        System.out.println("   → Originator: Creates and uses mementos");
        System.out.println("   → Memento: Stores state");
        System.out.println("   → Caretaker: Manages mementos");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}