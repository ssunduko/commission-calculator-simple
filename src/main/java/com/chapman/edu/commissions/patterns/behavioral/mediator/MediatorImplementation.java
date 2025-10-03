package com.chapman.edu.commissions.patterns.behavioral.mediator;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MEDIATOR PATTERN - COMMISSION SYSTEM IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation demonstrates the Mediator pattern for coordinating interactions
 * in a commission system. When a deal closes, multiple components need to be notified
 * and coordinated: commission calculations, approval workflows, notifications, audit logs,
 * and dispute tracking.
 *
 * BUSINESS CONTEXT:
 * In a commission system, when a deal is won:
 * 1. Commission needs to be calculated
 * 2. Approval workflow may be triggered (if amount is high)
 * 3. Sales rep needs to be notified
 * 4. Manager needs to be notified
 * 5. Finance team needs to be notified
 * 6. Audit log needs to be updated
 * 7. Potential disputes need to be tracked
 *
 * Without a mediator, each component would need to know about all others,
 * creating a tightly coupled mess. The mediator centralizes this coordination.
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Components don't need to know about each other
 * 2. Easy to add new components (e.g., new notification channels)
 * 3. Coordination logic is centralized and testable
 * 4. Can modify workflow without changing components
 * 5. Reduces dependencies between subsystems
 *
 * @author Commission Calculator Educational Project
 */
public class MediatorImplementation {

    /**
     * COMMISSION EVENT
     *
     * Represents events that occur in the commission system.
     * Events are passed through the mediator to interested components.
     */
    public static class CommissionEvent {
        private final String eventType;
        private final String sourceComponent;
        private final Map<String, Object> data;
        private final LocalDateTime timestamp;

        public CommissionEvent(String eventType, String sourceComponent) {
            this.eventType = eventType;
            this.sourceComponent = sourceComponent;
            this.data = new HashMap<>();
            this.timestamp = LocalDateTime.now();
        }

        public CommissionEvent addData(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public String getEventType() {
            return eventType;
        }

        public String getSourceComponent() {
            return sourceComponent;
        }

        public Object getData(String key) {
            return data.get(key);
        }

        public Map<String, Object> getAllData() {
            return new HashMap<>(data);
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "CommissionEvent{" +
                   "type='" + eventType + '\'' +
                   ", source='" + sourceComponent + '\'' +
                   ", data=" + data +
                   '}';
        }
    }

    /**
     * COMMISSION SYSTEM MEDIATOR INTERFACE
     *
     * Defines the interface for coordinating commission system components.
     */
    public interface CommissionSystemMediator {
        void registerComponent(SystemComponent component);
        void notify(SystemComponent sender, CommissionEvent event);
        void unregisterComponent(SystemComponent component);
    }

    /**
     * SYSTEM COMPONENT (Colleague Base Class)
     *
     * Base class for all components in the commission system.
     * Each component knows the mediator but not other components.
     */
    public static abstract class SystemComponent {
        protected CommissionSystemMediator mediator;
        protected String componentName;
        protected boolean enabled = true;

        public SystemComponent(String componentName) {
            this.componentName = componentName;
        }

        public void setMediator(CommissionSystemMediator mediator) {
            this.mediator = mediator;
        }

        public String getComponentName() {
            return componentName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Send an event through the mediator.
         */
        protected void sendEvent(CommissionEvent event) {
            if (mediator != null && enabled) {
                mediator.notify(this, event);
            }
        }

        /**
         * Handle an event received from the mediator.
         */
        public abstract void handleEvent(CommissionEvent event);

        /**
         * Check if this component is interested in the given event type.
         */
        public abstract boolean isInterestedIn(String eventType);
    }

    /**
     * CONCRETE MEDIATOR - Commission Coordination Hub
     *
     * Implements the coordination logic for the commission system.
     * Routes events to appropriate components based on event type.
     */
    public static class CommissionCoordinationHub implements CommissionSystemMediator {
        private List<SystemComponent> components;
        private List<CommissionEvent> eventHistory;
        private boolean loggingEnabled = true;

        public CommissionCoordinationHub() {
            this.components = new ArrayList<>();
            this.eventHistory = new ArrayList<>();
        }

        @Override
        public void registerComponent(SystemComponent component) {
            components.add(component);
            component.setMediator(this);
            log("Registered component: " + component.getComponentName());
        }

        @Override
        public void unregisterComponent(SystemComponent component) {
            components.remove(component);
            log("Unregistered component: " + component.getComponentName());
        }

