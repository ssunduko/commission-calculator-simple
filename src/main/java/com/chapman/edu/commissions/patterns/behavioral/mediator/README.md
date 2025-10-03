# Mediator Pattern Implementation

## Overview

The **Mediator Pattern** is a behavioral design pattern that defines an object that encapsulates how a set of objects interact. It promotes loose coupling by keeping objects from referring to each other explicitly, and lets you vary their interaction independently.

This implementation demonstrates the Mediator pattern through **Commission System Coordination**, showing how multiple system components communicate through a central coordination hub without knowing about each other.

## Pattern Components

### 1. Mediator Interface (`CommissionSystemMediator`)
Defines the interface for communicating with system components:
```java
interface CommissionSystemMediator {
    void notify(SystemComponent sender, CommissionEvent event);
    void registerComponent(SystemComponent component);
}
```

### 2. Concrete Mediator (`CommissionCoordinationHub`)
Implements coordination logic between components:
- Maintains references to all components
- Routes events between appropriate components
- Orchestrates complex workflows
- Provides centralized logging and metrics

### 3. Colleague Abstract Class (`SystemComponent`)
Base class for all components that communicate through the mediator:
```java
abstract class SystemComponent {
    protected CommissionSystemMediator mediator;

    public void setMediator(CommissionSystemMediator mediator) {
        this.mediator = mediator;
    }

    protected void notifyMediator(CommissionEvent event) {
        mediator.notify(this, event);
    }

    public abstract void handleEvent(CommissionEvent event);
}
```

### 4. Concrete Components

The system includes 7 specialized components:

| Component | Responsibility | Events Interested In |
|-----------|---------------|---------------------|
| **DealTrackerComponent** | Track deal lifecycle | (none - initiates events) |
| **CommissionCalculatorComponent** | Calculate commissions | DEAL_WON |
| **ApprovalWorkflowComponent** | Handle approval workflows | COMMISSION_CALCULATED |
| **NotificationComponent** | Send notifications | COMMISSION_CALCULATED, APPROVAL_REQUIRED, APPROVED, REJECTED |
| **AuditLogComponent** | Maintain audit trails | ALL events |
| **DisputeTrackerComponent** | Flag potential disputes | COMMISSION_CALCULATED |
| **AnalyticsComponent** | Collect metrics | DEAL_WON, COMMISSION_CALCULATED, APPROVAL_REQUIRED, APPROVED, REJECTED |

### 5. Event Object (`CommissionEvent`)
Contains communication data:
- Event type
- Source component
- Event data (flexible key-value pairs)
- Timestamp

## Problem Solved

### Without Mediator ❌

```java
// Components tightly coupled to each other
public class DealTracker {
    private CommissionCalculator calculator;
    private NotificationService notifications;
    private AuditLog auditLog;
    private ApprovalWorkflow approvalWorkflow;
    private DisputeTracker disputeTracker;
    private Analytics analytics;

    public void onDealWon(Deal deal) {
        // DealTracker knows about ALL other components
        auditLog.log("Deal won: " + deal.getId());

        BigDecimal commission = calculator.calculate(deal);

        analytics.recordDeal(deal);
        analytics.recordCommission(commission);

        if (commission.compareTo(new BigDecimal("50000")) > 0) {
            approvalWorkflow.requestApproval(deal, commission);
            notifications.sendApprovalRequest(deal);
        }

        if (disputeTracker.checkForDispute(deal, commission)) {
            notifications.sendDisputeAlert(deal);
        }

        notifications.notifyRep(deal, commission);
    }
}

// CommissionCalculator also knows about others
public class CommissionCalculator {
    private NotificationService notifications;
    private AuditLog auditLog;
    private Analytics analytics;

    public BigDecimal calculate(Deal deal) {
        auditLog.log("Calculating commission");
        BigDecimal result = // calculation
        analytics.recordCalculation(result);
        notifications.sendCalculationComplete(deal, result);
        return result;
    }
}
```

**Problems:**
- 🔴 Tight coupling: Components know about many other components
- 🔴 Hard to add new components (must update all dependent classes)
- 🔴 Difficult to test in isolation
- 🔴 Complex web of dependencies (N×N relationships)
- 🔴 Hard to understand interaction flow
- 🔴 Cannot reuse components independently
- 🔴 Changes propagate through entire system

