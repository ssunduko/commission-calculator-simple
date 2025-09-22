package com.chapman.edu.commissions.brokenabstraction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature Envy Anti-Pattern Example
 * 
 * Feature Envy occurs when a method in one class seems more interested in the features (methods or attributes)
 * of another class than the ones in its own class. It's a sign that the method might belong in the other class.
 * 
 * Characteristics of Feature Envy:
 * 1. A method accesses the data of another object more than its own data
 * 2. A method calls multiple methods on another object to perform its function
 * 3. A method is more interested in the state of another class than its own
 * 4. A method uses more features of another class than its own class
 * 5. A method would be better placed in the class it's envying
 * 
 * This example demonstrates Feature Envy by creating classes where methods in one class
 * are more interested in the features of another class.
 */
public class FeatureEnvy {
    
    /**
     * Main method to demonstrate the Feature Envy anti-pattern
     */
    public static void main(String[] args) {
        // Create a deal
        Deal deal = new Deal("Enterprise Software Deal", new BigDecimal("50000.00"), "REP001");
        deal.setId("DEAL001");
        
        // Create products
        DealProduct product1 = new DealProduct("SW001", "Enterprise Software License", 10, new BigDecimal("5000.00"));
        product1.setId("PROD001");
        
        DealProduct product2 = new DealProduct("HW001", "Server Hardware", 2, new BigDecimal("10000.00"));
        product2.setId("PROD002");
        
        // Add products to deal
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        // Create a commission calculator
        CommissionCalculator calculator = new CommissionCalculator();
        
        // Calculate commission (this method exhibits Feature Envy)
        BigDecimal commission = calculator.calculateCommission(deal);
        System.out.println("Commission for deal: " + commission);
        
        // Create a report generator
        ReportGenerator reportGenerator = new ReportGenerator();
        
        // Generate deal report (this method exhibits Feature Envy)
        String report = reportGenerator.generateDealReport(deal);
        System.out.println("\nDeal Report:\n" + report);
        
        // Create a notification service
        NotificationService notificationService = new NotificationService();
        
        // Send deal notification (this method exhibits Feature Envy)
        notificationService.sendDealNotification(deal, "john.doe@example.com");
    }
}

/**
 * Deal class - represents a sales deal
 */
class Deal {
    private String id;
    private String title;
    private BigDecimal value;
    private String status;
    private String salesRepId;
    private List<DealProduct> products;
    private LocalDate closeDate;
    private LocalDate createdDate;
    
    /**
     * Constructor
     */
    public Deal(String title, BigDecimal value, String salesRepId) {
        this.title = title;
        this.value = value;
        this.salesRepId = salesRepId;
        this.status = "OPEN";
        this.products = new ArrayList<>();
        this.createdDate = LocalDate.now();
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
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    public List<DealProduct> getProducts() {
        return products;
    }
    
    public void setProducts(List<DealProduct> products) {
        this.products = products;
    }
    
    public void addProduct(DealProduct product) {
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
    
    /**
     * Calculate the total value of the deal based on product prices
     */
    public BigDecimal calculateTotalValue() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (DealProduct product : products) {
            BigDecimal productTotal = product.getPrice()
                    .multiply(new BigDecimal(product.getQuantity()));
            
            total = total.add(productTotal);
        }
        
        return total;
    }
}

/**
 * DealProduct class - represents a product within a deal
 */
class DealProduct {
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    
    /**
     * Constructor
     */
    public DealProduct(String productId, String productName, int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
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
    
    /**
     * Calculate the total price for this product (price * quantity)
     */
    public BigDecimal calculateTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}

/**
 * CommissionCalculator class - calculates commissions for deals
 * 
 * The calculateCommission method exhibits Feature Envy because it's more interested
 * in the Deal class's data and methods than its own.
 */
class CommissionCalculator {
    
