package com.chapman.edu.commissions.patterns.behavioral.command;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.command.CommandStructure.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * COMMAND PATTERN IMPLEMENTATION
 * ===============================
 *
 * This file demonstrates concrete implementations of the Command Pattern,
 * showing how the abstract structure is applied to solve real business problems
 * in the commission calculator domain.
 *
 * IMPLEMENTATIONS PROVIDED:
 * 1. DealManager - Concrete Receiver
 * 2. UpdateDealStatusCommand - Concrete Command (simple state change)
 * 3. UpdateDealValueCommand - Concrete Command (value modification)
 * 4. CreateDealCommand - Concrete Command (creation operation)
 * 5. MacroDealCommand - Composite Command (multiple operations)
 *
 * LEARNING OBJECTIVES:
 * - How to implement commands with different complexities
 * - How to store state for undo functionality
 * - How to implement receivers with business logic
 * - How to create composite commands
 * - Best practices for production-ready implementations
 */
public class CommandImplementation {

    /**
     * CONCRETE RECEIVER: DealManager
     * ===============================
     *
     * The receiver contains the actual business logic for deal operations.
     * Commands delegate to this receiver to perform the real work.
     *
     * PATTERN ROLE: Receiver
     *
     * RESPONSIBILITIES:
     * 1. Maintain the collection of deals (business state)
     * 2. Implement actual business operations
     * 3. Provide data needed for undo operations
     * 4. Validate business rules
     *
     * KEY DESIGN DECISIONS:
     * - Stateful: Maintains the list of deals
     * - Returns previous state: Enables commands to undo
     * - Business logic encapsulation: Commands don't duplicate this logic
     * - Domain model integration: Works with Deal entity
     */
    public static class DealManager implements DealReceiver {

        private static final Logger LOGGER = Logger.getLogger(DealManager.class.getName());

        /**
         * The collection of deals being managed.
         *
         * RECEIVER STATE:
         * The receiver maintains business state. Commands modify this state
         * through the receiver's methods, not by directly accessing the collection.
         *
         * ENCAPSULATION:
         * We keep this private and provide methods to interact with it.
         * This ensures all modifications go through proper business logic.
         */
        private final List<Deal> deals;

        public DealManager() {
            this.deals = new ArrayList<>();
        }

        /**
         * Updates the status of a deal.
         *
         * RECEIVER RESPONSIBILITY:
         * This method contains the actual business logic. The command just
         * calls this method - it doesn't implement the logic itself.
         *
         * UNDO SUPPORT:
         * Returns the previous status so the command can store it for undo.
         * This is a key pattern: receivers return what's needed to reverse changes.
         *
         * @param dealId the ID of the deal to update
         * @param newStatus the new status to set
         * @return the previous status (null if deal not found)
         */
        @Override
        public DealStatus updateDealStatus(String dealId, DealStatus newStatus) {
            Deal deal = findDealById(dealId);
            if (deal == null) {
                LOGGER.warning("Deal not found: " + dealId);
                return null;
            }

            // Capture old status for undo
            DealStatus oldStatus = deal.getStatus();

            // Business rule validation could go here
            // For example: Can't reopen a LOST deal
            if (oldStatus == DealStatus.LOST && newStatus == DealStatus.OPEN) {
                throw new IllegalStateException("Cannot reopen a lost deal");
            }

            // Perform the state change
            deal.setStatus(newStatus);

            LOGGER.info(String.format("Deal %s status changed: %s → %s",
                    dealId, oldStatus, newStatus));

            // Return old value for undo
            return oldStatus;
        }

        /**
         * Updates the value of a deal.
         *
         * VALUE CHANGE HANDLING:
         * Demonstrates how receivers handle value modifications and provide
         * previous values for undo.
         *
         * @param dealId the ID of the deal to update
         * @param newValue the new value to set
         * @return the previous value (null if deal not found)
         */
        @Override
        public BigDecimal updateDealValue(String dealId, BigDecimal newValue) {
            Deal deal = findDealById(dealId);
            if (deal == null) {
                LOGGER.warning("Deal not found: " + dealId);
                return null;
            }

            // Validate business rules
            if (newValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Deal value cannot be negative");
            }

            // Capture old value for undo
            BigDecimal oldValue = deal.getValue();

            // Perform the change
            deal.setValue(newValue);

            LOGGER.info(String.format("Deal %s value changed: $%s → $%s",
                    dealId, oldValue, newValue));

            return oldValue;
        }

