package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionRule;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

/**
 * Abstract Factory Implementation
 * 
 * This example demonstrates a more complete implementation of the Abstract Factory pattern
 * for creating different types of commission-related objects.
 * 
 * IMPLEMENTATION DETAILS:
 * This implementation shows a real-world application of the Abstract Factory pattern
 * in a commission calculation system. It demonstrates:
 * 
 * 1. Multiple Product Types:
 *    - CommissionPlanCreator: Creates commission plans with different rate structures
 *    - CommissionCalculator: Calculates commissions using different algorithms
 * 
 * 2. Product Families:
 *    - Standard Family: Lower commission rates (5%) with no bonuses
 *    - Premium Family: Higher commission rates (8%) with additional bonuses (10%)
 * 
 * 3. Consistent Configuration:
 *    - Each factory ensures that its products work together consistently
 *    - Standard calculators work with standard plans, premium calculators with premium plans
 * 
 * BUSINESS DOMAIN APPLICATION:
 * In commission systems, it's critical that calculation methods match the commission plan's
 * structure. The Abstract Factory pattern ensures this consistency by:
 * - Grouping related objects (plans and calculators) into families
 * - Ensuring that objects from the same family work together correctly
 * - Allowing the system to switch between different commission structures easily
 * 
 * ADVANCED PATTERN FEATURES DEMONSTRATED:
 * - Product Interdependence: Calculators depend on plans created by the same factory
 * - Configuration Consistency: Each factory creates products with consistent configuration
 * - Family-Specific Behavior: Premium products include additional features (bonuses)
 * - Runtime Family Selection: The appropriate factory can be selected at runtime
 * 
 * PRACTICAL BENEFITS IN THIS CONTEXT:
 * - Business Rule Encapsulation: Commission rules are encapsulated in product families
 * - Consistent Application: Ensures commission plans and calculators are compatible
 * - Extensibility: New commission structures can be added as new factory implementations
 * - Isolation: Changes to one commission structure don't affect others
 */
public class AbstractFactoryImplementation {

