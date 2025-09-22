package com.chapman.edu.commissions.orthogonality;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of Low Orthogonality.
 * 
 * Low orthogonality occurs when components are tightly coupled and changes to one component
 * affect others. This makes the code harder to maintain, test, and extend.
 * 
 * This class demonstrates low orthogonality by tightly coupling different concerns
 * (deal processing, user management, and report generation) in a single class with
 * interdependent methods that share state and implementation details.
 */
public class LowOrthogonality {
    
    // Shared state used by multiple methods
    private Deal currentDeal;
    private User currentUser;
    private BigDecimal dealValue;
    private String reportFormat = "standard"; // Could be "standard", "detailed", or "summary"
    
    /**
     * Sets the current deal and calculates its value.
     * This method has side effects, modifying the shared state.
     * 
     * @param deal the deal to set as current
     */
    public void setCurrentDeal(Deal deal) {
        if (deal == null) {
            throw new IllegalArgumentException("Deal cannot be null");
        }
        
        this.currentDeal = deal;
        
        // Calculate and store the deal value
        calculateDealValue();
    }
    
    /**
     * Sets the current user.
     * This method has side effects, modifying the shared state.
     * 
     * @param user the user to set as current
     */
    public void setCurrentUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        this.currentUser = user;
    }
    
    /**
     * Sets the report format.
     * This method has side effects, modifying the shared state.
     * 
     * @param format the report format to use
     */
    public void setReportFormat(String format) {
        if (format == null || (!format.equals("standard") && !format.equals("detailed") && !format.equals("summary"))) {
            throw new IllegalArgumentException("Invalid report format");
        }
        
        this.reportFormat = format;
    }
    
    /**
     * Calculates the value of the current deal.
     * This method has side effects, modifying the shared state.
     * It also depends on the currentDeal state being properly set.
     */
    private void calculateDealValue() {
        if (currentDeal == null) {
            throw new IllegalStateException("Current deal is not set");
        }
        
        BigDecimal totalValue = BigDecimal.ZERO;
        for (DealProduct product : currentDeal.getProducts()) {
            BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
            totalValue = totalValue.add(productValue);
        }
        
        this.dealValue = totalValue.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Applies a discount to the current deal value.
     * This method has side effects, modifying the shared state.
     * It depends on calculateDealValue() being called first.
     * 
     * @param discountPercentage the discount percentage
     * @return the discounted value
     */
    public BigDecimal applyDiscount(BigDecimal discountPercentage) {
        if (currentDeal == null) {
            throw new IllegalStateException("Current deal is not set");
        }
        
        if (dealValue == null) {
            calculateDealValue();
        }
        
        BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        return dealValue.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Gets the full name of the current user.
     * This method depends on the currentUser state being properly set.
     * 
     * @return the full name of the current user
     */
    public String getUserFullName() {
        if (currentUser == null) {
            throw new IllegalStateException("Current user is not set");
        }
        
        return currentUser.getFirstName() + " " + currentUser.getLastName();
    }
    
    /**
     * Checks if the current user is a sales representative.
     * This method depends on the currentUser state being properly set.
     * 
     * @return true if the current user is a sales representative, false otherwise
     */
    public boolean isCurrentUserSalesRep() {
        if (currentUser == null) {
            throw new IllegalStateException("Current user is not set");
        }
        
        return currentUser.isSalesRep();
    }
    
    /**
     * Generates a deal summary report.
     * This method depends on both currentDeal and currentUser being properly set,
     * and on calculateDealValue() being called first.
     * It also depends on the reportFormat state.
     * 
     * @return a summary report
     */
    public String generateDealSummary() {
        if (currentDeal == null) {
            throw new IllegalStateException("Current deal is not set");
        }
        
        if (currentUser == null) {
            throw new IllegalStateException("Current user is not set");
        }
        
        if (dealValue == null) {
            calculateDealValue();
        }
        
        StringBuilder report = new StringBuilder();
        
        if (reportFormat.equals("summary")) {
            report.append("Deal: ").append(currentDeal.getTitle())
                  .append(", Value: $").append(dealValue)
                  .append(", Rep: ").append(getUserFullName());
        } else if (reportFormat.equals("detailed")) {
            report.append("Deal Details\n");
            report.append("============\n");
            report.append("ID: ").append(currentDeal.getId()).append("\n");
            report.append("Title: ").append(currentDeal.getTitle()).append("\n");
            report.append("Value: $").append(dealValue).append("\n");
            report.append("Status: ").append(currentDeal.getStatus()).append("\n");
            report.append("Sales Rep: ").append(getUserFullName()).append("\n");
            report.append("Products: ").append(currentDeal.getProducts().size()).append("\n");
            report.append("Created: ").append(currentDeal.getCreatedDate()).append("\n");
            report.append("Last Modified: ").append(currentDeal.getLastModifiedDate()).append("\n");
            
            report.append("\nProduct Details:\n");
            for (DealProduct product : currentDeal.getProducts()) {
                report.append("- ").append(product.getProductName())
                      .append(", Qty: ").append(product.getQuantity())
                      .append(", Price: $").append(product.getPrice())
                      .append("\n");
            }
        } else {
            // Standard format
            report.append("Deal Summary\n");
            report.append("===========\n");
            report.append("Deal ID: ").append(currentDeal.getId()).append("\n");
            report.append("Title: ").append(currentDeal.getTitle()).append("\n");
            report.append("Value: $").append(dealValue).append("\n");
            report.append("Status: ").append(currentDeal.getStatus()).append("\n");
            report.append("Sales Rep: ").append(getUserFullName()).append("\n");
            report.append("Products: ").append(currentDeal.getProducts().size()).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Demonstrates how the low orthogonality design is used.
     * Note how changes to one aspect (like report format) affect other operations.
     */
    public void demonstrateLowOrthogonality() {
        // Create a deal and a user
        Deal deal = new Deal("Sample Deal", new BigDecimal("10000"), "user123");
        deal.setId("deal123");
        
        DealProduct product1 = new DealProduct("prod1", "Product 1", 2, new BigDecimal("1000"));
        DealProduct product2 = new DealProduct("prod2", "Product 2", 3, new BigDecimal("2000"));
        
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        User user = new User("jdoe", "jdoe@example.com", "John", "Doe");
        user.setId("user123");
        
        // Set up the current state
        setCurrentDeal(deal);
        setCurrentUser(user);
        
        // Generate a standard report
        String standardReport = generateDealSummary();
        
        // Change the report format and generate a new report
        setReportFormat("detailed");
        String detailedReport = generateDealSummary();
        
        // Apply a discount
        BigDecimal discountedValue = applyDiscount(new BigDecimal("10"));
        
        // Problems with this design:
        // 1. Methods depend on shared state being properly set
        // 2. Methods have side effects, modifying shared state
        // 3. The order of method calls matters (e.g., setCurrentDeal must be called before generateDealSummary)
        // 4. Changes to one aspect (like report format) affect other operations
        // 5. Testing is difficult because of the shared state and dependencies
    }
}