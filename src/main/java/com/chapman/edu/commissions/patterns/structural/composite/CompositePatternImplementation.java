package com.chapman.edu.commissions.patterns.structural.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a concrete implementation of the Composite Pattern using the
 * Commission Calculator domain model.
 * 
 * The Composite Pattern allows us to treat individual products and collections of products
 * (like deals) uniformly when calculating values or commissions.
 */
public class CompositePatternImplementation {

    /**
     * SalesComponent - The component interface that defines operations common to both
     * individual products and deals (collections of products).
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
     * This is analogous to the DealProduct class in the model.
     */
    public static class ProductItem implements SalesComponent {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
        private BigDecimal discount;

        public ProductItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.discount = BigDecimal.ZERO;
        }

        public ProductItem(String productId, String productName, int quantity, BigDecimal price, BigDecimal discount) {
            this(productId, productName, quantity, price);
            this.discount = discount;
        }

        @Override
        public BigDecimal calculateValue() {
            return price.multiply(new BigDecimal(quantity)).subtract(discount);
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

        public BigDecimal getDiscount() {
            return discount;
        }

        public void setDiscount(BigDecimal discount) {
            this.discount = discount;
        }
    }

    /**
     * SalesDeal - Represents a collection of products (composite) in the sales hierarchy.
     * This is analogous to the Deal class in the model.
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
     * SalesReport - A client class that works with the SalesComponent interface.
     */
    public static class SalesReport {
        /**
         * Generate a report for any sales component (product or deal).
         */
        public void generateReport(SalesComponent component) {
            System.out.println("Sales Report for: " + component.getName());
            System.out.println("Total Value: $" + component.calculateValue());
            
            // If it's a composite, we can also show details about its components
            if (component instanceof SalesDeal) {
                SalesDeal deal = (SalesDeal) component;
                System.out.println("Components:");
                for (SalesComponent subComponent : deal.getComponents()) {
                    System.out.println("  - " + subComponent.getName() + ": $" + subComponent.calculateValue());
                }
            }
            System.out.println();
        }
    }
}