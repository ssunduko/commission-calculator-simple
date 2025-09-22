package com.chapman.edu.commissions.leaks;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class demonstrates a leaky abstraction related to the "Defensive Programming" principle.
 * 
 * Defensive Programming is a practice where code is designed to handle unexpected inputs,
 * error conditions, and potential misuse. When defensive programming is not applied,
 * abstractions can leak implementation details and assumptions, making the code fragile.
 * 
 * Common defensive programming techniques include:
 * 1. Input validation
 * 2. Precondition checking
 * 3. Immutability
 * 4. Defensive copying
 * 5. Exception handling
 */
public class DefensiveProgrammingExample {

    /**
     * This example demonstrates a non-defensive approach that leaks implementation details.
     */
    public static class NonDefensiveApproach {
        
        /**
         * A deal manager that doesn't use defensive programming techniques.
         * This class leaks implementation details and is vulnerable to misuse.
         */
        public static class DealManager {
            private List<Deal> deals;
            
            /**
             * Constructor without defensive programming.
             * Leaky abstraction: Doesn't initialize the deals list, making it vulnerable to NullPointerException.
             */
            public DealManager() {
                // Missing initialization of deals list
            }
            /**
             * Adds a deal without defensive programming.
             * Leaky abstractions:
             * 1. No null check for the deal parameter
             * 2. No validation of deal properties
             * 3. Potential NullPointerException if deals list is not initialized
             */
            public void addDeal(Deal deal) {
                // Missing null check
                if (deals == null)
                    deals = new ArrayList<>();
                deals.add(deal);
            }
            /**
             * Gets deals without defensive programming.
             * Leaky abstraction: Returns the internal list, exposing it to external modification.
             */
            public List<Deal> getDeals() {
                // Returns the internal list, allowing external code to modify it
                return deals;
            }
            
            /**
             * Calculates total value without defensive programming.
             * Leaky abstractions:
             * 1. No null check for the deals list
             * 2. No handling of null deal values
             * 3. No handling of arithmetic exceptions
             */
            public BigDecimal calculateTotalValue() {
                // Missing null check for deals list
                BigDecimal total = BigDecimal.ZERO;
                for (Deal deal : deals) {
                    // Missing null check for deal.getValue()
                    total = total.add(deal.getValue());
                }
                return total;
            }
            
