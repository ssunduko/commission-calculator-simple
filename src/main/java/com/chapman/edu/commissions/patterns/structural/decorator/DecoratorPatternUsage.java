package com.chapman.edu.commissions.patterns.structural.decorator;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.structural.decorator.DecoratorPatternImplementation.*;
import com.chapman.edu.commissions.patterns.structural.decorator.DecoratorPatternStructure.*;


import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class demonstrates how to use the Decorator Pattern in practice.
 * 
 * The Decorator Pattern allows us to add new functionality to objects without altering their structure.
 * It is a flexible alternative to subclassing for extending functionality.
 */
public class DecoratorPatternUsage {

    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern Usage Example ===");
        
        // Example 1: Using the simple commission decorators
        System.out.println("\n--- Example 1: Simple Commission Decorators ---");
        demonstrateSimpleCommissionDecorators();
        // Example 2: Using the deal decorators
        System.out.println("\n--- Example 2: Deal Decorators ---");
        demonstrateDealDecorators();
        // Example 3: Combining multiple decorators
        System.out.println("\n--- Example 3: Combining Multiple Decorators ---");
        demonstrateCombiningDecorators();
    }
    
    /**
     * Demonstrates the use of simple commission decorators from DecoratorPatternStructure
     */
    private static void demonstrateSimpleCommissionDecorators() {
        // Create a base commission
        Commission baseCommission = new BaseCommission(new BigDecimal("1000.00"));
        System.out.println("Base Commission: " + baseCommission.getDescription());
        System.out.println("Amount: $" + baseCommission.calculate());
        
        // Add a bonus to the commission
        Commission commissionWithBonus = new BonusDecorator(baseCommission, new BigDecimal("200.00"));
        System.out.println("\nCommission with Bonus: " + commissionWithBonus.getDescription());
        System.out.println("Amount: $" + commissionWithBonus.calculate());
        
        // Add an accelerator to the commission
        Commission commissionWithAccelerator = new AcceleratorDecorator(baseCommission, new BigDecimal("1.5"));
        System.out.println("\nCommission with Accelerator: " + commissionWithAccelerator.getDescription());
        System.out.println("Amount: $" + commissionWithAccelerator.calculate());
        
        // Apply tax to the commission
        Commission commissionAfterTax = new TaxDecorator(baseCommission, new BigDecimal("0.3"));
        System.out.println("\nCommission after Tax: " + commissionAfterTax.getDescription());
        System.out.println("Amount: $" + commissionAfterTax.calculate());
    }
    
    /**
     * Demonstrates the use of deal decorators from DecoratorPatternImplementation
     */
    private static void demonstrateDealDecorators() {
        // Create a sample deal
        Deal deal = createSampleDeal();
        
        // Create a basic deal component
        DealComponent basicDeal = new BasicDeal(deal);
        System.out.println("Basic Deal: " + basicDeal.getTitle());
        System.out.println("Value: $" + basicDeal.calculateValue());
        
        // Apply a discount to the deal
        DealComponent discountedDeal = new DiscountDecorator(basicDeal, new BigDecimal("0.1")); // 10% discount
        System.out.println("\nDiscounted Deal: " + discountedDeal.getTitle());
        System.out.println("Value: $" + discountedDeal.calculateValue());
        
        // Apply a premium to the deal
        DealComponent premiumDeal = new PremiumDecorator(basicDeal, new BigDecimal("0.15")); // 15% premium
        System.out.println("\nPremium Deal: " + premiumDeal.getTitle());
        System.out.println("Value: $" + premiumDeal.calculateValue());
        
        // Apply urgency to the deal
        DealComponent urgentDeal = new UrgencyDecorator(basicDeal, LocalDate.now().plusDays(5)); // Deadline in 5 days
        System.out.println("\nUrgent Deal: " + urgentDeal.getTitle());
        System.out.println("Value: $" + urgentDeal.calculateValue());
        
        // Apply logging to the deal
        DealComponent loggingDeal = new LoggingDecorator(basicDeal);
        System.out.println("\nLogging Deal Operations:");
        loggingDeal.getTitle();
        loggingDeal.calculateValue();
        loggingDeal.getSalesRepId();
        loggingDeal.getProducts();
    }
    
    /**
     * Demonstrates combining multiple decorators
     */
    private static void demonstrateCombiningDecorators() {
        // Create a sample deal
        Deal deal = createSampleDeal();
        
        // Create a basic deal component
        DealComponent basicDeal = new BasicDeal(deal);
        
        // Example 1: Combine discount and urgency
        DealComponent discountedUrgentDeal = new UrgencyDecorator(
            new DiscountDecorator(basicDeal, new BigDecimal("0.1")),
            LocalDate.now().plusDays(5)
        );
        System.out.println("Discounted Urgent Deal: " + discountedUrgentDeal.getTitle());
        System.out.println("Value: $" + discountedUrgentDeal.calculateValue());
        
        // Example 2: Combine premium and logging
        DealComponent premiumLoggingDeal = new LoggingDecorator(
            new PremiumDecorator(basicDeal, new BigDecimal("0.2"))
        );
        System.out.println("\nPremium Logging Deal:");
        System.out.println("Title: " + premiumLoggingDeal.getTitle());
        System.out.println("Value: $" + premiumLoggingDeal.calculateValue());
        
        // Example 3: Complex combination
        DealComponent complexDeal = new LoggingDecorator(
            new UrgencyDecorator(
                new PremiumDecorator(
                    new DiscountDecorator(basicDeal, new BigDecimal("0.05")),
                    new BigDecimal("0.1")
                ),
                LocalDate.now().plusDays(3)
            )
        );
        System.out.println("\nComplex Deal:");
        System.out.println("Title: " + complexDeal.getTitle());
        System.out.println("Value: $" + complexDeal.calculateValue());
        
        // Example 4: Combining commission decorators
        Commission baseCommission = new BaseCommission(new BigDecimal("1000.00"));
        Commission complexCommission = new TaxDecorator(
            new AcceleratorDecorator(
                new BonusDecorator(baseCommission, new BigDecimal("300.00")),
                new BigDecimal("1.2")
            ),
            new BigDecimal("0.25")
        );
        System.out.println("\nComplex Commission: " + complexCommission.getDescription());
        System.out.println("Amount: $" + complexCommission.calculate());
    }
    
    /**
     * Creates a sample deal for demonstration purposes
     */
    private static Deal createSampleDeal() {
        Deal deal = new Deal("Enterprise Software License", new BigDecimal("10000.00"), "REP001");
        deal.setId("DEAL001");
        deal.setStatus(DealStatus.OPEN);
        deal.setCloseDate(LocalDate.now().plusMonths(1));
        
        DealProduct product1 = new DealProduct();
        product1.setId("PROD001");
        product1.setProductId("SW001");
        product1.setProductName("Enterprise License - Basic");
        product1.setQuantity(5);
        product1.setPrice(new BigDecimal("1000.00"));
        product1.setDealId(deal.getId());
        
        DealProduct product2 = new DealProduct();
        product2.setId("PROD002");
        product2.setProductId("SW002");
        product2.setProductName("Enterprise License - Premium");
        product2.setQuantity(2);
        product2.setPrice(new BigDecimal("2500.00"));
        product2.setDealId(deal.getId());
        
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        return deal;
    }
}