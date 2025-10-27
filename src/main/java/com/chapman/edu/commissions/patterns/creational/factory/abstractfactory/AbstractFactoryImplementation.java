package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionRule;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

/**
 * Abstract Factory Pattern - Implementation (Concrete Elements Only)
 *
 * PATTERN ROLE:
 * This file contains ONLY the concrete implementations of the Abstract Factory pattern.
 * These are the actual working classes that implement the abstract interfaces defined
 * in AbstractFactoryStructure.java. This separation demonstrates the distinction between
 * the pattern's structure (contracts) and its implementation (actual behavior).
 *
 * IMPLEMENTATION DETAILS:
 * This implementation demonstrates two complete product families in the commission domain:
 *
 * 1. STANDARD FAMILY (Conservative, Entry-Level):
 *    - StandardCommissionPlanCreator: Creates plans with 5% base commission rate
 *    - StandardCommissionCalculator: Calculates commission using plan's rate directly
 *    - BasicDealValidator: Basic validation (deal value > 0)
 *    - StandardCommissionSystemFactory: Creates all Standard family products
 *
 * 2. PREMIUM FAMILY (Enhanced, High-Performance):
 *    - PremiumCommissionPlanCreator: Creates plans with 8% base commission rate
 *    - PremiumCommissionCalculator: Calculates with plan rate + 10% bonus
 *    - AdvancedDealValidator: Advanced validation (value > 0 AND has products)
 *    - PremiumCommissionSystemFactory: Creates all Premium family products
 *
 * FAMILY CONSISTENCY:
 * Each factory ensures that its products work together correctly:
 * - Standard factory creates components designed for entry-level sales programs
 * - Premium factory creates components designed for high-value sales programs
 * - Products from the same family share compatible assumptions and behaviors
 *
 * BUSINESS DOMAIN APPLICATION:
 * In real-world commission systems, different tiers require different handling:
 * - Junior sales reps might use Standard family (basic validation, standard rates)
 * - Senior sales reps might use Premium family (strict validation, enhanced rates)
 * - Enterprise accounts might use a future Enterprise family (complex validation, tiered rates)
 *
 * DESIGN PRINCIPLES APPLIED:
 *
 * - Single Responsibility Principle:
 *   Each class has one reason to change (e.g., StandardCommissionCalculator only changes
 *   if standard calculation logic changes)
 *
 * - Open/Closed Principle:
 *   New product families can be added without modifying existing implementations
 *
 * - Liskov Substitution Principle:
 *   Any concrete factory can substitute for CommissionSystemFactory without breaking clients
 *
 * - Dependency Inversion Principle:
 *   These concrete classes implement abstract interfaces, not the other way around
 *
 * EXTENSIBILITY:
 * To add a new product family (e.g., Enterprise):
 * 1. Create EnterpriseCommissionPlanCreator implementing CommissionPlanCreator
 * 2. Create EnterpriseCommissionCalculator implementing CommissionCalculator
 * 3. Create EnterpriseDealValidator implementing DealValidator
 * 4. Create EnterpriseCommissionSystemFactory implementing CommissionSystemFactory
 * 5. No changes needed to existing code or client code
 */
public class AbstractFactoryImplementation {

    // ==================== STANDARD FAMILY PRODUCTS ====================