        /**
         * Creates a new deal.
         *
         * CREATION OPERATION:
         * For create operations, undo means delete. No previous state to return.
         *
         * @param deal the deal to create
         */
        @Override
        public void createDeal(Deal deal) {
            Objects.requireNonNull(deal, "Deal cannot be null");
            Objects.requireNonNull(deal.getId(), "Deal ID cannot be null");

            // Check for duplicate
            if (findDealById(deal.getId()) != null) {
                throw new IllegalStateException("Deal already exists: " + deal.getId());
            }

            // Add to collection
            deals.add(deal);

            LOGGER.info(String.format("Deal created: %s (%s) - $%s",
                    deal.getId(), deal.getTitle(), deal.getValue()));
        }

        /**
         * Removes a deal.
         *
         * DELETION OPERATION:
         * Returns the deleted deal so it can be restored on undo.
         *
         * @param dealId the ID of the deal to remove
         * @return the removed deal (null if not found)
         */
        @Override
        public Deal removeDeal(String dealId) {
            Deal deal = findDealById(dealId);
            if (deal == null) {
                LOGGER.warning("Deal not found: " + dealId);
                return null;
            }

            deals.remove(deal);

            LOGGER.info("Deal removed: " + dealId);

            return deal;
        }

        /**
         * Retrieves a deal by ID.
         */
        @Override
        public Deal getDeal(String dealId) {
            return findDealById(dealId);
        }

        /**
         * Helper method to find a deal by ID.
         */
        private Deal findDealById(String dealId) {
            return deals.stream()
                    .filter(d -> dealId.equals(d.getId()))
                    .findFirst()
                    .orElse(null);
        }

        /**
         * Returns all deals (defensive copy).
         */
        public List<Deal> getAllDeals() {
            return new ArrayList<>(deals);
        }

        /**
         * Returns the number of deals.
         */
        public int getDealCount() {
            return deals.size();
        }
    }

    /**
     * CONCRETE COMMAND #1: UpdateDealStatusCommand
     * =============================================
     *
     * This command encapsulates a deal status update operation.
     * Demonstrates basic command implementation with undo support.
     *
     * PATTERN ROLE: Concrete Command
     *
     * KEY CONCEPTS DEMONSTRATED:
     * - Parameter storage (dealId, newStatus)
     * - Previous state storage (oldStatus for undo)
     * - Receiver delegation
     * - Bidirectional operations (execute/undo)
     */
    public static class UpdateDealStatusCommand extends AbstractCommand {

        private static final Logger LOGGER = Logger.getLogger(UpdateDealStatusCommand.class.getName());

        /**
         * The receiver that will perform the actual operation.
         *
         * RECEIVER REFERENCE:
         * Commands hold a reference to their receiver. This is set at
         * construction time and doesn't change.
         *
         * DEPENDENCY INJECTION:
         * The receiver is injected via constructor, following the
         * Dependency Inversion Principle.
         */
        private final DealReceiver receiver;

        /**
         * Parameters for the operation.
         *
         * PARAMETER STORAGE:
         * Commands store all parameters needed for their operation.
         * This allows commands to be created (parameterized) in one place
         * and executed later in a different place.
         */
        private final String dealId;
        private final DealStatus newStatus;

        /**
         * Previous state for undo.
         *
         * UNDO STATE:
         * To undo an operation, we need to know the previous state.
         * This is captured during execute() and used during undo().
         *
         * IMPORTANT: This is set during execute(), not during construction,
         * because we don't know the old value until we perform the operation.
         */
        private DealStatus oldStatus;

        /**
         * Constructor: Parameterizes the command.
         *
         * COMMAND PARAMETERIZATION:
         * The command is configured with everything it needs to execute.
         * This makes commands "complete" objects that can be passed around,
         * stored, and executed at any time.
         *
         * @param receiver the object that will perform the operation
         * @param dealId the ID of the deal to update
         * @param newStatus the new status to set
         */
        public UpdateDealStatusCommand(DealReceiver receiver, String dealId, DealStatus newStatus) {
            super();
            this.receiver = Objects.requireNonNull(receiver, "Receiver cannot be null");
            this.dealId = Objects.requireNonNull(dealId, "Deal ID cannot be null");
            this.newStatus = Objects.requireNonNull(newStatus, "New status cannot be null");
        }

