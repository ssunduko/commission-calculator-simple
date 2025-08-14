package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.User;

import java.util.List;

/**
 * This interface defines report generation operations.
 * It follows the Single Responsibility Principle by focusing only on report generation.
 */
public interface ReportGenerator {
    
    /**
     * Generates a commission report for a specific sales rep.
     * 
     * @param salesRep The sales rep for whom to generate the report
     * @param calculations The commission calculations to include in the report
     * @return The report content as a string
     */
    String generateCommissionReport(User salesRep, List<CommissionCalculation> calculations);
    
    /**
     * Generates a CSV report for a specific sales rep.
     * 
     * @param salesRep The sales rep for whom to generate the report
     * @param calculations The commission calculations to include in the report
     * @return The CSV report content as a string
     */
    String generateCommissionCsvReport(User salesRep, List<CommissionCalculation> calculations);
    
    /**
     * Saves a report to a file.
     * 
     * @param reportContent The content of the report
     * @param filePath The path where to save the report
     * @return True if the report was saved successfully, false otherwise
     */
    boolean saveReportToFile(String reportContent, String filePath);
}