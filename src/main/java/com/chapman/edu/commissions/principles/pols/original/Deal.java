package com.chapman.edu.commissions.principles.pols.original;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sales deal in the system.
 * This class violates the Principle of Least Surprise (POLS) because:
 * 1. The calculateTotalValue() method calculates the total value based on products
 *    but doesn't update the deal's value field, which could be surprising to users.
 * 2. The addProduct() method doesn't update the deal's value, which could also be surprising.
 */
public class Deal {
    private String id;
    private String title;
    private BigDecimal value;
    private String status;
    private String salesRepId;
    private List<Product> products;
    private LocalDate closeDate;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    
    /**
     * Default constructor
     */
    public Deal() {
        this.products = new ArrayList<>();
        this.createdDate = LocalDate.now();
        this.lastModifiedDate = LocalDate.now();
        this.value = BigDecimal.ZERO;
        this.status = "OPEN";
    }
    
    /**
     * Constructor with essential fields
     */
    public Deal(String title, BigDecimal value, String salesRepId) {
        this();
        this.title = title;
        this.value = value;
        this.salesRepId = salesRepId;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastModifiedDate = LocalDate.now();
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    /**
     * Add a product to the deal
     * POLS Violation: This method doesn't update the deal's value
     * when a product is added, which could be surprising to users.
     */
    public void addProduct(Product product) {
        this.products.add(product);
        // The deal's value is not updated here, which could be surprising
    }
    
    public LocalDate getCloseDate() {
        return closeDate;
    }
    
    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    /**
     * Calculate the total value of the deal based on product prices
     * POLS Violation: This method calculates the total value but doesn't
     * update the deal's value field, which could be surprising to users.
     * 
     * @return the total value of all products in the deal
     */
    public BigDecimal calculateTotalValue() {
        return products.stream()
                .map(product -> product.getPrice().multiply(new BigDecimal(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // The deal's value field is not updated here, which could be surprising
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deal deal = (Deal) o;
        return Objects.equals(id, deal.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Deal{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", value=" + value +
                ", status='" + status + '\'' +
                ", salesRepId='" + salesRepId + '\'' +
                ", closeDate=" + closeDate +
                '}';
    }
    
    /**
     * Simple product class for demonstration purposes
     */
    public static class Product {
        private String id;
        private String name;
        private BigDecimal price;
        private int quantity;
        
        public Product() {
            this.price = BigDecimal.ZERO;
            this.quantity = 0;
        }
        
        public Product(String name, BigDecimal price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}