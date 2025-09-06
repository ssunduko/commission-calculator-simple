package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import java.math.BigDecimal;

/**
 * Abstract Factory Structure
 * 
 * INTENT:
 * The Abstract Factory pattern provides an interface for creating families of related
 * or dependent objects without specifying their concrete classes. It ensures that
 * created objects work together and are compatible with each other, enforcing
 * consistency across product families.
 *
 * KEY CONCEPTS:
 * - Abstract Factory: Interface declaring creation methods for each product type (CommissionSystemFactory)
 * - Concrete Factories: Implementations of the abstract factory (StandardCommissionSystemFactory, PremiumCommissionSystemFactory)
 * - Abstract Products: Interfaces for product types (CommissionCalculator, DealValidator)
 * - Concrete Products: Implementations of the abstract products (StandardCommissionCalculator, BasicDealValidator, etc.)
 * - Product Families: Sets of related products created by the same factory (Standard products, Premium products)
 *
 * USE CASES:
 * - When a system needs to be independent of how its products are created, composed, and represented
 * - When a system should be configured with one of multiple families of products
 * - When related products must be used together and this constraint needs to be enforced
 * - When you want to provide a library of products and reveal only their interfaces, not implementations
 * - When the lifetime of the dependency is conceptually shorter than the lifetime of the consumer
 *
 * BENEFITS:
 * - Ensures compatibility between products: Objects created by the same factory work together
 * - Isolates concrete classes: Client code works with abstract interfaces, not implementations
 * - Promotes consistency: All products from a family share common characteristics
 * - Supports product family switching: Entire product family can be switched by changing factory
 * - Follows Single Responsibility and Open/Closed Principles: Each factory is responsible for one family
 *
 * DRAWBACKS:
 * - Complexity: Introduces many interfaces and classes, making the code more complex
 * - Extensibility challenges: Adding new product types requires modifying all factory interfaces and implementations
 * - Commitment to an interface: All factories must support all products defined by the abstract factory
 * - Potential performance overhead: Additional layers of abstraction can impact performance
 *
 * This example demonstrates the structure of the Abstract Factory pattern
 * with abstract product interfaces and concrete product implementations.
 */
public class AbstractFactoryStructure {

    /**
     * Abstract Product: Commission Calculator
     */
    public interface CommissionCalculator {
        CommissionCalculation calculateCommission(Deal deal, User salesRep);
    }

    /**
     * Abstract Product: Deal Validator
     */
    public interface DealValidator {
        boolean validateDeal(Deal deal);
    }

    /**
     * Abstract Factory: Commission System Factory
     */
    public interface CommissionSystemFactory {
        CommissionCalculator createCommissionCalculator();
        DealValidator createDealValidator();
    }

    /**
     * Concrete Product: Standard Commission Calculator
     */
    public static class StandardCommissionCalculator implements CommissionCalculator {
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep) {
            // Calculate standard commission (e.g., 5% of deal value)
            BigDecimal commissionAmount = deal.getValue().multiply(new BigDecimal("0.05"));

            CommissionCalculation calculation = new CommissionCalculation(deal.getId(), salesRep.getId(), commissionAmount);
            calculation.setId("STD-COMM-" + deal.getId());
            calculation.setCalculatedBy("StandardCommissionCalculator");
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * Concrete Product: Premium Commission Calculator
     */
    public static class PremiumCommissionCalculator implements CommissionCalculator {
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep) {
            // Calculate premium commission (e.g., 8% of deal value)
            BigDecimal commissionAmount = deal.getValue().multiply(new BigDecimal("0.08"));

            CommissionCalculation calculation = new CommissionCalculation(deal.getId(), salesRep.getId(), commissionAmount);
            calculation.setId("PREM-COMM-" + deal.getId());
            calculation.setCalculatedBy("PremiumCommissionCalculator");
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * Concrete Product: Basic Deal Validator
     */
    public static class BasicDealValidator implements DealValidator {
        @Override
        public boolean validateDeal(Deal deal) {
            // Basic validation: deal must have a value greater than zero
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0;
        }
    }

    /**
     * Concrete Product: Advanced Deal Validator
     */
    public static class AdvancedDealValidator implements DealValidator {
        @Override
        public boolean validateDeal(Deal deal) {
            // Advanced validation: deal must have a value greater than zero and at least one product
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0 && !deal.getProducts().isEmpty();
        }
    }

    /**
     * Concrete Factory: Standard Commission System Factory
     */
    public static class StandardCommissionSystemFactory implements CommissionSystemFactory {
        @Override
        public CommissionCalculator createCommissionCalculator() {
            return new StandardCommissionCalculator();
        }

        @Override
        public DealValidator createDealValidator() {
            return new BasicDealValidator();
        }
    }

    /**
     * Concrete Factory: Premium Commission System Factory
     */
    public static class PremiumCommissionSystemFactory implements CommissionSystemFactory {
        @Override
        public CommissionCalculator createCommissionCalculator() {
            return new PremiumCommissionCalculator();
        }

        @Override
        public DealValidator createDealValidator() {
            return new AdvancedDealValidator();
        }
    }
}
