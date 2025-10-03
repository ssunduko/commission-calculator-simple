package com.chapman.edu.commissions.patterns.behavioral.cor;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CHAIN OF RESPONSIBILITY PATTERN - COMMISSION APPROVAL IMPLEMENTATION
 *
 * REAL-WORLD APPLICATION:
 * This implementation demonstrates using the Chain of Responsibility pattern for
 * commission approval workflows. Different approval levels (Sales Manager, Regional Director,
 * VP of Sales, CFO) handle different commission amounts based on their authority limits.
 *
 * BUSINESS CONTEXT:
 * In a commission system, large commissions require approval before payout. The approval
 * authority depends on the commission amount:
 * - Under $5,000: Auto-approved (no human approval needed)
 * - $5,000-$25,000: Sales Manager approval
 * - $25,000-$100,000: Regional Director approval
 * - $100,000-$500,000: VP of Sales approval
 * - Over $500,000: CFO approval required
 *
 * Additionally, all commissions go through validation, fraud detection, and audit logging
 * regardless of amount (cross-cutting concerns using interceptor-style handlers).
 *
 * BENEFITS IN THIS CONTEXT:
 * 1. Eliminates complex if-else chains for routing approvals
 * 2. Easy to add new approval levels or modify thresholds
 * 3. Separates approval logic from commission calculation
 * 4. Enables adding cross-cutting concerns (logging, validation) without modifying approvers
 * 5. Makes approval workflow explicit and configurable
 *
 * @author Commission Calculator Educational Project
 */
public class CoRImplementation {

    /**
     * COMMISSION APPROVAL REQUEST
     *
     * Represents a commission that needs approval.
     * Contains all necessary information for approval decision.
     */
    public static class CommissionApprovalRequest {
        private final String requestId;
        private final Deal deal;
        private final String salesRepId;
        private final String salesRepName;
        private final BigDecimal commissionAmount;
        private final LocalDate requestDate;
        private final List<String> approvalHistory;
        private boolean approved = false;
        private boolean rejected = false;
        private String rejectionReason;

        public CommissionApprovalRequest(String requestId, Deal deal, String salesRepId,
                                        String salesRepName, BigDecimal commissionAmount) {
            this.requestId = requestId;
            this.deal = deal;
            this.salesRepId = salesRepId;
            this.salesRepName = salesRepName;
            this.commissionAmount = commissionAmount.setScale(2, RoundingMode.HALF_UP);
            this.requestDate = LocalDate.now();
            this.approvalHistory = new ArrayList<>();
        }

        public String getRequestId() {
            return requestId;
        }

        public Deal getDeal() {
            return deal;
        }

        public String getSalesRepId() {
            return salesRepId;
        }

        public String getSalesRepName() {
            return salesRepName;
        }

        public BigDecimal getCommissionAmount() {
            return commissionAmount;
        }

        public LocalDate getRequestDate() {
            return requestDate;
        }

        public List<String> getApprovalHistory() {
            return new ArrayList<>(approvalHistory);
        }

        public void addToHistory(String entry) {
            approvalHistory.add(entry);
        }

        public boolean isApproved() {
            return approved;
        }

        public void approve(String approver, String comments) {
            this.approved = true;
            addToHistory("✓ APPROVED by " + approver + ": " + comments);
        }

        public boolean isRejected() {
            return rejected;
        }

        public void reject(String rejector, String reason) {
            this.rejected = true;
            this.rejectionReason = reason;
            addToHistory("✗ REJECTED by " + rejector + ": " + reason);
        }

        public String getRejectionReason() {
            return rejectionReason;
        }

        public boolean isProcessed() {
            return approved || rejected;
        }

        @Override
        public String toString() {
            return "CommissionApprovalRequest{" +
                   "id='" + requestId + '\'' +
                   ", salesRep='" + salesRepName + '\'' +
                   ", amount=$" + commissionAmount +
                   ", dealTitle='" + deal.getTitle() + '\'' +
                   '}';
        }
    }

    /**
     * APPROVAL HANDLER INTERFACE
     *
     * Defines the interface for commission approval handlers.
     */
    public interface ApprovalHandler {
        ApprovalHandler setNext(ApprovalHandler handler);
        void approve(CommissionApprovalRequest request);
        String getHandlerName();
    }

    /**
     * BASE APPROVAL HANDLER
     *
     * Provides common chain management logic for all approval handlers.
     */
    public static abstract class BaseApprovalHandler implements ApprovalHandler {
        private ApprovalHandler nextHandler;

