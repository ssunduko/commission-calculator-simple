package com.chapman.edu.commissions.patterns.structural.composite;

import java.math.BigDecimal;

/**
 * This class demonstrates how to use the Composite Pattern implementation.
 * 
 * It shows how to create and work with both individual products (leaves) and
 * deals (composites) in a uniform way.
 */
public class CompositePatternUsage {

    public static void main(String[] args) {
        // Using the implementation from CompositePatternImplementation
        useCompositePattern();
    }

    /**
     * Demonstrates how to use the Composite Pattern with the Commission Calculator domain model.
     */
    public static void useCompositePattern() {
        System.out.println("=== Composite Pattern Usage Example ===");
        
        // Create individual product items (leaves)
        CompositePatternImplementation.ProductItem laptop = 
                new CompositePatternImplementation.ProductItem(
                        "P001", 
                        "Business Laptop", 
                        2, 
                        new BigDecimal("1200.00"));
        
        CompositePatternImplementation.ProductItem monitor = 
                new CompositePatternImplementation.ProductItem(
                        "P002", 
                        "27-inch Monitor", 
                        2, 
                        new BigDecimal("350.00"));
        
        CompositePatternImplementation.ProductItem software = 
                new CompositePatternImplementation.ProductItem(
                        "P003", 
                        "Office Software Suite", 
                        2, 
                        new BigDecimal("200.00"));
        
        CompositePatternImplementation.ProductItem printer = 
                new CompositePatternImplementation.ProductItem(
                        "P004", 
                        "Color Laser Printer", 
                        1, 
                        new BigDecimal("450.00"));
        
        CompositePatternImplementation.ProductItem server = 
                new CompositePatternImplementation.ProductItem(
                        "P005", 
                        "Enterprise Server", 
                        1, 
                        new BigDecimal("3000.00"));
        
        // Create sales deals (composites)
        CompositePatternImplementation.SalesDeal workstationDeal = 
                new CompositePatternImplementation.SalesDeal(
                        "Workstation Package", 
                        "REP001");
        
        CompositePatternImplementation.SalesDeal enterpriseDeal = 
                new CompositePatternImplementation.SalesDeal(
                        "Enterprise Solution", 
                        "REP001");
        
        // Build the composite structure
        
        // Add products to the workstation deal
        workstationDeal.addComponent(laptop);
        workstationDeal.addComponent(monitor);
        workstationDeal.addComponent(software);
        
        // Add products and the workstation deal to the enterprise deal
        enterpriseDeal.addComponent(workstationDeal);  // Nested composite
        enterpriseDeal.addComponent(printer);
        enterpriseDeal.addComponent(server);
        
        // Create a sales report client
        CompositePatternImplementation.SalesReport report = 
                new CompositePatternImplementation.SalesReport();
        
        // Generate reports for different components
        
        // 1. Report for an individual product (leaf)
        System.out.println("\n--- Report for Individual Product ---");
        report.generateReport(laptop);
        
        // 2. Report for a simple deal (composite with only products)
        System.out.println("\n--- Report for Workstation Deal ---");
        report.generateReport(workstationDeal);
        
        // 3. Report for a complex deal (composite with nested composites)
        System.out.println("\n--- Report for Enterprise Deal ---");
        report.generateReport(enterpriseDeal);
        
        // Demonstrate uniform treatment of components
        System.out.println("\n--- Demonstrating Uniform Treatment ---");
        
        // We can calculate the value of any component the same way
        System.out.println("Laptop Value: $" + laptop.calculateValue());
        System.out.println("Workstation Deal Value: $" + workstationDeal.calculateValue());
        System.out.println("Enterprise Deal Value: $" + enterpriseDeal.calculateValue());
        
        // We can modify the structure dynamically
        System.out.println("\n--- After Adding Discount to Laptop ---");
        laptop.setDiscount(new BigDecimal("200.00"));
        
        // The changes automatically propagate through the composite structure
        report.generateReport(enterpriseDeal);
        
        // We can remove components
        System.out.println("\n--- After Removing Server from Enterprise Deal ---");
        enterpriseDeal.removeComponent(server);
        report.generateReport(enterpriseDeal);
    }
}