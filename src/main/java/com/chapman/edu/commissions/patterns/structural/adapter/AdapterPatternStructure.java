package com.chapman.edu.commissions.patterns.structural.adapter;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates the structure of the Adapter Pattern.
 * 
 * The Adapter Pattern is a structural design pattern that allows objects with incompatible interfaces
 * to collaborate. It acts as a bridge between two incompatible interfaces by wrapping an instance
 * of one class into an adapter class that presents the expected interface.
 * 
 * Key components of the Adapter Pattern:
 * 1. Target - The interface that the client expects to work with
 * 2. Adaptee - The existing class with incompatible interface
 * 3. Adapter - The class that implements the Target interface and translates calls to the Adaptee
 * 4. Client - The class that interacts with the Target interface
 * 
 * When to use the Adapter Pattern:
 * - When you want to use an existing class, but its interface doesn't match what you need
 * - When you want to create a reusable class that cooperates with classes that don't necessarily
 *   have compatible interfaces
 * - When you need to use several existing subclasses but it's impractical to adapt their interface
 *   by subclassing each one
 */
public class AdapterPatternStructure {

    /**
     * Target Interface
     * This is the interface that the client expects to work with.
     */
    public interface ReportData {
        String getReportTitle();
        BigDecimal getReportValue();
        String getOwnerName();
        List<String> getItemDescriptions();
        BigDecimal getTotalAmount();
    }
    
    /**
     * Adaptee
     * This is the existing class with an incompatible interface.
     * In this case, we're using the Deal class from our model.
     * The Deal class has methods like getTitle(), getValue(), getSalesRepId(), etc.,
     * but the client expects methods like getReportTitle(), getReportValue(), getOwnerName(), etc.
     */
    // The Deal class from com.chapman.edu.commissions.model is our Adaptee
    
    /**
     * Adapter
     * This class implements the Target interface and translates calls to the Adaptee.
     */
    public static class DealReportAdapter implements ReportData {
        private Deal deal;
        
        public DealReportAdapter(Deal deal) {
            this.deal = deal;
        }
        
        @Override
        public String getReportTitle() {
            return deal.getTitle();
        }
        
        @Override
        public BigDecimal getReportValue() {
            return deal.getValue();
        }
        
        @Override
        public String getOwnerName() {
            return deal.getSalesRepId(); // In a real scenario, we might look up the name from the ID
        }
        
        @Override
        public List<String> getItemDescriptions() {
            return deal.getProducts().stream()
                    .map(product -> product.getProductName() + " (Qty: " + product.getQuantity() + ")")
                    .toList();
        }
        
        @Override
        public BigDecimal getTotalAmount() {
            return deal.calculateTotalValue();
        }
    }
    
    /**
     * Client
     * This class interacts with the Target interface.
     */
    public static class ReportGenerator {
        public void generateReport(ReportData data) {
            System.out.println("Report: " + data.getReportTitle());
            System.out.println("Owner: " + data.getOwnerName());
            System.out.println("Value: " + data.getReportValue());
            System.out.println("Items:");
            data.getItemDescriptions().forEach(item -> System.out.println("- " + item));
            System.out.println("Total Amount: " + data.getTotalAmount());
        }
    }
}