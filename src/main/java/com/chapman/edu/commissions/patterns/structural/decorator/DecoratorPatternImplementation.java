package com.chapman.edu.commissions.patterns.structural.decorator;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This class demonstrates a concrete implementation of the Decorator Pattern
 * using the model classes from the commission calculator application.
 * 
 * In this implementation, we create decorators for the Deal class to add
 * additional behaviors and calculations without modifying the original class.
 */
public class DecoratorPatternImplementation {

    /**
     * Component Interface - defines the interface for objects that can have responsibilities added to them
     */
    public interface DealComponent {
        String getTitle();
        BigDecimal calculateValue();
        String getSalesRepId();
        List<DealProduct> getProducts();
    }

    /**
     * Concrete Component - adapts the Deal class to the DealComponent interface
     */
    public static class BasicDeal implements DealComponent {
        private Deal deal;
        
        public BasicDeal(Deal deal) {
            this.deal = deal;
        }
        
        @Override
        public String getTitle() {
            return deal.getTitle();
        }
        
        @Override
        public BigDecimal calculateValue() {
            return deal.getValue();
        }
        
        @Override
        public String getSalesRepId() {
            return deal.getSalesRepId();
        }
        
        @Override
        public List<DealProduct> getProducts() {
            return deal.getProducts();
        }
    }

    /**
     * Decorator - maintains a reference to a DealComponent object and defines an interface that conforms to DealComponent's interface
     */
    public static abstract class DealDecorator implements DealComponent {
        protected DealComponent decoratedDeal;
        
        public DealDecorator(DealComponent decoratedDeal) {
            this.decoratedDeal = decoratedDeal;
        }
        
        @Override
        public String getTitle() {
            return decoratedDeal.getTitle();
        }
        
        @Override
        public BigDecimal calculateValue() {
            return decoratedDeal.calculateValue();
        }
        
        @Override
        public String getSalesRepId() {
            return decoratedDeal.getSalesRepId();
        }
        
        @Override
        public List<DealProduct> getProducts() {
            return decoratedDeal.getProducts();
        }
    }

    /**
     * Concrete Decorator - adds a discount to the deal value
     */
    public static class DiscountDecorator extends DealDecorator {
        private BigDecimal discountRate;
        
        public DiscountDecorator(DealComponent decoratedDeal, BigDecimal discountRate) {
            super(decoratedDeal);
            this.discountRate = discountRate;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal originalValue = decoratedDeal.calculateValue();
            BigDecimal discountAmount = originalValue.multiply(discountRate);
            return originalValue.subtract(discountAmount);
        }
        
        @Override
        public String getTitle() {
            return decoratedDeal.getTitle() + " (Discounted)";
        }
    }

    /**
     * Concrete Decorator - adds a premium to the deal value
     */
    public static class PremiumDecorator extends DealDecorator {
        private BigDecimal premiumRate;
        
        public PremiumDecorator(DealComponent decoratedDeal, BigDecimal premiumRate) {
            super(decoratedDeal);
            this.premiumRate = premiumRate;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal originalValue = decoratedDeal.calculateValue();
            BigDecimal premiumAmount = originalValue.multiply(premiumRate);
            return originalValue.add(premiumAmount);
        }
        
        @Override
        public String getTitle() {
            return decoratedDeal.getTitle() + " (Premium)";
        }
    }

    /**
     * Concrete Decorator - adds urgency handling to the deal
     */
    public static class UrgencyDecorator extends DealDecorator {
        private LocalDate deadline;
        
        public UrgencyDecorator(DealComponent decoratedDeal, LocalDate deadline) {
            super(decoratedDeal);
            this.deadline = deadline;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal originalValue = decoratedDeal.calculateValue();
            
            // If the current date is after the deadline, apply a penalty
            if (LocalDate.now().isAfter(deadline)) {
                return originalValue.multiply(new BigDecimal("0.9")); // 10% penalty
            }
            
            // If the current date is within 7 days of the deadline, apply a small bonus
            if (LocalDate.now().plusDays(7).isAfter(deadline)) {
                return originalValue.multiply(new BigDecimal("1.05")); // 5% bonus for urgency
            }
            
            return originalValue;
        }
        
        @Override
        public String getTitle() {
            if (LocalDate.now().isAfter(deadline)) {
                return decoratedDeal.getTitle() + " (Overdue)";
            } else if (LocalDate.now().plusDays(7).isAfter(deadline)) {
                return decoratedDeal.getTitle() + " (Urgent)";
            } else {
                return decoratedDeal.getTitle();
            }
        }
    }

    /**
     * Concrete Decorator - adds logging functionality to the deal
     */
    public static class LoggingDecorator extends DealDecorator {
        public LoggingDecorator(DealComponent decoratedDeal) {
            super(decoratedDeal);
        }
        
        @Override
        public String getTitle() {
            String title = decoratedDeal.getTitle();
            System.out.println("Getting title: " + title);
            return title;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal value = decoratedDeal.calculateValue();
            System.out.println("Calculating value: " + value);
            return value;
        }
        
        @Override
        public String getSalesRepId() {
            String salesRepId = decoratedDeal.getSalesRepId();
            System.out.println("Getting sales rep ID: " + salesRepId);
            return salesRepId;
        }
        
        @Override
        public List<DealProduct> getProducts() {
            List<DealProduct> products = decoratedDeal.getProducts();
            System.out.println("Getting products: " + products.size() + " products found");
            return products;
        }
    }
}