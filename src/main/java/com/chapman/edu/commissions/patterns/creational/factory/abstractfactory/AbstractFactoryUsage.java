package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.CommissionCalculator;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.CommissionSystemFactory;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.DealValidator;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.StandardCommissionSystemFactory;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.PremiumCommissionSystemFactory;
import java.math.BigDecimal;

/**
 * Abstract Factory Usage
 * 
 * This class demonstrates how to use the Abstract Factory pattern.
 * The Abstract Factory pattern provides an interface for creating families of related objects.
 * 
 * CLIENT PERSPECTIVE:
 * This example shows how client code interacts with the Abstract Factory pattern:
 * 1. The client creates an instance of a concrete factory (StandardCommissionSystemFactory or PremiumCommissionSystemFactory)
 * 2. The client uses the abstract factory interface (CommissionSystemFactory) to work with the factory
 * 3. The client obtains multiple related products from the factory (CommissionCalculator and DealValidator)
 * 4. The client uses these products together, knowing they are compatible
 * 
 * KEY DEMONSTRATION POINTS:
 * - Family Consistency: Products from the same factory work together correctly
 * - Client Decoupling: Client code works with abstract interfaces, not concrete implementations
 * - Runtime Selection: Different product families can be selected at runtime
 * - Coordinated Usage: Multiple products from the same family are used together
 * 
 * PATTERN BENEFITS SHOWN:
 * - Isolation from concrete classes: Client never references concrete product classes
 * - Family consistency: Products from the same factory are guaranteed to work together
 * - Easy switching: Changing the factory changes all products consistently
 * - Encapsulation: Creation logic is encapsulated in the factory classes
 * 
 * REAL-WORLD ANALOGY:
 * Think of the Abstract Factory as a car manufacturer. When you buy a Toyota, all parts
 * (engine, transmission, electronics) are Toyota parts designed to work together. When you
 * buy a Ford, you get Ford parts. The Abstract Factory ensures you don't mix Toyota engines
 * with Ford transmissions, which could cause compatibility issues.
 */
public class AbstractFactoryUsage {

    /**
     * Main method to demonstrate the usage of the Abstract Factory pattern
     */
    public static void main(String[] args) {
        // Create a sample deal and sales rep
        Deal deal = new Deal("Sample Deal", new BigDecimal("10000.00"), "SALES-001");
        deal.setId("DEAL-001");

        User salesRep = new User();
        salesRep.setId("SALES-001");
        salesRep.setFirstName("John");
        salesRep.setLastName("Doe");
        salesRep.addRole(UserRole.SALES_REP);

        // Use the standard commission system factory
        System.out.println("Using Standard Commission System:");
        processCommission(new StandardCommissionSystemFactory(), deal, salesRep);

        // Use the premium commission system factory
        System.out.println("\nUsing Premium Commission System:");
        processCommission(new PremiumCommissionSystemFactory(), deal, salesRep);
    }

    /**
     * Process a commission using the provided factory
     * 
     * @param factory the commission system factory to use
     * @param deal the deal to process
     * @param salesRep the sales representative
     */
    private static void processCommission(CommissionSystemFactory factory, Deal deal, User salesRep) {
        // Create the products using the factory
        CommissionCalculator calculator = factory.createCommissionCalculator();
        DealValidator validator = factory.createDealValidator();

        // Use the products
        System.out.println("Deal Validation Result: " + validator.validateDeal(deal));

        if (validator.validateDeal(deal)) {
            CommissionCalculation calculation = calculator.calculateCommission(deal, salesRep);

            System.out.println("Commission Calculation:");
            System.out.println("ID: " + calculation.getId());
            System.out.println("Calculated By: " + calculation.getCalculatedBy());
            System.out.println("Base Commission: " + calculation.getBaseCommission());
            System.out.println("Net Commission: " + calculation.getNetCommission());
        } else {
            System.out.println("Deal validation failed. No commission calculated.");
        }
    }
}
