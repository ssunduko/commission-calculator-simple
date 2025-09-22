package com.chapman.edu.commissions.encapsulation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class Encapsulation Example
 * 
 * This class demonstrates class encapsulation by hiding internal classes/structures.
 * Class encapsulation allows:
 * 1. Hiding implementation classes from client code
 * 2. Exposing only the necessary interfaces to the outside world
 * 3. Controlling how internal components interact
 * 4. Reducing complexity for clients by hiding internal details
 */
public class ClassEncapsulationExample {

    /**
     * The CommissionPlanManager class demonstrates class encapsulation by hiding
     * internal implementation classes and exposing only what clients need.
     */
    public static class CommissionPlanManager {
        // Internal storage for plans - hidden from clients
        private final List<CommissionPlan> plans = new ArrayList<>();
        /**
         * Creates a new commission plan.
         * The internal structure of CommissionPlan is hidden from clients.
         * 
         * @param name the name of the plan
         * @param effectiveDate the effective date of the plan
         * @return the ID of the created plan
         */
        public String createPlan(String name, LocalDate effectiveDate) {
            // Generate a unique ID
            String planId = "PLAN-" + UUID.randomUUID().toString().substring(0, 8);
            // Create a new plan using the internal CommissionPlan class
            CommissionPlan plan = new CommissionPlan(planId, name, effectiveDate);
            // Store the plan
            plans.add(plan);
            // Return only the ID to the client
            return planId;
        }
        
        /**
         * Adds a rule to a plan.
         * The internal structure of CommissionRule is hidden from clients.
         * 
         * @param planId the ID of the plan
         * @param name the name of the rule
         * @param rate the commission rate
         * @return the ID of the created rule
         */
        public String addRuleToPlan(String planId, String name, BigDecimal rate) {
            // Find the plan
            CommissionPlan plan = findPlanById(planId);
            if (plan == null) {
                throw new IllegalArgumentException("Plan not found: " + planId);
            }
            
            // Generate a unique ID for the rule
            String ruleId = "RULE-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Create a new rule using the internal CommissionRule class
            CommissionRule rule = new CommissionRule(ruleId, name, rate);
            
            // Add the rule to the plan
            plan.addRule(rule);
            
            // Return only the ID to the client
            return ruleId;
        }
        
        /**
         * Gets a summary of a plan.
         * The internal structure of CommissionPlan is hidden, and only a summary is exposed.
         * 
         * @param planId the ID of the plan
         * @return a summary of the plan
         */
        public PlanSummary getPlanSummary(String planId) {
            // Find the plan
            CommissionPlan plan = findPlanById(planId);
            if (plan == null) {
                throw new IllegalArgumentException("Plan not found: " + planId);
            }
            
            // Create and return a summary (a public-facing DTO)
            return new PlanSummary(
                plan.getId(),
                plan.getName(),
                plan.getEffectiveDate(),
                plan.getRules().size()
            );
        }
        
        /**
         * Calculates commission for a sale using a specific plan.
         * The internal calculation logic is hidden from clients.
         * 
         * @param planId the ID of the plan
         * @param saleAmount the sale amount
         * @return the calculated commission
         */
        public BigDecimal calculateCommission(String planId, BigDecimal saleAmount) {
            // Find the plan
            CommissionPlan plan = findPlanById(planId);
            if (plan == null) {
                throw new IllegalArgumentException("Plan not found: " + planId);
            }
            
            // Use the internal CommissionCalculator to calculate the commission
            return CommissionCalculator.calculate(plan, saleAmount);
        }
        
        /**
         * Finds a plan by ID.
         * This is a private helper method, hidden from clients.
         */
        private CommissionPlan findPlanById(String planId) {
            return plans.stream()
                .filter(plan -> plan.getId().equals(planId))
                .findFirst()
                .orElse(null);
        }
        
        /**
         * PlanSummary is a public Data Transfer Object (DTO) that exposes
         * only the necessary information about a CommissionPlan to clients.
         */
        public static class PlanSummary {
            private final String id;
            private final String name;
            private final LocalDate effectiveDate;
            private final int ruleCount;
            