### With Mediator ✅

```java
// Components only know the mediator
public class DealTrackerComponent extends SystemComponent {
    public void onDealWon(Deal deal) {
        CommissionEvent event = new CommissionEvent("DEAL_WON", "DealTracker");
        event.addData("deal", deal);
        notifyMediator(event);  // Only talks to mediator
    }
}

public class CommissionCalculatorComponent extends SystemComponent {
    public void handleEvent(CommissionEvent event) {
        if (event.getEventType().equals("DEAL_WON")) {
            Deal deal = (Deal) event.getData("deal");
            BigDecimal commission = calculate(deal);

            CommissionEvent newEvent = new CommissionEvent(
                "COMMISSION_CALCULATED", "Calculator"
            );
            newEvent.addData("deal", deal);
            newEvent.addData("commission", commission);
            notifyMediator(newEvent);  // Only talks to mediator
        }
    }
}

// Mediator coordinates everything
CommissionCoordinationHub hub = new CommissionCoordinationHub();
hub.registerComponent(new DealTrackerComponent());
hub.registerComponent(new CommissionCalculatorComponent());
hub.registerComponent(new NotificationComponent());
hub.registerComponent(new AuditLogComponent());
```

**Benefits:**
- ✅ Loose coupling: Components only know mediator
- ✅ Easy to add components without modifying existing ones
- ✅ Components testable in isolation
- ✅ Simple 1-to-many relationships
- ✅ Clear, centralized interaction logic
- ✅ Components are reusable
- ✅ Easy to understand and modify workflows

## Component Interaction Diagram

```
Without Mediator (Tight Coupling):
┌──────────┐     ┌──────────┐     ┌──────────┐
│  Deal    │────▶│Calculator│────▶│  Notify  │
│ Tracker  │◀────│          │◀────│          │
└─────┬────┘     └─────┬────┘     └─────┬────┘
      │                │                │
      │  ┌─────────────┼────────────┐   │
      │  │             │            │   │
      ▼  ▼             ▼            ▼   ▼
  ┌──────────┐   ┌──────────┐   ┌──────────┐
  │ Approval │   │  Audit   │   │Analytics │
  └──────────┘   └──────────┘   └──────────┘
     (N×N relationships - chaotic dependencies)

With Mediator (Loose Coupling):
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Deal    │────────▶│          │◀────────│Calculator│
│ Tracker  │         │          │         │          │
└──────────┘         │          │         └──────────┘
                     │          │
┌──────────┐         │ Mediator │         ┌──────────┐
│  Notify  │◀────────│   Hub    │────────▶│ Approval │
└──────────┘         │          │         └──────────┘
                     │          │
┌──────────┐         │          │         ┌──────────┐
│  Audit   │◀────────│          │────────▶│Analytics │
└──────────┘         └──────────┘         └──────────┘
     (1-to-many relationships - centralized control)
```

## Event Flow Example

```
User Action: Deal Won ($60,000)

1. DealTracker.onDealWon(deal)
   ↓
   Sends: DEAL_WON event to Hub
   ↓
2. Hub routes to interested components:
   ├─▶ CommissionCalculatorComponent
   │   └─▶ Calculates commission = $9,000
   │       └─▶ Sends: COMMISSION_CALCULATED event
   │           ↓
   ├─▶ AnalyticsComponent (records deal)
   └─▶ AuditLogComponent (logs deal)

3. Hub routes COMMISSION_CALCULATED:
   ├─▶ ApprovalWorkflowComponent (amount > $50k requires approval)
   │   └─▶ Sends: APPROVAL_REQUIRED event
   │       ↓
   ├─▶ NotificationComponent (notifies rep)
   ├─▶ DisputeTrackerComponent (checks for issues)
   ├─▶ AnalyticsComponent (records commission)
   └─▶ AuditLogComponent (logs calculation)

4. Hub routes APPROVAL_REQUIRED:
   ├─▶ NotificationComponent (notifies manager)
   ├─▶ AnalyticsComponent (records approval request)
   └─▶ AuditLogComponent (logs approval request)

Complete audit trail created automatically!
```

## File Structure

