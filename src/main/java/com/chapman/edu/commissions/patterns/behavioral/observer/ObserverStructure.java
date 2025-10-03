package com.chapman.edu.commissions.patterns.behavioral.observer;

import com.chapman.edu.commissions.model.Deal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * OBSERVER PATTERN STRUCTURE
 * ===========================
 *
 * This file demonstrates the core structure of the Observer Pattern, showing the fundamental
 * interfaces and contracts that define how subjects and observers interact.
 *
 * PATTERN INTENT:
 * Define a one-to-many dependency between objects so that when one object changes state,
 * all its dependents are notified and updated automatically.
 *
 * PATTERN PARTICIPANTS:
 * 1. Subject Interface - Defines the contract for managing observers
 * 2. Observer Interface - Defines the contract for receiving notifications
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Separation of concerns through interfaces
 * - Loose coupling between subject and observers
 * - Polymorphic notification mechanism
 * - Observer registration and deregistration
 */
public class ObserverStructure {

    /**
     * SUBJECT INTERFACE
     * =================
     *
     * The Subject interface represents the "observable" side of the pattern.
     * Any class that wants to notify observers of state changes should implement this interface.
     *
     * RESPONSIBILITIES:
     * - Maintain a collection of observers
     * - Provide methods to register (attach) observers
     * - Provide methods to unregister (detach) observers
     * - Provide a method to notify all registered observers
     *
     * DESIGN BENEFITS:
     * - Subjects don't need to know concrete observer types
     * - Enables polymorphic treatment of different subject implementations
     * - Supports dependency inversion (depend on abstraction, not concretions)
     */
    public interface DealSubject {

        /**
         * Attaches an observer to receive notifications.
         *
         * OBSERVER PATTERN CONCEPT:
         * This method allows observers to "subscribe" to the subject. Once attached,
         * the observer will receive all future notifications until it is detached.
         *
         * IMPLEMENTATION CONSIDERATIONS:
         * - Should prevent duplicate observers (same observer attached multiple times)
         * - Should validate that observer is not null
         * - May need to be thread-safe in concurrent environments
         * - Order of attachment may determine notification order
         *
         * @param observer the observer to attach (must not be null)
         */
        void attach(DealObserver observer);

        /**
         * Detaches an observer so it no longer receives notifications.
         *
         * OBSERVER PATTERN CONCEPT:
         * This method allows observers to "unsubscribe" from the subject.
         * After detachment, the observer will no longer receive notifications.
         *
         * MEMORY MANAGEMENT:
         * This is critical for preventing memory leaks! If observers are not properly
         * detached, the subject will maintain references to them, preventing garbage
         * collection even when the observer is no longer needed elsewhere.
         *
         * BEST PRACTICES:
         * - Always detach observers when they are no longer needed
         * - Use try-finally blocks to ensure detachment
         * - Consider implementing AutoCloseable for automatic cleanup
         *
         * @param observer the observer to detach
         */
        void detach(DealObserver observer);

        /**
         * Notifies all registered observers of a state change.
         *
         * OBSERVER PATTERN CONCEPT:
         * This is the core notification mechanism. When the subject's state changes,
         * it calls this method to inform all observers. The subject iterates through
         * its list of observers and calls the update method on each one.
         *
         * IMPLEMENTATION STRATEGIES:
         *
         * 1. PUSH MODEL (recommended for most cases):
         *    Subject sends all relevant data to observers in the notification.
         *    Pro: Observers receive everything they need without additional queries.
         *    Con: Subject must know what data observers need.
         *
         * 2. PULL MODEL:
         *    Subject just notifies that something changed; observers query for data.
         *    Pro: More flexible; observers get only what they need.
         *    Con: Requires observers to know subject's interface; more complex.
         *
         * ERROR HANDLING:
         * Should catch exceptions from individual observers to prevent one faulty
         * observer from breaking the notification chain to other observers.
         *
         * THREAD SAFETY:
         * In multi-threaded environments, this method needs synchronization to
         * prevent concurrent modification of the observer list during iteration.
         */
        void notifyObservers();
    }

