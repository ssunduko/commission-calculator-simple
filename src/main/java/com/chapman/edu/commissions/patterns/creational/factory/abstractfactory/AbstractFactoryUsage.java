package com.chapman.edu.commissions.patterns.creational.factory.abstractfactory;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryStructure.*;
import com.chapman.edu.commissions.patterns.creational.factory.abstractfactory.AbstractFactoryImplementation.*;

import java.math.BigDecimal;

/**
 * Abstract Factory Pattern - Usage Demonstration
 *
 * DEMONSTRATION PURPOSE:
 * This class demonstrates how client code uses the Abstract Factory pattern to create
 * and work with families of related objects. It shows the key benefits of the pattern
 * in a real-world commission calculation scenario.
 *
 * KEY DEMONSTRATION POINTS:
 *
 * 1. CLIENT DECOUPLING:
 *    - Client code works exclusively with abstract interfaces
 *    - Never references concrete product classes directly
 *    - Can switch between product families transparently
 *
 * 2. FAMILY CONSISTENCY:
 *    - All products from the same factory work together correctly
 *    - Standard factory creates standard plan creator, calculator, and validator
 *    - Premium factory creates premium plan creator, calculator, and validator
 *    - No risk of mixing incompatible products
 *
 * 3. RUNTIME SELECTION:
 *    - Product family can be selected at runtime based on business logic
 *    - In this example, selection is based on sales representative tier
 *    - Could be based on customer type, product category, configuration, etc.
 *
 * 4. COORDINATED USAGE:
 *    - Multiple products from the same family are used together seamlessly
 *    - Validator ensures deal meets family's standards
 *    - Plan creator creates plans with family's rate structure
 *    - Calculator applies family's calculation logic
 *
 * REAL-WORLD SCENARIO:
 * A sales organization processes commissions for deals made by different tiers of
 * sales representatives. Each tier has its own validation rules, commission rates,
 * and calculation logic. The Abstract Factory pattern ensures that when processing
 * a deal, all components (validator, plan, calculator) are consistent with the
 * sales representative's tier.
 *
 * PATTERN BENEFITS SHOWN:
 * - Isolation from concrete classes:
 *   Client never references StandardCommissionCalculator or PremiumCommissionCalculator
 *   directly. Works only with CommissionCalculator interface.
 *
 * - Family consistency:
 *   Products from the same factory are guaranteed to work together. No risk of using
 *   BasicDealValidator with PremiumCommissionCalculator, which could cause issues.
 *
 * - Easy switching:
 *   Changing from standard to premium family requires only changing the factory instance.
 *   All other code remains the same.
 *
 * - Encapsulation:
 *   Creation logic is encapsulated in factory classes. Client doesn't know or care
 *   how products are created, only that they work together.
 *
 * COMPARISON TO ALTERNATIVES:
 *
 * Without Abstract Factory, client code might do this:
 * ```java
 * // Bad: Client must know about all concrete classes
 * DealValidator validator = new BasicDealValidator();
 * CommissionCalculator calculator = new StandardCommissionCalculator();
 * // Risk: Could accidentally mix families
 * calculator = new PremiumCommissionCalculator(); // Now inconsistent!
 * ```
 * With Abstract Factory, client code does this:
 * ```java
 * // Good: Client works with abstractions
 * CommissionSystemFactory factory = getFactory(salesRep);
 * DealValidator validator = factory.createValidator();
 * CommissionCalculator calculator = factory.createCalculator();
 * // Benefit: Always consistent, factory ensures compatibility
 * ```
 */
public class AbstractFactoryUsage {

