package com.chapman.edu.commissions.principles.solid.fixed.lsp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * This class implements the CommissionCalculator interface for regular sales representatives.
 * It follows the Liskov Substitution Principle by adhering to the contract defined in the interface.
 */
public class BaseCommissionCalculator implements CommissionCalculator {
    
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10% commission
    
    @Override
    public BigDecimal calculateCommission(List<BigDecimal> sales) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal sale : sales) {
            total = total.add(sale);
        }
        return total.multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean canCalculate(List<BigDecimal> sales) {
        // Base calculator can handle any sales data
        return sales != null && !sales.isEmpty();
    }
}