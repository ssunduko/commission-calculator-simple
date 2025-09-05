package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Basic Singleton implementation based on CommissionCalculation.
 * 
 * This is the simplest form of the Singleton pattern, using a private constructor
 * and a static method to access the single instance.
 */
public final class BasicSingleton {

    private static BasicSingleton instance;

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor for the BasicSingleton class.
     * This constructor initializes the singleton instance's commissionCalculation field
     * with default values including a unique ID and the identifier for the calculator.
     * This ensures that the singleton instance is configured correctly upon its creation
     * and maintains its intended behavior and data integrity throughout its lifecycle.
     */
    private BasicSingleton() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-BASIC");
        this.commissionCalculation.setCalculatedBy("BasicSingleton");
    }

    /**
     * Retrieves the single instance of the BasicSingleton class.
     * If the instance does not exist, it is created.
     *
     * Note: This implementation is not thread-safe. In a multithreaded environment,
     * multiple threads could potentially create multiple instances simultaneously.
     *
     * @return the singleton instance of BasicSingleton
     */
    public static BasicSingleton getInstance() {
        if (instance == null) {
            instance = new BasicSingleton();
        }
        return instance;
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
