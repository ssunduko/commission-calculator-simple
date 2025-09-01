package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * This interface defines a strategy for calculating discounts.
 * It follows the Open/Closed Principle by allowing new discount strategies to be added
 * without modifying existing code.
 */
public interface DiscountStrategy {
    
    /**
     * Determines if this strategy applies to the given product.
     * 
     * @param product The product to check
     * @return True if this strategy applies to the product, false otherwise
     */
    boolean appliesTo(DealProduct product);
    
    /**
     * Calculates the discount for a product.
     * 
     * @param product The product to calculate the discount for
     * @return The discount amount
     */
    BigDecimal calculateDiscount(DealProduct product);
}