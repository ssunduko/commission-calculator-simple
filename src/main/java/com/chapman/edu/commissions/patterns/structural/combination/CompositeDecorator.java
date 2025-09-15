package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.DealProduct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates the combination of Composite and Decorator patterns.
 * 
 * Composite Pattern: Allows us to treat individual objects and compositions of objects uniformly
 * Decorator Pattern: Dynamically adds responsibilities to objects without modifying their code
 * 
 * The CompositeDecorator combines these patterns by:
 * 1. Using the Composite pattern to create a tree structure of sales components
 * 2. Using the Decorator pattern to add additional behaviors to these components
 */
public class CompositeDecorator {

    /**
     * SalesComponent - The component interface for the Composite pattern
     * This is also the Component interface for the Decorator pattern
     */
    public interface SalesComponent {
        /**
         * Calculate the value of this component.
         * @return the value as a BigDecimal
         */
        BigDecimal calculateValue();
        
        /**
         * Get the name of this component.
         * @return the name as a String
         */
        String getName();
    }

    /**
     * ProductItem - Represents an individual product (leaf) in the sales hierarchy.
     * This is a ConcreteComponent in the Decorator pattern.
     */
    public static class ProductItem implements SalesComponent {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;

        public ProductItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        @Override
        public BigDecimal calculateValue() {
            return price.multiply(new BigDecimal(quantity));
        }

        @Override
        public String getName() {
            return productName;
        }

        // Getters and setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    /**
     * SalesDeal - Represents a collection of products (composite) in the sales hierarchy.
     * This is a Composite in the Composite pattern and a ConcreteComponent in the Decorator pattern.
     */
    public static class SalesDeal implements SalesComponent {
        private String id;
        private String title;
        private List<SalesComponent> components = new ArrayList<>();
        private String salesRepId;

        public SalesDeal(String title, String salesRepId) {
            this.title = title;
            this.salesRepId = salesRepId;
        }

        /**
         * Add a component (product or nested deal) to this deal.
         */
        public void addComponent(SalesComponent component) {
            components.add(component);
        }

        /**
         * Remove a component from this deal.
         */
        public void removeComponent(SalesComponent component) {
            components.remove(component);
        }

        /**
         * Get all components in this deal.
         */
        public List<SalesComponent> getComponents() {
            return components;
        }

