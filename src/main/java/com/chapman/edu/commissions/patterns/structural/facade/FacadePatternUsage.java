package com.chapman.edu.commissions.patterns.structural.facade;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.patterns.structural.facade.FacadePatternImplementation.CommissionFacade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates how to use the Facade Pattern implementation.
 * 
 * The Facade Pattern provides a simplified interface to a complex subsystem.
 * In this example, we use the CommissionFacade to perform operations related to
 * commission calculations without directly interacting with the subsystem components.
 */
public class FacadePatternUsage {

    /**
     * Demonstrates getting deals by sales rep using the facade
     */
    private static void demonstrateGetDealsBySalesRep(CommissionFacade facade) {
        System.out.println("\n=== Getting Deals by Sales Rep ===");

        try {
            // Create a few deals for the sales rep
            List<DealProduct> products = new ArrayList<>();
            DealProduct product = new DealProduct("prod1", "Software License", 1, new BigDecimal("1000"));
            products.add(product);

            facade.createDeal("Deal 1", new BigDecimal("1000"), "user1", products);
            facade.createDeal("Deal 2", new BigDecimal("2000"), "user1", products);
            facade.createDeal("Deal 3", new BigDecimal("3000"), "user1", products);

            // Get the deals for the sales rep
            List<Deal> deals = facade.getDealsBySalesRep("user1");

            System.out.println("Deals for sales rep user1:");
            for (Deal deal : deals) {
                System.out.println("- " + deal.getTitle() + " ($" + deal.getValue() + ")");
            }

        } catch (Exception e) {
            System.out.println("Error getting deals: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates creating a deal using the facade
     */
    private static void demonstrateCreateDeal(CommissionFacade facade) {
        System.out.println("\n=== Creating a Deal ===");
        
        try {
            // Create a list of products for the deal
            List<DealProduct> products = new ArrayList<>();
            
            // Add some products
            DealProduct product1 = new DealProduct("prod1", "Software License", 2, new BigDecimal("1000"));
            products.add(product1);
            
            DealProduct product2 = new DealProduct("prod2", "Support Package", 1, new BigDecimal("500"));
            products.add(product2);
            
            // Create the deal using the facade
            Deal deal = facade.createDeal("Enterprise Deal", new BigDecimal("2500"), "user1", products);
            
            System.out.println("Deal created successfully:");
            System.out.println("Title: " + deal.getTitle());
            System.out.println("Value: " + deal.getValue());
            System.out.println("Sales Rep ID: " + deal.getSalesRepId());
            System.out.println("Products: " + deal.getProducts().size());
            
        } catch (Exception e) {
            System.out.println("Error creating deal: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates closing a deal as won using the facade
     */
    private static void demonstrateCloseDealAsWon(CommissionFacade facade) {
        System.out.println("\n=== Closing a Deal as Won ===");
        
        try {
            // First, create a deal
            List<DealProduct> products = new ArrayList<>();
            DealProduct product = new DealProduct("prod1", "Software License", 1, new BigDecimal("1000"));
            products.add(product);
            
            Deal deal = facade.createDeal("Small Deal", new BigDecimal("1000"), "user1", products);
            
            // Now close the deal as won
            BigDecimal commission = facade.closeDealAsWon(deal.getId());
            
            System.out.println("Deal closed as won:");
            System.out.println("Deal ID: " + deal.getId());
            System.out.println("Commission: " + commission);
            
        } catch (Exception e) {
            System.out.println("Error closing deal: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates getting total commission using the facade
     */
    private static void demonstrateGetTotalCommission(CommissionFacade facade) {
        System.out.println("\n=== Getting Total Commission ===");
        
        try {
            // Create a few deals for the sales rep
            List<DealProduct> products = new ArrayList<>();
            DealProduct product = new DealProduct("prod1", "Software License", 1, new BigDecimal("1000"));
            products.add(product);
            
            Deal deal1 = facade.createDeal("Deal 1", new BigDecimal("10000"), "user1", products);
            Deal deal2 = facade.createDeal("Deal 2", new BigDecimal("20000"), "user1", products);
            Deal deal3 = facade.createDeal("Deal 3", new BigDecimal("30000"), "user1", products);
            
            // Close the deals as won
            facade.closeDealAsWon(deal1.getId());
            facade.closeDealAsWon(deal2.getId());
            facade.closeDealAsWon(deal3.getId());
            
            // Get the total commission for the sales rep
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now().plusDays(7);
            BigDecimal totalCommission = facade.getTotalCommission("user1", startDate, endDate);
            
            System.out.println("Total commission for sales rep user1:");
            System.out.println("Period: " + startDate + " to " + endDate);
            System.out.println("Total Commission: $" + totalCommission);
            
        } catch (Exception e) {
            System.out.println("Error getting total commission: " + e.getMessage());
        }
    }

    /**
     * Main method to demonstrate the usage of the Facade Pattern
     */
    public static void main(String[] args) {
        // Create an instance of the facade
        CommissionFacade facade = new CommissionFacade();

        // Demonstrate creating a deal
        demonstrateCreateDeal(facade);

        // Demonstrate closing a deal as won
        demonstrateCloseDealAsWon(facade);

        // Demonstrate getting deals by sales rep
        demonstrateGetDealsBySalesRep(facade);

        // Demonstrate getting total commission
        demonstrateGetTotalCommission(facade);
    }
}