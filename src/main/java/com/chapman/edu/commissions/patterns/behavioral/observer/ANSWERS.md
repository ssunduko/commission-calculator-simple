# Observer Pattern - Knowledge Test Answers

## About This Document

This document provides comprehensive answers to the questions in QUESTIONS.md. The answers reference code from the three consolidated implementation files:
- **ObserverStructure.java** - Core pattern structure
- **ObserverImplementation.java** - Concrete implementations
- **ObserverUsage.java** - Usage examples and best practices

All code examples are based on these files.

---

## Multiple Choice Questions

### 1. What is the primary intent of the Observer Pattern?
**Answer: b) To define a one-to-many dependency between objects**

The Observer Pattern's primary intent is to establish a one-to-many dependency where multiple objects (observers) can react to state changes in a single object (subject). When the subject changes, all registered observers are automatically notified and updated.

### 2. In the Observer Pattern, what is the role of the Subject?
**Answer: b) To maintain a list of observers and notify them of state changes**

The Subject's responsibilities include:
- Maintaining a collection of observers
- Providing methods to attach and detach observers
- Notifying all registered observers when its state changes
- Optionally, providing methods for observers to query its state

### 3. Which method do observers typically implement to receive notifications?
**Answer: c) `update()` or similar (like `onDealUpdated()`)**

The Observer interface defines an update method (or similar naming like `onDealUpdated()`, `notify()`, `handleEvent()`) that the subject calls when notifying observers. The method name may vary, but the concept remains the same: it's the callback that observers implement to respond to subject changes.

### 4. What is a key benefit of the Observer Pattern?
**Answer: b) It promotes loose coupling between subject and observers**

Loose coupling is the primary benefit. The subject doesn't need to know the concrete types of its observers—it only knows they implement the Observer interface. This allows observers to be added, removed, or modified without changing the subject code.

### 5. In our implementation, when is the `notifyObservers()` method called?
**Answer: c) When the subject's state changes**

In our `ObservableDealTracker`, `notifyObservers()` is called after state-changing operations like `createDeal()`, `updateDealStatus()`, and `updateDealValue()`. This ensures observers are informed whenever the subject's state changes.

### 6. What happens when an observer is detached from a subject?
**Answer: c) That specific observer stops receiving notifications**

Detaching an observer removes it from the subject's observer list. That observer will no longer receive notifications, but other observers continue to function normally. The observer object itself is not destroyed—it simply stops being notified by that particular subject.

### 7. Which design principle does the Observer Pattern best support?
**Answer: b) Open/Closed Principle**

The Observer Pattern strongly supports the Open/Closed Principle: the system is open for extension (you can add new observers) but closed for modification (you don't need to change the subject when adding new observers). It also supports the Single Responsibility Principle by separating concerns.

### 8. In the "push" model of the Observer Pattern (used in our implementation), what does the subject do?
**Answer: b) Sends detailed information to observers during notification**

In the push model, the subject sends relevant data (the deal object and event type) to observers during notification. Observers receive all the information they need without having to query the subject. This is simpler but less flexible than the pull model.

### 9. Why does our `ObservableDealTracker.notifyObservers()` method catch exceptions?
**Answer: b) To prevent one faulty observer from breaking the notification chain**

Exception handling ensures that if one observer throws an exception during notification, it doesn't prevent other observers from being notified. This makes the system more resilient and prevents observer failures from cascading.

```java
for (DealObserver observer : observers) {
    try {
        observer.onDealUpdated(lastAffectedDeal, lastEventType);
    } catch (Exception e) {
        LOGGER.severe("Error notifying observer " +
            observer.getClass().getSimpleName() + ": " + e.getMessage());
    }
}
```

### 10. Can a single observer instance observe multiple subjects?
**Answer: c) Yes, observers can be attached to multiple subjects**

An observer instance can be registered with multiple subjects. This is demonstrated in our code where the same observer class can be instantiated and attached to different `ObservableDealTracker` instances, each with different configurations.

## Short Answer Questions

### 11. Explain the difference between the "push" model and the "pull" model in the Observer Pattern. Which one is used in our implementation?

**Answer:**

**Push Model (Used in Our Implementation):**
- The subject sends detailed information to observers during notification
- In our case: `observer.onDealUpdated(deal, eventType)` sends both the deal and event type
- **Advantages:** Simple for observers; they receive everything they need
- **Disadvantages:** Subject must know what data observers need; less flexible

**Pull Model (Alternative):**
- The subject only notifies that something changed
- Observers then query the subject to get the information they need
- **Advantages:** More flexible; observers get only what they need
- **Disadvantages:** Requires observers to know subject's interface; more complex

**Our Implementation:**
We use the push model. When `notifyObservers()` is called, it passes the deal object and event type to each observer's `onDealUpdated()` method, providing all necessary information without requiring observers to query back.

### 12. Describe a real-world scenario (outside of software) that exemplifies the Observer Pattern.

**Answer:**

**News Subscription Service:**
Think of a newspaper or magazine publisher (the subject) and its subscribers (observers):

- **Subject:** The newspaper publisher
- **Observers:** Individual subscribers
- **attach():** When someone subscribes to the newspaper
- **detach():** When someone cancels their subscription
- **notifyObservers():** When a new edition is published, all current subscribers automatically receive it
- **Benefits:** The publisher doesn't need to know who its subscribers are personally—it just maintains a mailing list and sends papers to everyone on it. Subscribers can join or leave at any time.

**Other Examples:**
- **Auction house:** When someone makes a bid (subject changes state), all interested bidders (observers) are notified
- **Weather station:** Temperature/pressure changes notify all registered displays and monitoring systems
- **Stock market ticker:** Price changes notify all investors watching that stock
- **YouTube channel:** When a creator uploads a video (subject), all subscribers (observers) receive a notification

### 13. What are the responsibilities of the Subject interface (`DealSubject`) in our implementation?

**Answer:**

The `DealSubject` interface defines three key responsibilities:

1. **Observer Registration (`attach`):**
   - Allow observers to register for notifications
   - Add observers to the internal list

2. **Observer Deregistration (`detach`):**
   - Allow observers to unsubscribe from notifications
   - Remove observers from the internal list

3. **Observer Notification (`notifyObservers`):**
   - Inform all registered observers when state changes
   - Provide a consistent notification mechanism

**Why Use an Interface?**
- Enables multiple implementations of observable subjects
- Allows different subjects to be treated polymorphically
- Supports the Dependency Inversion Principle (depend on abstractions, not concretions)
- Makes testing easier (can create mock subjects)

### 14. How does the Observer Pattern promote the Open/Closed Principle?

**Answer:**

The Observer Pattern promotes the Open/Closed Principle (open for extension, closed for modification) in several ways:

**Open for Extension:**
- New observers can be created at any time without modifying existing code
- Example: We can add a `MetricsCollectionObserver` or `EmailDigestObserver` without changing `ObservableDealTracker`
- Observers can be added or removed at runtime

**Closed for Modification:**
- The subject (`ObservableDealTracker`) doesn't need to be modified when adding new observers
- Existing observers don't need to change when new observers are added
- The core notification mechanism remains unchanged

**Example:**
```java
// Adding a new observer requires NO changes to existing code
public class MetricsCollectionObserver implements DealObserver {
    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Collect metrics
    }
}

// Usage - extending the system without modification
dealTracker.attach(new MetricsCollectionObserver());
```

This is a powerful demonstration of good object-oriented design.

### 15. In our implementation, why do we use the interface type (`DealObserver`) instead of concrete types when storing observers in the subject?

**Answer:**

Using the interface type provides several critical benefits:

**1. Loose Coupling:**
```java
private final List<DealObserver> observers;  // Interface type
// NOT: private final List<CommissionCalculationObserver> observers;
```

The subject doesn't depend on concrete observer classes—it only knows about the `DealObserver` interface.

**2. Polymorphism:**
The list can hold any object that implements `DealObserver`:
```java
observers.add(new CommissionCalculationObserver(...));
observers.add(new AuditLogObserver());
observers.add(new NotificationObserver(...));
```

**3. Flexibility:**
- New observer types can be added without changing the subject
- Observers can be mixed and matched freely
- Same interface allows different implementations

**4. Dependency Inversion Principle:**
- High-level module (subject) doesn't depend on low-level modules (concrete observers)
- Both depend on abstraction (DealObserver interface)

**5. Testability:**
- Easy to create mock observers for testing
- Can verify notification behavior without real observer logic

## Code Analysis Questions

### 16. Review the `CommissionCalculationObserver` class. Why does it only respond to "STATUS_CHANGED" events when the status is `WON`?

**Answer:**

```java
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Only calculate commission when a deal is closed
    if ("STATUS_CHANGED".equals(eventType) && deal.getStatus() == DealStatus.WON) {
        calculateCommission(deal);
    }
}
```

**Business Logic Reason:**
Commissions should only be calculated and paid when deals are actually won/closed. Calculating commissions for other events would be incorrect:
- Deal creation: No commission yet (deal not closed)
- Value updates: Premature to calculate (deal still in progress)
- Lost deals: No commission earned
- Won deals: This is when commission is earned and should be calculated

**Design Pattern Benefit:**
This demonstrates **selective event handling**—observers can choose which events they care about. Not all observers need to respond to all events. This filtering keeps the observer logic focused and efficient.

**Alternative Approaches:**
- Could create separate observer interfaces for different event types
- Could use event filtering at the subject level
- Could implement a more sophisticated event subscription mechanism

### 17. Examine the `ObservableDealTracker.attach()` method. Why does it check if the observer already exists before adding it?

**Answer:**

```java
@Override
public void attach(DealObserver observer) {
    Objects.requireNonNull(observer, "Observer cannot be null");

    if (!observers.contains(observer)) {
        observers.add(observer);
        // ...
    }
}
```

**Reasons for the Check:**

**1. Prevents Duplicate Notifications:**
Without this check, the same observer could be added multiple times, causing it to receive duplicate notifications for every event:
```java
// Without the check:
tracker.attach(observer);
tracker.attach(observer);  // Same instance added twice
// Now observer.onDealUpdated() would be called twice per event!
```

**2. Maintains Set Semantics:**
The observer list should behave like a set—each observer should appear at most once.

**3. Idempotent Operation:**
Calling `attach()` multiple times with the same observer has the same effect as calling it once. This makes the API more predictable and error-resistant.

**4. Resource Efficiency:**
Prevents unnecessary memory usage and processing time from duplicate observers.

**5. Debugging:**
Makes it easier to reason about how many times an observer will be notified—exactly once per event.

**Note:** This relies on proper `equals()` implementation. If observers don't override `equals()`, reference equality is used (which is usually correct for observers).

### 18. Look at the three concrete observers (CommissionCalculationObserver, AuditLogObserver, NotificationObserver). How are they different in terms of which events they respond to?

**Answer:**

**CommissionCalculationObserver - Highly Selective:**
```java
if ("STATUS_CHANGED".equals(eventType) && deal.getStatus() == DealStatus.WON) {
    calculateCommission(deal);
}
```
- **Responds to:** Only STATUS_CHANGED events where status is WON
- **Ignores:** CREATED, VALUE_UPDATED, and other status changes
- **Rationale:** Commissions only calculated when deals are won

**AuditLogObserver - Universal:**
```java
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Records every single event without filtering
    String logEntry = String.format(...);
    auditLog.add(logEntry);
}
```
- **Responds to:** ALL events
- **Ignores:** Nothing
- **Rationale:** Audit logs need complete history of all changes

**NotificationObserver - Conditionally Selective:**
```java
switch (eventType) {
    case "CREATED":
        handleDealCreated(deal);  // With high-value threshold logic
        break;
    case "STATUS_CHANGED":
        handleStatusChanged(deal);  // Only for WON/LOST
        break;
    case "VALUE_UPDATED":
        handleValueUpdated(deal);
        break;
    default:
        // Logs but doesn't send notifications
}
```
- **Responds to:** Multiple event types with complex conditional logic
- **Special handling:** High-value deals get escalated notifications
- **Rationale:** Different stakeholders need different notifications

**Key Insight:**
This demonstrates the flexibility of the Observer Pattern—each observer can implement its own filtering and response logic independently.

### 19. In `ObserverPatternDemo`, what would happen if we called `createDeal()` before attaching any observers?

**Answer:**

**What Would Happen:**
```java
ObservableDealTracker tracker = new ObservableDealTracker();
Deal deal = createSampleDeal(...);
tracker.createDeal(deal);  // No observers attached yet!
// Later...
tracker.attach(commissionObserver);
```

**Result:**
1. The deal would be created and added to the tracker's internal list
2. `notifyObservers()` would be called
3. The loop would iterate over an empty list of observers
4. No observers would be notified (because there are none)
5. No errors would occur

