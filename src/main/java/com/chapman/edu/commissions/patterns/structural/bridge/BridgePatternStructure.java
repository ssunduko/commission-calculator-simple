package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionPlan;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates the structure of the Bridge Pattern.
 * 
 * The Bridge Pattern is a structural design pattern that separates an abstraction from its implementation
 * so that the two can vary independently. It involves an interface which acts as a bridge between
 * the abstraction class and implementation classes.
 * 
 * Key components of the Bridge Pattern:
 * 1. Abstraction - Defines the abstract interface and maintains a reference to the implementor
 * 2. Refined Abstraction - Extends the abstraction and provides more specialized operations
 * 3. Implementor - Defines the interface for implementation classes
 * 4. Concrete Implementor - Implements the Implementor interface
 * 
 * When to use the Bridge Pattern:
 * - When you want to avoid a permanent binding between an abstraction and its implementation
 * - When both the abstractions and their implementations should be extensible through subclasses
 * - When changes in the implementation should not impact the client code
 * - When you have a proliferation of classes resulting from a coupled interface and numerous implementations
 */
public class BridgePatternStructure {

    /**
     * Implementor
     * This interface defines the operations that concrete implementors must implement.
     */
    public interface CommissionCalculator {
        BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan);
    }
    
    /**
     * Concrete Implementor A
     * This class provides a specific implementation of the CommissionCalculator interface.
     */
    public static class StandardCommissionCalculator implements CommissionCalculator {
        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            // In a real implementation, this would calculate the commission based on standard rules
            return deal.getValue().multiply(new BigDecimal("0.05")); // 5% commission
        }
    }
    
    /**
     * Concrete Implementor B
     * This class provides another implementation of the CommissionCalculator interface.
     */
    public static class TieredCommissionCalculator implements CommissionCalculator {
        @Override
        public BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan) {
            // In a real implementation, this would calculate the commission based on tiered rules
            BigDecimal dealValue = deal.getValue();
            
            if (dealValue.compareTo(new BigDecimal("10000")) > 0) {
                return dealValue.multiply(new BigDecimal("0.07")); // 7% commission for deals over $10,000
            } else {
                return dealValue.multiply(new BigDecimal("0.05")); // 5% commission for deals up to $10,000
            }
        }
    }
    
    /**
     * Abstraction
     * This class defines the abstract interface and maintains a reference to the implementor.
     */
    public static abstract class CommissionService {
        protected CommissionCalculator calculator;
        
        public CommissionService(CommissionCalculator calculator) {
            this.calculator = calculator;
        }
        
        public abstract BigDecimal processCommission(String dealId, String userId);
    }
    
    /**
     * Refined Abstraction A
     * This class extends the abstraction and provides more specialized operations.
     */
    public static class SalesCommissionService extends CommissionService {
        private DealRepository dealRepository;
        private UserRepository userRepository;
        
        public SalesCommissionService(CommissionCalculator calculator, 
                                     DealRepository dealRepository,
                                     UserRepository userRepository) {
            super(calculator);
            this.dealRepository = dealRepository;
            this.userRepository = userRepository;
        }
        
        @Override
        public BigDecimal processCommission(String dealId, String userId) {
            Deal deal = dealRepository.getDealById(dealId);
            User user = userRepository.getUserById(userId);
            CommissionPlan plan = userRepository.getCommissionPlan(userId);
            
            return calculator.calculateCommission(deal, user, plan);
        }
    }
    
    /**
     * Refined Abstraction B
     * This class provides another extension of the abstraction.
     */
    public static class PartnerCommissionService extends CommissionService {
        private DealRepository dealRepository;
        private UserRepository userRepository;
        
        public PartnerCommissionService(CommissionCalculator calculator,
                                       DealRepository dealRepository,
                                       UserRepository userRepository) {
            super(calculator);
            this.dealRepository = dealRepository;
            this.userRepository = userRepository;
        }
        
        @Override
        public BigDecimal processCommission(String dealId, String userId) {
            Deal deal = dealRepository.getDealById(dealId);
            User user = userRepository.getUserById(userId);
            CommissionPlan plan = userRepository.getCommissionPlan(userId);
            
            // Partner commissions might have additional logic
            BigDecimal commission = calculator.calculateCommission(deal, user, plan);
            
            // Apply partner-specific adjustments (e.g., partner tier bonuses)
            return commission.multiply(new BigDecimal("1.1")); // 10% bonus for partners
        }
    }
    
    /**
     * Helper interfaces for the example
     */
    public interface DealRepository {
        Deal getDealById(String dealId);
    }
    
    public interface UserRepository {
        User getUserById(String userId);
        CommissionPlan getCommissionPlan(String userId);
    }
    
    /**
     * Client
     * This class demonstrates how to use the Bridge Pattern.
     */
    public static class Client {
        public static void main(String[] args) {
            // Create implementors
            CommissionCalculator standardCalculator = new StandardCommissionCalculator();
            CommissionCalculator tieredCalculator = new TieredCommissionCalculator();
            
            // Create repositories (would be actual implementations in a real system)
            DealRepository dealRepository = dealId -> new Deal("Sample Deal", new BigDecimal("10000"), "salesRep123");
            UserRepository userRepository = new UserRepository() {
                @Override
                public User getUserById(String userId) {
                    return new User("username", "email@example.com", "John", "Doe");
                }
                
                @Override
                public CommissionPlan getCommissionPlan(String userId) {
                    return new CommissionPlan("Standard Plan", null);
                }
            };
            
            // Create abstractions with different implementations
            CommissionService salesService = new SalesCommissionService(standardCalculator, dealRepository, userRepository);
            CommissionService partnerService = new PartnerCommissionService(tieredCalculator, dealRepository, userRepository);
            
            // Use the abstractions
            BigDecimal salesCommission = salesService.processCommission("deal123", "user456");
            BigDecimal partnerCommission = partnerService.processCommission("deal123", "user789");
            
            System.out.println("Sales Commission: " + salesCommission);
            System.out.println("Partner Commission: " + partnerCommission);
        }
    }
}