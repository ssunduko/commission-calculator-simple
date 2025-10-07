package com.chapman.edu.commissions.patterns.behavioral.mediator;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorStructure.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
 */
public class MediatorImplementation {

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
}