package com.chapman.edu.commissions.patterns.behavioral.observer;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.behavioral.observer.ObserverStructure.DealObserver;
import com.chapman.edu.commissions.patterns.behavioral.observer.ObserverStructure.DealSubject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * OBSERVER PATTERN IMPLEMENTATION
 * ================================
 *
 * This file demonstrates concrete implementations of the Observer Pattern,
 * showing how the abstract structure is applied to solve real business problems
 * in the commission calculator domain.
 *
 * IMPLEMENTATIONS PROVIDED:
 * 1. ObservableDealTracker - Concrete Subject
 * 2. CommissionCalculationObserver - Concrete Observer (selective event handling)
 * 3. AuditLogObserver - Concrete Observer (universal event handling)
 * 4. NotificationObserver - Concrete Observer (conditional event handling)
 *
 * LEARNING OBJECTIVES:
 * - How to implement the Subject interface with business logic
 * - How to implement the Observer interface with different strategies
 * - How to manage state in subjects and observers
 * - How to handle different event types
 * - Best practices for production-ready implementations
 */
public class ObserverImplementation {

    /**
     * CONCRETE SUBJECT: ObservableDealTracker
     * ========================================
     *
     * This class represents a concrete implementation of the Subject interface.
     * It manages a collection of deals and notifies observers when deals are
     * created, updated, or their status changes.
     *
     * PATTERN ROLE: Concrete Subject
     *
     * RESPONSIBILITIES:
     * 1. Maintain business state (the list of deals)
     * 2. Implement business operations (createDeal, updateDealStatus, etc.)
     * 3. Manage observer collection (inherited from interface)
     * 4. Trigger notifications when state changes
     *
     * KEY DESIGN DECISIONS:
     * - Stores both the observer list and the business data (deals)
     * - Each business method updates state, then notifies observers
     * - Uses "push model" - sends deal and event type to observers
     * - Tracks last event for notification context
     */
    public static class ObservableDealTracker implements DealSubject {

        private static final Logger LOGGER = Logger.getLogger(ObservableDealTracker.class.getName());

        /**
         * The collection of observers interested in deal changes.
         *
         * IMPLEMENTATION NOTE:
         * Using a List allows us to maintain insertion order and have the same
         * observer potentially registered multiple times (if we choose to allow it).
         * Alternative: Use a Set to automatically prevent duplicates.
         */
        private final List<DealObserver> observers;

        /**
         * The business data being managed - the collection of deals.
         *
         * PATTERN CONCEPT:
         * This is the "state" that observers are interested in. When this changes,
         * we notify observers. The observers don't directly access this list -
         * they receive notifications about changes instead.
         */
        private final List<Deal> deals;

        /**
         * Context variables for notifications.
         *
         * WHY WE NEED THESE:
         * When notifyObservers() is called, it needs to know what changed.
         * These variables store the context of the most recent change so that
         * observers receive relevant information.
         *
         * ALTERNATIVE APPROACH:
         * Instead of instance variables, we could pass these as parameters to
         * notifyObservers(). The current approach keeps the notification method
         * signature simple and matches the standard Observer Pattern.
         */
        private String lastEventType;
        private Deal lastAffectedDeal;

        public ObservableDealTracker() {
            this.observers = new ArrayList<>();
            this.deals = new ArrayList<>();
        }

        // ============================================================
        // OBSERVER MANAGEMENT METHODS
        // ============================================================

        /**
         * Attaches an observer to receive deal notifications.
         *
         * IMPLEMENTATION STRATEGY:
         * - Validates observer is not null (fail-fast principle)
         * - Checks for duplicates (prevents multiple notifications)
         * - Logs the attachment for debugging and monitoring
         */
        @Override
        public void attach(DealObserver observer) {
            Objects.requireNonNull(observer, "Observer cannot be null");

            // Check if observer is already attached to prevent duplicate notifications
            if (!observers.contains(observer)) {
                observers.add(observer);
                LOGGER.info("Observer attached: " + observer.getClass().getSimpleName() +
                        " (Total observers: " + observers.size() + ")");
            }
        }

