package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.CommissionCalculator;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.CommissionFactory;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.CommissionPlanCreator;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.StandardCommissionFactory;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.PremiumCommissionFactory;
import java.math.BigDecimal;

/**
 * Abstract Factory Calling Code
 * 
 * This class demonstrates how to use the Abstract Factory implementation.
 * The Abstract Factory pattern provides an interface for creating families of related objects.
 * 
 * ADVANCED IMPLEMENTATION DEMONSTRATION:
 * This example shows a more complex implementation of the Abstract Factory pattern where:
 * 1. Multiple related products are created (CommissionPlanCreator and CommissionCalculator)
 * 2. Products have dependencies on each other (calculator uses the plan created by the plan creator)
 * 3. The products work together in a coordinated workflow
 * 
 * WORKFLOW DEMONSTRATED:
 * - Create a factory (StandardCommissionFactory or PremiumCommissionFactory)
 * - Get a plan creator from the factory
 * - Get a calculator from the same factory
 * - Create a plan using the plan creator
 * - Use the calculator with the plan to calculate commissions
 * 
 * KEY PATTERN ASPECTS SHOWN:
 * - Product Interdependence: Products from the same factory are designed to work together
 * - Consistent Configuration: All products share the same configuration (standard or premium)
 * - Family Cohesion: Products within a family have coordinated behavior
 * - Client Simplification: Client code is simplified by working with the factory abstraction
 * 
 * BUSINESS VALUE:
 * This pattern provides significant business value in commission systems by:
 * - Ensuring consistency between commission plans and calculation methods
 * - Allowing different commission structures to be easily swapped
 * - Maintaining the integrity of business rules across related components
 * - Providing a clean separation between different commission strategies
 * 
 * COMPARISON WITH SIMPLER EXAMPLE:
 * Unlike AbstractFactoryUsage.java which shows a simpler implementation,
 * this example demonstrates:
 * - More complex product relationships
 * - Products that depend on each other's output
 * - A more realistic business workflow
 * - Greater coordination between family members
 */
public class AbstractFactoryCallingCode {

    /**
     * Main method to demonstrate the usage of the Abstract Factory implementation
     */
    public static void main(String[] args) {
        // Create a sample deal and sales rep
        Deal deal = new Deal("Sample Deal", new BigDecimal("10000.00"), "SALES-001");
        deal.setId("DEAL-001");
        User salesRep = new User();
        salesRep.setId("SALES-001");
        salesRep.setFirstName("John");
        salesRep.setLastName("Doe");
        salesRep.addRole(com.chapman.edu.commissions.model.UserRole.SALES_REP);
        // Use the standard commission factory
        System.out.println("Using Standard Commission Factory:");
        processCommission(new StandardCommissionFactory(), deal, salesRep);
        // Use the premium commission factory
        System.out.println("\nUsing Premium Commission Factory:");
        processCommission(new PremiumCommissionFactory(), deal, salesRep);
    }
    /**
     * Process a commission using the provided factory
     * @param factory the commission factory to use
     * @param deal the deal to process
     * @param salesRep the sales representative
     */
    private static void processCommission(CommissionFactory factory, Deal deal, User salesRep) {
        // Create the products using the factory
        CommissionPlanCreator planCreator = factory.createPlanCreator();
        CommissionCalculator calculator = factory.createCalculator();
        // Create a commission plan
        CommissionPlan plan = planCreator.createCommissionPlan(
            "Commission Plan for " + deal.getTitle(),
            "Commission plan for deal " + deal.getId() + " created on " + java.time.LocalDate.now()
        );
        // Calculate the commission
        CommissionCalculation calculation = calculator.calculateCommission(deal, salesRep, plan);

        // Print the results
        System.out.println("Commission Plan:");
        System.out.println("ID: " + plan.getId());
        System.out.println("Name: " + plan.getName());
        System.out.println("Rules: " + plan.getRules().size());
        if (!plan.getRules().isEmpty()) {
            System.out.println("Base Rate: " + plan.getRules().get(0).getRate());
        }

        System.out.println("\nCommission Calculation:");
        System.out.println("ID: " + calculation.getId());
        System.out.println("Calculated By: " + calculation.getCalculatedBy());
        System.out.println("Base Commission: " + calculation.getBaseCommission());
        System.out.println("Net Commission: " + calculation.getNetCommission());
    }
}
