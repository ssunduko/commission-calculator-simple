package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This class calculates taxes for deals using a collection of tax strategies.
 * It follows the Open/Closed Principle by being open for extension (adding new strategies)
 * but closed for modification (the calculation logic doesn't need to change).
 */
public class TaxCalculator {
    
    private final List<TaxStrategy> taxStrategies;
    private final TaxStrategy defaultTaxStrategy;
    
    /**
     * Constructor that initializes with default tax strategies.
     */
    public TaxCalculator() {
        this.taxStrategies = new ArrayList<>();
        
        // Register default strategies
        taxStrategies.add(new USTaxStrategy());
        taxStrategies.add(new UKTaxStrategy());
        taxStrategies.add(new CATaxStrategy());
        taxStrategies.add(new AUTaxStrategy());
        
        // Default tax strategy for countries not specifically handled
        this.defaultTaxStrategy = new TaxStrategy() {
            private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.05"); // 5% default tax
            
            @Override
            public boolean appliesTo(String countryCode) {
                return true; // This is a fallback strategy
            }
            
            @Override
            public BigDecimal calculateTax(DealProduct product) {
                BigDecimal productPrice = product.getPrice();
                int quantity = product.getQuantity();
                BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
                
                return productTotal.multiply(DEFAULT_TAX_RATE)
                        .setScale(2, RoundingMode.HALF_UP);
            }
        };
    }
    
    /**
     * Constructor that allows custom tax strategies.
     */
    public TaxCalculator(List<TaxStrategy> taxStrategies, TaxStrategy defaultTaxStrategy) {
        this.taxStrategies = new ArrayList<>(taxStrategies);
        this.defaultTaxStrategy = defaultTaxStrategy;
    }
    
    /**
     * Adds a new tax strategy.
     */
    public void addTaxStrategy(TaxStrategy strategy) {
        taxStrategies.add(strategy);
    }
    
    /**
     * Calculates the total tax for a deal in a specific country.
     */
    public BigDecimal calculateTax(Deal deal, String countryCode) {
        BigDecimal totalTax = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();
        
        for (DealProduct product : products) {
            // Find the appropriate strategy for this country
            TaxStrategy applicableStrategy = defaultTaxStrategy;
            for (TaxStrategy strategy : taxStrategies) {
                if (strategy.appliesTo(countryCode)) {
                    applicableStrategy = strategy;
                    break; // Use the first applicable strategy
                }
            }
            
            BigDecimal tax = applicableStrategy.calculateTax(product);
            totalTax = totalTax.add(tax);
        }
        
        return totalTax.setScale(2, RoundingMode.HALF_UP);
    }
}