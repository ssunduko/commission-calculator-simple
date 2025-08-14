package com.chapman.edu.commissions.principles.solid.fixed.isp;

/**
 * This interface defines reporting operations.
 * It follows the Interface Segregation Principle by focusing only on reporting,
 * allowing clients to depend only on the methods they need.
 */
public interface ReportingService {
    
    /**
     * Generates a commission report for a specific sales rep.
     * 
     * @param salesRepId The ID of the sales rep
     * @param startDate The start date for the report period (format: YYYY-MM-DD)
     * @param endDate The end date for the report period (format: YYYY-MM-DD)
     * @return The report data as a byte array
     */
    byte[] generateCommissionReport(String salesRepId, String startDate, String endDate);
    
    /**
     * Generates a team report for a specific manager.
     * 
     * @param managerId The ID of the manager
     * @param startDate The start date for the report period (format: YYYY-MM-DD)
     * @param endDate The end date for the report period (format: YYYY-MM-DD)
     * @return The report data as a byte array
     */
    byte[] generateTeamReport(String managerId, String startDate, String endDate);
    
    /**
     * Generates a system-wide report.
     * 
     * @param startDate The start date for the report period (format: YYYY-MM-DD)
     * @param endDate The end date for the report period (format: YYYY-MM-DD)
     * @return The report data as a byte array
     */
    byte[] generateSystemWideReport(String startDate, String endDate);
    
    /**
     * Emails a report to a recipient.
     * 
     * @param reportType The type of report to generate
     * @param recipientId The ID of the recipient
     * @param startDate The start date for the report period (format: YYYY-MM-DD)
     * @param endDate The end date for the report period (format: YYYY-MM-DD)
     */
    void emailReport(String reportType, String recipientId, String startDate, String endDate);
    
    /**
     * Schedules a recurring report.
     * 
     * @param reportType The type of report to generate
     * @param recipientId The ID of the recipient
     * @param frequency The frequency of the report (e.g., "DAILY", "WEEKLY", "MONTHLY")
     */
    void scheduleRecurringReport(String reportType, String recipientId, String frequency);
}