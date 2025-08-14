package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class implements the DatabaseService interface.
 * It is responsible only for database operations, following the Single Responsibility Principle.
 */
public class DatabaseServiceImpl implements DatabaseService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseServiceImpl.class.getName());
    
    // In-memory database for demonstration purposes
    private Map<String, CommissionCalculation> calculationsDatabase = new HashMap<>();
    private Map<String, Deal> dealsDatabase = new HashMap<>();
    private Map<String, CommissionPlan> plansDatabase = new HashMap<>();
    private Map<String, User> usersDatabase = new HashMap<>();
    
    @Override
    public Deal getDealFromDatabase(String dealId) {
        LOGGER.info("Getting deal from database: " + dealId);
        return dealsDatabase.get(dealId);
    }
    
    @Override
    public void saveDeal(Deal deal) {
        LOGGER.info("Saving deal to database: " + deal.getId());
        dealsDatabase.put(deal.getId(), deal);
    }
    
    @Override
    public List<Deal> getDealsBySalesRep(String salesRepId) {
        LOGGER.info("Getting deals for sales rep: " + salesRepId);
        List<Deal> result = new ArrayList<>();
        for (Deal deal : dealsDatabase.values()) {
            if (deal.getSalesRepId().equals(salesRepId)) {
                result.add(deal);
            }
        }
        return result;
    }
    
    @Override
    public CommissionPlan getPlanFromDatabase(String planId) {
        LOGGER.info("Getting commission plan from database: " + planId);
        return plansDatabase.get(planId);
    }
    
    @Override
    public CommissionPlan getDefaultPlan() {
        LOGGER.info("Getting default commission plan");
        // In a real implementation, we would have a designated default plan
        // For simplicity, we'll return the first plan in the database or create a new one
        if (!plansDatabase.isEmpty()) {
            return plansDatabase.values().iterator().next();
        }
        
        // Create a default plan
        CommissionPlan defaultPlan = new CommissionPlan();
        defaultPlan.setId("DEFAULT-PLAN");
        defaultPlan.setName("Default Commission Plan");
        plansDatabase.put(defaultPlan.getId(), defaultPlan);
        return defaultPlan;
    }
    
    @Override
    public void savePlan(CommissionPlan plan) {
        LOGGER.info("Saving commission plan to database: " + plan.getId());
        plansDatabase.put(plan.getId(), plan);
    }
    
    @Override
    public User getUserFromDatabase(String userId) {
        LOGGER.info("Getting user from database: " + userId);
        return usersDatabase.get(userId);
    }
    
    @Override
    public void saveUser(User user) {
        LOGGER.info("Saving user to database: " + user.getId());
        usersDatabase.put(user.getId(), user);
    }
    
    @Override
    public void saveCalculationToDatabase(CommissionCalculation calculation) {
        LOGGER.info("Saving commission calculation to database: " + calculation.getId());
        calculationsDatabase.put(calculation.getId(), calculation);
    }
    
    @Override
    public CommissionCalculation getCalculationFromDatabase(String calculationId) {
        LOGGER.info("Getting commission calculation from database: " + calculationId);
        return calculationsDatabase.get(calculationId);
    }
    
    @Override
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        LOGGER.info("Getting calculations for sales rep: " + salesRepId);
        List<CommissionCalculation> result = new ArrayList<>();
        for (CommissionCalculation calculation : calculationsDatabase.values()) {
            if (calculation.getSalesRepId().equals(salesRepId)) {
                result.add(calculation);
            }
        }
        return result;
    }
}