        @Override
        public void notify(SystemComponent sender, CommissionEvent event) {
            // Log event
            eventHistory.add(event);
            log("Event received: " + event.getEventType() + " from " + sender.getComponentName());

            // Route to interested components
            for (SystemComponent component : components) {
                if (component != sender && component.isEnabled() &&
                    component.isInterestedIn(event.getEventType())) {
                    log("  → Routing to " + component.getComponentName());
                    component.handleEvent(event);
                }
            }
        }

        private void log(String message) {
            if (loggingEnabled) {
                System.out.println("🔄 Hub: " + message);
            }
        }

        public void setLoggingEnabled(boolean enabled) {
            this.loggingEnabled = enabled;
        }

        public List<CommissionEvent> getEventHistory() {
            return new ArrayList<>(eventHistory);
        }

        public int getEventCount() {
            return eventHistory.size();
        }
    }

    /**
     * DEAL TRACKER COMPONENT
     *
     * Monitors deal status changes and triggers commission calculations.
     */
    public static class DealTrackerComponent extends SystemComponent {
        public DealTrackerComponent() {
            super("DealTracker");
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            // Interested in commission calculated events to update deal status
            return eventType.equals("COMMISSION_CALCULATED") ||
                   eventType.equals("APPROVAL_COMPLETED");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            System.out.println("  📊 " + componentName + ": Handling " + event.getEventType());

            if (event.getEventType().equals("COMMISSION_CALCULATED")) {
                BigDecimal amount = (BigDecimal) event.getData("amount");
                System.out.println("     Updated deal with commission: $" + amount);
            } else if (event.getEventType().equals("APPROVAL_COMPLETED")) {
                System.out.println("     Deal approval status updated");
            }
        }

        /**
         * When a deal is won, trigger commission calculation.
         */
        public void onDealWon(Deal deal) {
            System.out.println("\n💼 " + componentName + ": Deal won - " + deal.getTitle());

            CommissionEvent event = new CommissionEvent("DEAL_WON", componentName);
            event.addData("deal", deal);
            event.addData("dealId", deal.getId());
            event.addData("dealValue", deal.getValue());
            event.addData("salesRepId", deal.getSalesRepId());

            sendEvent(event);
        }
    }

    /**
     * COMMISSION CALCULATOR COMPONENT
     *
     * Calculates commissions when deals are won.
     */
    public static class CommissionCalculatorComponent extends SystemComponent {
        private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10%

        public CommissionCalculatorComponent() {
            super("CommissionCalculator");
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("DEAL_WON");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            System.out.println("  💰 " + componentName + ": Calculating commission");

            Deal deal = (Deal) event.getData("deal");
            BigDecimal commission = calculateCommission(deal);

            System.out.println("     Commission calculated: $" + commission);

            // Notify system of calculated commission
            CommissionEvent calculatedEvent = new CommissionEvent("COMMISSION_CALCULATED", componentName);
            calculatedEvent.addData("deal", deal);
            calculatedEvent.addData("dealId", deal.getId());
            calculatedEvent.addData("amount", commission);
            calculatedEvent.addData("salesRepId", deal.getSalesRepId());

            sendEvent(calculatedEvent);
        }

        private BigDecimal calculateCommission(Deal deal) {
            return deal.getValue().multiply(COMMISSION_RATE);
        }
    }

    /**
     * APPROVAL WORKFLOW COMPONENT
     *
     * Triggers approval workflows for high-value commissions.
     */
    public static class ApprovalWorkflowComponent extends SystemComponent {
        private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("5000");

        public ApprovalWorkflowComponent() {
            super("ApprovalWorkflow");
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("COMMISSION_CALCULATED");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            BigDecimal amount = (BigDecimal) event.getData("amount");

            if (amount.compareTo(APPROVAL_THRESHOLD) >= 0) {
                System.out.println("  ✅ " + componentName + ": Approval required for $" + amount);
                initiateApproval(event);
            } else {
                System.out.println("  ✅ " + componentName + ": Auto-approved (below threshold)");
                autoApprove(event);
            }
        }

        private void initiateApproval(CommissionEvent event) {
            System.out.println("     Initiating approval workflow...");

            CommissionEvent approvalEvent = new CommissionEvent("APPROVAL_REQUIRED", componentName);
            approvalEvent.addData("dealId", event.getData("dealId"));
            approvalEvent.addData("amount", event.getData("amount"));
            approvalEvent.addData("salesRepId", event.getData("salesRepId"));

            sendEvent(approvalEvent);
        }

