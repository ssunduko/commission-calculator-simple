package com.chapman.edu.commissions.brokenabstraction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Anemic Domain Model Anti-Pattern Example
 * 
 * An Anemic Domain Model is a domain model with very little behavior, consisting mostly of getter and setter methods.
 * It's essentially a collection of data containers with no real domain logic.
 * 
 * Characteristics of an Anemic Domain Model:
 * 1. Domain objects are just data holders with getters and setters
 * 2. Business logic is moved to service classes
 * 3. Domain objects have no behavior or validation
 * 4. Domain objects are not encapsulated
 * 5. Business rules are scattered across service classes
 * 
 * This example demonstrates an Anemic Domain Model by creating domain objects that are just data containers,
 * with all business logic moved to service classes.
 */
public class AnemicDomainModel {
    
    /**
     * Main method to demonstrate the Anemic Domain Model anti-pattern
     */
    public static void main(String[] args) {
        // Create a user (just a data container)
        AnemicUser user = new AnemicUser();
        user.setId("USER001");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        
        // Create a deal (just a data container)
        AnemicDeal deal = new AnemicDeal();
        deal.setId("DEAL001");
        deal.setTitle("Enterprise Software Deal");
        deal.setValue(new BigDecimal("50000.00"));
        deal.setStatus("OPEN");
        deal.setSalesRepId(user.getId());
        deal.setCreatedDate(LocalDate.now());
        
        // Create a product (just a data container)
        AnemicDealProduct product = new AnemicDealProduct();
        product.setId("PROD001");
        product.setProductId("SW001");
        product.setProductName("Enterprise Software License");
        product.setQuantity(10);
        product.setPrice(new BigDecimal("5000.00"));
        product.setDiscount(BigDecimal.ZERO);
        product.setDealId(deal.getId());
        
        // Add product to deal
        List<AnemicDealProduct> products = new ArrayList<>();
        products.add(product);
        deal.setProducts(products);
        
        // Create a commission plan (just a data container)
        AnemicCommissionPlan plan = new AnemicCommissionPlan();
        plan.setId("PLAN001");
        plan.setName("Standard Commission Plan");
        plan.setStatus("ACTIVE");
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(11));
        
        // Use service classes for business logic
        UserService userService = new UserService();
        DealService dealService = new DealService();
        CommissionService commissionService = new CommissionService();
        
        // Business logic is in service classes, not in domain objects
        if (userService.isUserActive(user) && userService.hasRole(user, "SALES_REP")) {
            System.out.println("User is an active sales rep");
            
            if (dealService.isDealOpen(deal)) {
                System.out.println("Deal is open");
                
                BigDecimal totalValue = dealService.calculateTotalValue(deal);
                System.out.println("Deal total value: " + totalValue);
                
                if (commissionService.isPlanActive(plan, LocalDate.now())) {
                    System.out.println("Commission plan is active");
                    
                    BigDecimal commission = commissionService.calculateCommission(deal, plan);
                    System.out.println("Commission amount: " + commission);
                    
                    // Create a commission calculation (just a data container)
                    AnemicCommissionCalculation calculation = new AnemicCommissionCalculation();
                    calculation.setId("CALC001");
                    calculation.setDealId(deal.getId());
                    calculation.setSalesRepId(user.getId());
                    calculation.setBaseCommission(commission);
                    calculation.setGrossCommission(commission);
                    calculation.setNetCommission(commission);
                    calculation.setStatus("CALCULATED");
                    calculation.setCalculationDate(LocalDate.now());
                    calculation.setPlanId(plan.getId());
                    
                    // Save calculation using service
                    commissionService.saveCalculation(calculation);
                    
                    // Approve calculation using service
                    commissionService.approveCalculation(calculation);
                    
                    System.out.println("Commission calculation approved: " + calculation.getId());
                }
            }
        }
    }
}

/**
 * Anemic User class - just a data container with getters and setters
 */
class AnemicUser {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private List<String> roles = new ArrayList<>();
    private boolean active;
    private LocalDate lastLogin;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDate getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDate lastLogin) { this.lastLogin = lastLogin; }
}

/**
 * Anemic Deal class - just a data container with getters and setters
 */
class AnemicDeal {
    private String id;
    private String title;
    private BigDecimal value;
    private String status;
    private String salesRepId;
    private List<AnemicDealProduct> products = new ArrayList<>();
    private LocalDate closeDate;
    private LocalDate createdDate;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getSalesRepId() { return salesRepId; }
    public void setSalesRepId(String salesRepId) { this.salesRepId = salesRepId; }
    
    public List<AnemicDealProduct> getProducts() { return products; }
    public void setProducts(List<AnemicDealProduct> products) { this.products = products; }
    
    public LocalDate getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }
    
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
}

/**
 * Anemic DealProduct class - just a data container with getters and setters
 */
class AnemicDealProduct {
    private String id;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private String dealId;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    
    public String getDealId() { return dealId; }
    public void setDealId(String dealId) { this.dealId = dealId; }
}

/**
 * Anemic CommissionPlan class - just a data container with getters and setters
 */
