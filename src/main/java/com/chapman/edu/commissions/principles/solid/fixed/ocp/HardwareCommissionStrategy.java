package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class implements the CommissionStrategy interface for hardware products.
 * It follows the Open/Closed Principle by providing a specific implementation
 * without modifying the interface or other implementations.
 */
public class HardwareCommissionStrategy implements CommissionStrategy {
    
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.05"); // 5% commission
    
    @Override
    public boolean appliesTo(DealProduct product) {
        return "HARDWARE".equals(product.getProductId());
    }
    
    @Override
    public BigDecimal calculateCommission(DealProduct product) {
        BigDecimal productPrice = product.getPrice();
        int quantity = product.getQuantity();
        BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        
        return productTotal.multiply(COMMISSION_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}