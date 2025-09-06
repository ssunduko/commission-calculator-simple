package com.chapman.edu.commissions.patterns.creational.factory.factorymethods;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.patterns.creational.factory.factorymethods.FactoryMethodStructure.StandardCommissionCalculatorFactory;
import com.chapman.edu.commissions.patterns.creational.factory.factorymethods.FactoryMethodStructure.BonusCommissionCalculatorFactory;
import com.chapman.edu.commissions.patterns.creational.factory.factorymethods.FactoryMethodStructure.AcceleratedCommissionCalculatorFactory;
import java.math.BigDecimal;

/**
 * Factory Method Usage
 * 
 * This class demonstrates how to use the Factory Method pattern.
 * The Factory Method pattern lets a class defer instantiation to subclasses.
 * 
 * CLIENT PERSPECTIVE:
 * This example shows how client code interacts with the Factory Method pattern:
 * 1. The client creates instances of concrete factories (StandardCommissionCalculatorFactory, etc.)
 * 2. The client uses the abstract interface (CommissionCalculatorFactory) to work with these factories
 * 3. The client calls the template method (calculateCommission) which internally uses the factory method
 * 4. The client receives products (CommissionCalculation objects) without knowing their concrete classes
 * 
 * KEY DEMONSTRATION POINTS:
 * - Polymorphism: The same method call (calculateCommission) produces different results based on the factory type
 * - Abstraction: The client works with abstract types, not concrete implementations
 * - Decoupling: The client is decoupled from the specific product creation logic
 * - Consistency: All products are created through a uniform interface
 * 
 * COMPARISON WITH SIMPLE FACTORY:
 * Unlike the Simple Factory pattern where a single class creates all product types,
 * here each factory type is responsible for creating one type of product. This approach:
 * - Is more extensible (new factories can be added without modifying existing code)
 * - Follows the Single Responsibility Principle more closely
 * - Allows for more specialized creation logic in each factory
 * - Requires more classes but provides better separation of concerns
 * 
 * PRACTICAL USAGE SCENARIOS:
 * - When different business units need different implementations of the same interface
 * - When a framework needs to allow users to extend and customize object creation
 * - When object creation involves complex logic that should be encapsulated
 */
public class FactoryMethodUsage {

    /**
     * Main method to demonstrate the usage of the Factory Method pattern
     */
    public static void main(String[] args) {
        // Create factories for different types of commission calculations
        FactoryMethodStructure.CommissionCalculatorFactory standardFactory = new StandardCommissionCalculatorFactory();
        FactoryMethodStructure.CommissionCalculatorFactory bonusFactory = new BonusCommissionCalculatorFactory();
        FactoryMethodStructure.CommissionCalculatorFactory acceleratedFactory = new AcceleratedCommissionCalculatorFactory();

        // Use the factories to create commission calculations
        CommissionCalculation standardCalc = standardFactory.calculateCommission(
            "DEAL-001", "SALES-001", new BigDecimal("1000.00"));

        CommissionCalculation bonusCalc = bonusFactory.calculateCommission(
            "DEAL-002", "SALES-002", new BigDecimal("1000.00"));

        CommissionCalculation acceleratedCalc = acceleratedFactory.calculateCommission(
            "DEAL-003", "SALES-003", new BigDecimal("1000.00"));

        // Print the results
        System.out.println("Standard Commission Calculation:");
        System.out.println("ID: " + standardCalc.getId());
        System.out.println("Calculated By: " + standardCalc.getCalculatedBy());
        System.out.println("Base Commission: " + standardCalc.getBaseCommission());
        System.out.println("Net Commission: " + standardCalc.getNetCommission());
        System.out.println();

        System.out.println("Bonus Commission Calculation:");
        System.out.println("ID: " + bonusCalc.getId());
        System.out.println("Calculated By: " + bonusCalc.getCalculatedBy());
        System.out.println("Base Commission: " + bonusCalc.getBaseCommission());
        System.out.println("Net Commission: " + bonusCalc.getNetCommission());
        System.out.println();

        System.out.println("Accelerated Commission Calculation:");
        System.out.println("ID: " + acceleratedCalc.getId());
        System.out.println("Calculated By: " + acceleratedCalc.getCalculatedBy());
        System.out.println("Base Commission: " + acceleratedCalc.getBaseCommission());
        System.out.println("Net Commission: " + acceleratedCalc.getNetCommission());
    }
}
