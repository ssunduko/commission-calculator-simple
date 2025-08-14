package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * This interface defines a strategy for calculating commissions.
 * It follows the Open/Closed Principle by allowing new commission strategies to be added
 * without modifying existing code.
 */
public interface CommissionStrategy {
    
    /**
     * Determines if this strategy applies to the given product.
     * 
     * @param product The product to check
     * @return True if this strategy applies to the product, false otherwise
     */
    boolean appliesTo(DealProduct product);
    
    /**
     * Calculates the commission for a product.
     * 
     * @param product The product to calculate the commission for
     * @return The commission amount
     */
    BigDecimal calculateCommission(DealProduct product);
}