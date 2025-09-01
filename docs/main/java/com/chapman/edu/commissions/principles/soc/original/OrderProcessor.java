package com.chapman.edu.commissions.principles.soc.original;

import java.util.ArrayList;
import java.util.List;

/**
 * This class violates the Separation of Concerns principle by mixing multiple responsibilities:
 * - Business logic (order processing, commission calculation)
 * - Data access (database operations)
 * - Presentation logic (formatting output for display)
 * - Validation logic
 * - Logging
 */
public class OrderProcessor {
    
    private double commissionRate = 0.1; // 10% commission
    private List<String> orderDatabase = new ArrayList<>(); // Simulating a database
    
    /**
     * Processes a new order with all concerns mixed in a single method
     * 
     * @param orderId the order ID
     * @param customerName the customer's name
     * @param productName the product name
     * @param quantity the quantity ordered
     * @param unitPrice the unit price
     * @return a formatted order summary
     */
    public String processOrder(String orderId, String customerName, String productName, int quantity, double unitPrice) {
        // Validation logic
        if (orderId == null || orderId.trim().isEmpty()) {
            System.out.println("ERROR: Order ID cannot be empty");
            return "Error: Invalid order ID";
        }
        
        if (customerName == null || customerName.trim().isEmpty()) {
            System.out.println("ERROR: Customer name cannot be empty");
            return "Error: Invalid customer name";
        }
        
        if (productName == null || productName.trim().isEmpty()) {
            System.out.println("ERROR: Product name cannot be empty");
            return "Error: Invalid product name";
        }
        
        if (quantity <= 0) {
            System.out.println("ERROR: Quantity must be greater than zero");
            return "Error: Invalid quantity";
        }
        
        if (unitPrice <= 0) {
            System.out.println("ERROR: Unit price must be greater than zero");
            return "Error: Invalid unit price";
        }
        
        // Business logic - Calculate total and commission
        double totalAmount = quantity * unitPrice;
        double commission = totalAmount * commissionRate;
        
        // Data access - Save to "database"
        String orderRecord = String.format("Order: %s, Customer: %s, Product: %s, Quantity: %d, Unit Price: $%.2f, Total: $%.2f, Commission: $%.2f",
                orderId, customerName, productName, quantity, unitPrice, totalAmount, commission);
        orderDatabase.add(orderRecord);
        
        // Logging
        System.out.println("INFO: Order processed successfully - " + orderRecord);
        
        // Presentation logic - Format output for display
        StringBuilder output = new StringBuilder();
        output.append("===== ORDER SUMMARY =====\n");
        output.append("Order ID: ").append(orderId).append("\n");
        output.append("Customer: ").append(customerName).append("\n");
        output.append("Product: ").append(productName).append("\n");
        output.append("Quantity: ").append(quantity).append("\n");
        output.append("Unit Price: $").append(String.format("%.2f", unitPrice)).append("\n");
        output.append("Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
        output.append("Commission: $").append(String.format("%.2f", commission)).append("\n");
        output.append("=======================");
        
        return output.toString();
    }
    
    /**
     * Updates the commission rate and recalculates all commissions
     * 
     * @param newRate the new commission rate
     * @return a summary of the update
     */
    public String updateCommissionRate(double newRate) {
        // Validation
        if (newRate <= 0 || newRate >= 1) {
            System.out.println("ERROR: Commission rate must be between 0 and 1");
            return "Error: Invalid commission rate";
        }
        
        // Business logic
        double oldRate = commissionRate;
        commissionRate = newRate;
        
        // Data access - Update all records with new commission
        List<String> updatedRecords = new ArrayList<>();
        for (String record : orderDatabase) {
            // Parse the record to extract values
            String[] parts = record.split(", ");
            String orderId = parts[0].substring(7);
            String customerName = parts[1].substring(10);
            String productName = parts[2].substring(9);
            int quantity = Integer.parseInt(parts[3].substring(10));
            double unitPrice = Double.parseDouble(parts[4].substring(12, parts[4].length() - 1));
            
            // Recalculate with new rate
            double totalAmount = quantity * unitPrice;
            double newCommission = totalAmount * newRate;
            
            // Create updated record
            String updatedRecord = String.format("Order: %s, Customer: %s, Product: %s, Quantity: %d, Unit Price: $%.2f, Total: $%.2f, Commission: $%.2f",
                    orderId, customerName, productName, quantity, unitPrice, totalAmount, newCommission);
            updatedRecords.add(updatedRecord);
        }
        
        // Replace old records with updated ones
        orderDatabase = updatedRecords;
        
        // Logging
        System.out.println("INFO: Commission rate updated from " + oldRate + " to " + newRate);
        
        // Presentation logic
        StringBuilder output = new StringBuilder();
        output.append("Commission Rate Update\n");
        output.append("---------------------\n");
        output.append("Old Rate: ").append(String.format("%.2f", oldRate * 100)).append("%\n");
        output.append("New Rate: ").append(String.format("%.2f", newRate * 100)).append("%\n");
        output.append("Updated ").append(orderDatabase.size()).append(" order(s)");
        
        return output.toString();
    }
    
    /**
     * Generates a report of all orders
     * 
     * @param format the format of the report ("plain", "html", or "csv")
     * @return the formatted report
     */
    public String generateOrderReport(String format) {
        // Validation
        if (format == null || !(format.equals("plain") || format.equals("html") || format.equals("csv"))) {
            System.out.println("ERROR: Invalid format. Must be 'plain', 'html', or 'csv'");
            return "Error: Invalid report format";
        }
        
        // Business logic - Calculate totals
        double totalSales = 0;
        double totalCommission = 0;
        
        for (String record : orderDatabase) {
            // Extract total and commission from record
            String[] parts = record.split(", ");
            double total = Double.parseDouble(parts[5].substring(7, parts[5].length() - 1));
            double commission = Double.parseDouble(parts[6].substring(12, parts[6].length() - 1));
            
            totalSales += total;
            totalCommission += commission;
        }
        
        // Presentation logic - Format based on requested format
        StringBuilder report = new StringBuilder();
        
        if (format.equals("plain")) {
            report.append("ORDER REPORT\n");
            report.append("------------\n");
            report.append("Number of Orders: ").append(orderDatabase.size()).append("\n");
            report.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
            report.append("Total Commission: $").append(String.format("%.2f", totalCommission)).append("\n\n");
            report.append("Order Details:\n");
            
            for (String record : orderDatabase) {
                report.append(record).append("\n");
            }
        } else if (format.equals("html")) {
            report.append("<html><head><title>Order Report</title></head><body>\n");
            report.append("<h1>Order Report</h1>\n");
            report.append("<p>Number of Orders: ").append(orderDatabase.size()).append("</p>\n");
            report.append("<p>Total Sales: $").append(String.format("%.2f", totalSales)).append("</p>\n");
            report.append("<p>Total Commission: $").append(String.format("%.2f", totalCommission)).append("</p>\n");
            report.append("<h2>Order Details:</h2>\n<ul>\n");
            
            for (String record : orderDatabase) {
                report.append("<li>").append(record).append("</li>\n");
            }
            
            report.append("</ul></body></html>");
        } else if (format.equals("csv")) {
            report.append("Order ID,Customer,Product,Quantity,Unit Price,Total,Commission\n");
            
            for (String record : orderDatabase) {
                // Convert record to CSV format
                String csvRecord = record.replace("Order: ", "")
                        .replace("Customer: ", "")
                        .replace("Product: ", "")
                        .replace("Quantity: ", "")
                        .replace("Unit Price: $", "")
                        .replace("Total: $", "")
                        .replace("Commission: $", "")
                        .replace(", ", ",");
                report.append(csvRecord).append("\n");
            }
        }
        
        // Logging
        System.out.println("INFO: Generated " + format + " report with " + orderDatabase.size() + " orders");
        
        return report.toString();
    }
}