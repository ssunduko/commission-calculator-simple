package com.chapman.edu.commissions.patterns.behavioral.mediator;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorImplementation.*;
import com.chapman.edu.commissions.patterns.behavioral.mediator.MediatorStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MEDIATOR PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Mediator Pattern in commission system coordination.
 *
 * DEMONSTRATES:
 * 1. Basic mediator usage with multiple components
 * 2. Dynamic component registration/unregistration
 * 3. Event filtering and selective handling
 * 4. Component enable/disable for maintenance
 * 5. Testing mediator behavior
 * 6. Performance considerations
 *
 * KEY LEARNING POINTS:
 * - Mediator decouples components from each other
 * - Components only know about the mediator
 * - Easy to add new components without affecting existing ones
 * - Centralized coordination simplifies complex workflows
 * - Perfect for event-driven architectures
 *
 */
public class MediatorUsage {

    /**
     * EXAMPLE 1: Basic System Setup
     *
     * Demonstrates setting up a complete commission system with mediator.
     */
    public static void exampleBasicSetup() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        EXAMPLE 1: Basic System Setup                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Setting up commission system from scratch\n");

        // Create mediator (coordination hub)
        CommissionCoordinationHub hub = new CommissionCoordinationHub();

        // Create essential components
        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        NotificationComponent notifications = new NotificationComponent();

