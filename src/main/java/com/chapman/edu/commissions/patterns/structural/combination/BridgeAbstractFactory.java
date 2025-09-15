package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * This class demonstrates the combination of Bridge and Abstract Factory patterns.
 * 
 * Bridge Pattern: Separates an abstraction from its implementation so that both can vary independently
 * Abstract Factory Pattern: Provides an interface for creating families of related objects
 * 
 * The BridgeAbstractFactory combines these patterns by:
 * 1. Using the Bridge pattern to separate commission processors from calculation strategies
 * 2. Using the Abstract Factory pattern to create families of related objects (processors and strategies)
 */
public class BridgeAbstractFactory {

    /**
     * CommissionCalculationStrategy - The Implementor interface in the Bridge pattern
     */
    public interface CommissionCalculationStrategy {
        /**
         * Calculate commission for a deal.
         * @param deal The deal to calculate commission for
         * @param user The user (sales rep) associated with the deal
         * @param plan The commission plan to use
         * @return The calculated commission amount
         */
        BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan);
    }

    /**
     * FlatRateStrategy - A Concrete Implementor in the Bridge pattern
     */
    public static class FlatRateStrategy implements CommissionCalculationStrategy {
        private BigDecimal rate;

        public FlatRateStrategy(BigDecimal rate) {
            this.rate = rate;
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            return deal.getValue().multiply(rate);
        }
    }

    /**
     * TieredStrategy - A Concrete Implementor in the Bridge pattern
     */
    public static class TieredStrategy implements CommissionCalculationStrategy {
        private Map<BigDecimal, BigDecimal> tiers;

        public TieredStrategy() {
            tiers = new HashMap<>();
            // Define tiers: deal value threshold -> commission rate
            tiers.put(new BigDecimal("5000"), new BigDecimal("0.05"));   // 5% for deals up to $5,000
            tiers.put(new BigDecimal("10000"), new BigDecimal("0.07"));  // 7% for deals $5,001-$10,000
            tiers.put(new BigDecimal("25000"), new BigDecimal("0.10"));  // 10% for deals $10,001-$25,000
            tiers.put(new BigDecimal("50000"), new BigDecimal("0.12"));  // 12% for deals $25,001-$50,000
            tiers.put(new BigDecimal("100000"), new BigDecimal("0.15")); // 15% for deals over $50,000
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            BigDecimal dealValue = deal.getValue();
            BigDecimal rate = new BigDecimal("0.03"); // Default rate

            // Find the appropriate tier
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
     * ProductBasedStrategy - A Concrete Implementor in the Bridge pattern
     */
    public static class ProductBasedStrategy implements CommissionCalculationStrategy {
        private Map<String, BigDecimal> productRates;

        public ProductBasedStrategy() {
            productRates = new HashMap<>();
            // Define commission rates for different product types
            productRates.put("SOFTWARE", new BigDecimal("0.15"));  // 15% for software
            productRates.put("HARDWARE", new BigDecimal("0.08"));  // 8% for hardware
            productRates.put("SERVICE", new BigDecimal("0.20"));   // 20% for services
            productRates.put("SUPPORT", new BigDecimal("0.10"));   // 10% for support
        }

        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            BigDecimal totalCommission = BigDecimal.ZERO;

            // Calculate commission for each product in the deal
            deal.getProducts().forEach(product -> {
                String productType = getProductType(product.getProductName());
                BigDecimal rate = productRates.getOrDefault(productType, new BigDecimal("0.05"));
                BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                BigDecimal commission = productValue.multiply(rate);
                totalCommission.add(commission);
            });

            return totalCommission;
        }

        private String getProductType(String productName) {
            if (productName.toUpperCase().contains("LICENSE") || 
                productName.toUpperCase().contains("SOFTWARE")) {
                return "SOFTWARE";
            } else if (productName.toUpperCase().contains("HARDWARE") || 
                       productName.toUpperCase().contains("DEVICE")) {
                return "HARDWARE";
            } else if (productName.toUpperCase().contains("SERVICE") || 
                       productName.toUpperCase().contains("CONSULTING")) {
                return "SERVICE";
            } else if (productName.toUpperCase().contains("SUPPORT") || 
                       productName.toUpperCase().contains("MAINTENANCE")) {
                return "SUPPORT";
            } else {
                return "OTHER";
            }
        }
    }

    /**
     * CommissionProcessor - The Abstraction in the Bridge pattern
     */
    public abstract class CommissionProcessor {
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

        public void setStrategy(CommissionCalculationStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Process commission for a deal.
         * @param dealId The ID of the deal to process
         * @return The commission calculation result
         */
        public abstract CommissionCalculation processCommission(String dealId);
    }

    /**
     * SalesRepCommissionProcessor - A Refined Abstraction in the Bridge pattern
     */
    public class SalesRepCommissionProcessor extends CommissionProcessor {
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

            BigDecimal commissionAmount = strategy.calculateCommission(deal, salesRep, plan);

            CommissionCalculation calculation = new CommissionCalculation();
            calculation.setDealId(dealId);
            calculation.setSalesRepId(salesRep.getId());
            calculation.setBaseCommission(commissionAmount);
            calculation.setCalculationDate(LocalDate.now());

            return calculation;
        }
    }

    /**
     * ManagerCommissionProcessor - Another Refined Abstraction in the Bridge pattern
     */
    public class ManagerCommissionProcessor extends CommissionProcessor {
        private BigDecimal overrideRate;

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
            BigDecimal baseCommission = strategy.calculateCommission(deal, salesRep, plan);

            // Apply manager override rate
            BigDecimal managerCommission = baseCommission.multiply(overrideRate);

            CommissionCalculation calculation = new CommissionCalculation();
            calculation.setDealId(dealId);
            calculation.setSalesRepId(manager.getId());
            calculation.setBaseCommission(managerCommission);
            calculation.setCalculationDate(LocalDate.now());

            return calculation;
        }
    }

    /**
     * CommissionProcessorFactory - Abstract Factory interface
     * This is the Abstract Factory in the Abstract Factory pattern
     */
    public interface CommissionProcessorFactory {
        /**
         * Create a commission processor for a sales rep.
         * @return A commission processor for sales reps
         */
        CommissionProcessor createSalesRepProcessor();

        /**
         * Create a commission processor for a manager.
         * @return A commission processor for managers
         */
        CommissionProcessor createManagerProcessor();

        /**
         * Create a commission calculation strategy.
         * @return A commission calculation strategy
         */
        CommissionCalculationStrategy createCalculationStrategy();
    }

    /**
     * StandardCommissionProcessorFactory - Concrete Factory for standard commission processing
     * This is a Concrete Factory in the Abstract Factory pattern
     */
    public class StandardCommissionProcessorFactory implements CommissionProcessorFactory {
        private DealService dealService;
        private UserService userService;

        public StandardCommissionProcessorFactory(DealService dealService, UserService userService) {
            this.dealService = dealService;
            this.userService = userService;
        }

        @Override
        public CommissionProcessor createSalesRepProcessor() {
            return new SalesRepCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService
            );
        }

        @Override
        public CommissionProcessor createManagerProcessor() {
            return new ManagerCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService,
                new BigDecimal("0.25") // 25% override rate
            );
        }

        @Override
        public CommissionCalculationStrategy createCalculationStrategy() {
            return new FlatRateStrategy(new BigDecimal("0.10")); // 10% flat rate
        }
    }

    /**
     * TieredCommissionProcessorFactory - Concrete Factory for tiered commission processing
     * This is a Concrete Factory in the Abstract Factory pattern
     */
    public class TieredCommissionProcessorFactory implements CommissionProcessorFactory {
        private DealService dealService;
        private UserService userService;

        public TieredCommissionProcessorFactory(DealService dealService, UserService userService) {
            this.dealService = dealService;
            this.userService = userService;
        }

        @Override
        public CommissionProcessor createSalesRepProcessor() {
            return new SalesRepCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService
            );
        }

        @Override
        public CommissionProcessor createManagerProcessor() {
            return new ManagerCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService,
                new BigDecimal("0.20") // 20% override rate
            );
        }

        @Override
        public CommissionCalculationStrategy createCalculationStrategy() {
            return new TieredStrategy();
        }
    }

    /**
     * ProductBasedCommissionProcessorFactory - Concrete Factory for product-based commission processing
     * This is a Concrete Factory in the Abstract Factory pattern
     */
    public class ProductBasedCommissionProcessorFactory implements CommissionProcessorFactory {
        private DealService dealService;
        private UserService userService;

        public ProductBasedCommissionProcessorFactory(DealService dealService, UserService userService) {
            this.dealService = dealService;
            this.userService = userService;
        }

        @Override
        public CommissionProcessor createSalesRepProcessor() {
            return new SalesRepCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService
            );
        }

        @Override
        public CommissionProcessor createManagerProcessor() {
            return new ManagerCommissionProcessor(
                createCalculationStrategy(),
                dealService,
                userService,
                new BigDecimal("0.15") // 15% override rate
            );
        }

        @Override
        public CommissionCalculationStrategy createCalculationStrategy() {
            return new ProductBasedStrategy();
        }
    }

    /**
     * DealService interface - Used by the CommissionProcessor
     */
    public interface DealService {
        Deal getDealById(String dealId);
    }

    /**
     * UserService interface - Used by the CommissionProcessor
     */
    public interface UserService {
        User getUserById(String userId);
        CommissionPlan getCommissionPlan(String userId);
        User getManagerForUser(String userId);
    }

    /**
     * Client code that demonstrates how to use the BridgeAbstractFactory
     */
    public static void main(String[] args) {
        System.out.println("===== Bridge + Abstract Factory Pattern Combination Example =====\n");

        // Create an instance of the BridgeAbstractFactory
        BridgeAbstractFactory example = new BridgeAbstractFactory();

        // Create services (these would typically be injected)
        DealService dealService = example.new DealServiceImpl();
        UserService userService = example.new UserServiceImpl();

        // Create different factories
        CommissionProcessorFactory standardFactory = 
            example.new StandardCommissionProcessorFactory(dealService, userService);

        CommissionProcessorFactory tieredFactory = 
            example.new TieredCommissionProcessorFactory(dealService, userService);

        CommissionProcessorFactory productBasedFactory = 
            example.new ProductBasedCommissionProcessorFactory(dealService, userService);

        // Use the standard factory
        System.out.println("Using Standard Commission Processor Factory:");
        CommissionProcessor salesRepProcessor = standardFactory.createSalesRepProcessor();
        CommissionProcessor managerProcessor = standardFactory.createManagerProcessor();

        CommissionCalculation salesRepCalc = salesRepProcessor.processCommission("deal-1");
        CommissionCalculation managerCalc = managerProcessor.processCommission("deal-1");

        System.out.println("Sales Rep Commission: $" + salesRepCalc.getBaseCommission());
        System.out.println("Manager Commission: $" + managerCalc.getBaseCommission());

        // Use the tiered factory
        System.out.println("\nUsing Tiered Commission Processor Factory:");
        salesRepProcessor = tieredFactory.createSalesRepProcessor();
        managerProcessor = tieredFactory.createManagerProcessor();

        salesRepCalc = salesRepProcessor.processCommission("deal-1");
        managerCalc = managerProcessor.processCommission("deal-1");

        System.out.println("Sales Rep Commission: $" + salesRepCalc.getBaseCommission());
        System.out.println("Manager Commission: $" + managerCalc.getBaseCommission());

        // Use the product-based factory
        System.out.println("\nUsing Product-Based Commission Processor Factory:");
        salesRepProcessor = productBasedFactory.createSalesRepProcessor();
        managerProcessor = productBasedFactory.createManagerProcessor();

        salesRepCalc = salesRepProcessor.processCommission("deal-1");
        managerCalc = managerProcessor.processCommission("deal-1");

        System.out.println("Sales Rep Commission: $" + salesRepCalc.getBaseCommission());
        System.out.println("Manager Commission: $" + managerCalc.getBaseCommission());

        System.out.println("\nBenefits of combining Bridge and Abstract Factory patterns:");
        System.out.println("1. Separation of abstraction from implementation (Bridge)");
        System.out.println("2. Creation of families of related objects (Abstract Factory)");
        System.out.println("3. Ability to switch between different implementations at runtime");
        System.out.println("4. Extensibility: new processors and strategies can be added independently");
    }

    /**
     * DealServiceImpl - Implementation of DealService
     */
    public class DealServiceImpl implements DealService {
        private Map<String, Deal> deals;

        public DealServiceImpl() {
            deals = new HashMap<>();

            // Create a sample deal
            Deal deal1 = new Deal();
            deal1.setId("deal-1");
            deal1.setTitle("Enterprise Software Package");
            deal1.setValue(new BigDecimal("10000.00"));
            deal1.setSalesRepId("user-1");

            deals.put(deal1.getId(), deal1);
        }

        @Override
        public Deal getDealById(String dealId) {
            return deals.get(dealId);
        }
    }

    /**
     * UserServiceImpl - Implementation of UserService
     */
    public class UserServiceImpl implements UserService {
        private Map<String, User> users;
        private Map<String, CommissionPlan> plans;
        private Map<String, String> managerRelationships;

        public UserServiceImpl() {
            users = new HashMap<>();
            plans = new HashMap<>();
            managerRelationships = new HashMap<>();

            // Create sample users
            User salesRep = new User();
            salesRep.setId("user-1");
            salesRep.setFirstName("John");
            salesRep.setLastName("Doe");
            salesRep.addRole(UserRole.SALES_REP);

            User manager = new User();
            manager.setId("user-2");
            manager.setFirstName("Jane");
            manager.setLastName("Smith");
            manager.addRole(UserRole.SALES_MANAGER);

            users.put(salesRep.getId(), salesRep);
            users.put(manager.getId(), manager);

            // Set up manager relationship
            managerRelationships.put(salesRep.getId(), manager.getId());

            // Create commission plans
            CommissionPlan salesRepPlan = new CommissionPlan();
            salesRepPlan.setId("plan-1");
            salesRepPlan.setName("Standard Sales Rep Plan");

            CommissionPlan managerPlan = new CommissionPlan();
            managerPlan.setId("plan-2");
            managerPlan.setName("Standard Manager Plan");

            plans.put(salesRep.getId(), salesRepPlan);
            plans.put(manager.getId(), managerPlan);
        }

        @Override
        public User getUserById(String userId) {
            return users.get(userId);
        }

        @Override
        public CommissionPlan getCommissionPlan(String userId) {
            return plans.get(userId);
        }

        @Override
        public User getManagerForUser(String userId) {
            String managerId = managerRelationships.get(userId);
            return users.get(managerId);
        }
    }
}
