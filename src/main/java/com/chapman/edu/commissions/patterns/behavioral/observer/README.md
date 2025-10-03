# Observer Pattern Implementation

## Overview

This package demonstrates the **Observer Pattern**, a behavioral design pattern that establishes a one-to-many dependency between objects. When the subject (observable) changes state, all registered observers are automatically notified and updated.

## Pattern Intent

Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified and updated automatically, promoting loose coupling between components.

## File Organization

This implementation is organized into three consolidated files for clarity and ease of learning:

### 1. **ObserverStructure.java** - Pattern Structure
Defines the core interfaces and abstract base classes that form the foundation of the Observer Pattern.

**Contains:**
- `DealSubject` interface - Subject contract for managing observers
- `DealObserver` interface - Observer contract for receiving notifications
- `AbstractDealSubject` - Optional base class with reusable observer management logic

**Key Concepts:**
- Interface-based design for loose coupling
- Push vs. pull notification models
- Observer registration and lifecycle management
- Error isolation strategies
- Memory leak prevention

### 2. **ObserverImplementation.java** - Pattern Implementation
Provides concrete implementations demonstrating different observer strategies.

**Contains:**
- `ObservableDealTracker` - Concrete subject managing deals and notifications
- `CommissionCalculationObserver` - Selective event handling (only WON deals)
- `AuditLogObserver` - Universal event handling (all events)
- `NotificationObserver` - Conditional event handling (complex routing logic)

**Key Concepts:**
- Business logic integration with the pattern
- Different observer strategies (selective, universal, conditional)
- State management in subjects and observers
- Event filtering and routing
- Production-ready error handling

### 3. **ObserverUsage.java** - Pattern Usage
Demonstrates how to use the pattern with complete examples, best practices, and common pitfalls.

**Contains:**
- Complete 11-step demonstration workflow
- Observer independence and reusability examples
- Best practices with try-finally and AutoCloseable
- Common pitfalls and solutions
- Conditional observer attachment patterns
- Observer monitoring techniques

**Key Concepts:**
- Subject and observer lifecycle
- Dynamic observer management (attach/detach)
- Memory leak prevention
- Multiple subjects with different observer sets
- Observer configuration and reuse

## Implementation Highlights

### Observer Strategies Demonstrated

#### 1. Selective Event Handling (CommissionCalculationObserver)
```java
// Only processes STATUS_CHANGED events when status is WON
if ("STATUS_CHANGED".equals(eventType) && deal.getStatus() == DealStatus.WON) {
    calculateCommission(deal);
}
```
- Efficient filtering of irrelevant events
- Focused on specific business logic
- Ignores events it doesn't care about

#### 2. Universal Event Handling (AuditLogObserver)
```java
// Records every event without filtering
auditLog.add(createLogEntry(deal, eventType));
```
- Comprehensive tracking for compliance
- No filtering - completeness is critical
- Maintains independent audit trail

#### 3. Conditional Event Handling (NotificationObserver)
```java
// Different logic for different events
switch (eventType) {
    case "CREATED": handleDealCreated(deal); break;
    case "STATUS_CHANGED": handleStatusChanged(deal); break;
    case "VALUE_UPDATED": handleValueUpdated(deal); break;
}
```
- Complex business rules per event type
- Intelligent routing based on deal characteristics
- Integration with external notification systems

### Key Benefits Demonstrated

1. **Loose Coupling**
   - Subject doesn't know concrete observer types
   - Observers don't know about each other
   - Easy to add new observers without modifying existing code

2. **Open/Closed Principle**
   - System is open for extension (new observers)
   - System is closed for modification (subject unchanged)

3. **Runtime Flexibility**
   - Observers can be attached/detached dynamically
   - Same observer can observe multiple subjects
   - Different subjects can have different observer combinations

4. **Separation of Concerns**
   - Deal management logic separate from commission calculation
   - Audit logging separate from notification logic
   - Each observer focuses on single responsibility

5. **Event-Driven Architecture**
   - Changes propagate automatically
   - No need for polling or manual coordination
   - Real-time response to state changes

## Real-World Use Cases

### Commission Calculation System
When a deal closes, multiple systems need to respond:
- Calculate and record commission amounts
- Log the transaction for compliance
- Notify the sales representative
- Update performance dashboards
- Trigger payment workflows

The Observer Pattern allows all these concerns to be handled independently without coupling them together.