        /**
         * Executes the status update.
         *
         * EXECUTION FLOW:
         * 1. Delegate to receiver to perform actual work
         * 2. Capture old status for undo
         * 3. Handle errors appropriately
         */
        @Override
        protected void doExecute() {
            // Delegate to receiver - command doesn't implement business logic
            oldStatus = receiver.updateDealStatus(dealId, newStatus);

            if (oldStatus == null) {
                throw new IllegalStateException("Failed to update deal status - deal not found: " + dealId);
            }

            LOGGER.info(String.format("Command executed: Status %s → %s for deal %s",
                    oldStatus, newStatus, dealId));
        }

        /**
         * Undoes the status update.
         *
         * UNDO IMPLEMENTATION:
         * Reverses the operation by setting the status back to the old value.
         *
         * COMPENSATING OPERATION:
         * Undo is implemented as another call to updateDealStatus() with
         * the old value. This is simpler than trying to "reverse" the operation.
         */
        @Override
        protected void doUndo() {
            // Reverse the operation by setting back to old status
            DealStatus current = receiver.updateDealStatus(dealId, oldStatus);

            LOGGER.info(String.format("Command undone: Status %s → %s for deal %s",
                    current, oldStatus, dealId));
        }

        /**
         * Provides a human-readable description.
         *
         * DESCRIPTION FORMAT:
         * Clear and specific, including key parameters so users know
         * exactly what this command does.
         */
        @Override
        public String getDescription() {
            return String.format("Update Deal %s status to %s", dealId, newStatus);
        }
    }

    /**
     * CONCRETE COMMAND #2: UpdateDealValueCommand
     * ============================================
     * This command encapsulates a deal value update operation.
     * Similar structure to UpdateDealStatusCommand but operates on value.
     * PATTERN ROLE: Concrete Command
     * DEMONSTRATES:
     * - Numeric value change handling
     * - Same pattern as status command (consistency)
     * - Precise undo for monetary values
     */
    public static class UpdateDealValueCommand extends AbstractCommand {

        private static final Logger LOGGER = Logger.getLogger(UpdateDealValueCommand.class.getName());

        private final DealReceiver receiver;
        private final String dealId;
        private final BigDecimal newValue;

        /**
         * Previous value for undo.
         * BIGDECIMAL FOR MONEY:
         * Using BigDecimal for monetary values ensures precision.
         * This is critical for undo - we need exact values, not
         * floating-point approximations.
         */
        private BigDecimal oldValue;

        public UpdateDealValueCommand(DealReceiver receiver, String dealId, BigDecimal newValue) {
            super();
            this.receiver = Objects.requireNonNull(receiver, "Receiver cannot be null");
            this.dealId = Objects.requireNonNull(dealId, "Deal ID cannot be null");
            this.newValue = Objects.requireNonNull(newValue, "New value cannot be null");
        }

        @Override
        protected void doExecute() {
            oldValue = receiver.updateDealValue(dealId, newValue);

            if (oldValue == null) {
                throw new IllegalStateException("Failed to update deal value - deal not found: " + dealId);
            }

            LOGGER.info(String.format("Command executed: Value $%s → $%s for deal %s",
                    oldValue, newValue, dealId));
        }

        @Override
        protected void doUndo() {
            BigDecimal current = receiver.updateDealValue(dealId, oldValue);

            LOGGER.info(String.format("Command undone: Value $%s → $%s for deal %s",
                    current, oldValue, dealId));
        }

        @Override
        public String getDescription() {
            return String.format("Update Deal %s value to $%s", dealId, newValue);
        }
    }

    /**
     * CONCRETE COMMAND #3: CreateDealCommand
     * =======================================
     *
     * This command encapsulates deal creation.
     * Demonstrates how to handle creation operations with undo.
     *
     * PATTERN ROLE: Concrete Command
     *
     * KEY CONCEPTS:
     * - Creation operation handling
     * - Undo via deletion (compensating operation)
     * - No previous state to capture (object didn't exist)
     */
    public static class CreateDealCommand extends AbstractCommand {

        private static final Logger LOGGER = Logger.getLogger(CreateDealCommand.class.getName());

