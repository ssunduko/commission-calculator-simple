# State Pattern Implementation

## Overview

The **State Pattern** is a behavioral design pattern that allows an object to alter its behavior when its internal state changes. The object will appear to change its class.

This implementation demonstrates the State pattern through a **Commission Dispute Resolution** workflow system, showing how complex state-dependent behavior can be elegantly managed without massive conditional statements.

## Pattern Components

### 1. State Interface (`DisputeState`)
- Defines the contract for all concrete states
- Declares methods for all possible actions in the dispute lifecycle
- Each state implements these methods according to its specific rules

### 2. Concrete States
Five states representing the dispute lifecycle:

| State | Description | Available Actions |
|-------|-------------|-------------------|
| **SubmittedState** | Initial state when dispute is created | addComment, assignReviewer, escalate |
| **UnderReviewState** | Dispute assigned to reviewer | addComment, assignReviewer, approve, reject, escalate |
| **EscalatedState** | Requires management attention | addComment, approve, reject, assignReviewer (returns to review) |
| **ResolvedState** | Decision made (approved/rejected) | addComment, escalate (challenge), close |
| **ClosedState** | Terminal state - no more changes | [None - read-only] |

### 3. Context (`DisputeContext`)
- Maintains reference to current state
- Delegates all requests to the current state object
- Contains dispute data (ID, amounts, comments, etc.)
- Allows states to transition to other states

## Problem Solved

### Without State Pattern ❌

```java
public void processAction(String action) {
    if (status == Status.SUBMITTED) {
        if (action.equals("approve")) {
            System.out.println("Error: Cannot approve in SUBMITTED state");
        } else if (action.equals("assignReviewer")) {
            status = Status.UNDER_REVIEW;
            // ... approval logic
        } else if (action.equals("escalate")) {
            status = Status.ESCALATED;
            // ... escalation logic
        }
    } else if (status == Status.UNDER_REVIEW) {
        if (action.equals("approve")) {
            status = Status.RESOLVED;
            // ... approval logic
        } else if (action.equals("reject")) {
            status = Status.RESOLVED;
            // ... rejection logic
        }
        // ... more conditions
    } else if (status == Status.ESCALATED) {
        // ... more nested conditions
    }
    // ... continues with massive if/else chains
}
```

**Problems:**
- 🔴 Huge conditional statements that are hard to maintain
- 🔴 Business logic scattered across multiple conditions
- 🔴 Difficult to add new states without breaking existing code
- 🔴 Testing requires covering all possible state/action combinations
- 🔴 Hard to understand state transition rules

### With State Pattern ✅

```java
public void approve(BigDecimal amount) {
    currentState.approve(this, amount);  // Delegate to current state
}
```

**Benefits:**
- ✅ Each state encapsulates its own behavior
- ✅ Adding new states doesn't modify existing states
- ✅ State transitions are explicit and clear
- ✅ Each state is independently testable
- ✅ Follows Open/Closed Principle

## File Structure

```
state/
├── StateStructure.java          # Generic State pattern structure
├── StateImplementation.java     # Commission dispute implementation
├── StateUsage.java              # Comprehensive usage examples
├── state-pattern.puml           # UML class diagram
└── README.md                    # This file
```

## Key Concepts Demonstrated

### 1. State Encapsulation
Each state class encapsulates:
- Valid actions for that state
- Business rules specific to that state
- Valid state transitions from that state

### 2. Context Delegation
The `DisputeContext` doesn't know which concrete state it has:
```java
// Context just delegates - doesn't care about the specific state
public void approve(BigDecimal amount) {
    currentState.approve(this, amount);
}
```

### 3. State Transitions
States manage their own transitions:
```java
public void approve(DisputeContext context, BigDecimal adjustedAmount) {
    // Do state-specific work
    context.setAdjustedAmount(adjustedAmount);

    // Transition to next state
    context.setState(new ResolvedState());
}
```

### 4. Invalid Operation Prevention
Invalid operations are handled gracefully by each state:
```java
// In ClosedState - all modifications are rejected
public void addComment(DisputeContext context, String comment, String author) {
    System.out.println("✗ Cannot add comments - dispute is closed");
}
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.state.StateStructure"
```

**Output shows:**
- Basic state machine with 3 states (A → B → C → A)
- Simple state transitions
- Context delegation

### Run Commission Dispute Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.state.StateImplementation"
```

**Output shows:**
- Complete dispute lifecycle scenarios
- Normal approval and escalation workflows
- Comment history tracking

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.state.StateUsage"
```

**Output shows:**
- 6 detailed scenarios covering different workflows
- Edge cases and invalid operation handling
- State transition validation patterns

## Usage Examples

### Example 1: Happy Path - Simple Approval

```java
// Create a dispute (starts in SUBMITTED state)
DisputeContext dispute = new DisputeContext(
    "DISP-2024-001",
    "John Smith",
    new BigDecimal("5000.00")
);

// Add initial comment
dispute.addComment("Commission calculation error", "John Smith");

// Assign reviewer (transitions to UNDER_REVIEW)
dispute.assignReviewer("Jane Reviewer");

// Approve with adjusted amount (transitions to RESOLVED)
dispute.approve(new BigDecimal("5500.00"));

// Close the dispute (transitions to CLOSED)
dispute.close();
```

### Example 2: Escalation Workflow

