package com.chapman.edu.commissions.encapsulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Interface Encapsulation Example
 * 
 * This class demonstrates interface encapsulation by abstracting implementation behind contracts.
 * Interface encapsulation allows:
 * 1. Hiding implementation details behind interfaces
 * 2. Providing multiple implementations of the same interface
 * 3. Enabling loose coupling between components
 * 4. Supporting the Dependency Inversion Principle
 */
public class InterfaceEncapsulationExample {

    /**
     * The PaymentProcessor interface defines a contract for processing payments.
     * It encapsulates the implementation details of how payments are processed.
     */
    public interface PaymentProcessor {
        /**
         * Process a payment.
         * 
         * @param amount the payment amount
         * @param currency the payment currency
         * @param source the payment source
         * @param destination the payment destination
         * @return a PaymentResult containing the result of the payment processing
         */
        PaymentResult processPayment(BigDecimal amount, String currency, String source, String destination);
        
        /**
         * Check if the processor supports a specific currency.
         * 
         * @param currency the currency to check
         * @return true if the currency is supported, false otherwise
         */
        boolean supportsCurrency(String currency);
        
        /**
         * Get the name of the payment processor.
         * 
         * @return the name of the payment processor
         */
        String getName();
    }
    
    /**
     * The PaymentResult class represents the result of a payment processing operation.
     */
    public static class PaymentResult {
        private final String transactionId;
        private final boolean success;
        private final String message;
        private final LocalDate timestamp;
        
        public PaymentResult(String transactionId, boolean success, String message) {
            this.transactionId = transactionId;
            this.success = success;
            this.message = message;
            this.timestamp = LocalDate.now();
        }
        
        public String getTransactionId() {
            return transactionId;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public LocalDate getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return "Payment " + (success ? "succeeded" : "failed") + 
                   ": " + message + 
                   " (Transaction ID: " + transactionId + 
                   ", Timestamp: " + timestamp + ")";
        }
    }
    
    /**
     * CreditCardProcessor is an implementation of the PaymentProcessor interface.
     * The implementation details are hidden from clients who use the interface.
     */
    public static class CreditCardProcessor implements PaymentProcessor {
        private static final List<String> SUPPORTED_CURRENCIES = List.of("USD", "EUR", "GBP");
        
        @Override
        public PaymentResult processPayment(BigDecimal amount, String currency, String source, String destination) {
            // Implementation details are hidden from clients
            String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Validate inputs
            if (!supportsCurrency(currency)) {
                return new PaymentResult(transactionId, false, "Currency not supported: " + currency);
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new PaymentResult(transactionId, false, "Invalid amount: " + amount);
            }
            
            if (source == null || source.trim().isEmpty()) {
                return new PaymentResult(transactionId, false, "Invalid source");
            }
            
            // Process the payment (simplified for example)
            // In a real system, this would connect to a payment gateway
            
            // Apply processing fee (implementation detail)
            BigDecimal fee = amount.multiply(new BigDecimal("0.029")).add(new BigDecimal("0.30"));
            BigDecimal totalCharge = amount.add(fee).setScale(2, RoundingMode.HALF_UP);
            
            return new PaymentResult(transactionId, true, 
                                    "Processed credit card payment of " + amount + " " + currency + 
                                    " (Total charge: " + totalCharge + " " + currency + ")");
        }
        
        @Override
        public boolean supportsCurrency(String currency) {
            return SUPPORTED_CURRENCIES.contains(currency);
        }
        
        @Override
        public String getName() {
            return "Credit Card Processor";
        }
        
        // Private helper methods (implementation details hidden from clients)
        
        private boolean validateCreditCard(String cardNumber) {
            // Implementation details hidden from clients
            return cardNumber != null && cardNumber.length() >= 13 && cardNumber.length() <= 19;
        }
        
        private BigDecimal calculateFee(BigDecimal amount) {
            // Implementation details hidden from clients
            return amount.multiply(new BigDecimal("0.029")).add(new BigDecimal("0.30"));
        }
    }
    
    /**
     * PayPalProcessor is another implementation of the PaymentProcessor interface.
     * Clients can use this interchangeably with CreditCardProcessor.
     */
    public static class PayPalProcessor implements PaymentProcessor {
        private static final List<String> SUPPORTED_CURRENCIES = List.of("USD", "EUR", "GBP", "CAD", "AUD");
        
