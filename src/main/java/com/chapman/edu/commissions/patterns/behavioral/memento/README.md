# Memento Pattern Implementation

## Overview

The **Memento Pattern** is a behavioral design pattern that captures and externalizes an object's internal state without violating encapsulation, so the object can be restored to this state later. It provides the ability to implement undo/redo functionality, versioning, and transactional operations.

This implementation demonstrates the Memento pattern through **Commission Plan Versioning** and **Deal Transaction Management**, showing how to save snapshots, restore previous states, and manage version history without exposing internal implementation details.

## Pattern Components

### 1. Memento (`CommissionPlanMemento`)
Stores the internal state snapshot of the originator:
```java
class CommissionPlanMemento {
    private final String planId;
    private final String planName;
    private final PlanStatus status;
    private final List<CommissionRule> rules;  // Deep copy
    private final LocalDateTime timestamp;
    private final String label;

    // Only originator can create and read
}
```

### 2. Originator (`CommissionPlanEditor`)
Creates and uses mementos:
- Creates mementos containing snapshots of its state
- Restores its state from mementos
- Manages the actual business state

### 3. Caretaker (`VersionHistoryManager`)
Manages mementos:
- Stores collection of mementos
- Provides undo/redo functionality
- Never examines or modifies memento contents
- Manages history limits and cleanup

## Problem Solved

### Without Memento ❌

```java
// Exposing internal state for undo
public class CommissionPlan {
    // All fields must be public or have getters/setters
    public String planName;
    public List<CommissionRule> rules;
    public PlanStatus status;

    // Client must know internal structure
    public void saveState(CommissionPlanSnapshot snapshot) {
        snapshot.planName = this.planName;
        snapshot.rules = new ArrayList<>(this.rules);
        snapshot.status = this.status;
        // Exposes internal implementation!
    }

    public void restoreState(CommissionPlanSnapshot snapshot) {
        this.planName = snapshot.planName;
        this.rules = new ArrayList<>(snapshot.rules);
        this.status = snapshot.status;
    }
}

// Client code knows too much about internals
class UndoManager {
    public void undo(CommissionPlan plan, CommissionPlanSnapshot snapshot) {
        // Must understand plan's internal structure
        plan.planName = snapshot.planName;
        plan.rules = snapshot.rules;
        plan.status = snapshot.status;
        // Violates encapsulation!
    }
}
```

**Problems:**
- 🔴 Violates encapsulation (exposes internal state)
- 🔴 Tight coupling between plan and undo manager
- 🔴 Client must understand internal structure
- 🔴 Hard to change internal representation
- 🔴 No protection against state corruption
- 🔴 Difficult to manage multiple versions

### With Memento ✅

```java
// Encapsulation preserved
public class CommissionPlanEditor {
    private CommissionPlan plan;  // Private!

    // Create opaque memento
    public CommissionPlanMemento save(String label) {
        return new CommissionPlanMemento(plan, label, username);
    }

    // Restore from memento
    public void restore(CommissionPlanMemento memento) {
        // Only originator knows how to restore
        this.plan = memento.reconstructPlan();
    }
}

// Caretaker doesn't know internals
class VersionHistoryManager {
    private List<CommissionPlanMemento> history;

    public void checkpoint(String label) {
        history.add(editor.save(label));  // Opaque!
    }

    public void undo() {
        editor.restore(history.get(--currentIndex));  // Don't examine!
    }
}
```