    /**
     * Main method demonstrating the Abstract Factory pattern in action
     *
     * DEMONSTRATION FLOW:
     * 1. Create sample data (deals and sales representatives)
     * 2. Demonstrate Standard family usage with complete workflow
     * 3. Demonstrate Premium family usage with complete workflow
     * 4. Show how the same client code works with different families
     * 5. Compare results to highlight family differences
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("ABSTRACT FACTORY PATTERN - USAGE DEMONSTRATION");
        System.out.println("Commission Calculation System with Multiple Product Families");
        System.out.println("=".repeat(80));
        System.out.println();

        // ==================== SETUP: Create Sample Data ====================

        // Create a sample deal with products
        Deal deal = new Deal("Enterprise Software License", new BigDecimal("10000.00"), "SALES-001");
        deal.setId("DEAL-001");

        // Add products to the deal (required for advanced validation)
        DealProduct product1 = new DealProduct();
        product1.setId("PROD-001");
        product1.setProductName("Enterprise License - 100 users");
        product1.setQuantity(1);
        product1.setPrice(new BigDecimal("8000.00"));
        deal.addProduct(product1);

        DealProduct product2 = new DealProduct();
        product2.setId("PROD-002");
        product2.setProductName("Premium Support - 1 year");
        product2.setQuantity(1);
        product2.setPrice(new BigDecimal("2000.00"));
        deal.addProduct(product2);

        // Create a junior sales representative (uses standard tier)
        User juniorSalesRep = new User();
        juniorSalesRep.setId("SALES-001");
        juniorSalesRep.setFirstName("Alice");
        juniorSalesRep.setLastName("Johnson");
        juniorSalesRep.addRole(UserRole.SALES_REP);

        // Create a senior sales representative (uses premium tier)
        User seniorSalesRep = new User();
        seniorSalesRep.setId("SALES-002");
        seniorSalesRep.setFirstName("Bob");
        seniorSalesRep.setLastName("Smith");
        seniorSalesRep.addRole(UserRole.SALES_REP);

        System.out.println("Sample Deal Created:");
        System.out.println("  Deal ID: " + deal.getId());
        System.out.println("  Deal Title: " + deal.getTitle());
        System.out.println("  Deal Value: $" + deal.getValue());
        System.out.println("  Products: " + deal.getProducts().size());
        System.out.println();

        // ==================== DEMONSTRATION 1: Standard Family ====================
        System.out.println("-".repeat(80));
        System.out.println("DEMONSTRATION 1: Standard Commission System (Entry-Level Tier)");
        System.out.println("-".repeat(80));
        System.out.println();
        System.out.println("Processing commission for: " + juniorSalesRep.getFirstName() + " "
            + juniorSalesRep.getLastName() + " (Junior Sales Rep)");
        System.out.println();
        // Use the standard commission system factory
        // KEY POINT: Client code works with CommissionSystemFactory interface
        processCommissionWithFactory(
            new StandardCommissionSystemFactory(),
            deal,
            juniorSalesRep,
            "Standard"
        );
        // ==================== DEMONSTRATION 2: Premium Family ====================
        System.out.println("-".repeat(80));
        System.out.println("DEMONSTRATION 2: Premium Commission System (High-Performance Tier)");
        System.out.println("-".repeat(80));
        System.out.println();
        System.out.println("Processing commission for: " + seniorSalesRep.getFirstName() + " "
            + seniorSalesRep.getLastName() + " (Senior Sales Rep)");
        System.out.println();
        // Use the premium commission system factory
        // KEY POINT: Same client code, different factory, different behavior
        processCommissionWithFactory(
            new PremiumCommissionSystemFactory(),
            deal,
            seniorSalesRep,
            "Premium"
        );

        System.out.println();

        // ==================== DEMONSTRATION 3: Runtime Factory Selection ====================

        System.out.println("-".repeat(80));
        System.out.println("DEMONSTRATION 3: Runtime Factory Selection");
        System.out.println("-".repeat(80));
        System.out.println();

        System.out.println("Demonstrating how factory can be selected at runtime based on business logic:");
        System.out.println();

        // Process commission for junior rep using runtime selection
        CommissionSystemFactory factoryForJunior = selectFactoryForSalesRep(juniorSalesRep);
        System.out.println("Selected factory for " + juniorSalesRep.getFirstName() + ": "
            + factoryForJunior.getClass().getSimpleName());
        processCommissionWithFactory(factoryForJunior, deal, juniorSalesRep, "Auto-Selected");

        System.out.println();

        // Process commission for senior rep using runtime selection
        CommissionSystemFactory factoryForSenior = selectFactoryForSalesRep(seniorSalesRep);
        System.out.println("Selected factory for " + seniorSalesRep.getFirstName() + ": "
            + factoryForSenior.getClass().getSimpleName());
        processCommissionWithFactory(factoryForSenior, deal, seniorSalesRep, "Auto-Selected");

        System.out.println();

        // ==================== SUMMARY ====================

        System.out.println("=".repeat(80));
        System.out.println("PATTERN BENEFITS DEMONSTRATED");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("1. FAMILY CONSISTENCY:");
        System.out.println("   - Standard factory creates compatible standard components");
        System.out.println("   - Premium factory creates compatible premium components");
        System.out.println("   - No risk of mixing incompatible products");
        System.out.println();
        System.out.println("2. CLIENT DECOUPLING:");
        System.out.println("   - Client code works only with abstract interfaces");
        System.out.println("   - Never references concrete product classes");
        System.out.println("   - Same code works with any product family");
        System.out.println();
        System.out.println("3. EASY SWITCHING:");
        System.out.println("   - Change factory instance to switch entire product family");
        System.out.println("   - No other code changes needed");
        System.out.println("   - Demonstrated by using same processCommissionWithFactory method");
        System.out.println();
        System.out.println("4. RUNTIME FLEXIBILITY:");
        System.out.println("   - Factory selection based on business logic (sales rep tier)");
        System.out.println("   - Could also be based on customer type, product category, config, etc.");
        System.out.println("   - System behavior adapts dynamically");
        System.out.println();
        System.out.println("For a $10,000 deal:");
        System.out.println("   - Standard family: $500 commission (5% rate, no bonus)");
        System.out.println("   - Premium family: $880 commission (8% rate + 10% bonus)");
        System.out.println("   - Difference: 76% higher payout for premium tier");
        System.out.println();
        System.out.println("=".repeat(80));
    }

    /**
     * Process a commission using the provided factory
     *
     * KEY PATTERN DEMONSTRATION:
     * This method demonstrates the core benefit of the Abstract Factory pattern.
     * It works entirely with abstract interfaces and can handle any product family
     * without knowing which specific family it's working with.
     *
     * DECOUPLING IN ACTION:
     * - Parameter type is CommissionSystemFactory (abstract interface)
     * - Never references StandardCommissionSystemFactory or PremiumCommissionSystemFactory
     * - Works with CommissionCalculator interface, not concrete implementations
     * - Works with DealValidator interface, not concrete implementations
     * - Works with CommissionPlanCreator interface, not concrete implementations
     *
     * COORDINATED PRODUCT USAGE:
     * 1. Create plan creator from factory
     * 2. Create commission plan using the plan creator
     * 3. Create validator from factory
     * 4. Validate deal using the validator
     * 5. Create calculator from factory
     * 6. Calculate commission using the calculator and plan
     *
     * All products (plan creator, validator, calculator) come from the same factory,
     * ensuring they work together correctly.
     *
     * @param factory the commission system factory to use (standard, premium, etc.)
     * @param deal the deal to process
     * @param salesRep the sales representative
     * @param familyName descriptive name of the family for output
     */
    private static void processCommissionWithFactory(
        CommissionSystemFactory factory,
        Deal deal,
        User salesRep,
        String familyName
    ) {
        System.out.println("Using " + familyName + " Commission System:");
        System.out.println();

        // STEP 1: Create plan creator from factory
        // KEY POINT: We receive CommissionPlanCreator interface, not concrete class
        CommissionPlanCreator planCreator = factory.createPlanCreator();
        System.out.println("1. Created Plan Creator: " + planCreator.getClass().getSimpleName());

        // STEP 2: Create commission plan
        // KEY POINT: Plan is created by the plan creator, ensuring it matches the family
        CommissionPlan plan = planCreator.createCommissionPlan(
            familyName + " Commission Plan Q4 2024",
            "Commission plan for " + familyName.toLowerCase() + " tier sales representatives"
        );
        System.out.println("2. Created Commission Plan:");
        System.out.println("   - Plan ID: " + plan.getId());
        System.out.println("   - Plan Name: " + plan.getName());
        System.out.println("   - Base Rate: " + plan.getRules().get(0).getRate().multiply(new BigDecimal("100")) + "%");
        System.out.println();

        // STEP 3: Create validator from factory
        // KEY POINT: We receive DealValidator interface, not concrete class
        DealValidator validator = factory.createValidator();
        System.out.println("3. Created Validator: " + validator.getClass().getSimpleName());

        // STEP 4: Validate deal
        boolean isValid = validator.validateDeal(deal);
        System.out.println("4. Deal Validation Result: " + (isValid ? "PASSED" : "FAILED"));

        if (!isValid) {
            System.out.println("   Validation Error: " + validator.getInvalidReason(deal));
            System.out.println("   Commission calculation aborted.");
            return;
        }
        System.out.println();

        // STEP 5: Create calculator from factory
        // KEY POINT: We receive CommissionCalculator interface, not concrete class
        CommissionCalculator calculator = factory.createCalculator();
        System.out.println("5. Created Calculator: " + calculator.getClass().getSimpleName());

        // STEP 6: Calculate commission
        // KEY POINT: Calculator uses the plan created by plan creator from same family
        // This ensures compatibility between plan structure and calculation logic
        CommissionCalculation calculation = calculator.calculateCommission(deal, salesRep, plan);

        System.out.println("6. Commission Calculation Complete:");
        System.out.println("   - Calculation ID: " + calculation.getId());
        System.out.println("   - Calculated By: " + calculation.getCalculatedBy());
        System.out.println("   - Plan Used: " + calculation.getPlanId());
        System.out.println("   - Deal Value: $" + deal.getValue());
        System.out.println("   - Base Commission: $" + calculation.getBaseCommission());
        System.out.println("   - Net Commission: $" + calculation.getNetCommission());

        // Show the calculation breakdown for clarity
        BigDecimal rate = plan.getRules().get(0).getRate();
        BigDecimal baseCalc = deal.getValue().multiply(rate);
        System.out.println();
        System.out.println("   Calculation Breakdown:");
        System.out.println("   - Deal Value: $" + deal.getValue());
        System.out.println("   - Commission Rate: " + rate.multiply(new BigDecimal("100")) + "%");
        System.out.println("   - Base Commission: $" + baseCalc);

        if (familyName.equals("Premium")) {
            BigDecimal bonus = baseCalc.multiply(new BigDecimal("0.10"));
            System.out.println("   - Premium Bonus (10%): $" + bonus);
            System.out.println("   - Total Commission: $" + calculation.getNetCommission());
        }
    }

