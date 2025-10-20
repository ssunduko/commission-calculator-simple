package com.chapman.edu.commissions.patterns.creational.factory.factorymethod;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factory Method Implementation
 *
 * This example demonstrates concrete implementations of the Factory Method pattern
 * using the CommissionCalculatorFactory abstract creator defined in FactoryMethodStructure.
 *
 * IMPLEMENTATION DETAILS:
 * This implementation shows how different concrete factory classes can be created
 * to produce different variants of CommissionCalculation objects. Each factory
 * implements the factory method (createCommissionCalculation) with its own specific
 * business logic for calculating commissions.
 *
 * CONCRETE FACTORIES DEMONSTRATED:
 * 1. StandardCommissionCalculatorFactory:
 *    - Creates standard commission calculations
 *    - Uses the base commission amount without modification
 *    - ID prefix: "STANDARD-"
 *
 * 2. BonusCommissionCalculatorFactory:
 *    - Creates commission calculations with bonus applied
 *    - Adds 10% bonus to the base commission amount
 *    - ID prefix: "BONUS-"
 *    - Business rule: baseCommission = amount + (amount × 0.1)
 *
 * 3. AcceleratedCommissionCalculatorFactory:
 *    - Creates commission calculations with accelerated rate
 *    - Applies 1.5x multiplier to the base commission amount
 *    - ID prefix: "ACCELERATED-"
 *    - Business rule: baseCommission = amount × 1.5
 *
 * PATTERN APPLICATION:
 * These concrete factories extend the abstract CommissionCalculatorFactory from
 * FactoryMethodStructure.java and override the factory method to create different
 * types of commission calculations. The template method (calculateCommission) in
 * the parent class handles the common workflow, while each concrete factory
 * customizes the object creation.
 *
 * BENEFITS DEMONSTRATED:
 * - Extensibility: New commission types can be added by creating new factory classes
 * - Encapsulation: Each factory encapsulates its specific calculation logic
 * - Polymorphism: All factories conform to the same interface (CommissionCalculatorFactory)
 * - Open/Closed Principle: New factories can be added without modifying existing code
 *
 *  */
public class FactoryMethodImplementation {

    /**
     * Concrete Creator for standard commission calculations
     */
    public static class StandardCommissionCalculatorFactory extends FactoryMethodStructure.CommissionCalculatorFactory {
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
    public static class BonusCommissionCalculatorFactory extends FactoryMethodStructure.CommissionCalculatorFactory {
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
    public static class AcceleratedCommissionCalculatorFactory extends FactoryMethodStructure.CommissionCalculatorFactory {
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
