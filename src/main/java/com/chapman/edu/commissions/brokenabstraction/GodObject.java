package com.chapman.edu.commissions.brokenabstraction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * God Object Anti-Pattern Example
 * 
 * A God Object (or God Class) is an object that knows too much or does too much.
 * It's a class that has grown too large and has too many responsibilities.
 * 
 * Characteristics of a God Object:
 * 1. It has too many instance variables and methods
 * 2. It has low cohesion (methods and data are not strongly related)
 * 3. It has high coupling (depends on many other classes)
 * 4. It violates the Single Responsibility Principle
 * 5. It's difficult to understand, maintain, and test
 * 
 * This class demonstrates a God Object by combining user management, deal management,
 * commission calculation, reporting, and data persistence into a single class.
 */
public class GodObject {
    
    // User-related fields
    private Map<String, String> userCredentials = new HashMap<>();
    private Map<String, String> userEmails = new HashMap<>();
    private Map<String, String> userFirstNames = new HashMap<>();
    private Map<String, String> userLastNames = new HashMap<>();
    private Map<String, Set<String>> userRoles = new HashMap<>();
    private Map<String, Boolean> userActiveStatus = new HashMap<>();
    private Map<String, LocalDate> userLastLogins = new HashMap<>();
    private Map<String, String> userManagers = new HashMap<>();
    private Map<String, String> userDepartments = new HashMap<>();
    private Map<String, String> userTerritories = new HashMap<>();
    
    // Deal-related fields
    private Map<String, String> dealTitles = new HashMap<>();
    private Map<String, BigDecimal> dealValues = new HashMap<>();
    private Map<String, String> dealStatuses = new HashMap<>();
    private Map<String, String> dealSalesReps = new HashMap<>();
    private Map<String, List<String>> dealProducts = new HashMap<>();
    private Map<String, LocalDate> dealCloseDates = new HashMap<>();
    private Map<String, LocalDate> dealCreatedDates = new HashMap<>();
    
    // Product-related fields
    private Map<String, String> productNames = new HashMap<>();
    private Map<String, Integer> productQuantities = new HashMap<>();
    private Map<String, BigDecimal> productPrices = new HashMap<>();
    private Map<String, BigDecimal> productDiscounts = new HashMap<>();
    private Map<String, String> productDeals = new HashMap<>();
    
    // Commission plan-related fields
    private Map<String, String> planNames = new HashMap<>();
    private Map<String, List<String>> planRules = new HashMap<>();
    private Map<String, List<String>> planTiers = new HashMap<>();
    private Map<String, List<String>> planBonuses = new HashMap<>();
    private Map<String, String> planStatuses = new HashMap<>();
    private Map<String, LocalDate> planStartDates = new HashMap<>();
    private Map<String, LocalDate> planEndDates = new HashMap<>();
    
    // Commission calculation-related fields
    private Map<String, String> calculationDeals = new HashMap<>();
    private Map<String, String> calculationSalesReps = new HashMap<>();
    private Map<String, BigDecimal> calculationBaseAmounts = new HashMap<>();
    private Map<String, List<String>> calculationBonuses = new HashMap<>();
    private Map<String, BigDecimal> calculationTotalAmounts = new HashMap<>();
    private Map<String, String> calculationStatuses = new HashMap<>();
    private Map<String, LocalDate> calculationDates = new HashMap<>();
    private Map<String, LocalDate> calculationPayoutDates = new HashMap<>();
    
    // Database connection fields
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private boolean dbConnected;
    
    // Email configuration fields
    private String smtpServer;
    private int smtpPort;
    private String emailUsername;
    private String emailPassword;
    
    // Report generation fields
    private String reportOutputDir;
    private String reportTemplateDir;
    private List<String> generatedReports = new ArrayList<>();
    
    /**
     * Constructor
     */
    public GodObject() {
        // Initialize database connection
        this.dbUrl = "jdbc:mysql://localhost:3306/commissions";
        this.dbUsername = "admin";
        this.dbPassword = "password";
        this.dbConnected = false;
        
        // Initialize email configuration
        this.smtpServer = "smtp.company.com";
        this.smtpPort = 587;
        this.emailUsername = "commissions@company.com";
        this.emailPassword = "emailpassword";
        
        // Initialize report directories
        this.reportOutputDir = "/reports/output/";
        this.reportTemplateDir = "/reports/templates/";
    }
    
    // User Management Methods
    
    /**
     * Create a new user
     */
    public String createUser(String username, String password, String email, String firstName, String lastName) {
        String userId = generateId();
        userCredentials.put(userId, password);
        userEmails.put(userId, email);
        userFirstNames.put(userId, firstName);
        userLastNames.put(userId, lastName);
        userRoles.put(userId, new HashSet<>());
        userActiveStatus.put(userId, true);
        userLastLogins.put(userId, LocalDate.now());
        
        // Log the action
        logAction("User created: " + userId);
        
        // Send welcome email
        sendEmail(email, "Welcome to Commission System", "Welcome " + firstName + "! Your account has been created.");
        
        return userId;
    }
    
