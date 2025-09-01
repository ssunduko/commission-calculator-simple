package com.chapman.edu.commissions.principles.oop.polymorphism;

import java.math.BigDecimal;

/**
 * This interface defines the contract for different commission calculator implementations.
 * It demonstrates polymorphism by:
 * 1. Declaring methods that will be implemented differently by various classes
 * 2. Providing a common type for all commission calculators
 */
public interface CommissionCalculator {
    
    /**
     * Calculate the commission amount for a given deal value.
     * 
     * @param dealValue The total value of the deal
     * @return The calculated commission amount
     */
    BigDecimal calculateCommission(BigDecimal dealValue);
    
    /**
     * Get the commission rate as a percentage.
     * 
     * @return The commission rate as a percentage
     */
    BigDecimal getCommissionRate();
    
    /**
     * Get a description of the commission calculation method.
     * 
     * @return A string describing how the commission is calculated
     */
    String getDescription();
    
    /**
     * Check if a deal qualifies for this commission calculation.
     * 
     * @param dealValue The total value of the deal
     * @param salesRepId The ID of the sales representative
     * @return true if the deal qualifies, false otherwise
     */
    boolean qualifiesForCommission(BigDecimal dealValue, String salesRepId);
    
    /**
     * Get the name of the commission calculator.
     * 
     * @return The name of the commission calculator
     */
    String getName();
}