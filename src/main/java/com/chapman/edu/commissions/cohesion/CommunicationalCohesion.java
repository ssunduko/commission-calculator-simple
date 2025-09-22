package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Example of Communicational Cohesion.
 * 
 * Communicational Cohesion occurs when parts of a module are grouped together because they
 * operate on the same data or share the same input/output. The functions might be performing
 * different operations, but they all work on the same data structure.
 * 
 * This class demonstrates communicational cohesion by grouping different methods that all
 * operate on the same Deal object. Each method performs a different operation on the deal,
 * but they all share the same data.
 */
public class CommunicationalCohesion {
    /**
     * Analyzes a deal and performs various operations on it.
     * This method demonstrates communicational cohesion by calling different methods
     * that all operate on the same deal object.
     * 
     * @param deal the deal to analyze
     */
    public void analyzeDeal(Deal deal) {
        if (deal == null) {
            throw new IllegalArgumentException("Deal cannot be null");
        }
        // Calculate and print deal metrics
        calculateDealMetrics(deal);
        // Check if the deal is profitable
        boolean isProfitable = isDealProfitable(deal);
        System.out.println("Deal is profitable: " + isProfitable);
        // Check if the deal is at risk
        boolean isAtRisk = isDealAtRisk(deal);
        System.out.println("Deal is at risk: " + isAtRisk);
        // Generate a summary of the deal
        String summary = generateDealSummary(deal);
        System.out.println("Deal Summary: " + summary);
        // Check if the deal is eligible for special discounts
        boolean isEligibleForDiscount = isEligibleForSpecialDiscount(deal);
        System.out.println("Deal is eligible for special discount: " + isEligibleForDiscount);
        // Calculate the expected close date if not already set
        LocalDate expectedCloseDate = calculateExpectedCloseDate(deal);
        System.out.println("Expected close date: " + expectedCloseDate);
    }
    
    /**
     * Calculates various metrics for a deal.
     * 
     * @param deal the deal to calculate metrics for
     */
    private void calculateDealMetrics(Deal deal) {
        BigDecimal totalValue = deal.calculateTotalValue();
        int productCount = deal.getProducts().size();
        BigDecimal averageProductValue = BigDecimal.ZERO;
        
        if (productCount > 0) {
            averageProductValue = totalValue.divide(new BigDecimal(productCount), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        System.out.println("Deal Metrics:");
        System.out.println("- Total Value: $" + totalValue);
        System.out.println("- Product Count: " + productCount);
        System.out.println("- Average Product Value: $" + averageProductValue);
    }
    
    /**
     * Determines if a deal is profitable based on its products.
     * 
     * @param deal the deal to check
     * @return true if the deal is profitable, false otherwise
     */
    private boolean isDealProfitable(Deal deal) {
        // In a real system, this would involve complex profit margin calculations
        // For this example, we'll use a simple heuristic: deal is profitable if total value > $10,000
        return deal.calculateTotalValue().compareTo(new BigDecimal("10000")) > 0;
    }
    
    /**
     * Determines if a deal is at risk based on its status and age.
     * 
     * @param deal the deal to check
     * @return true if the deal is at risk, false otherwise
     */
    private boolean isDealAtRisk(Deal deal) {
        // Deal is at risk if it's been open for more than 90 days
        if (deal.getStatus() == DealStatus.OPEN && deal.getCreatedDate() != null) {
            LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
            return deal.getCreatedDate().isBefore(ninetyDaysAgo);
        }
        
        return false;
    }
    
    /**
     * Generates a summary of a deal.
     * 
     * @param deal the deal to summarize
     * @return a summary of the deal
     */
    private String generateDealSummary(Deal deal) {
        StringBuilder summary = new StringBuilder();
        
        summary.append(deal.getTitle())
               .append(" (").append(deal.getStatus()).append(")")
               .append(" - $").append(deal.getValue())
               .append(" - Products: ").append(deal.getProducts().size());
        
        return summary.toString();
    }
    
    /**
     * Determines if a deal is eligible for special discounts based on its products.
     * 
     * @param deal the deal to check
     * @return true if the deal is eligible for special discounts, false otherwise
     */
    private boolean isEligibleForSpecialDiscount(Deal deal) {
        List<DealProduct> products = deal.getProducts();
        
        // Check if the deal has at least 3 products
        if (products.size() < 3) {
            return false;
        }
        
        // Check if the total value is at least $5,000
        if (deal.calculateTotalValue().compareTo(new BigDecimal("5000")) < 0) {
            return false;
        }
        
        // In a real system, there would be more complex rules
        return true;
    }
    
    /**
     * Calculates the expected close date for a deal if not already set.
     * 
     * @param deal the deal to calculate the expected close date for
     * @return the expected close date
     */
    private LocalDate calculateExpectedCloseDate(Deal deal) {
        // If the deal already has a close date, return it
        if (deal.getCloseDate() != null) {
            return deal.getCloseDate();
        }
        
        // Otherwise, estimate a close date based on creation date
        // In a real system, this would involve more complex logic
        if (deal.getCreatedDate() != null) {
            return deal.getCreatedDate().plusDays(30); // Assume 30 days sales cycle
        }
        
        // If no created date, use today + 30 days
        return LocalDate.now().plusDays(30);
    }
}