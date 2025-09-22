package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Example of Procedural Cohesion.
 * 
 * Procedural Cohesion occurs when parts of a module are grouped together because they
 * follow a specified sequence of execution, where the output from one part serves as
 * input to the next part.
 * 
 * This class demonstrates procedural cohesion by grouping methods that follow a specific
 * sequence in the deal creation process. Each method performs a step in the process and
 * the output of one method is used as input to the next method.
 */
public class ProceduralCohesion {
    /**
     * Creates a new deal following a procedural sequence of steps.
     * This method demonstrates procedural cohesion by calling a sequence of methods
     * where each method's output is used as input to the next method.
     * @param title the title of the deal
     * @param salesRep the sales representative for the deal
     * @param products the products to include in the deal
     * @return the created deal
     */
    public Deal createDeal(String title, User salesRep, List<DealProduct> products) {
        // Step 1: Validate inputs
        validateInputs(title, salesRep, products);
        // Step 2: Create a basic deal
        Deal deal = createBasicDeal(title, salesRep);
        // Step 3: Add products to the deal
        addProductsToDeal(deal, products);
        // Step 4: Calculate the deal value
        calculateDealValue(deal);
        // Step 5: Set default dates
        setDefaultDates(deal);
        // Step 6: Assign a unique ID
        assignUniqueId(deal);
        // Step 7: Log the deal creation
        logDealCreation(deal);
        
        return deal;
    }
    
    /**
     * Step 1: Validates the inputs for creating a deal.
     * 
     * @param title the title of the deal
     * @param salesRep the sales representative for the deal
     * @param products the products to include in the deal
     * @throws IllegalArgumentException if any input is invalid
     */
    private void validateInputs(String title, User salesRep, List<DealProduct> products) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Deal title cannot be null or empty");
        }
        
        if (salesRep == null) {
            throw new IllegalArgumentException("Sales representative cannot be null");
        }
        
        if (!salesRep.isSalesRep()) {
            throw new IllegalArgumentException("User must have the SALES_REP role");
        }
        
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Deal must have at least one product");
        }
        
        for (DealProduct product : products) {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            if (product.getProductId() == null || product.getProductId().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            if (product.getQuantity() <= 0) {
                throw new IllegalArgumentException("Product quantity must be greater than zero");
            }
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be greater than zero");
            }
        }
        
        System.out.println("Inputs validated successfully");
    }
    
    /**
     * Step 2: Creates a basic deal with title and sales rep.
     * 
     * @param title the title of the deal
     * @param salesRep the sales representative for the deal
     * @return the created basic deal
     */
    private Deal createBasicDeal(String title, User salesRep) {
        Deal deal = new Deal();
        deal.setTitle(title);
        deal.setSalesRepId(salesRep.getId());
        deal.setStatus(DealStatus.OPEN);
        
        System.out.println("Basic deal created: " + title);
        return deal;
    }
    
    /**
     * Step 3: Adds products to the deal.
     * 
     * @param deal the deal to add products to
     * @param products the products to add
     */
    private void addProductsToDeal(Deal deal, List<DealProduct> products) {
        for (DealProduct product : products) {
            // Set the deal ID on the product
            product.setDealId(deal.getId());
            
            // Add the product to the deal
            deal.addProduct(product);
            
            System.out.println("Added product to deal: " + product.getProductName());
        }
    }
    
    /**
     * Step 4: Calculates the total value of the deal based on its products.
     * 
     * @param deal the deal to calculate the value for
     */
    private void calculateDealValue(Deal deal) {
        BigDecimal totalValue = deal.calculateTotalValue();
        deal.setValue(totalValue);
        
        System.out.println("Deal value calculated: $" + totalValue);
    }
    
    /**
     * Step 5: Sets default dates for the deal.
     * 
     * @param deal the deal to set dates for
     */
    private void setDefaultDates(Deal deal) {
        LocalDate now = LocalDate.now();
        deal.setCreatedDate(now);
        deal.setLastModifiedDate(now);
        
        System.out.println("Default dates set for deal");
    }
    
    /**
     * Step 6: Assigns a unique ID to the deal.
     * 
     * @param deal the deal to assign an ID to
     */
    private void assignUniqueId(Deal deal) {
        String uniqueId = UUID.randomUUID().toString();
        deal.setId(uniqueId);
        
        System.out.println("Unique ID assigned to deal: " + uniqueId);
    }
    
    /**
     * Step 7: Logs the deal creation.
     * 
     * @param deal the deal that was created
     */
    private void logDealCreation(Deal deal) {
        // In a real system, this would log to a database or file
        System.out.println("Deal creation logged: " + deal.getId() + " - " + deal.getTitle());
    }
}