    /**
     * Select appropriate factory based on sales representative characteristics
     *
     * RUNTIME FACTORY SELECTION:
     * This method demonstrates how the Abstract Factory pattern enables runtime
     * selection of product families based on business logic. In a real system,
     * this might be much more sophisticated, considering:
     * - Sales representative performance metrics
     * - Customer account type
     * - Product category
     * - Geographic region
     * - Configuration settings
     * - Business rules engine
     *
     * CURRENT LOGIC (SIMPLIFIED):
     * This simplified example uses the sales representative's ID to determine tier:
     * - IDs ending in "001" → Standard tier
     * - IDs ending in "002" → Premium tier
     *
     * In a real system, this would query a database or configuration service to
     * determine the appropriate tier based on the sales rep's performance level,
     * tenure, account assignments, etc.
     *
     * DESIGN PATTERN BENEFIT:
     * The key benefit is that once the factory is selected, all subsequent product
     * creation is automatically consistent. The client doesn't need to remember to
     * use the right calculator, validator, and plan creator - the factory ensures
     * they all match.
     *
     * @param salesRep the sales representative to evaluate
     * @return the appropriate CommissionSystemFactory for this sales rep
     */
    private static CommissionSystemFactory selectFactoryForSalesRep(User salesRep) {
        // In a real system, this would:
        // 1. Query database for sales rep's tier
        // 2. Check performance metrics
        // 3. Evaluate business rules
        // 4. Consider configuration settings

        // Simplified logic for demonstration:
        // Assume sales reps with ID ending in "002" are senior (premium tier)
        if (salesRep.getId().endsWith("002")) {
            System.out.println("   Decision: Sales rep " + salesRep.getId()
                + " qualifies for Premium tier");
            return new PremiumCommissionSystemFactory();
        } else {
            System.out.println("   Decision: Sales rep " + salesRep.getId()
                + " assigned to Standard tier");
            return new StandardCommissionSystemFactory();
        }

        // Future extension could add more tiers:
        // if (salesRep.isEnterprise()) {
        //     return new EnterpriseCommissionSystemFactory();
        // }
    }

