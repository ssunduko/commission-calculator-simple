package com.chapman.edu.commissions.principles.oop.polymorphism;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates how polymorphism works with the commission calculator classes.
 * It shows:
 * 1. Creating instances of different commission calculator implementations
 * 2. How they can be treated as the common interface type
 * 3. Runtime polymorphism by calling methods on interface references
 * 4. How the same method call produces different results based on the actual object type
 */
public class PolymorphismDemo {
    
    public static void main(String[] args) {
        System.out.println("Polymorphism Demonstration\n");
        
        // Create instances of different commission calculator implementations
        CommissionCalculator standardCalculator = new StandardCommissionCalculator(
            new BigDecimal("5.00")
        );
        
        TieredCommissionCalculator tieredCalculator = new TieredCommissionCalculator(
            "Tiered Sales Commission"
        );
        tieredCalculator.addTier(new BigDecimal("0"), new BigDecimal("5000"), new BigDecimal("3.00"));
        tieredCalculator.addTier(new BigDecimal("5000"), new BigDecimal("10000"), new BigDecimal("5.00"));
        tieredCalculator.addTier(new BigDecimal("10000"), null, new BigDecimal("7.00"));
        
        PerformanceCommissionCalculator performanceCalculator = new PerformanceCommissionCalculator(
            new BigDecimal("4.00")
        );
        performanceCalculator.setSalesRepPerformance("SALES-001", PerformanceCommissionCalculator.PerformanceLevel.BRONZE);
        performanceCalculator.setSalesRepPerformance("SALES-002", PerformanceCommissionCalculator.PerformanceLevel.SILVER);
        performanceCalculator.setSalesRepPerformance("SALES-003", PerformanceCommissionCalculator.PerformanceLevel.GOLD);
        performanceCalculator.setSalesRepPerformance("SALES-004", PerformanceCommissionCalculator.PerformanceLevel.PLATINUM);
        
        // Add all calculators to a list of CommissionCalculator (demonstrating polymorphism)
        List<CommissionCalculator> calculators = new ArrayList<>();
        calculators.add(standardCalculator);
        calculators.add(tieredCalculator);
        calculators.add(performanceCalculator);
        
        // Sample deal values to test with
        BigDecimal smallDeal = new BigDecimal("3000.00");
        BigDecimal mediumDeal = new BigDecimal("7500.00");
        BigDecimal largeDeal = new BigDecimal("15000.00");
        
        // Demonstrate polymorphism by calling the same methods on different implementations
        System.out.println("1. Calling the same methods on different implementations:");
        for (CommissionCalculator calculator : calculators) {
            System.out.println("\nCalculator: " + calculator.getName());
            System.out.println("Commission Rate: " + calculator.getCommissionRate() + "%");
            System.out.println("Small Deal Commission: " + calculator.calculateCommission(smallDeal));
            System.out.println("Medium Deal Commission: " + calculator.calculateCommission(mediumDeal));
            System.out.println("Large Deal Commission: " + calculator.calculateCommission(largeDeal));
        }
        
        // Demonstrate specific behavior of each calculator type
        System.out.println("\n2. Specific behavior of each calculator type:");
        
        // Standard calculator
        System.out.println("\nStandard Calculator Details:");
        System.out.println(standardCalculator.getDescription());
        
        // Tiered calculator
        System.out.println("\nTiered Calculator Details:");
        System.out.println(tieredCalculator.getDescription());
        System.out.println("Tier for Small Deal: " + tieredCalculator.getTierForDealValue(smallDeal).getRate() + "%");
        System.out.println("Tier for Medium Deal: " + tieredCalculator.getTierForDealValue(mediumDeal).getRate() + "%");
        System.out.println("Tier for Large Deal: " + tieredCalculator.getTierForDealValue(largeDeal).getRate() + "%");
        
        // Performance calculator
        System.out.println("\nPerformance Calculator Details:");
        System.out.println(performanceCalculator.getDescription());
        
        String[] salesReps = {"SALES-001", "SALES-002", "SALES-003", "SALES-004"};
        System.out.println("\nCommissions for Medium Deal by Performance Level:");
        for (String salesRepId : salesReps) {
            System.out.println(salesRepId + " (" + 
                              performanceCalculator.getPerformanceLevel(salesRepId) + "): " + 
                              performanceCalculator.calculateCommission(mediumDeal, salesRepId));
        }
        
        // Demonstrate runtime polymorphism with a method that accepts the interface
        System.out.println("\n3. Runtime Polymorphism with a method that accepts the interface:");
        processCommission(standardCalculator, largeDeal, "SALES-001");
        processCommission(tieredCalculator, largeDeal, "SALES-001");
        processCommission(performanceCalculator, largeDeal, "SALES-003");
        
        System.out.println("\nPolymorphism Benefits:");
        System.out.println("1. Code Flexibility: The same code can work with different calculator types");
        System.out.println("2. Extensibility: New calculator types can be added without changing existing code");
        System.out.println("3. Simplification: Complex conditional logic is replaced with polymorphic method calls");
        System.out.println("4. Decoupling: Code depends on the CommissionCalculator interface, not specific implementations");
    }
    
    /**
     * This method demonstrates polymorphism by accepting any type of CommissionCalculator.
     * It works with any class that implements the CommissionCalculator interface.
     */
    private static void processCommission(CommissionCalculator calculator, BigDecimal dealValue, String salesRepId) {
        System.out.println("Processing commission with " + calculator.getName() + ":");
        
        if (calculator.qualifiesForCommission(dealValue, salesRepId)) {
            BigDecimal commission = calculator.calculateCommission(dealValue);
            System.out.println("Commission amount: " + commission);
        } else {
            System.out.println("Deal does not qualify for commission");
        }
    }
}