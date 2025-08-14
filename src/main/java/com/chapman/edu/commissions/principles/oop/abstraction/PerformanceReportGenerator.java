package com.chapman.edu.commissions.principles.oop.abstraction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class extends the ReportGenerator abstract class for performance reports.
 * It demonstrates abstraction by:
 * 1. Implementing the abstract methods from the parent class
 * 2. Providing specific implementation for generating performance reports
 */
public class PerformanceReportGenerator extends ReportGenerator {
    
    private String department;
    private Map<String, PerformanceLevel> employeePerformance;
    
    /**
     * Constructor with essential fields
     */
    public PerformanceReportGenerator(String reportTitle, String generatedBy, String department) {
        super(reportTitle, generatedBy);
        this.department = department;
        this.employeePerformance = new HashMap<>();
    }
    
    /**
     * Set the performance level for an employee
     * 
     * @param employeeId The ID of the employee
     * @param level The performance level
     */
    public void setEmployeePerformance(String employeeId, PerformanceLevel level) {
        employeePerformance.put(employeeId, level);
    }
    
    /**
     * Implementation of the abstract method to fetch data for the report.
     * In a real application, this would fetch data from a database or service.
     * For this example, we generate sample data.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @return A list of performance data items for the report
     */
    @Override
    protected List<String> fetchReportData(LocalDate startDate, LocalDate endDate) {
        // In a real application, this would fetch data from a database or service
        // For this example, we'll generate sample data
        List<String> performanceData = new ArrayList<>();
        
        // Generate sample performance data
        Random random = new Random();
        String[] employees = {"EMP001,John Smith", "EMP002,Mary Johnson", 
                             "EMP003,Robert Brown", "EMP004,Lisa Davis",
                             "EMP005,Michael Wilson", "EMP006,Sarah Martinez"};
        
        for (String employee : employees) {
            String[] parts = employee.split(",");
            String employeeId = parts[0];
            String employeeName = parts[1];
            
            // Set random performance level if not already set
            if (!employeePerformance.containsKey(employeeId)) {
                PerformanceLevel[] levels = PerformanceLevel.values();
                employeePerformance.put(employeeId, levels[random.nextInt(levels.length)]);
            }
            
            PerformanceLevel level = employeePerformance.get(employeeId);
            
            // Generate metrics based on performance level
            int tasksCompleted = 20 + random.nextInt(30) + (level.ordinal() * 10); // Higher for better performance
            int qualityScore = 70 + random.nextInt(20) + (level.ordinal() * 5); // Higher for better performance
            int customerSatisfaction = 60 + random.nextInt(30) + (level.ordinal() * 5); // Higher for better performance
            
            String performanceRecord = String.format("%s,%s,%s,%d,%d,%d",
                    employeeId, employeeName, level.name(), 
                    tasksCompleted, qualityScore, customerSatisfaction);
            performanceData.add(performanceRecord);
        }
        
        return performanceData;
    }
    
    /**
     * Implementation of the abstract method to process the report data.
     * This formats the performance data into a readable table.
     * 
     * @param reportData The data to process
     * @return The processed data as a formatted table
     */
    @Override
    protected String processReportData(List<String> reportData) {
        StringBuilder processedData = new StringBuilder();
        
        // Add department information
        processedData.append("Department: ").append(department).append("\n\n");
        
        // Add table header
        processedData.append(String.format("%-10s %-20s %-12s %15s %15s %25s\n",
                "Emp ID", "Employee Name", "Performance", "Tasks Completed", "Quality Score", "Customer Satisfaction"));
        processedData.append("-".repeat(100)).append("\n");
        
        // Add table rows
        int totalTasks = 0;
        int totalQuality = 0;
        int totalSatisfaction = 0;
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            String employeeId = parts[0];
            String employeeName = parts[1];
            String performanceLevel = parts[2];
            int tasksCompleted = Integer.parseInt(parts[3]);
            int qualityScore = Integer.parseInt(parts[4]);
            int customerSatisfaction = Integer.parseInt(parts[5]);
            
            processedData.append(String.format("%-10s %-20s %-12s %15d %15d %25d\n",
                    employeeId, employeeName, performanceLevel, 
                    tasksCompleted, qualityScore, customerSatisfaction));
            
            totalTasks += tasksCompleted;
            totalQuality += qualityScore;
            totalSatisfaction += customerSatisfaction;
        }
        
