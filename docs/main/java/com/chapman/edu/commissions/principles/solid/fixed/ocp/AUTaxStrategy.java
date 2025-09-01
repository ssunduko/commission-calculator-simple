package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class implements the TaxStrategy interface for Australia.
 * It follows the Open/Closed Principle by providing a specific implementation
 * without modifying the interface or other implementations.
 */
public class AUTaxStrategy implements TaxStrategy {
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% GST
    
    @Override
    public boolean appliesTo(String countryCode) {
        return "AU".equals(countryCode);
    }
    
    @Override
    public BigDecimal calculateTax(DealProduct product) {
        BigDecimal productPrice = product.getPrice();
        int quantity = product.getQuantity();
        BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        
        return productTotal.multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}