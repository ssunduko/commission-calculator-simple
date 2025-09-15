package com.chapman.edu.commissions.patterns.structural.proxy;

import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.UserRole;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class demonstrates a comprehensive implementation of the Proxy Pattern.
 * 
 * The Proxy Pattern provides a surrogate or placeholder for another object to control
 * access to it. This implementation shows different types of proxies:
 * 
 * 1. Virtual Proxy: Delays the creation of expensive objects until they are needed
 * 2. Protection Proxy: Controls access to the original object based on permissions
 * 3. Remote Proxy: Represents an object in a different address space
 * 4. Caching Proxy: Caches results to improve performance
 * 5. Smart Proxy: Adds additional housekeeping (reference counting, locking)
 */
public class ProxyPatternImplementation {

    /**
     * Subject interface for Commission Calculation Service
     */
    public interface CommissionCalculationService {
        CommissionCalculation calculateCommission(String dealId, String userId);
        void saveCalculation(CommissionCalculation calculation);
    }

    /**
     * RealSubject implementation of Commission Calculation Service
     */
    public static class RealCommissionCalculationService implements CommissionCalculationService {
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            System.out.println("RealCommissionCalculationService: Performing expensive commission calculation for deal: " + dealId);
            
            // Simulate an expensive calculation
            try {
                Thread.sleep(2000); // Simulate 2 seconds of processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // In a real application, this would perform actual calculations based on business rules
            CommissionCalculation calculation = new CommissionCalculation(dealId, userId, new BigDecimal("1000.00"));
            calculation.setId("calc-" + System.currentTimeMillis());
            calculation.setCalculatedBy("RealCommissionCalculationService");
            
            return calculation;
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            System.out.println("RealCommissionCalculationService: Saving calculation to database: " + calculation.getId());
            // In a real application, this would save the calculation to a database
        }
    }

    /**
     * Virtual Proxy for Commission Calculation Service
     * Delays the creation of the expensive RealCommissionCalculationService until it's needed
     */
    public static class VirtualProxyCommissionService implements CommissionCalculationService {
        private CommissionCalculationService realService;
        
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            System.out.println("VirtualProxy: Lazy initialization of the real service");
            
            if (realService == null) {
                System.out.println("VirtualProxy: Creating real service instance for the first time");
                realService = new RealCommissionCalculationService();
            }
            
            return realService.calculateCommission(dealId, userId);
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            if (realService == null) {
                System.out.println("VirtualProxy: Creating real service instance for the first time");
                realService = new RealCommissionCalculationService();
            }
            
