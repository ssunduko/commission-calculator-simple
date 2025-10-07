package com.chapman.edu.commissions.patterns.behavioral.command;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * COMMAND PATTERN STRUCTURE
 * ==========================
 *
 * This file demonstrates the core structure of the Command Pattern, showing the fundamental
 * interfaces and contracts that define how commands encapsulate operations as objects.
 *
 * PATTERN INTENT:
 * Encapsulate a request as an object, thereby letting you parameterize clients with different
 * requests, queue or log requests, and support undoable operations.
 *
 * PATTERN PARTICIPANTS:
 * 1. Command Interface - Defines the contract for executing operations
 * 2. Invoker - Asks the command to carry out the request
 * 3. Receiver - Knows how to perform the actual work
 * 4. ConcreteCommand - Binds receiver to an action
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Separating request from execution
 * - Command as first-class objects
 * - Support for undo/redo operations
 * - Command queuing and logging
 * - Macro commands (composite pattern integration)
 */
public class CommandStructure {

    /**
     * COMMAND INTERFACE
     * =================
     *
     * The Command interface represents the core abstraction of the pattern.
     * It defines a single method that all concrete commands must implement.
     * RESPONSIBILITIES:
     * - Define a standard interface for executing operations
     * - Support undo/redo through reverse operations
     * - Enable command composition and sequencing
     * DESIGN BENEFITS:
     * - Commands can be passed around as objects
     * - Commands can be stored and executed later
     * - Commands can be queued, logged, and undone
     * - New command types can be added without changing existing code
     */
    public interface Command {
        /**
         * Executes the command.
         *
         * COMMAND PATTERN CONCEPT:
         * This is the core method that performs the encapsulated operation.
         * When called, it executes the business logic, typically by delegating
         * to a receiver object.
         * EXECUTION FLOW:
         * 1. Command is created with necessary parameters
         * 2. Invoker calls execute() when ready
         * 3. Command delegates to receiver to perform actual work
         * 4. State changes are recorded for potential undo
         * IMPLEMENTATION STRATEGIES:
         * - Simple commands: Execute directly without a receiver
         * - Complex commands: Delegate to receiver's methods
         * - Transactional commands: Support rollback on failure
         * - Logged commands: Record execution for audit trails
         */
        void execute();

        /**
         * Undoes the command, reversing its effects.
         *
         * UNDO CONCEPT:
         * The undo operation reverses the changes made by execute().
         * This is one of the most powerful features of the Command Pattern.
         *
         * IMPLEMENTATION REQUIREMENTS:
         * To support undo, commands must:
         * 1. Store previous state before executing
         * 2. Implement reverse operation logic
         * 3. Handle edge cases (what if execute() was never called?)
         *
         * UNDO STRATEGIES:
         *
         * 1. MEMENTO PATTERN INTEGRATION:
         *    Store complete object state before changes
         *    Pro: Simple to implement, handles any complexity
         *    Con: Memory intensive for large objects
         *
         * 2. COMPENSATING OPERATIONS:
         *    Implement reverse logic for each operation
         *    Pro: Memory efficient
         *    Con: Complex to implement correctly
         *
         * 3. DELTA RECORDING:
         *    Store only what changed
         *    Pro: Efficient memory usage
         *    Con: Requires careful tracking of changes
         *
         * ERROR HANDLING:
         * If undo cannot be performed (corrupted state, external dependencies),
         * should throw an exception with clear error message.
         */
        void undo();

        /**
         * Returns a description of what this command does.
         *
         * METADATA CONCEPT:
         * Commands should be self-describing for logging, debugging,
         * and displaying in user interfaces (like undo history).
         *
         * USE CASES:
         * - Building undo/redo UI menus ("Undo: Update Deal Value")
         * - Audit logging ("User executed: Create Deal")
         * - Debugging ("Current command: Change Deal Status")
         * - Command history displays
         *
         * IMPLEMENTATION GUIDELINES:
         * - Use clear, user-friendly language
         * - Include relevant details (e.g., "Update Deal DEAL-001 value to $50,000")
         * - Keep it concise but informative
         * - Consider internationalization for production systems
         */
        String getDescription();
    }

