package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates discounts for deals using a collection of discount strategies.
 * It follows the Open/Closed Principle by being open for extension (adding new strategies)
 * but closed for modification (the calculation logic doesn't need to change).
 */
public class DiscountCalculator {
    
    private final List<DiscountStrategy> discountStrategies;
    
    /**
     * Constructor that initializes with default discount strategies.
     */
    public DiscountCalculator() {
        this.discountStrategies = new ArrayList<>();
        
        // Register default strategies
        discountStrategies.add(new SoftwareDiscountStrategy());
        discountStrategies.add(new HardwareDiscountStrategy());
        discountStrategies.add(new ServiceDiscountStrategy());
        discountStrategies.add(new TrainingDiscountStrategy());
    }
    
    /**
     * Constructor that allows custom discount strategies.
     */
    public DiscountCalculator(List<DiscountStrategy> discountStrategies) {
        this.discountStrategies = new ArrayList<>(discountStrategies);
    }
    
    /**
     * Adds a new discount strategy.
     */
    public void addDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.add(strategy);
    }
    
    /**
     * Calculates the total discount for a deal.
     */
    public BigDecimal calculateDiscount(Deal deal) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();
        
        for (DealProduct product : products) {
            // Find the appropriate strategy for this product
            for (DiscountStrategy strategy : discountStrategies) {
                if (strategy.appliesTo(product)) {
                    BigDecimal discount = strategy.calculateDiscount(product);
                    totalDiscount = totalDiscount.add(discount);
                    break; // Use the first applicable strategy
                }
            }
        }
        
        return totalDiscount.setScale(2, RoundingMode.HALF_UP);
    }
}