**Code Analysis:**
```java
@Override
public void notifyObservers() {
    LOGGER.info(String.format("Notifying %d observer(s) of event: %s",
            observers.size(), lastEventType));  // Would print "0 observer(s)"

    for (DealObserver observer : observers) {  // Empty list - loop doesn't execute
        observer.onDealUpdated(...);
    }
}
```

**Implications:**
- The observer attached later would NOT receive retroactive notifications
- Only future events would trigger that observer
- This is expected behavior—observers only receive notifications while attached

**Alternative Design:**
Some implementations maintain an event history and replay it to newly attached observers, but this adds complexity and memory overhead.

### 20. Why does `NotificationObserver` maintain a list of sent notifications? What purpose does this serve?

**Answer:**

```java
private final List<String> sentNotifications;

public List<String> getSentNotifications() {
    return new ArrayList<>(sentNotifications);
}
```

**Purposes:**

**1. Testing and Verification:**
Most important reason—enables unit testing:
```java
@Test
public void testNotificationsAreSent() {
    NotificationObserver observer = new NotificationObserver(...);
    // ... trigger events ...
    assertEquals(3, observer.getSentNotifications().size());
    assertTrue(observer.getSentNotifications().get(0).contains("High-Value Deal"));
}
```

**2. Debugging and Troubleshooting:**
Developers can inspect what notifications were sent during execution to diagnose issues.

**3. Audit Trail:**
Provides a record of communication for compliance or review purposes.

**4. Demonstration Purposes:**
In our demo class, we can show how many notifications were sent:
```java
LOGGER.info("Notifications sent: " + notificationObserver.getSentNotifications().size());
```

**5. Idempotency Checking:**
Could be extended to prevent duplicate notifications for the same event.

**Production Consideration:**
In a real system, storing notifications in memory indefinitely could cause memory issues. Better approaches:
- Write to a database
- Use a circular buffer with size limits
- Only track in development/test modes
- Use proper logging frameworks

## Design Questions

### 21. What are potential drawbacks or challenges of using the Observer Pattern?

**Answer:**

**1. Unexpected Update Chains:**
- One notification can trigger another, creating complex chains
- Can lead to cascading updates that are hard to debug
- Example: Observer A updates something that notifies Observer B, which updates something else...

**2. Memory Leaks:**
If observers aren't properly detached, they remain in memory even when no longer needed:
```java
// Observer holds reference to large object
dealTracker.attach(observer);
// If observer is never detached, it can't be garbage collected
```

**3. No Guaranteed Order:**
- Observers are notified in arbitrary order (usually attachment order)
- Can cause issues if one observer depends on another's actions
- Hard to enforce execution dependencies

**4. Performance Issues:**
- With many observers, notification can be slow
- Each event triggers iteration through all observers
- Synchronous notification blocks the subject

**5. Debugging Complexity:**
- Indirect flow of control makes debugging harder
- Can't easily see who's observing what
- Stack traces can be confusing

**6. No Notification Details:**
- Observers don't know what specifically changed (unless told)
- May need to compare entire state to detect changes
- Can lead to unnecessary processing

**7. Update Overhead:**
- Observers may receive notifications they don't care about
- Need to implement filtering logic
- Can't easily unsubscribe from specific event types

**8. Thread Safety:**
- Concurrent modifications to observer list can cause issues
- Notifications during attach/detach operations can be problematic
- Need synchronization in multi-threaded environments

### 22. How would you modify the implementation to allow observers to specify which types of events they want to receive?

**Answer:**

**Approach 1: Event-Specific Interfaces**
```java
public interface DealCreatedObserver {
    void onDealCreated(Deal deal);
}

public interface DealStatusChangedObserver {
    void onDealStatusChanged(Deal deal, DealStatus oldStatus, DealStatus newStatus);
}

// Subject supports multiple observer types
public interface DealSubject {
    void attachCreatedObserver(DealCreatedObserver observer);
    void attachStatusObserver(DealStatusChangedObserver observer);
    // ...
}
```

**Approach 2: Event Subscription with Enum**
```java
public enum DealEventType {
    CREATED, STATUS_CHANGED, VALUE_UPDATED
}

public void attach(DealObserver observer, Set<DealEventType> interestedEvents) {
    observerSubscriptions.put(observer, interestedEvents);
}

@Override
public void notifyObservers() {
    for (Map.Entry<DealObserver, Set<DealEventType>> entry : observerSubscriptions.entrySet()) {
        if (entry.getValue().contains(currentEventType)) {
            entry.getKey().onDealUpdated(lastAffectedDeal, lastEventType);
        }
    }
}
```

**Approach 3: Predicate-Based Filtering**
```java
public void attach(DealObserver observer, Predicate<DealEvent> filter) {
    observerFilters.put(observer, filter);
}

// Usage:
tracker.attach(observer, event ->
    event.getType() == EventType.CREATED &&
    event.getDeal().getValue().compareTo(threshold) > 0
);
```

**Approach 4: Annotation-Based (Advanced)**
```java
public class MyObserver implements DealObserver {
    @Subscribe(eventType = DealEventType.CREATED)
    public void handleCreated(Deal deal) { }

    @Subscribe(eventType = DealEventType.STATUS_CHANGED)
    public void handleStatusChange(Deal deal) { }
}
```

**Recommended: Approach 2** for balance between flexibility and simplicity.

### 23. Compare and contrast the Observer Pattern with the Mediator Pattern. When would you use one over the other?

**Answer:**

**Observer Pattern:**

**Structure:**
- One-to-many relationship
- Subject knows about observers
- Observers don't know about each other
- Decentralized communication

**Communication:**
```
Subject → Observer 1
       → Observer 2
       → Observer 3
```

**Use Cases:**
- Event notification systems
- UI updates (Model-View)
- Logging and auditing
- Real-time data distribution

**Pros:**
- Simple one-way communication
- Easy to add new observers
- Observers completely independent

**Cons:**
- No coordination between observers
- Can't easily implement complex workflows
- Subject must know notification protocol

**Mediator Pattern:**

**Structure:**
- Many-to-many relationship through central mediator
- Components communicate via mediator
- Components don't know about each other
- Centralized communication

**Communication:**
```
Component 1 ↔ Mediator ↔ Component 2
Component 3 ↔         ↔ Component 4
```

**Use Cases:**
- Complex UI dialogs with interdependent controls
- Workflow coordination
- Chat rooms (mediator routes messages)
- Air traffic control systems

**Pros:**
- Centralized control logic
- Components highly decoupled
- Easy to change interaction logic
- Can implement complex coordination

**Cons:**
- Mediator can become complex
- Single point of failure
- Can become a "god object"

**When to Use Observer:**
- One object's changes should notify multiple others
- Observers act independently
- Simple one-way notification
- Runtime subscription/unsubscription needed

**When to Use Mediator:**
- Multiple objects need to communicate
- Complex interaction logic
- Need to coordinate between components
- Want to centralize control flow

**Example Comparison:**

**Observer (Our Implementation):**
When a deal changes, multiple independent systems respond (commission calculation, audit logging, notifications). Each observer acts independently.

**Mediator (Alternative for Complex Workflows):**
A deal approval workflow where multiple components must coordinate: compliance check → manager approval → finance approval → final processing. The mediator coordinates the sequence and handles failures.

**Can Be Combined:**
Some systems use both—a mediator coordinates complex interactions, and observers listen for state changes within mediated components.

### 24. In our implementation, observers are notified in the order they were attached. How could you modify the design to support prioritized observers?

**Answer:**

**Approach 1: Priority Field in Observer Interface**
```java
public interface DealObserver {
    void onDealUpdated(Deal deal, String eventType);
    int getPriority();  // Higher numbers = higher priority
}

// In Subject:
private final List<DealObserver> observers = new ArrayList<>();

@Override
public void attach(DealObserver observer) {
    observers.add(observer);
    // Sort by priority (descending)
    observers.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
}
```

**Approach 2: Separate Priority Parameter**
```java
public void attach(DealObserver observer, int priority) {
    observerPriorities.put(observer, priority);
    sortObservers();
}

private void sortObservers() {
    observers.sort((o1, o2) -> {
        int p1 = observerPriorities.getOrDefault(o1, 0);
        int p2 = observerPriorities.getOrDefault(o2, 0);
        return Integer.compare(p2, p1);
    });
}
```

**Approach 3: Priority Queues**
```java
private static class PrioritizedObserver implements Comparable<PrioritizedObserver> {
    final DealObserver observer;
    final int priority;

    @Override
    public int compareTo(PrioritizedObserver other) {
        return Integer.compare(other.priority, this.priority);
    }
}

private final PriorityQueue<PrioritizedObserver> observers = new PriorityQueue<>();

@Override
public void notifyObservers() {
    // Create a copy to avoid modification during iteration
    List<PrioritizedObserver> sorted = new ArrayList<>(observers);
    for (PrioritizedObserver po : sorted) {
        po.observer.onDealUpdated(lastAffectedDeal, lastEventType);
    }
}
```

**Approach 4: Named Priority Levels**
```java
public enum ObserverPriority {
    CRITICAL(100),
    HIGH(75),
    NORMAL(50),
    LOW(25);

    final int value;
    ObserverPriority(int value) { this.value = value; }
}

public void attach(DealObserver observer, ObserverPriority priority) {
    // ...
}

// Usage:
tracker.attach(auditObserver, ObserverPriority.CRITICAL);  // Run first
tracker.attach(commissionObserver, ObserverPriority.HIGH);
tracker.attach(notificationObserver, ObserverPriority.NORMAL);
```

**Recommended: Approach 4** for clarity and type safety.

**Real-World Example:**
```java
// Audit logging should happen first (highest priority)
tracker.attach(auditObserver, ObserverPriority.CRITICAL);

// Commission calculation second (needs clean state)
tracker.attach(commissionObserver, ObserverPriority.HIGH);

// Notifications last (after all processing)
tracker.attach(notificationObserver, ObserverPriority.NORMAL);
```

**Considerations:**
- Prioritization adds complexity
- Only needed when observer order matters
- Can make system harder to understand
- Consider if the dependency suggests a different pattern (Chain of Responsibility, Mediator)

### 25. What would be the consequences of having the `notifyObservers()` method call observer updates asynchronously (in separate threads)?

**Answer:**

**Implementation Example:**
```java
@Override
public void notifyObservers() {
    ExecutorService executor = Executors.newCachedThreadPool();

    for (DealObserver observer : observers) {
        executor.submit(() -> {
            try {
                observer.onDealUpdated(lastAffectedDeal, lastEventType);
            } catch (Exception e) {
                LOGGER.severe("Error in observer: " + e.getMessage());
            }
        });
    }

    executor.shutdown();
}
```

**Positive Consequences:**

**1. Improved Performance:**
- Subject doesn't wait for slow observers
- Multiple observers process simultaneously
- Main thread continues immediately

**2. Responsiveness:**
- UI remains responsive during long operations
- Critical path isn't blocked by non-critical observers

**3. Scalability:**
- Better CPU utilization with parallel processing
- Can handle more observers efficiently

**Negative Consequences:**

**1. Race Conditions:**
- Observers might access shared state concurrently
- Need synchronization mechanisms
- Data corruption risks

**2. Unpredictable Ordering:**
```java
// With async, execution order is non-deterministic
tracker.createDeal(deal);
// Audit log might complete before or after commission calculation!
```

**3. Error Handling Complexity:**
- Exceptions in threads harder to catch
- Need sophisticated error handling
- May need callbacks or futures

**4. Resource Management:**
- Thread creation overhead
- Need to manage thread pools
- Risk of thread exhaustion

**5. Debugging Difficulty:**
- Harder to trace execution flow
- Race conditions are intermittent
- Stack traces spread across threads

**6. State Consistency:**
```java
// Problem: Deal might change before observer executes
dealTracker.updateDealValue("DEAL-001", new BigDecimal("100000"));
dealTracker.updateDealValue("DEAL-001", new BigDecimal("150000"));
// Which value will observer see? Depends on timing!
```

**7. Testing Challenges:**
- Non-deterministic behavior
- Harder to write reliable tests
- Need to wait for async operations

**Better Approach: Event Queue**
```java
private final BlockingQueue<DealEvent> eventQueue = new LinkedBlockingQueue<>();

public void notifyObservers() {
    DealEvent event = new DealEvent(lastAffectedDeal, lastEventType);
    eventQueue.offer(event);
}

// Separate thread processes events
private void processEvents() {
    while (running) {
        DealEvent event = eventQueue.take();
        for (DealObserver observer : observers) {
            observer.onDealUpdated(event.getDeal(), event.getEventType());
        }
    }
}
```

**Recommendation:**
- Use synchronous by default (simpler, predictable)
- Use asynchronous only when performance requires it
- If async needed, use proper concurrent data structures and event queuing
- Consider reactive frameworks (RxJava, Project Reactor) for complex async scenarios