        @Override
        public PaymentResult processPayment(BigDecimal amount, String currency, String source, String destination) {
            // Implementation details are hidden from clients
            String transactionId = "PP-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Validate inputs
            if (!supportsCurrency(currency)) {
                return new PaymentResult(transactionId, false, "Currency not supported: " + currency);
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new PaymentResult(transactionId, false, "Invalid amount: " + amount);
            }
            
            if (source == null || source.trim().isEmpty()) {
                return new PaymentResult(transactionId, false, "Invalid source");
            }
            
            // Process the payment (simplified for example)
            // In a real system, this would connect to PayPal's API
            
            // Apply processing fee (implementation detail)
            BigDecimal fee = calculateFee(amount, currency);
            BigDecimal totalCharge = amount.add(fee).setScale(2, RoundingMode.HALF_UP);
            
            return new PaymentResult(transactionId, true, 
                                    "Processed PayPal payment of " + amount + " " + currency + 
                                    " (Total charge: " + totalCharge + " " + currency + ")");
        }
        
        @Override
        public boolean supportsCurrency(String currency) {
            return SUPPORTED_CURRENCIES.contains(currency);
        }
        
        @Override
        public String getName() {
            return "PayPal Processor";
        }
        
        // Private helper methods (implementation details hidden from clients)
        
        private BigDecimal calculateFee(BigDecimal amount, String currency) {
            // Implementation details hidden from clients
            // Different fee structure than credit card
            if ("USD".equals(currency)) {
                return amount.multiply(new BigDecimal("0.039")).add(new BigDecimal("0.49"));
            } else {
                return amount.multiply(new BigDecimal("0.049")).add(new BigDecimal("0.59"));
            }
        }
    }
    
    /**
     * PaymentService uses the PaymentProcessor interface without knowing the implementation details.
     * This demonstrates the power of interface encapsulation.
     */
    public static class PaymentService {
        private final List<PaymentProcessor> processors;
        
        public PaymentService() {
            this.processors = new ArrayList<>();
        }
        
        public void addProcessor(PaymentProcessor processor) {
            processors.add(processor);
        }
        
        public PaymentResult processPayment(BigDecimal amount, String currency, String source, String destination) {
            // Find a processor that supports the currency
            for (PaymentProcessor processor : processors) {
                if (processor.supportsCurrency(currency)) {
                    System.out.println("Using " + processor.getName() + " to process payment");
                    return processor.processPayment(amount, currency, source, destination);
                }
            }
            
            return new PaymentResult("NONE", false, "No payment processor available for currency: " + currency);
        }
    }
    
    /**
     * Main method to demonstrate interface encapsulation
     */
    public static void main(String[] args) {
        // Create payment processors
        PaymentProcessor creditCardProcessor = new CreditCardProcessor();
        PaymentProcessor payPalProcessor = new PayPalProcessor();
        
        // Create payment service
        PaymentService paymentService = new PaymentService();
        paymentService.addProcessor(creditCardProcessor);
        paymentService.addProcessor(payPalProcessor);
        
        // Process payments
        System.out.println("Processing USD payment...");
        PaymentResult result1 = paymentService.processPayment(
            new BigDecimal("100.00"), "USD", "4111111111111111", "Merchant A");
        System.out.println(result1);
        
        System.out.println("\nProcessing EUR payment...");
        PaymentResult result2 = paymentService.processPayment(
            new BigDecimal("50.00"), "EUR", "paypal@example.com", "Merchant B");
        System.out.println(result2);
        
        System.out.println("\nProcessing CAD payment...");
        PaymentResult result3 = paymentService.processPayment(
            new BigDecimal("75.00"), "CAD", "paypal@example.com", "Merchant C");
        System.out.println(result3);
        
        System.out.println("\nProcessing JPY payment...");
        PaymentResult result4 = paymentService.processPayment(
            new BigDecimal("5000.00"), "JPY", "4111111111111111", "Merchant D");
        System.out.println(result4);
        
        // Note that the client code (PaymentService) doesn't know or care about the
        // implementation details of the payment processors. It only depends on the
        // PaymentProcessor interface, not on concrete implementations.
        
        // This allows for:
        // 1. Easy substitution of different implementations
        // 2. Adding new payment processors without changing client code
        // 3. Testing with mock implementations
        // 4. Loose coupling between components
    }
}