    /**
     * Authenticate a user
     */
    public boolean authenticateUser(String userId, String password) {
        if (!userCredentials.containsKey(userId)) {
            return false;
        }
        
        boolean authenticated = userCredentials.get(userId).equals(password);
        
        if (authenticated) {
            userLastLogins.put(userId, LocalDate.now());
            logAction("User authenticated: " + userId);
        } else {
            logAction("Failed authentication attempt for user: " + userId);
        }
        
        return authenticated;
    }
    
    /**
     * Add a role to a user
     */
    public void addUserRole(String userId, String role) {
        if (!userRoles.containsKey(userId)) {
            userRoles.put(userId, new HashSet<>());
        }
        
        userRoles.get(userId).add(role);
        logAction("Role added to user: " + userId + ", role: " + role);
    }
    
    /**
     * Check if a user has a specific role
     */
    public boolean hasUserRole(String userId, String role) {
        if (!userRoles.containsKey(userId)) {
            return false;
        }
        
        return userRoles.get(userId).contains(role);
    }
    
    /**
     * Deactivate a user
     */
    public void deactivateUser(String userId) {
        userActiveStatus.put(userId, false);
        logAction("User deactivated: " + userId);
        
        // Send notification email
        String email = userEmails.get(userId);
        if (email != null) {
            sendEmail(email, "Account Deactivated", "Your account has been deactivated.");
        }
    }
    
    // Deal Management Methods
    
    /**
     * Create a new deal
     */
    public String createDeal(String title, BigDecimal value, String salesRepId) {
        String dealId = generateId();
        dealTitles.put(dealId, title);
        dealValues.put(dealId, value);
        dealStatuses.put(dealId, "OPEN");
        dealSalesReps.put(dealId, salesRepId);
        dealProducts.put(dealId, new ArrayList<>());
        dealCreatedDates.put(dealId, LocalDate.now());
        
        logAction("Deal created: " + dealId + " by sales rep: " + salesRepId);
        
        // Notify sales rep
        String email = userEmails.get(salesRepId);
        if (email != null) {
            sendEmail(email, "New Deal Created", "A new deal has been created: " + title);
        }
        
        return dealId;
    }
    
    /**
     * Add a product to a deal
     */
    public String addProductToDeal(String dealId, String productName, int quantity, BigDecimal price) {
        String productId = generateId();
        productNames.put(productId, productName);
        productQuantities.put(productId, quantity);
        productPrices.put(productId, price);
        productDiscounts.put(productId, BigDecimal.ZERO);
        productDeals.put(productId, dealId);
        
        if (!dealProducts.containsKey(dealId)) {
            dealProducts.put(dealId, new ArrayList<>());
        }
        
        dealProducts.get(dealId).add(productId);
        
        // Update deal value
        updateDealValue(dealId);
        
        logAction("Product added to deal: " + dealId + ", product: " + productId);
        
        return productId;
    }
    
    /**
     * Update the status of a deal
     */
    public void updateDealStatus(String dealId, String status) {
        dealStatuses.put(dealId, status);
        
        if (status.equals("CLOSED_WON")) {
            dealCloseDates.put(dealId, LocalDate.now());
            
            // Calculate commission
            calculateCommission(dealId);
            
            // Notify sales rep
            String salesRepId = dealSalesReps.get(dealId);
            if (salesRepId != null) {
                String email = userEmails.get(salesRepId);
                if (email != null) {
                    sendEmail(email, "Deal Closed", "Your deal has been closed: " + dealTitles.get(dealId));
                }
            }
        }
        
        logAction("Deal status updated: " + dealId + ", status: " + status);
    }
    
