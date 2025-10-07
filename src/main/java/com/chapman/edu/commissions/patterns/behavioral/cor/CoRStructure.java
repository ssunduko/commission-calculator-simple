package com.chapman.edu.commissions.patterns.behavioral.cor;

import com.chapman.edu.commissions.model.Deal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CHAIN OF RESPONSIBILITY PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Chain of Responsibility Pattern lets you pass requests along a chain of handlers.
 * Upon receiving a request, each handler decides either to process the request or to pass
 * it to the next handler in the chain. This decouples the sender of a request from its receivers.
 *
 * PROBLEM IT SOLVES:
 * - Avoid coupling the sender of a request to its receiver
 * - Allow more than one object to handle a request
 * - Let the set of objects that can handle a request be specified dynamically
 * - Eliminate complex conditional logic for routing requests
 * - Make it easy to add or remove handlers without affecting the client
 *
 * WHEN TO USE:
 * - More than one object may handle a request, and the handler isn't known a priori
 * - You want to issue a request to one of several objects without specifying the receiver explicitly
 * - The set of objects that can handle a request should be specified dynamically
 * - You want to avoid explicit if-else chains for handling different request types
 * - Processing steps need to be executed in a specific order
 *
 * COMPONENTS:
 * 1. Handler (Interface/Abstract): Defines interface for handling requests and optionally
 *    implements the successor link
 * 2. ConcreteHandler: Handles requests it is responsible for; can access its successor
 * 3. Client: Initiates the request to a Handler object in the chain
 *
 * KEY CONCEPTS:
 * - Handlers are chained together (each has reference to next handler)
 * - Request flows through the chain until a handler processes it
 * - Handler can: (1) Process and stop, (2) Process and pass to next, (3) Pass to next
 * - The chain can be assembled at runtime
 *
 * @author Commission Calculator Educational Project
 */
public class CoRStructure {

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
}