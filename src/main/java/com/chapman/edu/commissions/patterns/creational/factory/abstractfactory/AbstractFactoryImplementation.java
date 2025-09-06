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
     * Abstract Product: Commission Plan Creator
     */
    public interface CommissionPlanCreator {
        CommissionPlan createCommissionPlan(String name, String description);
    }

    /**
     * Abstract Product: Commission Calculator
     */
    public interface CommissionCalculator {
        CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan);
    }

    /**
     * Abstract Factory: Commission Factory
     */
    public interface CommissionFactory {
        CommissionPlanCreator createPlanCreator();
        CommissionCalculator createCalculator();
    }

    /**
     * Concrete Product: Standard Commission Plan Creator
     */
    public static class StandardCommissionPlanCreator implements CommissionPlanCreator {
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));
            plan.setId("STD-PLAN-" + System.currentTimeMillis());
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));

            // Create a base rate rule
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("STD-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Standard Base Rate");
            baseRateRule.setDescription(description);
            baseRateRule.setRate(new BigDecimal("0.05")); // 5% base commission rate
            baseRateRule.setType(CommissionRule.RuleType.STANDARD);
            baseRateRule.setPriority(1);

            // Add the rule to the plan
            plan.addRule(baseRateRule);
            return plan;
        }
    }

    /**
     * Concrete Product: Premium Commission Plan Creator
     */
    public static class PremiumCommissionPlanCreator implements CommissionPlanCreator {
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));
            plan.setId("PREM-PLAN-" + System.currentTimeMillis());
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));

            // Create a base rate rule
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("PREM-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Premium Base Rate");
            baseRateRule.setDescription(description);
            baseRateRule.setRate(new BigDecimal("0.08")); // 8% base commission rate
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
     * Concrete Factory: Premium Commission Factory
     */
    public static class PremiumCommissionFactory implements CommissionFactory {
        @Override
        public CommissionPlanCreator createPlanCreator() {
            return new PremiumCommissionPlanCreator();
        }
        @Override
        public CommissionCalculator createCalculator() {
            return new PremiumCommissionCalculator();
        }
    }

    /**
     * Concrete Factory: Standard Commission Factory
     */
    public static class StandardCommissionFactory implements CommissionFactory {
        @Override
        public CommissionPlanCreator createPlanCreator() {
            return new StandardCommissionPlanCreator();
        }

        @Override
        public CommissionCalculator createCalculator() {
            return new StandardCommissionCalculator();
        }
    }
}
