package com.chapman.edu.commissions.encapsulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Method Encapsulation Example
 * 
 * This class demonstrates method encapsulation by hiding implementation details behind public methods.
 * Method encapsulation allows:
 * 1. Hiding complex implementation details from the client
 * 2. Changing the internal implementation without affecting client code
 * 3. Providing a simple, stable interface for clients to use
 * 4. Improving code maintainability and reducing coupling
 */
public class MethodEncapsulationExample {
    /**
     * The CommissionCalculator class demonstrates method encapsulation by hiding
     * the complex calculation logic behind simple public methods.
     */
    public static class CommissionCalculator {
        // Constants used in calculations
        private static final BigDecimal HUNDRED = new BigDecimal("100");
        private static final int SCALE = 2;
        /**
         * Calculates commission based on sale amount and rate.
         * The implementation details of how the commission is calculated
         * are hidden from the client.
         * 
         * @param saleAmount the total sale amount
         * @param commissionRate the commission rate as a percentage
         * @return the calculated commission amount
         */
        public BigDecimal calculateCommission(BigDecimal saleAmount, BigDecimal commissionRate) {
            // Validate inputs
            validateInputs(saleAmount, commissionRate);
            // Calculate base commission
            BigDecimal commission = calculateBaseCommission(saleAmount, commissionRate);
            // Apply any adjustments
            commission = applyAdjustments(commission);
            // Round to standard scale
            return roundToStandardScale(commission);
        }
        
        /**
         * Calculates tiered commission based on sale amount and tier thresholds.
         * The implementation details are hidden from the client.
         * 
         * @param saleAmount the total sale amount
         * @param tiers the commission tiers
         * @return the calculated commission amount
         */
        public BigDecimal calculateTieredCommission(BigDecimal saleAmount, List<CommissionTier> tiers) {
            // Validate inputs
            if (saleAmount == null || saleAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Sale amount must be non-negative");
            }
            
            if (tiers == null || tiers.isEmpty()) {
                throw new IllegalArgumentException("Tiers cannot be null or empty");
            }
            
            // Find applicable tier and calculate commission
            BigDecimal commission = findAndCalculateTierCommission(saleAmount, tiers);
            
            // Apply any adjustments
            commission = applyAdjustments(commission);
            
            // Round to standard scale
            return roundToStandardScale(commission);
        }
        
        // Private helper methods that encapsulate implementation details
        
        /**
         * Validates the input parameters for commission calculation.
         * This is an implementation detail hidden from clients.
         */
        private void validateInputs(BigDecimal saleAmount, BigDecimal commissionRate) {
            if (saleAmount == null || saleAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Sale amount must be non-negative");
            }
            
            if (commissionRate == null || commissionRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Commission rate must be non-negative");
            }
        }
        
        /**
         * Calculates the base commission amount.
         * This is an implementation detail hidden from clients.
         */
        private BigDecimal calculateBaseCommission(BigDecimal saleAmount, BigDecimal commissionRate) {
            // Convert percentage to decimal (e.g., 5% to 0.05)
            BigDecimal rateAsDecimal = commissionRate.divide(HUNDRED, 10, RoundingMode.HALF_UP);
            
            // Calculate commission
            return saleAmount.multiply(rateAsDecimal);
        }
        
        /**
         * Applies any adjustments to the commission amount.
         * This is an implementation detail hidden from clients.
         */
        private BigDecimal applyAdjustments(BigDecimal commission) {
            // In a real system, this might apply various business rules
            // For this example, we'll just return the commission as is
            return commission;
        }
        
        /**
         * Finds the applicable tier and calculates the commission.
         * This is an implementation detail hidden from clients.
         */
        private BigDecimal findAndCalculateTierCommission(BigDecimal saleAmount, List<CommissionTier> tiers) {
            // Sort tiers by threshold (ascending)
            tiers.sort((t1, t2) -> t1.getThreshold().compareTo(t2.getThreshold()));
            
            // Find applicable tier
            CommissionTier applicableTier = null;
            for (CommissionTier tier : tiers) {
                if (saleAmount.compareTo(tier.getThreshold()) >= 0) {
                    applicableTier = tier;
                } else {
                    break;
                }
            }
            
            // If no tier applies, return zero
            if (applicableTier == null) {
                return BigDecimal.ZERO;
            }
            
            // Calculate commission using the applicable tier's rate
            return calculateBaseCommission(saleAmount, applicableTier.getRate());
        }
        
        /**
         * Rounds the commission amount to the standard scale.
         * This is an implementation detail hidden from clients.
         */
        private BigDecimal roundToStandardScale(BigDecimal amount) {
            return amount.setScale(SCALE, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * The CommissionTier class represents a tier in a tiered commission structure.
     */
    public static class CommissionTier {
        private BigDecimal threshold;
        private BigDecimal rate;
        
        public CommissionTier(BigDecimal threshold, BigDecimal rate) {
            this.threshold = threshold;
            this.rate = rate;
        }
        
        public BigDecimal getThreshold() {
            return threshold;
        }
        
        public BigDecimal getRate() {
            return rate;
        }
    }
    
    /**
     * Main method to demonstrate method encapsulation
     */
    public static void main(String[] args) {
        // Create a commission calculator
        CommissionCalculator calculator = new CommissionCalculator();
        
        // Calculate flat commission
        BigDecimal saleAmount = new BigDecimal("10000.00");
        BigDecimal commissionRate = new BigDecimal("5.00"); // 5%
        
        BigDecimal commission = calculator.calculateCommission(saleAmount, commissionRate);
        System.out.println("Sale Amount: $" + saleAmount);
        System.out.println("Commission Rate: " + commissionRate + "%");
        System.out.println("Flat Commission: $" + commission);
        
        // Calculate tiered commission
        List<CommissionTier> tiers = new ArrayList<>();
        tiers.add(new CommissionTier(new BigDecimal("0.00"), new BigDecimal("2.00"))); // 0-4999: 2%
        tiers.add(new CommissionTier(new BigDecimal("5000.00"), new BigDecimal("3.00"))); // 5000-9999: 3%
        tiers.add(new CommissionTier(new BigDecimal("10000.00"), new BigDecimal("5.00"))); // 10000+: 5%
        
        BigDecimal tieredCommission = calculator.calculateTieredCommission(saleAmount, tiers);
        System.out.println("\nTiered Commission: $" + tieredCommission);
        
        // The client code doesn't need to know how the commission is calculated
        // It only needs to call the public methods with the required parameters
        // The implementation details are encapsulated within the CommissionCalculator class
    }
}