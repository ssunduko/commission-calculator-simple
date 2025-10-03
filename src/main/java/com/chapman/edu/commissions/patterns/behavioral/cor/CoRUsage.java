package com.chapman.edu.commissions.patterns.behavioral.cor;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.cor.CoRImplementation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CHAIN OF RESPONSIBILITY PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Chain of Responsibility Pattern in commission approval workflows.
 *
 * DEMONSTRATES:
 * 1. Basic chain usage with different approval levels
 * 2. Adding/removing handlers dynamically
 * 3. Short-circuiting the chain (early termination)
 * 4. Interceptor pattern within CoR
 * 5. Multiple chains for different scenarios
 * 6. Testing and debugging chain behavior
 *
 * KEY LEARNING POINTS:
 * - Chain decouples sender from receiver
 * - Handlers can be added/removed/reordered easily
 * - Cross-cutting concerns implemented as interceptors
 * - Chain provides flexibility in request handling
 * - Audit trails naturally emerge from chain processing
 *
 * @author Commission Calculator Educational Project
 */
public class CoRUsage {

    /**
     * EXAMPLE 1: Standard Approval Chain
     *
     * Demonstrates the basic usage with full approval chain.
     */
    public static void exampleStandardChain() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        EXAMPLE 1: Standard Approval Chain                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Build standard chain
        ApprovalHandler chain = buildStandardChain();

        // Test with different amounts
        System.out.println("Scenario: Processing commissions at different amounts\n");

        // Low amount - auto-approved
        CommissionApprovalRequest req1 = createRequest("REQ-101", "Small Deal",
            new BigDecimal("25000"), new BigDecimal("2500"), "Alice Johnson");

        System.out.println("→ Request 1: $2,500 commission (Low amount)");
        System.out.println("-".repeat(60));
        chain.approve(req1);
        printResult(req1);

        // Medium amount - manager approval
        CommissionApprovalRequest req2 = createRequest("REQ-102", "Medium Deal",
            new BigDecimal("150000"), new BigDecimal("15000"), "Bob Smith");

