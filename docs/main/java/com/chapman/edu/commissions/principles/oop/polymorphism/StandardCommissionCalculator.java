package com.chapman.edu.commissions.principles.oop.polymorphism;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class implements the CommissionCalculator interface for standard commissions.
 * It demonstrates polymorphism by:
 * 1. Implementing the CommissionCalculator interface
 * 2. Providing a specific implementation for calculating standard commissions
 */
public class StandardCommissionCalculator implements CommissionCalculator {
    
    private BigDecimal commissionRate;
    private BigDecimal minimumDealValue;
    
    /**
     * Constructor with commission rate
     */
    public StandardCommissionCalculator(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
        this.minimumDealValue = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with commission rate and minimum deal value
     */
    public StandardCommissionCalculator(BigDecimal commissionRate, BigDecimal minimumDealValue) {
        this.commissionRate = commissionRate;
        this.minimumDealValue = minimumDealValue;
    }
    
    /**
     * Calculate the commission amount for a given deal value.
     * For standard commission, this is simply the deal value multiplied by the commission rate.
     * 
     * @param dealValue The total value of the deal
     * @return The calculated commission amount
     */
    @Override
    public BigDecimal calculateCommission(BigDecimal dealValue) {
        if (dealValue.compareTo(minimumDealValue) < 0) {
            return BigDecimal.ZERO;
        }
        
        return dealValue.multiply(commissionRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
    }
    
    /**
     * Get the commission rate as a percentage.
     * 
     * @return The commission rate as a percentage
     */
    @Override
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    /**
     * Get a description of the commission calculation method.
     * 
     * @return A string describing how the commission is calculated
     */
    @Override
    public String getDescription() {
        return "Standard commission calculated as " + commissionRate + "% of deal value" +
               (minimumDealValue.compareTo(BigDecimal.ZERO) > 0 ? 
                " (minimum deal value: " + minimumDealValue + ")" : "");
    }
    
    /**
     * Check if a deal qualifies for this commission calculation.
     * For standard commission, any deal above the minimum value qualifies.
     * 
     * @param dealValue The total value of the deal
     * @param salesRepId The ID of the sales representative (not used in this implementation)
     * @return true if the deal qualifies, false otherwise
     */
    @Override
    public boolean qualifiesForCommission(BigDecimal dealValue, String salesRepId) {
        return dealValue.compareTo(minimumDealValue) >= 0;
    }
    
    /**
     * Get the name of the commission calculator.
     * 
     * @return The name of the commission calculator
     */
    @Override
    public String getName() {
        return "Standard Commission Calculator";
    }
    
    /**
     * Set the commission rate.
     * 
     * @param commissionRate The new commission rate
     */
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    /**
     * Get the minimum deal value.
     * 
     * @return The minimum deal value
     */
    public BigDecimal getMinimumDealValue() {
        return minimumDealValue;
    }
    
    /**
     * Set the minimum deal value.
     * 
     * @param minimumDealValue The new minimum deal value
     */
    public void setMinimumDealValue(BigDecimal minimumDealValue) {
        this.minimumDealValue = minimumDealValue;
    }
}