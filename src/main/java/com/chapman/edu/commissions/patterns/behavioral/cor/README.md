# Chain of Responsibility Pattern Implementation

## Overview

The **Chain of Responsibility Pattern** is a behavioral design pattern that lets you pass requests along a chain of handlers. Upon receiving a request, each handler decides either to process the request or to pass it to the next handler in the chain.

This implementation demonstrates the Chain of Responsibility pattern through **Commission Approval Workflows**, showing how different approval authorities handle commission requests based on amount thresholds and business rules.

## Pattern Components

### 1. Handler Interface (`ApprovalHandler`)
Defines the interface for handling approval requests:
```java
interface ApprovalHandler {
    ApprovalHandler setNext(ApprovalHandler handler);
    void approve(CommissionApprovalRequest request);
    String getHandlerName();
}
```

### 2. Base Handler (`BaseApprovalHandler`)
Provides chain management logic:
- Maintains reference to next handler
- Implements chain traversal
- Provides template for handling decisions

### 3. Concrete Handlers

**Business Logic Handlers** (approval authorities):

| Handler | Authority Level | Amount Range | Responsibility |
|---------|----------------|--------------|----------------|
| **AutoApprovalHandler** | System | < $5,000 | Automatic approval for low-value commissions |
| **SalesManagerApprovalHandler** | Manager | $5,000 - $25,000 | Manager-level approval |
| **RegionalDirectorApprovalHandler** | Director | $25,000 - $100,000 | Director-level approval with documentation check |
| **VPSalesApprovalHandler** | VP | $100,000 - $500,000 | Executive-level review |
| **CFOApprovalHandler** | CFO | ≥ $500,000 | Highest authority for very large commissions |

**Cross-Cutting Concern Handlers** (interceptors):

| Handler | Purpose | When | Continues Chain |
|---------|---------|------|-----------------|
| **ValidationHandler** | Validate business rules | All requests | Yes (if valid) |
| **FraudDetectionHandler** | Check fraud indicators | All requests | Yes (if clean) |
| **AuditLogHandler** | Create audit trail | All requests | Yes (always) |

### 4. Request (`CommissionApprovalRequest`)
Contains:
- Deal information
- Commission amount
- Sales representative details
- Approval history (audit trail)
- Status (approved/rejected/pending)

## Problem Solved

### Without Chain of Responsibility ❌

```java
public class CommissionApprover {
    public void approveCommission(Commission commission) {
        BigDecimal amount = commission.getAmount();

        // Massive if-else chain
        if (!validate(commission)) {
            reject("Validation failed");
        } else if (hasFraudIndicators(commission)) {
            reject("Fraud detected");
        } else if (amount.compareTo(new BigDecimal("5000")) < 0) {
            autoApprove();
        } else if (amount.compareTo(new BigDecimal("25000")) < 0) {
            if (managerApproves()) {
                approve();
            }
        } else if (amount.compareTo(new BigDecimal("100000")) < 0) {
            if (directorApproves()) {
                approve();
            }
        } else if (amount.compareTo(new BigDecimal("500000")) < 0) {
            if (vpApproves()) {
                approve();
            }
        } else {
            if (cfoApproves()) {
                approve();
            }
        }

        audit(commission);
    }
}
```

**Problems:**
- 🔴 Complex nested conditionals
- 🔴 Hard to add new approval levels
- 🔴 Tight coupling between approval logic and routing logic
- 🔴 Difficult to test individual approval rules
- 🔴 Cannot reuse approval logic
- 🔴 Hard to modify approval workflow

### With Chain of Responsibility ✅

```java
// Build chain
ApprovalHandler chain = new ValidationHandler()
    .setNext(new FraudDetectionHandler())
    .setNext(new AutoApprovalHandler())
    .setNext(new SalesManagerApprovalHandler("Manager"))
    .setNext(new RegionalDirectorApprovalHandler("Director"))
    .setNext(new VPSalesApprovalHandler("VP"))
    .setNext(new CFOApprovalHandler("CFO"))
    .setNext(new AuditLogHandler());

// Use chain
chain.approve(request);
```

**Benefits:**
- ✅ Clean, linear chain construction
- ✅ Easy to add/remove handlers
- ✅ Each handler has single responsibility
- ✅ Handlers are independently testable
- ✅ Workflow is explicit and configurable
- ✅ Loose coupling between handlers

## Chain Flow Diagram

