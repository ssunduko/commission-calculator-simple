package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * Example of Logical Cohesion.
 * 
 * Logical Cohesion occurs when parts of a module are grouped because they are logically
 * categorized as doing the same kind of function, even though they are different operations
 * that are not related by the flow of data.
 * 
 * This class demonstrates logical cohesion by grouping different validation methods
 * that perform similar functions (validation) but on different types of data and with
 * different validation rules.
 */
public class LogicalCohesion {
    
    /**
     * Validates a user object.
     * 
     * @param user the user to validate
     * @return true if the user is valid, false otherwise
     */
    public boolean validateUser(User user) {
        if (user == null) {
            return false;
        }
        // Check required fields
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return false;
        }
        // Validate email format
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }
        // Check that user has at least one role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * Validates a deal object.
     * 
     * @param deal the deal to validate
     * @return true if the deal is valid, false otherwise
     */
    public boolean validateDeal(Deal deal) {
        if (deal == null) {
            return false;
        }
        // Check required fields
        if (deal.getTitle() == null || deal.getTitle().isEmpty()) {
            return false;
        }
        if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (deal.getSalesRepId() == null || deal.getSalesRepId().isEmpty()) {
            return false;
        }
        // A deal must have at least one product
        if (deal.getProducts() == null || deal.getProducts().isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * Validates a deal product object.
     * 
     * @param product the deal product to validate
     * @return true if the deal product is valid, false otherwise
     */
    public boolean validateDealProduct(DealProduct product) {
        if (product == null) {
            return false;
        }
        // Check required fields
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            return false;
        }
        
        if (product.getProductName() == null || product.getProductName().isEmpty()) {
            return false;
        }
        
        if (product.getQuantity() <= 0) {
            return false;
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates a commission rate.
     * 
     * @param rate the commission rate to validate
     * @return true if the commission rate is valid, false otherwise
     */
    public boolean validateCommissionRate(BigDecimal rate) {
        if (rate == null) {
            return false;
        }
        
        // Commission rate must be between 0 and 1 (0% to 100%)
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates a list of deal products.
     * 
     * @param products the list of deal products to validate
     * @return true if all products are valid, false otherwise
     */
    public boolean validateDealProducts(List<DealProduct> products) {
        if (products == null || products.isEmpty()) {
            return false;
        }
        
        // Validate each product in the list
        for (DealProduct product : products) {
            if (!validateDealProduct(product)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates a deal status transition.
     * 
     * @param currentStatus the current status of the deal
     * @param newStatus the new status to transition to
     * @return true if the status transition is valid, false otherwise
     */
    public boolean validateDealStatusTransition(DealStatus currentStatus, DealStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        // Define valid transitions
        switch (currentStatus) {
            case OPEN:
                // From OPEN, can transition to WON, LOST, or CANCELLED
                return newStatus == DealStatus.WON || 
                       newStatus == DealStatus.LOST || 
                       newStatus == DealStatus.CANCELLED;
            case WON:
                // From WON, can only transition to CANCELLED
                return newStatus == DealStatus.CANCELLED;
            case LOST:
                // From LOST, can transition back to OPEN or to CANCELLED
                return newStatus == DealStatus.OPEN || 
                       newStatus == DealStatus.CANCELLED;
            case CANCELLED:
                // From CANCELLED, can only transition back to OPEN
                return newStatus == DealStatus.OPEN;
            default:
                return false;
        }
    }
}