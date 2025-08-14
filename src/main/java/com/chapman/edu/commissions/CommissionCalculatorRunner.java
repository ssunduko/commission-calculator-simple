package com.chapman.edu.commissions;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

/**
 * CommissionCalculatorRunner application class for the Sales Commission Calculator.
 * This application provides functionality for calculating, tracking, and managing sales commissions.
 * This is a simplified version without web and database dependencies.
 */
public class CommissionCalculatorRunner {
    private static final Logger LOGGER = Logger.getLogger(CommissionCalculatorRunner.class.getName());
    private static final Properties properties = new Properties();

    public static void main(String[] args) {
        loadProperties();

        LOGGER.info("Starting Sales Commission Calculator application");

        // Example of creating and using model objects
        User salesRep = createSampleUser();
        CommissionPlan plan = createSampleCommissionPlan();
        Deal deal = createSampleDeal(salesRep);

        LOGGER.info("Created sample user: " + salesRep.getUsername());
        LOGGER.info("Created sample commission plan: " + plan.getName());
        LOGGER.info("Created sample deal: " + deal.getTitle() + " worth $" + deal.getValue());

        LOGGER.info("Application initialized successfully");
    }

    private static void loadProperties() {
        try (InputStream input = CommissionCalculatorRunner.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                LOGGER.warning("Unable to find application.properties");
                return;
            }
            properties.load(input);
            LOGGER.info("Properties loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load properties", e);
        }
    }

    /**
     * Creates a sample user for demonstration purposes.
     * 
     * @return a sample User object
     */
    private static User createSampleUser() {
        User user = new User();
        user.setId("1");
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.addRole(UserRole.SALES_REP);
        return user;
    }

    /**
     * Creates a sample commission plan for demonstration purposes.
     * 
     * @return a sample CommissionPlan object
     */
    private static CommissionPlan createSampleCommissionPlan() {
        CommissionPlan plan = new CommissionPlan();
        plan.setId("1");
        plan.setName("Standard Sales Commission Plan");
        return plan;
    }

    /**
     * Creates a sample deal for demonstration purposes.
     * 
     * @param salesRep the sales representative associated with the deal
     * @return a sample Deal object
     */
    private static Deal createSampleDeal(User salesRep) {
        Deal deal = new Deal();
        deal.setId("1");
        deal.setTitle("Enterprise Software License");
        deal.setValue(new BigDecimal("100000.00"));
        deal.setSalesRepId(salesRep.getId());
        return deal;
    }
}
