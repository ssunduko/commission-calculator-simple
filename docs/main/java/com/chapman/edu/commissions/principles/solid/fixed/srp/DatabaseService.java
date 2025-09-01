package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.util.List;

/**
 * This interface defines database operations for commission-related entities.
 * It follows the Single Responsibility Principle by focusing only on data access.
 */
public interface DatabaseService {
    
    // Deal operations
    Deal getDealFromDatabase(String dealId);
    void saveDeal(Deal deal);
    List<Deal> getDealsBySalesRep(String salesRepId);
    
    // Commission Plan operations
    CommissionPlan getPlanFromDatabase(String planId);
    CommissionPlan getDefaultPlan();
    void savePlan(CommissionPlan plan);
    
    // User operations
    User getUserFromDatabase(String userId);
    void saveUser(User user);
    
    // Commission Calculation operations
    void saveCalculationToDatabase(CommissionCalculation calculation);
    CommissionCalculation getCalculationFromDatabase(String calculationId);
    List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId);
}