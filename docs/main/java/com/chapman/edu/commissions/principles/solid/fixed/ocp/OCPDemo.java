package com.chapman.edu.commissions.principles.solid.fixed.ocp;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * This class demonstrates how the Open/Closed Principle is applied.
 * It shows how the code is open for extension but closed for modification.
 */
public class OCPDemo {

    public static void main(String[] args) {
        // Create a sample deal with different product types
        Deal deal = createSampleDeal();

        // Create calculators with default strategies
        DiscountCalculator discountCalculator = new DiscountCalculator();
        CommissionCalculator commissionCalculator = new CommissionCalculator();
        TaxCalculator taxCalculator = new TaxCalculator();

        // Calculate discount, commission, and tax
        System.out.println("Calculating discount, commission, and tax for a deal with multiple product types");
        BigDecimal totalDiscount = discountCalculator.calculateDiscount(deal);
        BigDecimal totalCommission = commissionCalculator.calculateCommission(deal);
        BigDecimal usTax = taxCalculator.calculateTax(deal, "US");
        BigDecimal ukTax = taxCalculator.calculateTax(deal, "UK");

        // Display results
        System.out.println("Total Discount: $" + totalDiscount);
        System.out.println("Total Commission: $" + totalCommission);
        System.out.println("US Tax: $" + usTax);
        System.out.println("UK Tax: $" + ukTax);

        // Demonstrate extending the system with a new product type
        System.out.println("\nExtending the system with a new product type (SUBSCRIPTION)");

        // Create new strategies for the new product type
        DiscountStrategy subscriptionDiscountStrategy = new DiscountStrategy() {
            @Override
            public boolean appliesTo(DealProduct product) {
                return "SUBSCRIPTION".equals(product.getProductId());
            }

            @Override
            public BigDecimal calculateDiscount(DealProduct product) {
                BigDecimal productPrice = product.getPrice();
                int quantity = product.getQuantity();
                return productPrice.multiply(new BigDecimal("0.25"))
                        .multiply(BigDecimal.valueOf(quantity));
            }
        };

        CommissionStrategy subscriptionCommissionStrategy = new CommissionStrategy() {
            @Override
            public boolean appliesTo(DealProduct product) {
                return "SUBSCRIPTION".equals(product.getProductId());
            }

            @Override
            public BigDecimal calculateCommission(DealProduct product) {
                BigDecimal productPrice = product.getPrice();
                int quantity = product.getQuantity();
                BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));
                return productTotal.multiply(new BigDecimal("0.15"));
            }
        };

        // Add the new strategies to the calculators
        discountCalculator.addDiscountStrategy(subscriptionDiscountStrategy);
        commissionCalculator.addCommissionStrategy(subscriptionCommissionStrategy);

        // Add a subscription product to the deal
        DealProduct subscriptionProduct = new DealProduct();
        subscriptionProduct.setProductId("SUBSCRIPTION");
        subscriptionProduct.setProductName("Annual Subscription");
        subscriptionProduct.setPrice(new BigDecimal("1200"));
        subscriptionProduct.setQuantity(1);
        deal.addProduct(subscriptionProduct);

        // Calculate again with the new product type
        System.out.println("\nCalculating again with the new product type");
        totalDiscount = discountCalculator.calculateDiscount(deal);
        totalCommission = commissionCalculator.calculateCommission(deal);

        // Display updated results
        System.out.println("Total Discount: $" + totalDiscount);
        System.out.println("Total Commission: $" + totalCommission);

        // Demonstrate how OCP is applied
        System.out.println("\nOCP Demonstration Complete");
        System.out.println("The system is open for extension but closed for modification:");
        System.out.println("1. We added a new product type (SUBSCRIPTION) without modifying existing code");
        System.out.println("2. We created new strategies for the new product type");
        System.out.println("3. We added these strategies to the existing calculators");
        System.out.println("4. The calculator classes didn't need to change to support the new product type");
        System.out.println("\nThis demonstrates the Open/Closed Principle in action.");
    }

    /**
     * Creates a sample deal with different product types.
     */
    private static Deal createSampleDeal() {
        Deal deal = new Deal();
        deal.setId("DEAL-001");
        deal.setTitle("Sample Deal");
        deal.setValue(new BigDecimal("10000"));

        // Add software product
        DealProduct softwareProduct = new DealProduct();
        softwareProduct.setProductId("SOFTWARE");
        softwareProduct.setProductName("Enterprise Software");
        softwareProduct.setPrice(new BigDecimal("5000"));
        softwareProduct.setQuantity(1);
        deal.addProduct(softwareProduct);

        // Add hardware product
        DealProduct hardwareProduct = new DealProduct();
        hardwareProduct.setProductId("HARDWARE");
        hardwareProduct.setProductName("Server");
        hardwareProduct.setPrice(new BigDecimal("2000"));
        hardwareProduct.setQuantity(2);
        deal.addProduct(hardwareProduct);

        // Add service product
        DealProduct serviceProduct = new DealProduct();
        serviceProduct.setProductId("SERVICE");
        serviceProduct.setProductName("Implementation Service");
        serviceProduct.setPrice(new BigDecimal("1000"));
        serviceProduct.setQuantity(1);
        deal.addProduct(serviceProduct);

        return deal;
    }
}
