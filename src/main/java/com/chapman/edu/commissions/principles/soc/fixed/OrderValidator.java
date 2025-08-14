package com.chapman.edu.commissions.principles.soc.fixed;

/**
 * This class is responsible only for validating order data
 * It follows the Separation of Concerns principle by focusing solely on validation logic
 */
public class OrderValidator {
    
    /**
     * Validates order ID
     * 
     * @param orderId the order ID to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidOrderId(String orderId) {
        return orderId != null && !orderId.trim().isEmpty();
    }
    
    /**
     * Validates customer name
     * 
     * @param customerName the customer name to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidCustomerName(String customerName) {
        return customerName != null && !customerName.trim().isEmpty();
    }
    
    /**
     * Validates product name
     * 
     * @param productName the product name to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidProductName(String productName) {
        return productName != null && !productName.trim().isEmpty();
    }
    
    /**
     * Validates quantity
     * 
     * @param quantity the quantity to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Validates unit price
     * 
     * @param unitPrice the unit price to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidUnitPrice(double unitPrice) {
        return unitPrice > 0;
    }
    
    /**
     * Validates commission rate
     * 
     * @param rate the commission rate to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidCommissionRate(double rate) {
        return rate > 0 && rate < 1;
    }
    
    /**
     * Validates report format
     * 
     * @param format the report format to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidReportFormat(String format) {
        return format != null && (format.equals("plain") || format.equals("html") || format.equals("csv"));
    }
    
    /**
     * Validates all order fields
     * 
     * @param orderId the order ID
     * @param customerName the customer name
     * @param productName the product name
     * @param quantity the quantity
     * @param unitPrice the unit price
     * @return null if all fields are valid, otherwise an error message
     */
    public String validateOrder(String orderId, String customerName, String productName, int quantity, double unitPrice) {
        if (!isValidOrderId(orderId)) {
            return "Error: Invalid order ID";
        }
        
        if (!isValidCustomerName(customerName)) {
            return "Error: Invalid customer name";
        }
        
        if (!isValidProductName(productName)) {
            return "Error: Invalid product name";
        }
        
        if (!isValidQuantity(quantity)) {
            return "Error: Invalid quantity";
        }
        
        if (!isValidUnitPrice(unitPrice)) {
            return "Error: Invalid unit price";
        }
        
        return null; // No validation errors
    }
}