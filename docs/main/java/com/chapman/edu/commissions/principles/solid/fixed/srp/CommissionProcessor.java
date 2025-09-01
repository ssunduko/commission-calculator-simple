package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

import java.util.List;
import java.util.logging.Logger;

/**
 * This class orchestrates the commission calculation process.
 * It follows the Single Responsibility Principle by delegating specific responsibilities to specialized classes.
 */
public class CommissionProcessor {
    private static final Logger LOGGER = Logger.getLogger(CommissionProcessor.class.getName());
    
    private final CommissionCalculator calculator;
    private final DatabaseService databaseService;
    private final EmailService emailService;
    private final ReportGenerator reportGenerator;
    
    /**
     * Constructor that takes all required dependencies.
     */
    public CommissionProcessor(
            CommissionCalculator calculator,
            DatabaseService databaseService,
            EmailService emailService,
            ReportGenerator reportGenerator) {
        this.calculator = calculator;
        this.databaseService = databaseService;
        this.emailService = emailService;
        this.reportGenerator = reportGenerator;
    }
    
    /**
     * Processes a commission calculation for a deal.
     * This method orchestrates the entire process but delegates specific responsibilities to specialized classes.
     */
    public CommissionCalculation processCommission(String dealId, String planId) {
        LOGGER.info("Processing commission for deal: " + dealId + " with plan: " + planId);
        
        // 1. Calculate commission (delegated to CommissionCalculator)
        CommissionCalculation calculation = calculator.calculateCommission(dealId, planId);
        
        // 2. Save to database (delegated to DatabaseService)
        databaseService.saveCalculationToDatabase(calculation);
        
        // 3. Send email notification (delegated to EmailService)
        User salesRep = databaseService.getUserFromDatabase(calculation.getSalesRepId());
        emailService.sendCommissionNotification(salesRep, calculation);
        
        // 4. Generate and save report (delegated to ReportGenerator)
        List<CommissionCalculation> calculations = databaseService.getCalculationsBySalesRep(salesRep.getId());
        String reportContent = reportGenerator.generateCommissionReport(salesRep, calculations);
        String reportPath = "reports/commission_" + salesRep.getId() + "_" + System.currentTimeMillis() + ".txt";
        reportGenerator.saveReportToFile(reportContent, reportPath);
        
        LOGGER.info("Commission processing completed for deal: " + dealId);
        return calculation;
    }
    
    /**
     * Retrieves commission calculations for a sales rep.
     */
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        return databaseService.getCalculationsBySalesRep(salesRepId);
    }
}