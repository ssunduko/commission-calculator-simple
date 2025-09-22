package com.chapman.edu.commissions.idempotency;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class demonstrates Infrastructure Idempotency.
 * 
 * Infrastructure Idempotency is provided by the system or framework rather than
 * being implemented in the application code. Examples include:
 * - Database transactions
 * - Message queues with deduplication
 * - HTTP methods (GET, PUT, DELETE are idempotent by design)
 * - API gateways with request deduplication
 * 
 * This class simulates these infrastructure-level idempotency mechanisms.
 */
public class InfrastructureIdempotency {

    // Simulate a database with transaction support
    private static class DatabaseSimulator {
        private final Map<String, Deal> deals = new HashMap<>();
        private final Map<String, String> processedTransactions = new HashMap<>();
        
        /**
         * Simulates a database transaction with idempotency support
         * In real systems, this would be handled by the database transaction manager
         */
        public boolean executeTransaction(String transactionId, Runnable operation) {
            // Check if this transaction has already been processed
            if (processedTransactions.containsKey(transactionId)) {
                System.out.println("Transaction " + transactionId + " already processed. Skipping.");
                return false;
            }
            
            // Execute the operation
            System.out.println("Executing transaction " + transactionId);
            operation.run();
            
            // Mark transaction as processed
            processedTransactions.put(transactionId, "COMPLETED");
            return true;
        }
        
        public void saveDeal(Deal deal) {
            deals.put(deal.getId(), deal);
            System.out.println("Deal saved: " + deal.getId());
        }
        
        public Deal getDeal(String dealId) {
            return deals.get(dealId);
        }
    }
    
    // Simulate a message queue with deduplication
    private static class MessageQueueSimulator {
        private final ConcurrentHashMap<String, Boolean> processedMessages = new ConcurrentHashMap<>();
        
        /**
         * Simulates a message queue that ensures each message is processed exactly once
         * In real systems, this would be handled by the message queue service
         */
        public boolean processMessage(String messageId, Runnable messageHandler) {
            // Atomically check and update processed status
            if (processedMessages.putIfAbsent(messageId, true) != null) {
                System.out.println("Message " + messageId + " already processed. Skipping.");
                return false;
            }
            
            // Process the message
            System.out.println("Processing message " + messageId);
            messageHandler.run();
            return true;
        }
    }
    
    // Simulate an API gateway with request deduplication
    private static class ApiGatewaySimulator {
        private final Map<String, Object> responseCache = new HashMap<>();
        
        /**
         * Simulates an API gateway that caches responses for idempotent requests
         * In real systems, this would be handled by the API gateway
         */
        public Object handleRequest(String requestId, String method, Object requestBody, 
                                   java.util.function.Function<Object, Object> handler) {
            // For idempotent HTTP methods (GET, PUT, DELETE), check cache
            if (isIdempotentMethod(method)) {
                if (responseCache.containsKey(requestId)) {
                    System.out.println(method + " request " + requestId + " already processed. Returning cached response.");
                    return responseCache.get(requestId);
                }
                
                // Process the request
                System.out.println("Processing " + method + " request " + requestId);
                Object response = handler.apply(requestBody);
                
                // Cache the response
                responseCache.put(requestId, response);
                return response;
            } else {
                // For non-idempotent methods (POST), always process
                System.out.println("Processing " + method + " request " + requestId + " (non-idempotent)");
                return handler.apply(requestBody);
            }
        }
        
        private boolean isIdempotentMethod(String method) {
            return "GET".equals(method) || "PUT".equals(method) || 
                   "DELETE".equals(method) || "HEAD".equals(method);
        }
    }
    
    // Instances of our simulators
    private final DatabaseSimulator database = new DatabaseSimulator();
    private final MessageQueueSimulator messageQueue = new MessageQueueSimulator();
    private final ApiGatewaySimulator apiGateway = new ApiGatewaySimulator();
    
