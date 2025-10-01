package com.chapman.edu.commissions.fundamentals.disabled;

import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 @Disabled annotation with the commission calculator model classes.
 * 
 * The @Disabled annotation is used to disable tests that should not be executed.
 * This is useful for:
 * 1. Temporarily disabling failing tests during development
 * 2. Disabling tests that are not yet implemented
 * 3. Disabling tests that are no longer relevant
 * 4. Disabling tests that are too slow or resource-intensive for regular execution
 * 5. Disabling tests that depend on external resources that are not available
 * 
 * Key concepts:
 * - @Disabled can be applied to individual test methods or entire test classes
 * - Disabled tests are not executed but are still reported in test results
 * - You can provide a reason for disabling the test using the value parameter
 * - Disabled tests help maintain test history and can be easily re-enabled later
 */
@DisplayName("JUnit Disabled Tests Demo")
public class JUnitDisabledTest {

    private User user;
    private Deal deal;
    private CommissionPlan plan;
    private CommissionCalculation calculation;

    /**
     * Set up test data before each test
     */
    @BeforeEach
    void setUp() {
        // Set up User
        user = new User();
        user.setId("test-user");
        user.setUsername("test.user");
        user.setEmail("test.user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.addRole(UserRole.SALES_REP);

        // Set up Deal
        deal = new Deal();
        deal.setId("test-deal");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000.00"));
        deal.setSalesRepId(user.getId());
        deal.setStatus(DealStatus.OPEN);

        // Add products to deal
        DealProduct product1 = new DealProduct();
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("5000.00"));
        product1.setQuantity(1);
        deal.addProduct(product1);

        DealProduct product2 = new DealProduct();
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("2500.00"));
        product2.setQuantity(2);
        deal.addProduct(product2);

        // Set up CommissionPlan
        plan = new CommissionPlan();
        plan.setId("test-plan");
        plan.setName("Test Plan");
        plan.setCurrency(Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusDays(30));
        plan.setEffectiveEndDate(LocalDate.now().plusDays(30));