        @Override
        public BigDecimal calculateValue() {
            return components.stream()
                    .map(SalesComponent::calculateValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public String getName() {
            return title;
        }

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSalesRepId() {
            return salesRepId;
        }

        public void setSalesRepId(String salesRepId) {
            this.salesRepId = salesRepId;
        }
    }

    /**
     * SalesComponentDecorator - Base decorator class for SalesComponent.
     * This is the Decorator in the Decorator pattern.
     */
    public static abstract class SalesComponentDecorator implements SalesComponent {
        protected SalesComponent decoratedComponent;
        
        public SalesComponentDecorator(SalesComponent decoratedComponent) {
            this.decoratedComponent = decoratedComponent;
        }
        
        @Override
        public BigDecimal calculateValue() {
            return decoratedComponent.calculateValue();
        }
        
        @Override
        public String getName() {
            return decoratedComponent.getName();
        }
    }
    
    /**
     * DiscountDecorator - Applies a discount to a SalesComponent.
     * This is a ConcreteDecorator in the Decorator pattern.
     */
    public static class DiscountDecorator extends SalesComponentDecorator {
        private BigDecimal discountRate;
        
        public DiscountDecorator(SalesComponent decoratedComponent, BigDecimal discountRate) {
            super(decoratedComponent);
            this.discountRate = discountRate;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal originalValue = super.calculateValue();
            BigDecimal discountAmount = originalValue.multiply(discountRate);
            return originalValue.subtract(discountAmount);
        }
        
        @Override
        public String getName() {
            return super.getName() + " (with " + discountRate.multiply(new BigDecimal("100")) + "% discount)";
        }
    }
    
    /**
     * UrgencyDecorator - Adds an urgency premium to a SalesComponent based on deadline proximity.
     * This is a ConcreteDecorator in the Decorator pattern.
     */
    public static class UrgencyDecorator extends SalesComponentDecorator {
        private LocalDate deadline;
        
        public UrgencyDecorator(SalesComponent decoratedComponent, LocalDate deadline) {
            super(decoratedComponent);
            this.deadline = deadline;
        }
        
        @Override
        public BigDecimal calculateValue() {
            BigDecimal originalValue = super.calculateValue();
            
            // Calculate days until deadline
            long daysUntilDeadline = LocalDate.now().until(deadline).getDays();
            
            // Apply urgency premium based on deadline proximity
            if (daysUntilDeadline <= 0) {
                // Past deadline, apply 15% premium
                return originalValue.multiply(new BigDecimal("1.15"));
            } else if (daysUntilDeadline <= 7) {
                // Within a week, apply 10% premium
                return originalValue.multiply(new BigDecimal("1.10"));
            } else if (daysUntilDeadline <= 30) {
                // Within a month, apply 5% premium
                return originalValue.multiply(new BigDecimal("1.05"));
            } else {
                // More than a month away, no premium
                return originalValue;
            }
        }
        
        @Override
        public String getName() {
            long daysUntilDeadline = LocalDate.now().until(deadline).getDays();
            if (daysUntilDeadline <= 0) {
                return super.getName() + " (URGENT: Past deadline)";
            } else if (daysUntilDeadline <= 7) {
                return super.getName() + " (URGENT: Due in " + daysUntilDeadline + " days)";
            } else {
                return super.getName() + " (Due in " + daysUntilDeadline + " days)";
            }
        }
    }
    
    /**
     * LoggingDecorator - Adds logging functionality to a SalesComponent.
     * This is a ConcreteDecorator in the Decorator pattern.
     */
    public static class LoggingDecorator extends SalesComponentDecorator {
        public LoggingDecorator(SalesComponent decoratedComponent) {
            super(decoratedComponent);
        }
        
        @Override
        public BigDecimal calculateValue() {
            System.out.println("Calculating value for: " + decoratedComponent.getName());
            BigDecimal result = super.calculateValue();
            System.out.println("Value calculated: " + result);
            return result;
        }
        
        @Override
        public String getName() {
            System.out.println("Getting name for component");
            return super.getName();
        }
    }
    
    /**
     * Client code that demonstrates how to use the CompositeDecorator
     */
    public static void main(String[] args) {
        System.out.println("===== Composite + Decorator Pattern Combination Example =====\n");
        
        // Create individual products (leaf nodes)
        ProductItem laptop = new ProductItem("P1", "Laptop", 2, new BigDecimal("1200.00"));
        ProductItem monitor = new ProductItem("P2", "Monitor", 3, new BigDecimal("300.00"));
        ProductItem keyboard = new ProductItem("P3", "Keyboard", 5, new BigDecimal("50.00"));
        ProductItem mouse = new ProductItem("P4", "Mouse", 5, new BigDecimal("25.00"));
        
        // Create a composite deal
        SalesDeal hardwareBundle = new SalesDeal("Hardware Bundle", "sales-rep-1");
        hardwareBundle.addComponent(laptop);
        hardwareBundle.addComponent(monitor);
        
        // Create another composite deal
        SalesDeal accessoriesBundle = new SalesDeal("Accessories Bundle", "sales-rep-1");
        accessoriesBundle.addComponent(keyboard);
        accessoriesBundle.addComponent(mouse);
        
        // Create a top-level composite that contains other composites
        SalesDeal completeSolution = new SalesDeal("Complete IT Solution", "sales-rep-1");
        completeSolution.addComponent(hardwareBundle);
        completeSolution.addComponent(accessoriesBundle);
        
        // Calculate and display the value of the complete solution
        System.out.println("Original Deal: " + completeSolution.getName());
        System.out.println("Original Value: $" + completeSolution.calculateValue());
        
        // Apply a discount decorator to the complete solution
        SalesComponent discountedSolution = new DiscountDecorator(completeSolution, new BigDecimal("0.10"));
        System.out.println("\nAfter Discount: " + discountedSolution.getName());
        System.out.println("Discounted Value: $" + discountedSolution.calculateValue());
        
        // Apply an urgency decorator to the discounted solution
        LocalDate deadline = LocalDate.now().plusDays(5);
        SalesComponent urgentDiscountedSolution = new UrgencyDecorator(discountedSolution, deadline);
        System.out.println("\nAfter Urgency: " + urgentDiscountedSolution.getName());
        System.out.println("Urgent Discounted Value: $" + urgentDiscountedSolution.calculateValue());
        
        // Apply a logging decorator to the urgent discounted solution
        SalesComponent loggingUrgentDiscountedSolution = new LoggingDecorator(urgentDiscountedSolution);
        System.out.println("\nWith Logging:");
        System.out.println("Final Value: $" + loggingUrgentDiscountedSolution.calculateValue());
        
        System.out.println("\nBenefits of combining Composite and Decorator patterns:");
        System.out.println("1. Uniform treatment of individual objects and compositions (Composite)");
        System.out.println("2. Dynamic addition of responsibilities to objects (Decorator)");
        System.out.println("3. Ability to apply decorators to both individual objects and compositions");
        System.out.println("4. Flexible and extensible design with single responsibility classes");
    }
}