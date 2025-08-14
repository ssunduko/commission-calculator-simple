package com.chapman.edu.commissions.principles.solid.fixed.dip;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.util.List;

/**
 * This interface defines the contract for database operations.
 * It follows the Dependency Inversion Principle by providing an abstraction
 * that high-level modules can depend on, rather than depending on concrete implementations.
 */
public interface Database {
    
    /**
     * Gets a deal by its ID.
     * 
     * @param dealId The ID of the deal to retrieve
     * @return The deal, or null if not found
     */
    Deal getDealById(String dealId);
    
    /**
     * Gets a user by their ID.
     * 
     * @param userId The ID of the user to retrieve
     * @return The user, or null if not found
     */
    User getUserById(String userId);
    
    /**
     * Saves a commission calculation to the database.
     * 
     * @param calculation The calculation to save
     */
    void saveCommissionCalculation(CommissionCalculation calculation);
    
    /**
     * Gets commission calculations for a specific sales rep.
     * 
     * @param salesRepId The ID of the sales rep
     * @return A list of commission calculations for the sales rep
     */
    List<CommissionCalculation> getCommissionCalculationsBySalesRep(String salesRepId);
}