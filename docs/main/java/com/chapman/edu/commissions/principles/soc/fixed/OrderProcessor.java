package com.chapman.edu.commissions.principles.soc.fixed;

import java.util.List;

/**
 * This class is responsible for order processing business logic
 * It follows the Separation of Concerns principle by focusing solely on business operations
 * and delegating other responsibilities to specialized classes
 */
public class OrderProcessor {
    
    private double commissionRate = 0.1; // 10% commission
    private final OrderRepository repository;
    private final OrderValidator validator;
    private final OrderFormatter formatter;
    private final Logger logger;
    
    /**
     * Constructor that initializes dependencies
     */
    public OrderProcessor() {
        this.repository = new OrderRepository();
        this.validator = new OrderValidator();
        this.formatter = new OrderFormatter();
        this.logger = new Logger();
    }
    
    /**
     * Constructor with dependency injection for testing
     * 
     * @param repository the order repository
     * @param validator the order validator
     * @param formatter the order formatter
     * @param logger the logger
     */
    public OrderProcessor(OrderRepository repository, OrderValidator validator, OrderFormatter formatter, Logger logger) {
        this.repository = repository;
        this.validator = validator;
        this.formatter = formatter;
        this.logger = logger;
    }
    
    /**
     * Processes a new order
     * 
     * @param orderId the order ID
     * @param customerName the customer's name
     * @param productName the product name
     * @param quantity the quantity ordered
     * @param unitPrice the unit price
     * @return a formatted order summary
     */
    public String processOrder(String orderId, String customerName, String productName, int quantity, double unitPrice) {
        // Validate input using the validator
        String validationError = validator.validateOrder(orderId, customerName, productName, quantity, unitPrice);
        if (validationError != null) {
            logger.error(validationError);
            return validationError;
        }
        
        // Create order object
        Order order = new Order(orderId, customerName, productName, quantity, unitPrice);
        
        // Calculate commission
        double commission = order.getTotalAmount() * commissionRate;
        order.setCommission(commission);
        
        // Save to repository
        repository.saveOrder(order);
        
        // Log success
        logger.info("Order processed successfully - " + orderId);
        
        // Format and return result using formatter
        return formatter.formatOrderSummary(order);
    }
    
    /**
     * Updates the commission rate
     * 
     * @param newRate the new commission rate
     * @return a summary of the update
     */
    public String updateCommissionRate(double newRate) {
        // Validate input using the validator
        if (!validator.isValidCommissionRate(newRate)) {
            String error = "Error: Invalid commission rate";
            logger.error(error);
            return error;
        }
        
        // Update rate
        double oldRate = commissionRate;
        commissionRate = newRate;
        
        // Update all orders in repository
        repository.updateAllCommissions(newRate);
        
        // Log success
        logger.info("Commission rate updated from " + oldRate + " to " + newRate);
        
        // Format and return result using formatter
        return formatter.formatCommissionUpdate(oldRate, newRate, repository.getOrderCount());
    }
    
    /**
     * Generates a report of all orders
     * 
     * @param format the format of the report ("plain", "html", or "csv")
     * @return the formatted report
     */
    public String generateOrderReport(String format) {
        // Validate input using the validator
        if (!validator.isValidReportFormat(format)) {
            String error = "Error: Invalid report format";
            logger.error(error);
            return error;
        }
        
        // Get all orders from repository
        List<Order> orders = repository.getAllOrders();
        
        // Format report based on requested format
        String report;
        if (format.equals("plain")) {
            report = formatter.formatPlainTextReport(orders);
        } else if (format.equals("html")) {
            report = formatter.formatHtmlReport(orders);
        } else {
            report = formatter.formatCsvReport(orders);
        }
        
        // Log success
        logger.info("Generated " + format + " report with " + orders.size() + " orders");
        
        return report;
    }
}