        @Override
        public ApprovalHandler setNext(ApprovalHandler handler) {
            this.nextHandler = handler;
            return handler;
        }

        @Override
        public void approve(CommissionApprovalRequest request) {
            // Check if request already processed
            if (request.isProcessed()) {
                passToNext(request);
                return;
            }

            // Check if this handler can process
            if (canApprove(request)) {
                processApproval(request);

                // Continue chain if needed (e.g., for audit trail)
                if (shouldContinueChain(request)) {
                    passToNext(request);
                }
            } else {
                // This handler cannot approve, pass to next
                request.addToHistory("→ Forwarded to " +
                    (nextHandler != null ? nextHandler.getHandlerName() : "next level"));
                passToNext(request);
            }
        }

        protected void passToNext(CommissionApprovalRequest request) {
            if (nextHandler != null) {
                nextHandler.approve(request);
            } else {
                // End of chain
                if (!request.isProcessed()) {
                    request.reject("System", "No approver available at this level");
                    System.out.println("⚠️  End of approval chain - Request requires higher authority");
                }
            }
        }

        protected abstract boolean canApprove(CommissionApprovalRequest request);
        protected abstract void processApproval(CommissionApprovalRequest request);

        protected boolean shouldContinueChain(CommissionApprovalRequest request) {
            return false;  // Default: stop after approval
        }
    }

    /**
     * AUTO-APPROVAL HANDLER
     *
     * Automatically approves low-value commissions (under $5,000).
     * No human approval needed.
     */
    public static class AutoApprovalHandler extends BaseApprovalHandler {
        private static final BigDecimal THRESHOLD = new BigDecimal("5000");

        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            return request.getCommissionAmount().compareTo(THRESHOLD) < 0;
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("🤖 AutoApprovalHandler: Processing request " + request.getRequestId());
            System.out.println("   Amount: $" + request.getCommissionAmount() + " (under $" + THRESHOLD + ")");
            request.approve("System (Auto-Approval)",
                          "Automatically approved - below threshold");
            System.out.println("   ✓ APPROVED automatically");
        }

