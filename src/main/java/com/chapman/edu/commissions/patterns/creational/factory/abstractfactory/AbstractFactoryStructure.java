package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

/**
 * Abstract Factory Pattern - Structure (Abstract Elements Only)
 *
 * INTENT:
 * The Abstract Factory pattern provides an interface for creating families of related
 * or dependent objects without specifying their concrete classes. It ensures that
 * created objects work together and are compatible with each other, enforcing
 * consistency across product families.
 *
 * PATTERN ROLE:
 * This file contains ONLY the abstract structural elements of the Abstract Factory pattern.
 * These are the contracts and interfaces that define what products can be created and
 * what operations they support, without any concrete implementation details.
 *
 * KEY CONCEPTS:
 *
 * 1. ABSTRACT FACTORY (CommissionSystemFactory):
 *    - Declares creation methods for each product type in the family
 *    - Defines the contract that all concrete factories must fulfill
 *    - Ensures that all factories can create a complete set of related products
 *    - Does NOT contain any concrete implementation
 *
 * 2. ABSTRACT PRODUCTS (CommissionCalculator, DealValidator, CommissionPlanCreator):
 *    - Define interfaces for different types of products
 *    - Specify operations that all concrete products must support
 *    - Client code works exclusively with these interfaces
 *    - Enable polymorphism and loose coupling
 *
 * 3. PRODUCT FAMILIES:
 *    - Each family contains one variant of each abstract product
 *    - Products within a family are designed to work together
 *    - Example families: Standard (basic validation, 5% commission) and
 *      Premium (advanced validation, 8% commission with bonuses)
 *
 * DESIGN PRINCIPLES DEMONSTRATED:
 *
 * - Program to an Interface, not an Implementation:
 *   Client code depends on abstract interfaces, never on concrete classes
 *
 * - Dependency Inversion Principle:
 *   High-level modules (clients) and low-level modules (concrete products) both
 *   depend on abstractions (these interfaces)
 *
 * - Interface Segregation Principle:
 *   Each interface has a specific, focused purpose with only necessary methods
 *
 * - Open/Closed Principle:
 *   System is open for extension (new product families) but closed for modification
 *   (adding families doesn't require changing these interfaces)
 *
 * USE CASES:
 * - When a system needs to be independent of how its products are created
 * - When related products must be used together and this constraint needs enforcement
 * - When you want to provide a library of products revealing only interfaces
 * - When a system should be configured with one of multiple families of products
 *
 * BENEFITS:
 * - Ensures compatibility between products from the same family
 * - Isolates concrete classes from client code
 * - Promotes consistency across product families
 * - Supports easy switching between different product families
 * - Follows SOLID principles
 *
 * DRAWBACKS:
 * - Adds complexity with multiple interfaces and classes
 * - Adding new product types requires modifying all factory interfaces
 * - All factories must support all products defined in the abstract factory
 * - May introduce performance overhead from additional abstraction layers
 *
 * COMMISSION DOMAIN APPLICATION:
 * In a commission calculation system, we need:
 * - Calculators that compute commissions using different algorithms
 * - Validators that ensure deals meet specific criteria
 * - Plan creators that configure commission structures
 *
 * These must work together consistently. A standard validator should pair with
 * a standard calculator and standard plan creator. The Abstract Factory pattern
 * ensures this consistency by grouping related products into families.
 */
public class AbstractFactoryStructure {