class AnemicCommissionPlan {
    private String id;
    private String name;
    private String status;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getEffectiveStartDate() { return effectiveStartDate; }
    public void setEffectiveStartDate(LocalDate effectiveStartDate) { this.effectiveStartDate = effectiveStartDate; }
    
    public LocalDate getEffectiveEndDate() { return effectiveEndDate; }
    public void setEffectiveEndDate(LocalDate effectiveEndDate) { this.effectiveEndDate = effectiveEndDate; }
}

/**
 * Anemic CommissionCalculation class - just a data container with getters and setters
 */
class AnemicCommissionCalculation {
    private String id;
    private String dealId;
    private String salesRepId;
    private BigDecimal baseCommission;
    private BigDecimal grossCommission;
    private BigDecimal netCommission;
    private String status;
    private LocalDate calculationDate;
    private LocalDate payoutDate;
    private String planId;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDealId() { return dealId; }
    public void setDealId(String dealId) { this.dealId = dealId; }
    
    public String getSalesRepId() { return salesRepId; }
    public void setSalesRepId(String salesRepId) { this.salesRepId = salesRepId; }
    
    public BigDecimal getBaseCommission() { return baseCommission; }
    public void setBaseCommission(BigDecimal baseCommission) { this.baseCommission = baseCommission; }
    
    public BigDecimal getGrossCommission() { return grossCommission; }
    public void setGrossCommission(BigDecimal grossCommission) { this.grossCommission = grossCommission; }
    
    public BigDecimal getNetCommission() { return netCommission; }
    public void setNetCommission(BigDecimal netCommission) { this.netCommission = netCommission; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDate getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDate calculationDate) { this.calculationDate = calculationDate; }
    
    public LocalDate getPayoutDate() { return payoutDate; }
    public void setPayoutDate(LocalDate payoutDate) { this.payoutDate = payoutDate; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
}

/**
 * Service class for user-related business logic
 */
class UserService {
    /**
     * Check if a user is active
     */
    public boolean isUserActive(AnemicUser user) {
        return user.isActive();
    }
    
    /**
     * Check if a user has a specific role
     */
    public boolean hasRole(AnemicUser user, String role) {
        return user.getRoles().contains(role);
    }
    
    /**
     * Get the full name of a user
     */
    public String getFullName(AnemicUser user) {
        return user.getFirstName() + " " + user.getLastName();
    }
    
    /**
     * Authenticate a user
     */
    public boolean authenticate(AnemicUser user, String password) {
        // In a real system, this would hash the password and compare it to the stored hash
        return password != null && password.equals(user.getPasswordHash());
    }
}

/**
 * Service class for deal-related business logic
 */
class DealService {
    /**
     * Check if a deal is open
     */
    public boolean isDealOpen(AnemicDeal deal) {
        return "OPEN".equals(deal.getStatus());
    }
    
    /**
     * Calculate the total value of a deal
     */
    public BigDecimal calculateTotalValue(AnemicDeal deal) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (AnemicDealProduct product : deal.getProducts()) {
            BigDecimal productTotal = product.getPrice()
                    .multiply(new BigDecimal(product.getQuantity()))
                    .subtract(product.getDiscount());
            
            total = total.add(productTotal);
        }
        
        return total;
    }
    
    /**
     * Close a deal
     */
    public void closeDeal(AnemicDeal deal) {
        deal.setStatus("CLOSED_WON");
        deal.setCloseDate(LocalDate.now());
    }
    
    /**
     * Add a product to a deal
     */
    public void addProduct(AnemicDeal deal, AnemicDealProduct product) {
        product.setDealId(deal.getId());
        deal.getProducts().add(product);
    }
}

/**
 * Service class for commission-related business logic
 */
class CommissionService {
    /**
     * Check if a commission plan is active on a given date
     */
    public boolean isPlanActive(AnemicCommissionPlan plan, LocalDate date) {
        if (!"ACTIVE".equals(plan.getStatus())) {
            return false;
        }
        
        boolean afterStart = plan.getEffectiveStartDate() == null || !date.isBefore(plan.getEffectiveStartDate());
        boolean beforeEnd = plan.getEffectiveEndDate() == null || !date.isAfter(plan.getEffectiveEndDate());
        
        return afterStart && beforeEnd;
    }
    
    /**
     * Calculate commission for a deal
     */
    public BigDecimal calculateCommission(AnemicDeal deal, AnemicCommissionPlan plan) {
        // Simple commission calculation (5% of deal value)
        return deal.getValue().multiply(new BigDecimal("0.05"));
    }
    
    /**
     * Save a commission calculation
     */
    public void saveCalculation(AnemicCommissionCalculation calculation) {
        // In a real system, this would save the calculation to a database
        System.out.println("Saving calculation: " + calculation.getId());
    }
    
    /**
     * Approve a commission calculation
     */
    public void approveCalculation(AnemicCommissionCalculation calculation) {
        calculation.setStatus("APPROVED");
        calculation.setPayoutDate(LocalDate.now().plusMonths(1));
        
        // In a real system, this would update the calculation in a database
        System.out.println("Approving calculation: " + calculation.getId());
    }
}