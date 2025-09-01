package com.chapman.edu.commissions.principles.oop.abstraction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class extends the ReportGenerator abstract class for sales reports.
 * It demonstrates abstraction by:
 * 1. Implementing the abstract methods from the parent class
 * 2. Providing specific implementation for generating sales reports
 * @author Sergey Sundukovskiy Ph.D.
 * @version 1.0
 */
public class SalesReportGenerator extends ReportGenerator {
    
    private String region;
    private List<String> productCategories;
    
    /**
     * Constructor with essential fields
     */
    public SalesReportGenerator(String reportTitle, String generatedBy, String region) {
        super(reportTitle, generatedBy);
        this.region = region;
        this.productCategories = new ArrayList<>();
        
        // Add some default product categories
        this.productCategories.add("Hardware");
        this.productCategories.add("Software");
        this.productCategories.add("Services");
        this.productCategories.add("Support");
    }
    
    /**
     * Implementation of the abstract method to fetch data for the report.
     * In a real application, this would fetch data from a database or service.
     * For this example, we generate sample data.
     * 
     * @param startDate The start date for the report period
     * @param endDate The end date for the report period
     * @return A list of sales data items for the report
     */
    @Override
    protected List<String> fetchReportData(LocalDate startDate, LocalDate endDate) {
        // In a real application, this would fetch data from a database or service
        // For this example, we'll generate sample data
        List<String> salesData = new ArrayList<>();
        
        // Generate sample sales data
        Random random = new Random();
        String[] customers = {"Acme Corp", "Globex Inc", "Initech", "Umbrella Corp", "Wayne Enterprises"};
        
        for (String customer : customers) {
            int salesCount = 2 + random.nextInt(4); // 2-5 sales per customer
            
            for (int i = 1; i <= salesCount; i++) {
                String productCategory = productCategories.get(random.nextInt(productCategories.size()));
                double saleAmount = 2000 + random.nextInt(18000); // $2000-$20000
                int quantity = 1 + random.nextInt(10); // 1-10 items
                
                String saleData = String.format("%s,%s,%s,%d,%.2f",
                        customer, productCategory, getRandomDateBetween(startDate, endDate), 
                        quantity, saleAmount);
                salesData.add(saleData);
            }
        }
        
        return salesData;
    }
    
    /**
     * Implementation of the abstract method to process the report data.
     * This formats the sales data into a readable table.
     * 
     * @param reportData The data to process
     * @return The processed data as a formatted table
     */
    @Override
    protected String processReportData(List<String> reportData) {
        StringBuilder processedData = new StringBuilder();
        
        // Add region information
        processedData.append("Region: ").append(region).append("\n\n");
        
        // Add table header
        processedData.append(String.format("%-20s %-15s %-12s %10s %15s\n",
                "Customer", "Product Category", "Sale Date", "Quantity", "Sale Amount ($)"));
        processedData.append("-".repeat(80)).append("\n");
        
        // Add table rows
        double totalSaleAmount = 0;
        int totalQuantity = 0;
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            String customer = parts[0];
            String productCategory = parts[1];
            String saleDate = parts[2];
            int quantity = Integer.parseInt(parts[3]);
            double saleAmount = Double.parseDouble(parts[4]);
            
            processedData.append(String.format("%-20s %-15s %-12s %10d %15.2f\n",
                    customer, productCategory, saleDate, quantity, saleAmount));
            
            totalSaleAmount += saleAmount;
            totalQuantity += quantity;
        }
        
        // Add table footer
        processedData.append("-".repeat(80)).append("\n");
        processedData.append(String.format("%-49s %10d %15.2f\n",
                "TOTAL", totalQuantity, totalSaleAmount));
        
        return processedData.toString();
    }
    
    /**
     * Implementation of the abstract method to generate a summary of the report.
     * This provides a summary of the sales data by product category.
     * 
     * @param reportData The data to summarize
     * @return The report summary as a string
     */
    @Override
    protected String generateReportSummary(List<String> reportData) {
        StringBuilder summary = new StringBuilder();
        summary.append("\nSALES REPORT SUMMARY\n");
        summary.append("-".repeat(25)).append("\n");
        
        // Calculate summary statistics
        int totalSales = reportData.size();
        double totalSaleAmount = 0;
        Map<String, Double> categoryTotals = new HashMap<>();
        
        for (String dataItem : reportData) {
            String[] parts = dataItem.split(",");
            String productCategory = parts[1];
            double saleAmount = Double.parseDouble(parts[4]);
            
            totalSaleAmount += saleAmount;
            
            // Update category totals
            categoryTotals.put(productCategory, 
                    categoryTotals.getOrDefault(productCategory, 0.0) + saleAmount);
        }
        
        double averageSaleAmount = totalSaleAmount / totalSales;
        
        // Add summary statistics
        summary.append(String.format("Total Sales: %d\n", totalSales));
        summary.append(String.format("Total Sale Amount: $%.2f\n", totalSaleAmount));
        summary.append(String.format("Average Sale Amount: $%.2f\n\n", averageSaleAmount));
        
        // Add category breakdown
        summary.append("SALES BY PRODUCT CATEGORY\n");
        summary.append("-".repeat(25)).append("\n");
        
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();
            double percentage = (amount / totalSaleAmount) * 100;
            
            summary.append(String.format("%-15s $%10.2f (%5.1f%%)\n", 
                    category, amount, percentage));
        }
        
        return summary.toString();
    }
    
    /**
     * Override the generateReportHeader method to provide a custom header for sales reports.
     * 
     * @return The report header as a string
     */
    @Override
    protected String generateReportHeader() {
        return "=== SALES REPORT: " + reportTitle + " ===";
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
     * Get the region.
     * 
     * @return The region
     */
    public String getRegion() {
        return region;
    }
    
    /**
     * Set the region.
     * 
     * @param region The new region
     */
    public void setRegion(String region) {
        this.region = region;
    }
    
    /**
     * Get the product categories.
     * 
     * @return The product categories
     */
    public List<String> getProductCategories() {
        return new ArrayList<>(productCategories);
    }
    
    /**
     * Add a product category.
     * 
     * @param category The product category to add
     */
    public void addProductCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.productCategories.add(category);
        }
    }
}