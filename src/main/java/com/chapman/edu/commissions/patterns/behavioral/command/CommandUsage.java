package com.chapman.edu.commissions.patterns.behavioral.command;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.command.CommandImplementation.*;
import com.chapman.edu.commissions.patterns.behavioral.command.CommandStructure.*;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * COMMAND PATTERN USAGE DEMONSTRATION
 * ====================================
 *
 * This file demonstrates how to use the Command Pattern in practice, showing
 * the complete workflow from setup through execution to undo/redo operations.
 *
 * DEMONSTRATION FLOW:
 * 1. Creating the receiver (business logic)
 * 2. Creating the invoker (command executor)
 * 3. Creating and executing commands
 * 4. Using undo functionality
 * 5. Using redo functionality
 * 6. Creating and executing macro commands
 * 7. Command pattern benefits demonstration
 *
 * LEARNING OBJECTIVES:
 * - How to wire up Command Pattern components
 * - How commands encapsulate operations as objects
 * - How undo/redo functionality works
 * - How to create composite operations with macros
 * - Best practices for using the pattern
 * - Real-world application scenarios
 */
public class CommandUsage {

    private static final Logger LOGGER = Logger.getLogger(CommandUsage.class.getName());

    /**
     * MAIN DEMONSTRATION
     * ==================
     *
     * This main method walks through a complete Command Pattern scenario,
     * demonstrating all the key features and benefits of the pattern.
     */
    public static void main(String[] args) {
        LOGGER.info("=== COMMAND PATTERN USAGE DEMONSTRATION ===\n");

        // ================================================================
        // STEP 1: Create the Receiver
        // ================================================================
        // The receiver contains the actual business logic.
        // Commands will delegate to this receiver to perform operations.

        LOGGER.info("--- Step 1: Creating the Receiver (Deal Manager) ---");
        DealManager dealManager = new DealManager();
        LOGGER.info("Created DealManager (receiver)");
        LOGGER.info("");

        // ================================================================
        // STEP 2: Create the Invoker
        // ================================================================
        // The invoker executes commands and manages undo/redo history.
        // It doesn't know about specific command types - just the Command interface.

        LOGGER.info("--- Step 2: Creating the Invoker (Command Executor) ---");
        CommandInvoker invoker = new CommandInvoker();
        LOGGER.info("Created CommandInvoker");
        LOGGER.info("");

        // ================================================================
        // STEP 3: Create and Execute Commands
        // ================================================================
        // Commands are created with parameters, then executed via the invoker.
        // This demonstrates the separation between request creation and execution.

        LOGGER.info("--- Step 3: Creating and Executing Commands ---");

        // COMMAND 1: Create a deal
        // Notice how we create the command as an object, then execute it separately
        Deal deal1 = createSampleDeal("DEAL-001", "Cloud Infrastructure Deal", "100000.00", "REP-101");
        Command createCmd1 = new CreateDealCommand(dealManager, deal1);

        // Execute the command through the invoker
        invoker.executeCommand(createCmd1);
        // ↑ The invoker doesn't know this is a CreateDealCommand - it just knows it's a Command
        LOGGER.info("");

        // COMMAND 2: Create another deal
        Deal deal2 = createSampleDeal("DEAL-002", "Software Licensing Deal", "50000.00", "REP-102");
        Command createCmd2 = new CreateDealCommand(dealManager, deal2);
        invoker.executeCommand(createCmd2);
        LOGGER.info("");

        // Display current state
        LOGGER.info("Current state: " + dealManager.getDealCount() + " deals in system");
        LOGGER.info("");

        // ================================================================
        // STEP 4: Update Commands
        // ================================================================
        // Demonstrate update operations with automatic undo tracking.

        LOGGER.info("--- Step 4: Executing Update Commands ---");

        // Update the value of deal 1
        Command updateValueCmd = new UpdateDealValueCommand(
                dealManager,
                "DEAL-001",
                new BigDecimal("125000.00")  // Increased from $100,000
        );
        invoker.executeCommand(updateValueCmd);
        LOGGER.info("");

        // Update the status of deal 1
        Command updateStatusCmd = new UpdateDealStatusCommand(
                dealManager,
                "DEAL-001",
                DealStatus.WON
        );
        invoker.executeCommand(updateStatusCmd);
        LOGGER.info("");

        // ================================================================
        // STEP 5: View Command History
        // ================================================================
        // The invoker tracks all executed commands.

        LOGGER.info("--- Step 5: Command History ---");
        LOGGER.info(invoker.getHistory());
        LOGGER.info("Total commands in history: " + invoker.getUndoCount());
        LOGGER.info("");

        // ================================================================
        // STEP 6: UNDO Operations
        // ================================================================
        // This is where the Command Pattern really shines!
        // We can undo operations in reverse order.

        LOGGER.info("--- Step 6: Undoing Commands ---");

        // Undo the last command (status update)
        LOGGER.info("Undoing last command...");
        invoker.undo();
        // ↑ Deal 1 status is now back to OPEN

        Deal deal = dealManager.getDeal("DEAL-001");
        LOGGER.info("Deal 1 status after undo: " + deal.getStatus());
        LOGGER.info("");

        // Undo the value update
        LOGGER.info("Undoing value update...");
        invoker.undo();
        LOGGER.info("Deal 1 value after undo: $" + dealManager.getDeal("DEAL-001").getValue());
        LOGGER.info("");

        // ================================================================
        // STEP 7: REDO Operations
        // ================================================================
        // Commands that were undone can be redone.

        LOGGER.info("--- Step 7: Redoing Commands ---");

        LOGGER.info("Redoing value update...");
        invoker.redo();
        LOGGER.info("Deal 1 value after redo: $" + dealManager.getDeal("DEAL-001").getValue());
        LOGGER.info("");

        LOGGER.info("Redoing status update...");
        invoker.redo();
        LOGGER.info("Deal 1 status after redo: " + dealManager.getDeal("DEAL-001").getStatus());
        LOGGER.info("");

        // ================================================================
        // STEP 8: Macro Commands (Composite Operations)
        // ================================================================
        // Demonstrate executing multiple commands as a single atomic operation.

        LOGGER.info("--- Step 8: Macro Commands (Composite Operations) ---");

        // Create a macro that represents a complete deal lifecycle
        MacroDealCommand dealLifecycleMacro = new MacroDealCommand(
                "Complete Deal Lifecycle for DEAL-002"
        );

        // Add multiple operations to the macro
        dealLifecycleMacro
                .addCommand(new UpdateDealValueCommand(
                        dealManager,
                        "DEAL-002",
                        new BigDecimal("55000.00")  // Negotiate up from $50,000
                ))
                .addCommand(new UpdateDealStatusCommand(
                        dealManager,
                        "DEAL-002",
                        DealStatus.WON  // Close the deal
                ));

        LOGGER.info("Created macro with " + dealLifecycleMacro.getCommandCount() + " commands");

        // Execute the entire macro as one operation
        invoker.executeCommand(dealLifecycleMacro);
        // ↑ Both commands execute, and they're treated as a single unit for undo/redo
        LOGGER.info("");

        // Verify the results
        Deal deal2After = dealManager.getDeal("DEAL-002");
        LOGGER.info(String.format("Deal 2 after macro: Value=$%s, Status=%s",
                deal2After.getValue(), deal2After.getStatus()));
        LOGGER.info("");

        // ================================================================
        // STEP 9: Undoing a Macro (Atomic Undo)
        // ================================================================
        // When you undo a macro, ALL its commands are undone as one unit.

        LOGGER.info("--- Step 9: Undoing Macro Command ---");
        LOGGER.info("Undoing the entire macro...");

        invoker.undo();
        // ↑ This undoes BOTH the value update AND the status update

        Deal deal2AfterUndo = dealManager.getDeal("DEAL-002");
        LOGGER.info(String.format("Deal 2 after macro undo: Value=$%s, Status=%s",
                deal2AfterUndo.getValue(), deal2AfterUndo.getStatus()));
        LOGGER.info("Both operations were undone as a single unit!");
        LOGGER.info("");

        // ================================================================
        // STEP 10: Command Queuing
        // ================================================================
        // Demonstrate how commands can be queued and executed later.

        demonstrateCommandQueuing(dealManager);

        // ================================================================
        // STEP 11: Command as First-Class Objects
        // ================================================================
        // Demonstrate passing commands around, storing them, etc.

        demonstrateCommandsAsObjects(dealManager, invoker);

        // ================================================================
        // STEP 12: Final State Summary
        // ================================================================
        LOGGER.info("--- Step 12: Final System State ---");
        LOGGER.info("Total deals: " + dealManager.getDealCount());
        LOGGER.info("Commands that can be undone: " + invoker.getUndoCount());
        LOGGER.info("Commands that can be redone: " + invoker.getRedoCount());
        LOGGER.info("");

        // ================================================================
        // PATTERN BENEFITS DEMONSTRATED
        // ================================================================
        LOGGER.info("--- Pattern Benefits Demonstrated ---");
        LOGGER.info("✓ Encapsulation: Requests encapsulated as objects");
        LOGGER.info("✓ Undo/Redo: Full undo/redo functionality with no extra code");
        LOGGER.info("✓ Macro Commands: Complex operations composed from simple ones");
        LOGGER.info("✓ Parameterization: Commands created with parameters, executed later");
        LOGGER.info("✓ Queuing: Commands can be queued and executed in sequence");
        LOGGER.info("✓ Logging: Complete history of all operations");
        LOGGER.info("✓ Decoupling: Invoker decoupled from concrete command types");
        LOGGER.info("✓ Extensibility: New commands added without modifying existing code");
    }

