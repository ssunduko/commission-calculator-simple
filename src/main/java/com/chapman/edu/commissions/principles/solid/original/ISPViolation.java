package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.User;

import java.util.List;

/**
 * This interface violates the Interface Segregation Principle (ISP).
 * 
 * ISP states that no client should be forced to depend on methods it does not use.
 * This means that interfaces should be client-specific rather than general-purpose.
 * 
 * The violation occurs because:
 * 1. The CommissionSystemService interface is too large and has too many responsibilities
 * 2. Clients that only need commission calculation are forced to depend on dispute resolution methods
 * 3. Clients that only need reporting are forced to depend on user management methods
 */
public interface ISPViolation {
    
    // Commission calculation methods
    CommissionCalculation calculateCommission(String dealId, String planId);
    CommissionCalculation recalculateCommission(String calculationId);
    List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId);
    List<CommissionCalculation> getCalculationsByDeal(String dealId);
    
    // Deal management methods
    Deal createDeal(Deal deal);
    Deal updateDeal(Deal deal);
    Deal getDealById(String dealId);
    List<Deal> getAllDeals();
    List<Deal> getDealsBySalesRep(String salesRepId);
    void deleteDeal(String dealId);
    
    // User management methods
    User createUser(User user);
    User updateUser(User user);
    User getUserById(String userId);
    List<User> getAllUsers();
    void deleteUser(String userId);
    boolean authenticateUser(String username, String password);
    void changePassword(String userId, String oldPassword, String newPassword);
    
    // Dispute resolution methods
    Dispute createDispute(Dispute dispute);
    Dispute updateDispute(Dispute dispute);
    Dispute getDisputeById(String disputeId);
    List<Dispute> getAllDisputes();
    List<Dispute> getDisputesBySalesRep(String salesRepId);
    void resolveDispute(String disputeId, String resolution, String resolvedBy);
    
    // Reporting methods
    byte[] generateCommissionReport(String salesRepId, String startDate, String endDate);
    byte[] generateTeamReport(String managerId, String startDate, String endDate);
    byte[] generateSystemWideReport(String startDate, String endDate);
    void emailReport(String reportType, String recipientId, String startDate, String endDate);
    void scheduleRecurringReport(String reportType, String recipientId, String frequency);
    
    // System administration methods
    void backupDatabase();
    void restoreDatabase(String backupId);
    void purgeOldData(String olderThan);
    void updateSystemSettings(String settingName, String settingValue);
    String getSystemSetting(String settingName);
    void logSystemActivity(String activity, String performedBy);
}

/**
 * This class implements the ISPViolation interface but only uses a small subset of its methods.
 * It violates ISP because it's forced to implement methods it doesn't need.
 */
class CommissionCalculatorClient implements ISPViolation {
    
    // This client only cares about commission calculation
    
    @Override
    public CommissionCalculation calculateCommission(String dealId, String planId) {
        // Actual implementation
        return null;
    }
    
    @Override
    public CommissionCalculation recalculateCommission(String calculationId) {
        // Actual implementation
        return null;
    }
    
    @Override
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        // Actual implementation
        return null;
    }
    
    @Override
    public List<CommissionCalculation> getCalculationsByDeal(String dealId) {
        // Actual implementation
        return null;
    }
    
    // The client is forced to implement all these methods it doesn't need
    
    @Override
    public Deal createDeal(Deal deal) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public Deal updateDeal(Deal deal) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public Deal getDealById(String dealId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public List<Deal> getAllDeals() {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public List<Deal> getDealsBySalesRep(String salesRepId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void deleteDeal(String dealId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public User createUser(User user) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public User updateUser(User user) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public User getUserById(String userId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public List<User> getAllUsers() {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void deleteUser(String userId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public boolean authenticateUser(String username, String password) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public Dispute createDispute(Dispute dispute) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public Dispute updateDispute(Dispute dispute) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public Dispute getDisputeById(String disputeId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public List<Dispute> getAllDisputes() {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public List<Dispute> getDisputesBySalesRep(String salesRepId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void resolveDispute(String disputeId, String resolution, String resolvedBy) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public byte[] generateCommissionReport(String salesRepId, String startDate, String endDate) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public byte[] generateTeamReport(String managerId, String startDate, String endDate) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public byte[] generateSystemWideReport(String startDate, String endDate) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void emailReport(String reportType, String recipientId, String startDate, String endDate) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void scheduleRecurringReport(String reportType, String recipientId, String frequency) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void backupDatabase() {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void restoreDatabase(String backupId) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void purgeOldData(String olderThan) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void updateSystemSettings(String settingName, String settingValue) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public String getSystemSetting(String settingName) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
    
    @Override
    public void logSystemActivity(String activity, String performedBy) {
        throw new UnsupportedOperationException("Not needed by this client");
    }
}