package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * Data Coupling Example
 * 
 * Data coupling occurs when modules share data through parameters.
 * This is considered a low level of coupling and is generally desirable.
 * 
 * In this example, the CommissionCalculator class receives only the specific data
 * it needs through parameters, creating data coupling.
 */
public class DataCoupling {
    
    /**
     * CommissionCalculator class that calculates commissions
     */
    public static class CommissionCalculator {
        
        /**
         * Calculate commission based on deal value and rate
         * 
         * @param dealValue The value of the deal (data coupling)
         * @param commissionRate The commission rate (data coupling)
         * @return The calculated commission
         */
        public BigDecimal calculateCommission(BigDecimal dealValue, BigDecimal commissionRate) {
            // Data coupling: We're passing only the specific data needed (dealValue and commissionRate)
            // rather than passing the entire Deal object
            
            BigDecimal commission = dealValue.multiply(commissionRate);
            
            System.out.println("Calculated commission for deal value: " + dealValue);
            System.out.println("Commission rate: " + commissionRate);
            System.out.println("Commission amount: " + commission);
            
            return commission;
        }
        
        /**
         * Calculate tiered commission based on deal value
         * 
         * @param dealValue The value of the deal (data coupling)
         * @return The calculated commission
         */
        public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
            // Data coupling: We're passing only the specific data needed (dealValue)
            
            BigDecimal commission;
            
            // Apply tiered commission rates
            if (dealValue.compareTo(new BigDecimal("10000")) > 0) {
                commission = dealValue.multiply(new BigDecimal("0.15"));
            } else if (dealValue.compareTo(new BigDecimal("5000")) > 0) {
                commission = dealValue.multiply(new BigDecimal("0.10"));
            } else {
                commission = dealValue.multiply(new BigDecimal("0.05"));
            }
            
            System.out.println("Calculated tiered commission for deal value: " + dealValue);
            System.out.println("Commission amount: " + commission);
            
            return commission;
        }
    }
    
    /**
     * ProductValueCalculator class that calculates product values
     */
    public static class ProductValueCalculator {
        
        /**
         * Calculate the total value of a product
         * 
         * @param price The price of the product (data coupling)
         * @param quantity The quantity of the product (data coupling)
         * @return The total value
         */
        public BigDecimal calculateProductValue(BigDecimal price, int quantity) {
            // Data coupling: We're passing only the specific data needed (price and quantity)
            // rather than passing the entire DealProduct object
            
            BigDecimal totalValue = price.multiply(new BigDecimal(quantity));
            
            System.out.println("Calculated product value for price: " + price + " and quantity: " + quantity);
            System.out.println("Total value: " + totalValue);
            
            return totalValue;
        }
        
        /**
         * Calculate the discounted value of a product
         * 
         * @param price The price of the product (data coupling)
         * @param quantity The quantity of the product (data coupling)
         * @param discountRate The discount rate (data coupling)
         * @return The discounted value
         */
        public BigDecimal calculateDiscountedValue(BigDecimal price, int quantity, BigDecimal discountRate) {
            // Data coupling: We're passing only the specific data needed
            
            BigDecimal totalValue = price.multiply(new BigDecimal(quantity));
            BigDecimal discount = totalValue.multiply(discountRate);
            BigDecimal discountedValue = totalValue.subtract(discount);
            
            System.out.println("Calculated discounted value for price: " + price + 
                               ", quantity: " + quantity + 
                               ", and discount rate: " + discountRate);
            System.out.println("Discounted value: " + discountedValue);
            
            return discountedValue;
        }
    }
    
    public static void main(String[] args) {
        // Create a deal and product
        Deal deal = new Deal("Test Deal", new BigDecimal("1000.00"), "sales-rep-1");
        DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00"));
        deal.addProduct(product);
        
        // Calculate the total value of the deal
        BigDecimal dealValue = deal.calculateTotalValue();
        
        // Create calculators
        CommissionCalculator commissionCalculator = new CommissionCalculator();
        ProductValueCalculator productValueCalculator = new ProductValueCalculator();
        
        // Calculate commission using data coupling
        BigDecimal commission = commissionCalculator.calculateCommission(dealValue, new BigDecimal("0.10"));
        BigDecimal tieredCommission = commissionCalculator.calculateTieredCommission(dealValue);
        
        // Calculate product values using data coupling
        BigDecimal productValue = productValueCalculator.calculateProductValue(product.getPrice(), product.getQuantity());
        BigDecimal discountedValue = productValueCalculator.calculateDiscountedValue(
                product.getPrice(), product.getQuantity(), new BigDecimal("0.15"));
        
        // Print results
        System.out.println("\nDeal value: " + dealValue);
        System.out.println("Standard commission: " + commission);
        System.out.println("Tiered commission: " + tieredCommission);
        System.out.println("Product value: " + productValue);
        System.out.println("Discounted product value: " + discountedValue);
    }
}