    /**
     * ADVANCED USAGE #1: Command Queuing
     * ===================================
     *
     * Demonstrates how commands can be queued and executed as a batch.
     * This is useful for deferred execution, batch processing, or scheduling.
     */
    private static void demonstrateCommandQueuing(DealManager dealManager) {
        LOGGER.info("--- Step 10: Command Queuing (Batch Execution) ---");

        // Create a separate invoker for the queue
        CommandInvoker queueInvoker = new CommandInvoker();

        // Create multiple commands but don't execute them yet
        Command cmd1 = new CreateDealCommand(
                dealManager,
                createSampleDeal("DEAL-003", "Queued Deal 1", "30000.00", "REP-103")
        );

        Command cmd2 = new CreateDealCommand(
                dealManager,
                createSampleDeal("DEAL-004", "Queued Deal 2", "40000.00", "REP-104")
        );

        Command cmd3 = new UpdateDealStatusCommand(
                dealManager,
                "DEAL-003",
                DealStatus.WON
        );

        LOGGER.info("Created 3 commands for queued execution");

        // Execute them all as a batch
        LOGGER.info("Executing queued commands...");
        queueInvoker.executeCommand(cmd1);
        queueInvoker.executeCommand(cmd2);
        queueInvoker.executeCommand(cmd3);

        LOGGER.info("All queued commands executed");
        LOGGER.info("This demonstrates how commands can be:");
        LOGGER.info("  • Created in one place, executed in another");
        LOGGER.info("  • Stored and executed later");
        LOGGER.info("  • Queued for batch processing");
        LOGGER.info("  • Scheduled for future execution");
        LOGGER.info("");
    }

