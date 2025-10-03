# Command Pattern Implementation

## Overview

This directory contains a comprehensive implementation of the **Command Pattern** applied to the commission calculator domain. The Command Pattern is a behavioral design pattern that encapsulates a request as an object, thereby enabling parameterization of clients with different requests, queuing of requests, logging of operations, and support for undoable operations.

## Pattern Intent

**Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.**

## Key Benefits

1. **Undo/Redo Functionality**: Commands store previous state, enabling full undo/redo capabilities
2. **Request Parameterization**: Operations can be created as objects and passed around
3. **Command Queuing**: Commands can be queued for batch or deferred execution
4. **Audit Logging**: Complete history of all operations is automatically maintained
5. **Macro Commands**: Complex operations can be composed from simpler commands
6. **Decoupling**: Invoker is decoupled from the objects that perform operations
7. **Extensibility**: New commands can be added without modifying existing code

## Files in This Directory

### 1. CommandStructure.java
Defines the fundamental structure and interfaces of the Command Pattern:

- **Command Interface**: Core abstraction with `execute()`, `undo()`, and `getDescription()` methods
- **DealReceiver Interface**: Defines operations that can be performed on deals
- **CommandInvoker Class**: Executes commands and manages undo/redo history
- **AbstractCommand Class**: Optional base class providing common command functionality

**Key Concepts Demonstrated:**
- Command as first-class objects
- Undo/redo infrastructure with stacks
- Template Method pattern for command lifecycle
- Separation of request from execution

### 2. CommandImplementation.java
Provides concrete implementations of commands and receiver:

- **DealManager**: Concrete receiver containing business logic for deal operations
- **UpdateDealStatusCommand**: Command for changing deal status (with undo)
- **UpdateDealValueCommand**: Command for changing deal value (with undo)
- **CreateDealCommand**: Command for creating deals (undo via deletion)
- **MacroDealCommand**: Composite command for executing multiple commands atomically

**Key Concepts Demonstrated:**
- Storing previous state for undo
- Compensating operations
- Composite Pattern integration (macro commands)
- Transactional semantics (all-or-nothing execution)

### 3. CommandUsage.java
Demonstrates practical usage of the Command Pattern:

- Complete workflow from setup to execution
- Undo and redo operations
- Macro command creation and execution
- Command queuing for batch processing
- Commands as first-class objects
- Best practices and common pitfalls
- Real-world application scenarios

**Key Concepts Demonstrated:**
- Command lifecycle management
- History tracking
- Factory methods for commands
- Validation and logging patterns
- Real-world use cases

### 4. command-pattern.puml
PlantUML diagram showing the structure of the implemented Command Pattern, including all classes, interfaces, and their relationships with pattern component annotations.

## Pattern Participants

### 1. Command (Interface)
- Declares interface for executing operations
- Provides `execute()` method for performing the operation
- Provides `undo()` method for reversing the operation
- Provides `getDescription()` for metadata

### 2. ConcreteCommand (UpdateDealStatusCommand, UpdateDealValueCommand, etc.)
- Implements the Command interface
- Defines binding between Receiver and action
- Stores parameters and previous state for undo
- Implements `execute()` by delegating to Receiver

### 3. Receiver (DealManager)
- Knows how to perform the actual business operations
- Contains the domain logic
- Returns previous state to enable undo

### 4. Invoker (CommandInvoker)
- Asks the command to carry out a request
- Maintains command history for undo/redo
- Doesn't know about concrete command types

### 5. Client (Usage code)
- Creates ConcreteCommand objects
- Sets the receiver
- Passes commands to the Invoker

## How It Works

### Execution Flow

```
1. Client creates a Command with parameters and Receiver
2. Client passes Command to Invoker
3. Invoker calls Command.execute()
4. Command delegates to Receiver to perform actual work
5. Command stores previous state for undo
6. Invoker adds Command to history
```

### Undo Flow

```
1. Client calls Invoker.undo()
2. Invoker pops most recent Command from history
3. Invoker calls Command.undo()
4. Command restores previous state via Receiver
5. Invoker moves Command to redo stack
```

### Redo Flow

```
1. Client calls Invoker.redo()
2. Invoker pops Command from redo stack
3. Invoker calls Command.execute() again
4. Command re-performs the operation
5. Invoker moves Command back to history
```

## Usage Example

```java
// Create receiver (business logic)
DealManager dealManager = new DealManager();

// Create invoker (command executor)
CommandInvoker invoker = new CommandInvoker();

// Create and execute a command
Deal deal = new Deal();
deal.setId("DEAL-001");
deal.setValue(new BigDecimal("100000"));

Command createCommand = new CreateDealCommand(dealManager, deal);
invoker.executeCommand(createCommand);

// Update the deal value
Command updateCommand = new UpdateDealValueCommand(
    dealManager,
    "DEAL-001",
    new BigDecimal("125000")
);
invoker.executeCommand(updateCommand);

// Undo the value update
invoker.undo();  // Value returns to $100,000

// Redo the value update
invoker.redo();  // Value goes back to $125,000

// Create a macro command
MacroDealCommand macro = new MacroDealCommand("Close Deal");
macro.addCommand(new UpdateDealValueCommand(dealManager, "DEAL-001", new BigDecimal("150000")));
macro.addCommand(new UpdateDealStatusCommand(dealManager, "DEAL-001", DealStatus.WON));

// Execute multiple commands as one atomic operation
invoker.executeCommand(macro);

// Undo both operations with a single undo
invoker.undo();
```

## Command Types Implemented

