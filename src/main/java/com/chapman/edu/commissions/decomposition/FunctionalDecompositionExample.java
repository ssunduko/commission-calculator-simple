package com.chapman.edu.commissions.decomposition;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionRule;
import com.chapman.edu.commissions.model.CommissionTier;
import com.chapman.edu.commissions.model.BonusRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Functional Decomposition Example for Commission Calculation
 * 
 * This class demonstrates the functional decomposition approach to software design.
 * 
 * Functional Decomposition:
 * - Focuses on breaking down a system into functions or procedures
 * - Organizes code around the actions or operations that need to be performed
 * - Functions are the primary unit of organization
 * - Data is passed between functions as parameters and return values
 * - Follows a top-down approach where complex problems are broken down into simpler sub-problems
 * 
 * In this example, we decompose the commission calculation process into distinct functions,
 * each responsible for a specific part of the calculation.
 */
public class FunctionalDecompositionExample {

    /**
     * Main function to calculate commission for a deal
     * This is the top-level function that orchestrates the commission calculation process.
     * It demonstrates how functional decomposition breaks down a complex process into
     * smaller, more manageable functions.
     * @param deal The deal for which to calculate commission
     * @param plan The commission plan to apply
     * @return The calculated commission amount
     */
    public static BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
        // Step 1: Validate inputs
        if (!validateInputs(deal, plan)) {
            return BigDecimal.ZERO;
        }
        // Step 2: Calculate base commission
        BigDecimal baseCommission = calculateBaseCommission(deal, plan);
        // Step 3: Apply tiers
        BigDecimal tieredCommission = applyCommissionTiers(deal, plan, baseCommission);
        // Step 4: Calculate bonuses
        BigDecimal bonusAmount = calculateBonuses(deal, plan);
        // Step 5: Apply adjustments (e.g., caps, accelerators)
        BigDecimal adjustedCommission = applyAdjustments(tieredCommission, deal, plan);
        // Step 6: Combine all components
        BigDecimal totalCommission = adjustedCommission.add(bonusAmount);
        // Step 7: Round to two decimal places
        return totalCommission.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Validates the input deal and commission plan
     * 
     * @param deal The deal to validate
     * @param plan The commission plan to validate
     * @return True if inputs are valid, false otherwise
     */
    private static boolean validateInputs(Deal deal, CommissionPlan plan) {
        // Check if deal is null
        if (deal == null) {
            System.out.println("Deal cannot be null");
            return false;
        }
        
        // Check if plan is null
        if (plan == null) {
            System.out.println("Commission plan cannot be null");
            return false;
        }
        
        // Check if deal has products
        if (deal.getProducts() == null || deal.getProducts().isEmpty()) {
            System.out.println("Deal must have at least one product");
            return false;
        }
        
        // Check if plan is active
        if (!plan.isActiveOn(LocalDate.now())) {
            System.out.println("Commission plan is not active");
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculates the base commission for a deal
     * 
     * @param deal The deal for which to calculate base commission
     * @param plan The commission plan to apply
     * @return The base commission amount
     */
    private static BigDecimal calculateBaseCommission(Deal deal, CommissionPlan plan) {
        BigDecimal dealValue = deal.calculateTotalValue();
        BigDecimal baseRate = getBaseCommissionRate(plan);
        
        return dealValue.multiply(baseRate);
    }
    
    /**
     * Gets the base commission rate from the commission plan
     * 
     * @param plan The commission plan
     * @return The base commission rate
     */
    private static BigDecimal getBaseCommissionRate(CommissionPlan plan) {
        // In a real implementation, this would extract the base rate from the plan
        // For this example, we'll use a default rate
        return new BigDecimal("0.05"); // 5% base commission rate
    }
    
    /**
     * Applies commission tiers to adjust the base commission
     * 
     * @param deal The deal
     * @param plan The commission plan
     * @param baseCommission The base commission amount
     * @return The adjusted commission after applying tiers
     */
    private static BigDecimal applyCommissionTiers(Deal deal, CommissionPlan plan, BigDecimal baseCommission) {
        BigDecimal dealValue = deal.calculateTotalValue();
        List<CommissionTier> tiers = plan.getTiers();
        
        // If no tiers, return base commission
        if (tiers == null || tiers.isEmpty()) {
            return baseCommission;
        }
        
        // In a real implementation, this would apply the tier logic
        // For this example, we'll simulate a simple tier adjustment
        if (dealValue.compareTo(new BigDecimal("50000")) > 0) {
            return baseCommission.multiply(new BigDecimal("1.2")); // 20% increase for deals over $50,000
        }
        
        return baseCommission;
    }
    
    /**
     * Calculates bonuses for a deal
     * 
     * @param deal The deal
     * @param plan The commission plan
     * @return The total bonus amount
     */
    private static BigDecimal calculateBonuses(Deal deal, CommissionPlan plan) {
        List<BonusRule> bonusRules = plan.getBonuses();
        
        // If no bonus rules, return zero
        if (bonusRules == null || bonusRules.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalBonus = BigDecimal.ZERO;
        
        // In a real implementation, this would apply each bonus rule
        // For this example, we'll simulate a simple bonus calculation
        if (isQuarterEndDeal(deal)) {
            totalBonus = totalBonus.add(new BigDecimal("1000")); // $1000 bonus for quarter-end deals
        }
        
        if (isFirstTimeBuyer(deal)) {
            totalBonus = totalBonus.add(new BigDecimal("500")); // $500 bonus for first-time buyers
        }
        
        return totalBonus;
    }
    
    /**
     * Checks if a deal was closed at the end of a quarter
     * 
     * @param deal The deal to check
     * @return True if the deal was closed at the end of a quarter
     */
    private static boolean isQuarterEndDeal(Deal deal) {
        if (deal.getCloseDate() == null) {
            return false;
        }
        
        LocalDate closeDate = deal.getCloseDate();
        int month = closeDate.getMonthValue();
        
        // Check if month is March, June, September, or December
        return month == 3 || month == 6 || month == 9 || month == 12;
    }
    
    /**
     * Checks if a deal is for a first-time buyer
     * 
     * @param deal The deal to check
     * @return True if the deal is for a first-time buyer
     */
    private static boolean isFirstTimeBuyer(Deal deal) {
        // In a real implementation, this would check if the customer is a first-time buyer
        // For this example, we'll return a fixed value
        return false;
    }
    
    /**
     * Applies adjustments to the commission amount
     * 
     * @param commission The commission amount to adjust
     * @param deal The deal
     * @param plan The commission plan
     * @return The adjusted commission amount
     */
    private static BigDecimal applyAdjustments(BigDecimal commission, Deal deal, CommissionPlan plan) {
        // Apply cap if necessary
        BigDecimal cappedCommission = applyCap(commission, plan);
        
        // Apply accelerators if applicable
        BigDecimal acceleratedCommission = applyAccelerators(cappedCommission, deal, plan);
        
        return acceleratedCommission;
    }
    
    /**
     * Applies a cap to the commission amount if specified in the plan
     * 
     * @param commission The commission amount
     * @param plan The commission plan
     * @return The capped commission amount
     */
    private static BigDecimal applyCap(BigDecimal commission, CommissionPlan plan) {
        // In a real implementation, this would check if the plan has a cap
        // For this example, we'll use a fixed cap
        BigDecimal cap = new BigDecimal("10000");
        
        if (commission.compareTo(cap) > 0) {
            return cap;
        }
        
        return commission;
    }
    
    /**
     * Applies accelerators to the commission amount if applicable
     * 
     * @param commission The commission amount
     * @param deal The deal
     * @param plan The commission plan
     * @return The accelerated commission amount
     */
    private static BigDecimal applyAccelerators(BigDecimal commission, Deal deal, CommissionPlan plan) {
        // In a real implementation, this would check if accelerators apply
        // For this example, we'll use a simple accelerator
        if (hasExceededQuota(deal.getSalesRepId())) {
            return commission.multiply(new BigDecimal("1.1")); // 10% accelerator
        }
        
        return commission;
    }
    
    /**
     * Checks if a sales representative has exceeded their quota
     * 
     * @param salesRepId The ID of the sales representative
     * @return True if the sales representative has exceeded their quota
     */
    private static boolean hasExceededQuota(String salesRepId) {
        // In a real implementation, this would check if the sales rep has exceeded their quota
        // For this example, we'll return a fixed value
        return false;
    }
    
    /**
     * Example usage of the functional decomposition approach
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
        plan.setStatus(com.chapman.edu.commissions.model.PlanStatus.ACTIVE);
        
        // Calculate commission
        BigDecimal commission = calculateCommission(deal, plan);
        
        System.out.println("Deal: " + deal.getTitle());
        System.out.println("Deal Value: $" + deal.calculateTotalValue());
        System.out.println("Commission: $" + commission);
    }
}