```
mediator/
├── MediatorStructure.java           # Generic Mediator pattern structure
├── MediatorImplementation.java      # Commission coordination implementation
├── MediatorUsage.java               # Comprehensive usage examples
├── mediator-pattern.puml            # UML class diagram
└── README.md                        # This file
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorStructure"
```

**Output shows:**
- Basic mediator with colleague communication
- Smart mediator with intelligent routing
- Urgent vs. private vs. broadcast messages

### Run Commission Coordination Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorImplementation"
```

**Output shows:**
- 3 deals processed through coordination hub
- Complete event flow for each deal
- Different approval workflows based on amounts
- Automatic audit trail creation

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorUsage"
```

**Output shows:**
- 6 detailed scenarios
- Basic system setup
- Dynamic component management
- Component enable/disable
- Event filtering
- Testing patterns
- Complex workflow coordination

## Usage Examples

### Example 1: Basic System Setup

```java
// Create mediator
CommissionCoordinationHub hub = new CommissionCoordinationHub();

// Create components
DealTrackerComponent dealTracker = new DealTrackerComponent();
CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
NotificationComponent notifications = new NotificationComponent();

// Register with mediator
hub.registerComponent(dealTracker);
hub.registerComponent(calculator);
hub.registerComponent(notifications);

// Use system - components coordinate automatically
Deal deal = createDeal("Enterprise Deal", new BigDecimal("30000"));
dealTracker.onDealWon(deal);
// Calculator, notifications, etc. automatically triggered
```

### Example 2: Adding Components Dynamically

```java
// Start with minimal system
CommissionCoordinationHub hub = new CommissionCoordinationHub();
hub.registerComponent(new DealTrackerComponent());
hub.registerComponent(new CommissionCalculatorComponent());

// Process some deals
dealTracker.onDealWon(deal1);

// Add analytics later without changing existing components
AnalyticsComponent analytics = new AnalyticsComponent();
hub.registerComponent(analytics);

// Analytics automatically starts receiving events
dealTracker.onDealWon(deal2);
```

### Example 3: Component Enable/Disable

```java
// Disable notifications for maintenance
notifications.setEnabled(false);

// System continues working, but notifications not sent
dealTracker.onDealWon(deal);

// Re-enable
notifications.setEnabled(true);
dealTracker.onDealWon(nextDeal);
```

### Example 4: Event Filtering

```java
public class AnalyticsComponent extends SystemComponent {
    @Override
    public boolean isInterestedIn(String eventType) {
        // Only handle specific events
        return eventType.equals("DEAL_WON") ||
               eventType.equals("COMMISSION_CALCULATED") ||
               eventType.equals("APPROVED");
    }

    @Override
    public void handleEvent(CommissionEvent event) {
        // Only called for events we're interested in
    }
}
```

### Example 5: Testing with Mediator

```java
@Test
void testCalculatorComponent() {
    // Create test mediator
    CommissionCoordinationHub testHub = new CommissionCoordinationHub();

    // Test component in isolation
    CommissionCalculatorComponent calculator =
        new CommissionCalculatorComponent();
    testHub.registerComponent(calculator);

    // Create test event
    CommissionEvent event = new CommissionEvent("DEAL_WON", "Test");
    event.addData("deal", testDeal);

    // Test
    calculator.handleEvent(event);

    // Verify
    assertEquals(1, testHub.getEventCount());
}
```

### Example 6: Mock Components for Testing