            public PlanSummary(String id, String name, LocalDate effectiveDate, int ruleCount) {
                this.id = id;
                this.name = name;
                this.effectiveDate = effectiveDate;
                this.ruleCount = ruleCount;
            }
            
            public String getId() {
                return id;
            }
            
            public String getName() {
                return name;
            }
            
            public LocalDate getEffectiveDate() {
                return effectiveDate;
            }
            
            public int getRuleCount() {
                return ruleCount;
            }
            
            @Override
            public String toString() {
                return "Plan: " + name + " (ID: " + id + ")" +
                       ", Effective Date: " + effectiveDate +
                       ", Rules: " + ruleCount;
            }
        }
    }
    
    /**
     * CommissionPlan is a private internal class that is hidden from clients.
     * Clients interact with CommissionPlan only through the CommissionPlanManager.
     */
    private static class CommissionPlan {
        private final String id;
        private final String name;
        private final LocalDate effectiveDate;
        private final List<CommissionRule> rules;
        
        public CommissionPlan(String id, String name, LocalDate effectiveDate) {
            this.id = id;
            this.name = name;
            this.effectiveDate = effectiveDate;
            this.rules = new ArrayList<>();
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public LocalDate getEffectiveDate() {
            return effectiveDate;
        }
        
        public List<CommissionRule> getRules() {
            return rules;
        }
        
        public void addRule(CommissionRule rule) {
            rules.add(rule);
        }
    }
    
    /**
     * CommissionRule is a private internal class that is hidden from clients.
     * Clients interact with CommissionRule only through the CommissionPlanManager.
     */
    private static class CommissionRule {
        private final String id;
        private final String name;
        private final BigDecimal rate;
        
        public CommissionRule(String id, String name, BigDecimal rate) {
            this.id = id;
            this.name = name;
            this.rate = rate;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public BigDecimal getRate() {
            return rate;
        }
    }
    
    /**
     * CommissionCalculator is a private utility class that is hidden from clients.
     * It contains the internal logic for calculating commissions.
     */
    private static class CommissionCalculator {
        /**
         * Calculates commission for a sale using a specific plan.
         */
        public static BigDecimal calculate(CommissionPlan plan, BigDecimal saleAmount) {
            // For simplicity, we'll use the first rule's rate
            // In a real system, this would be more complex
            if (plan.getRules().isEmpty()) {
                return BigDecimal.ZERO;
            }
            
            CommissionRule rule = plan.getRules().get(0);
            BigDecimal rate = rule.getRate();
            
            // Convert percentage to decimal (e.g., 5% to 0.05)
            BigDecimal rateAsDecimal = rate.divide(new BigDecimal("100"));
            
            // Calculate commission
            return saleAmount.multiply(rateAsDecimal);
        }
    }
    
    /**
     * Main method to demonstrate class encapsulation
     */
    public static void main(String[] args) {
        // Create a commission plan manager
        CommissionPlanManager manager = new CommissionPlanManager();
        
        // Create a plan
        String planId = manager.createPlan("Standard Plan", LocalDate.now());
        System.out.println("Created plan with ID: " + planId);
        
        // Add rules to the plan
        String ruleId1 = manager.addRuleToPlan(planId, "Standard Commission", new BigDecimal("5.00"));
        String ruleId2 = manager.addRuleToPlan(planId, "Premium Commission", new BigDecimal("7.50"));
        System.out.println("Added rules with IDs: " + ruleId1 + ", " + ruleId2);
        
        // Get plan summary
        CommissionPlanManager.PlanSummary summary = manager.getPlanSummary(planId);
        System.out.println("Plan Summary: " + summary);
        
        // Calculate commission
        BigDecimal saleAmount = new BigDecimal("10000.00");
        BigDecimal commission = manager.calculateCommission(planId, saleAmount);
        System.out.println("Sale Amount: $" + saleAmount);
        System.out.println("Commission: $" + commission);
        
        // Note that the client code never interacts directly with:
        // - CommissionPlan
        // - CommissionRule
        // - CommissionCalculator
        // These internal classes are encapsulated and hidden from the client.
    }
}