```
Request Flow:
┌─────────────┐
│  Client     │
│  Submits    │
│  Request    │
└──────┬──────┘
       ↓
┌──────────────────┐
│ ValidationHandler│ → Validates business rules
└────────┬─────────┘
         ↓ (if valid)
┌──────────────────┐
│FraudDetection    │ → Checks fraud indicators
└────────┬─────────┘
         ↓ (if clean)
┌──────────────────┐
│ AutoApproval     │ → Approves if < $5k [STOPS if approved]
└────────┬─────────┘
         ↓ (if ≥ $5k)
┌──────────────────┐
│ SalesManager     │ → Approves $5k-$25k [STOPS if approved]
└────────┬─────────┘
         ↓ (if ≥ $25k)
┌──────────────────┐
│RegionalDirector  │ → Approves $25k-$100k [STOPS if approved]
└────────┬─────────┘
         ↓ (if ≥ $100k)
┌──────────────────┐
│    VPSales       │ → Approves $100k-$500k [STOPS if approved]
└────────┬─────────┘
         ↓ (if ≥ $500k)
┌──────────────────┐
│      CFO         │ → Approves ≥ $500k [STOPS if approved]
└────────┬─────────┘
         ↓
┌──────────────────┐
│  AuditLog        │ → Records audit trail [ALWAYS continues]
└──────────────────┘
```

## File Structure

```
cor/
├── CoRStructure.java            # Generic CoR pattern structure
├── CoRImplementation.java       # Commission approval implementation
├── CoRUsage.java                # Comprehensive usage examples
├── cor-pattern.puml             # UML class diagram
└── README.md                    # This file
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.cor.CoRStructure"
```

**Output shows:**
- Priority-based request routing
- Interceptor-style handlers (logging, validation)
- Chain behavior with different handler types

### Run Commission Approval Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.cor.CoRImplementation"
```

**Output shows:**
- 5 approval requests with different amounts
- Complete approval workflow for each
- Cross-cutting concerns (validation, fraud, audit)
- Approval history/audit trails

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.cor.CoRUsage"
```

**Output shows:**
- 6 detailed scenarios
- Dynamic chain modification
- Early termination/short-circuiting
- Multiple parallel chains
- Audit trail analysis
- Testing patterns

## Usage Examples

### Example 1: Basic Chain Usage

```java
// Create chain
ApprovalHandler chain = new AutoApprovalHandler();
chain.setNext(new SalesManagerApprovalHandler("Manager"))
     .setNext(new RegionalDirectorApprovalHandler("Director"));

// Create request
CommissionApprovalRequest request = new CommissionApprovalRequest(
    "REQ-001", deal, "REP-123", "John Smith", new BigDecimal("15000")
);

// Process
chain.approve(request);

// Check result
if (request.isApproved()) {
    System.out.println("Approved!");
}
```

### Example 2: Building Chain with Interceptors

```java
// Build chain with cross-cutting concerns
ApprovalHandler chain = new ValidationHandler()          // Validate all
    .setNext(new FraudDetectionHandler())                // Check fraud
    .setNext(new AutoApprovalHandler())                  // Business logic
    .setNext(new SalesManagerApprovalHandler("Manager")) // Business logic
    .setNext(new AuditLogHandler());                     // Audit all

chain.approve(request);
```

### Example 3: Dynamic Chain Modification

```java
// Build different chains for different scenarios

// Fast-track chain (trusted source)
ApprovalHandler fastTrack = new ValidationHandler()
    .setNext(new AutoApprovalHandler())
    .setNext(new SalesManagerApprovalHandler("Quick Approver"));

// High-risk chain (suspicious source)
ApprovalHandler highRisk = new ValidationHandler()
    .setNext(new FraudDetectionHandler())
    .setNext(new SalesManagerApprovalHandler("Careful Approver"))
    .setNext(new RegionalDirectorApprovalHandler("Extra Review"));

// Use appropriate chain
if (isTrustedSource(request)) {
    fastTrack.approve(request);
} else {
    highRisk.approve(request);
}
```

### Example 4: Testing Individual Handlers