    /**
     * ABSTRACT PRODUCT A: Interface for creating commission plans
     *
     * PATTERN COMPONENT: This is an "AbstractProduct" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Defines the interface for objects that create commission plans.
     * Different implementations will create plans with different rate structures
     * (e.g., standard rates vs. premium rates).
     *
     * PRODUCT FAMILY ROLE:
     * This is the first type of product in our product families. Each concrete
     * factory will create a specific implementation of this interface that is
     * compatible with the other products from the same family.
     *
     * WHY INTERFACE:
     * Using an interface ensures client code depends only on abstractions,
     * not on concrete implementations. This is the Dependency Inversion Principle.
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
     * ABSTRACT PRODUCT B: Interface for calculating commissions
     *
     * PATTERN COMPONENT: This is an "AbstractProduct" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Defines the interface for objects that calculate commissions from deals.
     * Different implementations will use different calculation algorithms
     * (e.g., standard calculation vs. premium calculation with bonuses).
     *
     * PRODUCT FAMILY ROLE:
     * This is the second type of product in our product families. Calculators
     * are designed to work with plans from the same family. A standard calculator
     * works with standard plans, premium calculators with premium plans.
     *
     * COMPATIBILITY GUARANTEE:
     * The Abstract Factory pattern ensures that when you get a calculator from
     * a factory, it will be compatible with the plan creator from that same factory.
     */
    public interface CommissionCalculator {
        /**
         * Calculates commission for a deal using the specified plan
         *
         * @param deal the deal to calculate commission for
         * @param salesRep the sales representative earning the commission
         * @param plan the commission plan to use for calculation
         * @return a CommissionCalculation with the computed commission amount
         */
        CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan);
    }

    /**
     * ABSTRACT FACTORY: Interface for creating families of related commission objects
     *
     * PATTERN COMPONENT: This is the "AbstractFactory" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Declares methods for creating each type of product in the family.
     * Concrete implementations create products that are designed to work together.
     *
     * PRODUCT FAMILY CONCEPT:
     * This factory creates TWO related products:
     * 1. CommissionPlanCreator - creates commission plans
     * 2. CommissionCalculator - calculates commissions
     *
     * These products MUST work together correctly. The Abstract Factory pattern
     * guarantees this by ensuring both products come from the same factory.
     *
     * KEY BENEFIT:
     * Client code can be written against this interface and work with ANY
     * product family (standard, premium, etc.) without knowing which specific
     * family is being used.
     *
     * EXAMPLE FAMILIES:
     * - Standard Family: 5% rate plans + basic calculator
     * - Premium Family: 8% rate plans + calculator with 10% bonus
     */
    public interface CommissionFactory {
        /**
         * Creates a commission plan creator for this factory's product family
         *
         * @return a CommissionPlanCreator specific to this family
         */
        CommissionPlanCreator createPlanCreator();

        /**
         * Creates a commission calculator for this factory's product family
         *
         * @return a CommissionCalculator specific to this family
         */
        CommissionCalculator createCalculator();
    }

    /**
     * CONCRETE PRODUCT A1: Standard tier commission plan creator
     *
     * PATTERN COMPONENT: This is a "ConcreteProduct" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Creates commission plans for the STANDARD product family with conservative
     * commission rates suitable for standard sales tiers or entry-level programs.
     *
     * FAMILY MEMBERSHIP:
     * Part of the STANDARD family - works with StandardCommissionCalculator.
     *
     * CONFIGURATION:
     * - 5% base commission rate (conservative)
     * - Standard rule type
     * - 1-year validity period
     * - "STD-" prefix for identification
     *
     * BUSINESS LOGIC:
     * Standard plans are designed for typical sales scenarios without special
     * incentives or bonuses. They provide predictable, moderate commission rates.
     */
    public static class StandardCommissionPlanCreator implements CommissionPlanCreator {
        /**
         * Creates a standard commission plan with 5% base rate
         *
         * @param name the name of the plan
         * @param description the description of the plan
         * @return a CommissionPlan configured with standard rates
         */
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            // Create the base commission plan object
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));
            // Set unique identifier with STD prefix to indicate standard family
            plan.setId("STD-PLAN-" + System.currentTimeMillis());
            // Set validity period - starts now, ends in 1 year
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));

            // Create the base commission rate rule for standard tier
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("STD-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Standard Base Rate");
            baseRateRule.setDescription(description);
            // KEY CONFIGURATION: 5% commission rate for standard tier
            baseRateRule.setRate(new BigDecimal("0.05"));
            baseRateRule.setType(CommissionRule.RuleType.STANDARD);
            baseRateRule.setPriority(1);

            // Add the rule to the plan
            plan.addRule(baseRateRule);
            return plan;
        }
    }

    /**
     * CONCRETE PRODUCT A2: Premium tier commission plan creator
     *
     * PATTERN COMPONENT: This is a "ConcreteProduct" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Creates commission plans for the PREMIUM product family with enhanced
     * commission rates designed for top-tier sales programs or strategic accounts.
     *
     * FAMILY MEMBERSHIP:
     * Part of the PREMIUM family - works with PremiumCommissionCalculator.
     *
     * CONFIGURATION:
     * - 8% base commission rate (60% higher than standard)
     * - Standard rule type
     * - 1-year validity period
     * - "PREM-" prefix for identification
     *
     * BUSINESS LOGIC:
     * Premium plans incentivize high-value sales with higher base rates.
     * When combined with PremiumCommissionCalculator (which adds 10% bonus),
     * total effective rate becomes even more attractive.
     *
     * FAMILY COMPATIBILITY:
     * Designed to work seamlessly with PremiumCommissionCalculator which
     * understands and enhances these premium rates with additional bonuses.
     */
    public static class PremiumCommissionPlanCreator implements CommissionPlanCreator {
        /**
         * Creates a premium commission plan with 8% base rate
         *
         * @param name the name of the plan
         * @param description the description of the plan
         * @return a CommissionPlan configured with premium rates
         */
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            // Create the base commission plan object
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));
            // Set unique identifier with PREM prefix to indicate premium family
            plan.setId("PREM-PLAN-" + System.currentTimeMillis());
            // Set validity period - starts now, ends in 1 year
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));

            // Create the base commission rate rule for premium tier
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("PREM-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Premium Base Rate");
            baseRateRule.setDescription(description);
            // KEY CONFIGURATION: 8% commission rate for premium tier (60% higher than standard)
            baseRateRule.setRate(new BigDecimal("0.08"));
            baseRateRule.setType(CommissionRule.RuleType.STANDARD);
            baseRateRule.setPriority(1);

            // Add the rule to the plan
            plan.addRule(baseRateRule);
            return plan;
        }
    }

    /**
     * Concrete Product: Standard Commission Calculator
     */
    public static class StandardCommissionCalculator implements CommissionCalculator {
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // Get the base rate from the first rule in the plan
            BigDecimal baseRate = BigDecimal.ZERO;
            if (!plan.getRules().isEmpty()) {
                baseRate = plan.getRules().get(0).getRate();
            }

            // Calculate commission based on the base rate
            BigDecimal commissionAmount = deal.getValue().multiply(baseRate);

            CommissionCalculation calculation = new CommissionCalculation(deal.getId(), salesRep.getId(), commissionAmount);
            calculation.setId("STD-CALC-" + System.currentTimeMillis());
            calculation.setCalculatedBy("StandardCommissionCalculator");
            calculation.setPlanId(plan.getId());
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * Concrete Product: Premium Commission Calculator
     */
    public static class PremiumCommissionCalculator implements CommissionCalculator {
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // Get the base rate from the first rule in the plan
            BigDecimal baseRate = BigDecimal.ZERO;
            if (!plan.getRules().isEmpty()) {
                baseRate = plan.getRules().get(0).getRate();
            }
            // Calculate commission based on the base rate
            BigDecimal commissionAmount = deal.getValue().multiply(baseRate);
            // Add a bonus for premium plans
            BigDecimal bonusAmount = commissionAmount.multiply(new BigDecimal("0.1")); // 10% bonus
            commissionAmount = commissionAmount.add(bonusAmount);

            CommissionCalculation calculation = new CommissionCalculation(deal.getId(), salesRep.getId(), commissionAmount);
            calculation.setId("PREM-CALC-" + System.currentTimeMillis());
            calculation.setCalculatedBy("PremiumCommissionCalculator");
            calculation.setPlanId(plan.getId());
            calculation.recalculate();

            return calculation;
        }
    }
    /**
     * CONCRETE FACTORY 2: Premium commission system factory
     *
     * PATTERN COMPONENT: This is a "ConcreteFactory" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Creates the complete PREMIUM product family for commission processing.
     * Ensures all products work together correctly for premium-tier sales programs.
     *
     * PRODUCTS CREATED:
     * 1. PremiumCommissionPlanCreator - creates plans with 8% rates
     * 2. PremiumCommissionCalculator - calculates with 8% rate + 10% bonus
     *
     * FAMILY GUARANTEE:
     * All products from this factory are designed to work together:
     * - Plan creator makes premium plans (8% rate)
     * - Calculator expects premium plans and adds bonus
     * - Combined effect: Competitive premium commission structure
     *
     * USE CASE:
     * Use this factory for high-value sales programs, strategic accounts,
     * or top-tier sales representatives who merit enhanced compensation.
     *
     * EASY SWITCHING:
     * Client code can switch from standard to premium by simply changing
     * the factory instance - no other code changes needed.
     */
    public static class PremiumCommissionFactory implements CommissionFactory {
        /**
         * Creates a premium commission plan creator
         *
         * @return PremiumCommissionPlanCreator that makes 8% rate plans
         */
        @Override
        public CommissionPlanCreator createPlanCreator() {
            return new PremiumCommissionPlanCreator();
        }

        /**
         * Creates a premium commission calculator
         *
         * @return PremiumCommissionCalculator that adds 10% bonus
         */
        @Override
        public CommissionCalculator createCalculator() {
            return new PremiumCommissionCalculator();
        }
    }

    /**
     * CONCRETE FACTORY 1: Standard commission system factory
     *
     * PATTERN COMPONENT: This is a "ConcreteFactory" in the Abstract Factory pattern
     *
     * PURPOSE:
     * Creates the complete STANDARD product family for commission processing.
     * Ensures all products work together correctly for standard-tier sales programs.
     *
     * PRODUCTS CREATED:
     * 1. StandardCommissionPlanCreator - creates plans with 5% rates
     * 2. StandardCommissionCalculator - calculates with 5% rate, no bonuses
     *
     * FAMILY GUARANTEE:
     * All products from this factory are designed to work together:
     * - Plan creator makes standard plans (5% rate)
     * - Calculator uses plan rate directly with no bonuses
     * - Combined effect: Predictable, moderate commission structure
     *
     * USE CASE:
     * Use this factory for typical sales programs, entry-level representatives,
     * or situations requiring conservative, predictable commission rates.
     *
     * PATTERN BENEFIT DEMONSTRATED:
     * By using this factory, client code is guaranteed to get compatible
     * plan creators and calculators without having to manually ensure compatibility.
     */
    public static class StandardCommissionFactory implements CommissionFactory {
        /**
         * Creates a standard commission plan creator
         *
         * @return StandardCommissionPlanCreator that makes 5% rate plans
         */
        @Override
        public CommissionPlanCreator createPlanCreator() {
            return new StandardCommissionPlanCreator();
        }

        /**
         * Creates a standard commission calculator
         *
         * @return StandardCommissionCalculator that uses plan rates directly
         */
        @Override
        public CommissionCalculator createCalculator() {
            return new StandardCommissionCalculator();
        }
    }
}
