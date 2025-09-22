package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Common Coupling Example
 * 
 * Common coupling occurs when multiple modules share global data.
 * This creates dependencies between modules through the shared data.
 * 
 * In this example, we have a global configuration and a shared list of deals
 * that are accessed by multiple classes, creating common coupling.
 */
public class CommonCoupling {
    
    // Shared global data - this creates common coupling
    public static class GlobalConfig {
        public static BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.10");
        public static String DEFAULT_CURRENCY = "USD";
        public static int MAX_PRODUCTS_PER_DEAL = 10;
        public static List<Deal> ALL_DEALS = new ArrayList<>();
    }
    
    /**
     * DealManager class that uses the global configuration
     */
    public static class DealManager {
        
        public void createDeal(String title, String salesRepId) {
            Deal deal = new Deal(title, BigDecimal.ZERO, salesRepId);
            
            // Common coupling: Using shared global data
            GlobalConfig.ALL_DEALS.add(deal);
            
            System.out.println("Deal created and added to global list");
            System.out.println("Total deals: " + GlobalConfig.ALL_DEALS.size());
        }
        
        public BigDecimal calculateCommission(Deal deal) {
            BigDecimal totalValue = deal.calculateTotalValue();
            
            // Common coupling: Using shared global commission rate
            return totalValue.multiply(GlobalConfig.DEFAULT_COMMISSION_RATE);
        }
    }
    
    /**
     * ProductManager class that also uses the global configuration
     */
    public static class ProductManager {
        
        public void addProductToDeal(Deal deal, String productId, String productName, int quantity, BigDecimal price) {
            // Common coupling: Using shared global max products limit
            if (deal.getProducts().size() >= GlobalConfig.MAX_PRODUCTS_PER_DEAL) {
                System.out.println("Cannot add product: Maximum products per deal reached");
                return;
            }
            
            DealProduct product = new DealProduct(productId, productName, quantity, price);
            deal.addProduct(product);
            
            System.out.println("Product added to deal");
            System.out.println("Total products: " + deal.getProducts().size());
        }
        
        public String formatPrice(BigDecimal price) {
            // Common coupling: Using shared global currency
            return price.toString() + " " + GlobalConfig.DEFAULT_CURRENCY;
        }
    }
    
    public static void main(String[] args) {
        DealManager dealManager = new DealManager();
        ProductManager productManager = new ProductManager();
        
        // Create a deal
        dealManager.createDeal("Test Deal", "sales-rep-1");
        Deal deal = GlobalConfig.ALL_DEALS.get(0);
        
        // Add products to the deal
        productManager.addProductToDeal(deal, "prod1", "Product 1", 2, new BigDecimal("100.00"));
        productManager.addProductToDeal(deal, "prod2", "Product 2", 1, new BigDecimal("50.00"));
        
        // Calculate commission
        BigDecimal commission = dealManager.calculateCommission(deal);
        System.out.println("Commission: " + productManager.formatPrice(commission));
        
        // Change global configuration
        GlobalConfig.DEFAULT_COMMISSION_RATE = new BigDecimal("0.15");
        GlobalConfig.DEFAULT_CURRENCY = "EUR";
        
        // Recalculate with new configuration
        commission = dealManager.calculateCommission(deal);
        System.out.println("New commission: " + productManager.formatPrice(commission));
    }
}