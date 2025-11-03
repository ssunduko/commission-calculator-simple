package com.chapman.edu.commissions.patterns.structural.decorator;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.util.List;

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
     * Component Interface - defines the interface for objects that can have responsibilities added to them
     */
    public interface DealComponent {
        String getTitle();
        BigDecimal calculateValue();
        String getSalesRepId();
        List<DealProduct> getProducts();
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
}