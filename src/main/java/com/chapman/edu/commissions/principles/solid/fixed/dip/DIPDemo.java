package com.chapman.edu.commissions.principles.solid.fixed.dip;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates how the Dependency Inversion Principle is applied.
 * It shows how high-level modules depend on abstractions, not concrete implementations.
 */
public class DIPDemo {
    
    public static void main(String[] args) {
        // Create concrete implementations of the interfaces
        Database database = new MockDatabase();
        Logger logger = new ConsoleLogger();
        EmailService emailService = new MockEmailService();
        
        // Create the high-level module that depends on abstractions
        CommissionService commissionService = new CommissionService(database, logger, emailService);
        
        // Set up some test data
        setupTestData((MockDatabase) database);
        
        // Calculate commission
        System.out.println("Calculating commission for deal DEAL-001");
        CommissionCalculation calculation = commissionService.calculateCommission("DEAL-001", "SALES-001");
        System.out.println("Commission calculation created with ID: " + calculation.getId());
        
        // Get calculations for a sales rep
        System.out.println("\nGetting calculations for sales rep SALES-001");
        List<CommissionCalculation> calculations = commissionService.getCalculationsBySalesRep("SALES-001");
        System.out.println("Found " + calculations.size() + " calculations");
        
        // Demonstrate how DIP is applied
        System.out.println("\nDIP Demonstration Complete");
        System.out.println("The CommissionService depends on abstractions, not concrete implementations:");
        System.out.println("1. It depends on the Database interface, not MySqlDatabase");
        System.out.println("2. It depends on the Logger interface, not FileLogger");
        System.out.println("3. It depends on the EmailService interface, not SmtpEmailService");
        System.out.println("\nThis allows us to easily swap implementations without changing the CommissionService:");
        System.out.println("- We can use MockDatabase for testing instead of MySqlDatabase");
        System.out.println("- We can use ConsoleLogger for development instead of FileLogger");
        System.out.println("- We can use MockEmailService for testing instead of SmtpEmailService");
        System.out.println("\nThis demonstrates the Dependency Inversion Principle in action.");
    }
    
    /**
     * Sets up test data in the mock database.
     */
    private static void setupTestData(MockDatabase database) {
        // Create a test deal
        Deal deal = new Deal();
        deal.setId("DEAL-001");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000"));
        deal.setSalesRepId("SALES-001");
        database.saveDeal(deal);
        
        // Create a test user
        User user = new User();
        user.setId("SALES-001");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        database.saveUser(user);
    }
    
    /**
     * A mock implementation of the Database interface for testing.
     */
    private static class MockDatabase implements Database {
        private Deal deal;
        private User user;
        private CommissionCalculation calculation;
        
        @Override
        public Deal getDealById(String dealId) {
            System.out.println("MockDatabase: Getting deal " + dealId);
            return deal;
        }
        
        @Override
        public User getUserById(String userId) {
            System.out.println("MockDatabase: Getting user " + userId);
            return user;
        }
        
        @Override
        public void saveCommissionCalculation(CommissionCalculation calculation) {
            System.out.println("MockDatabase: Saving calculation " + calculation.getId());
            this.calculation = calculation;
        }
        
        @Override
        public List<CommissionCalculation> getCommissionCalculationsBySalesRep(String salesRepId) {
            System.out.println("MockDatabase: Getting calculations for sales rep " + salesRepId);
            return calculation != null ? List.of(calculation) : List.of();
        }
        
        public void saveDeal(Deal deal) {
            this.deal = deal;
        }
        
        public void saveUser(User user) {
            this.user = user;
        }
    }
    
    /**
     * A simple implementation of the Logger interface that logs to the console.
     */
    private static class ConsoleLogger implements Logger {
        @Override
        public void logInfo(String message) {
            System.out.println("INFO: " + message);
        }
        
        @Override
        public void logError(String message) {
            System.err.println("ERROR: " + message);
        }
        
        @Override
        public void logError(String message, Exception e) {
            System.err.println("ERROR: " + message);
            e.printStackTrace();
        }
    }
    
    /**
     * A mock implementation of the EmailService interface for testing.
     */
    private static class MockEmailService implements EmailService {
        @Override
        public void sendEmail(String toAddress, String subject, String body) {
            System.out.println("MockEmailService: Sending email to " + toAddress);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
        }
        
        @Override
        public void sendEmailWithAttachment(String toAddress, String subject, String body, String attachmentPath) {
            System.out.println("MockEmailService: Sending email with attachment to " + toAddress);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("Attachment: " + attachmentPath);
        }
    }
}