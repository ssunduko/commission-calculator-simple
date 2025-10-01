package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class demonstrating how to use multiple fixtures together
 * to create complex test scenarios for commission processing.
 * 
 * This class shows the power of the Fixture pattern when dealing with
 * interconnected objects and complex business scenarios. By combining
 * multiple fixtures, we can easily create realistic test scenarios
 * without extensive setup code.
 * 
 * Key concepts demonstrated:
 * - Combining multiple fixtures to create complex scenarios
 * - Testing relationships between different domain objects
 * - Simulating real-world commission calculation workflows
 * - Maintaining test data consistency across object relationships
 */
class JUnitFixtureTest {

    /**
     * Test that demonstrates a complete commission calculation scenario
     * using multiple fixtures to create a realistic business workflow.
     */
    @Test
    void testCompleteCommissionScenario() {
        // Arrange: Create a complete commission scenario using multiple fixtures
        
        // Create a sales representative who will earn commissions
        User salesRep = UserFixture.createSalesRep();
        
        // Create an active commission plan for calculations
        CommissionPlan activePlan = CommissionPlanFixture.createBasicActivePlan();
        
        // Create a won deal that should generate commissions
        Deal wonDeal = DealFixture.createWonDeal();
        
        // Ensure the deal is assigned to our sales rep
        wonDeal.setSalesRepId(salesRep.getId());
        
        // Act & Assert: Verify the complete scenario setup
        assertNotNull(salesRep, "Sales rep should be created");
        assertNotNull(activePlan, "Commission plan should be created");
        assertNotNull(wonDeal, "Deal should be created");
        
        // Verify relationships and business rules
        assertTrue(salesRep.isSalesRep(), "User should be a sales representative");
        assertTrue(activePlan.isActiveOn(LocalDate.now()), "Plan should be active for today");
        assertEquals(DealStatus.WON, wonDeal.getStatus(), "Deal should be won to generate commissions");
        assertEquals(salesRep.getId(), wonDeal.getSalesRepId(), "Deal should be assigned to the sales rep");
        
        // Verify commission eligibility criteria
        assertTrue(wonDeal.getValue().compareTo(BigDecimal.ZERO) > 0, "Deal should have positive value");
        assertNotNull(wonDeal.getCloseDate(), "Won deal should have a close date");
        assertTrue(salesRep.isActive(), "Sales rep should be active to earn commissions");
    }

    /**
     * Test that demonstrates a multi-user, multi-deal scenario
     * for testing team-based commission calculations.
     */
    @Test
    void testTeamCommissionScenario() {
        // Arrange: Create a team scenario with manager and subordinates
        
        // Create a sales manager
        User manager = UserFixture.createSalesManager();
        
        // Create sales reps reporting to the manager
        User salesRep1 = UserFixture.createUserWithManager(manager.getId());
        User salesRep2 = UserFixture.createUserWithManager(manager.getId());
        
        // Create deals for each sales rep
        Deal deal1 = DealFixture.createDealForSalesRep(salesRep1.getId());
        Deal deal2 = DealFixture.createHighValueDeal();
        deal2.setSalesRepId(salesRep2.getId());
        
        // Create commission plan for the team
        CommissionPlan teamPlan = CommissionPlanFixture.createBasicActivePlan();
        
        // Act & Assert: Verify team scenario setup
        assertNotNull(manager, "Manager should be created");
        assertNotNull(salesRep1, "First sales rep should be created");
        assertNotNull(salesRep2, "Second sales rep should be created");
        
        // Verify hierarchical relationships
        assertEquals(manager.getId(), salesRep1.getManagerId(), "First rep should report to manager");
        assertEquals(manager.getId(), salesRep2.getManagerId(), "Second rep should report to manager");
        assertTrue(manager.isSalesManager(), "Manager should have manager role");
        assertTrue(salesRep1.isSalesRep(), "First rep should have sales rep role");
        assertTrue(salesRep2.isSalesRep(), "Second rep should have sales rep role");
        
        // Verify deal assignments
        assertEquals(salesRep1.getId(), deal1.getSalesRepId(), "Deal 1 should be assigned to rep 1");
        assertEquals(salesRep2.getId(), deal2.getSalesRepId(), "Deal 2 should be assigned to rep 2");
        
        // Verify commission plan applicability
        assertTrue(teamPlan.isActiveOn(LocalDate.now()), "Team plan should be active");
    }

