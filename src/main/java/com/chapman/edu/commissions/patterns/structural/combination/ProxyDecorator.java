package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class demonstrates the combination of Proxy and Decorator patterns.
 * 
 * Proxy Pattern: Provides a surrogate or placeholder for another object to control access to it
 * Decorator Pattern: Dynamically adds responsibilities to objects without modifying their code
 * 
 * The ProxyDecorator combines these patterns by:
 * 1. Using the Proxy pattern to control access to deal valuation services
 * 2. Using the Decorator pattern to add additional behaviors to these services
 */
public class ProxyDecorator {

    /**
     * DealValuationService - The component interface for both patterns
     */
    public interface DealValuationService {
        /**
         * Calculate the value of a deal.
         * @param deal The deal to evaluate
         * @return The calculated value
         */
        BigDecimal calculateDealValue(Deal deal);
        
        /**
         * Get the service name.
         * @return The service name
         */
        String getServiceName();
    }
    
    /**
     * StandardDealValuationService - The real subject in the Proxy pattern
     * and a ConcreteComponent in the Decorator pattern.
     */
    public static class StandardDealValuationService implements DealValuationService {
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            // Simulate complex calculation
            try {
                Thread.sleep(100); // Simulate processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            BigDecimal total = BigDecimal.ZERO;
            for (DealProduct product : deal.getProducts()) {
                BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                total = total.add(productValue);
            }
            
            return total;
        }
        
        @Override
        public String getServiceName() {
            return "Standard Deal Valuation Service";
        }
    }
    
    /**
     * DealValuationDecorator - Base decorator class for DealValuationService.
     * This is the Decorator in the Decorator pattern.
     */
    public static abstract class DealValuationDecorator implements DealValuationService {
        protected DealValuationService decoratedService;
        
        public DealValuationDecorator(DealValuationService decoratedService) {
            this.decoratedService = decoratedService;
        }
        
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            return decoratedService.calculateDealValue(deal);
        }
        
