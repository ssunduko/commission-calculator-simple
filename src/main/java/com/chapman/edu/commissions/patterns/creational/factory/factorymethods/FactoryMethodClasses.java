package com.chapman.edu.commissions.patterns.creational.factory.factorymethods;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import java.math.BigDecimal;

/**
 * Factory Method Classes
 * 
 * This example demonstrates how to use the Factory Method implementation
 * with the Deal class from the model package.
 * 
 * PRACTICAL DEMONSTRATION:
 * This class shows a real-world application of the Factory Method pattern where:
 * - Different types of deals (hardware, software, service) are created with their specific products
 * - Each deal type has its own factory that knows how to create and configure that type
 * - The client code works with the abstract factory interface, not concrete implementations
 * 
 * BUSINESS CONTEXT:
 * In a sales commission system, different product categories often have:
 * - Different product catalogs
 * - Different pricing structures
 * - Different bundling rules
 * - Different commission calculation rules
 * 
 * The Factory Method pattern helps manage this complexity by:
 * - Encapsulating the creation logic for each deal type in its own factory
 * - Ensuring that each deal is properly configured with appropriate products
 * - Allowing new deal types to be added without changing existing code
 * - Providing a consistent interface for creating all types of deals
 * 
 * IMPLEMENTATION HIGHLIGHTS:
 * - Each factory creates a deal with a specific ID prefix (HW-, SW-, SVC-)
 * - Each factory adds type-specific products to the deal
 * - The template method (createDealWithProducts) ensures consistent initialization
 * - The factory method (createDeal) allows customization by each factory
 * 
 * This example demonstrates how the Factory Method pattern can be used to create
 * complex business objects with their associated components in a structured way.
 */
public class FactoryMethodClasses {

    /**
     * Main method to demonstrate the usage of the Factory Method implementation
     */
    public static void main(String[] args) {
        // Create factories for different types of deals
        FactoryMethodImplementation.DealFactory hardwareFactory = new FactoryMethodImplementation.HardwareDealFactory();
        FactoryMethodImplementation.DealFactory softwareFactory = new FactoryMethodImplementation.SoftwareDealFactory();
        FactoryMethodImplementation.DealFactory serviceFactory = new FactoryMethodImplementation.ServiceDealFactory();

        // Use the factories to create deals with products
        Deal hardwareDeal = hardwareFactory.createDealWithProducts(
            "Hardware Deal", new BigDecimal("5000.00"), "SALES-001");

        Deal softwareDeal = softwareFactory.createDealWithProducts(
            "Software Deal", new BigDecimal("3000.00"), "SALES-002");

        Deal serviceDeal = serviceFactory.createDealWithProducts(
            "Service Deal", new BigDecimal("2000.00"), "SALES-003");

        // Print the results
        System.out.println("Hardware Deal:");
        System.out.println("ID: " + hardwareDeal.getId());
        System.out.println("Title: " + hardwareDeal.getTitle());
        System.out.println("Value: " + hardwareDeal.getValue());
        System.out.println("Products:");
        for (DealProduct product : hardwareDeal.getProducts()) {
            System.out.println("  - " + product.getProductName() + " (ID: " + product.getProductId() + ")");
            System.out.println("    Price: " + product.getPrice() + ", Quantity: " + product.getQuantity());
        }
        System.out.println("Total Value: " + hardwareDeal.calculateTotalValue());
        System.out.println();

        System.out.println("Software Deal:");
        System.out.println("ID: " + softwareDeal.getId());
        System.out.println("Title: " + softwareDeal.getTitle());
        System.out.println("Value: " + softwareDeal.getValue());
        System.out.println("Products:");
        for (DealProduct product : softwareDeal.getProducts()) {
            System.out.println("  - " + product.getProductName() + " (ID: " + product.getProductId() + ")");
            System.out.println("    Price: " + product.getPrice() + ", Quantity: " + product.getQuantity());
        }
        System.out.println("Total Value: " + softwareDeal.calculateTotalValue());
        System.out.println();

        System.out.println("Service Deal:");
        System.out.println("ID: " + serviceDeal.getId());
        System.out.println("Title: " + serviceDeal.getTitle());
        System.out.println("Value: " + serviceDeal.getValue());
        System.out.println("Products:");
        for (DealProduct product : serviceDeal.getProducts()) {
            System.out.println("  - " + product.getProductName() + " (ID: " + product.getProductId() + ")");
            System.out.println("    Price: " + product.getPrice() + ", Quantity: " + product.getQuantity());
        }
        System.out.println("Total Value: " + serviceDeal.calculateTotalValue());
    }
}
