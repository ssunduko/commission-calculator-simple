package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the ReportGenerator interface.
 * It is responsible only for generating reports, following the Single Responsibility Principle.
 */
public class ReportGeneratorImpl implements ReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(ReportGeneratorImpl.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public String generateCommissionReport(User salesRep, List<CommissionCalculation> calculations) {
        StringBuilder report = new StringBuilder();
        
        // Add report header
        report.append("Commission Report\n");
        report.append("===============================\n\n");
        report.append("Sales Rep: ").append(salesRep.getFullName()).append("\n");
        report.append("Generated: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n\n");
        
        // Add summary
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (CommissionCalculation calc : calculations) {
            totalCommission = totalCommission.add(calc.getNetCommission());
        }
        
        report.append("Total Commissions: $").append(totalCommission).append("\n");
        report.append("Number of Deals: ").append(calculations.size()).append("\n\n");
        
        // Add details for each calculation
        report.append("Commission Details:\n");
        report.append("-------------------------------\n");
        
        for (CommissionCalculation calc : calculations) {
            report.append("Deal ID: ").append(calc.getDealId()).append("\n");
            report.append("Calculation Date: ").append(calc.getCalculationDate().format(DATE_FORMATTER)).append("\n");
            report.append("Base Commission: $").append(calc.getBaseCommission()).append("\n");
            report.append("Gross Commission: $").append(calc.getGrossCommission()).append("\n");
            report.append("Net Commission: $").append(calc.getNetCommission()).append("\n");
            report.append("Status: ").append(calc.getStatus()).append("\n");
            report.append("-------------------------------\n");
        }
        
        return report.toString();
    }
    
    @Override
    public String generateCommissionCsvReport(User salesRep, List<CommissionCalculation> calculations) {
        StringBuilder csv = new StringBuilder();
        
        // Add CSV header
        csv.append("Deal ID,Calculation Date,Base Commission,Gross Commission,Net Commission,Status\n");
        
        // Add data rows
        for (CommissionCalculation calc : calculations) {
            csv.append(calc.getDealId()).append(",");
            csv.append(calc.getCalculationDate().format(DATE_FORMATTER)).append(",");
            csv.append(calc.getBaseCommission()).append(",");
            csv.append(calc.getGrossCommission()).append(",");
            csv.append(calc.getNetCommission()).append(",");
            csv.append(calc.getStatus()).append("\n");
        }
        
        return csv.toString();
    }
    
    @Override
    public boolean saveReportToFile(String reportContent, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(reportContent);
            LOGGER.info("Report saved to: " + filePath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving report to file: " + filePath, e);
            return false;
        }
    }
}