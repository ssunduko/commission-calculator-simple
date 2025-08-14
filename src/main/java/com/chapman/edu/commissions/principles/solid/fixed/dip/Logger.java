package com.chapman.edu.commissions.principles.solid.fixed.dip;

/**
 * This interface defines the contract for logging operations.
 * It follows the Dependency Inversion Principle by providing an abstraction
 * that high-level modules can depend on, rather than depending on concrete implementations.
 */
public interface Logger {
    
    /**
     * Logs an informational message.
     * 
     * @param message The message to log
     */
    void logInfo(String message);
    
    /**
     * Logs an error message.
     * 
     * @param message The message to log
     */
    void logError(String message);
    
    /**
     * Logs an error message with an exception.
     * 
     * @param message The message to log
     * @param e The exception to log
     */
    void logError(String message, Exception e);
}