package com.chapman.edu.commissions.patterns.creational.factory.factorymethods;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Factory Method Structure
 * 
 * INTENT:
 * The Factory Method pattern defines an interface for creating objects but lets
 * subclasses decide which classes to instantiate. It allows a class to defer
 * instantiation to subclasses, promoting loose coupling and adherence to the
 * Open/Closed Principle.
 * 
 * KEY CONCEPTS:
 * - Creator: An abstract class that declares the factory method (CommissionCalculatorFactory)
 * - Concrete Creators: Subclasses that implement the factory method (StandardCommissionCalculatorFactory, etc.)
 * - Product: The object created by the factory method (CommissionCalculation)
 * - Template Method: A method in the Creator that calls the factory method (calculateCommission)
 * 
 * USE CASES:
 * - When a class can't anticipate the type of objects it needs to create
 * - When a class wants its subclasses to specify the objects it creates
 * - When you want to provide users of your framework with a way to extend its internal components
 * - When you need to create different implementations of an interface based on some condition
 * 
 * BENEFITS:
 * - Follows Open/Closed Principle: New product types can be added without changing existing code
 * - Promotes loose coupling: Creator works with products through abstract interfaces
 * - Provides hooks for subclasses: Allows customization of object creation logic
 * - Connects parallel class hierarchies: Links creator hierarchy with product hierarchy
 * 
 * DRAWBACKS:
 * - Complexity: Introduces additional classes and indirection
 * - Inheritance requirement: Requires creating a new subclass for each product type
 * - Potential overuse: Can lead to an explosion of subclasses if there are many product variations
 * - Initialization parameters: Can be challenging to handle different initialization parameters
 * 
 * This example demonstrates the structure of the Factory Method pattern
 * with an abstract creator class and concrete creator subclasses.
 */
public class FactoryMethodStructure {

    /**
     * Abstract Creator class that declares the factory method
     */
    public static abstract class CommissionCalculatorFactory {

        /**
         * Factory method that subclasses must implement
         */
        protected abstract CommissionCalculation createCommissionCalculation(String dealId, String salesRepId, BigDecimal amount);

        /**
         * Template method that uses the factory method
         */
        public CommissionCalculation calculateCommission(String dealId, String salesRepId, BigDecimal amount) {
            // Create the commission calculation using the factory method
            CommissionCalculation calculation = createCommissionCalculation(dealId, salesRepId, amount);

            // Perform common operations
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * Concrete Creator for standard commission calculations
     */
    public static class StandardCommissionCalculatorFactory extends CommissionCalculatorFactory {
        @Override
        protected CommissionCalculation createCommissionCalculation(String dealId, String salesRepId, BigDecimal amount) {
            CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);
            calculation.setId("STANDARD-" + dealId);
            calculation.setCalculatedBy("StandardCommissionCalculatorFactory");
            return calculation;
        }
    }

    /**
     * Concrete Creator for bonus commission calculations
     */
    public static class BonusCommissionCalculatorFactory extends CommissionCalculatorFactory {
        @Override
        protected CommissionCalculation createCommissionCalculation(String dealId, String salesRepId, BigDecimal amount) {
            CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);
            calculation.setId("BONUS-" + dealId);
            calculation.setCalculatedBy("BonusCommissionCalculatorFactory");

            // Apply a 10% bonus to the base commission
            BigDecimal bonusAmount = amount.multiply(new BigDecimal("0.1"));
            calculation.setBaseCommission(amount.add(bonusAmount));

            return calculation;
        }
    }

    /**
     * Concrete Creator for accelerated commission calculations
     */
    public static class AcceleratedCommissionCalculatorFactory extends CommissionCalculatorFactory {
        @Override
        protected CommissionCalculation createCommissionCalculation(String dealId, String salesRepId, BigDecimal amount) {
            CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);
            calculation.setId("ACCELERATED-" + dealId);
            calculation.setCalculatedBy("AcceleratedCommissionCalculatorFactory");

            // Apply a 1.5x multiplier to the base commission
            calculation.setBaseCommission(amount.multiply(new BigDecimal("1.5")));

            return calculation;
        }
    }
}
