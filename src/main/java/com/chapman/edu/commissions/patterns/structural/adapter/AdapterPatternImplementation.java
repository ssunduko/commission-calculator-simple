package com.chapman.edu.commissions.patterns.structural.adapter;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class demonstrates a concrete implementation of the Adapter Pattern.
 * 
 * In this example, we're adapting our Deal model to work with a third-party
 * payment processing system that expects a different interface.
 * 
 * The Adapter Pattern allows us to reuse our existing Deal class without modifying it,
 * while still being able to integrate with the third-party system.
 */
public class AdapterPatternImplementation {

    /**
     * Target Interface
     * This is the interface that the third-party payment system expects.
     */
    public interface PaymentTransaction {
        String getTransactionId();
        String getCustomerId();
        double getAmount();
        String getCurrency();
        String getTransactionDate();
        List<PaymentItem> getItems();
        String getStatus();
    }

    /**
     * Another interface required by the Target
     */
    public interface PaymentItem {
        String getItemId();
        String getDescription();
        int getQuantity();
        double getUnitPrice();
        double getTotalPrice();
    }

    /**
     * Implementation of the PaymentItem interface
     */
    public static class PaymentItemImpl implements PaymentItem {
        private String itemId;
        private String description;
        private int quantity;
        private double unitPrice;

        public PaymentItemImpl(String itemId, String description, int quantity, double unitPrice) {
            this.itemId = itemId;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        @Override
        public String getItemId() {
            return itemId;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public int getQuantity() {
            return quantity;
        }

        @Override
        public double getUnitPrice() {
            return unitPrice;
        }

        @Override
        public double getTotalPrice() {
            return unitPrice * quantity;
        }
    }

    /**
     * Adapter
     * This class adapts our Deal model to the PaymentTransaction interface.
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
     * Third-party payment processor (the client)
     * This class expects to work with the PaymentTransaction interface.
     */
    public static class PaymentProcessor {
        public void processPayment(PaymentTransaction transaction) {
            System.out.println("Processing payment transaction: " + transaction.getTransactionId());
            System.out.println("Customer: " + transaction.getCustomerId());
            System.out.println("Amount: " + transaction.getAmount() + " " + transaction.getCurrency());
            System.out.println("Date: " + transaction.getTransactionDate());
            System.out.println("Status: " + transaction.getStatus());
            System.out.println("Items:");

            for (PaymentItem item : transaction.getItems()) {
                System.out.println("  - " + item.getDescription() + 
                                   " (Qty: " + item.getQuantity() + 
                                   ", Unit Price: " + item.getUnitPrice() + 
                                   ", Total: " + item.getTotalPrice() + ")");
            }

            System.out.println("Payment processed successfully!");
        }
    }
}