    /**
     * ADVANCED USAGE #2: Commands as First-Class Objects
     * ===================================================
     *
     * Demonstrates that commands are objects that can be:
     * - Passed as parameters
     * - Stored in collections
     * - Returned from methods
     * - Serialized (in production)
     */
    private static void demonstrateCommandsAsObjects(DealManager dealManager, CommandInvoker invoker) {
        LOGGER.info("--- Step 11: Commands as First-Class Objects ---");

        // Commands can be stored in variables
        Command savedCommand = new UpdateDealValueCommand(
                dealManager,
                "DEAL-001",
                new BigDecimal("150000.00")
        );

        LOGGER.info("Command stored in variable: " + savedCommand.getDescription());

        // Commands can be passed to methods
        executeWithLogging(invoker, savedCommand);

        // Commands can be returned from factory methods
        Command factoryCommand = createDealUpdateCommand(dealManager, "DEAL-001", new BigDecimal("175000.00"));
        LOGGER.info("Command created by factory: " + factoryCommand.getDescription());

        executeWithLogging(invoker, factoryCommand);

        LOGGER.info("This demonstrates that commands are true objects that can be:");
        LOGGER.info("  • Stored in variables");
        LOGGER.info("  • Passed to methods");
        LOGGER.info("  • Returned from methods");
        LOGGER.info("  • Stored in collections");
        LOGGER.info("  • Serialized to disk (in production)");
        LOGGER.info("");
    }

