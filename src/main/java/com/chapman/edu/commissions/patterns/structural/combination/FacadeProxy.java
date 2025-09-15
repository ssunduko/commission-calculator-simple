package com.chapman.edu.commissions.patterns.structural.combination;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class demonstrates the combination of Facade and Proxy patterns.
 * 
 * Facade Pattern: Provides a simplified interface to a complex subsystem
 * Proxy Pattern: Provides a surrogate or placeholder for another object to control access to it
 * 
 * The FacadeProxy combines these patterns by:
 * 1. Using the Facade pattern to provide a simplified interface to the commission system
 * 2. Using the Proxy pattern to control access to the facade and add additional functionality
 */
public class FacadeProxy {

    /**
     * CommissionFacade - The Facade in the Facade pattern
     * Provides a simplified interface to the complex commission subsystem
     */
    public static class CommissionFacade {
        private DealService dealService;
        private UserService userService;
        private CommissionService commissionService;
        private ReportService reportService;
        
        public CommissionFacade() {
            this.dealService = new DealService();
            this.userService = new UserService();
            this.commissionService = new CommissionService();
            this.reportService = new ReportService();
        }
        
        /**
         * Create a new deal.
         */
        public Deal createDeal(String title, BigDecimal value, String salesRepId, List<DealProduct> products) {
            // Validate inputs
            if (title == null || title.isEmpty()) {
                throw new IllegalArgumentException("Deal title cannot be empty");
            }
            
            if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deal value must be positive");
            }
            
            if (salesRepId == null || salesRepId.isEmpty()) {
                throw new IllegalArgumentException("Sales rep ID cannot be empty");
            }
            
            // Create the deal
            Deal deal = dealService.createDeal(title, value, salesRepId);
            
            // Add products to the deal
            if (products != null) {
                for (DealProduct product : products) {
                    dealService.addProductToDeal(deal, product);
                }
            }
            
            return deal;
        }
        
        /**
         * Close a deal as won and calculate commission.
         */
        public BigDecimal closeDealAsWon(String dealId) {
            // Get the deal
            Deal deal = dealService.getDealById(dealId);
            if (deal == null) {
                throw new IllegalArgumentException("Deal not found: " + dealId);
            }
            
            // Update deal status
            dealService.updateDealStatus(deal, DealStatus.WON);
            
            // Get the sales rep
            User salesRep = userService.getUserById(deal.getSalesRepId());
            if (salesRep == null) {
                throw new IllegalArgumentException("Sales rep not found: " + deal.getSalesRepId());
            }
            
            // Get the commission plan
            CommissionPlan plan = userService.getCommissionPlan(salesRep.getId());
            
            // Calculate commission
            BigDecimal commission = commissionService.calculateCommission(deal, salesRep, plan);
            
            // Generate report
            reportService.generateCommissionReport(deal, salesRep, commission);
            
            return commission;
        }
        