        @Override
        public String getHandlerName() {
            return "Auto-Approval System";
        }
    }

    /**
     * SALES MANAGER APPROVAL HANDLER
     *
     * Approves commissions between $5,000 and $25,000.
     */
    public static class SalesManagerApprovalHandler extends BaseApprovalHandler {
        private static final BigDecimal MIN_THRESHOLD = new BigDecimal("5000");
        private static final BigDecimal MAX_THRESHOLD = new BigDecimal("25000");
        private final String managerName;

        public SalesManagerApprovalHandler(String managerName) {
            this.managerName = managerName;
        }

        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            BigDecimal amount = request.getCommissionAmount();
            return amount.compareTo(MIN_THRESHOLD) >= 0 &&
                   amount.compareTo(MAX_THRESHOLD) < 0;
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("👔 SalesManagerApprovalHandler: Processing request " +
                             request.getRequestId());
            System.out.println("   Manager: " + managerName);
            System.out.println("   Amount: $" + request.getCommissionAmount());

            // Simulate approval logic
            if (isValidDeal(request.getDeal())) {
                request.approve("Sales Manager (" + managerName + ")",
                              "Deal verified and approved");
                System.out.println("   ✓ APPROVED by Sales Manager");
            } else {
                request.reject("Sales Manager (" + managerName + ")",
                             "Deal validation failed");
                System.out.println("   ✗ REJECTED by Sales Manager");
            }
        }

        private boolean isValidDeal(Deal deal) {
            // Simulate validation: deal must be WON
            return deal.getStatus() == DealStatus.WON;
        }

        @Override
        public String getHandlerName() {
            return "Sales Manager (" + managerName + ")";
        }
    }

    /**
     * REGIONAL DIRECTOR APPROVAL HANDLER
     *
     * Approves commissions between $25,000 and $100,000.
     */
    public static class RegionalDirectorApprovalHandler extends BaseApprovalHandler {
        private static final BigDecimal MIN_THRESHOLD = new BigDecimal("25000");
        private static final BigDecimal MAX_THRESHOLD = new BigDecimal("100000");
        private final String directorName;

        public RegionalDirectorApprovalHandler(String directorName) {
            this.directorName = directorName;
        }

        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            BigDecimal amount = request.getCommissionAmount();
            return amount.compareTo(MIN_THRESHOLD) >= 0 &&
                   amount.compareTo(MAX_THRESHOLD) < 0;
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("👔👔 RegionalDirectorApprovalHandler: Processing request " +
                             request.getRequestId());
            System.out.println("   Director: " + directorName);
            System.out.println("   Amount: $" + request.getCommissionAmount());

            // Additional validation for larger commissions
            if (hasValidDocumentation(request)) {
                request.approve("Regional Director (" + directorName + ")",
                              "Documentation verified and approved");
                System.out.println("   ✓ APPROVED by Regional Director");
            } else {
                request.reject("Regional Director (" + directorName + ")",
                             "Missing required documentation");
                System.out.println("   ✗ REJECTED by Regional Director");
            }
        }

        private boolean hasValidDocumentation(CommissionApprovalRequest request) {
            // Simulate documentation check
            // In real system, would check for signed contracts, POs, etc.
            return request.getDeal().getCloseDate() != null;
        }

        @Override
        public String getHandlerName() {
            return "Regional Director (" + directorName + ")";
        }
    }

    /**
     * VP OF SALES APPROVAL HANDLER
     *
     * Approves commissions between $100,000 and $500,000.
     */
    public static class VPSalesApprovalHandler extends BaseApprovalHandler {
        private static final BigDecimal MIN_THRESHOLD = new BigDecimal("100000");
        private static final BigDecimal MAX_THRESHOLD = new BigDecimal("500000");
        private final String vpName;

        public VPSalesApprovalHandler(String vpName) {
            this.vpName = vpName;
        }

        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            BigDecimal amount = request.getCommissionAmount();
            return amount.compareTo(MIN_THRESHOLD) >= 0 &&
                   amount.compareTo(MAX_THRESHOLD) < 0;
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("👔👔👔 VPSalesApprovalHandler: Processing request " +
                             request.getRequestId());
            System.out.println("   VP of Sales: " + vpName);
            System.out.println("   Amount: $" + request.getCommissionAmount());

            // Executive-level validation
            if (meetsExecutiveReviewCriteria(request)) {
                request.approve("VP of Sales (" + vpName + ")",
                              "Executive review completed and approved");
                System.out.println("   ✓ APPROVED by VP of Sales");
            } else {
                request.reject("VP of Sales (" + vpName + ")",
                             "Does not meet executive approval criteria");
                System.out.println("   ✗ REJECTED by VP of Sales");
            }
        }

        private boolean meetsExecutiveReviewCriteria(CommissionApprovalRequest request) {
            // Simulate executive review criteria
            // Check deal value is proportional to commission
            BigDecimal dealValue = request.getDeal().getValue();
            BigDecimal commissionPercent = request.getCommissionAmount()
                .divide(dealValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

            // Commission should be reasonable (e.g., under 30%)
            return commissionPercent.compareTo(new BigDecimal("30")) < 0;
        }

        @Override
        public String getHandlerName() {
            return "VP of Sales (" + vpName + ")";
        }
    }

    /**
     * CFO APPROVAL HANDLER
     *
     * Approves commissions over $500,000.
     * Highest level of approval authority.
     */
    public static class CFOApprovalHandler extends BaseApprovalHandler {
        private static final BigDecimal THRESHOLD = new BigDecimal("500000");
        private final String cfoName;

        public CFOApprovalHandler(String cfoName) {
            this.cfoName = cfoName;
        }

        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            return request.getCommissionAmount().compareTo(THRESHOLD) >= 0;
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("💼 CFOApprovalHandler: Processing request " + request.getRequestId());
            System.out.println("   CFO: " + cfoName);
            System.out.println("   Amount: $" + request.getCommissionAmount() +
                             " (Executive approval required)");

            // CFO-level review
            if (meetsFinancialGuidelines(request)) {
                request.approve("CFO (" + cfoName + ")",
                              "Financial review completed - approved with CFO authority");
                System.out.println("   ✓ APPROVED by CFO");
            } else {
                request.reject("CFO (" + cfoName + ")",
                             "Does not meet financial guidelines");
                System.out.println("   ✗ REJECTED by CFO");
            }
        }

        private boolean meetsFinancialGuidelines(CommissionApprovalRequest request) {
            // Simulate CFO-level financial review
            // Check if commission is within budget, reasonable, etc.
            return true;  // For demo, assume all pass CFO review
        }

        @Override
        public String getHandlerName() {
            return "CFO (" + cfoName + ")";
        }
    }

    /**
     * VALIDATION HANDLER (Interceptor)
     *
     * Validates all requests before they reach approvers.
     * This is a cross-cutting concern that runs regardless of amount.
     */
    public static class ValidationHandler extends BaseApprovalHandler {
        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            return true;  // Validate all requests
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("🔍 ValidationHandler: Validating request " + request.getRequestId());

            List<String> errors = new ArrayList<>();

            // Validate commission amount
            if (request.getCommissionAmount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Commission amount must be positive");
            }

            // Validate deal
            if (request.getDeal() == null) {
                errors.add("Deal is required");
            } else if (request.getDeal().getStatus() != DealStatus.WON) {
                errors.add("Commission only allowed for WON deals");
            }

            // Validate sales rep
            if (request.getSalesRepId() == null || request.getSalesRepId().isEmpty()) {
                errors.add("Sales representative ID is required");
            }

            if (!errors.isEmpty()) {
                String errorMsg = String.join("; ", errors);
                request.reject("Validation System", errorMsg);
                System.out.println("   ✗ Validation FAILED: " + errorMsg);
            } else {
                request.addToHistory("✓ Validation passed");
                System.out.println("   ✓ Validation PASSED");
            }
        }

        @Override
        protected boolean shouldContinueChain(CommissionApprovalRequest request) {
            return !request.isRejected();  // Continue only if not rejected
        }

        @Override
        public String getHandlerName() {
            return "Validation System";
        }
    }

    /**
     * FRAUD DETECTION HANDLER (Interceptor)
     *
     * Checks for potential fraud indicators.
     * Another cross-cutting concern.
     */
    public static class FraudDetectionHandler extends BaseApprovalHandler {
        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            return true;  // Check all requests
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("🚨 FraudDetectionHandler: Scanning for fraud indicators");

            boolean suspicious = false;
            List<String> flags = new ArrayList<>();

            // Check for unusually high commission rate
            BigDecimal dealValue = request.getDeal().getValue();
            BigDecimal commissionRate = request.getCommissionAmount()
                .divide(dealValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

            if (commissionRate.compareTo(new BigDecimal("50")) > 0) {
                flags.add("Unusually high commission rate: " + commissionRate + "%");
                suspicious = true;
            }

            // Check for same-day deal close
            if (request.getDeal().getCloseDate() != null &&
                request.getDeal().getCloseDate().equals(LocalDate.now())) {
                flags.add("Deal closed same day as request");
                // Not necessarily fraud, just flag it
            }

            if (suspicious) {
                String flagMsg = String.join("; ", flags);
                request.reject("Fraud Detection System", "FRAUD ALERT: " + flagMsg);
                System.out.println("   🚨 FRAUD ALERT: " + flagMsg);
            } else if (!flags.isEmpty()) {
                request.addToHistory("⚠ Fraud check: " + String.join("; ", flags));
                System.out.println("   ⚠️  Flagged but not blocked: " + String.join("; ", flags));
            } else {
                request.addToHistory("✓ Fraud check passed");
                System.out.println("   ✓ No fraud indicators detected");
            }
        }

        @Override
        protected boolean shouldContinueChain(CommissionApprovalRequest request) {
            return !request.isRejected();  // Continue only if not rejected
        }

        @Override
        public String getHandlerName() {
            return "Fraud Detection System";
        }
    }

    /**
     * AUDIT LOG HANDLER (Interceptor)
     *
     * Logs all approval requests for compliance.
     * Runs at the end of the chain.
     */
    public static class AuditLogHandler extends BaseApprovalHandler {
        @Override
        protected boolean canApprove(CommissionApprovalRequest request) {
            return true;  // Audit all requests
        }

        @Override
        protected void processApproval(CommissionApprovalRequest request) {
            System.out.println("📝 AuditLogHandler: Recording audit trail");
            request.addToHistory("Audit log entry created");
            System.out.println("   ✓ Audit trail recorded");
        }

        @Override
        protected boolean shouldContinueChain(CommissionApprovalRequest request) {
            return true;  // Can continue, but usually last in chain
        }

        @Override
        public String getHandlerName() {
            return "Audit Log System";
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Chain of Responsibility pattern works for commission approvals.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║    CHAIN OF RESPONSIBILITY - COMMISSION APPROVAL DEMO     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Build the approval chain
        ApprovalHandler chain = buildApprovalChain();

        // Create test requests with different amounts
        List<CommissionApprovalRequest> requests = createTestRequests();

        // Process each request through the chain
        for (CommissionApprovalRequest request : requests) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Processing: " + request);
            System.out.println("=".repeat(60) + "\n");

            chain.approve(request);

            // Display result
            System.out.println("\n📋 RESULT:");
            if (request.isApproved()) {
                System.out.println("   ✅ REQUEST APPROVED");
            } else if (request.isRejected()) {
                System.out.println("   ❌ REQUEST REJECTED: " + request.getRejectionReason());
            }

            System.out.println("\n📜 Approval History:");
            for (String entry : request.getApprovalHistory()) {
                System.out.println("   " + entry);
            }
        }

        System.out.println("\n\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                         SUMMARY                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("The approval chain demonstrates:");
        System.out.println("  ✓ Dynamic routing based on commission amount");
        System.out.println("  ✓ Automatic handling of low-value commissions");
        System.out.println("  ✓ Escalation to appropriate authority level");
        System.out.println("  ✓ Cross-cutting concerns (validation, fraud, audit)");
        System.out.println("  ✓ Complete audit trail for all requests");
        System.out.println();
    }

    /**
     * Helper: Build the approval chain with all handlers
     */
    private static ApprovalHandler buildApprovalChain() {
        // Interceptors (cross-cutting concerns)
        ApprovalHandler validation = new ValidationHandler();
        ApprovalHandler fraudDetection = new FraudDetectionHandler();

        // Approval authorities (increasing authority)
        ApprovalHandler autoApproval = new AutoApprovalHandler();
        ApprovalHandler salesManager = new SalesManagerApprovalHandler("Michael Chen");
        ApprovalHandler regionalDirector = new RegionalDirectorApprovalHandler("Sarah Johnson");
        ApprovalHandler vpSales = new VPSalesApprovalHandler("David Martinez");
        ApprovalHandler cfo = new CFOApprovalHandler("Elizabeth Taylor");

        // Audit (runs at end)
        ApprovalHandler audit = new AuditLogHandler();

        // Build chain: Validation → Fraud → Auto → Manager → Director → VP → CFO → Audit
        validation.setNext(fraudDetection)
                 .setNext(autoApproval)
                 .setNext(salesManager)
                 .setNext(regionalDirector)
                 .setNext(vpSales)
                 .setNext(cfo)
                 .setNext(audit);

        return validation;
    }

    /**
     * Helper: Create test requests with various amounts
     */
    private static List<CommissionApprovalRequest> createTestRequests() {
        List<CommissionApprovalRequest> requests = new ArrayList<>();

        // Request 1: Low value (auto-approved)
        Deal deal1 = new Deal("Small Software License", new BigDecimal("20000"), "REP-001");
        deal1.setId("DEAL-001");
        deal1.setStatus(DealStatus.WON);
        deal1.setCloseDate(LocalDate.now().minusDays(5));
        requests.add(new CommissionApprovalRequest("REQ-001", deal1, "REP-001",
                    "Alice Johnson", new BigDecimal("3000")));

        // Request 2: Manager level
        Deal deal2 = new Deal("Hardware Package", new BigDecimal("80000"), "REP-002");
        deal2.setId("DEAL-002");
        deal2.setStatus(DealStatus.WON);
        deal2.setCloseDate(LocalDate.now().minusDays(3));
        requests.add(new CommissionApprovalRequest("REQ-002", deal2, "REP-002",
                    "Bob Smith", new BigDecimal("12000")));

        // Request 3: Director level
        Deal deal3 = new Deal("Enterprise Software Suite", new BigDecimal("500000"), "REP-003");
        deal3.setId("DEAL-003");
        deal3.setStatus(DealStatus.WON);
        deal3.setCloseDate(LocalDate.now().minusDays(10));
        requests.add(new CommissionApprovalRequest("REQ-003", deal3, "REP-003",
                    "Carol White", new BigDecimal("75000")));

        // Request 4: VP level
        Deal deal4 = new Deal("Multi-Year Services Contract", new BigDecimal("2000000"), "REP-004");
        deal4.setId("DEAL-004");
        deal4.setStatus(DealStatus.WON);
        deal4.setCloseDate(LocalDate.now().minusDays(15));
        requests.add(new CommissionApprovalRequest("REQ-004", deal4, "REP-004",
                    "Dave Brown", new BigDecimal("300000")));

        // Request 5: CFO level (very high value)
        Deal deal5 = new Deal("Strategic Partnership Deal", new BigDecimal("10000000"), "REP-005");
        deal5.setId("DEAL-005");
        deal5.setStatus(DealStatus.WON);
        deal5.setCloseDate(LocalDate.now().minusDays(20));
        requests.add(new CommissionApprovalRequest("REQ-005", deal5, "REP-005",
                    "Eve Davis", new BigDecimal("600000")));

        return requests;
    }
}