        // Add table footer
        processedData.append("-".repeat(100)).append("\n");
        processedData.append(String.format("%-44s %15d %15d %25d\n",
                "TOTAL", totalTasks, totalQuality, totalSatisfaction));
        
        return processedData.toString();
    }
    
    /**
     * Implementation of the abstract method to generate a summary of the report.
     * This provides a summary of the performance data by performance level.
     * 
     * @param reportData The data to summarize
     * @return The report summary as a string
     */
    @Override
    protected String generateReportSummary(List<String> reportData) {
        StringBuilder summary = new StringBuilder();
        summary.append("\nPERFORMANCE REPORT SUMMARY\n");
        summary.append("-".repeat(30)).append("\n");
        
        // Calculate summary statistics
        int totalEmployees = reportData.size();
        Map<String, Integer> levelCounts = new HashMap<>();
        Map<String, List<Integer>> levelMetrics = new HashMap<>();
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            String performanceLevel = parts[2];
            int tasksCompleted = Integer.parseInt(parts[3]);
            int qualityScore = Integer.parseInt(parts[4]);
            int customerSatisfaction = Integer.parseInt(parts[5]);
            
            // Update level counts
            levelCounts.put(performanceLevel, 
                    levelCounts.getOrDefault(performanceLevel, 0) + 1);
            
            // Update level metrics
            if (!levelMetrics.containsKey(performanceLevel)) {
                levelMetrics.put(performanceLevel, new ArrayList<>());
                levelMetrics.get(performanceLevel).add(0); // Tasks
                levelMetrics.get(performanceLevel).add(0); // Quality
                levelMetrics.get(performanceLevel).add(0); // Satisfaction
            }
            
            List<Integer> metrics = levelMetrics.get(performanceLevel);
            metrics.set(0, metrics.get(0) + tasksCompleted);
            metrics.set(1, metrics.get(1) + qualityScore);
            metrics.set(2, metrics.get(2) + customerSatisfaction);
        }
        
        // Add summary statistics
        summary.append(String.format("Total Employees: %d\n\n", totalEmployees));
        
        // Add performance level breakdown
        summary.append("PERFORMANCE LEVEL BREAKDOWN\n");
        summary.append("-".repeat(30)).append("\n");
        
        for (PerformanceLevel level : PerformanceLevel.values()) {
            String levelName = level.name();
            int count = levelCounts.getOrDefault(levelName, 0);
            double percentage = ((double) count / totalEmployees) * 100;
            
            summary.append(String.format("%-12s: %2d employees (%5.1f%%)\n", 
                    levelName, count, percentage));
        }
        
        // Add metrics by performance level
        summary.append("\nAVERAGE METRICS BY PERFORMANCE LEVEL\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("%-12s %15s %15s %25s\n", 
                "Level", "Tasks Completed", "Quality Score", "Customer Satisfaction"));
        summary.append("-".repeat(70)).append("\n");
        
        for (PerformanceLevel level : PerformanceLevel.values()) {
            String levelName = level.name();
            if (levelMetrics.containsKey(levelName)) {
                List<Integer> metrics = levelMetrics.get(levelName);
                int count = levelCounts.get(levelName);
                double avgTasks = (double) metrics.get(0) / count;
                double avgQuality = (double) metrics.get(1) / count;
                double avgSatisfaction = (double) metrics.get(2) / count;
                
                summary.append(String.format("%-12s %15.1f %15.1f %25.1f\n", 
                        levelName, avgTasks, avgQuality, avgSatisfaction));
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Override the generateReportHeader method to provide a custom header for performance reports.
     * 
     * @return The report header as a string
     */
    @Override
    protected String generateReportHeader() {
        return "=== PERFORMANCE REPORT: " + reportTitle + " ===";
    }
    
    /**
     * Get the department.
     * 
     * @return The department
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Set the department.
     * 
     * @param department The new department
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Get the performance level for an employee.
     * 
     * @param employeeId The ID of the employee
     * @return The performance level, or null if not set
     */
    public PerformanceLevel getEmployeePerformance(String employeeId) {
        return employeePerformance.get(employeeId);
    }
    
    /**
     * Enum representing performance levels
     */
    public enum PerformanceLevel {
        NEEDS_IMPROVEMENT,
        MEETS_EXPECTATIONS,
        EXCEEDS_EXPECTATIONS,
        OUTSTANDING
    }
}