    /**
     * Example 1: Database transaction idempotency
     * Demonstrates how database transactions can ensure operations are only applied once
     */
    public void databaseTransactionExample(Deal deal) {
        String transactionId = UUID.randomUUID().toString();
        
        // Ensure the deal has an ID
        if (deal.getId() == null) {
            deal.setId(UUID.randomUUID().toString());
        }
        
        // First execution - should succeed
        database.executeTransaction(transactionId, () -> {
            deal.setStatus(DealStatus.WON);
            database.saveDeal(deal);
        });
        
        // Second execution with same transaction ID - should be skipped
        database.executeTransaction(transactionId, () -> {
            deal.setStatus(DealStatus.LOST); // This should not happen
            database.saveDeal(deal);
        });
        
        // Verify the deal status
        Deal savedDeal = database.getDeal(deal.getId());
        System.out.println("Deal status after transactions: " + savedDeal.getStatus());
    }
    
    /**
     * Example 2: Message queue deduplication
     * Demonstrates how message queues can ensure messages are processed exactly once
     */
    public void messageQueueExample(Deal deal) {
        String messageId = UUID.randomUUID().toString();
        
        // First message processing - should succeed
        messageQueue.processMessage(messageId, () -> {
            System.out.println("Adding product to deal: " + deal.getId());
            deal.addProduct(new DealProduct("PROD-MQ", "Message Queue Product", 1, new BigDecimal("150")));
        });
        
        // Second attempt with same message ID - should be skipped
        messageQueue.processMessage(messageId, () -> {
            System.out.println("This should not be executed");
            deal.addProduct(new DealProduct("PROD-DUP", "Duplicate Product", 1, new BigDecimal("200")));
        });
        
        // Verify products in the deal
        System.out.println("Products in deal after message processing: " + deal.getProducts().size());
        for (DealProduct product : deal.getProducts()) {
            System.out.println(" - " + product.getProductName());
        }
    }
    
    /**
     * Example 3: API gateway request deduplication
     * Demonstrates how API gateways can ensure idempotent HTTP methods are truly idempotent
     */
    public void apiGatewayExample(Deal deal) {
        String requestId = UUID.randomUUID().toString();
        
        // GET request (idempotent) - should be processed
        Deal result1 = (Deal) apiGateway.handleRequest(
            requestId, 
            "GET", 
            deal.getId(), 
            (id) -> {
                System.out.println("Retrieving deal: " + id);
                return deal;
            }
        );
        
        // Same GET request again - should return cached result
        Deal result2 = (Deal) apiGateway.handleRequest(
            requestId, 
            "GET", 
            deal.getId(), 
            (id) -> {
                System.out.println("This retrieval should not be executed");
                return null;
            }
        );
        
        // POST request (non-idempotent) - always processed
        String postRequestId = UUID.randomUUID().toString();
        apiGateway.handleRequest(
            postRequestId, 
            "POST", 
            new DealProduct("PROD-API", "API Product", 1, new BigDecimal("300")), 
            (product) -> {
                System.out.println("Creating new product: " + ((DealProduct)product).getProductName());
                deal.addProduct((DealProduct)product);
                return "Created";
            }
        );
        
        // Same POST request again - would be processed again (not idempotent)
        apiGateway.handleRequest(
            postRequestId, 
            "POST", 
            new DealProduct("PROD-API2", "Another API Product", 1, new BigDecimal("400")), 
            (product) -> {
                System.out.println("Creating another product: " + ((DealProduct)product).getProductName());
                deal.addProduct((DealProduct)product);
                return "Created";
            }
        );
    }
    
    /**
     * Main method to demonstrate infrastructure idempotency
     */
    public static void main(String[] args) {
        InfrastructureIdempotency demo = new InfrastructureIdempotency();
        
        // Create a sample deal
        Deal deal = new Deal("Infrastructure Demo Deal", new BigDecimal("2000"), "REP002");
        deal.setId(UUID.randomUUID().toString());
        
        // Demonstrate infrastructure idempotency examples
        System.out.println("=== Infrastructure Idempotency Examples ===");
        
        // Example 1: Database transactions
        System.out.println("\n1. Database Transaction Idempotency:");
        demo.databaseTransactionExample(deal);
        
        // Example 2: Message queue deduplication
        System.out.println("\n2. Message Queue Deduplication:");
        demo.messageQueueExample(deal);
        
        // Example 3: API gateway request deduplication
        System.out.println("\n3. API Gateway Request Deduplication:");
        demo.apiGatewayExample(deal);
    }
}