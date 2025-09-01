package com.chapman.edu.commissions.principles.solid.fixed.lsp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * This class implements the CommissionCalculator interface for senior sales representatives.
 * It follows the Liskov Substitution Principle by explicitly defining its preconditions
 * through the canCalculate method, rather than throwing exceptions in the calculateCommission method.
 */
public class SeniorSalesCommissionCalculator implements CommissionCalculator {
    
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10% base commission
    private static final BigDecimal BONUS_MULTIPLIER = new BigDecimal("1.2"); // 20% bonus
    private static final int MINIMUM_SALES_COUNT = 5;
    
    @Override
    public BigDecimal calculateCommission(List<BigDecimal> sales) {
        // We don't need to check the precondition here because clients should
        // call canCalculate first to verify that this calculator can be used
        
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal sale : sales) {
            total = total.add(sale);
        }
        
        // Senior sales reps get a 20% bonus on their commission
        return total.multiply(COMMISSION_RATE)
                .multiply(BONUS_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean canCalculate(List<BigDecimal> sales) {
        // Senior calculator requires at least 5 sales
        return sales != null && sales.size() >= MINIMUM_SALES_COUNT;
    }
}