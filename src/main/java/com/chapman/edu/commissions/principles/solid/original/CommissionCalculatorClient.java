package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the ISPViolation interface but only uses a small subset of its methods.
 * It violates ISP because it's forced to implement methods it doesn't need.
 * @author Sergey Sundukovskiy Ph.D.
 * @version 1.0
 */
class CommissionCalculatorClient implements ISPViolation {

    // This client only cares about commission calculation

    // Simulated database for demonstration purposes
    private final Map<String, CommissionCalculation> calculations = new HashMap<>();
    private final Map<String, List<CommissionCalculation>> calculationsBySalesRep = new HashMap<>();
    private final Map<String, List<CommissionCalculation>> calculationsByDeal = new HashMap<>();

    @Override
    public CommissionCalculation calculateCommission(String dealId, String planId) {
        // Simplified implementation for demonstration purposes
        CommissionCalculation calculation = new CommissionCalculation();
        calculation.setId("CALC-" + System.currentTimeMillis());
        calculation.setDealId(dealId);
        calculation.setPlanId(planId);

        // Store the calculation
        calculations.put(calculation.getId(), calculation);

        // Update indexes
        addToSalesRepIndex(calculation);
        addToDealIndex(calculation);

        return calculation;
    }

    @Override
    public CommissionCalculation recalculateCommission(String calculationId) {
        // ISP Violation: This method returns null, indicating it's not fully implemented
        // but we're forced to implement it due to the ISPViolation interface
        return null;
    }

    @Override
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        return calculationsBySalesRep.getOrDefault(salesRepId, new ArrayList<>());
    }

    @Override
    public List<CommissionCalculation> getCalculationsByDeal(String dealId) {
        // ISP Violation: This method returns null, indicating it's not fully implemented
        // but we're forced to implement it due to the ISPViolation interface
        return null;
    }

    private void addToSalesRepIndex(CommissionCalculation calculation) {
        String salesRepId = calculation.getSalesRepId();
        if (salesRepId != null) {
            List<CommissionCalculation> salesRepCalculations = calculationsBySalesRep.computeIfAbsent(
                    salesRepId, k -> new ArrayList<>());
            salesRepCalculations.add(calculation);
        }
    }

    private void addToDealIndex(CommissionCalculation calculation) {
        String dealId = calculation.getDealId();
        if (dealId != null) {
            List<CommissionCalculation> dealCalculations = calculationsByDeal.computeIfAbsent(
                    dealId, k -> new ArrayList<>());
            dealCalculations.add(calculation);
        }
    }

    // The client is forced to implement all these methods it doesn't need

    // The following methods are ISP violations - this client doesn't need them
    // but is forced to implement them due to the ISPViolation interface

    @Override
    public Deal createDeal(Deal deal) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public Deal updateDeal(Deal deal) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public Deal getDealById(String dealId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public List<Deal> getAllDeals() {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public List<Deal> getDealsBySalesRep(String salesRepId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void deleteDeal(String dealId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public User createUser(User user) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public User updateUser(User user) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public User getUserById(String userId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public List<User> getAllUsers() {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void deleteUser(String userId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public Dispute createDispute(Dispute dispute) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public Dispute updateDispute(Dispute dispute) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public Dispute getDisputeById(String disputeId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public List<Dispute> getAllDisputes() {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public List<Dispute> getDisputesBySalesRep(String salesRepId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void resolveDispute(String disputeId, String resolution, String resolvedBy) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public byte[] generateCommissionReport(String salesRepId, String startDate, String endDate) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public byte[] generateTeamReport(String managerId, String startDate, String endDate) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public byte[] generateSystemWideReport(String startDate, String endDate) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void emailReport(String reportType, String recipientId, String startDate, String endDate) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void scheduleRecurringReport(String reportType, String recipientId, String frequency) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void backupDatabase() {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void restoreDatabase(String backupId) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void purgeOldData(String olderThan) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void updateSystemSettings(String settingName, String settingValue) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public String getSystemSetting(String settingName) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }

    @Override
    public void logSystemActivity(String activity, String performedBy) {
        // ISP Violation: This method throws an exception, indicating it's not needed by this client
        throw new UnsupportedOperationException("Not needed by this client");
    }
}
