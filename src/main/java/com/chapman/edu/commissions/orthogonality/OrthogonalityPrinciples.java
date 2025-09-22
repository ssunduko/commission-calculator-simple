package com.chapman.edu.commissions.orthogonality;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

/**
 * Demonstrates key principles of orthogonality in software design.
 * 
 * Orthogonality is a design principle where changing one component doesn't affect others.
 * This class showcases various techniques to achieve orthogonality in code.
 */
public class OrthogonalityPrinciples {

    /**
     * Principle 1: Separation of Concerns
     * 
     * Separate different aspects of the program into distinct sections that address
     * different concerns. This makes the code more modular and easier to maintain.
     */
    public static class SeparationOfConcerns {
        
        // Deal-related concerns
        public static class DealService {
            public BigDecimal calculateDealValue(Deal deal) {
                // Implementation focused only on deal value calculation
                return deal.calculateTotalValue();
            }
        }
        
        // User-related concerns
        public static class UserService {
            public boolean isUserAuthorizedForDeal(User user, Deal deal) {
                // Implementation focused only on user authorization
                return user.getId().equals(deal.getSalesRepId()) || user.isSalesManager();
            }
        }
        
        // Commission-related concerns
        public static class CommissionService {
            public CommissionCalculation calculateCommission(Deal deal, User user) {
                // Implementation focused only on commission calculation
                // (simplified for example purposes)
                CommissionCalculation calculation = new CommissionCalculation();
                calculation.setDealId(deal.getId());
                calculation.setSalesRepId(user.getId());
                return calculation;
            }
        }
    }
    
    /**
     * Principle 2: Single Responsibility Principle
     * 
     * Each class should have only one reason to change. This principle is closely
     * related to orthogonality as it promotes independence between components.
     */
    public static class SingleResponsibilityPrinciple {
        
        // This class has a single responsibility: validating deals
        public static class DealValidator {
            public boolean isValid(Deal deal) {
                if (deal == null) return false;
                if (deal.getTitle() == null || deal.getTitle().isEmpty()) return false;
                if (deal.getSalesRepId() == null || deal.getSalesRepId().isEmpty()) return false;
                if (deal.getProducts() == null || deal.getProducts().isEmpty()) return false;
                return true;
            }
        }
        
        // This class has a single responsibility: formatting deal information
        public static class DealFormatter {
            public String format(Deal deal) {
                return String.format("Deal: %s, Value: $%s, Status: %s",
                        deal.getTitle(),
                        deal.calculateTotalValue(),
                        deal.getStatus());
            }
        }
    }
    
    /**
     * Principle 3: Dependency Injection
     * 
     * Instead of creating dependencies inside a class, inject them from outside.
     * This reduces coupling and makes the code more testable and flexible.
     */
    public static class DependencyInjection {
        
        public interface DealRepository {
            Deal findById(String id);
            void save(Deal deal);
        }
        
        public static class DealService {
            private final DealRepository repository;
            
            // Dependencies are injected through the constructor
            public DealService(DealRepository repository) {
                this.repository = repository;
            }
            
            public Deal getDeal(String id) {
                return repository.findById(id);
            }
            
            public void saveDeal(Deal deal) {
                repository.save(deal);
            }
        }
    }
    
    /**
     * Principle 4: Interface Segregation
     * 
     * Create specific interfaces rather than general-purpose ones.
     * This allows clients to depend only on the methods they actually use.
     */
    public static class InterfaceSegregation {
        
        // Specific interface for deal value calculation
        public interface DealValueCalculator {
            BigDecimal calculateValue(Deal deal);
        }
        
        // Specific interface for deal validation
        public interface DealValidator {
            boolean isValid(Deal deal);
        }
        
        // Implementation that uses both interfaces
        public static class DealProcessor implements DealValueCalculator, DealValidator {
            @Override
            public BigDecimal calculateValue(Deal deal) {
                return deal.calculateTotalValue();
            }
            
            @Override
            public boolean isValid(Deal deal) {
                return deal != null && deal.getProducts() != null && !deal.getProducts().isEmpty();
            }
        }
    }
    