    /**
     * Calculate the total value of a deal
     */
    private void updateDealValue(String dealId) {
        List<String> products = dealProducts.get(dealId);
        if (products == null) {
            dealValues.put(dealId, BigDecimal.ZERO);
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (String productId : products) {
            BigDecimal price = productPrices.get(productId);
            int quantity = productQuantities.get(productId);
            BigDecimal discount = productDiscounts.get(productId);
            
            if (price != null && discount != null) {
                total = total.add(price.multiply(new BigDecimal(quantity)).subtract(discount));
            }
        }
        
        dealValues.put(dealId, total);
    }
    
    // Commission Plan Methods
    
    /**
     * Create a new commission plan
     */
    public String createCommissionPlan(String name, String currency) {
        String planId = generateId();
        planNames.put(planId, name);
        planRules.put(planId, new ArrayList<>());
        planTiers.put(planId, new ArrayList<>());
        planBonuses.put(planId, new ArrayList<>());
        planStatuses.put(planId, "DRAFT");
        
        logAction("Commission plan created: " + planId);
        
        return planId;
    }
    
    /**
     * Activate a commission plan
     */
    public void activateCommissionPlan(String planId, LocalDate startDate, LocalDate endDate) {
        planStatuses.put(planId, "ACTIVE");
        planStartDates.put(planId, startDate);
        planEndDates.put(planId, endDate);
        
        logAction("Commission plan activated: " + planId);
        
        // Notify all sales reps
        for (String userId : userRoles.keySet()) {
            if (hasUserRole(userId, "SALES_REP")) {
                String email = userEmails.get(userId);
                if (email != null) {
                    sendEmail(email, "New Commission Plan Activated", 
                            "A new commission plan has been activated: " + planNames.get(planId));
                }
            }
        }
    }
    
    // Commission Calculation Methods
    
    /**
     * Calculate commission for a deal
     */
    public String calculateCommission(String dealId) {
        String salesRepId = dealSalesReps.get(dealId);
        if (salesRepId == null) {
            logAction("Cannot calculate commission: no sales rep for deal " + dealId);
            return null;
        }
        
        BigDecimal dealValue = dealValues.get(dealId);
        if (dealValue == null) {
            logAction("Cannot calculate commission: no value for deal " + dealId);
            return null;
        }
        
        // Simple commission calculation (5% of deal value)
        BigDecimal baseCommission = dealValue.multiply(new BigDecimal("0.05"));
        
        String calculationId = generateId();
        calculationDeals.put(calculationId, dealId);
        calculationSalesReps.put(calculationId, salesRepId);
        calculationBaseAmounts.put(calculationId, baseCommission);
        calculationBonuses.put(calculationId, new ArrayList<>());
        calculationTotalAmounts.put(calculationId, baseCommission);
        calculationStatuses.put(calculationId, "CALCULATED");
        calculationDates.put(calculationId, LocalDate.now());
        
        logAction("Commission calculated: " + calculationId + " for deal: " + dealId);
        
        // Notify sales rep
        String email = userEmails.get(salesRepId);
        if (email != null) {
            sendEmail(email, "Commission Calculated", 
                    "Commission has been calculated for your deal: " + dealTitles.get(dealId) + 
                    ". Amount: " + baseCommission);
        }
        
        // Generate commission report
        generateCommissionReport(calculationId);
        
        return calculationId;
    }
    
    /**
     * Approve a commission calculation
     */
    public void approveCommission(String calculationId) {
        calculationStatuses.put(calculationId, "APPROVED");
        calculationPayoutDates.put(calculationId, LocalDate.now().plusMonths(1));
        
        logAction("Commission approved: " + calculationId);
        
        // Notify sales rep
        String salesRepId = calculationSalesReps.get(calculationId);
        if (salesRepId != null) {
            String email = userEmails.get(salesRepId);
            if (email != null) {
                String dealId = calculationDeals.get(calculationId);
                BigDecimal amount = calculationTotalAmounts.get(calculationId);
                
                sendEmail(email, "Commission Approved", 
                        "Your commission for deal " + dealTitles.get(dealId) + 
                        " has been approved. Amount: " + amount);
            }
        }
    }
    
    // Reporting Methods
    
    /**
     * Generate a commission report
     */
    public String generateCommissionReport(String calculationId) {
        String reportId = generateId();
        String reportPath = reportOutputDir + "commission_" + calculationId + ".pdf";
        
        // In a real system, this would generate a PDF report
        logAction("Commission report generated: " + reportPath);
        
        generatedReports.add(reportPath);
        
        return reportPath;
    }
    
    /**
     * Generate a sales performance report
     */
    public String generateSalesReport(String salesRepId, LocalDate startDate, LocalDate endDate) {
        String reportId = generateId();
        String reportPath = reportOutputDir + "sales_" + salesRepId + "_" + startDate + "_" + endDate + ".pdf";
        
        // In a real system, this would generate a PDF report
        logAction("Sales report generated: " + reportPath);
        
        generatedReports.add(reportPath);
        
        return reportPath;
    }
    
    // Database Methods
    
    /**
     * Connect to the database
     */
    public boolean connectToDatabase() {
        // In a real system, this would establish a database connection
        logAction("Connected to database: " + dbUrl);
        dbConnected = true;
        return true;
    }
    
    /**
     * Save all data to the database
     */
    public boolean saveAllData() {
        if (!dbConnected) {
            connectToDatabase();
        }
        
        // In a real system, this would save all data to the database
        logAction("All data saved to database");
        
        return true;
    }
    
    /**
     * Load all data from the database
     */
    public boolean loadAllData() {
        if (!dbConnected) {
            connectToDatabase();
        }
        
        // In a real system, this would load all data from the database
        logAction("All data loaded from database");
        
        return true;
    }
    
    // Email Methods
    
    /**
     * Send an email
     */
    public boolean sendEmail(String to, String subject, String body) {
        // In a real system, this would send an email
        logAction("Email sent to: " + to + ", subject: " + subject);
        
        return true;
    }
    
    // Utility Methods
    
    /**
     * Generate a unique ID
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Log an action
     */
    private void logAction(String action) {
        System.out.println("[" + LocalDate.now() + "] " + action);
    }
}