## Practical Application Questions

### 26. Scenario: You need to add a new feature that sends SMS messages when high-value deals are closed. How would you implement this using the Observer Pattern without modifying existing code?

**Answer:**

**Step 1: Create the New Observer**
```java
package com.chapman.edu.commissions.patterns.behavioral.observer;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Observer that sends SMS notifications for high-value closed deals.
 * Demonstrates extending the system without modifying existing code (Open/Closed Principle).
 */
public class SmsNotificationObserver implements DealObserver {

    private static final Logger LOGGER = Logger.getLogger(SmsNotificationObserver.class.getName());

    private final BigDecimal highValueThreshold;
    private final SmsService smsService;

    public SmsNotificationObserver(BigDecimal highValueThreshold, SmsService smsService) {
        this.highValueThreshold = highValueThreshold;
        this.smsService = smsService;
    }

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Only send SMS for high-value deals that are closed
        if ("STATUS_CHANGED".equals(eventType) &&
            deal.getStatus() == DealStatus.WON &&
            deal.getValue().compareTo(highValueThreshold) > 0) {

            sendSmsNotification(deal);
        }
    }

    private void sendSmsNotification(Deal deal) {
        String phoneNumber = getSalesRepPhoneNumber(deal.getSalesRepId());
        String message = String.format(
            "Congratulations! Your deal '%s' worth $%s has closed!",
            deal.getTitle(),
            deal.getValue()
        );

        smsService.sendSms(phoneNumber, message);

        LOGGER.info(String.format("[SMS SENT] To: %s | Message: %s",
            phoneNumber, message));
    }

    private String getSalesRepPhoneNumber(String salesRepId) {
        // In real implementation, look up from database
        return "+1-555-" + salesRepId;
    }
}

// SMS Service interface (for dependency injection)
interface SmsService {
    void sendSms(String phoneNumber, String message);
}
```

**Step 2: Register the Observer (NO existing code modified!)**
```java
// In ObserverPatternDemo or main application
public static void main(String[] args) {
    ObservableDealTracker dealTracker = new ObservableDealTracker();

    // Existing observers (unchanged)
    dealTracker.attach(new CommissionCalculationObserver(new BigDecimal("0.10")));
    dealTracker.attach(new AuditLogObserver());
    dealTracker.attach(new NotificationObserver(new BigDecimal("50000")));

    // NEW: Add SMS observer without modifying anything else
    SmsService smsService = new TwilioSmsService(); // Or mock for testing
    dealTracker.attach(new SmsNotificationObserver(new BigDecimal("100000"), smsService));

    // Rest of application continues unchanged
    // ...
}
```

**Why This Works (Open/Closed Principle):**
- No modification to `ObservableDealTracker`
- No modification to existing observers
- No modification to `DealObserver` interface
- System extended purely through addition

**Testing:**
```java
@Test
public void testSmsNotificationSentForHighValueClosedDeals() {
    // Arrange
    MockSmsService mockSms = new MockSmsService();
    SmsNotificationObserver smsObserver = new SmsNotificationObserver(
        new BigDecimal("100000"),
        mockSms
    );

    Deal highValueDeal = new Deal();
    highValueDeal.setId("DEAL-001");
    highValueDeal.setValue(new BigDecimal("150000"));
    highValueDeal.setStatus(DealStatus.WON);

    // Act
    smsObserver.onDealUpdated(highValueDeal, "STATUS_CHANGED");

    // Assert
    assertEquals(1, mockSms.getSentMessages().size());
    assertTrue(mockSms.getSentMessages().get(0).contains("150000"));
}
```

### 27. Scenario: An observer is causing performance issues by taking too long to process updates. How would you identify and address this problem?

**Answer:**

**Step 1: Identify the Problem**

**Approach A: Add Timing Instrumentation**
```java
@Override
public void notifyObservers() {
    LOGGER.info(String.format("Notifying %d observer(s) of event: %s",
            observers.size(), lastEventType));

    for (DealObserver observer : observers) {
        long startTime = System.nanoTime();

        try {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        } catch (Exception e) {
            LOGGER.severe("Error notifying observer " +
                observer.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            long duration = (System.nanoTime() - startTime) / 1_000_000; // Convert to ms

            if (duration > 100) { // Log slow observers (>100ms)
                LOGGER.warning(String.format(
                    "SLOW OBSERVER: %s took %d ms to process %s event",
                    observer.getClass().getSimpleName(),
                    duration,
                    lastEventType
                ));
            }
        }
    }
}
```

**Approach B: Use Profiler**
- Run application with profiler (JProfiler, YourKit, VisualVM)
- Identify which observer method is consuming CPU time
- Analyze hot spots and bottlenecks

**Step 2: Address the Problem**

**Solution 1: Optimize the Observer**
```java
// Before: Slow observer making synchronous external calls
public class SlowNotificationObserver implements DealObserver {
    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // PROBLEM: Synchronous HTTP call blocks notification chain
        httpClient.post("/api/notifications", deal);  // Takes 500ms!
    }
}

// After: Optimized with async processing
public class OptimizedNotificationObserver implements DealObserver {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Non-blocking: submit to thread pool and return immediately
        executor.submit(() -> {
            try {
                httpClient.post("/api/notifications", deal);
            } catch (Exception e) {
                LOGGER.severe("Failed to send notification: " + e.getMessage());
            }
        });
    }
}
```

**Solution 2: Use Event Queue**
```java
public class QueuedNotificationObserver implements DealObserver {
    private final BlockingQueue<DealEvent> eventQueue = new LinkedBlockingQueue<>();

    public QueuedNotificationObserver() {
        // Start background processor thread
        new Thread(this::processEvents).start();
    }

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Just queue the event and return immediately
        eventQueue.offer(new DealEvent(deal, eventType));
    }

    private void processEvents() {
        while (true) {
            try {
                DealEvent event = eventQueue.take();
                // Process slowly here without blocking main thread
                processNotification(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

**Solution 3: Implement Timeout**
```java
@Override
public void notifyObservers() {
    ExecutorService timeoutExecutor = Executors.newCachedThreadPool();

    for (DealObserver observer : observers) {
        Future<?> future = timeoutExecutor.submit(() -> {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        });

        try {
            // Wait maximum 200ms for each observer
            future.get(200, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LOGGER.severe("Observer timeout: " + observer.getClass().getSimpleName());
            future.cancel(true);
        } catch (Exception e) {
            LOGGER.severe("Observer error: " + e.getMessage());
        }
    }

    timeoutExecutor.shutdown();
}
```

**Solution 4: Lazy Processing**
```java
public class LazyAuditLogObserver implements DealObserver {
    private final BlockingQueue<AuditEntry> pendingEntries = new LinkedBlockingQueue<>();

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Just create entry, don't write to database yet
        pendingEntries.offer(new AuditEntry(deal, eventType));
    }

    // Batch write periodically
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void flushToDatabase() {
        List<AuditEntry> batch = new ArrayList<>();
        pendingEntries.drainTo(batch);

        if (!batch.isEmpty()) {
            database.batchInsert(batch); // More efficient than individual inserts
        }
    }
}
```

**Solution 5: Remove or Replace Observer**
```java
// If observer can't be optimized and isn't critical:
dealTracker.detach(slowObserver);

// Or replace with more efficient implementation:
dealTracker.detach(oldSlowObserver);
dealTracker.attach(newFastObserver);
```

**Monitoring Strategy:**
```java
public class PerformanceMonitoringSubject extends ObservableDealTracker {
    private final Map<String, LongSummaryStatistics> observerStats = new ConcurrentHashMap<>();

    @Override
    public void notifyObservers() {
        for (DealObserver observer : getObservers()) {
            String observerName = observer.getClass().getSimpleName();
            long start = System.nanoTime();

            observer.onDealUpdated(getLastDeal(), getLastEventType());

            long duration = System.nanoTime() - start;
            observerStats.computeIfAbsent(observerName, k -> new LongSummaryStatistics())
                        .accept(duration);
        }
    }

    public void printPerformanceReport() {
        observerStats.forEach((name, stats) -> {
            System.out.printf("%s: avg=%dms, max=%dms, count=%d%n",
                name,
                stats.getAverage() / 1_000_000,
                stats.getMax() / 1_000_000,
                stats.getCount()
            );
        });
    }
}
```

### 28. Scenario: You need to ensure that the AuditLogObserver always runs before other observers. How would you modify the implementation to support this requirement?

**Answer:**

**Solution 1: Priority-Based Ordering (Recommended)**

```java
// Step 1: Add priority support to observer interface
public interface PrioritizedDealObserver extends DealObserver {
    default int getPriority() {
        return 50; // Default normal priority
    }
}

// Step 2: Modify AuditLogObserver to declare high priority
public class AuditLogObserver implements PrioritizedDealObserver {
    @Override
    public int getPriority() {
        return 100; // High priority - runs first
    }

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Audit logging logic
    }
}

// Step 3: Update subject to respect priorities
public class ObservableDealTracker implements DealSubject {
    private final List<DealObserver> observers = new ArrayList<>();

    @Override
    public void attach(DealObserver observer) {
        observers.add(observer);
        sortObservers(); // Re-sort after adding
    }

    private void sortObservers() {
        observers.sort((o1, o2) -> {
            int p1 = (o1 instanceof PrioritizedDealObserver)
                ? ((PrioritizedDealObserver) o1).getPriority()
                : 50;
            int p2 = (o2 instanceof PrioritizedDealObserver)
                ? ((PrioritizedDealObserver) o2).getPriority()
                : 50;
            return Integer.compare(p2, p1); // Higher priority first
        });
    }

    @Override
    public void notifyObservers() {
        // Observers already sorted by priority
        for (DealObserver observer : observers) {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        }
    }
}
```

**Solution 2: Separate Observer Categories**

```java
public class ObservableDealTracker implements DealSubject {
    private final List<DealObserver> criticalObservers = new ArrayList<>();
    private final List<DealObserver> normalObservers = new ArrayList<>();

    public void attachCritical(DealObserver observer) {
        criticalObservers.add(observer);
    }

    @Override
    public void attach(DealObserver observer) {
        normalObservers.add(observer);
    }

    @Override
    public void notifyObservers() {
        // Critical observers run first
        for (DealObserver observer : criticalObservers) {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        }

        // Then normal observers
        for (DealObserver observer : normalObservers) {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        }
    }
}

// Usage:
dealTracker.attachCritical(auditLogObserver); // Runs first
dealTracker.attach(commissionObserver);        // Runs after
dealTracker.attach(notificationObserver);      // Runs after
```

**Solution 3: Explicit Ordering with Named Phases**

```java
public enum ObserverPhase {
    PRE_PROCESSING(0),    // Audit, validation
    PROCESSING(1),        // Business logic, calculations
    POST_PROCESSING(2),   // Notifications, side effects
    CLEANUP(3);           // Cleanup, finalization

    final int order;
    ObserverPhase(int order) { this.order = order; }
}

public class PhaseBasedDealTracker implements DealSubject {
    private final Map<ObserverPhase, List<DealObserver>> observersByPhase = new EnumMap<>(ObserverPhase.class);

    public PhaseBasedDealTracker() {
        for (ObserverPhase phase : ObserverPhase.values()) {
            observersByPhase.put(phase, new ArrayList<>());
        }
    }

    public void attach(DealObserver observer, ObserverPhase phase) {
        observersByPhase.get(phase).add(observer);
    }

    @Override
    public void notifyObservers() {
        // Process phases in order
        for (ObserverPhase phase : ObserverPhase.values()) {
            List<DealObserver> observers = observersByPhase.get(phase);

            LOGGER.info("Executing " + phase + " phase with " + observers.size() + " observer(s)");

            for (DealObserver observer : observers) {
                observer.onDealUpdated(lastAffectedDeal, lastEventType);
            }
        }
    }
}

// Usage:
tracker.attach(auditObserver, ObserverPhase.PRE_PROCESSING);      // Runs first
tracker.attach(commissionObserver, ObserverPhase.PROCESSING);      // Runs second
tracker.attach(notificationObserver, ObserverPhase.POST_PROCESSING); // Runs third
```

**Solution 4: Manual Ordering**

```java
public class ObservableDealTracker implements DealSubject {
    private final List<DealObserver> observers = new ArrayList<>();

    public void attachFirst(DealObserver observer) {
        observers.add(0, observer); // Add to beginning
    }

    public void attachLast(DealObserver observer) {
        observers.add(observer); // Add to end
    }

    @Override
    public void attach(DealObserver observer) {
        attachLast(observer); // Default behavior
    }
}

