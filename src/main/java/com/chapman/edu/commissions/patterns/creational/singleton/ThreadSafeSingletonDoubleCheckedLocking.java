package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Thread-Safe Singleton implementation using double-checked locking.
 * 
 * This implementation ensures thread safety while minimizing the performance impact
 * by only synchronizing the critical section of the code.
 */
public final class ThreadSafeSingletonDoubleCheckedLocking {

    private static volatile ThreadSafeSingletonDoubleCheckedLocking instance;

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor for the ThreadSafeSingletonDoubleCheckedLocking class.
     *
     * This constructor initializes a default instance of the CommissionCalculation class
     * with predefined values. The unique ID and calculatedBy fields of the CommissionCalculation
     * instance are set to indicate that it is managed by the ThreadSafeSingletonDoubleCheckedLocking
     * singleton class. This ensures the proper identification of the commission calculation
     * when used within the system.
     */
    private ThreadSafeSingletonDoubleCheckedLocking() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-THREAD-SAFE-DOUBLE-CHECKED");
        this.commissionCalculation.setCalculatedBy("ThreadSafeSingletonDoubleCheckedLocking");
    }

    /**
     * Get the singleton instance using double-checked locking
     * 
     * This method uses double-checked locking to ensure thread safety
     * while minimizing the performance impact of synchronization.
     * 
     * @return the singleton instance
     */
    public static ThreadSafeSingletonDoubleCheckedLocking getInstance() {
        // First check (not synchronized)
        if (instance == null) {
            // Synchronize only if instance is null
            synchronized (ThreadSafeSingletonDoubleCheckedLocking.class) {
                // Second check (synchronized)
                if (instance == null) {
                    instance = new ThreadSafeSingletonDoubleCheckedLocking();
                }
            }
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