    /**
     * ALTERNATIVE USAGE PATTERN: Dependency Injection
     *
     * In production code, you would typically NOT instantiate factories directly
     * in the client code. Instead, you would use dependency injection:
     *
     * ```java
     * public class CommissionService {
     *     private final CommissionSystemFactory factory;
     *
     *     // Factory is injected via constructor
     *     public CommissionService(CommissionSystemFactory factory) {
     *         this.factory = factory;
     *     }
     *
     *     public CommissionCalculation processCommission(Deal deal, User salesRep) {
     *         CommissionCalculator calculator = factory.createCalculator();
     *         DealValidator validator = factory.createValidator();
     *         CommissionPlanCreator planCreator = factory.createPlanCreator();
     *
     *         CommissionPlan plan = planCreator.createCommissionPlan(...);
     *
     *         if (validator.validateDeal(deal)) {
     *             return calculator.calculateCommission(deal, salesRep, plan);
     *         }
     *         return null;
     *     }
     * }
     * ```
     *
     * Configuration (Spring example):
     * ```java
     * @Configuration
     * public class CommissionConfig {
     *     @Bean
     *     @ConditionalOnProperty(name = "commission.tier", havingValue = "standard")
     *     public CommissionSystemFactory standardFactory() {
     *         return new StandardCommissionSystemFactory();
     *     }
     *
     *     @Bean
     *     @ConditionalOnProperty(name = "commission.tier", havingValue = "premium")
     *     public CommissionSystemFactory premiumFactory() {
     *         return new PremiumCommissionSystemFactory();
     *     }
     * }
     * ```
     *
     * This approach provides:
     * - Better testability (can inject mock factories)
     * - Configuration flexibility (change factory via config)
     * - Cleaner separation of concerns
     */
}