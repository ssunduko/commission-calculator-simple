package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BridgePatternImplementation provides concrete implementations for the Bridge Pattern.
 *
 * This class contains all the concrete classes that implement the interfaces and extend
 * the abstract classes defined in BridgePatternStructure.
 *
 * ### Implementation Structure:
 * 1. **Concrete Implementors**: Specific commission calculation strategies
 * 2. **Refined Abstractions**: Specific commission processors
 * 3. **Service Implementations**: Helper services for demonstration
 *
 * ### Bridge Pattern Benefit Demonstrated:
 * We can combine any calculation strategy (Implementor) with any processor (Abstraction)
 * independently. For example:
 * - SalesRepCommissionProcessor + FlatRateStrategy
 * - SalesRepCommissionProcessor + TieredValueStrategy
 * - ManagerCommissionProcessor + FlatRateStrategy
 * - ManagerCommissionProcessor + ProductBasedStrategy
 *
 * This creates M × N combinations without creating M × N classes!
 *
 * @see BridgePatternStructure for interface and abstract class definitions
 * @see BridgePatternUsage for usage examples
 */
public class BridgePatternImplementation {

    // ========== CONCRETE IMPLEMENTORS (Calculation Strategies) ==========
    // These classes implement the CommissionCalculationStrategy interface,
    // providing different algorithms for calculating commissions.
    /**
     * FlatRateStrategy - CONCRETE IMPLEMENTOR A
     *
     * Implements a simple flat rate commission calculation strategy where all deals
     * receive the same commission percentage regardless of size or product type.
     *
     * #### Role in Bridge Pattern:
     * - Implements the CommissionCalculationStrategy interface (Implementor)
     * - Provides a specific calculation algorithm (flat rate)
     * - Can be combined with any Abstraction (processor)
     * - Encapsulates the implementation details of flat rate calculation
     *
     * #### Domain Context:
     * This strategy is suitable for straightforward commission plans where complexity
     * isn't needed. It's simple, predictable, and easy to understand for sales reps.
     *
     * #### Advantages:
     * - Simple and predictable
     * - Easy to explain to sales representatives
     * - Low computational overhead
     *
     * #### When to Use:
     * - When commission structure is simple
     * - When all products/deals should be treated equally
     * - For small companies or simple sales teams
     */
    public static class FlatRateStrategy implements CommissionCalculationStrategy {
        private final BigDecimal rate;

        /**
         * Create a flat rate strategy with the specified commission rate.
         *
         * @param rate The commission rate (e.g., 0.05 for 5%)
         */
        public FlatRateStrategy(BigDecimal rate) {
            this.rate = rate;
        }