        /**
         * Detaches an observer from receiving further notifications.
         *
         * MEMORY MANAGEMENT:
         * This is critical for preventing memory leaks! When an observer is no longer
         * needed, it must be detached or the subject will hold a reference to it,
         * preventing garbage collection.
         */
        @Override
        public void detach(DealObserver observer) {
            if (observers.remove(observer)) {
                LOGGER.info("Observer detached: " + observer.getClass().getSimpleName() +
                        " (Total observers: " + observers.size() + ")");
            }
        }

        /**
         * Notifies all registered observers of a state change.
         *
         * ERROR ISOLATION STRATEGY:
         * Each observer notification is wrapped in a try-catch block. This ensures
         * that if one observer throws an exception, it doesn't prevent other observers
         * from being notified. This is a critical resilience pattern.
         *
         * NOTIFICATION ORDER:
         * Observers are notified in the order they were attached. If specific ordering
         * is required (e.g., audit log must run first), consider implementing a
         * priority system or using separate notification phases.
         */
        @Override
        public void notifyObservers() {
            LOGGER.info(String.format("Notifying %d observer(s) of event: %s",
                    observers.size(), lastEventType));

            // Iterate through all observers and notify them
            for (DealObserver observer : observers) {
                try {
                    // Call the observer's update method with current state
                    // This is the "push model" - we're pushing data to observers
                    observer.onDealUpdated(lastAffectedDeal, lastEventType);

                } catch (Exception e) {
                    // CRITICAL: Isolate observer errors to prevent cascade failures
                    LOGGER.severe("Error notifying observer " +
                            observer.getClass().getSimpleName() + ": " + e.getMessage());

                    // In production, consider:
                    // - Alerting to monitoring systems
                    // - Implementing retry logic
                    // - Auto-detaching consistently failing observers
                }
            }
        }

        // ============================================================
        // BUSINESS METHODS (State-changing operations)
        // ============================================================
        // Pattern: Each method follows the same structure:
        // 1. Validate input
        // 2. Update business state
        // 3. Set notification context (lastAffectedDeal, lastEventType)
        // 4. Call notifyObservers()
        // ============================================================

        /**
         * Creates a new deal and notifies observers.
         *
         * PATTERN DEMONSTRATION:
         * This method shows the typical flow in the Observer Pattern:
         * 1. Business logic executes (add deal to list)
         * 2. State change context is recorded (which deal, what event)
         * 3. Observers are notified automatically
         *
         * The caller doesn't need to know about observers - the notification
         * happens automatically as a side effect of the state change.
         */
        public void createDeal(Deal deal) {
            Objects.requireNonNull(deal, "Deal cannot be null");

            // Update business state
            deals.add(deal);

            // Set notification context
            lastAffectedDeal = deal;
            lastEventType = "CREATED";

            LOGGER.info("Deal created: " + deal.getTitle() + " (ID: " + deal.getId() + ")");

            // Notify all observers about the new deal
            // This is where the Observer Pattern "magic" happens - we don't need to
            // know who the observers are or what they'll do with this information
            notifyObservers();
        }

        /**
         * Updates the status of a deal and notifies observers.
         *
         * BUSINESS LOGIC + PATTERN:
         * This method demonstrates how business validation (finding the deal)
         * integrates with the notification pattern. If the business operation
         * fails (deal not found), we don't notify observers.
         */
        public void updateDealStatus(String dealId, DealStatus newStatus) {
            // Business logic: find the deal
            Deal deal = findDealById(dealId);
            if (deal == null) {
                LOGGER.warning("Deal not found: " + dealId);
                return; // Don't notify if operation failed
            }

            // Capture old status for logging (optional - could pass to observers)
            DealStatus oldStatus = deal.getStatus();

            // Update business state
            deal.setStatus(newStatus);

            // Set notification context
            lastAffectedDeal = deal;
            lastEventType = "STATUS_CHANGED";

            LOGGER.info(String.format("Deal status updated: %s (ID: %s) - %s -> %s",
                    deal.getTitle(), dealId, oldStatus, newStatus));

            // Notify observers of the status change
            notifyObservers();
        }