    /**
     * CONCRETE PRODUCT: Standard Commission Plan Creator
     *
     * PATTERN COMPONENT: Concrete Product (implements CommissionPlanCreator)
     *
     * PURPOSE:
     * Creates commission plans for the STANDARD product family with conservative
     * commission rates suitable for entry-level sales programs or standard sales tiers.
     *
     * FAMILY MEMBERSHIP:
     * Part of the STANDARD family - works with StandardCommissionCalculator and BasicDealValidator.
     * All Standard family components share a philosophy of simplicity and predictability.
     *
     * CONFIGURATION DETAILS:
     * - Base commission rate: 5%
     * - Rule type: STANDARD
     * - Validity period: 1 year from creation
     * - ID prefix: "STD-PLAN-" for easy identification and debugging
     *
     * BUSINESS LOGIC:
     * Standard plans are designed for typical sales scenarios without special incentives.
     * They provide predictable, moderate commission rates that are easy to understand
     * and administer. Ideal for high-volume, lower-value transactions.
     *
     * IMPLEMENTATION NOTES:
     * - Uses timestamp in ID to ensure uniqueness
     * - Creates a single base rate rule (can be extended with additional rules)
     * - Sets effective dates to define plan validity period
     * - Uses USD currency (could be parameterized for international use)
     */
    public static class StandardCommissionPlanCreator implements CommissionPlanCreator {
        /**
         * Creates a standard commission plan with 5% base rate
         * @param name the name of the plan (e.g., "Q4 2024 Standard Plan")
         * @param description the description of the plan for documentation
         * @return a CommissionPlan configured with standard rates and rules
         */
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            // Create the base commission plan object with name and currency
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));
            // Set unique identifier with STD prefix to indicate standard family
            // Timestamp ensures uniqueness across multiple plan creations
            plan.setId("STD-PLAN-" + System.currentTimeMillis());
            // Set validity period - plan is active starting now and ending in 1 year
            // This allows for annual plan reviews and updates
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));
            // Create the base commission rate rule for standard tier
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("STD-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Standard Base Rate");
            baseRateRule.setDescription(description);
            // KEY CONFIGURATION: 5% commission rate for standard tier
            // This is the defining characteristic of the standard family
            baseRateRule.setRate(new BigDecimal("0.05"));
            baseRateRule.setType(CommissionRule.RuleType.STANDARD);
            baseRateRule.setPriority(1);

            // Add the rule to the plan - more rules could be added for complexity
            plan.addRule(baseRateRule);
            return plan;
        }
    }

    /**
     * CONCRETE PRODUCT: Standard Commission Calculator
     *
     * PATTERN COMPONENT: Concrete Product (implements CommissionCalculator)
     *
     * PURPOSE:
     * Calculates commissions for the STANDARD product family using straightforward
     * logic without bonuses or special adjustments. Applies the commission plan's
     * rate directly to the deal value.
     *
     * FAMILY MEMBERSHIP:
     * Part of the STANDARD family - designed to work with plans created by
     * StandardCommissionPlanCreator (which have 5% rates).
     *
     * CALCULATION ALGORITHM:
     * 1. Extract base rate from the commission plan's first rule
     * 2. Multiply deal value by base rate
     * 3. No bonuses, no adjustments, no special cases
     * 4. Create calculation record with result
     *
     * DESIGN RATIONALE:
     * Standard calculators prioritize simplicity and predictability. Sales representatives
     * using this calculator can easily understand their commission: deal value × rate.
     * No surprises, no complex formulas.
     *
     * BUSINESS LOGIC:
     * - For a $10,000 deal with 5% rate: commission = $500
     * - Transparent and easy to verify
     * - Suitable for high-volume sales environments
     *
     * IMPLEMENTATION NOTES:
     * - Assumes at least one rule exists in the plan (standard family guarantees this)
     * - Uses plan's first rule as the primary rate source
     * - Could be enhanced to consider multiple rules, tiers, or thresholds
     */
    public static class StandardCommissionCalculator implements CommissionCalculator {
        /**
         * Calculates commission using the plan's base rate with no additional bonuses
         *
         * @param deal the deal to calculate commission for
         * @param salesRep the sales representative earning the commission
         * @param plan the commission plan to use (should be from StandardCommissionPlanCreator)
         * @return a CommissionCalculation with the computed commission amount
         */
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // Get the base rate from the first rule in the plan
            // Standard plans always have at least one rule with the base rate
            BigDecimal baseRate = BigDecimal.ZERO;
            if (!plan.getRules().isEmpty()) {
                baseRate = plan.getRules().get(0).getRate();
            }

            // Calculate commission: deal value × base rate (e.g., $10,000 × 0.05 = $500)
            BigDecimal commissionAmount = deal.getValue().multiply(baseRate);

            // Create the calculation record with all relevant metadata
            CommissionCalculation calculation = new CommissionCalculation(
                deal.getId(),
                salesRep.getId(),
                commissionAmount
            );

            // Set metadata for tracking and debugging
            calculation.setId("STD-CALC-" + System.currentTimeMillis());
            calculation.setCalculatedBy("StandardCommissionCalculator");
            calculation.setPlanId(plan.getId());

            // Trigger recalculation to update internal state
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * CONCRETE PRODUCT: Basic Deal Validator
     *
     * PATTERN COMPONENT: Concrete Product (implements DealValidator)
     *
     * PURPOSE:
     * Provides basic validation for the STANDARD product family. Enforces minimal
     * requirements to ensure deals are eligible for commission calculation.
     *
     * FAMILY MEMBERSHIP:
     * Part of the STANDARD family - designed for entry-level sales programs where
     * validation requirements are minimal and focus on basic data integrity.
     *
     * VALIDATION RULES:
     * - Deal value must be greater than zero
     * - That's it! Simple and straightforward.
     *
     * DESIGN RATIONALE:
     * Standard family prioritizes ease of use and high throughput. Minimal validation
     * reduces friction in the sales process and allows for quick commission calculations.
     * Appropriate when business rules are simple or enforced elsewhere.
     *
     * BUSINESS LOGIC:
     * In entry-level sales programs, the main concern is ensuring deals have value.
     * More complex validation (product requirements, customer types, etc.) might be
     * handled by other systems or not required at this tier.
     *
     * IMPLEMENTATION NOTES:
     * - Single validation rule keeps logic simple and fast
     * - Could be extended with additional checks if business requirements change
     * - Returns clear error message for debugging and user feedback
     */
    public static class BasicDealValidator implements DealValidator {
        /**
         * Validates that the deal has a positive value
         *
         * @param deal the deal to validate
         * @return true if deal value > 0, false otherwise
         */
        @Override
        public boolean validateDeal(Deal deal) {
            // Basic validation: deal must have a value greater than zero
            // This prevents commission calculations on zero-value or negative deals
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0;
        }

        /**
         * Provides a clear error message when validation fails
         *
         * @param deal the deal to check
         * @return error message if invalid, null if valid
         */
        @Override
        public String getInvalidReason(Deal deal) {
            if (deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                return "Deal value must be greater than zero";
            }
            return null; // Deal is valid
        }
    }

    // ==================== PREMIUM FAMILY PRODUCTS ====================

    /**
     * CONCRETE PRODUCT: Premium Commission Plan Creator
     *
     * PATTERN COMPONENT: Concrete Product (implements CommissionPlanCreator)
     *
     * PURPOSE:
     * Creates commission plans for the PREMIUM product family with enhanced commission
     * rates designed for high-performance sales programs or strategic account management.
     *
     * FAMILY MEMBERSHIP:
     * Part of the PREMIUM family - works with PremiumCommissionCalculator (which adds
     * bonuses) and AdvancedDealValidator (which enforces stricter rules).
     *
     * CONFIGURATION DETAILS:
     * - Base commission rate: 8% (60% higher than standard)
     * - Rule type: STANDARD (could be PREMIUM if such enum existed)
     * - Validity period: 1 year from creation
     * - ID prefix: "PREM-PLAN-" for easy identification
     *
     * BUSINESS LOGIC:
     * Premium plans incentivize high-value sales and top-performing representatives.
     * The 8% base rate, combined with the PremiumCommissionCalculator's 10% bonus,
     * creates a total effective rate of 8.8%, making this tier significantly more
     * attractive than standard.
     *
     * STRATEGIC VALUE:
     * - Attracts and retains top sales talent
     * - Encourages pursuit of high-value deals
     * - Rewards sales representatives who meet advanced validation requirements
     *
     * FAMILY COMPATIBILITY:
     * Designed to work seamlessly with PremiumCommissionCalculator which expects
     * premium-rate plans and enhances them with additional bonuses.
     */
    public static class PremiumCommissionPlanCreator implements CommissionPlanCreator {
        /**
         * Creates a premium commission plan with 8% base rate
         *
         * @param name the name of the plan (e.g., "Q4 2024 Premium Plan")
         * @param description the description of the plan for documentation
         * @return a CommissionPlan configured with premium rates and rules
         */
        @Override
        public CommissionPlan createCommissionPlan(String name, String description) {
            // Create the base commission plan object with name and currency
            CommissionPlan plan = new CommissionPlan(name, Currency.getInstance("USD"));

            // Set unique identifier with PREM prefix to indicate premium family
            plan.setId("PREM-PLAN-" + System.currentTimeMillis());

            // Set validity period - plan is active for 1 year
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));

            // Create the base commission rate rule for premium tier
            CommissionRule baseRateRule = new CommissionRule();
            baseRateRule.setId("PREM-RULE-" + System.currentTimeMillis());
            baseRateRule.setName("Premium Base Rate");
            baseRateRule.setDescription(description);

            // KEY CONFIGURATION: 8% commission rate for premium tier
            // This is 60% higher than standard (8% vs 5%)
            // Combined with calculator bonus: effective rate = 8% + (8% × 10%) = 8.8%
            baseRateRule.setRate(new BigDecimal("0.08"));
            baseRateRule.setType(CommissionRule.RuleType.STANDARD);
            baseRateRule.setPriority(1);

            // Add the rule to the plan
            plan.addRule(baseRateRule);
            return plan;
        }
    }

    /**
     * CONCRETE PRODUCT: Premium Commission Calculator
     *
     * PATTERN COMPONENT: Concrete Product (implements CommissionCalculator)
     *
     * PURPOSE:
     * Calculates commissions for the PREMIUM product family using enhanced logic
     * that includes a 10% bonus on top of the base commission rate.
     *
     * FAMILY MEMBERSHIP:
     * Part of the PREMIUM family - designed to work with plans created by
     * PremiumCommissionPlanCreator (which have 8% rates).
     *
     * CALCULATION ALGORITHM:
     * 1. Extract base rate from the commission plan's first rule (8%)
     * 2. Calculate base commission: deal value × base rate
     * 3. Calculate bonus: base commission × 10%
     * 4. Total commission = base commission + bonus
     *
     * BUSINESS LOGIC:
     * For a $10,000 deal with 8% rate:
     * - Base commission: $10,000 × 0.08 = $800
     * - Bonus: $800 × 0.10 = $80
     * - Total: $880 (compared to $500 for standard)
     *
     * STRATEGIC VALUE:
     * - 76% higher payout than standard for the same deal ($880 vs $500)
     * - Rewards high performers with substantially better compensation
     * - Justifies stricter validation requirements (AdvancedDealValidator)
     *
     * DESIGN RATIONALE:
     * Premium calculators add complexity but provide competitive compensation.
     * The 10% bonus is transparent and easy to explain to sales representatives,
     * serving as a clear incentive for achieving premium tier status.
     */
    public static class PremiumCommissionCalculator implements CommissionCalculator {
        /**
         * Calculates commission using the plan's base rate plus a 10% bonus
         *
         * @param deal the deal to calculate commission for
         * @param salesRep the sales representative earning the commission
         * @param plan the commission plan to use (should be from PremiumCommissionPlanCreator)
         * @return a CommissionCalculation with the computed commission amount including bonus
         */
        @Override
        public CommissionCalculation calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // Get the base rate from the first rule in the plan
            // Premium plans always have at least one rule with the base rate (8%)
            BigDecimal baseRate = BigDecimal.ZERO;
            if (!plan.getRules().isEmpty()) {
                baseRate = plan.getRules().get(0).getRate();
            }

            // Calculate base commission: deal value × base rate
            // Example: $10,000 × 0.08 = $800
            BigDecimal commissionAmount = deal.getValue().multiply(baseRate);

            // Calculate and add premium bonus: base commission × 10%
            // Example: $800 × 0.10 = $80
            // This is the key differentiator of the premium family
            BigDecimal bonusAmount = commissionAmount.multiply(new BigDecimal("0.10"));
            commissionAmount = commissionAmount.add(bonusAmount);
            // Total: $800 + $80 = $880

            // Create the calculation record with all relevant metadata
            CommissionCalculation calculation = new CommissionCalculation(
                deal.getId(),
                salesRep.getId(),
                commissionAmount
            );

            // Set metadata for tracking and debugging
            calculation.setId("PREM-CALC-" + System.currentTimeMillis());
            calculation.setCalculatedBy("PremiumCommissionCalculator");
            calculation.setPlanId(plan.getId());

            // Trigger recalculation to update internal state
            calculation.recalculate();

            return calculation;
        }
    }

    /**
     * CONCRETE PRODUCT: Advanced Deal Validator
     *
     * PATTERN COMPONENT: Concrete Product (implements DealValidator)
     *
     * PURPOSE:
     * Provides advanced validation for the PREMIUM product family. Enforces stricter
     * requirements to ensure deals meet quality standards before qualifying for
     * enhanced commission rates.
     *
     * FAMILY MEMBERSHIP:
     * Part of the PREMIUM family - designed for high-value sales programs where
     * validation must ensure deal quality and completeness.
     *
     * VALIDATION RULES:
     * 1. Deal value must be greater than zero (basic requirement)
     * 2. Deal must have at least one product (ensures deal completeness)
     *
     * DESIGN RATIONALE:
     * With higher commission rates comes higher scrutiny. Advanced validation ensures
     * that premium commissions are only paid on complete, well-structured deals.
     * This protects the business from paying enhanced rates on incomplete or
     * placeholder deals.
     *
     * BUSINESS LOGIC:
     * In premium sales programs, deals should be fully detailed with actual products.
     * Requiring at least one product ensures:
     * - Deal is not just a placeholder or estimate
     * - Sales rep has identified specific solutions for the customer
     * - Deal has sufficient detail for fulfillment and delivery
     *
     * COMPARISON TO BASIC VALIDATOR:
     * - BasicDealValidator: Only checks value > 0 (1 rule)
     * - AdvancedDealValidator: Checks value > 0 AND has products (2 rules)
     * - Higher bar reflects higher commission potential
     *
     * EXTENSIBILITY:
     * Could be extended with additional rules such as:
     * - Minimum deal value threshold
     * - Customer type requirements
     * - Approval status checks
     * - Compliance validations
     */
    public static class AdvancedDealValidator implements DealValidator {
        /**
         * Validates that the deal has positive value AND at least one product
         *
         * @param deal the deal to validate
         * @return true if deal meets all advanced validation criteria, false otherwise
         */
        @Override
        public boolean validateDeal(Deal deal) {
            // Advanced validation combines multiple requirements:
            // 1. Deal must have positive value (prevents zero/negative value deals)
            // 2. Deal must have at least one product (ensures completeness)
            return deal.getValue().compareTo(BigDecimal.ZERO) > 0
                && !deal.getProducts().isEmpty();
        }

        /**
         * Provides specific error messages based on which validation rule failed
         *
         * @param deal the deal to check
         * @return specific error message if invalid, null if valid
         */
        @Override
        public String getInvalidReason(Deal deal) {
            // Check each validation rule and return specific error message

            if (deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                return "Deal value must be greater than zero";
            }

            if (deal.getProducts().isEmpty()) {
                return "Deal must have at least one product";
            }

            return null; // Deal passed all validation rules
        }
    }

    // ==================== CONCRETE FACTORIES ====================

    /**
     * CONCRETE FACTORY: Standard Commission System Factory
     *
     * PATTERN COMPONENT: Concrete Factory (implements CommissionSystemFactory)
     *
     * PURPOSE:
     * Creates the complete STANDARD product family for commission processing.
     * Ensures all products work together correctly for entry-level sales programs.
     *
     * PRODUCTS CREATED:
     * 1. StandardCommissionPlanCreator - creates plans with 5% rates
     * 2. StandardCommissionCalculator - calculates with 5% rate, no bonuses
     * 3. BasicDealValidator - validates that deal value > 0
     *
     * FAMILY GUARANTEE:
     * All products from this factory are designed to work together:
     * - Plan creator makes standard plans (5% rate)
     * - Calculator uses plan rate directly with no bonuses
     * - Validator enforces minimal requirements (value > 0)
     * - Combined effect: Predictable, moderate commission structure
     *
     * USE CASES:
     * - Entry-level sales representatives
     * - Standard product sales
     * - High-volume, lower-value transactions
     * - Sales programs requiring simple, transparent commission structures
     *
     * BUSINESS VALUE:
     * - Easy to understand and administer
     * - Low overhead in validation and calculation
     * - Suitable for most typical sales scenarios
     * - Provides baseline commission structure
     *
     * PATTERN BENEFIT DEMONSTRATED:
     * By using this factory, client code is guaranteed to get compatible plan creators,
     * calculators, and validators without having to manually ensure compatibility.
     * The factory encapsulates the knowledge of which products work together.
     */
    public static class StandardCommissionSystemFactory implements CommissionSystemFactory {
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

        /**
         * Creates a basic deal validator
         *
         * @return BasicDealValidator that checks value > 0
         */
        @Override
        public DealValidator createValidator() {
            return new BasicDealValidator();
        }
    }

    /**
     * CONCRETE FACTORY: Premium Commission System Factory
     *
     * PATTERN COMPONENT: Concrete Factory (implements CommissionSystemFactory)
     *
     * PURPOSE:
     * Creates the complete PREMIUM product family for commission processing.
     * Ensures all products work together correctly for high-value sales programs.
     *
     * PRODUCTS CREATED:
     * 1. PremiumCommissionPlanCreator - creates plans with 8% rates
     * 2. PremiumCommissionCalculator - calculates with 8% rate + 10% bonus
     * 3. AdvancedDealValidator - validates value > 0 AND has products
     *
     * FAMILY GUARANTEE:
     * All products from this factory are designed to work together:
     * - Plan creator makes premium plans (8% rate)
     * - Calculator uses plan rate and adds 10% bonus (effective: 8.8%)
     * - Validator enforces stricter requirements (value > 0 AND has products)
     * - Combined effect: Competitive premium commission structure with quality controls
     *
     * USE CASES:
     * - Senior sales representatives
     * - Top performers
     * - High-value or strategic account sales
     * - Sales programs requiring enhanced compensation and quality standards
     *
     * BUSINESS VALUE:
     * - Attracts and retains top sales talent with 76% higher commissions
     * - Enforces quality standards through advanced validation
     * - Rewards representatives who maintain complete, detailed deals
     * - Justifies higher rates with stricter requirements
     *
     * EASY SWITCHING:
     * Client code can switch from standard to premium by simply changing the factory
     * instance - no other code changes needed. This demonstrates the power of the
     * Abstract Factory pattern in enabling flexible configuration.
     *
     * EXAMPLE COMPARISON:
     * For a $10,000 deal:
     * - Standard factory products yield: $500 commission
     * - Premium factory products yield: $880 commission (76% more)
     */
    public static class PremiumCommissionSystemFactory implements CommissionSystemFactory {
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

        /**
         * Creates an advanced deal validator
         *
         * @return AdvancedDealValidator that checks value > 0 AND has products
         */
        @Override
        public DealValidator createValidator() {
            return new AdvancedDealValidator();
        }
    }
}