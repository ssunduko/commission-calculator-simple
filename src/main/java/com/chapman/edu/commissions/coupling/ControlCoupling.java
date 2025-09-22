package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;

/**
 * Control Coupling Example
 * 
 * Control coupling occurs when one module passes a flag, switch, or other control information
 * to another module, influencing its internal logic and behavior.
 * 
 * In this example, the DealProcessor class receives control flags that determine
 * how it processes deals, creating control coupling.
 */
public class ControlCoupling {
    
    /**
     * Enum representing different processing modes
     */
    public enum ProcessingMode {
        STANDARD,
        EXPEDITED,
        BULK
    }
    
    /**
     * DealProcessor class that processes deals based on control flags
     */
    public static class DealProcessor {
        
        /**
         * Process a deal based on the provided processing mode (control flag)
         * @param deal The deal to process
         * @param mode The processing mode (control flag)
         * @param applyDiscount Whether to apply a discount (another control flag)
         */
        public void processDeal(Deal deal, ProcessingMode mode, boolean applyDiscount) {
            System.out.println("Processing deal: " + deal.getTitle());
            
            // Control coupling: The behavior of this method is controlled by the mode parameter
            switch (mode) {
                case STANDARD:
                    System.out.println("Using standard processing");
                    // Standard processing logic
                    break;
                    
                case EXPEDITED:
                    System.out.println("Using expedited processing");
                    // Expedited processing logic
                    break;
                    
                case BULK:
                    System.out.println("Using bulk processing");
                    // Bulk processing logic
                    break;
            }
            
            // Control coupling: The behavior is also controlled by the applyDiscount flag
            if (applyDiscount) {
                System.out.println("Applying discount to deal");
                // Apply discount logic
            } else {
                System.out.println("No discount applied");
            }
            
            // Complete the processing
            deal.setStatus(DealStatus.WON);
            System.out.println("Deal processed successfully");
        }
        
        /**
         * Another example of control coupling with a flag parameter
         * 
         * @param deal The deal to validate
         * @param strictValidation Whether to use strict validation rules (control flag)
         * @return True if the deal is valid, false otherwise
         */
        public boolean validateDeal(Deal deal, boolean strictValidation) {
            // Control coupling: The validation logic changes based on the strictValidation flag
            if (strictValidation) {
                System.out.println("Performing strict validation");
                // Strict validation logic
                return deal.getProducts().size() > 0 && 
                       deal.getSalesRepId() != null && 
                       deal.calculateTotalValue().compareTo(BigDecimal.ZERO) > 0;
            } else {
                System.out.println("Performing basic validation");
                // Basic validation logic
                return deal.getProducts().size() > 0;
            }
        }
    }
    
    public static void main(String[] args) {
        // Create a deal
        Deal deal = new Deal("Test Deal", new BigDecimal("1000.00"), "sales-rep-1");
        deal.addProduct(new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00")));
        
        // Create a deal processor
        DealProcessor processor = new DealProcessor();
        
        // Process the deal with different control flags
        processor.processDeal(deal, ProcessingMode.STANDARD, false);
        
        // Create another deal
        Deal deal2 = new Deal("Another Deal", new BigDecimal("500.00"), "sales-rep-2");
        deal2.addProduct(new DealProduct("prod2", "Product 2", 1, new BigDecimal("50.00")));
        
        // Process with different control flags
        processor.processDeal(deal2, ProcessingMode.EXPEDITED, true);
        
        // Validate deals with different control flags
        boolean isValid1 = processor.validateDeal(deal, true);
        boolean isValid2 = processor.validateDeal(deal2, false);
        
        System.out.println("Deal 1 strict validation: " + isValid1);
        System.out.println("Deal 2 basic validation: " + isValid2);
    }
}