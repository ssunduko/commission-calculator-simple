package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternImplementation.PaymentTransaction;
import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternImplementation.PaymentItem;
import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternImplementation.PaymentItemImpl;
import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternImplementation.PaymentProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class demonstrates the combination of Adapter and Facade patterns.
 * 
 * Adapter Pattern: Adapts the Deal model to work with a third-party payment system
 * Facade Pattern: Provides a simplified interface to the complex subsystem of deal management,
 *                 user management, and payment processing
 * 
 * The AdapterFacade combines these patterns by:
 * 1. Using the Facade pattern to provide a simple interface for commission-related operations
 * 2. Using the Adapter pattern internally to adapt our domain models to work with external systems
 */
public class AdapterFacade {

    /**
     * The PaymentFacade provides a simplified interface to the complex subsystem
     * of deal management, user management, and payment processing.
     * 
     * It uses adapters internally to convert between different interfaces.
     */
    public static class PaymentFacade {
        private DealService dealService;
        private UserService userService;
        private PaymentProcessor paymentProcessor;

        public PaymentFacade() {
            this.dealService = new DealService();
            this.userService = new UserService();
            this.paymentProcessor = new PaymentProcessor();
        }

        /**
         * Process payment for a deal.
         * This method demonstrates the Facade pattern by hiding the complexity of:
         * 1. Retrieving the deal
         * 2. Adapting the deal to a payment transaction
         * 3. Processing the payment
         */
        public void processPaymentForDeal(String dealId) {
            // Get the deal from the service
            Deal deal = dealService.getDealById(dealId);

            if (deal == null) {
                throw new IllegalArgumentException("Deal not found: " + dealId);
            }

            // Adapt the deal to a payment transaction (Adapter Pattern)
            PaymentTransaction transaction = new DealPaymentAdapter(deal);

            // Process the payment using the adapted transaction
            paymentProcessor.processPayment(transaction);

            // Update deal status
            dealService.updateDealStatus(deal, DealStatus.WON);

            System.out.println("Payment processed and deal status updated to WON");
        }

        /**
         * Get all payments for a sales representative.
         * This method demonstrates the Facade pattern by hiding the complexity of:
         * 1. Retrieving all deals for a sales rep
         * 2. Adapting each deal to a payment transaction
         * 3. Returning a simplified list of transactions
         */
        public List<PaymentTransaction> getPaymentsForSalesRep(String salesRepId) {
            // Get all deals for the sales rep
            List<Deal> deals = dealService.getDealsBySalesRep(salesRepId);

            // Adapt each deal to a payment transaction
            List<PaymentTransaction> transactions = new ArrayList<>();
            for (Deal deal : deals) {
                if (deal.getStatus() == DealStatus.WON) {
                    transactions.add(new DealPaymentAdapter(deal));
                }
            }

            return transactions;
        }

        /**
         * Generate a payment report for a sales representative.
         * This method demonstrates the Facade pattern by providing a simple interface
         * to generate a complex report.
         */
        public void generatePaymentReport(String salesRepId) {
            User user = userService.getUserById(salesRepId);
            if (user == null) {
                throw new IllegalArgumentException("User not found: " + salesRepId);
            }

            List<PaymentTransaction> transactions = getPaymentsForSalesRep(salesRepId);

            System.out.println("Payment Report for: " + user.getFullName());
            System.out.println("Total Transactions: " + transactions.size());

            BigDecimal total = BigDecimal.ZERO;
            for (PaymentTransaction transaction : transactions) {
                System.out.println("Transaction ID: " + transaction.getTransactionId());
                System.out.println("  Amount: " + transaction.getAmount() + " " + transaction.getCurrency());
                System.out.println("  Date: " + transaction.getTransactionDate());
                total = total.add(BigDecimal.valueOf(transaction.getAmount()));
            }

            System.out.println("Total Amount: " + total);
        }
    }

    /**
     * Adapter class that adapts the Deal model to the PaymentTransaction interface.
     * This is the same adapter from the original Adapter pattern example.
     */
    public static class DealPaymentAdapter implements PaymentTransaction {
        private Deal deal;
        private static final String DEFAULT_CURRENCY = "USD";
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        public DealPaymentAdapter(Deal deal) {
            this.deal = deal;
        }

