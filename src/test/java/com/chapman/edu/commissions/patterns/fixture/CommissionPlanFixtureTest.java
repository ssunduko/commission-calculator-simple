package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class demonstrating the use of CommissionPlanFixture for creating consistent plan test data.
 * 
 * Commission plans are complex objects that define the rules for calculating commissions.
 * This class shows how the Fixture pattern helps create various plan scenarios
 * without repetitive setup code, making tests more focused and maintainable.
 * 
 * The fixture pattern benefits demonstrated:
 * - Simplified creation of complex commission plan configurations
 * - Consistent plan setups across different test scenarios
 * - Easy testing of plan activation, deactivation, and date-based logic
 * - Reduced maintenance when CommissionPlan model changes
 */
class CommissionPlanFixtureTest {

    /**
     * Test that demonstrates creating a basic active commission plan using fixtures.
     * Active plans are the primary plans used for commission calculations.
     */
    @Test
    void testCreateBasicActivePlan() {
        // Arrange: Use fixture to create a basic active plan
        CommissionPlan activePlan = CommissionPlanFixture.createBasicActivePlan();
        
        // Act & Assert: Verify active plan properties
        assertNotNull(activePlan, "Active plan should not be null");
        assertEquals("plan-001", activePlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.ACTIVE, activePlan.getStatus(), "Plan should be active");
        assertEquals("Standard Sales Plan", activePlan.getName(), "Should have correct name");
        assertEquals(Currency.getInstance("USD"), activePlan.getCurrency(), "Should use USD currency");
        assertNotNull(activePlan.getEffectiveStartDate(), "Should have start date");
        assertNotNull(activePlan.getEffectiveEndDate(), "Should have end date");
        assertEquals("system", activePlan.getCreatedBy(), "Should have correct creator");
    }

    /**
     * Test that demonstrates creating a draft commission plan using fixtures.
     * Draft plans should not be used for commission calculations until activated.
     */
    @Test
    void testCreateDraftPlan() {
        // Arrange: Use fixture to create a draft plan
        CommissionPlan draftPlan = CommissionPlanFixture.createDraftPlan();
        
        // Act & Assert: Verify draft plan properties
        assertNotNull(draftPlan, "Draft plan should not be null");
        assertEquals("plan-002", draftPlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.DRAFT, draftPlan.getStatus(), "Plan should be in draft status");
        assertEquals("New Territory Plan", draftPlan.getName(), "Should have correct name");
        assertEquals("finance-admin", draftPlan.getCreatedBy(), "Should have correct creator");
        assertNull(draftPlan.getEffectiveStartDate(), "Draft plan should not have start date");
        assertNull(draftPlan.getEffectiveEndDate(), "Draft plan should not have end date");
    }

    /**
     * Test that demonstrates creating an inactive commission plan using fixtures.
     * Inactive plans should not be used for new calculations but may be referenced historically.
     */
    @Test
    void testCreateInactivePlan() {
        // Arrange: Use fixture to create an inactive plan
        CommissionPlan inactivePlan = CommissionPlanFixture.createInactivePlan();
        
        // Act & Assert: Verify inactive plan properties
        assertNotNull(inactivePlan, "Inactive plan should not be null");
        assertEquals("plan-003", inactivePlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.INACTIVE, inactivePlan.getStatus(), "Plan should be inactive");
        assertEquals("Legacy Plan", inactivePlan.getName(), "Should have correct name");
        assertTrue(inactivePlan.getEffectiveEndDate().isBefore(LocalDate.now()), 
                  "Inactive plan should have ended in the past");
    }

    /**
     * Test that demonstrates creating a future commission plan using fixtures.
     * Future plans should not be active until their effective start date.
     */
    @Test
    void testCreateFuturePlan() {
        // Arrange: Use fixture to create a future plan
        CommissionPlan futurePlan = CommissionPlanFixture.createFuturePlan();
        
        // Act & Assert: Verify future plan properties
        assertNotNull(futurePlan, "Future plan should not be null");
        assertEquals("plan-004", futurePlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.ACTIVE, futurePlan.getStatus(), "Plan should be active (but not yet effective)");
        assertTrue(futurePlan.getEffectiveStartDate().isAfter(LocalDate.now()), 
                  "Future plan should start in the future");
        assertFalse(futurePlan.isActiveOn(LocalDate.now()), 
                   "Future plan should not be active today");
    }