    /**
     * ABSTRACT PRODUCT A: Commission Calculator Interface
     *
     * PATTERN COMPONENT: Abstract Product
     *
     * PURPOSE:
     * Defines the contract for calculating commissions from deals. Different concrete
     * implementations provide different calculation algorithms (e.g., standard rates,
     * premium rates with bonuses, tiered rates, etc.).
     *
     * ROLE IN PATTERN:
     * This is one of the product types that the Abstract Factory creates. Each product
     * family will provide its own implementation of this interface, ensuring that the
     * calculator matches the overall commission strategy of that family.
     *
     * CLIENT PERSPECTIVE:
     * Client code receives an instance of this interface from the factory and uses it
     * to calculate commissions without knowing or caring about the specific implementation.
     *
     * DESIGN CONSIDERATIONS:
     * - Method signature includes all information needed for any calculation strategy
     * - Returns a domain object (CommissionCalculation) rather than primitive values
     * - Interface is stable and unlikely to change, supporting Open/Closed Principle
     */
    public interface CommissionCalculator {
        /**
         * Calculate commission for a deal
         *
         * @param deal the deal to calculate commission for
         * @param salesRep the sales representative who made the deal
         * @param plan the commission plan to use for calculation
         * @return the calculated commission result with all details
         */
        CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan);
    }

    /**
     * ABSTRACT PRODUCT B: Deal Validator Interface
     *
     * PATTERN COMPONENT: Abstract Product
     *
     * PURPOSE:
     * Defines the contract for validating deals. Different concrete implementations
     * provide different validation rules (e.g., basic validation, advanced validation
     * with product checks, enterprise validation with compliance rules, etc.).
     *
     * ROLE IN PATTERN:
     * This is another product type in the family. The validation rules must align with
     * the commission calculation strategy. For example, a premium commission calculator
     * might require advanced validation to ensure deal quality.
     *
     * INTERFACE DESIGN:
     * Provides two methods:
     * 1. validateDeal() - boolean check for quick validation
     * 2. getInvalidReason() - detailed error message for debugging/user feedback
     *
     * WHY TWO METHODS:
     * Separation allows clients to check validity quickly without generating error
     * messages, but also provides detailed feedback when needed.
     */
    public interface DealValidator {
        /**
         * Validate a deal according to this validator's rules
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
     * ABSTRACT PRODUCT C: Commission Plan Creator Interface
     *
     * PATTERN COMPONENT: Abstract Product
     *
     * PURPOSE:
     * Defines the contract for creating commission plans. Different concrete implementations
     * create plans with different rate structures (e.g., standard 5% rates, premium 8% rates,
     * tiered rates based on volume, etc.).
     *
     * ROLE IN PATTERN:
     * This product creates the commission plan that will be used by the calculator.
     * The plan structure must align with what the calculator expects. A standard
     * calculator works with standard plans, premium calculators with premium plans.
     *
     * FACTORY WITHIN FACTORY:
     * This is essentially a specialized factory for creating commission plans. The Abstract
     * Factory creates this specialized factory, which then creates plans. This demonstrates
     * how patterns can be composed together.
     *
     * PRODUCT CONSISTENCY:
     * Plans created by this interface must be compatible with the calculators and validators
     * from the same product family. The Abstract Factory ensures this by creating all three
     * products from the same concrete factory.
     */
    public interface CommissionPlanCreator {
        /**
         * Creates a commission plan with the specified name and description
         *
         * @param name the name of the commission plan
         * @param description the description of the plan
         * @return a CommissionPlan configured according to this creator's family type
         */
        CommissionPlan createCommissionPlan(String name, String description);
    }

    /**
     * ABSTRACT FACTORY: Commission System Factory Interface
     *
     * PATTERN COMPONENT: Abstract Factory (Core of the Pattern)
     *
     * PURPOSE:
     * Declares creation methods for each type of product in a commission system family.
     * Concrete implementations create products that are designed to work together.
     *
     * PRODUCT FAMILY CONCEPT:
     * This factory creates THREE related products:
     * 1. CommissionCalculator - calculates commissions
     * 2. DealValidator - validates deals before commission calculation
     * 3. CommissionPlanCreator - creates commission plan configurations
     *
     * These products MUST work together correctly. The Abstract Factory pattern
     * guarantees this by ensuring all three come from the same concrete factory.
     *
     * KEY BENEFIT:
     * Client code can be written against this interface and work with ANY product
     * family (standard, premium, enterprise, etc.) without knowing which specific
     * family is being used. This is the essence of polymorphism and loose coupling.
     *
     * EXAMPLE FAMILIES:
     * - Standard Family:
     *   - BasicDealValidator (checks value > 0)
     *   - StandardCommissionCalculator (5% rate)
     *   - StandardCommissionPlanCreator (5% base rate plans)
     *
     * - Premium Family:
     *   - AdvancedDealValidator (checks value > 0 AND has products)
     *   - PremiumCommissionCalculator (8% rate + 10% bonus)
     *   - PremiumCommissionPlanCreator (8% base rate plans)
     *
     * RUNTIME FLEXIBILITY:
     * The appropriate factory can be selected at runtime based on:
     * - Sales representative tier/performance level
     * - Customer type (SMB vs Enterprise)
     * - Product category (standard vs premium products)
     * - Configuration settings
     * - Business rules
     *
     * DEPENDENCY INJECTION:
     * In practice, a dependency injection framework would inject the appropriate
     * factory implementation into client code, making the system highly configurable
     * and testable.
     */
    public interface CommissionSystemFactory {
        /**
         * Creates a commission calculator for this factory's product family
         *
         * FACTORY METHOD:
         * This is a factory method within the abstract factory. Each concrete factory
         * will return a specific calculator implementation appropriate for its family.
         *
         * @return a CommissionCalculator specific to this family
         */
        CommissionCalculator createCalculator();
        /**
         * Creates a deal validator for this factory's product family
         *
         * PRODUCT COMPATIBILITY:
         * The validator returned will enforce rules consistent with the commission
         * calculator from the same factory. This ensures deals are validated according
         * to the same standards used for commission calculation.
         *
         * @return a DealValidator specific to this family
         */
        DealValidator createValidator();
        /**
         * Creates a commission plan creator for this factory's product family
         *
         * CONFIGURATION CONSISTENCY:
         * The plan creator will generate commission plans with rate structures that
         * match the calculator's algorithm. A standard factory creates plan creators
         * that make 5% rate plans, premium factory creates 8% rate plan creators.
         *
         * @return a CommissionPlanCreator specific to this family
         */
        CommissionPlanCreator createPlanCreator();
    }
}