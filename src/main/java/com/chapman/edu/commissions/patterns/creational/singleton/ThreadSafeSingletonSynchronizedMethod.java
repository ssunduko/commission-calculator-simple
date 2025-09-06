package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Thread-Safe Singleton implementation using synchronized method.
 * 
 * This implementation ensures thread safety by synchronizing the getInstance() method.
 */
public final class ThreadSafeSingletonSynchronizedMethod {

    private static ThreadSafeSingletonSynchronizedMethod instance;

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor to initialize the singleton instance.
     *
     * This constructor sets up the internal `CommissionCalculation` object
     * with default values. The singleton instance is identified by a unique
     * ID and is marked as calculated by the ThreadSafeSingletonSynchronizedMethod class.
     *
     * This constructor is only called internally within the class to ensure
     * a single instance of the class.
     */
    private ThreadSafeSingletonSynchronizedMethod() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-THREAD-SAFE-SYNC-METHOD");
        this.commissionCalculation.setCalculatedBy("ThreadSafeSingletonSynchronizedMethod");
    }

    /**
     * Get the singleton instance
     * 
     * This method is synchronized to ensure thread safety.
     * Only one thread can execute this method at a time.
     * 
     * @return the singleton instance
     */
    public static synchronized ThreadSafeSingletonSynchronizedMethod getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingletonSynchronizedMethod();
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