// Usage:
dealTracker.attachFirst(auditLogObserver);     // Always runs first
dealTracker.attach(commissionObserver);         // Normal order
dealTracker.attach(notificationObserver);       // Normal order
```

**Recommended Approach: Solution 3 (Phase-Based)**

This provides the best balance of:
- Clear, self-documenting code
- Explicit ordering guarantees
- Flexibility for future requirements
- Easy to understand and maintain

**Alternative Consideration:**

If ordering is critical, consider whether Observer is the right pattern. Ordering dependencies might suggest:
- **Chain of Responsibility**: Explicit ordered processing
- **Pipeline Pattern**: Sequential stages with explicit flow
- **Mediator Pattern**: Centralized coordination

### 29. Scenario: You want to implement an observer that prevents a deal from being created if certain conditions aren't met. How would you modify the pattern to support validation observers?

**Answer:**

The standard Observer Pattern is notification-only (one-way). To support validation, we need to modify it to allow observers to influence the operation.

**Solution 1: Validation Observer with Veto Power**

```java
// Step 1: Create validation-specific observer interface
public interface DealValidationObserver {
    /**
     * Validates a deal before it's created.
     * @return ValidationResult indicating success or failure with message
     */
    ValidationResult validateDeal(Deal deal);
}

public class ValidationResult {
    private final boolean valid;
    private final String message;

    public static ValidationResult success() {
        return new ValidationResult(true, "Validation passed");
    }

    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }

    private ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}

// Step 2: Implement concrete validators
public class DealValueValidationObserver implements DealValidationObserver {
    private final BigDecimal minimumValue;
    private final BigDecimal maximumValue;

    public DealValueValidationObserver(BigDecimal minimumValue, BigDecimal maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public ValidationResult validateDeal(Deal deal) {
        if (deal.getValue().compareTo(minimumValue) < 0) {
            return ValidationResult.failure(
                String.format("Deal value $%s is below minimum of $%s",
                    deal.getValue(), minimumValue)
            );
        }

        if (deal.getValue().compareTo(maximumValue) > 0) {
            return ValidationResult.failure(
                String.format("Deal value $%s exceeds maximum of $%s",
                    deal.getValue(), maximumValue)
            );
        }

        return ValidationResult.success();
    }
}

public class DealComplianceValidationObserver implements DealValidationObserver {
    @Override
    public ValidationResult validateDeal(Deal deal) {
        // Check if sales rep is authorized
        if (!isAuthorizedSalesRep(deal.getSalesRepId())) {
            return ValidationResult.failure(
                "Sales rep " + deal.getSalesRepId() + " is not authorized"
            );
        }

        // Check for required fields
        if (deal.getTitle() == null || deal.getTitle().trim().isEmpty()) {
            return ValidationResult.failure("Deal title is required");
        }

        return ValidationResult.success();
    }

    private boolean isAuthorizedSalesRep(String salesRepId) {
        // Check against authorization database
        return salesRepId != null && !salesRepId.isEmpty();
    }
}

// Step 3: Modify subject to support validators
public class ObservableDealTracker implements DealSubject {
    private final List<DealObserver> observers = new ArrayList<>();
    private final List<DealValidationObserver> validators = new ArrayList<>();

    public void attachValidator(DealValidationObserver validator) {
        validators.add(validator);
    }

    public void detachValidator(DealValidationObserver validator) {
        validators.remove(validator);
    }

    public void createDeal(Deal deal) throws DealValidationException {
        // Run all validators BEFORE creating the deal
        List<String> errors = new ArrayList<>();

        for (DealValidationObserver validator : validators) {
            ValidationResult result = validator.validateDeal(deal);
            if (!result.isValid()) {
                errors.add(result.getMessage());
            }
        }

        // If any validation failed, throw exception
        if (!errors.isEmpty()) {
            throw new DealValidationException("Deal validation failed", errors);
        }

        // Validation passed - proceed with creation
        deals.add(deal);
        lastAffectedDeal = deal;
        lastEventType = "CREATED";

        LOGGER.info("Deal created (passed all validations): " + deal.getTitle());

        // Now notify regular observers
        notifyObservers();
    }
}

public class DealValidationException extends Exception {
    private final List<String> validationErrors;

    public DealValidationException(String message, List<String> validationErrors) {
        super(message + ": " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
}
```

**Solution 2: Event-Based with Cancellable Events**

```java
public class DealEvent {
    private final Deal deal;
    private final String eventType;
    private boolean cancelled = false;
    private String cancellationReason;

    public void cancel(String reason) {
        this.cancelled = true;
        this.cancellationReason = reason;
    }

    public boolean isCancelled() { return cancelled; }
    public String getCancellationReason() { return cancellationReason; }

    // getters...
}

public interface DealObserver {
    void onDealEvent(DealEvent event);
}

public class ObservableDealTracker {
    public void createDeal(Deal deal) {
        DealEvent event = new DealEvent(deal, "BEFORE_CREATE");

        // Notify observers - they can cancel the event
        for (DealObserver observer : observers) {
            observer.onDealEvent(event);

            if (event.isCancelled()) {
                LOGGER.warning("Deal creation cancelled: " + event.getCancellationReason());
                return; // Don't create the deal
            }
        }

        // Event not cancelled - proceed
        deals.add(deal);
        notifyObservers(new DealEvent(deal, "CREATED"));
    }
}

// Validator implementation
public class ValidationObserver implements DealObserver {
    @Override
    public void onDealEvent(DealEvent event) {
        if (event.getEventType().equals("BEFORE_CREATE")) {
            if (event.getDeal().getValue().compareTo(BigDecimal.ZERO) <= 0) {
                event.cancel("Deal value must be positive");
            }
        }
    }
}
```

**Solution 3: Chain of Responsibility (Alternative Pattern)**

```java
public interface DealValidator {
    ValidationResult validate(Deal deal);
    void setNext(DealValidator next);
}

public abstract class AbstractDealValidator implements DealValidator {
    private DealValidator next;

    @Override
    public void setNext(DealValidator next) {
        this.next = next;
    }

    protected ValidationResult validateNext(Deal deal) {
        if (next != null) {
            return next.validate(deal);
        }
        return ValidationResult.success();
    }
}

public class DealValueValidator extends AbstractDealValidator {
    @Override
    public ValidationResult validate(Deal deal) {
        if (deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            return ValidationResult.failure("Value must be positive");
        }
        return validateNext(deal);
    }
}

// Usage
DealValidator chain = new DealValueValidator();
chain.setNext(new DealComplianceValidator());
chain.setNext(new DealAuthorizationValidator());

ValidationResult result = chain.validate(deal);
if (result.isValid()) {
    dealTracker.createDeal(deal);
}
```

**Recommended: Solution 1 (Separate Validation Observers)**

**Pros:**
- ✅ Clear separation between validation and notification
- ✅ Validators run before state changes
- ✅ Comprehensive error collection
- ✅ Type-safe and explicit

**Usage Example:**

```java
public static void main(String[] args) {
    ObservableDealTracker tracker = new ObservableDealTracker();

    // Attach validators
    tracker.attachValidator(new DealValueValidationObserver(
        new BigDecimal("1000"),    // Min: $1,000
        new BigDecimal("1000000")  // Max: $1,000,000
    ));
    tracker.attachValidator(new DealComplianceValidationObserver());

    // Attach regular observers
    tracker.attach(new AuditLogObserver());
    tracker.attach(new CommissionCalculationObserver(new BigDecimal("0.10")));

    // Try to create valid deal
    try {
        Deal validDeal = new Deal("Valid Deal", new BigDecimal("50000"), "REP-001");
        tracker.createDeal(validDeal); // Success - validators pass, observers notified
    } catch (DealValidationException e) {
        System.err.println("Validation failed: " + e.getMessage());
    }

    // Try to create invalid deal
    try {
        Deal invalidDeal = new Deal("Invalid Deal", new BigDecimal("500"), "REP-001");
        tracker.createDeal(invalidDeal); // Fails - value too low
    } catch (DealValidationException e) {
        System.err.println("Validation failed: " + e.getValidationErrors());
        // Output: ["Deal value $500 is below minimum of $1000"]
    }
}
```

### 30. Scenario: How would you test the `ObservableDealTracker` to ensure it properly notifies all observers? Describe a JUnit test approach.

**Answer:**

**Comprehensive Testing Strategy:**

```java
package com.chapman.edu.commissions.patterns.behavioral.observer;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for ObservableDealTracker.
 * Tests notification behavior, observer management, and edge cases.
 */
public class ObservableDealTrackerTest {

    private ObservableDealTracker tracker;
    private DealObserver mockObserver1;
    private DealObserver mockObserver2;
    private DealObserver mockObserver3;

    @BeforeEach
    void setUp() {
        tracker = new ObservableDealTracker();
        mockObserver1 = mock(DealObserver.class);
        mockObserver2 = mock(DealObserver.class);
        mockObserver3 = mock(DealObserver.class);
    }

    // Test 1: Basic Notification
    @Test
    @DisplayName("Should notify all attached observers when deal is created")
    void testNotifiesAllObserversOnDealCreation() {
        // Arrange
        tracker.attach(mockObserver1);
        tracker.attach(mockObserver2);
        tracker.attach(mockObserver3);

        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);

        // Assert - each observer notified exactly once
        verify(mockObserver1, times(1)).onDealUpdated(eq(deal), eq("CREATED"));
        verify(mockObserver2, times(1)).onDealUpdated(eq(deal), eq("CREATED"));
        verify(mockObserver3, times(1)).onDealUpdated(eq(deal), eq("CREATED"));
    }

    // Test 2: Correct Event Data
    @Test
    @DisplayName("Should pass correct deal and event type to observers")
    void testPassesCorrectDataToObservers() {
        // Arrange
        tracker.attach(mockObserver1);
        Deal deal = createTestDeal("DEAL-001", "Enterprise License", "50000");

        // Use ArgumentCaptor to capture the arguments
        ArgumentCaptor<Deal> dealCaptor = ArgumentCaptor.forClass(Deal.class);
        ArgumentCaptor<String> eventCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        tracker.createDeal(deal);

        // Assert
        verify(mockObserver1).onDealUpdated(dealCaptor.capture(), eventCaptor.capture());

        Deal capturedDeal = dealCaptor.getValue();
        String capturedEvent = eventCaptor.getValue();

        assertEquals("DEAL-001", capturedDeal.getId());
        assertEquals("Enterprise License", capturedDeal.getTitle());
        assertEquals(new BigDecimal("50000"), capturedDeal.getValue());
        assertEquals("CREATED", capturedEvent);
    }

    // Test 3: No Observers
    @Test
    @DisplayName("Should handle creation with no observers attached without error")
    void testCreateDealWithNoObservers() {
        // Arrange
        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> tracker.createDeal(deal));
        assertEquals(1, tracker.getDeals().size());
    }

    // Test 4: Attach/Detach
    @Test
    @DisplayName("Should not notify detached observers")
    void testDetachedObserverNotNotified() {
        // Arrange
        tracker.attach(mockObserver1);
        tracker.attach(mockObserver2);
        tracker.detach(mockObserver1); // Remove first observer

        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);

        // Assert
        verify(mockObserver1, never()).onDealUpdated(any(), any());
        verify(mockObserver2, times(1)).onDealUpdated(any(), any());
    }

    // Test 5: Multiple Events
    @Test
    @DisplayName("Should notify observers for different event types")
    void testMultipleEventTypes() {
        // Arrange
        tracker.attach(mockObserver1);
        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);
        tracker.updateDealStatus("DEAL-001", DealStatus.WON);
        tracker.updateDealValue("DEAL-001", new BigDecimal("15000"));

