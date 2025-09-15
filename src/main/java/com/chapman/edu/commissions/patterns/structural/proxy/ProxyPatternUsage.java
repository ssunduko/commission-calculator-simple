package com.chapman.edu.commissions.patterns.structural.proxy;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.util.Map;

/**
 * This class demonstrates how to use the different proxy implementations
 * in a client application. It provides concrete examples of how to apply
 * the proxy pattern in real-world scenarios.
 */
public class ProxyPatternUsage {

    public static void main(String[] args) {
        System.out.println("===== Proxy Pattern Usage Examples =====\n");
        
        // Create test users with different roles
        User adminUser = createUser("admin", UserRole.FINANCE_ADMIN);
        User salesUser = createUser("sales", UserRole.SALES_REP);
        User regularUser = createUser("regular", UserRole.SYSTEM_ADMIN);
        
        // Example 1: Virtual Proxy
        demonstrateVirtualProxy();
        
        // Example 2: Protection Proxy
        demonstrateProtectionProxy(adminUser, salesUser, regularUser);
        
        // Example 3: Caching Proxy
        demonstrateCachingProxy();
        
        // Example 4: Smart Proxy
        demonstrateSmartProxy();
        
        // Example 5: Remote Proxy
        demonstrateRemoteProxy();
    }
    
    /**
     * Demonstrates the Virtual Proxy implementation
     */
    private static void demonstrateVirtualProxy() {
        System.out.println("\n===== Virtual Proxy Example =====");
        System.out.println("Virtual Proxy delays the creation of expensive objects until they are needed.");
        
        // Create the virtual proxy
        ProxyPatternImplementation.CommissionCalculationService virtualProxy = 
            new ProxyPatternImplementation.VirtualProxyCommissionService();
        
        System.out.println("Virtual proxy created, but the real service hasn't been instantiated yet.");
        System.out.println("No expensive resources have been allocated at this point.");
        
        // First call will create the real service
        System.out.println("\nMaking first call to the proxy...");
        CommissionCalculation calculation1 = virtualProxy.calculateCommission("deal-1", "user-1");
        System.out.println("Calculation result: " + calculation1.getId());
        
        // Subsequent calls will reuse the existing real service
        System.out.println("\nMaking second call to the proxy...");
        CommissionCalculation calculation2 = virtualProxy.calculateCommission("deal-2", "user-1");
        System.out.println("Calculation result: " + calculation2.getId());
        
        System.out.println("\nVirtual Proxy Benefits:");
        System.out.println("1. Lazy initialization of expensive resources");
        System.out.println("2. Transparent to the client code");
        System.out.println("3. Improves application startup time");
    }
    