    /**
     * Principle 5: Pure Functions
     * 
     * Functions that always produce the same output for the same input and have no side effects.
     * Pure functions are inherently orthogonal as they don't depend on or modify external state.
     */
    public static class PureFunctions {
        
        // Pure function: always returns the same result for the same input, no side effects
        public static BigDecimal calculateDiscountedValue(BigDecimal originalValue, BigDecimal discountPercentage) {
            BigDecimal discountFactor = BigDecimal.ONE.subtract(
                    discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            return originalValue.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        }
        
        // Pure function: transforms a list without modifying the original
        public static <T, R> List<R> map(List<T> items, Function<T, R> mapper) {
            return items.stream().map(mapper).toList();
        }
    }
    
    /**
     * Principle 6: Immutability
     * 
     * Immutable objects cannot be changed after creation. This eliminates temporal coupling
     * and makes code more thread-safe and easier to reason about.
     */
    public static class Immutability {
        
        // Immutable class: all fields are final and there are no setters
        public static final class ImmutableDealSummary {
            private final String id;
            private final String title;
            private final BigDecimal value;
            private final String salesRepName;
            
            public ImmutableDealSummary(String id, String title, BigDecimal value, String salesRepName) {
                this.id = id;
                this.title = title;
                this.value = value;
                this.salesRepName = salesRepName;
            }
            
            // Only getters, no setters
            public String getId() { return id; }
            public String getTitle() { return title; }
            public BigDecimal getValue() { return value; }
            public String getSalesRepName() { return salesRepName; }
            
            // Create a new instance with modified values instead of changing existing ones
            public ImmutableDealSummary withValue(BigDecimal newValue) {
                return new ImmutableDealSummary(id, title, newValue, salesRepName);
            }
        }
    }
    
    /**
     * Principle 7: Command-Query Separation
     * 
     * Methods should either change state (commands) or return values (queries), but not both.
     * This makes code more predictable and easier to test.
     */
    public static class CommandQuerySeparation {
        
        public static class DealManager {
            private Deal currentDeal;
            
            // Command: changes state but doesn't return a value
            public void setCurrentDeal(Deal deal) {
                this.currentDeal = deal;
            }
            
            // Query: returns a value but doesn't change state
            public Deal getCurrentDeal() {
                return currentDeal;
            }
            
            // Query: returns a value but doesn't change state
            public BigDecimal getCurrentDealValue() {
                return currentDeal != null ? currentDeal.calculateTotalValue() : BigDecimal.ZERO;
            }
        }
    }
    
    /**
     * Demonstrates how these orthogonality principles can be applied together
     * to create a well-designed system.
     */
    public void demonstrateOrthogonalityPrinciples() {
        // Create sample data
        Deal deal = new Deal("Sample Deal", new BigDecimal("10000"), "user123");
        deal.setId("deal123");
        
        DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("1000"));
        deal.addProduct(product);
        
        User user = new User("jdoe", "jdoe@example.com", "John", "Doe");
        user.setId("user123");
        
        // Demonstrate Separation of Concerns
        SeparationOfConcerns.DealService dealService = new SeparationOfConcerns.DealService();
        SeparationOfConcerns.UserService userService = new SeparationOfConcerns.UserService();
        
        BigDecimal dealValue = dealService.calculateDealValue(deal);
        boolean isAuthorized = userService.isUserAuthorizedForDeal(user, deal);
        
        // Demonstrate Single Responsibility Principle
        SingleResponsibilityPrinciple.DealValidator validator = new SingleResponsibilityPrinciple.DealValidator();
        SingleResponsibilityPrinciple.DealFormatter formatter = new SingleResponsibilityPrinciple.DealFormatter();
        
        boolean isValid = validator.isValid(deal);
        String formattedDeal = formatter.format(deal);
        
        // Demonstrate Pure Functions
        BigDecimal discountedValue = PureFunctions.calculateDiscountedValue(dealValue, new BigDecimal("10"));
        
        // Demonstrate Immutability
        Immutability.ImmutableDealSummary summary = new Immutability.ImmutableDealSummary(
                deal.getId(), deal.getTitle(), dealValue, user.getFullName());
        
        // Create a new summary with a different value instead of modifying the existing one
        Immutability.ImmutableDealSummary discountedSummary = summary.withValue(discountedValue);
    }
}