    /**
     * USAGE PATTERNS AND BEST PRACTICES
     * ==================================
     *
     * This section demonstrates common patterns and best practices for using
     * the Command Pattern in real applications.
     */
    public static class UsageBestPractices {

        /**
         * PATTERN 1: Command Factory
         * ==========================
         *
         * Use factory methods to create commonly-used commands.
         * This reduces duplication and ensures consistency.
         */
        public static class CommandFactory {

            private final DealReceiver receiver;

            public CommandFactory(DealReceiver receiver) {
                this.receiver = receiver;
            }

            /**
             * Factory method for creating deal status update commands.
             */
            public Command createStatusUpdateCommand(String dealId, DealStatus newStatus) {
                return new UpdateDealStatusCommand(receiver, dealId, newStatus);
            }

            /**
             * Factory method for creating deal value update commands.
             */
            public Command createValueUpdateCommand(String dealId, BigDecimal newValue) {
                return new UpdateDealValueCommand(receiver, dealId, newValue);
            }

            /**
             * Factory method for creating a "close deal" macro.
             */
            public Command createCloseDealMacro(String dealId, BigDecimal finalValue) {
                MacroDealCommand macro = new MacroDealCommand("Close Deal " + dealId);
                macro.addCommand(createValueUpdateCommand(dealId, finalValue));
                macro.addCommand(createStatusUpdateCommand(dealId, DealStatus.WON));
                return macro;
            }
        }

        /**
         * PATTERN 2: Command Validation
         * ==============================
         *
         * Validate commands before execution.
         */
        public static class ValidatingInvoker extends CommandInvoker {

            /**
             * Validates command before executing.
             */
            @Override
            public void executeCommand(Command command) {
                // Pre-execution validation
                if (command == null) {
                    throw new IllegalArgumentException("Command cannot be null");
                }

                String description = command.getDescription();
                if (description == null || description.trim().isEmpty()) {
                    throw new IllegalStateException("Command must have a valid description");
                }

                // Execute the validated command
                super.executeCommand(command);
            }
        }

        /**
         * PATTERN 3: Transactional Command Execution
         * ===========================================
         *
         * Wrap command execution in transaction boundaries.
         */
        public static class TransactionalInvoker extends CommandInvoker {

            @Override
            public void executeCommand(Command command) {
                // In production, this would:
                // 1. Begin database transaction
                // 2. Execute command
                // 3. Commit transaction
                // 4. On error: Rollback transaction

                try {
                    // beginTransaction();
                    super.executeCommand(command);
                    // commitTransaction();

                } catch (Exception e) {
                    // rollbackTransaction();
                    throw e;
                }
            }
        }

        /**
         * PATTERN 4: Logging Invoker
         * ===========================
         *
         * Add detailed logging around command execution.
         */
        public static class LoggingInvoker extends CommandInvoker {

            private static final Logger LOGGER = Logger.getLogger(LoggingInvoker.class.getName());