    /**
     * OBSERVER INTERFACE
     * ==================
     *
     * The Observer interface represents the "watching" side of the pattern.
     * Any class that wants to react to subject state changes should implement this interface.
     *
     * RESPONSIBILITIES:
     * - Define the update/notification method
     * - React appropriately to state changes
     * - Maintain reference to subject (if using pull model)
     *
     * DESIGN BENEFITS:
     * - Subjects can notify observers without knowing their concrete types
     * - New observer types can be added without modifying subjects
     * - Observers can be reused with different subjects
     * - Supports loose coupling
     */
    public interface DealObserver {

        /**
         * Called by the subject when a deal is updated.
         *
         * OBSERVER PATTERN CONCEPT:
         * This is the callback method that subjects invoke when notifying observers.
         * Each observer implements this method with its own logic for responding
         * to the notification.
         *
         * PARAMETERS EXPLAINED:
         * @param deal - The deal that changed (PUSH model - subject provides data)
         *              In a PULL model, this might be omitted, and observers would
         *              query the subject for needed information.
         *
         * @param eventType - Describes what happened (e.g., "CREATED", "STATUS_CHANGED")
         *                   This allows observers to react differently to different events.
         *                   Observers can ignore events they don't care about.
         *
         * IMPLEMENTATION GUIDELINES:
         *
         * 1. SELECTIVE HANDLING:
         *    Observers don't have to respond to every event. They can filter:
         *    <code>
         *    if ("STATUS_CHANGED".equals(eventType)) {
         *        // Only respond to status changes
         *    }
         *    </code>
         *
         * 2. AVOID CIRCULAR UPDATES:
         *    Observers should NOT modify the subject during notification, as this
         *    can cause infinite loops:
         *    BAD: subject.updateDeal() inside onDealUpdated()
         *    GOOD: Queue the update for later execution
         *
         * 3. EXCEPTION HANDLING:
         *    Don't let exceptions propagate - they might prevent other observers
         *    from being notified. Catch and log errors instead.
         *
         * 4. PERFORMANCE:
         *    Keep processing fast. If expensive operations are needed, consider
         *    executing them asynchronously to avoid blocking the subject.
         *
         * 5. STATELESSNESS:
         *    Observers should generally not depend on the order they're called.
         *    If order matters, consider a different pattern (Chain of Responsibility).
         */
        void onDealUpdated(Deal deal, String eventType);
    }

    /**
     * ABSTRACT BASE SUBJECT (Optional Enhancement)
     * ============================================
     *
     * While not required by the pattern, an abstract base class can provide
     * a reusable implementation of observer management logic.
     *
     * BENEFITS:
     * - Reduces code duplication across multiple subject implementations
     * - Ensures consistent observer management behavior
     * - Provides a template for concrete subjects
     *
     * USAGE:
     * Concrete subjects can extend this class and just focus on their
     * specific business logic, while inheriting standard observer management.
     */
    public abstract static class AbstractDealSubject implements DealSubject {

        /**
         * The collection of registered observers.
         *
         * DESIGN CHOICE: ArrayList vs. Other Collections
         * - ArrayList: Good for small-to-medium observer counts, simple iteration
         * - CopyOnWriteArrayList: Better for concurrent access, many reads vs. writes
         * - LinkedHashSet: Guarantees no duplicates, maintains insertion order
         * - WeakHashMap: Allows garbage collection of observers (prevents memory leaks)
         *
         * For most cases, ArrayList is sufficient and performant.
         */
        protected final List<DealObserver> observers = new ArrayList<>();

        /**
         * Stores information about the last state change for notification purposes.
         * Concrete subjects set these before calling notifyObservers().
         */
        protected Deal lastAffectedDeal;
        protected String lastEventType;

        /**
         * Implements the attach logic with common safeguards.
         *
         * IMPLEMENTATION DETAILS:
         * 1. Null check prevents NullPointerException during notification
         * 2. Duplicate check prevents same observer from being notified multiple times
         * 3. Logging aids debugging and monitoring
         */
        @Override
        public void attach(DealObserver observer) {
            // Guard against null observers - fail fast with clear error message
            Objects.requireNonNull(observer, "Observer cannot be null");

            // Prevent duplicate registrations
            // Without this check, the same observer could receive multiple notifications
            // for each event, which is almost never desired behavior
            if (!observers.contains(observer)) {
                observers.add(observer);

                // Logging helps track observer lifecycle and diagnose issues
                Logger.getLogger(getClass().getName()).info(
                        "Observer attached: " + observer.getClass().getSimpleName() +
                                " (Total observers: " + observers.size() + ")"
                );
            } else {
                // Log when duplicate attachment is attempted (might indicate a bug)
                Logger.getLogger(getClass().getName()).warning(
                        "Observer already attached: " + observer.getClass().getSimpleName()
                );
            }
        }