        private void autoApprove(CommissionEvent event) {
            CommissionEvent approvedEvent = new CommissionEvent("APPROVAL_COMPLETED", componentName);
            approvedEvent.addData("dealId", event.getData("dealId"));
            approvedEvent.addData("amount", event.getData("amount"));
            approvedEvent.addData("approved", true);
            approvedEvent.addData("approver", "System (Auto-Approved)");

            sendEvent(approvedEvent);
        }
    }

    /**
     * NOTIFICATION COMPONENT
     *
     * Sends notifications to relevant parties.
     */
    public static class NotificationComponent extends SystemComponent {
        private List<String> sentNotifications;

        public NotificationComponent() {
            super("NotificationService");
            this.sentNotifications = new ArrayList<>();
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("COMMISSION_CALCULATED") ||
                   eventType.equals("APPROVAL_REQUIRED") ||
                   eventType.equals("APPROVAL_COMPLETED") ||
                   eventType.equals("DISPUTE_CREATED");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            System.out.println("  📧 " + componentName + ": Sending notifications");

            switch (event.getEventType()) {
                case "COMMISSION_CALCULATED":
                    notifySalesRep(event);
                    break;
                case "APPROVAL_REQUIRED":
                    notifyManager(event);
                    break;
                case "APPROVAL_COMPLETED":
                    notifyFinanceTeam(event);
                    break;
                case "DISPUTE_CREATED":
                    notifyDisputeTeam(event);
                    break;
            }
        }

        private void notifySalesRep(CommissionEvent event) {
            String salesRepId = (String) event.getData("salesRepId");
            BigDecimal amount = (BigDecimal) event.getData("amount");
            String notification = "Commission calculated: $" + amount + " for " + salesRepId;
            sentNotifications.add(notification);
            System.out.println("     → Email sent to sales rep: " + salesRepId);
        }

        private void notifyManager(CommissionEvent event) {
            BigDecimal amount = (BigDecimal) event.getData("amount");
            String notification = "Approval required for commission: $" + amount;
            sentNotifications.add(notification);
            System.out.println("     → Email sent to manager for approval");
        }

        private void notifyFinanceTeam(CommissionEvent event) {
            BigDecimal amount = (BigDecimal) event.getData("amount");
            String notification = "Commission approved for payout: $" + amount;
            sentNotifications.add(notification);
            System.out.println("     → Email sent to finance team");
        }

        private void notifyDisputeTeam(CommissionEvent event) {
            String disputeId = (String) event.getData("disputeId");
            String notification = "New dispute created: " + disputeId;
            sentNotifications.add(notification);
            System.out.println("     → Email sent to dispute team");
        }

        public List<String> getSentNotifications() {
            return new ArrayList<>(sentNotifications);
        }
    }

    /**
     * AUDIT LOG COMPONENT
     *
     * Records all commission-related events for compliance.
     */
    public static class AuditLogComponent extends SystemComponent {
        private List<String> auditTrail;

        public AuditLogComponent() {
            super("AuditLog");
            this.auditTrail = new ArrayList<>();
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return true;  // Audit everything
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            String logEntry = String.format("[%s] %s: %s",
                event.getTimestamp(),
                event.getEventType(),
                event.getAllData());

            auditTrail.add(logEntry);
            System.out.println("  📝 " + componentName + ": Logged event");
        }

        public List<String> getAuditTrail() {
            return new ArrayList<>(auditTrail);
        }

        public void printAuditTrail() {
            System.out.println("\n📜 AUDIT TRAIL:");
            System.out.println("=".repeat(60));
            for (int i = 0; i < auditTrail.size(); i++) {
                System.out.println((i + 1) + ". " + auditTrail.get(i));
            }
            System.out.println("=".repeat(60));
        }
    }

    /**
     * DISPUTE TRACKER COMPONENT
     *
     * Monitors for potential disputes and flags issues.
     */
    public static class DisputeTrackerComponent extends SystemComponent {
        private List<String> flaggedDisputes;

        public DisputeTrackerComponent() {
            super("DisputeTracker");
            this.flaggedDisputes = new ArrayList<>();
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("COMMISSION_CALCULATED");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            BigDecimal amount = (BigDecimal) event.getData("amount");
            Deal deal = (Deal) event.getData("deal");

            // Check for potential dispute (e.g., very high commission rate)
            BigDecimal commissionRate = amount.divide(deal.getValue(), 4, java.math.RoundingMode.HALF_UP);

            if (commissionRate.compareTo(new BigDecimal("0.20")) > 0) {
                System.out.println("  ⚠️  " + componentName + ": High commission rate detected - " +
                                 "potential dispute");
                flagPotentialDispute(event);
            }
        }