        /**
         * Calculate commission using a flat rate percentage.
         *
         * **IMPLEMENTOR BEHAVIOR:** This method contains the actual calculation logic.
         * The abstraction (processor) doesn't know or care about these details - it
         * just calls this method and gets the result.
         *
         * Formula: commission = dealValue × rate
         *
         * @param deal The deal for which to calculate commission
         * @param user The sales representative (not used in this strategy)
         * @param plan The commission plan (not used in this strategy)
         * @return The calculated commission amount
         */
        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            // Only calculate commission for won deals
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            // Simple multiplication: dealValue * rate
            return deal.getValue().multiply(rate);
        }
    }

    /**
     * TieredValueStrategy - CONCRETE IMPLEMENTOR B
     * Implements a tiered commission calculation strategy where the commission rate
     * increases as the deal value crosses certain thresholds.
     *
     * #### Role in Bridge Pattern:
     * - Implements the CommissionCalculationStrategy interface (Implementor)
     * - Provides a more complex calculation algorithm (tiered)
     * - Can be used with the same abstractions as FlatRateStrategy
     * - Demonstrates how implementations can vary in complexity
     *
     * #### Domain Context:
     * This strategy incentivizes larger deals by offering higher commission rates
     * for bigger sales. It's common in sales organizations that want to motivate
     * reps to pursue high-value opportunities.
     * #### Tiers Example:
     * - Up to $5,000: 3%
     * - $5,001 - $10,000: 5%
     * - $10,001 - $25,000: 7%
     * - $25,001 - $50,000: 10%
     * - $50,001 - $100,000: 12%
     * - Over $100,000: 15%
     *
     * #### Advantages:
     * - Incentivizes larger deals
     * - Rewards high performers
     * - Flexible tier structure
     *
     * **Bridge Pattern Note:** Despite being more complex than FlatRateStrategy,
     * this strategy can be used with the exact same abstractions (processors)
     * without any changes to the abstraction code.
     */
    public static class TieredValueStrategy implements CommissionCalculationStrategy {
        private final Map<BigDecimal, BigDecimal> tiers;

        /**
         * Create a tiered value strategy with predefined tiers.
         */
        public TieredValueStrategy() {
            tiers = new HashMap<>();
            tiers.put(new BigDecimal("5000"), new BigDecimal("0.03"));    // 3% up to $5K
            tiers.put(new BigDecimal("10000"), new BigDecimal("0.05"));   // 5% up to $10K
            tiers.put(new BigDecimal("25000"), new BigDecimal("0.07"));   // 7% up to $25K
            tiers.put(new BigDecimal("50000"), new BigDecimal("0.10"));   // 10% up to $50K
            tiers.put(new BigDecimal("100000"), new BigDecimal("0.12"));  // 12% up to $100K
        }

        /**
         * Calculate commission using tiered rates based on deal value.
         *
         * **IMPLEMENTOR BEHAVIOR:** This implementation is significantly more complex
         * than FlatRateStrategy, but the abstraction uses it the same way. This
         * demonstrates how the Bridge Pattern allows implementations to vary in
         * complexity without affecting the abstraction.
         *
         * @param deal The deal for which to calculate commission
         * @param user The sales representative (not used in this strategy)
         * @param plan The commission plan (not used in this strategy)
         * @return The calculated commission amount based on the appropriate tier
         */
        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            BigDecimal dealValue = deal.getValue();
            BigDecimal rate = new BigDecimal("0.15"); // Default 15% for deals over $100K

            // Find the appropriate tier for this deal value
            for (Map.Entry<BigDecimal, BigDecimal> tier : tiers.entrySet()) {
                if (dealValue.compareTo(tier.getKey()) <= 0) {
                    rate = tier.getValue();
                    break;
                }
            }

            return dealValue.multiply(rate);
        }
    }

    /**
     * ProductBasedStrategy - CONCRETE IMPLEMENTOR C
     *
     * Implements a product-based commission calculation strategy where different
     * product types earn different commission rates.
     *
     * #### Role in Bridge Pattern:
     * - Implements the CommissionCalculationStrategy interface (Implementor)
     * - Provides product-type-specific calculation logic
     * - Demonstrates yet another implementation approach
     * - Shows how implementations can use deal internals (products)
     *
     * #### Domain Context:
     * This strategy is used when different products have different profit margins
     * or strategic importance. For example, software licenses might have higher
     * margins than hardware, so they warrant higher commission rates.
     *
     * #### Product Type Rates:
     * - Hardware: 3% (low margin, commodity products)
     * - Software: 10% (high margin, recurring revenue)
     * - Service: 15% (high margin, consultative selling)
     * - Training: 20% (highest margin, requires expertise)
     *
     * **Bridge Pattern Note:** This strategy uses a completely different approach
     * (iterating through products) than the other strategies, yet it's still
     * compatible with all abstractions. The abstraction doesn't care about these
     * implementation details.
     */
    public static class ProductBasedStrategy implements CommissionCalculationStrategy {
        private final Map<String, BigDecimal> productRates;

        /**
         * Create a product-based strategy with predefined product type rates.
         */
        public ProductBasedStrategy() {
            productRates = new HashMap<>();
            productRates.put("Hardware", new BigDecimal("0.03"));  // 3%
            productRates.put("Software", new BigDecimal("0.10"));  // 10%
            productRates.put("Service", new BigDecimal("0.15"));   // 15%
            productRates.put("Training", new BigDecimal("0.20"));  // 20%
        }

        /**
         * Calculate commission by summing commissions for each product type.
         *
         * **IMPLEMENTOR BEHAVIOR:** This implementation uses a fundamentally different
         * approach (product-level calculation) than the other strategies (deal-level),
         * yet it implements the same interface. This flexibility is a key benefit of
         * the Bridge Pattern.
         *
         * @param deal The deal containing products to calculate commission for
         * @param user The sales representative (not used in this strategy)
         * @param plan The commission plan (not used in this strategy)
         * @return The total commission across all products
         */
        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalCommission = BigDecimal.ZERO;

            // Calculate commission for each product based on its type
            for (DealProduct product : deal.getProducts()) {
                String productType = getProductType(product.getProductName());
                BigDecimal rate = productRates.getOrDefault(productType, new BigDecimal("0.05"));
                BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                totalCommission = totalCommission.add(productValue.multiply(rate));
            }

            return totalCommission;
        }

        /**
         * Determine product type from product name.
         *
         * In a real system, this would query a product catalog or database.
         * Here, we use simple string matching for demonstration.
         *
         * @param productName The name of the product
         * @return The product type category
         */
        private String getProductType(String productName) {
            String lowerName = productName.toLowerCase();
            if (lowerName.contains("hardware")) return "Hardware";
            if (lowerName.contains("software")) return "Software";
            if (lowerName.contains("service")) return "Service";
            if (lowerName.contains("training")) return "Training";
            return "Other";
        }
    }

    // ========== REFINED ABSTRACTIONS (Commission Processors) ==========
    // These classes extend CommissionProcessor (the Abstraction),
    // providing specialized commission processing logic.

    /**
     * SalesRepCommissionProcessor - REFINED ABSTRACTION A
     *
     * Processes commissions for sales representatives using a standard workflow.
     *
     * #### Role in Bridge Pattern:
     * - Extends CommissionProcessor (Abstraction)
     * - Provides specific implementation of processCommission()
     * - Uses the strategy (Implementor) to perform calculations
     * - Adds sales-rep-specific processing logic
     *
     * #### Processing Workflow:
     * 1. Retrieve the deal from the deal service
     * 2. Retrieve the sales rep who owns the deal
     * 3. Retrieve the commission plan for the sales rep
     * 4. Delegate calculation to the strategy (Implementor)
     * 5. Package the result in a CommissionCalculation object
     *
     * **Bridge Pattern in Action:** This class uses the strategy reference inherited
     * from CommissionProcessor to delegate the actual calculation. The strategy could
     * be any implementation (FlatRate, Tiered, ProductBased), and this class doesn't
     * need to know which one it is.
     */
    public static class SalesRepCommissionProcessor extends CommissionProcessor {

        /**
         * Create a sales rep commission processor.
         *
         * @param strategy The calculation strategy to use (can be any Implementor)
         * @param dealService Service for retrieving deals
         * @param userService Service for retrieving users
         */
        public SalesRepCommissionProcessor(CommissionCalculationStrategy strategy,
                                           DealService dealService,
                                           UserService userService) {
            super(strategy, dealService, userService);
        }

        /**
         * Process commission for a sales representative's deal.
         *
         * **REFINED ABSTRACTION BEHAVIOR:** This method implements the high-level
         * workflow of commission processing. It uses services to gather data, then
         * delegates the actual calculation to the strategy (Implementor).
         *
         * **Bridge Pattern Note:** The strategy reference (this.strategy) is the
         * "bridge" to the implementation. We call strategy.calculateCommission()
         * without knowing which concrete implementation we're using.
         *
         * @param dealId The ID of the deal to process
         * @return A CommissionCalculation object with the result
         */
        @Override
        public CommissionCalculation processCommission(String dealId) {
            // Step 1-3: Gather required data
            Deal deal = dealService.getDealById(dealId);
            User salesRep = userService.getUserById(deal.getSalesRepId());
            CommissionPlan plan = userService.getCommissionPlan(salesRep.getId());

            // Step 4: Delegate calculation to the strategy (THE BRIDGE IN ACTION!)
            BigDecimal commissionAmount = strategy.calculateCommission(deal, salesRep, plan);

            // Step 5: Package the result
            CommissionCalculation calculation = new CommissionCalculation(
                    dealId, salesRep.getId(), commissionAmount);
            calculation.setCalculatedBy("SalesRepCommissionProcessor");
            calculation.setPlanId(plan.getId());

            return calculation;
        }
    }

    /**
     * ManagerCommissionProcessor - REFINED ABSTRACTION B
     *
     * Processes manager override commissions with additional business logic.
     *
     * #### Role in Bridge Pattern:
     * - Extends CommissionProcessor (Abstraction)
     * - Provides alternative implementation of processCommission()
     * - Uses the same strategy interface as SalesRepCommissionProcessor
     * - Adds manager-specific logic (override calculation)
     *
     * #### Processing Workflow:
     * 1. Retrieve the deal and sales rep (like SalesRepCommissionProcessor)
     * 2. Retrieve the MANAGER for the sales rep (different!)
     * 3. Retrieve the manager's commission plan
     * 4. Calculate base commission using the strategy
     * 5. Apply manager override rate (additional business logic)
     * 6. Package the result for the manager (not the sales rep)
     *
     * **Bridge Pattern Benefit:** This processor uses the SAME strategy interface
     * as SalesRepCommissionProcessor, but applies different business logic around it.
     * We can mix and match: ManagerCommissionProcessor + FlatRateStrategy, or
     * ManagerCommissionProcessor + TieredValueStrategy, etc.
     */
    public static class ManagerCommissionProcessor extends CommissionProcessor {
        /**
         * Manager override rate (percentage of rep commission).
         *
         * This is specific to this refined abstraction and demonstrates how
         * different abstractions can have different additional properties
         * beyond the basic strategy reference.
         */
        private final BigDecimal overrideRate;

        /**
         * Create a manager commission processor.
         *
         * @param strategy The calculation strategy to use
         * @param dealService Service for retrieving deals
         * @param userService Service for retrieving users
         * @param overrideRate The percentage of rep commission the manager receives
         */
        public ManagerCommissionProcessor(CommissionCalculationStrategy strategy,
                                          DealService dealService,
                                          UserService userService,
                                          BigDecimal overrideRate) {
            super(strategy, dealService, userService);
            this.overrideRate = overrideRate;
        }

        /**
         * Process manager override commission for a deal.
         *
         * **REFINED ABSTRACTION BEHAVIOR:** This method has a different workflow
         * than SalesRepCommissionProcessor, but still uses the same strategy interface.
         * This demonstrates how different abstractions can use the same implementation
         * in different ways.
         *
         * **Bridge Pattern Note:** Even though this processor has additional logic
         * (override rate calculation), it still delegates the base calculation to
         * the strategy. The strategy doesn't need to know it's being used by a
         * manager processor - it just calculates the commission.
         *
         * @param dealId The ID of the deal to process
         * @return A CommissionCalculation object for the manager's override commission
         */
        @Override
        public CommissionCalculation processCommission(String dealId) {
            // Gather data - note we get both the rep AND their manager
            Deal deal = dealService.getDealById(dealId);
            User salesRep = userService.getUserById(deal.getSalesRepId());
            User manager = userService.getManagerForUser(salesRep.getId());
            CommissionPlan plan = userService.getCommissionPlan(manager.getId());

            // Calculate base commission using the strategy (THE BRIDGE!)
            BigDecimal repCommission = strategy.calculateCommission(deal, salesRep, plan);

            // Apply manager-specific business logic
            BigDecimal managerCommission = repCommission.multiply(overrideRate);

            // Package result for the manager (not the rep)
            CommissionCalculation calculation = new CommissionCalculation(
                    dealId, manager.getId(), managerCommission);
            calculation.setCalculatedBy("ManagerCommissionProcessor");
            calculation.setPlanId(plan.getId());

            return calculation;
        }
    }

    // ========== SERVICE IMPLEMENTATIONS (For Demonstration) ==========
    // These classes provide concrete implementations of the service interfaces
    // defined in BridgePatternStructure. They're used for testing and demonstration.

    /**
     * DealServiceImpl - Implementation of DealService interface.
     *
     * Provides in-memory storage of sample deals for demonstration purposes.
     * In a real system, this would query a database or external service.
     */
    public static class DealServiceImpl implements DealService {
        private Map<String, Deal> deals;

        /**
         * Create a deal service with sample data.
         */
        public DealServiceImpl() {
            deals = new HashMap<>();

            // Create sample deals representing different scenarios
            Deal deal1 = new Deal("Hardware Sale", new BigDecimal("5000"), "user1");
            deal1.setId("deal1");
            deal1.setStatus(DealStatus.WON);
            deal1.addProduct(new DealProduct("prod1", "Dell Hardware Laptop", 2,
                    new BigDecimal("2500")));

            Deal deal2 = new Deal("Software License", new BigDecimal("15000"), "user1");
            deal2.setId("deal2");
            deal2.setStatus(DealStatus.WON);
            deal2.addProduct(new DealProduct("prod2", "Microsoft Software Office License", 10,
                    new BigDecimal("1500")));

            Deal deal3 = new Deal("Consulting Services", new BigDecimal("25000"), "user2");
            deal3.setId("deal3");
            deal3.setStatus(DealStatus.WON);
            deal3.addProduct(new DealProduct("prod3", "IT Consulting Service", 1,
                    new BigDecimal("25000")));

            deals.put(deal1.getId(), deal1);
            deals.put(deal2.getId(), deal2);
            deals.put(deal3.getId(), deal3);
        }

        @Override
        public Deal getDealById(String dealId) {
            return deals.get(dealId);
        }

        @Override
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            List<Deal> repDeals = new ArrayList<>();
            for (Deal deal : deals.values()) {
                if (deal.getSalesRepId().equals(salesRepId)) {
                    repDeals.add(deal);
                }
            }
            return repDeals;
        }
    }

    /**
     * UserServiceImpl - Implementation of UserService interface.
     *
     * Provides in-memory storage of sample users and commission plans.
     * In a real system, this would query a database or external service.
     */
    public static class UserServiceImpl implements UserService {
        private Map<String, User> users;
        private Map<String, CommissionPlan> plans;
        private Map<String, String> managerMap;    // Maps user ID to manager ID
        private Map<String, String> userPlanMap;   // Maps user ID to plan ID

        /**
         * Create a user service with sample data.
         */
        public UserServiceImpl() {
            users = new HashMap<>();
            plans = new HashMap<>();
            managerMap = new HashMap<>();
            userPlanMap = new HashMap<>();

            // Create sample users
            User user1 = new User("jsmith", "john.smith@example.com", "John", "Smith");
            user1.setId("user1");
            user1.addRole(UserRole.SALES_REP);

            User user2 = new User("mjohnson", "mary.johnson@example.com", "Mary", "Johnson");
            user2.setId("user2");
            user2.addRole(UserRole.SALES_REP);

            User manager = new User("rjones", "robert.jones@example.com", "Robert", "Jones");
            manager.setId("manager1");
            manager.addRole(UserRole.SALES_MANAGER);

            users.put(user1.getId(), user1);
            users.put(user2.getId(), user2);
            users.put(manager.getId(), manager);

            // Set up manager relationships
            managerMap.put(user1.getId(), manager.getId());
            managerMap.put(user2.getId(), manager.getId());

            // Create sample commission plans
            CommissionPlan standardPlan = new CommissionPlan("Standard Plan", Currency.getInstance("USD"));
            standardPlan.setId("plan1");

            CommissionPlan managerPlan = new CommissionPlan("Manager Plan", Currency.getInstance("USD"));
            managerPlan.setId("plan2");

            plans.put(standardPlan.getId(), standardPlan);
            plans.put(managerPlan.getId(), managerPlan);

            // Assign plans to users
            userPlanMap.put(user1.getId(), standardPlan.getId());
            userPlanMap.put(user2.getId(), standardPlan.getId());
            userPlanMap.put(manager.getId(), managerPlan.getId());
        }

        @Override
        public User getUserById(String userId) {
            return users.get(userId);
        }

        @Override
        public CommissionPlan getCommissionPlan(String userId) {
            String planId = userPlanMap.get(userId);
            return planId != null ? plans.get(planId) : null;
        }

        @Override
        public User getManagerForUser(String userId) {
            String managerId = managerMap.get(userId);
            return managerId != null ? users.get(managerId) : null;
        }
    }
}