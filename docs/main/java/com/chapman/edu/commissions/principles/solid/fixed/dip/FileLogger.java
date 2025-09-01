package com.chapman.edu.commissions.principles.solid.fixed.dip;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Logger interface for file-based logging.
 * It follows the Dependency Inversion Principle by implementing an interface
 * that high-level modules depend on, rather than having them depend on this concrete implementation.
 */
public class FileLogger implements com.chapman.edu.commissions.principles.solid.fixed.dip.Logger {
    
    private static final Logger JAVA_LOGGER = Logger.getLogger(FileLogger.class.getName());
    private final String logFile;
    
    /**
     * Constructor with log file path.
     */
    public FileLogger(String logFile) {
        this.logFile = logFile;
    }
    
    @Override
    public void logInfo(String message) {
        log("INFO", message);
    }
    
    @Override
    public void logError(String message) {
        log("ERROR", message);
    }
    
    @Override
    public void logError(String message, Exception e) {
        log("ERROR", message + ": " + e.getMessage());
        JAVA_LOGGER.log(Level.SEVERE, message, e);
    }
    
    /**
     * Writes a log entry to the file.
     */
    private void log(String level, String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(LocalDate.now() + " [" + level + "] " + message + "\n");
        } catch (IOException e) {
            JAVA_LOGGER.log(Level.SEVERE, "Error writing to log file", e);
        }
    }
}