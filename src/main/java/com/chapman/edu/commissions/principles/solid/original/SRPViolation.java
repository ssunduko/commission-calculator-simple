package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class violates the Single Responsibility Principle (SRP).
 * 
 * SRP states that a class should have only one reason to change, meaning it should have
 * only one responsibility. This class handles multiple responsibilities:
 * 1. Commission calculation
 * 2. Data persistence (database operations)
 * 3. Email notification
 * 4. Logging
 * 5. Report generation
 */
public class SRPViolation {

    private static final Logger LOGGER = Logger.getLogger(SRPViolation.class.getName());
    private Map<String, CommissionCalculation> calculationsDatabase = new HashMap<>();
    private Map<String, Deal> dealsDatabase = new HashMap<>();
    private Map<String, CommissionPlan> plansDatabase = new HashMap<>();
    private Map<String, User> usersDatabase = new HashMap<>();

    /**
     * Calculates commission, saves to database, sends email notification, and generates report.
     * This method is doing too many things and violates SRP.
     */
    public CommissionCalculation processCommission(String dealId, String planId) {
        LOGGER.info("Processing commission for deal: " + dealId + " with plan: " + planId);

        // 1. Retrieve data from database
        Deal deal = getDealFromDatabase(dealId);
        if (deal == null) {
            LOGGER.severe("Deal not found: " + dealId);
            throw new IllegalArgumentException("Deal not found: " + dealId);
        }

        CommissionPlan plan = getPlanFromDatabase(planId);
        if (plan == null) {
            LOGGER.warning("Plan not found: " + planId + ". Using default plan.");
            plan = getDefaultPlan();
        }

        User salesRep = getUserFromDatabase(deal.getSalesRepId());
        if (salesRep == null) {
            LOGGER.severe("Sales rep not found: " + deal.getSalesRepId());
            throw new IllegalArgumentException("Sales rep not found: " + deal.getSalesRepId());
        }

        // 2. Calculate commission
        CommissionCalculation calculation = new CommissionCalculation();
        calculation.setId(generateId());
        calculation.setDealId(dealId);
        calculation.setSalesRepId(deal.getSalesRepId());
        calculation.setPlanId(plan.getId());
        calculation.setCalculationDate(LocalDate.now());

        // Calculate base commission
        BigDecimal baseCommission = calculateBaseCommission(deal, plan);
        calculation.setBaseCommission(baseCommission);

        // Apply bonuses and accelerators
        applyBonuses(calculation, deal, plan);
        applyAccelerators(calculation, deal, plan);

        // Calculate total commission
        BigDecimal totalCommission = calculation.calculateTotalCommission();
        calculation.setGrossCommission(totalCommission);
        calculation.setNetCommission(totalCommission); // Simplified, no deductions

        // 3. Save to database
        saveCalculationToDatabase(calculation);

        // 4. Send email notification
        sendEmailNotification(salesRep, calculation);

        // 5. Generate commission report
        generateCommissionReport(calculation, salesRep, deal);

        LOGGER.info("Commission processing completed for deal: " + dealId);
        return calculation;
    }

    // Database operations
    private Deal getDealFromDatabase(String dealId) {
        // Simulating database access
        return dealsDatabase.get(dealId);
    }

    private CommissionPlan getPlanFromDatabase(String planId) {
        // Simulating database access
        return plansDatabase.get(planId);
    }

    private CommissionPlan getDefaultPlan() {
        // Simulating default plan retrieval
        CommissionPlan defaultPlan = new CommissionPlan();
        defaultPlan.setId("DEFAULT");
        defaultPlan.setName("Default Commission Plan");
        return defaultPlan;
    }

    private User getUserFromDatabase(String userId) {
        // Simulating database access
        return usersDatabase.get(userId);
    }

    private void saveCalculationToDatabase(CommissionCalculation calculation) {
        // Simulating database save operation
        calculationsDatabase.put(calculation.getId(), calculation);
        LOGGER.info("Saved calculation to database: " + calculation.getId());
    }

    // Commission calculation logic
    private BigDecimal calculateBaseCommission(Deal deal, CommissionPlan plan) {
        // Simplified commission calculation
        BigDecimal dealAmount = deal.getValue();
        BigDecimal commissionRate = BigDecimal.valueOf(0.05); // Default 5%

        // In a real implementation, we would get the rate from the plan's tiers or rules
        // For simplicity, we're using a hardcoded rate

        return dealAmount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
    }

    private void applyBonuses(CommissionCalculation calculation, Deal deal, CommissionPlan plan) {
        // Simplified bonus application
        // In a real implementation, this would apply various bonus rules
        LOGGER.info("Applying bonuses for calculation: " + calculation.getId());
    }

    private void applyAccelerators(CommissionCalculation calculation, Deal deal, CommissionPlan plan) {
        // Simplified accelerator application
        // In a real implementation, this would apply various accelerator rules
        LOGGER.info("Applying accelerators for calculation: " + calculation.getId());
    }

    // Email notification
    private void sendEmailNotification(User salesRep, CommissionCalculation calculation) {
        // Simulating email sending
        String emailContent = "Dear " + salesRep.getFullName() + ",\n\n" +
                "Your commission for deal " + calculation.getDealId() + " has been calculated.\n" +
                "Amount: $" + calculation.getNetCommission() + "\n\n" +
                "Thank you,\nCommission Department";

        LOGGER.info("Sending email to: " + salesRep.getEmail());
        LOGGER.info("Email content: " + emailContent);

        // In a real implementation, this would use an email service
    }

    // Report generation
    private void generateCommissionReport(CommissionCalculation calculation, User salesRep, Deal deal) {
        // Simulating report generation
        String reportContent = "Commission Report\n" +
                "=================\n" +
                "Date: " + LocalDate.now() + "\n" +
                "Sales Rep: " + salesRep.getFullName() + "\n" +
                "Deal ID: " + deal.getId() + "\n" +
                "Deal Amount: $" + deal.getValue() + "\n" +
                "Commission: $" + calculation.getNetCommission() + "\n";

        String fileName = "commission_report_" + calculation.getId() + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(reportContent);
            LOGGER.info("Report generated: " + fileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error generating report", e);
        }
    }

    private String generateId() {
        return "CALC-" + System.currentTimeMillis();
    }

    // Additional database operations that don't belong in this class
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        List<CommissionCalculation> result = new ArrayList<>();
        for (CommissionCalculation calc : calculationsDatabase.values()) {
            if (calc.getSalesRepId().equals(salesRepId)) {
                result.add(calc);
            }
        }
        return result;
    }

    public void updateUserProfile(User user) {
        // This method doesn't belong in a commission service
        usersDatabase.put(user.getId(), user);
        LOGGER.info("Updated user profile: " + user.getId());
    }

    public void generateMonthlyReport() {
        // This method doesn't belong in a commission service
        LOGGER.info("Generating monthly commission report");
        // Report generation logic
    }
}
