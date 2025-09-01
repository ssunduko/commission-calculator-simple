package com.chapman.edu.commissions.principles.oop.polymorphism;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the CommissionCalculator interface for performance-based commissions.
 * It demonstrates polymorphism by:
 * 1. Implementing the CommissionCalculator interface
 * 2. Providing a specific implementation for calculating performance-based commissions
 */
public class PerformanceCommissionCalculator implements CommissionCalculator {
    
    private BigDecimal baseRate;
    private Map<String, PerformanceLevel> salesRepPerformance;
    
    /**
     * Constructor with base rate
     */
    public PerformanceCommissionCalculator(BigDecimal baseRate) {
        this.baseRate = baseRate;
        this.salesRepPerformance = new HashMap<>();
    }
    
    /**
     * Set the performance level for a sales representative
     * 
     * @param salesRepId The ID of the sales representative
     * @param level The performance level (BRONZE, SILVER, GOLD, PLATINUM)
     */
    public void setSalesRepPerformance(String salesRepId, PerformanceLevel level) {
        salesRepPerformance.put(salesRepId, level);
    }
    
    /**
     * Calculate the commission amount for a given deal value.
     * For performance-based commission, the rate depends on the sales rep's performance level.
     * 
     * @param dealValue The total value of the deal
     * @param salesRepId The ID of the sales representative
     * @return The calculated commission amount
     */
    public BigDecimal calculateCommission(BigDecimal dealValue, String salesRepId) {
        PerformanceLevel level = salesRepPerformance.getOrDefault(salesRepId, PerformanceLevel.BRONZE);
        BigDecimal adjustedRate = baseRate.multiply(level.getMultiplier());
        
        return dealValue.multiply(adjustedRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
    }
    
    /**
     * Calculate the commission amount for a given deal value.
     * This overloaded method is required by the interface but needs a salesRepId to work properly.
     * If called without a salesRepId, it assumes BRONZE level.
     * 
     * @param dealValue The total value of the deal
     * @return The calculated commission amount at BRONZE level
     */
    @Override
    public BigDecimal calculateCommission(BigDecimal dealValue) {
        // Default to BRONZE level if no sales rep ID is provided
        return calculateCommission(dealValue, "UNKNOWN");
    }
    
    /**
     * Get the commission rate as a percentage.
     * For performance-based commission, this returns the base rate.
     * 
     * @return The base commission rate
     */
    @Override
    public BigDecimal getCommissionRate() {
        return baseRate;
    }
    
    /**
     * Get a description of the commission calculation method.
     * 
     * @return A string describing how the commission is calculated
     */
    @Override
    public String getDescription() {
        StringBuilder description = new StringBuilder("Performance-based commission with base rate of " + 
                                                     baseRate + "% and the following multipliers:\n");
        
        for (PerformanceLevel level : PerformanceLevel.values()) {
            description.append(level.name()).append(": ")
                      .append(level.getMultiplier()).append("x (")
                      .append(baseRate.multiply(level.getMultiplier())).append("%)\n");
        }
        
        return description.toString();
    }
    
    /**
     * Check if a deal qualifies for this commission calculation.
     * For performance-based commission, all deals qualify.
     * 
     * @param dealValue The total value of the deal
     * @param salesRepId The ID of the sales representative
     * @return true if the deal qualifies, false otherwise
     */
    @Override
    public boolean qualifiesForCommission(BigDecimal dealValue, String salesRepId) {
        return true; // All deals qualify for performance-based commission
    }
    
    /**
     * Get the name of the commission calculator.
     * 
     * @return The name of the commission calculator
     */
    @Override
    public String getName() {
        return "Performance-Based Commission Calculator";
    }
    
    /**
     * Get the performance level for a sales representative
     * 
     * @param salesRepId The ID of the sales representative
     * @return The performance level, or BRONZE if not set
     */
    public PerformanceLevel getPerformanceLevel(String salesRepId) {
        return salesRepPerformance.getOrDefault(salesRepId, PerformanceLevel.BRONZE);
    }
    
    /**
     * Set the base commission rate
     * 
     * @param baseRate The new base commission rate
     */
    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }
    
    /**
     * Enum representing performance levels with associated multipliers
     */
    public enum PerformanceLevel {
        BRONZE(new BigDecimal("1.0")),
        SILVER(new BigDecimal("1.25")),
        GOLD(new BigDecimal("1.5")),
        PLATINUM(new BigDecimal("2.0"));
        
        private final BigDecimal multiplier;
        
        PerformanceLevel(BigDecimal multiplier) {
            this.multiplier = multiplier;
        }
        
        public BigDecimal getMultiplier() {
            return multiplier;
        }
    }
}