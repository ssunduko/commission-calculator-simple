package com.chapman.edu.commissions.patterns.creational.singleton;

import com.chapman.edu.commissions.model.CommissionCalculation;
import java.math.BigDecimal;

/**
 * Thread-Safe Singleton implementation using initialization-on-demand (Holder pattern).
 * 
 * This implementation ensures thread safety while also providing lazy initialization
 * without the need for synchronization. It uses a static inner class to hold the instance.
 */
public final class ThreadSafeSingletonInitializationOnDemand {

    private CommissionCalculation commissionCalculation;

    /**
     * Private constructor for the ThreadSafeSingletonInitializationOnDemand class.
     *
     * This constructor initializes the singleton instance with default values.
     * It assigns a new instance of {@code CommissionCalculation} to the {@code commissionCalculation} field
     * and sets its ID and creator information to indicate it belongs to the singleton.
     *
     * The singleton nature of this class ensures that this constructor is only called once
     * during the lifecycle of the application, when the singleton instance is first created.
     */
    private ThreadSafeSingletonInitializationOnDemand() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-THREAD-SAFE-HOLDER");
        this.commissionCalculation.setCalculatedBy("ThreadSafeSingletonInitializationOnDemand");
    }

    /**
     * SingletonHolder is a static nested class that holds the singleton instance of
     * the {@code ThreadSafeSingletonInitializationOnDemand} class.
     *
     * This implementation uses the Initialization-on-Demand Holder Idiom to achieve
     * thread-safe lazy initialization of the singleton instance without requiring
     * synchronized blocks or explicit synchronization logic.
     *
     * The JVM ensures that a class is loaded and initialized only when it is first accessed.
     * This guarantees thread safety during the initialization of the singleton instance.
     *
     * The SingletonHolder class is only loaded when the {@code getInstance()} method of
     * the enclosing {@code ThreadSafeSingletonInitializationOnDemand} class is invoked
     * for the first time. Subsequent calls to retrieve the singleton instance are efficient
     * because the initialization process is already complete.
     */
    private static class SingletonHolder {
        // The single instance of the class
        private static final ThreadSafeSingletonInitializationOnDemand INSTANCE = new ThreadSafeSingletonInitializationOnDemand();
    }

    /**
     * Get the singleton instance
     * 
     * This method uses the initialization-on-demand holder idiom.
     * The SingletonHolder class is only loaded when this method is called for the first time.
     * JVM guarantees thread safety during class initialization.
     * 
     * @return the singleton instance
     */
    public static ThreadSafeSingletonInitializationOnDemand getInstance() {
        return SingletonHolder.INSTANCE;
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
