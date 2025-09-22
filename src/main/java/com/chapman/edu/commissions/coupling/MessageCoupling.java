package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Message Coupling Example
 * 
 * Message coupling occurs when components communicate only through messages or interfaces,
 * without sharing any internal data. This is the lowest form of coupling and is highly desirable.
 * 
 * In this example, we demonstrate message coupling through the use of interfaces and events.
 */
public class MessageCoupling {
    
    /**
     * Interface for deal events
     */
    public interface DealEventListener {
        void onDealCreated(String dealId, String title);
        void onDealUpdated(String dealId, DealStatus status);
        void onDealClosed(String dealId, BigDecimal finalValue);
    }
    
    /**
     * Interface for deal operations
     */
    public interface DealOperations {
        String createDeal(String title, String salesRepId);
        void updateDealStatus(String dealId, DealStatus status);
        void closeDeal(String dealId);
    }
    
    /**
     * DealService class that implements the DealOperations interface
     */
    public static class DealService implements DealOperations {
        private List<Deal> deals = new ArrayList<>();
        private List<DealEventListener> listeners = new ArrayList<>();
        
        /**
         * Register a listener for deal events
         * 
         * @param listener The listener to register
         */
        public void registerListener(DealEventListener listener) {
            listeners.add(listener);
        }
        
        /**
         * Create a new deal
         * 
         * @param title The title of the deal
         * @param salesRepId The sales rep ID
         * @return The ID of the created deal
         */
        @Override
        public String createDeal(String title, String salesRepId) {
            Deal deal = new Deal(title, BigDecimal.ZERO, salesRepId);
            deal.setId("DEAL-" + (deals.size() + 1));
            deals.add(deal);
            
            // Notify listeners
            for (DealEventListener listener : listeners) {
                listener.onDealCreated(deal.getId(), deal.getTitle());
            }
            
            return deal.getId();
        }
        
        /**
         * Update the status of a deal
         * 
         * @param dealId The ID of the deal
         * @param status The new status
         */
        @Override
        public void updateDealStatus(String dealId, DealStatus status) {
            Deal deal = findDealById(dealId);
            if (deal != null) {
                deal.setStatus(status);
                
                // Notify listeners
                for (DealEventListener listener : listeners) {
                    listener.onDealUpdated(dealId, status);
                }
            }
        }
        
        /**
         * Close a deal
         * 
         * @param dealId The ID of the deal
         */
        @Override
        public void closeDeal(String dealId) {
            Deal deal = findDealById(dealId);
            if (deal != null) {
                deal.setStatus(DealStatus.WON);
                deal.setCloseDate(java.time.LocalDate.now());
                
                // Notify listeners
                for (DealEventListener listener : listeners) {
                    listener.onDealClosed(dealId, deal.calculateTotalValue());
                }
            }
        }
        
        /**
         * Add a product to a deal
         * 
         * @param dealId The ID of the deal
         * @param productId The ID of the product
         * @param productName The name of the product
         * @param quantity The quantity
         * @param price The price
         */
        public void addProductToDeal(String dealId, String productId, String productName, int quantity, BigDecimal price) {
            Deal deal = findDealById(dealId);
            if (deal != null) {
                DealProduct product = new DealProduct(productId, productName, quantity, price);
                deal.addProduct(product);
            }
        }
        
        /**
         * Find a deal by ID
         * 
         * @param dealId The ID of the deal
         * @return The deal, or null if not found
         */
        private Deal findDealById(String dealId) {
            return deals.stream()
                    .filter(d -> d.getId().equals(dealId))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    /**
     * CommissionService class that listens for deal events
     */
    public static class CommissionService implements DealEventListener {
        
        @Override
        public void onDealCreated(String dealId, String title) {
            System.out.println("Commission Service: New deal created - " + dealId + ": " + title);
            System.out.println("Commission Service: Initializing commission tracking for deal " + dealId);
        }
        
        @Override
        public void onDealUpdated(String dealId, DealStatus status) {
            System.out.println("Commission Service: Deal " + dealId + " updated to status: " + status);
            System.out.println("Commission Service: Updating commission projections for deal " + dealId);
        }
        
        @Override
        public void onDealClosed(String dealId, BigDecimal finalValue) {
            System.out.println("Commission Service: Deal " + dealId + " closed with final value: " + finalValue);
            
            // Calculate commission (simplified example)
            BigDecimal commission = finalValue.multiply(new BigDecimal("0.10"));
            System.out.println("Commission Service: Calculating final commission: " + commission);
            System.out.println("Commission Service: Processing commission payment...");
        }
    }
    
    /**
     * NotificationService class that also listens for deal events
     */
    public static class NotificationService implements DealEventListener {
        
        @Override
        public void onDealCreated(String dealId, String title) {
            System.out.println("Notification Service: Sending notification for new deal - " + dealId + ": " + title);
        }
        
        @Override
        public void onDealUpdated(String dealId, DealStatus status) {
            System.out.println("Notification Service: Sending notification for deal status update - " + dealId + ": " + status);
        }
        
        @Override
        public void onDealClosed(String dealId, BigDecimal finalValue) {
            System.out.println("Notification Service: Sending notification for deal closure - " + dealId + " with value: " + finalValue);
        }
    }
    
    public static void main(String[] args) {
        // Create services
        DealService dealService = new DealService();
        CommissionService commissionService = new CommissionService();
        NotificationService notificationService = new NotificationService();
        
        // Register listeners
        dealService.registerListener(commissionService);
        dealService.registerListener(notificationService);
        
        // Create a deal
        System.out.println("Creating a new deal...");
        String dealId = dealService.createDeal("Test Deal", "sales-rep-1");
        System.out.println();
        
        // Add products to the deal
        System.out.println("Adding products to the deal...");
        dealService.addProductToDeal(dealId, "prod1", "Product 1", 2, new BigDecimal("100.00"));
        dealService.addProductToDeal(dealId, "prod2", "Product 2", 1, new BigDecimal("50.00"));
        System.out.println();
        
        // Update deal status
        System.out.println("Updating deal status...");
        dealService.updateDealStatus(dealId, DealStatus.WON);
        System.out.println();
        
        // Close the deal
        System.out.println("Closing the deal...");
        dealService.closeDeal(dealId);
    }
}