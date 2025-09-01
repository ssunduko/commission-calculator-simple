package com.chapman.edu.commissions.principles.soc.fixed;

/**
 * This class is responsible only for logging
 * It follows the Separation of Concerns principle by focusing solely on logging functionality
 */
public class Logger {
    
    /**
     * Log levels
     */
    public enum LogLevel {
        INFO,
        WARNING,
        ERROR
    }
    
    /**
     * Logs a message with the specified log level
     * 
     * @param level the log level
     * @param message the message to log
     */
    public void log(LogLevel level, String message) {
        String formattedMessage = String.format("%s: %s", level.toString(), message);
        System.out.println(formattedMessage);
    }
    
    /**
     * Logs an info message
     * 
     * @param message the message to log
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Logs a warning message
     * 
     * @param message the message to log
     */
    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    /**
     * Logs an error message
     * 
     * @param message the message to log
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }
}