        /**
         * Updates the value of a deal and notifies observers.
         */
        public void updateDealValue(String dealId, BigDecimal newValue) {
            Deal deal = findDealById(dealId);
            if (deal == null) {
                LOGGER.warning("Deal not found: " + dealId);
                return;
            }

            BigDecimal oldValue = deal.getValue();

            // Update business state
            deal.setValue(newValue);

            // Set notification context
            lastAffectedDeal = deal;
            lastEventType = "VALUE_UPDATED";

            LOGGER.info(String.format("Deal value updated: %s (ID: %s) - $%s -> $%s",
                    deal.getTitle(), dealId, oldValue, newValue));

            // Notify observers
            notifyObservers();
        }

        // ============================================================
        // HELPER METHODS
        // ============================================================

        private Deal findDealById(String dealId) {
            return deals.stream()
                    .filter(d -> dealId.equals(d.getId()))
                    .findFirst()
                    .orElse(null);
        }

        public List<Deal> getDeals() {
            return new ArrayList<>(deals); // Defensive copy
        }

        public int getObserverCount() {
            return observers.size();
        }
    }

    /**
     * CONCRETE OBSERVER #1: CommissionCalculationObserver
     * ====================================================
     *
     * This observer demonstrates SELECTIVE event handling - it only responds
     * to specific events that are relevant to its purpose.
     *
     * PATTERN ROLE: Concrete Observer
     *
     * BUSINESS PURPOSE:
     * Automatically calculates commissions when deals are won (closed).
     *
     * KEY CONCEPTS DEMONSTRATED:
     * - Event filtering (only processes STATUS_CHANGED events with WON status)
     * - Stateful observer (maintains commission rate configuration)
     * - Business logic encapsulation (commission calculation logic lives here)
     * - Separation of concerns (commission logic separate from deal management)
     */
    public static class CommissionCalculationObserver implements DealObserver {

        private static final Logger LOGGER = Logger.getLogger(CommissionCalculationObserver.class.getName());

        /**
         * Configuration: The commission rate to apply.
         *
         * STATEFUL OBSERVER CONCEPT:
         * Observers can maintain their own state (like this commission rate).
         * Different instances of the same observer class can have different
         * configurations, allowing flexible reuse.
         */
        private final BigDecimal commissionRate;

        public CommissionCalculationObserver(BigDecimal commissionRate) {
            this.commissionRate = commissionRate;
        }

        /**
         * Responds to deal updates by calculating commissions for won deals.
         *
         * SELECTIVE FILTERING STRATEGY:
         * This observer uses a two-level filter:
         * 1. First filter: Only process "STATUS_CHANGED" events
         * 2. Second filter: Only process if new status is WON
         *
         * WHY THIS APPROACH:
         * - Efficient: Ignores irrelevant events quickly
         * - Clear: Intent is obvious from the code
         * - Flexible: Easy to add more conditions if needed
         *
         * ALTERNATIVE APPROACHES:
         * - Could create separate observer interfaces for different event types
         * - Could use event subscription filters at the subject level
         * - Could implement a more sophisticated event routing system
         */
        @Override
        public void onDealUpdated(Deal deal, String eventType) {
            // FIRST FILTER: Check event type
            // We only care about status changes, not creation or value updates
            if (!"STATUS_CHANGED".equals(eventType)) {
                return; // Ignore other event types
            }

            // SECOND FILTER: Check if deal was won
            // Commissions are only calculated when deals close successfully
            if (deal.getStatus() != DealStatus.WON) {
                return; // Ignore other status changes (LOST, PENDING, etc.)
            }

            // Both filters passed - calculate commission
            calculateCommission(deal);
        }

