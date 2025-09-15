package com.chapman.edu.commissions.patterns.structural.decorator;

import java.math.BigDecimal;

/**
 * This class demonstrates the structure of the Decorator Pattern.
 * 
 * The Decorator Pattern allows behavior to be added to individual objects, 
 * either statically or dynamically, without affecting the behavior of other 
 * objects from the same class.
 * 
 * Key components:
 * 1. Component Interface - defines the interface for objects that can have responsibilities added to them
 * 2. Concrete Component - defines an object to which additional responsibilities can be attached
 * 3. Decorator - maintains a reference to a Component object and defines an interface that conforms to Component's interface
 * 4. Concrete Decorator - adds responsibilities to the component
 */
public class DecoratorPatternStructure {

    /**
     * Component Interface - defines the interface for objects that can have responsibilities added to them
     */
    public interface Commission {
        BigDecimal calculate();
        String getDescription();
    }

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
     * Decorator - maintains a reference to a Component object and defines an interface that conforms to Component's interface
     */
    public static abstract class CommissionDecorator implements Commission {
        protected Commission decoratedCommission;
        
        public CommissionDecorator(Commission decoratedCommission) {
            this.decoratedCommission = decoratedCommission;
        }
        
        @Override
        public BigDecimal calculate() {
            return decoratedCommission.calculate();
        }
        
        @Override
        public String getDescription() {
            return decoratedCommission.getDescription();
        }
    }

    /**
     * Concrete Decorator - adds responsibilities to the component
     */
    public static class BonusDecorator extends CommissionDecorator {
        private BigDecimal bonusAmount;
        
        public BonusDecorator(Commission decoratedCommission, BigDecimal bonusAmount) {
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
    public static class AcceleratorDecorator extends CommissionDecorator {
        private BigDecimal multiplier;
        
        public AcceleratorDecorator(Commission decoratedCommission, BigDecimal multiplier) {
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
    public static class TaxDecorator extends CommissionDecorator {
        private BigDecimal taxRate;
        
        public TaxDecorator(Commission decoratedCommission, BigDecimal taxRate) {
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
}