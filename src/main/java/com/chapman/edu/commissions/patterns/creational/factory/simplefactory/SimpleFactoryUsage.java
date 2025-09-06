package com.chapman.edu.commissions.patterns.creational.factory.simplefactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Simple Factory Usage
 * 
 * This class demonstrates how to use the Simple Factory pattern.
 * The Simple Factory encapsulates the object creation logic and provides
 * a simple interface for creating objects.
 * 
 * KEY POINTS IN THIS EXAMPLE:
 * - Client code (this class) doesn't need to know how to create different types of commission calculations
 * - The factory handles all the complexity of object creation and configuration
 * - Client code only needs to know what type of object it wants (standard, bonus, accelerated)
 * - The factory provides a consistent interface for creating different types of objects
 * 
 * BENEFITS DEMONSTRATED:
 * - Simplicity: Client code is clean and focused on using the objects, not creating them
 * - Consistency: All objects are created through the same interface
 * - Maintainability: If the creation process changes, only the factory needs to be updated
 * - Readability: The code clearly shows what types of objects are being created
 * 
 * REAL-WORLD ANALOGY:
 * Think of the Simple Factory as a restaurant menu. You (the client) don't need to know
 * how to cook each dish - you just order by name, and the kitchen (the factory) knows
 * how to prepare each dish according to its recipe.
 */
public class SimpleFactoryUsage {

    /**
     * Main method to demonstrate the usage of the Simple Factory
     */
    public static void main(String[] args) {
        // Create a standard commission calculation
        CommissionCalculation standardCalc = SimpleFactory.createCommissionCalculation(
            "standard", "DEAL-001", "SALES-001", new BigDecimal("1000.00"));

        // Create a bonus commission calculation
        CommissionCalculation bonusCalc = SimpleFactory.createCommissionCalculation(
            "bonus", "DEAL-002", "SALES-002", new BigDecimal("1000.00"));

        // Create an accelerated commission calculation
        CommissionCalculation acceleratedCalc = SimpleFactory.createCommissionCalculation(
            "accelerated", "DEAL-003", "SALES-003", new BigDecimal("1000.00"));

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
