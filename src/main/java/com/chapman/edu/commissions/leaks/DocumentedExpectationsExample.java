package com.chapman.edu.commissions.leaks;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * This class demonstrates a leaky abstraction related to the "Documented Expectations" principle.
 * 
 * Documented Expectations is a principle that suggests clearly documenting the expected behavior,
 * inputs, outputs, preconditions, postconditions, and invariants of code. When expectations are
 * not properly documented, abstractions leak implementation details, making the code harder to use
 * correctly and more prone to errors.
 * 
 * Common documentation techniques include:
 * 1. Javadoc comments for classes, methods, and fields
 * 2. Clear parameter and return value descriptions
 * 3. Explicit preconditions and postconditions
 * 4. Exception documentation
 * 5. Usage examples
 */
public class DocumentedExpectationsExample {

    /**
     * This example demonstrates poorly documented code that leaks implementation details.
     */
    public static class PoorlyDocumented {
        
        /**
         * Commission calculator
         */
        public static class CommissionCalculator {
            private List<Deal> deals;
            private CommissionPlan plan;
            
            /**
             * Constructor
             */
            public CommissionCalculator(CommissionPlan plan) {
                this.plan = plan;
                this.deals = new ArrayList<>();
            }
            
            /**
             * Add deal
             */
            public void addDeal(Deal deal) {
                deals.add(deal);
            }
            
            /**
             * Calculate commission
             */
            public BigDecimal calculate() {
                BigDecimal total = BigDecimal.ZERO;
                
                // Leaky abstraction: Implementation details exposed without documentation
                for (Deal deal : deals) {
                    // Skip inactive deals
                    if (deal.getStatus() == null || deal.getStatus().toString().equals("CLOSED")) {
                        continue;
                    }
                    
                    // Skip deals outside the plan's effective date range
                    if (plan.getEffectiveStartDate() != null && 
                        deal.getCreatedDate().isBefore(plan.getEffectiveStartDate())) {
                        continue;
                    }
                    
                    if (plan.getEffectiveEndDate() != null && 
                        deal.getCreatedDate().isAfter(plan.getEffectiveEndDate())) {
                        continue;
                    }
                    
                    // Apply commission rate based on deal value
                    BigDecimal rate;
                    if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
                        rate = new BigDecimal("0.08");
                    } else {
                        rate = new BigDecimal("0.05");
                    }
                    
                    // Calculate commission
                    BigDecimal commission = deal.getValue().multiply(rate);
                    
                    // Apply accelerator for deals over $20,000
                    if (deal.getValue().compareTo(new BigDecimal("20000")) > 0) {
                        commission = commission.multiply(new BigDecimal("1.2"));
                    }
                    
                    total = total.add(commission);
                }
                
                return total;
            }
            
            /**
             * Get commission for a specific deal
             */
            public BigDecimal getCommissionForDeal(String dealId) {
                for (Deal deal : deals) {
                    if (deal.getId().equals(dealId)) {
                        // Duplicate code from calculate() method
                        // Leaky abstraction: Implementation details duplicated without documentation
                        if (deal.getStatus() == null || deal.getStatus().toString().equals("CLOSED")) {
                            return BigDecimal.ZERO;
                        }
                        
                        if (plan.getEffectiveStartDate() != null && 
                            deal.getCreatedDate().isBefore(plan.getEffectiveStartDate())) {
                            return BigDecimal.ZERO;
                        }
                        
                        if (plan.getEffectiveEndDate() != null && 
                            deal.getCreatedDate().isAfter(plan.getEffectiveEndDate())) {
                            return BigDecimal.ZERO;
                        }
                        
                        BigDecimal rate;
                        if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
                            rate = new BigDecimal("0.08");
                        } else {
                            rate = new BigDecimal("0.05");
                        }
                        
                        BigDecimal commission = deal.getValue().multiply(rate);
                        
                        if (deal.getValue().compareTo(new BigDecimal("20000")) > 0) {
                            commission = commission.multiply(new BigDecimal("1.2"));
                        }
                        
                        return commission;
                    }
                }
                
