package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDeal;
import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDealProduct;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Demonstrates the usage of the Prototype Pattern in the Commission System.
 * 
 * This class shows how to create prototype objects and clone them to create new instances
 * with similar properties, demonstrating both shallow and deep cloning.
 */
public class PrototypePatternUsage {

    /**
     * Main method to demonstrate the Prototype pattern usage.
     */
    public static void main(String[] args) {
        // Demonstrate basic prototype usage
        demonstrateBasicPrototypeUsage();
        
        System.out.println("\n-----------------------------------\n");
        
        // Demonstrate shallow vs deep cloning
        demonstrateShallowVsDeepCloning();
        
        System.out.println("\n-----------------------------------\n");
        
        // Demonstrate prototype registry usage
        demonstratePrototypeRegistryUsage();
    }
    
    /**
     * Demonstrates basic usage of the Prototype pattern.
     * Shows how to create a prototype and clone it to create new instances.
     */
    private static void demonstrateBasicPrototypeUsage() {
        System.out.println("BASIC PROTOTYPE USAGE DEMONSTRATION");
        System.out.println("----------------------------------");
        
        // Create a prototype deal
        CloneableDeal prototypeDeal = new CloneableDeal("Standard Software Deal", new BigDecimal("10000.00"), "REP001");
        prototypeDeal.setId("DEAL-PROTOTYPE-1");
        prototypeDeal.setStatus(DealStatus.OPEN);
        prototypeDeal.setCloseDate(LocalDate.now().plusMonths(1));
        
        // Add products to the prototype deal
        CloneableDealProduct product1 = new CloneableDealProduct("PROD-001", "Basic Software License", 1, new BigDecimal("5000.00"));
        CloneableDealProduct product2 = new CloneableDealProduct("PROD-002", "Premium Support", 1, new BigDecimal("5000.00"));
        prototypeDeal.addProduct(product1);
        prototypeDeal.addProduct(product2);
        
        System.out.println("Original Prototype Deal:");
        System.out.println("ID: " + prototypeDeal.getId());
        System.out.println("Title: " + prototypeDeal.getTitle());
        System.out.println("Value: " + prototypeDeal.getValue());
        System.out.println("Status: " + prototypeDeal.getStatus());
        System.out.println("Products: " + prototypeDeal.getProducts().size());
        
        // Clone the prototype to create a new deal
        CloneableDeal clonedDeal = prototypeDeal.clone();
        clonedDeal.setId("DEAL-CLONE-1");
        clonedDeal.setTitle("Enterprise Software Deal");
        clonedDeal.setValue(new BigDecimal("15000.00"));
        
        System.out.println("\nCloned Deal (after modifications):");
        System.out.println("ID: " + clonedDeal.getId());
        System.out.println("Title: " + clonedDeal.getTitle());
        System.out.println("Value: " + clonedDeal.getValue());
        System.out.println("Status: " + clonedDeal.getStatus());
        System.out.println("Products: " + clonedDeal.getProducts().size());
        
        System.out.println("\nOriginal Prototype Deal (unchanged):");
        System.out.println("ID: " + prototypeDeal.getId());
        System.out.println("Title: " + prototypeDeal.getTitle());
        System.out.println("Value: " + prototypeDeal.getValue());
    }
    
