package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stamp Coupling Example
 * 
 * Stamp coupling occurs when modules share a composite data structure and use only parts of it.
 * This creates dependencies between modules through the shared data structure.
 * 
 * In this example, the DealAnalyzer and DealReporter classes receive the entire Deal object
 * but only use specific parts of it, creating stamp coupling.
 */
public class StampCoupling {
    
    /**
     * DealAnalyzer class that analyzes deals
     */
    public static class DealAnalyzer {
        
        /**
         * Analyze a deal's profitability
         * 
         * @param deal The entire deal object (stamp coupling)
         * @return The profitability score
         */
        public double analyzeProfitability(Deal deal) {
            // Stamp coupling: We're passing the entire Deal object,
            // but this method only uses the products to calculate the total value
            
            BigDecimal totalValue = deal.calculateTotalValue();
            
            // Calculate profitability score (simplified example)
            double profitabilityScore = totalValue.doubleValue() / 1000.0;
            
            System.out.println("Analyzed profitability for deal: " + deal.getTitle());
            System.out.println("Total value: " + totalValue);
            System.out.println("Profitability score: " + profitabilityScore);
            
            return profitabilityScore;
        }
        
        /**
         * Analyze a deal's product mix
         * 
         * @param deal The entire deal object (stamp coupling)
         * @return The product diversity score
         */
        public double analyzeProductMix(Deal deal) {
            // Stamp coupling: We're passing the entire Deal object,
            // but this method only uses the products list
            
            List<DealProduct> products = deal.getProducts();
            
            // Calculate product diversity score (simplified example)
            double diversityScore = products.size() / 5.0;
            
            System.out.println("Analyzed product mix for deal: " + deal.getTitle());
            System.out.println("Number of products: " + products.size());
            System.out.println("Product diversity score: " + diversityScore);
            
            return diversityScore;
        }
    }
    
    /**
     * DealReporter class that generates reports for deals
     */
    public static class DealReporter {
        
        /**
         * Generate a summary report for a deal
         * 
         * @param deal The entire deal object (stamp coupling)
         * @return The summary report
         */
        public String generateSummaryReport(Deal deal) {
            // Stamp coupling: We're passing the entire Deal object,
            // but this method only uses the title, salesRepId, and status
            
            StringBuilder report = new StringBuilder();
            report.append("Deal Summary Report\n");
            report.append("------------------\n");
            report.append("Title: ").append(deal.getTitle()).append("\n");
            report.append("Sales Rep: ").append(deal.getSalesRepId()).append("\n");
            report.append("Status: ").append(deal.getStatus()).append("\n");
            
            System.out.println("Generated summary report for deal: " + deal.getTitle());
            
            return report.toString();
        }
        
        /**
         * Generate a financial report for a deal
         * 
         * @param deal The entire deal object (stamp coupling)
         * @return The financial report
         */
        public String generateFinancialReport(Deal deal) {
            // Stamp coupling: We're passing the entire Deal object,
            // but this method only uses the value and products
            
            StringBuilder report = new StringBuilder();
            report.append("Deal Financial Report\n");
            report.append("--------------------\n");
            report.append("Title: ").append(deal.getTitle()).append("\n");
            report.append("Total Value: ").append(deal.calculateTotalValue()).append("\n");
            report.append("Products:\n");
            
            for (DealProduct product : deal.getProducts()) {
                report.append("  - ").append(product.getProductName())
                      .append(": ").append(product.getQuantity())
                      .append(" x ").append(product.getPrice())
                      .append(" = ").append(product.calculateTotalPrice()).append("\n");
            }
            
            System.out.println("Generated financial report for deal: " + deal.getTitle());
            
            return report.toString();
        }
    }
    
    public static void main(String[] args) {
        // Create a deal
        Deal deal = new Deal("Test Deal", new BigDecimal("1000.00"), "sales-rep-1");
        deal.addProduct(new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00")));
        deal.addProduct(new DealProduct("prod2", "Product 2", 1, new BigDecimal("50.00")));
        
        // Create analyzer and reporter
        DealAnalyzer analyzer = new DealAnalyzer();
        DealReporter reporter = new DealReporter();
        
        // Analyze the deal
        double profitabilityScore = analyzer.analyzeProfitability(deal);
        double diversityScore = analyzer.analyzeProductMix(deal);
        
        // Generate reports
        String summaryReport = reporter.generateSummaryReport(deal);
        String financialReport = reporter.generateFinancialReport(deal);
        
        // Print reports
        System.out.println("\nSummary Report:");
        System.out.println(summaryReport);
        
        System.out.println("\nFinancial Report:");
        System.out.println(financialReport);
    }
}