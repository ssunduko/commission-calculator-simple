package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.CommissionCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements only the CommissionCalculationService interface.
 * It follows the Interface Segregation Principle by depending only on the interface it needs,
 * rather than being forced to implement methods it doesn't use.
 */
public class CommissionCalculatorClient implements CommissionCalculationService {
    
    // Simulated database for demonstration purposes
    private final Map<String, CommissionCalculation> calculations = new HashMap<>();
    private final Map<String, List<CommissionCalculation>> calculationsBySalesRep = new HashMap<>();
    private final Map<String, List<CommissionCalculation>> calculationsByDeal = new HashMap<>();
    
    @Override
    public CommissionCalculation calculateCommission(String dealId, String planId) {
        // Simplified implementation for demonstration purposes
        CommissionCalculation calculation = new CommissionCalculation();
        calculation.setId("CALC-" + System.currentTimeMillis());
        calculation.setDealId(dealId);
        calculation.setPlanId(planId);
        
        // Store the calculation
        calculations.put(calculation.getId(), calculation);
        
        // Update indexes
        addToSalesRepIndex(calculation);
        addToDealIndex(calculation);
        
        return calculation;
    }
    
    @Override
    public CommissionCalculation recalculateCommission(String calculationId) {
        // Simplified implementation for demonstration purposes
        CommissionCalculation calculation = calculations.get(calculationId);
        if (calculation != null) {
            // Perform recalculation logic here
            // For demonstration, we'll just return the existing calculation
            return calculation;
        }
        return null;
    }
    
    @Override
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        return calculationsBySalesRep.getOrDefault(salesRepId, new ArrayList<>());
    }
    
    @Override
    public List<CommissionCalculation> getCalculationsByDeal(String dealId) {
        return calculationsByDeal.getOrDefault(dealId, new ArrayList<>());
    }
    
    private void addToSalesRepIndex(CommissionCalculation calculation) {
        String salesRepId = calculation.getSalesRepId();
        if (salesRepId != null) {
            List<CommissionCalculation> salesRepCalculations = calculationsBySalesRep.computeIfAbsent(
                    salesRepId, k -> new ArrayList<>());
            salesRepCalculations.add(calculation);
        }
    }
    
    private void addToDealIndex(CommissionCalculation calculation) {
        String dealId = calculation.getDealId();
        if (dealId != null) {
            List<CommissionCalculation> dealCalculations = calculationsByDeal.computeIfAbsent(
                    dealId, k -> new ArrayList<>());
            dealCalculations.add(calculation);
        }
    }
}