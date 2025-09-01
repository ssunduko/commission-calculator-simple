package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * This interface defines a strategy for calculating taxes.
 * It follows the Open/Closed Principle by allowing new tax strategies to be added
 * without modifying existing code.
 */
public interface TaxStrategy {
    
    /**
     * Determines if this strategy applies to the given country code.
     * 
     * @param countryCode The country code to check
     * @return True if this strategy applies to the country, false otherwise
     */
    boolean appliesTo(String countryCode);
    
    /**
     * Calculates the tax for a product in a specific country.
     * 
     * @param product The product to calculate the tax for
     * @return The tax amount
     */
    BigDecimal calculateTax(DealProduct product);
}