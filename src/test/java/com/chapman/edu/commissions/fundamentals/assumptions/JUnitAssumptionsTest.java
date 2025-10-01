package com.chapman.edu.commissions.fundamentals.assumptions;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * This class demonstrates the use of JUnit 5 assumptions.
 * 
 * Assumptions in JUnit are used to run tests only if certain conditions are met.
 * If an assumption fails, the test is skipped rather than marked as failed.
 * This is useful for tests that should only run under specific conditions,
 * such as when a certain environment variable is set or when running on a specific OS.
 * 
 * The main assumption methods are:
 * 1. assumeTrue() - continues if the condition is true, otherwise skips the test
 * 2. assumeFalse() - continues if the condition is false, otherwise skips the test
 * 3. assumingThat() - executes the given code block only if the assumption is true
 */
public class JUnitAssumptionsTest {

    private User user;
    private Deal deal;
    private CommissionPlan plan;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    public void setUp() {
        // Initialize a user
        user = new User();
        user.setId("test-user");
        user.setUsername("test.user");
        user.setEmail("test.user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.addRole(UserRole.SALES_REP);

        // Initialize a deal
        deal = new Deal();
        deal.setId("test-deal");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000.00"));
        deal.setSalesRepId(user.getId());
        deal.setStatus(DealStatus.OPEN);

        // Initialize a commission plan
        plan = new CommissionPlan();
        plan.setId("test-plan");
        plan.setName("Test Plan");
        plan.setCurrency(Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(1));
    }

    /**
     * Demonstrates the use of assumeTrue().
     * 
     * The test will only run if the user has the SALES_REP role.
     * If the user doesn't have this role, the test is skipped.
     */
    @Test
    public void testAssumeTrue() {
        // Assume that the user has the SALES_REP role
        assumeTrue(user.hasRole(UserRole.SALES_REP), 
                "Skipping test because user doesn't have SALES_REP role");

        // If the assumption passes, this code will execute
        assertEquals("Test User", user.getFullName(), "User's full name should match");
        assertTrue(user.isSalesRep(), "User should be a sales rep");
    }

    /**
     * Demonstrates the use of assumeFalse().
     * 
     * The test will only run if the deal is not won.
     * If the deal is won, the test is skipped.
     */
    @Test
    public void testAssumeFalse() {
        // Assume that the deal is not won
        assumeFalse(deal.getStatus() == DealStatus.WON, 
                "Skipping test because deal is already won");

        // If the assumption passes, this code will execute
        assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN");

        // Change the deal status to WON
        deal.setStatus(DealStatus.WON);
        assertEquals(DealStatus.WON, deal.getStatus(), "Deal status should now be WON");
    }

    /**
     * Demonstrates the use of assumingThat().
     * 
     * The code block inside assumingThat() will only execute if the condition is true.
     * Unlike assumeTrue() and assumeFalse(), the test will continue even if the assumption fails.
     */
    @Test
    public void testAssumingThat() {
        // Check if the commission plan is active today
        LocalDate today = LocalDate.now();

        // This code block will only execute if the plan is active today
        assumingThat(plan.isActiveOn(today), () -> {
            System.out.println("Commission plan is active today");
            assertTrue(plan.getStatus() == PlanStatus.ACTIVE, "Plan status should be ACTIVE");
        });

        // This code will always execute, regardless of whether the assumption passed or failed
        assertEquals("Test Plan", plan.getName(), "Plan name should match");
    }

    /**
     * Demonstrates how assumptions can be used with environment-specific conditions.
     * 
     * This test will only run if a specific system property is set.
     */
    @Test
    public void testEnvironmentSpecificAssumption() {
        // Get the value of a system property (or environment variable)
        String testEnv = System.getProperty("test.environment", "dev");

        // Only run this test in the "prod" environment
        assumeTrue("prod".equals(testEnv), 
                "Skipping test because it should only run in production environment");

        // If the assumption passes, this code will execute
        System.out.println("Running test in production environment");

        // Add your production-specific test logic here
    }

    /**
     * Demonstrates how assumptions can be used with multiple conditions.
     * 
     * This test will only run if both conditions are met.
     */
    @Test
    public void testMultipleAssumptions() {
        // Assume the user is a sales rep and the deal is open
        assumeTrue(user.hasRole(UserRole.SALES_REP) && deal.getStatus() == DealStatus.OPEN,
                "Skipping test because either user is not a sales rep or deal is not open");

        // If both assumptions pass, this code will execute
        assertEquals(user.getId(), deal.getSalesRepId(), "Deal should be assigned to the user");
    }
}
