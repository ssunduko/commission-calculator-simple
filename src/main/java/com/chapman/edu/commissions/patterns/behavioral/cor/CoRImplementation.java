package com.chapman.edu.commissions.patterns.behavioral.cor;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.cor.CoRStructure.*;

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

    }