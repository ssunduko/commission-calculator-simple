package com.chapman.edu.commissions.idempotency;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class demonstrates Implementation Idempotency.
 * 
 * Implementation Idempotency is achieved by designing operations to be idempotent
 * through specific implementation techniques, even when the underlying operation
 * is not naturally idempotent.
 * 
 * Common techniques include:
 * - Using request IDs to track processed requests
 * - Conditional execution based on current state
 * - Tracking processed operations
 * - Using idempotency keys
 */
public class ImplementationIdempotency {

    // Map to track processed request IDs
    private final Map<String, Boolean> processedRequests = new HashMap<>();
    
    // Map to store the results of operations by request ID
    private final Map<String, BigDecimal> operationResults = new HashMap<>();
    
    /**
     * Example 1: Using request IDs to make a non-idempotent operation idempotent.
     * Adding a product to a deal is not naturally idempotent (calling it twice would add the product twice).
     * By tracking request IDs, we ensure the operation is only performed once.
     */
    public boolean addProductWithRequestId(Deal deal, DealProduct product, String requestId) {
        // Check if this request has already been processed
        if (processedRequests.containsKey(requestId)) {
            System.out.println("Request " + requestId + " already processed. Skipping operation.");
            return false;
        }
        
        // Process the request
        System.out.println("Processing request " + requestId + ": Adding product " + product.getProductName());
        deal.addProduct(product);
        
        // Mark the request as processed
        processedRequests.put(requestId, true);
        return true;
    }
    
    /**
     * Example 2: Conditional execution based on current state.
     * Only transition to WON status if the deal is not already in that state.
     */
    public boolean conditionalStatusUpdate(Deal deal, DealStatus newStatus) {
        // Check current state before applying the operation
        if (deal.getStatus() == newStatus) {
            System.out.println("Deal is already in " + newStatus + " status. No update needed.");
            return false;
        }
        
        // Perform the operation
        System.out.println("Updating deal status from " + deal.getStatus() + " to " + newStatus);
        deal.setStatus(newStatus);
        return true;
    }
    
    /**
     * Example 3: Caching operation results with idempotency keys.
     * Calculate commission only once for a given request ID and return cached result for subsequent calls.
     */
    public BigDecimal calculateCommissionWithIdempotencyKey(Deal deal, double commissionRate, String idempotencyKey) {
        // Check if we already calculated this commission
        if (operationResults.containsKey(idempotencyKey)) {
            System.out.println("Returning cached commission result for key: " + idempotencyKey);
            return operationResults.get(idempotencyKey);
        }
        
        // Calculate commission (potentially expensive operation)
        System.out.println("Calculating commission for deal: " + deal.getTitle());
        BigDecimal dealValue = deal.calculateTotalValue();
        BigDecimal commission = dealValue.multiply(BigDecimal.valueOf(commissionRate));
        
        // Cache the result
        operationResults.put(idempotencyKey, commission);
        return commission;
    }
    
    /**
     * Example 4: Implementing a safe update method that only updates if a condition is met.
     * This ensures that concurrent updates don't overwrite each other unexpectedly.
     */
    public boolean safeUpdateDealValue(Deal deal, BigDecimal newValue, BigDecimal expectedCurrentValue) {
        // Only update if the current value matches the expected value
        if (deal.getValue().compareTo(expectedCurrentValue) != 0) {
            System.out.println("Deal value has changed. Expected: " + expectedCurrentValue + 
                               ", Actual: " + deal.getValue() + ". Update aborted.");
            return false;
        }
        
        // Perform the update
        System.out.println("Updating deal value from " + deal.getValue() + " to " + newValue);
        deal.setValue(newValue);
        return true;
    }
    
    /**
     * Main method to demonstrate implementation idempotency
     */
    public static void main(String[] args) {
        ImplementationIdempotency demo = new ImplementationIdempotency();
        
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", new BigDecimal("1000"), "REP001");
        
        // Demonstrate implementation idempotency examples
        System.out.println("=== Implementation Idempotency Examples ===");
        
        // Example 1: Using request IDs
        System.out.println("\n1. Using Request IDs:");
        String requestId = UUID.randomUUID().toString();
        DealProduct product = new DealProduct("PROD1", "Product 1", 2, new BigDecimal("100"));
        
        // First attempt - should succeed
        demo.addProductWithRequestId(deal, product, requestId);
        
        // Second attempt with same request ID - should be skipped
        demo.addProductWithRequestId(deal, product, requestId);
        
        // New request ID - should succeed
        demo.addProductWithRequestId(deal, new DealProduct("PROD2", "Product 2", 1, new BigDecimal("200")), 
                                    UUID.randomUUID().toString());
        
        // Example 2: Conditional execution
        System.out.println("\n2. Conditional Execution:");
        deal.setStatus(DealStatus.OPEN);
        
        // First update - should succeed
        demo.conditionalStatusUpdate(deal, DealStatus.WON);
        
        // Second update to same status - should be skipped
        demo.conditionalStatusUpdate(deal, DealStatus.WON);
        
        // Example 3: Caching operation results
        System.out.println("\n3. Caching Operation Results:");
        String idempotencyKey = "commission-" + deal.getId() + "-" + System.currentTimeMillis();
        
        // First calculation - should compute
        BigDecimal commission1 = demo.calculateCommissionWithIdempotencyKey(deal, 0.1, idempotencyKey);
        System.out.println("Commission: " + commission1);
        
        // Second calculation with same key - should return cached result
        BigDecimal commission2 = demo.calculateCommissionWithIdempotencyKey(deal, 0.1, idempotencyKey);
        System.out.println("Commission (from cache): " + commission2);
        
        // Example 4: Safe updates
        System.out.println("\n4. Safe Updates:");
        BigDecimal currentValue = deal.getValue();
        
        // First update with correct expected value - should succeed
        demo.safeUpdateDealValue(deal, new BigDecimal("1200"), currentValue);
        
        // Second update with outdated expected value - should fail
        demo.safeUpdateDealValue(deal, new BigDecimal("1500"), currentValue);
    }
}