        private final DealReceiver receiver;
        private final Deal deal;

        /**
         * Constructor: Takes the deal to create.
         *
         * OBJECT CREATION PATTERN:
         * For creation commands, we store the entire object to be created.
         * This allows us to recreate it on redo if needed.
         *
         * @param receiver the object that will perform the creation
         * @param deal the deal to create
         */
        public CreateDealCommand(DealReceiver receiver, Deal deal) {
            super();
            this.receiver = Objects.requireNonNull(receiver, "Receiver cannot be null");
            this.deal = Objects.requireNonNull(deal, "Deal cannot be null");
        }

        /**
         * Executes the creation.
         *
         * CREATION LOGIC:
         * Delegate to receiver to add the deal to the system.
         */
        @Override
        protected void doExecute() {
            receiver.createDeal(deal);

            LOGGER.info(String.format("Command executed: Created deal %s (%s)",
                    deal.getId(), deal.getTitle()));
        }

        /**
         * Undoes the creation by removing the deal.
         *
         * UNDO CREATION = DELETE:
         * To undo a creation, we delete the created object.
         * This is a compensating operation.
         *
         * IMPORTANT: We could store the removed deal if we wanted to
         * support redo after undo, but our base infrastructure handles
         * this by re-executing the command.
         */
        @Override
        protected void doUndo() {
            Deal removed = receiver.removeDeal(deal.getId());

            if (removed == null) {
                throw new IllegalStateException("Failed to undo creation - deal not found: " + deal.getId());
            }

            LOGGER.info(String.format("Command undone: Removed deal %s", deal.getId()));
        }

        @Override
        public String getDescription() {
            return String.format("Create Deal %s (%s) - $%s",
                    deal.getId(), deal.getTitle(), deal.getValue());
        }
    }

    /**
     * CONCRETE COMMAND #4: MacroDealCommand (Composite Command)
     * ==========================================================
     *
     * This command executes multiple commands as a single unit.
     * Demonstrates the Composite Pattern integrated with Command Pattern.
     *
     * PATTERN ROLE: Composite Command
     *
     * KEY CONCEPTS DEMONSTRATED:
     * - Composite Pattern integration
     * - Transactional semantics (all-or-nothing)
     * - Cascading undo (undo all sub-commands)
     * - Command composition
     *
     * USE CASES:
     * - Multi-step operations that should be atomic
     * - Recording macros (sequences of user actions)
     * - Batch operations
     * - Transaction-like command groups
     */
    public static class MacroDealCommand extends AbstractCommand {

        private static final Logger LOGGER = Logger.getLogger(MacroDealCommand.class.getName());

        /**
         * The list of commands that make up this macro.
         *
         * COMPOSITE STRUCTURE:
         * A macro command is composed of other commands. This follows
         * the Composite Pattern - treating individual objects and
         * compositions uniformly.
         *
         * EXECUTION ORDER:
         * Commands are executed in the order they were added.
         * Undo happens in reverse order (LIFO).
         */
        private final List<Command> commands;

        /**
         * Track which commands were successfully executed for proper undo.
         *
         * PARTIAL EXECUTION TRACKING:
         * If execution fails partway through, we need to know which
         * commands to undo. This list tracks successful executions.
         */
        private final List<Command> executedCommands;

        /**
         * Optional description for the macro.
         */
        private final String description;

        /**
         * Constructor for creating a macro command.
         *
         * @param description description of what this macro does
         */
        public MacroDealCommand(String description) {
            super();
            this.description = description;
            this.commands = new ArrayList<>();
            this.executedCommands = new ArrayList<>();
        }

        /**
         * Adds a command to the macro.
         *
         * BUILDER PATTERN:
         * Returns 'this' to enable fluent chaining:
         * macro.addCommand(cmd1).addCommand(cmd2).addCommand(cmd3);
         *
         * @param command the command to add
         * @return this macro command (for fluent API)
         */
        public MacroDealCommand addCommand(Command command) {
            Objects.requireNonNull(command, "Command cannot be null");
            commands.add(command);
            return this;
        }