```java
@Test
void testWithMockComponents() {
    class MockNotificationComponent extends NotificationComponent {
        public int notificationCount = 0;

        @Override
        public void handleEvent(CommissionEvent event) {
            notificationCount++;
        }
    }

    CommissionCoordinationHub hub = new CommissionCoordinationHub();
    MockNotificationComponent mock = new MockNotificationComponent();

    hub.registerComponent(new DealTrackerComponent());
    hub.registerComponent(mock);

    dealTracker.onDealWon(deal);

    assertEquals(1, mock.notificationCount);
}
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- Each component has one responsibility
- DealTracker: tracks deals
- Calculator: calculates commissions
- Notifications: sends notifications
- Mediator: coordinates interactions

### Open/Closed Principle (OCP)
- **Open for extension**: Add new components without modifying existing ones
- **Closed for modification**: Components unchanged when adding new ones

### Dependency Inversion Principle (DIP)
- Components depend on SystemComponent abstraction
- Components depend on CommissionSystemMediator interface
- Concrete implementations are pluggable

### Interface Segregation Principle (ISP)
- Components use `isInterestedIn()` to filter events
- Don't force components to handle irrelevant events

### Hollywood Principle
- "Don't call us, we'll call you"
- Components don't call each other
- Mediator calls components when appropriate

## When to Use Mediator

✅ **Use Mediator when:**
- A set of objects communicate in well-defined but complex ways
- Reusing an object is difficult due to many dependencies
- Behavior distributed across several classes should be customizable
- You want to avoid creating many interconnected classes
- You need centralized control over object interactions
- You're building an event-driven system
- You need audit trails of interactions

❌ **Don't use Mediator when:**
- Objects have simple, direct relationships
- Only 2-3 objects interact
- Performance is critical (mediator adds indirection)
- Mediator becomes a "God Object" doing too much
- Objects should directly control their interactions

## Advantages and Disadvantages

### Advantages ✅

1. **Loose Coupling**
   - Components don't know about each other
   - Only know the mediator
   - Easy to modify or replace components

2. **Centralized Control**
   - All interaction logic in one place
   - Easy to understand workflows
   - Simplified debugging

3. **Reusability**
   - Components are independently reusable
   - No hard dependencies on other components
   - Easy to test in isolation

4. **Easy Extension**
   - Add new components without modifying existing ones
   - Components automatically integrate
   - No cascading changes

5. **Simplified Components**
   - Components focus on their core responsibility
   - No need to manage multiple references
   - Cleaner, simpler code

6. **Event-Driven Architecture**
   - Natural fit for reactive systems
   - Asynchronous communication possible
   - Audit trails created automatically

### Disadvantages ❌

1. **Mediator Complexity**
   - Mediator can become complex
   - Risk of "God Object" anti-pattern
   - Must carefully design mediator responsibilities

2. **Single Point of Failure**
   - All communication goes through mediator
   - Mediator failure affects entire system
   - Must ensure mediator reliability

3. **Performance Overhead**
   - Extra indirection through mediator
   - Event routing has cost
   - Not suitable for high-frequency communications

4. **Potential Bottleneck**
   - Mediator can become performance bottleneck
   - Must handle concurrent events properly
   - May need async processing

## Testing Strategy

### Unit Testing Individual Components

```java
@Test
void testCalculatorComponentCalculation() {
    CommissionCoordinationHub hub = new CommissionCoordinationHub();
    CommissionCalculatorComponent calculator =
        new CommissionCalculatorComponent();
    hub.registerComponent(calculator);

    CommissionEvent event = new CommissionEvent("DEAL_WON", "Test");
    Deal deal = createDeal("Test", new BigDecimal("10000"));
    event.addData("deal", deal);

    calculator.handleEvent(event);

    // Verify calculator processed correctly
    assertEquals(1, hub.getEventCount());
}
```

### Integration Testing Component Interactions

```java
@Test
void testCompleteWorkflow() {
    CommissionCoordinationHub hub = new CommissionCoordinationHub();

    DealTrackerComponent dealTracker = new DealTrackerComponent();
    CommissionCalculatorComponent calculator =
        new CommissionCalculatorComponent();
    NotificationComponent notifications = new NotificationComponent();

    hub.registerComponent(dealTracker);
    hub.registerComponent(calculator);
    hub.registerComponent(notifications);

    Deal deal = createDeal("Test", new BigDecimal("20000"));
    dealTracker.onDealWon(deal);

    // Verify complete workflow
    assertTrue(hub.getEventCount() >= 2); // DEAL_WON + COMMISSION_CALCULATED
    assertTrue(notifications.getSentNotifications().size() > 0);
}
```

### Testing Event Filtering

```java
@Test
void testEventFiltering() {
    CommissionCoordinationHub hub = new CommissionCoordinationHub();

    class CountingComponent extends SystemComponent {
        public int eventCount = 0;

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("DEAL_WON");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            eventCount++;
        }
    }

    CountingComponent counter = new CountingComponent();
    hub.registerComponent(counter);

    // Send various events
    hub.notify(null, new CommissionEvent("DEAL_WON", "Test"));
    hub.notify(null, new CommissionEvent("OTHER", "Test"));

    // Should only receive DEAL_WON
    assertEquals(1, counter.eventCount);
}
```

## Real-World Applications

The Mediator pattern is useful for:

1. **GUI Frameworks**
   - Dialog boxes coordinating multiple controls
   - Form validation across multiple fields
   - MVC/MVP/MVVM architectures

2. **Air Traffic Control**
   - Mediator = control tower
   - Colleagues = aircraft
   - Centralized coordination prevents collisions

3. **Chat Applications**
   - Mediator = chat room/server
   - Colleagues = users
   - Routes messages between users

4. **Workflow Engines**
   - Mediator = workflow coordinator
   - Colleagues = workflow steps
   - Orchestrates complex business processes

5. **Event Bus Systems**
   - Mediator = event bus
   - Colleagues = event publishers/subscribers
   - Decouples event producers from consumers

6. **Microservices Orchestration**
   - Mediator = service orchestrator
   - Colleagues = microservices
   - Coordinates service interactions

## Common Pitfalls to Avoid

### ❌ Don't: Create "God Object" Mediator

```java
// BAD - mediator doing too much
public class MegaMediator implements Mediator {
    public void notify(Component sender, Event event) {
        // 500 lines of complex logic
        // Business logic, validation, persistence, etc.
        // TOO MUCH RESPONSIBILITY!
    }
}
```

### ✅ Do: Keep Mediator Focused on Coordination

```java
// GOOD - mediator just routes
public class FocusedMediator implements Mediator {
    public void notify(Component sender, Event event) {
        // Just route to interested components
        for (Component c : components) {
            if (c.isInterestedIn(event.getType())) {
                c.handleEvent(event);
            }
        }
    }
}
```

### ❌ Don't: Let Components Communicate Directly

```java
// BAD - defeats the pattern
public class CalculatorComponent extends Component {
    private NotificationComponent notifications;  // Direct reference!

