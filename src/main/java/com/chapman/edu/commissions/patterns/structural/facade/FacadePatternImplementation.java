package com.chapman.edu.commissions.patterns.structural.facade;

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
import java.util.List;
import java.util.UUID;

/**
 * This class provides a concrete implementation of the Facade Pattern.
 * 
 * The Facade Pattern is implemented here using the Commission Calculator domain model.
 * The CommissionFacade class provides a simplified interface to the complex subsystem
 * of deal management, user management, and commission calculations.
 */
public class FacadePatternImplementation {

    /**
     * The CommissionFacade class is the facade that provides a simplified interface
     * to the complex subsystem of commission calculations.
     */
    public static class CommissionFacade {
        private DealService dealService;
        private UserService userService;
        private CommissionService commissionService;
        private ReportService reportService;
        
        /**
         * Constructor initializes all the subsystem components
         */
        public CommissionFacade() {
            this.dealService = new DealService();
            this.userService = new UserService();
            this.commissionService = new CommissionService();
            this.reportService = new ReportService();
        }
        
        /**
         * Creates a new deal with the given details
         * 
         * @param title The title of the deal
         * @param value The value of the deal
         * @param salesRepId The ID of the sales representative
         * @param products The list of products in the deal
         * @return The created deal
         */
        public Deal createDeal(String title, BigDecimal value, String salesRepId, List<DealProduct> products) {
            // Validate the sales rep exists
            User salesRep = userService.getUserById(salesRepId);
            if (salesRep == null || !salesRep.isSalesRep()) {
                throw new IllegalArgumentException("Invalid sales representative ID");
            }
            
            // Create the deal
            Deal deal = dealService.createDeal(title, value, salesRepId);
            
            // Add products to the deal
            for (DealProduct product : products) {
                dealService.addProductToDeal(deal, product);
            }
            
            return deal;
        }
        
        /**
         * Marks a deal as won and calculates the commission
         * 
         * @param dealId The ID of the deal
         * @return The calculated commission
         */
        public BigDecimal closeDealAsWon(String dealId) {
            // Get the deal
            Deal deal = dealService.getDealById(dealId);
            if (deal == null) {
                throw new IllegalArgumentException("Invalid deal ID");
            }
            
            // Update the deal status
            dealService.updateDealStatus(deal, DealStatus.WON);
            
            // Get the sales rep
            User salesRep = userService.getUserById(deal.getSalesRepId());
            
            // Get the commission plan
            CommissionPlan plan = userService.getCommissionPlan(salesRep.getId());
            
            // Calculate the commission
            BigDecimal commission = commissionService.calculateCommission(deal, salesRep, plan);
            
            // Generate a commission report
            reportService.generateCommissionReport(deal, salesRep, commission);
            
            return commission;
        }
        
        /**
         * Gets all deals for a sales representative
         * 
         * @param salesRepId The ID of the sales representative
         * @return The list of deals
         */
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            // Validate the sales rep exists
            User salesRep = userService.getUserById(salesRepId);
            if (salesRep == null || !salesRep.isSalesRep()) {
                throw new IllegalArgumentException("Invalid sales representative ID");
            }
            
            // Get the deals
            return dealService.getDealsBySalesRep(salesRepId);
        }
        
