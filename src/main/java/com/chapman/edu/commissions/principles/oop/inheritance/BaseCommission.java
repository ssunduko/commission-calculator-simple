package com.chapman.edu.commissions.principles.oop.inheritance;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Base class for commission calculations.
 * This class demonstrates the base class in inheritance by:
 * 1. Defining common properties and methods for all commission types
 * 2. Providing a basic implementation of commission calculation
 */
public class BaseCommission {
    // Common properties for all commission types
    protected String id;
    protected String dealId;
    protected String salesRepId;
    protected BigDecimal amount;
    protected LocalDate calculationDate;
    protected String description;
    
    /**
     * Default constructor
     */
    public BaseCommission() {
        this.calculationDate = LocalDate.now();
        this.amount = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with essential fields
     */
    public BaseCommission(String dealId, String salesRepId, BigDecimal amount) {
        this();
        this.dealId = dealId;
        this.salesRepId = salesRepId;
        this.amount = amount;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDealId() {
        return dealId;
    }
    
    public void setDealId(String dealId) {
        this.dealId = dealId;
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getCalculationDate() {
        return calculationDate;
    }
    
    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Calculate the commission amount.
     * This method can be overridden by derived classes to provide specific implementations.
     * @return the calculated commission amount
     */
    public BigDecimal calculateCommission() {
        return amount;
    }
    
    /**
     * Get a summary of the commission.
     * @return a string summary of the commission
     */
    public String getSummary() {
        return "Base Commission: " + amount + " for Deal: " + dealId;
    }
    
    @Override
    public String toString() {
        return "BaseCommission{" +
                "id='" + id + '\'' +
                ", dealId='" + dealId + '\'' +
                ", salesRepId='" + salesRepId + '\'' +
                ", amount=" + amount +
                ", calculationDate=" + calculationDate +
                '}';
    }
}