```java
DisputeContext dispute = new DisputeContext(
    "DISP-2024-002",
    "Sarah Johnson",
    new BigDecimal("10000.00")
);

dispute.assignReviewer("Bob Reviewer");

// Complex case - escalate to management
dispute.escalate("High value - requires management approval");

// Management reviews and approves
dispute.approve(new BigDecimal("11000.00"));

dispute.close();
```

### Example 3: Invalid Operations

```java
DisputeContext dispute = new DisputeContext(
    "DISP-2024-003",
    "Mike Anderson",
    new BigDecimal("3000.00")
);

// Try to approve without assigning reviewer - REJECTED
dispute.approve(new BigDecimal("3500.00"));
// Output: "✗ Cannot approve - dispute must be assigned first"

// Correct workflow
dispute.assignReviewer("Linda Chen");
dispute.approve(new BigDecimal("3500.00"));  // Now it works!
```

## State Transition Diagram

```
┌─────────────┐
│  SUBMITTED  │ ────┐
└─────────────┘     │
       │            │ escalate()
       │ assignReviewer()
       ↓            ↓
┌─────────────┐   ┌─────────────┐
│UNDER_REVIEW │   │ ESCALATED   │
└─────────────┘   └─────────────┘
       │                 │
       │ approve()/      │ approve()/
       │ reject()        │ reject()
       ↓                 ↓
┌─────────────┐ ←────────┘
│  RESOLVED   │
└─────────────┘
       │          ↑
       │          │ escalate() (re-escalate)
       │          │
       │ close()  │
       ↓          │
┌─────────────┐ ─┘
│   CLOSED    │
└─────────────┘
  [Terminal]
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- Each state class has one responsibility: manage behavior for that specific state
- Context class responsibility: maintain state reference and delegate requests

### Open/Closed Principle (OCP)
- Open for extension: Add new states without modifying existing states
- Closed for modification: Existing states remain unchanged

### Dependency Inversion Principle (DIP)
- Context depends on `DisputeState` interface, not concrete states
- High-level policy (state transitions) doesn't depend on low-level details

### Liskov Substitution Principle (LSP)
- Any concrete state can be substituted for another
- Context works with any state that implements the interface

## Testing Strategy

### Unit Testing Individual States
```java
@Test
void testSubmittedStateTransition() {
    DisputeContext context = new DisputeContext("TEST-001", "John", new BigDecimal("1000"));
    assertEquals("SUBMITTED", context.getStateName());

    context.assignReviewer("Jane");
    assertEquals("UNDER_REVIEW", context.getStateName());
}
```

### Integration Testing Workflows
```java
@Test
void testCompleteApprovalWorkflow() {
    DisputeContext context = createDispute();
    context.assignReviewer("Reviewer");
    context.approve(new BigDecimal("1500"));
    context.close();

    assertEquals("CLOSED", context.getStateName());
}
```

### Testing Invalid Operations
```java
@Test
void testInvalidOperationsPrevented() {
    DisputeContext context = createDispute();

    // Should not throw exception, just log error
    context.approve(new BigDecimal("1500")); // Invalid in SUBMITTED state

    // Should still be in SUBMITTED state
    assertEquals("SUBMITTED", context.getStateName());
}
```

## Real-World Applications

The State pattern is useful for:

1. **Order Processing Systems**
   - States: Pending → Processing → Shipped → Delivered → Completed

2. **Document Approval Workflows**
   - States: Draft → Submitted → Under Review → Approved/Rejected → Published

3. **Connection Management**
   - States: Disconnected → Connecting → Connected → Disconnecting

4. **Game Character States**
   - States: Standing → Walking → Running → Jumping → Falling

5. **Ticket/Issue Tracking**
   - States: Open → In Progress → Review → Resolved → Closed

## Common Pitfalls to Avoid

### ❌ Don't: Let Context know about concrete states
```java
// BAD - Context shouldn't know about concrete states
if (currentState instanceof SubmittedState) {
    // Do something specific
}
```

### ✅ Do: Use polymorphism
```java
// GOOD - Let the state handle it
currentState.handle(this);
```

### ❌ Don't: Put business logic in Context
```java
// BAD - Context doing state-specific work
public void approve(BigDecimal amount) {
    if (currentState.getStateName().equals("UNDER_REVIEW")) {
        this.adjustedAmount = amount;
        currentState = new ResolvedState();
    }
}
```

### ✅ Do: Delegate to states
```java
// GOOD - Delegate to state
public void approve(BigDecimal amount) {
    currentState.approve(this, amount);
}
```

## When NOT to Use State Pattern

Avoid State pattern when:
- 🚫 You have only 2-3 simple states with minimal behavior differences
- 🚫 State transitions are simple and unlikely to change
- 🚫 States don't have complex business rules
- 🚫 The overhead of multiple classes outweighs the benefits

In these cases, a simple enum or boolean flag may suffice.

## Related Patterns

- **Strategy Pattern**: Similar structure, but Strategy focuses on algorithms while State focuses on object behavior based on state
- **Command Pattern**: Can be used with State to encapsulate state transition actions
- **Observer Pattern**: States can notify observers when transitions occur

## Further Learning

To deepen understanding:
1. Run all three demo files and study the output
2. Modify `StateImplementation.java` to add a new state (e.g., "Under Legal Review")
3. Implement state transition guards (conditions that must be met before transitioning)
4. Add state entry/exit actions
5. Implement state history tracking (memento pattern integration)

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four
- **Head First Design Patterns** - Freeman & Freeman
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler