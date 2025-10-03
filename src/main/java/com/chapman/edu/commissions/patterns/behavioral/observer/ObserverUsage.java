package com.chapman.edu.commissions.patterns.behavioral.observer;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.observer.ObserverImplementation.*;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * OBSERVER PATTERN USAGE DEMONSTRATION
 * =====================================
 *
 * This file demonstrates how to use the Observer Pattern in practice, showing
 * the complete workflow from setup through execution to cleanup.
 *
 * DEMONSTRATION FLOW:
 * 1. Creating the subject (observable)
 * 2. Creating observers with different configurations
 * 3. Attaching observers to the subject
 * 4. Triggering events and seeing automatic notifications
 * 5. Dynamic observer management (attach/detach at runtime)
 * 6. Observer independence and reusability
 *
 * LEARNING OBJECTIVES:
 * - How to wire up the Observer Pattern components
 * - How observers automatically respond to events
 * - How to manage observer lifecycle
 * - How the same observer can work with different subjects
 * - Best practices for using the pattern
 */
public class ObserverUsage {

    private static final Logger LOGGER = Logger.getLogger(ObserverUsage.class.getName());

    /**
     * MAIN DEMONSTRATION
     * ==================
     *
     * This main method walks through a complete Observer Pattern scenario,
     * demonstrating all the key features and benefits of the pattern.
     */
    public static void main(String[] args) {
        LOGGER.info("=== OBSERVER PATTERN USAGE DEMONSTRATION ===\n");

        // ================================================================
        // STEP 1: Create the Subject (Observable)
        // ================================================================
        // The subject is the object whose state changes we want to observe.
        // In our case, it's a deal tracker that manages sales deals.

        LOGGER.info("--- Step 1: Creating the Observable Subject ---");
        ObservableDealTracker dealTracker = new ObservableDealTracker();

        LOGGER.info("Created ObservableDealTracker");
        LOGGER.info("Initial observer count: " + dealTracker.getObserverCount());
        LOGGER.info("");

        // ================================================================
        // STEP 2: Create Observers
        // ================================================================
        // Observers are objects that want to be notified when the subject
        // changes. Each observer can have different configuration and logic.

        LOGGER.info("--- Step 2: Creating Observers ---");

        // OBSERVER 1: Commission Calculator
        // Configured with a 10% commission rate
        // Will calculate commissions when deals are won
        CommissionCalculationObserver commissionObserver =
                new CommissionCalculationObserver(new BigDecimal("0.10"));
        LOGGER.info("Created CommissionCalculationObserver with 10% rate");

        // OBSERVER 2: Audit Logger
        // Will record all deal changes for compliance
        // No configuration needed - records everything
        AuditLogObserver auditObserver = new AuditLogObserver();
        LOGGER.info("Created AuditLogObserver");

        // OBSERVER 3: Notification System
        // Configured with $50,000 threshold for high-value deals
        // Will send different notifications based on deal value and events
        NotificationObserver notificationObserver =
                new NotificationObserver(new BigDecimal("50000"));
        LOGGER.info("Created NotificationObserver with $50,000 threshold");
        LOGGER.info("");

        // ================================================================
        // STEP 3: Attach Observers to Subject
        // ================================================================
        // This is where the Observer Pattern is "activated". By attaching
        // observers, we establish the one-to-many relationship.

        LOGGER.info("--- Step 3: Attaching Observers to Subject ---");

        // Attach each observer to the subject
        // From this point on, whenever the subject's state changes,
        // these observers will be automatically notified
        dealTracker.attach(commissionObserver);
        dealTracker.attach(auditObserver);
        dealTracker.attach(notificationObserver);

        LOGGER.info("All observers attached");
        LOGGER.info("Current observer count: " + dealTracker.getObserverCount());
        LOGGER.info("");

        // KEY CONCEPT: The subject doesn't know the concrete types of these
        // observers - it only knows they implement the DealObserver interface.
        // This is loose coupling in action!

        // ================================================================
        // STEP 4: Trigger Events - Observers Are Automatically Notified
        // ================================================================
        // Now we perform business operations. The beauty of the Observer Pattern
        // is that we don't need to manually notify observers - it happens
        // automatically when state changes.

        LOGGER.info("--- Step 4: Creating Deals (Observers Auto-Notified) ---");

        // CREATE DEAL 1: Small deal (below high-value threshold)
        // What will happen:
        // - AuditLogObserver: Will record the creation
        // - NotificationObserver: Will send normal team notification (not high-value)
        // - CommissionCalculationObserver: Won't do anything (deal not closed yet)
        Deal deal1 = createSampleDeal("DEAL-001", "Small Software License", "25000.00", "REP-101");
        dealTracker.createDeal(deal1);
        // ↑ This single method call triggers notifications to ALL three observers!
        LOGGER.info("");

        // CREATE DEAL 2: Large deal (above high-value threshold)
        // What will happen:
        // - AuditLogObserver: Will record the creation
        // - NotificationObserver: Will send HIGH-VALUE alert to managers
        // - CommissionCalculationObserver: Won't do anything (deal not closed yet)
        Deal deal2 = createSampleDeal("DEAL-002", "Enterprise Cloud Solution", "150000.00", "REP-102");
        dealTracker.createDeal(deal2);
        LOGGER.info("");

        // ================================================================
        // STEP 5: Update Deal Status - Different Observers React Differently
        // ================================================================
        // This demonstrates how different observers can respond to the same
        // event in completely different ways.

        LOGGER.info("--- Step 5: Updating Deal Status ---");

        // Close the first deal (mark as WON)
        // What will happen:
        // - AuditLogObserver: Will record the status change
        // - NotificationObserver: Will send "Deal Closed" celebration message
        // - CommissionCalculationObserver: Will CALCULATE COMMISSION (this is what it waits for!)
        dealTracker.updateDealStatus("DEAL-001", DealStatus.WON);
        // ↑ Notice: Commission is calculated automatically! We didn't have to
        // remember to call a commission service - the observer handled it.
        LOGGER.info("");

        // ================================================================
        // STEP 6: Update Deal Value - Selective Observer Response
        // ================================================================
        // Not all observers care about all events. This shows event filtering.

        LOGGER.info("--- Step 6: Updating Deal Value ---");

        // Increase the value of deal 2
        // What will happen:
        // - AuditLogObserver: Will record the value change
        // - NotificationObserver: Will notify the sales rep
        // - CommissionCalculationObserver: Won't do anything (only cares about closures)
        dealTracker.updateDealValue("DEAL-002", new BigDecimal("175000.00"));
        LOGGER.info("");

        // ================================================================
        // STEP 7: Close Another Deal
        // ================================================================

        LOGGER.info("--- Step 7: Closing Second Deal ---");

        // Close deal 2 (much larger commission!)
        dealTracker.updateDealStatus("DEAL-002", DealStatus.WON);
        // All three observers respond, each doing their specific job
        LOGGER.info("");

        // ================================================================
        // STEP 8: Dynamic Observer Management - Detach Observer
        // ================================================================
        // One of the key features of the Observer Pattern is the ability to
        // add and remove observers at runtime. This demonstrates detachment.

        LOGGER.info("--- Step 8: Detaching Audit Observer ---");

        // Remove the audit observer
        // After this, the audit observer will no longer receive notifications
        dealTracker.detach(auditObserver);

        LOGGER.info("Audit observer detached");
        LOGGER.info("Remaining observers: " + dealTracker.getObserverCount());
        LOGGER.info("");

        // ================================================================
        // STEP 9: Events After Detachment
        // ================================================================
        // This proves that detached observers don't receive notifications.

        LOGGER.info("--- Step 9: Creating Deal After Detaching Audit Observer ---");

        // Create a new deal
        // What will happen:
        // - AuditLogObserver: WON'T be notified (it was detached!)
        // - NotificationObserver: Will send notification (still attached)
        // - CommissionCalculationObserver: Won't do anything (deal not closed)
        Deal deal3 = createSampleDeal("DEAL-003", "Professional Services", "75000.00", "REP-103");
        dealTracker.createDeal(deal3);

        LOGGER.info("Notice: Audit observer was NOT notified (it was detached)");
        LOGGER.info("");

        // ================================================================
        // STEP 10: Display Summary
        // ================================================================
        // This shows the cumulative effect of all the notifications.

        LOGGER.info("--- Step 10: Summary ---");
        LOGGER.info("Total deals tracked: " + dealTracker.getDeals().size());
        LOGGER.info("Active observers: " + dealTracker.getObserverCount());

        // The audit observer stopped receiving updates after detachment
        // It has 6 entries: 2 creates + 1 status change + 1 value update + 1 status change + 1 create (before detach)
        LOGGER.info("Audit log entries: " + auditObserver.getLogCount() +
                " (stopped receiving updates after detachment)");

        // The notification observer received all events
        // Multiple notifications for: 2 creates (1 high-value) + 2 status changes + 1 value update + 1 create
        LOGGER.info("Notifications sent: " + notificationObserver.getSentNotifications().size());
        LOGGER.info("");

        // ================================================================
        // STEP 11: Observer Independence and Reusability
        // ================================================================
        // This demonstrates that observers are independent objects that can
        // be reused with different subjects.

        demonstrateObserverIndependence();

        // ================================================================
        // PATTERN BENEFITS DEMONSTRATED
        // ================================================================
        LOGGER.info("--- Pattern Benefits Demonstrated ---");
        LOGGER.info("✓ Loose Coupling: Subject doesn't know concrete observer types");
        LOGGER.info("✓ Open/Closed: Added functionality (observers) without modifying subject");
        LOGGER.info("✓ Single Responsibility: Each observer has one clear purpose");
        LOGGER.info("✓ Runtime Flexibility: Attached and detached observers dynamically");
        LOGGER.info("✓ Automatic Propagation: State changes automatically triggered notifications");
        LOGGER.info("✓ Independent Observers: Each observer reacted differently to same events");
    }

    /**
     * ADVANCED USAGE: Observer Independence
     * ======================================
     *
     * This method demonstrates that:
     * 1. Observers are independent objects
     * 2. The same observer type can be used with different subjects
     * 3. Different subjects can have different sets of observers
     * 4. Observer instances can have different configurations
     */
    private static void demonstrateObserverIndependence() {
        LOGGER.info("--- Step 11: Demonstrating Observer Independence ---");
        LOGGER.info("Creating a second deal tracker with different observers...");

        // ================================================================
        // Create a Second Subject
        // ================================================================
        // This is completely independent of the first tracker
        ObservableDealTracker tracker2 = new ObservableDealTracker();

        // ================================================================
        // Create a New Observer Instance with Different Configuration
        // ================================================================
        // This commission observer has a DIFFERENT rate (15% vs 10%)
        // This shows that observer instances can be configured differently
        CommissionCalculationObserver commissionObserver2 =
                new CommissionCalculationObserver(new BigDecimal("0.15")); // 15% rate

        // ================================================================
        // Attach Observer to Second Subject
        // ================================================================
        // We only attach one observer to this tracker
        // This demonstrates that different subjects can have different observer sets
        tracker2.attach(commissionObserver2);

        LOGGER.info("Second tracker has only commission observer (15% rate)");

        // ================================================================
        // Trigger Events on Second Subject
        // ================================================================
        // Create and close a deal on the second tracker
        Deal deal = createSampleDeal("DEAL-004", "Test Deal", "100000.00", "REP-104");
        tracker2.createDeal(deal);
        tracker2.updateDealStatus("DEAL-004", DealStatus.WON);

        // KEY OBSERVATION:
        // - Only the commission observer was notified (we only attached that one)
        // - It used a 15% rate (different from the first tracker's 10%)
        // - The first tracker's observers were NOT notified (complete independence)

        LOGGER.info("Notice: Only commission calculation observer was notified (with 15% rate)");
        LOGGER.info("This demonstrates that:");
        LOGGER.info("  • Observers can be reused with different subjects");
        LOGGER.info("  • Each subject maintains its own observer list");
        LOGGER.info("  • Observer instances can have different configurations");
        LOGGER.info("  • Changes to one subject don't affect observers of other subjects");
        LOGGER.info("");
    }

    /**
     * USAGE BEST PRACTICES
     * ====================
     *
     * This section demonstrates common patterns and best practices for using
     * the Observer Pattern in real applications.
     */
    public static class UsageBestPractices {

        /**
         * PATTERN 1: Try-Finally for Guaranteed Cleanup
         * ==============================================
         *
         * Always detach observers when done to prevent memory leaks.
         * Use try-finally to ensure cleanup happens even if exceptions occur.
         */
        public static void patternTryFinally() {
            ObservableDealTracker tracker = new ObservableDealTracker();
            CommissionCalculationObserver observer =
                    new CommissionCalculationObserver(new BigDecimal("0.10"));

            // Attach observer
            tracker.attach(observer);

            try {
                // Use the tracker and observer
                // ... business logic ...

            } finally {
                // CRITICAL: Always detach in finally block
                // This ensures cleanup happens even if exceptions occur
                tracker.detach(observer);
            }
        }

        /**
         * PATTERN 2: Observer Registration Object (for automatic cleanup)
         * ================================================================
         *
         * Create a wrapper that implements AutoCloseable for try-with-resources.
         */
        public static void patternAutoCloseable() {
            ObservableDealTracker tracker = new ObservableDealTracker();

            // Use try-with-resources for automatic cleanup
            try (ObserverRegistration registration = new ObserverRegistration(
                    tracker,
                    new CommissionCalculationObserver(new BigDecimal("0.10")))) {

                // Use the tracker
                // ... business logic ...

            } // Observer automatically detached when leaving this block
        }

        /**
         * Helper class for automatic observer cleanup.
         */
        static class ObserverRegistration implements AutoCloseable {
            private final ObservableDealTracker tracker;
            private final CommissionCalculationObserver observer;

            public ObserverRegistration(ObservableDealTracker tracker,
                                       CommissionCalculationObserver observer) {
                this.tracker = tracker;
                this.observer = observer;
                tracker.attach(observer);
            }

            @Override
            public void close() {
                tracker.detach(observer);
            }
        }

        /**
         * PATTERN 3: Conditional Observer Attachment
         * ===========================================
         *
         * Attach different observers based on runtime conditions.
         */
        public static void patternConditionalAttachment(boolean enableCommissions,
                                                       boolean enableAudit,
                                                       boolean enableNotifications) {
            ObservableDealTracker tracker = new ObservableDealTracker();

            // Conditionally attach observers based on configuration
            if (enableCommissions) {
                tracker.attach(new CommissionCalculationObserver(new BigDecimal("0.10")));
            }

            if (enableAudit) {
                tracker.attach(new AuditLogObserver());
            }

            if (enableNotifications) {
                tracker.attach(new NotificationObserver(new BigDecimal("50000")));
            }

            // The beauty: Business logic doesn't change based on which observers are attached
            // ... use tracker normally ...
        }

        /**
         * PATTERN 4: Observer Factory
         * ============================
         *
         * Use a factory to create and configure observers based on environment.
         */
        public static ObservableDealTracker createProductionTracker() {
            ObservableDealTracker tracker = new ObservableDealTracker();

            // Production observers
            tracker.attach(new AuditLogObserver()); // Always audit in production
            tracker.attach(new CommissionCalculationObserver(new BigDecimal("0.10")));
            tracker.attach(new NotificationObserver(new BigDecimal("100000"))); // Higher threshold in prod

            return tracker;
        }

        public static ObservableDealTracker createDevelopmentTracker() {
            ObservableDealTracker tracker = new ObservableDealTracker();

            // Development observers (maybe no notifications to avoid spam)
            tracker.attach(new AuditLogObserver());
            tracker.attach(new CommissionCalculationObserver(new BigDecimal("0.10")));
            // No notification observer in dev

            return tracker;
        }

