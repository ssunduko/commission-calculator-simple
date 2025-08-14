package com.chapman.edu.commissions;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the model classes in the Sales Commission Calculator application.
 * These tests don't rely on web or database functionality.
 */
public class CommissionCalculatorTest {

    /**
     * Test that the User model can be created and used correctly.
     */
    @Test
    public void testUserModel() {
        User user = new User();
        user.setId("1");
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.addRole(UserRole.SALES_REP);
        
        assertEquals("1", user.getId(), "User ID should match");
        assertEquals("john.doe", user.getUsername(), "Username should match");
        assertEquals("john.doe@example.com", user.getEmail(), "Email should match");
        assertEquals("John", user.getFirstName(), "First name should match");
        assertEquals("Doe", user.getLastName(), "Last name should match");
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
        assertEquals("John Doe", user.getFullName(), "Full name should be correctly concatenated");
        
        System.out.println("[DEBUG_LOG] User model test passed");
    }
    
    /**
     * Test that the CommissionPlan model can be created and used correctly.
     */
    @Test
    public void testCommissionPlanModel() {
        CommissionPlan plan = new CommissionPlan();
        plan.setId("1");
        plan.setName("Standard Sales Commission Plan");
        
        assertEquals("1", plan.getId(), "Plan ID should match");
        assertEquals("Standard Sales Commission Plan", plan.getName(), "Plan name should match");
        
        System.out.println("[DEBUG_LOG] CommissionPlan model test passed");
    }
    
    /**
     * Test that the Deal model can be created and used correctly.
     */
    @Test
    public void testDealModel() {
        Deal deal = new Deal();
        deal.setId("1");
        deal.setTitle("Enterprise Software License");
        deal.setValue(new BigDecimal("100000.00"));
        deal.setSalesRepId("1");
        
        assertEquals("1", deal.getId(), "Deal ID should match");
        assertEquals("Enterprise Software License", deal.getTitle(), "Deal title should match");
        assertEquals(new BigDecimal("100000.00"), deal.getValue(), "Deal value should match");
        assertEquals("1", deal.getSalesRepId(), "Sales rep ID should match");
        
        System.out.println("[DEBUG_LOG] Deal model test passed");
    }
}