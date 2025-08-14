package com.chapman.edu.commissions.principles.solid.original;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class hierarchy violates the Liskov Substitution Principle (LSP).
 * 
 * LSP states that objects of a superclass should be replaceable with objects of a subclass
 * without affecting the correctness of the program.
 * 
 * The violation occurs in the commission calculator example because:
 * 1. The CommissionCalculator class has a calculateCommission method with no preconditions
 * 2. The SeniorSalesCommissionCalculator subclass adds a precondition (minimum 5 sales)
 * 3. Code that depends on CommissionCalculator's behavior will break when a SeniorSalesCommissionCalculator is used instead
 */
public class LSPViolation {

    /**
     * Example of LSP violation with commission calculations.
     */
    public static class CommissionCalculator {
        public BigDecimal calculateCommission(List<BigDecimal> sales) {
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal sale : sales) {
                total = total.add(sale);
            }
            return total.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Subclass that violates LSP by adding a precondition.
     */
    public static class SeniorSalesCommissionCalculator extends CommissionCalculator {
        @Override
        public BigDecimal calculateCommission(List<BigDecimal> sales) {
            // Violates LSP: Adds a precondition that wasn't in the parent class
            if (sales.size() < 5) {
                throw new IllegalArgumentException("Senior sales reps must have at least 5 sales");
            }

            return super.calculateCommission(sales).multiply(new BigDecimal("1.2"));
        }
    }

    /**
     * Demonstrates the commission calculator LSP violation.
     */
    public void demonstrateCommissionLSPViolation() {
        List<BigDecimal> sales = new ArrayList<>();
        sales.add(new BigDecimal("1000"));
        sales.add(new BigDecimal("2000"));

        CommissionCalculator calculator = new CommissionCalculator();
        System.out.println("Regular commission: " + calculator.calculateCommission(sales));

        try {
            // This will throw an exception due to the LSP violation
            CommissionCalculator seniorCalculator = new SeniorSalesCommissionCalculator();
            System.out.println("Senior commission: " + seniorCalculator.calculateCommission(sales));
        } catch (IllegalArgumentException e) {
            System.out.println("LSP Violation in commission calculator: " + e.getMessage());
        }
    }
}