        // Assert
        verify(mockObserver1, times(1)).onDealUpdated(any(), eq("CREATED"));
        verify(mockObserver1, times(1)).onDealUpdated(any(), eq("STATUS_CHANGED"));
        verify(mockObserver1, times(1)).onDealUpdated(any(), eq("VALUE_UPDATED"));
    }

    // Test 6: Observer Exception Handling
    @Test
    @DisplayName("Should continue notifying other observers if one throws exception")
    void testExceptionInObserverDoesNotStopOthers() {
        // Arrange
        DealObserver faultyObserver = mock(DealObserver.class);
        doThrow(new RuntimeException("Observer error"))
            .when(faultyObserver).onDealUpdated(any(), any());

        tracker.attach(faultyObserver);
        tracker.attach(mockObserver1);
        tracker.attach(mockObserver2);

        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);

        // Assert - other observers still notified despite first one failing
        verify(mockObserver1, times(1)).onDealUpdated(any(), any());
        verify(mockObserver2, times(1)).onDealUpdated(any(), any());
    }

    // Test 7: Duplicate Attach Prevention
    @Test
    @DisplayName("Should not add same observer twice")
    void testDuplicateObserverNotAdded() {
        // Arrange
        tracker.attach(mockObserver1);
        tracker.attach(mockObserver1); // Try to add same observer again

        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);

        // Assert - observer should only be notified once
        verify(mockObserver1, times(1)).onDealUpdated(any(), any());
        assertEquals(1, tracker.getObserverCount());
    }

    // Test 8: Real Observer Integration Test
    @Test
    @DisplayName("Integration test with real observers")
    void testWithRealObservers() {
        // Arrange
        TestObserver observer1 = new TestObserver("Observer1");
        TestObserver observer2 = new TestObserver("Observer2");

        tracker.attach(observer1);
        tracker.attach(observer2);

        Deal deal = createTestDeal("DEAL-001", "Test Deal", "10000");

        // Act
        tracker.createDeal(deal);
        tracker.updateDealStatus("DEAL-001", DealStatus.WON);

        // Assert
        assertEquals(2, observer1.getNotificationCount());
        assertEquals(2, observer2.getNotificationCount());
        assertTrue(observer1.hasReceivedEvent("CREATED"));
        assertTrue(observer1.hasReceivedEvent("STATUS_CHANGED"));
    }

    // Test 9: Observer State After Multiple Operations
    @Test
    @DisplayName("Should maintain correct observer count through attach/detach operations")
    void testObserverCountCorrectness() {
        // Act & Assert
        assertEquals(0, tracker.getObserverCount());

        tracker.attach(mockObserver1);
        assertEquals(1, tracker.getObserverCount());

        tracker.attach(mockObserver2);
        assertEquals(2, tracker.getObserverCount());

        tracker.detach(mockObserver1);
        assertEquals(1, tracker.getObserverCount());

        tracker.detach(mockObserver2);
        assertEquals(0, tracker.getObserverCount());
    }

    // Test 10: Null Safety
    @Test
    @DisplayName("Should throw exception when attaching null observer")
    void testNullObserverThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            tracker.attach(null);
        });
    }

    // Helper Methods

    private Deal createTestDeal(String id, String title, String value) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle(title);
        deal.setValue(new BigDecimal(value));
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("REP-001");
        return deal;
    }

    /**
     * Test double for integration tests - tracks notifications received.
     */
    private static class TestObserver implements DealObserver {
        private final String name;
        private int notificationCount = 0;
        private final List<String> receivedEvents = new ArrayList<>();
        private final List<Deal> receivedDeals = new ArrayList<>();

        public TestObserver(String name) {
            this.name = name;
        }

        @Override
        public void onDealUpdated(Deal deal, String eventType) {
            notificationCount++;
            receivedEvents.add(eventType);
            receivedDeals.add(deal);
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public boolean hasReceivedEvent(String eventType) {
            return receivedEvents.contains(eventType);
        }

        public List<String> getReceivedEvents() {
            return new ArrayList<>(receivedEvents);
        }
    }
}
```

**Testing Real Observers:**

```java
/**
 * Test the concrete observer implementations.
 */
public class ConcreteObserversTest {

    @Test
    @DisplayName("CommissionCalculationObserver calculates commission for won deals")
    void testCommissionCalculation() {
        // Arrange
        CommissionCalculationObserver observer =
            new CommissionCalculationObserver(new BigDecimal("0.10"));

        Deal deal = new Deal();
        deal.setId("DEAL-001");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("100000"));
        deal.setStatus(DealStatus.WON);

        // Act
        observer.onDealUpdated(deal, "STATUS_CHANGED");

        // Assert - check logs or captured output
        // Expected commission: $10,000 (10% of $100,000)
    }

    @Test
    @DisplayName("AuditLogObserver records all events")
    void testAuditLogging() {
        // Arrange
        AuditLogObserver observer = new AuditLogObserver();

        Deal deal1 = new Deal();
        deal1.setId("DEAL-001");
        deal1.setValue(new BigDecimal("50000"));

        Deal deal2 = new Deal();
        deal2.setId("DEAL-002");
        deal2.setValue(new BigDecimal("75000"));

        // Act
        observer.onDealUpdated(deal1, "CREATED");
        observer.onDealUpdated(deal1, "STATUS_CHANGED");
        observer.onDealUpdated(deal2, "CREATED");

        // Assert
        assertEquals(3, observer.getLogCount());
        List<String> log = observer.getAuditLog();
        assertTrue(log.get(0).contains("CREATED"));
        assertTrue(log.get(0).contains("DEAL-001"));
        assertTrue(log.get(1).contains("STATUS_CHANGED"));
        assertTrue(log.get(2).contains("DEAL-002"));
    }

    @Test
    @DisplayName("NotificationObserver sends notifications for high-value deals")
    void testNotifications() {
        // Arrange
        NotificationObserver observer =
            new NotificationObserver(new BigDecimal("50000")); // $50k threshold

        Deal highValueDeal = new Deal();
        highValueDeal.setId("DEAL-001");
        highValueDeal.setValue(new BigDecimal("100000")); // Above threshold
        highValueDeal.setSalesRepId("REP-001");

        Deal normalDeal = new Deal();
        normalDeal.setId("DEAL-002");
        normalDeal.setValue(new BigDecimal("25000")); // Below threshold
        normalDeal.setSalesRepId("REP-002");

        // Act
        observer.onDealUpdated(highValueDeal, "CREATED");
        observer.onDealUpdated(normalDeal, "CREATED");

        // Assert
        List<String> notifications = observer.getSentNotifications();
        assertEquals(2, notifications.size());

        // First notification should be for high-value to managers
        assertTrue(notifications.get(0).contains("sales-managers@company.com"));
        assertTrue(notifications.get(0).contains("High-Value Deal Alert"));

        // Second notification should be normal to team
        assertTrue(notifications.get(1).contains("sales-team@company.com"));
        assertTrue(notifications.get(1).contains("New Deal Created"));
    }
}
```

**Running Tests:**

```bash
# Run all observer pattern tests
mvn test -Dtest=ObservableDealTrackerTest

# Run with coverage
mvn clean test jacoco:report -Dtest=ObservableDealTrackerTest

# View coverage report
# Open target/site/jacoco/index.html
```

**Test Coverage Goals:**
- All notification paths
- Attach/detach operations
- Multiple observers
- Exception handling
- Edge cases (no observers, null values)
- Different event types
- Real observer behavior

## Critical Thinking Questions

### 31. The Observer Pattern can lead to "cascading updates" where one notification triggers another, potentially creating an infinite loop. How could this happen in our implementation, and how would you prevent it?

**Answer:**

**How Cascading Updates Can Occur:**

**Scenario 1: Observer Modifies Subject**
```java
public class ProblematicObserver implements DealObserver {
    private final ObservableDealTracker tracker;

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        if (eventType.equals("VALUE_UPDATED")) {
            // PROBLEM: Modifying the deal triggers another notification!
            tracker.updateDealStatus(deal.getId(), DealStatus.UNDER_REVIEW);
            // This triggers STATUS_CHANGED notification
            // If another observer updates value, we get infinite loop!
        }
    }
}
```

**Scenario 2: Circular Observer Dependency**
```java
public class ObserverA implements DealObserver {
    private SubjectB subjectB;

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Notification from Subject A triggers update to Subject B
        subjectB.updateRelatedDeal(deal);
    }
}

public class ObserverB implements DealObserver {
    private SubjectA subjectA;

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // Notification from Subject B triggers update to Subject A
        // INFINITE LOOP: A → B → A → B → ...
        subjectA.updateRelatedDeal(deal);
    }
}
```

**Scenario 3: Self-Triggering Observer**
```java
public class RecursiveObserver implements DealObserver {
    private final ObservableDealTracker tracker;
    private int counter = 0;

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        counter++;
        if (counter < 1000) {  // "Safety" limit - but still problematic!
            // Each notification triggers another update
            tracker.updateDealValue(deal.getId(),
                deal.getValue().add(BigDecimal.ONE));
        }
    }
}
```

**Prevention Strategies:**

**Strategy 1: Event Replay Prevention**
```java
public class ObservableDealTracker implements DealSubject {
    private final ThreadLocal<Boolean> isNotifying = ThreadLocal.withInitial(() -> false);
    private final Set<String> recentEvents = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void notifyObservers() {
        // Prevent recursive notifications
        if (isNotifying.get()) {
            LOGGER.warning("Attempted recursive notification - blocked");
            return;
        }

        String eventKey = lastEventType + ":" + lastAffectedDeal.getId();

        // Prevent duplicate events in short timeframe
        if (recentEvents.contains(eventKey)) {
            LOGGER.warning("Duplicate event detected - blocked: " + eventKey);
            return;
        }

        try {
            isNotifying.set(true);
            recentEvents.add(eventKey);

            // Normal notification logic
            for (DealObserver observer : observers) {
                observer.onDealUpdated(lastAffectedDeal, lastEventType);
            }
        } finally {
            isNotifying.set(false);

            // Clear recent events after short delay
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    recentEvents.remove(eventKey);
                }
            }, 100); // 100ms window
        }
    }
}
```

**Strategy 2: Maximum Depth Limiting**
```java
public class ObservableDealTracker implements DealSubject {
    private final ThreadLocal<Integer> notificationDepth = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_NOTIFICATION_DEPTH = 5;

    @Override
    public void notifyObservers() {
        int currentDepth = notificationDepth.get();

        if (currentDepth >= MAX_NOTIFICATION_DEPTH) {
            throw new IllegalStateException(
                "Maximum notification depth exceeded - possible infinite loop detected!"
            );
        }

        try {
            notificationDepth.set(currentDepth + 1);

            for (DealObserver observer : observers) {
                observer.onDealUpdated(lastAffectedDeal, lastEventType);
            }
        } finally {
            notificationDepth.set(currentDepth);
        }
    }
}
```

**Strategy 3: Immutable Event Objects**
```java
// Make events immutable so observers can't trigger changes
public final class DealEvent {
    private final Deal dealSnapshot;  // Copy of deal at time of event
    private final String eventType;
    private final LocalDateTime timestamp;

    public DealEvent(Deal deal, String eventType) {
        this.dealSnapshot = createSnapshot(deal);  // Deep copy
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

    private Deal createSnapshot(Deal deal) {
        // Create defensive copy
        Deal copy = new Deal();
        copy.setId(deal.getId());
        copy.setTitle(deal.getTitle());
        copy.setValue(deal.getValue());
        copy.setStatus(deal.getStatus());
        return copy;
    }

    // Only getters, no setters - immutable
    public Deal getDeal() { return dealSnapshot; }
    public String getEventType() { return eventType; }
}
```

**Strategy 4: Asynchronous Processing**
```java
public class ObservableDealTracker implements DealSubject {
    private final BlockingQueue<DealEvent> eventQueue = new LinkedBlockingQueue<>();
    private final Set<String> processingEvents = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void notifyObservers() {
        DealEvent event = new DealEvent(lastAffectedDeal, lastEventType);
        eventQueue.offer(event);
    }