        /**
         * Implements the detach logic with proper cleanup.
         *
         * MEMORY LEAK PREVENTION:
         * This method is crucial for memory management. Observers that are no longer
         * needed must be detached, or the subject will hold references to them
         * indefinitely, preventing garbage collection.
         */
        @Override
        public void detach(DealObserver observer) {
            // Remove returns true if observer was present, false otherwise
            if (observers.remove(observer)) {
                Logger.getLogger(getClass().getName()).info(
                        "Observer detached: " + observer.getClass().getSimpleName() +
                                " (Total observers: " + observers.size() + ")"
                );
            } else {
                // Log when attempting to detach an observer that isn't registered
                // This might indicate a logic error in the calling code
                Logger.getLogger(getClass().getName()).warning(
                        "Attempted to detach unregistered observer: " +
                                observer.getClass().getSimpleName()
                );
            }
        }

        /**
         * Implements the notification mechanism with error isolation.
         *
         * CRITICAL DESIGN DECISIONS:
         *
         * 1. ERROR ISOLATION:
         *    Each observer is called in a try-catch block. This ensures that if one
         *    observer throws an exception, other observers still get notified.
         *    Without this, a single buggy observer could break the entire chain.
         *
         * 2. ITERATION SAFETY:
         *    If observers might detach themselves during notification, consider
         *    iterating over a copy of the list to avoid ConcurrentModificationException.
         *
         * 3. NOTIFICATION ORDER:
         *    Observers are notified in the order they were attached. If order matters
         *    for your use case, consider adding priority support or documenting the
         *    behavior clearly.
         */
        @Override
        public void notifyObservers() {
            Logger.getLogger(getClass().getName()).info(
                    String.format("Notifying %d observer(s) of event: %s",
                            observers.size(), lastEventType)
            );

            // Iterate through all registered observers
            for (DealObserver observer : observers) {
                try {
                    // Call the observer's update method (PUSH model - we provide the data)
                    observer.onDealUpdated(lastAffectedDeal, lastEventType);

                } catch (Exception e) {
                    // CRITICAL: Catch exceptions to prevent one faulty observer from
                    // breaking the notification chain to other observers
                    Logger.getLogger(getClass().getName()).severe(
                            "Error notifying observer " + observer.getClass().getSimpleName() +
                                    ": " + e.getMessage()
                    );

                    // In production, you might want to:
                    // - Send this to a monitoring system
                    // - Automatically detach consistently failing observers
                    // - Implement a circuit breaker pattern
                }
            }
        }

        /**
         * Utility method to get the current number of observers.
         * Useful for debugging, monitoring, and testing.
         */
        public int getObserverCount() {
            return observers.size();
        }

        /**
         * Utility method to detach all observers.
         * Useful for cleanup during shutdown or testing.
         */
        public void detachAll() {
            // Create a copy to avoid concurrent modification during iteration
            List<DealObserver> observersCopy = new ArrayList<>(observers);
            observersCopy.forEach(this::detach);
        }
    }

    /**
     * PATTERN STRUCTURE SUMMARY
     * =========================
     *
     * The Observer Pattern structure consists of two main interfaces:
     *
     * 1. SUBJECT (DealSubject):
     *    - Knows about observers
     *    - Manages observer lifecycle (attach/detach)
     *    - Notifies observers of changes
     *    - Doesn't know concrete observer types
     *
     * 2. OBSERVER (DealObserver):
     *    - Defines update interface
     *    - Receives notifications from subject
     *    - Implements specific response logic
     *    - Doesn't need to know about other observers
     *
     * RELATIONSHIP:
     * Subject (1) -----> (many) Observers
     *
     * The subject maintains a list of observers and notifies them when its state changes.
     * This creates a one-to-many relationship where one subject can notify multiple
     * observers, and each observer can observe multiple subjects.
     *
     * KEY PRINCIPLES SUPPORTED:
     * - Open/Closed Principle: Open for extension (new observers), closed for modification
     * - Dependency Inversion: Both depend on abstractions (interfaces), not concretions
     * - Single Responsibility: Subject manages state, observers handle their own concerns
     * - Loose Coupling: Subject and observers know each other only through interfaces
     */
}