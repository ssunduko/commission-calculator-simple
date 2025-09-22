package com.chapman.edu.commissions.decomposition;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Object-Oriented Decomposition Example for Commission Calculation
 * 
 * This class demonstrates the object-oriented decomposition approach to software design.
 * 
 * Object-Oriented Decomposition:
 * - Focuses on organizing code around objects that encapsulate data and behavior
 * - Objects are instances of classes that define their structure and behavior
 * - Emphasizes concepts like encapsulation, inheritance, and polymorphism
 * - Promotes code reuse and modularity through class hierarchies
 * - Models real-world entities and their relationships
 * 
 * In this example, we decompose the commission calculation process into a set of
 * interacting objects, each with specific responsibilities.
 */
public class ObjectOrientedDecompositionExample {

    /**
     * Main method to demonstrate the object-oriented approach
     */
    public static void main(String[] args) {
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", new BigDecimal("75000"), "REP001");
        deal.setCloseDate(LocalDate.now());
        
        // Add products to the deal
        DealProduct product1 = new DealProduct("PROD001", "Product A", 2, new BigDecimal("25000"));
        DealProduct product2 = new DealProduct("PROD002", "Product B", 1, new BigDecimal("25000"));
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        // Create a sample commission plan
        CommissionPlan plan = new CommissionPlan("Standard Plan", null);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(1));
        plan.setStatus(PlanStatus.ACTIVE);
        
        // Create a commission calculator
        CommissionCalculator calculator = new CommissionCalculator();
        
        // Calculate commission
        CommissionResult result = calculator.calculateCommission(deal, plan);
        
