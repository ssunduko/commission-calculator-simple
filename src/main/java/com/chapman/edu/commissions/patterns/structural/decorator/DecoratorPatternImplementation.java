package com.chapman.edu.commissions.patterns.structural.decorator;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.patterns.structural.decorator.DecoratorPatternStructure.*;

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
     * Concrete Component - defines an object to which additional responsibilities can be attached
     */
    public static class BaseCommission implements Commission {
        private BigDecimal amount;

        public BaseCommission(BigDecimal amount) {
            this.amount = amount;
        }

        @Override
        public BigDecimal calculate() {
            return amount;
        }

        @Override
        public String getDescription() {
            return "Base Commission";
        }
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
     * Concrete Decorator - adds responsibilities to the component
     */
    public static class BonusDecorator extends DecoratorPatternStructure.CommissionDecorator {
        private BigDecimal bonusAmount;

        public BonusDecorator(DecoratorPatternStructure.Commission decoratedCommission, BigDecimal bonusAmount) {
            super(decoratedCommission);
            this.bonusAmount = bonusAmount;
        }

        @Override
        public BigDecimal calculate() {
            return decoratedCommission.calculate().add(bonusAmount);
        }

        @Override
        public String getDescription() {
            return decoratedCommission.getDescription() + " + Bonus";
        }
    }

    /**
     * Concrete Decorator - adds responsibilities to the component
     */
    public static class AcceleratorDecorator extends DecoratorPatternStructure.CommissionDecorator {
        private BigDecimal multiplier;

        public AcceleratorDecorator(DecoratorPatternStructure.Commission decoratedCommission, BigDecimal multiplier) {
            super(decoratedCommission);
            this.multiplier = multiplier;
        }

        @Override
        public BigDecimal calculate() {
            return decoratedCommission.calculate().multiply(multiplier);
        }

        @Override
        public String getDescription() {
            return decoratedCommission.getDescription() + " with Accelerator";
        }
    }

    /**
     * Concrete Decorator - adds responsibilities to the component
     */
    public static class TaxDecorator extends DecoratorPatternStructure.CommissionDecorator {
        private BigDecimal taxRate;

        public TaxDecorator(DecoratorPatternStructure.Commission decoratedCommission, BigDecimal taxRate) {
            super(decoratedCommission);
            this.taxRate = taxRate;
        }

        @Override
        public BigDecimal calculate() {
            BigDecimal commission = decoratedCommission.calculate();
            BigDecimal taxAmount = commission.multiply(taxRate);
            return commission.subtract(taxAmount);
        }

        @Override
        public String getDescription() {
            return decoratedCommission.getDescription() + " after Tax";
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