        /**
         * PATTERN 5: Observer Monitoring
         * ===============================
         *
         * Monitor observer count to detect potential memory leaks.
         */
        public static void patternMonitoring(ObservableDealTracker tracker) {
            int observerCount = tracker.getObserverCount();

            // Alert if observer count is unexpectedly high
            if (observerCount > 10) {
                LOGGER.warning("Unusually high observer count: " + observerCount +
                        " - possible memory leak!");
            }

            // In production, you might:
            // - Send this to a monitoring service (Prometheus, Datadog, etc.)
            // - Set up alerts for abnormal observer counts
            // - Periodically log observer counts for trend analysis
        }
    }

    /**
     * COMMON PITFALLS AND HOW TO AVOID THEM
     * ======================================
     */
    public static class CommonPitfalls {

        /**
         * PITFALL 1: Forgetting to Detach Observers
         * ==========================================
         *
         * Problem: Observers remain attached even after they're no longer needed,
         * causing memory leaks.
         *
         * Solution: Always detach observers, preferably using try-finally or
         * try-with-resources.
         */
        public static void pitfallForgettingToDetach() {
            // BAD: No cleanup
            ObservableDealTracker tracker = new ObservableDealTracker();
            tracker.attach(new AuditLogObserver());
            // ... use tracker ...
            // Observer never detached - MEMORY LEAK!

            // GOOD: Explicit cleanup
            ObservableDealTracker tracker2 = new ObservableDealTracker();
            AuditLogObserver observer = new AuditLogObserver();
            tracker2.attach(observer);
            try {
                // ... use tracker ...
            } finally {
                tracker2.detach(observer); // Always cleanup
            }
        }

        /**
         * PITFALL 2: Modifying Subject During Notification
         * =================================================
         *
         * Problem: Observer modifies the subject during notification, causing
         * infinite loops or unexpected behavior.
         *
         * Solution: Never modify the subject directly in observer callbacks.
         * Queue modifications for later execution.
         */
        public static class ProblematicObserver implements ObserverStructure.DealObserver {
            private final ObservableDealTracker tracker;

            public ProblematicObserver(ObservableDealTracker tracker) {
                this.tracker = tracker;
            }

            @Override
            public void onDealUpdated(Deal deal, String eventType) {
                // BAD: Modifying subject during notification
                // This can cause infinite loops!
                if (eventType.equals("CREATED")) {
                    tracker.updateDealStatus(deal.getId(), DealStatus.OPEN);
                    // This triggers another notification, which triggers this again...
                }
            }
        }

        /**
         * PITFALL 3: Assuming Notification Order
         * =======================================
         *
         * Problem: Code depends on observers being notified in a specific order.
         *
         * Solution: Don't assume order. If order is critical, use a different
         * pattern (Chain of Responsibility) or implement priority support.
         */
        public static void pitfallAssumingOrder() {
            // DON'T write code that assumes observer A runs before observer B
            // Observers should be independent and order-agnostic

            // If you need guaranteed order, consider:
            // 1. Implementing a priority system
            // 2. Using separate notification phases
            // 3. Using Chain of Responsibility pattern instead
        }
    }

    /**
     * Helper method to create a sample deal for demonstration purposes.
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
     * USAGE SUMMARY
     * =============
     *
     * This file demonstrated:
     *
     * 1. BASIC USAGE:
     *    - Creating subject and observers
     *    - Attaching observers to subject
     *    - Triggering events and automatic notifications
     *    - Detaching observers
     *
     * 2. ADVANCED CONCEPTS:
     *    - Observer independence and reusability
     *    - Dynamic observer management
     *    - Different observer configurations
     *    - Multiple subjects with different observer sets
     *
     * 3. BEST PRACTICES:
     *    - Try-finally for cleanup
     *    - AutoCloseable for automatic cleanup
     *    - Conditional observer attachment
     *    - Observer factories
     *    - Observer monitoring
     *
     * 4. COMMON PITFALLS:
     *    - Forgetting to detach observers
     *    - Modifying subject during notification
     *    - Assuming notification order
     *
     * KEY TAKEAWAYS:
     * - Observer Pattern enables automatic notification of state changes
     * - Subjects and observers are loosely coupled
     * - Observers can be added/removed at runtime
     * - Each observer can react differently to the same event
     * - Always remember to detach observers to prevent memory leaks
     * - Keep observer logic independent and order-agnostic
     */
}