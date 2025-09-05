package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Enum Singleton implementation.
 * 
 * This implementation uses Java's enum type to create a singleton.
 * Enum singletons are considered the simplest and most effective way to implement
 * the Singleton pattern in Java.
 */
public enum EnumSingleton {

    // Eager initialization (enum constants are initialized when enum class is loaded)
    INSTANCE;

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor for the EnumSingleton.
     *
     * This constructor is invoked automatically by the JVM when the enum is loaded.
     * It initializes the CommissionCalculation instance with default values,
     * setting a unique identifier and the calculatedBy field to "EnumSingleton".
     * The initialization ensures that the singleton instance maintains
     * a consistent and predefined default state.
     */
    EnumSingleton() {
        // Initialize with default values
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-ENUM");
        this.commissionCalculation.setCalculatedBy("EnumSingleton");
    }

    /**
     * Get the commission calculation managed by this singleton
     * 
     * @return the commission calculation
     */
    public CommissionCalculation getCommissionCalculation() {
        return commissionCalculation;
    }

    /**
     * Set the commission calculation managed by this singleton
     * 
     * @param commissionCalculation the commission calculation to set
     */
    public void setCommissionCalculation(CommissionCalculation commissionCalculation) {
        this.commissionCalculation = commissionCalculation;
    }

    /**
     * Calculate commission for a deal
     * 
     * @param dealId the ID of the deal
     * @param salesRepId the ID of the sales representative
     * @param amount the base commission amount
     * @return the calculated commission
     */
    public CommissionCalculation calculateCommission(String dealId, String salesRepId, BigDecimal amount) {
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);
        calculation.recalculate();
        return calculation;
    }
}
