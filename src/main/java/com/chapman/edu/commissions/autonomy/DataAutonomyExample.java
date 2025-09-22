package com.chapman.edu.commissions.autonomy;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Autonomy Example
 * 
 * Data Autonomy refers to a component's ability to own and manage its data storage.
 * This class demonstrates data autonomy by encapsulating all data storage and management
 * for deals and users within the component itself, without relying on external data sources.
 * 
 * Key characteristics of Data Autonomy:
 * 1. The component owns its data storage mechanism
 * 2. Data access is controlled through well-defined interfaces
 * 3. Internal data structures are hidden from external components
 * 4. The component is responsible for data validation and integrity
 */
public class DataAutonomyExample {

    /**
     * Private data stores - these are encapsulated within the component
     * and not directly accessible from outside
     */
    private final Map<String, Deal> dealStore = new HashMap<>();
    private final Map<String, User> userStore = new HashMap<>();
    
    /**
     * Constructor that initializes the data stores with some sample data
     */
    public DataAutonomyExample() {
        initializeSampleData();
    }
    
    /**
     * Creates a new deal and stores it in the internal data store
     * 
     * @param title The title of the deal
     * @param value The value of the deal
     * @param salesRepId The ID of the sales representative
     * @return The ID of the created deal
     */
    public String createDeal(String title, BigDecimal value, String salesRepId) {
        // Validate that the sales rep exists
        if (!userStore.containsKey(salesRepId)) {
            throw new IllegalArgumentException("Sales representative not found");
        }
        
        // Create a new deal with a unique ID
        Deal deal = new Deal(title, value, salesRepId);
        String dealId = "DEAL-" + UUID.randomUUID().toString().substring(0, 8);
        deal.setId(dealId);
        deal.setCreatedDate(LocalDate.now());
        
        // Store the deal
        dealStore.put(dealId, deal);
        
        return dealId;
    }
    
    /**
     * Adds a product to an existing deal
     * 
     * @param dealId The ID of the deal
     * @param productName The name of the product
     * @param quantity The quantity of the product
     * @param price The price of the product
     * @return The ID of the created deal product
     */
    public String addProductToDeal(String dealId, String productName, int quantity, BigDecimal price) {
        // Validate that the deal exists
        Deal deal = getDealById(dealId);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found");
        }
        
        // Create a new deal product
        String productId = "PROD-" + UUID.randomUUID().toString().substring(0, 8);
        DealProduct product = new DealProduct(productId, productName, quantity, price);
        product.setId("DP-" + UUID.randomUUID().toString().substring(0, 8));
        product.setDealId(dealId);
        
        // Add the product to the deal
        deal.addProduct(product);
        
        // Update the deal value based on the products
        deal.setValue(deal.calculateTotalValue());
        
        return product.getId();
    }
    
    /**
     * Updates the status of a deal
     * 
     * @param dealId The ID of the deal
     * @param status The new status of the deal
     */
    public void updateDealStatus(String dealId, DealStatus status) {
        // Validate that the deal exists
        Deal deal = getDealById(dealId);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found");
        }
        
        // Update the deal status
        deal.setStatus(status);
        
        // If the deal is won or lost, set the close date
        if (status == DealStatus.WON || status == DealStatus.LOST) {
            deal.setCloseDate(LocalDate.now());
        }
    }
    
    /**
     * Creates a new user and stores it in the internal data store
     * 
     * @param username The username of the user
     * @param email The email of the user
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param role The role of the user
     * @return The ID of the created user
     */
    public String createUser(String username, String email, String firstName, String lastName, UserRole role) {
        // Create a new user with a unique ID
        User user = new User(username, email, firstName, lastName);
        String userId = "USER-" + UUID.randomUUID().toString().substring(0, 8);
        user.setId(userId);
        user.addRole(role);
        
        // Store the user
        userStore.put(userId, user);
        
        return userId;
    }
    
    /**
     * Gets a deal by its ID
     * 
     * @param dealId The ID of the deal
     * @return The deal, or null if not found
     */
    public Deal getDealById(String dealId) {
        return dealStore.get(dealId);
    }
    
    /**
     * Gets a user by their ID
     * 
     * @param userId The ID of the user
     * @return The user, or null if not found
     */
    public User getUserById(String userId) {
        return userStore.get(userId);
    }
    
    /**
     * Gets all deals for a specific sales representative
     * 
     * @param salesRepId The ID of the sales representative
     * @return A list of deals for the sales representative
     */
    public List<Deal> getDealsBySalesRep(String salesRepId) {
        List<Deal> result = new ArrayList<>();
        
        for (Deal deal : dealStore.values()) {
            if (deal.getSalesRepId().equals(salesRepId)) {
                result.add(deal);
            }
        }
        
        return result;
    }
    
    /**
     * Gets all users with a specific role
     * 
     * @param role The role to filter by
     * @return A list of users with the specified role
     */
    public List<User> getUsersByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        
        for (User user : userStore.values()) {
            if (user.hasRole(role)) {
                result.add(user);
            }
        }
        
        return result;
    }
    
    /**
     * Initializes the data stores with some sample data
     */
    private void initializeSampleData() {
        // Create some users
        String salesRep1Id = createUser("jsmith", "jsmith@example.com", "John", "Smith", UserRole.SALES_REP);
        String salesRep2Id = createUser("mjohnson", "mjohnson@example.com", "Mary", "Johnson", UserRole.SALES_REP);
        createUser("rbrown", "rbrown@example.com", "Robert", "Brown", UserRole.SALES_MANAGER);
        
        // Create some deals
        String deal1Id = createDeal("Software License Deal", new BigDecimal("10000.00"), salesRep1Id);
        addProductToDeal(deal1Id, "Enterprise License", 1, new BigDecimal("10000.00"));
        
        String deal2Id = createDeal("Hardware Purchase", new BigDecimal("5000.00"), salesRep2Id);
        addProductToDeal(deal2Id, "Server", 2, new BigDecimal("2000.00"));
        addProductToDeal(deal2Id, "Workstation", 3, new BigDecimal("1000.00"));
        
        // Update deal statuses
        updateDealStatus(deal1Id, DealStatus.WON);
        updateDealStatus(deal2Id, DealStatus.OPEN);
    }
    
    /**
     * Main method to demonstrate the usage of the DataAutonomyExample class
     */
    public static void main(String[] args) {
        DataAutonomyExample example = new DataAutonomyExample();
        
        // Get all sales representatives
        List<User> salesReps = example.getUsersByRole(UserRole.SALES_REP);
        System.out.println("Sales Representatives:");
        for (User user : salesReps) {
            System.out.println("- " + user.getFullName() + " (" + user.getEmail() + ")");
            
            // Get deals for this sales rep
            List<Deal> deals = example.getDealsBySalesRep(user.getId());
            System.out.println("  Deals:");
            for (Deal deal : deals) {
                System.out.println("  - " + deal.getTitle() + " (" + deal.getStatus() + "): $" + deal.getValue());
                
                // Get products for this deal
                System.out.println("    Products:");
                for (DealProduct product : deal.getProducts()) {
                    System.out.println("    - " + product.getProductName() + " x" + product.getQuantity() + ": $" + product.getPrice());
                }
            }
            System.out.println();
        }
    }
}