        System.out.println("\n→ Request 2: $15,000 commission (Manager level)");
        System.out.println("-".repeat(60));
        chain.approve(req2);
        printResult(req2);

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Same chain, different handlers processed based on amount.");
        System.out.println("Client doesn't need to know which handler will process.\n");
    }

    /**
     * EXAMPLE 2: Dynamic Chain Modification
     *
     * Shows how to add or remove handlers at runtime.
     */
    public static void exampleDynamicModification() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 2: Dynamic Chain Modification               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Building custom chain for special circumstances\n");

        // Build minimal chain (no fraud detection)
        ApprovalHandler validation = new ValidationHandler();
        ApprovalHandler autoApproval = new AutoApprovalHandler();
        ApprovalHandler manager = new SalesManagerApprovalHandler("Quick Approver");

        validation.setNext(autoApproval).setNext(manager);

        System.out.println("Chain 1: Validation → Auto → Manager (fast track)\n");

        CommissionApprovalRequest req = createRequest("REQ-201", "Rush Deal",
            new BigDecimal("100000"), new BigDecimal("8000"), "Carol White");

        System.out.println("→ Processing with fast-track chain:");
        System.out.println("-".repeat(60));
        validation.approve(req);
        printResult(req);

        // Now build chain WITH fraud detection for high-risk
        System.out.println("\n\nChain 2: Validation → Fraud → Auto → Manager (high-risk)\n");

        ApprovalHandler validation2 = new ValidationHandler();
        ApprovalHandler fraud = new FraudDetectionHandler();
        ApprovalHandler autoApproval2 = new AutoApprovalHandler();
        ApprovalHandler manager2 = new SalesManagerApprovalHandler("Careful Approver");

        validation2.setNext(fraud).setNext(autoApproval2).setNext(manager2);

        CommissionApprovalRequest req2 = createRequest("REQ-202", "Suspicious Deal",
            new BigDecimal("100000"), new BigDecimal("8000"), "Dave Brown");

        System.out.println("→ Processing with high-risk chain:");
        System.out.println("-".repeat(60));
        validation2.approve(req2);
        printResult(req2);

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Chain composition can be customized based on business rules.");
        System.out.println("Add fraud detection for high-risk, skip for trusted sources.\n");
    }

    /**
     * EXAMPLE 3: Early Termination (Short-Circuiting)
     *
     * Demonstrates how validation failures short-circuit the chain.
     */
    public static void exampleEarlyTermination() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 3: Early Termination / Short-Circuiting     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        ApprovalHandler chain = buildStandardChain();

        System.out.println("Scenario: Invalid requests stop chain early\n");

        // Invalid request 1: Deal not WON
        Deal openDeal = new Deal("Open Deal", new BigDecimal("100000"), "REP-301");
        openDeal.setId("DEAL-301");
        openDeal.setStatus(DealStatus.OPEN);  // Not WON!

        CommissionApprovalRequest req1 = new CommissionApprovalRequest("REQ-301",
            openDeal, "REP-301", "Eve Davis", new BigDecimal("10000"));

        System.out.println("→ Request 1: Commission for OPEN deal (invalid)");
        System.out.println("-".repeat(60));
        chain.approve(req1);
        printResult(req1);

        // Invalid request 2: Negative amount
        Deal wonDeal = new Deal("Won Deal", new BigDecimal("100000"), "REP-302");
        wonDeal.setId("DEAL-302");
        wonDeal.setStatus(DealStatus.WON);
        wonDeal.setCloseDate(LocalDate.now());

        CommissionApprovalRequest req2 = new CommissionApprovalRequest("REQ-302",
            wonDeal, "REP-302", "Frank Miller", new BigDecimal("-5000"));

        System.out.println("\n→ Request 2: Negative commission amount (invalid)");
        System.out.println("-".repeat(60));
        chain.approve(req2);
        printResult(req2);

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Validation handler rejects invalid requests immediately.");
        System.out.println("Chain short-circuits - no need to check all handlers.\n");
    }

    /**
     * EXAMPLE 4: Multiple Parallel Chains
     *
     * Shows using different chains for different purposes.
     */
    public static void exampleMultipleChains() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        EXAMPLE 4: Multiple Parallel Chains               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Different approval chains for different regions\n");

        // North America chain (strict)
        ApprovalHandler naChain = buildNorthAmericaChain();
        System.out.println("North America Chain: Validation → Fraud → Auto → Manager → Director");

        // Europe chain (more lenient)
        ApprovalHandler euChain = buildEuropeChain();
        System.out.println("Europe Chain: Validation → Auto → Manager\n");

        CommissionApprovalRequest naRequest = createRequest("REQ-401", "NA Deal",
            new BigDecimal("200000"), new BigDecimal("20000"), "NA Sales Rep");

        CommissionApprovalRequest euRequest = createRequest("REQ-402", "EU Deal",
            new BigDecimal("200000"), new BigDecimal("20000"), "EU Sales Rep");

        System.out.println("→ North America Request ($20k):");
        System.out.println("-".repeat(60));
        naChain.approve(naRequest);
        System.out.println("  Steps: " + naRequest.getApprovalHistory().size());

        System.out.println("\n→ Europe Request ($20k):");
        System.out.println("-".repeat(60));
        euChain.approve(euRequest);
        System.out.println("  Steps: " + euRequest.getApprovalHistory().size());

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Different regions can have different approval workflows.");
        System.out.println("NA: " + naRequest.getApprovalHistory().size() + " steps");
        System.out.println("EU: " + euRequest.getApprovalHistory().size() + " steps\n");
    }

    /**
     * EXAMPLE 5: Audit Trail Analysis
     *
     * Demonstrates using the approval history for analysis.
     */
    public static void exampleAuditTrail() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 5: Audit Trail Analysis                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        ApprovalHandler chain = buildStandardChain();

        System.out.println("Scenario: Analyzing approval workflow for compliance\n");

        List<CommissionApprovalRequest> requests = new ArrayList<>();

        // Process multiple requests
        for (int i = 1; i <= 3; i++) {
            BigDecimal amount = new BigDecimal(i * 50000);  // $50k, $100k, $150k
            CommissionApprovalRequest req = createRequest("REQ-50" + i, "Deal " + i,
                amount.multiply(new BigDecimal("5")), amount, "Rep " + i);
            chain.approve(req);
            requests.add(req);
        }

        // Analyze audit trails
        System.out.println("📊 AUDIT TRAIL ANALYSIS:");
        System.out.println("=".repeat(60));

        for (CommissionApprovalRequest req : requests) {
            System.out.println("\n" + req.getRequestId() + " ($" + req.getCommissionAmount() + "):");
            System.out.println("  Status: " + (req.isApproved() ? "APPROVED" : "REJECTED"));
            System.out.println("  Process steps: " + req.getApprovalHistory().size());
            System.out.println("  Trail:");
            for (String entry : req.getApprovalHistory()) {
                System.out.println("    • " + entry);
            }
        }

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Chain automatically creates complete audit trail.");
        System.out.println("Perfect for compliance, debugging, and analysis.\n");
    }

    /**
     * EXAMPLE 6: Testing Chain Behavior
     *
     * Shows how to test individual handlers and chains.
     */
    public static void exampleTesting() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 6: Testing Chain Behavior                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Unit testing approval logic\n");

        // Test 1: Auto-approval threshold
        System.out.println("Test 1: Auto-approval threshold ($5,000)");
        System.out.println("-".repeat(60));

        ApprovalHandler autoHandler = new AutoApprovalHandler();

        CommissionApprovalRequest belowThreshold = createRequest("TEST-1", "Below",
            new BigDecimal("20000"), new BigDecimal("4999"), "Test Rep");

        CommissionApprovalRequest atThreshold = createRequest("TEST-2", "At",
            new BigDecimal("20000"), new BigDecimal("5000"), "Test Rep");

        autoHandler.approve(belowThreshold);
        autoHandler.approve(atThreshold);

        System.out.println("$4,999: " + (belowThreshold.isApproved() ? "✓ APPROVED" : "✗ NOT APPROVED"));
        System.out.println("$5,000: " + (atThreshold.isApproved() ? "✓ APPROVED" : "✗ NOT APPROVED"));

        // Test 2: Validation rules
        System.out.println("\nTest 2: Validation rules");
        System.out.println("-".repeat(60));

        ApprovalHandler validator = new ValidationHandler();

        // Valid
        CommissionApprovalRequest valid = createRequest("TEST-3", "Valid",
            new BigDecimal("100000"), new BigDecimal("10000"), "Test Rep");
        validator.approve(valid);

        // Invalid (negative amount)
        CommissionApprovalRequest invalid = new CommissionApprovalRequest("TEST-4",
            createDeal("Invalid", new BigDecimal("100000"), DealStatus.WON),
            "REP-TEST", "Test Rep", new BigDecimal("-1000"));
        validator.approve(invalid);

        System.out.println("Valid request: " + (!valid.isRejected() ? "✓ PASSED" : "✗ FAILED"));
        System.out.println("Invalid request: " + (invalid.isRejected() ? "✓ REJECTED" : "✗ PASSED"));

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Handlers can be tested independently.");
        System.out.println("Chain behavior emerges from individual handler logic.\n");
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
        System.out.println("║     CHAIN OF RESPONSIBILITY - COMPREHENSIVE EXAMPLES      ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates commission approval workflow patterns       ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        exampleStandardChain();
        pause();

        exampleDynamicModification();
        pause();

        exampleEarlyTermination();
        pause();

        exampleMultipleChains();
        pause();

        exampleAuditTrail();
        pause();

        exampleTesting();

        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. LOOSE COUPLING");
        System.out.println("   → Sender doesn't know which handler will process");
        System.out.println("   → Handlers only know about their successor");
        System.out.println();
        System.out.println("2. DYNAMIC CHAINS");
        System.out.println("   → Chain can be built/modified at runtime");
        System.out.println("   → Easy to add/remove handlers");
        System.out.println();
        System.out.println("3. FLEXIBLE PROCESSING");
        System.out.println("   → Handler can process and stop");
        System.out.println("   → Handler can process and continue");
        System.out.println("   → Handler can just pass to next");
        System.out.println();
        System.out.println("4. CROSS-CUTTING CONCERNS");
        System.out.println("   → Interceptors handle validation, logging, fraud");
        System.out.println("   → Separated from business logic handlers");
        System.out.println();
        System.out.println("5. AUDIT TRAILS");
        System.out.println("   → Chain naturally creates process history");
        System.out.println("   → Perfect for compliance and debugging");
        System.out.println();
        System.out.println("6. TESTABILITY");
        System.out.println("   → Each handler can be tested independently");
        System.out.println("   → Chain behavior is composable");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    // Helper methods

    private static ApprovalHandler buildStandardChain() {
        ApprovalHandler validation = new ValidationHandler();
        ApprovalHandler fraud = new FraudDetectionHandler();
        ApprovalHandler autoApproval = new AutoApprovalHandler();
        ApprovalHandler manager = new SalesManagerApprovalHandler("Michael Chen");
        ApprovalHandler director = new RegionalDirectorApprovalHandler("Sarah Johnson");
        ApprovalHandler vp = new VPSalesApprovalHandler("David Martinez");
        ApprovalHandler cfo = new CFOApprovalHandler("Elizabeth Taylor");
        ApprovalHandler audit = new AuditLogHandler();

        return validation.setNext(fraud)
                        .setNext(autoApproval)
                        .setNext(manager)
                        .setNext(director)
                        .setNext(vp)
                        .setNext(cfo)
                        .setNext(audit);
    }

    private static ApprovalHandler buildNorthAmericaChain() {
        return new ValidationHandler()
            .setNext(new FraudDetectionHandler())
            .setNext(new AutoApprovalHandler())
            .setNext(new SalesManagerApprovalHandler("NA Manager"))
            .setNext(new RegionalDirectorApprovalHandler("NA Director"));
    }

    private static ApprovalHandler buildEuropeChain() {
        return new ValidationHandler()
            .setNext(new AutoApprovalHandler())
            .setNext(new SalesManagerApprovalHandler("EU Manager"));
    }

    private static CommissionApprovalRequest createRequest(String id, String dealTitle,
                                                          BigDecimal dealValue, BigDecimal commission,
                                                          String repName) {
        Deal deal = createDeal(dealTitle, dealValue, DealStatus.WON);
        return new CommissionApprovalRequest(id, deal, "REP-" + id, repName, commission);
    }

    private static Deal createDeal(String title, BigDecimal value, DealStatus status) {
        Deal deal = new Deal(title, value, "REP-X");
        deal.setId("DEAL-" + System.currentTimeMillis());
        deal.setStatus(status);
        if (status == DealStatus.WON) {
            deal.setCloseDate(LocalDate.now().minusDays(5));
        }
        return deal;
    }

    private static void printResult(CommissionApprovalRequest req) {
        System.out.println("\n📋 Result: " + (req.isApproved() ? "✅ APPROVED" :
                          (req.isRejected() ? "❌ REJECTED - " + req.getRejectionReason() : "⏳ PENDING")));
        System.out.println("   Process steps: " + req.getApprovalHistory().size());
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