        private void flagPotentialDispute(CommissionEvent event) {
            String dealId = (String) event.getData("dealId");
            flaggedDisputes.add(dealId);

            CommissionEvent disputeEvent = new CommissionEvent("DISPUTE_FLAGGED", componentName);
            disputeEvent.addData("dealId", dealId);
            disputeEvent.addData("reason", "High commission rate");

            sendEvent(disputeEvent);
        }

        public List<String> getFlaggedDisputes() {
            return new ArrayList<>(flaggedDisputes);
        }
    }

    /**
     * ANALYTICS COMPONENT
     *
     * Collects statistics and metrics.
     */
    public static class AnalyticsComponent extends SystemComponent {
        private int dealsProcessed = 0;
        private BigDecimal totalCommissions = BigDecimal.ZERO;
        private int approvalsRequired = 0;

        public AnalyticsComponent() {
            super("Analytics");
        }

        @Override
        public boolean isInterestedIn(String eventType) {
            return eventType.equals("DEAL_WON") ||
                   eventType.equals("COMMISSION_CALCULATED") ||
                   eventType.equals("APPROVAL_REQUIRED");
        }

        @Override
        public void handleEvent(CommissionEvent event) {
            switch (event.getEventType()) {
                case "DEAL_WON":
                    dealsProcessed++;
                    break;
                case "COMMISSION_CALCULATED":
                    BigDecimal amount = (BigDecimal) event.getData("amount");
                    totalCommissions = totalCommissions.add(amount);
                    break;
                case "APPROVAL_REQUIRED":
                    approvalsRequired++;
                    break;
            }
        }

        public void printStatistics() {
            System.out.println("\n📊 ANALYTICS SUMMARY:");
            System.out.println("=".repeat(60));
            System.out.println("Deals Processed: " + dealsProcessed);
            System.out.println("Total Commissions: $" + totalCommissions);
            System.out.println("Approvals Required: " + approvalsRequired);
            System.out.println("=".repeat(60));
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Mediator pattern coordinates commission system components.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     MEDIATOR PATTERN - COMMISSION SYSTEM DEMO             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create mediator
        CommissionCoordinationHub hub = new CommissionCoordinationHub();

        // Create and register components
        System.out.println("Setting up commission system components...\n");

        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        ApprovalWorkflowComponent approvalWorkflow = new ApprovalWorkflowComponent();
        NotificationComponent notifications = new NotificationComponent();
        AuditLogComponent auditLog = new AuditLogComponent();
        DisputeTrackerComponent disputeTracker = new DisputeTrackerComponent();
        AnalyticsComponent analytics = new AnalyticsComponent();

        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);
        hub.registerComponent(approvalWorkflow);
        hub.registerComponent(notifications);
        hub.registerComponent(auditLog);
        hub.registerComponent(disputeTracker);
        hub.registerComponent(analytics);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Scenario 1: Low-value deal (auto-approved)
        System.out.println("SCENARIO 1: Low-Value Deal ($25,000)\n");
        Deal deal1 = new Deal("Small Software License", new BigDecimal("25000"), "REP-001");
        deal1.setId("DEAL-001");
        deal1.setStatus(DealStatus.WON);
        deal1.setCloseDate(LocalDate.now());

        dealTracker.onDealWon(deal1);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Scenario 2: High-value deal (requires approval)
        System.out.println("SCENARIO 2: High-Value Deal ($100,000)\n");
        Deal deal2 = new Deal("Enterprise Software Suite", new BigDecimal("100000"), "REP-002");
        deal2.setId("DEAL-002");
        deal2.setStatus(DealStatus.WON);
        deal2.setCloseDate(LocalDate.now());

        dealTracker.onDealWon(deal2);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Display results
        auditLog.printAuditTrail();
        System.out.println();
        analytics.printStatistics();

        System.out.println("\n📧 Notifications Sent: " + notifications.getSentNotifications().size());
        System.out.println("⚠️  Disputes Flagged: " + disputeTracker.getFlaggedDisputes().size());

        System.out.println("\n\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                         SUMMARY                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("The mediator coordinated 7 components without them knowing");
        System.out.println("about each other:");
        System.out.println("  • Deal Tracker → Commission Calculator → Approval");
        System.out.println("  • All events → Notifications → Audit Log → Analytics");
        System.out.println();
        System.out.println("Benefits demonstrated:");
        System.out.println("  ✓ Loose coupling between components");
        System.out.println("  ✓ Centralized coordination logic");
        System.out.println("  ✓ Easy to add/remove components");
        System.out.println("  ✓ Complete audit trail automatically created");
        System.out.println();
    }
}