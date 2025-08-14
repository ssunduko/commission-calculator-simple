package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class implements the DiscountStrategy interface for software products.
 * It follows the Open/Closed Principle by providing a specific implementation
 * without modifying the interface or other implementations.
 */
public class SoftwareDiscountStrategy implements DiscountStrategy {
    
    private static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10"); // 10% discount
    
    @Override
    public boolean appliesTo(DealProduct product) {
        return "SOFTWARE".equals(product.getProductId());
    }
    
    @Override
    public BigDecimal calculateDiscount(DealProduct product) {
        BigDecimal productPrice = product.getPrice();
        int quantity = product.getQuantity();
        
        return productPrice.multiply(DISCOUNT_RATE)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}