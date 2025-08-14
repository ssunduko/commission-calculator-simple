package com.chapman.edu.commissions.principles.soc.fixed;

import java.util.List;

/**
 * This class is responsible only for formatting order data for presentation
 * It follows the Separation of Concerns principle by focusing solely on presentation logic
 */
public class OrderFormatter {
    
    /**
     * Formats an order as a summary
     * 
     * @param order the order to format
     * @return a formatted order summary
     */
    public String formatOrderSummary(Order order) {
        StringBuilder output = new StringBuilder();
        output.append("===== ORDER SUMMARY =====\n");
        output.append("Order ID: ").append(order.getOrderId()).append("\n");
        output.append("Customer: ").append(order.getCustomerName()).append("\n");
        output.append("Product: ").append(order.getProductName()).append("\n");
        output.append("Quantity: ").append(order.getQuantity()).append("\n");
        output.append("Unit Price: $").append(String.format("%.2f", order.getUnitPrice())).append("\n");
        output.append("Total Amount: $").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        output.append("Commission: $").append(String.format("%.2f", order.getCommission())).append("\n");
        output.append("=======================");
        
        return output.toString();
    }
    
    /**
     * Formats a commission rate update summary
     * 
     * @param oldRate the old commission rate
     * @param newRate the new commission rate
     * @param updatedCount the number of orders updated
     * @return a formatted commission update summary
     */
    public String formatCommissionUpdate(double oldRate, double newRate, int updatedCount) {
        StringBuilder output = new StringBuilder();
        output.append("Commission Rate Update\n");
        output.append("---------------------\n");
        output.append("Old Rate: ").append(String.format("%.2f", oldRate * 100)).append("%\n");
        output.append("New Rate: ").append(String.format("%.2f", newRate * 100)).append("%\n");
        output.append("Updated ").append(updatedCount).append(" order(s)");
        
        return output.toString();
    }
    
    /**
     * Formats a list of orders as a plain text report
     * 
     * @param orders the orders to include in the report
     * @return a formatted plain text report
     */
    public String formatPlainTextReport(List<Order> orders) {
        double totalSales = 0;
        double totalCommission = 0;
        
        // Calculate totals
        for (Order order : orders) {
            totalSales += order.getTotalAmount();
            totalCommission += order.getCommission();
        }
        
        StringBuilder report = new StringBuilder();
        report.append("ORDER REPORT\n");
        report.append("------------\n");
        report.append("Number of Orders: ").append(orders.size()).append("\n");
        report.append("Total Sales: $").append(String.format("%.2f", totalSales)).append("\n");
        report.append("Total Commission: $").append(String.format("%.2f", totalCommission)).append("\n\n");
        report.append("Order Details:\n");
        
        for (Order order : orders) {
            report.append(formatOrderRecord(order)).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Formats a list of orders as an HTML report
     * 
     * @param orders the orders to include in the report
     * @return a formatted HTML report
     */
    public String formatHtmlReport(List<Order> orders) {
        double totalSales = 0;
        double totalCommission = 0;
        
        // Calculate totals
        for (Order order : orders) {
            totalSales += order.getTotalAmount();
            totalCommission += order.getCommission();
        }
        
        StringBuilder report = new StringBuilder();
        report.append("<html><head><title>Order Report</title></head><body>\n");
        report.append("<h1>Order Report</h1>\n");
        report.append("<p>Number of Orders: ").append(orders.size()).append("</p>\n");
        report.append("<p>Total Sales: $").append(String.format("%.2f", totalSales)).append("</p>\n");
        report.append("<p>Total Commission: $").append(String.format("%.2f", totalCommission)).append("</p>\n");
        report.append("<h2>Order Details:</h2>\n<ul>\n");
        
        for (Order order : orders) {
            report.append("<li>").append(formatOrderRecord(order)).append("</li>\n");
        }
        
        report.append("</ul></body></html>");
        
        return report.toString();
    }
    
    /**
     * Formats a list of orders as a CSV report
     * 
     * @param orders the orders to include in the report
     * @return a formatted CSV report
     */
    public String formatCsvReport(List<Order> orders) {
        StringBuilder report = new StringBuilder();
        report.append("Order ID,Customer,Product,Quantity,Unit Price,Total,Commission\n");
        
        for (Order order : orders) {
            report.append(order.getOrderId()).append(",");
            report.append(order.getCustomerName()).append(",");
            report.append(order.getProductName()).append(",");
            report.append(order.getQuantity()).append(",");
            report.append(String.format("%.2f", order.getUnitPrice())).append(",");
            report.append(String.format("%.2f", order.getTotalAmount())).append(",");
            report.append(String.format("%.2f", order.getCommission())).append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Formats an order as a record string
     * 
     * @param order the order to format
     * @return a formatted order record
     */
    private String formatOrderRecord(Order order) {
        return String.format("Order: %s, Customer: %s, Product: %s, Quantity: %d, Unit Price: $%.2f, Total: $%.2f, Commission: $%.2f",
                order.getOrderId(), order.getCustomerName(), order.getProductName(), order.getQuantity(), 
                order.getUnitPrice(), order.getTotalAmount(), order.getCommission());
    }
}