    /**
     * RECEIVER INTERFACE
     * ==================
     *
     * The Receiver is the object that knows how to perform the actual work.
     * Commands delegate to receivers to carry out operations.
     *
     * RESPONSIBILITIES:
     * - Contains business logic for operations
     * - Knows how to perform the actual work
     * - Maintains state that commands modify
     *
     * DESIGN PATTERN CONCEPT:
     * The separation between Command and Receiver enables:
     * - Different commands to use the same receiver
     * - Same command logic to work with different receivers
     * - Commands to coordinate multiple receivers
     */
    public interface DealReceiver {

        /**
         * Updates the status of a deal.
         *
         * RECEIVER RESPONSIBILITY:
         * This method contains the actual business logic for changing a deal's status.
         * Commands will call this method but don't implement the logic themselves.
         *
         * @param dealId the ID of the deal to update
         * @param newStatus the new status to set
         * @return the previous status (needed for undo)
         */
        DealStatus updateDealStatus(String dealId, DealStatus newStatus);

        /**
         * Updates the value of a deal.
         *
         * @param dealId the ID of the deal to update
         * @param newValue the new value to set
         * @return the previous value (needed for undo)
         */
        BigDecimal updateDealValue(String dealId, BigDecimal newValue);

        /**
         * Creates a new deal.
         *
         * @param deal the deal to create
         */
        void createDeal(Deal deal);

        /**
         * Removes a deal.
         *
         * @param dealId the ID of the deal to remove
         * @return the removed deal (needed for undo)
         */
        Deal removeDeal(String dealId);

        /**
         * Retrieves a deal by ID.
         *
         * @param dealId the ID of the deal
         * @return the deal, or null if not found
         */
        Deal getDeal(String dealId);
    }

    /**
     * INVOKER CLASS
     * =============
     *
     * The Invoker is responsible for executing commands. It doesn't know about
     * concrete command types or what they do - it just calls execute().
     *
     * PATTERN ROLE: Invoker
     *
     * RESPONSIBILITIES:
     * - Store commands for execution
     * - Execute commands when requested
     * - Maintain command history for undo/redo
     * - Provide undo/redo functionality
     *
     * KEY DESIGN DECISIONS:
     * - Decoupled from specific command types
     * - Maintains execution history
     * - Supports both synchronous and deferred execution
     */
    public static class CommandInvoker {

        private static final Logger LOGGER = Logger.getLogger(CommandInvoker.class.getName());

        /**
         * History of executed commands for undo functionality.
         *
         * UNDO/REDO IMPLEMENTATION:
         * We use a stack to maintain command history. The most recently
         * executed command is on top, making it easy to undo in reverse order.
         *
         * DESIGN CHOICE: Stack vs. List
         * - Stack: Natural fit for undo/redo (LIFO)
         * - Clear semantic meaning (push/pop)
         * - Efficient for typical undo operations
         *
         * MEMORY CONSIDERATIONS:
         * In production, you might:
         * - Limit history size (e.g., max 100 commands)
         * - Clear old commands to free memory
         * - Persist history to disk for crash recovery
         */
        private final Stack<Command> commandHistory = new Stack<>();

        /**
         * Stack for redo functionality.
         *
         * REDO CONCEPT:
         * When a command is undone, it's moved to the redo stack.
         * This allows users to "redo" undone operations.
         *
         * IMPORTANT: The redo stack is cleared when a new command is executed,
         * because you can't redo after performing a new action (it would create
         * inconsistent state).
         */
        private final Stack<Command> redoStack = new Stack<>();

