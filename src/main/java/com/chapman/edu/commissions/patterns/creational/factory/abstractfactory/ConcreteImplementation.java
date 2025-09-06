package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;

/**
 * Concrete Implementation
 * 
 * This file demonstrates the concrete implementations of the abstract interfaces
 * used in the Abstract Factory pattern.
 * 
 * ROLE IN THE ABSTRACT FACTORY PATTERN:
 * This file contains the concrete classes that implement the abstract interfaces:
 * 
 * 1. Concrete Products:
 *    - StandardCommissionCalculator, PremiumCommissionCalculator: Implement CommissionCalculator
 *    - BasicDealValidator, AdvancedDealValidator: Implement DealValidator
 *    - Each product implements its interface with specific business logic
 * 
 * 2. Concrete Factories:
 *    - StandardCommissionSystemFactory: Creates standard products (basic validation, standard rates)
 *    - PremiumCommissionSystemFactory: Creates premium products (advanced validation, higher rates)
 *    - Each factory creates a consistent family of related products
 * 
 * IMPLEMENTATION CHARACTERISTICS:
 * - Family Consistency: Each factory creates products that work together consistently
 *   (e.g., StandardCommissionSystemFactory creates StandardCommissionCalculator and BasicDealValidator)
 * - Business Logic Encapsulation: Each product encapsulates specific business rules
 *   (e.g., standard commission is 5%, premium is 8%)
 * - Clear Separation: Different product families are clearly separated
 *   (standard vs. premium)
 * 
 * CONCRETE IMPLEMENTATION CONSIDERATIONS:
 * - Cohesion: Each concrete class has a single, well-defined responsibility
 * - Loose Coupling: Concrete classes depend on abstractions, not other concrete classes
 * - Substitutability: Any concrete product can be replaced with another implementation
 *   of the same interface without affecting client code
 * - Family Integrity: Products within a family are designed to work together
 * 
 * This implementation demonstrates how the Abstract Factory pattern creates
 * families of related objects that share common characteristics and are
 * designed to work together seamlessly.
 */
public class ConcreteImplementation {

    /**
     * Concrete Product: Standard Commission Calculator
     * 
     * This class implements the CommissionCalculator interface to provide
     * a standard commission calculation algorithm.
     */
    public static class StandardCommissionCalculator implements AbstractInterfaces.CommissionCalculator {
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
     * 
     * This class implements the CommissionCalculator interface to provide
     * a premium commission calculation algorithm.
     */
    public static class PremiumCommissionCalculator implements AbstractInterfaces.CommissionCalculator {
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
     * 
     * This class implements the DealValidator interface to provide
     * basic deal validation rules.
     */
    public static class BasicDealValidator implements AbstractInterfaces.DealValidator {
        @Override
        public boolean validateDeal(Deal deal) {
            // Basic validation: deal must have a value greater than zero
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0;
        }

        @Override
        public String getInvalidReason(Deal deal) {
            if (deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                return "Deal value must be greater than zero";
            }
            return null;
        }
    }

    /**
     * Concrete Product: Advanced Deal Validator
     * 
     * This class implements the DealValidator interface to provide
     * advanced deal validation rules.
     */
    public static class AdvancedDealValidator implements AbstractInterfaces.DealValidator {
        @Override
        public boolean validateDeal(Deal deal) {
            // Advanced validation: deal must have a value greater than zero and at least one product
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0 && !deal.getProducts().isEmpty();
        }

        @Override
        public String getInvalidReason(Deal deal) {
            if (deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                return "Deal value must be greater than zero";
            }
            if (deal.getProducts().isEmpty()) {
                return "Deal must have at least one product";
            }
            return null;
        }
    }

    /**
     * Concrete Factory: Standard Commission System Factory
     * 
     * This class implements the CommissionSystemFactory interface to provide
     * a family of standard commission-related objects.
     */
    public static class StandardCommissionSystemFactory implements AbstractInterfaces.CommissionSystemFactory {
        @Override
        public AbstractInterfaces.CommissionCalculator createCommissionCalculator() {
            return new StandardCommissionCalculator();
        }

        @Override
        public AbstractInterfaces.DealValidator createDealValidator() {
            return new BasicDealValidator();
        }
    }

    /**
     * Concrete Factory: Premium Commission System Factory
     * 
     * This class implements the CommissionSystemFactory interface to provide
     * a family of premium commission-related objects.
     */
    public static class PremiumCommissionSystemFactory implements AbstractInterfaces.CommissionSystemFactory {
        @Override
        public AbstractInterfaces.CommissionCalculator createCommissionCalculator() {
            return new PremiumCommissionCalculator();
        }

        @Override
        public AbstractInterfaces.DealValidator createDealValidator() {
            return new AdvancedDealValidator();
        }
    }
}