### 1. Update Commands
- **UpdateDealStatusCommand**: Changes deal status
- **UpdateDealValueCommand**: Changes deal value
- Store previous values for precise undo
- Most common type of command

### 2. Creation Commands
- **CreateDealCommand**: Creates new deals
- Undo by deleting the created object
- No previous state to store

### 3. Composite Commands
- **MacroDealCommand**: Executes multiple commands as one unit
- Provides transactional semantics (all-or-nothing)
- Undo cascades to all sub-commands in reverse order
- Enables complex multi-step operations

## Design Decisions

### 1. AbstractCommand Base Class
- Provides template method pattern for execute/undo
- Enforces execution state tracking
- Adds automatic timestamp tracking
- Centralizes common logging

### 2. Stack-Based History
- Uses Stack for undo history (LIFO)
- Uses separate Stack for redo
- Redo cleared when new command executes
- Prevents inconsistent state

### 3. Previous State Storage
- Commands capture previous state during execute()
- Not before execute() (to get actual current state)
- Enables precise undo operations
- Returned by receiver methods

### 4. Compensating Operations
- Undo implemented as reverse operation
- Simpler than storing complete object state
- More maintainable for complex objects
- Requires careful implementation

## Real-World Applications

### 1. Text Editors
- Each edit operation is a command
- Full undo/redo of editing history
- Examples: Insert, Delete, Replace, Format

### 2. Financial Systems
- Each transaction is a command
- Audit trail of all operations
- Ability to reverse transactions
- Macro commands for complex transfers

### 3. Database Migrations
- Each schema change is a command
- Execute() applies migration
- Undo() rolls back migration
- Repeatable and reversible

### 4. Game Development
- Player actions as commands
- Replay functionality
- Network synchronization
- Save/load game state

### 5. Workflow Systems
- Each workflow step is a command
- Queue commands for execution
- Pause/resume workflows
- Rollback failed workflows

## Best Practices

1. **Always Use Invoker**: Don't call `command.execute()` directly - always use invoker to maintain history

2. **Immutable Commands**: Make command fields final and don't provide setters after construction

3. **Store Previous State**: Capture previous state in `execute()`, not in constructor

4. **Handle Failures**: Throw exceptions on execution failure; invoker won't add failed commands to history

5. **Limit History Size**: In production, limit history to prevent memory issues (e.g., max 100 commands)

6. **Descriptive Names**: Provide clear descriptions for UI display and debugging

7. **Validate Parameters**: Validate command parameters in constructor or before execution

8. **Use Factories**: Create factory methods for commonly-used commands

## Common Pitfalls to Avoid

1. ❌ **Not Storing State for Undo**: Always capture previous state before making changes
2. ❌ **Bypassing Invoker**: Don't execute commands directly
3. ❌ **Mutable Commands**: Don't allow command state to change after construction
4. ❌ **Ignoring Execution Failures**: Always handle and propagate exceptions
5. ❌ **Unlimited History**: Can cause memory leaks; implement size limits
6. ❌ **Circular Dependencies**: Commands shouldn't modify other commands

## Testing Considerations

Commands are highly testable:

```java
@Test
void testCommandExecutionAndUndo() {
    DealManager manager = new DealManager();
    Deal deal = createDeal("DEAL-001", "100000");
    manager.createDeal(deal);

    // Test execution
    Command cmd = new UpdateDealValueCommand(
        manager, "DEAL-001", new BigDecimal("125000")
    );
    cmd.execute();

    assertEquals(new BigDecimal("125000"), manager.getDeal("DEAL-001").getValue());

    // Test undo
    cmd.undo();
    assertEquals(new BigDecimal("100000"), manager.getDeal("DEAL-001").getValue());
}
```

## Relationship to Other Patterns

- **Composite Pattern**: Macro commands use Composite to group commands
- **Memento Pattern**: Alternative approach to storing state for undo
- **Strategy Pattern**: Commands can use Strategy for different execution approaches
- **Template Method**: AbstractCommand uses Template Method for lifecycle management
- **Chain of Responsibility**: Commands can be chained for sequential processing

## SOLID Principles Demonstrated

- **Single Responsibility**: Each command does one thing; receiver handles business logic
- **Open/Closed**: New commands can be added without modifying existing code
- **Liskov Substitution**: All commands can be used polymorphically via Command interface
- **Interface Segregation**: Command interface is focused and minimal
- **Dependency Inversion**: Invoker depends on Command abstraction, not concrete commands

## Performance Considerations

- **Memory**: Each command stores state; limit history size in production
- **Execution Speed**: Minimal overhead; delegation is fast
- **Undo Speed**: O(1) for single undo; O(n) for undo-all
- **History Search**: O(n) if searching history; consider indexing for large histories

## Extension Points

1. **Serialization**: Implement Serializable to save commands to disk
2. **Remote Execution**: Send commands over network for distributed systems
3. **Command Scheduling**: Add timestamp and executor service for delayed execution
4. **Priority Queue**: Execute commands based on priority
5. **Conditional Execution**: Add `canExecute()` method for pre-execution validation
6. **Progress Tracking**: Add progress callbacks for long-running commands

## Conclusion

The Command Pattern is a powerful tool for building flexible, maintainable systems with undo/redo capabilities. This implementation demonstrates all the key concepts and provides a solid foundation for extending the pattern to solve real-world problems in the commission calculator domain and beyond.

The pattern's strength lies in its simplicity and flexibility - encapsulating operations as objects opens up numerous possibilities for command queuing, logging, undo/redo, macros, and more, all while maintaining clean separation of concerns and adhering to SOLID principles.