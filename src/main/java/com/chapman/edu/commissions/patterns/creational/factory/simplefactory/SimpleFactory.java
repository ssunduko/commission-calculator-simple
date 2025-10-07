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
     * FACTORY METHOD: Creates commission calculation objects based on type parameter
     *
     * PURPOSE:
     * This is the core factory method that encapsulates the object creation logic.
     * Instead of client code calling "new CommissionCalculation()" and configuring it,
     * clients call this method which handles all initialization details.
     *
     * PATTERN COMPONENT: This is the "Factory" in the Simple Factory pattern
     *
     * HOW IT WORKS:
     * 1. Accepts a type parameter to determine which variant to create
     * 2. Creates base CommissionCalculation object
     * 3. Applies type-specific configuration using conditional logic (switch statement)
     * 4. Returns fully initialized object to client
     *
     * BENEFITS:
     * - Centralizes creation logic in one place
     * - Hides complexity from client code
     * - Provides meaningful method name instead of generic "new"
     * - Makes it easy to add validation and error handling
     *
     * DRAWBACK:
     * - Adding new types requires modifying this method (violates Open/Closed Principle)
     * - This is acceptable for stable, limited set of types
     *
     * @param type the type of commission calculation to create ("standard", "bonus", "accelerated")
     * @param dealId the ID of the deal this commission is for
     * @param salesRepId the ID of the sales representative earning the commission
     * @param amount the base commission amount before any adjustments
     * @return a fully configured commission calculation instance
     * @throws IllegalArgumentException if an unknown type is provided
     */
    public static CommissionCalculation createCommissionCalculation(String type, String dealId, String salesRepId, BigDecimal amount) {
        // STEP 1: Create the base product object
        // All commission calculations start with the same base constructor
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);

        // STEP 2: Apply common configuration
        // Set a unique identifier based on the type - helps with debugging and tracking
        calculation.setId("COMMISSION-" + type.toUpperCase());

        // Set the calculated by field - indicates this was created by the factory
        calculation.setCalculatedBy("SimpleFactory-" + type);

        // STEP 3: Apply type-specific initialization using conditional logic
        // This is where the Simple Factory's conditional logic lives
        // Each case handles the unique configuration for that commission type
        switch (type.toLowerCase()) {
            case "standard":
                // STANDARD VARIANT: No additional processing needed
                // The base commission amount is used as-is
                // This represents a straightforward commission with no bonuses or multipliers
                break;

            case "bonus":
                // BONUS VARIANT: Add a percentage bonus to the base commission
                // Calculate 10% bonus on top of the base amount
                BigDecimal bonusAmount = amount.multiply(new BigDecimal("0.1"));
                // Set the new base commission (original + 10% bonus)
                calculation.setBaseCommission(amount.add(bonusAmount));
                // Use case: Reward exceptional sales performance or quota achievement
                break;

            case "accelerated":
                // ACCELERATED VARIANT: Apply a multiplier to the base commission
                // Multiply the base amount by 1.5x for accelerated earnings
                calculation.setBaseCommission(amount.multiply(new BigDecimal("1.5")));
                // Use case: Incentivize closing large deals or strategic accounts
                break;

            default:
                // ERROR HANDLING: Validate input and provide clear error message
                // This prevents creation of invalid commission calculations
                throw new IllegalArgumentException("Unknown commission calculation type: " + type);
        }

        // STEP 4: Finalize the product
        // Recalculate ensures all derived fields are updated based on the configuration
        calculation.recalculate();

        // STEP 5: Return the fully initialized product
        // Client receives a ready-to-use object without knowing how it was configured
        return calculation;
    }
}
