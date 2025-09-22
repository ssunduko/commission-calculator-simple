package com.chapman.edu.commissions.leaks;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Currency;

/**
 * This class demonstrates a leaky abstraction related to the "Design For Change" principle.
 * 
 * Design For Change is a principle that suggests software should be designed to accommodate
 * future changes with minimal disruption. When this principle is violated, abstractions can leak
 * implementation details that make changes difficult.
 * 
 * In this example, we show how tightly coupling the commission calculation logic directly to
 * specific deal types creates a leaky abstraction that makes it difficult to add new deal types
 * or change the calculation logic.
 */
public class DesignForChangeExample {
    /**
     * This class represents a commission calculator that violates the "Design For Change" principle.
     * It has hardcoded logic for specific deal types, making it difficult to extend or modify.
     */
    public static class RigidCommissionCalculator {
        
        /**
         * Calculates commission for a deal based on hardcoded rules for specific deal types.
         * This is a leaky abstraction because:
         * 1. It exposes implementation details about how commissions are calculated
         * 2. Adding a new deal type requires modifying this method
         * 3. Changing calculation logic for an existing deal type affects all clients
         * @param deal The deal to calculate commission for
         * @param plan The commission plan to use
         * @return The calculated commission amount
         */
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            if (deal == null || plan == null) {
                throw new IllegalArgumentException("Deal and plan cannot be null");
            }
            // Leaky abstraction: Hardcoded logic for specific deal types
            if (deal.getTitle().contains("Standard")) {
                // Standard deal commission is 5% of deal value
                return deal.getValue().multiply(new BigDecimal("0.05"));
            } else if (deal.getTitle().contains("Premium")) {
                // Premium deal commission is 8% of deal value
                return deal.getValue().multiply(new BigDecimal("0.08"));
            } else if (deal.getTitle().contains("Enterprise")) {
                // Enterprise deal commission is 10% of deal value
                return deal.getValue().multiply(new BigDecimal("0.10"));
            } else {
                // Default commission is 3% of deal value
                return deal.getValue().multiply(new BigDecimal("0.03"));
            }
        }
    }
    
    /**
     * This interface represents a better design that follows the "Design For Change" principle.
     * It defines a contract for commission calculation strategies.
     */
    public interface CommissionStrategy {
        BigDecimal calculateCommission(Deal deal, CommissionPlan plan);
    }
    
    /**
     * Implementation of CommissionStrategy for standard deals.
     */
    public static class StandardDealCommissionStrategy implements CommissionStrategy {
        @Override
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            return deal.getValue().multiply(new BigDecimal("0.05"));
        }
    }
    
    /**
     * Implementation of CommissionStrategy for premium deals.
     */
    public static class PremiumDealCommissionStrategy implements CommissionStrategy {
        @Override
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            return deal.getValue().multiply(new BigDecimal("0.08"));
        }
    }
    
    /**
     * Implementation of CommissionStrategy for enterprise deals.
     */
    public static class EnterpriseDealCommissionStrategy implements CommissionStrategy {
        @Override
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            return deal.getValue().multiply(new BigDecimal("0.10"));
        }
    }
    
    /**
     * Default implementation of CommissionStrategy.
     */
    public static class DefaultCommissionStrategy implements CommissionStrategy {
        @Override
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            return deal.getValue().multiply(new BigDecimal("0.03"));
        }
    }
    
    /**
     * This class represents a flexible commission calculator that follows the "Design For Change" principle.
     * It uses the Strategy pattern to allow for different commission calculation strategies.
     */
    public static class FlexibleCommissionCalculator {
        private final List<CommissionStrategySelector> strategySelectors = new ArrayList<>();
        private final CommissionStrategy defaultStrategy = new DefaultCommissionStrategy();
        
        public FlexibleCommissionCalculator() {
            // Register strategy selectors
            strategySelectors.add(deal -> 
                deal.getTitle().contains("Standard") ? new StandardDealCommissionStrategy() : null);
            strategySelectors.add(deal -> 
                deal.getTitle().contains("Premium") ? new PremiumDealCommissionStrategy() : null);
            strategySelectors.add(deal -> 
                deal.getTitle().contains("Enterprise") ? new EnterpriseDealCommissionStrategy() : null);
        }
        
        /**
         * Calculates commission for a deal using the appropriate strategy.
         * This is a better abstraction because:
         * 1. It hides implementation details of how commissions are calculated
         * 2. Adding a new deal type only requires adding a new strategy and selector
         * 3. Changing calculation logic for an existing deal type only affects that strategy
         * 
         * @param deal The deal to calculate commission for
         * @param plan The commission plan to use
         * @return The calculated commission amount
         */
        public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
            if (deal == null || plan == null) {
                throw new IllegalArgumentException("Deal and plan cannot be null");
            }
            
            // Find the appropriate strategy for this deal
            for (CommissionStrategySelector selector : strategySelectors) {
                CommissionStrategy strategy = selector.selectStrategy(deal);
                if (strategy != null) {
                    return strategy.calculateCommission(deal, plan);
                }
            }
            
            // Use default strategy if no specific strategy is found
            return defaultStrategy.calculateCommission(deal, plan);
        }
        
        /**
         * Adds a new strategy selector to the calculator.
         * This allows for extending the calculator without modifying existing code.
         * 
         * @param selector The strategy selector to add
         */
        public void addStrategySelector(CommissionStrategySelector selector) {
            strategySelectors.add(selector);
        }
    }
    
    /**
     * Interface for selecting a commission strategy based on a deal.
     */
    public interface CommissionStrategySelector {
        CommissionStrategy selectStrategy(Deal deal);
    }
    
    /**
     * Main method to demonstrate the leaky abstraction and the better design.
     */
    public static void main(String[] args) {
        // Create sample deals
        Deal standardDeal = new Deal("Standard Deal", new BigDecimal("10000"), "REP001");
        Deal premiumDeal = new Deal("Premium Deal", new BigDecimal("20000"), "REP001");
        Deal enterpriseDeal = new Deal("Enterprise Deal", new BigDecimal("50000"), "REP001");
        Deal customDeal = new Deal("Custom Deal", new BigDecimal("15000"), "REP001");
        
        // Create a commission plan
        CommissionPlan plan = new CommissionPlan("Basic Plan", Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusDays(30));
        plan.setEffectiveEndDate(LocalDate.now().plusDays(30));
        
        // Calculate commissions using the rigid calculator
        RigidCommissionCalculator rigidCalculator = new RigidCommissionCalculator();
        System.out.println("=== Rigid Calculator ===");
        System.out.println("Standard Deal Commission: " + rigidCalculator.calculateCommission(standardDeal, plan));
        System.out.println("Premium Deal Commission: " + rigidCalculator.calculateCommission(premiumDeal, plan));
        System.out.println("Enterprise Deal Commission: " + rigidCalculator.calculateCommission(enterpriseDeal, plan));
        System.out.println("Custom Deal Commission: " + rigidCalculator.calculateCommission(customDeal, plan));
        
        // Calculate commissions using the flexible calculator
        FlexibleCommissionCalculator flexibleCalculator = new FlexibleCommissionCalculator();
        System.out.println("\n=== Flexible Calculator ===");
        System.out.println("Standard Deal Commission: " + flexibleCalculator.calculateCommission(standardDeal, plan));
        System.out.println("Premium Deal Commission: " + flexibleCalculator.calculateCommission(premiumDeal, plan));
        System.out.println("Enterprise Deal Commission: " + flexibleCalculator.calculateCommission(enterpriseDeal, plan));
        System.out.println("Custom Deal Commission: " + flexibleCalculator.calculateCommission(customDeal, plan));
        
        // Add a new strategy for custom deals
        flexibleCalculator.addStrategySelector(deal -> {
            if (deal.getTitle().contains("Custom")) {
                return new CommissionStrategy() {
                    @Override
                    public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
                        return deal.getValue().multiply(new BigDecimal("0.07"));
                    }
                };
            }
            return null;
        });
        
        // Calculate commissions again with the new strategy
        System.out.println("\n=== Flexible Calculator with Custom Strategy ===");
        System.out.println("Custom Deal Commission: " + flexibleCalculator.calculateCommission(customDeal, plan));
    }
}