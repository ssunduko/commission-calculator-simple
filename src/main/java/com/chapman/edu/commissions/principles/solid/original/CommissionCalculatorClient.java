package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.User;

import java.util.List;

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
