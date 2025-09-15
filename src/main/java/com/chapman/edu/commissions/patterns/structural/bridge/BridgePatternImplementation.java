package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a concrete implementation of the Bridge Pattern using the Commission Calculator domain model.
 * 
 * The Bridge Pattern separates the abstraction (CommissionProcessor) from its implementation (CommissionCalculationStrategy),
 * allowing them to vary independently. This implementation demonstrates how different commission calculation strategies
 * can be used with different types of commission processors.
 */
public class BridgePatternImplementation {

    /**
     * Implementor
     * This interface defines the operations that concrete implementors must implement.
     */
    public interface CommissionCalculationStrategy {
        /**
         * Calculate commission for a deal
         * @param deal The deal for which to calculate commission
         * @param user The sales representative who owns the deal
         * @param plan The commission plan to apply
         * @return The calculated commission amount
         */
        BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan);
    }

    /**
     * Concrete Implementor A
     * Implements a flat rate commission calculation strategy
     */
    public static class FlatRateStrategy implements CommissionCalculationStrategy {
        private final BigDecimal rate;

        public FlatRateStrategy(BigDecimal rate) {
            this.rate = rate;
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            return deal.getValue().multiply(rate);
        }
    }

    /**
     * Concrete Implementor B
     * Implements a tiered commission calculation strategy based on deal value
     */
    public static class TieredValueStrategy implements CommissionCalculationStrategy {
        private final Map<BigDecimal, BigDecimal> tiers;

        public TieredValueStrategy() {
            tiers = new HashMap<>();
            tiers.put(new BigDecimal("5000"), new BigDecimal("0.03"));  // 3% for deals up to $5,000
            tiers.put(new BigDecimal("10000"), new BigDecimal("0.05")); // 5% for deals up to $10,000
            tiers.put(new BigDecimal("25000"), new BigDecimal("0.07")); // 7% for deals up to $25,000
            tiers.put(new BigDecimal("50000"), new BigDecimal("0.10")); // 10% for deals up to $50,000
            tiers.put(new BigDecimal("100000"), new BigDecimal("0.12")); // 12% for deals up to $100,000
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            BigDecimal dealValue = deal.getValue();
            BigDecimal rate = new BigDecimal("0.15"); // Default 15% for deals over $100,000

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
     * Concrete Implementor C
     * Implements a product-based commission calculation strategy
     */
    public static class ProductBasedStrategy implements CommissionCalculationStrategy {
        private final Map<String, BigDecimal> productRates;

        public ProductBasedStrategy() {
            productRates = new HashMap<>();
            productRates.put("Hardware", new BigDecimal("0.03")); // 3% for hardware products
            productRates.put("Software", new BigDecimal("0.10")); // 10% for software products
            productRates.put("Service", new BigDecimal("0.15"));  // 15% for service products
            productRates.put("Training", new BigDecimal("0.20")); // 20% for training products
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            if (deal.getStatus() != DealStatus.WON) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalCommission = BigDecimal.ZERO;

            for (DealProduct product : deal.getProducts()) {
                String productType = getProductType(product.getProductName());
                BigDecimal rate = productRates.getOrDefault(productType, new BigDecimal("0.05")); // Default 5%
                BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                totalCommission = totalCommission.add(productValue.multiply(rate));
            }

            return totalCommission;
        }

        private String getProductType(String productName) {
            // In a real implementation, this would determine the product type based on the product name or ID
            if (productName.toLowerCase().contains("hardware")) return "Hardware";
            if (productName.toLowerCase().contains("software")) return "Software";
            if (productName.toLowerCase().contains("service")) return "Service";
            if (productName.toLowerCase().contains("training")) return "Training";
            return "Other";
        }
    }

    /**
     * Abstraction
     * This abstract class defines the interface for the abstraction and maintains a reference to the implementor.
     */
    public static abstract class CommissionProcessor {
        protected CommissionCalculationStrategy strategy;
        protected DealService dealService;
        protected UserService userService;

        public CommissionProcessor(CommissionCalculationStrategy strategy, 
                                  DealService dealService,
                                  UserService userService) {
            this.strategy = strategy;
            this.dealService = dealService;
            this.userService = userService;
        }

        /**
         * Change the commission calculation strategy at runtime
         * @param strategy The new strategy to use
         */
        public void setStrategy(CommissionCalculationStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Process commission for a deal
         * @param dealId The ID of the deal
         * @return The commission calculation result
         */
        public abstract CommissionCalculation processCommission(String dealId);
    }

    /**
     * Refined Abstraction A
     * Implements commission processing for sales representatives
     */
    public static class SalesRepCommissionProcessor extends CommissionProcessor {

        public SalesRepCommissionProcessor(CommissionCalculationStrategy strategy, 
                                          DealService dealService,
                                          UserService userService) {
            super(strategy, dealService, userService);
        }

        @Override
        public CommissionCalculation processCommission(String dealId) {
            Deal deal = dealService.getDealById(dealId);
            User salesRep = userService.getUserById(deal.getSalesRepId());
            CommissionPlan plan = userService.getCommissionPlan(salesRep.getId());

            // Calculate commission using the strategy
            BigDecimal commissionAmount = strategy.calculateCommission(deal, salesRep, plan);

            // Create and return the commission calculation
            CommissionCalculation calculation = new CommissionCalculation(dealId, salesRep.getId(), commissionAmount);
            calculation.setCalculatedBy("SalesRepCommissionProcessor");
            calculation.setPlanId(plan.getId());

            return calculation;
        }
    }

    /**
     * Refined Abstraction B
     * Implements commission processing for sales managers with overrides
     */
    public static class ManagerCommissionProcessor extends CommissionProcessor {
        private final BigDecimal overrideRate;

        public ManagerCommissionProcessor(CommissionCalculationStrategy strategy, 
                                         DealService dealService,
                                         UserService userService,
                                         BigDecimal overrideRate) {
            super(strategy, dealService, userService);
            this.overrideRate = overrideRate;
        }

        @Override
        public CommissionCalculation processCommission(String dealId) {
            Deal deal = dealService.getDealById(dealId);
            User salesRep = userService.getUserById(deal.getSalesRepId());
            User manager = userService.getManagerForUser(salesRep.getId());
            CommissionPlan plan = userService.getCommissionPlan(manager.getId());

            // Calculate base commission using the strategy
            BigDecimal repCommission = strategy.calculateCommission(deal, salesRep, plan);

            // Apply manager override
            BigDecimal managerCommission = repCommission.multiply(overrideRate);

            // Create and return the commission calculation
            CommissionCalculation calculation = new CommissionCalculation(dealId, manager.getId(), managerCommission);
            calculation.setCalculatedBy("ManagerCommissionProcessor");
            calculation.setPlanId(plan.getId());

            return calculation;
        }
    }

    /**
     * Service interfaces for the implementation
     */
    public interface DealService {
        Deal getDealById(String dealId);
        List<Deal> getDealsBySalesRep(String salesRepId);
    }

    public interface UserService {
        User getUserById(String userId);
        CommissionPlan getCommissionPlan(String userId);
        User getManagerForUser(String userId);
    }

    /**
     * Simple implementations of the service interfaces for demonstration purposes
     */
    public static class DealServiceImpl implements DealService {
        private Map<String, Deal> deals;

        public DealServiceImpl() {
            deals = new HashMap<>();

            // Create some sample deals
            Deal deal1 = new Deal("Hardware Sale", new BigDecimal("5000"), "user1");
            deal1.setId("deal1");
            deal1.setStatus(DealStatus.WON);
            deal1.addProduct(new DealProduct("prod1", "Dell Laptop", 2, new BigDecimal("2500")));

            Deal deal2 = new Deal("Software License", new BigDecimal("15000"), "user1");
            deal2.setId("deal2");
            deal2.setStatus(DealStatus.WON);
            deal2.addProduct(new DealProduct("prod2", "Microsoft Office License", 10, new BigDecimal("1500")));

            Deal deal3 = new Deal("Consulting Services", new BigDecimal("25000"), "user2");
            deal3.setId("deal3");
            deal3.setStatus(DealStatus.WON);
            deal3.addProduct(new DealProduct("prod3", "IT Consulting Services", 1, new BigDecimal("25000")));

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

    public static class UserServiceImpl implements UserService {
        private Map<String, User> users;
        private Map<String, CommissionPlan> plans;
        private Map<String, String> managerMap; // Maps user ID to manager ID
        private Map<String, String> userPlanMap; // Maps user ID to plan ID

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
            if (planId != null) {
                return plans.get(planId);
            }
            return null;
        }

        @Override
        public User getManagerForUser(String userId) {
            String managerId = managerMap.get(userId);
            if (managerId != null) {
                return users.get(managerId);
            }
            return null;
        }
    }
}
