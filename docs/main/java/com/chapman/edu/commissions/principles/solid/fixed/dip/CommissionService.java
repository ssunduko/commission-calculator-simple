package com.chapman.edu.commissions.principles.solid.fixed.dip;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * This class demonstrates proper adherence to the Dependency Inversion Principle.
 * It depends on abstractions (interfaces) rather than concrete implementations.
 */
public class CommissionService {
    
    // Dependencies are defined as interfaces, not concrete classes
    private final Database database;
    private final Logger logger;
    private final EmailService emailService;
    
    /**
     * Constructor that takes dependencies as interfaces.
     * This allows for dependency injection and follows DIP.
     */
    public CommissionService(Database database, Logger logger, EmailService emailService) {
        this.database = database;
        this.logger = logger;
        this.emailService = emailService;
    }
    
    /**
     * Calculates commission for a deal.
     */
    public CommissionCalculation calculateCommission(String dealId, String salesRepId) {
        try {
            // Log using the logger interface
            logger.logInfo("Calculating commission for deal: " + dealId);
            
            // Get data using the database interface
            Deal deal = database.getDealById(dealId);
            if (deal == null) {
                logger.logError("Deal not found: " + dealId);
                throw new IllegalArgumentException("Deal not found: " + dealId);
            }
            
            User salesRep = database.getUserById(salesRepId);
            if (salesRep == null) {
                logger.logError("Sales rep not found: " + salesRepId);
                throw new IllegalArgumentException("Sales rep not found: " + salesRepId);
            }
            
            // Calculate commission (simplified)
            BigDecimal commissionAmount = deal.getValue().multiply(BigDecimal.valueOf(0.05));
            
            // Create commission calculation
            CommissionCalculation calculation = new CommissionCalculation();
            calculation.setId("CALC-" + System.currentTimeMillis());
            calculation.setDealId(dealId);
            calculation.setSalesRepId(salesRepId);
            calculation.setBaseCommission(commissionAmount);
            calculation.setCalculationDate(LocalDate.now());
            
            // Save using the database interface
            database.saveCommissionCalculation(calculation);
            
            // Send email using the email service interface
            String emailSubject = "Commission Calculated for Deal " + dealId;
            String emailBody = "Dear " + salesRep.getFullName() + ",\n\n" +
                    "Your commission for deal " + dealId + " has been calculated.\n" +
                    "Amount: $" + commissionAmount + "\n\n" +
                    "Thank you,\nCommission Department";
            emailService.sendEmail(salesRep.getEmail(), emailSubject, emailBody);
            
            logger.logInfo("Commission calculation completed for deal: " + dealId);
            return calculation;
            
        } catch (Exception e) {
            logger.logError("Error calculating commission: " + e.getMessage(), e);
            throw new RuntimeException("Error calculating commission", e);
        }
    }
    
    /**
     * Gets calculations for a sales rep.
     */
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        try {
            logger.logInfo("Getting calculations for sales rep: " + salesRepId);
            return database.getCommissionCalculationsBySalesRep(salesRepId);
        } catch (Exception e) {
            logger.logError("Error getting calculations: " + e.getMessage(), e);
            throw new RuntimeException("Error getting calculations", e);
        }
    }
}