        // Set up CommissionCalculation
        calculation = new CommissionCalculation();
        calculation.setId("test-calculation");
        calculation.setDealId(deal.getId());
        calculation.setSalesRepId(user.getId());
        calculation.setBaseCommission(new BigDecimal("1000.00"));
        calculation.setPlanId(plan.getId());
    }

    /**
     * This section demonstrates basic @Disabled usage
     * 
     * The @Disabled annotation can be used without any parameters to simply disable a test.
     * This is useful for quickly disabling tests during development.
     */

    @Test
    @Disabled
    @DisplayName("Basic disabled test without reason")
    void testBasicDisabled() {
        // This test is disabled and will not run
        // No reason is provided, so it will show as "Disabled" in test results
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }

    @Test
    @Disabled("This test is temporarily disabled during refactoring")
    @DisplayName("Disabled test with reason")
    void testDisabledWithReason() {
        // This test is disabled with a specific reason
        // The reason will be shown in test results
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
    }

    /**
     * This section demonstrates disabling tests that are not yet implemented
     * 
     * During test-driven development, you might write test method signatures
     * before implementing the actual test logic. @Disabled helps track these.
     */

    @Test
    @Disabled("Test not yet implemented - waiting for commission calculation logic")
    @DisplayName("Calculate complex commission with multiple tiers")
    void testCalculateComplexCommission() {
        // TODO: Implement this test when commission calculation logic is ready
        // This test should verify complex commission calculations with multiple tiers
        fail("Test not implemented yet");
    }

    @Test
    @Disabled("Test not yet implemented - waiting for bonus calculation feature")
    @DisplayName("Calculate commission with performance bonuses")
    void testCalculateCommissionWithBonuses() {
        // TODO: Implement this test when bonus calculation feature is ready
        // This test should verify commission calculations including performance bonuses
        fail("Test not implemented yet");
    }

    /**
     * This section demonstrates disabling tests that are failing temporarily
     * 
     * Sometimes tests fail due to temporary issues like external dependencies,
     * infrastructure problems, or bugs being worked on. @Disabled helps manage these.
     */

    @Test
    @Disabled("Failing due to database connection issue - ticket #123")
    @DisplayName("Save commission calculation to database")
    void testSaveCommissionCalculation() {
        // This test is disabled because it depends on database connectivity
        // which is currently having issues
        
        // Simulate saving to database
        calculation.setCalculatedBy("system");
        
        // This would normally save to database and verify
        // Currently disabled due to infrastructure issues
        assertNotNull(calculation.getCalculatedBy(), "Calculated by should be set");
    }

    @Test
    @Disabled("Intermittent failure - investigating race condition")
    @DisplayName("Concurrent commission calculations")
    void testConcurrentCommissionCalculations() {
        // This test is disabled due to intermittent failures
        // Likely caused by race conditions in concurrent processing
        
        // Test logic for concurrent calculations would go here
        // Currently disabled while investigating the race condition
        assertTrue(true, "Placeholder assertion");
    }

    /**
     * This section demonstrates disabling resource-intensive tests
     * 
     * Some tests might be too slow or resource-intensive for regular execution.
     * These can be disabled for normal test runs but enabled for specific scenarios.
     */

    @Test
    @Disabled("Performance test - too slow for regular CI runs")
    @DisplayName("Performance test for large deal processing")
    void testLargeDealProcessingPerformance() {
        // This test processes thousands of deals and takes several minutes
        // It's disabled for regular test runs but can be enabled for performance testing
        
        // Simulate processing large number of deals
        for (int i = 0; i < 10000; i++) {
            Deal largeDeal = new Deal("Deal " + i, new BigDecimal("1000.00"), user.getId());
            // Process deal...
        }
        
        assertTrue(true, "Performance test completed");
    }

    @Test
    @Disabled("Memory intensive test - disabled for CI environment")
    @DisplayName("Memory usage test for commission calculations")
    void testMemoryUsageForCommissionCalculations() {
        // This test creates many objects and monitors memory usage
        // It's disabled for CI environments with limited memory
        
        // Create many commission calculations to test memory usage
        for (int i = 0; i < 100000; i++) {
            CommissionCalculation calc = new CommissionCalculation();
            calc.setBaseCommission(new BigDecimal("100.00"));
        }
        
        assertTrue(true, "Memory test completed");
    }

    /**
     * This section demonstrates disabling tests that depend on external resources
     * 
     * Tests that depend on external APIs, services, or resources that might not
     * be available in all environments can be disabled conditionally.
     */

    @Test
    @Disabled("Requires external payment service - not available in test environment")
    @DisplayName("Process commission payment through external service")
    void testProcessCommissionPayment() {
        // This test requires an external payment service
        // It's disabled because the service is not available in test environment
        
        calculation.setStatus(CommissionCalculation.CommissionStatus.APPROVED);
        
        // Would normally call external payment service here
        // Currently disabled due to service unavailability
        assertEquals(CommissionCalculation.CommissionStatus.APPROVED, 
                calculation.getStatus(), "Calculation should be approved");
    }

    @Test
    @Disabled("Requires email service configuration - disabled until configured")
    @DisplayName("Send commission notification email")
    void testSendCommissionNotificationEmail() {
        // This test requires email service configuration
        // It's disabled until the email service is properly configured
        
        calculation.setStatus(CommissionCalculation.CommissionStatus.PAID);
        
        // Would normally send email notification here
        // Currently disabled due to missing email configuration
        assertEquals(CommissionCalculation.CommissionStatus.PAID, 
                calculation.getStatus(), "Calculation should be paid");
    }

    /**
     * This section demonstrates disabling deprecated functionality tests
     * 
     * When functionality is deprecated but not yet removed, related tests
     * can be disabled to indicate they're no longer actively maintained.
     */

    @Test
    @Disabled("Testing deprecated commission calculation method - will be removed in v2.0")
    @DisplayName("Test deprecated commission calculation method")
    void testDeprecatedCommissionCalculation() {
        // This test verifies deprecated functionality
        // It's disabled because the functionality will be removed soon
        
        // Test deprecated calculation method
        BigDecimal result = calculation.getBaseCommission();
        
        assertNotNull(result, "Deprecated calculation should return a result");
    }

    /**
     * This section demonstrates disabling tests for specific business scenarios
     * 
     * Some tests might only be relevant for specific business scenarios
     * or customer configurations that are not currently active.
     */

    @Test
    @Disabled("Feature disabled for current client - may be re-enabled later")
    @DisplayName("Test multi-currency commission calculations")
    void testMultiCurrencyCommissionCalculations() {
        // This test verifies multi-currency support
        // It's disabled because the current client doesn't use multiple currencies
        
        plan.setCurrency(Currency.getInstance("EUR"));
        
        // Test multi-currency calculations
        assertEquals(Currency.getInstance("EUR"), plan.getCurrency(), 
                "Plan should support EUR currency");
    }

    @Test
    @Disabled("Advanced feature not yet enabled for production")
    @DisplayName("Test AI-powered commission optimization")
    void testAIPoweredCommissionOptimization() {
        // This test verifies AI-powered features
        // It's disabled because the feature is not yet enabled for production
        
        // Test AI optimization logic
        assertTrue(true, "AI optimization test placeholder");
    }

    /**
     * Working tests for comparison
     * 
     * These tests are not disabled and demonstrate normal test execution
     * alongside the disabled tests above.
     */

    @Test
    @DisplayName("Working test - User role verification")
    void testUserRoleVerification() {
        // This test is not disabled and will run normally
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
        assertTrue(user.isSalesRep(), "User should be identified as sales rep");
    }

    @Test
    @DisplayName("Working test - Deal value calculation")
    void testDealValueCalculation() {
        // This test is not disabled and will run normally
        BigDecimal expectedValue = new BigDecimal("10000.00");
        assertEquals(expectedValue, deal.calculateTotalValue(), "Deal total value should match");
    }

    @Test
    @DisplayName("Working test - Commission plan status")
    void testCommissionPlanStatus() {
        // This test is not disabled and will run normally
        assertEquals(PlanStatus.ACTIVE, plan.getStatus(), "Plan should be active");
        assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active today");
    }
}