    /**
     * Test that demonstrates a territory-based commission scenario
     * using fixtures to create geographically distributed sales.
     */
    @Test
    void testTerritoryCommissionScenario() {
        // Arrange: Create territory-based scenario
        
        // Create sales reps in different territories
        User westCoastRep = UserFixture.createUserWithTerritory("West Coast");
        User eastCoastRep = UserFixture.createUserWithTerritory("East Coast");
        User internationalRep = UserFixture.createUserWithTerritory("International");
        
        // Create deals for each territory
        Deal westCoastDeal = DealFixture.createDealForSalesRep(westCoastRep.getId());
        Deal eastCoastDeal = DealFixture.createMultiProductDeal();
        eastCoastDeal.setSalesRepId(eastCoastRep.getId());
        Deal internationalDeal = DealFixture.createHighValueDeal();
        internationalDeal.setSalesRepId(internationalRep.getId());
        
        // Create territory-specific commission plans
        CommissionPlan usPlan = CommissionPlanFixture.createBasicActivePlan();
        CommissionPlan euroPlan = CommissionPlanFixture.createEuroPlan();
        
        // Act & Assert: Verify territory scenario setup
        assertEquals("West Coast", westCoastRep.getTerritory(), "Should have correct territory");
        assertEquals("East Coast", eastCoastRep.getTerritory(), "Should have correct territory");
        assertEquals("International", internationalRep.getTerritory(), "Should have correct territory");
        
        // Verify deal-territory alignment
        assertEquals(westCoastRep.getId(), westCoastDeal.getSalesRepId(), "West Coast deal should be assigned correctly");
        assertEquals(eastCoastRep.getId(), eastCoastDeal.getSalesRepId(), "East Coast deal should be assigned correctly");
        assertEquals(internationalRep.getId(), internationalDeal.getSalesRepId(), "International deal should be assigned correctly");
        
        // Verify commission plan currencies
        assertEquals(Currency.getInstance("USD"), usPlan.getCurrency(), "US plan should use USD");
        assertEquals(Currency.getInstance("EUR"), euroPlan.getCurrency(), "Euro plan should use EUR");
    }

    /**
     * Test that demonstrates a time-based commission scenario
     * for testing quarterly or annual commission calculations.
     */
    @Test
    void testTimeBasedCommissionScenario() {
        // Arrange: Create time-based scenario with historical and current data
        
        // Create sales rep with specific creation date
        LocalDate repStartDate = LocalDate.of(2023, 1, 1);
        User salesRep = UserFixture.createUserWithCreationDate(repStartDate);
        
        // Create deals from different time periods
        LocalDate q1CloseDate = LocalDate.of(2023, 3, 31);
        LocalDate q2CloseDate = LocalDate.of(2023, 6, 30);
        LocalDate currentDate = LocalDate.now();
        
        Deal q1Deal = DealFixture.createDealWithCloseDate(q1CloseDate);
        q1Deal.setSalesRepId(salesRep.getId());
        
        Deal q2Deal = DealFixture.createDealWithCloseDate(q2CloseDate);
        q2Deal.setSalesRepId(salesRep.getId());
        
        Deal currentDeal = DealFixture.createDealWithCloseDate(currentDate);
        currentDeal.setSalesRepId(salesRep.getId());
        
        // Create commission plans for different periods
        CommissionPlan q1Plan = CommissionPlanFixture.createPlanWithDateRange(
            LocalDate.of(2023, 1, 1), LocalDate.of(2023, 3, 31));
        CommissionPlan currentPlan = CommissionPlanFixture.createBasicActivePlan();
        
        // Act & Assert: Verify time-based scenario setup
        assertEquals(repStartDate, salesRep.getCreatedDate(), "Rep should have correct start date");
        
        // Verify deal timing
        assertEquals(q1CloseDate, q1Deal.getCloseDate(), "Q1 deal should close in Q1");
        assertEquals(q2CloseDate, q2Deal.getCloseDate(), "Q2 deal should close in Q2");
        assertEquals(currentDate, currentDeal.getCloseDate(), "Current deal should close today");
        
        // Verify plan effectiveness for different periods
        assertTrue(q1Plan.isActiveOn(q1CloseDate), "Q1 plan should be active for Q1 deal");
        assertFalse(q1Plan.isActiveOn(currentDate), "Q1 plan should not be active currently");
        assertTrue(currentPlan.isActiveOn(currentDate), "Current plan should be active today");
        
        // Verify all deals belong to the same rep
        assertEquals(salesRep.getId(), q1Deal.getSalesRepId(), "Q1 deal should belong to rep");
        assertEquals(salesRep.getId(), q2Deal.getSalesRepId(), "Q2 deal should belong to rep");
        assertEquals(salesRep.getId(), currentDeal.getSalesRepId(), "Current deal should belong to rep");
    }

