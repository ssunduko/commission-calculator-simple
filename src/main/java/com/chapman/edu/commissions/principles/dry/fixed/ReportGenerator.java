package com.chapman.edu.commissions.principles.dry.fixed;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A report generator class that follows the DRY principle
 * by extracting common calculations and formatting logic into reusable methods.
 */
public class ReportGenerator {
    
    // Constants for report formatting
    private static final String DAILY_REPORT_TITLE = "DAILY SALES REPORT";
    private static final String MONTHLY_REPORT_TITLE = "MONTHLY SALES REPORT";
    private static final String YEARLY_REPORT_TITLE = "YEARLY SALES REPORT";
    private static final String RECEIPT_TITLE = "RECEIPT";
    private static final String INVOICE_TITLE = "INVOICE";
    
    // Standard tax rate
    private static final double STANDARD_TAX_RATE = 0.1; // 10% tax
    
    /**
     * Generates a daily sales report
     * 
     * @param date the date for the report
     * @param salesData list of sales amounts
     * @return a formatted sales report
     */
    public String generateDailySalesReport(LocalDate date, List<Double> salesData) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return generateSalesReport(DAILY_REPORT_TITLE, "Date", formattedDate, salesData);
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
        LocalDate date = LocalDate.of(year, month, 1);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        return generateSalesReport(MONTHLY_REPORT_TITLE, "Month", formattedDate, salesData);
    }
    
    /**
     * Generates a yearly sales report
     * 
     * @param year the year
     * @param salesData list of sales amounts
     * @return a formatted sales report
     */
    public String generateYearlySalesReport(int year, List<Double> salesData) {
        String formattedDate = String.valueOf(year);
        return generateSalesReport(YEARLY_REPORT_TITLE, "Year", formattedDate, salesData);
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
        return generateFinancialDocument(RECEIPT_TITLE, customerName, amount, "Total");
    }
    
    /**
     * Generates an invoice with tax calculation
     * 
     * @param customerName the customer's name
     * @param amount the purchase amount
     * @return a formatted invoice
     */
    public String generateInvoice(String customerName, double amount) {
        return generateFinancialDocument(INVOICE_TITLE, customerName, amount, "Total Due");
    }
    
    /**
     * Generates a sales report with the given parameters
     * 
     * @param title the report title
     * @param dateLabel the label for the date field (e.g., "Date", "Month", "Year")
     * @param dateValue the formatted date value
     * @param salesData the list of sales amounts
     * @return a formatted sales report
     */
    private String generateSalesReport(String title, String dateLabel, String dateValue, List<Double> salesData) {
        double totalSales = calculateTotal(salesData);
        double averageSale = calculateAverage(salesData);
        
        String formattedTotal = formatCurrency(totalSales);
        String formattedAverage = formatCurrency(averageSale);
        
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n");
        report.append("=".repeat(title.length())).append("\n");
        report.append(dateLabel).append(": ").append(dateValue).append("\n");
        report.append("Total Sales: ").append(formattedTotal).append("\n");
        report.append("Number of Sales: ").append(salesData.size()).append("\n");
        report.append("Average Sale: ").append(formattedAverage).append("\n");
        
        return report.toString();
    }
    
    /**
     * Generates a financial document (receipt or invoice) with the given parameters
     * 
     * @param title the document title
     * @param customerName the customer's name
     * @param amount the purchase amount
     * @param totalLabel the label for the total field (e.g., "Total", "Total Due")
     * @return a formatted financial document
     */
    private String generateFinancialDocument(String title, String customerName, double amount, String totalLabel) {
        double taxAmount = calculateTax(amount, STANDARD_TAX_RATE);
        double total = amount + taxAmount;
        
        String formattedAmount = formatCurrency(amount);
        String formattedTax = formatCurrency(taxAmount);
        String formattedTotal = formatCurrency(total);
        
        StringBuilder document = new StringBuilder();
        document.append(title).append("\n");
        document.append("=======").append("\n");
        document.append("Customer: ").append(customerName).append("\n");
        document.append("Amount: ").append(formattedAmount).append("\n");
        document.append("Tax (10%): ").append(formattedTax).append("\n");
        document.append(totalLabel).append(": ").append(formattedTotal).append("\n");
        
        return document.toString();
    }
    
    /**
     * Calculates the total of a list of values
     * 
     * @param values the list of values
     * @return the total
     */
    private double calculateTotal(List<Double> values) {
        double total = 0;
        for (Double value : values) {
            total += value;
        }
        return total;
    }
    
    /**
     * Calculates the average of a list of values
     * 
     * @param values the list of values
     * @return the average, or 0 if the list is empty
     */
    private double calculateAverage(List<Double> values) {
        return values.isEmpty() ? 0 : calculateTotal(values) / values.size();
    }
    
    /**
     * Formats a value as currency
     * 
     * @param value the value to format
     * @return the formatted currency string
     */
    private String formatCurrency(double value) {
        return String.format("$%.2f", value);
    }
}