```java
@Test
void testAutoApprovalThreshold() {
    ApprovalHandler autoHandler = new AutoApprovalHandler();

    // Below threshold
    CommissionApprovalRequest below = createRequest(new BigDecimal("4000"));
    autoHandler.approve(below);
    assertTrue(below.isApproved());

    // At threshold
    CommissionApprovalRequest at = createRequest(new BigDecimal("5000"));
    autoHandler.approve(at);
    assertFalse(at.isApproved());  // Should pass to next handler
}
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- Each handler has one responsibility
- ValidationHandler: validates
- FraudDetectionHandler: detects fraud
- SalesManagerApprovalHandler: approves within authority

### Open/Closed Principle (OCP)
- **Open for extension**: Add new handlers to chain
- **Closed for modification**: Existing handlers unchanged

### Dependency Inversion Principle (DIP)
- Client depends on Handler interface
- Handlers depend on ApprovalHandler interface
- Concrete implementations are pluggable

### Liskov Substitution Principle (LSP)
- Any handler can be substituted in the chain
- Chain behavior remains consistent

## Handler Types

### 1. Processing Handlers
Handle the request and **stop** the chain:
```java
protected void processApproval(CommissionApprovalRequest request) {
    request.approve("Sales Manager", "Approved");
    // Chain stops (shouldContinueChain() returns false)
}
```

### 2. Interceptor Handlers
Process the request and **continue** the chain:
```java
protected void processApproval(CommissionApprovalRequest request) {
    log("Processing request: " + request.getId());
    // Don't mark as handled - let chain continue
}

protected boolean shouldContinueChain(CommissionApprovalRequest request) {
    return true;  // Always continue
}
```

### 3. Filter Handlers
May reject and stop, or pass along:
```java
protected void processApproval(CommissionApprovalRequest request) {
    if (isValid(request)) {
        // Valid - continue chain
    } else {
        request.reject("Validation", "Invalid data");
        // Chain stops due to rejection
    }
}
```

## When to Use Chain of Responsibility

✅ **Use CoR when:**
- Multiple objects may handle a request
- Handler isn't known in advance
- You want to issue request to one of several objects without specifying receiver
- Set of handlers should be specified dynamically
- You want to avoid explicit if-else chains for routing
- You need an audit trail of request processing

❌ **Don't use CoR when:**
- Only one object handles the request
- Handler is known at compile time
- Simple if-else is sufficient
- Performance is critical (chain traversal has overhead)
- Request must be handled (no handler = failure)

## Advantages and Disadvantages

### Advantages ✅

1. **Reduced Coupling**
   - Sender doesn't know which handler will process
   - Handlers only know about successor

2. **Flexibility**
   - Add/remove handlers at runtime
   - Change handler order dynamically

3. **Single Responsibility**
   - Each handler does one thing
   - Easy to understand and test

4. **Open/Closed**
   - Add new handlers without modifying existing code
   - Extend behavior without changing client

5. **Audit Trails**
   - Chain naturally creates process history
   - Perfect for compliance

### Disadvantages ❌

1. **No Guarantee of Handling**
   - Request may reach end of chain unhandled
   - Must design for this scenario

2. **Runtime Overhead**
   - Traversing chain has cost
   - Each handler checked even if not applicable

3. **Debugging Complexity**
   - Hard to trace which handler processed request
   - Chain behavior may not be obvious

4. **Configuration Complexity**
   - Must correctly order handlers
   - Dependencies between handlers must be managed

## Testing Strategy

### Unit Testing Individual Handlers

```java
@Test
void testSalesManagerApprovalRange() {
    SalesManagerApprovalHandler handler =
        new SalesManagerApprovalHandler("Test Manager");

    // Within range
    CommissionApprovalRequest req1 = createRequest(new BigDecimal("10000"));
    handler.approve(req1);
    assertTrue(req1.isApproved());

    // Below range (should pass to next)
    CommissionApprovalRequest req2 = createRequest(new BigDecimal("4000"));
    handler.approve(req2);
    assertFalse(req2.isProcessed());  // Not handled by this handler
}
```

### Integration Testing Chain Behavior

```java
@Test
void testCompleteApprovalChain() {
    ApprovalHandler chain = buildStandardChain();

    // Test various amounts
    testAmount(chain, new BigDecimal("3000"), true);   // Auto-approved
    testAmount(chain, new BigDecimal("10000"), true);  // Manager-approved
    testAmount(chain, new BigDecimal("50000"), true);  // Director-approved
}

