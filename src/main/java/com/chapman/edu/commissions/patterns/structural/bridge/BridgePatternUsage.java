package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.CommissionCalculationStrategy;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.CommissionProcessor;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.DealService;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.DealServiceImpl;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.FlatRateStrategy;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.ManagerCommissionProcessor;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.ProductBasedStrategy;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.SalesRepCommissionProcessor;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.TieredValueStrategy;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.UserService;
import com.chapman.edu.commissions.patterns.structural.bridge.BridgePatternImplementation.UserServiceImpl;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates how to use the Bridge Pattern implementation in a real-world scenario.
 * 
 * The Bridge Pattern allows us to:
 * 1. Separate the abstraction (CommissionProcessor) from its implementation (CommissionCalculationStrategy)
 * 2. Change the implementation at runtime
 * 3. Extend both the abstraction and implementation independently
 * 
 * In this example, we'll show how to:
 * - Create different commission calculation strategies
 * - Create different commission processors
 * - Process commissions for different deals
 * - Change strategies at runtime
 */
public class BridgePatternUsage {

    public static void main(String[] args) {
        // Create the services
        DealService dealService = new DealServiceImpl();
        UserService userService = new UserServiceImpl();
        
        // Create the strategies
        CommissionCalculationStrategy flatRateStrategy = new FlatRateStrategy(new BigDecimal("0.05"));
        CommissionCalculationStrategy tieredStrategy = new TieredValueStrategy();
        CommissionCalculationStrategy productBasedStrategy = new ProductBasedStrategy();
        
        // Create the processors
        CommissionProcessor salesRepProcessor = new SalesRepCommissionProcessor(
                flatRateStrategy, dealService, userService);
        
        CommissionProcessor managerProcessor = new ManagerCommissionProcessor(
                tieredStrategy, dealService, userService, new BigDecimal("0.25"));
        
        System.out.println("=== Bridge Pattern Usage Example ===");
        System.out.println();
        
        // Example 1: Process commission for a hardware sale using flat rate strategy
        System.out.println("Example 1: Process commission for a hardware sale using flat rate strategy");
        CommissionCalculation calculation1 = salesRepProcessor.processCommission("deal1");
        printCommissionDetails(calculation1, dealService, userService);
        
        // Example 2: Process commission for a software license using tiered strategy
        System.out.println("\nExample 2: Process commission for a software license using tiered strategy");
        // Change the strategy at runtime
        salesRepProcessor.setStrategy(tieredStrategy);
        CommissionCalculation calculation2 = salesRepProcessor.processCommission("deal2");
        printCommissionDetails(calculation2, dealService, userService);
        
        // Example 3: Process commission for a consulting service using product-based strategy
        System.out.println("\nExample 3: Process commission for a consulting service using product-based strategy");
        // Change the strategy at runtime
        salesRepProcessor.setStrategy(productBasedStrategy);
        CommissionCalculation calculation3 = salesRepProcessor.processCommission("deal3");
        printCommissionDetails(calculation3, dealService, userService);
        
        // Example 4: Process manager override commission for a hardware sale
        System.out.println("\nExample 4: Process manager override commission for a hardware sale");
        CommissionCalculation calculation4 = managerProcessor.processCommission("deal1");
        printCommissionDetails(calculation4, dealService, userService);
        
        // Example 5: Process all deals for a sales rep using different strategies
        System.out.println("\nExample 5: Process all deals for a sales rep using different strategies");
        processAllDealsForSalesRep("user1", salesRepProcessor, dealService);
        
        // Example 6: Compare different strategies for the same deal
        System.out.println("\nExample 6: Compare different strategies for the same deal");
        compareStrategiesForDeal("deal2", dealService, userService);
    }
    
    /**
     * Print the details of a commission calculation
     */
    private static void printCommissionDetails(CommissionCalculation calculation, 
                                              DealService dealService, 
                                              UserService userService) {
        Deal deal = dealService.getDealById(calculation.getDealId());
        User user = userService.getUserById(calculation.getSalesRepId());
        
        System.out.println("Deal: " + deal.getTitle() + " (Value: $" + deal.getValue() + ")");
        System.out.println("User: " + user.getFullName());
        System.out.println("Commission Amount: $" + calculation.getBaseCommission());
        System.out.println("Calculated By: " + calculation.getCalculatedBy());
    }
    
    /**
     * Process all deals for a sales rep using the provided processor
     */
    private static void processAllDealsForSalesRep(String salesRepId, 
                                                 CommissionProcessor processor,
                                                 DealService dealService) {
        List<Deal> deals = dealService.getDealsBySalesRep(salesRepId);
        
        for (Deal deal : deals) {
            if (deal.getStatus() == DealStatus.WON) {
                CommissionCalculation calculation = processor.processCommission(deal.getId());
                System.out.println("Deal: " + deal.getTitle() + " - Commission: $" + calculation.getBaseCommission());
            }
        }
    }
    
    /**
     * Compare different strategies for the same deal
     */
    private static void compareStrategiesForDeal(String dealId, 
                                               DealService dealService, 
                                               UserService userService) {
        Deal deal = dealService.getDealById(dealId);
        User user = userService.getUserById(deal.getSalesRepId());
        
        // Create strategies
        CommissionCalculationStrategy flatRateStrategy = new FlatRateStrategy(new BigDecimal("0.05"));
        CommissionCalculationStrategy tieredStrategy = new TieredValueStrategy();
        CommissionCalculationStrategy productBasedStrategy = new ProductBasedStrategy();
        
        // Calculate commissions using different strategies
        BigDecimal flatRateCommission = flatRateStrategy.calculateCommission(deal, user, userService.getCommissionPlan(user.getId()));
        BigDecimal tieredCommission = tieredStrategy.calculateCommission(deal, user, userService.getCommissionPlan(user.getId()));
        BigDecimal productBasedCommission = productBasedStrategy.calculateCommission(deal, user, userService.getCommissionPlan(user.getId()));
        
        // Print comparison
        System.out.println("Deal: " + deal.getTitle() + " (Value: $" + deal.getValue() + ")");
        System.out.println("Flat Rate Strategy (5%): $" + flatRateCommission);
        System.out.println("Tiered Value Strategy: $" + tieredCommission);
        System.out.println("Product-Based Strategy: $" + productBasedCommission);
        
        // Determine the best strategy
        BigDecimal bestCommission = flatRateCommission;
        String bestStrategy = "Flat Rate Strategy";
        
        if (tieredCommission.compareTo(bestCommission) > 0) {
            bestCommission = tieredCommission;
            bestStrategy = "Tiered Value Strategy";
        }
        
        if (productBasedCommission.compareTo(bestCommission) > 0) {
            bestCommission = productBasedCommission;
            bestStrategy = "Product-Based Strategy";
        }
        
        System.out.println("Best Strategy: " + bestStrategy + " ($" + bestCommission + ")");
    }
}