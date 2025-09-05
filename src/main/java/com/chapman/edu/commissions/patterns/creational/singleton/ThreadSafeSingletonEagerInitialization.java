package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Thread-Safe Singleton implementation using eager initialization.
 * 
 * This implementation ensures thread safety by initializing the instance
 * at class loading time, eliminating the need for synchronization.
 */
public final class ThreadSafeSingletonEagerInitialization {

    private static final ThreadSafeSingletonEagerInitialization INSTANCE = new ThreadSafeSingletonEagerInitialization();

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor for ThreadSafeSingletonEagerInitialization.
     *
     * This constructor initializes the singleton instance of the class with default values.
     * It sets up a {@link CommissionCalculation} object with a predefined ID and calculatedBy attributes
     * to clearly identify and document the singleton's characteristics. This ensures that the instance
     * is uniquely recognized and assigned specific properties related to its purpose.
     *
     * Note that this constructor is invoked only once during class loading when the singleton instance
     * is created. Subsequent calls to access the singleton instance do not trigger this constructor,
     * as the instance is already pre-initialized and cached.
     *
     * This approach ensures thread-safety and avoids the need for lazy initialization or explicit
     * synchronization mechanisms during runtime.
     */
    private ThreadSafeSingletonEagerInitialization() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-THREAD-SAFE-EAGER");
        this.commissionCalculation.setCalculatedBy("ThreadSafeSingletonEagerInitialization");
    }

    /**
     * Get the singleton instance
     * 
     * This method simply returns the pre-initialized instance.
     * No synchronization or null checks are needed.
     * 
     * @return the singleton instance
     */
    public static ThreadSafeSingletonEagerInitialization getInstance() {
        return INSTANCE;
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