            @Override
            public void executeCommand(Command command) {
                long startTime = System.currentTimeMillis();

                LOGGER.info("=== EXECUTING COMMAND ===");
                LOGGER.info("Description: " + command.getDescription());
                LOGGER.info("Start time: " + startTime);

                try {
                    super.executeCommand(command);

                    long endTime = System.currentTimeMillis();
                    LOGGER.info("Execution time: " + (endTime - startTime) + "ms");
                    LOGGER.info("Status: SUCCESS");

                } catch (Exception e) {
                    long endTime = System.currentTimeMillis();
                    LOGGER.severe("Execution time: " + (endTime - startTime) + "ms");
                    LOGGER.severe("Status: FAILED");
                    LOGGER.severe("Error: " + e.getMessage());
                    throw e;

                } finally {
                    LOGGER.info("=== END COMMAND ===");
                }
            }
        }

        /**
         * PATTERN 5: Command History Limits
         * ==================================
         *
         * Limit history size to prevent memory issues.
         */
        public static class LimitedHistoryInvoker extends CommandInvoker {

            private static final int MAX_HISTORY_SIZE = 100;

            // In a real implementation, you'd override the internal history management
            // to enforce the limit. This is a conceptual example.

            /**
             * Clears old history when limit is reached.
             */
            private void enforceHistoryLimit() {
                if (getUndoCount() > MAX_HISTORY_SIZE) {
                    // Remove oldest commands
                    // Implementation would require access to internal stack
                    LOGGER.info("History limit reached, clearing old commands");
                }
            }
        }
    }

    /**
     * COMMON PITFALLS AND HOW TO AVOID THEM
     * ======================================
     */
    public static class CommonPitfalls {

        /**
         * PITFALL 1: Forgetting to Store State for Undo
         * ==============================================
         *
         * Problem: Command doesn't capture previous state before executing,
         * making undo impossible or incorrect.
         *
         * Solution: Always capture previous state in execute(), before making changes.
         */
        public static void pitfallNotStoringState() {
            // BAD: No state stored for undo
            class BadCommand extends AbstractCommand {
                private final DealReceiver receiver;
                private final String dealId;
                private final DealStatus newStatus;
                // Missing: DealStatus oldStatus;

                public BadCommand(DealReceiver receiver, String dealId, DealStatus newStatus) {
                    this.receiver = receiver;
                    this.dealId = dealId;
                    this.newStatus = newStatus;
                }

                @Override
                protected void doExecute() {
                    receiver.updateDealStatus(dealId, newStatus);
                    // Forgot to capture old status!
                }

                @Override
                protected void doUndo() {
                    // Can't undo - we don't know the previous status!
                    throw new UnsupportedOperationException("Undo not supported");
                }

                @Override
                public String getDescription() {
                    return "Bad command";
                }
            }

            // GOOD: Properly stores previous state
            // See UpdateDealStatusCommand for correct implementation
        }

        /**
         * PITFALL 2: Executing Commands Without Invoker
         * ==============================================
         *
         * Problem: Calling command.execute() directly bypasses history tracking.
         *
         * Solution: Always use the invoker to execute commands.
         */
        public static void pitfallBypassingInvoker(DealManager dealManager) {
            Command command = new UpdateDealStatusCommand(
                    dealManager,
                    "DEAL-001",
                    DealStatus.WON
            );

            // BAD: Direct execution (no history, no undo)
            command.execute();

            // GOOD: Execute through invoker
            CommandInvoker invoker = new CommandInvoker();
            invoker.executeCommand(command);
        }

        /**
         * PITFALL 3: Modifying Command After Execution
         * =============================================
         *
         * Problem: Changing command state after execution corrupts undo state.
         *
         * Solution: Make commands immutable after construction.
         * Use final fields and don't provide setters.
         */
        public static void pitfallMutableCommands() {
            // Commands should be immutable after construction
            // All parameters should be final
            // No setters should be provided
            // See our command implementations for examples
        }

        /**
         * PITFALL 4: Not Handling Command Execution Failures
         * ===================================================
         *
         * Problem: Partial execution leaves system in inconsistent state.
         *
         * Solution: Use try-catch in execute(), and don't add to history if execution fails.
         */
        public static void pitfallNotHandlingFailures() {
            // The invoker handles this correctly - see executeCommand() implementation
            // Commands should throw exceptions on failure
            // Invoker should not add failed commands to history
        }
    }