        /**
         * Executes a command and adds it to history.
         *
         * EXECUTION PATTERN:
         * 1. Call command's execute() method
         * 2. If successful, add to history
         * 3. Clear redo stack (new action invalidates redo history)
         * 4. Log the execution
         *
         * ERROR HANDLING:
         * If execute() throws an exception, the command is NOT added to history,
         * preserving consistent state.
         *
         * @param command the command to execute
         */
        public void executeCommand(Command command) {
            try {
                LOGGER.info("Executing command: " + command.getDescription());

                // Execute the command
                command.execute();

                // Add to history only if execution succeeded
                commandHistory.push(command);

                // Clear redo stack because new actions invalidate redo history
                // You can't redo after doing something new
                if (!redoStack.isEmpty()) {
                    LOGGER.info("Clearing redo stack (new command executed)");
                    redoStack.clear();
                }

                LOGGER.info("Command executed successfully");

            } catch (Exception e) {
                // If execution fails, don't add to history
                LOGGER.severe("Command execution failed: " + e.getMessage());
                throw new RuntimeException("Failed to execute command: " + command.getDescription(), e);
            }
        }

        /**
         * Undoes the most recently executed command.
         *
         * UNDO IMPLEMENTATION:
         * 1. Pop command from history stack
         * 2. Call command's undo() method
         * 3. Push command to redo stack (so it can be redone)
         * 4. Log the undo
         *
         * EDGE CASES:
         * - Nothing to undo: Method returns false
         * - Undo fails: Exception is logged but doesn't affect stacks
         *
         * @return true if undo was performed, false if no commands to undo
         */
        public boolean undo() {
            if (commandHistory.isEmpty()) {
                LOGGER.warning("Nothing to undo");
                return false;
            }

            try {
                // Get the most recent command
                Command command = commandHistory.pop();

                LOGGER.info("Undoing command: " + command.getDescription());

                // Undo the command
                command.undo();

                // Move to redo stack so it can be redone
                redoStack.push(command);

                LOGGER.info("Command undone successfully");
                return true;

            } catch (Exception e) {
                LOGGER.severe("Undo failed: " + e.getMessage());
                throw new RuntimeException("Failed to undo command", e);
            }
        }

        /**
         * Redoes the most recently undone command.
         *
         * REDO IMPLEMENTATION:
         * 1. Pop command from redo stack
         * 2. Call command's execute() method again
         * 3. Push command back to history
         * 4. Log the redo
         *
         * @return true if redo was performed, false if no commands to redo
         */
        public boolean redo() {
            if (redoStack.isEmpty()) {
                LOGGER.warning("Nothing to redo");
                return false;
            }

            try {
                // Get the most recently undone command
                Command command = redoStack.pop();

                LOGGER.info("Redoing command: " + command.getDescription());

                // Re-execute the command
                command.execute();

                // Move back to history
                commandHistory.push(command);

                LOGGER.info("Command redone successfully");
                return true;

            } catch (Exception e) {
                LOGGER.severe("Redo failed: " + e.getMessage());
                throw new RuntimeException("Failed to redo command", e);
            }
        }

        /**
         * Returns the command history as a readable list.
         *
         * USE CASES:
         * - Displaying undo history in UI
         * - Debugging command execution
         * - Audit logging
         * - Testing command flow
         */
        public String getHistory() {
            if (commandHistory.isEmpty()) {
                return "No commands in history";
            }

            StringBuilder history = new StringBuilder("Command History:\n");
            int index = 1;
            for (Command command : commandHistory) {
                history.append(String.format("%d. %s\n", index++, command.getDescription()));
            }
            return history.toString();
        }

        /**
         * Clears all command history.
         *
         * USE CASES:
         * - Starting a new session
         * - Freeing memory
         * - After saving state (don't need old undo history)
         */
        public void clearHistory() {
            commandHistory.clear();
            redoStack.clear();
            LOGGER.info("Command history cleared");
        }

        /**
         * Returns the number of commands that can be undone.
         */
        public int getUndoCount() {
            return commandHistory.size();
        }

        /**
         * Returns the number of commands that can be redone.
         */
        public int getRedoCount() {
            return redoStack.size();
        }
    }

    /**
     * ABSTRACT BASE COMMAND (Optional Enhancement)
     * ============================================
     *
     * While not required by the pattern, an abstract base class can provide
     * common functionality for commands.
     *
     * BENEFITS:
     * - Reduces code duplication
     * - Provides standard timestamp tracking
     * - Enforces consistent logging
     * - Can implement common validation
     */
    public abstract static class AbstractCommand implements Command {

