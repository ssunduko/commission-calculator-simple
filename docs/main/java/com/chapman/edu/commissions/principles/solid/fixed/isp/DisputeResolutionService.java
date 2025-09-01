package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.Dispute;

import java.util.List;

/**
 * This interface defines dispute resolution operations.
 * It follows the Interface Segregation Principle by focusing only on dispute resolution,
 * allowing clients to depend only on the methods they need.
 */
public interface DisputeResolutionService {
    
    /**
     * Creates a new dispute.
     * 
     * @param dispute The dispute to create
     * @return The created dispute with its ID assigned
     */
    Dispute createDispute(Dispute dispute);
    
    /**
     * Updates an existing dispute.
     * 
     * @param dispute The dispute to update
     * @return The updated dispute
     */
    Dispute updateDispute(Dispute dispute);
    
    /**
     * Gets a dispute by its ID.
     * 
     * @param disputeId The ID of the dispute
     * @return The dispute, or null if not found
     */
    Dispute getDisputeById(String disputeId);
    
    /**
     * Gets all disputes in the system.
     * 
     * @return A list of all disputes
     */
    List<Dispute> getAllDisputes();
    
    /**
     * Gets all disputes for a specific sales rep.
     * 
     * @param salesRepId The ID of the sales rep
     * @return A list of disputes
     */
    List<Dispute> getDisputesBySalesRep(String salesRepId);
    
    /**
     * Resolves a dispute.
     * 
     * @param disputeId The ID of the dispute to resolve
     * @param resolution The resolution description
     * @param resolvedBy The ID of the user who resolved the dispute
     */
    void resolveDispute(String disputeId, String resolution, String resolvedBy);
}