    /**
     * Calculate commission for a deal
     * 
     * This method exhibits Feature Envy because it's more interested in the Deal class's
     * data and methods than its own. It accesses multiple properties and methods of the Deal class.
     * 
     * A better design would be to move this method to the Deal class or create a proper
     * domain service that doesn't rely so heavily on the internal details of Deal.
     */
    public BigDecimal calculateCommission(Deal deal) {
        // Feature Envy: Accessing multiple properties of Deal
        System.out.println("Calculating commission for deal: " + deal.getId() + " - " + deal.getTitle());
        
        // Feature Envy: Calling methods on Deal
        BigDecimal dealValue = deal.calculateTotalValue();
        System.out.println("Deal value: " + dealValue);
        
        // Feature Envy: Accessing Deal's products
        System.out.println("Products in deal:");
        for (DealProduct product : deal.getProducts()) {
            // Feature Envy: Accessing multiple properties of DealProduct
            System.out.println("  - " + product.getProductName() + 
                    " (Quantity: " + product.getQuantity() + 
                    ", Price: " + product.getPrice() + 
                    ", Total: " + product.calculateTotalPrice() + ")");
        }
        
        // Simple commission calculation (5% of deal value)
        BigDecimal commissionRate = new BigDecimal("0.05");
        BigDecimal commission = dealValue.multiply(commissionRate);
        
        // Feature Envy: More access to Deal properties
        if ("CLOSED_WON".equals(deal.getStatus())) {
            // Add 2% bonus for closed deals
            commission = commission.add(dealValue.multiply(new BigDecimal("0.02")));
        }
        
        return commission;
    }
}

/**
 * ReportGenerator class - generates reports for deals
 * 
 * The generateDealReport method exhibits Feature Envy because it's more interested
 * in the Deal class's data and methods than its own.
 */
class ReportGenerator {
    
    /**
     * Generate a report for a deal
     * 
     * This method exhibits Feature Envy because it's more interested in the Deal class's
     * data and methods than its own. It accesses multiple properties and methods of the Deal class.
     * 
     * A better design would be to move this method to the Deal class or create a proper
     * domain service that doesn't rely so heavily on the internal details of Deal.
     */
    public String generateDealReport(Deal deal) {
        // Feature Envy: Accessing multiple properties of Deal
        StringBuilder report = new StringBuilder();
        report.append("Deal Report\n");
        report.append("==========\n");
        report.append("ID: ").append(deal.getId()).append("\n");
        report.append("Title: ").append(deal.getTitle()).append("\n");
        report.append("Status: ").append(deal.getStatus()).append("\n");
        report.append("Sales Rep: ").append(deal.getSalesRepId()).append("\n");
        report.append("Created Date: ").append(deal.getCreatedDate()).append("\n");
        
        // Feature Envy: Calling methods on Deal
        report.append("Total Value: ").append(deal.calculateTotalValue()).append("\n");
        
        // Feature Envy: Accessing Deal's products
        report.append("\nProducts:\n");
        for (DealProduct product : deal.getProducts()) {
            // Feature Envy: Accessing multiple properties of DealProduct
            report.append("  - ").append(product.getProductName())
                  .append(" (ID: ").append(product.getProductId()).append(")\n")
                  .append("    Quantity: ").append(product.getQuantity()).append("\n")
                  .append("    Price: ").append(product.getPrice()).append("\n")
                  .append("    Total: ").append(product.calculateTotalPrice()).append("\n");
        }
        
        return report.toString();
    }
}

/**
 * NotificationService class - sends notifications about deals
 * 
 * The sendDealNotification method exhibits Feature Envy because it's more interested
 * in the Deal class's data and methods than its own.
 */
class NotificationService {
    
    /**
     * Send a notification about a deal
     * 
     * This method exhibits Feature Envy because it's more interested in the Deal class's
     * data and methods than its own. It accesses multiple properties and methods of the Deal class.
     * 
     * A better design would be to move this method to the Deal class or create a proper
     * domain service that doesn't rely so heavily on the internal details of Deal.
     */
    public void sendDealNotification(Deal deal, String recipientEmail) {
        // Feature Envy: Accessing multiple properties of Deal
        System.out.println("Sending notification about deal: " + deal.getId() + " - " + deal.getTitle());
        
        // Feature Envy: Calling methods on Deal
        BigDecimal dealValue = deal.calculateTotalValue();
        
        // Construct email content
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Deal Information:\n");
        emailContent.append("ID: ").append(deal.getId()).append("\n");
        emailContent.append("Title: ").append(deal.getTitle()).append("\n");
        emailContent.append("Status: ").append(deal.getStatus()).append("\n");
        emailContent.append("Value: ").append(dealValue).append("\n");
        
        // Feature Envy: Accessing Deal's products
        emailContent.append("\nProducts:\n");
        for (DealProduct product : deal.getProducts()) {
            // Feature Envy: Accessing multiple properties of DealProduct
            emailContent.append("  - ").append(product.getProductName())
                  .append(" (Quantity: ").append(product.getQuantity())
                  .append(", Price: ").append(product.getPrice())
                  .append(")\n");
        }
        
        // In a real system, this would send an email
        System.out.println("Email sent to: " + recipientEmail);
        System.out.println("Content:\n" + emailContent.toString());
    }
}