    /**
     * Test that demonstrates a complex multi-product, multi-currency scenario
     * for testing international commission calculations.
     */
    @Test
    void testMultiCurrencyMultiProductScenario() {
        // Arrange: Create complex international scenario
        
        // Create international sales team
        User usRep = UserFixture.createUserWithTerritory("North America");
        User euRep = UserFixture.createUserWithTerritory("Europe");
        User manager = UserFixture.createSalesManager();
        
        // Set up reporting relationships
        usRep.setManagerId(manager.getId());
        euRep.setManagerId(manager.getId());
        
        // Create multi-product deals
        Deal usMultiProductDeal = DealFixture.createMultiProductDeal();
        usMultiProductDeal.setSalesRepId(usRep.getId());
        
        Deal euDiscountedDeal = DealFixture.createDiscountedDeal();
        euDiscountedDeal.setSalesRepId(euRep.getId());
        
        // Create currency-specific commission plans
        CommissionPlan usdPlan = CommissionPlanFixture.createPlanWithCurrency(Currency.getInstance("USD"));
        CommissionPlan eurPlan = CommissionPlanFixture.createPlanWithCurrency(Currency.getInstance("EUR"));
        
        // Act & Assert: Verify complex scenario setup
        
        // Verify team structure
        assertEquals(manager.getId(), usRep.getManagerId(), "US rep should report to manager");
        assertEquals(manager.getId(), euRep.getManagerId(), "EU rep should report to manager");
        assertTrue(manager.isSalesManager(), "Manager should have manager role");
        
        // Verify deal complexity
        assertTrue(usMultiProductDeal.getProducts().size() > 1, "US deal should have multiple products");
        assertTrue(euDiscountedDeal.getProducts().size() > 0, "EU deal should have products");
        assertTrue(euDiscountedDeal.getProducts().get(0).getDiscount().compareTo(BigDecimal.ZERO) > 0, 
                  "EU deal should have discounted products");
        
        // Verify currency alignment
        assertEquals(Currency.getInstance("USD"), usdPlan.getCurrency(), "USD plan should use USD currency");
        assertEquals(Currency.getInstance("EUR"), eurPlan.getCurrency(), "EUR plan should use EUR currency");
        
        // Verify deal assignments
        assertEquals(usRep.getId(), usMultiProductDeal.getSalesRepId(), "US deal should be assigned to US rep");
        assertEquals(euRep.getId(), euDiscountedDeal.getSalesRepId(), "EU deal should be assigned to EU rep");
        
        // Verify all plans are active
        assertTrue(usdPlan.isActiveOn(LocalDate.now()), "USD plan should be active");
        assertTrue(eurPlan.isActiveOn(LocalDate.now()), "EUR plan should be active");
    }

    /**
     * Test that demonstrates edge case scenarios using fixtures
     * for testing error handling and boundary conditions.
     */
    @Test
    void testEdgeCaseScenarios() {
        // Arrange: Create edge case scenarios
        
        // Create inactive user with deals (should not earn commissions)
        User inactiveUser = UserFixture.createInactiveUser();
        Deal dealForInactiveUser = DealFixture.createDealForSalesRep(inactiveUser.getId());
        
        // Create small deal that might not meet minimum thresholds
        Deal smallDeal = DealFixture.createSmallDeal();
        User salesRep = UserFixture.createSalesRep();
        smallDeal.setSalesRepId(salesRep.getId());
        
        // Create cancelled and lost deals (should not generate commissions)
        Deal cancelledDeal = DealFixture.createCancelledDeal();
        Deal lostDeal = DealFixture.createLostDeal();
        cancelledDeal.setSalesRepId(salesRep.getId());
        lostDeal.setSalesRepId(salesRep.getId());
        
        // Create expired commission plan
        CommissionPlan expiredPlan = CommissionPlanFixture.createExpiredPlan();
        
        // Act & Assert: Verify edge case handling
        
        // Inactive user scenarios
        assertFalse(inactiveUser.isActive(), "User should be inactive");
        assertEquals(inactiveUser.getId(), dealForInactiveUser.getSalesRepId(), "Deal should be assigned to inactive user");
        
        // Small deal scenarios
        assertTrue(smallDeal.getValue().compareTo(new BigDecimal("5000.00")) < 0, "Should be a small deal");
        assertEquals(DealStatus.WON, smallDeal.getStatus(), "Small deal should still be won");
        
        // Non-commission generating deals
        assertEquals(DealStatus.CANCELLED, cancelledDeal.getStatus(), "Deal should be cancelled");
        assertEquals(DealStatus.LOST, lostDeal.getStatus(), "Deal should be lost");
        assertNull(cancelledDeal.getCloseDate(), "Cancelled deal should not have close date");
        assertNull(lostDeal.getCloseDate(), "Lost deal should not have close date");
        
        // Expired plan scenarios
        assertEquals(PlanStatus.ACTIVE, expiredPlan.getStatus(), "Plan should be active but expired");
        assertFalse(expiredPlan.isActiveOn(LocalDate.now()), "Expired plan should not be effective today");
        
        // Verify all deals belong to active sales rep (except inactive user deal)
        assertEquals(salesRep.getId(), smallDeal.getSalesRepId(), "Small deal should belong to active rep");
        assertEquals(salesRep.getId(), cancelledDeal.getSalesRepId(), "Cancelled deal should belong to active rep");
        assertEquals(salesRep.getId(), lostDeal.getSalesRepId(), "Lost deal should belong to active rep");
        assertTrue(salesRep.isActive(), "Sales rep should be active");
    }
}