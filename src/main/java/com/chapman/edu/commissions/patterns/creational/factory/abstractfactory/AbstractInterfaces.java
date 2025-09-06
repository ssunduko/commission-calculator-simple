package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

/**
 * Abstract Interfaces
 * 
 * This file demonstrates the abstract interfaces used in the Abstract Factory pattern.
 * These interfaces define the contract for creating families of related objects.
 * 
 * ROLE IN THE ABSTRACT FACTORY PATTERN:
 * This file contains the core interfaces that form the foundation of the Abstract Factory pattern:
 * 1. Abstract Product Interfaces (CommissionCalculator, DealValidator)
 *    - Define what products can do, not how they do it
 *    - Allow client code to work with any product variant
 *    - Establish the contract that concrete products must fulfill
 * 
 * 2. Abstract Factory Interface (CommissionSystemFactory)
 *    - Declares creation methods for each abstract product type
 *    - Ensures that all concrete factories provide a complete family of products
 *    - Defines the contract that concrete factories must fulfill
 * 
 * DESIGN PRINCIPLES DEMONSTRATED:
 * - Interface Segregation: Each interface has a specific, focused purpose
 * - Dependency Inversion: High-level modules depend on abstractions, not concrete classes
 * - Open/Closed: System can be extended with new products without modifying existing code
 * - Program to an Interface: Client code works with interfaces, not implementations
 * 
 * INTERFACE DESIGN CONSIDERATIONS:
 * - Cohesion: Each interface represents a single, well-defined responsibility
 * - Completeness: Interfaces include all methods needed by client code
 * - Abstraction: Interfaces focus on what operations are available, not implementation details
 * - Consistency: Related interfaces follow consistent naming and parameter conventions
 * 
 * These interfaces form the contract between the client code and the factory system,
 * allowing for loose coupling and easy substitution of different product families.
 */
public class AbstractInterfaces {

    /**
     * Abstract Product Interface: Commission Calculator
     * 
     * This interface defines the contract for commission calculators.
     * Different implementations can provide different calculation algorithms.
     */
    public interface CommissionCalculator {
        /**
         * Calculate commission for a deal
         * 
         * @param deal the deal to calculate commission for
         * @param salesRep the sales representative who made the deal
         * @return the calculated commission
         */
        CommissionCalculation calculateCommission(Deal deal, User salesRep);
    }

    /**
     * Abstract Product Interface: Deal Validator
     * 
     * This interface defines the contract for deal validators.
     * Different implementations can provide different validation rules.
     */
    public interface DealValidator {
        /**
         * Validate a deal
         * 
         * @param deal the deal to validate
         * @return true if the deal is valid, false otherwise
         */
        boolean validateDeal(Deal deal);

        /**
         * Get the reason why a deal is invalid
         * 
         * @param deal the deal to check
         * @return the reason why the deal is invalid, or null if it's valid
         */
        String getInvalidReason(Deal deal);
    }

    /**
     * Abstract Factory Interface: Commission System Factory
     * 
     * This interface defines the contract for creating families of related objects.
     * Different implementations can provide different families of objects.
     */
    public interface CommissionSystemFactory {
        /**
         * Create a commission calculator
         * 
         * @return a new commission calculator
         */
        CommissionCalculator createCommissionCalculator();

        /**
         * Create a deal validator
         * 
         * @return a new deal validator
         */
        DealValidator createDealValidator();
    }
}
