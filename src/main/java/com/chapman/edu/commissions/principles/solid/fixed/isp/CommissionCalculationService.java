package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.CommissionCalculation;

import java.util.List;

/**
 * This interface defines commission calculation operations.
 * It follows the Interface Segregation Principle by focusing only on commission calculation,
 * allowing clients to depend only on the methods they need.
 */
public interface CommissionCalculationService {
    
    /**
     * Calculates commission for a deal using a specific plan.
     * 
     * @param dealId The ID of the deal
     * @param planId The ID of the commission plan
     * @return The commission calculation result
     */
    CommissionCalculation calculateCommission(String dealId, String planId);
    
    /**
     * Recalculates commission for an existing calculation.
     * 
     * @param calculationId The ID of the calculation to recalculate
     * @return The updated commission calculation result
     */
    CommissionCalculation recalculateCommission(String calculationId);
    
    /**
     * Gets all commission calculations for a sales rep.
     * 
     * @param salesRepId The ID of the sales rep
     * @return A list of commission calculations
     */
    List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId);
    
    /**
     * Gets all commission calculations for a deal.
     * 
     * @param dealId The ID of the deal
     * @return A list of commission calculations
     */
    List<CommissionCalculation> getCalculationsByDeal(String dealId);
}