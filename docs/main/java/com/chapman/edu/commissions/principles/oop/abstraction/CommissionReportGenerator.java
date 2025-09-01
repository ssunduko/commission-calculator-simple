package com.chapman.edu.commissions.principles.oop.abstraction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class extends the ReportGenerator abstract class for commission reports.
 * It demonstrates abstraction by:
 * 1. Implementing the abstract methods from the parent class
 * 2. Providing specific implementation for generating commission reports
 * @author Sergey Sundukovskiy Ph.D.
 * @version 1.0
 */
public class CommissionReportGenerator extends ReportGenerator {
    
    private String salesTeam;
    
    /**
     * Constructor with essential fields
     */
    public CommissionReportGenerator(String reportTitle, String generatedBy, String salesTeam) {
        super(reportTitle, generatedBy);
        this.salesTeam = salesTeam;
    }
    
    /**
     * Implementation of the abstract method to fetch data for the report.
     * In a real application, this would fetch data from a database or service.
     * For this example, we generate sample data.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @return A list of commission data items for the report
     */
    @Override
    protected List<String> fetchReportData(LocalDate startDate, LocalDate endDate) {
        // In a real application, this would fetch data from a database or service
        // For this example, we'll generate sample data
        List<String> commissionData = new ArrayList<>();
        
        // Generate sample commission data
        Random random = new Random();
        String[] salesReps = {"John Smith", "Mary Johnson", "Robert Brown", "Lisa Davis"};
        
        for (String salesRep : salesReps) {
            int dealCount = 3 + random.nextInt(5); // 3-7 deals per sales rep
            
            for (int i = 1; i <= dealCount; i++) {
                double dealValue = 1000 + random.nextInt(9000); // $1000-$10000
                double commissionAmount = dealValue * (0.03 + random.nextDouble() * 0.07); // 3-10% commission
                
                String dealData = String.format("%s,Deal-%d,%s,%.2f,%.2f",
                        salesRep, i, getRandomDateBetween(startDate, endDate), dealValue, commissionAmount);
                commissionData.add(dealData);
            }
        }
        
        return commissionData;
    }
    
    /**
     * Implementation of the abstract method to process the report data.
     * This formats the commission data into a readable table.
     * 
     * @param reportData The data to process
     * @return The processed data as a formatted table
     */
    @Override
    protected String processReportData(List<String> reportData) {
        StringBuilder processedData = new StringBuilder();
        
        // Add sales team information
        processedData.append("Sales Team: ").append(salesTeam).append("\n\n");
        
        // Add table header
        processedData.append(String.format("%-20s %-15s %-12s %15s %15s\n",
                "Sales Representative", "Deal ID", "Deal Date", "Deal Value ($)", "Commission ($)"));
        processedData.append("-".repeat(80)).append("\n");
        
        // Add table rows
        double totalDealValue = 0;
        double totalCommission = 0;
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            String salesRep = parts[0];
            String dealId = parts[1];
            String dealDate = parts[2];
            double dealValue = Double.parseDouble(parts[3]);
            double commission = Double.parseDouble(parts[4]);
            
            processedData.append(String.format("%-20s %-15s %-12s %15.2f %15.2f\n",
                    salesRep, dealId, dealDate, dealValue, commission));
            
            totalDealValue += dealValue;
            totalCommission += commission;
        }
        
        // Add table footer
        processedData.append("-".repeat(80)).append("\n");
        processedData.append(String.format("%-49s %15.2f %15.2f\n",
                "TOTAL", totalDealValue, totalCommission));
        
        return processedData.toString();
    }
    
    /**
     * Implementation of the abstract method to generate a summary of the report.
     * This provides a summary of the commission data.
     * 
     * @param reportData The data to summarize
     * @return The report summary as a string
     */
    @Override
    protected String generateReportSummary(List<String> reportData) {
        StringBuilder summary = new StringBuilder();
        summary.append("\nCOMMISSION REPORT SUMMARY\n");
        summary.append("-".repeat(30)).append("\n");
        
        // Calculate summary statistics
        int totalDeals = reportData.size();
        double totalDealValue = 0;
        double totalCommission = 0;
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            double dealValue = Double.parseDouble(parts[3]);
            double commission = Double.parseDouble(parts[4]);
            
            totalDealValue += dealValue;
            totalCommission += commission;
        }
        
        double averageDealValue = totalDealValue / totalDeals;
        double averageCommission = totalCommission / totalDeals;
        double commissionRate = (totalCommission / totalDealValue) * 100;
        
        // Add summary statistics
        summary.append(String.format("Total Deals: %d\n", totalDeals));
        summary.append(String.format("Total Deal Value: $%.2f\n", totalDealValue));
        summary.append(String.format("Total Commission: $%.2f\n", totalCommission));
        summary.append(String.format("Average Deal Value: $%.2f\n", averageDealValue));
        summary.append(String.format("Average Commission: $%.2f\n", averageCommission));
        summary.append(String.format("Overall Commission Rate: %.2f%%\n", commissionRate));
        
        return summary.toString();
    }
    
    /**
     * Override the generateReportHeader method to provide a custom header for commission reports.
     * 
     * @return The report header as a string
     */
    @Override
    protected String generateReportHeader() {
        return "=== COMMISSION REPORT: " + reportTitle + " ===";
    }
    
    /**
     * Get a random date between the start and end dates.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return A random date between the start and end dates
     */
    private String getRandomDateBetween(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomDay = startEpochDay + (long) (Math.random() * (endEpochDay - startEpochDay));
        
        return formatDate(LocalDate.ofEpochDay(randomDay));
    }
    
    /**
     * Get the sales team.
     * 
     * @return The sales team
     */
    public String getSalesTeam() {
        return salesTeam;
    }
    
    /**
     * Set the sales team.
     * 
     * @param salesTeam The new sales team
     */
    public void setSalesTeam(String salesTeam) {
        this.salesTeam = salesTeam;
    }
}