        /**
         * Gets the total commission earned by a sales representative
         * 
         * @param salesRepId The ID of the sales representative
         * @param startDate The start date for the commission period
         * @param endDate The end date for the commission period
         * @return The total commission
         */
        public BigDecimal getTotalCommission(String salesRepId, LocalDate startDate, LocalDate endDate) {
            // Validate the sales rep exists
            User salesRep = userService.getUserById(salesRepId);
            if (salesRep == null || !salesRep.isSalesRep()) {
                throw new IllegalArgumentException("Invalid sales representative ID");
            }
            
            // Get the deals for the period
            List<Deal> deals = dealService.getDealsBySalesRepAndDateRange(salesRepId, startDate, endDate);
            
            // Get the commission plan
            CommissionPlan plan = userService.getCommissionPlan(salesRep.getId());
            
            // Calculate the total commission
            BigDecimal totalCommission = BigDecimal.ZERO;
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
     * DealService is a subsystem component that handles operations related to deals
     */
    private static class DealService {
        // In a real implementation, this would use a database
        private List<Deal> deals = new ArrayList<>();
        
        public Deal createDeal(String title, BigDecimal value, String salesRepId) {
            Deal deal = new Deal(title, value, salesRepId);
            deal.setId(UUID.randomUUID().toString());
            deal.setCreatedDate(LocalDate.now());
            deals.add(deal);
            return deal;
        }
        
        public Deal getDealById(String dealId) {
            return deals.stream()
                    .filter(deal -> deal.getId().equals(dealId))
                    .findFirst()
                    .orElse(null);
        }
        
        public void updateDealStatus(Deal deal, DealStatus status) {
            deal.setStatus(status);
            if (status == DealStatus.WON || status == DealStatus.LOST) {
                deal.setCloseDate(LocalDate.now());
            }
        }
        
        public void addProductToDeal(Deal deal, DealProduct product) {
            product.setId(UUID.randomUUID().toString());
            product.setDealId(deal.getId());
            deal.addProduct(product);
        }
        
        public List<Deal> getDealsBySalesRep(String salesRepId) {
            return deals.stream()
                    .filter(deal -> deal.getSalesRepId().equals(salesRepId))
                    .toList();
        }
        
        public List<Deal> getDealsBySalesRepAndDateRange(String salesRepId, LocalDate startDate, LocalDate endDate) {
            return deals.stream()
                    .filter(deal -> deal.getSalesRepId().equals(salesRepId))
                    .filter(deal -> {
                        LocalDate closeDate = deal.getCloseDate();
                        return closeDate != null && 
                               !closeDate.isBefore(startDate) && 
                               !closeDate.isAfter(endDate);
                    })
                    .toList();
        }
    }
    
    /**
     * UserService is a subsystem component that handles operations related to users
     */
    private static class UserService {
        // In a real implementation, this would use a database
        private List<User> users = new ArrayList<>();
        private List<CommissionPlan> plans = new ArrayList<>();
        
        public UserService() {
            // Create some sample users
            User user1 = new User("jdoe", "john.doe@example.com", "John", "Doe");
            user1.setId("user1");
            user1.addRole(UserRole.SALES_REP);
            users.add(user1);
            
            User user2 = new User("jsmith", "jane.smith@example.com", "Jane", "Smith");
            user2.setId("user2");
            user2.addRole(UserRole.SALES_MANAGER);
            users.add(user2);
            
            // Create a sample commission plan
            CommissionPlan plan = new CommissionPlan("Standard Plan", null);
            plan.setId("plan1");
            plans.add(plan);
        }
        
        public User getUserById(String userId) {
            return users.stream()
                    .filter(user -> user.getId().equals(userId))
                    .findFirst()
                    .orElse(null);
        }
        
        public CommissionPlan getCommissionPlan(String userId) {
            // In a real implementation, this would retrieve the specific plan for the user
            return plans.get(0);
        }
    }
    
    /**
     * CommissionService is a subsystem component that handles commission calculations
     */
    private static class CommissionService {
        public BigDecimal calculateCommission(Deal deal, User salesRep, CommissionPlan plan) {
            // In a real implementation, this would use the rules and tiers from the plan
            // For simplicity, we'll just calculate a flat 5% commission
            return deal.getValue().multiply(new BigDecimal("0.05"));
        }
    }
    
    /**
     * ReportService is a subsystem component that handles report generation
     */
    private static class ReportService {
        public void generateCommissionReport(Deal deal, User salesRep, BigDecimal commission) {
            // In a real implementation, this would generate a report
            System.out.println("Commission Report");
            System.out.println("----------------");
            System.out.println("Deal: " + deal.getTitle());
            System.out.println("Sales Rep: " + salesRep.getFullName());
            System.out.println("Commission: " + commission);
        }
    }
}