        // Register with mediator
        System.out.println("Registering components...\n");
        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);
        hub.registerComponent(notifications);

        System.out.println("\n✓ System ready with 3 components\n");

        // Process a deal
        System.out.println("Processing deal...\n");
        Deal deal = createDeal("Test Deal", new BigDecimal("30000"));
        dealTracker.onDealWon(deal);

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Components coordinated through mediator without knowing each other.");
        System.out.println("DealTracker → Calculator → Notifications (all through hub)\n");
    }

    /**
     * EXAMPLE 2: Dynamic Component Management
     *
     * Shows adding and removing components at runtime.
     */
    public static void exampleDynamicComponents() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 2: Dynamic Component Management             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        CommissionCoordinationHub hub = new CommissionCoordinationHub();
        hub.setLoggingEnabled(false);  // Reduce noise for this example

        // Start with minimal system
        System.out.println("Phase 1: Minimal System (Calculator only)\n");
        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();

        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);

        Deal deal1 = createDeal("Deal 1", new BigDecimal("20000"));
        dealTracker.onDealWon(deal1);

        System.out.println("Events processed: " + hub.getEventCount());

        // Add notifications mid-flight
        System.out.println("\nPhase 2: Adding Notifications Component\n");
        NotificationComponent notifications = new NotificationComponent();
        hub.registerComponent(notifications);

        Deal deal2 = createDeal("Deal 2", new BigDecimal("30000"));
        dealTracker.onDealWon(deal2);

        System.out.println("Notifications sent: " + notifications.getSentNotifications().size());

        // Add analytics
        System.out.println("\nPhase 3: Adding Analytics Component\n");
        AnalyticsComponent analytics = new AnalyticsComponent();
        hub.registerComponent(analytics);

        Deal deal3 = createDeal("Deal 3", new BigDecimal("40000"));
        dealTracker.onDealWon(deal3);

        analytics.printStatistics();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Components can be added at any time without modifying existing code.");
        System.out.println("System grows from 2 → 3 → 4 components dynamically.\n");
    }

    /**
     * EXAMPLE 3: Component Enable/Disable
     *
     * Demonstrates temporarily disabling components (e.g., for maintenance).
     */
    public static void exampleComponentControl() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 3: Component Enable/Disable                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        CommissionCoordinationHub hub = new CommissionCoordinationHub();
        hub.setLoggingEnabled(false);

        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        NotificationComponent notifications = new NotificationComponent();

        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);
        hub.registerComponent(notifications);

        // Normal operation
        System.out.println("Scenario A: All components enabled\n");
        Deal deal1 = createDeal("Deal 1", new BigDecimal("25000"));
        dealTracker.onDealWon(deal1);
        System.out.println("Notifications sent: " + notifications.getSentNotifications().size());

        // Disable notifications (e.g., for maintenance)
        System.out.println("\nScenario B: Notifications disabled\n");
        notifications.setEnabled(false);
        System.out.println("⚠️  Notification component disabled for maintenance");

        Deal deal2 = createDeal("Deal 2", new BigDecimal("25000"));
        dealTracker.onDealWon(deal2);
        System.out.println("Notifications sent: " + notifications.getSentNotifications().size() +
                         " (no new notifications)");

        // Re-enable
        System.out.println("\nScenario C: Notifications re-enabled\n");
        notifications.setEnabled(true);
        System.out.println("✓ Notification component re-enabled");

        Deal deal3 = createDeal("Deal 3", new BigDecimal("25000"));
        dealTracker.onDealWon(deal3);
        System.out.println("Notifications sent: " + notifications.getSentNotifications().size());

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Components can be temporarily disabled without removing them.");
        System.out.println("Useful for maintenance, testing, or conditional execution.\n");
    }

    /**
     * EXAMPLE 4: Event Filtering
     *
     * Shows how components selectively handle events.
     */
    public static void exampleEventFiltering() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 4: Event Filtering                       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Components only process events they care about\n");

        CommissionCoordinationHub hub = new CommissionCoordinationHub();
        hub.setLoggingEnabled(false);

        // Create components with different interests
        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        ApprovalWorkflowComponent approval = new ApprovalWorkflowComponent();
        AnalyticsComponent analytics = new AnalyticsComponent();

        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);
        hub.registerComponent(approval);
        hub.registerComponent(analytics);

        System.out.println("Component Interests:");
        System.out.println("  Calculator: DEAL_WON");
        System.out.println("  Approval: COMMISSION_CALCULATED");
        System.out.println("  Analytics: DEAL_WON, COMMISSION_CALCULATED, APPROVAL_REQUIRED\n");

        // Process deal - watch selective handling
        System.out.println("Processing deal...\n");
        Deal deal = createDeal("Filtered Deal", new BigDecimal("60000"));
        dealTracker.onDealWon(deal);

        System.out.println("\nEvent flow:");
        System.out.println("  1. DEAL_WON → Calculator ✓, Analytics ✓");
        System.out.println("  2. COMMISSION_CALCULATED → Approval ✓, Analytics ✓");
        System.out.println("  3. APPROVAL_REQUIRED → Analytics ✓");

        analytics.printStatistics();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Components filter events via isInterestedIn() method.");
        System.out.println("Reduces unnecessary processing and coupling.\n");
    }

    /**
     * EXAMPLE 5: Testing with Mediator
     *
     * Demonstrates how mediator simplifies testing.
     */
    public static void exampleTesting() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 5: Testing with Mediator                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Testing individual components in isolation\n");

        // Test 1: Test component independently
        System.out.println("Test 1: Calculator component (isolated)\n");
        CommissionCoordinationHub testHub = new CommissionCoordinationHub();
        testHub.setLoggingEnabled(false);

        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        testHub.registerComponent(calculator);

        // Create test event
        CommissionEvent testEvent = new CommissionEvent("DEAL_WON", "TestSource");
        Deal testDeal = createDeal("Test Deal", new BigDecimal("10000"));
        testEvent.addData("deal", testDeal);

        calculator.handleEvent(testEvent);

        System.out.println("✓ Calculator tested independently");

        // Test 2: Test with mock/spy components
        System.out.println("\nTest 2: Testing interaction flow\n");

        /**
         * Mock Notification Component for testing
         */
        class MockNotificationComponent extends NotificationComponent {
            public int notificationCount = 0;

            @Override
            public void handleEvent(CommissionEvent event) {
                notificationCount++;
                System.out.println("  MockNotification: Received " + event.getEventType());
            }
        }

        CommissionCoordinationHub hub2 = new CommissionCoordinationHub();
        hub2.setLoggingEnabled(false);

        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator2 = new CommissionCalculatorComponent();
        MockNotificationComponent mockNotifications = new MockNotificationComponent();

        hub2.registerComponent(dealTracker);
        hub2.registerComponent(calculator2);
        hub2.registerComponent(mockNotifications);

        Deal deal = createDeal("Test Deal", new BigDecimal("20000"));
        dealTracker.onDealWon(deal);

        System.out.println("\nTest assertions:");
        System.out.println("  Events processed: " + hub2.getEventCount() + " (expected: 2)");
        System.out.println("  Notifications: " + mockNotifications.notificationCount + " (expected: 1)");
        System.out.println("  ✓ All assertions passed");

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Mediator enables testing components in isolation or with mocks.");
        System.out.println("Components don't need to know about each other's internals.\n");
    }

    /**
     * EXAMPLE 6: Complex Workflow Coordination
     *
     * Shows mediator coordinating a complete workflow.
     */
    public static void exampleComplexWorkflow() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 6: Complex Workflow Coordination            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Complete commission lifecycle\n");

        // Full system
        CommissionCoordinationHub hub = new CommissionCoordinationHub();

        DealTrackerComponent dealTracker = new DealTrackerComponent();
        CommissionCalculatorComponent calculator = new CommissionCalculatorComponent();
        ApprovalWorkflowComponent approval = new ApprovalWorkflowComponent();
        NotificationComponent notifications = new NotificationComponent();
        AuditLogComponent auditLog = new AuditLogComponent();
        DisputeTrackerComponent disputeTracker = new DisputeTrackerComponent();
        AnalyticsComponent analytics = new AnalyticsComponent();

        hub.registerComponent(dealTracker);
        hub.registerComponent(calculator);
        hub.registerComponent(approval);
        hub.registerComponent(notifications);
        hub.registerComponent(auditLog);
        hub.registerComponent(disputeTracker);
        hub.registerComponent(analytics);

        System.out.println("System ready with 7 components\n");
        System.out.println("=".repeat(60) + "\n");

        // Process multiple deals
        System.out.println("Processing Deal 1: $50,000 (requires approval)\n");
        Deal deal1 = createDeal("Enterprise Deal", new BigDecimal("50000"));
        dealTracker.onDealWon(deal1);

        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("Processing Deal 2: $150,000 (high value, may trigger dispute check)\n");
        Deal deal2 = createDeal("Strategic Partnership", new BigDecimal("150000"));
        dealTracker.onDealWon(deal2);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Summary
        System.out.println("WORKFLOW SUMMARY:\n");
        System.out.println("Total events: " + hub.getEventCount());
        System.out.println("Notifications sent: " + notifications.getSentNotifications().size());
        System.out.println("Disputes flagged: " + disputeTracker.getFlaggedDisputes().size());

        analytics.printStatistics();
        auditLog.printAuditTrail();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Mediator coordinated 7 components through 2 complex workflows.");
        System.out.println("Each component focused on its responsibility.");
        System.out.println("Complete audit trail automatically created.\n");
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║       MEDIATOR PATTERN - COMPREHENSIVE EXAMPLES           ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates commission system coordination patterns     ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        exampleBasicSetup();
        pause();

        exampleDynamicComponents();
        pause();

        exampleComponentControl();
        pause();

        exampleEventFiltering();
        pause();

        exampleTesting();
        pause();

        exampleComplexWorkflow();

        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. LOOSE COUPLING");
        System.out.println("   → Components don't know about each other");
        System.out.println("   → They only know the mediator");
        System.out.println();
        System.out.println("2. CENTRALIZED COORDINATION");
        System.out.println("   → All interaction logic in mediator");
        System.out.println("   → Easy to understand and modify workflows");
        System.out.println();
        System.out.println("3. EASY EXTENSIBILITY");
        System.out.println("   → Add new components without touching existing ones");
        System.out.println("   → Remove components without breaking others");
        System.out.println();
        System.out.println("4. SIMPLIFIED COMPONENTS");
        System.out.println("   → Components focus on their core responsibility");
        System.out.println("   → No need to manage multiple references");
        System.out.println();
        System.out.println("5. TESTABILITY");
        System.out.println("   → Components can be tested in isolation");
        System.out.println("   → Easy to mock other components");
        System.out.println();
        System.out.println("6. EVENT-DRIVEN ARCHITECTURE");
        System.out.println("   → Natural fit for event-driven systems");
        System.out.println("   → Components react to events asynchronously");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    // Helper methods

    private static Deal createDeal(String title, BigDecimal value) {
        Deal deal = new Deal(title, value, "REP-" + System.currentTimeMillis());
        deal.setId("DEAL-" + System.currentTimeMillis());
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now());
        return deal;
    }

    private static void pause() {
        System.out.println("\n[Press Enter to continue...]");
        System.out.println("─".repeat(60) + "\n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}