package com.chapman.edu.commissions.principles.solid.fixed.lsp;

import java.math.BigDecimal;
import java.util.List;

/**
 * This interface defines the contract for commission calculators.
 * It helps ensure the Liskov Substitution Principle by establishing
 * a clear contract that all implementations must follow.
 */
public interface CommissionCalculator {
    
    /**
     * Calculates commission for a list of sales.
     * 
     * @param sales The list of sales amounts
     * @return The calculated commission amount
     */
    BigDecimal calculateCommission(List<BigDecimal> sales);
    
    /**
     * Checks if the calculator can process the given sales data.
     * This method allows implementations to define their preconditions explicitly,
     * which helps maintain LSP by making preconditions part of the contract.
     * 
     * @param sales The list of sales amounts
     * @return True if the calculator can process the sales, false otherwise
     */
    boolean canCalculate(List<BigDecimal> sales);
}