package com.chapman.edu.commissions.idempotency;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * This class demonstrates Natural Idempotency.
 * 
 * Natural Idempotency occurs when an operation is inherently idempotent by its nature.
 * This means that applying the operation multiple times has the same effect as applying it once.
 * 
 * Examples include:
 * - Setting a value (x = 5)
 * - Setting an object to a specific state
 * - Mathematical operations like max, min, absolute value
 * - Set operations like union, intersection
 */
public class NaturalIdempotency {

    /**
     * Example 1: Setting a deal status is naturally idempotent.
     * No matter how many times we set the status to WON, the result is the same.
     */
    public void setDealStatusExample(Deal deal) {
        System.out.println("Original deal status: " + deal.getStatus());
        
        // First call to set status
        deal.setStatus(DealStatus.WON);
        System.out.println("After first status update: " + deal.getStatus());
        
        // Second call to set status - idempotent operation
        deal.setStatus(DealStatus.WON);
        System.out.println("After second status update: " + deal.getStatus());
        
        // The status remains WON regardless of how many times we set it
    }
    
    /**
     * Example 2: Mathematical operations like max are naturally idempotent.
     * Applying max(a, b) multiple times gives the same result.
     */
    public BigDecimal maxPriceExample(DealProduct product1, DealProduct product2) {
        BigDecimal maxPrice = product1.getPrice().max(product2.getPrice());
        System.out.println("Max price: " + maxPrice);
        
        // Applying max again with the same result is idempotent
        maxPrice = maxPrice.max(product1.getPrice());
        maxPrice = maxPrice.max(product2.getPrice());
        System.out.println("Max price after multiple operations: " + maxPrice);
        
        return maxPrice;
    }
    
    /**
     * Example 3: Set operations like union are naturally idempotent.
     * Adding the same element to a set multiple times has the same effect as adding it once.
     */
    public Set<String> productIdSetExample(Deal deal) {
        Set<String> productIds = new HashSet<>();
        
        // Add all product IDs to the set
        for (DealProduct product : deal.getProducts()) {
            productIds.add(product.getProductId());
        }
        System.out.println("Product IDs after first addition: " + productIds);
        
        // Add them again - idempotent operation
        for (DealProduct product : deal.getProducts()) {
            productIds.add(product.getProductId());
        }
        System.out.println("Product IDs after second addition: " + productIds);
        
        return productIds;
    }
    
    /**
     * Example 4: Absolute value is naturally idempotent.
     * abs(abs(x)) = abs(x)
     */
    public BigDecimal absoluteValueExample(BigDecimal value) {
        BigDecimal absValue = value.abs();
        System.out.println("Absolute value: " + absValue);
        
        // Applying abs again - idempotent operation
        absValue = absValue.abs();
        System.out.println("Absolute value after second operation: " + absValue);
        
        return absValue;
    }
    
    /**
     * Main method to demonstrate natural idempotency
     */
    public static void main(String[] args) {
        NaturalIdempotency demo = new NaturalIdempotency();
        
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", new BigDecimal("1000"), "REP001");
        deal.addProduct(new DealProduct("PROD1", "Product 1", 2, new BigDecimal("100")));
        deal.addProduct(new DealProduct("PROD2", "Product 2", 1, new BigDecimal("200")));
        
        // Demonstrate natural idempotency examples
        System.out.println("=== Natural Idempotency Examples ===");
        
        System.out.println("\n1. Setting Deal Status:");
        demo.setDealStatusExample(deal);
        
        System.out.println("\n2. Max Price Operation:");
        demo.maxPriceExample(deal.getProducts().get(0), deal.getProducts().get(1));
        
        System.out.println("\n3. Set Operations:");
        demo.productIdSetExample(deal);
        
        System.out.println("\n4. Absolute Value:");
        demo.absoluteValueExample(new BigDecimal("-500"));
    }
}