        /**
         * Timestamp when the command was created.
         *
         * AUDIT TRAIL:
         * Tracking when commands were created helps with:
         * - Debugging ("This command was created 2 hours ago")
         * - Audit logs ("User executed command at 2023-10-15 14:30")
         * - Performance analysis ("Command took 500ms to execute")
         */
        protected final LocalDateTime createdAt;

        /**
         * Timestamp when the command was executed.
         */
        protected LocalDateTime executedAt;

        /**
         * Flag indicating whether the command has been executed.
         *
         * STATE TRACKING:
         * This helps prevent:
         * - Executing the same command twice
         * - Undoing a command that was never executed
         * - Inconsistent state from improper command lifecycle
         */
        protected boolean executed = false;

        public AbstractCommand() {
            this.createdAt = LocalDateTime.now();
        }

        /**
         * Template method for execution with common pre/post processing.
         *
         * TEMPLATE METHOD PATTERN:
         * This method defines the skeleton of the execution algorithm:
         * 1. Pre-execution checks and logging
         * 2. Call abstract doExecute() (subclass implements)
         * 3. Post-execution tracking
         *
         * INVARIANTS ENFORCED:
         * - Timestamps are always set
         * - Executed flag is always updated
         * - Logging always happens
         * - Subclasses just implement business logic
         */
        @Override
        public final void execute() {
            Logger.getLogger(getClass().getName()).info(
                    "Executing: " + getDescription()
            );

            // Perform the actual execution (implemented by subclass)
            doExecute();

            // Track execution
            this.executedAt = LocalDateTime.now();
            this.executed = true;
        }

        /**
         * Template method for undo with common pre/post processing.
         */
        @Override
        public final void undo() {
            if (!executed) {
                throw new IllegalStateException(
                        "Cannot undo command that was never executed: " + getDescription()
                );
            }

            Logger.getLogger(getClass().getName()).info(
                    "Undoing: " + getDescription()
            );

            // Perform the actual undo (implemented by subclass)
            doUndo();

            // Mark as not executed (can be executed again)
            this.executed = false;
        }

        /**
         * Subclasses implement this to provide the actual execution logic.
         */
        protected abstract void doExecute();

        /**
         * Subclasses implement this to provide the actual undo logic.
         */
        protected abstract void doUndo();

        /**
         * Utility method to check if command has been executed.
         */
        public boolean isExecuted() {
            return executed;
        }

        /**
         * Utility method to get execution timestamp.
         */
        public LocalDateTime getExecutedAt() {
            return executedAt;
        }
    }

    /**
     * PATTERN STRUCTURE SUMMARY
     * =========================
     *
     * The Command Pattern structure consists of four main components:
     *
     * 1. COMMAND (Command interface):
     *    - Declares execution interface
     *    - Supports undo operation
     *    - Provides description for logging
     *
     * 2. CONCRETE COMMAND:
     *    - Implements Command interface
     *    - Stores parameters and state for undo
     *    - Binds receiver to action
     *    - Implements execute() and undo()
     *
     * 3. INVOKER (CommandInvoker):
     *    - Executes commands
     *    - Maintains command history
     *    - Provides undo/redo functionality
     *    - Decoupled from concrete commands
     *
     * 4. RECEIVER (DealReceiver):
     *    - Performs actual business operations
     *    - Contains domain knowledge
     *    - Accessed by commands
     *
     * RELATIONSHIPS:
     * Client → creates ConcreteCommand → references Receiver
     * Client → gives ConcreteCommand to Invoker
     * Invoker → calls execute() on Command
     * Command → calls methods on Receiver
     *
     * KEY PRINCIPLES SUPPORTED:
     * - Single Responsibility: Commands focus on one operation
     * - Open/Closed: New commands added without modifying existing code
     * - Dependency Inversion: Invoker depends on Command abstraction
     * - Command as first-class object: Can be stored, queued, logged
     * - Separation of Concerns: Request separated from execution
     */
}