        /**
         * Executes all commands in sequence.
         *
         * TRANSACTIONAL SEMANTICS:
         * - All commands execute in order
         * - If any command fails, already-executed commands are undone
         * - System is left in consistent state (all-or-nothing)
         *
         * ERROR HANDLING:
         * On failure, we automatically undo previously executed commands
         * in reverse order, ensuring rollback to initial state.
         */
        @Override
        protected void doExecute() {
            LOGGER.info(String.format("Executing macro command with %d sub-commands", commands.size()));

            // Clear previously executed commands (in case of re-execution)
            executedCommands.clear();

            try {
                // Execute each command in sequence
                for (Command command : commands) {
                    LOGGER.info("  Executing: " + command.getDescription());

                    command.execute();

                    // Track successful execution for potential rollback
                    executedCommands.add(command);
                }

                LOGGER.info("Macro command completed successfully");

            } catch (Exception e) {
                // ROLLBACK: If any command fails, undo all previously executed commands
                LOGGER.severe("Macro command failed, rolling back executed commands");

                rollbackExecutedCommands();

                // Re-throw the exception to indicate failure
                throw new RuntimeException("Macro command failed: " + e.getMessage(), e);
            }
        }

        /**
         * Undoes all commands in reverse order.
         *
         * REVERSE ORDER UNDO:
         * Commands are undone in reverse order (LIFO) to properly
         * reverse dependencies. If command B depends on command A,
         * we must undo B before undoing A.
         *
         * CASCADING UNDO:
         * Each sub-command's undo() is called, which may itself
         * trigger more undo operations if those are composite commands.
         */
        @Override
        protected void doUndo() {
            LOGGER.info("Undoing macro command");

            // Undo in reverse order (LIFO)
            for (int i = executedCommands.size() - 1; i >= 0; i--) {
                Command command = executedCommands.get(i);

                try {
                    LOGGER.info("  Undoing: " + command.getDescription());
                    command.undo();

                } catch (Exception e) {
                    // Log but continue undoing other commands
                    // In production, you might want to throw here instead
                    LOGGER.severe("Failed to undo sub-command: " + e.getMessage());
                }
            }

            // Clear the executed commands list
            executedCommands.clear();

            LOGGER.info("Macro command undone");
        }

        /**
         * Helper method to rollback during failed execution.
         *
         * ROLLBACK IMPLEMENTATION:
         * Called when execution fails partway through. Undoes all
         * commands that were successfully executed before the failure.
         */
        private void rollbackExecutedCommands() {
            // Undo in reverse order
            for (int i = executedCommands.size() - 1; i >= 0; i--) {
                Command command = executedCommands.get(i);

                try {
                    LOGGER.info("  Rolling back: " + command.getDescription());
                    command.undo();

                } catch (Exception undoException) {
                    // Log but continue rolling back
                    LOGGER.severe("Failed to rollback sub-command: " + undoException.getMessage());
                }
            }

            executedCommands.clear();
        }

        @Override
        public String getDescription() {
            return String.format("Macro: %s (%d commands)", description, commands.size());
        }

        /**
         * Returns the number of commands in this macro.
         */
        public int getCommandCount() {
            return commands.size();
        }
    }

    /**
     * IMPLEMENTATION SUMMARY
     * ======================
     *
     * This file demonstrated four types of command implementations:
     *
     * 1. UPDATE COMMANDS (UpdateDealStatusCommand, UpdateDealValueCommand):
     *    - Modify existing state
     *    - Store previous state for undo
     *    - Implement undo via compensating operation
     *    - Most common command type
     *
     * 2. CREATION COMMANDS (CreateDealCommand):
     *    - Create new objects
     *    - Undo via deletion
     *    - No previous state to capture
     *    - Store created object for potential redo
     *
     * 3. COMPOSITE COMMANDS (MacroDealCommand):
     *    - Execute multiple commands as one unit
     *    - Transactional semantics (all-or-nothing)
     *    - Cascading undo in reverse order
     *    - Enable complex multi-step operations
     *
     * 4. RECEIVER (DealManager):
     *    - Contains actual business logic
     *    - Returns previous state for undo
     *    - Validates business rules
     *    - Maintains domain state
     *
     * KEY TAKEAWAYS:
     * - Commands are lightweight - they delegate to receivers
     * - Previous state is captured during execute, not before
     * - Undo is implemented via compensating operations
     * - Composite commands enable complex operations
     * - Receivers return data needed for undo
     * - Commands are parameterized at construction
     * - Execute and undo are symmetric operations
     */
}