package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of Sequential Cohesion.
 * 
 * Sequential Cohesion occurs when parts of a module are grouped together because the output
 * from one part serves as input to another part, creating a chain of related tasks where data
 * flows from one operation to the next.
 * 
 * This class demonstrates sequential cohesion by implementing a commission calculation pipeline
 * where the output of each step becomes the input to the next step, forming a data processing
 * chain.
 */
public class SequentialCohesion {
    /**
     * Calculates commission for a deal using a sequential pipeline.
     * This method demonstrates sequential cohesion by creating a pipeline where
     * the output of each step becomes the input to the next step.
     * @param deal the deal to calculate commission for
     * @param salesRep the sales representative who owns the deal
     * @return the calculated commission
     */
    public CommissionCalculation calculateCommission(Deal deal, User salesRep) {
        if (deal == null || salesRep == null) {
            throw new IllegalArgumentException("Deal and sales rep cannot be null");
        }
        // Step 1: Extract the relevant products from the deal
        List<DealProduct> eligibleProducts = extractEligibleProducts(deal);
        // Step 2: Calculate the base commission amount from eligible products
        BigDecimal baseCommissionAmount = calculateBaseCommissionAmount(eligibleProducts);
        // Step 3: Apply commission rate based on the sales rep's performance
        BigDecimal commissionRate = determineCommissionRate(salesRep);
        // Step 4: Calculate the gross commission by applying the rate
        BigDecimal grossCommission = applyCommissionRate(baseCommissionAmount, commissionRate);
        // Step 5: Apply any applicable bonuses
        BigDecimal bonusAmount = calculateBonusAmount(deal, grossCommission);
        // Step 6: Calculate the final commission amount
        BigDecimal finalCommissionAmount = calculateFinalCommissionAmount(grossCommission, bonusAmount);
        // Step 7: Create and return the commission calculation object
        return createCommissionCalculation(deal, salesRep, finalCommissionAmount);
    }
    
    /**
     * Step 1: Extracts the eligible products from a deal for commission calculation.
     * 
     * @param deal the deal to extract products from
     * @return a list of eligible products
     */
    private List<DealProduct> extractEligibleProducts(Deal deal) {
        List<DealProduct> eligibleProducts = new ArrayList<>();
        
        for (DealProduct product : deal.getProducts()) {
            // In a real system, there would be complex eligibility rules
            // For this example, we'll consider all products with price > 0 as eligible
            if (product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                eligibleProducts.add(product);
            }
        }
        
        System.out.println("Extracted " + eligibleProducts.size() + " eligible products");
        return eligibleProducts;
    }
    
    /**
     * Step 2: Calculates the base commission amount from eligible products.
     * 
     * @param eligibleProducts the list of eligible products
     * @return the base commission amount
     */
    private BigDecimal calculateBaseCommissionAmount(List<DealProduct> eligibleProducts) {
        BigDecimal baseAmount = BigDecimal.ZERO;
        
        for (DealProduct product : eligibleProducts) {
            // Calculate the product's contribution to the commission
            BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
            baseAmount = baseAmount.add(productValue);
        }
        
        System.out.println("Calculated base commission amount: $" + baseAmount);
        return baseAmount;
    }
    
    /**
     * Step 3: Determines the commission rate based on the sales rep's performance.
     * 
     * @param salesRep the sales representative
     * @return the commission rate
     */
    private BigDecimal determineCommissionRate(User salesRep) {
        // In a real system, this would be based on the sales rep's performance metrics,
        // territory, experience level, etc.
        // For this example, we'll use a fixed rate of 10%
        BigDecimal rate = new BigDecimal("0.10");
        
        System.out.println("Determined commission rate for " + salesRep.getFullName() + ": " + rate);
        return rate;
    }
    
    /**
     * Step 4: Applies the commission rate to the base amount.
     * 
     * @param baseAmount the base commission amount
     * @param rate the commission rate
     * @return the gross commission amount
     */
    private BigDecimal applyCommissionRate(BigDecimal baseAmount, BigDecimal rate) {
        BigDecimal grossCommission = baseAmount.multiply(rate);
        
        System.out.println("Applied commission rate: $" + grossCommission);
        return grossCommission;
    }
    
    /**
     * Step 5: Calculates any applicable bonus amount.
     * 
     * @param deal the deal
     * @param grossCommission the gross commission amount
     * @return the bonus amount
     */
    private BigDecimal calculateBonusAmount(Deal deal, BigDecimal grossCommission) {
        // In a real system, this would involve complex bonus rules
        // For this example, we'll add a 5% bonus for deals with more than 3 products
        BigDecimal bonusAmount = BigDecimal.ZERO;
        
        if (deal.getProducts().size() > 3) {
            bonusAmount = grossCommission.multiply(new BigDecimal("0.05"));
        }
        
        System.out.println("Calculated bonus amount: $" + bonusAmount);
        return bonusAmount;
    }
    
    /**
     * Step 6: Calculates the final commission amount by adding the bonus to the gross commission.
     * 
     * @param grossCommission the gross commission amount
     * @param bonusAmount the bonus amount
     * @return the final commission amount
     */
    private BigDecimal calculateFinalCommissionAmount(BigDecimal grossCommission, BigDecimal bonusAmount) {
        BigDecimal finalAmount = grossCommission.add(bonusAmount);
        
        System.out.println("Calculated final commission amount: $" + finalAmount);
        return finalAmount;
    }
    
    /**
     * Step 7: Creates a commission calculation object with the final amount.
     * 
     * @param deal the deal
     * @param salesRep the sales representative
     * @param finalAmount the final commission amount
     * @return the commission calculation object
     */
    private CommissionCalculation createCommissionCalculation(Deal deal, User salesRep, BigDecimal finalAmount) {
        CommissionCalculation calculation = new CommissionCalculation(
                deal.getId(),
                salesRep.getId(),
                finalAmount);
        
        calculation.setCalculationDate(LocalDate.now());
        calculation.setCalculatedBy("SequentialCohesion");
        
        System.out.println("Created commission calculation: " + calculation);
        return calculation;
    }
}