private void testAmount(ApprovalHandler chain, BigDecimal amount,
                       boolean shouldApprove) {
    CommissionApprovalRequest request = createRequest(amount);
    chain.approve(request);
    assertEquals(shouldApprove, request.isApproved());
}
```

### Testing Chain Modifications

```java
@Test
void testDynamicChainModification() {
    // Build minimal chain
    ApprovalHandler chain1 = new AutoApprovalHandler()
        .setNext(new SalesManagerApprovalHandler("Manager"));

    // Build extended chain
    ApprovalHandler chain2 = new ValidationHandler()
        .setNext(new FraudDetectionHandler())
        .setNext(new AutoApprovalHandler())
        .setNext(new SalesManagerApprovalHandler("Manager"));

    // Same request, different chains, different results
    CommissionApprovalRequest req1 = createRequest(new BigDecimal("10000"));
    CommissionApprovalRequest req2 = createRequest(new BigDecimal("10000"));

    chain1.approve(req1);
    chain2.approve(req2);

    // Chain2 has more steps in history
    assertTrue(req2.getApprovalHistory().size() >
              req1.getApprovalHistory().size());
}
```

## Real-World Applications

The Chain of Responsibility pattern is useful for:

1. **Event Handling Systems**
   - GUI event handling (button clicks, key presses)
   - Event bubbling in DOM
   - Middleware in web frameworks

2. **Logging Frameworks**
   - Log level filtering (DEBUG → INFO → WARN → ERROR)
   - Multiple log destinations (console, file, network)

3. **Authentication/Authorization**
   - Multiple authentication methods (OAuth, SAML, Basic Auth)
   - Permission checking at different levels

4. **Request Processing Pipelines**
   - HTTP middleware (Express.js, ASP.NET)
   - Servlet filters
   - Interceptors

5. **Help Systems**
   - Context-sensitive help
   - Widget → Panel → Window → Application

## Common Pitfalls to Avoid

### ❌ Don't: Create circular chains

```java
// BAD - creates infinite loop
Handler h1 = new HandlerA();
Handler h2 = new HandlerB();
h1.setNext(h2);
h2.setNext(h1);  // Circular!
```

### ✅ Do: Ensure chain has end

```java
// GOOD - chain has clear end
Handler h1 = new HandlerA();
Handler h2 = new HandlerB();
Handler h3 = new HandlerC();
h1.setNext(h2).setNext(h3);  // Linear chain
```

### ❌ Don't: Forget to check for end of chain

```java
// BAD - no null check
protected void passToNext(Request request) {
    nextHandler.handle(request);  // NullPointerException if no next!
}
```

### ✅ Do: Handle end of chain gracefully

```java
// GOOD - check for null
protected void passToNext(Request request) {
    if (nextHandler != null) {
        nextHandler.handle(request);
    } else {
        handleEndOfChain(request);
    }
}
```

### ❌ Don't: Put all logic in base class

```java
// BAD - defeats purpose of pattern
public abstract class BaseHandler {
    public void handle(Request request) {
        if (request.getType() == "A") {
            // Handler A logic
        } else if (request.getType() == "B") {
            // Handler B logic
        }
        // ... defeats the pattern!
    }
}
```

### ✅ Do: Let each handler decide

```java
// GOOD - each handler has its logic
public abstract class BaseHandler {
    public void handle(Request request) {
        if (canHandle(request)) {
            processRequest(request);
        } else {
            passToNext(request);
        }
    }

    protected abstract boolean canHandle(Request request);
    protected abstract void processRequest(Request request);
}
```

## Chain of Responsibility vs Other Patterns

### CoR vs Decorator
- **Decorator**: Adds behavior, all decorators execute
- **CoR**: One handler processes (usually), request may not be handled
- **Use Decorator when**: All wrappers should execute
- **Use CoR when**: One handler should process

### CoR vs Command
- **Command**: Encapsulates request as object
- **CoR**: Passes request through chain of handlers
- Often used together: Command as the request in CoR

### CoR vs Observer
- **Observer**: Notifies all observers
- **CoR**: One handler typically processes
- **Observer**: All interested parties notified
- **CoR**: First capable handler processes

## Related Patterns

- **Composite Pattern**: Often used with CoR for tree structures
- **Command Pattern**: Requests can be Command objects
- **Decorator Pattern**: Similar structure, different intent

## Further Learning

To deepen understanding:

1. Run all three demo files and study the output
2. Add a new approval level (e.g., "Senior Director" between Director and VP)
3. Implement a chain that collects results from all handlers (not just first)
4. Create a bidirectional chain (request can flow backwards)
5. Implement priority-based handling (highest priority handler processes first)
6. Add chain monitoring/metrics (track which handlers process most often)

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 223-232)
- **Head First Design Patterns** - Freeman & Freeman
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler
- **Pattern-Oriented Software Architecture** - Buschmann et al.