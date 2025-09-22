package com.chapman.edu.commissions.concerns;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * This class demonstrates the use of access modifiers in Java.
 * 
 * Access modifiers control the visibility and accessibility of classes, methods, and fields.
 * Java provides four access levels:
 * 1. public - accessible from any class
 * 2. protected - accessible within the same package and subclasses
 * 3. default (no modifier) - accessible only within the same package
 * 4. private - accessible only within the same class
 * 
 * This example shows how access modifiers can be used to implement encapsulation
 * and information hiding in a commission calculator system.
 */
public class AccessModifiersExample {
    
    /**
     * Main method to demonstrate access modifiers
     */
    public static void main(String[] args) {
        // Create a commission calculator
        CommissionCalculator calculator = new CommissionCalculator();
        
        // Create a deal with products
        Deal deal = new Deal("Sample Deal", BigDecimal.valueOf(10000), "REP001");
        deal.addProduct(new DealProduct("PROD001", "Product A", 2, BigDecimal.valueOf(3000)));
        deal.addProduct(new DealProduct("PROD002", "Product B", 1, BigDecimal.valueOf(4000)));
        
        // Calculate commission
        BigDecimal commission = calculator.calculateCommission(deal);
        System.out.println("Commission for deal: " + commission);
        
        // Try to access different levels of visibility
        System.out.println("\nAccessing different levels of visibility:");
        
        // Public method - accessible from anywhere
        System.out.println("Public baseRate: " + calculator.getBaseRate());
        
        // The following would cause compilation errors if uncommented:
        
        // Protected method - only accessible within the same package and subclasses
        // System.out.println("Protected tieredRate: " + calculator.calculateTieredRate(deal));
        
        // Default (package-private) method - only accessible within the same package
        // System.out.println("Default bonusRate: " + calculator.calculateBonusRate(deal));
        
        // Private method - only accessible within the same class
        // System.out.println("Private validateDeal result: " + calculator.validateDeal(deal));
        
        // Create a specialized calculator that extends CommissionCalculator
        EnterpriseCommissionCalculator enterpriseCalculator = new EnterpriseCommissionCalculator();
        BigDecimal enterpriseCommission = enterpriseCalculator.calculateCommission(deal);
        System.out.println("\nEnterprise commission for deal: " + enterpriseCommission);
        
        // Demonstrate access to protected members from a subclass
        enterpriseCalculator.demonstrateProtectedAccess(deal);
    }
    
    /**
     * CommissionCalculator class demonstrates the use of different access modifiers
     */
    public static class CommissionCalculator {
        // Public field - accessible from any class
        public final BigDecimal BASE_COMMISSION_RATE = BigDecimal.valueOf(0.05);
        
        // Protected field - accessible within the same package and subclasses
        protected BigDecimal tieredRateThreshold = BigDecimal.valueOf(5000);
        
        // Default (package-private) field - accessible only within the same package
        BigDecimal bonusRate = BigDecimal.valueOf(0.02);
        
        // Private field - accessible only within the same class
        private boolean enableValidation = true;
        
        /**
         * Public method to calculate commission
         * This is part of the public API and can be accessed from anywhere
         * 
         * @param deal the deal to calculate commission for
         * @return the calculated commission
         */
        public BigDecimal calculateCommission(Deal deal) {
            // Validate the deal (private method)
            if (enableValidation && !validateDeal(deal)) {
                return BigDecimal.ZERO;
            }
            
            // Calculate base commission
            BigDecimal baseCommission = deal.getValue().multiply(getBaseRate());
            
            // Add tiered commission if applicable (protected method)
            baseCommission = baseCommission.add(calculateTieredRate(deal));
            
            // Add bonus if applicable (default/package-private method)
            baseCommission = baseCommission.add(calculateBonusRate(deal));
            
            return baseCommission;
        }
        
        /**
         * Public method to get the base commission rate
         * This is part of the public API and can be accessed from anywhere
         * 
         * @return the base commission rate
         */
        public BigDecimal getBaseRate() {
            return BASE_COMMISSION_RATE;
        }
        
        /**
         * Protected method to calculate tiered commission rate
         * This can be accessed by subclasses and classes in the same package
         * 
         * @param deal the deal to calculate tiered rate for
         * @return the tiered commission amount
         */
        protected BigDecimal calculateTieredRate(Deal deal) {
            if (deal.getValue().compareTo(tieredRateThreshold) > 0) {
                BigDecimal excess = deal.getValue().subtract(tieredRateThreshold);
                return excess.multiply(BigDecimal.valueOf(0.02));
            }
            return BigDecimal.ZERO;
        }
        
        /**
         * Default (package-private) method to calculate bonus rate
         * This can only be accessed by classes in the same package
         * 
         * @param deal the deal to calculate bonus for
         * @return the bonus amount
         */
        BigDecimal calculateBonusRate(Deal deal) {
            if (deal.getProducts().size() > 1) {
                return deal.getValue().multiply(bonusRate);
            }
            return BigDecimal.ZERO;
        }
        
        /**
         * Private method to validate a deal
         * This can only be accessed within this class
         * 
         * @param deal the deal to validate
         * @return true if the deal is valid, false otherwise
         */
        private boolean validateDeal(Deal deal) {
            return deal != null && deal.getValue().compareTo(BigDecimal.ZERO) > 0;
        }
    }
    
    /**
     * EnterpriseCommissionCalculator extends CommissionCalculator to demonstrate
     * how protected members can be accessed by subclasses.
     */
    public static class EnterpriseCommissionCalculator extends CommissionCalculator {
        /**
         * Override the public calculateCommission method
         * 
         * @param deal the deal to calculate commission for
         * @return the calculated commission with enterprise adjustments
         */
        @Override
        public BigDecimal calculateCommission(Deal deal) {
            // Call the parent class method first
            BigDecimal commission = super.calculateCommission(deal);
            
            // Apply enterprise discount
            return commission.multiply(BigDecimal.valueOf(0.9)); // 10% discount
        }
        
        /**
         * Demonstrate access to protected members from the parent class
         * 
         * @param deal the deal to use for demonstration
         */
        public void demonstrateProtectedAccess(Deal deal) {
            System.out.println("From subclass - can access protected tieredRateThreshold: " + tieredRateThreshold);
            System.out.println("From subclass - can call protected calculateTieredRate: " + calculateTieredRate(deal));
            
            // Can modify protected fields inherited from parent
            tieredRateThreshold = BigDecimal.valueOf(7500);
            System.out.println("From subclass - modified tieredRateThreshold: " + tieredRateThreshold);
            
            // The following would cause compilation errors if uncommented:
            
            // Cannot access private members of the parent class
            // System.out.println(enableValidation);
            // System.out.println(validateDeal(deal));
        }
    }
}