        @Override
        public String getServiceName() {
            return decoratedService.getServiceName();
        }
    }
    
    /**
     * PremiumProductDecorator - Adds a premium for high-value products.
     * This is a ConcreteDecorator in the Decorator pattern.
     */
    public static class PremiumProductDecorator extends DealValuationDecorator {
        private BigDecimal premiumThreshold;
        private BigDecimal premiumRate;
        
        public PremiumProductDecorator(DealValuationService decoratedService, 
                                      BigDecimal premiumThreshold, 
                                      BigDecimal premiumRate) {
            super(decoratedService);
            this.premiumThreshold = premiumThreshold;
            this.premiumRate = premiumRate;
        }
        
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            BigDecimal baseValue = super.calculateDealValue(deal);
            
            // Apply premium for high-value products
            for (DealProduct product : deal.getProducts()) {
                if (product.getPrice().compareTo(premiumThreshold) > 0) {
                    BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                    BigDecimal premium = productValue.multiply(premiumRate);
                    baseValue = baseValue.add(premium);
                }
            }
            
            return baseValue;
        }
        
        @Override
        public String getServiceName() {
            return super.getServiceName() + " with Premium Product Analysis";
        }
    }
    
    /**
     * SeasonalDiscountDecorator - Applies seasonal discounts.
     * This is a ConcreteDecorator in the Decorator pattern.
     */
    public static class SeasonalDiscountDecorator extends DealValuationDecorator {
        private Map<String, BigDecimal> seasonalDiscounts;
        
        public SeasonalDiscountDecorator(DealValuationService decoratedService) {
            super(decoratedService);
            
            // Initialize seasonal discounts
            this.seasonalDiscounts = new HashMap<>();
            seasonalDiscounts.put("SUMMER", new BigDecimal("0.05")); // 5% summer discount
            seasonalDiscounts.put("WINTER", new BigDecimal("0.10")); // 10% winter discount
            seasonalDiscounts.put("SPRING", new BigDecimal("0.03")); // 3% spring discount
            seasonalDiscounts.put("FALL", new BigDecimal("0.07"));   // 7% fall discount
        }
        
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            BigDecimal baseValue = super.calculateDealValue(deal);
            
            // Determine current season (simplified)
            String currentSeason = getCurrentSeason();
            
            // Apply seasonal discount if applicable
            if (seasonalDiscounts.containsKey(currentSeason)) {
                BigDecimal discountRate = seasonalDiscounts.get(currentSeason);
                BigDecimal discount = baseValue.multiply(discountRate);
                return baseValue.subtract(discount);
            }
            
            return baseValue;
        }
        
        private String getCurrentSeason() {
            // Simplified season determination based on month
            int month = LocalDate.now().getMonthValue();
            
            if (month >= 3 && month <= 5) {
                return "SPRING";
            } else if (month >= 6 && month <= 8) {
                return "SUMMER";
            } else if (month >= 9 && month <= 11) {
                return "FALL";
            } else {
                return "WINTER";
            }
        }
        
        @Override
        public String getServiceName() {
            return super.getServiceName() + " with Seasonal Discounts";
        }
    }
    
    /**
     * CachingDealValuationProxy - A proxy that caches calculation results.
     * This is a Proxy in the Proxy pattern.
     */
    public static class CachingDealValuationProxy implements DealValuationService {
        private DealValuationService realService;
        private Map<String, BigDecimal> cache;
        
        public CachingDealValuationProxy(DealValuationService realService) {
            this.realService = realService;
            this.cache = new HashMap<>();
        }
        
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            String dealId = deal.getId();
            
            // Check if result is in cache
            if (cache.containsKey(dealId)) {
                System.out.println("Returning cached value for deal: " + dealId);
                return cache.get(dealId);
            }
            
            // Calculate value using the real service
            System.out.println("Calculating value for deal: " + dealId);
            BigDecimal value = realService.calculateDealValue(deal);
            
            // Store in cache
            cache.put(dealId, value);
            
            return value;
        }
        
        @Override
        public String getServiceName() {
            return "Caching Proxy for " + realService.getServiceName();
        }
        
        /**
         * Clear the cache.
         */
        public void clearCache() {
            cache.clear();
        }
    }
    
    /**
     * ProtectionDealValuationProxy - A proxy that controls access based on user roles.
     * This is a Proxy in the Proxy pattern.
     */
    public static class ProtectionDealValuationProxy implements DealValuationService {
        private DealValuationService realService;
        private User currentUser;
        
        public ProtectionDealValuationProxy(DealValuationService realService, User currentUser) {
            this.realService = realService;
            this.currentUser = currentUser;
        }
        
        @Override
        public BigDecimal calculateDealValue(Deal deal) {
            // Check if user has permission to access the deal
            if (!hasPermission(deal)) {
                throw new SecurityException("User does not have permission to access this deal");
            }
            
            return realService.calculateDealValue(deal);
        }
        
        private boolean hasPermission(Deal deal) {
            // Allow access if user is the sales rep for the deal
            if (deal.getSalesRepId().equals(currentUser.getId())) {
                return true;
            }
            
            // Allow access if user is a sales manager or admin
            if (currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN) ||
                currentUser.hasRole(UserRole.SYSTEM_ADMIN)) {
                return true;
            }
            
            return false;
        }
        
        @Override
        public String getServiceName() {
            return "Protection Proxy for " + realService.getServiceName();
        }
    }
    
    /**
     * Client code that demonstrates how to use the ProxyDecorator
     */
    public static void main(String[] args) {
        System.out.println("===== Proxy + Decorator Pattern Combination Example =====\n");
        
        // Create a sample deal
        Deal sampleDeal = createSampleDeal();
        
        // Create a sample user
        User salesRep = createSampleUser();
        
        // Create the base service
        DealValuationService baseService = new StandardDealValuationService();
        
        // Decorate the service with additional behaviors
        DealValuationService decoratedService = new PremiumProductDecorator(
            new SeasonalDiscountDecorator(baseService),
            new BigDecimal("1000.00"),  // Premium threshold
            new BigDecimal("0.02")      // Premium rate (2%)
        );
        
        // Create a protection proxy for the decorated service
        DealValuationService protectionProxy = new ProtectionDealValuationProxy(decoratedService, salesRep);
        
        // Create a caching proxy for the protection proxy
        CachingDealValuationProxy cachingProxy = new CachingDealValuationProxy(protectionProxy);
        
        // Use the combined proxy-decorated service
        System.out.println("Service: " + cachingProxy.getServiceName());
        
        // First call - will calculate and cache
        System.out.println("\nFirst call:");
        BigDecimal value1 = cachingProxy.calculateDealValue(sampleDeal);
        System.out.println("Deal Value: $" + value1);
        
        // Second call - will use cache
        System.out.println("\nSecond call:");
        BigDecimal value2 = cachingProxy.calculateDealValue(sampleDeal);
        System.out.println("Deal Value: $" + value2);
        
        // Clear cache and call again
        System.out.println("\nAfter clearing cache:");
        cachingProxy.clearCache();
        BigDecimal value3 = cachingProxy.calculateDealValue(sampleDeal);
        System.out.println("Deal Value: $" + value3);
        
        System.out.println("\nBenefits of combining Proxy and Decorator patterns:");
        System.out.println("1. Control access to objects (Proxy)");
        System.out.println("2. Add behaviors to objects dynamically (Decorator)");
        System.out.println("3. Separate concerns: access control vs. additional functionality");
        System.out.println("4. Flexible composition of different proxies and decorators");
    }
    
    /**
     * Helper method to create a sample deal
     */
    private static Deal createSampleDeal() {
        Deal deal = new Deal();
        deal.setId("deal-1");
        deal.setTitle("Enterprise Software Package");
        deal.setSalesRepId("user-1");
        
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
     * Helper method to create a sample user
     */
    private static User createSampleUser() {
        User user = new User();
        user.setId("user-1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.addRole(UserRole.SALES_REP);
        return user;
    }
}