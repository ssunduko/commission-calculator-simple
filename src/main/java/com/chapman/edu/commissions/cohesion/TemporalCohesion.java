package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionCalculation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of Temporal Cohesion.
 * 
 * Temporal Cohesion occurs when parts of a module are grouped together because they are
 * executed at the same time or during the same phase of execution, even though they might
 * be performing different functions.
 * 
 * This class demonstrates temporal cohesion by grouping different operations that all need
 * to be performed when a deal is closed (marked as WON). These operations include updating
 * the deal status, calculating commissions, notifying stakeholders, and updating reports.
 */
public class TemporalCohesion {
    
    private List<User> users;
    private List<Deal> deals;
    private List<CommissionCalculation> commissionCalculations;
    
    /**
     * Constructor initializing the lists.
     */
    public TemporalCohesion() {
        this.users = new ArrayList<>();
        this.deals = new ArrayList<>();
        this.commissionCalculations = new ArrayList<>();
    }
    
    /**
     * Processes a deal being closed (marked as WON).
     * This method demonstrates temporal cohesion by grouping different operations
     * that all need to be performed at the same time (when a deal is closed).
     * 
     * @param deal the deal being closed
     * @param closedBy the user who closed the deal
     * @return true if the deal was successfully processed, false otherwise
     */
    public boolean processDealClosed(Deal deal, User closedBy) {
        if (deal == null || closedBy == null) {
            return false;
        }
        // 1. Update deal status to WON
        updateDealStatus(deal, DealStatus.WON);
        // 2. Set the close date to today
        setDealCloseDate(deal, LocalDate.now());
        // 3. Calculate commission for the sales rep
        calculateCommission(deal);
        // 4. Send notifications to stakeholders
        notifySalesRep(deal);
        notifySalesManager(deal);
        notifyFinanceTeam(deal);
        // 5. Update reports and dashboards
        updateSalesReport(deal);
        updateCommissionReport(deal);
        updateRevenueForecasts(deal);
        // 6. Log the activity
        logDealClosedActivity(deal, closedBy);
        
        return true;
    }
    
    /**
     * Updates the status of a deal.
     * 
     * @param deal the deal to update
     * @param newStatus the new status
     */
    private void updateDealStatus(Deal deal, DealStatus newStatus) {
        deal.setStatus(newStatus);
        System.out.println("Deal status updated to: " + newStatus);
    }
    
    /**
     * Sets the close date of a deal.
     * 
     * @param deal the deal to update
     * @param closeDate the close date
     */
    private void setDealCloseDate(Deal deal, LocalDate closeDate) {
        deal.setCloseDate(closeDate);
        System.out.println("Deal close date set to: " + closeDate);
    }
    
    /**
     * Calculates commission for a deal.
     * 
     * @param deal the deal to calculate commission for
     */
    private void calculateCommission(Deal deal) {
        // Simplified commission calculation (in a real system, this would be more complex)
        BigDecimal commissionAmount = deal.getValue().multiply(new BigDecimal("0.1")); // 10% commission
        
        CommissionCalculation calculation = new CommissionCalculation(
                deal.getId(), 
                deal.getSalesRepId(), 
                commissionAmount);
        
        commissionCalculations.add(calculation);
        System.out.println("Commission calculated: $" + commissionAmount);
    }
    
    /**
     * Notifies the sales rep about the closed deal.
     * 
     * @param deal the closed deal
     */
    private void notifySalesRep(Deal deal) {
        // In a real system, this would send an email or notification to the sales rep
        System.out.println("Notification sent to sales rep for deal: " + deal.getTitle());
    }
    
    /**
     * Notifies the sales manager about the closed deal.
     * 
     * @param deal the closed deal
     */
    private void notifySalesManager(Deal deal) {
        // In a real system, this would send an email or notification to the sales manager
        System.out.println("Notification sent to sales manager for deal: " + deal.getTitle());
    }
    
    /**
     * Notifies the finance team about the closed deal.
     * 
     * @param deal the closed deal
     */
    private void notifyFinanceTeam(Deal deal) {
        // In a real system, this would send an email or notification to the finance team
        System.out.println("Notification sent to finance team for deal: " + deal.getTitle());
    }
    
    /**
     * Updates the sales report with the closed deal.
     * 
     * @param deal the closed deal
     */
    private void updateSalesReport(Deal deal) {
        // In a real system, this would update a sales report
        System.out.println("Sales report updated with deal: " + deal.getTitle());
    }
    
    /**
     * Updates the commission report with the closed deal.
     * 
     * @param deal the closed deal
     */
    private void updateCommissionReport(Deal deal) {
        // In a real system, this would update a commission report
        System.out.println("Commission report updated with deal: " + deal.getTitle());
    }
    
    /**
     * Updates revenue forecasts with the closed deal.
     * 
     * @param deal the closed deal
     */
    private void updateRevenueForecasts(Deal deal) {
        // In a real system, this would update revenue forecasts
        System.out.println("Revenue forecasts updated with deal: " + deal.getTitle());
    }
    
    /**
     * Logs the deal closed activity.
     * 
     * @param deal the closed deal
     * @param closedBy the user who closed the deal
     */
    private void logDealClosedActivity(Deal deal, User closedBy) {
        // In a real system, this would log the activity in an audit log
        System.out.println("Deal closed activity logged: " + deal.getTitle() + 
                " closed by " + closedBy.getFullName());
    }
}