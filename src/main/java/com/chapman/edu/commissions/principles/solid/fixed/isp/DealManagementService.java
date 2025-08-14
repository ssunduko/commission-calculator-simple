package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.Deal;

import java.util.List;

/**
 * This interface defines deal management operations.
 * It follows the Interface Segregation Principle by focusing only on deal management,
 * allowing clients to depend only on the methods they need.
 */
public interface DealManagementService {
    
    /**
     * Creates a new deal.
     * 
     * @param deal The deal to create
     * @return The created deal with its ID assigned
     */
    Deal createDeal(Deal deal);
    
    /**
     * Updates an existing deal.
     * 
     * @param deal The deal to update
     * @return The updated deal
     */
    Deal updateDeal(Deal deal);
    
    /**
     * Gets a deal by its ID.
     * 
     * @param dealId The ID of the deal
     * @return The deal, or null if not found
     */
    Deal getDealById(String dealId);
    
    /**
     * Gets all deals in the system.
     * 
     * @return A list of all deals
     */
    List<Deal> getAllDeals();
    
    /**
     * Gets all deals for a specific sales rep.
     * 
     * @param salesRepId The ID of the sales rep
     * @return A list of deals
     */
    List<Deal> getDealsBySalesRep(String salesRepId);
    
    /**
     * Deletes a deal.
     * 
     * @param dealId The ID of the deal to delete
     */
    void deleteDeal(String dealId);
}