        /**
         * Calculates and logs the commission for a won deal.
         *
         * BUSINESS LOGIC ENCAPSULATION:
         * The commission calculation logic is encapsulated in the observer.
         * The subject (ObservableDealTracker) doesn't need to know anything
         * about commission calculations - this observer handles it independently.
         *
         * REAL-WORLD EXTENSION POINTS:
         * In a production system, this method would:
         * - Create a CommissionCalculation record in the database
         * - Update the sales rep's commission balance
         * - Trigger payment processing workflows
         * - Send commission statements
         * - Update accounting systems
         */
        private void calculateCommission(Deal deal) {
            // Calculate commission amount
            BigDecimal commissionAmount = deal.getValue().multiply(commissionRate);

            // Log the calculation (in production, this would be persisted)
            LOGGER.info(String.format(
                    "[COMMISSION CALCULATOR] Deal '%s' (ID: %s) closed. " +
                            "Deal Value: $%s, Commission Rate: %s%%, Commission Amount: $%s",
                    deal.getTitle(),
                    deal.getId(),
                    deal.getValue(),
                    commissionRate.multiply(new BigDecimal("100")),
                    commissionAmount
            ));

            // In production implementation:
            // commissionService.createCommission(deal, commissionAmount);
            // paymentProcessor.schedulePayment(deal.getSalesRepId(), commissionAmount);
            // notificationService.sendCommissionStatement(deal.getSalesRepId(), commissionAmount);
        }
    }

    /**
     * CONCRETE OBSERVER #2: AuditLogObserver
     * =======================================
     *
     * This observer demonstrates UNIVERSAL event handling - it responds to
     * ALL events without filtering.
     *
     * PATTERN ROLE: Concrete Observer
     *
     * BUSINESS PURPOSE:
     * Maintains a comprehensive audit trail of all deal changes for
     * compliance, debugging, and historical analysis.
     *
     * KEY CONCEPTS DEMONSTRATED:
     * - Universal event handling (no filtering - records everything)
     * - Stateful observer (maintains audit log collection)
     * - Independent state management (audit log is separate from subject)
     * - Data persistence pattern (though simplified in this example)
     */
    public static class AuditLogObserver implements DealObserver {

        private static final Logger LOGGER = Logger.getLogger(AuditLogObserver.class.getName());
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * In-memory audit log storage.
         *
         * STATE MANAGEMENT:
         * This observer maintains its own state (the audit log) independent
         * of the subject. This demonstrates that observers can have complex
         * internal state that they manage in response to notifications.
         *
         * PRODUCTION CONSIDERATION:
         * In a real system, this would write to:
         * - A database table (for queryable audit logs)
         * - A log file (for file-based auditing)
         * - An audit service (for centralized compliance tracking)
         * - A message queue (for asynchronous processing)
         */
        private final List<String> auditLog;

        public AuditLogObserver() {
            this.auditLog = new ArrayList<>();
        }

        /**
         * Records every deal update in the audit log.
         *
         * UNIVERSAL HANDLING STRATEGY:
         * Unlike CommissionCalculationObserver which filters events, this observer
         * processes EVERY notification it receives. This is appropriate for audit
         * logging where completeness is more important than efficiency.
         *
         * NO FILTERING RATIONALE:
         * Audit logs must be complete for compliance purposes. We can't afford to
         * miss any changes, so we record everything and let queries filter later.
         */
        @Override
        public void onDealUpdated(Deal deal, String eventType) {
            // Create timestamp for audit entry
            String timestamp = LocalDateTime.now().format(FORMATTER);

            // Format comprehensive audit log entry with all relevant information
            // In production, this might include: user who made change, IP address,
            // before/after values, transaction ID, etc.
            String logEntry = String.format(
                    "[%s] Event: %s | Deal ID: %s | Title: %s | Value: $%s | Status: %s",
                    timestamp,
                    eventType,
                    deal.getId(),
                    deal.getTitle(),
                    deal.getValue(),
                    deal.getStatus()
            );

            // Store in audit log
            auditLog.add(logEntry);

            // Also log to system logger for immediate visibility
            LOGGER.info("[AUDIT LOG] " + logEntry);

            // Production implementation would:
            // - Write to database: auditRepository.save(new AuditEntry(...))
            // - Include user context: SecurityContext.getCurrentUser()
            // - Include change details: captureBeforeAfterValues(deal)
            // - Support querying: buildIndexes(), enableFullTextSearch()
        }

