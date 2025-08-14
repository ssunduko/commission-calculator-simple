package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates commissions for deals using a collection of commission strategies.
 * It follows the Open/Closed Principle by being open for extension (adding new strategies)
 * but closed for modification (the calculation logic doesn't need to change).
 */
public class CommissionCalculator {
    
    private final List<CommissionStrategy> commissionStrategies;
    
    /**
     * Constructor that initializes with default commission strategies.
     */
    public CommissionCalculator() {
        this.commissionStrategies = new ArrayList<>();
        
        // Register default strategies
        commissionStrategies.add(new SoftwareCommissionStrategy());
        commissionStrategies.add(new HardwareCommissionStrategy());
        commissionStrategies.add(new ServiceCommissionStrategy());
        commissionStrategies.add(new TrainingCommissionStrategy());
    }
    
    /**
     * Constructor that allows custom commission strategies.
     */
    public CommissionCalculator(List<CommissionStrategy> commissionStrategies) {
        this.commissionStrategies = new ArrayList<>(commissionStrategies);
    }
    
    /**
     * Adds a new commission strategy.
     */
    public void addCommissionStrategy(CommissionStrategy strategy) {
        commissionStrategies.add(strategy);
    }
    
    /**
     * Calculates the total commission for a deal.
     */
    public BigDecimal calculateCommission(Deal deal) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();
        
        for (DealProduct product : products) {
            // Find the appropriate strategy for this product
            for (CommissionStrategy strategy : commissionStrategies) {
                if (strategy.appliesTo(product)) {
                    BigDecimal commission = strategy.calculateCommission(product);
                    totalCommission = totalCommission.add(commission);
                    break; // Use the first applicable strategy
                }
            }
        }
        
        return totalCommission.setScale(2, RoundingMode.HALF_UP);
    }
}