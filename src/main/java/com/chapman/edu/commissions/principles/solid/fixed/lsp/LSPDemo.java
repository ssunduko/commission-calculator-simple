package com.chapman.edu.commissions.principles.solid.fixed.lsp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates the proper use of the commission calculators
 * while respecting the Liskov Substitution Principle.
 */
public class LSPDemo {
    
    /**
     * Demonstrates how to use the commission calculators in a way that respects LSP.
     */
    public void demonstrateCommissionCalculation() {
        // Create some sample sales data
        List<BigDecimal> smallSalesList = new ArrayList<>();
        smallSalesList.add(new BigDecimal("1000"));
        smallSalesList.add(new BigDecimal("2000"));
        
        List<BigDecimal> largeSalesList = new ArrayList<>();
        largeSalesList.add(new BigDecimal("1000"));
        largeSalesList.add(new BigDecimal("2000"));
        largeSalesList.add(new BigDecimal("3000"));
        largeSalesList.add(new BigDecimal("4000"));
        largeSalesList.add(new BigDecimal("5000"));
        
        // Create calculators
        CommissionCalculator baseCalculator = new BaseCommissionCalculator();
        CommissionCalculator seniorCalculator = new SeniorSalesCommissionCalculator();
        
        // Process small sales list
        System.out.println("Processing small sales list (2 sales):");
        processCommission(baseCalculator, smallSalesList);
        processCommission(seniorCalculator, smallSalesList);
        
        // Process large sales list
        System.out.println("\nProcessing large sales list (5 sales):");
        processCommission(baseCalculator, largeSalesList);
        processCommission(seniorCalculator, largeSalesList);
    }
    
    /**
     * Processes commission calculation using the provided calculator and sales data.
     * This method respects LSP by checking if the calculator can handle the data before using it.
     */
    private void processCommission(CommissionCalculator calculator, List<BigDecimal> sales) {
        String calculatorType = calculator.getClass().getSimpleName();
        
        if (calculator.canCalculate(sales)) {
            BigDecimal commission = calculator.calculateCommission(sales);
            System.out.println(calculatorType + " calculated commission: $" + commission);
        } else {
            System.out.println(calculatorType + " cannot calculate commission for the provided sales data.");
        }
    }
    
    /**
     * CommissionCalculatorRunner method to run the demonstration.
     */
    public static void main(String[] args) {
        LSPDemo demo = new LSPDemo();
        demo.demonstrateCommissionCalculation();
    }
}