    /**
     * REAL-WORLD APPLICATION SCENARIOS
     * =================================
     */
    public static class RealWorldScenarios {

        /**
         * SCENARIO 1: Text Editor Undo/Redo
         * ==================================
         *
         * Commands: InsertTextCommand, DeleteTextCommand, ReplaceTextCommand
         * Benefit: Full undo/redo of all editing operations
         */
        public static void textEditorScenario() {
            LOGGER.info("TEXT EDITOR: Commands represent editing operations");
            LOGGER.info("- User types: InsertTextCommand");
            LOGGER.info("- User deletes: DeleteTextCommand");
            LOGGER.info("- User presses Undo: Invoker.undo()");
            LOGGER.info("- User presses Redo: Invoker.redo()");
        }

        /**
         * SCENARIO 2: Financial Transactions
         * ===================================
         *
         * Commands: DebitAccountCommand, CreditAccountCommand, TransferCommand
         * Benefit: Atomic operations, audit trail, ability to reverse transactions
         */
        public static void financialTransactionsScenario() {
            LOGGER.info("FINANCIAL: Commands represent transactions");
            LOGGER.info("- Each transaction is a command");
            LOGGER.info("- Command history provides audit trail");
            LOGGER.info("- Undo enables transaction reversal");
            LOGGER.info("- Macro commands enable complex multi-account operations");
        }

        /**
         * SCENARIO 3: Database Migrations
         * ================================
         *
         * Commands: CreateTableCommand, AddColumnCommand, CreateIndexCommand
         * Benefit: Repeatable migrations, rollback capability
         */
        public static void databaseMigrationScenario() {
            LOGGER.info("DATABASE: Commands represent schema changes");
            LOGGER.info("- Each migration is a command");
            LOGGER.info("- Execute() applies migration");
            LOGGER.info("- Undo() rolls back migration");
            LOGGER.info("- Macro commands enable complex multi-step migrations");
        }
    }

    /**
     * Helper method to create a sample deal.
     */
    private static Deal createSampleDeal(String id, String title, String value, String salesRepId) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle(title);
        deal.setValue(new BigDecimal(value));
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(salesRepId);
        return deal;
    }

    /**
     * Helper method to execute a command with logging.
     */
    private static void executeWithLogging(CommandInvoker invoker, Command command) {
        LOGGER.info("Executing command via helper method: " + command.getDescription());
        invoker.executeCommand(command);
    }

    /**
     * Factory method to create deal update commands.
     */
    private static Command createDealUpdateCommand(DealManager dealManager, String dealId, BigDecimal newValue) {
        return new UpdateDealValueCommand(dealManager, dealId, newValue);
    }

    /**
     * USAGE SUMMARY
     * =============
     *
     * This file demonstrated:
     *
     * 1. BASIC USAGE:
     *    - Creating receivers and invokers
     *    - Creating and executing commands
     *    - Using undo and redo
     *    - Working with macro commands
     *
     * 2. ADVANCED CONCEPTS:
     *    - Command queuing for batch execution
     *    - Commands as first-class objects
     *    - Command factories
     *    - Validation and logging wrappers
     *
     * 3. BEST PRACTICES:
     *    - Factory methods for common commands
     *    - Command validation before execution
     *    - Transactional command execution
     *    - Detailed logging
     *    - History size limits
     *
     * 4. COMMON PITFALLS:
     *    - Not storing state for undo
     *    - Bypassing the invoker
     *    - Mutable commands
     *    - Not handling execution failures
     *
     * 5. REAL-WORLD SCENARIOS:
     *    - Text editor undo/redo
     *    - Financial transactions
     *    - Database migrations
     *
     * KEY TAKEAWAYS:
     * - Commands encapsulate operations as objects
     * - Invoker executes commands and manages history
     * - Undo/redo comes "for free" with proper design
     * - Macro commands enable complex atomic operations
     * - Commands can be queued, logged, and serialized
     * - Pattern enables separation of request and execution
     * - Always execute commands through the invoker
     * - Always store previous state for undo
     */
}