        /**
         * Get all deals for a sales rep.
         */
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            return dealService.getDealsBySalesRep(salesRepId);
        }
        
        /**
         * Get total commission for a sales rep in a date range.
         */
        public BigDecimal getTotalCommission(String salesRepId, LocalDate startDate, LocalDate endDate) {
            // Validate inputs
            if (salesRepId == null || salesRepId.isEmpty()) {
                throw new IllegalArgumentException("Sales rep ID cannot be empty");
            }
            
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start and end dates cannot be null");
            }
            
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            
            // Get the sales rep
            User salesRep = userService.getUserById(salesRepId);
            if (salesRep == null) {
                throw new IllegalArgumentException("Sales rep not found: " + salesRepId);
            }
            
            // Get deals in date range
            List<Deal> deals = dealService.getDealsBySalesRepAndDateRange(salesRepId, startDate, endDate);
            
            // Calculate total commission
            BigDecimal totalCommission = BigDecimal.ZERO;
            CommissionPlan plan = userService.getCommissionPlan(salesRepId);
            
            for (Deal deal : deals) {
                if (deal.getStatus() == DealStatus.WON) {
                    BigDecimal commission = commissionService.calculateCommission(deal, salesRep, plan);
                    totalCommission = totalCommission.add(commission);
                }
            }
            
            return totalCommission;
        }
    }
    
    /**
     * CommissionFacadeProxy - The Proxy in the Proxy pattern
     * Controls access to the CommissionFacade and adds additional functionality
     */
    public static class CommissionFacadeProxy {
        private CommissionFacade realFacade;
        private User currentUser;
        private Map<String, Object> cache;
        private boolean loggingEnabled;
        
        public CommissionFacadeProxy(User currentUser) {
            this.realFacade = new CommissionFacade();
            this.currentUser = currentUser;
            this.cache = new HashMap<>();
            this.loggingEnabled = false;
        }
        
        /**
         * Enable or disable logging.
         */
        public void setLoggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
        }
        
        /**
         * Clear the cache.
         */
        public void clearCache() {
            cache.clear();
            if (loggingEnabled) {
                System.out.println("Cache cleared");
            }
        }
        
        /**
         * Create a new deal.
         * Proxy adds access control and logging.
         */
        public Deal createDeal(String title, BigDecimal value, String salesRepId, List<DealProduct> products) {
            // Check permissions
            if (!canCreateDeal(salesRepId)) {
                throw new SecurityException("User does not have permission to create deals for this sales rep");
            }
            
            // Log operation
            if (loggingEnabled) {
                System.out.println("Creating deal: " + title + " for sales rep: " + salesRepId);
            }
            
            // Delegate to real facade
            Deal deal = realFacade.createDeal(title, value, salesRepId, products);
            
            // Log result
            if (loggingEnabled) {
                System.out.println("Deal created: " + deal.getId());
            }
            
            return deal;
        }
        
        /**
         * Close a deal as won and calculate commission.
         * Proxy adds access control, caching, and logging.
         */
        public BigDecimal closeDealAsWon(String dealId) {
            // Check permissions
            Deal deal = realFacade.dealService.getDealById(dealId);
            if (deal == null) {
                throw new IllegalArgumentException("Deal not found: " + dealId);
            }
            
            if (!canManageDeal(deal)) {
                throw new SecurityException("User does not have permission to close this deal");
            }
            
            // Check cache
            String cacheKey = "commission_" + dealId;
            if (cache.containsKey(cacheKey)) {
                if (loggingEnabled) {
                    System.out.println("Returning cached commission for deal: " + dealId);
                }
                return (BigDecimal) cache.get(cacheKey);
            }
            
            // Log operation
            if (loggingEnabled) {
                System.out.println("Closing deal as won: " + dealId);
            }
            
            // Delegate to real facade
            BigDecimal commission = realFacade.closeDealAsWon(dealId);
            
            // Cache result
            cache.put(cacheKey, commission);
            
            // Log result
            if (loggingEnabled) {
                System.out.println("Deal closed with commission: $" + commission);
            }
            
            return commission;
        }
        
        /**
         * Get all deals for a sales rep.
         * Proxy adds access control, caching, and logging.
         */
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            // Check permissions
            if (!canViewDealsForSalesRep(salesRepId)) {
                throw new SecurityException("User does not have permission to view deals for this sales rep");
            }
            
            // Check cache
            String cacheKey = "deals_" + salesRepId;
            if (cache.containsKey(cacheKey)) {
                if (loggingEnabled) {
                    System.out.println("Returning cached deals for sales rep: " + salesRepId);
                }
                return (List<Deal>) cache.get(cacheKey);
            }
            
            // Log operation
            if (loggingEnabled) {
                System.out.println("Getting deals for sales rep: " + salesRepId);
            }
            
            // Delegate to real facade
            List<Deal> deals = realFacade.getDealsBySalesRep(salesRepId);
            
            // Cache result
            cache.put(cacheKey, deals);
            
            // Log result
            if (loggingEnabled) {
                System.out.println("Found " + deals.size() + " deals for sales rep: " + salesRepId);
            }
            
            return deals;
        }
        
        /**
         * Get total commission for a sales rep in a date range.
         * Proxy adds access control, caching, and logging.
         */
        public BigDecimal getTotalCommission(String salesRepId, LocalDate startDate, LocalDate endDate) {
            // Check permissions
            if (!canViewCommissionForSalesRep(salesRepId)) {
                throw new SecurityException("User does not have permission to view commission for this sales rep");
            }
            
            // Check cache
            String cacheKey = "total_commission_" + salesRepId + "_" + startDate + "_" + endDate;
            if (cache.containsKey(cacheKey)) {
                if (loggingEnabled) {
                    System.out.println("Returning cached total commission for sales rep: " + salesRepId);
                }
                return (BigDecimal) cache.get(cacheKey);
            }
            
            // Log operation
            if (loggingEnabled) {
                System.out.println("Getting total commission for sales rep: " + salesRepId + 
                                  " from " + startDate + " to " + endDate);
            }
            
            // Delegate to real facade
            BigDecimal totalCommission = realFacade.getTotalCommission(salesRepId, startDate, endDate);
            
            // Cache result
            cache.put(cacheKey, totalCommission);
            
            // Log result
            if (loggingEnabled) {
                System.out.println("Total commission: $" + totalCommission);
            }
            
            return totalCommission;
        }
        
        // Permission check methods
        private boolean canCreateDeal(String salesRepId) {
            // Allow if user is the sales rep
            if (currentUser.getId().equals(salesRepId)) {
                return true;
            }
            
            // Allow if user is a manager or admin
            if (currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN) ||
                currentUser.hasRole(UserRole.SYSTEM_ADMIN)) {
                return true;
            }
            
            return false;
        }
        
        private boolean canManageDeal(Deal deal) {
            // Allow if user is the sales rep for the deal
            if (currentUser.getId().equals(deal.getSalesRepId())) {
                return true;
            }
            
            // Allow if user is a manager or admin
            if (currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN) ||
                currentUser.hasRole(UserRole.SYSTEM_ADMIN)) {
                return true;
            }
            
            return false;
        }
        
        private boolean canViewDealsForSalesRep(String salesRepId) {
            // Allow if user is the sales rep
            if (currentUser.getId().equals(salesRepId)) {
                return true;
            }
            
            // Allow if user is a manager or admin
            if (currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN) ||
                currentUser.hasRole(UserRole.SYSTEM_ADMIN)) {
                return true;
            }
            
            return false;
        }
        
        private boolean canViewCommissionForSalesRep(String salesRepId) {
            // Allow if user is the sales rep
            if (currentUser.getId().equals(salesRepId)) {
                return true;
            }
            
            // Allow if user is a manager or admin
            if (currentUser.hasRole(UserRole.SALES_MANAGER) || 
                currentUser.hasRole(UserRole.FINANCE_ADMIN) ||
                currentUser.hasRole(UserRole.SYSTEM_ADMIN)) {
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Supporting service classes used by the CommissionFacade
     */
    public static class DealService {
        private List<Deal> deals;
        
        public DealService() {
            this.deals = new ArrayList<>();
        }
        
        public Deal createDeal(String title, BigDecimal value, String salesRepId) {
            Deal deal = new Deal();
            deal.setId("deal-" + (deals.size() + 1));
            deal.setTitle(title);
            deal.setValue(value);
            deal.setSalesRepId(salesRepId);
            deal.setStatus(DealStatus.OPEN);
            deals.add(deal);
            return deal;
        }
        
        public Deal getDealById(String dealId) {
            for (Deal deal : deals) {
                if (deal.getId().equals(dealId)) {
                    return deal;
                }
            }
            return null;
        }
        
        public void updateDealStatus(Deal deal, DealStatus status) {
            deal.setStatus(status);
            if (status == DealStatus.WON) {
                deal.setCloseDate(LocalDate.now());
            }
        }
        
        public void addProductToDeal(Deal deal, DealProduct product) {
            deal.getProducts().add(product);
        }
        
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            List<Deal> result = new ArrayList<>();
            for (Deal deal : deals) {
                if (deal.getSalesRepId().equals(salesRepId)) {
                    result.add(deal);
                }
            }
            return result;
        }
        
        public List<Deal> getDealsBySalesRepAndDateRange(String salesRepId, LocalDate startDate, LocalDate endDate) {
            List<Deal> result = new ArrayList<>();
            for (Deal deal : deals) {
                if (deal.getSalesRepId().equals(salesRepId) && 
                    deal.getCloseDate() != null &&
                    !deal.getCloseDate().isBefore(startDate) && 
                    !deal.getCloseDate().isAfter(endDate)) {
                    result.add(deal);
                }
            }
            return result;
        }
    }
    
    public static class UserService {
        private Map<String, User> users;
        private Map<String, CommissionPlan> plans;
        
        public UserService() {
            this.users = new HashMap<>();
            this.plans = new HashMap<>();
            
            // Create sample users
            User user1 = new User();
            user1.setId("user-1");
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.addRole(UserRole.SALES_REP);
            
            User user2 = new User();
            user2.setId("user-2");
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.addRole(UserRole.SALES_MANAGER);
            
            users.put(user1.getId(), user1);
            users.put(user2.getId(), user2);
            
            // Create commission plans
            CommissionPlan plan1 = new CommissionPlan();
            plan1.setId("plan-1");
            plan1.setName("Standard Sales Rep Plan");
            
            CommissionPlan plan2 = new CommissionPlan();
            plan2.setId("plan-2");
            plan2.setName("Standard Manager Plan");
            
            plans.put(user1.getId(), plan1);
            plans.put(user2.getId(), plan2);
        }
        
        public User getUserById(String userId) {
            return users.get(userId);
        }
        
        public CommissionPlan getCommissionPlan(String userId) {
            return plans.get(userId);
        }
    }
    
    public static class CommissionService {
        public BigDecimal calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // Simple commission calculation (10% of deal value)
            return deal.getValue().multiply(new BigDecimal("0.10"));
        }
    }
    
    public static class ReportService {
        public void generateCommissionReport(Deal deal, User salesRep, BigDecimal commission) {
            System.out.println("Commission Report");
            System.out.println("----------------");
            System.out.println("Deal: " + deal.getTitle() + " (" + deal.getId() + ")");
            System.out.println("Sales Rep: " + salesRep.getFullName());
            System.out.println("Commission: $" + commission);
            System.out.println("Date: " + LocalDate.now());
            System.out.println("----------------");
        }
    }
    
    /**
     * Client code that demonstrates how to use the FacadeProxy
     */
    public static void main(String[] args) {
        System.out.println("===== Facade + Proxy Pattern Combination Example =====\n");
        
        // Create a sales rep user
        User salesRep = new User();
        salesRep.setId("user-1");
        salesRep.setFirstName("John");
        salesRep.setLastName("Doe");
        salesRep.addRole(UserRole.SALES_REP);
        
        // Create a manager user
        User manager = new User();
        manager.setId("user-2");
        manager.setFirstName("Jane");
        manager.setLastName("Smith");
        manager.addRole(UserRole.SALES_MANAGER);
        
        // Create a proxy for the sales rep
        System.out.println("Creating proxy for sales rep...");
        CommissionFacadeProxy salesRepProxy = new CommissionFacadeProxy(salesRep);
        salesRepProxy.setLoggingEnabled(true);
        
        // Create a deal
        System.out.println("\nCreating a deal...");
        List<DealProduct> products = new ArrayList<>();
        
        DealProduct product1 = new DealProduct();
        product1.setProductId("prod-1");
        product1.setProductName("Software License");
        product1.setQuantity(5);
        product1.setPrice(new BigDecimal("1000.00"));
        products.add(product1);
        
        Deal deal = salesRepProxy.createDeal("Enterprise Deal", new BigDecimal("5000.00"), salesRep.getId(), products);
        
        // Close the deal as won
        System.out.println("\nClosing the deal as won...");
        BigDecimal commission = salesRepProxy.closeDealAsWon(deal.getId());
        
        // Get deals for the sales rep
        System.out.println("\nGetting deals for the sales rep...");
        List<Deal> deals = salesRepProxy.getDealsBySalesRep(salesRep.getId());
        System.out.println("Number of deals: " + deals.size());
        
        // Get total commission for the sales rep
        System.out.println("\nGetting total commission for the sales rep...");
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        BigDecimal totalCommission = salesRepProxy.getTotalCommission(salesRep.getId(), startDate, endDate);
        System.out.println("Total commission: $" + totalCommission);
        
        // Create a proxy for the manager
        System.out.println("\nCreating proxy for manager...");
        CommissionFacadeProxy managerProxy = new CommissionFacadeProxy(manager);
        managerProxy.setLoggingEnabled(true);
        
        // Manager can view deals for the sales rep
        System.out.println("\nManager viewing deals for the sales rep...");
        deals = managerProxy.getDealsBySalesRep(salesRep.getId());
        System.out.println("Number of deals: " + deals.size());
        
        System.out.println("\nBenefits of combining Facade and Proxy patterns:");
        System.out.println("1. Simplified interface to complex subsystems (Facade)");
        System.out.println("2. Control access to the facade (Proxy)");
        System.out.println("3. Add additional functionality like caching and logging (Proxy)");
        System.out.println("4. Separation of concerns: business logic vs. cross-cutting concerns");
    }
}