    /**
     * Test that demonstrates creating an expired commission plan using fixtures.
     * Expired plans should only be used for historical commission calculations.
     */
    @Test
    void testCreateExpiredPlan() {
        // Arrange: Use fixture to create an expired plan
        CommissionPlan expiredPlan = CommissionPlanFixture.createExpiredPlan();
        
        // Act & Assert: Verify expired plan properties
        assertNotNull(expiredPlan, "Expired plan should not be null");
        assertEquals("plan-005", expiredPlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.ACTIVE, expiredPlan.getStatus(), "Plan should be active (but expired)");
        assertTrue(expiredPlan.getEffectiveEndDate().isBefore(LocalDate.now()), 
                  "Expired plan should have ended in the past");
        assertFalse(expiredPlan.isActiveOn(LocalDate.now()), 
                   "Expired plan should not be active today");
    }

    /**
     * Test that demonstrates creating an open-ended commission plan using fixtures.
     * Open-ended plans remain active indefinitely until manually deactivated.
     */
    @Test
    void testCreateOpenEndedPlan() {
        // Arrange: Use fixture to create an open-ended plan
        CommissionPlan openEndedPlan = CommissionPlanFixture.createOpenEndedPlan();
        
        // Act & Assert: Verify open-ended plan properties
        assertNotNull(openEndedPlan, "Open-ended plan should not be null");
        assertEquals("plan-006", openEndedPlan.getId(), "Should have correct plan ID");
        assertEquals(PlanStatus.ACTIVE, openEndedPlan.getStatus(), "Plan should be active");
        assertNotNull(openEndedPlan.getEffectiveStartDate(), "Should have start date");
        assertNull(openEndedPlan.getEffectiveEndDate(), "Should not have end date");
        assertTrue(openEndedPlan.isActiveOn(LocalDate.now()), 
                  "Open-ended plan should be active today");
        assertTrue(openEndedPlan.isActiveOn(LocalDate.now().plusYears(1)), 
                  "Open-ended plan should be active in the future");
    }

    /**
     * Test that demonstrates creating a commission plan with different currency using fixtures.
     * Different currencies may require different calculation methods or conversion rates.
     */
    @Test
    void testCreateEuroPlan() {
        // Arrange: Use fixture to create a plan with EUR currency
        CommissionPlan euroPlan = CommissionPlanFixture.createEuroPlan();
        
        // Act & Assert: Verify EUR plan properties
        assertNotNull(euroPlan, "Euro plan should not be null");
        assertEquals("plan-007", euroPlan.getId(), "Should have correct plan ID");
        assertEquals(Currency.getInstance("EUR"), euroPlan.getCurrency(), "Should use EUR currency");
        assertEquals("European Sales Plan", euroPlan.getName(), "Should have correct name");
        assertEquals("eu-finance", euroPlan.getCreatedBy(), "Should have correct creator");
    }

    /**
     * Test that demonstrates creating a commission plan with specific date range using fixtures.
     * This allows precise control over when the plan should be considered active.
     */
    @Test
    void testCreatePlanWithDateRange() {
        // Arrange: Use fixture to create a plan with specific date range
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        CommissionPlan customDatePlan = CommissionPlanFixture.createPlanWithDateRange(startDate, endDate);
        
        // Act & Assert: Verify custom date range
        assertNotNull(customDatePlan, "Custom date plan should not be null");
        assertEquals("plan-008", customDatePlan.getId(), "Should have correct plan ID");
        assertEquals(startDate, customDatePlan.getEffectiveStartDate(), "Should have correct start date");
        assertEquals(endDate, customDatePlan.getEffectiveEndDate(), "Should have correct end date");
        assertEquals("test-admin", customDatePlan.getCreatedBy(), "Should have correct creator");
    }

    /**
     * Test that demonstrates creating a commission plan for a specific creator using fixtures.
     * This helps test authorization and audit trail functionality.
     */
    @Test
    void testCreatePlanByUser() {
        // Arrange: Use fixture to create a plan by specific user
        String customCreator = "manager-123";
        CommissionPlan userPlan = CommissionPlanFixture.createPlanByUser(customCreator);
        
        // Act & Assert: Verify creator assignment
        assertNotNull(userPlan, "User plan should not be null");
        assertEquals("plan-009", userPlan.getId(), "Should have correct plan ID");
        assertEquals(customCreator, userPlan.getCreatedBy(), "Should have correct creator");
        assertEquals(PlanStatus.ACTIVE, userPlan.getStatus(), "Should be active");
    }

