package com.chapman.edu.commissions.principles.yagni.original;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sales deal in the system with YAGNI violations.
 * This class contains many fields and methods that aren't needed for the basic functionality.
 */
public class Deal {
    private String id;
    private String title;
    private BigDecimal value;
    private String status;  // A simple string would suffice instead of an enum for basic functionality
    private String salesRepId;
    private List<Product> products;  // YAGNI: Not needed for basic functionality
    private LocalDate closeDate;  // YAGNI: Not needed for basic functionality
    private LocalDate createdDate;  // YAGNI: Not needed for basic functionality
    private LocalDate lastModifiedDate;  // YAGNI: Not needed for basic functionality
    
    /**
     * Default constructor
     */
    public Deal() {
        this.products = new ArrayList<>();  // YAGNI: Initializing a field that isn't needed
        this.createdDate = LocalDate.now();  // YAGNI: Setting a field that isn't needed
        this.lastModifiedDate = LocalDate.now();  // YAGNI: Setting a field that isn't needed
    }
    
    /**
     * Constructor with essential fields
     */
    public Deal(String title, BigDecimal value, String salesRepId) {
        this();
        this.title = title;
        this.value = value;
        this.salesRepId = salesRepId;
        this.status = "OPEN";  // YAGNI: Setting a status when it might not be needed
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
        this.lastModifiedDate = LocalDate.now();  // YAGNI: Updating a field that isn't needed
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    // YAGNI: Methods for fields that aren't needed for basic functionality
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    public void addProduct(Product product) {
        this.products.add(product);
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
     * YAGNI: Calculate the total value of the deal based on product prices
     * This is redundant since the deal already has a value field
     * @return the total value of all products in the deal
     */
    public BigDecimal calculateTotalValue() {
        return products.stream()
                .map(product -> product.getPrice().multiply(new BigDecimal(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * YAGNI: Check if the deal is closed
     * This is a simple status check that could be done directly
     * @return true if the deal status is "CLOSED"
     */
    public boolean isClosed() {
        return "CLOSED".equals(status);
    }
    
    /**
     * YAGNI: Calculate days since creation
     * This is not needed for basic functionality
     * @return the number of days since the deal was created
     */
    public long getDaysSinceCreation() {
        return createdDate != null ? 
               java.time.temporal.ChronoUnit.DAYS.between(createdDate, LocalDate.now()) : 
               0;
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
     * Inner class representing a product in a deal
     * YAGNI: This inner class isn't needed for basic functionality
     */
    public static class Product {
        private String id;
        private String name;
        private BigDecimal price;
        private int quantity;
        private String type;
        
        public Product() {
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
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        @Override
        public String toString() {
            return "Product{" +
                    "name='" + name + '\'' +
                    ", price=" + price +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}