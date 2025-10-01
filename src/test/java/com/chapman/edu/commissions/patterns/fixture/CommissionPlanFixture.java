package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.time.LocalDate;
import java.util.Currency;

/**
 * Fixture class for creating CommissionPlan test data.
 * 
 * Commission plans define the rules for calculating commissions and are complex objects
 * with multiple relationships. This fixture ensures consistent plan creation across tests
 * and provides various scenarios commonly used in commission testing.
 * 
 * Benefits of using fixtures for commission plans:
 * - Consistent plan configurations across tests
 * - Easy creation of complex plan hierarchies
 * - Simplified testing of plan activation and deactivation
 * - Standardized test data for commission calculations
 */
public class CommissionPlanFixture {
    
    /**
     * Creates a basic active commission plan with standard configuration.
     * This represents the most common type of commission plan used in calculations.
     * 
     * @return an active CommissionPlan with basic settings
     */
    public static CommissionPlan createBasicActivePlan() {
        CommissionPlan plan = new CommissionPlan("Standard Sales Plan", Currency.getInstance("USD"));
        plan.setId("plan-001");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(6));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(6));
        plan.setCreatedBy("system");
        return plan;
    }
    
    /**
     * Creates a draft commission plan for testing plan development workflows.
     * Draft plans should not be used for commission calculations until activated.
     * 
     * @return a CommissionPlan in DRAFT status
     */
    public static CommissionPlan createDraftPlan() {
        CommissionPlan plan = new CommissionPlan("New Territory Plan", Currency.getInstance("USD"));
        plan.setId("plan-002");
        plan.setStatus(PlanStatus.DRAFT);
        plan.setCreatedBy("finance-admin");
        return plan;
    }
    
    /**
     * Creates an inactive commission plan for testing historical scenarios.
     * Inactive plans should not be used for new commission calculations
     * but may be referenced for historical data.
     * 
     * @return a CommissionPlan in INACTIVE status
     */
    public static CommissionPlan createInactivePlan() {
        CommissionPlan plan = new CommissionPlan("Legacy Plan", Currency.getInstance("USD"));
        plan.setId("plan-003");
        plan.setStatus(PlanStatus.INACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusYears(2));
        plan.setEffectiveEndDate(LocalDate.now().minusYears(1));
        plan.setCreatedBy("system");
        return plan;
    }
    
    /**
     * Creates a commission plan with future effective dates for testing plan scheduling.
     * Future plans should not be active until their effective start date.
     * 
     * @return a CommissionPlan with future effective dates
     */
    public static CommissionPlan createFuturePlan() {
        CommissionPlan plan = new CommissionPlan("Q4 Bonus Plan", Currency.getInstance("USD"));
        plan.setId("plan-004");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().plusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(4));
        plan.setCreatedBy("finance-admin");
        return plan;
    }
    
    /**
     * Creates a commission plan with expired dates for testing historical calculations.
     * Expired plans should only be used for historical commission calculations.
     * 
     * @return a CommissionPlan with past effective dates
     */
    public static CommissionPlan createExpiredPlan() {
        CommissionPlan plan = new CommissionPlan("Q1 Special Plan", Currency.getInstance("USD"));
        plan.setId("plan-005");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(8));
        plan.setEffectiveEndDate(LocalDate.now().minusMonths(5));
        plan.setCreatedBy("system");
        return plan;
    }
    
    /**
     * Creates a commission plan with no end date for testing open-ended plans.
     * Plans without end dates remain active indefinitely until manually deactivated.
     * 
     * @return a CommissionPlan with no end date
     */
    public static CommissionPlan createOpenEndedPlan() {
        CommissionPlan plan = new CommissionPlan("Permanent Base Plan", Currency.getInstance("USD"));
        plan.setId("plan-006");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(12));
        // No end date set - plan remains active indefinitely
        plan.setCreatedBy("system");
        return plan;
    }
    
    /**
     * Creates a commission plan in a different currency for testing multi-currency scenarios.
     * Different currencies may require different calculation methods or conversion rates.
     * 
     * @return a CommissionPlan using EUR currency
     */
    public static CommissionPlan createEuroPlan() {
        CommissionPlan plan = new CommissionPlan("European Sales Plan", Currency.getInstance("EUR"));
        plan.setId("plan-007");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(3));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(9));
        plan.setCreatedBy("eu-finance");
        return plan;
    }
    
    /**
     * Creates a commission plan with specific effective dates for testing date-based logic.
     * This allows precise control over when the plan should be considered active.
     * 
     * @param startDate the effective start date
     * @param endDate the effective end date
     * @return a CommissionPlan with the specified date range
     */
    public static CommissionPlan createPlanWithDateRange(LocalDate startDate, LocalDate endDate) {
        CommissionPlan plan = new CommissionPlan("Custom Date Plan", Currency.getInstance("USD"));
        plan.setId("plan-008");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(startDate);
        plan.setEffectiveEndDate(endDate);
        plan.setCreatedBy("test-admin");
        return plan;
    }
    
    /**
     * Creates a commission plan for a specific creator for testing audit scenarios.
     * This helps test authorization and audit trail functionality.
     * 
     * @param createdBy the user who created the plan
     * @return a CommissionPlan created by the specified user
     */
    public static CommissionPlan createPlanByUser(String createdBy) {
        CommissionPlan plan = new CommissionPlan("User-Specific Plan", Currency.getInstance("USD"));
        plan.setId("plan-009");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(11));
        plan.setCreatedBy(createdBy);
        return plan;
    }
    
    /**
     * Creates a commission plan with a specific currency for testing currency-specific logic.
     * Different currencies may have different rounding rules or calculation methods.
     * 
     * @param currency the currency for the commission plan
     * @return a CommissionPlan using the specified currency
     */
    public static CommissionPlan createPlanWithCurrency(Currency currency) {
        CommissionPlan plan = new CommissionPlan("Multi-Currency Plan", currency);
        plan.setId("plan-010");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(2));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(10));
        plan.setCreatedBy("global-finance");
        return plan;
    }
}