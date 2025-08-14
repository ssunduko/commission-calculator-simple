package com.chapman.edu.commissions.principles.oop.polymorphism;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the CommissionCalculator interface for tiered commissions.
 * It demonstrates polymorphism by:
 * 1. Implementing the CommissionCalculator interface
 * 2. Providing a specific implementation for calculating tiered commissions
 */
public class TieredCommissionCalculator implements CommissionCalculator {
    
    private List<Tier> tiers;
    private String name;
    
    /**
     * Constructor with name
     */
    public TieredCommissionCalculator(String name) {
        this.name = name;
        this.tiers = new ArrayList<>();
    }
    
    /**
     * Add a tier to the calculator
     * 
     * @param minValue The minimum deal value for this tier
     * @param maxValue The maximum deal value for this tier (null for no maximum)
     * @param rate The commission rate for this tier
     */
    public void addTier(BigDecimal minValue, BigDecimal maxValue, BigDecimal rate) {
        tiers.add(new Tier(minValue, maxValue, rate));
    }
    
    /**
     * Calculate the commission amount for a given deal value.
     * For tiered commission, the rate depends on which tier the deal value falls into.
     * 
     * @param dealValue The total value of the deal
     * @return The calculated commission amount
     */
    @Override
    public BigDecimal calculateCommission(BigDecimal dealValue) {
        for (Tier tier : tiers) {
            if (tier.isInTier(dealValue)) {
                return dealValue.multiply(tier.getRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            }
        }
        
        // If no tier matches, return zero
        return BigDecimal.ZERO;
    }
    
    /**
     * Get the commission rate as a percentage.
     * For tiered commission, this returns the highest tier rate.
     * 
     * @return The highest tier commission rate
     */
    @Override
    public BigDecimal getCommissionRate() {
        BigDecimal highestRate = BigDecimal.ZERO;
        
        for (Tier tier : tiers) {
            if (tier.getRate().compareTo(highestRate) > 0) {
                highestRate = tier.getRate();
            }
        }
        
        return highestRate;
    }
    
    /**
     * Get a description of the commission calculation method.
     * 
     * @return A string describing how the commission is calculated
     */
    @Override
    public String getDescription() {
        StringBuilder description = new StringBuilder("Tiered commission with the following tiers:\n");
        
        for (int i = 0; i < tiers.size(); i++) {
            Tier tier = tiers.get(i);
            description.append("Tier ").append(i + 1).append(": ");
            
            if (tier.getMinValue().compareTo(BigDecimal.ZERO) == 0) {
                description.append("Up to ");
                description.append(tier.getMaxValue());
            } else if (tier.getMaxValue() == null) {
                description.append(tier.getMinValue());
                description.append(" and above");
            } else {
                description.append(tier.getMinValue());
                description.append(" to ");
                description.append(tier.getMaxValue());
            }
            
            description.append(" = ").append(tier.getRate()).append("%\n");
        }
        
        return description.toString();
    }
    
    /**
     * Check if a deal qualifies for this commission calculation.
     * For tiered commission, any deal that falls into a tier qualifies.
     * 
     * @param dealValue The total value of the deal
     * @param salesRepId The ID of the sales representative (not used in this implementation)
     * @return true if the deal qualifies, false otherwise
     */
    @Override
    public boolean qualifiesForCommission(BigDecimal dealValue, String salesRepId) {
        for (Tier tier : tiers) {
            if (tier.isInTier(dealValue)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the name of the commission calculator.
     * 
     * @return The name of the commission calculator
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Get the tier for a given deal value.
     * 
     * @param dealValue The deal value
     * @return The tier that the deal value falls into, or null if none
     */
    public Tier getTierForDealValue(BigDecimal dealValue) {
        for (Tier tier : tiers) {
            if (tier.isInTier(dealValue)) {
                return tier;
            }
        }
        
        return null;
    }
    
    /**
     * Inner class representing a commission tier
     */
    public static class Tier {
        private BigDecimal minValue;
        private BigDecimal maxValue; // null means no maximum
        private BigDecimal rate;
        
        public Tier(BigDecimal minValue, BigDecimal maxValue, BigDecimal rate) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.rate = rate;
        }
        
        public boolean isInTier(BigDecimal value) {
            boolean aboveMin = value.compareTo(minValue) >= 0;
            boolean belowMax = maxValue == null || value.compareTo(maxValue) <= 0;
            
            return aboveMin && belowMax;
        }
        
        public BigDecimal getMinValue() {
            return minValue;
        }
        
        public BigDecimal getMaxValue() {
            return maxValue;
        }
        
        public BigDecimal getRate() {
            return rate;
        }
    }
}