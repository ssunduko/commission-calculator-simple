package com.chapman.edu.commissions.principles.dry.original;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A report generator class that violates the DRY principle
 * by duplicating calculations and formatting logic across methods.
 */
public class ReportGenerator {
    
    /**
     * Generates a daily sales report
     * 
     * @param date the date for the report
     * @param salesData list of sales amounts
     * @return a formatted sales report
     */
    public String generateDailySalesReport(LocalDate date, List<Double> salesData) {
        // Format date - DUPLICATED in other report methods
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Calculate total sales - DUPLICATED in monthly and yearly reports
        double totalSales = 0;
        for (Double sale : salesData) {
            totalSales += sale;
        }
        
        // Calculate average sale - DUPLICATED in monthly and yearly reports
        double averageSale = salesData.isEmpty() ? 0 : totalSales / salesData.size();
        
        // Format currency - DUPLICATED in other methods
        String formattedTotal = String.format("$%.2f", totalSales);
        String formattedAverage = String.format("$%.2f", averageSale);
        
        // Build report - DUPLICATED structure in other report methods
        StringBuilder report = new StringBuilder();
        report.append("DAILY SALES REPORT\n");
        report.append("=================\n");
        report.append("Date: ").append(formattedDate).append("\n");
        report.append("Total Sales: ").append(formattedTotal).append("\n");
        report.append("Number of Sales: ").append(salesData.size()).append("\n");
        report.append("Average Sale: ").append(formattedAverage).append("\n");
        
        return report.toString();
    }
    
    /**
     * Generates a monthly sales report
     * 
     * @param month the month (1-12)
     * @param year the year
     * @param salesData list of sales amounts
     * @return a formatted sales report
     */
    public String generateMonthlySalesReport(int month, int year, List<Double> salesData) {
        // Format date - DUPLICATED from daily report
        LocalDate date = LocalDate.of(year, month, 1);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        
        // Calculate total sales - DUPLICATED from daily report
        double totalSales = 0;
        for (Double sale : salesData) {
            totalSales += sale;
        }
        
        // Calculate average sale - DUPLICATED from daily report
        double averageSale = salesData.isEmpty() ? 0 : totalSales / salesData.size();
        
        // Format currency - DUPLICATED from daily report
        String formattedTotal = String.format("$%.2f", totalSales);
        String formattedAverage = String.format("$%.2f", averageSale);
        
        // Build report - DUPLICATED structure from daily report
        StringBuilder report = new StringBuilder();
        report.append("MONTHLY SALES REPORT\n");
        report.append("===================\n");
        report.append("Month: ").append(formattedDate).append("\n");
        report.append("Total Sales: ").append(formattedTotal).append("\n");
        report.append("Number of Sales: ").append(salesData.size()).append("\n");
        report.append("Average Sale: ").append(formattedAverage).append("\n");
        
        return report.toString();
    }
    
    /**
     * Generates a yearly sales report
     * 
     * @param year the year
     * @param salesData list of sales amounts
     * @return a formatted sales report
     */
    public String generateYearlySalesReport(int year, List<Double> salesData) {
        // Format date - DUPLICATED from other reports
        String formattedDate = String.valueOf(year);
        
        // Calculate total sales - DUPLICATED from other reports
        double totalSales = 0;
        for (Double sale : salesData) {
            totalSales += sale;
        }
        
        // Calculate average sale - DUPLICATED from other reports
        double averageSale = salesData.isEmpty() ? 0 : totalSales / salesData.size();
        
        // Format currency - DUPLICATED from other reports
        String formattedTotal = String.format("$%.2f", totalSales);
        String formattedAverage = String.format("$%.2f", averageSale);
        
        // Build report - DUPLICATED structure from other reports
        StringBuilder report = new StringBuilder();
        report.append("YEARLY SALES REPORT\n");
        report.append("==================\n");
        report.append("Year: ").append(formattedDate).append("\n");
        report.append("Total Sales: ").append(formattedTotal).append("\n");
        report.append("Number of Sales: ").append(salesData.size()).append("\n");
        report.append("Average Sale: ").append(formattedAverage).append("\n");
        
        return report.toString();
    }
    
    /**
     * Calculates tax for a given amount
     * 
     * @param amount the amount to calculate tax for
     * @param taxRate the tax rate (e.g., 0.1 for 10%)
     * @return the tax amount
     */
    public double calculateTax(double amount, double taxRate) {
        return amount * taxRate;
    }
    
    /**
     * Generates a receipt with tax calculation
     * 
     * @param customerName the customer's name
     * @param amount the purchase amount
     * @return a formatted receipt
     */
    public String generateReceipt(String customerName, double amount) {
        // Tax calculation - DUPLICATED from calculateTax method
        double taxRate = 0.1; // 10% tax
        double taxAmount = amount * taxRate;
        double total = amount + taxAmount;
        
        // Format currency - DUPLICATED from report methods
        String formattedAmount = String.format("$%.2f", amount);
        String formattedTax = String.format("$%.2f", taxAmount);
        String formattedTotal = String.format("$%.2f", total);
        
        // Build receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("RECEIPT\n");
        receipt.append("=======\n");
        receipt.append("Customer: ").append(customerName).append("\n");
        receipt.append("Amount: ").append(formattedAmount).append("\n");
        receipt.append("Tax (10%): ").append(formattedTax).append("\n");
        receipt.append("Total: ").append(formattedTotal).append("\n");
        
        return receipt.toString();
    }
    
    /**
     * Generates an invoice with tax calculation
     * 
     * @param customerName the customer's name
     * @param amount the purchase amount
     * @return a formatted invoice
     */
    public String generateInvoice(String customerName, double amount) {
        // Tax calculation - DUPLICATED from calculateTax method and generateReceipt
        double taxRate = 0.1; // 10% tax
        double taxAmount = amount * taxRate;
        double total = amount + taxAmount;
        
        // Format currency - DUPLICATED from other methods
        String formattedAmount = String.format("$%.2f", amount);
        String formattedTax = String.format("$%.2f", taxAmount);
        String formattedTotal = String.format("$%.2f", total);
        
        // Build invoice - Similar structure to receipt
        StringBuilder invoice = new StringBuilder();
        invoice.append("INVOICE\n");
        invoice.append("=======\n");
        invoice.append("Customer: ").append(customerName).append("\n");
        invoice.append("Amount: ").append(formattedAmount).append("\n");
        invoice.append("Tax (10%): ").append(formattedTax).append("\n");
        invoice.append("Total Due: ").append(formattedTotal).append("\n");
        
        return invoice.toString();
    }
}