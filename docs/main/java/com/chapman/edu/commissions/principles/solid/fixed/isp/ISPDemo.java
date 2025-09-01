package com.chapman.edu.commissions.principles.solid.fixed.isp;

import com.chapman.edu.commissions.model.CommissionCalculation;

import java.util.List;

/**
 * This class demonstrates how the Interface Segregation Principle is applied.
 * It shows how clients can use only the interfaces they need without being forced to depend on methods they don't use.
 */
public class ISPDemo {
    
    public static void main(String[] args) {
        // Create a client that only implements the CommissionCalculationService interface
        CommissionCalculationService calculationService = new CommissionCalculatorClient();
        
        // Use the client to calculate commissions
        System.out.println("Calculating commission for deal DEAL-001 with plan PLAN-001");
        CommissionCalculation calculation = calculationService.calculateCommission("DEAL-001", "PLAN-001");
        System.out.println("Commission calculation created with ID: " + calculation.getId());
        
        // Get calculations by sales rep
        System.out.println("\nGetting calculations for sales rep SALES-001");
        List<CommissionCalculation> salesRepCalculations = calculationService.getCalculationsBySalesRep("SALES-001");
        System.out.println("Found " + salesRepCalculations.size() + " calculations");
        
        // Get calculations by deal
        System.out.println("\nGetting calculations for deal DEAL-001");
        List<CommissionCalculation> dealCalculations = calculationService.getCalculationsByDeal("DEAL-001");
        System.out.println("Found " + dealCalculations.size() + " calculations");
        
        // Recalculate a commission
        if (!dealCalculations.isEmpty()) {
            String calculationId = dealCalculations.get(0).getId();
            System.out.println("\nRecalculating commission with ID: " + calculationId);
            CommissionCalculation recalculation = calculationService.recalculateCommission(calculationId);
            if (recalculation != null) {
                System.out.println("Commission recalculated successfully");
            } else {
                System.out.println("Commission recalculation failed");
            }
        }
        
        // Note that we don't need to implement or use any methods from other interfaces
        // such as DealManagementService, UserManagementService, DisputeResolutionService, etc.
        // This demonstrates the Interface Segregation Principle in action.
        System.out.println("\nISP Demonstration Complete");
        System.out.println("The CommissionCalculatorClient only implements the CommissionCalculationService interface");
        System.out.println("It is not forced to implement methods from other interfaces that it doesn't need");
    }
}