        /**
         * Returns all audit log entries.
         *
         * DEFENSIVE COPYING:
         * We return a copy of the audit log to prevent external modification.
         * This maintains encapsulation and prevents clients from corrupting
         * the audit trail.
         */
        public List<String> getAuditLog() {
            return new ArrayList<>(auditLog);
        }

        public int getLogCount() {
            return auditLog.size();
        }
    }

    /**
     * CONCRETE OBSERVER #3: NotificationObserver
     * ===========================================
     *
     * This observer demonstrates CONDITIONAL event handling - it processes
     * multiple event types but with complex conditional logic for each.
     *
     * PATTERN ROLE: Concrete Observer
     *
     * BUSINESS PURPOSE:
     * Sends appropriate notifications to stakeholders based on deal events,
     * with intelligent routing based on deal characteristics.
     *
     * KEY CONCEPTS DEMONSTRATED:
     * - Multi-event handling (processes several event types differently)
     * - Conditional logic (high-value deals get different treatment)
     * - External system integration (simulated email/notification sending)
     * - Configuration-driven behavior (threshold-based logic)
     */
    public static class NotificationObserver implements DealObserver {

        private static final Logger LOGGER = Logger.getLogger(NotificationObserver.class.getName());

        /**
         * Configuration: Threshold for "high-value" deals requiring special handling.
         */
        private final BigDecimal highValueThreshold;

        /**
         * Tracking: Record of sent notifications (for testing/verification).
         */
        private final List<String> sentNotifications;

        public NotificationObserver(BigDecimal highValueThreshold) {
            this.highValueThreshold = highValueThreshold;
            this.sentNotifications = new ArrayList<>();
        }

        /**
         * Routes notifications based on event type and deal characteristics.
         *
         * EVENT ROUTING STRATEGY:
         * This observer uses a switch statement to handle different event types.
         * Each event type has its own handler method, keeping the code organized
         * and maintainable.
         *
         * DESIGN PATTERN WITHIN A PATTERN:
         * This is actually a mini-implementation of the Strategy pattern within
         * our Observer - different strategies for different event types.
         */
        @Override
        public void onDealUpdated(Deal deal, String eventType) {
            // Route to appropriate handler based on event type
            switch (eventType) {
                case "CREATED":
                    handleDealCreated(deal);
                    break;

                case "STATUS_CHANGED":
                    handleStatusChanged(deal);
                    break;

                case "VALUE_UPDATED":
                    handleValueUpdated(deal);
                    break;

                default:
                    // Log unhandled events for debugging
                    // We don't send notifications for every possible event
                    LOGGER.fine(String.format("Deal %s: %s event (no notification)",
                            deal.getId(), eventType));
            }
        }

        /**
         * Handles notifications when a new deal is created.
         *
         * CONDITIONAL LOGIC:
         * High-value deals get escalated to management, while normal deals
         * just go to the team. This demonstrates business rule implementation
         * within an observer.
         */
        private void handleDealCreated(Deal deal) {
            String message = String.format(
                    "New deal created: '%s' (ID: %s) - Value: $%s - Sales Rep: %s",
                    deal.getTitle(),
                    deal.getId(),
                    deal.getValue(),
                    deal.getSalesRepId()
            );

            // BUSINESS RULE: High-value deals require management attention
            if (deal.getValue().compareTo(highValueThreshold) > 0) {
                // Escalate to management
                sendNotification("sales-managers@company.com", "High-Value Deal Alert", message);
            } else {
                // Normal notification to team
                sendNotification("sales-team@company.com", "New Deal Created", message);
            }
        }

