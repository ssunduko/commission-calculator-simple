package com.chapman.edu.commissions.patterns.creational.factory.simplefactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Simple Factory Implementation
 * 
 * INTENT:
 * The Simple Factory pattern centralizes object creation logic in a single class,
 * removing the need for client code to know how to create different types of objects.
 * It encapsulates the instantiation logic and provides a simple interface for creating objects.
 * 
 * KEY CONCEPTS:
 * - Factory: A class with a method that returns different types of objects based on input parameters
 * - Product: The objects created by the factory (CommissionCalculation in this case)
 * - Client: Code that uses the factory to create objects instead of creating them directly
 * 
 * USE CASES:
 * - When you need to create different types of objects based on some input
 * - When you want to hide the complexity of object creation from client code
 * - When you have a limited set of product types that are unlikely to change frequently
 * - When you want to centralize object creation logic to avoid duplication
 * 
 * BENEFITS:
 * - Encapsulation: Hides the details of object creation from client code
 * - Centralization: Keeps object creation logic in one place, making it easier to maintain
 * - Simplicity: Provides a simple interface for creating complex objects
 * - Naming: Gives meaningful names to object creation processes
 * 
 * DRAWBACKS:
 * - Violates Open/Closed Principle: Adding new product types requires modifying the factory class
 * - Single Responsibility Principle concerns: The factory class can become too large if it creates many types
 * - Complexity: Introduces an additional class that needs to be maintained
 * - Conditional Logic: Often relies on switch/if statements which can become unwieldy
 * 
 * This example demonstrates a simple factory that creates different types of
 * commission calculations based on the provided type.
 */
public class SimpleFactory {

    /**
     * Creates a commission calculation based on the specified type
     * 
     * @param type the type of commission calculation to create
     * @param dealId the ID of the deal
     * @param salesRepId the ID of the sales representative
     * @param amount the base commission amount
     * @return a new commission calculation instance
     */
    public static CommissionCalculation createCommissionCalculation(String type, String dealId, String salesRepId, BigDecimal amount) {
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);

        // Set a unique identifier based on the type
        calculation.setId("COMMISSION-" + type.toUpperCase());

        // Set the calculated by field based on the type
        calculation.setCalculatedBy("SimpleFactory-" + type);

        // Perform type-specific initialization
        switch (type.toLowerCase()) {
            case "standard":
                // Standard commission calculation - no additional processing
                break;
            case "bonus":
                // Apply a 10% bonus to the base commission
                BigDecimal bonusAmount = amount.multiply(new BigDecimal("0.1"));
                calculation.setBaseCommission(amount.add(bonusAmount));
                break;
            case "accelerated":
                // Apply a 1.5x multiplier to the base commission
                calculation.setBaseCommission(amount.multiply(new BigDecimal("1.5")));
                break;
            default:
                throw new IllegalArgumentException("Unknown commission calculation type: " + type);
        }

        // Recalculate the commission amounts
        calculation.recalculate();

        return calculation;
    }
}