    /**
     * Demonstrates the Protection Proxy implementation
     */
    private static void demonstrateProtectionProxy(User adminUser, User salesUser, User regularUser) {
        System.out.println("\n===== Protection Proxy Example =====");
        System.out.println("Protection Proxy controls access to the real service based on user permissions.");
        
        // Create protection proxies for different users
        ProxyPatternImplementation.CommissionCalculationService adminProxy = 
            new ProxyPatternImplementation.ProtectionProxyCommissionService(adminUser);
        
        ProxyPatternImplementation.CommissionCalculationService salesProxy = 
            new ProxyPatternImplementation.ProtectionProxyCommissionService(salesUser);
        
        ProxyPatternImplementation.CommissionCalculationService regularProxy = 
            new ProxyPatternImplementation.ProtectionProxyCommissionService(regularUser);
        
        // Admin user can calculate and save commissions
        System.out.println("\nAdmin user attempting to calculate commission:");
        CommissionCalculation adminCalculation = adminProxy.calculateCommission("deal-3", "user-2");
        
        System.out.println("\nAdmin user attempting to save calculation:");
        try {
            adminProxy.saveCalculation(adminCalculation);
            System.out.println("Admin successfully saved the calculation.");
        } catch (SecurityException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Sales user can calculate but not save commissions
        System.out.println("\nSales user attempting to calculate commission:");
        CommissionCalculation salesCalculation = salesProxy.calculateCommission("deal-4", "user-3");
        
        System.out.println("\nSales user attempting to save calculation:");
        try {
            salesProxy.saveCalculation(salesCalculation);
            System.out.println("Sales user successfully saved the calculation.");
        } catch (SecurityException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Regular user can't calculate or save commissions
        System.out.println("\nRegular user attempting to calculate commission:");
        try {
            CommissionCalculation regularCalculation = regularProxy.calculateCommission("deal-5", "user-4");
            System.out.println("Regular user successfully calculated commission.");
        } catch (SecurityException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("\nProtection Proxy Benefits:");
        System.out.println("1. Enforces access control policies");
        System.out.println("2. Centralizes security logic");
        System.out.println("3. Transparent to the client code");
    }
    
    /**
     * Demonstrates the Caching Proxy implementation
     */
    private static void demonstrateCachingProxy() {
        System.out.println("\n===== Caching Proxy Example =====");
        System.out.println("Caching Proxy stores results to improve performance for repeated calls.");
        
        // Create the caching proxy
        ProxyPatternImplementation.CachingProxyCommissionService cachingProxy = 
            new ProxyPatternImplementation.CachingProxyCommissionService();
        
        // First call will calculate and cache the result
        System.out.println("\nFirst call for deal-6 (not cached):");
        long startTime = System.currentTimeMillis();
        CommissionCalculation calculation1 = cachingProxy.calculateCommission("deal-6", "user-5");
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        
        // Second call for the same parameters will use the cached result
        System.out.println("\nSecond call for deal-6 (should be cached):");
        startTime = System.currentTimeMillis();
        CommissionCalculation calculation2 = cachingProxy.calculateCommission("deal-6", "user-5");
        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        
        // Different parameters will calculate a new result
        System.out.println("\nCall for deal-7 (not cached):");
        startTime = System.currentTimeMillis();
        CommissionCalculation calculation3 = cachingProxy.calculateCommission("deal-7", "user-5");
        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        
        // Clear the cache
        System.out.println("\nClearing the cache...");
        cachingProxy.clearCache();
        
        // After clearing, even the same parameters will recalculate
        System.out.println("\nCall for deal-6 after cache cleared (not cached):");
        startTime = System.currentTimeMillis();
        CommissionCalculation calculation4 = cachingProxy.calculateCommission("deal-6", "user-5");
        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
        
        System.out.println("\nCaching Proxy Benefits:");
        System.out.println("1. Improves performance for repeated operations");
        System.out.println("2. Reduces load on expensive resources");
        System.out.println("3. Transparent to the client code");
    }
    
    /**
     * Demonstrates the Smart Proxy implementation
     */
    private static void demonstrateSmartProxy() {
        System.out.println("\n===== Smart Proxy Example =====");
        System.out.println("Smart Proxy adds additional housekeeping like reference counting and logging.");
        
        // Create the smart proxy
        ProxyPatternImplementation.SmartProxyCommissionService smartProxy = 
            new ProxyPatternImplementation.SmartProxyCommissionService();
        
        // Make several calls to the proxy
        System.out.println("\nMaking calls to the smart proxy:");
        smartProxy.calculateCommission("deal-8", "user-6");
        smartProxy.calculateCommission("deal-9", "user-6");
        smartProxy.calculateCommission("deal-8", "user-7");
        
        // Get reference count
        System.out.println("\nCurrent reference count: " + smartProxy.getReferenceCount());
        
        // Get access statistics
        System.out.println("\nAccess statistics:");
        Map<String, Integer> statistics = smartProxy.getAccessStatistics();
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            System.out.println("Deal " + entry.getKey() + " accessed " + entry.getValue() + " times");
        }
        
        System.out.println("\nSmart Proxy Benefits:");
        System.out.println("1. Provides additional functionality like logging and metrics");
        System.out.println("2. Manages resources through reference counting");
        System.out.println("3. Enables advanced monitoring and debugging");
    }
    
    /**
     * Demonstrates the Remote Proxy implementation
     */
    private static void demonstrateRemoteProxy() {
        System.out.println("\n===== Remote Proxy Example =====");
        System.out.println("Remote Proxy represents an object that exists in a different address space.");
        
        // Create the remote proxy
        ProxyPatternImplementation.CommissionCalculationService remoteProxy = 
            new ProxyPatternImplementation.RemoteProxyCommissionService("https://remote-commission-service.example.com");
        
        // Make a call to the remote service
        System.out.println("\nMaking call to remote service:");
        CommissionCalculation calculation = remoteProxy.calculateCommission("deal-10", "user-8");
        System.out.println("Received calculation with ID: " + calculation.getId());
        
        // Save the calculation to the remote service
        System.out.println("\nSaving calculation to remote service:");
        remoteProxy.saveCalculation(calculation);
        
        System.out.println("\nRemote Proxy Benefits:");
        System.out.println("1. Hides the complexity of remote communication");
        System.out.println("2. Handles network-related concerns (serialization, error handling)");
        System.out.println("3. Provides a local interface to a remote service");
    }
    
    /**
     * Helper method to create a user with a specific role
     */
    private static User createUser(String username, UserRole role) {
        User user = new User();
        user.setId(username + "-id");
        user.setUsername(username);
        user.setFirstName(username.substring(0, 1).toUpperCase() + username.substring(1));
        user.setLastName("User");
        user.addRole(role);
        return user;
    }
}