        /**
         * Handles notifications when a deal's status changes.
         *
         * STATUS-SPECIFIC LOGIC:
         * Different statuses trigger different notifications to different audiences.
         */
        private void handleStatusChanged(Deal deal) {
            // Check for successful closure
            if (deal.getStatus() == DealStatus.WON) {
                String message = String.format(
                        "Deal closed: '%s' (ID: %s) - Final Value: $%s - Congratulations to Rep %s!",
                        deal.getTitle(),
                        deal.getId(),
                        deal.getValue(),
                        deal.getSalesRepId()
                );
                // Celebrate wins with the whole team
                sendNotification("sales-team@company.com", "Deal Closed", message);

            } else if (deal.getStatus() == DealStatus.LOST) {
                String message = String.format(
                        "Deal lost: '%s' (ID: %s) - Sales Rep: %s",
                        deal.getTitle(),
                        deal.getId(),
                        deal.getSalesRepId()
                );
                // Private notification to sales rep (learning opportunity)
                sendNotification(deal.getSalesRepId() + "@company.com", "Deal Status Update", message);
            }
            // Other status changes (PENDING, etc.) don't trigger notifications
        }

        /**
         * Handles notifications when a deal's value is updated.
         */
        private void handleValueUpdated(Deal deal) {
            String message = String.format(
                    "Deal value updated: '%s' (ID: %s) - New Value: $%s",
                    deal.getTitle(),
                    deal.getId(),
                    deal.getValue()
            );

            // Notify the sales rep managing the deal
            sendNotification(deal.getSalesRepId() + "@company.com", "Deal Value Updated", message);
        }

        /**
         * Simulates sending a notification (email, Slack, SMS, etc.).
         *
         * EXTERNAL SYSTEM INTEGRATION:
         * In production, this would integrate with actual notification services.
         * This demonstrates how observers can trigger actions in external systems
         * without the subject knowing anything about those systems.
         */
        private void sendNotification(String recipient, String subject, String message) {
            String notification = String.format(
                    "To: %s | Subject: %s | Message: %s",
                    recipient, subject, message
            );

            // Track sent notifications
            sentNotifications.add(notification);

            // Log the notification
            LOGGER.info("[NOTIFICATION SENT] " + notification);

            // Production implementation would:
            // - emailService.send(new Email(recipient, subject, message));
            // - slackClient.postMessage(channel, message);
            // - smsService.sendSms(phoneNumber, message);
            // - pushNotificationService.send(userId, message);
        }

        public List<String> getSentNotifications() {
            return new ArrayList<>(sentNotifications);
        }
    }

    /**
     * IMPLEMENTATION SUMMARY
     * ======================
     *
     * This file demonstrated three different observer implementation strategies:
     *
     * 1. SELECTIVE HANDLING (CommissionCalculationObserver):
     *    - Filters for specific event type and conditions
     *    - Ignores irrelevant events
     *    - Efficient for specialized observers
     *
     * 2. UNIVERSAL HANDLING (AuditLogObserver):
     *    - Processes all events without filtering
     *    - Comprehensive tracking
     *    - Appropriate for logging/auditing
     *
     * 3. CONDITIONAL HANDLING (NotificationObserver):
     *    - Processes multiple event types
     *    - Complex conditional logic per event
     *    - Business rule implementation
     *
     * KEY TAKEAWAYS:
     * - Observers are independent - they don't know about each other
     * - Each observer can have different logic and state
     * - Observers can be added/removed without modifying the subject
     * - Different observers can respond to the same event differently
     * - The pattern supports the Open/Closed Principle perfectly
     */
}