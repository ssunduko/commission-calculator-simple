package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class violates the Dependency Inversion Principle (DIP).
 * 
 * DIP states that:
 * 1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
 * 2. Abstractions should not depend on details. Details should depend on abstractions.
 * 
 * The violation occurs because:
 * 1. The CommissionService class directly depends on concrete implementations (MySqlDatabase, FileLogger, EmailSender)
 * 2. The high-level business logic is tightly coupled with low-level implementation details
 * 3. It's difficult to swap out implementations (e.g., changing from MySQL to MongoDB or file logging to cloud logging)
 */
public class DIPViolation {
    
    private static final Logger LOGGER = Logger.getLogger(DIPViolation.class.getName());
    
    // Violates DIP: Direct dependency on concrete implementations
    private final MySqlDatabase database;
    private final FileLogger logger;
    private final EmailSender emailSender;
    
    public DIPViolation() {
        // Violates DIP: Creating concrete implementations directly
        this.database = new MySqlDatabase("jdbc:mysql://localhost:3306/commissions", "user", "password");
        this.logger = new FileLogger("commission_service.log");
        this.emailSender = new EmailSender("smtp.company.com", 587, "notifications@company.com");
    }
    
    public CommissionCalculation calculateCommission(String dealId, String salesRepId) {
        try {
            // Violates DIP: Direct dependency on concrete implementation details
            logger.logInfo("Calculating commission for deal: " + dealId);
            
            // Get deal from database
            Deal deal = database.getDealById(dealId);
            if (deal == null) {
                logger.logError("Deal not found: " + dealId);
                throw new IllegalArgumentException("Deal not found: " + dealId);
            }
            
            // Get sales rep from database
            User salesRep = database.getUserById(salesRepId);
            if (salesRep == null) {
                logger.logError("Sales rep not found: " + salesRepId);
                throw new IllegalArgumentException("Sales rep not found: " + salesRepId);
            }
            
            // Calculate commission (simplified)
            BigDecimal commissionAmount = deal.getValue().multiply(BigDecimal.valueOf(0.05));
            
            // Create commission calculation
            CommissionCalculation calculation = new CommissionCalculation();
            calculation.setId("CALC-" + System.currentTimeMillis());
            calculation.setDealId(dealId);
            calculation.setSalesRepId(salesRepId);
            calculation.setBaseCommission(commissionAmount);
            calculation.setCalculationDate(LocalDate.now());
            
            // Save to database
            database.saveCommissionCalculation(calculation);
            
            // Send email notification
            String emailSubject = "Commission Calculated for Deal " + dealId;
            String emailBody = "Dear " + salesRep.getFullName() + ",\n\n" +
                    "Your commission for deal " + dealId + " has been calculated.\n" +
                    "Amount: $" + commissionAmount + "\n\n" +
                    "Thank you,\nCommission Department";
            emailSender.sendEmail(salesRep.getEmail(), emailSubject, emailBody);
            
            logger.logInfo("Commission calculation completed for deal: " + dealId);
            return calculation;
            
        } catch (Exception e) {
            logger.logError("Error calculating commission: " + e.getMessage());
            throw new RuntimeException("Error calculating commission", e);
        }
    }
    
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        try {
            logger.logInfo("Getting calculations for sales rep: " + salesRepId);
            return database.getCommissionCalculationsBySalesRep(salesRepId);
        } catch (Exception e) {
            logger.logError("Error getting calculations: " + e.getMessage());
            throw new RuntimeException("Error getting calculations", e);
        }
    }
    
    // Low-level concrete implementation
    private static class MySqlDatabase {
        private final String url;
        private final String username;
        private final String password;
        
        public MySqlDatabase(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }
        
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url, username, password);
        }
        
        public Deal getDealById(String dealId) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM deals WHERE id = ?")) {
                
                stmt.setString(1, dealId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    Deal deal = new Deal();
                    deal.setId(rs.getString("id"));
                    deal.setTitle(rs.getString("title"));
                    // Set other properties
                    return deal;
                }
                
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        }
        
        public User getUserById(String userId) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setUsername(rs.getString("username"));
                    // Set other properties
                    return user;
                }
                
                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        }
        
        public void saveCommissionCalculation(CommissionCalculation calculation) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO commission_calculations (id, deal_id, sales_rep_id, base_commission, calculation_date) " +
                                 "VALUES (?, ?, ?, ?, ?)")) {
                
                stmt.setString(1, calculation.getId());
                stmt.setString(2, calculation.getDealId());
                stmt.setString(3, calculation.getSalesRepId());
                stmt.setBigDecimal(4, calculation.getBaseCommission());
                stmt.setObject(5, calculation.getCalculationDate());
                
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        }
        
        public List<CommissionCalculation> getCommissionCalculationsBySalesRep(String salesRepId) {
            List<CommissionCalculation> calculations = new ArrayList<>();
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT * FROM commission_calculations WHERE sales_rep_id = ?")) {
                
                stmt.setString(1, salesRepId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    CommissionCalculation calculation = new CommissionCalculation();
                    calculation.setId(rs.getString("id"));
                    calculation.setDealId(rs.getString("deal_id"));
                    calculation.setSalesRepId(rs.getString("sales_rep_id"));
                    calculation.setBaseCommission(rs.getBigDecimal("base_commission"));
                    // Set other properties
                    
                    calculations.add(calculation);
                }
                
                return calculations;
            } catch (SQLException e) {
                throw new RuntimeException("Database error", e);
            }
        }
    }
    
    // Another low-level concrete implementation
    private static class FileLogger {
        private String logFile;
        
        public FileLogger(String logFile) {
            this.logFile = logFile;
        }
        
        public void logInfo(String message) {
            log("INFO", message);
        }
        
        public void logError(String message) {
            log("ERROR", message);
        }
        
        private void log(String level, String message) {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(LocalDate.now() + " [" + level + "] " + message + "\n");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error writing to log file", e);
            }
        }
    }
    
    // Another low-level concrete implementation
    private static class EmailSender {
        private String smtpServer;
        private int smtpPort;
        private String fromAddress;
        
        public EmailSender(String smtpServer, int smtpPort, String fromAddress) {
            this.smtpServer = smtpServer;
            this.smtpPort = smtpPort;
            this.fromAddress = fromAddress;
        }
        
        public void sendEmail(String toAddress, String subject, String body) {
            // In a real implementation, this would use JavaMail or similar
            LOGGER.info("Sending email to: " + toAddress);
            LOGGER.info("Subject: " + subject);
            LOGGER.info("Body: " + body);
        }
    }
}