    // Background thread processes events
    private void processEventQueue() {
        while (running) {
            try {
                DealEvent event = eventQueue.take();
                String eventKey = event.getEventType() + ":" + event.getDeal().getId();

                // Skip if already processing this event
                if (!processingEvents.add(eventKey)) {
                    continue;
                }

                try {
                    for (DealObserver observer : observers) {
                        observer.onDealUpdated(event.getDeal(), event.getEventType());
                    }
                } finally {
                    processingEvents.remove(eventKey);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

**Strategy 5: Design Guidelines**
```java
/**
 * OBSERVER PATTERN BEST PRACTICES TO PREVENT CASCADING UPDATES:
 *
 * 1. Observers should NOT modify the subject during notification
 * 2. If modification needed, queue it for later execution
 * 3. Use event versioning or timestamps to detect duplicates
 * 4. Implement notification depth limiting
 * 5. Log all notifications for debugging
 * 6. Make event data immutable
 * 7. Document observer responsibilities clearly
 */

public class SafeObserver implements DealObserver {
    private final BlockingQueue<Runnable> deferredActions = new LinkedBlockingQueue<>();

    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        // DON'T modify subject directly
        // Instead, queue action for later
        if (needsFollowUpAction(deal, eventType)) {
            deferredActions.offer(() -> {
                // This runs AFTER notification completes
                performFollowUpAction(deal);
            });
        }
    }

    public void processDeferredActions() {
        Runnable action;
        while ((action = deferredActions.poll()) != null) {
            action.run();
        }
    }
}
```

**Recommended Combination:**
1. Use Strategy 2 (depth limiting) for safety
2. Use Strategy 3 (immutable events) for correctness
3. Follow Strategy 5 (design guidelines) for prevention

**Testing for Cascades:**
```java
@Test
@DisplayName("Should detect and prevent infinite notification loops")
void testInfiniteLoopPrevention() {
    ObservableDealTracker tracker = new ObservableDealTracker();

    // Create observer that tries to trigger infinite loop
    DealObserver recursiveObserver = new DealObserver() {
        @Override
        public void onDealUpdated(Deal deal, String eventType) {
            // Try to trigger another notification
            tracker.updateDealValue(deal.getId(),
                deal.getValue().add(BigDecimal.ONE));
        }
    };

    tracker.attach(recursiveObserver);
    Deal deal = createTestDeal("DEAL-001", "Test", "10000");
    tracker.createDeal(deal);

    // Should throw exception or prevent loop
    assertThrows(IllegalStateException.class, () -> {
        tracker.updateDealValue("DEAL-001", new BigDecimal("20000"));
    });
}
```

### 32. Some developers argue that the Observer Pattern violates the "Law of Demeter" (principle of least knowledge). Do you agree? Why or why not?

**Answer:**

**Law of Demeter (LoD) Recap:**
"Only talk to your immediate friends" - an object should only call methods on:
1. Itself
2. Objects passed as parameters
3. Objects it creates
4. Its direct component objects

**The Argument Against Observer Pattern:**

**Violation Claim:**
```java
// Observer receives a Deal object
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Observer then accesses Deal's internals
    BigDecimal value = deal.getValue();              // Accessing Deal
    DealStatus status = deal.getStatus();             // Accessing Deal
    String salesRepId = deal.getSalesRepId();         // Accessing Deal

    // Even worse - might access deeply nested objects
    String productName = deal.getProducts().get(0).getName();  // Chain of calls!
}
```

The observer is "reaching into" the Deal object and calling multiple methods on it, potentially violating LoD if we interpret it strictly.

**Counter-Arguments (Why It Doesn't Violate LoD):**

**1. Deal is a Parameter**
```java
public void onDealUpdated(Deal deal, String eventType)
                        // ↑ Deal is explicitly passed as parameter
```
According to LoD, it's acceptable to call methods on objects passed as parameters. The Deal is provided to the observer specifically for this purpose.

**2. Deal is a Data Transfer Object (DTO)**
```java
// Deal is designed to expose its data
public class Deal {
    public BigDecimal getValue() { ... }  // Intentional public API
    public DealStatus getStatus() { ... }
    // These are part of Deal's contract
}
```
The Deal class is designed with a public API intended to be used by observers. This is not "reaching into" internals—it's using the published interface.

**3. Alternative Would Be Worse**
```java
// If we tried to "fix" the LoD violation:
@Override
public void onDealUpdated(
    String dealId,
    String dealTitle,
    BigDecimal dealValue,
    DealStatus dealStatus,
    String salesRepId,
    List<DealProduct> products,
    LocalDate closeDate,
    // ... 20 more parameters ...
    String eventType
) {
    // This is clearly worse!
}
```
Passing individual fields would lead to parameter explosion and tight coupling to the Deal structure.

**4. Observer Pattern Promotes Loose Coupling**
```java
// Subject doesn't know observer concrete types
private final List<DealObserver> observers;  // Interface, not concrete class

// Observer doesn't know subject concrete type
public interface DealObserver {
    void onDealUpdated(Deal deal, String eventType);
}
```
The pattern reduces coupling between subject and observer, which is the spirit of LoD even if the letter might be debatable.

**My Position: It Depends on Implementation**

**Acceptable (Doesn't Violate LoD):**
```java
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Using Deal's direct methods - OK
    if (deal.getValue().compareTo(threshold) > 0) {
        sendNotification(deal.getTitle(), deal.getValue());
    }
}
```

**Questionable (Potentially Violates LoD):**
```java
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Deep chaining - problematic
    String managerEmail = deal.getSalesRep()
                             .getTeam()
                             .getManager()
                             .getEmail();  // Too many dots!

    // Accessing implementation details
    deal.getInternalStateMap().get("private_field");  // Definitely wrong!
}
```

**Better Approaches:**

**Approach 1: Rich Events**
```java
// Instead of just passing Deal, create specific event objects
public class DealClosedEvent {
    private final String dealId;
    private final String dealTitle;
    private final BigDecimal dealValue;
    private final String salesRepId;

    // Exactly the data observers need, nothing more
}

@Override
public void onDealClosed(DealClosedEvent event) {
    // Only uses event's direct methods
    BigDecimal commission = event.getDealValue().multiply(rate);
}
```

**Approach 2: Facade Methods on Deal**
```java
public class Deal {
    // Instead of exposing all internals, provide high-level methods
    public boolean isHighValue(BigDecimal threshold) {
        return this.value.compareTo(threshold) > 0;
    }

    public String formatSummary() {
        return String.format("%s: $%s (%s)", title, value, status);
    }

    public boolean qualifiesForBonus() {
        return status == DealStatus.WON && value.compareTo(BONUS_THRESHOLD) > 0;
    }
}

// Observer uses these high-level methods
@Override
public void onDealUpdated(Deal deal, String eventType) {
    if (deal.isHighValue(threshold)) {  // One method call, clear intent
        sendAlert(deal.formatSummary());
    }
}
```

**Approach 3: Tell, Don't Ask**
```java
// Instead of asking Deal for data and making decisions:
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // BAD: Asking for data
    if (deal.getValue().compareTo(threshold) > 0 &&
        deal.getStatus() == DealStatus.WON) {
        calculateCommission(deal.getValue());
    }
}

// Better: Tell Deal what to do
@Override
public void onDealUpdated(Deal deal, String eventType) {
    // Deal calculates and returns commission if applicable
    Optional<BigDecimal> commission = deal.calculateCommissionIfEligible(rate);
    commission.ifPresent(this::recordCommission);
}
```

**Conclusion:**

**I partially agree** with the criticism, but with nuance:

**Agrees:** Deep property chains violate LoD
- `deal.getSalesRep().getTeam().getManager()` is problematic
- Accessing private/internal state is wrong

**Disagrees:** Direct use of public API doesn't violate LoD
- `deal.getValue()` is fine—it's the published interface
- Deal is passed as a parameter specifically for this purpose
- Alternative approaches would create worse problems

**Recommendation:**
- Use Observer Pattern but design thoughtfully
- Provide facade methods for complex operations
- Use specific event objects for complex scenarios
- Avoid deep property chaining
- Follow "Tell, Don't Ask" principle where possible

The Observer Pattern is a valuable tool that, when used properly, doesn't violate the spirit of the Law of Demeter.

### 33. Modern frameworks often use event buses or reactive streams instead of the traditional Observer Pattern. What advantages do these approaches offer?

**Answer:**

**Traditional Observer Pattern Limitations:**
```java
// Traditional approach
public class ObservableDealTracker {
    private final List<DealObserver> observers = new ArrayList<>();

    public void createDeal(Deal deal) {
        deals.add(deal);
        // Tightly coupled to notification logic
        for (DealObserver observer : observers) {
            observer.onDealUpdated(deal, "CREATED");
        }
    }
}
```

**Modern Alternatives:**

---

**1. Event Bus (e.g., Google Guava EventBus, Spring ApplicationEventPublisher)**

**Advantages:**

**A. Decoupling**
```java
// Publisher doesn't know about listeners
@Component
public class DealService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void createDeal(Deal deal) {
        dealRepository.save(deal);
        // Just publish - don't know who's listening
        eventPublisher.publishEvent(new DealCreatedEvent(deal));
    }
}

// Listener registers itself
@Component
public class CommissionCalculator {
    @EventListener
    public void handleDealCreated(DealCreatedEvent event) {
        // Calculate commission
    }
}
```

**B. No Manual Registration**
```java
// Traditional Observer: Manual wiring
tracker.attach(observer1);
tracker.attach(observer2);

// Event Bus: Auto-discovery via annotations
@EventListener  // Automatically registered!
public void handleEvent(DealCreatedEvent event) { }
```

**C. Type-Safe Events**
```java
// Different event types, different handlers
@EventListener
public void handleCreated(DealCreatedEvent event) { }

@EventListener
public void handleClosed(DealClosedEvent event) { }

@EventListener
public void handleAnyDeal(DealEvent event) { }  // Base class
```

**D. Event Filtering**
```java
@EventListener(condition = "#event.deal.value > 50000")
public void handleHighValueDeal(DealCreatedEvent event) {
    // Only receives high-value deals
}
```

**E. Async Support**
```java
@Async
@EventListener
public void handleDealAsync(DealCreatedEvent event) {
    // Runs in separate thread automatically
}
```

---

**2. Reactive Streams (e.g., RxJava, Project Reactor, Java 9 Flow API)**

**Advantages:**

**A. Functional Composition**
```java
// Traditional Observer: Imperative
public void onDealUpdated(Deal deal, String eventType) {
    if (eventType.equals("CREATED") && deal.getValue().compareTo(threshold) > 0) {
        processHighValueDeal(deal);
    }
}

// Reactive: Declarative
Flux<Deal> dealStream = dealPublisher.getDeals();

dealStream
    .filter(deal -> deal.getValue().compareTo(threshold) > 0)
    .map(this::calculateCommission)
    .flatMap(this::saveToDatabase)
    .subscribe(commission -> LOGGER.info("Commission saved: " + commission));
```

**B. Backpressure Handling**
```java
// Problem with traditional Observer: Fast producer, slow consumer
dealStream
    .onBackpressureBuffer(1000)  // Buffer up to 1000 events
    .onBackpressureDrop()         // Or drop events if overwhelmed
    .subscribe(this::processSlowly);
```

**C. Error Handling**
```java
dealStream
    .map(this::processRiskily)
    .onErrorContinue((error, deal) -> {
        // Error in one event doesn't stop the stream
        LOGGER.error("Failed to process " + deal, error);
    })
    .subscribe(result -> LOGGER.info("Success: " + result));
```

**D. Time-Based Operations**
```java
// Buffer events and process in batches
dealStream
    .buffer(Duration.ofSeconds(5))  // Collect 5 seconds worth
    .subscribe(deals -> processBatch(deals));

// Rate limiting
dealStream
    .throttleFirst(Duration.ofSeconds(1))  // Max 1 per second
    .subscribe(this::processExpensive);

// Timeouts
dealStream
    .timeout(Duration.ofSeconds(10))
    .subscribe(this::process);
```

**E. Complex Event Processing**
```java
// Combine multiple streams
Flux<Deal> deals = dealPublisher.getDeals();
Flux<User> users = userPublisher.getUsers();

Flux.zip(deals, users)
    .filter(tuple -> tuple.getT1().getSalesRepId().equals(tuple.getT2().getId()))
    .subscribe(tuple -> {
        Deal deal = tuple.getT1();
        User user = tuple.getT2();
        sendNotification(user, deal);
    });

// Window operations
dealStream
    .window(100)  // Group into windows of 100
    .flatMap(window -> window.reduce(BigDecimal.ZERO,
        (sum, deal) -> sum.add(deal.getValue())))
    .subscribe(total -> LOGGER.info("Batch total: " + total));
```

**F. Hot vs. Cold Streams**
```java
// Cold: Each subscriber gets all events from the start
Flux<Deal> coldStream = Flux.fromIterable(historicalDeals);

// Hot: Subscribers only get events from subscription time onward
Flux<Deal> hotStream = dealProcessor
    .publish()  // Convert to hot stream
    .autoConnect();  // Start emitting immediately

// Supports replay for late subscribers
Flux<Deal> replayStream = hotStream.replay(10);  // Last 10 events
```

---

**3. Message Queues (e.g., RabbitMQ, Kafka)**

**Advantages:**

**A. Persistence**
```java
// Traditional Observer: Events lost if observer is down
// Message Queue: Events persisted until consumed

kafkaTemplate.send("deal-events", new DealCreatedEvent(deal));
// Event stored in Kafka until consumer processes it
```

**B. Scalability**
```java
// Multiple consumers process events in parallel
@KafkaListener(topics = "deal-events", groupId = "commission-calculators")
public void handleDeal(DealCreatedEvent event) {
    // Multiple instances load-balance automatically
}
```

**C. Replay Capability**
```java
// Can reprocess historical events
consumer.seek(partition, earliestOffset);
// Replay all events from the beginning
```

**D. Cross-Service Communication**
```java
// Services in different languages/platforms
// Java service publishes
kafkaTemplate.send("deal-events", event);

// Python service consumes
consumer.subscribe(['deal-events'])
for message in consumer:
    process_deal(message.value)
```

---

**Comparison Table:**

| Feature | Traditional Observer | Event Bus | Reactive Streams | Message Queue |
|---------|---------------------|-----------|------------------|---------------|
| **Coupling** | Tight | Loose | Loose | Very Loose |
| **Setup Complexity** | Low | Medium | High | High |
| **Type Safety** | Medium | High | High | Low |
| **Async Support** | Manual | Easy | Built-in | Built-in |
| **Backpressure** | No | No | Yes | Yes |
| **Error Handling** | Manual | Good | Excellent | Excellent |
| **Scalability** | Low | Medium | High | Very High |
| **Persistence** | No | No | No | Yes |
| **Cross-Process** | No | Limited | Limited | Yes |
| **Time Operations** | No | Limited | Excellent | Limited |
| **Learning Curve** | Low | Low | High | Medium |

---

**When to Use Each:**

**Traditional Observer:**
```java
// Simple, in-memory, synchronous notifications
✅ GUI events
✅ Simple business logic
✅ Educational purposes
✅ Tight performance requirements
❌ Complex async workflows
❌ Distributed systems
```

**Event Bus:**
```java
// Spring applications, moderate complexity
✅ Decoupled components in monolith
✅ Annotation-based configuration
✅ Framework integration
❌ Cross-service communication
❌ Complex stream processing
```

**Reactive Streams:**
```java
// High-throughput, complex processing
✅ Real-time data processing
✅ Backpressure handling needed
✅ Complex event composition
✅ Asynchronous workflows
❌ Simple use cases (overkill)
❌ Team unfamiliar with reactive
```

**Message Queue:**
```java
// Distributed systems, mission-critical
✅ Microservices architecture
✅ Event sourcing
✅ Cross-platform communication
✅ Guaranteed delivery needed
❌ Simple in-process events
❌ Low latency requirements
```

---

**Migration Example:**

```java
// Traditional Observer
public class CommissionObserver implements DealObserver {
    @Override
    public void onDealUpdated(Deal deal, String eventType) {
        if ("STATUS_CHANGED".equals(eventType) && deal.getStatus() == DealStatus.WON) {
            calculateCommission(deal);
        }
    }
}

// ↓ Migrated to Event Bus
@Component
public class CommissionEventListener {
    @EventListener
    @Async
    public void handleDealClosed(DealClosedEvent event) {
        calculateCommission(event.getDeal());
    }
}

// ↓ Migrated to Reactive Streams
@Service
public class CommissionReactiveService {
    public Flux<Commission> processDeals(Flux<Deal> dealStream) {
        return dealStream
            .filter(deal -> deal.getStatus() == DealStatus.WON)
            .map(this::calculateCommission)
            .onErrorContinue((error, deal) ->
                LOGGER.error("Failed to calculate commission", error))
            .subscribeOn(Schedulers.parallel());
    }
}
```

**Conclusion:**

Modern approaches offer:
- Better scalability
- Built-in async/parallel support
- Advanced error handling
- Backpressure management
- Time-based operations
- Cross-service communication

But come with:
- Higher complexity
- Steeper learning curve
- More infrastructure
- Potential over-engineering for simple cases

**Recommendation:** Start with traditional Observer for simple cases, evolve to modern approaches as complexity grows.

### 34. In our implementation, the subject stores references to observers. Could this cause memory leaks? If so, how would you prevent them?

**Answer:**

**Yes, this can absolutely cause memory leaks!**

**How Memory Leaks Occur:**

**Scenario 1: Forgotten Detachment**
```java
public class DealDashboard {
    private DealObserver observer;

    public void initialize(ObservableDealTracker tracker) {
        observer = new DashboardUpdateObserver();
        tracker.attach(observer);  // ← Reference stored in tracker

        // Problem: When DealDashboard is closed, if we don't detach:
    }

    public void close() {
        // FORGOT TO DETACH!
        // tracker.detach(observer);

        // Even though DealDashboard is "closed", the observer is still
        // registered with tracker, preventing garbage collection
    }
}
```

**Memory Impact:**
```java
ObservableDealTracker tracker = new ObservableDealTracker();

for (int i = 0; i < 10000; i++) {
    DealDashboard dashboard = new DealDashboard();
    dashboard.initialize(tracker);
    dashboard.close();  // Closes but doesn't detach
}

// tracker now holds 10,000 observer references!
// None can be garbage collected despite dashboards being "closed"
System.out.println(tracker.getObserverCount());  // 10,000!
```

**Scenario 2: Long-Lived Subject with Short-Lived Observers**
```java
// Global/singleton tracker
public class Application {
    private static final ObservableDealTracker GLOBAL_TRACKER = new ObservableDealTracker();

    public static void showDealDialog(Deal deal) {
        DealDialog dialog = new DealDialog();

        // Dialog observes the global tracker
        GLOBAL_TRACKER.attach(dialog.getObserver());

        dialog.show();
        dialog.close();  // Dialog disposed, but observer still attached!

        // Observer holds reference to dialog → memory leak
    }
}
```

**Scenario 3: Anonymous Inner Classes**
```java
public class DealProcessor {
    private List<Deal> processedDeals = new ArrayList<>();  // Large data

    public void registerObserver(ObservableDealTracker tracker) {
        // Anonymous inner class holds implicit reference to DealProcessor
        tracker.attach(new DealObserver() {
            @Override
            public void onDealUpdated(Deal deal, String eventType) {
                // Can access processedDeals
                processedDeals.add(deal);  // ← Implicit 'this' reference
            }
        });

        // Even if DealProcessor should be GC'd, the observer
        // (attached to tracker) holds a reference to it
    }
}
```

---

**Prevention Strategies:**

**Strategy 1: Weak References**
```java
public class ObservableDealTracker implements DealSubject {
    // Use WeakReference to allow observers to be garbage collected
    private final List<WeakReference<DealObserver>> observers = new ArrayList<>();

    @Override
    public void attach(DealObserver observer) {
        observers.add(new WeakReference<>(observer));
    }

    @Override
    public void notifyObservers() {
        // Clean up dead references while notifying
        Iterator<WeakReference<DealObserver>> iterator = observers.iterator();

        while (iterator.hasNext()) {
            WeakReference<DealObserver> ref = iterator.next();
            DealObserver observer = ref.get();

            if (observer == null) {
                // Observer was garbage collected
                iterator.remove();
            } else {
                try {
                    observer.onDealUpdated(lastAffectedDeal, lastEventType);
                } catch (Exception e) {
                    LOGGER.severe("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public int getObserverCount() {
        // Clean up dead references before counting
        observers.removeIf(ref -> ref.get() == null);
        return observers.size();
    }
}
```

**Pros:**
- Automatic cleanup of unused observers
- No manual detach required
- Prevents memory leaks

**Cons:**
- Observer might be GC'd while still needed
- Need to keep strong reference somewhere else
- More complex code

**Strategy 2: Automatic Cleanup on Close**
```java
public interface Closeable Observer extends DealObserver, AutoCloseable {
    @Override
    void close();
}

public class DealDashboard implements AutoCloseable {
    private final CloseableObserver observer;
    private final ObservableDealTracker tracker;

    public DealDashboard(ObservableDealTracker tracker) {
        this.tracker = tracker;
        this.observer = new DashboardUpdateObserver();
        tracker.attach(observer);
    }

    @Override
    public void close() {
        tracker.detach(observer);  // Automatic cleanup
        observer.close();
    }
}

// Usage with try-with-resources
try (DealDashboard dashboard = new DealDashboard(tracker)) {
    dashboard.show();
} // Automatically detaches observer
```

**Strategy 3: Scope-Based Registration**
```java
public class ObservableDealTracker {
    /**
     * Attaches observer for a single notification, then auto-detaches.
     */
    public void attachOnce(DealObserver observer) {
        DealObserver onceWrapper = new DealObserver() {
            @Override
            public void onDealUpdated(Deal deal, String eventType) {
                try {
                    observer.onDealUpdated(deal, eventType);
                } finally {
                    detach(this);  // Auto-detach after first notification
                }
            }
        };
        attach(onceWrapper);
    }

    /**
     * Attaches observer with automatic timeout.
     */
    public void attachWithTimeout(DealObserver observer, Duration timeout) {
        attach(observer);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            detach(observer);
            LOGGER.info("Observer auto-detached after timeout");
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
```

**Strategy 4: Observer Lifecycle Management**
```java
public class ManagedObservableDealTracker extends ObservableDealTracker {
    private final Map<DealObserver, ObserverMetadata> observerMetadata = new HashMap<>();

    private static class ObserverMetadata {
        final LocalDateTime attachedAt;
        final String attachedBy;
        final StackTraceElement[] attachStackTrace;

        ObserverMetadata() {
            this.attachedAt = LocalDateTime.now();
            this.attachedBy = Thread.currentThread().getName();
            this.attachStackTrace = Thread.currentThread().getStackTrace();
        }
    }

    @Override
    public void attach(DealObserver observer) {
        super.attach(observer);
        observerMetadata.put(observer, new ObserverMetadata());
    }

    @Override
    public void detach(DealObserver observer) {
        super.detach(observer);
        observerMetadata.remove(observer);
    }

    /**
     * Debug method to find potential memory leaks.
     */
    public void reportLongLivedObservers(Duration threshold) {
        LocalDateTime cutoff = LocalDateTime.now().minus(threshold);

        observerMetadata.forEach((observer, metadata) -> {
            if (metadata.attachedAt.isBefore(cutoff)) {
                LOGGER.warning(String.format(
                    "Potential memory leak: Observer %s attached at %s by %s",
                    observer.getClass().getSimpleName(),
                    metadata.attachedAt,
                    metadata.attachedBy
                ));

                // Print stack trace of where it was attached
                for (StackTraceElement element : metadata.attachStackTrace) {
                    LOGGER.warning("  at " + element);
                }
            }
        });
    }
}
```

**Strategy 5: Explicit Lifecycle Contract**
```java
public interface DealObserver {
    void onDealUpdated(Deal deal, String eventType);

    /**
     * Called when observer is detached. Use for cleanup.
     */
    default void onDetached() {
        // Override to perform cleanup
    }
}

public class ObservableDealTracker implements DealSubject {
    @Override
    public void detach(DealObserver observer) {
        if (observers.remove(observer)) {
            // Notify observer it was detached
            try {
                observer.onDetached();
            } catch (Exception e) {
                LOGGER.severe("Error during observer cleanup: " + e.getMessage());
            }
        }
    }

    /**
     * Detach all observers and shut down.
     */
    public void shutdown() {
        List<DealObserver> observersCopy = new ArrayList<>(observers);
        observersCopy.forEach(this::detach);
        observers.clear();
    }
}
```

**Strategy 6: Static Analysis Tools**
```java
// Use tools to detect leaks:

// 1. FindBugs/SpotBugs annotations
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
private final List<DealObserver> observers = new ArrayList<>();

// 2. JUnit test to detect leaks
@Test
public void testNoMemoryLeakAfterDetach() {
    ObservableDealTracker tracker = new ObservableDealTracker();
    WeakReference<DealObserver> weakRef;

    {
        DealObserver observer = new AuditLogObserver();
        weakRef = new WeakReference<>(observer);
        tracker.attach(observer);
        tracker.detach(observer);
        // observer goes out of scope
    }

    // Force garbage collection
    System.gc();
    Thread.sleep(100);

    // Observer should be GC'd after detachment
    assertNull(weakRef.get(), "Observer should be garbage collected");
}

// 3. Monitor with VisualVM or JProfiler
// Look for growing List<DealObserver> in heap dumps
```

---

**Best Practices:**

**1. Document Lifecycle Requirements**
```java
/**
 * DealObserver must be explicitly detached when no longer needed.
 * Failure to detach will cause memory leaks.
 *
 * @example
 * <pre>
 * DealObserver observer = new MyObserver();
 * tracker.attach(observer);
 * try {
 *     // Use tracker
 * } finally {
 *     tracker.detach(observer);  // Always detach in finally block
 * }
 * </pre>
 */
public interface DealObserver { ... }
```

**2. Use Try-Finally**
```java
DealObserver observer = new MyObserver();
tracker.attach(observer);
try {
    // Do work
} finally {
    tracker.detach(observer);  // Guaranteed cleanup
}
```

**3. Implement AutoCloseable**
```java
public class DealObserverRegistration implements AutoCloseable {
    private final ObservableDealTracker tracker;
    private final DealObserver observer;

    public DealObserverRegistration(ObservableDealTracker tracker, DealObserver observer) {
        this.tracker = tracker;
        this.observer = observer;
        tracker.attach(observer);
    }

    @Override
    public void close() {
        tracker.detach(observer);
    }
}

// Usage
try (var registration = new DealObserverRegistration(tracker, observer)) {
    // Observer automatically detached when leaving scope
}
```

**4. Monitor in Production**
```java
@Scheduled(fixedRate = 3600000)  // Every hour
public void monitorObserverCounts() {
    int count = dealTracker.getObserverCount();
    metrics.gauge("deal_tracker.observer_count", count);

    if (count > 1000) {
        LOGGER.warning("Unusually high observer count: " + count +
            " - possible memory leak!");
    }
}
```

---

**Recommended Approach:**

**Combination Strategy:**
1. Use weak references for non-critical observers (Strategy 1)
2. Implement AutoCloseable for critical components (Strategy 2)
3. Add lifecycle management for debugging (Strategy 4)
4. Monitor observer counts in production (Best Practice 4)

**For Our Commission Calculator:**
```java
public class RobustObservableDealTracker implements DealSubject, AutoCloseable {
    // Critical observers: strong references (must explicitly detach)
    private final List<DealObserver> criticalObservers = new ArrayList<>();

    // Non-critical observers: weak references (auto-cleanup)
    private final List<WeakReference<DealObserver>> observers = new ArrayList<>();

    public void attachCritical(DealObserver observer) {
        criticalObservers.add(observer);
    }

    @Override
    public void attach(DealObserver observer) {
        observers.add(new WeakReference<>(observer));
    }

    @Override
    public void notifyObservers() {
        // Notify critical observers
        for (DealObserver observer : criticalObservers) {
            observer.onDealUpdated(lastAffectedDeal, lastEventType);
        }

        // Notify weak observers (with cleanup)
        observers.removeIf(ref -> {
            DealObserver observer = ref.get();
            if (observer == null) return true;  // Remove dead reference

            observer.onDealUpdated(lastAffectedDeal, lastEventType);
            return false;
        });
    }

    @Override
    public void close() {
        criticalObservers.clear();
        observers.clear();
    }
}
```

### 35. How does the Observer Pattern relate to the Model-View-Controller (MVC) architectural pattern?

**Answer:**

The Observer Pattern is a **fundamental building block** of the MVC pattern, specifically in the relationship between Model and View.

---

**MVC Architecture Overview:**

```
┌──────────┐         ┌──────────────┐         ┌──────────┐
│          │ updates │              │ updates │          │
│  Model   │────────>│ Controller   │────────>│   View   │
│          │<────────│              │<────────│          │
└──────────┘  reads  └──────────────┘  reads  └──────────┘
     ↑                                              │
     │                                              │
     └──────────────────────────────────────────────┘
              Observer Pattern relationship
```

**Components:**
1. **Model**: Business logic and data (Subject in Observer Pattern)
2. **View**: User interface (Observer in Observer Pattern)
3. **Controller**: Handles user input and coordinates Model and View

---

**Observer Pattern in MVC:**

**Model as Subject:**
```java
/**
 * Model - Subject in Observer Pattern
 * Maintains business state and notifies views of changes
 */
public class DealModel extends Observable {  // Subject
    private Deal deal;
    private final List<Observer> views = new ArrayList<>();  // Observers

    public void updateDealValue(BigDecimal newValue) {
        deal.setValue(newValue);

        // Notify all registered views
        notifyViews("VALUE_CHANGED");
    }

    public void updateDealStatus(DealStatus newStatus) {
        deal.setStatus(newStatus);

        // Notify all registered views
        notifyViews("STATUS_CHANGED");
    }

    // Subject interface implementation
    public void registerView(DealView view) {
        views.add(view);
    }

    public void unregisterView(DealView view) {
        views.remove(view);
    }

    private void notifyViews(String changeType) {
        for (Observer view : views) {
            view.update(this, changeType);  // Observer Pattern notification
        }
    }

    public Deal getDeal() {
        return deal;
    }
}
```

**View as Observer:**
```java
/**
 * View - Observer in Observer Pattern
 * Displays data and updates when model changes
 */
public class DealDetailView implements Observer {  // Observer
    private DealModel model;  // Reference to subject
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel statusLabel;

    public DealDetailView(DealModel model) {
        this.model = model;
        model.registerView(this);  // Register as observer

        initializeUI();
        updateDisplay();  // Initial display
    }

    /**
     * Observer Pattern callback - called when model changes
     */
    @Override
    public void update(Observable observable, Object arg) {
        String changeType = (String) arg;

        // Re-render based on what changed
        updateDisplay();

        // Could be more specific:
        if ("VALUE_CHANGED".equals(changeType)) {
            updateValueDisplay();
        } else if ("STATUS_CHANGED".equals(changeType)) {
            updateStatusDisplay();
        }
    }

    private void updateDisplay() {
        Deal deal = model.getDeal();
        titleLabel.setText(deal.getTitle());
        valueLabel.setText("$" + deal.getValue());
        statusLabel.setText(deal.getStatus().toString());
    }

    public void cleanup() {
        model.unregisterView(this);  // Unregister when view closes
    }
}
```

**Controller Coordinates:**
```java
/**
 * Controller - Mediates between View and Model
 * Handles user actions and updates model
 */
public class DealController {
    private final DealModel model;
    private final DealDetailView view;

    public DealController(DealModel model, DealDetailView view) {
        this.model = model;
        this.view = view;

        // Wire up view events to controller actions
        view.getUpdateButton().addActionListener(e -> handleUpdate());
        view.getCloseButton().addActionListener(e -> handleClose());
    }

    public void handleUpdate() {
        // Get data from view
        BigDecimal newValue = view.getValueInput();

        // Update model (which will notify view via Observer Pattern)
        model.updateDealValue(newValue);

        // No need to manually update view - Observer Pattern handles it!
    }

    public void handleClose() {
        DealStatus newStatus = DealStatus.WON;
        model.updateDealStatus(newStatus);  // Model notifies view automatically
    }
}
```

---

**Complete MVC Example with Our Commission Calculator:**

```java
// MODEL - Subject
public class CommissionCalculatorModel {
    private Deal deal;
    private BigDecimal commissionRate;
    private BigDecimal calculatedCommission;

    private final List<CommissionView> views = new ArrayList<>();

    // Business logic
    public void calculateCommission() {
        calculatedCommission = deal.getValue().multiply(commissionRate);
        notifyViews("COMMISSION_CALCULATED");
    }

    public void setDealValue(BigDecimal value) {
        deal.setValue(value);
        calculateCommission();  // Auto-recalculate
        notifyViews("DEAL_VALUE_CHANGED");
    }

    // Observer Pattern - Subject interface
    public void addView(CommissionView view) {
        views.add(view);
    }

    public void removeView(CommissionView view) {
        views.remove(view);
    }

    private void notifyViews(String event) {
        for (CommissionView view : views) {
            view.modelChanged(event);
        }
    }

    // Getters
    public Deal getDeal() { return deal; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public BigDecimal getCalculatedCommission() { return calculatedCommission; }
}

// VIEW - Observer
public class CommissionCalculatorView implements CommissionView {
    private CommissionCalculatorModel model;
    private CommissionCalculatorController controller;

    // UI Components
    private JTextField dealValueField;
    private JTextField commissionRateField;
    private JLabel commissionResultLabel;
    private JButton calculateButton;

    public CommissionCalculatorView(CommissionCalculatorModel model,
                                   CommissionCalculatorController controller) {
        this.model = model;
        this.controller = controller;

        model.addView(this);  // Register as observer

        initializeUI();
        wireUpEvents();
        refreshDisplay();
    }

    private void initializeUI() {
        dealValueField = new JTextField();
        commissionRateField = new JTextField();
        commissionResultLabel = new JLabel();
        calculateButton = new JButton("Calculate");

        // Layout code...
    }

    private void wireUpEvents() {
        // User actions go to controller
        calculateButton.addActionListener(e -> {
            BigDecimal value = new BigDecimal(dealValueField.getText());
            BigDecimal rate = new BigDecimal(commissionRateField.getText());
            controller.handleCalculateCommission(value, rate);
        });
    }

    /**
     * Observer Pattern callback - model changed
     */
    @Override
    public void modelChanged(String event) {
        refreshDisplay();  // Update UI to reflect model changes

        if ("COMMISSION_CALCULATED".equals(event)) {
            highlightResult();  // Visual feedback
        }
    }

    private void refreshDisplay() {
        // Pull data from model and update UI
        dealValueField.setText(model.getDeal().getValue().toString());
        commissionRateField.setText(model.getCommissionRate().toString());
        commissionResultLabel.setText("$" + model.getCalculatedCommission());
    }
}

// CONTROLLER - Mediator
public class CommissionCalculatorController {
    private final CommissionCalculatorModel model;

    public CommissionCalculatorController(CommissionCalculatorModel model) {
        this.model = model;
    }

    public void handleCalculateCommission(BigDecimal dealValue, BigDecimal rate) {
        // Validate input
        if (dealValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deal value must be positive");
        }

        // Update model
        model.setDealValue(dealValue);
        model.setCommissionRate(rate);
        model.calculateCommission();

        // Model automatically notifies views via Observer Pattern
        // No need to manually update view!
    }
}

// MAIN - Wire everything together
public class CommissionCalculatorApp {
    public static void main(String[] args) {
        // Create model
        CommissionCalculatorModel model = new CommissionCalculatorModel();

        // Create controller
        CommissionCalculatorController controller =
            new CommissionCalculatorController(model);

        // Create view(s) - multiple views can observe same model!
        CommissionCalculatorView mainView =
            new CommissionCalculatorView(model, controller);

        CommissionSummaryView summaryView =
            new CommissionSummaryView(model);  // Second view, same model

        // Both views automatically update when model changes
        // via Observer Pattern
    }
}
```

---

**Key Relationships:**

**1. One Model, Multiple Views:**
```java
// Observer Pattern enables multiple views of same data
CommissionCalculatorModel model = new CommissionCalculatorModel();

// Desktop view
CommissionDesktopView desktopView = new CommissionDesktopView(model);
model.addView(desktopView);

// Mobile view
CommissionMobileView mobileView = new CommissionMobileView(model);
model.addView(mobileView);

// Dashboard widget
CommissionWidgetView widgetView = new CommissionWidgetView(model);
model.addView(widgetView);

// All three views automatically stay in sync!
model.setDealValue(new BigDecimal("100000"));
// ↑ This one call updates all three views
```

**2. Separation of Concerns:**
```java
// Model: Business logic only, no UI code
public class DealModel {
    public void closeDeal() {
        this.status = DealStatus.WON;
        calculateCommission();
        notifyViews();  // Doesn't know HOW views will update
    }
}

// View: UI only, no business logic
public class DealView implements Observer {
    @Override
    public void update(Observable model, Object arg) {
        refreshDisplay();  // Just updates UI, no business logic
    }
}

// Controller: Coordination only
public class DealController {
    public void handleCloseButton() {
        model.closeDeal();  // Delegates to model
    }
}
```

**3. Testability:**
```java
// Can test model without UI
@Test
public void testCommissionCalculation() {
    CommissionCalculatorModel model = new CommissionCalculatorModel();
    model.setDealValue(new BigDecimal("100000"));
    model.setCommissionRate(new BigDecimal("0.10"));
    model.calculateCommission();

    assertEquals(new BigDecimal("10000.00"), model.getCalculatedCommission());
    // No UI needed for testing!
}

// Can test that view responds to model changes
@Test
public void testViewUpdatesOnModelChange() {
    CommissionCalculatorModel model = new CommissionCalculatorModel();
    MockCommissionView view = new MockCommissionView(model);

    model.setDealValue(new BigDecimal("50000"));

    assertTrue(view.wasNotified());
    assertEquals(1, view.getUpdateCount());
}
```

---

**Benefits of Observer Pattern in MVC:**

**Loose Coupling:** Model doesn't depend on view implementation
**Multiple Views:** Same model can drive multiple views
**Automatic Sync:** Views stay synchronized with model
**Testability:** Can test model independently of UI
**Flexibility:** Can add/remove views at runtime

---

**Modern Variations:**

**MVVM (Model-View-ViewModel):**
```java
// Similar to MVC but with data binding
public class DealViewModel extends Observable {
    private final DealModel model;

    // Observable properties that views bind to
    public ObservableField<String> dealValue = new ObservableField<>();
    public ObservableField<String> dealStatus = new ObservableField<>();

    public void updateFromModel() {
        dealValue.set(model.getDeal().getValue().toString());
        dealStatus.set(model.getDeal().getStatus().toString());
        // Views automatically update via data binding
    }
}
```

**React/Redux (Unidirectional Data Flow):**
```javascript
// State as single source of truth (Model)
const state = {
  deal: { value: 100000, status: 'OPEN' }
};

// Components subscribe to state changes (Observer Pattern)
function DealView({ deal }) {
  // View re-renders when state changes
  return <div>{deal.value}</div>;
}

// State changes trigger re-renders
dispatch({ type: 'UPDATE_DEAL_VALUE', value: 150000 });
// All subscribed components automatically update
```

---

**Conclusion:**

The Observer Pattern is **essential to MVC** because it:
1. Enables automatic view updates when model changes
2. Supports multiple views of the same data
3. Maintains separation of concerns
4. Makes the architecture testable and maintainable

**Without Observer Pattern**, MVC would require:
- Manual view updates (error-prone)
- Tight coupling between model and views
- Difficulty supporting multiple views
- More complex controller logic

The Observer Pattern is what makes MVC **practical and scalable** for real-world applications.