        // Display results
        System.out.println("Deal: " + deal.getTitle());
        System.out.println("Deal Value: $" + deal.calculateTotalValue());
        System.out.println("Base Commission: $" + result.getBaseCommission());
        System.out.println("Tier Adjustments: $" + result.getTierAdjustment());
        System.out.println("Bonuses: $" + result.getBonusAmount());
        System.out.println("Total Commission: $" + result.getTotalCommission());
    }
    
    /**
     * CommissionCalculator class responsible for calculating commissions
     * 
     * This class demonstrates encapsulation by hiding the implementation details
     * of commission calculation and exposing only the necessary interface.
     */
    public static class CommissionCalculator {
        private final List<CommissionRule> rules;
        private final List<BonusRule> bonusRules;
        
        /**
         * Constructor initializes the calculator with default rules
         */
        public CommissionCalculator() {
            this.rules = new ArrayList<>();
            this.bonusRules = new ArrayList<>();
            
            // Add default rules
            rules.add(new BaseCommissionRule());
            rules.add(new TierAdjustmentRule());
            
            // Add default bonus rules
            bonusRules.add(new QuarterEndBonusRule());
            bonusRules.add(new FirstTimeBuyerBonusRule());
        }
        
        /**
         * Calculate commission for a deal using the specified plan
         * 
         * @param deal The deal for which to calculate commission
         * @param plan The commission plan to apply
         * @return A CommissionResult object containing the calculation results
         */
        public CommissionResult calculateCommission(Deal deal, CommissionPlan plan) {
            // Validate inputs
            if (deal == null || plan == null || !plan.isActiveOn(LocalDate.now())) {
                return new CommissionResult(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
            
            // Create a context to hold calculation state
            CalculationContext context = new CalculationContext(deal, plan);
            
            // Apply all commission rules
            for (CommissionRule rule : rules) {
                rule.apply(context);
            }
            
            // Apply all bonus rules
            for (BonusRule rule : bonusRules) {
                rule.apply(context);
            }
            
            // Create and return the result
            return new CommissionResult(
                context.getBaseCommission(),
                context.getTierAdjustment(),
                context.getBonusAmount()
            );
        }
        
        /**
         * Add a custom commission rule
         * 
         * @param rule The rule to add
         */
        public void addCommissionRule(CommissionRule rule) {
            rules.add(rule);
        }
        
        /**
         * Add a custom bonus rule
         * 
         * @param rule The rule to add
         */
        public void addBonusRule(BonusRule rule) {
            bonusRules.add(rule);
        }
    }
    
    /**
     * CalculationContext class holds the state during commission calculation
     * 
     * This class demonstrates how objects can encapsulate related data and
     * provide controlled access to that data.
     */
    public static class CalculationContext {
        private final Deal deal;
        private final CommissionPlan plan;
        private BigDecimal baseCommission;
        private BigDecimal tierAdjustment;
        private BigDecimal bonusAmount;
        
        /**
         * Constructor initializes the context with a deal and plan
         * 
         * @param deal The deal being processed
         * @param plan The commission plan being applied
         */
        public CalculationContext(Deal deal, CommissionPlan plan) {
            this.deal = deal;
            this.plan = plan;
            this.baseCommission = BigDecimal.ZERO;
            this.tierAdjustment = BigDecimal.ZERO;
            this.bonusAmount = BigDecimal.ZERO;
        }
        
        // Getters and setters
        
        public Deal getDeal() {
            return deal;
        }
        
        public CommissionPlan getPlan() {
            return plan;
        }
        
        public BigDecimal getBaseCommission() {
            return baseCommission;
        }
        
        public void setBaseCommission(BigDecimal baseCommission) {
            this.baseCommission = baseCommission;
        }
        
        public BigDecimal getTierAdjustment() {
            return tierAdjustment;
        }
        
        public void setTierAdjustment(BigDecimal tierAdjustment) {
            this.tierAdjustment = tierAdjustment;
        }
        
        public BigDecimal getBonusAmount() {
            return bonusAmount;
        }
        
        public void setBonusAmount(BigDecimal bonusAmount) {
            this.bonusAmount = bonusAmount;
        }
        
        /**
         * Add to the bonus amount
         * 
         * @param amount The amount to add
         */
        public void addBonus(BigDecimal amount) {
            this.bonusAmount = this.bonusAmount.add(amount);
        }
    }
    
    /**
     * CommissionResult class represents the result of a commission calculation
     * 
     * This class demonstrates how objects can be used to group related data
     * and provide methods to operate on that data.
     */
    public static class CommissionResult {
        private final BigDecimal baseCommission;
        private final BigDecimal tierAdjustment;
        private final BigDecimal bonusAmount;
        
        /**
         * Constructor initializes the result with calculation components
         * 
         * @param baseCommission The base commission amount
         * @param tierAdjustment The tier adjustment amount
         * @param bonusAmount The bonus amount
         */
        public CommissionResult(BigDecimal baseCommission, BigDecimal tierAdjustment, BigDecimal bonusAmount) {
            this.baseCommission = baseCommission;
            this.tierAdjustment = tierAdjustment;
            this.bonusAmount = bonusAmount;
        }
        
        // Getters
        
        public BigDecimal getBaseCommission() {
            return baseCommission;
        }
        
        public BigDecimal getTierAdjustment() {
            return tierAdjustment;
        }
        
        public BigDecimal getBonusAmount() {
            return bonusAmount;
        }
        
        /**
         * Calculate the total commission
         * 
         * @return The total commission amount
         */
        public BigDecimal getTotalCommission() {
            return baseCommission.add(tierAdjustment).add(bonusAmount)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * CommissionRule interface defines the contract for commission rules
     * 
     * This interface demonstrates polymorphism, allowing different rule
     * implementations to be used interchangeably.
     */
    public interface CommissionRule {
        /**
         * Apply the rule to the calculation context
         * 
         * @param context The calculation context
         */
        void apply(CalculationContext context);
    }
    
    /**
     * BaseCommissionRule calculates the base commission amount
     * 
     * This class demonstrates how behavior can be encapsulated in a class
     * that implements an interface.
     */
    public static class BaseCommissionRule implements CommissionRule {
        @Override
        public void apply(CalculationContext context) {
            Deal deal = context.getDeal();
            BigDecimal dealValue = deal.calculateTotalValue();
            
            // Use a default base rate of 5%
            BigDecimal baseRate = new BigDecimal("0.05");
            
            BigDecimal baseCommission = dealValue.multiply(baseRate);
            context.setBaseCommission(baseCommission);
        }
    }
    
    /**
     * TierAdjustmentRule applies tier-based adjustments to the commission
     * 
     * This class demonstrates how different implementations of the same
     * interface can provide different behaviors.
     */
    public static class TierAdjustmentRule implements CommissionRule {
        @Override
        public void apply(CalculationContext context) {
            Deal deal = context.getDeal();
            BigDecimal dealValue = deal.calculateTotalValue();
            BigDecimal baseCommission = context.getBaseCommission();
            
            // Apply a simple tier adjustment for deals over $50,000
            if (dealValue.compareTo(new BigDecimal("50000")) > 0) {
                BigDecimal adjustment = baseCommission.multiply(new BigDecimal("0.2"));
                context.setTierAdjustment(adjustment);
            }
        }
    }
    
    /**
     * BonusRule interface defines the contract for bonus rules
     * 
     * This interface demonstrates how interfaces can be used to define
     * contracts for different types of rules.
     */
    public interface BonusRule {
        /**
         * Apply the bonus rule to the calculation context
         * 
         * @param context The calculation context
         */
        void apply(CalculationContext context);
    }
    
    /**
     * QuarterEndBonusRule applies a bonus for deals closed at the end of a quarter
     * 
     * This class demonstrates inheritance by implementing the BonusRule interface.
     */
    public static class QuarterEndBonusRule implements BonusRule {
        @Override
        public void apply(CalculationContext context) {
            Deal deal = context.getDeal();
            
            if (isQuarterEndDeal(deal)) {
                context.addBonus(new BigDecimal("1000"));
            }
        }
        
        /**
         * Check if a deal was closed at the end of a quarter
         * 
         * @param deal The deal to check
         * @return True if the deal was closed at the end of a quarter
         */
        private boolean isQuarterEndDeal(Deal deal) {
            if (deal.getCloseDate() == null) {
                return false;
            }
            
            LocalDate closeDate = deal.getCloseDate();
            int month = closeDate.getMonthValue();
            
            // Check if month is March, June, September, or December
            return month == 3 || month == 6 || month == 9 || month == 12;
        }
    }
    
    /**
     * FirstTimeBuyerBonusRule applies a bonus for deals with first-time buyers
     * 
     * This class demonstrates how multiple implementations of the same
     * interface can coexist and be used together.
     */
    public static class FirstTimeBuyerBonusRule implements BonusRule {
        @Override
        public void apply(CalculationContext context) {
            Deal deal = context.getDeal();
            
            if (isFirstTimeBuyer(deal)) {
                context.addBonus(new BigDecimal("500"));
            }
        }
        
        /**
         * Check if a deal is for a first-time buyer
         * 
         * @param deal The deal to check
         * @return True if the deal is for a first-time buyer
         */
        private boolean isFirstTimeBuyer(Deal deal) {
            // In a real implementation, this would check if the customer is a first-time buyer
            // For this example, we'll return a fixed value
            return false;
        }
    }
}