    /**
     * Demonstrates the difference between shallow and deep cloning.
     * Shows how modifications to referenced objects affect shallow clones but not deep clones.
     */
    private static void demonstrateShallowVsDeepCloning() {
        System.out.println("SHALLOW VS DEEP CLONING DEMONSTRATION");
        System.out.println("------------------------------------");
        
        // Create a prototype deal
        CloneableDeal prototypeDeal = new CloneableDeal("Software Deal", new BigDecimal("10000.00"), "REP001");
        prototypeDeal.setId("DEAL-PROTOTYPE-2");
        
        // Add products to the prototype deal
        CloneableDealProduct product = new CloneableDealProduct("PROD-001", "Software License", 1, new BigDecimal("10000.00"));
        product.setId("PRODUCT-1");
        prototypeDeal.addProduct(product);
        
        // Create a shallow clone
        CloneableDeal shallowClone = prototypeDeal.clone();
        shallowClone.setId("DEAL-SHALLOW-CLONE");
        
        // Create a deep clone
        CloneableDeal deepClone = prototypeDeal.deepClone();
        deepClone.setId("DEAL-DEEP-CLONE");
        
        System.out.println("Initial State:");
        System.out.println("Original Product Name: " + prototypeDeal.getProducts().get(0).getProductName());
        System.out.println("Shallow Clone Product Name: " + shallowClone.getProducts().get(0).getProductName());
        System.out.println("Deep Clone Product Name: " + deepClone.getProducts().get(0).getProductName());
        
        // Modify the product in the original deal
        ((CloneableDealProduct)prototypeDeal.getProducts().get(0)).setProductName("Modified Software License");
        
        System.out.println("\nAfter Modifying Original Product:");
        System.out.println("Original Product Name: " + prototypeDeal.getProducts().get(0).getProductName());
        System.out.println("Shallow Clone Product Name: " + shallowClone.getProducts().get(0).getProductName() + " (changed because it's a shallow copy)");
        System.out.println("Deep Clone Product Name: " + deepClone.getProducts().get(0).getProductName() + " (unchanged because it's a deep copy)");
    }
    
    /**
     * Demonstrates the usage of a Prototype Registry.
     * Shows how to store and retrieve prototypes from a registry.
     */
    private static void demonstratePrototypeRegistryUsage() {
        System.out.println("PROTOTYPE REGISTRY USAGE DEMONSTRATION");
        System.out.println("-------------------------------------");
        
        // Create a prototype registry
        PrototypeRegistry registry = new PrototypeRegistry();
        
        // Create and register prototype deals
        CloneableDeal standardDeal = new CloneableDeal("Standard Deal", new BigDecimal("10000.00"), "REP001");
        standardDeal.setStatus(DealStatus.OPEN);
        
        CloneableDeal premiumDeal = new CloneableDeal("Premium Deal", new BigDecimal("50000.00"), "REP001");
        premiumDeal.setStatus(DealStatus.OPEN);
        premiumDeal.addProduct(new CloneableDealProduct("PROD-001", "Premium License", 1, new BigDecimal("30000.00")));
        premiumDeal.addProduct(new CloneableDealProduct("PROD-002", "Premium Support", 1, new BigDecimal("20000.00")));
        
        CloneableDeal enterpriseDeal = new CloneableDeal("Enterprise Deal", new BigDecimal("100000.00"), "REP001");
        enterpriseDeal.setStatus(DealStatus.OPEN);
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-001", "Enterprise License", 1, new BigDecimal("60000.00")));
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-002", "Enterprise Support", 1, new BigDecimal("30000.00")));
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-003", "Training", 1, new BigDecimal("10000.00")));
        
        // Register the prototypes
        registry.addPrototype("standard", standardDeal);
        registry.addPrototype("premium", premiumDeal);
        registry.addPrototype("enterprise", enterpriseDeal);
        
        System.out.println("Registered Prototypes:");
        System.out.println("- standard");
        System.out.println("- premium");
        System.out.println("- enterprise");
        
        // Retrieve and customize a prototype
        CloneableDeal newDeal = registry.getPrototype("premium").deepClone();
        newDeal.setId("DEAL-NEW-1");
        newDeal.setTitle("Custom Premium Deal");
        newDeal.setSalesRepId("REP002");
        
        System.out.println("\nCreated a new deal from 'premium' prototype:");
        System.out.println("ID: " + newDeal.getId());
        System.out.println("Title: " + newDeal.getTitle());
        System.out.println("Value: " + newDeal.getValue());
        System.out.println("Sales Rep: " + newDeal.getSalesRepId());
        System.out.println("Products: " + newDeal.getProducts().size());
        
        // Create another deal from a different prototype
        CloneableDeal anotherDeal = registry.getPrototype("enterprise").deepClone();
        anotherDeal.setId("DEAL-NEW-2");
        anotherDeal.setTitle("Custom Enterprise Deal");
        
        System.out.println("\nCreated another deal from 'enterprise' prototype:");
        System.out.println("ID: " + anotherDeal.getId());
        System.out.println("Title: " + anotherDeal.getTitle());
        System.out.println("Value: " + anotherDeal.getValue());
        System.out.println("Products: " + anotherDeal.getProducts().size());
    }
}