            /**
             * Updates a deal without defensive programming.
             * Leaky abstractions:
             * 1. No validation of the updated deal
             * 2. No check if the deal exists
             * 3. Direct modification of the internal list
             */
            public void updateDeal(Deal updatedDeal) {
                // Missing null check
                for (int i = 0; i < deals.size(); i++) {
                    if (deals.get(i).getId().equals(updatedDeal.getId())) {
                        // Direct replacement without validation
                        deals.set(i, updatedDeal);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * This example demonstrates a defensive approach that protects implementation details.
     */
    public static class DefensiveApproach {
        
        /**
         * A deal manager that uses defensive programming techniques.
         * This class protects its implementation details and is robust against misuse.
         */
        public static class DealManager {
            private final List<Deal> deals;
            
            /**
             * Constructor with defensive programming.
             * Properly initializes the deals list to prevent NullPointerException.
             */
            public DealManager() {
                this.deals = new ArrayList<>();
            }
            
            /**
             * Adds a deal with defensive programming.
             * Defensive techniques:
             * 1. Null check for the deal parameter
             * 2. Validation of deal properties
             * 3. Defensive copying of mutable objects
             */
            public void addDeal(Deal deal) {
                // Null check
                Objects.requireNonNull(deal, "Deal cannot be null");
                
                // Validation
                if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Deal value must be positive");
                }
                
                if (deal.getTitle() == null || deal.getTitle().trim().isEmpty()) {
                    throw new IllegalArgumentException("Deal title cannot be empty");
                }
                
                // Create a defensive copy to avoid external modification
                Deal dealCopy = new Deal(deal.getTitle(), deal.getValue(), deal.getSalesRepId());
                dealCopy.setId(deal.getId());
                dealCopy.setStatus(deal.getStatus());
                dealCopy.setCloseDate(deal.getCloseDate());
                dealCopy.setCreatedDate(deal.getCreatedDate());
                dealCopy.setLastModifiedDate(deal.getLastModifiedDate());
                
                // Add the copy to the list
                deals.add(dealCopy);
                
                // Post-condition check
                assert deals.contains(dealCopy) : "Deal was not added to the list";
            }
            
            /**
             * Gets deals with defensive programming.
             * Returns an unmodifiable copy of the internal list to prevent external modification.
             */
            public List<Deal> getDeals() {
                // Return an unmodifiable copy to prevent external modification
                return Collections.unmodifiableList(new ArrayList<>(deals));
            }
            
            /**
             * Calculates total value with defensive programming.
             * Defensive techniques:
             * 1. Handles empty list case
             * 2. Handles null deal values
             * 3. Uses a try-catch block to handle arithmetic exceptions
             */
            public BigDecimal calculateTotalValue() {
                if (deals.isEmpty()) {
                    return BigDecimal.ZERO;
                }
                
                BigDecimal total = BigDecimal.ZERO;
                try {
                    for (Deal deal : deals) {
                        // Skip deals with null values
                        if (deal.getValue() != null) {
                            total = total.add(deal.getValue());
                        }
                    }
                } catch (ArithmeticException e) {
                    // Handle arithmetic exceptions (e.g., division by zero)
                    throw new RuntimeException("Error calculating total value: " + e.getMessage(), e);
                }
                
                return total;
            }
            
            /**
             * Updates a deal with defensive programming.
             * Defensive techniques:
             * 1. Null check for the updated deal
             * 2. Validation of the updated deal
             * 3. Check if the deal exists
             * 4. Defensive copying to prevent external modification
             */
            public void updateDeal(Deal updatedDeal) {
                // Null check
                Objects.requireNonNull(updatedDeal, "Updated deal cannot be null");
                Objects.requireNonNull(updatedDeal.getId(), "Deal ID cannot be null");
                
                // Validation
                if (updatedDeal.getValue() == null || updatedDeal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Deal value must be positive");
                }
                
                if (updatedDeal.getTitle() == null || updatedDeal.getTitle().trim().isEmpty()) {
                    throw new IllegalArgumentException("Deal title cannot be empty");
                }
                
                // Find the deal to update
                boolean dealFound = false;
                for (int i = 0; i < deals.size(); i++) {
                    if (deals.get(i).getId().equals(updatedDeal.getId())) {
                        // Create a defensive copy to avoid external modification
                        Deal dealCopy = new Deal(updatedDeal.getTitle(), updatedDeal.getValue(), updatedDeal.getSalesRepId());
                        dealCopy.setId(updatedDeal.getId());
                        dealCopy.setStatus(updatedDeal.getStatus());
                        dealCopy.setCloseDate(updatedDeal.getCloseDate());
                        dealCopy.setCreatedDate(updatedDeal.getCreatedDate());
                        dealCopy.setLastModifiedDate(updatedDeal.getLastModifiedDate());
                        
                        // Update the deal
                        deals.set(i, dealCopy);
                        dealFound = true;
                        break;
                    }
                }
                
                // Throw an exception if the deal was not found
                if (!dealFound) {
                    throw new IllegalArgumentException("Deal not found with ID: " + updatedDeal.getId());
                }
            }
        }
    }
    
    /**
     * Main method to demonstrate the non-defensive and defensive approaches.
     */
    public static void main(String[] args) {
        // Create sample deals
        Deal deal1 = new Deal("Standard Deal", new BigDecimal("10000"), "REP001");
        deal1.setId("DEAL-1");
        
        Deal deal2 = new Deal("Premium Deal", new BigDecimal("20000"), "REP002");
        deal2.setId("DEAL-2");
        
        // Demonstrate non-defensive approach
        System.out.println("=== Non-Defensive Approach ===");
        try {
            NonDefensiveApproach.DealManager nonDefensiveManager = new NonDefensiveApproach.DealManager();
            
            // This might cause NullPointerException if deals list is not initialized
            nonDefensiveManager.addDeal(deal1);
            nonDefensiveManager.addDeal(deal2);
            
            // This returns the internal list, allowing external modification
            List<Deal> nonDefensiveDeals = nonDefensiveManager.getDeals();
            System.out.println("Deals before modification: " + nonDefensiveDeals.size());
            
            // External code can modify the internal list
            nonDefensiveDeals.clear();
            System.out.println("Deals after external modification: " + nonDefensiveManager.getDeals().size());
            
            // This might cause NullPointerException if deals list is empty
            System.out.println("Total value: " + nonDefensiveManager.calculateTotalValue());
        } catch (Exception e) {
            System.out.println("Error in non-defensive approach: " + e.getMessage());
        }
        
        // Demonstrate defensive approach
        System.out.println("\n=== Defensive Approach ===");
        try {
            DefensiveApproach.DealManager defensiveManager = new DefensiveApproach.DealManager();
            
            // This is safe with proper validation
            defensiveManager.addDeal(deal1);
            defensiveManager.addDeal(deal2);
            
            // This returns an unmodifiable copy, preventing external modification
            List<Deal> defensiveDeals = defensiveManager.getDeals();
            System.out.println("Deals before attempted modification: " + defensiveDeals.size());
            
            try {
                // External code cannot modify the returned list
                defensiveDeals.clear();
            } catch (UnsupportedOperationException e) {
                System.out.println("Prevented external modification: " + e.getClass().getSimpleName());
            }
            
            System.out.println("Deals after attempted modification: " + defensiveManager.getDeals().size());
            
            // This is safe with proper null handling
            System.out.println("Total value: " + defensiveManager.calculateTotalValue());
            
            // Update a deal with validation
            Deal updatedDeal = new Deal("Updated Deal", new BigDecimal("15000"), "REP001");
            updatedDeal.setId("DEAL-1");
            defensiveManager.updateDeal(updatedDeal);
            
            System.out.println("Deal updated successfully");
            
            // Try to update a non-existent deal
            Deal nonExistentDeal = new Deal("Non-existent Deal", new BigDecimal("5000"), "REP003");
            nonExistentDeal.setId("DEAL-3");
            
            try {
                defensiveManager.updateDeal(nonExistentDeal);
            } catch (IllegalArgumentException e) {
                System.out.println("Properly handled non-existent deal: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error in defensive approach: " + e.getMessage());
        }
    }
}