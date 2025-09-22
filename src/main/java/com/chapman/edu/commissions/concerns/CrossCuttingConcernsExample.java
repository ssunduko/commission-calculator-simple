package com.chapman.edu.commissions.concerns;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionPlan;

import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class demonstrates cross-cutting concerns in Java.
 * 
 * Cross-cutting concerns are aspects of a program that affect multiple parts
 * of the system and can't be cleanly decomposed from the rest of the system.
 * Examples include logging, security, transaction management, error handling, etc.
 * 
 * In this example, we implement logging and security as cross-cutting concerns
 * that apply to various operations in the commission calculator system.
 */
public class CrossCuttingConcernsExample {
    
    // Logger is a cross-cutting concern as it's used across multiple components
    private static final Logger LOGGER = Logger.getLogger(CrossCuttingConcernsExample.class.getName());
    
    /**
     * Main method to demonstrate cross-cutting concerns
     */
    public static void main(String[] args) {
        // Create a sample user
        User user = new User("jsmith", "john.smith@example.com", "John", "Smith");
        user.setId("USER001");
        
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", java.math.BigDecimal.valueOf(10000), user.getId());
        deal.setId("DEAL001");
        
        // Create a sample commission plan
        CommissionPlan plan = new CommissionPlan("Standard Plan", null);
        plan.setId("PLAN001");
        
        // Create instances of our services with cross-cutting concerns
        DealService dealService = new DealService();
        CommissionService commissionService = new CommissionService();
        
        // Use the services
        dealService.createDeal(deal, user);
        commissionService.calculateCommission(deal, plan, user);
    }
    
    /**
     * Security aspect is a cross-cutting concern that handles authorization
     * across different parts of the application.
     */
    static class SecurityAspect {
        /**
         * Check if a user is authorized to perform an action
         * 
         * @param user the user to check
         * @param action the action to authorize
         * @return true if the user is authorized, false otherwise
         */
        public static boolean isAuthorized(User user, String action) {
            LOGGER.log(Level.INFO, "Checking authorization for user {0} to perform {1}", 
                    new Object[]{user.getUsername(), action});
            
            // In a real application, this would check against permissions in a database
            // For this example, we'll just do a simple check
            if (action.startsWith("CREATE") && !user.isSalesRep() && !user.isSalesManager()) {
                LOGGER.log(Level.WARNING, "User {0} not authorized to perform {1}", 
                        new Object[]{user.getUsername(), action});
                return false;
            }
            
            if (action.startsWith("CALCULATE") && !user.isSalesRep() && !user.isSalesManager() && !user.isFinanceAdmin()) {
                LOGGER.log(Level.WARNING, "User {0} not authorized to perform {1}", 
                        new Object[]{user.getUsername(), action});
                return false;
            }
            
            LOGGER.log(Level.INFO, "User {0} authorized to perform {1}", 
                    new Object[]{user.getUsername(), action});
            return true;
        }
    }
    
    /**
     * Logging aspect is a cross-cutting concern that handles logging
     * across different parts of the application.
     */
    static class LoggingAspect {
        /**
         * Log method entry with parameters
         * 
         * @param methodName the name of the method
         * @param params the parameters to log
         */
        public static void logEntry(String methodName, Object... params) {
            StringBuilder message = new StringBuilder("Entering method: " + methodName + " with parameters: ");
            for (Object param : params) {
                message.append(param).append(", ");
            }
            LOGGER.info(message.toString());
        }
        
        /**
         * Log method exit with result
         * 
         * @param methodName the name of the method
         * @param result the result to log
         */
        public static void logExit(String methodName, Object result) {
            LOGGER.info("Exiting method: " + methodName + " with result: " + result);
        }
        
        /**
         * Log an exception
         * 
         * @param methodName the name of the method
         * @param exception the exception to log
         */
        public static void logException(String methodName, Exception exception) {
            LOGGER.log(Level.SEVERE, "Exception in method: " + methodName, exception);
        }
    }
    
    /**
     * DealService demonstrates how cross-cutting concerns are applied
     * to business logic.
     */
    static class DealService {
        /**
         * Create a new deal
         * 
         * @param deal the deal to create
         * @param user the user creating the deal
         * @return true if the deal was created successfully, false otherwise
         */
        public boolean createDeal(Deal deal, User user) {
            String methodName = "createDeal";
            LoggingAspect.logEntry(methodName, deal, user);
            
            try {
                // Check if the user is authorized to create a deal
                if (!SecurityAspect.isAuthorized(user, "CREATE_DEAL")) {
                    LoggingAspect.logExit(methodName, false);
                    return false;
                }
                
                // Business logic for creating a deal would go here
                LOGGER.info("Creating deal: " + deal.getTitle());
                
                // In a real application, this would save the deal to a database
                deal.setLastModifiedDate(java.time.LocalDate.now());
                
                LoggingAspect.logExit(methodName, true);
                return true;
            } catch (Exception e) {
                LoggingAspect.logException(methodName, e);
                throw e;
            }
        }
    }
    
    /**
     * CommissionService demonstrates how cross-cutting concerns are applied
     * to business logic.
     */
    static class CommissionService {
        /**
         * Calculate commission for a deal
         * 
         * @param deal the deal to calculate commission for
         * @param plan the commission plan to use
         * @param user the user requesting the calculation
         * @return the calculated commission amount
         */
        public double calculateCommission(Deal deal, CommissionPlan plan, User user) {
            String methodName = "calculateCommission";
            LoggingAspect.logEntry(methodName, deal, plan, user);
            
            try {
                // Check if the user is authorized to calculate commission
                if (!SecurityAspect.isAuthorized(user, "CALCULATE_COMMISSION")) {
                    LoggingAspect.logExit(methodName, 0.0);
                    return 0.0;
                }
                
                // Business logic for calculating commission would go here
                LOGGER.info("Calculating commission for deal: " + deal.getTitle() + 
                        " using plan: " + plan.getName());
                
                // In a real application, this would calculate the commission based on the plan
                double commission = deal.getValue().doubleValue() * 0.05;
                
                LoggingAspect.logExit(methodName, commission);
                return commission;
            } catch (Exception e) {
                LoggingAspect.logException(methodName, e);
                throw e;
            }
        }
    }
}