**Benefits:**
- ✅ Encapsulation preserved (state is private)
- ✅ Loose coupling (caretaker doesn't know internals)
- ✅ Easy to change internal representation
- ✅ Protected state (immutable mementos)
- ✅ Complete version history management
- ✅ Undo/redo functionality

## State Management Comparison

```
Without Memento (Exposed State):
┌─────────────┐     Accesses      ┌─────────────┐
│   Client    │──────────────────▶│    Plan     │
│ (Undo Mgr)  │  Internal State   │ (Exposed)   │
└─────────────┘                   └─────────────┘
                                        ▲
                                        │ Knows internals
                                        │ Tight coupling

With Memento (Encapsulated):
┌─────────────┐                   ┌─────────────┐
│ Caretaker   │    save()/        │   Editor    │
│ (History)   │◀──────────────────│ (Originator)│
└──────┬──────┘    restore()      └─────────────┘
       │                                 │
       │ stores                          │ creates/reads
       │                                 │
       ▼                                 ▼
┌─────────────┐                   ┌─────────────┐
│  Memento    │                   │  Memento    │
│  (Opaque)   │                   │ (Private)   │
└─────────────┘                   └─────────────┘

Caretaker never examines memento contents!
Only originator can create/read mementos.
```

## File Structure

```
memento/
├── MementoStructure.java            # Generic Memento pattern structure
├── MementoImplementation.java       # Commission plan versioning implementation
├── MementoUsage.java                # Comprehensive usage examples
├── memento-pattern.puml             # UML class diagram
└── README.md                        # This file
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.memento.MementoStructure"
```

**Output shows:**
- Basic save/restore operations
- Restore to specific checkpoint
- Complex multi-field state management
- Caretaker managing history

### Run Commission Plan Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.memento.MementoImplementation"
```

**Output shows:**
- Complete plan versioning workflow
- Undo/redo with multiple checkpoints
- Version comparison
- Deal transaction management with commit/rollback

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.memento.MementoUsage"
```

**Output shows:**
- 6 detailed scenarios
- Basic undo/redo
- Named checkpoints
- Draft editing with discard
- Version comparison
- Transactional editing
- Auto-save and crash recovery

## Usage Examples

### Example 1: Basic Save and Restore

```java
// Create plan and editor
CommissionPlan plan = new CommissionPlan();
plan.setPlanName("Q1 Sales Plan");
CommissionPlanEditor editor = new CommissionPlanEditor(plan, "john");

// Save initial state
CommissionPlanMemento snapshot = editor.save("Initial version");

// Make changes
editor.setPlanName("Q1 Updated Plan");
editor.setStatus(PlanStatus.ACTIVE);

// Restore to saved state
editor.restore(snapshot);
// Plan is back to "Q1 Sales Plan" with DRAFT status
```

### Example 2: Undo/Redo with History Manager

```java
CommissionPlanEditor editor = new CommissionPlanEditor(plan, "alice");
VersionHistoryManager history = new VersionHistoryManager(editor);

// Save initial state
history.checkpoint("Initial");

// Make changes
editor.setPlanName("Version 2");
history.checkpoint("Version 2");

editor.setPlanName("Version 3");
history.checkpoint("Version 3");

// Undo
history.undo();  // Back to Version 2
history.undo();  // Back to Initial

// Redo
history.redo();  // Forward to Version 2
```

### Example 3: Named Checkpoints

```java
VersionHistoryManager history = new VersionHistoryManager(editor);

// Create named checkpoints at important stages
history.checkpoint("DRAFT_V1 - Initial draft");
// ... edit ...
history.checkpoint("RULES_COMPLETE - All rules added");
// ... edit ...
history.checkpoint("REVIEW_READY - Ready for manager review");
// ... edit ...
history.checkpoint("ACTIVATED - Plan now active");

// Restore to specific checkpoint by name
history.restoreCheckpoint("RULES_COMPLETE - All rules added");
```

### Example 4: Transactional Editing

```java
DealTransactionManager txManager = new DealTransactionManager(deal);

// Begin transaction
txManager.beginTransaction();

// Make changes
txManager.setTitle("New Title");
txManager.setValue(new BigDecimal("100000"));
txManager.setStatus(DealStatus.WON);

// Decide: commit or rollback
if (changesAreValid()) {
    txManager.commit();  // Keep changes
} else {
    txManager.rollback();  // Discard all changes
}
```

### Example 5: Version Comparison

```java
VersionHistoryManager history = new VersionHistoryManager(editor);

// Create multiple versions
history.checkpoint("V1");
// ... edit ...
history.checkpoint("V2");
// ... edit ...
history.checkpoint("V3");

// Compare versions
history.compareVersions(0, 2);
// Shows differences between V1 and V3
```

### Example 6: Auto-Save for Crash Recovery

```java
VersionHistoryManager history = new VersionHistoryManager(editor);

// Periodically auto-save
Timer autoSaveTimer = new Timer();
autoSaveTimer.schedule(new TimerTask() {
    public void run() {
        history.checkpoint("AUTOSAVE - " + LocalDateTime.now());
    }
}, 0, 60000);  // Every 60 seconds

// After crash, restore from last auto-save
history.showHistory();
history.restoreCheckpoint("AUTOSAVE - ...");
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- **Memento**: Stores state (one responsibility)
- **Originator**: Manages business logic (one responsibility)
- **Caretaker**: Manages history (one responsibility)

### Open/Closed Principle (OCP)
- Can add new types of mementos without modifying caretaker
- Can extend history management without changing originator

### Encapsulation
- Memento hides originator's internal state
- Only originator can read memento contents
- Caretaker treats mementos as opaque objects

### Liskov Substitution Principle (LSP)
- Different memento types can be used interchangeably
- History manager works with any memento implementation

## When to Use Memento

✅ **Use Memento when:**
- Need undo/redo functionality
- Need to save snapshots of object state
- Direct interface to get state would expose implementation
- Want to implement versioning or checkpoints
- Need transactional operations (commit/rollback)
- Building an editor or configuration tool
- Implementing auto-save functionality
- Need audit trail with complete state history

❌ **Don't use Memento when:**
- State is simple and can be easily copied
- Encapsulation is not a concern
- Memory constraints are severe (mementos can be large)
- State changes are infrequent
- Simple property-based undo is sufficient

## Advantages and Disadvantages

### Advantages ✅

1. **Preserves Encapsulation**
   - Internal state not exposed
   - Only originator accesses memento contents
   - Clean separation of concerns

2. **Simplifies Originator**
   - Originator doesn't manage history
   - Caretaker handles storage and retrieval
   - Clean, focused responsibilities

3. **Powerful Undo/Redo**
   - Complete state restoration
   - Navigate through history
   - Jump to specific checkpoints

4. **Safe Experimentation**
   - Make changes without fear
   - Easy to discard and restore
   - Try different approaches

5. **Audit Trail**
   - Complete history of changes
   - Who made changes and when
   - Compare different versions

6. **Transactional Operations**
   - Begin/commit/rollback pattern
   - All-or-nothing semantics
   - Reliable complex operations

### Disadvantages ❌

1. **Memory Overhead**
   - Each memento stores complete state
   - History can grow large
   - Must manage memory limits

2. **Creation Cost**
   - Deep copying can be expensive
   - Frequent snapshots impact performance
   - May need optimization for large objects

3. **Maintenance Complexity**
   - Must keep mementos in sync with originator
   - Changes to originator require memento updates
   - Versioning issues with persistent storage

4. **Caretaker Responsibility**
   - Must decide when to save snapshots
   - Must manage history size
   - Must handle cleanup and limits

## Testing Strategy

### Unit Testing Memento Creation

```java
@Test
void testMementoCreation() {
    CommissionPlan plan = createPlan("Test Plan");
    CommissionPlanEditor editor = new CommissionPlanEditor(plan, "test");

    CommissionPlanMemento memento = editor.save("Test snapshot");

    assertNotNull(memento);
    assertEquals("Test snapshot", memento.getLabel());
    assertNotNull(memento.getTimestamp());
}
```

### Testing State Restoration

```java
@Test
void testStateRestoration() {
    CommissionPlanEditor editor = new CommissionPlanEditor(plan, "test");

    // Save initial state
    editor.setPlanName("Original");
    CommissionPlanMemento snapshot = editor.save("Snapshot");

    // Modify
    editor.setPlanName("Modified");
    assertEquals("Modified", editor.getPlan().getPlanName());

    // Restore
    editor.restore(snapshot);
    assertEquals("Original", editor.getPlan().getPlanName());
}
```

### Testing Undo/Redo

```java
@Test
void testUndoRedo() {
    CommissionPlanEditor editor = new CommissionPlanEditor(plan, "test");
    VersionHistoryManager history = new VersionHistoryManager(editor);

    history.checkpoint("V1");
    editor.setPlanName("V2");
    history.checkpoint("V2");
    editor.setPlanName("V3");
    history.checkpoint("V3");

    // Undo
    assertTrue(history.undo());
    assertEquals("V2", editor.getPlan().getPlanName());

    // Redo
    assertTrue(history.redo());
    assertEquals("V3", editor.getPlan().getPlanName());
}
```

### Testing Memento Immutability

```java
@Test
void testMementoImmutability() {
    CommissionPlan plan = createPlan("Test");
    plan.addRule(new CommissionRule("Rule 1"));

    CommissionPlanMemento memento =
        new CommissionPlanMemento(plan, "Test", "user");

    // Modify original after memento creation
    plan.addRule(new CommissionRule("Rule 2"));

    // Memento should still have only 1 rule (deep copy)
    assertEquals(1, memento.getRules().size());
    assertEquals(2, plan.getRules().size());
}
```

### Testing Transaction Commit/Rollback

```java
@Test
void testTransactionCommit() {
    Deal deal = new Deal("Test", new BigDecimal("1000"), "REP");
    DealTransactionManager tx = new DealTransactionManager(deal);

    tx.beginTransaction();
    tx.setTitle("Modified");
    tx.commit();

    assertEquals("Modified", deal.getTitle());
}

@Test
void testTransactionRollback() {
    Deal deal = new Deal("Original", new BigDecimal("1000"), "REP");
    DealTransactionManager tx = new DealTransactionManager(deal);

    tx.beginTransaction();
    tx.setTitle("Modified");
    tx.rollback();

    assertEquals("Original", deal.getTitle());
}
```

## Real-World Applications

The Memento pattern is useful for:

1. **Text Editors**
   - Undo/redo typing operations
   - Multiple cursor positions
   - Document versions

2. **Graphics/Drawing Applications**
   - Undo drawing operations
   - Layer snapshots
   - Canvas history

3. **Configuration Management**
   - Rollback configuration changes
   - Version control for settings
   - A/B testing configurations

4. **Database Transactions**
   - Savepoints and rollback
   - Transaction isolation
   - Snapshot isolation

5. **Game Development**
   - Save/load game state
   - Replay functionality
   - Checkpoint system

6. **Form Editors**
   - Discard changes
   - Draft saving
   - Version comparison

7. **Workflow Systems**
   - Process versioning
   - Rollback to previous stage
   - Audit trail

## Common Pitfalls to Avoid

### ❌ Don't: Let Caretaker Examine Memento Contents

```java
// BAD - violates encapsulation
public class BadCaretaker {
    public void checkMemento(CommissionPlanMemento memento) {
        // Examining memento contents!
        if (memento.getRules().size() > 10) {
            System.out.println("Too many rules!");
        }
    }
}
```

### ✅ Do: Treat Mementos as Opaque

```java
// GOOD - doesn't examine contents
public class GoodCaretaker {
    private List<CommissionPlanMemento> history;

    public void save(CommissionPlanMemento memento) {
        history.add(memento);  // Just store it
    }

    public CommissionPlanMemento restore(int index) {
        return history.get(index);  // Just retrieve it
    }
}
```

### ❌ Don't: Forget Deep Copy in Memento

```java
// BAD - shallow copy
public class BadMemento {
    private List<CommissionRule> rules;

    public BadMemento(CommissionPlan plan) {
        this.rules = plan.getRules();  // Shallow copy!
    }
}
// Changes to original affect memento!
```

### ✅ Do: Always Deep Copy Mutable State

```java
// GOOD - deep copy
public class GoodMemento {
    private final List<CommissionRule> rules;

    public GoodMemento(CommissionPlan plan) {
        this.rules = new ArrayList<>(plan.getRules());  // Deep copy
    }
}
// Memento is independent of original
```

### ❌ Don't: Ignore Memory Limits

```java
// BAD - unlimited history
public class UnlimitedHistory {
    private List<Memento> history = new ArrayList<>();

    public void save(Memento m) {
        history.add(m);  // Grows forever!
    }
}
```

### ✅ Do: Enforce History Limits

```java
// GOOD - limited history
public class LimitedHistory {
    private List<Memento> history;
    private int maxSize = 50;

    public void save(Memento m) {
        history.add(m);
        if (history.size() > maxSize) {
            history.remove(0);  // Remove oldest
        }
    }
}
```

## Memento vs Other Patterns

### Memento vs Command
- **Command**: Encapsulates operations (behavior)
- **Memento**: Encapsulates state (data)
- Often used together: Command stores mementos for undo
- **Use Command when**: Need to undo operations
- **Use Memento when**: Need to restore state

### Memento vs Prototype
- **Prototype**: Clones objects for creation
- **Memento**: Captures state for restoration
- **Prototype**: Any code can clone
- **Memento**: Only originator accesses state

### Memento vs Serialization
- **Serialization**: Converts object to bytes
- **Memento**: Captures logical state
- **Memento**: More control over what's saved
- **Memento**: Preserves encapsulation better

## Related Patterns

- **Command Pattern**: Often combined with Memento for undo
- **Iterator Pattern**: Can use mementos to capture iteration state
- **Prototype Pattern**: Similar cloning concept, different purpose

## Optimization Strategies

### 1. Incremental Mementos
Store only changes (deltas) instead of full state:
```java
public class DeltaMemento {
    private Map<String, Object> changedFields;  // Only changed fields
}
```

### 2. Lazy Copying
Copy state only when first modification occurs:
```java
public class LazyMemento {
    private CommissionPlan snapshot;  // Created on first change
}
```

### 3. Compression
Compress memento data for long-term storage:
```java
public class CompressedMemento {
    private byte[] compressedData;  // Gzip compressed state
}
```

### 4. History Pruning
Keep only strategic checkpoints:
```java
public class PrunedHistory {
    // Keep: recent changes, major milestones, every Nth version
}
```

## Further Learning

To deepen understanding:

1. Run all three demo files and study the output
2. Implement incremental mementos (store only deltas)
3. Add memento serialization for persistent storage
4. Implement redo stack pruning (discard forward history on new changes)
5. Create compressed mementos for large objects
6. Add memento encryption for sensitive state
7. Implement memento pooling to reduce memory allocations
8. Create memento diff tool to visualize changes

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 283-291)
- **Head First Design Patterns** - Freeman & Freeman
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler
- **Pattern-Oriented Software Architecture** - Buschmann et al.