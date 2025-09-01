package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates how the Single Responsibility Principle is applied.
 * It shows how responsibilities are separated into specialized classes, each with a single reason to change.
 */
public class SRPDemo {

    public static void main(String[] args) {
        // Create instances of the specialized classes
        DatabaseService databaseService = new DatabaseServiceImpl();
        CommissionCalculator calculator = new CommissionCalculator(databaseService);
        EmailService emailService = new EmailServiceImpl("smtp.example.com", 587, "notifications@example.com");
        ReportGenerator reportGenerator = new ReportGeneratorImpl();

        // Create the coordinator class that uses the specialized classes
        CommissionProcessor processor = new CommissionProcessor(
                calculator, databaseService, emailService, reportGenerator);

        // Set up some test data
        setupTestData(databaseService);

        // Process a commission calculation
        System.out.println("Processing commission for deal DEAL-001 with plan PLAN-001");
        CommissionCalculation calculation = processor.processCommission("DEAL-001", "PLAN-001");
        System.out.println("Commission calculation created with ID: " + calculation.getId());

        // Get calculations for a sales rep
        System.out.println("\nGetting calculations for sales rep SALES-001");
        List<CommissionCalculation> calculations = processor.getCalculationsBySalesRep("SALES-001");
        System.out.println("Found " + calculations.size() + " calculations");

        // Demonstrate how SRP is applied
        System.out.println("\nSRP Demonstration Complete");
        System.out.println("The CommissionProcessor orchestrates the process but delegates specific responsibilities:");
        System.out.println("1. CommissionCalculator: Responsible only for calculating commissions");
        System.out.println("2. DatabaseService: Responsible only for data persistence");
        System.out.println("3. EmailService: Responsible only for sending email notifications");
        System.out.println("4. ReportGenerator: Responsible only for generating reports");
        System.out.println("\nEach class has a single responsibility and a single reason to change.");
    }

    /**
     * Sets up test data in the database service.
     */
    private static void setupTestData(DatabaseService databaseService) {
        // Create a test deal
        Deal deal = new Deal();
        deal.setId("DEAL-001");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000"));
        deal.setSalesRepId("SALES-001");
        databaseService.saveDeal(deal);

        // Create a test user
        User user = new User();
        user.setId("SALES-001");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        databaseService.saveUser(user);
    }
}