### Other Applications
- GUI event handling (button clicks, form submissions)
- Stock market tickers (price updates notify multiple displays)
- Social media (post notifications to followers)
- Sensor networks (temperature changes trigger multiple responses)
- Messaging systems (publish-subscribe patterns)

## Running the Examples

### Compile the Code
```bash
mvn compile
```

### Run the Usage Demonstration
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.observer.ObserverUsage"
```

### Expected Output
The demonstration will show:
1. Observer registration with the subject
2. Deal creation events triggering all observers
3. Status change events (including commission calculation for won deals)
4. Value update events
5. Observer detachment and its effect on notifications
6. Summary of events and observer activity

## Pattern Variations

### Push vs. Pull Model

**Push Model (Used in This Implementation):**
- Subject sends detailed information to observers
- Observers receive the deal object and event type
- Simpler for observers but less flexible
- Used in: `onDealUpdated(Deal deal, String eventType)`

**Pull Model (Alternative):**
- Subject only notifies that something changed
- Observers call back to get needed information
- More flexible but requires more coordination
- Would be: `onDealUpdated()` (no parameters)

### Error Handling

Our implementation catches exceptions from individual observers to prevent one faulty observer from breaking the notification chain:

```java
for (DealObserver observer : observers) {
    try {
        observer.onDealUpdated(deal, eventType);
    } catch (Exception e) {
        LOGGER.severe("Error notifying observer: " + e.getMessage());
        // Continue notifying other observers
    }
}
```

This ensures system resilience even with buggy observers.

## Design Considerations

### When to Use Observer Pattern
✅ Multiple objects need to react to state changes
✅ You want to avoid tight coupling between components
✅ The number of dependents may vary or be determined at runtime
✅ You need to maintain consistency across related objects

### When NOT to Use Observer Pattern
❌ You have only one dependent object (use direct method call)
❌ Notification order is critical (observers are notified in arbitrary order)
❌ Performance is critical and you have many observers
❌ The dependency chain becomes too complex to debug

## Best Practices

### 1. Always Detach Observers
```java
DealObserver observer = new MyObserver();
tracker.attach(observer);
try {
    // Use tracker
} finally {
    tracker.detach(observer);  // Prevent memory leaks
}
```

### 2. Use AutoCloseable for Automatic Cleanup
```java
try (ObserverRegistration registration = new ObserverRegistration(tracker, observer)) {
    // Observer automatically detached when leaving scope
}
```

### 3. Avoid Modifying Subject During Notification
```java
// BAD: Can cause infinite loops
public void onDealUpdated(Deal deal, String eventType) {
    tracker.updateDealStatus(deal.getId(), DealStatus.PENDING);  // ❌ Don't do this!
}

// GOOD: Queue for later execution
public void onDealUpdated(Deal deal, String eventType) {
    deferredActions.offer(() -> tracker.updateDealStatus(deal.getId(), DealStatus.PENDING));
}
```

### 4. Monitor Observer Counts
```java
int count = tracker.getObserverCount();
if (count > 1000) {
    LOGGER.warning("Unusually high observer count - possible memory leak!");
}
```

## Related Patterns

- **Mediator**: Centralizes complex communications between objects
- **Singleton**: Often used for implementing global subject instances
- **Event Bus**: Extension of Observer for decoupled event distribution
- **Publish-Subscribe**: Similar intent with message routing infrastructure
- **Chain of Responsibility**: Alternative when order of execution matters

## Modern Alternatives

While the Observer Pattern is fundamental, modern frameworks often use:
- **Event Buses** (Spring ApplicationEventPublisher, Guava EventBus)
- **Reactive Streams** (RxJava, Project Reactor)
- **Message Queues** (RabbitMQ, Kafka)

These provide additional features like:
- Backpressure handling
- Asynchronous processing
- Cross-service communication
- Event persistence and replay

However, the traditional Observer Pattern remains valuable for:
- Simple in-process notifications
- Educational purposes
- Tight performance requirements
- Cases where frameworks add unnecessary complexity

## Documentation Files

- **README.md** (this file) - Overview and usage guide
- **QUESTIONS.md** - 35 questions testing pattern comprehension
- **ANSWERS.md** - Detailed answers with code examples and explanations
- **observer-pattern.puml** - UML class diagram showing pattern structure

## Additional Resources

For detailed questions and comprehensive answers about the Observer Pattern concepts demonstrated in this implementation, see the accompanying `QUESTIONS.md` and `ANSWERS.md` files.

The PlantUML diagram (`observer-pattern.puml`) provides a visual representation of the pattern structure and relationships between components.