                return null;  // Leaky abstraction: Unclear what null return means
            }
        }
    }
    
    /**
     * This example demonstrates well-documented code that hides implementation details.
     */
    public static class WellDocumented {
        
        /**
         * A calculator for computing commissions based on deals and a commission plan.
         * <p>
         * This calculator applies commission rates based on deal values and the effective
         * date range of the commission plan. It also applies accelerators for high-value deals.
         * <p>
         * Usage example:
         * <pre>
         * CommissionPlan plan = new CommissionPlan("Basic Plan", Currency.getInstance("USD"));
         * plan.setEffectiveStartDate(LocalDate.now().minusDays(30));
         * plan.setEffectiveEndDate(LocalDate.now().plusDays(30));
         * 
         * CommissionCalculator calculator = new CommissionCalculator(plan);
         * calculator.addDeal(new Deal("Standard Deal", new BigDecimal("10000"), "REP001"));
         * BigDecimal commission = calculator.calculate();
         * </pre>
         */
        public static class CommissionCalculator {
            private final List<Deal> deals;
            private final CommissionPlan plan;
            
            /**
             * Creates a new commission calculator with the specified commission plan.
             * 
             * @param plan the commission plan to use for calculations (must not be null)
             * @throws IllegalArgumentException if the plan is null
             */
            public CommissionCalculator(CommissionPlan plan) {
                if (plan == null) {
                    throw new IllegalArgumentException("Commission plan cannot be null");
                }
                this.plan = plan;
                this.deals = new ArrayList<>();
            }
            
            /**
             * Adds a deal to the calculator for commission calculation.
             * <p>
             * The deal will be included in future calculations if it meets the following criteria:
             * <ul>
             *   <li>The deal is not closed</li>
             *   <li>The deal's creation date is within the plan's effective date range</li>
             * </ul>
             * 
             * @param deal the deal to add (must not be null)
             * @throws IllegalArgumentException if the deal is null
             */
            public void addDeal(Deal deal) {
                if (deal == null) {
                    throw new IllegalArgumentException("Deal cannot be null");
                }
                deals.add(deal);
            }
            
            /**
             * Calculates the total commission for all eligible deals.
             * <p>
             * A deal is eligible if:
             * <ul>
             *   <li>It is not closed</li>
             *   <li>Its creation date is within the plan's effective date range</li>
             * </ul>
             * <p>
             * Commission is calculated as follows:
             * <ul>
             *   <li>5% for deals up to $10,000</li>
             *   <li>8% for deals over $10,000</li>
             *   <li>20% accelerator bonus for deals over $20,000</li>
             * </ul>
             * 
             * @return the total commission amount (never null, may be zero)
             */
            public BigDecimal calculate() {
                BigDecimal total = BigDecimal.ZERO;
                
                for (Deal deal : deals) {
                    BigDecimal commission = calculateCommissionForDeal(deal);
                    if (commission != null) {
                        total = total.add(commission);
                    }
                }
                
                return total;
            }
            
            /**
             * Gets the commission for a specific deal identified by its ID.
             * <p>
             * The commission is calculated using the same rules as {@link #calculate()}.
             * 
             * @param dealId the ID of the deal to calculate commission for (must not be null)
             * @return the commission amount, or null if the deal is not found or not eligible
             * @throws IllegalArgumentException if dealId is null
             * @see #calculate() for commission calculation rules
             */
            public BigDecimal getCommissionForDeal(String dealId) {
                if (dealId == null) {
                    throw new IllegalArgumentException("Deal ID cannot be null");
                }
                
                for (Deal deal : deals) {
                    if (dealId.equals(deal.getId())) {
                        return calculateCommissionForDeal(deal);
                    }
                }
                
                return null;  // Deal not found
            }
            
            /**
             * Helper method to calculate commission for a single deal.
             * <p>
             * This private method encapsulates the commission calculation logic to avoid code duplication.
             * 
             * @param deal the deal to calculate commission for
             * @return the commission amount, or null if the deal is not eligible
             */
            private BigDecimal calculateCommissionForDeal(Deal deal) {
                // Check if the deal is eligible
                if (deal.getStatus() == null || deal.getStatus().toString().equals("CLOSED")) {
                    return null;  // Ineligible: deal is closed
                }
                
                if (plan.getEffectiveStartDate() != null && 
                    deal.getCreatedDate().isBefore(plan.getEffectiveStartDate())) {
                    return null;  // Ineligible: deal created before plan effective start date
                }
                
                if (plan.getEffectiveEndDate() != null && 
                    deal.getCreatedDate().isAfter(plan.getEffectiveEndDate())) {
                    return null;  // Ineligible: deal created after plan effective end date
                }
                
                // Determine commission rate based on deal value
                BigDecimal rate;
                if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
                    rate = new BigDecimal("0.08");  // 8% for deals over $10,000
                } else {
                    rate = new BigDecimal("0.05");  // 5% for deals up to $10,000
                }
                
                // Calculate base commission
                BigDecimal commission = deal.getValue().multiply(rate);
                
                // Apply accelerator for high-value deals
                if (deal.getValue().compareTo(new BigDecimal("20000")) > 0) {
                    commission = commission.multiply(new BigDecimal("1.2"));  // 20% accelerator
                }
                
                return commission;
            }
        }
    }
    
    /**
     * Main method to demonstrate the poorly documented and well-documented approaches.
     */
    public static void main(String[] args) {
        // Create a commission plan
        CommissionPlan plan = new CommissionPlan("Basic Plan", Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusDays(30));
        plan.setEffectiveEndDate(LocalDate.now().plusDays(30));
        
        // Create sample deals
        Deal deal1 = new Deal("Standard Deal", new BigDecimal("10000"), "REP001");
        deal1.setId("DEAL-1");
        deal1.setCreatedDate(LocalDate.now());
        
        Deal deal2 = new Deal("Premium Deal", new BigDecimal("20000"), "REP002");
        deal2.setId("DEAL-2");
        deal2.setCreatedDate(LocalDate.now());
        
        Deal deal3 = new Deal("Enterprise Deal", new BigDecimal("50000"), "REP003");
        deal3.setId("DEAL-3");
        deal3.setCreatedDate(LocalDate.now());
        
        // Demonstrate poorly documented approach
        System.out.println("=== Poorly Documented Approach ===");
        PoorlyDocumented.CommissionCalculator poorCalculator = new PoorlyDocumented.CommissionCalculator(plan);
        poorCalculator.addDeal(deal1);
        poorCalculator.addDeal(deal2);
        poorCalculator.addDeal(deal3);
        
        System.out.println("Total commission: " + poorCalculator.calculate());
        System.out.println("Commission for deal 1: " + poorCalculator.getCommissionForDeal("DEAL-1"));
        System.out.println("Commission for deal 2: " + poorCalculator.getCommissionForDeal("DEAL-2"));
        System.out.println("Commission for deal 3: " + poorCalculator.getCommissionForDeal("DEAL-3"));
        System.out.println("Commission for non-existent deal: " + poorCalculator.getCommissionForDeal("DEAL-4"));
        
        // Demonstrate well-documented approach
        System.out.println("\n=== Well-Documented Approach ===");
        WellDocumented.CommissionCalculator goodCalculator = new WellDocumented.CommissionCalculator(plan);
        goodCalculator.addDeal(deal1);
        goodCalculator.addDeal(deal2);
        goodCalculator.addDeal(deal3);
        
        System.out.println("Total commission: " + goodCalculator.calculate());
        System.out.println("Commission for deal 1: " + goodCalculator.getCommissionForDeal("DEAL-1"));
        System.out.println("Commission for deal 2: " + goodCalculator.getCommissionForDeal("DEAL-2"));
        System.out.println("Commission for deal 3: " + goodCalculator.getCommissionForDeal("DEAL-3"));
        System.out.println("Commission for non-existent deal: " + goodCalculator.getCommissionForDeal("DEAL-4"));
    }
}