        @Override
        public String getTransactionId() {
            return deal.getId() != null ? deal.getId() : UUID.randomUUID().toString();
        }

        @Override
        public String getCustomerId() {
            return deal.getSalesRepId();
        }

        @Override
        public double getAmount() {
            return deal.getValue().doubleValue();
        }

        @Override
        public String getCurrency() {
            return DEFAULT_CURRENCY;
        }

        @Override
        public String getTransactionDate() {
            LocalDate date = deal.getCloseDate() != null ? deal.getCloseDate() : LocalDate.now();
            return date.format(DATE_FORMATTER);
        }

        @Override
        public List<PaymentItem> getItems() {
            List<PaymentItem> paymentItems = new ArrayList<>();

            for (DealProduct product : deal.getProducts()) {
                PaymentItem item = new PaymentItemImpl(
                    product.getProductId(),
                    product.getProductName(),
                    product.getQuantity(),
                    product.getPrice().doubleValue()
                );
                paymentItems.add(item);
            }

            return paymentItems;
        }

        @Override
        public String getStatus() {
            // Map our DealStatus to the payment system's expected status values
            if (deal.getStatus() == DealStatus.WON) {
                return "COMPLETED";
            } else if (deal.getStatus() == DealStatus.CANCELLED) {
                return "CANCELLED";
            } else if (deal.getStatus() == DealStatus.LOST) {
                return "DECLINED";
            } else {
                return "PENDING";
            }
        }
    }

    /**
     * Simple service for deal management (part of the subsystem)
     */
    public static class DealService {
        private List<Deal> deals;

        public DealService() {
            // Initialize with some sample deals
            this.deals = new ArrayList<>();

            // Create sample deals
            Deal deal1 = new Deal();
            deal1.setId("deal-1");
            deal1.setTitle("Enterprise Software License");
            deal1.setValue(new BigDecimal("10000.00"));
            deal1.setSalesRepId("user-1");
            deal1.setStatus(DealStatus.OPEN);

            Deal deal2 = new Deal();
            deal2.setId("deal-2");
            deal2.setTitle("Consulting Services");
            deal2.setValue(new BigDecimal("5000.00"));
            deal2.setSalesRepId("user-1");
            deal2.setStatus(DealStatus.WON);

            deals.add(deal1);
            deals.add(deal2);
        }

        public Deal getDealById(String dealId) {
            return deals.stream()
                .filter(deal -> deal.getId().equals(dealId))
                .findFirst()
                .orElse(null);
        }

        public List<Deal> getDealsBySalesRep(String salesRepId) {
            List<Deal> result = new ArrayList<>();
            for (Deal deal : deals) {
                if (deal.getSalesRepId().equals(salesRepId)) {
                    result.add(deal);
                }
            }
            return result;
        }

        public void updateDealStatus(Deal deal, DealStatus status) {
            deal.setStatus(status);
            if (status == DealStatus.WON) {
                deal.setCloseDate(LocalDate.now());
            }
        }
    }

    /**
     * Simple service for user management (part of the subsystem)
     */
    public static class UserService {
        private List<User> users;

        public UserService() {
            // Initialize with some sample users
            this.users = new ArrayList<>();

            User user1 = new User();
            user1.setId("user-1");
            user1.setFirstName("John");
            user1.setLastName("Doe");

            users.add(user1);
        }

        public User getUserById(String userId) {
            return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
        }
    }

    /**
     * Client code that demonstrates how to use the AdapterFacade
     */
    public static void main(String[] args) {
        System.out.println("===== Adapter + Facade Pattern Combination Example =====\n");

        // Create the facade
        PaymentFacade paymentFacade = new PaymentFacade();

        // Process payment for a deal
        System.out.println("Processing payment for deal-1:");
        paymentFacade.processPaymentForDeal("deal-1");
        System.out.println();

        // Generate payment report for a sales rep
        System.out.println("Generating payment report for user-1:");
        paymentFacade.generatePaymentReport("user-1");

        System.out.println("\nBenefits of combining Adapter and Facade patterns:");
        System.out.println("1. Simplified interface to complex subsystems (Facade)");
        System.out.println("2. Ability to work with incompatible interfaces (Adapter)");
        System.out.println("3. Decoupling clients from subsystem implementation details");
        System.out.println("4. Easier integration with third-party systems");
    }
}