    public void handleEvent(Event event) {
        // ...
        notifications.send("Done");  // Direct call!
    }
}
```

### ✅ Do: All Communication Through Mediator

```java
// GOOD - only knows mediator
public class CalculatorComponent extends Component {
    public void handleEvent(Event event) {
        // ...
        notifyMediator(new Event("CALCULATION_COMPLETE"));
    }
}
```

### ❌ Don't: Forget to Unregister Components

```java
// BAD - memory leak
Component temp = new TempComponent();
mediator.registerComponent(temp);
// temp goes out of scope but mediator still has reference
```

### ✅ Do: Provide Unregister Mechanism

```java
// GOOD - clean up
Component temp = new TempComponent();
mediator.registerComponent(temp);
// When done...
mediator.unregisterComponent(temp);
```

## Mediator vs Other Patterns

### Mediator vs Observer
- **Observer**: Many-to-many relationships (all observers notified)
- **Mediator**: One-to-many relationships (centralized control)
- **Use Observer when**: Multiple objects should react to state changes
- **Use Mediator when**: Complex coordination logic needed

### Mediator vs Facade
- **Facade**: Simplifies interface to subsystem (one-way)
- **Mediator**: Coordinates bi-directional communication
- **Facade**: Subsystem doesn't know about facade
- **Mediator**: Colleagues know about mediator

### Mediator vs Command
- Often used together
- Command encapsulates requests as objects
- Mediator can use Command pattern for event objects

## Related Patterns

- **Observer Pattern**: Similar structure, different intent
- **Facade Pattern**: Similar simplification, but one-way
- **Command Pattern**: Often used for events in mediator
- **Chain of Responsibility**: Alternative for routing requests

## Further Learning

To deepen understanding:

1. Run all three demo files and study the output
2. Add a new component (e.g., "MetricsCollector")
3. Implement asynchronous event processing
4. Add priority-based event handling
5. Implement event filtering at mediator level
6. Create a bidirectional mediator (components can query each other)
7. Add event history/replay functionality
8. Implement transaction support (rollback failed workflows)

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 273-282)
- **Head First Design Patterns** - Freeman & Freeman
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler
- **Pattern-Oriented Software Architecture** - Buschmann et al.