    /**
     * Test that demonstrates creating a commission plan with specific currency using fixtures.
     * Different currencies may have different rounding rules or calculation methods.
     */
    @Test
    void testCreatePlanWithCurrency() {
        // Arrange: Use fixture to create a plan with specific currency
        Currency gbpCurrency = Currency.getInstance("GBP");
        CommissionPlan gbpPlan = CommissionPlanFixture.createPlanWithCurrency(gbpCurrency);
        
        // Act & Assert: Verify currency assignment
        assertNotNull(gbpPlan, "GBP plan should not be null");
        assertEquals("plan-010", gbpPlan.getId(), "Should have correct plan ID");
        assertEquals(gbpCurrency, gbpPlan.getCurrency(), "Should use GBP currency");
        assertEquals("Multi-Currency Plan", gbpPlan.getName(), "Should have correct name");
        assertEquals("global-finance", gbpPlan.getCreatedBy(), "Should have correct creator");
    }

    /**
     * Test that demonstrates the isActiveOn method behavior with different plan configurations.
     * This tests the core business logic for determining plan effectiveness.
     */
    @Test
    void testPlanActivationLogic() {
        // Arrange: Create plans with different activation scenarios
        CommissionPlan activePlan = CommissionPlanFixture.createBasicActivePlan();
        CommissionPlan draftPlan = CommissionPlanFixture.createDraftPlan();
        CommissionPlan futurePlan = CommissionPlanFixture.createFuturePlan();
        CommissionPlan expiredPlan = CommissionPlanFixture.createExpiredPlan();
        CommissionPlan openEndedPlan = CommissionPlanFixture.createOpenEndedPlan();
        
        LocalDate today = LocalDate.now();
        
        // Act & Assert: Test activation logic for different scenarios
        assertTrue(activePlan.isActiveOn(today), "Active plan should be active today");
        assertFalse(draftPlan.isActiveOn(today), "Draft plan should not be active");
        assertFalse(futurePlan.isActiveOn(today), "Future plan should not be active today");
        assertFalse(expiredPlan.isActiveOn(today), "Expired plan should not be active today");
        assertTrue(openEndedPlan.isActiveOn(today), "Open-ended plan should be active today");
        
        // Test future dates
        LocalDate futureDate = today.plusMonths(2);
        assertTrue(futurePlan.isActiveOn(futureDate), "Future plan should be active in the future");
        assertTrue(openEndedPlan.isActiveOn(futureDate), "Open-ended plan should be active in the future");
    }

    /**
     * Test that demonstrates the consistency of fixture-created plans.
     * Multiple calls to the same fixture method should create similar plans
     * with consistent base configuration.
     */
    @Test
    void testFixtureConsistency() {
        // Arrange: Create multiple active plans using the same fixture method
        CommissionPlan activePlan1 = CommissionPlanFixture.createBasicActivePlan();
        CommissionPlan activePlan2 = CommissionPlanFixture.createBasicActivePlan();
        
        // Act & Assert: Verify consistency while allowing for different instances
        assertNotSame(activePlan1, activePlan2, "Should be different object instances");
        assertEquals(activePlan1.getName(), activePlan2.getName(), "Should have same name");
        assertEquals(activePlan1.getStatus(), activePlan2.getStatus(), "Should have same status");
        assertEquals(activePlan1.getCurrency(), activePlan2.getCurrency(), "Should have same currency");
        assertEquals(activePlan1.getCreatedBy(), activePlan2.getCreatedBy(), "Should have same creator");
    }

    /**
     * Test that demonstrates using fixtures for status-based testing.
     * This shows how fixtures make it easy to test different plan statuses
     * and their impact on business logic.
     */
    @Test
    void testPlanStatusScenarios() {
        // Arrange: Create plans with different statuses using fixtures
        CommissionPlan activePlan = CommissionPlanFixture.createBasicActivePlan();
        CommissionPlan draftPlan = CommissionPlanFixture.createDraftPlan();
        CommissionPlan inactivePlan = CommissionPlanFixture.createInactivePlan();
        
        // Act & Assert: Verify status-specific behavior
        assertEquals(PlanStatus.ACTIVE, activePlan.getStatus(), "Active plan should have ACTIVE status");
        assertEquals(PlanStatus.DRAFT, draftPlan.getStatus(), "Draft plan should have DRAFT status");
        assertEquals(PlanStatus.INACTIVE, inactivePlan.getStatus(), "Inactive plan should have INACTIVE status");
        
        // Test business logic implications
        assertTrue(activePlan.isActiveOn(LocalDate.now()), "Active plan should be usable for calculations");
        assertFalse(draftPlan.isActiveOn(LocalDate.now()), "Draft plan should not be usable for calculations");
        assertFalse(inactivePlan.isActiveOn(LocalDate.now()), "Inactive plan should not be usable for calculations");
    }
}