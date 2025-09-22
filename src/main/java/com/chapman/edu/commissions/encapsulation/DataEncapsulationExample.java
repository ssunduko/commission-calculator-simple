package com.chapman.edu.commissions.encapsulation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data Encapsulation Example
 * 
 * This class demonstrates data encapsulation by using private fields with public methods (getters and setters).
 * Data encapsulation is a fundamental OOP concept that:
 * 1. Hides the internal state of an object
 * 2. Requires all interactions to occur through well-defined methods
 * 3. Allows for validation and control over how data is accessed and modified
 * 4. Helps maintain class invariants and ensures data integrity
 */
public class DataEncapsulationExample {
    
    /**
     * The Product class demonstrates data encapsulation by making all fields private
     * and providing controlled access through public methods.
     */
    public static class Product {
        // Private fields - not directly accessible from outside the class
        private String id;
        private String name;
        private BigDecimal price;
        private int stockQuantity;
        private LocalDate createdDate;
        
        // Constructor
        public Product() {
            this.createdDate = LocalDate.now();
        }
        // Public getters - provide read access to private fields
        public String getId() {
            return id;
        }

        public void setPrice(BigDecimal price) {
            // Validation logic ensures data integrity
            if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            this.price = price;
        }
        
        public String getName() {
            return name;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public int getStockQuantity() {
            return stockQuantity;
        }
        
        public LocalDate getCreatedDate() {
            return createdDate;
        }
        
        // Public setters - provide controlled write access to private fields
        public void setId(String id) {
            this.id = id;
        }
        
        public void setName(String name) {
            // Validation logic can be added here
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            this.name = name;
        }
        
        public void setStockQuantity(int stockQuantity) {
            // Validation logic ensures data integrity
            if (stockQuantity < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative");
            }
            this.stockQuantity = stockQuantity;
        }
        
        // Business logic methods that operate on the encapsulated data
        public boolean isInStock() {
            return stockQuantity > 0;
        }
        
        public void decreaseStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            if (quantity > stockQuantity) {
                throw new IllegalStateException("Not enough stock available");
            }
            
            stockQuantity -= quantity;
        }
        
        public void increaseStock(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            stockQuantity += quantity;
        }
    }
    
    /**
     * The Order class demonstrates data encapsulation with collections and relationships.
     */
    public static class Order {
        // Private fields
        private String id;
        private LocalDate orderDate;
        private String customerId;
        private List<OrderItem> items;
        private BigDecimal totalAmount;
        
        // Constructor
        public Order() {
            this.orderDate = LocalDate.now();
            this.items = new ArrayList<>();
            this.totalAmount = BigDecimal.ZERO;
        }
        
        // Getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public LocalDate getOrderDate() {
            return orderDate;
        }
        
        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }
        
        public String getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
        
        // Return a defensive copy of the items list to prevent external modification
        public List<OrderItem> getItems() {
            return new ArrayList<>(items);
        }
        
        // Controlled way to add items that maintains class invariants
        public void addItem(Product product, int quantity) {
            Objects.requireNonNull(product, "Product cannot be null");
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            OrderItem item = new OrderItem(product.getId(), product.getName(), 
                                          product.getPrice(), quantity);
            items.add(item);
            
            // Recalculate total amount
            calculateTotalAmount();
        }
        
        // Read-only access to total amount
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
        
        // Private method to calculate total amount
        private void calculateTotalAmount() {
            totalAmount = items.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
    
    /**
     * The OrderItem class demonstrates data encapsulation for a simple value object.
     */
    public static class OrderItem {
        // Private fields
        private String productId;
        private String productName;
        private BigDecimal price;
        private int quantity;
        
        // Constructor
        public OrderItem(String productId, String productName, BigDecimal price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }
        
        // Getters only - making this class immutable
        public String getProductId() {
            return productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        // Business logic method
        public BigDecimal calculateSubtotal() {
            return price.multiply(new BigDecimal(quantity));
        }
    }
    
    /**
     * Main method to demonstrate data encapsulation
     */
    public static void main(String[] args) {
        // Create a product with encapsulated data
        Product laptop = new Product();
        laptop.setId("P001");
        laptop.setName("Laptop");
        laptop.setPrice(new BigDecimal("999.99"));
        laptop.setStockQuantity(10);
        
        // Create an order with encapsulated data
        Order order = new Order();
        order.setId("O001");
        order.setCustomerId("C001");
        
        // Add item to order - the order controls how items are added and maintains its invariants
        order.addItem(laptop, 2);
        
        // Decrease product stock - the product controls how stock is modified
        laptop.decreaseStock(2);
        
        // Print order details
        System.out.println("Order ID: " + order.getId());
        System.out.println("Order Date: " + order.getOrderDate());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Total Amount: " + order.getTotalAmount());
        System.out.println("Items:");
        
        for (OrderItem item : order.getItems()) {
            System.out.println("  - " + item.getProductName() + 
                              " (Qty: " + item.getQuantity() + 
                              ", Price: " + item.getPrice() + 
                              ", Subtotal: " + item.calculateSubtotal() + ")");
        }
        
        System.out.println("Laptop remaining stock: " + laptop.getStockQuantity());
    }
}