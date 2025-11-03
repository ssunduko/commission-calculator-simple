package com.chapman.edu.commissions.patterns.structural.adapter;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternStructure.*;
import com.chapman.edu.commissions.patterns.structural.adapter.AdapterPatternImplementation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * This class demonstrates how to use the Adapter Pattern with our implementation.
 * 
 * The Adapter Pattern is particularly useful when:
 * - You need to use an existing class with a different interface
 * - You want to create a reusable class that cooperates with classes that don't have compatible interfaces
 * - You need to use several existing subclasses but it's impractical to adapt their interface by subclassing each one
 * 
 * In this example, we'll show how to use our adapters to make our Deal model work with
 * different client interfaces without modifying the original Deal class.
 */
public class AdapterPatternUsage {

    public static void main(String[] args) {
        // Create a sample Deal with products
        Deal deal = createSampleDeal();
        
        System.out.println("=== Original Deal Information ===");
        printDealInfo(deal);
        
        System.out.println("\n=== Using Deal with Report Generator (via Adapter) ===");
        // Use the DealReportAdapter to adapt the Deal to the ReportData interface
        AdapterPatternStructure.ReportData reportData = new DealReportAdapter(deal);
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport(reportData);
        
        System.out.println("\n=== Using Deal with Payment Processor (via Adapter) ===");
        // Use the DealPaymentAdapter to adapt the Deal to the PaymentTransaction interface
        PaymentTransaction paymentTransaction = new AdapterPatternImplementation.DealPaymentAdapter(deal);
        AdapterPatternImplementation.PaymentProcessor paymentProcessor = new AdapterPatternImplementation.PaymentProcessor();
        paymentProcessor.processPayment(paymentTransaction);
        
        System.out.println("\n=== Demonstrating the flexibility of the Adapter Pattern ===");
        // Change the deal status and see how the adapters handle it
        deal.setStatus(DealStatus.WON);
        System.out.println("Deal status changed to: " + deal.getStatus());
        
        System.out.println("\n=== Updated Report ===");
        reportGenerator.generateReport(reportData);
        
        System.out.println("\n=== Updated Payment Transaction ===");
        paymentProcessor.processPayment(paymentTransaction);
    }
    
    /**
     * Creates a sample Deal with products for demonstration purposes.
     * @return A sample Deal
     */
    private static Deal createSampleDeal() {
        // Create a new deal
        Deal deal = new Deal("Enterprise Software Solution", new BigDecimal("50000.00"), "SALES001");
        deal.setId(UUID.randomUUID().toString());
        deal.setStatus(DealStatus.OPEN);
        deal.setCloseDate(LocalDate.now().plusDays(30));
        
        // Add products to the deal
        DealProduct product1 = new DealProduct("SW001", "Enterprise License", 5, new BigDecimal("8000.00"));
        product1.setId(UUID.randomUUID().toString());
        product1.setDealId(deal.getId());
        
        DealProduct product2 = new DealProduct("SVC001", "Implementation Services", 1, new BigDecimal("10000.00"));
        product2.setId(UUID.randomUUID().toString());
        product2.setDealId(deal.getId());
        
        DealProduct product3 = new DealProduct("TRN001", "Training Package", 1, new BigDecimal("5000.00"));
        product3.setId(UUID.randomUUID().toString());
        product3.setDealId(deal.getId());
        
        // Add products to the deal
        deal.addProduct(product1);
        deal.addProduct(product2);
        deal.addProduct(product3);
        
        return deal;
    }
    
    /**
     * Prints the basic information of a Deal.
     * @param deal The Deal to print
     */
    private static void printDealInfo(Deal deal) {
        System.out.println("Deal ID: " + deal.getId());
        System.out.println("Title: " + deal.getTitle());
        System.out.println("Value: " + deal.getValue());
        System.out.println("Status: " + deal.getStatus());
        System.out.println("Sales Rep ID: " + deal.getSalesRepId());
        System.out.println("Close Date: " + deal.getCloseDate());
        System.out.println("Products:");
        
        for (DealProduct product : deal.getProducts()) {
            System.out.println("  - " + product.getProductName() + 
                               " (Qty: " + product.getQuantity() + 
                               ", Price: " + product.getPrice() + 
                               ", Total: " + product.calculateTotalPrice() + ")");
        }
        
        System.out.println("Total Deal Value: " + deal.calculateTotalValue());
    }
}