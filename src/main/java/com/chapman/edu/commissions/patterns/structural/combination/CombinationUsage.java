package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates the usage of the combined structural design patterns.
 * It provides concrete examples of how to use each pattern combination in real-world scenarios.
 */
public class CombinationUsage {

    public static void main(String[] args) {
        System.out.println("===== Structural Pattern Combinations Usage Examples =====\n");
        
        // Demonstrate each pattern combination
        demonstrateAdapterFacade();
        demonstrateCompositeDecorator();
        demonstrateProxyDecorator();
        demonstrateBridgeAbstractFactory();
        demonstrateFacadeProxy();
    }
    
    /**
     * Demonstrates how to use the AdapterFacade pattern combination.
     * This shows how to use a facade that internally uses adapters to work with external systems.
     */
    private static void demonstrateAdapterFacade() {
        System.out.println("\n===== Adapter + Facade Pattern Combination Usage =====");
        
        // Create the facade
        AdapterFacade.PaymentFacade paymentFacade = new AdapterFacade.PaymentFacade();
        
        try {
            // Process payment for a deal
            System.out.println("Processing payment for deal-1:");
            paymentFacade.processPaymentForDeal("deal-1");
            
            // Generate payment report for a sales rep
            System.out.println("\nGenerating payment report for user-1:");
            paymentFacade.generatePaymentReport("user-1");
            
            System.out.println("\nClient code doesn't need to know about the adapter or the complex subsystem.");
            System.out.println("It works with a simple facade interface that handles all the complexity internally.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Demonstrates how to use the CompositeDecorator pattern combination.
     * This shows how to build a tree structure of components and then decorate them with additional behaviors.
     */
    private static void demonstrateCompositeDecorator() {
        System.out.println("\n===== Composite + Decorator Pattern Combination Usage =====");
        
        // Create individual products (leaf nodes)
        CompositeDecorator.ProductItem laptop = new CompositeDecorator.ProductItem(
            "P1", "Laptop", 1, new BigDecimal("1500.00"));
        
        CompositeDecorator.ProductItem monitor = new CompositeDecorator.ProductItem(
            "P2", "Monitor", 2, new BigDecimal("400.00"));
        
        CompositeDecorator.ProductItem software = new CompositeDecorator.ProductItem(
            "P3", "Software License", 5, new BigDecimal("200.00"));
        
        // Create a composite deal
        CompositeDecorator.SalesDeal workstationBundle = new CompositeDecorator.SalesDeal(
            "Workstation Bundle", "sales-rep-1");
        workstationBundle.addComponent(laptop);
        workstationBundle.addComponent(monitor);
        
        // Create another composite deal
        CompositeDecorator.SalesDeal softwareBundle = new CompositeDecorator.SalesDeal(
            "Software Bundle", "sales-rep-1");
        softwareBundle.addComponent(software);
        
        // Create a top-level composite
        CompositeDecorator.SalesDeal enterpriseDeal = new CompositeDecorator.SalesDeal(
            "Enterprise Deal", "sales-rep-1");
        enterpriseDeal.addComponent(workstationBundle);
        enterpriseDeal.addComponent(softwareBundle);
        
        // Calculate and display the original value
        System.out.println("Original Deal: " + enterpriseDeal.getName());
        System.out.println("Original Value: $" + enterpriseDeal.calculateValue());
        
        // Apply a discount decorator to the entire deal
        CompositeDecorator.SalesComponent discountedDeal = 
            new CompositeDecorator.DiscountDecorator(enterpriseDeal, new BigDecimal("0.15"));
        
        System.out.println("\nAfter applying 15% discount to the entire deal:");
        System.out.println("Deal: " + discountedDeal.getName());
        System.out.println("Discounted Value: $" + discountedDeal.calculateValue());
        
        // Apply an urgency decorator to just the software bundle
        CompositeDecorator.SalesComponent urgentSoftware = 
            new CompositeDecorator.UrgencyDecorator(softwareBundle, LocalDate.now().plusDays(3));
        
        // Replace the software bundle in the enterprise deal with the urgent version
        enterpriseDeal.removeComponent(softwareBundle);
        enterpriseDeal.addComponent(urgentSoftware);
        
        // Apply discount to the modified deal
        CompositeDecorator.SalesComponent modifiedDiscountedDeal = 
            new CompositeDecorator.DiscountDecorator(enterpriseDeal, new BigDecimal("0.15"));
        
        System.out.println("\nAfter making software bundle urgent and applying discount:");
        System.out.println("Deal: " + modifiedDiscountedDeal.getName());
        System.out.println("Final Value: $" + modifiedDiscountedDeal.calculateValue());
        
        System.out.println("\nThe Composite pattern allows treating individual products and collections");
        System.out.println("of products uniformly, while the Decorator pattern allows dynamically");
        System.out.println("adding behaviors like discounts and urgency premiums to any component.");
    }
    
    /**
     * Demonstrates how to use the ProxyDecorator pattern combination.
     * This shows how to control access to services while also adding additional behaviors.
     */
    private static void demonstrateProxyDecorator() {
        System.out.println("\n===== Proxy + Decorator Pattern Combination Usage =====");
        
        // Create a sample deal
        Deal sampleDeal = createSampleDeal();
        
        // Create users with different roles
        User salesRep = createUser("sales-rep-1", "John", "Doe", UserRole.SALES_REP);
        User manager = createUser("manager-1", "Jane", "Smith", UserRole.SALES_MANAGER);
        User regularUser = createUser("user-1", "Bob", "Johnson", UserRole.SYSTEM_ADMIN);
        
        // Create the base valuation service
        ProxyDecorator.DealValuationService baseService = 
            new ProxyDecorator.StandardDealValuationService();
        
        // Decorate with premium product analysis
        ProxyDecorator.DealValuationService decoratedService = 
            new ProxyDecorator.PremiumProductDecorator(
                baseService, 
                new BigDecimal("1000.00"),  // Premium threshold
                new BigDecimal("0.02")      // Premium rate (2%)
            );
        
        // Further decorate with seasonal discounts
        decoratedService = new ProxyDecorator.SeasonalDiscountDecorator(decoratedService);
        
        // Create protection proxies for different users
        ProxyDecorator.DealValuationService salesRepProxy = 
            new ProxyDecorator.ProtectionDealValuationProxy(decoratedService, salesRep);
        
        ProxyDecorator.DealValuationService managerProxy = 
            new ProxyDecorator.ProtectionDealValuationProxy(decoratedService, manager);
        
        ProxyDecorator.DealValuationService regularUserProxy = 
            new ProxyDecorator.ProtectionDealValuationProxy(decoratedService, regularUser);
        
        // Create a caching proxy on top of the protection proxy for the sales rep
        ProxyDecorator.CachingDealValuationProxy cachingSalesRepProxy = 
            new ProxyDecorator.CachingDealValuationProxy(salesRepProxy);
        
        // Use the proxies
        System.out.println("Using caching + protection proxy with decorated service for sales rep:");
        try {
            BigDecimal value1 = cachingSalesRepProxy.calculateDealValue(sampleDeal);
            System.out.println("First call - Deal Value: $" + value1);
            
            BigDecimal value2 = cachingSalesRepProxy.calculateDealValue(sampleDeal);
            System.out.println("Second call (cached) - Deal Value: $" + value2);
            
            cachingSalesRepProxy.clearCache();
            BigDecimal value3 = cachingSalesRepProxy.calculateDealValue(sampleDeal);
            System.out.println("After clearing cache - Deal Value: $" + value3);
        } catch (Exception e) {
            System.out.println("Error with sales rep proxy: " + e.getMessage());
        }
        
        // Try with regular user (should fail due to protection proxy)
        System.out.println("\nTrying to access with regular user (should fail):");
        try {
            BigDecimal value = regularUserProxy.calculateDealValue(sampleDeal);
            System.out.println("Deal Value: $" + value);
        } catch (Exception e) {
            System.out.println("Error with regular user proxy: " + e.getMessage());
        }
        
        System.out.println("\nThe Proxy pattern controls access to the service based on user roles,");
        System.out.println("while the Decorator pattern adds behaviors like premium product analysis");
        System.out.println("and seasonal discounts to the service.");
    }
    
    /**
     * Demonstrates how to use the BridgeAbstractFactory pattern combination.
     * This shows how to create families of related objects while separating abstraction from implementation.
     */
    private static void demonstrateBridgeAbstractFactory() {
        System.out.println("\n===== Bridge + Abstract Factory Pattern Combination Usage =====");
        
        // Create an instance of BridgeAbstractFactory to access its inner classes
        BridgeAbstractFactory bridgeAbstractFactory = new BridgeAbstractFactory();
        
        // Create mock services for demonstration
        BridgeAbstractFactory.DealService dealService = 
            bridgeAbstractFactory.new DealServiceImpl();
        
        BridgeAbstractFactory.UserService userService = 
            bridgeAbstractFactory.new UserServiceImpl();
        
        // Create different factories for different commission processing strategies
        BridgeAbstractFactory.CommissionProcessorFactory standardFactory = 
            bridgeAbstractFactory.new StandardCommissionProcessorFactory(dealService, userService);
        
        BridgeAbstractFactory.CommissionProcessorFactory tieredFactory = 
            bridgeAbstractFactory.new TieredCommissionProcessorFactory(dealService, userService);
        
        BridgeAbstractFactory.CommissionProcessorFactory productBasedFactory = 
            bridgeAbstractFactory.new ProductBasedCommissionProcessorFactory(dealService, userService);
        
        // Use the standard factory to create processors
        System.out.println("Creating processors using Standard Factory:");
        BridgeAbstractFactory.CommissionProcessor salesRepProcessor = 
            standardFactory.createSalesRepProcessor();
        
        BridgeAbstractFactory.CommissionProcessor managerProcessor = 
            standardFactory.createManagerProcessor();
        
        // Process commissions
        System.out.println("Processing commission for deal-1 using Standard Factory processors:");
        try {
            CommissionCalculation salesRepCalc = salesRepProcessor.processCommission("deal-1");
            CommissionCalculation managerCalc = managerProcessor.processCommission("deal-1");
            
            System.out.println("Sales Rep Commission: $" + salesRepCalc.getBaseCommission());
            System.out.println("Manager Commission: $" + managerCalc.getBaseCommission());
        } catch (Exception e) {
            System.out.println("Error processing commission: " + e.getMessage());
        }
        
        // Switch to tiered factory
        System.out.println("\nSwitching to Tiered Factory:");
        salesRepProcessor = tieredFactory.createSalesRepProcessor();
        managerProcessor = tieredFactory.createManagerProcessor();
        
        // Process commissions with tiered strategy
        System.out.println("Processing commission for deal-1 using Tiered Factory processors:");
        try {
            CommissionCalculation salesRepCalc = salesRepProcessor.processCommission("deal-1");
            CommissionCalculation managerCalc = managerProcessor.processCommission("deal-1");
            
            System.out.println("Sales Rep Commission: $" + salesRepCalc.getBaseCommission());
            System.out.println("Manager Commission: $" + managerCalc.getBaseCommission());
        } catch (Exception e) {
            System.out.println("Error processing commission: " + e.getMessage());
        }
        
        System.out.println("\nThe Bridge pattern separates the commission processors (abstraction)");
        System.out.println("from the calculation strategies (implementation), while the Abstract Factory");
        System.out.println("pattern creates families of related objects (processors and strategies).");
    }
    
    /**
     * Demonstrates how to use the FacadeProxy pattern combination.
     * This shows how to provide a simplified interface to a complex subsystem while controlling access.
     */
    private static void demonstrateFacadeProxy() {
        System.out.println("\n===== Facade + Proxy Pattern Combination Usage =====");
        
        // Create users with different roles
        User salesRep = createUser("user-1", "John", "Doe", UserRole.SALES_REP);
        User manager = createUser("user-2", "Jane", "Smith", UserRole.SALES_MANAGER);
        
        // Create proxies for different users
        System.out.println("Creating facade proxy for sales rep:");
        FacadeProxy.CommissionFacadeProxy salesRepProxy = 
            new FacadeProxy.CommissionFacadeProxy(salesRep);
        salesRepProxy.setLoggingEnabled(true);
        
        System.out.println("\nCreating facade proxy for manager:");
        FacadeProxy.CommissionFacadeProxy managerProxy = 
            new FacadeProxy.CommissionFacadeProxy(manager);
        managerProxy.setLoggingEnabled(true);
        
        // Create a deal using the sales rep proxy
        System.out.println("\nSales rep creating a deal:");
        try {
            List<DealProduct> products = new ArrayList<>();
            
            DealProduct product = new DealProduct();
            product.setProductId("prod-1");
            product.setProductName("Enterprise Software");
            product.setQuantity(10);
            product.setPrice(new BigDecimal("1000.00"));
            products.add(product);
            
            Deal deal = salesRepProxy.createDeal(
                "Enterprise Deal", 
                new BigDecimal("10000.00"), 
                salesRep.getId(), 
                products
            );
            
            // Close the deal as won
            System.out.println("\nSales rep closing the deal as won:");
            BigDecimal commission = salesRepProxy.closeDealAsWon(deal.getId());
            System.out.println("Commission earned: $" + commission);
            
            // Get deals for the sales rep
            System.out.println("\nSales rep getting their own deals:");
            List<Deal> salesRepDeals = salesRepProxy.getDealsBySalesRep(salesRep.getId());
            System.out.println("Number of deals: " + salesRepDeals.size());
            
            // Manager getting deals for the sales rep
            System.out.println("\nManager getting deals for the sales rep:");
            List<Deal> managerViewOfDeals = managerProxy.getDealsBySalesRep(salesRep.getId());
            System.out.println("Number of deals: " + managerViewOfDeals.size());
            
            // Try to get deals for another sales rep (should work for manager, fail for sales rep)
            System.out.println("\nSales rep trying to get deals for another sales rep (should fail):");
            try {
                List<Deal> otherDeals = salesRepProxy.getDealsBySalesRep("another-rep");
                System.out.println("Number of deals: " + otherDeals.size());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
            System.out.println("\nManager trying to get deals for another sales rep (should work):");
            try {
                List<Deal> otherDeals = managerProxy.getDealsBySalesRep("another-rep");
                System.out.println("Number of deals: " + otherDeals.size());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("\nThe Facade pattern provides a simplified interface to the complex");
        System.out.println("commission subsystem, while the Proxy pattern controls access to the");
        System.out.println("facade based on user roles and adds cross-cutting concerns like logging.");
    }
    
    /**
     * Helper method to create a sample deal for demonstration purposes.
     */
    private static Deal createSampleDeal() {
        Deal deal = new Deal();
        deal.setId("deal-1");
        deal.setTitle("Enterprise Software Package");
        deal.setSalesRepId("sales-rep-1");
        deal.setValue(new BigDecimal("10000.00"));
        
        DealProduct product1 = new DealProduct();
        product1.setProductId("prod-1");
        product1.setProductName("Software License");
        product1.setQuantity(10);
        product1.setPrice(new BigDecimal("500.00"));
        
        DealProduct product2 = new DealProduct();
        product2.setProductId("prod-2");
        product2.setProductName("Premium Support");
        product2.setQuantity(1);
        product2.setPrice(new BigDecimal("2000.00"));
        
        deal.getProducts().add(product1);
        deal.getProducts().add(product2);
        
        return deal;
    }
    
    /**
     * Helper method to create a user with specified roles.
     */
    private static User createUser(String id, String firstName, String lastName, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.addRole(role);
        return user;
    }
}