            realService.saveCalculation(calculation);
        }
    }

    /**
     * Protection Proxy for Commission Calculation Service
     * Controls access to the real service based on user roles
     */
    public static class ProtectionProxyCommissionService implements CommissionCalculationService {
        private CommissionCalculationService realService;
        private User currentUser;
        
        public ProtectionProxyCommissionService(User currentUser) {
            this.realService = new RealCommissionCalculationService();
            this.currentUser = currentUser;
        }
        
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            // Check if the user has permission to calculate commissions
            if (currentUser.hasRole(UserRole.SALES_REP) || 
                currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN)) {
                
                System.out.println("ProtectionProxy: Access granted for commission calculation");
                return realService.calculateCommission(dealId, userId);
            } else {
                System.out.println("ProtectionProxy: Access denied for commission calculation");
                throw new SecurityException("Access denied: Insufficient privileges to calculate commissions");
            }
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            // Only finance admins can save calculations
            if (currentUser.hasRole(UserRole.FINANCE_ADMIN)) {
                System.out.println("ProtectionProxy: Access granted for saving calculation");
                realService.saveCalculation(calculation);
            } else {
                System.out.println("ProtectionProxy: Access denied for saving calculation");
                throw new SecurityException("Access denied: Only finance administrators can save calculations");
            }
        }
    }

    /**
     * Caching Proxy for Commission Calculation Service
     * Caches calculation results to improve performance
     */
    public static class CachingProxyCommissionService implements CommissionCalculationService {
        private CommissionCalculationService realService;
        private Map<String, CommissionCalculation> cache;
        
        public CachingProxyCommissionService() {
            this.realService = new RealCommissionCalculationService();
            this.cache = new HashMap<>();
        }
        
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            String cacheKey = dealId + "-" + userId;
            
            // Check if the result is in the cache
            if (cache.containsKey(cacheKey)) {
                System.out.println("CachingProxy: Returning cached calculation for deal: " + dealId);
                return cache.get(cacheKey);
            }
            
            // If not in cache, delegate to the real service
            System.out.println("CachingProxy: Cache miss, calculating commission for deal: " + dealId);
            CommissionCalculation calculation = realService.calculateCommission(dealId, userId);
            
            // Store the result in the cache
            cache.put(cacheKey, calculation);
            
            return calculation;
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            // Clear the cache when a calculation is saved
            System.out.println("CachingProxy: Clearing cache before saving calculation");
            cache.clear();
            
            realService.saveCalculation(calculation);
        }
        
        // Method to clear the cache
        public void clearCache() {
            System.out.println("CachingProxy: Manually clearing the cache");
            cache.clear();
        }
    }

    /**
     * Smart Proxy for Commission Calculation Service
     * Adds reference counting and logging functionality
     */
    public static class SmartProxyCommissionService implements CommissionCalculationService {
        private CommissionCalculationService realService;
        private AtomicInteger referenceCount;
        private Map<String, Integer> accessCount;
        
        public SmartProxyCommissionService() {
            this.realService = new RealCommissionCalculationService();
            this.referenceCount = new AtomicInteger(0);
            this.accessCount = new HashMap<>();
        }
        
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            // Increment reference count
            int count = referenceCount.incrementAndGet();
            System.out.println("SmartProxy: Reference count incremented to " + count);
            
            // Track access count for this deal
            accessCount.put(dealId, accessCount.getOrDefault(dealId, 0) + 1);
            System.out.println("SmartProxy: Deal " + dealId + " has been accessed " + accessCount.get(dealId) + " times");
            
            // Log the operation
            System.out.println("SmartProxy: Calculating commission for deal: " + dealId + " at " + System.currentTimeMillis());
            
            try {
                return realService.calculateCommission(dealId, userId);
            } finally {
                // Decrement reference count
                count = referenceCount.decrementAndGet();
                System.out.println("SmartProxy: Reference count decremented to " + count);
            }
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            // Increment reference count
            int count = referenceCount.incrementAndGet();
            System.out.println("SmartProxy: Reference count incremented to " + count);
            
            // Log the operation
            System.out.println("SmartProxy: Saving calculation: " + calculation.getId() + " at " + System.currentTimeMillis());
            
            try {
                realService.saveCalculation(calculation);
            } finally {
                // Decrement reference count
                count = referenceCount.decrementAndGet();
                System.out.println("SmartProxy: Reference count decremented to " + count);
            }
        }
        
        // Method to get the current reference count
        public int getReferenceCount() {
            return referenceCount.get();
        }
        
        // Method to get access statistics
        public Map<String, Integer> getAccessStatistics() {
            return new HashMap<>(accessCount);
        }
    }

    /**
     * Remote Proxy for Commission Calculation Service
     * Represents a service that exists in a different address space
     */
    public static class RemoteProxyCommissionService implements CommissionCalculationService {
        private String remoteServiceUrl;
        
        public RemoteProxyCommissionService(String remoteServiceUrl) {
            this.remoteServiceUrl = remoteServiceUrl;
        }
        
        @Override
        public CommissionCalculation calculateCommission(String dealId, String userId) {
            System.out.println("RemoteProxy: Connecting to remote service at " + remoteServiceUrl);
            
            // In a real application, this would make a network call to a remote service
            System.out.println("RemoteProxy: Sending calculation request for deal: " + dealId);
            
            // Simulate network latency
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Simulate receiving a response from the remote service
            CommissionCalculation calculation = new CommissionCalculation(dealId, userId, new BigDecimal("1000.00"));
            calculation.setId("remote-calc-" + System.currentTimeMillis());
            calculation.setCalculatedBy("RemoteService");
            
            System.out.println("RemoteProxy: Received calculation result from remote service");
            
            return calculation;
        }

        @Override
        public void saveCalculation(CommissionCalculation calculation) {
            System.out.println("RemoteProxy: Connecting to remote service at " + remoteServiceUrl);
            
            // In a real application, this would make a network call to a remote service
            System.out.println("RemoteProxy: Sending save request for calculation: " + calculation.getId());
            
            // Simulate network latency
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("RemoteProxy: Calculation saved successfully on remote service");
        }
    }
}